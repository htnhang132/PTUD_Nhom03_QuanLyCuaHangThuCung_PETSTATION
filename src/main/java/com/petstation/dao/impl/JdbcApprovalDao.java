package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcApprovalDao {
    private JdbcApprovalDao() {}

    public static long countPendingCount(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM yeu_cau_phe_duyet WHERE trang_thai='PENDING'", parameters);
    }

    public static int updateReject(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phieu_xuat_kho SET trang_thai='REJECTED' WHERE id=?", parameters);
    }

    public static int updateReject2(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE bien_ban_kiem_ke SET trang_thai='REJECTED' WHERE id=?", parameters);
    }

    public static long countApplyApproval(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM hoa_don WHERE id=? AND trang_thai='PAID'", parameters);
    }

    public static int updateApplyApproval(Object... parameters) {
        return SqlDatabase.update("UPDATE hoa_don SET ghi_chu=? WHERE id=?", parameters);
    }

    public static int updateSubmitAction(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                    + " yeu_cau_phe_duyet(ma_yeu_cau,loai,nguoi_gui_id,loai_doi_tuong,ma_doi_tuong,ly_do,noi_dung_truoc,noi_dung_sau,payload,gui_luc)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,SYSDATETIME())",
                parameters);
    }

    public static List<Object[]> findAll(Object... parameters) {
        return SqlDatabase.query(
                "SELECT"
                    + " y.ma_yeu_cau,y.loai,n.ho_ten,y.gui_luc,y.ly_do,y.noi_dung_truoc,y.noi_dung_sau,y.trang_thai,COALESCE(a.ho_ten,N''),COALESCE(y.phan_hoi,N'')"
                    + " FROM yeu_cau_phe_duyet y JOIN tai_khoan t ON t.id=y.nguoi_gui_id JOIN"
                    + " nhan_vien n ON n.id=t.nhan_vien_id LEFT JOIN tai_khoan u ON"
                    + " u.id=y.nguoi_xu_ly_id LEFT JOIN nhan_vien a ON a.id=u.nhan_vien_id ORDER BY"
                    + " y.gui_luc DESC,y.id DESC",
                parameters);
    }

    public static List<Object[]> findApprove(Object... parameters) {
        return SqlDatabase.query(
                "SELECT loai_doi_tuong,ma_doi_tuong,payload FROM"
                        + " yeu_cau_phe_duyet WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " ma_yeu_cau=? AND trang_thai='PENDING'",
                parameters);
    }

    public static int updateApprove(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE yeu_cau_phe_duyet SET"
                        + " trang_thai='APPROVED',nguoi_xu_ly_id=?,xu_ly_luc=SYSDATETIME()"
                        + " WHERE ma_yeu_cau=?",
                parameters);
    }

    public static List<Object[]> findReject(Object... parameters) {
        return SqlDatabase.query(
                "SELECT loai_doi_tuong,ma_doi_tuong FROM yeu_cau_phe_duyet"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_yeu_cau=? AND"
                        + " trang_thai='PENDING'",
                parameters);
    }

    public static int updateReject3(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phieu_doi_tra SET trang_thai='REJECTED' WHERE id=? AND"
                        + " trang_thai='PENDING'",
                parameters);
    }

    public static int updateReject4(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE yeu_cau_phe_duyet SET"
                    + " trang_thai='REJECTED',nguoi_xu_ly_id=?,phan_hoi=?,xu_ly_luc=SYSDATETIME()"
                    + " WHERE ma_yeu_cau=?",
                parameters);
    }
}
