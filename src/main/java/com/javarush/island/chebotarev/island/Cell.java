package com.javarush.island.chebotarev.island;

import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.organism.Organism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cell {

    private final List<Cell> nextCells = new ArrayList<>();
    private final Map<String, ConcurrentMap<String, Organism>> residents = new HashMap<>();
    private final Lock residentsLock = new ReentrantLock(true);

    public Map<String, ConcurrentMap<String, Organism>> getResidents() {
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
        Integer cellCapacity = Settings
                .get()
                .getIslandConfig()
                .getCellCapacity()
                .get(organismName);
        if (cellCapacity == null) {
            throw new IllegalArgumentException("Unknown organism name: " + organismName);
        }
        ConcurrentMap<String, Organism> map;
        residentsLock.lock();
        try {
            map = residents.get(organismName);
            if (map != null) {
                if (map.size() >= cellCapacity) {
                    return false;
                }
            } else {
                map = new ConcurrentHashMap<>();
                residents.put(organismName, map);
            }
        } finally {
            residentsLock.unlock();
        }
        map.put(organism.getUniqueName(), organism);
        return true;
    }

    public void removeOrganism(Organism organism) {
        String organismName = organism.getName();
        ConcurrentMap<String, Organism> map;
        residentsLock.lock();
        try {
            map = residents.get(organismName);
            if (map == null) {
                throw new IllegalArgumentException("Unknown organism name: " + organismName);
            }
        } finally {
            residentsLock.unlock();
        }
        Organism removedOrganism = map.remove(organism.getUniqueName());
        if (organism != removedOrganism) {
            throw new IllegalArgumentException();
        }
    }
}
