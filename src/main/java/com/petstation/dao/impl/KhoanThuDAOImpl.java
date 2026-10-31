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
            capNhat("UPDATE hoa_don SET chi_tiet_thanh_toan=? WHERE id=?", chiTiet, hoaDon);
        }
        if (hotel != null && !dong.isEmpty()) {
            for (DongThuTien d : dong) {
                capNhat(
                        "INSERT INTO lich_su_thu_coc_hotel"
                                + "(hop_dong_id,khoa_thu_coc,phuong_thuc,so_tien,khach_dua,tien_thua,"
                                + "phien_lam_viec_id,nguoi_thu_id,tao_luc)"
                                + " VALUES(?,?,?,?,?,?,?,?,SYSDATETIME())",
                        hotel,
                        khoa,
                        d.phuongThuc().name(),
                        d.soTien(),
                        d.khachDua(),
                        d.tienThua(),
                        ca,
                        nguoi);
            }
            String phuongThuc = dong.size() == 1 ? dong.get(0).phuongThuc().name() : "KET_HOP";
            capNhat(
                    "UPDATE hop_dong_luu_tru SET phuong_thuc_coc=?, khoa_thu_coc=? WHERE id=?",
                    phuongThuc,
                    khoa,
                    hotel);
        }
    }

    public List<DongThuTien> lay(String soHoaDon) {
        List<Object[]> cacDong =
                truyVan(
                        "SELECT COALESCE(chi_tiet_thanh_toan,N''), hinh_thuc_thanh_toan, tong_tien,"
                                + " COALESCE(tien_khach_dua,tong_tien) FROM hoa_don WHERE"
                                + " so_hoa_don=?",
                        soHoaDon);
        if (cacDong.isEmpty()) return List.of();
        Object[] h = cacDong.get(0);
        String chiTiet = String.valueOf(h[0]).trim();
        if (!chiTiet.isEmpty()) {
            List<DongThuTien> kq = new ArrayList<>();
            for (String phan : chiTiet.split(";")) {
                if (phan.isBlank()) continue;
                String[] c = phan.split(":");
                if (c.length >= 3) {
                    kq.add(
                            new DongThuTien(
                                    PhuongThuc.docTuCSDL(c[0]),
                                    Long.parseLong(c[1]),
                                    Long.parseLong(c[2]),
                                    true));
                }
            }
            if (!kq.isEmpty()) return kq;
        }
        return List.of(
                new DongThuTien(
                        PhuongThuc.docTuCSDL(String.valueOf(h[1])),
                        ((Number) h[2]).longValue(),
                        ((Number) h[3]).longValue(),
                        true));
    }
}