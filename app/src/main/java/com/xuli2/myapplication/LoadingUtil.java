package com.xuli2.myapplication;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingUtil {
    public static View creatLoadingView(Activity activity) {
        // activity根部的ViewGroup，其实是一个FrameLayout
        FrameLayout rootContainer =  (FrameLayout)activity.findViewById(android.R.id.content);    //拿到屏幕的布局
        FrameLayout frameLayout = new FrameLayout(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(params); //设置frameLayout的长宽
        View view = activity.getLayoutInflater().inflate(R.layout.activity_loading, null);
        frameLayout.addView(view);
        // 将菊花添加到FrameLayout中
        rootContainer.addView(frameLayout);
        return view;
    }
}
