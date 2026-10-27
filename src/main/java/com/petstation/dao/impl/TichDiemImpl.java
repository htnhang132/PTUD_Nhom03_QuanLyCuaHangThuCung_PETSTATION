package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.TichDiemDAO;

import java.util.List;

/** Truy cập sổ điểm và voucher, dùng chung transaction của nghiệp vụ. */
public final class TichDiemImpl implements TichDiemDAO {
    public TichDiemImpl() {}

    public List<Object[]> layRangBuocVoucher(String ma) {
        return truyVan(
                "SELECT da_dung,khoa_doi_diem,khach_hang_id FROM voucher WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE ma_voucher=?",
                ma);
    }

    public List<Object[]> timKhachHang(String ma) {
        return truyVan(
                "SELECT id,diem_tich_luy,dang_hoat_dong FROM khach_hang WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE ma_khach_hang=?",
                ma);
    }

    public List<Object[]> timTheoKhoa(String khoa) {
        return truyVan(
                "SELECT ma_voucher,khach_hang_id FROM voucher WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " khoa_doi_diem=?",
                khoa);
    }

    public String taoVoucher(long khachHang, String khoa) {
        String ma = taoMa("SPA");
        capNhat(
                "INSERT"
                    + " voucher(ma_voucher,khach_hang_id,menh_gia,hieu_luc_den,chi_ap_dung_spa,so_luong,da_dung,don_toi_thieu,trang_thai,khoa_doi_diem)"
                    + " VALUES(?,?,150000,DATEADD(day,60,SYSDATETIME()),1,1,0,0,'ACTIVE',?)",
                ma,
                khachHang,
                khoa);
        capNhat(
                "UPDATE khach_hang SET diem_tich_luy=diem_tich_luy-200 WHERE id=? AND"
                        + " diem_tich_luy>=200",
                khachHang);
        capNhat(
                "INSERT lich_su_diem(khach_hang_id,thay_doi,so_du_sau,ly_do) SELECT"
                    + " id,-200,diem_tich_luy,N'Đổi 200 điểm lấy voucher '+? FROM khach_hang WHERE"
                    + " id=?",
                ma,
                khachHang);
        return ma;
    }

    public List<Object[]> timVoucher(String ma) {
        return truyVan(
                "SELECT"
                    + " v.menh_gia,v.hieu_luc_den,v.da_dung,v.so_luong,v.don_toi_thieu,v.trang_thai,v.chi_ap_dung_spa,COALESCE(k.ma_khach_hang,'')"
                    + " FROM voucher v LEFT JOIN khach_hang k ON k.id=v.khach_hang_id WHERE"
                    + " v.ma_voucher=?",
                ma);
    }

    public List<Object[]> layCuaKhach(String ma) {
        return truyVan(
                "SELECT v.ma_voucher,v.menh_gia,v.hieu_luc_den,CASE WHEN v.da_dung>=v.so_luong THEN"
                    + " N'Đã dùng' WHEN v.hieu_luc_den<=SYSDATETIME() THEN N'Hết hạn' ELSE N'Còn"
                    + " hiệu lực' END FROM voucher v JOIN khach_hang k ON k.id=v.khach_hang_id"
                    + " WHERE k.ma_khach_hang=? ORDER BY v.id DESC",
                ma);
    }
}
