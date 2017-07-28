package com.netease.bobo.pathdemo.stock.model;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class StockInfo {
    public List<Pb> mPbs = new ArrayList<>();
    public List<Roe> mRoes = new ArrayList<>();
    private SparseArray<List<Pb>> mSortedPbs = new SparseArray<>();

    public boolean isEmpty() {
        return mPbs == null || mPbs.isEmpty() || mRoes == null || mRoes.isEmpty();
    }

    public List<Pb> getPastYear(int pastYear) {
        int needCount = pastYear * 240;
        if (mPbs != null) {
            return mPbs.subList(mPbs.size() > needCount ? mPbs.size() - needCount : 0, mPbs.size() - 1);
        }
        return new ArrayList<>();
    }

    public List<Roe> getPastRoeYearReporters(int year) {
        if (mRoes == null) {
            return new ArrayList<>();
        }

        List<Roe> roes = new ArrayList<>();

        for (int i = mRoes.size() - 1; i >= 0; i--) {
            Roe roe = mRoes.get(i);
            if (roe.reportdate.endsWith("1231")) {
                Roe r = new Roe();
                r.weightedroe = roe.weightedroe;
                r.reportdate = roe.reportdate.substring(0, 4);
                roes.add(r);
                if (roes.size() >= year) {
                    break;
                }
            }
        }
        Collections.reverse(roes);
        return roes;
    }

    public float getNowPb() {
        if (mPbs == null || mPbs.isEmpty()) {
            return Float.MAX_VALUE;
        }
        return mPbs.get(mPbs.size() - 1).pb;
    }

    public float getPbPosition(int year, float pos) {
        if (pos < 0 || pos > 1) {
            pos = 0.5f;
        }
        int position = (int) (pos * sortedPbs(year).size());
        return sortedPbs(year).get(position).pb;
    }

    private List<Pb> sortedPbs(int year) {
        if (mSortedPbs.get(year) == null || mSortedPbs.get(year).isEmpty()) {
            List<Pb> pbList = getPastYear(year);
            List<Pb> sortedPbs = new ArrayList<>();
            if (pbList != null && !pbList.isEmpty()) {
                sortedPbs.addAll(pbList);
            }
            Collections.sort(sortedPbs, new Comparator<Pb>() {
                @Override
                public int compare(Pb o1, Pb o2) {
                    return (int) (10000 * (o1.pb - o2.pb));
                }
            });
            mSortedPbs.put(year, sortedPbs);
        }
        return mSortedPbs.get(year);
    }

    public float getRoeAverageRoe(int year) {
        List<Roe> roes = getPastRoeYearReporters(year);
        float sum = 0f;
        for (Roe roe : roes) {
            sum += roe.weightedroe;
        }
        return sum / roes.size();
    }

    @Override
    public String toString() {
        return "StockInfo{" +
                "mPbs=" + mPbs +
                ", mRoes=" + mRoes +
                '}';
    }
}
