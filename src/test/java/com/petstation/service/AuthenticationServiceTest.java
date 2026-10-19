package com.petstation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.petstation.dao.AuditLogDao;
import com.petstation.dao.DatabaseConnection;
import com.petstation.dao.EmployeeDao;
import com.petstation.dao.TransactionManager;
import com.petstation.dao.UserAccountDao;
import com.petstation.model.AccountStatus;
import com.petstation.model.Employee;
import com.petstation.model.Permission;
import com.petstation.model.Role;
import com.petstation.model.UserAccount;
import com.petstation.model.UserSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

class AuthenticationServiceTest {
    private UserAccountDao accounts;
    private EmployeeDao employees;
    private AuditLogDao auditLogs;
    private AuthenticationService service;

    private UserAccount activeAccount(
            String username, String password, int failedCount, LocalDateTime lockedUntil) {
        return new UserAccount(
                1L,
                7L,
                username,
                BCrypt.hashpw(password, BCrypt.gensalt(4)),
                Role.SALES_STAFF,
                AccountStatus.ACTIVE,
                failedCount,
                lockedUntil,
                false);
    }

    @BeforeEach
    void setUp() throws Exception {
        accounts = mock(UserAccountDao.class);
        employees = mock(EmployeeDao.class);
        auditLogs = mock(AuditLogDao.class);

        DatabaseConnection database = mock(DatabaseConnection.class);
        when(database.open()).thenReturn(mock(Connection.class));
        TransactionManager transactions = new TransactionManager(database);
        service = new AuthenticationService(transactions, accounts, employees, auditLogs);
    }

    @Test
    void rejectsBlankUsername() {
        assertThatThrownBy(() -> service.login("  ", "secret".toCharArray()))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void rejectsEmptyPassword() {
        assertThatThrownBy(() -> service.login("admin", new char[0]))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void failsForUnknownUsername() throws Exception {
        when(accounts.findByUsername(any(), eq("ghost"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login("ghost", "secret123".toCharArray()))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void failsForWrongPasswordAndRecordsFailure() throws Exception {
        UserAccount account = activeAccount("admin", "secret123", 0, null);
        when(accounts.findByUsername(any(), eq("admin"))).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> service.login("admin", "wrong-pass".toCharArray()))
                .isInstanceOf(AuthenticationException.class);

        verify(accounts).recordFailedLogin(any(), eq(1L), eq(1), isNull());
        verify(auditLogs).insert(any(), argThat(e -> "LOGIN_FAILED".equals(e.action())));
    }

    @Test
    void locksAccountAfterFiveFailures() throws Exception {
        UserAccount account = activeAccount("admin", "secret123", 4, null);
        when(accounts.findByUsername(any(), eq("admin"))).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> service.login("admin", "wrong-pass".toCharArray()))
                .isInstanceOf(AuthenticationException.class);

        verify(accounts).recordFailedLogin(any(), eq(1L), eq(5), notNull());
    }

    @Test
    void rejectsTemporarilyLockedAccount() throws Exception {
        UserAccount account =
                activeAccount("admin", "secret123", 5, LocalDateTime.now().plusMinutes(10));
        when(accounts.findByUsername(any(), eq("admin"))).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> service.login("admin", "secret123".toCharArray()))
                .isInstanceOf(AuthenticationException.class);

        verify(accounts, never()).recordSuccessfulLogin(any(), anyLong());
        verify(auditLogs).insert(any(), argThat(e -> "LOGIN_REJECTED".equals(e.action())));
    }

    @Test
    void succeedsWithValidCredentials() throws Exception {
        UserAccount account = activeAccount("admin", "secret123", 0, null);
        Employee employee =
                new Employee(
                        7L,
                        "NV007",
                        "Nguyễn Văn Quản",
                        null,
                        "0900000000",
                        null,
                        "Cửa hàng trưởng",
                        LocalDate.now(),
                        true);
        when(accounts.findByUsername(any(), eq("admin"))).thenReturn(Optional.of(account));
        when(employees.findById(any(), eq(7L))).thenReturn(Optional.of(employee));
        when(accounts.findPermissions(any(), eq(1L))).thenReturn(Set.of(Permission.POS_SELL));

        UserSession session = service.login("admin", "secret123".toCharArray());

        assertThat(session.employee().id()).isEqualTo(7L);
        assertThat(session.has(Permission.POS_SELL)).isTrue();
        verify(accounts).recordSuccessfulLogin(any(), eq(1L));
        verify(auditLogs).insert(any(), argThat(e -> "LOGIN_SUCCESS".equals(e.action())));
    }
}
