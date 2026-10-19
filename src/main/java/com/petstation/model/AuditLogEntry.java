package com.petstation.model;

import java.time.LocalDateTime;

public record AuditLogEntry(
        long id,
        Long actorAccountId,
        String action,
        String entityType,
        String entityId,
        String oldValue,
        String newValue,
        String note,
        LocalDateTime occurredAt) {}
