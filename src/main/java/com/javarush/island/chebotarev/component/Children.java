package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.island.Cell;
import com.javarush.island.chebotarev.organism.Organism;

import java.util.List;

public record Children(List<Organism> list, Cell cell) {

    public boolean isEmpty() {
        return list.isEmpty();
    }
}
