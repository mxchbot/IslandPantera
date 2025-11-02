package com.javarush.island.chebotarev.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Utils {

    public static final ObjectMapper mapperYAML = new ObjectMapper(new YAMLFactory());

    public static <ET, CT> CT loadConfig(Class<ET> entityClass, Class<CT> configClass, ObjectMapper mapper) {
        if (!entityClass.isAnnotationPresent(Config.class)) {
            throw new IllegalArgumentException("Type " + entityClass.getSimpleName() + " is not annotated with @Config");
        }
        Config typeData = entityClass.getAnnotation(Config.class);
        String configPath = Const.CONFIG_DIRECTORY + '/' + typeData.filename();
        URL resource = entityClass.getClassLoader().getResource(configPath);
        if (resource == null) {
            throw new RuntimeException("Could not locate " + configPath);
        }
        try {
            CT config = mapper.readValue(resource, configClass);
            return Objects.requireNonNull(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
