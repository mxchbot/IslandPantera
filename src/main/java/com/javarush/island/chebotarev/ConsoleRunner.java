package com.javarush.island.chebotarev;

import com.javarush.island.chebotarev.component.Application;
import com.javarush.island.chebotarev.component.IslandConfig;
import com.javarush.island.chebotarev.repository.OrganismCreator;

public class ConsoleRunner {

    public static void main(String[] args) {
        try {
            IslandConfig islandConfig = IslandConfig.load();
            OrganismCreator organismCreator = new OrganismCreator();
            Application application = new Application(islandConfig, organismCreator);
            application.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("BYE");
    }
}
