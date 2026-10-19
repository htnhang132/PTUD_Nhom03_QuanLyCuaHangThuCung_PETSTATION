package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

/** Thực hiện truy vấn JDBC cho JDBC cài đặt DAO. */
public final class JdbcCaiDatDAO {
    /** Khởi tạo JdbcCaiDatDAO với các giá trị được cung cấp. */
    private JdbcCaiDatDAO() {}

    /**
     * Tìm giá trị trong JdbcCaiDatDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm giá trị
     */
    public static Object timGiaTri(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon("SELECT gia_tri FROM cai_dat WHERE khoa=?", cacThamSo);
    }

    /**
     * Thêm giá trị trong JdbcCaiDatDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả thêm giá trị
     */
    public static int themGiaTri(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO cai_dat(khoa,gia_tri,nguoi_cap_nhat_id) VALUES(?,?,?)", cacThamSo);
    }

    /**
     * Cập nhật giá trị trong JdbcCaiDatDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật giá trị
     */
    public static int capNhatGiaTri(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE cai_dat WITH(UPDLOCK,HOLDLOCK) SET"
                        + " gia_tri=?,nguoi_cap_nhat_id=?,ngay_cap_nhat=SYSDATETIME()"
                        + " WHERE khoa=?",
                cacThamSo);
    }
}
