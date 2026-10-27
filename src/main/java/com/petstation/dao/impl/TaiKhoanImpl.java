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
public final class TaiKhoanImpl implements TaiKhoanDAO {
    @Override
    public Optional<TaiKhoan> timTheoTenDangNhap(Connection ketNoi, String tenDangNhap)
            throws Exception {
        // SQL.
        String sql =
                """
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
                                TrangThaiTaiKhoan.valueOf(ketQua.getString("trang_thai")),
                                ketQua.getInt("so_lan_dang_nhap_sai"),
                                khoaDenLuc == null ? null : khoaDenLuc.toLocalDateTime(),
                                ketQua.getBoolean("bat_buoc_doi_mat_khau")));
            }
        }
    }

    @Override
    public Set<Quyen> timCacQuyen(Connection ketNoi, long idTaiKhoan) throws Exception {
        // SQL.
        String sql =
                """
                SELECT q.ma_quyen
                FROM tai_khoan tk
                JOIN vai_tro_quyen vtq ON vtq.vai_tro = tk.vai_tro
                JOIN quyen q ON q.ma_quyen = vtq.ma_quyen
                WHERE tk.id = ?
                """;
        // Các quyền.
        Set<Quyen> cacQuyen = EnumSet.noneOf(Quyen.class);
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idTaiKhoan);
            try (ResultSet ketQua = cauLenh.executeQuery()) {
                while (ketQua.next()) {
                    cacQuyen.add(Quyen.valueOf(ketQua.getString("ma_quyen")));
                }
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
        String sql =
                """
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
