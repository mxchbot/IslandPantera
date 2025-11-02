package com.javarush.island.chebotarev.organism.insect;

import com.javarush.island.chebotarev.component.Config;
import com.javarush.island.chebotarev.organism.OrganismConfig;

@Config(filename = "caterpillar.yaml")
public class Caterpillar extends Insect {

    public Caterpillar(OrganismConfig config) {
        super(config);
    }
}
