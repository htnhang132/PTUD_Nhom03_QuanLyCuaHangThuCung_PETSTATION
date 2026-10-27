package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.ViecCanLamDAO;

import java.time.LocalDateTime;
import java.util.List;

/** Truy vấn ViecCanLamService; quyền và transaction do tầng service quyết định. */
public final class ViecCanLamImpl implements ViecCanLamDAO {
    public ViecCanLamImpl() {}

    public List<Object[]> layLichSapDen() {

        return truyVan(
                "SELECT l.id,l.ma_lich_hen,p.ten,l.bat_dau,l.ket_thuc FROM lich_hen l"
                        + " JOIN thu_cung p ON p.id=l.thu_cung_id WHERE"
                        + " l.trang_thai='SCHEDULED' AND l.ket_thuc>=SYSDATETIME() AND"
                        + " l.bat_dau<DATEADD(day,1,SYSDATETIME())");
    }

    public List<Object[]> laySpaChoXuLy() {

        return truyVan(
                "SELECT id,ma_phieu,trang_thai,tao_luc FROM phieu_tiep_nhan_spa WHERE"
                        + " trang_thai IN ('RECEIVED','IN_PROGRESS','COMPLETED')");
    }

    public List<Object[]> layHotelSapTra() {

        return truyVan(
                "SELECT h.id,h.ma_hop_dong,p.ma_phong,h.ngay_ra_du_kien FROM"
                        + " hop_dong_luu_tru h JOIN phong_hotel p ON p.id=h.phong_id WHERE"
                        + " h.trang_thai='CHECKED_IN' AND"
                        + " h.ngay_ra_du_kien<=DATEADD(hour,2,SYSDATETIME())");
    }

    public List<Object[]> layPhongChoDon() {

        return truyVan("SELECT id,ma_phong FROM phong_hotel WHERE trang_thai='CLEANING'");
    }

    public List<Object[]> layTonThap() {

        return truyVan(
                "SELECT p.id,p.ma_san_pham,p.ten FROM san_pham p LEFT JOIN lo_hang l ON"
                        + " l.san_pham_id=p.id AND l.trang_thai='AVAILABLE' WHERE"
                        + " p.trang_thai='SELLING' GROUP BY"
                        + " p.id,p.ma_san_pham,p.ten,p.ton_toi_thieu HAVING"
                        + " COALESCE(SUM(l.so_luong_ton),0)<=p.ton_toi_thieu");
    }

    public List<Object[]> layLoSapHetHan() {

        return truyVan(
                "SELECT l.id,p.ma_san_pham,l.so_lo,l.han_su_dung FROM lo_hang l JOIN"
                        + " san_pham p ON p.id=l.san_pham_id WHERE l.so_luong_ton>0 AND"
                        + " l.trang_thai='AVAILABLE' AND"
                        + " l.han_su_dung<=DATEADD(day,30,CAST(GETDATE() AS date))");
    }

    public List<Object[]> layVaccineDenHan() {

        return truyVan(
                "WITH v AS(SELECT *,ROW_NUMBER() OVER(PARTITION BY"
                        + " thu_cung_id,ten_vaccine ORDER BY ngay_tiem DESC,id DESC) rn"
                        + " FROM tiem_phong) SELECT"
                        + " t.id,t.ma_thu_cung,t.ten,v.ten_vaccine,v.ngay_het_han,v.id FROM"
                        + " v JOIN thu_cung t ON t.id=v.thu_cung_id WHERE v.rn=1 AND"
                        + " t.dang_theo_doi=1 AND"
                        + " v.ngay_het_han<=DATEADD(day,7,CAST(GETDATE() AS date))");
    }

    public List<Object[]> layTrangThaiNhac(long taiKhoan) {

        return truyVan(
                "SELECT khoa,da_xem,hoan_den FROM nhac_viec_da_xem WHERE tai_khoan_id=?", taiKhoan);
    }

    public int luuTrangThaiNhac(long taiKhoan, String khoa, boolean daXem, LocalDateTime hoan) {

        return capNhat(
                "IF EXISTS(SELECT 1 FROM nhac_viec_da_xem WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " tai_khoan_id=? AND khoa=?) UPDATE nhac_viec_da_xem SET"
                        + " da_xem=?,hoan_den=? WHERE tai_khoan_id=? AND khoa=? ELSE INSERT"
                        + " INTO nhac_viec_da_xem(tai_khoan_id,khoa,da_xem,hoan_den)"
                        + " VALUES(?,?,?,?)",
                taiKhoan,
                khoa,
                daXem,
                hoan,
                taiKhoan,
                khoa,
                taiKhoan,
                khoa,
                daXem,
                hoan);
    }
}
