package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.KhoanThuDAO;
import com.petstation.model.DongThuTien;
import com.petstation.model.PhuongThuc;

import java.util.*;
import java.util.stream.Collectors;

public final class KhoanThuDAOImpl implements KhoanThuDAO {
    public KhoanThuDAOImpl() {}

    public void ghi(
            Long hoaDon, Long hotel, long ca, long nguoi, String khoa, List<DongThuTien> dong) {
        if (hoaDon != null) {
            String chiTiet =
                    dong.stream()
                            .map(
                                    d ->
                                            d.phuongThuc().name()
                                                    + ":"
                                                    + d.soTien()
                                                    + ":"
                                                    + d.khachDua()
                                                    + ":"
                                                    + d.tienThua())
                            .collect(Collectors.joining(";"));
            capNhat("UPDATE hoa_don SET chi_tiet_thanh_toan=? WHERE ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)", chiTiet, hoaDon, hoaDon);
        }
        if (hotel != null && !dong.isEmpty()) {
            String maHopDong = String.valueOf(hotel);
            List<Object[]> rHd = truyVan("SELECT ma_hop_dong FROM hop_dong_luu_tru WHERE ma_hop_dong = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)", hotel, hotel);
            if (!rHd.isEmpty()) maHopDong = String.valueOf(rHd.get(0)[0]);

            String maPhien = null;
            if (ca != 0) {
                List<Object[]> rPhien = truyVan("SELECT ma_phien FROM phien_lam_viec WHERE ma_phien = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)", ca, ca);
                if (!rPhien.isEmpty()) maPhien = String.valueOf(rPhien.get(0)[0]);
            }

            String tenDangNhapThu = com.petstation.service.PhienDangNhapService.layTenDangNhap();
            if (tenDangNhapThu == null || tenDangNhapThu.isBlank()) {
                if (nguoi != 0) {
                    List<Object[]> rUser = truyVan("SELECT ten_dang_nhap FROM tai_khoan WHERE ten_dang_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)", nguoi, nguoi);
                    if (!rUser.isEmpty()) tenDangNhapThu = String.valueOf(rUser.get(0)[0]);
                }
            }

            Object thoiDiemThu = layGiaTriDon("SELECT SYSDATETIME()");
            for (DongThuTien d : dong) {
                capNhat(
                        "INSERT INTO lich_su_thu_coc_hotel"
                                + "(ma_hop_dong,khoa_thu_coc,phuong_thuc,so_tien,khach_dua,tien_thua,"
                                + "ma_phien,ten_dang_nhap_thu,tao_luc)"
                                + " VALUES(?,?,?,?,?,?,?,?,?)",
                        maHopDong,
                        khoa,
                        d.phuongThuc().name(),
                        d.soTien(),
                        d.khachDua(),
                        d.tienThua(),
                        maPhien,
                        tenDangNhapThu,
                        thoiDiemThu);
            }
            String phuongThuc = dong.size() == 1 ? dong.get(0).phuongThuc().name() : "KET_HOP";
            capNhat(
                    "UPDATE hop_dong_luu_tru SET phuong_thuc_coc=?, khoa_thu_coc=? WHERE ma_hop_dong = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hop_dong, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)",
                    phuongThuc,
                    khoa,
                    hotel,
                    hotel);
        }
    }

    public List<DongThuTien> lay(String soHoaDon) {
        List<Object[]> cacDong =
                truyVan(
                        "SELECT COALESCE(chi_tiet_thanh_toan,N''), hinh_thuc_thanh_toan, tong_tien,"
                                + " COALESCE(tien_khach_dua,tong_tien) FROM hoa_don WHERE"
                                + " ma_hoa_don=?",
                        soHoaDon);
        if (cacDong.isEmpty()) return List.of();
        Object[] h = cacDong.get(0);
        String chiTiet = String.valueOf(h[0]).trim();
        if (!chiTiet.isEmpty()) {
            List<DongThuTien> kq = new ArrayList<>();
            for (String phan : chiTiet.split(";")) {
                if (phan.isBlank()) continue;
                String[] c = phan.split(":");
                if (c.length >= 2) {
                    PhuongThuc pt = PhuongThuc.docTuCSDL(c[0]);
                    long soTien = new java.math.BigDecimal(c[1].trim()).longValue();
                    long khachDua = c.length >= 3
                            ? new java.math.BigDecimal(c[2].trim()).longValue()
                            : soTien;
                    if (pt != PhuongThuc.TIEN_MAT || khachDua < soTien) {
                        khachDua = soTien;
                    }
                    kq.add(new DongThuTien(pt, soTien, khachDua, true));
                }
            }
            if (!kq.isEmpty()) return kq;
        }
        PhuongThuc pt = PhuongThuc.docTuCSDL(String.valueOf(h[1]));
        long soTien = ((Number) h[2]).longValue();
        long khachDua = ((Number) h[3]).longValue();
        if (pt != PhuongThuc.TIEN_MAT || khachDua < soTien) {
            khachDua = soTien;
        }
        return List.of(new DongThuTien(pt, soTien, khachDua, true));
    }
}
