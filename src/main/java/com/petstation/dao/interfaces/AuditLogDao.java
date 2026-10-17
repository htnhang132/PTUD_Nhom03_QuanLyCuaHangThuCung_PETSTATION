package com.petstation.dao.interfaces;

import com.petstation.model.AuditLogEntry;

import java.sql.Connection;

public interface AuditLogDao {
    void insert(Connection connection, AuditLogEntry entry) throws Exception;
}
