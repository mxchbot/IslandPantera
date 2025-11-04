package com.javarush.island.chebotarev;

import com.javarush.island.chebotarev.component.Application;
import com.javarush.island.chebotarev.component.ConsoleView;
import com.javarush.island.chebotarev.component.View;
import com.javarush.island.chebotarev.island.Island;
import com.javarush.island.chebotarev.repository.OrganismCreator;

public class ConsoleRunner {

    public static void main(String[] args) {
        try {
            OrganismCreator organismCreator = new OrganismCreator();
            Island island = new Island(organismCreator);
            View view = new ConsoleView(island);
            Application application = new Application(island, view);
            application.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("BYE");
    }
}
