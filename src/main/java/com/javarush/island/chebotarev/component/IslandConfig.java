package com.javarush.island.chebotarev.component;

@Config(filename = "island_config.yaml")
public class IslandConfig {

    private int tickPeriodMillis;
    private int rows;
    private int columns;

    public static IslandConfig load() {
        return Utils.loadConfig(IslandConfig.class, IslandConfig.class, Utils.mapperYAML);
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
}
