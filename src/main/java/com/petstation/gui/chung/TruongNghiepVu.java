package com.petstation.gui.chung;

import com.petstation.dto.MucLuaChon;
import com.petstation.gui.chung.thanhphan.BieuMauTruong.LuaChon;
import com.petstation.gui.chung.thanhphan.BieuMauTruong.TruongDuLieu;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.util.DinhDang;

import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public final class TruongNghiepVu {
    private TruongNghiepVu() {}

    public static TruongDuLieu taoLuaChon(String khoa, String nhan, List<Object[]> cacDong) {
        return TruongDuLieu.layLuaChon(khoa, nhan, cacDong);
    }

    public static TruongDuLieu taoLuaChonMuc(String khoa, String nhan, List<MucLuaChon> cacDong) {
        return new TruongDuLieu(
                khoa,
                nhan,
                "",
                cacDong.stream().map(m -> new LuaChon(m.giaTri(), m.nhan())).toList());
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
            throw new IllegalArgumentException(
                    "Vui lòng nhập thời gian theo định dạng dd/MM/yyyy HH:mm.");
        String val = s.trim();
        for (var fmt : new DateTimeFormatter[] {DinhDang.NGAY_GIO_U, DinhDang.NGAY_GIO_ISO_U}) {
            try {
                return LocalDateTime.parse(val, fmt);
            } catch (Exception ex) {
                LoggerFactory.getLogger(TruongNghiepVu.class)
                        .debug(
                                "Chuỗi thời gian '{}' chưa khớp định dạng {}: {}",
                                val,
                                fmt,
                                ex.getMessage());
            }
        }
        throw new IllegalArgumentException(
                "Thời gian không hợp lệ (định dạng chuẩn: dd/MM/yyyy HH:mm).");
    }

    public static String layThoiGianHienTai() {
        return LocalDateTime.now().withSecond(0).format(DinhDang.NGAY_GIO_U);
    }

    public static TruongDuLieu layThuCung() {
        return taoLuaChonMuc("pet", "Thú cưng", new NghiepVuService().layLuaChonThuCung());
    }

    public static TruongDuLieu layCacGoiDichVu() {
        return taoLuaChonMuc("package", "Dịch vụ", new NghiepVuService().layLuaChonGoiDichVu());
    }

    public static TruongDuLieu layCacLoHang() {
        return taoLuaChonMuc("batch", "Lô hàng", new NghiepVuService().layLuaChonLoHang());
    }
}
