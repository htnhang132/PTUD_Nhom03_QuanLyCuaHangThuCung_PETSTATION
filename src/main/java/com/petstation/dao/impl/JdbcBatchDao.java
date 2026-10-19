package com.petstation.dao.impl;

import com.petstation.dao.BatchDao;
import com.petstation.dao.DataAccessException;
import com.petstation.model.Batch;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of BatchDao */
public class JdbcBatchDao implements BatchDao {

    private final Connection connection;

    public JdbcBatchDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Batch create(Batch batch) {
        String sql =
                "INSERT INTO lo_hang (ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, "
                        + "so_luong, gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, batch.getBatchCode());
            stmt.setLong(2, batch.getProductId());
            stmt.setDate(
                    3,
                    batch.getManufacturingDate() != null
                            ? Date.valueOf(batch.getManufacturingDate())
                            : null);
            stmt.setDate(
                    4, batch.getExpiryDate() != null ? Date.valueOf(batch.getExpiryDate()) : null);
            stmt.setInt(5, batch.getQuantity());
            stmt.setBigDecimal(6, batch.getCostPrice());
            stmt.setObject(7, batch.getSupplierId());
            stmt.setString(8, batch.getStatus() != null ? batch.getStatus() : "IN_STOCK");
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    batch.setId(generatedKeys.getLong(1));
                }
            }

            return batch;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating batch: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Batch> findById(Long id) {
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBatch(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding batch: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Batch> findByBatchCode(String batchCode) {
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang WHERE ma_lo = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, batchCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBatch(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding batch by code: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<Batch> findByProductId(Long productId) {
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang WHERE san_pham_id = ? ORDER BY han_su_dung";

        List<Batch> batches = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    batches.add(mapResultSetToBatch(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding batches by product: " + e.getMessage(), e);
        }

        return batches;
    }

    @Override
    public List<Batch> findBySupplierId(Long supplierId) {
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang WHERE nha_cung_cap_id = ? ORDER BY ngay_nhap DESC";

        List<Batch> batches = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    batches.add(mapResultSetToBatch(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding batches by supplier: " + e.getMessage(), e);
        }

        return batches;
    }

    @Override
    public List<Batch> findByStatus(String status) {
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang WHERE trang_thai = ? ORDER BY ngay_nhap DESC";

        List<Batch> batches = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    batches.add(mapResultSetToBatch(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding batches by status: " + e.getMessage(), e);
        }

        return batches;
    }

    @Override
    public List<Batch> findExpiringBefore(LocalDate date) {
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, gia_nhap,"
                        + " nha_cung_cap_id, trang_thai, ngay_nhap, row_version FROM lo_hang WHERE"
                        + " han_su_dung < ? AND trang_thai = 'IN_STOCK' ORDER BY han_su_dung";

        List<Batch> batches = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    batches.add(mapResultSetToBatch(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding expiring batches: " + e.getMessage(), e);
        }

        return batches;
    }

    @Override
    public List<Batch> findExpiredBatches() {
        return findExpiringBefore(LocalDate.now());
    }

    @Override
    public List<Batch> findLowStockBatches(Integer threshold) {
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, gia_nhap,"
                        + " nha_cung_cap_id, trang_thai, ngay_nhap, row_version FROM lo_hang WHERE"
                        + " so_luong <= ? AND trang_thai = 'IN_STOCK' ORDER BY so_luong";

        List<Batch> batches = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, threshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    batches.add(mapResultSetToBatch(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding low stock batches: " + e.getMessage(), e);
        }

        return batches;
    }

    @Override
    public List<Batch> findAll() {
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang ORDER BY ngay_nhap DESC";

        List<Batch> batches = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                batches.add(mapResultSetToBatch(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all batches: " + e.getMessage(), e);
        }

        return batches;
    }

    @Override
    public boolean update(Batch batch) {
        String sql = "UPDATE lo_hang SET so_luong = ?, trang_thai = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, batch.getQuantity());
            stmt.setString(2, batch.getStatus());
            stmt.setLong(3, batch.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating batch: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateQuantity(Long batchId, Integer quantity) {
        String sql = "UPDATE lo_hang SET so_luong = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setLong(2, batchId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating batch quantity: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Long batchId, String status) {
        String sql = "UPDATE lo_hang SET trang_thai = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, batchId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating batch status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM lo_hang WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting batch: " + e.getMessage(), e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM lo_hang";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting batches: " + e.getMessage(), e);
        }

        return 0;
    }

    private Batch mapResultSetToBatch(ResultSet rs) throws SQLException {
        Batch batch = new Batch();
        batch.setId(rs.getLong("id"));
        batch.setBatchCode(rs.getString("ma_lo"));
        batch.setProductId(rs.getLong("san_pham_id"));

        Date mfgDate = rs.getDate("ngay_san_xuat");
        if (mfgDate != null) {
            batch.setManufacturingDate(mfgDate.toLocalDate());
        }

        Date expDate = rs.getDate("han_su_dung");
        if (expDate != null) {
            batch.setExpiryDate(expDate.toLocalDate());
        }

        batch.setQuantity(rs.getInt("so_luong"));
        batch.setCostPrice(rs.getBigDecimal("gia_nhap"));

        long supplierId = rs.getLong("nha_cung_cap_id");
        if (!rs.wasNull()) {
            batch.setSupplierId(supplierId);
        }

        batch.setStatus(rs.getString("trang_thai"));

        Timestamp createdAt = rs.getTimestamp("ngay_nhap");
        if (createdAt != null) {
            batch.setCreatedAt(createdAt.toLocalDateTime());
        }

        batch.setRowVersion(rs.getBytes("row_version"));

        return batch;
    }
}
