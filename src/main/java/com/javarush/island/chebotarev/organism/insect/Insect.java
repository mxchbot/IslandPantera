package com.javarush.island.chebotarev.organism.insect;

import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.config.OrganismConfig;

import java.util.Collection;
import java.util.List;

public class Insect extends Organism {

    protected Insect(String name, OrganismConfig config) {
        super(name, config);
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

    @Override
    public List<Organism> reproduction(Collection<Organism> organisms) {
        int childrenNum = organisms.size() / 10;
        childrenNum = (childrenNum == 0) ? 1 : childrenNum;
        //int childrenNum = Utils.random(1, (organisms.size() + 1));
        List<Organism> children = createChildren(childrenNum);
        double maxWeight = getConfig().getMaxWeight();
        for (Organism child : children) {
            Insect insectChild = (Insect) child;
            insectChild.setWeight(maxWeight);
        }
        return children;
    }
}
