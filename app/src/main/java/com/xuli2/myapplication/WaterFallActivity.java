package com.xuli2.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class WaterFallActivity extends AppCompatActivity {

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();

    private RecyclerView rv_waterfall;
    private DemoAdapter adapter;
    public static Context context;

    private SmartRefreshLayout refreshlayout;

    static String type=null;

    void init(){
        context=this;

        type = getIntent().getStringExtra("type");

        List<String> list = null;
        if(type.equals("pic")){
           list = MainActivity.map_pic_id.get(MainActivity.pickDate);
        }else if(type.equals("vod")){
            list = MainActivity.map_vod_id.get(MainActivity.pickDate);
        }

        if(list!=null) {
            for (int i = 0; i <list.size() ; i++) {
                Bitmap bitmap = null;
                if(type.equals("pic")){
                    bitmap = ImageUtils.getThumbnailsFromImageId(this.getContentResolver(),list.get(i));
                }else if(type.equals("vod")){
                    bitmap = ImageUtils.getThumbnailsFromVodId(this.getContentResolver(),list.get(i));
                }
                bitmaps.add(bitmap);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_fall);

        init();

        refreshlayout = findViewById(R.id.refreshlayout);

        rv_waterfall = findViewById(R.id.rv_waterfall);
        rv_waterfall.setHasFixedSize(true);
        rv_waterfall.setItemAnimator(null);
        //垂直方向的2列
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //防止Item切换
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rv_waterfall.setLayoutManager(layoutManager);
        final int spanCount = 2;
        rv_waterfall.addItemDecoration(new StaggeredDividerItemDecoration(this,10,spanCount));

        //解决底部滚动到顶部时，顶部item上方偶尔会出现一大片间隔的问题
        rv_waterfall.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int[] first = new int[spanCount];
                layoutManager.findFirstCompletelyVisibleItemPositions(first);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (first[0] == 1 || first[1] == 1)) {
                    layoutManager.invalidateSpanAssignments();
                }
            }
        });

        adapter = new DemoAdapter();
        rv_waterfall.setAdapter(adapter);
        adapter.replaceAll(bitmaps);

        //设置下拉刷新和上拉加载监听
        refreshlayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.replaceAll(getData());
//                        refreshLayout.finishRefresh();
//                    }
//                },500);
                refreshLayout.finishRefresh();
            }
        });

        refreshlayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.addData(adapter.getItemCount(),getData());
//                        refreshLayout.finishLoadMore();
//                    }
//                },500);
                refreshLayout.finishLoadMore();
            }
        });

    }

    @Override
    public void onBackPressed() {
        MainActivity.list_uri_click.clear();
        super.onBackPressed();
    }

}
