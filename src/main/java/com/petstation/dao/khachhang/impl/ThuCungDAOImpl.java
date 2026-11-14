package com.petstation.dao.khachhang.impl;

import static com.petstation.database.ThaoTacSQL.truyVan;
import static com.petstation.database.ThaoTacSQL.truyVanDanhSach;

import com.petstation.dao.khachhang.ThuCungDAO;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.util.DuLieu;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public final class ThuCungDAOImpl implements ThuCungDAO {
    public ThuCungDAOImpl() {}

    @Override
    public List<ThuCung> layTheoKhachHang(String maKhachHang) {
        return truyVanDanhSach(
                "SELECT t.ma_thu_cung, t.ma_khach_hang, t.ten, t.loai, t.giong, t.gioi_tinh,"
                        + " t.ngay_sinh, t.can_nang, t.tinh_trang_suc_khoe FROM thu_cung t WHERE"
                        + " t.ma_khach_hang = ? ORDER BY t.ma_thu_cung ASC",
                r -> chuyenDong(r),
                maKhachHang);
    }

    @Override
    public ThuCung timTheoMa(String maThuCung) {
        List<Object[]> rows =
                truyVan(
                        "SELECT t.ma_thu_cung, t.ma_khach_hang, t.ten, t.loai, t.giong,"
                            + " t.gioi_tinh, t.ngay_sinh, t.can_nang, t.tinh_trang_suc_khoe FROM"
                            + " thu_cung t WHERE t.ma_thu_cung = ?",
                        maThuCung);
        if (rows.isEmpty()) return null;
        return chuyenDong(rows.get(0));
    }

    @Override
    public List<ThuCung> layDanhSachThuCung() {
        List<Object[]> rows =
                truyVan(
                        "SELECT t.ma_thu_cung, t.ten, k.ho_ten, t.loai, t.giong, t.can_nang FROM"
                                + " thu_cung t LEFT JOIN khach_hang k ON k.ma_khach_hang ="
                                + " t.ma_khach_hang ORDER BY t.ma_thu_cung ASC");
        List<ThuCung> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            ThuCung t = new ThuCung();
            t.setMaThuCung(r[0] != null ? r[0].toString() : null);
            t.setTen(r[1] != null ? r[1].toString() : null);
            if (r[2] != null && !r[2].toString().isBlank()) {
                KhachHang kh = new KhachHang();
                kh.setHoTen(r[2].toString());
                t.setKhachHang(kh);
            }
            t.setLoai(r[3] != null ? r[3].toString() : null);
            t.setGiong(r[4] != null ? r[4].toString() : null);
            t.setCanNang(r[5] != null ? ((Number) r[5]).doubleValue() : 0.0);
            ketQua.add(t);
        }
        return ketQua;
    }

    @Override
    public ThuCung timThuCungHoatDong(String maThuCung) {
        List<Object[]> rows =
                truyVan(
                        "SELECT p.ma_thu_cung, p.ma_khach_hang, p.loai, p.can_nang FROM thu_cung p"
                                + " JOIN khach_hang k ON k.ma_khach_hang=p.ma_khach_hang WHERE"
                                + " p.ma_thu_cung=? AND p.dang_theo_doi=1 AND k.dang_hoat_dong=1",
                        maThuCung);
        if (rows.isEmpty()) return null;
        Object[] r = rows.get(0);
        ThuCung thu = new ThuCung(DuLieu.chuyenThanhChuoi(r[0]));
        String maKH = DuLieu.chuyenThanhChuoi(r[1]);
        if (maKH != null && !maKH.isBlank()) {
            thu.setKhachHang(new KhachHang(maKH));
        }
        thu.setLoai(DuLieu.chuyenThanhChuoi(r[2]));
        thu.setCanNang(r[3] instanceof Number n ? n.doubleValue() : 0.0);
        return thu;
    }

    private ThuCung chuyenDong(Object[] r) {
        ThuCung t = new ThuCung();
        t.setMaThuCung(r[0] != null ? r[0].toString() : null);
        if (r[1] != null) {
            KhachHang kh = new KhachHang();
            kh.setMaKhachHang(r[1].toString());
            t.setKhachHang(kh);
        }
        t.setTen(r[2] != null ? r[2].toString() : null);
        t.setLoai(r[3] != null ? r[3].toString() : null);
        t.setGiong(r[4] != null ? r[4].toString() : null);
        t.setGioiTinh(r[5] != null ? r[5].toString() : null);
        if (r[6] instanceof Date d) {
            t.setNgaySinh(d.toLocalDate());
        }
        t.setCanNang(r[7] != null ? ((Number) r[7]).doubleValue() : 0.0);
        t.setTinhTrangSucKhoe(r[8] != null ? r[8].toString() : null);
        return t;
    }
}
