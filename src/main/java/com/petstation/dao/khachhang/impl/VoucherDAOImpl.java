package com.petstation.dao.khachhang.impl;

import static com.petstation.database.ThaoTacSQL.truyVan;
import static com.petstation.database.ThaoTacSQL.truyVanDanhSach;

import com.petstation.dao.khachhang.VoucherDAO;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.Voucher;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class VoucherDAOImpl implements VoucherDAO {
    public VoucherDAOImpl() {}

    @Override
    public List<Voucher> layTheoKhachHang(String maKhachHang) {
        return truyVanDanhSach(
                "SELECT v.ma_voucher, v.ma_khach_hang, v.loai_giam_gia, v.gia_tri_giam,"
                        + " v.gia_tri_toi_thieu, v.han_su_dung, v.trang_thai, v.ngay_tao FROM"
                        + " voucher v WHERE v.ma_khach_hang = ? ORDER BY v.han_su_dung DESC",
                r -> chuyenDong(r),
                maKhachHang);
    }

    @Override
    public Voucher timTheoMa(String maVoucher) {
        List<Object[]> rows =
                truyVan(
                        "SELECT v.ma_voucher, v.ma_khach_hang, v.loai_giam_gia, v.gia_tri_giam,"
                            + " v.gia_tri_toi_thieu, v.han_su_dung, v.trang_thai, v.ngay_tao FROM"
                            + " voucher v WHERE v.ma_voucher = ?",
                        maVoucher);
        if (rows.isEmpty()) return null;
        return chuyenDong(rows.get(0));
    }

    @Override
    public List<Voucher> layDanhSachVoucher() {
        List<Object[]> rows =
                truyVan(
                        "SELECT v.ma_voucher, k.ho_ten, v.loai_giam_gia, v.gia_tri_giam,"
                            + " v.gia_tri_toi_thieu, v.han_su_dung, v.trang_thai FROM voucher v"
                            + " LEFT JOIN khach_hang k ON k.ma_khach_hang = v.ma_khach_hang ORDER"
                            + " BY v.han_su_dung DESC");
        List<Voucher> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            Voucher v = new Voucher();
            v.setMaVoucher(r[0] != null ? r[0].toString() : null);
            if (r[1] != null && !r[1].toString().isBlank()) {
                KhachHang kh = new KhachHang();
                kh.setHoTen(r[1].toString());
                v.setKhachHang(kh);
            }
            v.setMenhGia(r[3] != null ? ((Number) r[3]).longValue() : 0L);
            v.setDonToiThieu(r[4] != null ? ((Number) r[4]).longValue() : 0L);
            if (r[5] instanceof Timestamp ts) {
                v.setHieuLucDen(ts.toLocalDateTime());
            } else if (r[5] instanceof Date d) {
                v.setHieuLucDen(d.toLocalDate().atStartOfDay());
            }
            v.setTrangThai(r[6] != null ? r[6].toString() : null);
            ketQua.add(v);
        }
        return ketQua;
    }

    private Voucher chuyenDong(Object[] r) {
        Voucher v = new Voucher();
        v.setMaVoucher(r[0] != null ? r[0].toString() : null);
        if (r[1] != null) {
            KhachHang kh = new KhachHang();
            kh.setMaKhachHang(r[1].toString());
            v.setKhachHang(kh);
        }
        v.setMenhGia(r[3] != null ? ((Number) r[3]).longValue() : 0L);
        v.setDonToiThieu(r[4] != null ? ((Number) r[4]).longValue() : 0L);
        if (r[5] instanceof Timestamp ts) {
            v.setHieuLucDen(ts.toLocalDateTime());
        } else if (r[5] instanceof Date d) {
            v.setHieuLucDen(d.toLocalDate().atStartOfDay());
        }
        v.setTrangThai(r[6] != null ? r[6].toString() : null);
        return v;
    }
}
