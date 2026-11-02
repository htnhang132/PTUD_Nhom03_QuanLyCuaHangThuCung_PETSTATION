package com.petstation.dao.impl;

import com.petstation.dao.TaiKhoanDAO;
import com.petstation.model.Quyen;
import com.petstation.model.TaiKhoan;
import com.petstation.model.TrangThaiTaiKhoan;
import com.petstation.model.VaiTro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/** Thực hiện truy vấn JDBC cho JDBC người dùng tài khoản DAO. */
public final class TaiKhoanDAOImpl implements TaiKhoanDAO {
    @Override
    public Optional<TaiKhoan> timTheoTenDangNhap(Connection ketNoi, String tenDangNhap)
            throws Exception {
        // SQL.
        String sql = """
                SELECT id, nhan_vien_id, ten_dang_nhap, mat_khau_hash, vai_tro, trang_thai,
                       so_lan_dang_nhap_sai, khoa_den, bat_buoc_doi_mat_khau
                FROM tai_khoan
                WHERE LOWER(ten_dang_nhap) = LOWER(?)
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, tenDangNhap);
            try (ResultSet ketQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (!ketQua.next()) {
                    return Optional.empty();
                }
                // Khóa đến lúc.
                Timestamp khoaDenLuc = ketQua.getTimestamp("khoa_den");
                return Optional.of(
                        new TaiKhoan(
                                ketQua.getLong("id"),
                                ketQua.getLong("nhan_vien_id"),
                                ketQua.getString("ten_dang_nhap"),
                                ketQua.getString("mat_khau_hash"),
                                VaiTro.docTuCSDL(ketQua.getString("vai_tro")),
                                TrangThaiTaiKhoan.docTuCSDL(ketQua.getString("trang_thai")),
                                ketQua.getInt("so_lan_dang_nhap_sai"),
                                khoaDenLuc == null ? null : khoaDenLuc.toLocalDateTime(),
                                ketQua.getBoolean("bat_buoc_doi_mat_khau")));
            }
        }
    }

    @Override
    public Set<Quyen> timCacQuyen(Connection ketNoi, long idTaiKhoan) throws Exception {
        String sql = "SELECT vai_tro, quyen FROM tai_khoan WHERE id = ?";
        Set<Quyen> cacQuyen = EnumSet.noneOf(Quyen.class);
        String tenVaiTro = null;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idTaiKhoan);
            try (ResultSet ketQua = cauLenh.executeQuery()) {
                if (ketQua.next()) {
                    tenVaiTro = ketQua.getString("vai_tro");
                    String chuoiQuyen = ketQua.getString("quyen");
                    if (chuoiQuyen != null && !chuoiQuyen.isBlank()) {
                        for (String ma : chuoiQuyen.split("[,;]")) {
                            if (!ma.isBlank()) {
                                try {
                                    Quyen q = Quyen.docTuCSDL(ma.trim());
                                    if (q != null) cacQuyen.add(q);
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                }
            }
        }

        if (cacQuyen.isEmpty() && tenVaiTro != null) {
            VaiTro vt = VaiTro.docTuCSDL(tenVaiTro);
            try {
                for (Object[] dq : new QuyenVaiTroDAOImpl().timCua(vt.name())) {
                    if (dq != null && dq.length > 0 && dq[0] != null) {
                        try {
                            Quyen q = Quyen.docTuCSDL(String.valueOf(dq[0]));
                            if (q != null) cacQuyen.add(q);
                        } catch (Exception ignored) {
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if (cacQuyen.isEmpty()) {
            String sqlFallback = """
                    SELECT vtq.ma_quyen
                    FROM tai_khoan tk
                    JOIN vai_tro_quyen vtq ON vtq.vai_tro = tk.vai_tro
                    WHERE tk.id = ?
                    """;
            try (PreparedStatement cauLenhFallback = ketNoi.prepareStatement(sqlFallback)) {
                cauLenhFallback.setLong(1, idTaiKhoan);
                try (ResultSet rs = cauLenhFallback.executeQuery()) {
                    while (rs.next()) {
                        try {
                            Quyen q = Quyen.docTuCSDL(rs.getString("ma_quyen"));
                            if (q != null) cacQuyen.add(q);
                        } catch (Exception ignored) {
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return cacQuyen;
    }

    @Override
    public void ghiNhanDangNhapThatBai(
            Connection ketNoi, long idTaiKhoan, int soLuongThatBai, LocalDateTime khoaDenLuc)
            throws Exception {
        // SQL.
        String sql = "UPDATE tai_khoan SET so_lan_dang_nhap_sai = ?, khoa_den = ? WHERE id = ?";
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setInt(1, soLuongThatBai);
            cauLenh.setTimestamp(2, khoaDenLuc == null ? null : Timestamp.valueOf(khoaDenLuc));
            cauLenh.setLong(3, idTaiKhoan);
            cauLenh.executeUpdate();
        }
    }

    @Override
    public void ghiNhanDangNhapThanhCong(Connection ketNoi, long idTaiKhoan) throws Exception {
        // SQL.
        String sql = """
                UPDATE tai_khoan
                SET so_lan_dang_nhap_sai = 0, khoa_den = NULL, dang_nhap_cuoi = SYSUTCDATETIME()
                WHERE id = ?
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idTaiKhoan);
            cauLenh.executeUpdate();
        }
    }

    public Object layMaBamMatKhau(Object... cacThamSo) {
        return com.petstation.database.ThaoTacSQL.layGiaTriDon(
                "SELECT mat_khau_hash FROM tai_khoan WITH(UPDLOCK,HOLDLOCK) WHERE id=?", cacThamSo);
    }

    public int capNhatMatKhau(Object... cacThamSo) {
        return com.petstation.database.ThaoTacSQL.capNhat(
                "UPDATE tai_khoan SET mat_khau_hash=?,bat_buoc_doi_mat_khau=0 WHERE id=?",
                cacThamSo);
    }
}
