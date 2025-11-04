package com.javarush.island.chebotarev.island;

import com.javarush.island.chebotarev.component.Utils;
import com.javarush.island.chebotarev.config.IslandConfig;
import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.repository.OrganismCreator;

import java.util.Map;

public class Island {

    private final OrganismCreator organismCreator;
    private final Cell[][] cells;

    public Island(OrganismCreator organismCreator) {
        this.organismCreator = organismCreator;
        IslandConfig config = Settings.get().getIslandConfig();
        cells = new Cell[config.getRows()][config.getColumns()];
        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {
                cells[y][x] = new Cell();
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

    private void populate(Organism prototype, int population) {
        for (int i = 0; i < population; i++) {
            int rowIndex = Utils.random(0, cells.length);
            int columnIndex = Utils.random(0, cells[0].length);
            Cell randomCell = cells[rowIndex][columnIndex];
            randomCell.populate(prototype.clone());
        }
    }
}
