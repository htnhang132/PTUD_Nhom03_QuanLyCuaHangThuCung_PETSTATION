package com.petstation.dao.impl;

import com.petstation.dao.SqlDatabase;

import java.util.List;

/** SQL commands participating in the caller's current transaction. */
public final class JdbcWorkflowLookupDao {
    private JdbcWorkflowLookupDao() {}

    public static List<Object[]> optionsPet(Object... parameters) {
        return SqlDatabase.query(
                "SELECT p.ma_thu_cung,p.ma_thu_cung+N' - '+p.ten+N' / '+k.ho_ten FROM thu_cung p"
                        + " JOIN khach_hang k ON k.id=p.khach_hang_id WHERE p.dang_theo_doi=1 AND"
                        + " k.dang_hoat_dong=1 ORDER BY p.ma_thu_cung",
                parameters);
    }

    public static List<Object[]> optionsPackage(Object... parameters) {
        return SqlDatabase.query(
                "SELECT ma_goi,ma_goi+N' - '+ten_goi FROM goi_dich_vu_spa WHERE trang_thai='ACTIVE'"
                        + " ORDER BY ma_goi",
                parameters);
    }

    public static List<Object[]> optionsBatch(Object... parameters) {
        return SqlDatabase.query(
                "SELECT l.id,p.ma_san_pham+N' - '+p.ten+N' / '+l.so_lo+N' / tồn"
                    + " '+CAST(l.so_luong_ton AS nvarchar(20)) FROM lo_hang l JOIN san_pham p ON"
                    + " p.id=l.san_pham_id ORDER BY p.ma_san_pham,l.id",
                parameters);
    }

    public static List<Object[]> optionsRoom(Object... parameters) {
        return SqlDatabase.query(
                "SELECT ma_phong,ma_phong+N' - '+loai_phong+N' / '+CAST(gia_dem AS"
                        + " nvarchar(30))+N' / ngày' FROM phong_hotel WHERE trang_thai"
                        + " IN ('AVAILABLE','OCCUPIED') ORDER BY ma_phong",
                parameters);
    }

    public static List<Object[]> optionsSupplier(Object... parameters) {
        return SqlDatabase.query(
                "SELECT ma_ncc,ma_ncc+N' - '+ten FROM nha_cung_cap WHERE"
                        + " trang_thai='COOPERATING' ORDER BY ma_ncc",
                parameters);
    }

    public static List<Object[]> optionsProduct(Object... parameters) {
        return SqlDatabase.query(
                "SELECT ma_san_pham,ma_san_pham+N' - '+ten FROM san_pham ORDER BY" + " ma_san_pham",
                parameters);
    }

    public static List<Object[]> optionsItem(Object... parameters) {
        return SqlDatabase.query(
                "SELECT i.id,h.so_hoa_don+N' / '+COALESCE(i.ten_hang,p.ten)+N' / đã"
                        + " mua '+CAST(i.so_luong AS nvarchar(20)) FROM"
                        + " chi_tiet_don_hang i JOIN hoa_don h ON"
                        + " h.don_hang_id=i.don_hang_id JOIN san_pham p ON"
                        + " p.id=i.san_pham_id WHERE h.trang_thai='PAID' ORDER BY h.id"
                        + " DESC,i.id",
                parameters);
    }

    public static List<Object[]> optionsStaff(Object... parameters) {
        return SqlDatabase.query(
                "SELECT ma_nhan_vien,ma_nhan_vien+N' - '+ho_ten FROM nhan_vien"
                        + " WHERE dang_lam_viec=1 ORDER BY ma_nhan_vien",
                parameters);
    }
}
