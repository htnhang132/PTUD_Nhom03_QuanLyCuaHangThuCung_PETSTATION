package com.petstation.dao.impl;

import com.petstation.config.DataAccessException;
import com.petstation.dao.interfaces.SpaPriceMatrixDao;
import com.petstation.model.SpaPriceMatrix;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of SpaPriceMatrixDao
 */
public class JdbcSpaPriceMatrixDao implements SpaPriceMatrixDao {
    
    private final Connection connection;
    
    public JdbcSpaPriceMatrixDao(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public SpaPriceMatrix create(SpaPriceMatrix priceMatrix) {
        String sql = "INSERT INTO bang_gia_spa (goi_id, loai_thu_cung, can_nang_tu, can_nang_den, " +
                    "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, priceMatrix.getPackageId());
            stmt.setString(2, priceMatrix.getSpecies());
            stmt.setBigDecimal(3, priceMatrix.getWeightFrom());
            stmt.setBigDecimal(4, priceMatrix.getWeightTo());
            stmt.setBigDecimal(5, priceMatrix.getPrice());
            stmt.setObject(6, priceMatrix.getEstimatedDuration());
            stmt.setTimestamp(7, Timestamp.valueOf(priceMatrix.getEffectiveDate() != null ? 
                priceMatrix.getEffectiveDate() : LocalDateTime.now()));
            stmt.setTimestamp(8, priceMatrix.getExpiryDate() != null ? 
                Timestamp.valueOf(priceMatrix.getExpiryDate()) : null);
            
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    priceMatrix.setId(generatedKeys.getLong(1));
                }
            }
            
            return priceMatrix;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating spa price matrix: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<SpaPriceMatrix> findById(Long id) {
        String sql = "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, " +
                    "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion " +
                    "FROM bang_gia_spa WHERE gia_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSpaPriceMatrix(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding spa price matrix: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<SpaPriceMatrix> findByPackageId(Long packageId) {
        String sql = "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, " +
                    "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion " +
                    "FROM bang_gia_spa WHERE goi_id = ? ORDER BY loai_thu_cung, can_nang_tu";
        
        List<SpaPriceMatrix> matrices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, packageId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matrices.add(mapResultSetToSpaPriceMatrix(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding price matrices: " + e.getMessage(), e);
        }
        
        return matrices;
    }
    
    @Override
    public Optional<SpaPriceMatrix> findByPackageSpeciesAndWeight(Long packageId, String species, BigDecimal weight) {
        String sql = "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, " +
                    "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion " +
                    "FROM bang_gia_spa WHERE goi_id = ? AND loai_thu_cung = ? " +
                    "AND can_nang_tu <= ? AND can_nang_den > ? " +
                    "AND (ngay_het_hieu_luc IS NULL OR ngay_het_hieu_luc > GETDATE())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, packageId);
            stmt.setString(2, species);
            stmt.setBigDecimal(3, weight);
            stmt.setBigDecimal(4, weight);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSpaPriceMatrix(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding price by weight: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<SpaPriceMatrix> findAllActive() {
        String sql = "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, " +
                    "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion " +
                    "FROM bang_gia_spa WHERE ngay_het_hieu_luc IS NULL OR ngay_het_hieu_luc > GETDATE() " +
                    "ORDER BY goi_id, loai_thu_cung, can_nang_tu";
        
        List<SpaPriceMatrix> matrices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                matrices.add(mapResultSetToSpaPriceMatrix(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active price matrices: " + e.getMessage(), e);
        }
        
        return matrices;
    }
    
    @Override
    public List<SpaPriceMatrix> findBySpecies(String species) {
        String sql = "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, " +
                    "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion " +
                    "FROM bang_gia_spa WHERE loai_thu_cung = ? ORDER BY goi_id, can_nang_tu";
        
        List<SpaPriceMatrix> matrices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, species);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matrices.add(mapResultSetToSpaPriceMatrix(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding price matrices by species: " + e.getMessage(), e);
        }
        
        return matrices;
    }
    
    @Override
    public List<SpaPriceMatrix> findAll() {
        String sql = "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, " +
                    "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion " +
                    "FROM bang_gia_spa ORDER BY goi_id, loai_thu_cung, can_nang_tu";
        
        List<SpaPriceMatrix> matrices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                matrices.add(mapResultSetToSpaPriceMatrix(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all price matrices: " + e.getMessage(), e);
        }
        
        return matrices;
    }
    
    @Override
    public boolean update(SpaPriceMatrix priceMatrix) {
        String sql = "UPDATE bang_gia_spa SET don_gia = ?, thoi_gian_du_kien = ?, " +
                    "ngay_het_hieu_luc = ? WHERE gia_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, priceMatrix.getPrice());
            stmt.setObject(2, priceMatrix.getEstimatedDuration());
            stmt.setTimestamp(3, priceMatrix.getExpiryDate() != null ? 
                Timestamp.valueOf(priceMatrix.getExpiryDate()) : null);
            stmt.setLong(4, priceMatrix.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating spa price matrix: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updatePrice(Long priceMatrixId, BigDecimal newPrice) {
        String sql = "UPDATE bang_gia_spa SET don_gia = ? WHERE gia_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newPrice);
            stmt.setLong(2, priceMatrixId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating price: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM bang_gia_spa WHERE gia_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting spa price matrix: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int deleteByPackageId(Long packageId) {
        String sql = "DELETE FROM bang_gia_spa WHERE goi_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, packageId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting price matrices: " + e.getMessage(), e);
        }
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM bang_gia_spa";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting price matrices: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    private SpaPriceMatrix mapResultSetToSpaPriceMatrix(ResultSet rs) throws SQLException {
        SpaPriceMatrix matrix = new SpaPriceMatrix();
        matrix.setId(rs.getLong("gia_id"));
        matrix.setPackageId(rs.getLong("goi_id"));
        matrix.setSpecies(rs.getString("loai_thu_cung"));
        matrix.setWeightFrom(rs.getBigDecimal("can_nang_tu"));
        matrix.setWeightTo(rs.getBigDecimal("can_nang_den"));
        matrix.setPrice(rs.getBigDecimal("don_gia"));
        matrix.setEstimatedDuration((Integer) rs.getObject("thoi_gian_du_kien"));
        
        Timestamp effectiveDate = rs.getTimestamp("ngay_ap_dung");
        if (effectiveDate != null) {
            matrix.setEffectiveDate(effectiveDate.toLocalDateTime());
        }
        
        Timestamp expiryDate = rs.getTimestamp("ngay_het_hieu_luc");
        if (expiryDate != null) {
            matrix.setExpiryDate(expiryDate.toLocalDateTime());
        }
        
        matrix.setRowVersion(rs.getBytes("rowversion"));
        
        return matrix;
    }
}
