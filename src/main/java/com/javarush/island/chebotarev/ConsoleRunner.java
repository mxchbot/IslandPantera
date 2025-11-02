package com.javarush.island.chebotarev;

import com.javarush.island.chebotarev.component.Application;

public class ConsoleRunner {

    public static void main(String[] args) {
        try {
            Application application = new Application();
            application.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("BYE");
    }
}
