package com.javarush.island.chebotarev.config;

import java.util.Map;

public class IslandConfig {

    private int rows;
    private int columns;
    private Map<String, Integer> population;

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Map<String, Integer> getPopulation() {
        return population;
    }
}
