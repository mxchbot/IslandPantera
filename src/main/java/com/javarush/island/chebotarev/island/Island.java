package com.javarush.island.chebotarev.island;

import com.javarush.island.chebotarev.component.Utils;
import com.javarush.island.chebotarev.config.IslandConfig;
import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.repository.OrganismCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Island {

    private final List<Organism> organisms = new ArrayList<>();
    private final AtomicInteger organismIndex = new AtomicInteger(0);
    private final OrganismCreator organismCreator;
    private final Cell[][] cells;

    public Island(OrganismCreator organismCreator) {
        this.organismCreator = organismCreator;
        IslandConfig config = Settings.get().getIslandConfig();
        cells = new Cell[config.getRows()][config.getColumns()];
        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                cells[row][column] = new Cell();
            }
        }
        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                cells[row][column].updateNextCells(cells, row, column);
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void populate() {
        Map<String, Integer> population = Settings.get().getIslandConfig().getPopulation();
        population.forEach((key, value) -> populate(organismCreator.create(key), value));
    }

    public boolean hasNextOrganism() {
        return (organismIndex.get() < organisms.size());
    }

    public Organism nextOrganism() {
        int index = organismIndex.getAndIncrement();
        if (index < organisms.size()) {
            return organisms.get(index);
        } else {
            return null;
        }
    }

    public void resetOrganismIndex() {
        organismIndex.set(0);
    }

    private void populate(Organism prototype, int population) {
        for (int i = 0; i < population; i++) {
            int rowIndex = Utils.random(0, cells.length);
            int columnIndex = Utils.random(0, cells[0].length);
            Organism clone = prototype.clone();
            Cell randomCell = cells[rowIndex][columnIndex];
            if (randomCell.acceptOrganism(clone)) {
                clone.setCell(randomCell);
                organisms.add(clone);
            } else {
                i--;
            }
        }
    }
}
