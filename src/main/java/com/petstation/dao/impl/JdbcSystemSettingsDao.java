package com.petstation.dao.impl;

import com.petstation.config.DataAccessException;
import com.petstation.dao.interfaces.SystemSettingsDao;
import com.petstation.model.SystemSettings;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of SystemSettingsDao
 */
public class JdbcSystemSettingsDao implements SystemSettingsDao {
    
    private final Connection connection;
    
    public JdbcSystemSettingsDao(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public SystemSettings create(SystemSettings settings) {
        String sql = "INSERT INTO cai_dat (khoa, gia_tri, kieu_du_lieu, nhom, mo_ta, " +
                    "nguoi_cap_nhat_id, ngay_cap_nhat) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, settings.getKey());
            stmt.setString(2, settings.getValue());
            stmt.setString(3, settings.getDataType() != null ? settings.getDataType() : "STRING");
            stmt.setString(4, settings.getGroup());
            stmt.setString(5, settings.getDescription());
            stmt.setObject(6, settings.getLastUpdatedBy());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    settings.setId(generatedKeys.getLong(1));
                }
            }
            
            return settings;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating system setting: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<SystemSettings> findById(Long id) {
        String sql = "SELECT cai_dat_id, khoa, gia_tri, kieu_du_lieu, nhom, mo_ta, " +
                    "nguoi_cap_nhat_id, ngay_cap_nhat, rowversion FROM cai_dat WHERE cai_dat_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSettings(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding setting: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<SystemSettings> findByKey(String key) {
        String sql = "SELECT cai_dat_id, khoa, gia_tri, kieu_du_lieu, nhom, mo_ta, " +
                    "nguoi_cap_nhat_id, ngay_cap_nhat, rowversion FROM cai_dat WHERE khoa = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSettings(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding setting by key: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public String getValue(String key) {
        String sql = "SELECT gia_tri FROM cai_dat WHERE khoa = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("gia_tri");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting setting value: " + e.getMessage(), e);
        }
        
        return null;
    }
    
    @Override
    public String getValueOrDefault(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }
    
    @Override
    public List<SystemSettings> findByGroup(String group) {
        String sql = "SELECT cai_dat_id, khoa, gia_tri, kieu_du_lieu, nhom, mo_ta, " +
                    "nguoi_cap_nhat_id, ngay_cap_nhat, rowversion FROM cai_dat WHERE nhom = ? ORDER BY khoa";
        
        List<SystemSettings> settings = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, group);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    settings.add(mapResultSetToSettings(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding settings by group: " + e.getMessage(), e);
        }
        
        return settings;
    }
    
    @Override
    public List<SystemSettings> findAll() {
        String sql = "SELECT cai_dat_id, khoa, gia_tri, kieu_du_lieu, nhom, mo_ta, " +
                    "nguoi_cap_nhat_id, ngay_cap_nhat, rowversion FROM cai_dat ORDER BY nhom, khoa";
        
        List<SystemSettings> settings = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                settings.add(mapResultSetToSettings(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all settings: " + e.getMessage(), e);
        }
        
        return settings;
    }
    
    @Override
    public boolean update(SystemSettings settings) {
        String sql = "UPDATE cai_dat SET gia_tri = ?, mo_ta = ?, nguoi_cap_nhat_id = ?, " +
                    "ngay_cap_nhat = ? WHERE cai_dat_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, settings.getValue());
            stmt.setString(2, settings.getDescription());
            stmt.setObject(3, settings.getLastUpdatedBy());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(5, settings.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating setting: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateValue(String key, String value, Long updatedBy) {
        String sql = "UPDATE cai_dat SET gia_tri = ?, nguoi_cap_nhat_id = ?, ngay_cap_nhat = ? WHERE khoa = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setObject(2, updatedBy);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(4, key);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating setting value: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM cai_dat WHERE cai_dat_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting setting: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteByKey(String key) {
        String sql = "DELETE FROM cai_dat WHERE khoa = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, key);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting setting by key: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsByKey(String key) {
        String sql = "SELECT COUNT(*) FROM cai_dat WHERE khoa = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking setting existence: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM cai_dat";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting settings: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    private SystemSettings mapResultSetToSettings(ResultSet rs) throws SQLException {
        SystemSettings settings = new SystemSettings();
        settings.setId(rs.getLong("cai_dat_id"));
        settings.setKey(rs.getString("khoa"));
        settings.setValue(rs.getString("gia_tri"));
        settings.setDataType(rs.getString("kieu_du_lieu"));
        settings.setGroup(rs.getString("nhom"));
        settings.setDescription(rs.getString("mo_ta"));
        
        long updatedBy = rs.getLong("nguoi_cap_nhat_id");
        if (!rs.wasNull()) {
            settings.setLastUpdatedBy(updatedBy);
        }
        
        Timestamp updatedAt = rs.getTimestamp("ngay_cap_nhat");
        if (updatedAt != null) {
            settings.setLastUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        settings.setRowVersion(rs.getBytes("rowversion"));
        
        return settings;
    }
}
