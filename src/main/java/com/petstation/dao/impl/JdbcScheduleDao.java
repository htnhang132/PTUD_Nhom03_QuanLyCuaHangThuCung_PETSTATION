package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcScheduleDao {
    private JdbcScheduleDao() {}

    public static long countAssign(Object... parameters) {
        return SqlDatabase.number(
                "SELECT id FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE id=?", parameters);
    }

    public static long countAssign2(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM nhan_vien WHERE id=? AND dang_lam_viec=1", parameters);
    }

    public static long countMarkLeaveApproved(Object... parameters) {
        return SqlDatabase.number(
                "SELECT id FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE id=?", parameters);
    }

    public static List<Object[]> findRange(Object... parameters) {
        return SqlDatabase.query(
                "SELECT l.ngay_lam,c.ma_ca,n.ma_nhan_vien,l.loai FROM lich_lam_viec l JOIN"
                        + " ca_lam_viec c ON c.id=l.ca_lam_viec_id JOIN nhan_vien n ON"
                        + " n.id=l.nhan_vien_id WHERE l.ngay_lam>=? AND l.ngay_lam<? AND"
                        + " l.trang_thai<>'CANCELLED' ORDER BY"
                        + " l.ngay_lam,c.gio_bat_dau,n.ma_nhan_vien",
                parameters);
    }

    public static long countAssign3(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM lich_lam_viec WHERE nhan_vien_id=? AND"
                        + " ngay_lam=? AND trang_thai<>'CANCELLED' AND"
                        + " (loai='LEAVE' OR ca_lam_viec_id=(SELECT id FROM"
                        + " ca_lam_viec WHERE ma_ca=?))",
                parameters);
    }

    public static int updateAssign(Object... parameters) {
        return SqlDatabase.update(
                "DELETE FROM lich_lam_viec WHERE nhan_vien_id=? AND ngay_lam=? AND"
                        + " ca_lam_viec_id=(SELECT id FROM ca_lam_viec WHERE ma_ca=?) AND"
                        + " trang_thai='CANCELLED'",
                parameters);
    }

    public static int updateAssign2(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO lich_lam_viec(nhan_vien_id,ngay_lam,ca_lam_viec_id,loai)"
                        + " SELECT ?,?,id,'WORK' FROM ca_lam_viec WHERE ma_ca=?",
                parameters);
    }

    public static int updateRemove(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lich_lam_viec SET trang_thai='CANCELLED' WHERE nhan_vien_id=(SELECT id FROM"
                    + " nhan_vien WHERE ma_nhan_vien=?) AND ngay_lam=? AND ca_lam_viec_id=(SELECT"
                    + " id FROM ca_lam_viec WHERE ma_ca=?)",
                parameters);
    }

    public static int updateMarkLeaveApproved(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lich_lam_viec SET loai='LEAVE',trang_thai='OPEN' WHERE"
                        + " nhan_vien_id=? AND ngay_lam=?",
                parameters);
    }

    public static int updateMarkLeaveApproved2(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                        + " lich_lam_viec(nhan_vien_id,ngay_lam,ca_lam_viec_id,loai)"
                        + " SELECT ?,?,id,'LEAVE' FROM ca_lam_viec WHERE"
                        + " ma_ca='CA_SANG'",
                parameters);
    }

    public static long countMonthHours(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COALESCE(SUM(DATEDIFF(minute,c.gio_bat_dau,c.gio_ket_thuc)),0)/60"
                        + " FROM lich_lam_viec l JOIN ca_lam_viec c ON c.id=l.ca_lam_viec_id"
                        + " JOIN nhan_vien n ON n.id=l.nhan_vien_id WHERE n.ma_nhan_vien=? AND"
                        + " l.ngay_lam>=? AND l.ngay_lam<? AND l.loai='WORK' AND"
                        + " l.trang_thai<>'CANCELLED'",
                parameters);
    }
}
