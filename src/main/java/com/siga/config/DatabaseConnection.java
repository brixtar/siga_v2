// src/main/java/com/siga/config/DatabaseConnection.java

package com.siga.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            DatabaseConfig config = DatabaseConfig.getInstance();
            String url = config.getProperty("db.url");
            String user = config.getProperty("db.user");
            String password = config.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos", e);
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            logger.error("Error al verificar la conexión", e);
            throw new RuntimeException("Error al verificar la conexión", e);
        }
        return connection;
    }
}