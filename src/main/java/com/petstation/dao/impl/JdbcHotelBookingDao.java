package com.petstation.dao.impl;

import com.petstation.config.DataAccessException;
import com.petstation.dao.interfaces.HotelBookingDao;
import com.petstation.model.HotelBooking;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of HotelBookingDao
 */
public class JdbcHotelBookingDao implements HotelBookingDao {
    
    private final Connection connection;
    
    public JdbcHotelBookingDao(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public HotelBooking create(HotelBooking booking) {
        String sql = "INSERT INTO dat_phong_hotel (ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, " +
                    "gio_nhan_phong, gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, " +
                    "phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, booking.getBookingCode());
            stmt.setLong(2, booking.getPetId());
            stmt.setLong(3, booking.getCustomerId());
            stmt.setLong(4, booking.getRoomId());
            stmt.setTimestamp(5, booking.getCheckInTime() != null ? Timestamp.valueOf(booking.getCheckInTime()) : null);
            stmt.setTimestamp(6, booking.getExpectedCheckOutTime() != null ? Timestamp.valueOf(booking.getExpectedCheckOutTime()) : null);
            stmt.setTimestamp(7, booking.getActualCheckOutTime() != null ? Timestamp.valueOf(booking.getActualCheckOutTime()) : null);
            stmt.setString(8, booking.getTimeFrame());
            stmt.setBigDecimal(9, booking.getBasePrice());
            stmt.setBigDecimal(10, booking.getDepositAmount());
            stmt.setBigDecimal(11, booking.getLateFee());
            stmt.setBigDecimal(12, booking.getTotalAmount());
            stmt.setString(13, booking.getStatus() != null ? booking.getStatus() : "RESERVED");
            stmt.setString(14, booking.getNotes());
            stmt.setObject(15, booking.getEmployeeId());
            stmt.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));
            
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getLong(1));
                }
            }
            
            return booking;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating hotel booking: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<HotelBooking> findById(Long id) {
        String sql = "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong, " +
                    "gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, phi_tre_han, " +
                    "tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao, row_version " +
                    "FROM dat_phong_hotel WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHotelBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding hotel booking: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<HotelBooking> findByBookingCode(String bookingCode) {
        String sql = "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong, " +
                    "gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, phi_tre_han, " +
                    "tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao, row_version " +
                    "FROM dat_phong_hotel WHERE ma_dat_phong = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bookingCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHotelBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding hotel booking by code: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<HotelBooking> findByPetId(Long petId) {
        String sql = "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong, " +
                    "gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, phi_tre_han, " +
                    "tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao, row_version " +
                    "FROM dat_phong_hotel WHERE thu_cung_id = ? ORDER BY gio_nhan_phong DESC";
        
        return findBookings(sql, petId);
    }
    
    @Override
    public List<HotelBooking> findByCustomerId(Long customerId) {
        String sql = "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong, " +
                    "gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, phi_tre_han, " +
                    "tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao, row_version " +
                    "FROM dat_phong_hotel WHERE khach_hang_id = ? ORDER BY gio_nhan_phong DESC";
        
        return findBookings(sql, customerId);
    }
    
    @Override
    public List<HotelBooking> findByRoomId(Long roomId) {
        String sql = "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong, " +
                    "gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, phi_tre_han, " +
                    "tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao, row_version " +
                    "FROM dat_phong_hotel WHERE phong_id = ? ORDER BY gio_nhan_phong";
        
        return findBookings(sql, roomId);
    }
    
    @Override
    public List<HotelBooking> findByStatus(String status) {
        String sql = "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong, " +
                    "gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, phi_tre_han, " +
                    "tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao, row_version " +
                    "FROM dat_phong_hotel WHERE trang_thai = ? ORDER BY gio_nhan_phong";
        
        List<HotelBooking> bookings = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToHotelBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding bookings by status: " + e.getMessage(), e);
        }
        
        return bookings;
    }
    
    @Override
    public List<HotelBooking> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong, " +
                    "gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, phi_tre_han, " +
                    "tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao, row_version " +
                    "FROM dat_phong_hotel WHERE gio_nhan_phong BETWEEN ? AND ? ORDER BY gio_nhan_phong";
        
        List<HotelBooking> bookings = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToHotelBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding bookings by date range: " + e.getMessage(), e);
        }
        
        return bookings;
    }
    
    @Override
    public List<HotelBooking> findActiveBookings() {
        String sql = "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong, " +
                    "gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, phi_tre_han, " +
                    "tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao, row_version " +
                    "FROM dat_phong_hotel WHERE trang_thai IN ('RESERVED', 'CHECKED_IN') ORDER BY gio_nhan_phong";
        
        List<HotelBooking> bookings = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapResultSetToHotelBooking(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active bookings: " + e.getMessage(), e);
        }
        
        return bookings;
    }
    
    @Override
    public List<HotelBooking> findOverdueBookings() {
        String sql = "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong, " +
                    "gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, phi_tre_han, " +
                    "tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao, row_version " +
                    "FROM dat_phong_hotel WHERE trang_thai = 'CHECKED_IN' " +
                    "AND gio_tra_du_kien < GETDATE() ORDER BY gio_tra_du_kien";
        
        List<HotelBooking> bookings = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapResultSetToHotelBooking(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding overdue bookings: " + e.getMessage(), e);
        }
        
        return bookings;
    }
    
    @Override
    public List<HotelBooking> findCheckedInBookings() {
        return findByStatus("CHECKED_IN");
    }
    
    @Override
    public List<HotelBooking> findAll() {
        String sql = "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong, " +
                    "gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc, phi_tre_han, " +
                    "tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao, row_version " +
                    "FROM dat_phong_hotel ORDER BY ngay_tao DESC";
        
        List<HotelBooking> bookings = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapResultSetToHotelBooking(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all bookings: " + e.getMessage(), e);
        }
        
        return bookings;
    }
    
    @Override
    public boolean update(HotelBooking booking) {
        String sql = "UPDATE dat_phong_hotel SET gio_tra_thuc_te = ?, phi_tre_han = ?, " +
                    "tong_tien = ?, trang_thai = ?, ghi_chu = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, booking.getActualCheckOutTime() != null ? 
                Timestamp.valueOf(booking.getActualCheckOutTime()) : null);
            stmt.setBigDecimal(2, booking.getLateFee());
            stmt.setBigDecimal(3, booking.getTotalAmount());
            stmt.setString(4, booking.getStatus());
            stmt.setString(5, booking.getNotes());
            stmt.setLong(6, booking.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating hotel booking: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateStatus(Long bookingId, String status) {
        String sql = "UPDATE dat_phong_hotel SET trang_thai = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, bookingId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating booking status: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateActualCheckOutTime(Long bookingId, LocalDateTime checkOutTime) {
        String sql = "UPDATE dat_phong_hotel SET gio_tra_thuc_te = ?, trang_thai = 'CHECKED_OUT' WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(checkOutTime));
            stmt.setLong(2, bookingId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating check-out time: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM dat_phong_hotel WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting hotel booking: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT COUNT(*) FROM dat_phong_hotel " +
                    "WHERE phong_id = ? AND trang_thai IN ('RESERVED', 'CHECKED_IN') " +
                    "AND ((gio_nhan_phong BETWEEN ? AND ?) " +
                    "OR (gio_tra_du_kien BETWEEN ? AND ?) " +
                    "OR (gio_nhan_phong <= ? AND gio_tra_du_kien >= ?))";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, roomId);
            stmt.setTimestamp(2, Timestamp.valueOf(startTime));
            stmt.setTimestamp(3, Timestamp.valueOf(endTime));
            stmt.setTimestamp(4, Timestamp.valueOf(startTime));
            stmt.setTimestamp(5, Timestamp.valueOf(endTime));
            stmt.setTimestamp(6, Timestamp.valueOf(startTime));
            stmt.setTimestamp(7, Timestamp.valueOf(endTime));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking room availability: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM dat_phong_hotel";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting hotel bookings: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    private List<HotelBooking> findBookings(String sql, Long param) {
        List<HotelBooking> bookings = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToHotelBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding hotel bookings: " + e.getMessage(), e);
        }
        
        return bookings;
    }
    
    private HotelBooking mapResultSetToHotelBooking(ResultSet rs) throws SQLException {
        HotelBooking booking = new HotelBooking();
        booking.setId(rs.getLong("id"));
        booking.setBookingCode(rs.getString("ma_dat_phong"));
        booking.setPetId(rs.getLong("thu_cung_id"));
        booking.setCustomerId(rs.getLong("khach_hang_id"));
        booking.setRoomId(rs.getLong("phong_id"));
        
        Timestamp checkInTime = rs.getTimestamp("gio_nhan_phong");
        if (checkInTime != null) {
            booking.setCheckInTime(checkInTime.toLocalDateTime());
        }
        
        Timestamp expectedCheckOut = rs.getTimestamp("gio_tra_du_kien");
        if (expectedCheckOut != null) {
            booking.setExpectedCheckOutTime(expectedCheckOut.toLocalDateTime());
        }
        
        Timestamp actualCheckOut = rs.getTimestamp("gio_tra_thuc_te");
        if (actualCheckOut != null) {
            booking.setActualCheckOutTime(actualCheckOut.toLocalDateTime());
        }
        
        booking.setTimeFrame(rs.getString("khung_gio"));
        booking.setBasePrice(rs.getBigDecimal("gia_phong"));
        booking.setDepositAmount(rs.getBigDecimal("tien_coc"));
        booking.setLateFee(rs.getBigDecimal("phi_tre_han"));
        booking.setTotalAmount(rs.getBigDecimal("tong_tien"));
        booking.setStatus(rs.getString("trang_thai"));
        booking.setNotes(rs.getString("ghi_chu"));
        
        long employeeId = rs.getLong("nhan_vien_id");
        if (!rs.wasNull()) {
            booking.setEmployeeId(employeeId);
        }
        
        Timestamp createdAt = rs.getTimestamp("ngay_tao");
        if (createdAt != null) {
            booking.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        booking.setRowVersion(rs.getBytes("row_version"));
        
        return booking;
    }
}
