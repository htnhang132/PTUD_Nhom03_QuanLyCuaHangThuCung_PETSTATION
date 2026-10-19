package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC ca thu ngân DAO. */
public final class JdbcCaThuNganDAO {
    /** Khởi tạo JdbcCaThuNganDAO với các giá trị được cung cấp. */
    private JdbcCaThuNganDAO() {}

    /**
     * Đếm đóng trong JdbcCaThuNganDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm đóng
     */
    public static long demDong(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT id FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE id=?", cacThamSo);
    }

    /**
     * Tìm hiện tại trong JdbcCaThuNganDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm hiện tại
     */
    public static List<Object[]> timHienTai(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,ten_ca,mo_luc,tien_dau_ca FROM phien_lam_viec WHERE"
                        + " tai_khoan_id=? AND trang_thai='OPEN'",
                cacThamSo);
    }

    /**
     * Đếm bán hàng trong JdbcCaThuNganDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm bán hàng
     */
    public static long demBanHang(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(h.tong_tien),0) FROM hoa_don h JOIN don_hang d ON"
                    + " d.id=h.don_hang_id WHERE d.phien_lam_viec_id=? AND h.hinh_thuc_thanh_toan=?"
                    + " AND h.trang_thai='PAID'",
                cacThamSo);
    }

    /**
     * Đếm số lượng hóa đơn trong JdbcCaThuNganDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm số lượng hóa đơn
     */
    public static long demSoLuongHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE"
                        + " d.phien_lam_viec_id=? AND h.trang_thai='PAID'",
                cacThamSo);
    }

    /**
     * Đếm dự kiến tiền mặt trong JdbcCaThuNganDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả đếm dự kiến tiền mặt
     */
    public static long demDuKienTienMat(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(-p.chenh_lech),0) FROM phieu_doi_tra p JOIN hoa_don h"
                        + " ON h.id=p.hoa_don_id JOIN don_hang d ON d.id=h.don_hang_id WHERE"
                        + " d.phien_lam_viec_id=? AND p.trang_thai='COMPLETED' AND"
                        + " h.hinh_thuc_thanh_toan='CASH'",
                cacThamSo);
    }

    /**
     * Cập nhật mở trong JdbcCaThuNganDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật mở
     */
    public static int capNhatMo(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO phien_lam_viec(tai_khoan_id,ten_ca,tien_dau_ca,mo_luc)"
                        + " VALUES(?,?,?,SYSDATETIME())",
                cacThamSo);
    }

    /**
     * Cập nhật đóng trong JdbcCaThuNganDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả cập nhật đóng
     */
    public static int capNhatDong(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phien_lam_viec SET"
                    + " tien_thuc_te=?,chenh_lech=?,giai_trinh=?,dong_luc=SYSDATETIME(),trang_thai='HANDED_OVER'"
                    + " WHERE id=? AND trang_thai='OPEN'",
                cacThamSo);
    }
}
