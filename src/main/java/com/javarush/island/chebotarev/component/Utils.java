package com.javarush.island.chebotarev.component;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static final int availableProcessors = Runtime.getRuntime().availableProcessors();

    public static int random(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    public static Random getThreadLocalRandom() {
        return ThreadLocalRandom.current();
    }
}
