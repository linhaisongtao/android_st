package com.netease.bobo.pathdemo.stock.model;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.netease.bobo.pathdemo.NetUtil;
import com.netease.bobo.pathdemo.util.FileUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class StockManager {
    private static final String TAG = "StockManager";
    private static StockManager sStockManager = new StockManager();
    private Map<String, StockInfo> mStockInfoMap = new HashMap<>();
    private List<SBasicInfo> mSBasicInfos = new ArrayList<>();

    public static StockManager getStockManager() {
        return sStockManager;
    }

    public StockInfo getStockInfo(String code) {
        //内存缓存
        StockInfo info = mStockInfoMap.get(code);

        //文件缓存
        if (info == null || info.isEmpty()) {
            info = getStockInfoFromStorage(code);
        }

        if (info == null || info.isEmpty()) {
            info = requestStockInfo(code);
            mStockInfoMap.put(code, info);
            saveStockInfoToStorage(code, info);
            return info;
        } else {
            return info;
        }
    }

    private StockInfo getStockInfoFromStorage(String code) {
        Log.i(TAG, "getStockInfoFromStorage: read stock_info from storage " + code);
        String fileName = "stock_info_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "_" + code + ".json";
        String content = FileUtil.read(FileUtil.openFile(fileName));
        StockInfo stockInfo = JSON.parseObject(content, StockInfo.class);
        return stockInfo;
    }

    private void saveStockInfoToStorage(String code, StockInfo info) {
        Log.i(TAG, "saveStockInfoToStorage: save stock_info to storage " + code);
        String fileName = "stock_info_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "_" + code + ".json";
        FileUtil.write(FileUtil.openFile(fileName), JSON.toJSONString(info));
    }

    private StockInfo requestStockInfo(String code) {
        StockInfo stockInfo = new StockInfo();
        stockInfo.mRoes = requestRoeInfo(code);
        stockInfo.mPbs = requestPbInfo(code);
        return stockInfo;
    }

    private List<Pb> requestPbInfo(String code) {
        Log.i(TAG, "requestPbInfo: read pb from net " + code);
        Request.Builder builder = new Request.Builder();
        builder.url(String.format("https://www.joudou.com/stockinfogate/stock/logpepbs/%s.SH?_t=%d", code, System.currentTimeMillis()));
        Call call = NetUtil.getOkHttpClient().newCall(builder.build());
        try {
            Response response = call.execute();
            String content = response.body().string();
            PbList pbList = JSON.parseObject(content, PbList.class);
            List<List<String>> list = pbList.data.data;
            List<Pb> pbs = new ArrayList<>();
            if (pbList.data != null && list != null) {
                for (int i = 0; i < list.size(); i++) {
                    List<String> strings = list.get(i);
                    Pb pb = new Pb();
                    pb.date = strings.get(0);
                    pb.pb = Float.parseFloat(strings.get(3));
                    pbs.add(pb);
                }
            }
            return pbs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Roe> requestRoeInfo(String code) {
        Log.i(TAG, "requestRoeInfo: read roe from net " + code);
        String marketName = code.startsWith("6") ? "SH" : "SZ";
        long timestamp = System.currentTimeMillis();
        String url = String.format("https://xueqiu.com/stock/f10/finmainindex.json?symbol=%s%s&page=1&size=100&_=%d", marketName, code, timestamp);
        Request.Builder builder = new Request.Builder();
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        builder.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        builder.addHeader("Cookie", "s=fu11ppjlp2; u=581500552864127; device_id=39db6328c06d3004e8366340fe63bf00; aliyungf_tc=AQAAAJNnvESy/w4A/Tdr2o46dn5T7CjF; xq_a_token=82d9cefaa0793743cb186e53294ec0e61ac2abec; xq_r_token=11b86433a20d1d1eef63ecc12252297196a20e10; __utmt=1; __utma=1.1763699408.1500552864.1500552864.1501223896.2; __utmb=1.4.10.1501223896; __utmc=1; __utmz=1.1500552864.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); Hm_lvt_1db88642e346389874251b5a1eded6e3=1499949737,1500294058,1500552864,1501223896; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1501223911");
        builder.url(url);
        builder.method("GET", null);
        Call call = NetUtil.getOkHttpClient().newCall(builder.build());
        try {
            Response response = call.execute();
            String content = response.body().string();
            RoeList roeList = JSON.parseObject(content, RoeList.class);
            if (roeList != null && roeList.list != null) {
                Collections.reverse(roeList.list);
                return roeList.list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<SBasicInfo> getSelectedSList() {
        if (mSBasicInfos.isEmpty()) {
            //find from file
            String content = FileUtil.read(FileUtil.openFile("selected.json"));
            mSBasicInfos = JSON.parseArray(content, SBasicInfo.class);
            if (mSBasicInfos == null) {
                mSBasicInfos = new ArrayList<>();
            }
        }
        return mSBasicInfos;
    }

    public void addSBasicInfo(SBasicInfo info) {
        List<SBasicInfo> infos = getSBasicInfos();
        for (SBasicInfo basicInfo : infos) {
            if (info.code.equals(basicInfo.code)) {
                return;
            }
        }
        infos.add(0, info);
        saveSBasicInfoList();
    }

    private List<SBasicInfo> getSBasicInfos() {
        if (mSBasicInfos == null) {
            mSBasicInfos = new ArrayList<>();
        }
        return mSBasicInfos;
    }

    public void deleteSBasicInfo(String code) {
        if (mSBasicInfos != null) {
            for (int i = mSBasicInfos.size() - 1; i >= 0; i--) {
                if (code.equals(mSBasicInfos.get(i).code)) {
                    mSBasicInfos.remove(i);
                }
            }
        }
        saveSBasicInfoList();
    }

    private void saveSBasicInfoList() {
        if (mSBasicInfos != null) {
            String jsonString = JSON.toJSONString(mSBasicInfos);
            FileUtil.write(FileUtil.openFile("selected.json"), jsonString);
        }
    }
}
