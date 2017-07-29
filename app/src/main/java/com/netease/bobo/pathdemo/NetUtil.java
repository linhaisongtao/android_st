package com.netease.bobo.pathdemo;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.OkHttpClient;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class NetUtil {
    private static final OkHttpClient sOKHttpClient;

    static {
        sOKHttpClient = new OkHttpClient.Builder()
                .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.getApp())))
                .build();
    }

    public static OkHttpClient getOkHttpClient() {
        return sOKHttpClient;
    }
}
