package com.netease.bobo.pathdemo.stock.detail;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.netease.bobo.pathdemo.R;
import com.netease.bobo.pathdemo.stock.StockConfig;
import com.netease.bobo.pathdemo.stock.model.Pb;
import com.netease.bobo.pathdemo.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by daisongsong on 2017/7/29.
 */

public class PbChartProcessor {

    public void showPbChart(final View rootView, final List<Pb> pbs, final float pb20, final float pb50, final float pb80, final float currentPosition) {
        final LoadingView loadingView = (LoadingView) rootView.findViewById(R.id.mPbLoadingView);
        final LineChart chart = (LineChart) rootView.findViewById(R.id.mPbChart);
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
                set.setLineWidth(StockConfig.getStockConfig().LINE_WIDTH);
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
                        if (pbs.size() <= 0) {
                            return;
                        }
                        loadingView.hideLoading();
                        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                int index = (int) value;
                                return pbs.get(index).date;
                            }
                        });
                        StockDetailActivity.makeChartInit(chart);
                        chart.getAxisLeft().setAxisMinimum(Math.min(0, lineData.getYMin()));
                        chart.setData(lineData);

                        TextView mPbDetailTextView = (TextView) rootView.findViewById(R.id.mPbDetailTextView);
                        mPbDetailTextView.setText(String.format("pbNow:%.2f,pb20:%.2f,pb50:%.2f,pb80:%.2f", pbs.get(pbs.size() - 1).pb, pb20, pb50, pb80));

                        TextView pbResultTextView = (TextView) rootView.findViewById(R.id.mPbResultTextView);
                        String msg = String.format("当前点位:%.2f,", currentPosition);
                        if (currentPosition >= 0.8) {
                            msg += "严重高估";
                        } else if (currentPosition >= 0.6) {
                            msg += "高估";
                        } else if (currentPosition >= 0.4) {
                            msg += "正常";
                        } else if (currentPosition >= 0.2) {
                            msg += "低估";
                        } else {
                            msg += "严重低估";
                        }
                        pbResultTextView.setText(msg);
                    }
                });
    }

}
