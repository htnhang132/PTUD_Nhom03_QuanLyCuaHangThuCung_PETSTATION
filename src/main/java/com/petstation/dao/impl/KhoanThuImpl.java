package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.KhoanThuDAO;
import com.petstation.model.DongThuTien;
import com.petstation.model.PhuongThuc;

import java.util.*;

public final class KhoanThuImpl implements KhoanThuDAO {
    public KhoanThuImpl() {}

    public void ghi(
            Long hoaDon, Long hotel, long ca, long nguoi, String khoa, List<DongThuTien> dong) {
        for (var d : dong)
            capNhat(
                    "INSERT INTO"
                        + " khoan_thu(hoa_don_id,hop_dong_id,phien_id,nguoi_nhan_id,phuong_thuc,so_tien,khach_dua,tien_thua,nhan_luc,khoa)"
                        + " VALUES(?,?,?,?,?,?,?,?,SYSDATETIME(),?)",
                    hoaDon,
                    hotel,
                    ca,
                    nguoi,
                    d.phuongThuc().name(),
                    d.soTien(),
                    d.khachDua(),
                    d.tienThua(),
                    khoa + "-" + d.phuongThuc());
    }

    public List<DongThuTien> lay(String soHoaDon) {
        return truyVan(
                        "SELECT k.phuong_thuc,k.so_tien,k.khach_dua FROM khoan_thu k JOIN hoa_don h"
                                + " ON h.id=k.hoa_don_id WHERE h.so_hoa_don=? ORDER BY k.id",
                        soHoaDon)
                .stream()
                .map(
                        d ->
                                new DongThuTien(
                                        PhuongThuc.valueOf(d[0].toString()),
                                        ((Number) d[1]).longValue(),
                                        ((Number) d[2]).longValue(),
                                        true))
                .toList();
    }
}
