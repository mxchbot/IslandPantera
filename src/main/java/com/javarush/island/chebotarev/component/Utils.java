package com.javarush.island.chebotarev.component;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static final int availableProcessors = Runtime.getRuntime().availableProcessors();
    private static final double EPSILON = 1.0E-9;

    public static ThreadLocalRandom getThreadLocalRandom() {
        return ThreadLocalRandom.current();
    }

    public static int random(int origin, int bound) {
        return getThreadLocalRandom().nextInt(origin, bound);
    }

    public static boolean isZero(double d) {
        return Math.abs(d) < EPSILON;
    }

    public static boolean isEqual(double a, double b) {
        return isZero(a - b);
    }

    public static boolean isNegative(double d) {
        return Double.doubleToRawLongBits(d) < 0;
    }
}
