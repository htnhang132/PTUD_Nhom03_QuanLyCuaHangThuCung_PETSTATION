package com.petstation.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/** Short-lived connections; nested work shares the same transaction and rolls back together. */
public final class SqlDatabase {
    @FunctionalInterface
    public interface Work<T> {
        T run(Connection connection) throws Exception;
    }

    private static DatabaseConnection database;
    private static final ThreadLocal<Connection> CURRENT = new ThreadLocal<>();

    private SqlDatabase() {}

    public static void configure(DatabaseConnection value) {
        database = Objects.requireNonNull(value);
    }

    public static <T> T transaction(Work<T> work) {
        Connection existing = CURRENT.get();
        if (existing != null) {
            Savepoint savepoint;
            try {
                savepoint = existing.setSavepoint();
            } catch (SQLException e) {
                throw failure(e);
            }
            try {
                return work.run(existing);
            } catch (Exception e) {
                try {
                    existing.rollback(savepoint);
                } catch (SQLException rollback) {
                    e.addSuppressed(rollback);
                }
                throw failure(e);
            }
        }
        if (database == null) throw new IllegalStateException("Chưa cấu hình kết nối SQL Server.");
        try (Connection connection = database.open()) {
            connection.setAutoCommit(false);
            CURRENT.set(connection);
            try {
                T result = work.run(connection);
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                throw failure(e);
            } finally {
                CURRENT.remove();
            }
        } catch (SQLException e) {
            throw failure(e);
        }
    }

    private static RuntimeException failure(Exception e) {
        return e instanceof RuntimeException r
                ? r
                : new DataAccessException("Không thể truy cập SQL Server: " + e.getMessage(), e);
    }

    private static <T> T connection(Work<T> work) {
        Connection current = CURRENT.get();
        if (current == null) return transaction(work);
        try {
            return work.run(current);
        } catch (Exception e) {
            throw failure(e);
        }
    }

    private static PreparedStatement prepare(Connection c, String sql, Object... args)
            throws SQLException {
        PreparedStatement statement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < args.length; i++) {
            Object value = args[i];
            if (value instanceof LocalDate date)
                statement.setDate(i + 1, java.sql.Date.valueOf(date));
            else if (value instanceof LocalDateTime time)
                statement.setTimestamp(i + 1, Timestamp.valueOf(time));
            else statement.setObject(i + 1, value);
        }
        return statement;
    }

    public static List<Object[]> query(String sql, Object... args) {
        return connection(
                c -> {
                    try (PreparedStatement statement = prepare(c, sql, args);
                            ResultSet rs = statement.executeQuery()) {
                        List<Object[]> rows = new ArrayList<>();
                        int size = rs.getMetaData().getColumnCount();
                        while (rs.next()) {
                            Object[] row = new Object[size];
                            for (int i = 0; i < size; i++) row[i] = rs.getObject(i + 1);
                            rows.add(row);
                        }
                        return rows;
                    }
                });
    }

    public static int update(String sql, Object... args) {
        return connection(
                c -> {
                    try (PreparedStatement statement = prepare(c, sql, args)) {
                        return statement.executeUpdate();
                    }
                });
    }

    public static long insert(String sql, Object... args) {
        return connection(
                c -> {
                    try (PreparedStatement statement = prepare(c, sql, args)) {
                        statement.executeUpdate();
                        try (ResultSet keys = statement.getGeneratedKeys()) {
                            if (!keys.next())
                                throw new SQLException("Không nhận được mã bản ghi vừa tạo.");
                            return keys.getLong(1);
                        }
                    }
                });
    }

    public static Object scalar(String sql, Object... args) {
        List<Object[]> rows = query(sql, args);
        return rows.isEmpty() ? null : rows.get(0)[0];
    }

    public static long number(String sql, Object... args) {
        Object value = scalar(sql, args);
        return value == null ? 0 : ((Number) value).longValue();
    }

    public static String code(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

    public static String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
