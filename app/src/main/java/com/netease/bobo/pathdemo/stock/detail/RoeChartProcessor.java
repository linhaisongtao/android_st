package com.netease.bobo.pathdemo.stock.detail;

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
import com.netease.bobo.pathdemo.stock.model.Roe;
import com.netease.bobo.pathdemo.util.MathUtil;
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

public class RoeChartProcessor {

    public void showRoeChart(final View rootView, final List<Roe> roes, final float averageRoe) {
        final LoadingView loadingView = (LoadingView) rootView.findViewById(R.id.mRoeBenefitLoadingView);
        final LineChart chart = (LineChart) rootView.findViewById(R.id.mRoeChart);
        Observable.just(roes).flatMap(new Function<List<Roe>, ObservableSource<LineData>>() {
            @Override
            public ObservableSource<LineData> apply(List<Roe> roes) throws Exception {
                List<Entry> roeEntries = new ArrayList<Entry>();
                for (int i = 0; i < roes.size(); i++) {
                    roeEntries.add(new Entry(i, roes.get(i).weightedroe));
                }
                LineDataSet set = new LineDataSet(roeEntries, "weightedroe");
                set.setCircleRadius(StockConfig.getStockConfig().CIRCLE_RADIUS);
                set.setCubicIntensity(StockConfig.getStockConfig().CUBIC_INTENSITY);
                set.setLineWidth(StockConfig.getStockConfig().LINE_WIDTH);
                LineData lineData = new LineData();
                lineData.addDataSet(set);
                return Observable.just(lineData);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LineData>() {
                    @Override
                    public void accept(LineData lineData) throws Exception {
                        if (roes.size() <= 0) {
                            return;
                        }
                        loadingView.hideLoading();
                        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                return roes.get((int) value).reportdate;
                            }
                        });
                        StockDetailActivity.makeChartInit(chart);
                        chart.getXAxis().setLabelCount(roes.size(), true);
                        chart.getAxisLeft().setAxisMinimum(Math.min(0, lineData.getYMin()));
                        chart.setData(lineData);

                        TextView mRoeDetailTextView = (TextView) rootView.findViewById(R.id.mRoeDetailTextView);
                        mRoeDetailTextView.setText(String.format("average%d.roe:%.2f", StockConfig.getStockConfig().AVERAGE_ROE_COUNT, averageRoe));

                        TextView roeResultTextView = (TextView) rootView.findViewById(R.id.mRoeResultTextView);
                        refreshResultTip(roeResultTextView, roes, averageRoe);
                    }
                });
    }

    private void refreshResultTip(TextView resultTextView, List<Roe> roes, float averageRoe) {
        String msg = "";
        if (averageRoe >= 22) {
            msg += "超高收益";
        } else if (averageRoe >= 15) {
            msg += "高收益";
        } else if (averageRoe >= 10) {
            msg += "收益一般";
        } else {
            msg += "收益偏低";
        }
        List<Double> data = new ArrayList<>();
        for (int i = roes.size() - 1; i >= 0; i--) {
            if (data.size() < StockConfig.getStockConfig().AVERAGE_ROE_COUNT) {
                data.add((double) roes.get(i).weightedroe);
            } else {
                break;
            }
        }
        double[] d = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            d[i] = data.get(i);
        }
        double stardardDiviation = MathUtil.getStandardDiviation(d);
        msg += ",";
        if (stardardDiviation > 8) {
            msg += "收益波动很大";
        } else if (stardardDiviation > 4) {
            msg += "收益有波动";
        } else if (stardardDiviation > 2) {
            msg += "收益较稳定";
        } else {
            msg += "收益很稳定";
        }

        resultTextView.setText(msg + String.format(",标准差:%.4f", stardardDiviation));
    }
}
