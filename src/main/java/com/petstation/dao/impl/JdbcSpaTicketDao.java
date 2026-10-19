package com.petstation.dao.impl;

import com.petstation.dao.DataAccessException;
import com.petstation.dao.SpaTicketDao;
import com.petstation.model.SpaTicket;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of SpaTicketDao */
public class JdbcSpaTicketDao implements SpaTicketDao {

    private final Connection connection;

    public JdbcSpaTicketDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public SpaTicket create(SpaTicket spaTicket) {
        String sql =
                "INSERT INTO phieu_tiep_nhan_spa (ma_phieu, thu_cung_id, khach_hang_id, goi_id,"
                    + " nhan_vien_id, gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien,"
                    + " trang_thai, ghi_chu, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
                    + " ?)";

        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, spaTicket.getTicketCode());
            stmt.setLong(2, spaTicket.getPetId());
            stmt.setLong(3, spaTicket.getCustomerId());
            stmt.setLong(4, spaTicket.getPackageId());
            stmt.setObject(5, spaTicket.getEmployeeId());
            stmt.setTimestamp(
                    6,
                    Timestamp.valueOf(
                            spaTicket.getCheckInTime() != null
                                    ? spaTicket.getCheckInTime()
                                    : LocalDateTime.now()));
            stmt.setTimestamp(
                    7,
                    spaTicket.getCheckOutTime() != null
                            ? Timestamp.valueOf(spaTicket.getCheckOutTime())
                            : null);
            stmt.setBigDecimal(8, spaTicket.getBasePrice());
            stmt.setBigDecimal(9, spaTicket.getSurchargeAmount());
            stmt.setBigDecimal(10, spaTicket.getTotalAmount());
            stmt.setString(11, spaTicket.getStatus() != null ? spaTicket.getStatus() : "PENDING");
            stmt.setString(12, spaTicket.getNotes());
            stmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    spaTicket.setId(generatedKeys.getLong(1));
                }
            }

            return spaTicket;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating spa ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SpaTicket> findById(Long id) {
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE phieu_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSpaTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding spa ticket: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<SpaTicket> findByTicketCode(String ticketCode) {
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE ma_phieu = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ticketCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSpaTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding spa ticket by code: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<SpaTicket> findByPetId(Long petId) {
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE thu_cung_id = ? ORDER"
                    + " BY gio_nhan DESC";

        return findTickets(sql, petId);
    }

    @Override
    public List<SpaTicket> findByCustomerId(Long customerId) {
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE khach_hang_id = ? ORDER"
                    + " BY gio_nhan DESC";

        return findTickets(sql, customerId);
    }

    @Override
    public List<SpaTicket> findByPackageId(Long packageId) {
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE goi_id = ? ORDER BY"
                    + " gio_nhan DESC";

        return findTickets(sql, packageId);
    }

    @Override
    public List<SpaTicket> findByEmployeeId(Long employeeId) {
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE nhan_vien_id = ? ORDER"
                    + " BY gio_nhan DESC";

        return findTickets(sql, employeeId);
    }

    @Override
    public List<SpaTicket> findByStatus(String status) {
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE trang_thai = ? ORDER BY"
                    + " gio_nhan";

        List<SpaTicket> tickets = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToSpaTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding spa tickets by status: " + e.getMessage(), e);
        }

        return tickets;
    }

    @Override
    public List<SpaTicket> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE gio_nhan BETWEEN ? AND"
                    + " ? ORDER BY gio_nhan";

        List<SpaTicket> tickets = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToSpaTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding spa tickets by date range: " + e.getMessage(), e);
        }

        return tickets;
    }

    @Override
    public List<SpaTicket> findPendingTickets() {
        return findByStatus("PENDING");
    }

    @Override
    public List<SpaTicket> findInProgressTickets() {
        return findByStatus("IN_PROGRESS");
    }

    @Override
    public List<SpaTicket> findAll() {
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa ORDER BY ngay_tao DESC";

        List<SpaTicket> tickets = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tickets.add(mapResultSetToSpaTicket(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all spa tickets: " + e.getMessage(), e);
        }

        return tickets;
    }

    @Override
    public boolean update(SpaTicket spaTicket) {
        String sql =
                "UPDATE phieu_tiep_nhan_spa SET gio_tra = ?, phi_phu_thu = ?, tong_tien = ?, "
                        + "trang_thai = ?, ghi_chu = ? WHERE phieu_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(
                    1,
                    spaTicket.getCheckOutTime() != null
                            ? Timestamp.valueOf(spaTicket.getCheckOutTime())
                            : null);
            stmt.setBigDecimal(2, spaTicket.getSurchargeAmount());
            stmt.setBigDecimal(3, spaTicket.getTotalAmount());
            stmt.setString(4, spaTicket.getStatus());
            stmt.setString(5, spaTicket.getNotes());
            stmt.setLong(6, spaTicket.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating spa ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Long ticketId, String status) {
        String sql = "UPDATE phieu_tiep_nhan_spa SET trang_thai = ? WHERE phieu_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, ticketId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating spa ticket status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateCheckOutTime(Long ticketId, LocalDateTime checkOutTime) {
        String sql =
                "UPDATE phieu_tiep_nhan_spa SET gio_tra = ?, trang_thai = 'COMPLETED' WHERE"
                        + " phieu_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(checkOutTime));
            stmt.setLong(2, ticketId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating check-out time: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM phieu_tiep_nhan_spa WHERE phieu_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting spa ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM phieu_tiep_nhan_spa";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting spa tickets: " + e.getMessage(), e);
        }

        return 0;
    }

    private List<SpaTicket> findTickets(String sql, Long param) {
        List<SpaTicket> tickets = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToSpaTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding spa tickets: " + e.getMessage(), e);
        }

        return tickets;
    }

    private SpaTicket mapResultSetToSpaTicket(ResultSet rs) throws SQLException {
        SpaTicket ticket = new SpaTicket();
        ticket.setId(rs.getLong("phieu_id"));
        ticket.setTicketCode(rs.getString("ma_phieu"));
        ticket.setPetId(rs.getLong("thu_cung_id"));
        ticket.setCustomerId(rs.getLong("khach_hang_id"));
        ticket.setPackageId(rs.getLong("goi_id"));

        long employeeId = rs.getLong("nhan_vien_id");
        if (!rs.wasNull()) {
            ticket.setEmployeeId(employeeId);
        }

        Timestamp checkInTime = rs.getTimestamp("gio_nhan");
        if (checkInTime != null) {
            ticket.setCheckInTime(checkInTime.toLocalDateTime());
        }

        Timestamp checkOutTime = rs.getTimestamp("gio_tra");
        if (checkOutTime != null) {
            ticket.setCheckOutTime(checkOutTime.toLocalDateTime());
        }

        ticket.setBasePrice(rs.getBigDecimal("gia_goi"));
        ticket.setSurchargeAmount(rs.getBigDecimal("phi_phu_thu"));
        ticket.setTotalAmount(rs.getBigDecimal("tong_tien"));
        ticket.setStatus(rs.getString("trang_thai"));
        ticket.setNotes(rs.getString("ghi_chu"));

        Timestamp createdAt = rs.getTimestamp("ngay_tao");
        if (createdAt != null) {
            ticket.setCreatedAt(createdAt.toLocalDateTime());
        }

        ticket.setRowVersion(rs.getBytes("rowversion"));

        return ticket;
    }
}
