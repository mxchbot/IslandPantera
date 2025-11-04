package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.island.Cell;
import com.javarush.island.chebotarev.island.Island;

public class ConsoleView implements View {

    private final Island island;

    public ConsoleView(Island island) {
        this.island = island;
    }

    @Override
    public void show() {
        StringBuilder builder = new StringBuilder();
        Cell[][] cells = island.getCells();
        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {

            }
        }
    }
}
