package com.petstation.dao.impl;

import com.petstation.dao.PhieuTiepNhanSpaDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.PhieuTiepNhanSpa;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC phiếu tiếp nhận Spa DAO. */
public class JdbcPhieuTiepNhanSpaDAO implements PhieuTiepNhanSpaDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcPhieuTiepNhanSpaDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcPhieuTiepNhanSpaDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param phieuTiepNhanSpa phiếu tiếp nhận Spa
     * @return kết quả tạo
     */
    @Override
    public PhieuTiepNhanSpa tao(PhieuTiepNhanSpa phieuTiepNhanSpa) {
        // SQL.
        String sql =
                "INSERT INTO phieu_tiep_nhan_spa (ma_phieu, thu_cung_id, khach_hang_id, goi_id,"
                    + " nhan_vien_id, gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien,"
                    + " trang_thai, ghi_chu, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
                    + " ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setString(1, phieuTiepNhanSpa.layMaPhieuSpa());
            cauLenh.setLong(2, phieuTiepNhanSpa.layIdThuCung());
            cauLenh.setLong(3, phieuTiepNhanSpa.layIdKhachHang());
            cauLenh.setLong(4, phieuTiepNhanSpa.layIdGoiDichVu());
            cauLenh.setObject(5, phieuTiepNhanSpa.layIdNhanVien());
            cauLenh.setTimestamp(
                    6,
                    Timestamp.valueOf(
                            phieuTiepNhanSpa.layThoiGianNhanPhong() != null
                                    ? phieuTiepNhanSpa.layThoiGianNhanPhong()
                                    : LocalDateTime.now()));
            cauLenh.setTimestamp(
                    7,
                    phieuTiepNhanSpa.layThoiGianTraPhong() != null
                            ? Timestamp.valueOf(phieuTiepNhanSpa.layThoiGianTraPhong())
                            : null);
            cauLenh.setBigDecimal(8, phieuTiepNhanSpa.layGiaCoSo());
            cauLenh.setBigDecimal(9, phieuTiepNhanSpa.layTienPhuThu());
            cauLenh.setBigDecimal(10, phieuTiepNhanSpa.layTongTien());
            cauLenh.setString(
                    11,
                    phieuTiepNhanSpa.layTrangThai() != null
                            ? phieuTiepNhanSpa.layTrangThai()
                            : "PENDING");
            cauLenh.setString(12, phieuTiepNhanSpa.layGhiChu());
            cauLenh.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    phieuTiepNhanSpa.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return phieuTiepNhanSpa;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error creating spa ticket: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<PhieuTiepNhanSpa> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE phieu_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenPhieuTiepNhanSpa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding spa ticket: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo mã phiếu Spa trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param maPhieuSpa mã phiếu Spa
     * @return kết quả tìm theo mã phiếu Spa
     */
    @Override
    public Optional<PhieuTiepNhanSpa> timTheoMaPhieuSpa(String maPhieuSpa) {
        // SQL.
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE ma_phieu = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maPhieuSpa);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenPhieuTiepNhanSpa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding spa ticket by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo id thú cưng trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param idThuCung id thú cưng
     * @return kết quả tìm theo id thú cưng
     */
    @Override
    public List<PhieuTiepNhanSpa> timTheoIdThuCung(Long idThuCung) {
        // SQL.
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE thu_cung_id = ? ORDER"
                    + " BY gio_nhan DESC";

        return timCacPhieuSpa(sql, idThuCung);
    }

    /**
     * Tìm theo id khách hàng trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả tìm theo id khách hàng
     */
    @Override
    public List<PhieuTiepNhanSpa> timTheoIdKhachHang(Long idKhachHang) {
        // SQL.
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE khach_hang_id = ? ORDER"
                    + " BY gio_nhan DESC";

        return timCacPhieuSpa(sql, idKhachHang);
    }

    /**
     * Tìm theo id gói dịch vụ trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param idGoiDichVu id gói dịch vụ
     * @return kết quả tìm theo id gói dịch vụ
     */
    @Override
    public List<PhieuTiepNhanSpa> timTheoIdGoiDichVu(Long idGoiDichVu) {
        // SQL.
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE goi_id = ? ORDER BY"
                    + " gio_nhan DESC";

        return timCacPhieuSpa(sql, idGoiDichVu);
    }

    /**
     * Tìm theo id nhân viên trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param idNhanVien id nhân viên
     * @return kết quả tìm theo id nhân viên
     */
    @Override
    public List<PhieuTiepNhanSpa> timTheoIdNhanVien(Long idNhanVien) {
        // SQL.
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE nhan_vien_id = ? ORDER"
                    + " BY gio_nhan DESC";

        return timCacPhieuSpa(sql, idNhanVien);
    }

    /**
     * Tìm theo trạng thái trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    @Override
    public List<PhieuTiepNhanSpa> timTheoTrangThai(String trangThai) {
        // SQL.
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE trang_thai = ? ORDER BY"
                    + " gio_nhan";

        // Các phiếu Spa.
        List<PhieuTiepNhanSpa> cacPhieuSpa = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacPhieuSpa.add(anhXaKetQuaDatDenPhieuTiepNhanSpa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding spa tickets by status: " + loi.getMessage(), loi);
        }

        return cacPhieuSpa;
    }

    /**
     * Tìm theo khoảng ngày trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả tìm theo khoảng ngày
     */
    @Override
    public List<PhieuTiepNhanSpa> timTheoKhoangNgay(
            LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc) {
        // SQL.
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa WHERE gio_nhan BETWEEN ? AND"
                    + " ? ORDER BY gio_nhan";

        // Các phiếu Spa.
        List<PhieuTiepNhanSpa> cacPhieuSpa = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(1, Timestamp.valueOf(ngayBatDau));
            cauLenh.setTimestamp(2, Timestamp.valueOf(ngayKetThuc));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacPhieuSpa.add(anhXaKetQuaDatDenPhieuTiepNhanSpa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding spa tickets by date range: " + loi.getMessage(), loi);
        }

        return cacPhieuSpa;
    }

    /**
     * Tìm đang chờ các phiếu Spa trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @return kết quả tìm đang chờ các phiếu Spa
     */
    @Override
    public List<PhieuTiepNhanSpa> timDangChoCacPhieuSpa() {
        return timTheoTrangThai("PENDING");
    }

    /**
     * Tìm vào tiến độ các phiếu Spa trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @return kết quả tìm vào tiến độ các phiếu Spa
     */
    @Override
    public List<PhieuTiepNhanSpa> timVaoTienDoCacPhieuSpa() {
        return timTheoTrangThai("IN_PROGRESS");
    }

    /**
     * Tìm tất cả trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<PhieuTiepNhanSpa> timTatCa() {
        // SQL.
        String sql =
                "SELECT phieu_id, ma_phieu, thu_cung_id, khach_hang_id, goi_id, nhan_vien_id,"
                    + " gio_nhan, gio_tra, gia_goi, phi_phu_thu, tong_tien, trang_thai, ghi_chu,"
                    + " ngay_tao, rowversion FROM phieu_tiep_nhan_spa ORDER BY ngay_tao DESC";

        // Các phiếu Spa.
        List<PhieuTiepNhanSpa> cacPhieuSpa = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacPhieuSpa.add(anhXaKetQuaDatDenPhieuTiepNhanSpa(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all spa tickets: " + loi.getMessage(), loi);
        }

        return cacPhieuSpa;
    }

    /**
     * Cập nhật trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param phieuTiepNhanSpa phiếu tiếp nhận Spa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(PhieuTiepNhanSpa phieuTiepNhanSpa) {
        // SQL.
        String sql =
                "UPDATE phieu_tiep_nhan_spa SET gio_tra = ?, phi_phu_thu = ?, tong_tien = ?, "
                        + "trang_thai = ?, ghi_chu = ? WHERE phieu_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(
                    1,
                    phieuTiepNhanSpa.layThoiGianTraPhong() != null
                            ? Timestamp.valueOf(phieuTiepNhanSpa.layThoiGianTraPhong())
                            : null);
            cauLenh.setBigDecimal(2, phieuTiepNhanSpa.layTienPhuThu());
            cauLenh.setBigDecimal(3, phieuTiepNhanSpa.layTongTien());
            cauLenh.setString(4, phieuTiepNhanSpa.layTrangThai());
            cauLenh.setString(5, phieuTiepNhanSpa.layGhiChu());
            cauLenh.setLong(6, phieuTiepNhanSpa.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating spa ticket: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật trạng thái trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param idPhieuSpa id phiếu Spa
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatTrangThai(Long idPhieuSpa, String trangThai) {
        // SQL.
        String sql = "UPDATE phieu_tiep_nhan_spa SET trang_thai = ? WHERE phieu_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            cauLenh.setLong(2, idPhieuSpa);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating spa ticket status: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật thời gian trả phòng trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param idPhieuSpa id phiếu Spa
     * @param thoiGianTraPhong thời gian trả phòng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatThoiGianTraPhong(Long idPhieuSpa, LocalDateTime thoiGianTraPhong) {
        // SQL.
        String sql =
                "UPDATE phieu_tiep_nhan_spa SET gio_tra = ?, trang_thai = 'COMPLETED' WHERE"
                        + " phieu_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(1, Timestamp.valueOf(thoiGianTraPhong));
            cauLenh.setLong(2, idPhieuSpa);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating check-out time: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM phieu_tiep_nhan_spa WHERE phieu_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting spa ticket: " + loi.getMessage(), loi);
        }
    }

    /**
     * Đếm trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM phieu_tiep_nhan_spa";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error counting spa tickets: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Tìm các phiếu Spa trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param sql SQL
     * @param thamSo tham số
     * @return kết quả tìm các phiếu Spa
     */
    private List<PhieuTiepNhanSpa> timCacPhieuSpa(String sql, Long thamSo) {
        // Các phiếu Spa.
        List<PhieuTiepNhanSpa> cacPhieuSpa = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, thamSo);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacPhieuSpa.add(anhXaKetQuaDatDenPhieuTiepNhanSpa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding spa tickets: " + loi.getMessage(), loi);
        }

        return cacPhieuSpa;
    }

    /**
     * Ánh xạ kết quả đặt đến phiếu tiếp nhận Spa trong JdbcPhieuTiepNhanSpaDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến phiếu tiếp nhận Spa
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private PhieuTiepNhanSpa anhXaKetQuaDatDenPhieuTiepNhanSpa(ResultSet tapKetQua)
            throws SQLException {
        // Phiếu Spa.
        PhieuTiepNhanSpa phieuSpa = new PhieuTiepNhanSpa();
        phieuSpa.datId(tapKetQua.getLong("phieu_id"));
        phieuSpa.datMaPhieuSpa(tapKetQua.getString("ma_phieu"));
        phieuSpa.datIdThuCung(tapKetQua.getLong("thu_cung_id"));
        phieuSpa.datIdKhachHang(tapKetQua.getLong("khach_hang_id"));
        phieuSpa.datIdGoiDichVu(tapKetQua.getLong("goi_id"));

        // Id nhân viên.
        long idNhanVien = tapKetQua.getLong("nhan_vien_id");
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (!tapKetQua.wasNull()) {
            phieuSpa.datIdNhanVien(idNhanVien);
        }

        // Thời gian nhận phòng.
        Timestamp thoiGianNhanPhong = tapKetQua.getTimestamp("gio_nhan");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiGianNhanPhong != null) {
            phieuSpa.datThoiGianNhanPhong(thoiGianNhanPhong.toLocalDateTime());
        }

        // Thời gian trả phòng.
        Timestamp thoiGianTraPhong = tapKetQua.getTimestamp("gio_tra");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiGianTraPhong != null) {
            phieuSpa.datThoiGianTraPhong(thoiGianTraPhong.toLocalDateTime());
        }

        phieuSpa.datGiaCoSo(tapKetQua.getBigDecimal("gia_goi"));
        phieuSpa.datTienPhuThu(tapKetQua.getBigDecimal("phi_phu_thu"));
        phieuSpa.datTongTien(tapKetQua.getBigDecimal("tong_tien"));
        phieuSpa.datTrangThai(tapKetQua.getString("trang_thai"));
        phieuSpa.datGhiChu(tapKetQua.getString("ghi_chu"));

        // Thời điểm tạo.
        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_tao");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemTao != null) {
            phieuSpa.datThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        phieuSpa.datPhienBanBanGhi(tapKetQua.getBytes("rowversion"));

        return phieuSpa;
    }
}
