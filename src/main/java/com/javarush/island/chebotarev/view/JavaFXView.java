package com.javarush.island.chebotarev.view;

import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.island.Cell;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

public class JavaFXView extends View {

    private final int STATISTICS_WIDTH = 200;
    private final int STATISTICS_FONT_SIZE = 18;
    private final int CELL_LABEL_FONT_SIZE = 14;
    private final int ALERT_WIDTH = 800;
    private final int windowsWidth = Settings.get().getWindowConfig().getWidth();
    private final int windowsHeight = Settings.get().getWindowConfig().getHeight();
    private final int rows = Settings.get().getIslandConfig().getRows();
    private final int columns = Settings.get().getIslandConfig().getColumns();
    private final Label[][] labelCells = new Label[rows][columns];
    private final Label statistics;
    private Thread logicThread;

    public JavaFXView(Stage stage) {
        super(Settings.get().getWindowConfig().getCellIconCount());
        statistics = createStatisticsBox();
        GridPane islandPane = createIslandPane();
        HBox hBox = new HBox(islandPane, statistics);
        Scene scene = new Scene(hBox, windowsWidth, windowsHeight);
        stage.setScene(scene);
        stage.show();
    }

    public void setLogicThread(Thread logicThread) {
        this.logicThread = logicThread;
    }

    @Override
    public void show() {
        String[][] islandData = prepareIslandData();
        String[] statisticsLines = prepareStatisticsData();
        Platform.runLater(new ShowView(this, islandData, statisticsLines));
    }

    @Override
    public void showThrowable(Throwable throwable) {
        Platform.runLater(() -> {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            Alert alert = new Alert(Alert.AlertType.ERROR, stringWriter.toString());
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setPrefWidth(ALERT_WIDTH);
            alert.showAndWait();
            printWriter.close();
            System.exit(1);
        });
    }

    private Label createStatisticsBox() {
        Label label = new Label();
        label.setWrapText(true);
        label.setFont(Font.font(STATISTICS_FONT_SIZE));
        label.setMaxWidth(STATISTICS_WIDTH);
        label.setMinWidth(STATISTICS_WIDTH);
        return label;
    }

    private GridPane createIslandPane() {
        GridPane islandPane = new GridPane();
        islandPane.setPrefHeight(windowsHeight);
        islandPane.setPrefWidth(windowsWidth - STATISTICS_WIDTH);
        for (int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100d / columns);
            column.setHgrow(Priority.ALWAYS);
            islandPane.getColumnConstraints().add(column);
        }
        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100d / rows);
            row.setVgrow(Priority.NEVER);
            islandPane.getRowConstraints().add(row);
        }
        islandPane.setGridLinesVisible(true);
        for (int i = 0, mapLength = labelCells.length; i < mapLength; i++) {
            for (int j = 0; j < labelCells[i].length; j++) {
                Label label = new Label();
                label.setFont(Font.font(CELL_LABEL_FONT_SIZE));
                label.setWrapText(true);
                labelCells[i][j] = label;
                islandPane.add(labelCells[i][j], j, i);
            }
        }
        return islandPane;
    }

    private String[][] prepareIslandData() {
        String[][] data = new String[rows][columns];
        Cell[][] cells = island.getCells();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                data[row][column] = getIcons(cells[row][column]);
            }
        }
        return data;
    }

    private String[] prepareStatisticsData() {
        return island.collectStatistics();
    }

    private void interruptLogicThread() {
        logicThread.interrupt();
    }

    private static class ShowView implements Runnable {

        private final JavaFXView view;
        private final String[][] islandData;
        private final String[] statisticsLines;

        private ShowView(JavaFXView view, String[][] islandData, String[] statisticsLines) {
            this.view = view;
            this.islandData = islandData;
            this.statisticsLines = statisticsLines;
        }

        @Override
        public void run() {
            try {
                showIsland();
                showStatistics();
            } catch (Throwable e) {
                view.interruptLogicThread();
                view.showThrowable(e);
            }
        }

        private void showIsland() {
            int rows = view.rows;
            int columns = view.columns;
            Label[][] labelCells = view.labelCells;
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    labelCells[row][column].setText(islandData[row][column]);
                }
            }
        }

        private void showStatistics() {
            String statistics = String.join("\n", statisticsLines);
            view.statistics.setText(statistics);
        }
    }
}
