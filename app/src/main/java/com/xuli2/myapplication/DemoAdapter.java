package com.xuli2.myapplication;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.BaseViewHolder> {
    private ArrayList<Bitmap> bitmapList = new ArrayList<>();
    List<String> selecteds = new ArrayList<>();

    public void replaceAll(List<Bitmap> list) {
        bitmapList.clear();
        if (list != null && list.size() > 0) {
            bitmapList.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * 插入数据使用notifyItemInserted，如果要使用插入动画，必须使用notifyItemInserted
     * 才会有效果。即便不需要使用插入动画，也建议使用notifyItemInserted方式添加数据，
     * 不然容易出现闪动和间距错乱的问题
     */
    public void addData(int position, ArrayList<Bitmap> list) {
        bitmapList.addAll(position, list);
        notifyItemInserted(position);
    }

    //移除数据使用notifyItemRemoved
    public void removeData(int position) {
        bitmapList.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public DemoAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OneViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_water_fall, parent, false));
    }

    @Override
    public void onBindViewHolder(DemoAdapter.BaseViewHolder holder, int position) {
        holder.setData(bitmapList.get(position), position);
    }


    @Override
    public int getItemCount() {
        return bitmapList != null ? bitmapList.size() : 0;
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        void setData(Object data, int position) {

        }
    }

    private class OneViewHolder extends BaseViewHolder {
        private ImageView ivImage;

        public OneViewHolder(View view) {
            super(view);
            ivImage = view.findViewById(R.id.iv_item_water_fall);
        }

        @Override
        void setData(Object data, final int position) {
            if (data != null) {
                ivImage.setImageBitmap((Bitmap) data);
                ivImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView iv = (ImageView) v;

                        if(WaterFallActivity.type.equals("pic")){
                            click(iv,position,MainActivity.map_pic_id.get(MainActivity.pickDate), MediaStore.Images.Media.DATA,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media._ID);
                        }else if(WaterFallActivity.type.equals("vod")) {
                            click(iv,position,MainActivity.map_vod_id.get(MainActivity.pickDate), MediaStore.Video.Media.DATA, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media._ID);
                        }
                    }
                });
                //需要Item高度不同才能出现瀑布流的效果，此处简单粗暴地设置一下高度
                if (position % 2 == 0) {
                    ivImage.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250));
                } else {
                    ivImage.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 350));
                }
            }
        }
    }

    private void click(ImageView iv,int position,List<String> ids,String column,Uri uri,String _id){
        if (iv.getAlpha() == 0.5f) {
            iv.setAlpha(1f);
            selecteds.remove(ids.get(position));
        } else {
            iv.setAlpha(0.5f);
            selecteds.add(ids.get(position));
        }

        String[] IMAGES = {
               column
        };

        List<String> list = new ArrayList<>();
        for (int i = 0; i < selecteds.size() ; i++) {
            String value = selecteds.get(i);
            list.add("'"+value+"'");
        }
        String[] args = new String[]{TextUtils.join(",",list)};

        ContentResolver cr = WaterFallActivity.context.getContentResolver();
        Cursor cursor = cr.query(uri, IMAGES, _id+" in("+args[0]+") ",null, null);
        if (cursor != null) {
            MainActivity.list_uri_click.clear();
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                MainActivity.list_uri_click.add(path);
            }
        }
    }

}

