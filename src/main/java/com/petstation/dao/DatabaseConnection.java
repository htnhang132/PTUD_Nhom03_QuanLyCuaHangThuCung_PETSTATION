package com.petstation.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private final DatabaseConfig config;

    public DatabaseConnection(DatabaseConfig config) {
        this.config = config;
        DriverManager.setLoginTimeout(config.loginTimeoutSeconds());
    }

    public Connection open() throws SQLException {
        return DriverManager.getConnection(config.url(), config.user(), config.password());
    }

    public void verify() throws SQLException {
        try (Connection connection = open()) {
            if (!connection.isValid(config.loginTimeoutSeconds())) {
                throw new SQLException("SQL Server không phản hồi.");
            }
        }
    }
}
