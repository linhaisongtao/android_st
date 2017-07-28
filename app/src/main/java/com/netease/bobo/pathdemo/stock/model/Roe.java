package com.netease.bobo.pathdemo.stock.model;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class Roe {
    public String reportdate;
    public float weightedroe;

    public Roe() {
    }

    public Roe(String reportdate, float weightedroe) {
        this.reportdate = reportdate;
        this.weightedroe = weightedroe;
    }

    @Override
    public String toString() {
        return "Roe{" +
                "reportdate='" + reportdate + '\'' +
                ", weightedroe=" + weightedroe +
                '}';
    }
}
