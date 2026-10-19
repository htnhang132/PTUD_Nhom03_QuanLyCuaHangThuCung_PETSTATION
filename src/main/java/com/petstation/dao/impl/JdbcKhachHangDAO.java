package com.petstation.dao.impl;

import com.petstation.dao.KhachHangDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.KhachHang;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC khách hàng DAO. */
public class JdbcKhachHangDAO implements KhachHangDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcKhachHangDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcKhachHangDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcKhachHangDAO.
     *
     * @param khachHang khách hàng
     * @return kết quả tạo
     */
    @Override
    public KhachHang tao(KhachHang khachHang) {
        // SQL.
        String sql =
                "INSERT INTO khach_hang (ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, "
                        + "ngay_sinh, diem_tich_luy, hang_thanh_vien, ngay_tao) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setString(1, khachHang.layMaKhachHang());
            cauLenh.setString(2, khachHang.layHoTen());
            cauLenh.setString(3, khachHang.laySoDienThoai());
            cauLenh.setString(4, khachHang.layDiaChi());
            cauLenh.setDate(
                    5,
                    khachHang.layNgaySinh() != null ? Date.valueOf(khachHang.layNgaySinh()) : null);
            cauLenh.setInt(6, khachHang.layDiemTichLuy() != null ? khachHang.layDiemTichLuy() : 0);
            cauLenh.setString(
                    7,
                    khachHang.layHangThanhVien() != null
                            ? khachHang.layHangThanhVien()
                            : "REGULAR");
            cauLenh.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            // Bị ảnh hưởng các dòng.
            int biAnhHuongCacDong = cauLenh.executeUpdate();
            // Kiểm tra số bản ghi chịu tác động để phát hiện cập nhật thất bại.
            if (biAnhHuongCacDong == 0) {
                throw new TruyCapDuLieuException("Creating customer failed, no rows affected.");
            }

            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    khachHang.datId(duocSinhCacKhoa.getLong(1));
                } else {
                    throw new TruyCapDuLieuException("Creating customer failed, no ID obtained.");
                }
            }

            return khachHang;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error creating customer: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcKhachHangDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<KhachHang> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding customer by ID: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo mã khách hàng trong JdbcKhachHangDAO.
     *
     * @param maKhachHang mã khách hàng
     * @return kết quả tìm theo mã khách hàng
     */
    @Override
    public Optional<KhachHang> timTheoMaKhachHang(String maKhachHang) {
        // SQL.
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE ma_khach_hang = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maKhachHang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding customer by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo số điện thoại trong JdbcKhachHangDAO.
     *
     * @param soDienThoai số điện thoại
     * @return kết quả tìm theo số điện thoại
     */
    @Override
    public Optional<KhachHang> timTheoSoDienThoai(String soDienThoai) {
        // SQL.
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE so_dien_thoai = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, soDienThoai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding customer by phone: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm kiếm theo tên trong JdbcKhachHangDAO.
     *
     * @param ten tên
     * @return kết quả tìm kiếm theo tên
     */
    @Override
    public List<KhachHang> timKiemTheoTen(String ten) {
        // SQL.
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE ho_ten LIKE ? ORDER BY ho_ten";

        // Các khách hàng.
        List<KhachHang> cacKhachHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, "%" + ten + "%");
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacKhachHang.add(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error searching customers by name: " + loi.getMessage(), loi);
        }

        return cacKhachHang;
    }

    /**
     * Tìm tất cả trong JdbcKhachHangDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<KhachHang> timTatCa() {
        // SQL.
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang ORDER BY ngay_tao DESC";

        // Các khách hàng.
        List<KhachHang> cacKhachHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacKhachHang.add(anhXaKetQuaDatDenKhachHang(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all customers: " + loi.getMessage(), loi);
        }

        return cacKhachHang;
    }

    /**
     * Tìm theo hạng thành viên trong JdbcKhachHangDAO.
     *
     * @param hang hạng
     * @return kết quả tìm theo hạng thành viên
     */
    @Override
    public List<KhachHang> timTheoHangThanhVien(String hang) {
        // SQL.
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE hang_thanh_vien = ? ORDER BY diem_tich_luy DESC";

        // Các khách hàng.
        List<KhachHang> cacKhachHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, hang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacKhachHang.add(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding customers by tier: " + loi.getMessage(), loi);
        }

        return cacKhachHang;
    }

    /**
     * Cập nhật trong JdbcKhachHangDAO.
     *
     * @param khachHang khách hàng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(KhachHang khachHang) {
        // SQL.
        String sql =
                "UPDATE khach_hang SET ho_ten = ?, so_dien_thoai = ?, dia_chi = ?, "
                        + "ngay_sinh = ?, diem_tich_luy = ?, hang_thanh_vien = ? "
                        + "WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, khachHang.layHoTen());
            cauLenh.setString(2, khachHang.laySoDienThoai());
            cauLenh.setString(3, khachHang.layDiaChi());
            cauLenh.setDate(
                    4,
                    khachHang.layNgaySinh() != null ? Date.valueOf(khachHang.layNgaySinh()) : null);
            cauLenh.setInt(5, khachHang.layDiemTichLuy());
            cauLenh.setString(6, khachHang.layHangThanhVien());
            cauLenh.setLong(7, khachHang.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating customer: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật điểm tích lũy trong JdbcKhachHangDAO.
     *
     * @param idKhachHang id khách hàng
     * @param diem điểm
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatDiemTichLuy(Long idKhachHang, Integer diem) {
        // SQL.
        String sql = "UPDATE khach_hang SET diem_tich_luy = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setInt(1, diem);
            cauLenh.setLong(2, idKhachHang);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating loyalty points: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật hạng thành viên trong JdbcKhachHangDAO.
     *
     * @param idKhachHang id khách hàng
     * @param hang hạng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatHangThanhVien(Long idKhachHang, String hang) {
        // SQL.
        String sql = "UPDATE khach_hang SET hang_thanh_vien = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, hang);
            cauLenh.setLong(2, idKhachHang);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating membership tier: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcKhachHangDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM khach_hang WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting customer: " + loi.getMessage(), loi);
        }
    }

    /**
     * Lấy tồn tại theo số điện thoại trong JdbcKhachHangDAO.
     *
     * @param soDienThoai số điện thoại
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean layTonTaiTheoSoDienThoai(String soDienThoai) {
        // SQL.
        String sql = "SELECT COUNT(*) FROM khach_hang WHERE so_dien_thoai = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, soDienThoai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getInt(1) > 0;
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error checking customer existence: " + loi.getMessage(), loi);
        }

        return false;
    }

    /**
     * Đếm trong JdbcKhachHangDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM khach_hang";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting customers: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến khách hàng trong JdbcKhachHangDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến khách hàng
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private KhachHang anhXaKetQuaDatDenKhachHang(ResultSet tapKetQua) throws SQLException {
        // Khách hàng.
        KhachHang khachHang = new KhachHang();
        khachHang.datId(tapKetQua.getLong("id"));
        khachHang.datMaKhachHang(tapKetQua.getString("ma_khach_hang"));
        khachHang.datHoTen(tapKetQua.getString("ho_ten"));
        khachHang.datSoDienThoai(tapKetQua.getString("so_dien_thoai"));
        khachHang.datDiaChi(tapKetQua.getString("dia_chi"));

        // Ngày sinh.
        Date ngaySinh = tapKetQua.getDate("ngay_sinh");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngaySinh != null) {
            khachHang.datNgaySinh(ngaySinh.toLocalDate());
        }

        khachHang.datDiemTichLuy(tapKetQua.getInt("diem_tich_luy"));
        khachHang.datHangThanhVien(tapKetQua.getString("hang_thanh_vien"));

        // Thời điểm tạo.
        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_tao");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemTao != null) {
            khachHang.datThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        khachHang.datPhienBanBanGhi(tapKetQua.getBytes("row_version"));

        return khachHang;
    }
}
