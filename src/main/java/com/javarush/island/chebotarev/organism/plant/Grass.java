package com.javarush.island.chebotarev.organism.plant;

import com.javarush.island.chebotarev.component.Config;
import com.javarush.island.chebotarev.organism.OrganismConfig;

@Config(filename = "grass.yaml")
public class Grass extends Plant {

    public Grass(OrganismConfig config) {
        super(config);
    }
}
