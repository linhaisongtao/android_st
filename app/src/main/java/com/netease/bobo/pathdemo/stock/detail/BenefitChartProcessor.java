package com.netease.bobo.pathdemo.stock.detail;

import android.graphics.Color;
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
import com.netease.bobo.pathdemo.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by daisongsong on 2017/7/29.
 */

public class BenefitChartProcessor {

    public void showBenefitChart(final View rootView, final int maxYear, final float roe, final float pbBuy, final float pbSell) {
        final LoadingView loadingView = (LoadingView) rootView.findViewById(R.id.mBenefitLoadingView);
        final LineChart mChart = (LineChart) rootView.findViewById(R.id.chart1);
        Observable.create(new ObservableOnSubscribe<List<LineDataSet>>() {
            @Override
            public void subscribe(ObservableEmitter<List<LineDataSet>> e) throws Exception {
                List<LineDataSet> sets = new ArrayList<LineDataSet>();
                List<Entry> pures = new ArrayList<Entry>();
                List<Entry> sells = new ArrayList<Entry>();
                List<Entry> pb1s = new ArrayList<Entry>();
                for (int i = 0; i < maxYear + 1; i++) {
                    float pure = (float) Math.pow(1 + roe, i);
                    float priceSell = pure * pbSell;
                    pures.add(new Entry(i, pure - 1));
                    sells.add(new Entry(i, (priceSell - pbBuy) / pbBuy));
                    pb1s.add(new Entry(i, (pure - pbBuy) / pbBuy));
                }
                LineDataSet pureSet = new LineDataSet(pures, "pure");
                pureSet.setCircleRadius(StockConfig.getStockConfig().CIRCLE_RADIUS);
                pureSet.setCubicIntensity(StockConfig.getStockConfig().CUBIC_INTENSITY);
                pureSet.setColor(Color.GREEN);
                pureSet.setValueTextSize(10);
                pureSet.setLineWidth(StockConfig.getStockConfig().LINE_WIDTH);
                sets.add(pureSet);

                LineDataSet sellSet = new LineDataSet(sells, "sells");
                sellSet.setCircleRadius(StockConfig.getStockConfig().CIRCLE_RADIUS);
                sellSet.setCubicIntensity(StockConfig.getStockConfig().CUBIC_INTENSITY);
                sellSet.setValueTextSize(10);
                sellSet.setLineWidth(StockConfig.getStockConfig().LINE_WIDTH);
                sets.add(sellSet);

                LineDataSet pb1SellSet = new LineDataSet(pb1s, "pb1Sell");
                pb1SellSet.setCircleRadius(StockConfig.getStockConfig().CIRCLE_RADIUS);
                pb1SellSet.setCubicIntensity(StockConfig.getStockConfig().CUBIC_INTENSITY);
                pb1SellSet.setValueTextSize(10);
                pb1SellSet.setLineWidth(StockConfig.getStockConfig().LINE_WIDTH);
                pb1SellSet.setColor(Color.YELLOW);
                sets.add(pb1SellSet);

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
                        StockDetailActivity.makeChartInit(mChart);
                        mChart.getXAxis().setLabelCount(maxYear / 2);
                        mChart.setData(lineData);

                        TextView benefitDetailTextView = (TextView) rootView.findViewById(R.id.mBenefitDetailTextView);
                        benefitDetailTextView.setText(String.format("roe:%.2f%%,pbBuy:%.2f,pbSell:%.2f", roe * 100, pbBuy, pbSell));

                        TextView benefitResultTextView = (TextView) rootView.findViewById(R.id.mBenefitResultTextView);
                        float p = satisfy(lineDataSets, 5, 1);
                        String msg = "";
                        if (p >= 0.8f) {
                            msg += "满足收益要求";
                        } else if (p >= 0.6) {
                            msg += "有一定的风险";
                        } else {
                            msg += "不满足要求";
                        }

                        benefitResultTextView.setText(msg + String.format(",p:%.2f", p));
                    }
                });
    }

    private float satisfy(List<LineDataSet> sets, int index, float value) {
        int totalSetCount = sets.size();
        int satisfyCount = 0;
        for (LineDataSet set : sets) {
            Entry entry = set.getEntryForIndex(index);
            if (entry.getY() >= value) {
                satisfyCount++;
            }
        }
        return (float) (1.0 * satisfyCount / totalSetCount);
    }
}
