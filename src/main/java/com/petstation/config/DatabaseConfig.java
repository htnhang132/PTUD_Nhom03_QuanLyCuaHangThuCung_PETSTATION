package com.petstation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record DatabaseConfig(String url, String user, String password, int loginTimeoutSeconds) {
    public static DatabaseConfig load() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getResourceAsStream("/db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Không thể đọc cấu hình cơ sở dữ liệu.", exception);
        }

        String url = value("PETSTATION_DB_URL", properties, "db.url", "");
        String user = value("PETSTATION_DB_USER", properties, "db.user", "");
        String password = value("PETSTATION_DB_PASSWORD", properties, "db.password", "");
        int timeout = Integer.parseInt(value(
            "PETSTATION_DB_LOGIN_TIMEOUT_SECONDS", properties, "db.loginTimeoutSeconds", "10"
        ));

        if (url.isBlank() || user.isBlank()) {
            throw new IllegalStateException(
                "Chưa cấu hình SQL Server. Hãy đặt PETSTATION_DB_URL, PETSTATION_DB_USER, "
                    + "PETSTATION_DB_PASSWORD hoặc tạo src/main/resources/db.properties."
            );
        }
        return new DatabaseConfig(url, user, password, timeout);
    }

    private static String value(String environmentKey, Properties properties, String propertyKey, String fallback) {
        String environmentValue = System.getenv(environmentKey);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }
        return properties.getProperty(propertyKey, fallback).trim();
    }
}
