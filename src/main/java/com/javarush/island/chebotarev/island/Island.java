package com.javarush.island.chebotarev.island;

import com.javarush.island.chebotarev.component.Utils;
import com.javarush.island.chebotarev.config.IslandConfig;
import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.repository.OrganismCreator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Island {

    private final List<Organism> organisms = new ArrayList<>();
    private final AtomicInteger organismIndex = new AtomicInteger(0);
    private final Map<String, OrganismCounter> organismsCounters = new HashMap<>();
    private final Cell[][] cells = new Cell[Settings
            .get()
            .getIslandConfig()
            .getRows()]
            [Settings
            .get()
            .getIslandConfig()
            .getColumns()];

    public Island() {
        createOrganismsCounters();
        createCells();
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void populate() {
        Map<String, Integer> population = Settings.get().getIslandConfig().getPopulation();
        population.forEach((name, num) -> populate(OrganismCreator.create(name), num));
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

    public void resetGlobalListIndex() {
        organismIndex.set(0);
    }

    public void remove(Organism organism) {
        organism.removeFromCell();
        Organism removedOrganism = organisms.set(organism.getGlobalListIndex(), null);
        if (organism != removedOrganism) {
            throw new IllegalArgumentException();
        }
        OrganismCounter organismCounter = organismsCounters.get(organism.getName());
        if (organismCounter == null) {
            throw new IllegalArgumentException();
        }
        organismCounter.decrement();
    }

    public String[] collectStatistics() {
        String[] lines = new String[organismsCounters.size()];
        int i = 0;
        for (OrganismCounter counter : organismsCounters.values()) {
            lines[i++] = counter.getIcon() + ": " + counter.getCounter();
        }
        return lines;
    }

    private void createOrganismsCounters() {
        Set<String> names = OrganismCreator.getPrototypesNames();
        for (String name : names) {
            String icon = OrganismCreator.getIcon(name);
            organismsCounters.put(name, new OrganismCounter(icon));
        }
    }

    private void createCells() {
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

    private void populate(Organism prototype, int population) {
        for (int i = 0; i < population; i++) {
            int rowIndex = Utils.random(0, cells.length);
            int columnIndex = Utils.random(0, cells[0].length);
            Organism clone = prototype.clone();
            Cell randomCell = cells[rowIndex][columnIndex];
            if (randomCell.addOrganism(clone)) {
                clone.setCell(randomCell);
                organisms.add(clone);
                clone.setGlobalListIndex(organisms.size() - 1);
            } else {
                i--;
            }
        }
        OrganismCounter organismCounter = organismsCounters.get(prototype.getName());
        if (organismCounter == null) {
            throw new IllegalArgumentException();
        }
        organismCounter.add(population);
    }

    private static class OrganismCounter {

        private final String icon;
        private final AtomicInteger counter = new AtomicInteger(0);

        public OrganismCounter(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }

        public int getCounter() {
            return counter.get();
        }

        public void add(int population) {
            counter.addAndGet(population);
        }

        public void increment() {
            counter.incrementAndGet();
        }

        public void decrement() {
            counter.decrementAndGet();
        }
    }
}
