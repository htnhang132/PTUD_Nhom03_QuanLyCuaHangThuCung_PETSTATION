package com.petstation.dao.impl;

import com.petstation.dao.interfaces.UserAccountDao;
import com.petstation.model.AccountStatus;
import com.petstation.model.Permission;
import com.petstation.model.Role;
import com.petstation.model.UserAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public final class JdbcUserAccountDao implements UserAccountDao {
    @Override
    public Optional<UserAccount> findByUsername(Connection connection, String username) throws Exception {
        String sql = """
            SELECT id, nhan_vien_id, ten_dang_nhap, mat_khau_hash, vai_tro, trang_thai,
                   so_lan_dang_nhap_sai, khoa_den, bat_buoc_doi_mat_khau
            FROM tai_khoan
            WHERE LOWER(ten_dang_nhap) = LOWER(?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                Timestamp lockedUntil = result.getTimestamp("khoa_den");
                return Optional.of(new UserAccount(
                    result.getLong("id"),
                    result.getLong("nhan_vien_id"),
                    result.getString("ten_dang_nhap"),
                    result.getString("mat_khau_hash"),
                    Role.fromDatabase(result.getString("vai_tro")),
                    AccountStatus.valueOf(result.getString("trang_thai")),
                    result.getInt("so_lan_dang_nhap_sai"),
                    lockedUntil == null ? null : lockedUntil.toLocalDateTime(),
                    result.getBoolean("bat_buoc_doi_mat_khau")
                ));
            }
        }
    }

    @Override
    public Set<Permission> findPermissions(Connection connection, long accountId) throws Exception {
        String sql = """
            SELECT q.ma_quyen
            FROM tai_khoan tk
            JOIN vai_tro_quyen vtq ON vtq.vai_tro = tk.vai_tro
            JOIN quyen q ON q.ma_quyen = vtq.ma_quyen
            WHERE tk.id = ?
            """;
        Set<Permission> permissions = EnumSet.noneOf(Permission.class);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, accountId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    permissions.add(Permission.valueOf(result.getString("ma_quyen")));
                }
            }
        }
        return permissions;
    }

    @Override
    public void recordFailedLogin(
        Connection connection,
        long accountId,
        int failedCount,
        LocalDateTime lockedUntil
    ) throws Exception {
        String sql = "UPDATE tai_khoan SET so_lan_dang_nhap_sai = ?, khoa_den = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, failedCount);
            statement.setTimestamp(2, lockedUntil == null ? null : Timestamp.valueOf(lockedUntil));
            statement.setLong(3, accountId);
            statement.executeUpdate();
        }
    }

    @Override
    public void recordSuccessfulLogin(Connection connection, long accountId) throws Exception {
        String sql = """
            UPDATE tai_khoan
            SET so_lan_dang_nhap_sai = 0, khoa_den = NULL, dang_nhap_cuoi = SYSUTCDATETIME()
            WHERE id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, accountId);
            statement.executeUpdate();
        }
    }
}
