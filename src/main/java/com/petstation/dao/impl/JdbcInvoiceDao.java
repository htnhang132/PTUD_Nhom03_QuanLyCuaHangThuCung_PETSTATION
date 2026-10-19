package com.petstation.dao.impl;

import com.petstation.dao.DataAccessException;
import com.petstation.dao.InvoiceDao;
import com.petstation.model.Invoice;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of InvoiceDao */
public class JdbcInvoiceDao implements InvoiceDao {

    private final Connection connection;

    public JdbcInvoiceDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Invoice create(Invoice invoice) {
        String sql =
                "INSERT INTO hoa_don (ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
                    + " ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, invoice.getInvoiceCode());
            stmt.setObject(2, invoice.getCustomerId());
            stmt.setLong(3, invoice.getEmployeeId());
            stmt.setObject(4, invoice.getShiftSessionId());
            stmt.setTimestamp(
                    5,
                    Timestamp.valueOf(
                            invoice.getInvoiceDate() != null
                                    ? invoice.getInvoiceDate()
                                    : LocalDateTime.now()));
            stmt.setBigDecimal(6, invoice.getSubtotal());
            stmt.setBigDecimal(7, invoice.getDiscountAmount());
            stmt.setBigDecimal(8, invoice.getLoyaltyPointsAmount());
            stmt.setBigDecimal(9, invoice.getTaxAmount());
            stmt.setBigDecimal(10, invoice.getTotalAmount());
            stmt.setBigDecimal(11, invoice.getCustomerPaid());
            stmt.setBigDecimal(12, invoice.getChangeAmount());
            stmt.setString(13, invoice.getPaymentMethod());
            stmt.setString(14, invoice.getInvoiceType());
            stmt.setString(15, invoice.getStatus() != null ? invoice.getStatus() : "DRAFT");
            stmt.setString(16, invoice.getNotes());
            stmt.setTimestamp(17, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    invoice.setId(generatedKeys.getLong(1));
                }
            }

            return invoice;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding invoice: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Invoice> findByInvoiceCode(String invoiceCode) {
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE ma_hoa_don ="
                    + " ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, invoiceCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding invoice by code: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<Invoice> findByCustomerId(Long customerId) {
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE khach_hang_id"
                    + " = ? ORDER BY ngay_hoa_don DESC";

        List<Invoice> invoices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding invoices by customer: " + e.getMessage(), e);
        }

        return invoices;
    }

    @Override
    public List<Invoice> findByEmployeeId(Long employeeId) {
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE nhan_vien_id"
                    + " = ? ORDER BY ngay_hoa_don DESC";

        List<Invoice> invoices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding invoices by employee: " + e.getMessage(), e);
        }

        return invoices;
    }

    @Override
    public List<Invoice> findByShiftSessionId(Long shiftSessionId) {
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE"
                    + " phien_lam_viec_id = ? ORDER BY ngay_hoa_don";

        List<Invoice> invoices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, shiftSessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding invoices by shift: " + e.getMessage(), e);
        }

        return invoices;
    }

    @Override
    public List<Invoice> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE ngay_hoa_don"
                    + " BETWEEN ? AND ? ORDER BY ngay_hoa_don";

        List<Invoice> invoices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding invoices by date range: " + e.getMessage(), e);
        }

        return invoices;
    }

    @Override
    public List<Invoice> findByStatus(String status) {
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE trang_thai ="
                    + " ? ORDER BY ngay_hoa_don DESC";

        List<Invoice> invoices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding invoices by status: " + e.getMessage(), e);
        }

        return invoices;
    }

    @Override
    public List<Invoice> findByInvoiceType(String invoiceType) {
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE loai_hoa_don"
                    + " = ? ORDER BY ngay_hoa_don DESC";

        List<Invoice> invoices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, invoiceType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding invoices by type: " + e.getMessage(), e);
        }

        return invoices;
    }

    @Override
    public List<Invoice> findByPaymentMethod(String paymentMethod) {
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE"
                    + " phuong_thuc_thanh_toan = ? ORDER BY ngay_hoa_don DESC";

        List<Invoice> invoices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, paymentMethod);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding invoices by payment method: " + e.getMessage(), e);
        }

        return invoices;
    }

    @Override
    public List<Invoice> findAll() {
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don ORDER BY ngay_tao"
                    + " DESC";

        List<Invoice> invoices = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all invoices: " + e.getMessage(), e);
        }

        return invoices;
    }

    @Override
    public boolean update(Invoice invoice) {
        String sql =
                "UPDATE hoa_don SET tong_truoc_giam = ?, tien_giam_gia = ?, tien_diem = ?, "
                        + "tien_thue = ?, tong_tien = ?, tien_khach_dua = ?, tien_thua = ?, "
                        + "phuong_thuc_thanh_toan = ?, trang_thai = ?, ghi_chu = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, invoice.getSubtotal());
            stmt.setBigDecimal(2, invoice.getDiscountAmount());
            stmt.setBigDecimal(3, invoice.getLoyaltyPointsAmount());
            stmt.setBigDecimal(4, invoice.getTaxAmount());
            stmt.setBigDecimal(5, invoice.getTotalAmount());
            stmt.setBigDecimal(6, invoice.getCustomerPaid());
            stmt.setBigDecimal(7, invoice.getChangeAmount());
            stmt.setString(8, invoice.getPaymentMethod());
            stmt.setString(9, invoice.getStatus());
            stmt.setString(10, invoice.getNotes());
            stmt.setLong(11, invoice.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Long invoiceId, String status) {
        String sql = "UPDATE hoa_don SET trang_thai = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, invoiceId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating invoice status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM hoa_don WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql =
                "SELECT ISNULL(SUM(tong_tien), 0) FROM hoa_don "
                        + "WHERE ngay_hoa_don BETWEEN ? AND ? AND trang_thai = 'COMPLETED'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error calculating revenue: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalRevenueByShiftSession(Long shiftSessionId) {
        String sql =
                "SELECT ISNULL(SUM(tong_tien), 0) FROM hoa_don "
                        + "WHERE phien_lam_viec_id = ? AND trang_thai = 'COMPLETED'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, shiftSessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error calculating shift revenue: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public long countByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT COUNT(*) FROM hoa_don WHERE ngay_hoa_don BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting invoices: " + e.getMessage(), e);
        }

        return 0;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM hoa_don";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting invoices: " + e.getMessage(), e);
        }

        return 0;
    }

    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getLong("id"));
        invoice.setInvoiceCode(rs.getString("ma_hoa_don"));

        long customerId = rs.getLong("khach_hang_id");
        if (!rs.wasNull()) {
            invoice.setCustomerId(customerId);
        }

        invoice.setEmployeeId(rs.getLong("nhan_vien_id"));

        long shiftSessionId = rs.getLong("phien_lam_viec_id");
        if (!rs.wasNull()) {
            invoice.setShiftSessionId(shiftSessionId);
        }

        Timestamp invoiceDate = rs.getTimestamp("ngay_hoa_don");
        if (invoiceDate != null) {
            invoice.setInvoiceDate(invoiceDate.toLocalDateTime());
        }

        invoice.setSubtotal(rs.getBigDecimal("tong_truoc_giam"));
        invoice.setDiscountAmount(rs.getBigDecimal("tien_giam_gia"));
        invoice.setLoyaltyPointsAmount(rs.getBigDecimal("tien_diem"));
        invoice.setTaxAmount(rs.getBigDecimal("tien_thue"));
        invoice.setTotalAmount(rs.getBigDecimal("tong_tien"));
        invoice.setCustomerPaid(rs.getBigDecimal("tien_khach_dua"));
        invoice.setChangeAmount(rs.getBigDecimal("tien_thua"));
        invoice.setPaymentMethod(rs.getString("phuong_thuc_thanh_toan"));
        invoice.setInvoiceType(rs.getString("loai_hoa_don"));
        invoice.setStatus(rs.getString("trang_thai"));
        invoice.setNotes(rs.getString("ghi_chu"));

        Timestamp createdAt = rs.getTimestamp("ngay_tao");
        if (createdAt != null) {
            invoice.setCreatedAt(createdAt.toLocalDateTime());
        }

        invoice.setRowVersion(rs.getBytes("row_version"));

        return invoice;
    }
}
