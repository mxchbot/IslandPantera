package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.island.Island;
import com.javarush.island.chebotarev.organism.Organism;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class Application {

    private final List<WorkerThread> workers = new ArrayList<>();
    private final List<Organism> organisms = new ArrayList<>();
    private final Island island;
    private final View view;
    private final long tickPeriodNanos;
    private long startNanos;
    private long tickCount;
    private CyclicBarrier barrier;

    public Application(Island island, View view) {
        this.island = island;
        this.view = view;
        tickPeriodNanos = Settings
                .get()
                .getApplicationConfig()
                .getTickPeriodMillis() * 1_000_000;
        island.populate();
        startWorkers();
    }

    public void run() {
        startNanos = System.nanoTime();
        while (true) {
            if (!checkWorkers()) {
                return;
            }

            view.show();

            tickCount++;
            sleepUntilNextTick();
        }
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

    private void sleepUntilNextTick() {
        long nextTickNanos = startNanos + tickCount * tickPeriodNanos;
        long nowNanos = System.nanoTime();
        long sleepDurationNanos = nextTickNanos - nowNanos;
        if (sleepDurationNanos > 0) {
            try {
                TimeUnit.NANOSECONDS.sleep(sleepDurationNanos);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
