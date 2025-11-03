package com.javarush.island.chebotarev.island;

import com.javarush.island.chebotarev.component.Config;
import com.javarush.island.chebotarev.component.Utils;

import java.util.Map;

@Config(filename = "island_config.yaml")
public class IslandConfig {

    private int tickPeriodMillis;
    private int rows;
    private int columns;
    private Map<String, Integer> population;

    public static IslandConfig load() {
        return Utils.loadConfigYAML(IslandConfig.class, IslandConfig.class);
    }

    public int getTickPeriodMillis() {
        return tickPeriodMillis;
    }

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
