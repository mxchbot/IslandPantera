package com.javarush.island.chebotarev.island;

import com.javarush.island.chebotarev.organism.Organism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cell {

    private final Map<String, List<Organism>> residents = new HashMap<>();

    public Map<String, List<Organism>> getResidents() {
        return residents;
    }

    public void populate(Organism organism) {
        String organismName = organism.getName();
        residents.computeIfAbsent(organismName, key -> new ArrayList<>())
                .add(organism);
    }
}
