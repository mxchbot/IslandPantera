package com.javarush.island.chebotarev.organism;

import com.javarush.island.chebotarev.component.Utils;
import com.javarush.island.chebotarev.config.OrganismConfig;
import com.javarush.island.chebotarev.island.Cell;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Organism implements Cloneable {

    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private final String name;
    private final OrganismConfig config;
    private Integer id = idCounter.getAndIncrement();
    private Cell cell;

    public Organism(String name, OrganismConfig config) {
        this.name = name;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public OrganismConfig getConfig() {
        return config;
    }

    public Integer getId() {
        return id;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    @Override
    public Organism clone() {
        Organism clone;
        try {
            clone = (Organism) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.id = idCounter.getAndIncrement();
        return clone;
    }

    public void movement() {
        int maxSpeed = config.getMaxSpeed();
        if (maxSpeed == 0) {
            return;
        }
        Set<Cell> visitedCells = new HashSet<>();
        int speed = Utils.random(1, (maxSpeed + 1));
        for (int i = 0; i < speed; i++) {
            List<Cell> nextCells = cell.cloneNextCells();
            Collections.shuffle(nextCells, Utils.getThreadLocalRandom());
            Cell previousCell = cell;
            for (Cell nextCell : nextCells) {
                if (!visitedCells.contains(nextCell)) {
                    if (nextCell.acceptOrganism(this)) {
                        cell.removeOrganism(this);
                        visitedCells.add(cell);
                        cell = nextCell;
                        break;
                    }
                }
            }
            if (cell == previousCell) {
                break;
            }
        }
    }
}
