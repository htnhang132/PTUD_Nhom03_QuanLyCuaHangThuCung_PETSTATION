package com.petstation.dao.impl;

import com.petstation.dao.interfaces.AuditLogDao;
import com.petstation.model.AuditLogEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;

public final class JdbcAuditLogDao implements AuditLogDao {
    @Override
    public void insert(Connection connection, AuditLogEntry entry) throws Exception {
        String sql = """
            INSERT INTO nhat_ky_he_thong
                (tai_khoan_id, hanh_dong, loai_doi_tuong, ma_doi_tuong,
                 gia_tri_cu, gia_tri_moi, ghi_chu, thoi_gian)
            VALUES (?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME())
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (entry.actorAccountId() == null) {
                statement.setNull(1, java.sql.Types.BIGINT);
            } else {
                statement.setLong(1, entry.actorAccountId());
            }
            statement.setString(2, entry.action());
            statement.setString(3, entry.entityType());
            statement.setString(4, entry.entityId());
            statement.setString(5, entry.oldValue());
            statement.setString(6, entry.newValue());
            statement.setString(7, entry.note());
            statement.executeUpdate();
        }
    }
}
