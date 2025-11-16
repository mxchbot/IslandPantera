package com.javarush.island.chebotarev.island;

import com.javarush.island.chebotarev.component.Children;
import com.javarush.island.chebotarev.component.GlobalOrganismList;
import com.javarush.island.chebotarev.component.OrganismGroupsIterator;
import com.javarush.island.chebotarev.component.Utils;
import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.repository.OrganismCreator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Island {

    private final GlobalOrganismList globalOrganismList = new GlobalOrganismList();
    private final Map<String, OrganismCounter> organismsCounters = new HashMap<>();
    private final int rows = Settings.get().getIslandConfig().getRows();
    private final int columns = Settings.get().getIslandConfig().getColumns();
    private final Cell[][] cells = new Cell[rows][columns];
    private final OrganismGroupsIterator organismGroupsIterator = new OrganismGroupsIterator(cells);

    public Island() {
        createOrganismsCounters();
        createCells();
        organismGroupsIterator.reset();
    }

    public GlobalOrganismList getGlobalOrganismList() {
        return globalOrganismList;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public OrganismGroupsIterator getOrganismGroupsIterator() {
        return organismGroupsIterator;
    }

    public void populate() {
        Map<String, Integer> population = Settings.get().getIslandConfig().getPopulation();
        population.forEach((name, num) -> populate(OrganismCreator.create(name), num));
    }

    public void add(Children children) {
        List<Organism> list = children.list();
        Cell cell = children.cell();
        List<Organism> extraOrganisms = add(list, cell);
        if (extraOrganisms != null) {
            List<Cell> nextCells = cell.cloneNextCells();
            Collections.shuffle(nextCells, Utils.getThreadLocalRandom());
            for (Cell nextCell : nextCells) {
                extraOrganisms = add(extraOrganisms, nextCell);
                if (extraOrganisms == null) {
                    break;
                }
            }
        }
        int lastIndex = (extraOrganisms == null)
                ? list.size() - 1
                : list.size() - 1 - extraOrganisms.size();
        for (int i = 0; i <= lastIndex; i++) {
            globalOrganismList.safeAdd(list.get(i));
        }
        OrganismCounter organismCounter = organismsCounters.get(list.getFirst().getName());
        if (organismCounter == null) {
            throw new IllegalArgumentException();
        }
        organismCounter.add(lastIndex + 1);
    }

    public void remove(Organism organism) {
        organism.removeFromCell();
        globalOrganismList.remove(organism);
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
            lines[i++] = counter.getIcon() + ":" + counter.getCounter();
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
                globalOrganismList.add(clone);
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

    private List<Organism> add(List<Organism> organismList, Cell cell) {
        List<Organism> extraOrganisms = cell.addOrganismList(organismList);
        int addedOrganismNum = (extraOrganisms == null)
                ? organismList.size()
                : organismList.size() - extraOrganisms.size();
        for (int i = 0; i < addedOrganismNum; i++) {
            Organism organism = organismList.get(i);
            organism.setCell(cell);
        }
        return extraOrganisms;
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

        public void decrement() {
            counter.decrementAndGet();
        }
    }
}
