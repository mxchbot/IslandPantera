package com.javarush.island.chebotarev.organism.plant;

import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.config.OrganismConfig;

import java.util.Collection;
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

        growth();
    }

    @Override
    public List<Organism> reproduction(Collection<Organism> organisms) {
        double requiredWeight = getConfig().getMaxWeight() * 0.5;
        int capablePlantsCount = 0;
        for (Organism organism : organisms) {
            if (organism.getWeight() >= requiredWeight) {
                capablePlantsCount++;
            }
        }
        if (capablePlantsCount > 0) {
            return createChildren(capablePlantsCount);
        } else {
            return null;
        }
    }

    protected void growth() {
        double maxWeight = getConfig().getMaxWeight();
        double weightGain = maxWeight / 4.0;
        double newWeight;
        synchronized (this) {
            newWeight = getWeight() + weightGain;
            if (newWeight > maxWeight) {
                newWeight = maxWeight;
            }
            setWeight(newWeight);
        }
    }
}
