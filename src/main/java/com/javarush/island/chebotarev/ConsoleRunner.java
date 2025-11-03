package com.javarush.island.chebotarev;

import com.javarush.island.chebotarev.component.Application;
import com.javarush.island.chebotarev.island.Island;
import com.javarush.island.chebotarev.island.IslandConfig;
import com.javarush.island.chebotarev.repository.OrganismCreator;

public class ConsoleRunner {

    public static void main(String[] args) {
        try {
            IslandConfig islandConfig = IslandConfig.load();
            OrganismCreator organismCreator = new OrganismCreator();
            Island island = new Island(islandConfig, organismCreator);
            Application application = new Application(island);
            application.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("BYE");
    }
}
