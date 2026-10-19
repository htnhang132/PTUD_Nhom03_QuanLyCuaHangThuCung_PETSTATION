package com.petstation.dao.impl;

import com.petstation.dao.ChotNgayDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.ChotNgay;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC ngày khóa DAO. */
public class JdbcChotNgayDAO implements ChotNgayDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcChotNgayDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcChotNgayDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcChotNgayDAO.
     *
     * @param ngayKhoa ngày khóa
     * @return kết quả tạo
     */
    @Override
    public ChotNgay tao(ChotNgay ngayKhoa) {
        // SQL.
        String sql =
                "INSERT INTO chot_ngay (ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setDate(1, Date.valueOf(ngayKhoa.layNgayChot()));
            cauLenh.setLong(2, ngayKhoa.layNguoiKhoa());
            cauLenh.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            ngayKhoa.layThoiDiemKhoa() != null
                                    ? ngayKhoa.layThoiDiemKhoa()
                                    : LocalDateTime.now()));
            cauLenh.setBigDecimal(4, ngayKhoa.layTongDoanhThu());
            cauLenh.setBigDecimal(5, ngayKhoa.layTongTienMat());
            cauLenh.setBigDecimal(6, ngayKhoa.layTongTienChuyenKhoan());
            cauLenh.setBigDecimal(7, ngayKhoa.layTongTienThe());
            cauLenh.setInt(8, ngayKhoa.laySoLuongHoaDon());
            cauLenh.setString(9, ngayKhoa.layGhiChu());
            cauLenh.setString(
                    10, ngayKhoa.layTrangThai() != null ? ngayKhoa.layTrangThai() : "LOCKED");

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    ngayKhoa.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return ngayKhoa;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error creating day lock: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcChotNgayDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<ChotNgay> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai, rowversion FROM chot_ngay WHERE chot_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenNgayKhoa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding day lock: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo ngày chốt trong JdbcChotNgayDAO.
     *
     * @param ngayChot ngày chốt
     * @return kết quả tìm theo ngày chốt
     */
    @Override
    public Optional<ChotNgay> timTheoNgayChot(LocalDate ngayChot) {
        // SQL.
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai, rowversion FROM chot_ngay WHERE ngay_chot = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setDate(1, Date.valueOf(ngayChot));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenNgayKhoa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding day lock by date: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo người khóa trong JdbcChotNgayDAO.
     *
     * @param idNguoiDung id người dùng
     * @return kết quả tìm theo người khóa
     */
    @Override
    public List<ChotNgay> timTheoNguoiKhoa(Long idNguoiDung) {
        // SQL.
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai, rowversion FROM chot_ngay WHERE nguoi_chot_id = ? ORDER BY"
                        + " ngay_chot DESC";

        // Ngày khóa.
        List<ChotNgay> ngayKhoa = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idNguoiDung);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    ngayKhoa.add(anhXaKetQuaDatDenNgayKhoa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding day locks by user: " + loi.getMessage(), loi);
        }

        return ngayKhoa;
    }

    /**
     * Tìm theo trạng thái trong JdbcChotNgayDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    @Override
    public List<ChotNgay> timTheoTrangThai(String trangThai) {
        // SQL.
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai, rowversion FROM chot_ngay WHERE trang_thai = ? ORDER BY"
                        + " ngay_chot DESC";

        // Ngày khóa.
        List<ChotNgay> ngayKhoa = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    ngayKhoa.add(anhXaKetQuaDatDenNgayKhoa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding day locks by status: " + loi.getMessage(), loi);
        }

        return ngayKhoa;
    }

    /**
     * Tìm theo khoảng ngày trong JdbcChotNgayDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả tìm theo khoảng ngày
     */
    @Override
    public List<ChotNgay> timTheoKhoangNgay(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        // SQL.
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                    + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                    + " trang_thai, rowversion FROM chot_ngay WHERE ngay_chot BETWEEN ? AND ? ORDER"
                    + " BY ngay_chot";

        // Ngày khóa.
        List<ChotNgay> ngayKhoa = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setDate(1, Date.valueOf(ngayBatDau));
            cauLenh.setDate(2, Date.valueOf(ngayKetThuc));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    ngayKhoa.add(anhXaKetQuaDatDenNgayKhoa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding day locks by date range: " + loi.getMessage(), loi);
        }

        return ngayKhoa;
    }

    /**
     * Tìm đã khóa số ngày trong JdbcChotNgayDAO.
     *
     * @return kết quả tìm đã khóa số ngày
     */
    @Override
    public List<ChotNgay> timDaKhoaSoNgay() {
        return timTheoTrangThai("LOCKED");
    }

    /**
     * Tìm tất cả trong JdbcChotNgayDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<ChotNgay> timTatCa() {
        // SQL.
        String sql =
                "SELECT chot_id, ngay_chot, nguoi_chot_id, thoi_diem_chot, tong_doanh_thu,"
                        + " tong_tien_mat, tong_chuyen_khoan, tong_the, so_hoa_don, ghi_chu,"
                        + " trang_thai, rowversion FROM chot_ngay ORDER BY ngay_chot DESC";

        // Ngày khóa.
        List<ChotNgay> ngayKhoa = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                ngayKhoa.add(anhXaKetQuaDatDenNgayKhoa(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all day locks: " + loi.getMessage(), loi);
        }

        return ngayKhoa;
    }

    /**
     * Cập nhật trong JdbcChotNgayDAO.
     *
     * @param ngayKhoa ngày khóa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(ChotNgay ngayKhoa) {
        // SQL.
        String sql =
                "UPDATE chot_ngay SET tong_doanh_thu = ?, tong_tien_mat = ?, tong_chuyen_khoan = ?,"
                    + " tong_the = ?, so_hoa_don = ?, ghi_chu = ?, trang_thai = ? WHERE chot_id ="
                    + " ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setBigDecimal(1, ngayKhoa.layTongDoanhThu());
            cauLenh.setBigDecimal(2, ngayKhoa.layTongTienMat());
            cauLenh.setBigDecimal(3, ngayKhoa.layTongTienChuyenKhoan());
            cauLenh.setBigDecimal(4, ngayKhoa.layTongTienThe());
            cauLenh.setInt(5, ngayKhoa.laySoLuongHoaDon());
            cauLenh.setString(6, ngayKhoa.layGhiChu());
            cauLenh.setString(7, ngayKhoa.layTrangThai());
            cauLenh.setLong(8, ngayKhoa.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating day lock: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật trạng thái trong JdbcChotNgayDAO.
     *
     * @param idNgayKhoa id ngày khóa
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatTrangThai(Long idNgayKhoa, String trangThai) {
        // SQL.
        String sql = "UPDATE chot_ngay SET trang_thai = ? WHERE chot_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            cauLenh.setLong(2, idNgayKhoa);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating day lock status: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcChotNgayDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM chot_ngay WHERE chot_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting day lock: " + loi.getMessage(), loi);
        }
    }

    /**
     * Kiểm tra ngày đã khóa trong JdbcChotNgayDAO.
     *
     * @param ngay ngày
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean kiemTraNgayDaKhoa(LocalDate ngay) {
        // SQL.
        String sql = "SELECT COUNT(*) FROM chot_ngay WHERE ngay_chot = ? AND trang_thai = 'LOCKED'";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setDate(1, Date.valueOf(ngay));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getInt(1) > 0;
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error checking if day is locked: " + loi.getMessage(), loi);
        }

        return false;
    }

    /**
     * Đếm trong JdbcChotNgayDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM chot_ngay";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting day locks: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến ngày khóa trong JdbcChotNgayDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến ngày khóa
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private ChotNgay anhXaKetQuaDatDenNgayKhoa(ResultSet tapKetQua) throws SQLException {
        // Ngày khóa.
        ChotNgay ngayKhoa = new ChotNgay();
        ngayKhoa.datId(tapKetQua.getLong("chot_id"));

        // Ngày chốt.
        Date ngayChot = tapKetQua.getDate("ngay_chot");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayChot != null) {
            ngayKhoa.datNgayChot(ngayChot.toLocalDate());
        }

        ngayKhoa.datNguoiKhoa(tapKetQua.getLong("nguoi_chot_id"));

        // Thời điểm khóa.
        Timestamp thoiDiemKhoa = tapKetQua.getTimestamp("thoi_diem_chot");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemKhoa != null) {
            ngayKhoa.datThoiDiemKhoa(thoiDiemKhoa.toLocalDateTime());
        }

        ngayKhoa.datTongDoanhThu(tapKetQua.getBigDecimal("tong_doanh_thu"));
        ngayKhoa.datTongTienMat(tapKetQua.getBigDecimal("tong_tien_mat"));
        ngayKhoa.datTongTienChuyenKhoan(tapKetQua.getBigDecimal("tong_chuyen_khoan"));
        ngayKhoa.datTongTienThe(tapKetQua.getBigDecimal("tong_the"));
        ngayKhoa.datSoLuongHoaDon(tapKetQua.getInt("so_hoa_don"));
        ngayKhoa.datGhiChu(tapKetQua.getString("ghi_chu"));
        ngayKhoa.datTrangThai(tapKetQua.getString("trang_thai"));
        ngayKhoa.datPhienBanBanGhi(tapKetQua.getBytes("rowversion"));

        return ngayKhoa;
    }
}
