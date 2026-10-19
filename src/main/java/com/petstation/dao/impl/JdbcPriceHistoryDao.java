package com.petstation.dao.impl;

import com.petstation.config.DataAccessException;
import com.petstation.dao.interfaces.PriceHistoryDao;
import com.petstation.model.PriceHistory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of PriceHistoryDao
 */
public class JdbcPriceHistoryDao implements PriceHistoryDao {
    
    private final Connection connection;
    
    public JdbcPriceHistoryDao(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public PriceHistory create(PriceHistory priceHistory) {
        String sql = "INSERT INTO lich_su_gia (san_pham_id, gia_cu, gia_moi, ly_do, " +
                    "nguoi_thay_doi_id, thoi_diem_thay_doi, loai_thay_doi) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, priceHistory.getProductId());
            stmt.setBigDecimal(2, priceHistory.getOldPrice());
            stmt.setBigDecimal(3, priceHistory.getNewPrice());
            stmt.setString(4, priceHistory.getReason());
            stmt.setLong(5, priceHistory.getChangedBy());
            stmt.setTimestamp(6, Timestamp.valueOf(priceHistory.getChangedAt() != null ? 
                priceHistory.getChangedAt() : LocalDateTime.now()));
            stmt.setString(7, priceHistory.getChangeType() != null ? priceHistory.getChangeType() : "MANUAL");
            
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    priceHistory.setId(generatedKeys.getLong(1));
                }
            }
            
            return priceHistory;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating price history: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<PriceHistory> findById(Long id) {
        String sql = "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id, " +
                    "thoi_diem_thay_doi, loai_thay_doi, rowversion FROM lich_su_gia WHERE lich_su_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPriceHistory(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding price history: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<PriceHistory> findByProductId(Long productId) {
        String sql = "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id, " +
                    "thoi_diem_thay_doi, loai_thay_doi, rowversion " +
                    "FROM lich_su_gia WHERE san_pham_id = ? ORDER BY thoi_diem_thay_doi DESC";
        
        List<PriceHistory> histories = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapResultSetToPriceHistory(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding price history by product: " + e.getMessage(), e);
        }
        
        return histories;
    }
    
    @Override
    public List<PriceHistory> findByChangedBy(Long userId) {
        String sql = "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id, " +
                    "thoi_diem_thay_doi, loai_thay_doi, rowversion " +
                    "FROM lich_su_gia WHERE nguoi_thay_doi_id = ? ORDER BY thoi_diem_thay_doi DESC";
        
        List<PriceHistory> histories = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapResultSetToPriceHistory(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding price history by user: " + e.getMessage(), e);
        }
        
        return histories;
    }
    
    @Override
    public List<PriceHistory> findByChangeType(String changeType) {
        String sql = "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id, " +
                    "thoi_diem_thay_doi, loai_thay_doi, rowversion " +
                    "FROM lich_su_gia WHERE loai_thay_doi = ? ORDER BY thoi_diem_thay_doi DESC";
        
        List<PriceHistory> histories = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, changeType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapResultSetToPriceHistory(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding price history by type: " + e.getMessage(), e);
        }
        
        return histories;
    }
    
    @Override
    public List<PriceHistory> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id, " +
                    "thoi_diem_thay_doi, loai_thay_doi, rowversion " +
                    "FROM lich_su_gia WHERE thoi_diem_thay_doi BETWEEN ? AND ? ORDER BY thoi_diem_thay_doi DESC";
        
        List<PriceHistory> histories = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapResultSetToPriceHistory(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding price history by date range: " + e.getMessage(), e);
        }
        
        return histories;
    }
    
    @Override
    public Optional<PriceHistory> findLatestByProductId(Long productId) {
        String sql = "SELECT TOP 1 lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id, " +
                    "thoi_diem_thay_doi, loai_thay_doi, rowversion " +
                    "FROM lich_su_gia WHERE san_pham_id = ? ORDER BY thoi_diem_thay_doi DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPriceHistory(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding latest price history: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<PriceHistory> findAll() {
        String sql = "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id, " +
                    "thoi_diem_thay_doi, loai_thay_doi, rowversion " +
                    "FROM lich_su_gia ORDER BY thoi_diem_thay_doi DESC";
        
        List<PriceHistory> histories = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                histories.add(mapResultSetToPriceHistory(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all price history: " + e.getMessage(), e);
        }
        
        return histories;
    }
    
    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM lich_su_gia WHERE lich_su_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting price history: " + e.getMessage(), e);
        }
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM lich_su_gia";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting price history: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    private PriceHistory mapResultSetToPriceHistory(ResultSet rs) throws SQLException {
        PriceHistory history = new PriceHistory();
        history.setId(rs.getLong("lich_su_id"));
        history.setProductId(rs.getLong("san_pham_id"));
        history.setOldPrice(rs.getBigDecimal("gia_cu"));
        history.setNewPrice(rs.getBigDecimal("gia_moi"));
        history.setReason(rs.getString("ly_do"));
        history.setChangedBy(rs.getLong("nguoi_thay_doi_id"));
        
        Timestamp changedAt = rs.getTimestamp("thoi_diem_thay_doi");
        if (changedAt != null) {
            history.setChangedAt(changedAt.toLocalDateTime());
        }
        
        history.setChangeType(rs.getString("loai_thay_doi"));
        history.setRowVersion(rs.getBytes("rowversion"));
        
        return history;
    }
}
