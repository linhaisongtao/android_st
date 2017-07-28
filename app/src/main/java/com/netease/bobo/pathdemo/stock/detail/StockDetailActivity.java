package com.netease.bobo.pathdemo.stock.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.netease.bobo.pathdemo.R;
import com.netease.bobo.pathdemo.stock.StockConfig;
import com.netease.bobo.pathdemo.stock.model.Pb;
import com.netease.bobo.pathdemo.stock.model.Roe;
import com.netease.bobo.pathdemo.stock.model.SBasicInfo;
import com.netease.bobo.pathdemo.stock.model.StockInfo;
import com.netease.bobo.pathdemo.stock.model.StockManager;
import com.netease.bobo.pathdemo.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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

    private static void makeChartInit(LineChart chart) {
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
        y.setGridDashedLine(new DashPathEffect(new float[]{1f, 2f}, 2f));

        XAxis x = chart.getXAxis();
        x.setGridLineWidth(1);
        x.setGridColor(R.color.transGray);
        x.setGridDashedLine(new DashPathEffect(new float[]{1f, 2f}, 2f));
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
                        showPbChart(o.getPastYear(StockConfig.PB_YEAR_COUNT), o.getPbPosition(StockConfig.PB_YEAR_COUNT, 0.2f),
                                o.getPbPosition(StockConfig.PB_YEAR_COUNT, 0.5f), o.getPbPosition(StockConfig.PB_YEAR_COUNT, 0.8f));
                        showRoeChart(o.getPastRoeYearReporters(StockConfig.ROE_SHOW_YEAR_COUNT), o.getRoeAverageRoe(StockConfig.ROE_SHOW_YEAR_COUNT));
                        showBenefitChart(StockConfig.BENEFIT_YEAR_COUNT + 1,
                                (float) (0.01 * o.getRoeAverageRoe(StockConfig.ROE_SHOW_YEAR_COUNT) * StockConfig.FUTURE_ROE_RATIO),
                                o.getNowPb(), o.getPbPosition(StockConfig.PB_YEAR_COUNT, StockConfig.SELL_PB_POSITION));
                    }
                });
    }

    private void showPbChart(final List<Pb> pbs, final float pb20, final float pb50, final float pb80) {
        final LoadingView loadingView = (LoadingView) findViewById(R.id.mPbLoadingView);
        final LineChart chart = (LineChart) findViewById(R.id.mPbChart);
        Observable.just(pbs).flatMap(new Function<List<Pb>, ObservableSource<LineData>>() {
            @Override
            public ObservableSource<LineData> apply(List<Pb> roes) throws Exception {
                LineData lineData = new LineData();

                List<Entry> roeEntries = new ArrayList<Entry>();
                List<Entry> pb20s = new ArrayList<Entry>();
                List<Entry> pb50s = new ArrayList<Entry>();
                List<Entry> pb80s = new ArrayList<Entry>();
                for (int i = 0; i < roes.size(); i++) {
                    roeEntries.add(new Entry(i, roes.get(i).pb));
                    pb20s.add(new Entry(i, pb20));
                    pb50s.add(new Entry(i, pb50));
                    pb80s.add(new Entry(i, pb80));
                }
                LineDataSet set = new LineDataSet(roeEntries, "pb");
                set.setDrawCircles(false);

                LineDataSet set20 = new LineDataSet(pb20s, "pb20");
                set20.setDrawCircles(false);
                set20.setLineWidth(1);
                set20.setColor(Color.GREEN);
                set20.setFormLineWidth(1);
                set20.setHighlightLineWidth(1);
                set20.setFormLineDashEffect(new DashPathEffect(new float[]{1f, 2f}, 2f));

                LineDataSet set50 = new LineDataSet(pb50s, "pb50");
                set50.setDrawCircles(false);
                set50.setLineWidth(1);
                set50.setColor(Color.YELLOW);
                set50.setFormLineWidth(1);
                set50.setHighlightLineWidth(1);
                set50.setFormLineDashEffect(new DashPathEffect(new float[]{1f, 2f}, 2f));

                LineDataSet set80 = new LineDataSet(pb80s, "pb80");
                set80.setDrawCircles(false);
                set80.setLineWidth(1);
                set80.setColor(Color.RED);
                set80.setFormLineWidth(1);
                set80.setHighlightLineWidth(1);
                set80.setFormLineDashEffect(new DashPathEffect(new float[]{1f, 2f}, 2f));

                lineData.addDataSet(set);
                lineData.addDataSet(set20);
                lineData.addDataSet(set50);
                lineData.addDataSet(set80);
                return Observable.just(lineData);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LineData>() {
                    @Override
                    public void accept(LineData lineData) throws Exception {
                        loadingView.hideLoading();
                        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                int index = (int) value;
                                return pbs.get(index).date;
                            }
                        });
                        makeChartInit(chart);
                        chart.setData(lineData);

                        TextView mPbDetailTextView = (TextView) findViewById(R.id.mPbDetailTextView);
                        mPbDetailTextView.setText(String.format("pbNow:%.2f,pb20:%.2f,pb50:%.2f,pb80:%.2f", pbs.get(pbs.size() - 1).pb, pb20, pb50, pb80));
                    }
                });
    }

    private void showRoeChart(final List<Roe> roes, final float averageRoe) {
        final LoadingView loadingView = (LoadingView) findViewById(R.id.mRoeBenefitLoadingView);
        final LineChart chart = (LineChart) findViewById(R.id.mRoeChart);
        Observable.just(roes).flatMap(new Function<List<Roe>, ObservableSource<LineData>>() {
            @Override
            public ObservableSource<LineData> apply(List<Roe> roes) throws Exception {
                List<Entry> roeEntries = new ArrayList<Entry>();
                for (int i = 0; i < roes.size(); i++) {
                    roeEntries.add(new Entry(i, roes.get(i).weightedroe));
                }
                LineDataSet set = new LineDataSet(roeEntries, "weightedroe");
                set.setCircleRadius(StockConfig.CIRCLE_RADIUS);
                set.setCubicIntensity(StockConfig.CUBIC_INTENSITY);
                LineData lineData = new LineData();
                lineData.addDataSet(set);
                return Observable.just(lineData);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LineData>() {
                    @Override
                    public void accept(LineData lineData) throws Exception {
                        loadingView.hideLoading();
                        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                return roes.get((int) value).reportdate;
                            }
                        });
                        makeChartInit(chart);
                        chart.getXAxis().setLabelCount(roes.size(), true);
                        chart.setData(lineData);

                        TextView mRoeDetailTextView = (TextView) findViewById(R.id.mRoeDetailTextView);
                        mRoeDetailTextView.setText(String.format("average.roe:%.2f", averageRoe));
                    }
                });
    }

    private void showBenefitChart(final int maxYear, final float roe, final float pbBuy, final float pbSell) {
        final LoadingView loadingView = (LoadingView) findViewById(R.id.mBenefitLoadingView);
        final LineChart mChart = (LineChart) findViewById(R.id.chart1);
        Observable.create(new ObservableOnSubscribe<List<LineDataSet>>() {
            @Override
            public void subscribe(ObservableEmitter<List<LineDataSet>> e) throws Exception {
                List<LineDataSet> sets = new ArrayList<LineDataSet>();
                List<Entry> pures = new ArrayList<Entry>();
                List<Entry> sells = new ArrayList<Entry>();
                for (int i = 0; i < maxYear + 1; i++) {
                    float pure = (float) Math.pow(1 + roe, i);
                    float priceSell = pure * pbSell;
                    pures.add(new Entry(i, pure - 1));
                    sells.add(new Entry(i, (priceSell - pbBuy) / pbBuy));
                }
                LineDataSet pureSet = new LineDataSet(pures, "pure");
                pureSet.setCircleRadius(StockConfig.CIRCLE_RADIUS);
                pureSet.setCubicIntensity(StockConfig.CUBIC_INTENSITY);
                pureSet.setColor(Color.GREEN);
                pureSet.setValueTextSize(10);
                sets.add(pureSet);

                LineDataSet sellSet = new LineDataSet(sells, "sells");
                sellSet.setCircleRadius(StockConfig.CIRCLE_RADIUS);
                sellSet.setCubicIntensity(StockConfig.CUBIC_INTENSITY);
                sellSet.setValueTextSize(10);
                sets.add(sellSet);

                e.onNext(sets);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<LineDataSet>>() {
                    @Override
                    public void accept(List<LineDataSet> lineDataSets) throws Exception {
                        loadingView.hideLoading();
                        LineData lineData = new LineData();
                        for (LineDataSet lineDataSet : lineDataSets) {
                            lineData.addDataSet(lineDataSet);
                        }
                        mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                return "y" + value;
                            }
                        });
                        makeChartInit(mChart);
                        mChart.getXAxis().setLabelCount(maxYear / 2);
                        mChart.setData(lineData);

                        TextView benefitDetailTextView = (TextView) findViewById(R.id.mBenefitDetailTextView);
                        benefitDetailTextView.setText(String.format("roe:%.2f%%,pbBuy:%.2f,pbSell:%.2f", roe * 100, pbBuy, pbSell));
                    }
                });
    }

}
