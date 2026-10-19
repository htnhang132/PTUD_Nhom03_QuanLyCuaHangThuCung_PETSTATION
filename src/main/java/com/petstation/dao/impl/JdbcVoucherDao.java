package com.petstation.dao.impl;

import com.petstation.config.DataAccessException;
import com.petstation.dao.interfaces.VoucherDao;
import com.petstation.model.Voucher;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of VoucherDao
 */
public class JdbcVoucherDao implements VoucherDao {
    
    private final Connection connection;
    
    public JdbcVoucherDao(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Voucher create(Voucher voucher) {
        String sql = "INSERT INTO voucher (ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam, " +
                    "don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den, gioi_han_su_dung, " +
                    "da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem, ngay_tao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, voucher.getVoucherCode());
            stmt.setObject(2, voucher.getCustomerId());
            stmt.setString(3, voucher.getVoucherType());
            stmt.setBigDecimal(4, voucher.getDiscountValue());
            stmt.setString(5, voucher.getDiscountType());
            stmt.setBigDecimal(6, voucher.getMinOrderValue());
            stmt.setBigDecimal(7, voucher.getMaxDiscountAmount());
            stmt.setTimestamp(8, voucher.getValidFrom() != null ? Timestamp.valueOf(voucher.getValidFrom()) : null);
            stmt.setTimestamp(9, voucher.getValidUntil() != null ? Timestamp.valueOf(voucher.getValidUntil()) : null);
            stmt.setObject(10, voucher.getUsageLimit());
            stmt.setInt(11, voucher.getUsedCount() != null ? voucher.getUsedCount() : 0);
            stmt.setString(12, voucher.getStatus() != null ? voucher.getStatus() : "ACTIVE");
            stmt.setBoolean(13, voucher.getApplySpaOnly() != null ? voucher.getApplySpaOnly() : false);
            stmt.setTimestamp(14, voucher.getRedeemedDate() != null ? Timestamp.valueOf(voucher.getRedeemedDate()) : null);
            stmt.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now()));
            
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    voucher.setId(generatedKeys.getLong(1));
                }
            }
            
            return voucher;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating voucher: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Voucher> findById(Long id) {
        String sql = "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam, " +
                    "don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den, gioi_han_su_dung, " +
                    "da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem, ngay_tao, row_version " +
                    "FROM voucher WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVoucher(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding voucher: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Voucher> findByVoucherCode(String voucherCode) {
        String sql = "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam, " +
                    "don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den, gioi_han_su_dung, " +
                    "da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem, ngay_tao, row_version " +
                    "FROM voucher WHERE ma_voucher = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, voucherCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVoucher(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding voucher by code: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Voucher> findByCustomerId(Long customerId) {
        String sql = "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam, " +
                    "don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den, gioi_han_su_dung, " +
                    "da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem, ngay_tao, row_version " +
                    "FROM voucher WHERE khach_hang_id = ? ORDER BY ngay_tao DESC";
        
        List<Voucher> vouchers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vouchers.add(mapResultSetToVoucher(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding vouchers by customer: " + e.getMessage(), e);
        }
        
        return vouchers;
    }
    
    @Override
    public List<Voucher> findByVoucherType(String voucherType) {
        String sql = "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam, " +
                    "don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den, gioi_han_su_dung, " +
                    "da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem, ngay_tao, row_version " +
                    "FROM voucher WHERE loai_voucher = ? ORDER BY ngay_tao DESC";
        
        List<Voucher> vouchers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, voucherType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vouchers.add(mapResultSetToVoucher(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding vouchers by type: " + e.getMessage(), e);
        }
        
        return vouchers;
    }
    
    @Override
    public List<Voucher> findByStatus(String status) {
        String sql = "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam, " +
                    "don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den, gioi_han_su_dung, " +
                    "da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem, ngay_tao, row_version " +
                    "FROM voucher WHERE trang_thai = ? ORDER BY ngay_tao DESC";
        
        List<Voucher> vouchers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vouchers.add(mapResultSetToVoucher(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding vouchers by status: " + e.getMessage(), e);
        }
        
        return vouchers;
    }
    
    @Override
    public List<Voucher> findActiveVouchers() {
        String sql = "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam, " +
                    "don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den, gioi_han_su_dung, " +
                    "da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem, ngay_tao, row_version " +
                    "FROM voucher WHERE trang_thai = 'ACTIVE' " +
                    "AND hieu_luc_tu <= GETDATE() AND hieu_luc_den >= GETDATE() " +
                    "AND (gioi_han_su_dung IS NULL OR da_su_dung < gioi_han_su_dung) " +
                    "ORDER BY ngay_tao DESC";
        
        List<Voucher> vouchers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                vouchers.add(mapResultSetToVoucher(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active vouchers: " + e.getMessage(), e);
        }
        
        return vouchers;
    }
    
    @Override
    public List<Voucher> findExpiredVouchers() {
        String sql = "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam, " +
                    "don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den, gioi_han_su_dung, " +
                    "da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem, ngay_tao, row_version " +
                    "FROM voucher WHERE hieu_luc_den < GETDATE() ORDER BY hieu_luc_den DESC";
        
        List<Voucher> vouchers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                vouchers.add(mapResultSetToVoucher(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding expired vouchers: " + e.getMessage(), e);
        }
        
        return vouchers;
    }
    
    @Override
    public List<Voucher> findSpaOnlyVouchers() {
        String sql = "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam, " +
                    "don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den, gioi_han_su_dung, " +
                    "da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem, ngay_tao, row_version " +
                    "FROM voucher WHERE chi_ap_dung_spa = 1 ORDER BY ngay_tao DESC";
        
        List<Voucher> vouchers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                vouchers.add(mapResultSetToVoucher(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding spa-only vouchers: " + e.getMessage(), e);
        }
        
        return vouchers;
    }
    
    @Override
    public List<Voucher> findAll() {
        String sql = "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam, " +
                    "don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den, gioi_han_su_dung, " +
                    "da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem, ngay_tao, row_version " +
                    "FROM voucher ORDER BY ngay_tao DESC";
        
        List<Voucher> vouchers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                vouchers.add(mapResultSetToVoucher(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all vouchers: " + e.getMessage(), e);
        }
        
        return vouchers;
    }
    
    @Override
    public boolean update(Voucher voucher) {
        String sql = "UPDATE voucher SET trang_thai = ?, da_su_dung = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, voucher.getStatus());
            stmt.setInt(2, voucher.getUsedCount());
            stmt.setLong(3, voucher.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating voucher: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateStatus(Long voucherId, String status) {
        String sql = "UPDATE voucher SET trang_thai = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, voucherId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating voucher status: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean incrementUsedCount(Long voucherId) {
        String sql = "UPDATE voucher SET da_su_dung = da_su_dung + 1 WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, voucherId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error incrementing voucher usage: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM voucher WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting voucher: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsByVoucherCode(String voucherCode) {
        String sql = "SELECT COUNT(*) FROM voucher WHERE ma_voucher = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, voucherCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking voucher existence: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM voucher";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting vouchers: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    private Voucher mapResultSetToVoucher(ResultSet rs) throws SQLException {
        Voucher voucher = new Voucher();
        voucher.setId(rs.getLong("id"));
        voucher.setVoucherCode(rs.getString("ma_voucher"));
        
        long customerId = rs.getLong("khach_hang_id");
        if (!rs.wasNull()) {
            voucher.setCustomerId(customerId);
        }
        
        voucher.setVoucherType(rs.getString("loai_voucher"));
        voucher.setDiscountValue(rs.getBigDecimal("gia_tri_giam"));
        voucher.setDiscountType(rs.getString("loai_giam"));
        voucher.setMinOrderValue(rs.getBigDecimal("don_hang_toi_thieu"));
        voucher.setMaxDiscountAmount(rs.getBigDecimal("giam_toi_da"));
        
        Timestamp validFrom = rs.getTimestamp("hieu_luc_tu");
        if (validFrom != null) {
            voucher.setValidFrom(validFrom.toLocalDateTime());
        }
        
        Timestamp validUntil = rs.getTimestamp("hieu_luc_den");
        if (validUntil != null) {
            voucher.setValidUntil(validUntil.toLocalDateTime());
        }
        
        Integer usageLimit = (Integer) rs.getObject("gioi_han_su_dung");
        voucher.setUsageLimit(usageLimit);
        
        voucher.setUsedCount(rs.getInt("da_su_dung"));
        voucher.setStatus(rs.getString("trang_thai"));
        voucher.setApplySpaOnly(rs.getBoolean("chi_ap_dung_spa"));
        
        Timestamp redeemedDate = rs.getTimestamp("ngay_doi_diem");
        if (redeemedDate != null) {
            voucher.setRedeemedDate(redeemedDate.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("ngay_tao");
        if (createdAt != null) {
            voucher.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        voucher.setRowVersion(rs.getBytes("row_version"));
        
        return voucher;
    }
}
