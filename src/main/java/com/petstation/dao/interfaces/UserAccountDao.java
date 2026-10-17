package com.petstation.dao.interfaces;

import com.petstation.model.Permission;
import com.petstation.model.UserAccount;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface UserAccountDao {
    Optional<UserAccount> findByUsername(Connection connection, String username) throws Exception;
    Set<Permission> findPermissions(Connection connection, long accountId) throws Exception;
    void recordFailedLogin(Connection connection, long accountId, int failedCount, LocalDateTime lockedUntil) throws Exception;
    void recordSuccessfulLogin(Connection connection, long accountId) throws Exception;
}
