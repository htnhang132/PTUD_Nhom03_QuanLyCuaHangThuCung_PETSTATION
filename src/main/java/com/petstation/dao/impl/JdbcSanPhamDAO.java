package com.petstation.dao.impl;

import com.petstation.dao.SanPhamDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.SanPham;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC sản phẩm DAO. */
public class JdbcSanPhamDAO implements SanPhamDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcSanPhamDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcSanPhamDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcSanPhamDAO.
     *
     * @param sanPham sản phẩm
     * @return kết quả tạo
     */
    @Override
    public SanPham tao(SanPham sanPham) {
        // SQL.
        String sql =
                "INSERT INTO san_pham (ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, "
                        + "gia_ban, hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setString(1, sanPham.layMaSanPham());
            cauLenh.setString(2, sanPham.layTenSanPham());
            cauLenh.setLong(3, sanPham.layIdNhomHang());
            cauLenh.setString(4, sanPham.layDonVi());
            cauLenh.setBigDecimal(5, sanPham.layGiaBan());
            cauLenh.setString(6, sanPham.layURLAnh());
            cauLenh.setString(7, sanPham.layMoTa());
            cauLenh.setBoolean(
                    8, sanPham.layCanDonThuoc() != null ? sanPham.layCanDonThuoc() : false);
            cauLenh.setBoolean(
                    9, sanPham.layDangHoatDong() != null ? sanPham.layDangHoatDong() : true);
            cauLenh.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

            // Bị ảnh hưởng các dòng.
            int biAnhHuongCacDong = cauLenh.executeUpdate();
            // Kiểm tra số bản ghi chịu tác động để phát hiện cập nhật thất bại.
            if (biAnhHuongCacDong == 0) {
                throw new TruyCapDuLieuException("Creating product failed, no rows affected.");
            }

            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    sanPham.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return sanPham;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error creating product: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcSanPhamDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<SanPham> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenSanPham(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding product by ID: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo mã sản phẩm trong JdbcSanPhamDAO.
     *
     * @param maSanPham mã sản phẩm
     * @return kết quả tìm theo mã sản phẩm
     */
    @Override
    public Optional<SanPham> timTheoMaSanPham(String maSanPham) {
        // SQL.
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE ma_san_pham = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maSanPham);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenSanPham(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding product by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo id nhóm hàng trong JdbcSanPhamDAO.
     *
     * @param idNhomHang id nhóm hàng
     * @return kết quả tìm theo id nhóm hàng
     */
    @Override
    public List<SanPham> timTheoIdNhomHang(Long idNhomHang) {
        // SQL.
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE danh_muc_id = ? ORDER BY ten_san_pham";

        // Các sản phẩm.
        List<SanPham> cacSanPham = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idNhomHang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacSanPham.add(anhXaKetQuaDatDenSanPham(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding products by category: " + loi.getMessage(), loi);
        }

        return cacSanPham;
    }

    /**
     * Tìm kiếm theo tên trong JdbcSanPhamDAO.
     *
     * @param ten tên
     * @return kết quả tìm kiếm theo tên
     */
    @Override
    public List<SanPham> timKiemTheoTen(String ten) {
        // SQL.
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE ten_san_pham LIKE ? ORDER BY ten_san_pham";

        // Các sản phẩm.
        List<SanPham> cacSanPham = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, "%" + ten + "%");
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacSanPham.add(anhXaKetQuaDatDenSanPham(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error searching products: " + loi.getMessage(), loi);
        }

        return cacSanPham;
    }

    /**
     * Tìm tất cả hoạt động trong JdbcSanPhamDAO.
     *
     * @return kết quả tìm tất cả hoạt động
     */
    @Override
    public List<SanPham> timTatCaHoatDong() {
        // SQL.
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE hoat_dong = 1 ORDER BY ten_san_pham";

        // Các sản phẩm.
        List<SanPham> cacSanPham = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacSanPham.add(anhXaKetQuaDatDenSanPham(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding active products: " + loi.getMessage(), loi);
        }

        return cacSanPham;
    }

    /**
     * Tìm yêu cầu đơn thuốc trong JdbcSanPhamDAO.
     *
     * @return kết quả tìm yêu cầu đơn thuốc
     */
    @Override
    public List<SanPham> timYeuCauDonThuoc() {
        // SQL.
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE can_don_thuoc = 1 ORDER BY ten_san_pham";

        // Các sản phẩm.
        List<SanPham> cacSanPham = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacSanPham.add(anhXaKetQuaDatDenSanPham(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding prescription products: " + loi.getMessage(), loi);
        }

        return cacSanPham;
    }

    /**
     * Tìm tất cả trong JdbcSanPhamDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<SanPham> timTatCa() {
        // SQL.
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham ORDER BY ngay_tao DESC";

        // Các sản phẩm.
        List<SanPham> cacSanPham = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacSanPham.add(anhXaKetQuaDatDenSanPham(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all products: " + loi.getMessage(), loi);
        }

        return cacSanPham;
    }

    /**
     * Cập nhật trong JdbcSanPhamDAO.
     *
     * @param sanPham sản phẩm
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(SanPham sanPham) {
        // SQL.
        String sql =
                "UPDATE san_pham SET ten_san_pham = ?, danh_muc_id = ?, don_vi_tinh = ?, gia_ban ="
                    + " ?, hinh_anh_url = ?, mo_ta = ?, can_don_thuoc = ?, hoat_dong = ? WHERE id ="
                    + " ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, sanPham.layTenSanPham());
            cauLenh.setLong(2, sanPham.layIdNhomHang());
            cauLenh.setString(3, sanPham.layDonVi());
            cauLenh.setBigDecimal(4, sanPham.layGiaBan());
            cauLenh.setString(5, sanPham.layURLAnh());
            cauLenh.setString(6, sanPham.layMoTa());
            cauLenh.setBoolean(7, sanPham.layCanDonThuoc());
            cauLenh.setBoolean(8, sanPham.layDangHoatDong());
            cauLenh.setLong(9, sanPham.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating product: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật giá bán trong JdbcSanPhamDAO.
     *
     * @param idSanPham id sản phẩm
     * @param giaMoi giá mới
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatGiaBan(Long idSanPham, BigDecimal giaMoi) {
        // SQL.
        String sql = "UPDATE san_pham SET gia_ban = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setBigDecimal(1, giaMoi);
            cauLenh.setLong(2, idSanPham);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating product price: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật trạng thái hoạt động trong JdbcSanPhamDAO.
     *
     * @param idSanPham id sản phẩm
     * @param dangHoatDong đang hoạt động
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatTrangThaiHoatDong(Long idSanPham, Boolean dangHoatDong) {
        // SQL.
        String sql = "UPDATE san_pham SET hoat_dong = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setBoolean(1, dangHoatDong);
            cauLenh.setLong(2, idSanPham);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating product status: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcSanPhamDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM san_pham WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting product: " + loi.getMessage(), loi);
        }
    }

    /**
     * Lấy mã tồn tại theo sản phẩm trong JdbcSanPhamDAO.
     *
     * @param maSanPham mã sản phẩm
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean layMaTonTaiTheoSanPham(String maSanPham) {
        // SQL.
        String sql = "SELECT COUNT(*) FROM san_pham WHERE ma_san_pham = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maSanPham);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getInt(1) > 0;
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error checking product existence: " + loi.getMessage(), loi);
        }

        return false;
    }

    /**
     * Đếm trong JdbcSanPhamDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM san_pham";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting products: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến sản phẩm trong JdbcSanPhamDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến sản phẩm
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private SanPham anhXaKetQuaDatDenSanPham(ResultSet tapKetQua) throws SQLException {
        // Sản phẩm.
        SanPham sanPham = new SanPham();
        sanPham.datId(tapKetQua.getLong("id"));
        sanPham.datMaSanPham(tapKetQua.getString("ma_san_pham"));
        sanPham.datTenSanPham(tapKetQua.getString("ten_san_pham"));
        sanPham.datIdNhomHang(tapKetQua.getLong("danh_muc_id"));
        sanPham.datDonVi(tapKetQua.getString("don_vi_tinh"));
        sanPham.datGiaBan(tapKetQua.getBigDecimal("gia_ban"));
        sanPham.datURLAnh(tapKetQua.getString("hinh_anh_url"));
        sanPham.datMoTa(tapKetQua.getString("mo_ta"));
        sanPham.datCanDonThuoc(tapKetQua.getBoolean("can_don_thuoc"));
        sanPham.datDangHoatDong(tapKetQua.getBoolean("hoat_dong"));

        // Thời điểm tạo.
        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_tao");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemTao != null) {
            sanPham.datThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        sanPham.datPhienBanBanGhi(tapKetQua.getBytes("row_version"));

        return sanPham;
    }
}
