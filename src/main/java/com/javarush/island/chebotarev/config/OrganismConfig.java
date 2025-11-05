package com.javarush.island.chebotarev.config;

import java.util.Map;

public class OrganismConfig {

    private String icon;
    private double maxWeight;
    private Map<String, Integer> victims;

    public String getIcon() {
        return icon;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public Map<String, Integer> getVictims() {
        return victims;
    }
}
