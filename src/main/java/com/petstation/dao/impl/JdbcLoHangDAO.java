package com.petstation.dao.impl;

import com.petstation.dao.LoHangDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.LoHang;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC lô hàng DAO. */
public class JdbcLoHangDAO implements LoHangDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcLoHangDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcLoHangDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcLoHangDAO.
     *
     * @param loHang lô hàng
     * @return kết quả tạo
     */
    @Override
    public LoHang tao(LoHang loHang) {
        // SQL.
        String sql =
                "INSERT INTO lo_hang (ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, "
                        + "so_luong, gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setString(1, loHang.layMaLoHang());
            cauLenh.setLong(2, loHang.layIdSanPham());
            cauLenh.setDate(
                    3,
                    loHang.layNgaySanXuat() != null ? Date.valueOf(loHang.layNgaySanXuat()) : null);
            cauLenh.setDate(
                    4, loHang.layHanSuDung() != null ? Date.valueOf(loHang.layHanSuDung()) : null);
            cauLenh.setInt(5, loHang.laySoLuong());
            cauLenh.setBigDecimal(6, loHang.layGiaGiaNhap());
            cauLenh.setObject(7, loHang.layIdNhaCungCap());
            cauLenh.setString(
                    8, loHang.layTrangThai() != null ? loHang.layTrangThai() : "IN_STOCK");
            cauLenh.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    loHang.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return loHang;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error creating batch: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcLoHangDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<LoHang> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenLoHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding batch: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo mã lô hàng trong JdbcLoHangDAO.
     *
     * @param maLoHang mã lô hàng
     * @return kết quả tìm theo mã lô hàng
     */
    @Override
    public Optional<LoHang> timTheoMaLoHang(String maLoHang) {
        // SQL.
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang WHERE ma_lo = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maLoHang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenLoHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding batch by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo id sản phẩm trong JdbcLoHangDAO.
     *
     * @param idSanPham id sản phẩm
     * @return kết quả tìm theo id sản phẩm
     */
    @Override
    public List<LoHang> timTheoIdSanPham(Long idSanPham) {
        // SQL.
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang WHERE san_pham_id = ? ORDER BY han_su_dung";

        // Các lô hàng.
        List<LoHang> cacLoHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idSanPham);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacLoHang.add(anhXaKetQuaDatDenLoHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding batches by product: " + loi.getMessage(), loi);
        }

        return cacLoHang;
    }

    /**
     * Tìm theo id nhà cung cấp trong JdbcLoHangDAO.
     *
     * @param idNhaCungCap id nhà cung cấp
     * @return kết quả tìm theo id nhà cung cấp
     */
    @Override
    public List<LoHang> timTheoIdNhaCungCap(Long idNhaCungCap) {
        // SQL.
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang WHERE nha_cung_cap_id = ? ORDER BY ngay_nhap DESC";

        // Các lô hàng.
        List<LoHang> cacLoHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idNhaCungCap);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacLoHang.add(anhXaKetQuaDatDenLoHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding batches by supplier: " + loi.getMessage(), loi);
        }

        return cacLoHang;
    }

    /**
     * Tìm theo trạng thái trong JdbcLoHangDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    @Override
    public List<LoHang> timTheoTrangThai(String trangThai) {
        // SQL.
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang WHERE trang_thai = ? ORDER BY ngay_nhap DESC";

        // Các lô hàng.
        List<LoHang> cacLoHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacLoHang.add(anhXaKetQuaDatDenLoHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding batches by status: " + loi.getMessage(), loi);
        }

        return cacLoHang;
    }

    /**
     * Tìm sắp hết hạn trước trong JdbcLoHangDAO.
     *
     * @param ngay ngày
     * @return kết quả tìm sắp hết hạn trước
     */
    @Override
    public List<LoHang> timSapHetHanTruoc(LocalDate ngay) {
        // SQL.
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, gia_nhap,"
                        + " nha_cung_cap_id, trang_thai, ngay_nhap, row_version FROM lo_hang WHERE"
                        + " han_su_dung < ? AND trang_thai = 'IN_STOCK' ORDER BY han_su_dung";

        // Các lô hàng.
        List<LoHang> cacLoHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setDate(1, Date.valueOf(ngay));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacLoHang.add(anhXaKetQuaDatDenLoHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding expiring batches: " + loi.getMessage(), loi);
        }

        return cacLoHang;
    }

    /**
     * Tìm hết hạn các lô hàng trong JdbcLoHangDAO.
     *
     * @return kết quả tìm hết hạn các lô hàng
     */
    @Override
    public List<LoHang> timHetHanCacLoHang() {
        return timSapHetHanTruoc(LocalDate.now());
    }

    /**
     * Tìm thấp tồn kho các lô hàng trong JdbcLoHangDAO.
     *
     * @param nguong ngưỡng
     * @return kết quả tìm thấp tồn kho các lô hàng
     */
    @Override
    public List<LoHang> timThapTonKhoCacLoHang(Integer nguong) {
        // SQL.
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, gia_nhap,"
                        + " nha_cung_cap_id, trang_thai, ngay_nhap, row_version FROM lo_hang WHERE"
                        + " so_luong <= ? AND trang_thai = 'IN_STOCK' ORDER BY so_luong";

        // Các lô hàng.
        List<LoHang> cacLoHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setInt(1, nguong);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacLoHang.add(anhXaKetQuaDatDenLoHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding low stock batches: " + loi.getMessage(), loi);
        }

        return cacLoHang;
    }

    /**
     * Tìm tất cả trong JdbcLoHangDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<LoHang> timTatCa() {
        // SQL.
        String sql =
                "SELECT id, ma_lo, san_pham_id, ngay_san_xuat, han_su_dung, so_luong, "
                        + "gia_nhap, nha_cung_cap_id, trang_thai, ngay_nhap, row_version "
                        + "FROM lo_hang ORDER BY ngay_nhap DESC";

        // Các lô hàng.
        List<LoHang> cacLoHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacLoHang.add(anhXaKetQuaDatDenLoHang(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding all batches: " + loi.getMessage(), loi);
        }

        return cacLoHang;
    }

    /**
     * Cập nhật trong JdbcLoHangDAO.
     *
     * @param loHang lô hàng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(LoHang loHang) {
        // SQL.
        String sql = "UPDATE lo_hang SET so_luong = ?, trang_thai = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setInt(1, loHang.laySoLuong());
            cauLenh.setString(2, loHang.layTrangThai());
            cauLenh.setLong(3, loHang.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating batch: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật số lượng trong JdbcLoHangDAO.
     *
     * @param idLoHang id lô hàng
     * @param soLuong số lượng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatSoLuong(Long idLoHang, Integer soLuong) {
        // SQL.
        String sql = "UPDATE lo_hang SET so_luong = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setInt(1, soLuong);
            cauLenh.setLong(2, idLoHang);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating batch quantity: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật trạng thái trong JdbcLoHangDAO.
     *
     * @param idLoHang id lô hàng
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatTrangThai(Long idLoHang, String trangThai) {
        // SQL.
        String sql = "UPDATE lo_hang SET trang_thai = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            cauLenh.setLong(2, idLoHang);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating batch status: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcLoHangDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM lo_hang WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting batch: " + loi.getMessage(), loi);
        }
    }

    /**
     * Đếm trong JdbcLoHangDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM lo_hang";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting batches: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến lô hàng trong JdbcLoHangDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến lô hàng
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private LoHang anhXaKetQuaDatDenLoHang(ResultSet tapKetQua) throws SQLException {
        // Lô hàng.
        LoHang loHang = new LoHang();
        loHang.datId(tapKetQua.getLong("id"));
        loHang.datMaLoHang(tapKetQua.getString("ma_lo"));
        loHang.datIdSanPham(tapKetQua.getLong("san_pham_id"));

        // Ngày ngày sản xuất.
        Date ngayNgaySanXuat = tapKetQua.getDate("ngay_san_xuat");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayNgaySanXuat != null) {
            loHang.datNgaySanXuat(ngayNgaySanXuat.toLocalDate());
        }

        // Ngày hạn dùng.
        Date ngayHanDung = tapKetQua.getDate("han_su_dung");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ngayHanDung != null) {
            loHang.datHanSuDung(ngayHanDung.toLocalDate());
        }

        loHang.datSoLuong(tapKetQua.getInt("so_luong"));
        loHang.datGiaGiaNhap(tapKetQua.getBigDecimal("gia_nhap"));

        // Id nhà cung cấp.
        long idNhaCungCap = tapKetQua.getLong("nha_cung_cap_id");
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (!tapKetQua.wasNull()) {
            loHang.datIdNhaCungCap(idNhaCungCap);
        }

        loHang.datTrangThai(tapKetQua.getString("trang_thai"));

        // Thời điểm tạo.
        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_nhap");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemTao != null) {
            loHang.datThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        loHang.datPhienBanBanGhi(tapKetQua.getBytes("row_version"));

        return loHang;
    }
}
