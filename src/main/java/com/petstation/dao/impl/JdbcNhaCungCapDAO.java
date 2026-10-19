package com.petstation.dao.impl;

import com.petstation.dao.NhaCungCapDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.NhaCungCap;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC nhà cung cấp DAO. */
public class JdbcNhaCungCapDAO implements NhaCungCapDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcNhaCungCapDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcNhaCungCapDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcNhaCungCapDAO.
     *
     * @param nhaCungCap nhà cung cấp
     * @return kết quả tạo
     */
    @Override
    public NhaCungCap tao(NhaCungCap nhaCungCap) {
        // SQL.
        String sql =
                "INSERT INTO nha_cung_cap (ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, "
                        + "email, dia_chi, ma_so_thue, hoat_dong, ghi_chu, ngay_tao) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setString(1, nhaCungCap.layMaNhaCungCap());
            cauLenh.setString(2, nhaCungCap.layTenNhaCungCap());
            cauLenh.setString(3, nhaCungCap.layLienHeNguoi());
            cauLenh.setString(4, nhaCungCap.laySoDienThoai());
            cauLenh.setString(5, nhaCungCap.layEmail());
            cauLenh.setString(6, nhaCungCap.layDiaChi());
            cauLenh.setString(7, nhaCungCap.layMaThue());
            cauLenh.setBoolean(
                    8, nhaCungCap.layDangHoatDong() != null ? nhaCungCap.layDangHoatDong() : true);
            cauLenh.setString(9, nhaCungCap.layGhiChu());
            cauLenh.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    nhaCungCap.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return nhaCungCap;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error creating supplier: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcNhaCungCapDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<NhaCungCap> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi,"
                        + " ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version FROM nha_cung_cap"
                        + " WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenNhaCungCap(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding supplier: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo mã nhà cung cấp trong JdbcNhaCungCapDAO.
     *
     * @param maNhaCungCap mã nhà cung cấp
     * @return kết quả tìm theo mã nhà cung cấp
     */
    @Override
    public Optional<NhaCungCap> timTheoMaNhaCungCap(String maNhaCungCap) {
        // SQL.
        String sql =
                "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi,"
                        + " ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version FROM nha_cung_cap"
                        + " WHERE ma_ncc = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maNhaCungCap);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenNhaCungCap(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding supplier by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo số điện thoại trong JdbcNhaCungCapDAO.
     *
     * @param soDienThoai số điện thoại
     * @return kết quả tìm theo số điện thoại
     */
    @Override
    public Optional<NhaCungCap> timTheoSoDienThoai(String soDienThoai) {
        // SQL.
        String sql =
                "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi,"
                        + " ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version FROM nha_cung_cap"
                        + " WHERE so_dien_thoai = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, soDienThoai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenNhaCungCap(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding supplier by phone: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm kiếm theo tên trong JdbcNhaCungCapDAO.
     *
     * @param ten tên
     * @return kết quả tìm kiếm theo tên
     */
    @Override
    public List<NhaCungCap> timKiemTheoTen(String ten) {
        // SQL.
        String sql =
                "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi, "
                        + "ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version "
                        + "FROM nha_cung_cap WHERE ten_ncc LIKE ? ORDER BY ten_ncc";

        // Các nhà cung cấp.
        List<NhaCungCap> cacNhaCungCap = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, "%" + ten + "%");
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacNhaCungCap.add(anhXaKetQuaDatDenNhaCungCap(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error searching suppliers: " + loi.getMessage(), loi);
        }

        return cacNhaCungCap;
    }

    /**
     * Tìm tất cả hoạt động trong JdbcNhaCungCapDAO.
     *
     * @return kết quả tìm tất cả hoạt động
     */
    @Override
    public List<NhaCungCap> timTatCaHoatDong() {
        // SQL.
        String sql =
                "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi, "
                        + "ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version "
                        + "FROM nha_cung_cap WHERE hoat_dong = 1 ORDER BY ten_ncc";

        // Các nhà cung cấp.
        List<NhaCungCap> cacNhaCungCap = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacNhaCungCap.add(anhXaKetQuaDatDenNhaCungCap(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding active suppliers: " + loi.getMessage(), loi);
        }

        return cacNhaCungCap;
    }

    /**
     * Tìm tất cả trong JdbcNhaCungCapDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<NhaCungCap> timTatCa() {
        // SQL.
        String sql =
                "SELECT id, ma_ncc, ten_ncc, nguoi_lien_he, so_dien_thoai, email, dia_chi, "
                        + "ma_so_thue, hoat_dong, ghi_chu, ngay_tao, row_version "
                        + "FROM nha_cung_cap ORDER BY ngay_tao DESC";

        // Các nhà cung cấp.
        List<NhaCungCap> cacNhaCungCap = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacNhaCungCap.add(anhXaKetQuaDatDenNhaCungCap(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all suppliers: " + loi.getMessage(), loi);
        }

        return cacNhaCungCap;
    }

    /**
     * Cập nhật trong JdbcNhaCungCapDAO.
     *
     * @param nhaCungCap nhà cung cấp
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(NhaCungCap nhaCungCap) {
        // SQL.
        String sql =
                "UPDATE nha_cung_cap SET ten_ncc = ?, nguoi_lien_he = ?, so_dien_thoai = ?, email ="
                    + " ?, dia_chi = ?, ma_so_thue = ?, hoat_dong = ?, ghi_chu = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, nhaCungCap.layTenNhaCungCap());
            cauLenh.setString(2, nhaCungCap.layLienHeNguoi());
            cauLenh.setString(3, nhaCungCap.laySoDienThoai());
            cauLenh.setString(4, nhaCungCap.layEmail());
            cauLenh.setString(5, nhaCungCap.layDiaChi());
            cauLenh.setString(6, nhaCungCap.layMaThue());
            cauLenh.setBoolean(7, nhaCungCap.layDangHoatDong());
            cauLenh.setString(8, nhaCungCap.layGhiChu());
            cauLenh.setLong(9, nhaCungCap.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating supplier: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật trạng thái hoạt động trong JdbcNhaCungCapDAO.
     *
     * @param idNhaCungCap id nhà cung cấp
     * @param dangHoatDong đang hoạt động
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatTrangThaiHoatDong(Long idNhaCungCap, Boolean dangHoatDong) {
        // SQL.
        String sql = "UPDATE nha_cung_cap SET hoat_dong = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setBoolean(1, dangHoatDong);
            cauLenh.setLong(2, idNhaCungCap);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating supplier status: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcNhaCungCapDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM nha_cung_cap WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting supplier: " + loi.getMessage(), loi);
        }
    }

    /**
     * Lấy mã tồn tại theo nhà cung cấp trong JdbcNhaCungCapDAO.
     *
     * @param maNhaCungCap mã nhà cung cấp
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean layMaTonTaiTheoNhaCungCap(String maNhaCungCap) {
        // SQL.
        String sql = "SELECT COUNT(*) FROM nha_cung_cap WHERE ma_ncc = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maNhaCungCap);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getInt(1) > 0;
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error checking supplier existence: " + loi.getMessage(), loi);
        }

        return false;
    }

    /**
     * Đếm trong JdbcNhaCungCapDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM nha_cung_cap";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting suppliers: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến nhà cung cấp trong JdbcNhaCungCapDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến nhà cung cấp
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private NhaCungCap anhXaKetQuaDatDenNhaCungCap(ResultSet tapKetQua) throws SQLException {
        // Nhà cung cấp.
        NhaCungCap nhaCungCap = new NhaCungCap();
        nhaCungCap.datId(tapKetQua.getLong("id"));
        nhaCungCap.datMaNhaCungCap(tapKetQua.getString("ma_ncc"));
        nhaCungCap.datTenNhaCungCap(tapKetQua.getString("ten_ncc"));
        nhaCungCap.datLienHeNguoi(tapKetQua.getString("nguoi_lien_he"));
        nhaCungCap.datSoDienThoai(tapKetQua.getString("so_dien_thoai"));
        nhaCungCap.datEmail(tapKetQua.getString("email"));
        nhaCungCap.datDiaChi(tapKetQua.getString("dia_chi"));
        nhaCungCap.datMaThue(tapKetQua.getString("ma_so_thue"));
        nhaCungCap.datDangHoatDong(tapKetQua.getBoolean("hoat_dong"));
        nhaCungCap.datGhiChu(tapKetQua.getString("ghi_chu"));

        // Thời điểm tạo.
        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_tao");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemTao != null) {
            nhaCungCap.datThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        nhaCungCap.datPhienBanBanGhi(tapKetQua.getBytes("row_version"));

        return nhaCungCap;
    }
}
