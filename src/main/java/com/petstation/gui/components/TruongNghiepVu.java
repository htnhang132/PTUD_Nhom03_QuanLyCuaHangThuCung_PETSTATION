package com.petstation.gui.components;

import com.petstation.gui.components.common.BieuMauSQL.TruongDuLieu;
import com.petstation.service.LuaChonNghiepVuService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public final class TruongNghiepVu {
    private TruongNghiepVu() {}

    public static final DateTimeFormatter THOI_GIAN_NGAY =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    public static TruongDuLieu taoLuaChon(String khoa, String nhan, List<Object[]> cacDong) {
        return TruongDuLieu.layLuaChon(khoa, nhan, cacDong);
    }

    public static TruongDuLieu layTruongVanBan(String khoa, String nhan, String banDau) {
        return TruongDuLieu.chuyenThanhChuoi(khoa, nhan, banDau);
    }

    public static long docSoTien(Map<String, String> giaTri, String khoa) {
        String s = giaTri == null ? null : giaTri.get(khoa);
        if (s == null || s.isBlank())
            throw new IllegalArgumentException("Vui lòng nhập số tiền hợp lệ.");
        try {
            return Long.parseLong(s.trim().replaceAll("[.,\\s₫đ]", ""));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số tiền nhập vào không hợp lệ.");
        }
    }

    public static int docSoLuong(Map<String, String> giaTri, String khoa) {
        String s = giaTri == null ? null : giaTri.get(khoa);
        if (s == null || s.isBlank())
            throw new IllegalArgumentException("Vui lòng nhập số lượng hợp lệ.");
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số lượng phải là số nguyên hợp lệ.");
        }
    }

    public static LocalDateTime docThoiGian(Map<String, String> giaTri, String khoa) {
        String s = giaTri == null ? null : giaTri.get(khoa);
        if (s == null || s.isBlank())
            throw new IllegalArgumentException("Vui lòng nhập thời gian theo định dạng yyyy-MM-dd HH:mm.");
        try {
            return LocalDateTime.parse(s.trim(), THOI_GIAN_NGAY);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Thời gian không hợp lệ (định dạng chuẩn: yyyy-MM-dd HH:mm).");
        }
    }

    public static String layThoiGianHienTai() {
        return LocalDateTime.now().withSecond(0).format(THOI_GIAN_NGAY);
    }

    public static TruongDuLieu layThuCung() {
        return taoLuaChon("pet", "Thú cưng", LuaChonNghiepVuService.layLuaChonThuCung());
    }

    public static TruongDuLieu layCacGoiDichVu() {
        return taoLuaChon("package", "Dịch vụ", LuaChonNghiepVuService.layLuaChonGoiDichVu());
    }

    public static TruongDuLieu layCacLoHang() {
        return taoLuaChon("batch", "Lô hàng", LuaChonNghiepVuService.layLuaChonLoHang());
    }
}
