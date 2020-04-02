package com.steelmate.samplefloatingwindow;

import android.app.Application;

/**
 * @author xt on 2019/10/11 16:23
 */
public class MyApp extends Application {
    public static MyApp                   sMyApp;
    public        TpmsFloatingWindowModel tpmsFloatingWindowModel;


    @Override
    public void onCreate() {
        super.onCreate();
        sMyApp = this;

        tpmsFloatingWindowModel = new TpmsFloatingWindowModel();
    }


}
