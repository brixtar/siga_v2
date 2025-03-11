// src/main/java/com/siga/repository/impl/BaseRepositoryImpl.java

package com.siga.repository.impl;

import com.siga.config.DatabaseConnection;
import com.siga.repository.BaseRepository;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseRepositoryImpl<T, ID> implements BaseRepository<T, ID> {
    protected final Connection connection;

    protected BaseRepositoryImpl() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        this.connection = dbConnection.getConnection();
    }

    protected void handleSQLException(SQLException e, String operation) {
        log.error("Error during {} operation: {}", operation, e.getMessage());
        throw new RuntimeException("Database error during " + operation, e);
    }
}