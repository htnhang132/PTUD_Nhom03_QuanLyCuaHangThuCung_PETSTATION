package com.petstation.dao.impl;

import com.petstation.dao.LuaChonNghiepVuDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC nghiệp vụ lựa chọn DAO. */
public final class LuaChonNghiepVuDAOImpl implements LuaChonNghiepVuDAO {
    public LuaChonNghiepVuDAOImpl() {}

    public List<Object[]> layLuaChonThuCung(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ma_thu_cung,p.ma_thu_cung+N' - '+p.ten+N' / '+k.ho_ten FROM thu_cung p"
                        + " JOIN khach_hang k ON k.id=p.khach_hang_id WHERE p.dang_theo_doi=1 AND"
                        + " k.dang_hoat_dong=1 ORDER BY p.ma_thu_cung",
                cacThamSo);
    }

    public List<Object[]> layLuaChonGoiDichVu(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_goi,ma_goi+N' - '+ten_goi FROM goi_dich_vu_spa WHERE trang_thai='ACTIVE'"
                        + " ORDER BY ma_goi",
                cacThamSo);
    }

    public List<Object[]> layLuaChonLoHang(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT l.id,p.ma_san_pham+N' - '+p.ten+N' / '+l.so_lo+N' / tồn"
                    + " '+CAST(l.so_luong_ton AS nvarchar(20)) FROM lo_hang l JOIN san_pham p ON"
                    + " p.id=l.san_pham_id ORDER BY p.ma_san_pham,l.id",
                cacThamSo);
    }

    public List<Object[]> layLuaChonPhong(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_phong,ma_phong+N' - '+loai_phong+N' / '+CAST(gia_dem AS"
                        + " nvarchar(30))+N' / ngày' FROM phong_hotel WHERE trang_thai"
                        + " IN ('AVAILABLE','OCCUPIED') ORDER BY ma_phong",
                cacThamSo);
    }

    public List<Object[]> layLuaChonNhaCungCap(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_ncc,ma_ncc+N' - '+ten FROM nha_cung_cap WHERE"
                        + " trang_thai='COOPERATING' ORDER BY ma_ncc",
                cacThamSo);
    }

    public List<Object[]> layLuaChonSanPham(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_san_pham,ma_san_pham+N' - '+ten FROM san_pham ORDER BY" + " ma_san_pham",
                cacThamSo);
    }

    public List<Object[]> layLuaChonDongHang(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT i.id,h.so_hoa_don+N' / '+COALESCE(i.ten_hang,p.ten)+N' / đã"
                        + " mua '+CAST(i.so_luong AS nvarchar(20)) FROM"
                        + " chi_tiet_don_hang i JOIN hoa_don h ON"
                        + " h.don_hang_id=i.don_hang_id JOIN san_pham p ON"
                        + " p.id=i.san_pham_id WHERE h.trang_thai='PAID' ORDER BY h.id"
                        + " DESC,i.id",
                cacThamSo);
    }

    public List<Object[]> layLuaChonNhanVien(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_nhan_vien,ma_nhan_vien+N' - '+ho_ten FROM nhan_vien"
                        + " WHERE dang_lam_viec=1 ORDER BY ma_nhan_vien",
                cacThamSo);
    }
}
