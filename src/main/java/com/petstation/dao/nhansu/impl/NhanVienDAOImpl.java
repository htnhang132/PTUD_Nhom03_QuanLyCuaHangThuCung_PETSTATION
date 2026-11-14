package com.petstation.dao.nhansu.impl;

import com.petstation.dao.nhansu.NhanVienDAO;
import com.petstation.model.nhansu.NhanVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public final class NhanVienDAOImpl implements NhanVienDAO {
    @Override
    public Optional<NhanVien> timTheoId(Connection ketNoi, String maNhanVien) throws Exception {
        String sql =
                """
                SELECT ma_nhan_vien, ho_ten, so_dien_thoai, chuc_vu,
                       ngay_vao_lam, dang_lam_viec, cccd
                FROM nhan_vien
                WHERE ma_nhan_vien = ?
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maNhanVien);
            try (ResultSet ketQua = cauLenh.executeQuery()) {
                if (!ketQua.next()) {
                    return Optional.empty();
                }
                return Optional.of(
                        new NhanVien(
                                ketQua.getString("ma_nhan_vien"),
                                ketQua.getString("ho_ten"),
                                ketQua.getString("so_dien_thoai"),
                                ketQua.getString("chuc_vu"),
                                ketQua.getDate("ngay_vao_lam").toLocalDate(),
                                ketQua.getBoolean("dang_lam_viec"),
                                ketQua.getString("cccd")));
            }
        }
    }

    @Override
    public Optional<NhanVien> timTheoMa(Connection ketNoi, String maNhanVien) throws Exception {
        String sql =
                """
                SELECT ma_nhan_vien, ho_ten, so_dien_thoai, chuc_vu,
                       ngay_vao_lam, dang_lam_viec, cccd
                FROM nhan_vien
                WHERE ma_nhan_vien = ?
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maNhanVien);
            try (ResultSet ketQua = cauLenh.executeQuery()) {
                if (!ketQua.next()) {
                    return Optional.empty();
                }
                return Optional.of(
                        new NhanVien(
                                ketQua.getString("ma_nhan_vien"),
                                ketQua.getString("ho_ten"),
                                ketQua.getString("so_dien_thoai"),
                                ketQua.getString("chuc_vu"),
                                ketQua.getDate("ngay_vao_lam").toLocalDate(),
                                ketQua.getBoolean("dang_lam_viec"),
                                ketQua.getString("cccd")));
            }
        }
    }
}
