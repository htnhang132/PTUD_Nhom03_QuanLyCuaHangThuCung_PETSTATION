package com.petstation.dao.impl;

import com.petstation.dao.DataAccessException;
import com.petstation.dao.DayLockDao;
import com.petstation.model.DayLock;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of DayLockDao */
public class JdbcDayLockDao implements DayLockDao {

    private final Connection connection;

    public JdbcDayLockDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public DayLock create(DayLock dayLock) {
        String sql =
                "INSERT INTO chot_ngay (ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(dayLock.getLockDate()));
            stmt.setLong(2, dayLock.getLockedBy());
            stmt.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            dayLock.getLockedAt() != null
                                    ? dayLock.getLockedAt()
                                    : LocalDateTime.now()));
            stmt.setBigDecimal(4, dayLock.getTotalRevenue());
            stmt.setBigDecimal(5, dayLock.getTotalCash());
            stmt.setBigDecimal(6, dayLock.getTotalBankTransfer());
            stmt.setBigDecimal(7, dayLock.getTotalCard());
            stmt.setInt(8, dayLock.getInvoiceCount());
            stmt.setString(9, dayLock.getNotes());
            stmt.setString(10, dayLock.getStatus() != null ? dayLock.getStatus() : "LOCKED");

            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    dayLock.setId(generatedKeys.getLong(1));
                }
            }

            return dayLock;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating day lock: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<DayLock> findById(Long id) {
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai, rowversion FROM chot_ngay WHERE chot_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDayLock(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding day lock: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<DayLock> findByLockDate(LocalDate lockDate) {
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai, rowversion FROM chot_ngay WHERE ngay_chot = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(lockDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDayLock(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding day lock by date: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<DayLock> findByLockedBy(Long userId) {
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai, rowversion FROM chot_ngay WHERE nguoi_chot_id = ? ORDER BY"
                        + " ngay_chot DESC";

        List<DayLock> dayLocks = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dayLocks.add(mapResultSetToDayLock(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding day locks by user: " + e.getMessage(), e);
        }

        return dayLocks;
    }

    @Override
    public List<DayLock> findByStatus(String status) {
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai, rowversion FROM chot_ngay WHERE trang_thai = ? ORDER BY"
                        + " ngay_chot DESC";

        List<DayLock> dayLocks = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dayLocks.add(mapResultSetToDayLock(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding day locks by status: " + e.getMessage(), e);
        }

        return dayLocks;
    }

    @Override
    public List<DayLock> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                    + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                    + " trang_thai, rowversion FROM chot_ngay WHERE ngay_chot BETWEEN ? AND ? ORDER"
                    + " BY ngay_chot";

        List<DayLock> dayLocks = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dayLocks.add(mapResultSetToDayLock(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding day locks by date range: " + e.getMessage(), e);
        }

        return dayLocks;
    }

    @Override
    public List<DayLock> findLockedDays() {
        return findByStatus("LOCKED");
    }

    @Override
    public List<DayLock> findAll() {
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai, rowversion FROM chot_ngay ORDER BY ngay_chot DESC";

        List<DayLock> dayLocks = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dayLocks.add(mapResultSetToDayLock(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all day locks: " + e.getMessage(), e);
        }

        return dayLocks;
    }

    @Override
    public boolean update(DayLock dayLock) {
        String sql =
                "UPDATE chot_ngay SET tong_doanh_thu = ?, tong_tien_mat = ?, tong_chuyen_khoan = ?,"
                    + " tong_the = ?, so_hoa_don = ?, ghi_chu = ?, trang_thai = ? WHERE chot_id ="
                    + " ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, dayLock.getTotalRevenue());
            stmt.setBigDecimal(2, dayLock.getTotalCash());
            stmt.setBigDecimal(3, dayLock.getTotalBankTransfer());
            stmt.setBigDecimal(4, dayLock.getTotalCard());
            stmt.setInt(5, dayLock.getInvoiceCount());
            stmt.setString(6, dayLock.getNotes());
            stmt.setString(7, dayLock.getStatus());
            stmt.setLong(8, dayLock.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating day lock: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Long dayLockId, String status) {
        String sql = "UPDATE chot_ngay SET trang_thai = ? WHERE chot_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, dayLockId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating day lock status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM chot_ngay WHERE chot_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting day lock: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isDayLocked(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM chot_ngay WHERE ngay_chot = ? AND trang_thai = 'LOCKED'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if day is locked: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM chot_ngay";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting day locks: " + e.getMessage(), e);
        }

        return 0;
    }

    private DayLock mapResultSetToDayLock(ResultSet rs) throws SQLException {
        DayLock dayLock = new DayLock();
        dayLock.setId(rs.getLong("chot_id"));

        Date lockDate = rs.getDate("ngay_chot");
        if (lockDate != null) {
            dayLock.setLockDate(lockDate.toLocalDate());
        }

        dayLock.setLockedBy(rs.getLong("nguoi_chot_id"));

        Timestamp lockedAt = rs.getTimestamp("thoi_diem_chot");
        if (lockedAt != null) {
            dayLock.setLockedAt(lockedAt.toLocalDateTime());
        }

        dayLock.setTotalRevenue(rs.getBigDecimal("tong_doanh_thu"));
        dayLock.setTotalCash(rs.getBigDecimal("tong_tien_mat"));
        dayLock.setTotalBankTransfer(rs.getBigDecimal("tong_chuyen_khoan"));
        dayLock.setTotalCard(rs.getBigDecimal("tong_the"));
        dayLock.setInvoiceCount(rs.getInt("so_hoa_don"));
        dayLock.setNotes(rs.getString("ghi_chu"));
        dayLock.setStatus(rs.getString("trang_thai"));
        dayLock.setRowVersion(rs.getBytes("rowversion"));

        return dayLock;
    }
}
