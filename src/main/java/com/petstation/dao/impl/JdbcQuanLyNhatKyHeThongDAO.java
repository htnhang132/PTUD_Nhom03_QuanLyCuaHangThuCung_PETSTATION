package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC nhật ký hệ thống DAO. */
public final class JdbcQuanLyNhatKyHeThongDAO {
    /** Khởi tạo JdbcQuanLyNhatKyHeThongDAO với các giá trị được cung cấp. */
    private JdbcQuanLyNhatKyHeThongDAO() {}

    /**
     * Cập nhật nhật ký trong JdbcQuanLyNhatKyHeThongDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật nhật ký
     */
    public static int capNhatNhatKy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO"
                    + " nhat_ky_he_thong(tai_khoan_id,hanh_dong,ghi_chu,nguoi_dung,loai_su_kien,ket_qua,thoi_gian)"
                    + " VALUES(?,?,?,?,?,?,SYSDATETIME())",
                cacThamSo);
    }

    /**
     * Tìm các mục trong JdbcQuanLyNhatKyHeThongDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm các mục
     */
    public static List<Object[]> timCacMuc(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT TOP(2000) a.thoi_gian,COALESCE(a.nguoi_dung,n.ho_ten,N'Hệ"
                    + " thống'),a.hanh_dong,COALESCE(a.ghi_chu,a.ma_doi_tuong,N''),COALESCE(a.loai_su_kien,'SYSTEM'),COALESCE(a.ket_qua,N'Thành"
                    + " công') FROM nhat_ky_he_thong a LEFT JOIN tai_khoan t ON t.id=a.tai_khoan_id"
                    + " LEFT JOIN nhan_vien n ON n.id=t.nhan_vien_id ORDER BY a.thoi_gian DESC,a.id"
                    + " DESC",
                cacThamSo);
    }
}
