package com.netease.bobo.pathdemo;

import android.app.Application;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class App extends Application{
    private static  App sApp;
    public App() {
        this.sApp = this;
    }

    public static App getApp() {
        return sApp;
    }
}
