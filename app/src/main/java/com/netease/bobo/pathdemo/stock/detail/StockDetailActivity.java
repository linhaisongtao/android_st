package com.netease.bobo.pathdemo.stock.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.netease.bobo.pathdemo.R;
import com.netease.bobo.pathdemo.stock.model.Pb;
import com.netease.bobo.pathdemo.stock.model.Roe;
import com.netease.bobo.pathdemo.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public static void start(Context context) {
        Intent intent = new Intent(context, StockDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("S");

        setContentView(R.layout.activity_stock_detail);

        showBenefitChart(11, 0.2f, 2f, 1.6f);

        Random random = new Random();

        List<Roe> roes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            roes.add(new Roe("201" + i, 20 + 5 * random.nextFloat()));
        }
        showRoeChart(roes);


        List<Pb> pbs = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            pbs.add(new Pb(String.valueOf(i), (float) (10 + 3 * Math.sin(1.0 * i / 100))));
        }
        showPbChart(pbs);
    }

    private void showPbChart(final List<Pb> pbs) {
        final LoadingView loadingView = (LoadingView) findViewById(R.id.mPbLoadingView);
        final LineChart chart = (LineChart) findViewById(R.id.mPbChart);
        Observable.just(pbs).flatMap(new Function<List<Pb>, ObservableSource<LineData>>() {
            @Override
            public ObservableSource<LineData> apply(List<Pb> roes) throws Exception {
                List<Entry> roeEntries = new ArrayList<Entry>();
                for (int i = 0; i < roes.size(); i++) {
                    roeEntries.add(new Entry(i, roes.get(i).pb));
                }
                LineDataSet set = new LineDataSet(roeEntries, "pb");
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
                                int index = (int) value;
                                return pbs.get(index).date;
                            }
                        });
                        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        chart.getXAxis().setEnabled(true);
                        chart.getAxisRight().setEnabled(false);

                        chart.animateXY(2000, 2000);
                        chart.setData(lineData);
                    }
                });
    }

    private void showRoeChart(final List<Roe> roes) {
        final LoadingView loadingView = (LoadingView) findViewById(R.id.mRoeBenefitLoadingView);
        final LineChart chart = (LineChart) findViewById(R.id.mRoeChart);
        Observable.just(roes).flatMap(new Function<List<Roe>, ObservableSource<LineData>>() {
            @Override
            public ObservableSource<LineData> apply(List<Roe> roes) throws Exception {
                List<Entry> roeEntries = new ArrayList<Entry>();
                for (int i = 0; i < roes.size(); i++) {
                    roeEntries.add(new Entry(i, roes.get(i).roe));
                }
                LineDataSet set = new LineDataSet(roeEntries, "roe");
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
                                return roes.get((int) value).date;
                            }
                        });
                        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        chart.getXAxis().setEnabled(true);
                        chart.getAxisRight().setEnabled(false);

                        chart.animateXY(2000, 2000);
                        chart.setData(lineData);
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
                pureSet.setCubicIntensity(0.2f);
                pureSet.setColor(Color.GREEN);
                sets.add(pureSet);
                sets.add(new LineDataSet(sells, "sells"));
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
                        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        mChart.getXAxis().setEnabled(true);
                        mChart.getAxisRight().setEnabled(false);

                        mChart.animateXY(2000, 2000);
                        mChart.setData(lineData);
                    }
                });
    }

}
