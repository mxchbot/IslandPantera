package com.javarush.island.chebotarev.organism;

import java.util.Map;

public class OrganismConfig {

    private String name;
    private double maxWeight;
    private Map<String, Integer> victims;

    public String getName() {
        return name;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public Map<String, Integer> getVictims() {
        return victims;
    }
}
