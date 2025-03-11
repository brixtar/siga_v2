// src/main/java/com/siga/config/DatabaseConfig.java

package com.siga.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();
    private static DatabaseConfig instance;

    private DatabaseConfig() {
        loadProperties();
    }

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("No se pudo encontrar database.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error al cargar database.properties", ex);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}// src/main/java/com/siga/config/DatabaseConfig.java

package com.siga.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();
    private static DatabaseConfig instance;

    private DatabaseConfig() {
        loadProperties();
    }

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("No se pudo encontrar database.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error al cargar database.properties", ex);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}