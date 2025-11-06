package com.javarush.island.chebotarev.config;

import java.util.Map;

public class OrganismConfig {

    private String icon;
    private double maxWeight;
    private int maxSpeed;
    private Map<String, Integer> victims;

    public String getIcon() {
        return icon;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public Map<String, Integer> getVictims() {
        return victims;
    }
}
