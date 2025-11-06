package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.island.Cell;
import com.javarush.island.chebotarev.island.Island;

import java.util.stream.Collectors;

public class ConsoleView implements View {

    private final int cellCharCount = Settings.get().getConsoleConfig().getCellCharCount();
    private final Island island;

    public ConsoleView(Island island) {
        this.island = island;
    }

    @Override
    public void show() {
        StringBuilder builder = new StringBuilder();
        Cell[][] cells = island.getCells();
        for (Cell[] cellsRow : cells) {
            for (Cell cell : cellsRow) {
                appendResidents(builder, cell);
                builder.append(' ');
            }
            builder.append("\n\n");
        }
        System.out.println(builder);
    }

    @Override
    public void showThrowable(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void appendResidents(StringBuilder builder, Cell cell) {
        String icons = cell.getResidents()
                .values()
                .stream()
                .filter(map -> !map.isEmpty())
                .sorted((map1, map2) -> map2.size() - map1.size())
                .limit(cellCharCount)
                .map(map -> map.get(map
                                .keySet()
                                .stream()
                                .findFirst()
                                .get())
                        .getConfig()
                        .getIcon())
                .collect(Collectors.joining());
        builder.append(icons);
        int iconsCount = (int) cell.getResidents()
                .values()
                .stream()
                .filter(map -> !map.isEmpty())
                .limit(cellCharCount)
                .count();
        if (iconsCount < cellCharCount) {
            builder.append("◾".repeat(cellCharCount - iconsCount));
        }
    }
}
