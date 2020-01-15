package com.xuli2.myapplication;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

public class ImageUtils {
    /**
     * 根据图片的ID得到缩略图
     * @param cr
     * @param imageId
     * @return
     */
    public static Bitmap getThumbnailsFromImageId(ContentResolver cr, String imageId) {
        if (imageId == null || imageId.equals(""))
            return null;

        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        long imageIdLong = Long.parseLong(imageId);
        //via imageid get the bimap type thumbnail in thumbnail table.
        bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, imageIdLong, MediaStore.Images.Thumbnails.MINI_KIND, options);

        return bitmap;
    }

    public static Bitmap getThumbnailsFromVodId(ContentResolver cr, String imageId) {
        if (imageId == null || imageId.equals(""))
            return null;

        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        long imageIdLong = Long.parseLong(imageId);
        //via imageid get the bimap type thumbnail in thumbnail table.
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, imageIdLong, MediaStore.Images.Thumbnails.MINI_KIND, options);

        return bitmap;
    }
}
