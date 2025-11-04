package com.javarush.island.chebotarev.organism;

import com.javarush.island.chebotarev.config.OrganismConfig;

public class Organism implements Cloneable {

    private final String name;
    private final OrganismConfig config;

    public Organism(String name, OrganismConfig config) {
        this.name = name;
        this.config = config;
    }

    public String getName(){
        return name;
    }

    @Override
    public Organism clone() {
        try {
            return (Organism) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
