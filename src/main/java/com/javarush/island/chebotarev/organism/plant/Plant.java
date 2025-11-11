package com.javarush.island.chebotarev.organism.plant;

import com.javarush.island.chebotarev.config.GlobalOrganismConfig;
import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.config.OrganismConfig;

import java.util.List;

public class Plant extends Organism {

    protected Plant(String name, OrganismConfig config) {
        super(name, config);
    }

    @Override
    public void movement() {
    }

    @Override
    public void eating(List<Organism> disappearedOrganisms) {
        boolean thisOrganismIsDisappeared;
        synchronized (this) {
            thisOrganismIsDisappeared = isDisappeared();
        }
        if (thisOrganismIsDisappeared) {
            disappearedOrganisms.add(this);
        }
    }
}
