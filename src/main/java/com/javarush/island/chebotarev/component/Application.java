package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.repository.OrganismCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class Application {

    private final IslandConfig islandConfig;
    private final OrganismCreator organismCreator;
    private final List<WorkerThread> workers = new ArrayList<>();
    private final List<Organism> organisms = new ArrayList<>();
    private CyclicBarrier barrier;

    public Application(IslandConfig islandConfig, OrganismCreator organismCreator) {
        this.islandConfig = islandConfig;
        this.organismCreator = organismCreator;
        populateIsland();
        startWorkers();
    }

    public void run() {
        while (true) {
            if (!checkWorkers()) {
                return;
            }


        }
    }

    private void populateIsland() {
        Organism organism = organismCreator.create("wolf");
        System.out.println(organism.getName());
    }

    private void startWorkers() {
        int processorsNum = Runtime.getRuntime().availableProcessors();
        barrier = new CyclicBarrier(processorsNum + 1);
        for (int i = 0; i < processorsNum; i++) {
            workers.add(new WorkerThread(barrier));
        }
    }

    private boolean checkWorkers() {
        for (WorkerThread worker : workers) {
            if (!worker.isAlive()) {
                Throwable throwable = worker.getThrowable();
                if (throwable != null) {
                    throw new RuntimeException(throwable);
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
