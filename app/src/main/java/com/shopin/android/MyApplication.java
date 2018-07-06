package com.shopin.android;

import android.app.Application;

import com.shopin.android.anr.ANRWatchDog;

/**
 * @author will on 2018/7/6 14:55
 * @email pengweiqiang64@163.com
 * @description
 * @Version
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new ANRWatchDog().start();

    }
}
