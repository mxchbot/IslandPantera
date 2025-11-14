package com.javarush.island.chebotarev.organism;

import com.javarush.island.chebotarev.island.Cell;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OrganismGroup {

    private final Map<Integer, Organism> group;
    private final Cell cell;

    public OrganismGroup(Map<Integer, Organism> group, Cell cell) {
        this.group = group;
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }

    public List<Organism> reproduction() {
        Collection<Organism> organismsInGroup = group.values();
        if (organismsInGroup.isEmpty()) {
            throw new IllegalStateException("There are no organisms in group");
        }
        Organism organism = organismsInGroup.iterator().next();
        return organism.reproduction(organismsInGroup);
    }
}
