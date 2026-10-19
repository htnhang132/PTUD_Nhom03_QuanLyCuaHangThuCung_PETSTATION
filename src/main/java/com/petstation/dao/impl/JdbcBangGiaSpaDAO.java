package com.petstation.dao.impl;

import com.petstation.dao.BangGiaSpaDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.BangGiaSpa;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC Spa giá ma trận DAO. */
public class JdbcBangGiaSpaDAO implements BangGiaSpaDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcBangGiaSpaDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcBangGiaSpaDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcBangGiaSpaDAO.
     *
     * @param giaMaTran giá ma trận
     * @return kết quả tạo
     */
    @Override
    public BangGiaSpa tao(BangGiaSpa giaMaTran) {
        // SQL.
        String sql =
                "INSERT INTO bang_gia_spa (goi_id, loai_thu_cung, can_nang_tu, can_nang_den, "
                        + "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setLong(1, giaMaTran.layIdGoiDichVu());
            cauLenh.setString(2, giaMaTran.layLoai());
            cauLenh.setBigDecimal(3, giaMaTran.layCanNangTu());
            cauLenh.setBigDecimal(4, giaMaTran.layCanNangDen());
            cauLenh.setBigDecimal(5, giaMaTran.layGia());
            cauLenh.setObject(6, giaMaTran.layThoiLuongDuKien());
            cauLenh.setTimestamp(
                    7,
                    Timestamp.valueOf(
                            giaMaTran.layNgayHieuLuc() != null
                                    ? giaMaTran.layNgayHieuLuc()
                                    : LocalDateTime.now()));
            cauLenh.setTimestamp(
                    8,
                    giaMaTran.layHanSuDung() != null
                            ? Timestamp.valueOf(giaMaTran.layHanSuDung())
                            : null);

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    giaMaTran.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return giaMaTran;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error creating spa price matrix: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcBangGiaSpaDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<BangGiaSpa> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, "
                        + "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_spa WHERE gia_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenSpaGiaMaTran(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding spa price matrix: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo id gói dịch vụ trong JdbcBangGiaSpaDAO.
     *
     * @param idGoiDichVu id gói dịch vụ
     * @return kết quả tìm theo id gói dịch vụ
     */
    @Override
    public List<BangGiaSpa> timTheoIdGoiDichVu(Long idGoiDichVu) {
        // SQL.
        String sql =
                "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, "
                        + "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_spa WHERE goi_id = ? ORDER BY loai_thu_cung, can_nang_tu";

        // Các ma trận.
        List<BangGiaSpa> cacMaTran = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idGoiDichVu);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacMaTran.add(anhXaKetQuaDatDenSpaGiaMaTran(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding price matrices: " + loi.getMessage(), loi);
        }

        return cacMaTran;
    }

    /**
     * Tìm theo gói dịch vụ loài và cân nặng trong JdbcBangGiaSpaDAO.
     *
     * @param idGoiDichVu id gói dịch vụ
     * @param loai loài
     * @param canNang cân nặng
     * @return kết quả tìm theo gói dịch vụ loài và cân nặng
     */
    @Override
    public Optional<BangGiaSpa> timTheoGoiDichVuLoaiVaCanNang(
            Long idGoiDichVu, String loai, BigDecimal canNang) {
        // SQL.
        String sql =
                "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, "
                        + "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_spa WHERE goi_id = ? AND loai_thu_cung = ? "
                        + "AND can_nang_tu <= ? AND can_nang_den > ? "
                        + "AND (ngay_het_hieu_luc IS NULL OR ngay_het_hieu_luc > GETDATE())";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idGoiDichVu);
            cauLenh.setString(2, loai);
            cauLenh.setBigDecimal(3, canNang);
            cauLenh.setBigDecimal(4, canNang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenSpaGiaMaTran(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding price by weight: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm tất cả hoạt động trong JdbcBangGiaSpaDAO.
     *
     * @return kết quả tìm tất cả hoạt động
     */
    @Override
    public List<BangGiaSpa> timTatCaHoatDong() {
        // SQL.
        String sql =
                "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, don_gia,"
                        + " thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion FROM"
                        + " bang_gia_spa WHERE ngay_het_hieu_luc IS NULL OR ngay_het_hieu_luc >"
                        + " GETDATE() ORDER BY goi_id, loai_thu_cung, can_nang_tu";

        // Các ma trận.
        List<BangGiaSpa> cacMaTran = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacMaTran.add(anhXaKetQuaDatDenSpaGiaMaTran(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding active price matrices: " + loi.getMessage(), loi);
        }

        return cacMaTran;
    }

    /**
     * Tìm theo loài trong JdbcBangGiaSpaDAO.
     *
     * @param loai loài
     * @return kết quả tìm theo loài
     */
    @Override
    public List<BangGiaSpa> timTheoLoai(String loai) {
        // SQL.
        String sql =
                "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, "
                        + "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_spa WHERE loai_thu_cung = ? ORDER BY goi_id, can_nang_tu";

        // Các ma trận.
        List<BangGiaSpa> cacMaTran = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, loai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacMaTran.add(anhXaKetQuaDatDenSpaGiaMaTran(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding price matrices by species: " + loi.getMessage(), loi);
        }

        return cacMaTran;
    }

    /**
     * Tìm tất cả trong JdbcBangGiaSpaDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<BangGiaSpa> timTatCa() {
        // SQL.
        String sql =
                "SELECT gia_id, goi_id, loai_thu_cung, can_nang_tu, can_nang_den, "
                        + "don_gia, thoi_gian_du_kien, ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_spa ORDER BY goi_id, loai_thu_cung, can_nang_tu";

        // Các ma trận.
        List<BangGiaSpa> cacMaTran = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacMaTran.add(anhXaKetQuaDatDenSpaGiaMaTran(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all price matrices: " + loi.getMessage(), loi);
        }

        return cacMaTran;
    }

    /**
     * Cập nhật trong JdbcBangGiaSpaDAO.
     *
     * @param giaMaTran giá ma trận
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(BangGiaSpa giaMaTran) {
        // SQL.
        String sql =
                "UPDATE bang_gia_spa SET don_gia = ?, thoi_gian_du_kien = ?, "
                        + "ngay_het_hieu_luc = ? WHERE gia_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setBigDecimal(1, giaMaTran.layGia());
            cauLenh.setObject(2, giaMaTran.layThoiLuongDuKien());
            cauLenh.setTimestamp(
                    3,
                    giaMaTran.layHanSuDung() != null
                            ? Timestamp.valueOf(giaMaTran.layHanSuDung())
                            : null);
            cauLenh.setLong(4, giaMaTran.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating spa price matrix: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật giá trong JdbcBangGiaSpaDAO.
     *
     * @param idGiaMaTran id giá ma trận
     * @param giaMoi giá mới
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatGia(Long idGiaMaTran, BigDecimal giaMoi) {
        // SQL.
        String sql = "UPDATE bang_gia_spa SET don_gia = ? WHERE gia_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setBigDecimal(1, giaMoi);
            cauLenh.setLong(2, idGiaMaTran);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating price: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcBangGiaSpaDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM bang_gia_spa WHERE gia_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error deleting spa price matrix: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa theo id gói dịch vụ trong JdbcBangGiaSpaDAO.
     *
     * @param idGoiDichVu id gói dịch vụ
     * @return kết quả xóa theo id gói dịch vụ
     */
    @Override
    public int xoaTheoIdGoiDichVu(Long idGoiDichVu) {
        // SQL.
        String sql = "DELETE FROM bang_gia_spa WHERE goi_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idGoiDichVu);
            return cauLenh.executeUpdate();
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error deleting price matrices: " + loi.getMessage(), loi);
        }
    }

    /**
     * Đếm trong JdbcBangGiaSpaDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM bang_gia_spa";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error counting price matrices: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến Spa giá ma trận trong JdbcBangGiaSpaDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến Spa giá ma trận
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private BangGiaSpa anhXaKetQuaDatDenSpaGiaMaTran(ResultSet tapKetQua) throws SQLException {
        // Ma trận.
        BangGiaSpa maTran = new BangGiaSpa();
        maTran.datId(tapKetQua.getLong("gia_id"));
        maTran.datIdGoiDichVu(tapKetQua.getLong("goi_id"));
        maTran.datLoai(tapKetQua.getString("loai_thu_cung"));
        maTran.datCanNangTu(tapKetQua.getBigDecimal("can_nang_tu"));
        maTran.datCanNangDen(tapKetQua.getBigDecimal("can_nang_den"));
        maTran.datGia(tapKetQua.getBigDecimal("don_gia"));
        maTran.datThoiLuongDuKien((Integer) tapKetQua.getObject("thoi_gian_du_kien"));

        // Ngày hiệu lực.
        Timestamp ngayHieuLuc = tapKetQua.getTimestamp("ngay_ap_dung");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayHieuLuc != null) {
            maTran.datNgayHieuLuc(ngayHieuLuc.toLocalDateTime());
        }

        // Hạn sử dụng.
        Timestamp hanSuDung = tapKetQua.getTimestamp("ngay_het_hieu_luc");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (hanSuDung != null) {
            maTran.datHanSuDung(hanSuDung.toLocalDateTime());
        }

        maTran.datPhienBanBanGhi(tapKetQua.getBytes("rowversion"));

        return maTran;
    }
}
