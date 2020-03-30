package com.xuli2.myapplication;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    private static final String TAG = "Http";

    private static HttpServer server;
    private static final String REQUEST_ROOT = "/";

    public HttpServer(int port) {
        super(port);
    }

    public static HttpServer getInstance(int port) {
        if (server == null) {
            server = new HttpServer(port);
        }
        return server;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        if (StringUtils.isEmpty(MainActivity.pickDate)) {
            StringBuilder builder = new StringBuilder();
            builder.append("清在手机上选择需要下载的媒体日期");// 反馈给调用者的数据
            return newFixedLengthResponse(Response.Status.OK, "application/json;charset=UTF-8", builder.toString());
        }
        if (uri.length() > 1) {

            if (uri.equals("/upload.do")) {
                if (Method.POST.equals(session.getMethod())) {
                    Map<String, String> files = new HashMap<>();
                    try {
                        session.parseBody(files);
                    } catch (IOException ioe) {
                        return response404("Internal Error IO Exception: 失败 " + ioe.getMessage());
                    } catch (ResponseException re) {
                        return newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, "失败 " + re.getMessage());
                    }
                    // ▼ 2、copy file to target path xiaoyee ▼
                    Map<String, String> params = session.getParms();
                    for (Map.Entry<String, String> entry : files.entrySet()) {
                        try {
                            final String paramsKey = entry.getKey();
                            final String tmpFilePath = files.get(paramsKey);
                            SimpleDateFormat myFmt1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                            final String fileName = myFmt1.format(new Date()) + params.get(paramsKey).substring(params.get(paramsKey).lastIndexOf("."));
                            final File tmpFile = new File(tmpFilePath);
                            FileUtil.copyFile(tmpFile, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/" + fileName);
                        } catch (IOException ioe) {
                            return response404("Internal Error IO Exception: 失败 " + ioe.getMessage());
                        }
                    }
                } else {
                    return response404("Error 404:失败 未找到文件");
                }
                StringBuilder builder = new StringBuilder();
                builder.append("success");
                return newFixedLengthResponse(Response.Status.OK, "application/html;charset=UTF-8", builder.toString());
            } else if (uri.lastIndexOf(".") > 0 && uri.substring(uri.lastIndexOf(".") + 1).equals("css")) {
                StringBuilder builder = new StringBuilder();
                builder.append(FileUtil.getFromAssets(uri.substring(1), MainActivity.context));// 反馈给调用者的数据
                return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/css", builder.toString());
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append(FileUtil.getFromAssets(uri.substring(1), MainActivity.context));// 反馈给调用者的数据
                return newFixedLengthResponse(builder.toString());
            }
        }

        if (REQUEST_ROOT.equals(session.getUri()) || session.getUri().equals("")) {
            return responseFile(MainActivity.pickDate);
        }
        return response404(session.getUri());
    }


    //对于请求文件的，返回下载的文件
    public Response responseFile(String pickDate) {



        if (!FileUtil.isFileExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/easyCopy").getAbsolutePath())) {
            FileUtil.creatSDDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/easyCopy").getAbsolutePath());
        }


        Response response = null;
        Map<String, List<String>> map = null;
        String ext = null;
        List<String> list = null;
        if (MainActivity.list_uri_click.isEmpty()) {
            if (MainActivity.isPic) {
                map = MainActivity.map_pic;
                ext = "_图片";
            } else {
                map = MainActivity.map_vod;
                ext = "_视频";
            }
            pickDate = pickDate.replace("/", "");
            list = map.get(pickDate);
        } else {
            ext = "_" + String.valueOf((int) (Math.random() * 1000000));
            list = MainActivity.list_uri_click;
        }

        if (list == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("所选日期没有媒体");// 反馈给调用者的数据
            return newFixedLengthResponse(Response.Status.OK, "application/json;charset=UTF-8", builder.toString());
        }

        String[] strings = new String[list.size()];

        list.toArray(strings);

        if (!FileUtil.isFileExist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/easyCopy" + pickDate + ext + ".zip").getAbsolutePath())) {
            ZipUtil.zip(strings, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/easyCopy/" + pickDate + ext + ".zip").getAbsolutePath());
        }

        try {
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/easyCopy/" + pickDate + ext + ".zip").getAbsolutePath();
            //文件输入流
            FileInputStream fis = new FileInputStream(filePath);
            // 返回OK，同时传送文件，为了安全这里应该再加一个处理，即判断这个文件是否是我们所分享的文件，避免客户端访问了其他个人文件
            response = newFixedLengthResponse(Response.Status.OK, "application/octet-stream", fis, fis.available());
            response.addHeader("Content-Disposition", "attachment; filename=" + filePath.substring(filePath.lastIndexOf("/") + 1));
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
