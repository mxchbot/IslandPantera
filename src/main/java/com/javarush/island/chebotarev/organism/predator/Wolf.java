package com.javarush.island.chebotarev.organism.predator;

import com.javarush.island.chebotarev.component.Config;
import com.javarush.island.chebotarev.organism.OrganismConfig;

@Config(filename = "wolf.yaml")
public class Wolf extends Predator {

    public Wolf(OrganismConfig config) {
        super(config);
    }
}
