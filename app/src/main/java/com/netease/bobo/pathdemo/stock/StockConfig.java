package com.netease.bobo.pathdemo.stock;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.netease.bobo.pathdemo.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class StockConfig {
    private static StockConfig stockConfig;

    public float FUTURE_ROE_RATIO = 0.9f;
    public int BENEFIT_YEAR_COUNT = 10;
    public int ROE_SHOW_YEAR_COUNT = 10;
    public int AVERAGE_ROE_COUNT = 5;
    public float SELL_PB_POSITION = 0.2f;
    public int PB_YEAR_COUNT = 5;
    public int CIRCLE_RADIUS = 3;
    public float CUBIC_INTENSITY = 0.5f;
    public float LINE_WIDTH = 2;
    public List<String> WHITE_LIST = new ArrayList<String>() {{
        add("selected.json");
    }};

    public static StockConfig getStockConfig() {
        if (stockConfig == null) {
            refresh();
        }
        return stockConfig;
    }

    public static void refresh() {
        SharedPreferences sp = App.getApp().getSharedPreferences("stock.sp", Context.MODE_PRIVATE);
        String jsonString = sp.getString("stock_config", "{}");
        stockConfig = JSON.parseObject(jsonString, StockConfig.class);
    }

    public static void saveConfig(StockConfig config) {
        SharedPreferences sp = App.getApp().getSharedPreferences("stock.sp", Context.MODE_PRIVATE);
        sp.edit().putString("stock_config", JSON.toJSONString(config)).apply();
        refresh();
    }
}
