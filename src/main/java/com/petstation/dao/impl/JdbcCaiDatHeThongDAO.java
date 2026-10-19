package com.petstation.dao.impl;

import com.petstation.dao.CaiDatHeThongDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.CaiDatHeThong;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC hệ thống cài đặt DAO. */
public class JdbcCaiDatHeThongDAO implements CaiDatHeThongDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcCaiDatHeThongDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcCaiDatHeThongDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcCaiDatHeThongDAO.
     *
     * @param caiDat cài đặt
     * @return kết quả tạo
     */
    @Override
    public CaiDatHeThong tao(CaiDatHeThong caiDat) {
        // SQL.
        String sql =
                "INSERT INTO cai_dat (khoa, gia_tri, kieu_du_lieu, nhom, mo_ta, "
                        + "nguoi_cap_nhat_id, ngay_cap_nhat) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setString(1, caiDat.layKhoa());
            cauLenh.setString(2, caiDat.layGiaTri());
            cauLenh.setString(
                    3, caiDat.layKieuDuLieu() != null ? caiDat.layKieuDuLieu() : "STRING");
            cauLenh.setString(4, caiDat.layNhom());
            cauLenh.setString(5, caiDat.layMoTa());
            cauLenh.setObject(6, caiDat.layNguoiCapNhatCuoi());
            cauLenh.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    caiDat.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return caiDat;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error creating system setting: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcCaiDatHeThongDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<CaiDatHeThong> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT cai_dat_id, khoa, gia_tri, kieu_du_lieu, nhom, mo_ta, nguoi_cap_nhat_id,"
                        + " ngay_cap_nhat, rowversion FROM cai_dat WHERE cai_dat_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenCaiDat(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding setting: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo khóa trong JdbcCaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @return kết quả tìm theo khóa
     */
    @Override
    public Optional<CaiDatHeThong> timTheoKhoa(String khoa) {
        // SQL.
        String sql =
                "SELECT cai_dat_id, khoa, gia_tri, kieu_du_lieu, nhom, mo_ta, nguoi_cap_nhat_id,"
                        + " ngay_cap_nhat, rowversion FROM cai_dat WHERE khoa = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, khoa);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenCaiDat(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding setting by key: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Lấy giá trị trong JdbcCaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @return kết quả lấy giá trị
     */
    @Override
    public String layGiaTri(String khoa) {
        // SQL.
        String sql = "SELECT gia_tri FROM cai_dat WHERE khoa = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, khoa);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getString("gia_tri");
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error getting setting value: " + loi.getMessage(), loi);
        }

        return null;
    }

    /**
     * Lấy giá trị hoặc mặc định trong JdbcCaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @param giaTriMacDinh giá trị mặc định
     * @return kết quả lấy giá trị hoặc mặc định
     */
    @Override
    public String layGiaTriHoacMacDinh(String khoa, String giaTriMacDinh) {
        // Giá trị.
        String giaTri = layGiaTri(khoa);
        return giaTri != null ? giaTri : giaTriMacDinh;
    }

    /**
     * Tìm theo nhóm trong JdbcCaiDatHeThongDAO.
     *
     * @param nhom nhóm
     * @return kết quả tìm theo nhóm
     */
    @Override
    public List<CaiDatHeThong> timTheoNhom(String nhom) {
        // SQL.
        String sql =
                "SELECT cai_dat_id, khoa, gia_tri, kieu_du_lieu, nhom, mo_ta, nguoi_cap_nhat_id,"
                        + " ngay_cap_nhat, rowversion FROM cai_dat WHERE nhom = ? ORDER BY khoa";

        // Cài đặt.
        List<CaiDatHeThong> caiDat = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, nhom);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    caiDat.add(anhXaKetQuaDatDenCaiDat(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding settings by group: " + loi.getMessage(), loi);
        }

        return caiDat;
    }

    /**
     * Tìm tất cả trong JdbcCaiDatHeThongDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<CaiDatHeThong> timTatCa() {
        // SQL.
        String sql =
                "SELECT cai_dat_id, khoa, gia_tri, kieu_du_lieu, nhom, mo_ta, nguoi_cap_nhat_id,"
                        + " ngay_cap_nhat, rowversion FROM cai_dat ORDER BY nhom, khoa";

        // Cài đặt.
        List<CaiDatHeThong> caiDat = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                caiDat.add(anhXaKetQuaDatDenCaiDat(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all settings: " + loi.getMessage(), loi);
        }

        return caiDat;
    }

    /**
     * Cập nhật trong JdbcCaiDatHeThongDAO.
     *
     * @param caiDat cài đặt
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhat(CaiDatHeThong caiDat) {
        // SQL.
        String sql =
                "UPDATE cai_dat SET gia_tri = ?, mo_ta = ?, nguoi_cap_nhat_id = ?, "
                        + "ngay_cap_nhat = ? WHERE cai_dat_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, caiDat.layGiaTri());
            cauLenh.setString(2, caiDat.layMoTa());
            cauLenh.setObject(3, caiDat.layNguoiCapNhatCuoi());
            cauLenh.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            cauLenh.setLong(5, caiDat.layId());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating setting: " + loi.getMessage(), loi);
        }
    }

    /**
     * Cập nhật giá trị trong JdbcCaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @param giaTri giá trị
     * @param daCapNhatTheo đã cập nhật theo
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean capNhatGiaTri(String khoa, String giaTri, Long daCapNhatTheo) {
        // SQL.
        String sql =
                "UPDATE cai_dat SET gia_tri = ?, nguoi_cap_nhat_id = ?, ngay_cap_nhat = ? WHERE"
                        + " khoa = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, giaTri);
            cauLenh.setObject(2, daCapNhatTheo);
            cauLenh.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            cauLenh.setString(4, khoa);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating setting value: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa trong JdbcCaiDatHeThongDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM cai_dat WHERE cai_dat_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting setting: " + loi.getMessage(), loi);
        }
    }

    /**
     * Xóa theo khóa trong JdbcCaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoaTheoKhoa(String khoa) {
        // SQL.
        String sql = "DELETE FROM cai_dat WHERE khoa = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, khoa);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error deleting setting by key: " + loi.getMessage(), loi);
        }
    }

    /**
     * Lấy tồn tại theo khóa trong JdbcCaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean layTonTaiTheoKhoa(String khoa) {
        // SQL.
        String sql = "SELECT COUNT(*) FROM cai_dat WHERE khoa = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, khoa);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return tapKetQua.getInt(1) > 0;
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error checking setting existence: " + loi.getMessage(), loi);
        }

        return false;
    }

    /**
     * Đếm trong JdbcCaiDatHeThongDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM cai_dat";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting settings: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ kết quả đặt đến cài đặt trong JdbcCaiDatHeThongDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ kết quả đặt đến cài đặt
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private CaiDatHeThong anhXaKetQuaDatDenCaiDat(ResultSet tapKetQua) throws SQLException {
        // Cài đặt.
        CaiDatHeThong caiDat = new CaiDatHeThong();
        caiDat.datId(tapKetQua.getLong("cai_dat_id"));
        caiDat.datKhoa(tapKetQua.getString("khoa"));
        caiDat.datGiaTri(tapKetQua.getString("gia_tri"));
        caiDat.datKieuDuLieu(tapKetQua.getString("kieu_du_lieu"));
        caiDat.datNhom(tapKetQua.getString("nhom"));
        caiDat.datMoTa(tapKetQua.getString("mo_ta"));

        // Đã cập nhật theo.
        long daCapNhatTheo = tapKetQua.getLong("nguoi_cap_nhat_id");
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (!tapKetQua.wasNull()) {
            caiDat.datNguoiCapNhatCuoi(daCapNhatTheo);
        }

        // Thời điểm cập nhật.
        Timestamp thoiDiemCapNhat = tapKetQua.getTimestamp("ngay_cap_nhat");
        // Kiểm tra số bản ghi chịu tác động để phát hiện cập nhật thất bại.
        if (thoiDiemCapNhat != null) {
            caiDat.datThoiDiemCapNhatCuoi(thoiDiemCapNhat.toLocalDateTime());
        }

        caiDat.datPhienBanBanGhi(tapKetQua.getBytes("rowversion"));

        return caiDat;
    }
}
