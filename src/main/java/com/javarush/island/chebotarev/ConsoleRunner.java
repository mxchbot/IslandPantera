package com.javarush.island.chebotarev;

import com.javarush.island.chebotarev.component.Application;
import com.javarush.island.chebotarev.view.ConsoleView;
import com.javarush.island.chebotarev.view.View;
import com.javarush.island.chebotarev.island.Island;

public class ConsoleRunner {

    public static void main(String[] args) {
        View view = null;
        try {
            Island island = new Island();
            view = new ConsoleView(island);
            Application application = new Application(view, island);
            application.run();
        } catch (Throwable e) {
            if (view != null) {
                view.showThrowable(e);
            } else {
                e.printStackTrace();
            }
        }
    }
}
