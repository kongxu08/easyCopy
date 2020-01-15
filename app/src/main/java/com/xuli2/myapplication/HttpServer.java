package com.xuli2.myapplication;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    private static final String TAG = "Http";

    private static HttpServer server;
    private static  final String REQUEST_ROOT = "/";
    public HttpServer(int port) {
        super(port);
    }

    public static HttpServer getInstance(int port) {
        if(server==null){
            server = new HttpServer(port);
        }
        return server;
    }
    @Override
    public Response serve(IHTTPSession session) {
        if(StringUtils.isEmpty(MainActivity.pickDate)){
            StringBuilder builder = new StringBuilder();
            builder.append("清在手机上选择需要下载的媒体日期");// 反馈给调用者的数据
            return newFixedLengthResponse(Response.Status.OK,"application/json;charset=UTF-8",builder.toString());
        }
        return responseFile(MainActivity.pickDate);
    }



    //对于请求文件的，返回下载的文件
    public Response responseFile(String pickDate){

/*        View loadingView = MainActivity.loadingView;
        if(loadingView != null){
            if(loadingView.getVisibility()==View.GONE){
                loadingView.setVisibility(View.VISIBLE);
            }
        }*/

        if(!FileUtil.isFileExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/easyCopy").getAbsolutePath())){
            FileUtil.creatSDDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/easyCopy").getAbsolutePath());
        }



        Response response = null;
        Map<String,List<String>> map = null;
        String ext=null;
        List<String> list = null;
        if(MainActivity.list_uri_click.isEmpty()){
            if(MainActivity.isPic){
                map = MainActivity.map_pic;
                ext="_图片";
            }else{
                map = MainActivity.map_vod;
                ext="_视频";
            }
            pickDate = pickDate.replace("/","");
            list = map.get(pickDate);
        }else{
            ext="_"+String.valueOf((int)(Math.random()*1000000));
            list = MainActivity.list_uri_click;
        }

        if (list==null){
            StringBuilder builder = new StringBuilder();
            builder.append("所选日期没有媒体");// 反馈给调用者的数据
//            loadingView.setVisibility(View.GONE);
            return newFixedLengthResponse(Response.Status.OK,"application/json;charset=UTF-8",builder.toString());
        }

        String[] strings = new String[list.size()];

        list.toArray(strings);

        if(!FileUtil.isFileExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/easyCopy"+pickDate+ext+".zip").getAbsolutePath())){
            ZipUtil.zip(strings,Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/easyCopy/"+pickDate+ext+".zip").getAbsolutePath());
        }

        try {
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/easyCopy/"+pickDate+ext+".zip").getAbsolutePath();
            //文件输入流
            FileInputStream fis = new FileInputStream(filePath);
            // 返回OK，同时传送文件，为了安全这里应该再加一个处理，即判断这个文件是否是我们所分享的文件，避免客户端访问了其他个人文件
            response =newFixedLengthResponse(Response.Status.OK,"application/octet-stream",fis,fis.available());
            response.addHeader("Content-Disposition", "attachment; filename="+filePath.substring(filePath.lastIndexOf("/")+1));
//            loadingView.setVisibility(View.GONE);
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        loadingView.setVisibility(View.GONE);
        return response404(pickDate);
    }
    //页面不存在，或者文件不存在时
    public Response response404(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html>body>");
        builder.append("Sorry,Can't Found" + url + " !");
        builder.append("</body></html>\n");
        return newFixedLengthResponse(builder.toString());
    }

}
