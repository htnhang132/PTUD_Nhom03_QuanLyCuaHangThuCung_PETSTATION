package com.petstation.dao.impl;

import com.petstation.dao.LuaChonNghiepVuDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.util.DuLieu;

import java.util.List;

public final class LuaChonNghiepVuDAOImpl implements LuaChonNghiepVuDAO {
    public LuaChonNghiepVuDAOImpl() {}

    @Override
    public List<Object[]> layLuaChonThuCung(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ma_thu_cung, p.ma_thu_cung+N' - '+p.ten+N' / '+k.ho_ten FROM thu_cung p"
                        + " JOIN khach_hang k ON k.ma_khach_hang=p.ma_khach_hang WHERE p.dang_theo_doi=1 AND"
                        + " k.dang_hoat_dong=1 ORDER BY p.ma_thu_cung",
                cacThamSo);
    }

    @Override
    public List<Object[]> layLuaChonGoiDichVu(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_goi, ma_goi+N' - '+ten_goi FROM goi_dich_vu_spa WHERE trang_thai='HOAT_DONG'"
                        + " ORDER BY ma_goi",
                cacThamSo);
    }

    @Override
    public List<Object[]> layLuaChonLoHang(Object... cacThamSo) {
        List<Object[]> rows = ThaoTacSQL.truyVan(
                "SELECT l.ma_lo, p.ma_san_pham+N' - '+p.ten+N' / '+l.so_lo+N' / tồn"
                    + " '+CAST(l.so_luong_ton AS nvarchar(20)) FROM lo_hang l JOIN san_pham p ON"
                    + " p.ma_san_pham=l.ma_san_pham ORDER BY p.ma_san_pham, l.ma_lo",
                cacThamSo);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }

    @Override
    public List<Object[]> layLuaChonPhong(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_phong, ma_phong+N' - '+loai_phong+N' / '+CAST(gia_dem AS"
                        + " nvarchar(30))+N' / ngày' FROM phong_hotel WHERE trang_thai"
                        + " IN ('TRONG','DANG_O') ORDER BY ma_phong",
                cacThamSo);
    }

    @Override
    public List<Object[]> layLuaChonNhaCungCap(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_nha_cung_cap, ma_nha_cung_cap+N' - '+ten FROM nha_cung_cap WHERE"
                        + " trang_thai='DANG_HOP_TAC' ORDER BY ma_nha_cung_cap",
                cacThamSo);
    }

    @Override
    public List<Object[]> layLuaChonSanPham(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_san_pham, ma_san_pham+N' - '+ten FROM san_pham ORDER BY ma_san_pham",
                cacThamSo);
    }

    @Override
    public List<Object[]> layLuaChonDongHang(Object... cacThamSo) {
        List<Object[]> rows = ThaoTacSQL.truyVan(
                "SELECT i.ma_san_pham, h.ma_hoa_don+N' / '+COALESCE(i.ten_hang,p.ten)+N' / đã"
                        + " mua '+CAST(i.so_luong AS nvarchar(20)) FROM"
                        + " chi_tiet_hoa_don i JOIN hoa_don h ON"
                        + " h.ma_hoa_don=i.ma_hoa_don JOIN san_pham p ON"
                        + " p.ma_san_pham=i.ma_san_pham WHERE h.trang_thai='DA_THANH_TOAN' ORDER BY h.ma_hoa_don"
                        + " DESC, i.ma_san_pham",
                cacThamSo);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }

    @Override
    public List<Object[]> layLuaChonNhanVien(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_nhan_vien, ma_nhan_vien+N' - '+ho_ten FROM nhan_vien"
                        + " WHERE dang_lam_viec=1 ORDER BY ma_nhan_vien",
                cacThamSo);
    }
}
