package com.javarush.island.chebotarev.repository;

import com.javarush.island.chebotarev.config.Settings;
import com.javarush.island.chebotarev.organism.Organism;
import com.javarush.island.chebotarev.config.OrganismConfig;
import com.javarush.island.chebotarev.organism.herbivore.Goat;
import com.javarush.island.chebotarev.organism.insect.Caterpillar;
import com.javarush.island.chebotarev.organism.plant.Grass;
import com.javarush.island.chebotarev.organism.predator.Wolf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OrganismCreator {

    private static final Class<?>[] types = {
            Wolf.class,
            Goat.class,
            Grass.class,
            Caterpillar.class
    };
    private static final Map<String, Organism> prototypes = createPrototypes();

    public static Set<String> getPrototypesNames() {
        return prototypes.keySet();
    }

    public Organism create(String name) {
        Organism prototype = prototypes.get(name);
        if (prototype == null) {
            throw new IllegalArgumentException("No such organism: " + name);
        }
        return prototype.clone();
    }

    private static Map<String, Organism> createPrototypes() {
        Map<String, Organism> organisms = new HashMap<>();
        Map<String, OrganismConfig> configs = Settings.get().getOrganisms();
        for (Class<?> type : types) {
            String name = type.getSimpleName();
            OrganismConfig config = configs.get(name);
            if (config == null) {
                throw new IllegalArgumentException("No such organism: " + name);
            }
            Organism organism = generatePrototype(type, name, config);
            organisms.put(name, organism);
        }
        return organisms;
    }

    private static Organism generatePrototype(Class<?> type, String name, OrganismConfig config) {
        try {
            Constructor<?> constructor = type.getConstructor(String.class, OrganismConfig.class);
            return (Organism) constructor.newInstance(name, config);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
