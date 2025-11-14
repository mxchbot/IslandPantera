package com.javarush.island.chebotarev.component;

import com.javarush.island.chebotarev.island.GlobalOrganismList;
import com.javarush.island.chebotarev.island.Island;
import com.javarush.island.chebotarev.island.OrganismGroupsIterator;
import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.organism.OrganismGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class ThreadWorker extends ThreadAction {

    private static Island island;
    private static GlobalOrganismList globalOrganismList;
    private static OrganismGroupsIterator organismGroupsIterator;
    private static CyclicBarrier tickBarrier;
    private static OnCompletedGlobalListIteration onCompletedGlobalListIteration;
    private static OnCompletedOrganismGroupsIteration onCompletedOrganismGroupsIteration;
    private static CyclicBarrier movementBarrier;
    private static CyclicBarrier eatingBarrier;
    private static CyclicBarrier clearingBarrier;
    private static CyclicBarrier reproductionBarrier;
    private final List<Organism> disappearedOrganisms = new ArrayList<>();

    public ThreadWorker(Island island,
                        CyclicBarrier tickBarrier,
                        int threadsNum) {
        if (ThreadWorker.island == null) {
            ThreadWorker.island = island;
        }
        if (globalOrganismList == null) {
            globalOrganismList = island.getGlobalOrganismList();
        }
        if (organismGroupsIterator == null) {
            organismGroupsIterator = island.getOrganismGroupsIterator();
        }
        if (ThreadWorker.tickBarrier == null) {
            ThreadWorker.tickBarrier = tickBarrier;
        }
        if (onCompletedGlobalListIteration == null) {
            onCompletedGlobalListIteration = new OnCompletedGlobalListIteration(globalOrganismList);
        }
        if (onCompletedOrganismGroupsIteration == null) {
            onCompletedOrganismGroupsIteration = new OnCompletedOrganismGroupsIteration(organismGroupsIterator,
                    globalOrganismList);
        }
        if (movementBarrier == null) {
            movementBarrier = new CyclicBarrier(threadsNum,
                    onCompletedGlobalListIteration);
        }
        if (eatingBarrier == null) {
            eatingBarrier = new CyclicBarrier(threadsNum,
                    onCompletedGlobalListIteration);
        }
        if (clearingBarrier == null) {
            clearingBarrier = new CyclicBarrier(threadsNum);
        }
        if (reproductionBarrier == null) {
            reproductionBarrier = new CyclicBarrier(threadsNum,
                    onCompletedOrganismGroupsIteration);
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
        while (globalOrganismList.hasNextOrganism()) {
            Organism organism = globalOrganismList.nextOrganism();
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
        while (globalOrganismList.hasNextOrganism()) {
            Organism organism = globalOrganismList.nextOrganism();
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

    private void reproduction() throws Throwable {
        while (organismGroupsIterator.hasNextGroup()) {
            OrganismGroup group = organismGroupsIterator.nextGroup();
            if (group != null) {
                List<Organism> children = group.reproduction();
                if (children != null) {
                    if (children.isEmpty()) {
                        throw new IllegalStateException("There are no children");
                    }
                    island.add(children, group);
                }
            }
        }

        reproductionBarrier.await();
        Throwable throwable = onCompletedOrganismGroupsIteration.getThrowable();
        if (throwable != null) {
            throw throwable;
        }
    }

    private static class OnCompletedGlobalListIteration extends ThreadAction {

        private final GlobalOrganismList globalOrganismList;

        public OnCompletedGlobalListIteration(GlobalOrganismList globalOrganismList) {
            this.globalOrganismList = globalOrganismList;
        }

        @Override
        protected void doAction() {
            globalOrganismList.resetOrganismIndex();
        }
    }

    private static class OnCompletedOrganismGroupsIteration extends ThreadAction {

        private final OrganismGroupsIterator organismGroupsIterator;
        private final GlobalOrganismList globalOrganismList;

        private OnCompletedOrganismGroupsIteration(OrganismGroupsIterator organismGroupsIterator,
                                                   GlobalOrganismList globalOrganismList) {
            this.organismGroupsIterator = organismGroupsIterator;
            this.globalOrganismList = globalOrganismList;
        }

        @Override
        protected void doAction() throws Throwable {
            organismGroupsIterator.reset();
            globalOrganismList.resetOrganismIndex();
        }
    }
}
