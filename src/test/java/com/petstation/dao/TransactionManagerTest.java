package com.petstation.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

class TransactionManagerTest {
    private DatabaseConnection database;
    private Connection connection;
    private TransactionManager transactions;

    @BeforeEach
    void setUp() throws Exception {
        database = mock(DatabaseConnection.class);
        connection = mock(Connection.class);
        when(database.open()).thenReturn(connection);
        transactions = new TransactionManager(database);
    }

    @Test
    void commitsOnSuccess() throws Exception {
        String result = transactions.execute(c -> "ok");

        assertThat(result).isEqualTo("ok");
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection, never()).rollback();
    }

    @Test
    void propagatesRuntimeExceptionAndRollsBack() throws Exception {
        IllegalStateException failure = new IllegalStateException("boom");

        assertThatThrownBy(
                        () ->
                                transactions.execute(
                                        c -> {
                                            throw failure;
                                        }))
                .isSameAs(failure);

        verify(connection).rollback();
        verify(connection, never()).commit();
    }

    @Test
    void wrapsCheckedExceptionAndRollsBack() throws Exception {
        assertThatThrownBy(
                        () ->
                                transactions.execute(
                                        c -> {
                                            throw new SQLException("db error");
                                        }))
                .isInstanceOf(DataAccessException.class)
                .hasCauseInstanceOf(SQLException.class);

        verify(connection).rollback();
        verify(connection, never()).commit();
    }
}
