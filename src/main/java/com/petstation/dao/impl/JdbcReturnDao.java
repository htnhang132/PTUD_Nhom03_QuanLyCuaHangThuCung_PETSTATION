package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcReturnDao {
    private JdbcReturnDao() {}

    public static List<Object[]> findApplyReturn(Object... parameters) {
        return SqlDatabase.query(
                "SELECT lo_hang_id,so_luong FROM chi_tiet_doi_tra WHERE phieu_doi_tra_id=?",
                parameters);
    }

    public static int updateApplyReturn(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE id=?", parameters);
    }

    public static int updateApplyReturn2(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phieu_doi_tra SET trang_thai='COMPLETED' WHERE id=?", parameters);
    }

    public static int updateApplyReturn3(Object... parameters) {
        return SqlDatabase.update("UPDATE khach_hang SET diem_tich_luy=? WHERE id=?", parameters);
    }

    public static List<Object[]> findReturnItem(Object... parameters) {
        return SqlDatabase.query(
                "SELECT"
                    + " h.id,h.tong_tien,h.tam_tinh,h.thanh_toan_luc,i.don_gia,i.so_luong,h.trang_thai"
                    + " FROM chi_tiet_don_hang i WITH(UPDLOCK,HOLDLOCK) JOIN hoa_don h ON"
                    + " h.don_hang_id=i.don_hang_id WHERE i.id=? AND i.san_pham_id IS NOT NULL",
                parameters);
    }

    public static long countReturnItem(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COALESCE(SUM(c.so_luong),0) FROM chi_tiet_doi_tra c"
                        + " JOIN phieu_doi_tra p ON p.id=c.phieu_doi_tra_id WHERE"
                        + " c.chi_tiet_don_hang_id=? AND p.trang_thai IN"
                        + " ('PENDING','COMPLETED')",
                parameters);
    }

    public static long insertReturnItem(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO"
                    + " phieu_doi_tra(ma_phieu,hoa_don_id,nguoi_lap_id,chenh_lech,trang_thai,ly_do)"
                    + " VALUES(?,?,?,?,'PENDING',?)",
                parameters);
    }

    public static List<Object[]> findReturnItem2(Object... parameters) {
        return SqlDatabase.query(
                "SELECT b.lo_hang_id,b.so_luong-COALESCE((SELECT"
                        + " SUM(c.so_luong) FROM chi_tiet_doi_tra c JOIN"
                        + " phieu_doi_tra p ON p.id=c.phieu_doi_tra_id WHERE"
                        + " c.chi_tiet_don_hang_id=b.chi_tiet_don_hang_id AND"
                        + " c.lo_hang_id=b.lo_hang_id AND p.trang_thai IN"
                        + " ('PENDING','COMPLETED')),0) FROM phan_bo_lo_hang b"
                        + " WHERE b.chi_tiet_don_hang_id=? ORDER BY b.id",
                parameters);
    }

    public static int updateReturnItem(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                    + " chi_tiet_doi_tra(phieu_doi_tra_id,chi_tiet_don_hang_id,lo_hang_id,so_luong,tien_hoan)"
                    + " VALUES(?,?,?,?,?)",
                parameters);
    }

    public static long countApplyReturn(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM phieu_doi_tra WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " trang_thai='PENDING'",
                parameters);
    }

    public static List<Object[]> findApplyReturn2(Object... parameters) {
        return SqlDatabase.query(
                "SELECT"
                        + " h.id,h.diem_cong,h.diem_dung,h.tong_tien,d.khach_hang_id,p.chenh_lech"
                        + " FROM phieu_doi_tra p JOIN hoa_don h WITH(UPDLOCK,HOLDLOCK)"
                        + " ON h.id=p.hoa_don_id JOIN don_hang d ON d.id=h.don_hang_id"
                        + " WHERE p.id=? AND h.trang_thai='PAID'",
                parameters);
    }

    public static long countApplyReturn2(Object... parameters) {
        return SqlDatabase.number(
                "SELECT diem_tich_luy FROM khach_hang WITH(UPDLOCK,HOLDLOCK) WHERE" + " id=?",
                parameters);
    }

    public static int updateApplyReturn4(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO lich_su_diem(khach_hang_id,hoa_don_id,thay_doi,so_du_sau,ly_do)"
                        + " VALUES(?,?,?,?,N'Đổi trả hàng')",
                parameters);
    }
}
