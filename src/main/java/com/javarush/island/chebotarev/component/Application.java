package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.island.Island;
import com.javarush.island.chebotarev.organism.Organism;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Application {

    private final long TICK_BARRIER_TIMEOUT_SECONDS = 2;
    private final List<ThreadWorker> workers = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();
    private final long tickPeriodNanos;
    private CyclicBarrier tickBarrier;
    private long startNanos;
    private long tickCount;

    public Application(View view, Island island) {
        tickPeriodNanos = Settings
                .get()
                .getApplicationConfig()
                .getTickPeriodMillis() * 1_000_000;
        island.populate();
        startThreads(view, island);
    }

    public void run() throws Throwable {
        startNanos = System.nanoTime();
        while (true) {
            //tickBarrier.await();
            try {
                tickBarrier.await(TICK_BARRIER_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
            }
            checkThreads();
            tickCount++;
            sleepUntilNextTick();
        }
    }

    private void startThreads(View view, Island island) {
        OnStartedTick onStartedTick = new OnStartedTick(view);
        int threadsNum = 1;//Utils.availableProcessors;
        tickBarrier = new CyclicBarrier((threadsNum + 1), onStartedTick);
        for (int i = 0; i < threadsNum; i++) {
            ThreadWorker threadWorker = new ThreadWorker(island, tickBarrier, threadsNum);
            Thread thread = new Thread(threadWorker);
            thread.setDaemon(true);
            thread.start();
            workers.add(threadWorker);
            threads.add(thread);
        }
    }

    private void checkThreads() {
        for (int i = 0; i < threads.size(); i++) {
            Thread thread = threads.get(i);
            if (!thread.isAlive()) {
                ThreadWorker worker = workers.get(i);
                Throwable throwable = worker.getThrowable();
                if (throwable != null) {
                    throw new RuntimeException(throwable);
                } else {
                    throw new RuntimeException("Worker thread is dead");
                }
            }
        }
    }

    private void sleepUntilNextTick() throws InterruptedException {
        long nextTickNanos = startNanos + tickCount * tickPeriodNanos;
        long nowNanos = System.nanoTime();
        long sleepDurationNanos = nextTickNanos - nowNanos;
        if (sleepDurationNanos > 0) {
            TimeUnit.NANOSECONDS.sleep(sleepDurationNanos);
        }
    }

    private static class OnStartedTick extends ThreadAction {

        private final View view;

        private OnStartedTick(View view) {
            this.view = view;
        }

        @Override
        protected void doAction() {
            view.show();
        }
    }
}
