package com.javarush.island.chebotarev.island;

import com.javarush.island.chebotarev.component.Utils;
import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.repository.OrganismCreator;

import java.util.Map;

public class Island {

    private final IslandConfig config;
    private final OrganismCreator organismCreator;
    private final Cell[][] cells;

    public Island(IslandConfig config, OrganismCreator organismCreator) {
        this.config = config;
        this.organismCreator = organismCreator;
        cells = new Cell[config.getRows()][config.getColumns()];
        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {
                cells[y][x] = new Cell();
            }
        }
    }

    public IslandConfig getConfig() {
        return config;
    }

    public void populate() {
        Map<String, Integer> population = config.getPopulation();
        population.forEach((key, value) -> populate(organismCreator.create(key), value));
    }

    private void populate(Organism prototype, int population) {
        for (int i = 0; i < population; i++) {
            int rowIndex = Utils.random(0, cells.length);
            int columnIndex = Utils.random(0, cells[0].length);
            Cell randomCell = cells[rowIndex][columnIndex];
            randomCell.populate(prototype.clone());
        }
    }
}
