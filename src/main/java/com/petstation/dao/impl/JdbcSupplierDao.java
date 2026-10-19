package com.petstation.dao.impl;

import com.petstation.config.DataAccessException;
import com.petstation.dao.interfaces.SupplierDao;
import com.petstation.model.Supplier;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of SupplierDao
 */
public class JdbcSupplierDao implements SupplierDao {
    
    private final Connection connection;
    
    public JdbcSupplierDao(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Supplier create(Supplier supplier) {
        String sql = "INSERT INTO nha_cung_cap (ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, " +
                    "email, dia_chi, ma_so_thue, hoat_dong, ghi_chu, ngay_tao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, supplier.getSupplierCode());
            stmt.setString(2, supplier.getSupplierName());
            stmt.setString(3, supplier.getContactPerson());
            stmt.setString(4, supplier.getPhoneNumber());
            stmt.setString(5, supplier.getEmail());
            stmt.setString(6, supplier.getAddress());
            stmt.setString(7, supplier.getTaxCode());
            stmt.setBoolean(8, supplier.getIsActive() != null ? supplier.getIsActive() : true);
            stmt.setString(9, supplier.getNotes());
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    supplier.setId(generatedKeys.getLong(1));
                }
            }
            
            return supplier;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating supplier: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Supplier> findById(Long id) {
        String sql = "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi, " +
                    "ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version FROM nha_cung_cap WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding supplier: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Supplier> findBySupplierCode(String supplierCode) {
        String sql = "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi, " +
                    "ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version FROM nha_cung_cap WHERE ma_ncc = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplierCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding supplier by code: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Supplier> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi, " +
                    "ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version FROM nha_cung_cap WHERE so_dien_thoai = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, phoneNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding supplier by phone: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Supplier> searchByName(String name) {
        String sql = "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi, " +
                    "ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version " +
                    "FROM nha_cung_cap WHERE ten_ncc LIKE ? ORDER BY ten_ncc";
        
        List<Supplier> suppliers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error searching suppliers: " + e.getMessage(), e);
        }
        
        return suppliers;
    }
    
    @Override
    public List<Supplier> findAllActive() {
        String sql = "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi, " +
                    "ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version " +
                    "FROM nha_cung_cap WHERE hoat_dong = 1 ORDER BY ten_ncc";
        
        List<Supplier> suppliers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active suppliers: " + e.getMessage(), e);
        }
        
        return suppliers;
    }
    
    @Override
    public List<Supplier> findAll() {
        String sql = "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi, " +
                    "ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version " +
                    "FROM nha_cung_cap ORDER BY ngay_tao DESC";
        
        List<Supplier> suppliers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all suppliers: " + e.getMessage(), e);
        }
        
        return suppliers;
    }
    
    @Override
    public boolean update(Supplier supplier) {
        String sql = "UPDATE nha_cung_cap SET ten_ncc = ?, nguoi_lien_he = ?, so_dien_thoai = ?, " +
                    "email = ?, dia_chi = ?, ma_so_thue = ?, hoat_dong = ?, ghi_chu = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getPhoneNumber());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getTaxCode());
            stmt.setBoolean(7, supplier.getIsActive());
            stmt.setString(8, supplier.getNotes());
            stmt.setLong(9, supplier.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating supplier: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateActiveStatus(Long supplierId, Boolean isActive) {
        String sql = "UPDATE nha_cung_cap SET hoat_dong = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setLong(2, supplierId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating supplier status: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM nha_cung_cap WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting supplier: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsBySupplierCode(String supplierCode) {
        String sql = "SELECT COUNT(*) FROM nha_cung_cap WHERE ma_ncc = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplierCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking supplier existence: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM nha_cung_cap";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting suppliers: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setId(rs.getLong("id"));
        supplier.setSupplierCode(rs.getString("ma_ncc"));
        supplier.setSupplierName(rs.getString("ten_ncc"));
        supplier.setContactPerson(rs.getString("nguoi_lien_he"));
        supplier.setPhoneNumber(rs.getString("so_dien_thoai"));
        supplier.setEmail(rs.getString("email"));
        supplier.setAddress(rs.getString("dia_chi"));
        supplier.setTaxCode(rs.getString("ma_so_thue"));
        supplier.setIsActive(rs.getBoolean("hoat_dong"));
        supplier.setNotes(rs.getString("ghi_chu"));
        
        Timestamp createdAt = rs.getTimestamp("ngay_tao");
        if (createdAt != null) {
            supplier.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        supplier.setRowVersion(rs.getBytes("row_version"));
        
        return supplier;
    }
}
