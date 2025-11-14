package com.javarush.island.chebotarev.organism;

import com.javarush.island.chebotarev.component.Utils;
import com.javarush.island.chebotarev.config.GlobalOrganismConfig;
import com.javarush.island.chebotarev.config.OrganismConfig;
import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.island.Cell;
import com.javarush.island.chebotarev.repository.OrganismCreator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Organism implements Cloneable {

    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private final String name;
    private final OrganismConfig config;
    private final double starvationKilos;
    private final double weightLossKilos;
    private final double minWeight;
    private final int dissolutionTicks;
    private Integer id = idCounter.getAndIncrement();
    private Cell cell;
    private int globalListIndex;
    private double weight;
    private double saturation;
    private boolean becamePrey;
    private int dissolutionTickCount;

    public Organism(String name, OrganismConfig config) {
        this.name = name;
        this.config = config;
        GlobalOrganismConfig globalConfig = Settings.get().getGlobalOrganismConfig();
        starvationKilos = config.getCompleteSaturation() * globalConfig.getStarvation() * 0.01;
        weightLossKilos = config.getMaxWeight() * globalConfig.getWeightLoss() * 0.01;
        minWeight = config.getMaxWeight() / 4.0;
        dissolutionTicks = globalConfig.getDissolution();
        weight = config.getMaxWeight();
        saturation = config.getCompleteSaturation() * 0.5;
    }

    public String getName() {
        return name;
    }

    public OrganismConfig getConfig() {
        return config;
    }

    public Integer getId() {
        return id;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public int getGlobalListIndex() {
        return globalListIndex;
    }

    public void setGlobalListIndex(int globalListIndex) {
        this.globalListIndex = globalListIndex;
    }

    public double getWeight() {
        return weight;
    }

    protected void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public Organism clone() {
        Organism clone;
        try {
            clone = (Organism) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.id = idCounter.getAndIncrement();
        clone.weight = config.getMaxWeight();
        return clone;
    }

    public void removeFromCell() {
        cell.removeOrganism(this);
    }

    public void movement() {
        if (!isAlive()) {
            return;
        }
        int maxSpeed = config.getMaxSpeed();
        if (maxSpeed == 0) {
            throw new RuntimeException("Max speed has to be greater than 0");
        }
        Set<Cell> visitedCells = new HashSet<>();
        int speed = Utils.random(1, (maxSpeed + 1));
        for (int i = 0; i < speed; i++) {
            List<Cell> nextCells = cell.cloneNextCells();
            Collections.shuffle(nextCells, Utils.getThreadLocalRandom());
            Cell previousCell = cell;
            for (Cell nextCell : nextCells) {
                if (!visitedCells.contains(nextCell)) {
                    if (nextCell.addOrganism(this)) {
                        cell.removeOrganism(this);
                        visitedCells.add(cell);
                        cell = nextCell;
                        break;
                    }
                }
            }
            if (cell == previousCell) {
                break;
            }
        }
    }

    public void eating(List<Organism> disappearedOrganisms) {
        boolean thisOrganismIsDisappeared;
        boolean thisOrganismIsDead;
        boolean thisOrganismIsPrey;
        synchronized (this) {
            thisOrganismIsDisappeared = isDisappeared();
            thisOrganismIsDead = isDead();
            thisOrganismIsPrey = becamePrey;
        }
        if (thisOrganismIsDisappeared) {
            disappearedOrganisms.add(this);
            return;
        }
        if (thisOrganismIsDead || thisOrganismIsPrey) {
            dissolution(disappearedOrganisms);
            return;
        }

        starvation();

        synchronized (this) {
            thisOrganismIsDisappeared = isDisappeared();
            thisOrganismIsDead = isDead();
        }
        if (thisOrganismIsDisappeared) {
            disappearedOrganisms.add(this);
            return;
        }
        if (thisOrganismIsDead) {
            return;
        }

        catchPreys();
    }

    public List<Organism> reproduction(Collection<Organism> organisms) {
        int countOfWellFed = 0;
        for (Organism organism : organisms) {
            if (organism.isAlive()
                    && (organism.saturation > 0)
                    && !Utils.isZero(organism.saturation)) {
                countOfWellFed++;
            }
        }
        if (countOfWellFed > 1) {
            int childrenNum = countOfWellFed / 2;
            return createChildren(childrenNum);
        } else {
            return null;
        }
    }

    protected boolean isDisappeared() {
        return Utils.isZero(weight) || Utils.isNegative(weight);
    }

    protected List<Organism> createChildren(int childrenNum) {
        List<Organism> children = new ArrayList<>(childrenNum);
        Organism prototype = OrganismCreator.create(name);
        for (int i = 0; i < childrenNum; i++) {
            Organism clone = prototype.clone();
            clone.weight = config.getMaxWeight() / 3.0;
            children.add(clone);
        }
        return children;
    }

    private boolean isAlive() {
        return !becamePrey && !isDead() && !isDisappeared();
    }

    private void dissolution(List<Organism> disappearedOrganisms) {
        dissolutionTickCount++;
        if (dissolutionTickCount >= dissolutionTicks) {
            disappearedOrganisms.add(this);
        }
    }

    private void starvation() {
        if (Utils.isZero(saturation)) {
            synchronized (this) {
                if (!becamePrey) {
                    weight -= weightLossKilos;
                }
            }
        } else {
            double realStarvationKilos;
            double newSaturation = saturation - starvationKilos;
            if (Utils.isZero(newSaturation) || Utils.isNegative(newSaturation)) {
                realStarvationKilos = saturation;
                saturation = 0;
            } else {
                realStarvationKilos = starvationKilos;
                saturation = newSaturation;
            }
            double maxWeight = config.getMaxWeight();
            double weightGain = realStarvationKilos * 0.5;
            synchronized (this) {
                if (!becamePrey) {
                    weight += weightGain;
                    if (weight > maxWeight) {
                        weight = maxWeight;
                    }
                }
            }
        }
    }

    private void catchPreys() {
        double completeSaturation = config.getCompleteSaturation();
        if (completeSaturation == 0) {
            throw new RuntimeException("Complete saturation has to be greater than 0");
        }
        Set<String> preysNames = config.getPreys().keySet();
        List<Organism> preys = cell.collectPreys(preysNames);
        Collections.shuffle(preys, Utils.getThreadLocalRandom());
        for (Organism prey : preys) {
            if (!tryToCatchPrey(prey)) {
                continue;
            }
            double needSaturation = completeSaturation - saturation;
            double howMuchFoodWasEaten;
            synchronized (this) {
                if (becamePrey) {
                    return;
                }
            }
            synchronized (prey) {
                if (prey.isDisappeared()) {
                    continue;
                }
                howMuchFoodWasEaten = prey.becomePrey(needSaturation);
            }
            saturation += howMuchFoodWasEaten;
            if (Utils.isEqual(saturation, completeSaturation) || (saturation > completeSaturation)) {
                saturation = completeSaturation;
                return;
            }
        }
    }

    private boolean tryToCatchPrey(Organism prey) {
        Integer chance = config.getPreys().get(prey.name);
        checkChance(chance, prey.name);
        if (chance < 100) {
            boolean isPreyCaught = Utils.random(1, (100 + 1)) <= chance;
            if (isPreyCaught) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void checkChance(Integer chance, String preyName) {
        if (chance == null) {
            throw new RuntimeException("Unknown prey " + preyName + ", organism " + name);
        }
        if (chance == 0) {
            throw new RuntimeException("Chance for prey " + preyName + " has to be greater than 0" + ", organism " + name);
        }
    }

    private boolean isDead() {
        return weight < minWeight;
    }

    private double becomePrey(double needSaturation) {
        becamePrey = true;
        double newWeight = weight - needSaturation;
        if (Utils.isZero(newWeight) || Utils.isNegative(newWeight)) {
            double oldWeight = weight;
            weight = 0;
            return oldWeight;
        } else {
            weight = newWeight;
            return needSaturation;
        }
    }
}
