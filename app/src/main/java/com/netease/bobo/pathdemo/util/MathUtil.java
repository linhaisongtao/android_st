package com.netease.bobo.pathdemo.util;

/**
 * Created by daisongsong on 2017/7/29.
 */

public class MathUtil {


    public static double getStandardDiviation(double[] inputData) {
        double average = 0;
        double sum = 0;
        for (double v : inputData) {
            sum += v;
        }
        average = sum / inputData.length;

        double powerSum = 0;
        for (double v : inputData) {
            powerSum += Math.pow(v - average, 2);
        }
        double powerAverage = powerSum / inputData.length;
        return Math.sqrt(powerAverage);
    }


}
