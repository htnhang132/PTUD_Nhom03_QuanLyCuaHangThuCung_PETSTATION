package com.petstation.dao.impl;

import com.petstation.dao.NhanVienDAO;
import com.petstation.model.NhanVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC nhân viên DAO. */
public final class JdbcNhanVienDAO implements NhanVienDAO {
    /**
     * Tìm theo id trong JdbcNhanVienDAO.
     *
     * @param ketNoi kết nối
     * @param idNhanVien id nhân viên
     * @return kết quả tìm theo id
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @Override
    public Optional<NhanVien> timTheoId(Connection ketNoi, long idNhanVien) throws Exception {
        // SQL.
        String sql =
                """
                SELECT id, ma_nhan_vien, ho_ten, ngay_sinh, so_dien_thoai, dia_chi,
                       chuc_vu, ngay_vao_lam, dang_lam_viec
                FROM nhan_vien
                WHERE id = ?
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idNhanVien);
            try (ResultSet ketQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (!ketQua.next()) {
                    return Optional.empty();
                }
                return Optional.of(
                        new NhanVien(
                                ketQua.getLong("id"),
                                ketQua.getString("ma_nhan_vien"),
                                ketQua.getString("ho_ten"),
                                ketQua.getDate("ngay_sinh") == null
                                        ? null
                                        : ketQua.getDate("ngay_sinh").toLocalDate(),
                                ketQua.getString("so_dien_thoai"),
                                ketQua.getString("dia_chi"),
                                ketQua.getString("chuc_vu"),
                                ketQua.getDate("ngay_vao_lam").toLocalDate(),
                                ketQua.getBoolean("dang_lam_viec")));
            }
        }
    }
}
