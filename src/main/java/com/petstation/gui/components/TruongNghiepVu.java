package com.petstation.gui.components;

import static com.petstation.dao.ThaoTacSQL.*;

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
    /** Khởi tạo TruongNghiepVu với các giá trị được cung cấp. */
    private TruongNghiepVu() {}

    /** Thời gian ngày. */
    public static final DateTimeFormatter THOI_GIAN_NGAY =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    /**
     * Tạo lựa chọn trong TruongNghiepVu.
     *
     * @param khoa khóa
     * @param nhan nhãn
     * @param cacDong các dòng
     * @return kết quả tạo lựa chọn
     */
    public static TruongDuLieu taoLuaChon(String khoa, String nhan, List<Object[]> cacDong) {
        return TruongDuLieu.layLuaChon(khoa, nhan, cacDong);
    }

    /**
     * Lấy trường văn bản trong TruongNghiepVu.
     *
     * @param khoa khóa
     * @param nhan nhãn
     * @param banDau ban đầu
     * @return kết quả lấy trường văn bản
     */
    public static TruongDuLieu layTruongVanBan(String khoa, String nhan, String banDau) {
        return TruongDuLieu.chuyenThanhChuoi(khoa, nhan, banDau);
    }

    /**
     * Đọc số tiền trong TruongNghiepVu.
     *
     * @param giaTri giá trị
     * @param khoa khóa
     * @return kết quả đọc số tiền
     */
    public static long docSoTien(Map<String, String> giaTri, String khoa) {
        return Long.parseLong(giaTri.get(khoa));
    }

    /**
     * Đọc số lượng trong TruongNghiepVu.
     *
     * @param giaTri giá trị
     * @param khoa khóa
     * @return kết quả đọc số lượng
     */
    public static int docSoLuong(Map<String, String> giaTri, String khoa) {
        return Integer.parseInt(giaTri.get(khoa));
    }

    /**
     * Đọc thời gian trong TruongNghiepVu.
     *
     * @param giaTri giá trị
     * @param khoa khóa
     * @return kết quả đọc thời gian
     */
    public static LocalDateTime docThoiGian(Map<String, String> giaTri, String khoa) {
        return LocalDateTime.parse(giaTri.get(khoa), THOI_GIAN_NGAY);
    }

    /**
     * Lấy thời gian hiện tại trong TruongNghiepVu.
     *
     * @return kết quả lấy thời gian hiện tại
     */
    public static String layThoiGianHienTai() {
        return LocalDateTime.now().withSecond(0).format(THOI_GIAN_NGAY);
    }

    /**
     * Lấy thú cưng trong TruongNghiepVu.
     *
     * @return kết quả lấy thú cưng
     */
    public static TruongDuLieu layThuCung() {
        return taoLuaChon("pet", "Thú cưng", LuaChonNghiepVuService.layLuaChonThuCung());
    }

    /**
     * Lấy các gói dịch vụ trong TruongNghiepVu.
     *
     * @return kết quả lấy các gói dịch vụ
     */
    public static TruongDuLieu layCacGoiDichVu() {
        return taoLuaChon("package", "Dịch vụ", LuaChonNghiepVuService.layLuaChonGoiDichVu());
    }

    /**
     * Lấy các lô hàng trong TruongNghiepVu.
     *
     * @return kết quả lấy các lô hàng
     */
    public static TruongDuLieu layCacLoHang() {
        return taoLuaChon("batch", "Lô hàng", LuaChonNghiepVuService.layLuaChonLoHang());
    }
}
