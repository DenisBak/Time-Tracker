package com.denis.domain.factories;

import com.denis.domain.exceptions.DomainException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public class ConfigFactory {
    private static Map<String, Configuration> allConfigurations;
    private static Configurations configs = new Configurations();
    private static Logger logger = LogManager.getLogger();

    static {
        try {
            allConfigurations = new HashMap<>();

            File configFile = new File("classes/propertiesLocations.properties");
            logger.debug("Configuration readability: " + configFile.canRead());

            Configuration configsLocations = configs.properties(configFile);

            initializePropertiesMap(configsLocations);
        } catch (ConfigurationException e) {
            DomainException ex = new DomainException(e);
            logger.error("Can't load properties file/files. Check file status in src/java/resources/<yourProperty>.properties", ex);
        }
    }

    private static void initializePropertiesMap(Configuration locations) throws ConfigurationException {
        Iterator<String> propertiesNames = locations.getKeys();

        String configLocation;
        String name;

        while (propertiesNames.hasNext()) {
            name = propertiesNames.next();
            configLocation = locations.getString(name);

            Configuration configuration = configs.properties(new File(configLocation));
            allConfigurations.put(name, configuration);
        }
    }

    public static Configuration getConfigByName(ConfigNames name) {
        return allConfigurations.get(name.getName());
    }
}
