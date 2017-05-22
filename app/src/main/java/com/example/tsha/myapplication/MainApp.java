package com.example.tsha.myapplication;

import android.app.Application;

import com.example.tsha.myapplication.util.LogUtils;

/**
 * Created by ts.ha on 2017-04-21.
 */

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.configLog(this);
    }
}
