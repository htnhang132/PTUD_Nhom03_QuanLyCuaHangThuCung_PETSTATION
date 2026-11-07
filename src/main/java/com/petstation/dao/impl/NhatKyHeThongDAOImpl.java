package com.petstation.dao.impl;

import com.petstation.dao.NhatKyHeThongDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.NhatKyHeThong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public final class NhatKyHeThongDAOImpl implements NhatKyHeThongDAO {
    @Override
    public void them(Connection ketNoi, NhatKyHeThong muc) throws Exception {
        String sql =
                """
                INSERT INTO nhat_ky_he_thong
                    (ma_nhat_ky, ten_dang_nhap, hanh_dong, loai_doi_tuong, ma_doi_tuong,
                     gia_tri_cu, gia_tri_moi, ghi_chu, thoi_gian)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME())
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, muc.getMaNhatKy());
            if (muc.getNguoiThucHien() == null) {
                cauLenh.setNull(2, java.sql.Types.VARCHAR);
            } else {
                cauLenh.setString(2, muc.getNguoiThucHien().getTenDangNhap());
            }
            cauLenh.setString(3, muc.getThaoTac());
            cauLenh.setString(4, muc.getKieuThucThe());
            cauLenh.setString(5, muc.getIdThucThe());
            cauLenh.setString(6, muc.getGiaTriCu());
            cauLenh.setString(7, muc.getGiaTriMoi());
            cauLenh.setString(8, muc.getGhiChu());
            cauLenh.executeUpdate();
        }
    }

    public int capNhatNhatKy(Object... cacThamSo) {
        Object[] thamSoDayDu = new Object[cacThamSo.length + 1];
        thamSoDayDu[0] = com.petstation.util.DuLieu.taoMa("LOG");
        System.arraycopy(cacThamSo, 0, thamSoDayDu, 1, cacThamSo.length);
        return ThaoTacSQL.capNhat(
                "INSERT INTO nhat_ky_he_thong"
                    + "(ma_nhat_ky,ten_dang_nhap,hanh_dong,ghi_chu,nguoi_dung,loai_su_kien,ket_qua,thoi_gian)"
                    + " VALUES(?,?,?,?,?,?,?,SYSDATETIME())",
                thamSoDayDu);
    }

    public List<Object[]> timCacMuc(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT TOP(2000) a.thoi_gian,COALESCE(a.nguoi_dung,n.ho_ten,N'Hệ"
                    + " thống'),a.hanh_dong,COALESCE(a.ghi_chu,a.ma_doi_tuong,N''),COALESCE(a.loai_su_kien,'SYSTEM'),COALESCE(a.ket_qua,N'Thành"
                    + " công') FROM nhat_ky_he_thong a LEFT JOIN tai_khoan t ON"
                    + " t.ten_dang_nhap=a.ten_dang_nhap LEFT JOIN nhan_vien n ON"
                    + " n.ma_nhan_vien=t.ma_nhan_vien ORDER BY a.thoi_gian DESC,a.ma_nhat_ky DESC",
                cacThamSo);
    }
}
