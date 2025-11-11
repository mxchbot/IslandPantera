package com.javarush.island.chebotarev.organism.insect;

import com.javarush.island.chebotarev.config.GlobalOrganismConfig;
import com.javarush.island.chebotarev.config.OrganismConfig;
import com.javarush.island.chebotarev.organism.Organism;

import java.util.List;

public class Caterpillar extends Insect {

    public Caterpillar(String name, OrganismConfig config) {
        super(name, config);
    }

    @Override
    public void movement() {
    }

    @Override
    public void eating(List<Organism> disappearedOrganisms) {
    }
}
