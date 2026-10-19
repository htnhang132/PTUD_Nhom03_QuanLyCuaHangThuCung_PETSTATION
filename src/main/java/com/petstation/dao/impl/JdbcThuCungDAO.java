package com.petstation.dao.impl;

import com.petstation.dao.ThuCungDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.ThuCung;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC thú cưng DAO. */
public class JdbcThuCungDAO implements ThuCungDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcThuCungDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcThuCungDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcThuCungDAO.
     *
     * @param thuCung thú cưng
     * @return kết quả tạo
     */
    @Override
    public ThuCung tao(ThuCung thuCung) {
        // SQL.
        String sql =
                "INSERT INTO thu_cung (khach_hang_id, ma_thu_cung, ten, loai, giong, "
                        + "gioi_tinh, can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, "
                        + "di_ung, da_tiem_phong, ghi_chu_suc_khoe, ngay_tao) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setLong(1, thuCung.layIdKhachHang());
            cauLenh.setString(2, thuCung.layMaThuCung());
            cauLenh.setString(3, thuCung.layTen());
            cauLenh.setString(4, thuCung.layLoai());
            cauLenh.setString(5, thuCung.layGiong());
            cauLenh.setString(6, thuCung.layGioiTinh());
            cauLenh.setBigDecimal(7, thuCung.layCanNang());
            cauLenh.setDate(
                    8, thuCung.layNgaySinh() != null ? Date.valueOf(thuCung.layNgaySinh()) : null);
            cauLenh.setString(9, thuCung.layMauLong());
            cauLenh.setString(10, thuCung.layTinhCach());
            cauLenh.setString(11, thuCung.layURLAnh());
            cauLenh.setString(12, thuCung.layDiUng());
            cauLenh.setBoolean(
                    13, thuCung.layDaTrietSan() != null ? thuCung.layDaTrietSan() : false);
            cauLenh.setString(14, thuCung.layGhiChuSucKhoe());
            cauLenh.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now()));

            // Bị ảnh hưởng các dòng.
            int biAnhHuongCacDong = cauLenh.executeUpdate();
            // Kiểm tra số bản ghi chịu tác động để phát hiện cập nhật thất bại.
            if (biAnhHuongCacDong == 0) {
                throw new TruyCapDuLieuException("Creating pet failed, no rows affected.");
            }

            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    thuCung.datId(duocSinhCacKhoa.getLong(1));
                } else {
                    throw new TruyCapDuLieuException("Creating pet failed, no ID obtained.");
                }
            }

            return thuCung;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error creating pet: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcThuCungDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<ThuCung> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, "
                        + "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, "
                        + "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version "
                        + "FROM thu_cung WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenThuCung(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding pet by ID: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo mã thú cưng trong JdbcThuCungDAO.
     *
     * @param maThuCung mã thú cưng
     * @return kết quả tìm theo mã thú cưng
     */
    @Override
    public Optional<ThuCung> timTheoMaThuCung(String maThuCung) {
        // SQL.
        String sql =
                "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, "
                        + "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, "
                        + "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version "
                        + "FROM thu_cung WHERE ma_thu_cung = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maThuCung);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenThuCung(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding pet by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo id khách hàng trong JdbcThuCungDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả tìm theo id khách hàng
     */
    @Override
    public List<ThuCung> timTheoIdKhachHang(Long idKhachHang) {
        // SQL.
        String sql =
                "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, "
                        + "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, "
                        + "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version "
                        + "FROM thu_cung WHERE khach_hang_id = ? ORDER BY ten";

        // Các thú cưng.
        List<ThuCung> cacThuCung = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idKhachHang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacThuCung.add(anhXaKetQuaDatDenThuCung(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding pets by customer: " + loi.getMessage(), loi);
        }

        return cacThuCung;
    }

    /**
     * Tìm theo loài trong JdbcThuCungDAO.
     *
     * @param loai loài
     * @return kết quả tìm theo loài
     */
    @Override
    public List<ThuCung> timTheoLoai(String loai) {
        // SQL.
        String sql =
                "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, "
                        + "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, "
                        + "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version "
                        + "FROM thu_cung WHERE loai = ? ORDER BY ten";

        // Các thú cưng.
        List<ThuCung> cacThuCung = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, loai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacThuCung.add(anhXaKetQuaDatDenThuCung(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding pets by species: " + loi.getMessage(), loi);
        }

        return cacThuCung;
    }

    /**
     * Tìm kiếm theo tên trong JdbcThuCungDAO.
     *
     * @param ten tên
     * @return kết quả tìm kiếm theo tên
     */
    @Override
    public List<ThuCung> timKiemTheoTen(String ten) {
        // SQL.
        String sql =
                "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, "
                        + "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, "
                        + "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version "
                        + "FROM thu_cung WHERE ten LIKE ? ORDER BY ten";

        // Các thú cưng.
        List<ThuCung> cacThuCung = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, "%" + ten + "%");
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacThuCung.add(anhXaKetQuaDatDenThuCung(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error searching pets by name: " + loi.getMessage(), loi);
        }

        return cacThuCung;
    }

    /**
     * Tìm tất cả trong JdbcThuCungDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<ThuCung> timTatCa() {
        // SQL.
        String sql =
                "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, "
                        + "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, "
                        + "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version "
                        + "FROM thu_cung ORDER BY ngay_tao DESC";

        // Các thú cưng.
        List<ThuCung> cacThuCung = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacThuCung.add(anhXaKetQuaDatDenThuCung(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding all pets: " + loi.getMessage(), loi);
        }

        return cacThuCung;
    }

    /**
     * Cập nhật trong JdbcThuCungDAO.
     *
     * @param thuCung thú cưng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(ThuCung thuCung) {
        // SQL.
        String sql =
                "UPDATE thu_cung SET ten = ?, loai = ?, giong = ?, gioi_tinh = ?, can_nang = ?,"
                    + " ngay_sinh = ?, mau_long = ?, tinh_cach = ?, hinh_anh_url = ?, di_ung = ?,"
                    + " da_tiem_phong = ?, ghi_chu_suc_khoe = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, thuCung.layTen());
            cauLenh.setString(2, thuCung.layLoai());
            cauLenh.setString(3, thuCung.layGiong());
            cauLenh.setString(4, thuCung.layGioiTinh());
            cauLenh.setBigDecimal(5, thuCung.layCanNang());
            cauLenh.setDate(
                    6, thuCung.layNgaySinh() != null ? Date.valueOf(thuCung.layNgaySinh()) : null);
            cauLenh.setString(7, thuCung.layMauLong());
            cauLenh.setString(8, thuCung.layTinhCach());
            cauLenh.setString(9, thuCung.layURLAnh());
            cauLenh.setString(10, thuCung.layDiUng());
            cauLenh.setBoolean(11, thuCung.layDaTrietSan());
            cauLenh.setString(12, thuCung.layGhiChuSucKhoe());
            cauLenh.setLong(13, thuCung.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating pet: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật cân nặng trong JdbcThuCungDAO.
     *
     * @param idThuCung id thú cưng
     * @param canNang cân nặng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatCanNang(Long idThuCung, java.math.BigDecimal canNang) {
        // SQL.
        String sql = "UPDATE thu_cung SET can_nang = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setBigDecimal(1, canNang);
            cauLenh.setLong(2, idThuCung);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating pet weight: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcThuCungDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM thu_cung WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting pet: " + loi.getMessage(), loi);
        }
    }

    /**
     * Đếm theo id khách hàng trong JdbcThuCungDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả đếm theo id khách hàng
     */
    @Override
    public long demTheoIdKhachHang(Long idKhachHang) {
        // SQL.
        String sql = "SELECT COUNT(*) FROM thu_cung WHERE khach_hang_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idKhachHang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getLong(1);
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error counting pets by customer: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Đếm trong JdbcThuCungDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM thu_cung";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting pets: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến thú cưng trong JdbcThuCungDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến thú cưng
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private ThuCung anhXaKetQuaDatDenThuCung(ResultSet tapKetQua) throws SQLException {
        // Thú cưng.
        ThuCung thuCung = new ThuCung();
        thuCung.datId(tapKetQua.getLong("id"));
        thuCung.datIdKhachHang(tapKetQua.getLong("khach_hang_id"));
        thuCung.datMaThuCung(tapKetQua.getString("ma_thu_cung"));
        thuCung.datTen(tapKetQua.getString("ten"));
        thuCung.datLoai(tapKetQua.getString("loai"));
        thuCung.datGiong(tapKetQua.getString("giong"));
        thuCung.datGioiTinh(tapKetQua.getString("gioi_tinh"));
        thuCung.datCanNang(tapKetQua.getBigDecimal("can_nang"));

        // Ngày sinh.
        Date ngaySinh = tapKetQua.getDate("ngay_sinh");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngaySinh != null) {
            thuCung.datNgaySinh(ngaySinh.toLocalDate());
        }

        thuCung.datMauLong(tapKetQua.getString("mau_long"));
        thuCung.datTinhCach(tapKetQua.getString("tinh_cach"));
        thuCung.datURLAnh(tapKetQua.getString("hinh_anh_url"));
        thuCung.datDiUng(tapKetQua.getString("di_ung"));
        thuCung.datDaTrietSan(tapKetQua.getBoolean("da_tiem_phong"));
        thuCung.datGhiChuSucKhoe(tapKetQua.getString("ghi_chu_suc_khoe"));

        // Thời điểm tạo.
        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_tao");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemTao != null) {
            thuCung.datThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        thuCung.datPhienBanBanGhi(tapKetQua.getBytes("row_version"));

        return thuCung;
    }
}
