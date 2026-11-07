package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.QuyTrinhDAO;
import com.petstation.util.DuLieu;

import java.util.List;

public final class QuyTrinhDAOImpl implements QuyTrinhDAO {
    public QuyTrinhDAOImpl() {}

    public List<Object[]> laySpa(long id) {
        return truyVan(
                "SELECT trang_thai, CASE WHEN (ma_goi IS NOT NULL OR don_gia>0 OR tong_tien>0)"
                        + " THEN 1 ELSE 0 END FROM phieu_tiep_nhan_spa t WHERE (t.ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(t.ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                id, id);
    }

    public String layTrangThaiHotel(long id) {
        return chuyenThanhChuoi(
                layGiaTriDon("SELECT trang_thai FROM hop_dong_luu_tru WHERE (ma_hop_dong = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", id, id));
    }

    public List<Object[]> layLichHen(long id) {
        return truyVan(
                "SELECT l.ma_lich_hen, p.ma_thu_cung FROM lich_hen l JOIN thu_cung p ON"
                        + " p.ma_thu_cung=l.ma_thu_cung WHERE (l.ma_lich_hen = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(l.ma_lich_hen, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND l.trang_thai='DA_XAC_NHAN'",
                id, id);
    }

    public List<Object[]> layPhong() {
        List<Object[]> rows = truyVan(
                "SELECT p.ma_phong AS ma_khoa, p.ma_phong, p.loai_phong, p.gia_dem, p.trang_thai, "
                    + "(SELECT COUNT(*) FROM hop_dong_luu_tru h WHERE h.ma_phong=p.ma_phong AND h.trang_thai='DA_DAT'), "
                    + "COALESCE((SELECT TOP 1 h.ma_hop_dong FROM hop_dong_luu_tru h WHERE h.ma_phong=p.ma_phong AND h.trang_thai IN('DA_DAT','DANG_O') ORDER BY CASE h.trang_thai WHEN 'DANG_O' THEN 0 ELSE 1 END, h.ngay_vao), '') "
                    + "FROM phong_hotel p ORDER BY p.ma_phong");
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
            if (r[6] != null && !r[6].toString().isEmpty() && !"0".equals(r[6].toString())) {
                r[6] = DuLieu.layIdCauNoi(null, String.valueOf(r[6]));
            } else {
                r[6] = 0L;
            }
        }
        return rows;
    }

    public java.util.List<Object[]> layPhongChiTiet() {
        return truyVan(
                "SELECT ma_phong, loai_phong, gia_dem, trang_thai, suc_chua FROM phong_hotel ORDER BY ma_phong");
    }

    public java.util.List<Object[]> layLichPhong() {
        return truyVan(
                "SELECT h.ma_phong, h.ma_hop_dong, t.ten, k.ho_ten, h.ngay_vao, h.ngay_ra_du_kien, h.trang_thai "
                        + "FROM hop_dong_luu_tru h JOIN thu_cung t ON t.ma_thu_cung=h.ma_thu_cung JOIN "
                        + "khach_hang k ON k.ma_khach_hang=h.ma_khach_hang WHERE h.trang_thai "
                        + "IN('DA_DAT','DANG_O') ORDER BY h.ngay_vao, h.ma_hop_dong");
    }
}
