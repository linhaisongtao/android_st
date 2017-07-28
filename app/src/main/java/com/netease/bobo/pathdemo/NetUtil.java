package com.netease.bobo.pathdemo;

import okhttp3.OkHttpClient;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class NetUtil {
    private static final OkHttpClient sOKHttpClient;

    static {
        sOKHttpClient = new OkHttpClient.Builder().build();
    }

    public static OkHttpClient getOkHttpClient() {
        return sOKHttpClient;
    }
}
