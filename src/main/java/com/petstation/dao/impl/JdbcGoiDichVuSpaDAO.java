package com.petstation.dao.impl;

import com.petstation.dao.GoiDichVuSpaDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.GoiDichVuSpa;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC gói dịch vụ Spa DAO. */
public class JdbcGoiDichVuSpaDAO implements GoiDichVuSpaDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcGoiDichVuSpaDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcGoiDichVuSpaDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcGoiDichVuSpaDAO.
     *
     * @param goiDichVuSpa gói dịch vụ Spa
     * @return kết quả tạo
     */
    @Override
    public GoiDichVuSpa tao(GoiDichVuSpa goiDichVuSpa) {
        // SQL.
        String sql =
                "INSERT INTO goi_dich_vu_spa (ma_goi, ten_goi, mo_ta, loai_goi, trang_thai,"
                        + " ngay_tao) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setString(1, goiDichVuSpa.layMaGoiDichVu());
            cauLenh.setString(2, goiDichVuSpa.layTenGoiDichVu());
            cauLenh.setString(3, goiDichVuSpa.layMoTa());
            cauLenh.setString(4, goiDichVuSpa.layLoaiGoiDichVu());
            cauLenh.setString(
                    5,
                    goiDichVuSpa.layTrangThai() != null ? goiDichVuSpa.layTrangThai() : "ACTIVE");
            cauLenh.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            // Bị ảnh hưởng các dòng.
            int biAnhHuongCacDong = cauLenh.executeUpdate();
            // Kiểm tra số bản ghi chịu tác động để phát hiện cập nhật thất bại.
            if (biAnhHuongCacDong == 0) {
                throw new TruyCapDuLieuException("Creating spa package failed, no rows affected.");
            }

            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    goiDichVuSpa.datId(duocSinhCacKhoa.getLong(1));
                } else {
                    throw new TruyCapDuLieuException(
                            "Creating spa package failed, no ID obtained.");
                }
            }

            return goiDichVuSpa;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error creating spa package: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcGoiDichVuSpaDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<GoiDichVuSpa> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT goi_id, ma_goi, ten_goi, mo_ta, loai_goi, trang_thai, ngay_tao, rowversion "
                        + "FROM goi_dich_vu_spa WHERE goi_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenGoiDichVuSpa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding spa package by ID: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo mã gói dịch vụ trong JdbcGoiDichVuSpaDAO.
     *
     * @param maGoiDichVu mã gói dịch vụ
     * @return kết quả tìm theo mã gói dịch vụ
     */
    @Override
    public Optional<GoiDichVuSpa> timTheoMaGoiDichVu(String maGoiDichVu) {
        // SQL.
        String sql =
                "SELECT goi_id, ma_goi, ten_goi, mo_ta, loai_goi, trang_thai, ngay_tao, rowversion "
                        + "FROM goi_dich_vu_spa WHERE ma_goi = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maGoiDichVu);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenGoiDichVuSpa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding spa package by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo loại gói dịch vụ trong JdbcGoiDichVuSpaDAO.
     *
     * @param loaiGoiDichVu loại gói dịch vụ
     * @return kết quả tìm theo loại gói dịch vụ
     */
    @Override
    public List<GoiDichVuSpa> timTheoLoaiGoiDichVu(String loaiGoiDichVu) {
        // SQL.
        String sql =
                "SELECT goi_id, ma_goi, ten_goi, mo_ta, loai_goi, trang_thai, ngay_tao, rowversion "
                        + "FROM goi_dich_vu_spa WHERE loai_goi = ? ORDER BY ten_goi";

        // Các gói dịch vụ.
        List<GoiDichVuSpa> cacGoiDichVu = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, loaiGoiDichVu);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacGoiDichVu.add(anhXaKetQuaDatDenGoiDichVuSpa(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding spa packages by type: " + loi.getMessage(), loi);
        }

        return cacGoiDichVu;
    }

    /**
     * Tìm tất cả hoạt động trong JdbcGoiDichVuSpaDAO.
     *
     * @return kết quả tìm tất cả hoạt động
     */
    @Override
    public List<GoiDichVuSpa> timTatCaHoatDong() {
        // SQL.
        String sql =
                "SELECT goi_id, ma_goi, ten_goi, mo_ta, loai_goi, trang_thai, ngay_tao, rowversion "
                        + "FROM goi_dich_vu_spa WHERE trang_thai = 'ACTIVE' ORDER BY ten_goi";

        // Các gói dịch vụ.
        List<GoiDichVuSpa> cacGoiDichVu = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacGoiDichVu.add(anhXaKetQuaDatDenGoiDichVuSpa(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding active spa packages: " + loi.getMessage(), loi);
        }

        return cacGoiDichVu;
    }

    /**
     * Tìm tất cả trong JdbcGoiDichVuSpaDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<GoiDichVuSpa> timTatCa() {
        // SQL.
        String sql =
                "SELECT goi_id, ma_goi, ten_goi, mo_ta, loai_goi, trang_thai, ngay_tao, rowversion "
                        + "FROM goi_dich_vu_spa ORDER BY ngay_tao DESC";

        // Các gói dịch vụ.
        List<GoiDichVuSpa> cacGoiDichVu = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacGoiDichVu.add(anhXaKetQuaDatDenGoiDichVuSpa(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all spa packages: " + loi.getMessage(), loi);
        }

        return cacGoiDichVu;
    }

    /**
     * Cập nhật trong JdbcGoiDichVuSpaDAO.
     *
     * @param goiDichVuSpa gói dịch vụ Spa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(GoiDichVuSpa goiDichVuSpa) {
        // SQL.
        String sql =
                "UPDATE goi_dich_vu_spa SET ten_goi = ?, mo_ta = ?, loai_goi = ?, trang_thai = ? "
                        + "WHERE goi_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, goiDichVuSpa.layTenGoiDichVu());
            cauLenh.setString(2, goiDichVuSpa.layMoTa());
            cauLenh.setString(3, goiDichVuSpa.layLoaiGoiDichVu());
            cauLenh.setString(4, goiDichVuSpa.layTrangThai());
            cauLenh.setLong(5, goiDichVuSpa.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating spa package: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật trạng thái trong JdbcGoiDichVuSpaDAO.
     *
     * @param idGoiDichVu id gói dịch vụ
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatTrangThai(Long idGoiDichVu, String trangThai) {
        // SQL.
        String sql = "UPDATE goi_dich_vu_spa SET trang_thai = ? WHERE goi_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            cauLenh.setLong(2, idGoiDichVu);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating spa package status: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcGoiDichVuSpaDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM goi_dich_vu_spa WHERE goi_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error deleting spa package: " + loi.getMessage(), loi);
        }
    }

    /**
     * Lấy mã tồn tại theo gói dịch vụ trong JdbcGoiDichVuSpaDAO.
     *
     * @param maGoiDichVu mã gói dịch vụ
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean layMaTonTaiTheoGoiDichVu(String maGoiDichVu) {
        // SQL.
        String sql = "SELECT COUNT(*) FROM goi_dich_vu_spa WHERE ma_goi = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maGoiDichVu);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getInt(1) > 0;
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error checking spa package existence: " + loi.getMessage(), loi);
        }

        return false;
    }

    /**
     * Đếm trong JdbcGoiDichVuSpaDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM goi_dich_vu_spa";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error counting spa packages: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến gói dịch vụ Spa trong JdbcGoiDichVuSpaDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến gói dịch vụ Spa
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private GoiDichVuSpa anhXaKetQuaDatDenGoiDichVuSpa(ResultSet tapKetQua) throws SQLException {
        // Gói dịch vụ Spa.
        GoiDichVuSpa goiDichVuSpa = new GoiDichVuSpa();
        goiDichVuSpa.datId(tapKetQua.getLong("goi_id"));
        goiDichVuSpa.datMaGoiDichVu(tapKetQua.getString("ma_goi"));
        goiDichVuSpa.datTenGoiDichVu(tapKetQua.getString("ten_goi"));
        goiDichVuSpa.datMoTa(tapKetQua.getString("mo_ta"));
        goiDichVuSpa.datLoaiGoiDichVu(tapKetQua.getString("loai_goi"));
        goiDichVuSpa.datTrangThai(tapKetQua.getString("trang_thai"));

        // Thời điểm tạo.
        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_tao");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemTao != null) {
            goiDichVuSpa.datThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        goiDichVuSpa.datPhienBanBanGhi(tapKetQua.getBytes("rowversion"));

        return goiDichVuSpa;
    }
}
