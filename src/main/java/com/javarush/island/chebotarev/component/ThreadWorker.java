package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.island.Island;
import com.javarush.island.chebotarev.organism.Organism;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class ThreadWorker extends ThreadAction {

    private static Island island;
    private static CyclicBarrier tickBarrier;
    private static OnCompletedGlobalListIteration onCompletedGlobalListIteration;
    private static CyclicBarrier movementBarrier;
    private static CyclicBarrier eatingBarrier;
    private static CyclicBarrier clearingBarrier;
    private final List<Organism> disappearedOrganisms = new ArrayList<>();

    public ThreadWorker(Island island,
                        CyclicBarrier tickBarrier) {
        if (ThreadWorker.island == null) {
            ThreadWorker.island = island;
        }
        if (ThreadWorker.tickBarrier == null) {
            ThreadWorker.tickBarrier = tickBarrier;
        }
        if (onCompletedGlobalListIteration == null) {
            onCompletedGlobalListIteration = new OnCompletedGlobalListIteration(island);
        }
        if (movementBarrier == null) {
            movementBarrier = new CyclicBarrier(Utils.availableProcessors, onCompletedGlobalListIteration);
        }
        if (eatingBarrier == null) {
            eatingBarrier = new CyclicBarrier(Utils.availableProcessors, onCompletedGlobalListIteration);
        }
        if (clearingBarrier == null) {
            clearingBarrier = new CyclicBarrier(Utils.availableProcessors);
        }
    }

    @Override
    protected void doAction() throws Throwable {
        Thread currentThread = Thread.currentThread();
        while (!currentThread.isInterrupted()) {
            tickBarrier.await();

            movement();
            eating();
            reproduction();
        }
    }

    private void movement() throws Throwable {
        while (island.hasNextOrganism()) {
            Organism organism = island.nextOrganism();
            if (organism != null) {
                organism.movement();
            }
        }

        movementBarrier.await();
        Throwable throwable = onCompletedGlobalListIteration.getThrowable();
        if (throwable != null) {
            throw throwable;
        }
    }

    private void eating() throws Throwable {
        while (island.hasNextOrganism()) {
            Organism organism = island.nextOrganism();
            if (organism != null) {
                organism.eating(disappearedOrganisms);
            }
        }

        eatingBarrier.await();
        Throwable throwable = onCompletedGlobalListIteration.getThrowable();
        if (throwable != null) {
            throw throwable;
        }

        clearing();
    }

    private void clearing() throws Throwable {
        for (Organism deadOrganism : disappearedOrganisms) {
            island.remove(deadOrganism);
        }
        disappearedOrganisms.clear();

        clearingBarrier.await();
    }

    private void reproduction() {
    }

    private static class OnCompletedGlobalListIteration extends ThreadAction {

        private final Island island;

        public OnCompletedGlobalListIteration(Island island) {
            this.island = island;
        }

        @Override
        protected void doAction() {
            island.resetGlobalListIndex();
        }
    }
}
