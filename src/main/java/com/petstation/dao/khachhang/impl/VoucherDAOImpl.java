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
                "SELECT v.ma_voucher, v.ma_khach_hang, v.menh_gia, v.don_toi_thieu,"
                        + " v.hieu_luc_den, v.su_dung_luc, v.chi_ap_dung_spa, v.so_luong,"
                        + " v.da_dung, v.trang_thai, v.khoa_doi_diem FROM voucher v"
                        + " WHERE v.ma_khach_hang = ? ORDER BY v.hieu_luc_den DESC",
                r -> chuyenDong(r),
                maKhachHang);
    }

    @Override
    public Voucher timTheoMa(String maVoucher) {
        List<Object[]> rows =
                truyVan(
                        "SELECT v.ma_voucher, v.ma_khach_hang, v.menh_gia, v.don_toi_thieu,"
                                + " v.hieu_luc_den, v.su_dung_luc, v.chi_ap_dung_spa, v.so_luong,"
                                + " v.da_dung, v.trang_thai, v.khoa_doi_diem FROM voucher v"
                                + " WHERE v.ma_voucher = ?",
                        maVoucher);
        if (rows.isEmpty()) return null;
        return chuyenDong(rows.get(0));
    }

    @Override
    public List<Voucher> layDanhSachVoucher() {
        List<Object[]> rows =
                truyVan(
                        "SELECT v.ma_voucher, k.ho_ten, v.menh_gia, v.don_toi_thieu,"
                                + " v.hieu_luc_den, v.su_dung_luc, v.chi_ap_dung_spa, v.so_luong,"
                                + " v.da_dung, v.trang_thai, v.khoa_doi_diem FROM voucher v"
                                + " LEFT JOIN khach_hang k ON k.ma_khach_hang = v.ma_khach_hang"
                                + " ORDER BY v.hieu_luc_den DESC");
        List<Voucher> ketQua = new ArrayList<>();
        for (Object[] r : rows) {
            Voucher v = chuyenDong(r);
            if (r[1] != null && !r[1].toString().isBlank()) {
                KhachHang kh = new KhachHang();
                kh.setHoTen(r[1].toString());
                v.setKhachHang(kh);
            } else {
                v.setKhachHang(null);
            }
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
        v.setMenhGia(r[2] != null ? ((Number) r[2]).longValue() : 0L);
        v.setDonToiThieu(r[3] != null ? ((Number) r[3]).longValue() : 0L);
        if (r[4] instanceof Timestamp ts) {
            v.setHieuLucDen(ts.toLocalDateTime());
        } else if (r[4] instanceof Date d) {
            v.setHieuLucDen(d.toLocalDate().atStartOfDay());
        }
        if (r[5] instanceof Timestamp ts) {
            v.setSuDungLuc(ts.toLocalDateTime());
        }
        v.setChiApDungSpa(
                Boolean.TRUE.equals(r[6]) || (r[6] instanceof Number n && n.intValue() != 0));
        v.setSoLuong(r[7] != null ? ((Number) r[7]).intValue() : 1);
        v.setDaDung(r[8] != null ? ((Number) r[8]).intValue() : 0);
        v.setTrangThai(r[9] != null ? r[9].toString() : null);
        v.setKhoaDoiDiem(r[10] != null ? r[10].toString() : null);
        return v;
    }
}
