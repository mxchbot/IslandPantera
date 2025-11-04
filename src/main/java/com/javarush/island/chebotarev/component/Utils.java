package com.javarush.island.chebotarev.component;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
}
