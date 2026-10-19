package com.petstation.dao.impl;

import com.petstation.dao.NhatKyHeThongDAO;
import com.petstation.model.NhatKyHeThong;

import java.sql.Connection;
import java.sql.PreparedStatement;

/** Thực hiện truy vấn JDBC cho JDBC nhật ký hệ thống nhật ký DAO. */
public final class JdbcNhatKyHeThongDAO implements NhatKyHeThongDAO {
    /**
     * Thêm trong JdbcNhatKyHeThongDAO.
     *
     * @param ketNoi kết nối
     * @param muc mục
     * @throws Exception nếu thao tác không thể hoàn tất
     */
    @Override
    public void them(Connection ketNoi, NhatKyHeThong muc) throws Exception {
        // SQL.
        String sql =
                """
                INSERT INTO nhat_ky_he_thong
                    (tai_khoan_id, hanh_dong, loai_doi_tuong, ma_doi_tuong,
                     gia_tri_cu, gia_tri_moi, ghi_chu, thoi_gian)
                VALUES (?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME())
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (muc.idNguoiThucHienTaiKhoan() == null) {
                cauLenh.setNull(1, java.sql.Types.BIGINT);
            } else {
                cauLenh.setLong(1, muc.idNguoiThucHienTaiKhoan());
            }
            cauLenh.setString(2, muc.thaoTac());
            cauLenh.setString(3, muc.kieuThucThe());
            cauLenh.setString(4, muc.idThucThe());
            cauLenh.setString(5, muc.giaTriCu());
            cauLenh.setString(6, muc.giaTriMoi());
            cauLenh.setString(7, muc.ghiChu());
            cauLenh.executeUpdate();
        }
    }
}
