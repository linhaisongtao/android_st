package com.netease.bobo.pathdemo.stock.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.netease.bobo.pathdemo.R;
import com.netease.bobo.pathdemo.stock.StockConfig;
import com.netease.bobo.pathdemo.stock.model.SBasicInfo;
import com.netease.bobo.pathdemo.stock.model.StockInfo;
import com.netease.bobo.pathdemo.stock.model.StockManager;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/27.
 */

public class StockDetailActivity extends AppCompatActivity {
    private static final String TAG = "StockDetailActivity";
    private SBasicInfo mInfo;

    public static void start(Context context) {
        start(context, new SBasicInfo("600016", "msyh"));
    }

    public static void start(Context context, SBasicInfo info) {
        Intent intent = new Intent(context, StockDetailActivity.class);
        intent.putExtra("info", info);
        context.startActivity(intent);
    }

    static void makeChartInit(LineChart chart) {
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setEnabled(true);
        chart.getAxisRight().setEnabled(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.setScaleYEnabled(false);
        chart.animateXY(2000, 2000);

        YAxis y = chart.getAxisLeft();
        y.setGridLineWidth(1);
        y.setGridColor(R.color.transGray);
        y.setGridDashedLine(new DashPathEffect(new float[]{1f, 2f}, 5f));

        XAxis x = chart.getXAxis();
        x.setGridLineWidth(1);
        x.setGridColor(R.color.transGray);
        x.setGridDashedLine(new DashPathEffect(new float[]{1f, 2f}, 5f));
        x.setDrawGridLines(false);

        chart.setTouchEnabled(false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInfo = (SBasicInfo) getIntent().getSerializableExtra("info");
        getSupportActionBar().setTitle(mInfo.name + "[" + mInfo.code + "]");

        setContentView(R.layout.activity_stock_detail);

        Observable.create(new ObservableOnSubscribe<StockInfo>() {
            @Override
            public void subscribe(ObservableEmitter<StockInfo> e) throws Exception {
                StockInfo stockInfo = StockManager.getStockManager().getStockInfo(mInfo.code);
                e.onNext(stockInfo);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<StockInfo>() {
                    @Override
                    public void accept(StockInfo o) throws Exception {
                        Log.e(TAG, "accept: " + o);
                        View rootView = getWindow().getDecorView();
                        new PbChartProcessor().showPbChart(rootView,
                                o.getPastYear(StockConfig.getStockConfig().PB_YEAR_COUNT),
                                o.getPbPosition(StockConfig.getStockConfig().PB_YEAR_COUNT, 0.2f),
                                o.getPbPosition(StockConfig.getStockConfig().PB_YEAR_COUNT, 0.5f),
                                o.getPbPosition(StockConfig.getStockConfig().PB_YEAR_COUNT, 0.8f),
                                o.getCurrentPbPosition(StockConfig.getStockConfig().PB_YEAR_COUNT));

                        new RoeChartProcessor().showRoeChart(rootView,
                                o.getPastRoeYearReporters(StockConfig.getStockConfig().ROE_SHOW_YEAR_COUNT),
                                o.getRoeAverageRoe(StockConfig.getStockConfig().AVERAGE_ROE_COUNT));

                        new BenefitChartProcessor().showBenefitChart(rootView,
                                StockConfig.getStockConfig().BENEFIT_YEAR_COUNT,
                                (float) (0.01 * o.getRoeAverageRoe(StockConfig.getStockConfig().AVERAGE_ROE_COUNT) * StockConfig.getStockConfig().FUTURE_ROE_RATIO),
                                o.getNowPb(),
                                o.getPbPosition(StockConfig.getStockConfig().PB_YEAR_COUNT, StockConfig.getStockConfig().SELL_PB_POSITION));

                    }
                });
    }


}
