package com.petstation.dao.impl;

import com.petstation.dao.LichSuKhachHangDAO;
import com.petstation.database.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC lịch sử khách hàng DAO. */
public final class LichSuKhachHangImpl implements LichSuKhachHangDAO {
    public LichSuKhachHangImpl() {}

    public List<Object[]> layThuCung(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                        + " ma_thu_cung,ten,loai,giong,ngay_sinh,can_nang,tiem_chung,di_ung_benh_ly"
                        + " FROM thu_cung WHERE khach_hang_id=? ORDER BY ma_thu_cung",
                cacThamSo);
    }

    public List<Object[]> layCacHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " h.so_hoa_don,h.thanh_toan_luc,h.tong_tien,h.hinh_thuc_thanh_toan,h.trang_thai"
                    + " FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE"
                    + " d.khach_hang_id=? ORDER BY h.thanh_toan_luc DESC",
                cacThamSo);
    }

    public List<Object[]> layLichSuSpa(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT t.ma_phieu,p.ten,t.tao_luc,t.tong_tien,t.trang_thai FROM"
                        + " phieu_tiep_nhan_spa t JOIN thu_cung p ON p.id=t.thu_cung_id"
                        + " WHERE p.khach_hang_id=? ORDER BY t.tao_luc DESC",
                cacThamSo);
    }

    public List<Object[]> layLichSuLuuTru(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT h.ma_hop_dong,p.ten,r.ma_phong,h.ngay_vao,h.ngay_ra_du_kien,h.trang_thai"
                        + " FROM hop_dong_luu_tru h JOIN thu_cung p ON p.id=h.thu_cung_id JOIN"
                        + " phong_hotel r ON r.id=h.phong_id WHERE h.khach_hang_id=? ORDER BY"
                        + " h.ngay_vao DESC",
                cacThamSo);
    }

    public List<Object[]> layLichSuDiem(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT tao_luc,thay_doi,so_du_sau,ly_do FROM lich_su_diem WHERE"
                        + " khach_hang_id=? ORDER BY id DESC",
                cacThamSo);
    }

    public List<Object[]> layVoucher(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                        + " ma_voucher,menh_gia,hieu_luc_den,da_dung,so_luong,trang_thai"
                        + " FROM voucher WHERE khach_hang_id=? ORDER BY id DESC",
                cacThamSo);
    }
}
