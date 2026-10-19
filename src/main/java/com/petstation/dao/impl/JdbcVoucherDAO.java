package com.petstation.dao.impl;

import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.dao.VoucherDAO;
import com.petstation.model.Voucher;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC voucher DAO. */
public class JdbcVoucherDAO implements VoucherDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcVoucherDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcVoucherDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcVoucherDAO.
     *
     * @param voucher voucher
     * @return kết quả tạo
     */
    @Override
    public Voucher tao(Voucher voucher) {
        // SQL.
        String sql =
                "INSERT INTO voucher (ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam,"
                    + " loai_giam, don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den,"
                    + " gioi_han_su_dung, da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem,"
                    + " ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setString(1, voucher.layMaVoucher());
            cauLenh.setObject(2, voucher.layIdKhachHang());
            cauLenh.setString(3, voucher.layLoaiVoucher());
            cauLenh.setBigDecimal(4, voucher.layGiaTriGiamGia());
            cauLenh.setString(5, voucher.layKieuGiamGia());
            cauLenh.setBigDecimal(6, voucher.layGiaTriDonHangToiThieu());
            cauLenh.setBigDecimal(7, voucher.laySoTienGiamToiDa());
            cauLenh.setTimestamp(
                    8,
                    voucher.layNgayBatDauHieuLuc() != null
                            ? Timestamp.valueOf(voucher.layNgayBatDauHieuLuc())
                            : null);
            cauLenh.setTimestamp(
                    9,
                    voucher.layNgayHetHieuLuc() != null
                            ? Timestamp.valueOf(voucher.layNgayHetHieuLuc())
                            : null);
            cauLenh.setObject(10, voucher.layGioiHanSuDung());
            cauLenh.setInt(11, voucher.laySoLuongDaDung() != null ? voucher.laySoLuongDaDung() : 0);
            cauLenh.setString(
                    12, voucher.layTrangThai() != null ? voucher.layTrangThai() : "ACTIVE");
            cauLenh.setBoolean(
                    13, voucher.layApDungSpaChi() != null ? voucher.layApDungSpaChi() : false);
            cauLenh.setTimestamp(
                    14,
                    voucher.layNgaySuDung() != null
                            ? Timestamp.valueOf(voucher.layNgaySuDung())
                            : null);
            cauLenh.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now()));

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    voucher.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return voucher;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error creating voucher: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcVoucherDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<Voucher> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam,"
                    + " don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den,"
                    + " gioi_han_su_dung, da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem,"
                    + " ngay_tao, row_version FROM voucher WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenVoucher(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding voucher: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo mã voucher trong JdbcVoucherDAO.
     *
     * @param maVoucher mã voucher
     * @return kết quả tìm theo mã voucher
     */
    @Override
    public Optional<Voucher> timTheoMaVoucher(String maVoucher) {
        // SQL.
        String sql =
                "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam,"
                    + " don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den,"
                    + " gioi_han_su_dung, da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem,"
                    + " ngay_tao, row_version FROM voucher WHERE ma_voucher = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maVoucher);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenVoucher(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding voucher by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo id khách hàng trong JdbcVoucherDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả tìm theo id khách hàng
     */
    @Override
    public List<Voucher> timTheoIdKhachHang(Long idKhachHang) {
        // SQL.
        String sql =
                "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam,"
                    + " don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den,"
                    + " gioi_han_su_dung, da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem,"
                    + " ngay_tao, row_version FROM voucher WHERE khach_hang_id = ? ORDER BY"
                    + " ngay_tao DESC";

        // Các voucher.
        List<Voucher> cacVoucher = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idKhachHang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacVoucher.add(anhXaKetQuaDatDenVoucher(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding vouchers by customer: " + loi.getMessage(), loi);
        }

        return cacVoucher;
    }

    /**
     * Tìm theo loại voucher trong JdbcVoucherDAO.
     *
     * @param loaiVoucher loại voucher
     * @return kết quả tìm theo loại voucher
     */
    @Override
    public List<Voucher> timTheoLoaiVoucher(String loaiVoucher) {
        // SQL.
        String sql =
                "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam,"
                    + " don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den,"
                    + " gioi_han_su_dung, da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem,"
                    + " ngay_tao, row_version FROM voucher WHERE loai_voucher = ? ORDER BY ngay_tao"
                    + " DESC";

        // Các voucher.
        List<Voucher> cacVoucher = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, loaiVoucher);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacVoucher.add(anhXaKetQuaDatDenVoucher(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding vouchers by type: " + loi.getMessage(), loi);
        }

        return cacVoucher;
    }

    /**
     * Tìm theo trạng thái trong JdbcVoucherDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    @Override
    public List<Voucher> timTheoTrangThai(String trangThai) {
        // SQL.
        String sql =
                "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam,"
                    + " don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den,"
                    + " gioi_han_su_dung, da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem,"
                    + " ngay_tao, row_version FROM voucher WHERE trang_thai = ? ORDER BY ngay_tao"
                    + " DESC";

        // Các voucher.
        List<Voucher> cacVoucher = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacVoucher.add(anhXaKetQuaDatDenVoucher(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding vouchers by status: " + loi.getMessage(), loi);
        }

        return cacVoucher;
    }

    /**
     * Tìm hoạt động các voucher trong JdbcVoucherDAO.
     *
     * @return kết quả tìm hoạt động các voucher
     */
    @Override
    public List<Voucher> timHoatDongCacVoucher() {
        // SQL.
        String sql =
                "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam,"
                    + " don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den,"
                    + " gioi_han_su_dung, da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem,"
                    + " ngay_tao, row_version FROM voucher WHERE trang_thai = 'ACTIVE' AND"
                    + " hieu_luc_tu <= GETDATE() AND hieu_luc_den >= GETDATE() AND"
                    + " (gioi_han_su_dung IS NULL OR da_su_dung < gioi_han_su_dung) ORDER BY"
                    + " ngay_tao DESC";

        // Các voucher.
        List<Voucher> cacVoucher = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacVoucher.add(anhXaKetQuaDatDenVoucher(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding active vouchers: " + loi.getMessage(), loi);
        }

        return cacVoucher;
    }

    /**
     * Tìm hết hạn các voucher trong JdbcVoucherDAO.
     *
     * @return kết quả tìm hết hạn các voucher
     */
    @Override
    public List<Voucher> timHetHanCacVoucher() {
        // SQL.
        String sql =
                "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam,"
                    + " don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den,"
                    + " gioi_han_su_dung, da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem,"
                    + " ngay_tao, row_version FROM voucher WHERE hieu_luc_den < GETDATE() ORDER BY"
                    + " hieu_luc_den DESC";

        // Các voucher.
        List<Voucher> cacVoucher = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacVoucher.add(anhXaKetQuaDatDenVoucher(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding expired vouchers: " + loi.getMessage(), loi);
        }

        return cacVoucher;
    }

    /**
     * Tìm Spa chỉ các voucher trong JdbcVoucherDAO.
     *
     * @return kết quả tìm Spa chỉ các voucher
     */
    @Override
    public List<Voucher> timSpaChiCacVoucher() {
        // SQL.
        String sql =
                "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam,"
                    + " don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den,"
                    + " gioi_han_su_dung, da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem,"
                    + " ngay_tao, row_version FROM voucher WHERE chi_ap_dung_spa = 1 ORDER BY"
                    + " ngay_tao DESC";

        // Các voucher.
        List<Voucher> cacVoucher = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacVoucher.add(anhXaKetQuaDatDenVoucher(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding spa-only vouchers: " + loi.getMessage(), loi);
        }

        return cacVoucher;
    }

    /**
     * Tìm tất cả trong JdbcVoucherDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<Voucher> timTatCa() {
        // SQL.
        String sql =
                "SELECT id, ma_voucher, khach_hang_id, loai_voucher, gia_tri_giam, loai_giam,"
                    + " don_hang_toi_thieu, giam_toi_da, hieu_luc_tu, hieu_luc_den,"
                    + " gioi_han_su_dung, da_su_dung, trang_thai, chi_ap_dung_spa, ngay_doi_diem,"
                    + " ngay_tao, row_version FROM voucher ORDER BY ngay_tao DESC";

        // Các voucher.
        List<Voucher> cacVoucher = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacVoucher.add(anhXaKetQuaDatDenVoucher(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all vouchers: " + loi.getMessage(), loi);
        }

        return cacVoucher;
    }

    /**
     * Cập nhật trong JdbcVoucherDAO.
     *
     * @param voucher voucher
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(Voucher voucher) {
        // SQL.
        String sql = "UPDATE voucher SET trang_thai = ?, da_su_dung = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, voucher.layTrangThai());
            cauLenh.setInt(2, voucher.laySoLuongDaDung());
            cauLenh.setLong(3, voucher.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating voucher: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật trạng thái trong JdbcVoucherDAO.
     *
     * @param idVoucher id voucher
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatTrangThai(Long idVoucher, String trangThai) {
        // SQL.
        String sql = "UPDATE voucher SET trang_thai = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            cauLenh.setLong(2, idVoucher);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating voucher status: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tăng số lần sử dụng trong JdbcVoucherDAO.
     *
     * @param idVoucher id voucher
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean tangSoLanSuDung(Long idVoucher) {
        // SQL.
        String sql = "UPDATE voucher SET da_su_dung = da_su_dung + 1 WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idVoucher);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error incrementing voucher usage: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcVoucherDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM voucher WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting voucher: " + loi.getMessage(), loi);
        }
    }

    /**
     * Lấy mã tồn tại theo voucher trong JdbcVoucherDAO.
     *
     * @param maVoucher mã voucher
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean layMaTonTaiTheoVoucher(String maVoucher) {
        // SQL.
        String sql = "SELECT COUNT(*) FROM voucher WHERE ma_voucher = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maVoucher);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getInt(1) > 0;
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error checking voucher existence: " + loi.getMessage(), loi);
        }

        return false;
    }

    /**
     * Đếm trong JdbcVoucherDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM voucher";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting vouchers: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến voucher trong JdbcVoucherDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến voucher
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private Voucher anhXaKetQuaDatDenVoucher(ResultSet tapKetQua) throws SQLException {
        // Voucher.
        Voucher voucher = new Voucher();
        voucher.datId(tapKetQua.getLong("id"));
        voucher.datMaVoucher(tapKetQua.getString("ma_voucher"));

        // Id khách hàng.
        long idKhachHang = tapKetQua.getLong("khach_hang_id");
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (!tapKetQua.wasNull()) {
            voucher.datIdKhachHang(idKhachHang);
        }

        voucher.datLoaiVoucher(tapKetQua.getString("loai_voucher"));
        voucher.datGiaTriGiamGia(tapKetQua.getBigDecimal("gia_tri_giam"));
        voucher.datKieuGiamGia(tapKetQua.getString("loai_giam"));
        voucher.datGiaTriDonHangToiThieu(tapKetQua.getBigDecimal("don_hang_toi_thieu"));
        voucher.datSoTienGiamToiDa(tapKetQua.getBigDecimal("giam_toi_da"));

        // Ngày bắt đầu hiệu lực.
        Timestamp ngayBatDauHieuLuc = tapKetQua.getTimestamp("hieu_luc_tu");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayBatDauHieuLuc != null) {
            voucher.datNgayBatDauHieuLuc(ngayBatDauHieuLuc.toLocalDateTime());
        }

        // Ngày hết hiệu lực.
        Timestamp ngayHetHieuLuc = tapKetQua.getTimestamp("hieu_luc_den");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayHetHieuLuc != null) {
            voucher.datNgayHetHieuLuc(ngayHetHieuLuc.toLocalDateTime());
        }

        // Giới hạn sử dụng.
        Integer gioiHanSuDung = (Integer) tapKetQua.getObject("gioi_han_su_dung");
        voucher.datGioiHanSuDung(gioiHanSuDung);

        voucher.datSoLuongDaDung(tapKetQua.getInt("da_su_dung"));
        voucher.datTrangThai(tapKetQua.getString("trang_thai"));
        voucher.datApDungSpaChi(tapKetQua.getBoolean("chi_ap_dung_spa"));

        // Ngày sử dụng.
        Timestamp ngaySuDung = tapKetQua.getTimestamp("ngay_doi_diem");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngaySuDung != null) {
            voucher.datNgaySuDung(ngaySuDung.toLocalDateTime());
        }

        // Thời điểm tạo.
        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_tao");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemTao != null) {
            voucher.datThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        voucher.datPhienBanBanGhi(tapKetQua.getBytes("row_version"));

        return voucher;
    }
}
