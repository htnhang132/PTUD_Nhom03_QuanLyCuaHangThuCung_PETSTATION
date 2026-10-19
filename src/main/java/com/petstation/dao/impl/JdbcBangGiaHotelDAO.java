package com.petstation.dao.impl;

import com.petstation.dao.BangGiaHotelDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.BangGiaHotel;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC Hotel bảng giá DAO. */
public class JdbcBangGiaHotelDAO implements BangGiaHotelDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcBangGiaHotelDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcBangGiaHotelDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcBangGiaHotelDAO.
     *
     * @param bangGia bảng giá
     * @return kết quả tạo
     */
    @Override
    public BangGiaHotel tao(BangGiaHotel bangGia) {
        // SQL.
        String sql =
                "INSERT INTO bang_gia_hotel (phong_id, khung_gio, can_nang_tu, can_nang_den, "
                        + "don_gia, ngay_ap_dung, ngay_het_hieu_luc) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setLong(1, bangGia.layIdPhong());
            cauLenh.setString(2, bangGia.layKhungThoiGian());
            cauLenh.setBigDecimal(3, bangGia.layCanNangTu());
            cauLenh.setBigDecimal(4, bangGia.layCanNangDen());
            cauLenh.setBigDecimal(5, bangGia.layGia());
            cauLenh.setTimestamp(
                    6,
                    Timestamp.valueOf(
                            bangGia.layNgayHieuLuc() != null
                                    ? bangGia.layNgayHieuLuc()
                                    : LocalDateTime.now()));
            cauLenh.setTimestamp(
                    7,
                    bangGia.layHanSuDung() != null
                            ? Timestamp.valueOf(bangGia.layHanSuDung())
                            : null);

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    bangGia.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return bangGia;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error creating hotel pricing: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcBangGiaHotelDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<BangGiaHotel> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia,"
                        + " ngay_ap_dung, ngay_het_hieu_luc, rowversion FROM bang_gia_hotel WHERE"
                        + " gia_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenHotelBangGia(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding hotel pricing: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo id phòng trong JdbcBangGiaHotelDAO.
     *
     * @param idPhong id phòng
     * @return kết quả tìm theo id phòng
     */
    @Override
    public List<BangGiaHotel> timTheoIdPhong(Long idPhong) {
        // SQL.
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia, "
                        + "ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_hotel WHERE phong_id = ? ORDER BY khung_gio, can_nang_tu";

        // Bảng giá danh sách.
        List<BangGiaHotel> bangGiaDanhSach = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idPhong);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    bangGiaDanhSach.add(anhXaKetQuaDatDenHotelBangGia(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding pricing by room: " + loi.getMessage(), loi);
        }

        return bangGiaDanhSach;
    }

    /**
     * Tìm theo khung thời gian trong JdbcBangGiaHotelDAO.
     *
     * @param khungThoiGian khung thời gian
     * @return kết quả tìm theo khung thời gian
     */
    @Override
    public List<BangGiaHotel> timTheoKhungThoiGian(String khungThoiGian) {
        // SQL.
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia, "
                        + "ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_hotel WHERE khung_gio = ? ORDER BY phong_id, can_nang_tu";

        // Bảng giá danh sách.
        List<BangGiaHotel> bangGiaDanhSach = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, khungThoiGian);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    bangGiaDanhSach.add(anhXaKetQuaDatDenHotelBangGia(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding pricing by time frame: " + loi.getMessage(), loi);
        }

        return bangGiaDanhSach;
    }

    /**
     * Tìm theo phòng khung thời gian và cân nặng trong JdbcBangGiaHotelDAO.
     *
     * @param idPhong id phòng
     * @param khungThoiGian khung thời gian
     * @param canNang cân nặng
     * @return kết quả tìm theo phòng khung thời gian và cân nặng
     */
    @Override
    public Optional<BangGiaHotel> timTheoPhongKhungThoiGianVaCanNang(
            Long idPhong, String khungThoiGian, BigDecimal canNang) {
        // SQL.
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia, "
                        + "ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_hotel WHERE phong_id = ? AND khung_gio = ? "
                        + "AND can_nang_tu <= ? AND can_nang_den > ? "
                        + "AND (ngay_het_hieu_luc IS NULL OR ngay_het_hieu_luc > GETDATE())";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idPhong);
            cauLenh.setString(2, khungThoiGian);
            cauLenh.setBigDecimal(3, canNang);
            cauLenh.setBigDecimal(4, canNang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenHotelBangGia(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding pricing by criteria: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm tất cả hoạt động trong JdbcBangGiaHotelDAO.
     *
     * @return kết quả tìm tất cả hoạt động
     */
    @Override
    public List<BangGiaHotel> timTatCaHoatDong() {
        // SQL.
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia,"
                        + " ngay_ap_dung, ngay_het_hieu_luc, rowversion FROM bang_gia_hotel WHERE"
                        + " ngay_het_hieu_luc IS NULL OR ngay_het_hieu_luc > GETDATE() ORDER BY"
                        + " phong_id, khung_gio, can_nang_tu";

        // Bảng giá danh sách.
        List<BangGiaHotel> bangGiaDanhSach = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                bangGiaDanhSach.add(anhXaKetQuaDatDenHotelBangGia(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding active pricing: " + loi.getMessage(), loi);
        }

        return bangGiaDanhSach;
    }

    /**
     * Tìm tất cả trong JdbcBangGiaHotelDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<BangGiaHotel> timTatCa() {
        // SQL.
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia, "
                        + "ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_hotel ORDER BY phong_id, khung_gio, can_nang_tu";

        // Bảng giá danh sách.
        List<BangGiaHotel> bangGiaDanhSach = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                bangGiaDanhSach.add(anhXaKetQuaDatDenHotelBangGia(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding all pricing: " + loi.getMessage(), loi);
        }

        return bangGiaDanhSach;
    }

    /**
     * Cập nhật trong JdbcBangGiaHotelDAO.
     *
     * @param bangGia bảng giá
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(BangGiaHotel bangGia) {
        // SQL.
        String sql =
                "UPDATE bang_gia_hotel SET don_gia = ?, ngay_het_hieu_luc = ? WHERE gia_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setBigDecimal(1, bangGia.layGia());
            cauLenh.setTimestamp(
                    2,
                    bangGia.layHanSuDung() != null
                            ? Timestamp.valueOf(bangGia.layHanSuDung())
                            : null);
            cauLenh.setLong(3, bangGia.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating hotel pricing: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật giá trong JdbcBangGiaHotelDAO.
     *
     * @param idBangGia id bảng giá
     * @param giaMoi giá mới
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatGia(Long idBangGia, BigDecimal giaMoi) {
        // SQL.
        String sql = "UPDATE bang_gia_hotel SET don_gia = ? WHERE gia_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setBigDecimal(1, giaMoi);
            cauLenh.setLong(2, idBangGia);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating price: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcBangGiaHotelDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM bang_gia_hotel WHERE gia_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error deleting hotel pricing: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa theo id phòng trong JdbcBangGiaHotelDAO.
     *
     * @param idPhong id phòng
     * @return kết quả xóa theo id phòng
     */
    @Override
    public int xoaTheoIdPhong(Long idPhong) {
        // SQL.
        String sql = "DELETE FROM bang_gia_hotel WHERE phong_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idPhong);
            return cauLenh.executeUpdate();
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error deleting pricing by room: " + loi.getMessage(), loi);
        }
    }

    /**
     * Đếm trong JdbcBangGiaHotelDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM bang_gia_hotel";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error counting hotel pricing: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến Hotel bảng giá trong JdbcBangGiaHotelDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến Hotel bảng giá
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private BangGiaHotel anhXaKetQuaDatDenHotelBangGia(ResultSet tapKetQua) throws SQLException {
        // Bảng giá.
        BangGiaHotel bangGia = new BangGiaHotel();
        bangGia.datId(tapKetQua.getLong("gia_id"));
        bangGia.datIdPhong(tapKetQua.getLong("phong_id"));
        bangGia.datKhungThoiGian(tapKetQua.getString("khung_gio"));
        bangGia.datCanNangTu(tapKetQua.getBigDecimal("can_nang_tu"));
        bangGia.datCanNangDen(tapKetQua.getBigDecimal("can_nang_den"));
        bangGia.datGia(tapKetQua.getBigDecimal("don_gia"));

        // Ngày hiệu lực.
        Timestamp ngayHieuLuc = tapKetQua.getTimestamp("ngay_ap_dung");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayHieuLuc != null) {
            bangGia.datNgayHieuLuc(ngayHieuLuc.toLocalDateTime());
        }

        // Hạn sử dụng.
        Timestamp hanSuDung = tapKetQua.getTimestamp("ngay_het_hieu_luc");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (hanSuDung != null) {
            bangGia.datHanSuDung(hanSuDung.toLocalDateTime());
        }

        bangGia.datPhienBanBanGhi(tapKetQua.getBytes("rowversion"));

        return bangGia;
    }
}
