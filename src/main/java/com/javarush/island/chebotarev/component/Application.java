package com.javarush.island.chebotarev.component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class Application {

    private final List<WorkerThread> workers = new ArrayList<>();
    private final List<Organism> organisms = new ArrayList<>();
    private CyclicBarrier barrier;

    public Application() {
        int processorsNum = Runtime.getRuntime().availableProcessors();
        barrier = new CyclicBarrier(processorsNum + 1);
        for (int i = 0; i < processorsNum; i++) {
            workers.add(new WorkerThread(barrier));
        }
    }

    public void run() {
        while (true) {
            if (!checkWorkers()) {
                return;
            }


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
