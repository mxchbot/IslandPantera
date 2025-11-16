package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.island.Island;
import com.javarush.island.chebotarev.view.JavaFXView;
import javafx.stage.Stage;

public class JavaFXApplication extends javafx.application.Application {

    public static void launchFXWindow() {
        launch();
    }

    @Override
    public void start(Stage stage) {
        JavaFXView view = new JavaFXView(stage);
        new LogicThread(view);
    }

    private static class LogicThread extends Thread {

        private final JavaFXView view;

        public LogicThread(JavaFXView view) {
            this.view = view;
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            try {
                Island island = new Island();
                view.setIsland(island);
                view.setLogicThread(this);
                Application application = new Application(view, island);
                application.run();
            } catch (Throwable e) {
                view.showThrowable(e);
            }
        }
    }
}
