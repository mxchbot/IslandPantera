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
    public Organism clone() {
        try {
            return (Organism) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
