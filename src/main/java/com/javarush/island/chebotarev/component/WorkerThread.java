package com.javarush.island.chebotarev.component;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class WorkerThread extends Thread {

    private final CyclicBarrier barrier;
    private Stage stage;
    private Throwable throwable;

    public WorkerThread(CyclicBarrier barrier) {
        this.barrier = barrier;
        stage = Stage.MOVEMENT;
        start();
    }

    @Override
    public void run() {
        try {
            doWork();
        } catch (Throwable e) {
            throwable = e;
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }

    private void doWork() {
        while (true) {
            switch (stage) {
                case MOVEMENT:
                    movement();
                    stage = Stage.EATING;
                    break;
                case EATING:
                    eating();
                    stage = Stage.REPRODUCTION;
                    break;
                case REPRODUCTION:
                    reproduction();
                    stage = Stage.MOVEMENT;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + "stage == " + stage);
            }

            try {
                barrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void movement() {

    }

    private void eating() {

    }

    private void reproduction() {

    }

    private void nextOrganism() {

    }
}
