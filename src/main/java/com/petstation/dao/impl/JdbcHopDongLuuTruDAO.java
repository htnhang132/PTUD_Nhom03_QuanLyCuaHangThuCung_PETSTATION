package com.petstation.dao.impl;

import com.petstation.dao.HopDongLuuTruDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.HopDongLuuTru;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC hợp đồng lưu trú DAO. */
public class JdbcHopDongLuuTruDAO implements HopDongLuuTruDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcHopDongLuuTruDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcHopDongLuuTruDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcHopDongLuuTruDAO.
     *
     * @param hopDongLuuTru hợp đồng lưu trú
     * @return kết quả tạo
     */
    @Override
    public HopDongLuuTru tao(HopDongLuuTru hopDongLuuTru) {
        // SQL.
        String sql =
                "INSERT INTO dat_phong_hotel (ma_dat_phong, thu_cung_id, khach_hang_id, phong_id,"
                        + " gio_nhan_phong, gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong,"
                        + " tien_coc, phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id,"
                        + " ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setString(1, hopDongLuuTru.layMaHopDong());
            cauLenh.setLong(2, hopDongLuuTru.layIdThuCung());
            cauLenh.setLong(3, hopDongLuuTru.layIdKhachHang());
            cauLenh.setLong(4, hopDongLuuTru.layIdPhong());
            cauLenh.setTimestamp(
                    5,
                    hopDongLuuTru.layThoiGianNhanPhong() != null
                            ? Timestamp.valueOf(hopDongLuuTru.layThoiGianNhanPhong())
                            : null);
            cauLenh.setTimestamp(
                    6,
                    hopDongLuuTru.layThoiGianTraPhongDuKien() != null
                            ? Timestamp.valueOf(hopDongLuuTru.layThoiGianTraPhongDuKien())
                            : null);
            cauLenh.setTimestamp(
                    7,
                    hopDongLuuTru.layThoiGianTraPhongThucTe() != null
                            ? Timestamp.valueOf(hopDongLuuTru.layThoiGianTraPhongThucTe())
                            : null);
            cauLenh.setString(8, hopDongLuuTru.layKhungThoiGian());
            cauLenh.setBigDecimal(9, hopDongLuuTru.layGiaCoSo());
            cauLenh.setBigDecimal(10, hopDongLuuTru.layTienCoc());
            cauLenh.setBigDecimal(11, hopDongLuuTru.layPhiTre());
            cauLenh.setBigDecimal(12, hopDongLuuTru.layTongTien());
            cauLenh.setString(
                    13,
                    hopDongLuuTru.layTrangThai() != null
                            ? hopDongLuuTru.layTrangThai()
                            : "RESERVED");
            cauLenh.setString(14, hopDongLuuTru.layGhiChu());
            cauLenh.setObject(15, hopDongLuuTru.layIdNhanVien());
            cauLenh.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    hopDongLuuTru.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return hopDongLuuTru;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error creating hotel booking: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcHopDongLuuTruDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<HopDongLuuTru> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong,"
                        + " gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc,"
                        + " phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao,"
                        + " row_version FROM dat_phong_hotel WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenHopDongLuuTru(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding hotel booking: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo mã hợp đồng trong JdbcHopDongLuuTruDAO.
     *
     * @param maHopDong mã hợp đồng
     * @return kết quả tìm theo mã hợp đồng
     */
    @Override
    public Optional<HopDongLuuTru> timTheoMaHopDong(String maHopDong) {
        // SQL.
        String sql =
                "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong,"
                        + " gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc,"
                        + " phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao,"
                        + " row_version FROM dat_phong_hotel WHERE ma_dat_phong = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maHopDong);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenHopDongLuuTru(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding hotel booking by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo id thú cưng trong JdbcHopDongLuuTruDAO.
     *
     * @param idThuCung id thú cưng
     * @return kết quả tìm theo id thú cưng
     */
    @Override
    public List<HopDongLuuTru> timTheoIdThuCung(Long idThuCung) {
        // SQL.
        String sql =
                "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong,"
                        + " gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc,"
                        + " phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao,"
                        + " row_version FROM dat_phong_hotel WHERE thu_cung_id = ? ORDER BY"
                        + " gio_nhan_phong DESC";

        return timCacHopDongLuuTru(sql, idThuCung);
    }

    /**
     * Tìm theo id khách hàng trong JdbcHopDongLuuTruDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả tìm theo id khách hàng
     */
    @Override
    public List<HopDongLuuTru> timTheoIdKhachHang(Long idKhachHang) {
        // SQL.
        String sql =
                "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong,"
                        + " gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc,"
                        + " phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao,"
                        + " row_version FROM dat_phong_hotel WHERE khach_hang_id = ? ORDER BY"
                        + " gio_nhan_phong DESC";

        return timCacHopDongLuuTru(sql, idKhachHang);
    }

    /**
     * Tìm theo id phòng trong JdbcHopDongLuuTruDAO.
     *
     * @param idPhong id phòng
     * @return kết quả tìm theo id phòng
     */
    @Override
    public List<HopDongLuuTru> timTheoIdPhong(Long idPhong) {
        // SQL.
        String sql =
                "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong,"
                        + " gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc,"
                        + " phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao,"
                        + " row_version FROM dat_phong_hotel WHERE phong_id = ? ORDER BY"
                        + " gio_nhan_phong";

        return timCacHopDongLuuTru(sql, idPhong);
    }

    /**
     * Tìm theo trạng thái trong JdbcHopDongLuuTruDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    @Override
    public List<HopDongLuuTru> timTheoTrangThai(String trangThai) {
        // SQL.
        String sql =
                "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong,"
                        + " gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc,"
                        + " phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao,"
                        + " row_version FROM dat_phong_hotel WHERE trang_thai = ? ORDER BY"
                        + " gio_nhan_phong";

        // Các hợp đồng lưu trú.
        List<HopDongLuuTru> cacHopDongLuuTru = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacHopDongLuuTru.add(anhXaKetQuaDatDenHopDongLuuTru(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding bookings by status: " + loi.getMessage(), loi);
        }

        return cacHopDongLuuTru;
    }

    /**
     * Tìm theo khoảng ngày trong JdbcHopDongLuuTruDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả tìm theo khoảng ngày
     */
    @Override
    public List<HopDongLuuTru> timTheoKhoangNgay(
            LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc) {
        // SQL.
        String sql =
                "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong,"
                    + " gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc,"
                    + " phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao,"
                    + " row_version FROM dat_phong_hotel WHERE gio_nhan_phong BETWEEN ? AND ? ORDER"
                    + " BY gio_nhan_phong";

        // Các hợp đồng lưu trú.
        List<HopDongLuuTru> cacHopDongLuuTru = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(1, Timestamp.valueOf(ngayBatDau));
            cauLenh.setTimestamp(2, Timestamp.valueOf(ngayKetThuc));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacHopDongLuuTru.add(anhXaKetQuaDatDenHopDongLuuTru(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding bookings by date range: " + loi.getMessage(), loi);
        }

        return cacHopDongLuuTru;
    }

    /**
     * Tìm hoạt động các hợp đồng lưu trú trong JdbcHopDongLuuTruDAO.
     *
     * @return kết quả tìm hoạt động các hợp đồng lưu trú
     */
    @Override
    public List<HopDongLuuTru> timHoatDongCacHopDongLuuTru() {
        // SQL.
        String sql =
                "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong,"
                        + " gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc,"
                        + " phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao,"
                        + " row_version FROM dat_phong_hotel WHERE trang_thai IN ('RESERVED',"
                        + " 'CHECKED_IN') ORDER BY gio_nhan_phong";

        // Các hợp đồng lưu trú.
        List<HopDongLuuTru> cacHopDongLuuTru = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacHopDongLuuTru.add(anhXaKetQuaDatDenHopDongLuuTru(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding active bookings: " + loi.getMessage(), loi);
        }

        return cacHopDongLuuTru;
    }

    /**
     * Tìm quá hạn các hợp đồng lưu trú trong JdbcHopDongLuuTruDAO.
     *
     * @return kết quả tìm quá hạn các hợp đồng lưu trú
     */
    @Override
    public List<HopDongLuuTru> timQuaHanCacHopDongLuuTru() {
        // SQL.
        String sql =
                "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong,"
                        + " gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc,"
                        + " phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao,"
                        + " row_version FROM dat_phong_hotel WHERE trang_thai = 'CHECKED_IN' AND"
                        + " gio_tra_du_kien < GETDATE() ORDER BY gio_tra_du_kien";

        // Các hợp đồng lưu trú.
        List<HopDongLuuTru> cacHopDongLuuTru = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacHopDongLuuTru.add(anhXaKetQuaDatDenHopDongLuuTru(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding overdue bookings: " + loi.getMessage(), loi);
        }

        return cacHopDongLuuTru;
    }

    /**
     * Tìm đã kiểm tra vào các hợp đồng lưu trú trong JdbcHopDongLuuTruDAO.
     *
     * @return kết quả tìm đã kiểm tra vào các hợp đồng lưu trú
     */
    @Override
    public List<HopDongLuuTru> timDaKiemTraVaoCacHopDongLuuTru() {
        return timTheoTrangThai("CHECKED_IN");
    }

    /**
     * Tìm tất cả trong JdbcHopDongLuuTruDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<HopDongLuuTru> timTatCa() {
        // SQL.
        String sql =
                "SELECT id, ma_dat_phong, thu_cung_id, khach_hang_id, phong_id, gio_nhan_phong,"
                        + " gio_tra_du_kien, gio_tra_thuc_te, khung_gio, gia_phong, tien_coc,"
                        + " phi_tre_han, tong_tien, trang_thai, ghi_chu, nhan_vien_id, ngay_tao,"
                        + " row_version FROM dat_phong_hotel ORDER BY ngay_tao DESC";

        // Các hợp đồng lưu trú.
        List<HopDongLuuTru> cacHopDongLuuTru = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacHopDongLuuTru.add(anhXaKetQuaDatDenHopDongLuuTru(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all bookings: " + loi.getMessage(), loi);
        }

        return cacHopDongLuuTru;
    }

    /**
     * Cập nhật trong JdbcHopDongLuuTruDAO.
     *
     * @param hopDongLuuTru hợp đồng lưu trú
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(HopDongLuuTru hopDongLuuTru) {
        // SQL.
        String sql =
                "UPDATE dat_phong_hotel SET gio_tra_thuc_te = ?, phi_tre_han = ?, "
                        + "tong_tien = ?, trang_thai = ?, ghi_chu = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(
                    1,
                    hopDongLuuTru.layThoiGianTraPhongThucTe() != null
                            ? Timestamp.valueOf(hopDongLuuTru.layThoiGianTraPhongThucTe())
                            : null);
            cauLenh.setBigDecimal(2, hopDongLuuTru.layPhiTre());
            cauLenh.setBigDecimal(3, hopDongLuuTru.layTongTien());
            cauLenh.setString(4, hopDongLuuTru.layTrangThai());
            cauLenh.setString(5, hopDongLuuTru.layGhiChu());
            cauLenh.setLong(6, hopDongLuuTru.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating hotel booking: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật trạng thái trong JdbcHopDongLuuTruDAO.
     *
     * @param idHopDongLuuTru id hợp đồng lưu trú
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatTrangThai(Long idHopDongLuuTru, String trangThai) {
        // SQL.
        String sql = "UPDATE dat_phong_hotel SET trang_thai = ? WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, trangThai);
            cauLenh.setLong(2, idHopDongLuuTru);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating booking status: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật thời gian trả phòng thực tế trong JdbcHopDongLuuTruDAO.
     *
     * @param idHopDongLuuTru id hợp đồng lưu trú
     * @param thoiGianTraPhong thời gian trả phòng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatThoiGianTraPhongThucTe(
            Long idHopDongLuuTru, LocalDateTime thoiGianTraPhong) {
        // SQL.
        String sql =
                "UPDATE dat_phong_hotel SET gio_tra_thuc_te = ?, trang_thai = 'CHECKED_OUT' WHERE"
                        + " id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(1, Timestamp.valueOf(thoiGianTraPhong));
            cauLenh.setLong(2, idHopDongLuuTru);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating check-out time: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcHopDongLuuTruDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM dat_phong_hotel WHERE id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error deleting hotel booking: " + loi.getMessage(), loi);
        }
    }

    /**
     * Kiểm tra phòng khả dụng trong JdbcHopDongLuuTruDAO.
     *
     * @param idPhong id phòng
     * @param thoiGianBatDau thời gian bắt đầu
     * @param thoiGianKetThuc thời gian kết thúc
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean kiemTraPhongKhaDung(
            Long idPhong, LocalDateTime thoiGianBatDau, LocalDateTime thoiGianKetThuc) {
        // SQL.
        String sql =
                "SELECT COUNT(*) FROM dat_phong_hotel "
                        + "WHERE phong_id = ? AND trang_thai IN ('RESERVED', 'CHECKED_IN') "
                        + "AND ((gio_nhan_phong BETWEEN ? AND ?) "
                        + "OR (gio_tra_du_kien BETWEEN ? AND ?) "
                        + "OR (gio_nhan_phong <= ? AND gio_tra_du_kien >= ?))";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idPhong);
            cauLenh.setTimestamp(2, Timestamp.valueOf(thoiGianBatDau));
            cauLenh.setTimestamp(3, Timestamp.valueOf(thoiGianKetThuc));
            cauLenh.setTimestamp(4, Timestamp.valueOf(thoiGianBatDau));
            cauLenh.setTimestamp(5, Timestamp.valueOf(thoiGianKetThuc));
            cauLenh.setTimestamp(6, Timestamp.valueOf(thoiGianBatDau));
            cauLenh.setTimestamp(7, Timestamp.valueOf(thoiGianKetThuc));

            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getInt(1) == 0;
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error checking room availability: " + loi.getMessage(), loi);
        }

        return false;
    }

    /**
     * Đếm trong JdbcHopDongLuuTruDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM dat_phong_hotel";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error counting hotel bookings: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Tìm các hợp đồng lưu trú trong JdbcHopDongLuuTruDAO.
     *
     * @param sql SQL
     * @param thamSo tham số
     * @return kết quả tìm các hợp đồng lưu trú
     */
    private List<HopDongLuuTru> timCacHopDongLuuTru(String sql, Long thamSo) {
        // Các hợp đồng lưu trú.
        List<HopDongLuuTru> cacHopDongLuuTru = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, thamSo);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacHopDongLuuTru.add(anhXaKetQuaDatDenHopDongLuuTru(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding hotel bookings: " + loi.getMessage(), loi);
        }

        return cacHopDongLuuTru;
    }

    /**
     * Ánh xạ kết quả đặt đến hợp đồng lưu trú trong JdbcHopDongLuuTruDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến hợp đồng lưu trú
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private HopDongLuuTru anhXaKetQuaDatDenHopDongLuuTru(ResultSet tapKetQua) throws SQLException {
        // Hợp đồng lưu trú.
        HopDongLuuTru hopDongLuuTru = new HopDongLuuTru();
        hopDongLuuTru.datId(tapKetQua.getLong("id"));
        hopDongLuuTru.datMaHopDong(tapKetQua.getString("ma_dat_phong"));
        hopDongLuuTru.datIdThuCung(tapKetQua.getLong("thu_cung_id"));
        hopDongLuuTru.datIdKhachHang(tapKetQua.getLong("khach_hang_id"));
        hopDongLuuTru.datIdPhong(tapKetQua.getLong("phong_id"));

        // Thời gian nhận phòng.
        Timestamp thoiGianNhanPhong = tapKetQua.getTimestamp("gio_nhan_phong");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiGianNhanPhong != null) {
            hopDongLuuTru.datThoiGianNhanPhong(thoiGianNhanPhong.toLocalDateTime());
        }

        // Dự kiến trả phòng.
        Timestamp duKienTraPhong = tapKetQua.getTimestamp("gio_tra_du_kien");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (duKienTraPhong != null) {
            hopDongLuuTru.datThoiGianTraPhongDuKien(duKienTraPhong.toLocalDateTime());
        }

        // Thực tế trả phòng.
        Timestamp thucTeTraPhong = tapKetQua.getTimestamp("gio_tra_thuc_te");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thucTeTraPhong != null) {
            hopDongLuuTru.datThoiGianTraPhongThucTe(thucTeTraPhong.toLocalDateTime());
        }

        hopDongLuuTru.datKhungThoiGian(tapKetQua.getString("khung_gio"));
        hopDongLuuTru.datGiaCoSo(tapKetQua.getBigDecimal("gia_phong"));
        hopDongLuuTru.datTienCoc(tapKetQua.getBigDecimal("tien_coc"));
        hopDongLuuTru.datPhiTre(tapKetQua.getBigDecimal("phi_tre_han"));
        hopDongLuuTru.datTongTien(tapKetQua.getBigDecimal("tong_tien"));
        hopDongLuuTru.datTrangThai(tapKetQua.getString("trang_thai"));
        hopDongLuuTru.datGhiChu(tapKetQua.getString("ghi_chu"));

        // Id nhân viên.
        long idNhanVien = tapKetQua.getLong("nhan_vien_id");
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (!tapKetQua.wasNull()) {
            hopDongLuuTru.datIdNhanVien(idNhanVien);
        }

        // Thời điểm tạo.
        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_tao");
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (thoiDiemTao != null) {
            hopDongLuuTru.datThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        hopDongLuuTru.datPhienBanBanGhi(tapKetQua.getBytes("row_version"));

        return hopDongLuuTru;
    }
}
