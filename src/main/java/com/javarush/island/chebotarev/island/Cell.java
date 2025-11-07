package com.javarush.island.chebotarev.island;

import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.repository.OrganismCreator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cell {

    private final List<Cell> nextCells = new ArrayList<>();
    private final Map<String, Map<Integer, Organism>> residents = new HashMap<>();

    public Cell() {
        Set<String> names = OrganismCreator.getPrototypesNames();
        for (String name : names) {
            residents.put(name, new HashMap<>());
        }
    }

    public Map<String, Map<Integer, Organism>> getResidents() {
        return residents;
    }

    public void updateNextCells(Cell[][] cells, int row, int column) {
        if (row > 0) {
            nextCells.add(cells[row - 1][column]);
        }
        if (column > 0) {
            nextCells.add(cells[row][column - 1]);
        }
        if (row < (cells.length - 1)) {
            nextCells.add(cells[row + 1][column]);
        }
        if (column < (cells[0].length - 1)) {
            nextCells.add(cells[row][column + 1]);
        }
    }

    public List<Cell> cloneNextCells() {
        return new ArrayList<>(nextCells);
    }

    public boolean acceptOrganism(Organism organism) {
        String organismName = organism.getName();
        Integer organismId = organism.getId();
        Integer cellCapacity = Settings
                .get()
                .getIslandConfig()
                .getCellCapacity()
                .get(organismName);
        if (cellCapacity == null) {
            throw new IllegalArgumentException("Unknown organism name: " + organismName);
        }
        Map<Integer, Organism> map = residents.get(organismName);
        if (map == null) {
            throw new IllegalArgumentException("Unknown organism name: " + organismName);
        }
        synchronized (map) {
            if (map.size() < cellCapacity) {
                map.put(organismId, organism);
            } else {
                return false;
            }
        }
        return true;
    }

    public void removeOrganism(Organism organism) {
        String organismName = organism.getName();
        Integer organismId = organism.getId();
        Map<Integer, Organism> map = residents.get(organismName);
        if (map == null) {
            throw new IllegalArgumentException("Unknown organism name: " + organismName);
        }
        Organism removedOrganism;
        synchronized (map) {
            removedOrganism = map.remove(organismId);
        }
        if (organism != removedOrganism) {
            throw new IllegalArgumentException();
        }
    }
}
