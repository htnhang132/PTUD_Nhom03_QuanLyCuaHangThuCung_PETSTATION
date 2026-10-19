package com.petstation.dao;

import java.sql.Connection;
import java.sql.SQLException;

public final class TransactionManager {
    @FunctionalInterface
    public interface TransactionWork<T> {
        T execute(Connection connection) throws Exception;
    }

    private final DatabaseConnection database;

    public TransactionManager(DatabaseConnection database) {
        this.database = database;
    }

    public <T> T execute(TransactionWork<T> work) {
        try (Connection connection = database.open()) {
            connection.setAutoCommit(false);
            try {
                T result = work.execute(connection);
                connection.commit();
                return result;
            } catch (RuntimeException exception) {
                connection.rollback();
                throw exception;
            } catch (Exception exception) {
                connection.rollback();
                throw new DataAccessException("Giao dịch đã bị hoàn tác.", exception);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Không thể kết nối SQL Server.", exception);
        }
    }
}
