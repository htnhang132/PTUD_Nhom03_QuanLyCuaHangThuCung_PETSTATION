package com.petstation.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

public final class MauUngDung {
    private MauUngDung() {}

    public static final Color NEN_KEM = new Color(0xFD, 0xF9, 0xED);

    public static final Color DO_THUONG_HIEU = new Color(0xCE, 0x1D, 0x2D);

    public static final Color DO_RE_CHUOT = new Color(0xAD, 0x16, 0x25);

    public static final Color DO_NHAT = new Color(0xFC, 0xE8, 0xEA);

    public static final Color VANG = new Color(0xFD, 0xD6, 0x31);

    public static final Color NAU = new Color(0x61, 0x34, 0x1D);

    public static final Color THANH_BEN_NEN = new Color(0x3D, 0x1A, 0x0A);

    public static final Color THANH_BEN_RE_CHUOT = new Color(0x5A, 0x28, 0x12);

    public static final Color THANH_BEN_HOAT_DONG = new Color(0xCE, 0x1D, 0x2D);

    public static final Color VAN_BAN_TOI = new Color(0x1A, 0x0A, 0x00);

    public static final Color VAN_BAN_XAM = new Color(0x5B, 0x63, 0x70);

    public static final Color TRANG = Color.WHITE;

    public static final Color VIEN = new Color(0xDF, 0xD5, 0xC9);

    public static final Color BE_MAT_XEN_KE = new Color(0xFD, 0xF9, 0xED);

    public static final Color DONG_XEN_KE = new Color(0xFB, 0xF8, 0xF1);

    public static final Color XANH_LA = new Color(0x15, 0x80, 0x3D);

    public static final Color CAM = new Color(0xB4, 0x5F, 0x06);

    public static final Color XANH_DUONG = new Color(0x25, 0x63, 0xEB);

    public static final Color VAN_BAN_NGUY_HIEM = new Color(0xB9, 0x1C, 0x1C);

    public static final Color THANH_CONG = XANH_LA;

    public static final Color VAN_BAN_THANH_CONG = XANH_LA;

    public static final Color CANH_BAO = CAM;

    public static final Color NGUY_HIEM = VAN_BAN_NGUY_HIEM;

    public static final Color CHINH = DO_THUONG_HIEU;

    public static final Color VO_HIEU_HOA = new Color(0x9A, 0xA4, 0xAF);

    public static final Color THE_NEN = Color.WHITE;

    public static final Color CANH_BAO_NEN = new Color(0xFF, 0xF7, 0xE6);

    public static final Color DONG_Y_NEN = new Color(0xDC, 0xFC, 0xE7);

    public static final Color KHONG_HOP_LE_NEN = new Color(0xFE, 0xE2, 0xE2);

    private static final String NHOM = chonNhom();

    private static String chonNhom() {
        try {
            for (String ten :
                    GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .getAvailableFontFamilyNames()) {
                if ("Segoe UI".equals(ten)) return ten;
            }
        } catch (Throwable boQua) {
        }
        return Font.SANS_SERIF;
    }

    public static Font taoPhongChu(int kieuDang, int kichThuoc) {
        return new Font(NHOM, kieuDang, kichThuoc);
    }

    public static Color layMauTongMau(String vanBan) {
        if (vanBan == null) return VAN_BAN_TOI;
        String giaTriTam = vanBan.toLowerCase();
        if (layChuaBatKy(
                giaTriTam,
                "hết hàng",
                "từ chối",
                "đã huỷ",
                "đã hủy",
                "quá hạn",
                "lỗi",
                "khóa",
                "bảo trì",
                "chênh lệch")) return VAN_BAN_NGUY_HIEM;
        if (layChuaBatKy(
                giaTriTam, "chờ", "sắp hết", "đang xử lý", "tạm tính", "cần", "nghỉ phép", "chưa"))
            return CAM;
        if (giaTriTam.contains("đã ghi sổ")) return VAN_BAN_TOI;
        if (layChuaBatKy(
                giaTriTam,
                "hoàn thành",
                "đã thanh toán",
                "đã nhập",
                "đủ hàng",
                "đã duyệt",
                "đã hoàn tiền",
                "hoạt động",
                "đang làm",
                "đã xác nhận",
                "đã ghi",
                "đã áp dụng",
                "đang làm")) return XANH_LA;
        return VAN_BAN_TOI;
    }

    private static boolean layChuaBatKy(String vanBan, String... cacTuKhoa) {
        for (String tuKhoa : cacTuKhoa) {
            if (vanBan.contains(tuKhoa)) return true;
        }
        return false;
    }
}
