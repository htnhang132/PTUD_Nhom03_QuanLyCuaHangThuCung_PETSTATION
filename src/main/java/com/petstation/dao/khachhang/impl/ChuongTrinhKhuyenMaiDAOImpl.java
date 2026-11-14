package com.petstation.dao.khachhang.impl;

import static com.petstation.database.ThaoTacSQL.truyVan;
import static com.petstation.database.ThaoTacSQL.truyVanDanhSach;

import com.petstation.dao.khachhang.ChuongTrinhKhuyenMaiDAO;
import com.petstation.model.khachhang.ChuongTrinhKhuyenMai;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class ChuongTrinhKhuyenMaiDAOImpl implements ChuongTrinhKhuyenMaiDAO {
    public ChuongTrinhKhuyenMaiDAOImpl() {}

    @Override
    public List<ChuongTrinhKhuyenMai> layTatCa() {
        return truyVanDanhSach(
                "SELECT ma_khuyen_mai, ten, loai_giam_gia, gia_tri_giam, gia_tri_toi_thieu,"
                        + " bat_dau_luc, ket_thuc_luc, trang_thai, mo_ta FROM"
                        + " chuong_trinh_khuyen_mai ORDER BY ma_khuyen_mai ASC",
                r -> chuyenDong(r));
    }

    @Override
    public ChuongTrinhKhuyenMai timTheoMa(String maKhuyenMai) {
        List<Object[]> rows =
                truyVan(
                        "SELECT ma_khuyen_mai, ten, loai_giam_gia, gia_tri_giam, gia_tri_toi_thieu,"
                                + " bat_dau_luc, ket_thuc_luc, trang_thai, mo_ta FROM"
                                + " chuong_trinh_khuyen_mai WHERE ma_khuyen_mai = ?",
                        maKhuyenMai);
        if (rows.isEmpty()) return null;
        return chuyenDong(rows.get(0));
    }

    @Override
    public List<ChuongTrinhKhuyenMai> layDanhSachKhuyenMai() {
        List<Object[]> rows =
                truyVan(
                        "SELECT ma_khuyen_mai, ten, loai_giam_gia, gia_tri_giam, bat_dau_luc,"
                                + " ket_thuc_luc, trang_thai FROM chuong_trinh_khuyen_mai ORDER BY"
                                + " ma_khuyen_mai ASC");
        List<ChuongTrinhKhuyenMai> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            ChuongTrinhKhuyenMai k = new ChuongTrinhKhuyenMai();
            k.setMaChuongTrinh(r[0] != null ? r[0].toString() : null);
            k.setTen(r[1] != null ? r[1].toString() : null);
            k.setLoaiGiam(r[2] != null ? r[2].toString() : null);
            k.setGiaTri(r[3] != null ? ((Number) r[3]).doubleValue() : 0.0);
            if (r[4] instanceof Timestamp ts) {
                k.setBatDau(ts.toLocalDateTime());
            }
            if (r[5] instanceof Timestamp ts) {
                k.setKetThuc(ts.toLocalDateTime());
            }
            k.setTrangThai(r[6] != null ? r[6].toString() : null);
            ketQua.add(k);
        }
        return ketQua;
    }

    private ChuongTrinhKhuyenMai chuyenDong(Object[] r) {
        ChuongTrinhKhuyenMai k = new ChuongTrinhKhuyenMai();
        k.setMaChuongTrinh(r[0] != null ? r[0].toString() : null);
        k.setTen(r[1] != null ? r[1].toString() : null);
        k.setLoaiGiam(r[2] != null ? r[2].toString() : null);
        k.setGiaTri(r[3] != null ? ((Number) r[3]).doubleValue() : 0.0);
        k.setDonToiThieu(r[4] != null ? ((Number) r[4]).longValue() : 0L);
        if (r[5] instanceof Timestamp ts) {
            k.setBatDau(ts.toLocalDateTime());
        }
        if (r[6] instanceof Timestamp ts) {
            k.setKetThuc(ts.toLocalDateTime());
        }
        k.setTrangThai(r[7] != null ? r[7].toString() : null);
        k.setNguon(r[8] != null ? r[8].toString() : null);
        return k;
    }
}
