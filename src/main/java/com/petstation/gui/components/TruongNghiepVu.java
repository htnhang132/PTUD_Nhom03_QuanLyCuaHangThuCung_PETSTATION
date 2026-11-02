package com.petstation.gui.components;

import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.common.BieuMauSQL.TruongDuLieu;
import com.petstation.service.LuaChonNghiepVuService;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho nghiệp vụ các trường. */
public final class TruongNghiepVu {
    private TruongNghiepVu() {}

    /** Thời gian ngày. */
    public static final DateTimeFormatter THOI_GIAN_NGAY =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    public static TruongDuLieu taoLuaChon(String khoa, String nhan, List<Object[]> cacDong) {
        return TruongDuLieu.layLuaChon(khoa, nhan, cacDong);
    }

    public static TruongDuLieu layTruongVanBan(String khoa, String nhan, String banDau) {
        return TruongDuLieu.chuyenThanhChuoi(khoa, nhan, banDau);
    }

    public static long docSoTien(Map<String, String> giaTri, String khoa) {
        return Long.parseLong(giaTri.get(khoa));
    }

    public static int docSoLuong(Map<String, String> giaTri, String khoa) {
        return Integer.parseInt(giaTri.get(khoa));
    }

    public static LocalDateTime docThoiGian(Map<String, String> giaTri, String khoa) {
        return LocalDateTime.parse(giaTri.get(khoa), THOI_GIAN_NGAY);
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
