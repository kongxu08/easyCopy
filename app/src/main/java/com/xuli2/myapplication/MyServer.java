package com.xuli2.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

public class MyServer extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 创建对象，端口我这里设置为8080
        HttpServer myServer = new HttpServer(8080);
        try {
            // 开启HTTP服务
            myServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
