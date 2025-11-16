package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.island.Cell;
import com.javarush.island.chebotarev.organism.Organism;

import java.util.Iterator;
import java.util.Map;

public class OrganismGroupsIterator {

    private final int rows = Settings.get().getIslandConfig().getRows();
    private final int columns = Settings.get().getIslandConfig().getColumns();
    private final int lastCellIndex = rows * columns - 1;
    private final Cell[][] cells;
    private int cellIndex = 0;
    private Cell cell;
    private Iterator<Map<Integer, Organism>> groupsIterator;

    public OrganismGroupsIterator(Cell[][] cells) {
        this.cells = cells;
    }

    public synchronized boolean hasNextGroup() {
        return cellIndex <= lastCellIndex;
    }

    public OrganismGroup nextGroup() {
        Map<Integer, Organism> group = null;
        Cell groupCell = null;
        synchronized (this) {
            while (cellIndex <= lastCellIndex) {
                if (groupsIterator.hasNext()) {
                    group = groupsIterator.next();
                    if (!group.isEmpty()) {
                        groupCell = cell;
                        break;
                    } else {
                        group = null;
                    }
                } else {
                    cellIndex++;
                    if (cellIndex <= lastCellIndex) {
                        int row = cellIndex / columns;
                        int column = cellIndex % columns;
                        cell = cells[row][column];
                        groupsIterator = cell.getGroupsIterator();
                    }
                }
            }
        }
        if (group != null) {
            return new OrganismGroup(group, groupCell);
        } else {
            return null;
        }
    }

    public void reset() {
        cellIndex = 0;
        cell = cells[0][0];
        groupsIterator = cell.getGroupsIterator();
    }
}
