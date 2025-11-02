package com.javarush.island.chebotarev.organism.herbivore;

import com.javarush.island.chebotarev.component.Config;
import com.javarush.island.chebotarev.organism.OrganismConfig;

@Config(filename = "goat.yaml")
public class Goat extends Herbivore {

    public Goat(OrganismConfig config) {
        super(config);
    }
}
