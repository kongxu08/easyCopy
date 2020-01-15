package com.xuli2.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.duma.ld.mylibrary.SwitchView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import java.io.IOException;
import java.nio.channels.AcceptPendingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    TextView tv1;
    Button btn_clear;
    MaterialCalendarView mcv;
    SwitchView sv;
    ListView listView;
    HttpServer server;
    Context context;

    public static Map<String,List<String>> map_pic;
    public static Map<String,List<String>> map_vod;
    public static Map<String,List<Long>> map_pic_size;
    public static Map<String,List<String>> map_pic_id;
    public static List<String> list_uri_click;
    public static Map<String,List<Long>> map_vod_size;
    public static Map<String,List<String>> map_vod_id;
    public static String pickDate;
    public static boolean isPic=true;
    public static View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        //屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadingView =LoadingUtil.creatLoadingView(this);
        loadingView.setVisibility(View.GONE);

        list_uri_click = new ArrayList<>();

        List<Map<String,String>> listmaps=new ArrayList<>();
        Map map=new HashMap();
        map.put("name", "标题");
        map.put("number", "子标题");
        listmaps.add(map);
        listView = findViewById(R.id.list_view);
        SimpleAdapter adapter =new SimpleAdapter(MainActivity.this, listmaps,
                android.R.layout.simple_expandable_list_item_2, new String[]{"name","number"},
                new int[]{android.R.id.text1,android.R.id.text2});
        listView.setAdapter(adapter);

        sv=findViewById(R.id.sv);
        sv.setOnClickCheckedListener(new SwitchView.onClickCheckedListener() {
            @Override
            public void onClick() {
                if(isPic){
                    isPic=false;
                }else{
                    isPic=true;
                }
            }
        });

        tv1 = findViewById(R.id.tv1);
        tv1.setText("http://"+NetWorkUtils.getLocalIpAddress(this)+":8080");
        tv1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                /**之前的应用过期的方法，clipboardManager.setText(copy);*/
                assert cmb != null;
                cmb.setPrimaryClip(ClipData.newPlainText(null,tv1.getText()));
                T.showLongSuccess(context,"复制成功",true);
                return false;
            }
        });

        server = HttpServer.getInstance(8080);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtil.deleteFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/easyCopy").getAbsolutePath());
                T.showLongSuccess(context,"缓存清理成功",true);
            }
        });

        mcv = findViewById(R.id.calendarView);
        mcv.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String month = String.valueOf(date.getMonth()+1);
                month = month.length()==2?month:"0"+month;
                String day = String.valueOf(date.getDay());
                day = day.length()==2?day:"0"+day;
                pickDate=date.getYear()+"-"+month+"-"+day;
                changeData(pickDate);
            }
        });
        mcv.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                StringBuffer buffer = new StringBuffer();
                int yearOne = day.getYear();
                int monthOne = day.getMonth() + 1;
                buffer.append(yearOne).append("年").append(monthOne).append("月");
                return buffer;
            }
        });
        mcv.setSelectedDate(CalendarDay.today());

        //数据读写权限
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);
    }
    //创建监听权限的接口对象
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
            scanImages();
            scanVideos();
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(currentTime);
            pickDate=dateString;
            changeData(dateString);
        }

        @Override
        public void forbitPermissons() {
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public void scanImages(){
        map_pic = new HashMap();
        map_pic_size = new HashMap();
        map_pic_id = new HashMap();
        int i=0;
        String[] IMAGES = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_TAKEN};
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGES,null,null,null);
        if(cursor != null){
            while(cursor.moveToNext()){
                i++;
                String path = cursor.getString(0);
                String bucketName = cursor.getString(1);
                String id = cursor.getString(2);
                long size = cursor.getLong(3);
                long date = cursor.getLong(4);
                String dateStr="";
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat sdf11 = new SimpleDateFormat(pattern);
                Date date2 = new Date(date);
                dateStr = sdf11.format(date2);
                L.i(path+","+bucketName+","+id+","+size+","+dateStr);

                //================================================
                if(map_pic.get(dateStr)==null){
                    map_pic.put(dateStr,new ArrayList<String>());
                }
                map_pic.get(dateStr).add(path);

                if(map_pic_id.get(dateStr)==null){
                    map_pic_id.put(dateStr,new ArrayList<String>());
                }
                map_pic_id.get(dateStr).add(id);

                if(map_pic_size.get(dateStr)==null){
                    map_pic_size.put(dateStr,new ArrayList<Long>());
                }
                map_pic_size.get(dateStr).add(size);
            }
            cursor.close();
        }
        L.i("照片总数："+i);
    }

    public void scanVideos(){
        map_vod = new HashMap();
        map_vod_size = new HashMap();
        map_vod_id = new HashMap();

        int i=0;
        String[] VIDEOS = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media._ID};
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,VIDEOS,null,null,null);
        if(cursor != null){
            while(cursor.moveToNext()){
                i++;
                String path = cursor.getString(0);
                String bucketName = cursor.getString(1);
                String mimeType = cursor.getString(2);
                long size = cursor.getLong(3);
                long date = cursor.getLong(4);
                String id = cursor.getString(5);

                String dateStr="";
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat sdf11 = new SimpleDateFormat(pattern);
                Date date2 = new Date(date);
                dateStr = sdf11.format(date2);
                L.i(path+","+bucketName+","+mimeType+","+size+","+dateStr);

                //================================================
                if(map_vod.get(dateStr)==null){
                    map_vod.put(dateStr,new ArrayList<String>());
                }
                map_vod.get(dateStr).add(path);

                if(map_vod_size.get(dateStr)==null){
                    map_vod_size.put(dateStr,new ArrayList<Long>());
                }
                map_vod_size.get(dateStr).add(size);

                if(map_vod_id.get(dateStr)==null){
                    map_vod_id.put(dateStr,new ArrayList<String>());
                }
                map_vod_id.get(dateStr).add(id);
            }
            cursor.close();
        }
        L.i("视频总数："+i);
    }

    @Override
    public void onBackPressed() {
        server.stop();
        super.finish();
    }

    public void changeData(String key){
        List<Map<String,String>> listmaps=new ArrayList<>();
        List<String> pic_list = map_pic.get(key);
        List<String> vod_list = map_vod.get(key);
        List<Long> pic_size_list = map_pic_size.get(key);
        List<Long> vod_size_list = map_vod_size.get(key);

        Map picMap=new HashMap();
        listmaps.add(picMap);
        Map vodMap=new HashMap();
        listmaps.add(vodMap);

        if(pic_list!=null){
            picMap.put("number", "照片数量:"+pic_list.size());
        }else{
            picMap.put("number", "照片数量:0");
        }

        if(vod_list!=null){
            vodMap.put("number", "视频数量:"+vod_list.size());
        }else{
            vodMap.put("number", "视频数量:0");
        }

        if(pic_size_list!=null){
            long data=0l;
            for (int i = 0; i <pic_size_list.size() ; i++) {
                data+=pic_size_list.get(i);
            }
            DecimalFormat df = new DecimalFormat("#0.00");
            String littleStr=df.format((double) data/(1024*1024));
            picMap.put("size", littleStr+"MB");
        }else{
            picMap.put("size", "0MB");
        }

        if(vod_size_list!=null){
            long data=0l;
            for (int i = 0; i <vod_size_list.size() ; i++) {
                data+=vod_size_list.get(i);
            }
            DecimalFormat df = new DecimalFormat("#.00");
            String littleStr=df.format((double) data/(1024*1024));
            vodMap.put("size", littleStr+"MB");
        }else{
            vodMap.put("size", "0MB");
        }

        SimpleAdapter adapter =new SimpleAdapter(MainActivity.this, listmaps,
                android.R.layout.simple_expandable_list_item_2, new String[]{"number","size"},
                new int[]{android.R.id.text1,android.R.id.text2});

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),WaterFallActivity.class);
                if(position==0){
                    intent.putExtra("type","pic");
                }else {
                    intent.putExtra("type","vod");
                }
                //如果点击按钮开启了一个activity 当这个activity关闭的时候我想要获取到这个activity的数据需要使用下面的这个方法
                context.startActivity(intent);
            }
        });
    }
}
