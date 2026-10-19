package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcInventoryDao {
    private JdbcInventoryDao() {}

    public static int updatePurchase(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+? WHERE id=?", parameters);
    }

    public static int updatePurchase2(Object... parameters) {
        return SqlDatabase.update("UPDATE san_pham SET gia_nhap_gan_nhat=? WHERE id=?", parameters);
    }

    public static List<Object[]> findApplyWriteOff(Object... parameters) {
        return SqlDatabase.query(
                "SELECT lo_hang_id,so_luong FROM chi_tiet_phieu_xuat WHERE phieu_xuat_id=?",
                parameters);
    }

    public static int updateApplyWriteOff(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phieu_xuat_kho SET trang_thai='POSTED' WHERE id=?", parameters);
    }

    public static int updateApplyStocktake(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lo_hang SET so_luong_ton=? WHERE id=? AND so_luong_ton=?", parameters);
    }

    public static int updateApplyStocktake2(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE bien_ban_kiem_ke SET trang_thai='APPROVED' WHERE id=?", parameters);
    }

    public static long countPurchase(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM san_pham WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE id=? AND theo_doi_hsd=1",
                parameters);
    }

    public static long countPurchase2(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM nha_cung_cap WHERE id=? AND" + " trang_thai='COOPERATING'",
                parameters);
    }

    public static long insertPurchase(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO"
                    + " phieu_nhap_kho(so_phieu,nha_cung_cap_id,nguoi_lap_id,ngay_nhap,tong_tien,trang_thai,da_tra)"
                    + " VALUES(?,?,?,CAST(GETDATE() AS DATE),?,'POSTED',?)",
                parameters);
    }

    public static int updatePurchase3(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                    + " chi_tiet_phieu_nhap(phieu_nhap_id,san_pham_id,so_lo,han_su_dung,so_luong,gia_nhap)"
                    + " VALUES(?,?,?,?,?,?)",
                parameters);
    }

    public static List<Object[]> findPurchase(Object... parameters) {
        return SqlDatabase.query(
                "SELECT id,han_su_dung FROM lo_hang WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE san_pham_id=? AND so_lo=?",
                parameters);
    }

    public static int updatePurchase4(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton)"
                        + " VALUES(?,?,?,?)",
                parameters);
    }

    public static List<Object[]> findWriteOff(Object... parameters) {
        return SqlDatabase.query(
                "SELECT l.so_luong_ton,COALESCE(p.gia_nhap_gan_nhat,p.gia_ban)"
                        + " FROM lo_hang l WITH(UPDLOCK,HOLDLOCK) JOIN san_pham p"
                        + " ON p.id=l.san_pham_id WHERE l.id=?",
                parameters);
    }

    public static long insertWriteOff(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO"
                    + " phieu_xuat_kho(so_phieu,nguoi_lap_id,loai_xuat,ly_do,tong_gia_tri,trang_thai)"
                    + " VALUES(?,?,'WRITE_OFF',?,?,'DRAFT')",
                parameters);
    }

    public static int updateWriteOff(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                        + " chi_tiet_phieu_xuat(phieu_xuat_id,lo_hang_id,so_luong,gia_tri)"
                        + " VALUES(?,?,?,?)",
                parameters);
    }

    public static long countApplyWriteOff(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM phieu_xuat_kho WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " trang_thai='DRAFT'",
                parameters);
    }

    public static int updateApplyWriteOff2(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton-? WHERE id=? AND"
                        + " so_luong_ton>=?",
                parameters);
    }

    public static List<Object[]> findStocktake(Object... parameters) {
        return SqlDatabase.query(
                "SELECT so_luong_ton FROM lo_hang WITH(UPDLOCK,HOLDLOCK) WHERE" + " id=?",
                parameters);
    }

    public static long insertStocktake(Object... parameters) {
        return SqlDatabase.insert(
                "INSERT INTO"
                        + " bien_ban_kiem_ke(so_bien_ban,nguoi_lap_id,ky_kiem_ke,trang_thai)"
                        + " VALUES(?,?,?,?)",
                parameters);
    }

    public static int updateStocktake(Object... parameters) {
        return SqlDatabase.update(
                "INSERT INTO"
                    + " chi_tiet_kiem_ke(bien_ban_id,lo_hang_id,ton_he_thong,ton_thuc_te,nguyen_nhan)"
                    + " VALUES(?,?,?,?,?)",
                parameters);
    }

    public static long countApplyStocktake(Object... parameters) {
        return SqlDatabase.number(
                "SELECT COUNT(*) FROM bien_ban_kiem_ke WITH(UPDLOCK,HOLDLOCK) WHERE id=?"
                        + " AND trang_thai='PENDING'",
                parameters);
    }

    public static List<Object[]> findApplyStocktake(Object... parameters) {
        return SqlDatabase.query(
                "SELECT lo_hang_id,ton_he_thong,ton_thuc_te FROM chi_tiet_kiem_ke WHERE"
                        + " bien_ban_id=?",
                parameters);
    }

    public static int addSupplierPayment(Object... parameters) {
        return SqlDatabase.update(
                "UPDATE phieu_nhap_kho SET da_tra=da_tra+? WHERE id=? AND"
                        + " da_tra+?<=tong_tien AND trang_thai='POSTED'",
                parameters);
    }

    public static List<Object[]> purchaseDetails(Object... parameters) {
        return SqlDatabase.query(
                "SELECT p.ten,c.so_lo,c.han_su_dung,c.so_luong,c.gia_nhap FROM"
                        + " chi_tiet_phieu_nhap c JOIN san_pham p ON p.id=c.san_pham_id WHERE"
                        + " c.phieu_nhap_id=? ORDER BY c.id",
                parameters);
    }
}
