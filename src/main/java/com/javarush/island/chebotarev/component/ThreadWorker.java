package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.island.Island;
import com.javarush.island.chebotarev.organism.Organism;

import java.util.concurrent.CyclicBarrier;

public class ThreadWorker extends ThreadAction {

    private static Island island;
    private static CyclicBarrier tickBarrier;
    private static OnCompletedStage onCompletedMovement;
    private static CyclicBarrier movementBarrier;

    public ThreadWorker(Island island, CyclicBarrier tickBarrier) {
        if (ThreadWorker.island == null) {
            ThreadWorker.island = island;
        }
        if (ThreadWorker.tickBarrier == null) {
            ThreadWorker.tickBarrier = tickBarrier;
        }
        if (onCompletedMovement == null) {
            onCompletedMovement = new OnCompletedStage(island, Stage.MOVEMENT);
        }
        if (movementBarrier == null) {
            movementBarrier = new CyclicBarrier(Utils.availableProcessors, onCompletedMovement);
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
        Throwable throwable = onCompletedMovement.getThrowable();
        if (throwable != null) {
            throw throwable;
        }
    }

    private void eating() {
    }

    private void reproduction() {
    }

    private static class OnCompletedStage extends ThreadAction {

        private static Island island;
        private final Stage stage;

        public OnCompletedStage(Island island, Stage stage) {
            if  (OnCompletedStage.island == null) {
                OnCompletedStage.island = island;
            }
            this.stage = stage;
        }

        @Override
        protected void doAction() {
            switch (stage) {
                case MOVEMENT -> onCompletedMovement();
                default -> throw new IllegalStateException("Unexpected value: " + "stage == " + stage);
            }
        }

        private void onCompletedMovement() {
            island.resetOrganismIndex();
        }
    }
}
