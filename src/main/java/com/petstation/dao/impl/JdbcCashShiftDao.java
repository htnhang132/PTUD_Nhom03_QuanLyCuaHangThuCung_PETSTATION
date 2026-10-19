package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcCashShiftDao {
    private JdbcCashShiftDao() {}

    public static long countClose(Object... parameters) {
        return SqlDatabase.number(
                "SELECT id FROM phien_lam_viec WITH(UPDLOCK,HOLDLOCK) WHERE id=?", parameters);
    }

    public static List<Object[]> findCurrent(Object... parameters) {
        return SqlDatabase.query(
                "SELECT id,ten_ca,mo_luc,tien_dau_ca FROM phien_lam_viec WHERE"
                        + " tai_khoan_id=? AND trang_thai='OPEN'",
                parameters);
    }

    public static long countSales(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COALESCE(SUM(h.tong_tien),0) FROM hoa_don h JOIN don_hang d ON"
                    + " d.id=h.don_hang_id WHERE d.phien_lam_viec_id=? AND h.hinh_thuc_thanh_toan=?"
                    + " AND h.trang_thai='PAID'",
                parameters);
    }

    public static long countInvoiceCount(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE"
                        + " d.phien_lam_viec_id=? AND h.trang_thai='PAID'",
                parameters);
    }

    public static long countExpectedCash(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COALESCE(SUM(-p.chenh_lech),0) FROM phieu_doi_tra p JOIN hoa_don h"
                        + " ON h.id=p.hoa_don_id JOIN don_hang d ON d.id=h.don_hang_id WHERE"
                        + " d.phien_lam_viec_id=? AND p.trang_thai='COMPLETED' AND"
                        + " h.hinh_thuc_thanh_toan='CASH'",
                parameters);
    }

    public static int updateOpen(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO phien_lam_viec(tai_khoan_id,ten_ca,tien_dau_ca,mo_luc)"
                        + " VALUES(?,?,?,SYSDATETIME())",
                parameters);
    }

    public static int updateClose(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phien_lam_viec SET"
                    + " tien_thuc_te=?,chenh_lech=?,giai_trinh=?,dong_luc=SYSDATETIME(),trang_thai='HANDED_OVER'"
                    + " WHERE id=? AND trang_thai='OPEN'",
                parameters);
    }
}
