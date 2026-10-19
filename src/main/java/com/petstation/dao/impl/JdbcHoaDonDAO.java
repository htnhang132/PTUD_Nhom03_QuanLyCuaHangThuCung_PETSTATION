package com.petstation.dao.impl;

import com.petstation.dao.HoaDonDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.HoaDon;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC hóa đơn DAO. */
public class JdbcHoaDonDAO implements HoaDonDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcHoaDonDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcHoaDonDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcHoaDonDAO.
     *
     * @param hoaDon hóa đơn
     * @return kết quả tạo
     */
    @Override
    public HoaDon tao(HoaDon hoaDon) {
        // SQL.
        String sql =
                "INSERT INTO hoa_don (ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
                    + " ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setString(1, hoaDon.layMaHoaDon());
            cauLenh.setObject(2, hoaDon.layIdKhachHang());
            cauLenh.setLong(3, hoaDon.layIdNhanVien());
            cauLenh.setObject(4, hoaDon.layIdPhienCa());
            cauLenh.setTimestamp(
                    5,
                    Timestamp.valueOf(
                            hoaDon.layNgayLapHoaDon() != null
                                    ? hoaDon.layNgayLapHoaDon()
                                    : LocalDateTime.now()));
            cauLenh.setBigDecimal(6, hoaDon.layTamTinh());
            cauLenh.setBigDecimal(7, hoaDon.layTienGiamGia());
            cauLenh.setBigDecimal(8, hoaDon.layTienGiamTuDiem());
            cauLenh.setBigDecimal(9, hoaDon.layTienThue());
            cauLenh.setBigDecimal(10, hoaDon.layTongTien());
            cauLenh.setBigDecimal(11, hoaDon.layKhachHangDaTra());
            cauLenh.setBigDecimal(12, hoaDon.laySoTienThayDoi());
            cauLenh.setString(13, hoaDon.layPhuongThucThanhToan());
            cauLenh.setString(14, hoaDon.layLoaiHoaDon());
            cauLenh.setString(15, hoaDon.layTrangThai() != null ? hoaDon.layTrangThai() : "DRAFT");
            cauLenh.setString(16, hoaDon.layGhiChu());
            cauLenh.setTimestamp(17, Timestamp.valueOf(LocalDateTime.now()));

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    hoaDon.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return hoaDon;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error creating invoice: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcHoaDonDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<HoaDon> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenHoaDon(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding invoice: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo mã hóa đơn trong JdbcHoaDonDAO.
     *
     * @param maHoaDon mã hóa đơn
     * @return kết quả tìm theo mã hóa đơn
     */
    @Override
    public Optional<HoaDon> timTheoMaHoaDon(String maHoaDon) {
        // SQL.
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE ma_hoa_don ="
                    + " ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maHoaDon);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenHoaDon(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding invoice by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo id khách hàng trong JdbcHoaDonDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả tìm theo id khách hàng
     */
    @Override
    public List<HoaDon> timTheoIdKhachHang(Long idKhachHang) {
        // SQL.
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE khach_hang_id"
                    + " = ? ORDER BY ngay_hoa_don DESC";

        // Các hóa đơn.
        List<HoaDon> cacHoaDon = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idKhachHang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacHoaDon.add(anhXaKetQuaDatDenHoaDon(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding invoices by customer: " + loi.getMessage(), loi);
        }

        return cacHoaDon;
    }

    /**
     * Tìm theo id nhân viên trong JdbcHoaDonDAO.
     *
     * @param idNhanVien id nhân viên
     * @return kết quả tìm theo id nhân viên
     */
    @Override
    public List<HoaDon> timTheoIdNhanVien(Long idNhanVien) {
        // SQL.
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE nhan_vien_id"
                    + " = ? ORDER BY ngay_hoa_don DESC";

        // Các hóa đơn.
        List<HoaDon> cacHoaDon = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idNhanVien);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacHoaDon.add(anhXaKetQuaDatDenHoaDon(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding invoices by employee: " + loi.getMessage(), loi);
        }

        return cacHoaDon;
    }

    /**
     * Tìm theo id phiên ca trong JdbcHoaDonDAO.
     *
     * @param idPhienCa id phiên ca
     * @return kết quả tìm theo id phiên ca
     */
    @Override
    public List<HoaDon> timTheoIdPhienCa(Long idPhienCa) {
        // SQL.
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE"
                    + " phien_lam_viec_id = ? ORDER BY ngay_hoa_don";

        // Các hóa đơn.
        List<HoaDon> cacHoaDon = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idPhienCa);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacHoaDon.add(anhXaKetQuaDatDenHoaDon(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding invoices by shift: " + loi.getMessage(), loi);
        }

        return cacHoaDon;
    }

    /**
     * Tìm theo khoảng ngày trong JdbcHoaDonDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả tìm theo khoảng ngày
     */
    @Override
    public List<HoaDon> timTheoKhoangNgay(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc) {
        // SQL.
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE ngay_hoa_don"
                    + " BETWEEN ? AND ? ORDER BY ngay_hoa_don";

        // Các hóa đơn.
        List<HoaDon> cacHoaDon = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(1, Timestamp.valueOf(ngayBatDau));
            cauLenh.setTimestamp(2, Timestamp.valueOf(ngayKetThuc));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacHoaDon.add(anhXaKetQuaDatDenHoaDon(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding invoices by date range: " + loi.getMessage(), loi);
        }

        return cacHoaDon;
    }

    /**
     * Tìm theo trạng thái trong JdbcHoaDonDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    @Override
    public List<HoaDon> timTheoTrangThai(String trangThai) {
        // SQL.
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE trang_thai ="
                    + " ? ORDER BY ngay_hoa_don DESC";

        // Các hóa đơn.
        List<HoaDon> cacHoaDon = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacHoaDon.add(anhXaKetQuaDatDenHoaDon(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding invoices by status: " + loi.getMessage(), loi);
        }

        return cacHoaDon;
    }

    /**
     * Tìm theo loại hóa đơn trong JdbcHoaDonDAO.
     *
     * @param loaiHoaDon loại hóa đơn
     * @return kết quả tìm theo loại hóa đơn
     */
    @Override
    public List<HoaDon> timTheoLoaiHoaDon(String loaiHoaDon) {
        // SQL.
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE loai_hoa_don"
                    + " = ? ORDER BY ngay_hoa_don DESC";

        // Các hóa đơn.
        List<HoaDon> cacHoaDon = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, loaiHoaDon);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacHoaDon.add(anhXaKetQuaDatDenHoaDon(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding invoices by type: " + loi.getMessage(), loi);
        }

        return cacHoaDon;
    }

    /**
     * Tìm theo phương thức thanh toán trong JdbcHoaDonDAO.
     *
     * @param phuongThucThanhToan phương thức thanh toán
     * @return kết quả tìm theo phương thức thanh toán
     */
    @Override
    public List<HoaDon> timTheoPhuongThucThanhToan(String phuongThucThanhToan) {
        // SQL.
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don WHERE"
                    + " phuong_thuc_thanh_toan = ? ORDER BY ngay_hoa_don DESC";

        // Các hóa đơn.
        List<HoaDon> cacHoaDon = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, phuongThucThanhToan);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacHoaDon.add(anhXaKetQuaDatDenHoaDon(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding invoices by payment method: " + loi.getMessage(), loi);
        }

        return cacHoaDon;
    }

    /**
     * Tìm tất cả trong JdbcHoaDonDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<HoaDon> timTatCa() {
        // SQL.
        String sql =
                "SELECT id, ma_hoa_don, khach_hang_id, nhan_vien_id, phien_lam_viec_id,"
                    + " ngay_hoa_don, tong_truoc_giam, tien_giam_gia, tien_diem, tien_thue,"
                    + " tong_tien, tien_khach_dua, tien_thua, phuong_thuc_thanh_toan, loai_hoa_don,"
                    + " trang_thai, ghi_chu, ngay_tao, row_version FROM hoa_don ORDER BY ngay_tao"
                    + " DESC";

        // Các hóa đơn.
        List<HoaDon> cacHoaDon = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacHoaDon.add(anhXaKetQuaDatDenHoaDon(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all invoices: " + loi.getMessage(), loi);
        }

        return cacHoaDon;
    }

    /**
     * Cập nhật trong JdbcHoaDonDAO.
     *
     * @param hoaDon hóa đơn
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(HoaDon hoaDon) {
        // SQL.
        String sql =
                "UPDATE hoa_don SET tong_truoc_giam = ?, tien_giam_gia = ?, tien_diem = ?, "
                        + "tien_thue = ?, tong_tien = ?, tien_khach_dua = ?, tien_thua = ?, "
                        + "phuong_thuc_thanh_toan = ?, trang_thai = ?, ghi_chu = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setBigDecimal(1, hoaDon.layTamTinh());
            cauLenh.setBigDecimal(2, hoaDon.layTienGiamGia());
            cauLenh.setBigDecimal(3, hoaDon.layTienGiamTuDiem());
            cauLenh.setBigDecimal(4, hoaDon.layTienThue());
            cauLenh.setBigDecimal(5, hoaDon.layTongTien());
            cauLenh.setBigDecimal(6, hoaDon.layKhachHangDaTra());
            cauLenh.setBigDecimal(7, hoaDon.laySoTienThayDoi());
            cauLenh.setString(8, hoaDon.layPhuongThucThanhToan());
            cauLenh.setString(9, hoaDon.layTrangThai());
            cauLenh.setString(10, hoaDon.layGhiChu());
            cauLenh.setLong(11, hoaDon.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating invoice: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật trạng thái trong JdbcHoaDonDAO.
     *
     * @param idHoaDon id hóa đơn
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatTrangThai(Long idHoaDon, String trangThai) {
        // SQL.
        String sql = "UPDATE hoa_don SET trang_thai = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            cauLenh.setLong(2, idHoaDon);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating invoice status: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcHoaDonDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM hoa_don WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting invoice: " + loi.getMessage(), loi);
        }
    }

    /**
     * Lấy khoảng tổng doanh thu theo ngày trong JdbcHoaDonDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả lấy khoảng tổng doanh thu theo ngày
     */
    @Override
    public BigDecimal layKhoangTongDoanhThuTheoNgay(
            LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc) {
        // SQL.
        String sql =
                "SELECT ISNULL(SUM(tong_tien), 0) FROM hoa_don "
                        + "WHERE ngay_hoa_don BETWEEN ? AND ? AND trang_thai = 'COMPLETED'";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(1, Timestamp.valueOf(ngayBatDau));
            cauLenh.setTimestamp(2, Timestamp.valueOf(ngayKetThuc));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getBigDecimal(1);
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error calculating revenue: " + loi.getMessage(), loi);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Lấy tổng doanh thu theo phiên ca trong JdbcHoaDonDAO.
     *
     * @param idPhienCa id phiên ca
     * @return kết quả lấy tổng doanh thu theo phiên ca
     */
    @Override
    public BigDecimal layTongDoanhThuTheoPhienCa(Long idPhienCa) {
        // SQL.
        String sql =
                "SELECT ISNULL(SUM(tong_tien), 0) FROM hoa_don "
                        + "WHERE phien_lam_viec_id = ? AND trang_thai = 'COMPLETED'";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idPhienCa);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getBigDecimal(1);
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error calculating shift revenue: " + loi.getMessage(), loi);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Đếm theo khoảng ngày trong JdbcHoaDonDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả đếm theo khoảng ngày
     */
    @Override
    public long demTheoKhoangNgay(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc) {
        // SQL.
        String sql = "SELECT COUNT(*) FROM hoa_don WHERE ngay_hoa_don BETWEEN ? AND ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(1, Timestamp.valueOf(ngayBatDau));
            cauLenh.setTimestamp(2, Timestamp.valueOf(ngayKetThuc));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getLong(1);
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting invoices: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Đếm trong JdbcHoaDonDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM hoa_don";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting invoices: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến hóa đơn trong JdbcHoaDonDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến hóa đơn
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private HoaDon anhXaKetQuaDatDenHoaDon(ResultSet tapKetQua) throws SQLException {
        // Hóa đơn.
        HoaDon hoaDon = new HoaDon();
        hoaDon.datId(tapKetQua.getLong("id"));
        hoaDon.datMaHoaDon(tapKetQua.getString("ma_hoa_don"));

        // Id khách hàng.
        long idKhachHang = tapKetQua.getLong("khach_hang_id");
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (!tapKetQua.wasNull()) {
            hoaDon.datIdKhachHang(idKhachHang);
        }

        hoaDon.datIdNhanVien(tapKetQua.getLong("nhan_vien_id"));

        // Id phiên ca.
        long idPhienCa = tapKetQua.getLong("phien_lam_viec_id");
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (!tapKetQua.wasNull()) {
            hoaDon.datIdPhienCa(idPhienCa);
        }

        // Ngày lập hóa đơn.
        Timestamp ngayLapHoaDon = tapKetQua.getTimestamp("ngay_hoa_don");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayLapHoaDon != null) {
            hoaDon.datNgayLapHoaDon(ngayLapHoaDon.toLocalDateTime());
        }

        hoaDon.datTamTinh(tapKetQua.getBigDecimal("tong_truoc_giam"));
        hoaDon.datTienGiamGia(tapKetQua.getBigDecimal("tien_giam_gia"));
        hoaDon.datTienGiamTuDiem(tapKetQua.getBigDecimal("tien_diem"));
        hoaDon.datTienThue(tapKetQua.getBigDecimal("tien_thue"));
        hoaDon.datTongTien(tapKetQua.getBigDecimal("tong_tien"));
        hoaDon.datKhachHangDaTra(tapKetQua.getBigDecimal("tien_khach_dua"));
        hoaDon.datSoTienThayDoi(tapKetQua.getBigDecimal("tien_thua"));
        hoaDon.datPhuongThucThanhToan(tapKetQua.getString("phuong_thuc_thanh_toan"));
        hoaDon.datLoaiHoaDon(tapKetQua.getString("loai_hoa_don"));
        hoaDon.datTrangThai(tapKetQua.getString("trang_thai"));
        hoaDon.datGhiChu(tapKetQua.getString("ghi_chu"));

        // Thời điểm tạo.
        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_tao");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemTao != null) {
            hoaDon.datThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        hoaDon.datPhienBanBanGhi(tapKetQua.getBytes("row_version"));

        return hoaDon;
    }
}
