package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.QuyTrinhDAO;

import java.util.List;

/** Dữ liệu mới nhất để kiểm tra chuyển bước và sơ đồ phòng. */
public final class QuyTrinhImpl implements QuyTrinhDAO {
    public QuyTrinhImpl() {}

    public List<Object[]> laySpa(long id) {
        return truyVan(
                "SELECT trang_thai,(SELECT COUNT(*) FROM chi_tiet_phieu_spa WHERE"
                        + " phieu_spa_id=t.id) FROM phieu_tiep_nhan_spa t WHERE id=?",
                id);
    }

    public String layTrangThaiHotel(long id) {
        return chuyenThanhChuoi(
                layGiaTriDon("SELECT trang_thai FROM hop_dong_luu_tru WHERE id=?", id));
    }

    public List<Object[]> layLichHen(long id) {
        return truyVan(
                "SELECT l.ma_lich_hen,p.ma_thu_cung FROM lich_hen l JOIN thu_cung p ON"
                        + " p.id=l.thu_cung_id WHERE l.id=? AND l.trang_thai='SCHEDULED'",
                id);
    }

    public List<Object[]> layPhong() {
        return truyVan(
                "SELECT p.id,p.ma_phong,p.loai_phong,p.gia_dem,p.trang_thai,(SELECT COUNT(*) FROM"
                    + " hop_dong_luu_tru h WHERE h.phong_id=p.id AND"
                    + " h.trang_thai='BOOKED'),COALESCE((SELECT TOP 1 h.id FROM hop_dong_luu_tru h"
                    + " WHERE h.phong_id=p.id AND h.trang_thai IN('BOOKED','CHECKED_IN') ORDER BY"
                    + " CASE h.trang_thai WHEN 'CHECKED_IN' THEN 0 ELSE 1 END,h.ngay_vao),0) FROM"
                    + " phong_hotel p ORDER BY p.ma_phong");
    }

    public java.util.List<Object[]> layPhongChiTiet() {
        return truyVan(
                "SELECT id,ma_phong,loai_phong,gia_dem,trang_thai,suc_chua FROM phong_hotel ORDER"
                        + " BY ma_phong");
    }

    public java.util.List<Object[]> layLichPhong() {
        return truyVan(
                "SELECT h.phong_id,h.id,t.ten,k.ho_ten,h.ngay_vao,h.ngay_ra_du_kien,h.trang_thai"
                        + " FROM hop_dong_luu_tru h JOIN thu_cung t ON t.id=h.thu_cung_id JOIN"
                        + " khach_hang k ON k.id=h.khach_hang_id WHERE h.trang_thai"
                        + " IN('BOOKED','CHECKED_IN') ORDER BY h.ngay_vao,h.id");
    }
}
