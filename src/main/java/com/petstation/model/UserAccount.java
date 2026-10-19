package com.petstation.model;

import java.time.LocalDateTime;

public record UserAccount(
        long id,
        long employeeId,
        String username,
        String passwordHash,
        Role role,
        AccountStatus status,
        int failedLoginCount,
        LocalDateTime lockedUntil,
        boolean passwordChangeRequired) {
    public boolean temporarilyLocked(LocalDateTime now) {
        return lockedUntil != null && lockedUntil.isAfter(now);
    }
}
