package com.javarush.island.chebotarev.organism;

public class Organism implements Cloneable {

    private final OrganismConfig config;

    public Organism(OrganismConfig config) {
        this.config = config;
    }

    public String getName(){
        return config.getName();
    }

    @Override
    public Organism clone() throws CloneNotSupportedException {
        return (Organism) super.clone();
    }
}
