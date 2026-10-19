package com.petstation.service;

import static com.petstation.service.SessionService.*;

import com.petstation.dao.AuditLogDao;
import com.petstation.dao.EmployeeDao;
import com.petstation.dao.TransactionManager;
import com.petstation.dao.UserAccountDao;
import com.petstation.model.AccountStatus;
import com.petstation.model.AuditLogEntry;
import com.petstation.model.Employee;
import com.petstation.model.Permission;
import com.petstation.model.UserAccount;
import com.petstation.model.UserSession;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Set;

public final class AuthenticationService {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final TransactionManager transactions;
    private final UserAccountDao accounts;
    private final EmployeeDao employees;
    private final AuditLogDao auditLogs;

    public AuthenticationService(
            TransactionManager transactions,
            UserAccountDao accounts,
            EmployeeDao employees,
            AuditLogDao auditLogs) {
        this.transactions = transactions;
        this.accounts = accounts;
        this.employees = employees;
        this.auditLogs = auditLogs;
    }

    public UserSession login(String rawUsername, char[] password) {
        String username = rawUsername == null ? "" : rawUsername.trim();
        if (username.isEmpty()) {
            throw new AuthenticationException("Vui lòng nhập tên đăng nhập.");
        }
        if (password == null || password.length == 0) {
            throw new AuthenticationException("Vui lòng nhập mật khẩu.");
        }

        try {
            LoginResult result =
                    transactions.execute(
                            connection -> {
                                UserAccount account =
                                        accounts.findByUsername(connection, username).orElse(null);
                                if (account == null) {
                                    return LoginResult.failure(
                                            "Tên đăng nhập hoặc mật khẩu không đúng.");
                                }
                                LocalDateTime now = LocalDateTime.now();

                                if (account.status() != AccountStatus.ACTIVE) {
                                    writeAudit(
                                            connection,
                                            account.id(),
                                            "LOGIN_REJECTED",
                                            "Tài khoản không hoạt động");
                                    return LoginResult.failure(
                                            "Tài khoản đã bị khóa. Vui lòng liên hệ Cửa hàng"
                                                    + " trưởng.");
                                }
                                if (account.temporarilyLocked(now)) {
                                    writeAudit(
                                            connection,
                                            account.id(),
                                            "LOGIN_REJECTED",
                                            "Tài khoản đang tạm khóa");
                                    return LoginResult.failure(
                                            "Tài khoản bị tạm khóa đến "
                                                    + account.lockedUntil()
                                                    + ".");
                                }

                                String suppliedPassword = new String(password);
                                if (!BCrypt.checkpw(suppliedPassword, account.passwordHash())) {
                                    int failedCount = account.failedLoginCount() + 1;
                                    LocalDateTime lockedUntil =
                                            failedCount >= MAX_FAILED_ATTEMPTS
                                                    ? now.plusMinutes(LOCK_MINUTES)
                                                    : null;
                                    accounts.recordFailedLogin(
                                            connection, account.id(), failedCount, lockedUntil);
                                    writeAudit(
                                            connection,
                                            account.id(),
                                            "LOGIN_FAILED",
                                            "Sai mật khẩu lần " + failedCount);
                                    if (lockedUntil != null) {
                                        return LoginResult.failure(
                                                "Tài khoản bị tạm khóa. Vui lòng liên hệ Cửa hàng"
                                                        + " trưởng.");
                                    }
                                    return LoginResult.failure(
                                            "Tên đăng nhập hoặc mật khẩu không đúng. Bạn còn "
                                                    + (MAX_FAILED_ATTEMPTS - failedCount)
                                                    + "lần thử.");
                                }

                                Employee employee =
                                        employees
                                                .findById(connection, account.employeeId())
                                                .filter(Employee::active)
                                                .orElse(null);
                                if (employee == null) {
                                    writeAudit(
                                            connection,
                                            account.id(),
                                            "LOGIN_REJECTED",
                                            "Nhân viên đã ngừng làm việc");
                                    return LoginResult.failure("Nhân viên đã ngừng làm việc.");
                                }
                                accounts.recordSuccessfulLogin(connection, account.id());
                                Set<Permission> permissions =
                                        accounts.findPermissions(connection, account.id());
                                writeAudit(
                                        connection,
                                        account.id(),
                                        "LOGIN_SUCCESS",
                                        "Đăng nhập thành công");
                                return LoginResult.success(
                                        new UserSession(account, employee, permissions));
                            });
            if (result.errorMessage() != null) {
                throw new AuthenticationException(result.errorMessage());
            }
            return result.session();
        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }

    private void writeAudit(
            java.sql.Connection connection, Long accountId, String action, String note)
            throws Exception {
        auditLogs.insert(
                connection,
                new AuditLogEntry(
                        0,
                        accountId,
                        action,
                        "TAI_KHOAN",
                        accountId == null ? null : accountId.toString(),
                        null,
                        null,
                        note,
                        LocalDateTime.now()));
    }

    private record LoginResult(UserSession session, String errorMessage) {
        static LoginResult success(UserSession session) {
            return new LoginResult(session, null);
        }

        static LoginResult failure(String message) {
            return new LoginResult(null, message);
        }
    }
}
