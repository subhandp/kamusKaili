package com.uya.drawyerlayout;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by root on 06/04/18.
 */

public class MyApplicationStetho extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
