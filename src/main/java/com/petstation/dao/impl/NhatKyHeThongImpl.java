package com.petstation.dao.impl;

import com.petstation.dao.NhatKyHeThongDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.NhatKyHeThong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC nhật ký hệ thống DAO. */
public final class NhatKyHeThongImpl implements NhatKyHeThongDAO {
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

    public int capNhatNhatKy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " nhat_ky_he_thong(tai_khoan_id,hanh_dong,ghi_chu,nguoi_dung,loai_su_kien,ket_qua,thoi_gian)"
                    + " VALUES(?,?,?,?,?,?,SYSDATETIME())",
                cacThamSo);
    }

    public List<Object[]> timCacMuc(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT TOP(2000) a.thoi_gian,COALESCE(a.nguoi_dung,n.ho_ten,N'Hệ"
                    + " thống'),a.hanh_dong,COALESCE(a.ghi_chu,a.ma_doi_tuong,N''),COALESCE(a.loai_su_kien,'SYSTEM'),COALESCE(a.ket_qua,N'Thành"
                    + " công') FROM nhat_ky_he_thong a LEFT JOIN tai_khoan t ON t.id=a.tai_khoan_id"
                    + " LEFT JOIN nhan_vien n ON n.id=t.nhan_vien_id ORDER BY a.thoi_gian DESC,a.id"
                    + " DESC",
                cacThamSo);
    }
}
