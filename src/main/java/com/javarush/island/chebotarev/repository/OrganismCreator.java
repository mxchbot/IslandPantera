package com.javarush.island.chebotarev.repository;

import com.javarush.island.chebotarev.component.Utils;
import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.organism.OrganismConfig;
import com.javarush.island.chebotarev.organism.herbivore.Goat;
import com.javarush.island.chebotarev.organism.insect.Caterpillar;
import com.javarush.island.chebotarev.organism.plant.Grass;
import com.javarush.island.chebotarev.organism.predator.Wolf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class OrganismCreator {

    private static final Class<?>[] types = {
            Wolf.class,
            Goat.class,
            Grass.class,
            Caterpillar.class
    };
    private static final Map<String, Organism> prototypes = createPrototypes();

    public Organism create(String name) {
        Organism prototype = prototypes.get(name);
        if (prototype == null) {
            throw new IllegalArgumentException("No such organism: " + name);
        }
        return prototype.clone();
    }

    private static Map<String, Organism> createPrototypes() {
        Map<String, Organism> organisms = new HashMap<>();
        for (Class<?> type : types) {
            OrganismConfig config = Utils.loadConfigYAML(type, OrganismConfig.class);
            Organism organism = generatePrototype(type, config);
            organisms.put(config.getName(), organism);
        }
        return organisms;
    }

    private static Organism generatePrototype(Class<?> type, OrganismConfig config) {
        try {
            Constructor<?> constructor = type.getConstructor(OrganismConfig.class);
            return (Organism) constructor.newInstance(config);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
