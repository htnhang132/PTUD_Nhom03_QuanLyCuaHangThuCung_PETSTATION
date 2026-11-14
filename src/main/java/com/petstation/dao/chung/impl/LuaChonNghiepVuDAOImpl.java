package com.petstation.dao.chung.impl;

import com.petstation.dao.chung.LuaChonNghiepVuDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.MucLuaChon;
import com.petstation.util.DuLieu;

import java.util.List;

public final class LuaChonNghiepVuDAOImpl implements LuaChonNghiepVuDAO {
    public LuaChonNghiepVuDAOImpl() {}

    private static List<MucLuaChon> mapLuaChonChuoi(List<Object[]> rows) {
        return rows.stream()
                .map(
                        r ->
                                new MucLuaChon(
                                        DuLieu.chuyenThanhChuoi(r[0]),
                                        DuLieu.chuyenThanhChuoi(r[1])))
                .toList();
    }

    @Override
    public List<MucLuaChon> layLuaChonThuCung() {
        return mapLuaChonChuoi(
                ThaoTacSQL.truyVan(
                        "SELECT p.ma_thu_cung, p.ma_thu_cung+N' - '+p.ten+N' / '+k.ho_ten FROM"
                                + " thu_cung p JOIN khach_hang k ON k.ma_khach_hang=p.ma_khach_hang"
                                + " WHERE p.dang_theo_doi=1 AND k.dang_hoat_dong=1 ORDER BY"
                                + " p.ma_thu_cung"));
    }

    @Override
    public List<MucLuaChon> layLuaChonGoiDichVu() {
        return mapLuaChonChuoi(
                ThaoTacSQL.truyVan(
                        "SELECT ma_goi, ma_goi+N' - '+ten_goi FROM goi_dich_vu_spa WHERE"
                                + " trang_thai='HOAT_DONG' ORDER BY ma_goi"));
    }

    @Override
    public List<MucLuaChon> layLuaChonLoHang() {
        return mapLuaChonChuoi(
                ThaoTacSQL.truyVan(
                        "SELECT l.ma_lo, p.ma_san_pham+N' - '+p.ten+N' / '+l.so_lo+N' / tồn"
                            + " '+CAST(l.so_luong_ton AS nvarchar(20)) FROM lo_hang l JOIN san_pham"
                            + " p ON p.ma_san_pham=l.ma_san_pham ORDER BY p.ma_san_pham, l.ma_lo"));
    }

    @Override
    public List<MucLuaChon> layLuaChonPhong() {
        return mapLuaChonChuoi(
                ThaoTacSQL.truyVan(
                        "SELECT ma_phong, ma_phong+N' - '+loai_phong+N' / '+CAST(gia_dem AS"
                                + " nvarchar(30))+N' / ngày' FROM phong_hotel WHERE trang_thai"
                                + " IN ('TRONG','DANG_O') ORDER BY ma_phong"));
    }

    @Override
    public List<MucLuaChon> layLuaChonNhaCungCap() {
        return mapLuaChonChuoi(
                ThaoTacSQL.truyVan(
                        "SELECT ma_nha_cung_cap, ma_nha_cung_cap+N' - '+ten FROM nha_cung_cap WHERE"
                                + " trang_thai='DANG_HOP_TAC' ORDER BY ma_nha_cung_cap"));
    }

    @Override
    public List<MucLuaChon> layLuaChonSanPham() {
        return mapLuaChonChuoi(
                ThaoTacSQL.truyVan(
                        "SELECT ma_san_pham, ma_san_pham+N' - '+ten FROM san_pham ORDER BY"
                                + " ma_san_pham"));
    }

    @Override
    public List<MucLuaChon> layLuaChonDongHang() {
        return mapLuaChonChuoi(
                ThaoTacSQL.truyVan(
                        "SELECT i.ma_hoa_don + ':' + i.ma_san_pham,"
                                + " h.ma_hoa_don+N' / '+COALESCE(i.ten_hang,p.ten)+N' / đã mua"
                                + " '+CAST(i.so_luong AS nvarchar(20)) FROM chi_tiet_hoa_don i JOIN"
                                + " hoa_don h ON h.ma_hoa_don=i.ma_hoa_don JOIN san_pham p ON"
                                + " p.ma_san_pham=i.ma_san_pham WHERE h.trang_thai='DA_THANH_TOAN'"
                                + " ORDER BY h.ma_hoa_don DESC, i.ma_san_pham"));
    }

    @Override
    public List<MucLuaChon> layLuaChonNhanVien() {
        return mapLuaChonChuoi(
                ThaoTacSQL.truyVan(
                        "SELECT ma_nhan_vien, ma_nhan_vien+N' - '+ho_ten FROM nhan_vien"
                                + " WHERE dang_lam_viec=1 ORDER BY ma_nhan_vien"));
    }
}
