package com.petstation.gui.chung;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MauUngDung {
    private static final Logger LOG = LoggerFactory.getLogger(MauUngDung.class);

    private MauUngDung() {}

    public static final Color NEN_KEM = new Color(0xFD, 0xF9, 0xED);

    public static final Color DO_THUONG_HIEU = new Color(0xCE, 0x1D, 0x2D);

    public static final Color DO_RE_CHUOT = new Color(0xAD, 0x16, 0x25);

    public static final Color DO_NHAT = new Color(0xFC, 0xE8, 0xEA);

    public static final Color VANG = new Color(0xFD, 0xD6, 0x31);

    public static final Color NAU = new Color(0x61, 0x34, 0x1D);

    public static final Color VAN_BAN_TOI = new Color(0x1A, 0x0A, 0x00);

    public static final Color VAN_BAN_XAM = new Color(0x5B, 0x63, 0x70);

    public static final Color TRANG = Color.WHITE;

    public static final Color VIEN = new Color(0xDF, 0xD5, 0xC9);

    public static final Color BE_MAT_XEN_KE = new Color(0xFD, 0xF9, 0xED);

    public static final Color DONG_XEN_KE = new Color(0xFB, 0xF8, 0xF1);

    public static final Color XANH_LA = new Color(0x15, 0x80, 0x3D);

    public static final Color CAM = new Color(0xB4, 0x5F, 0x06);

    public static final Color VAN_BAN_NGUY_HIEM = new Color(0xB9, 0x1C, 0x1C);

    public static final Color THANH_CONG = XANH_LA;

    public static final Color CANH_BAO = CAM;

    public static final Color NGUY_HIEM = VAN_BAN_NGUY_HIEM;

    public static final Color CHINH = DO_THUONG_HIEU;

    public static final Color VO_HIEU_HOA = new Color(0x9A, 0xA4, 0xAF);

    public static final Color THE_NEN = Color.WHITE;

    public static final Color CANH_BAO_NEN = new Color(0xFF, 0xF7, 0xE6);

    public static final Color KHONG_HOP_LE_NEN = new Color(0xFE, 0xE2, 0xE2);

    public static final int CAO_DAU_VAO = 34;
    public static final int CAO_NUT = 34;
    public static final int CAO_DONG_BANG = 38;
    public static final int BAN_KINH_THE = 12;
    public static final int BAN_KINH_NUT = 10;
    public static final int KHOANG_CACH = 12;

    private static final String NHOM = chonNhom();
    private static final Map<Long, Font> BO_DEM_PHONG_CHU = new ConcurrentHashMap<>();

    private static String chonNhom() {
        try {
            for (String ten :
                    GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .getAvailableFontFamilyNames()) {
                if ("Segoe UI".equals(ten)) return ten;
            }
        } catch (RuntimeException ex) {
            LOG.warn("Không đọc được danh sách font hệ thống, dùng SansSerif.", ex);
        }
        return Font.SANS_SERIF;
    }

    public static Font taoPhongChu(int kieuDang, int kichThuoc) {
        long khoa = (((long) kieuDang) << 32) | (kichThuoc & 0xFFFFFFFFL);
        return BO_DEM_PHONG_CHU.computeIfAbsent(khoa, k -> new Font(NHOM, kieuDang, kichThuoc));
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
                "chênh lệch",
                "hỏng",
                "không đạt")) return VAN_BAN_NGUY_HIEM;
        if (layChuaBatKy(
                giaTriTam,
                "chờ",
                "tiếp nhận",
                "đã đặt",
                "đang dọn",
                "sắp hết",
                "đang xử lý",
                "tạm tính",
                "cần",
                "nghỉ phép",
                "chưa")) return CAM;
        if (giaTriTam.contains("đã ghi sổ")) return VAN_BAN_TOI;
        if (layChuaBatKy(
                giaTriTam,
                "hoàn thành",
                "hoàn tất",
                "đã thanh toán",
                "đã trả phòng",
                "trống",
                "đã nhập",
                "đủ hàng",
                "đã duyệt",
                "đã hoàn tiền",
                "hoạt động",
                "đang làm",
                "đang thực hiện",
                "đang ở",
                "có thú",
                "đã xác nhận",
                "đã ghi",
                "đã áp dụng")) return XANH_LA;
        return VAN_BAN_TOI;
    }

    private static boolean layChuaBatKy(String vanBan, String... cacTuKhoa) {
        for (String tuKhoa : cacTuKhoa) {
            if (vanBan.contains(tuKhoa)) return true;
        }
        return false;
    }

    public static final Color XAM_PHU = new Color(0x64748B);
    public static final Color XAM_DAM = new Color(0x334155);
    public static final Color XAM_VIEN_NHE = new Color(0xE2E8F0);
    public static final Color XAM_VIEN_TRUNG = new Color(0xCBD5E1);
    public static final Color XAM_NEN_NHAT = new Color(0xF1F5F9);
    public static final Color XAM_NEN_TRUNG = new Color(0xF3F4F6);
    public static final Color XAM_VIEN_THE = new Color(0xE5E7EB);
    public static final Color XAM_TRUNG_TINH = new Color(0xF5F5F5);
    public static final Color XAM_SIEU_NHAT = new Color(0xF8F9FA);
    public static final Color KEM_VIEN = new Color(0xD8D2C9);
    public static final Color KEM_NEN_NHAN = new Color(0xF3EDE5);
    public static final Color NAU_MO = new Color(0x8C7A6E);
    public static final Color DO_BAO_LOI = new Color(0xDC2626);
    public static final Color DO_NHAN_MANH = new Color(0xC81E1E);
    public static final Color DO_SAU = new Color(0x991B1B);
    public static final Color CAM_NHAN = new Color(0xB45309);
    public static final Color XANH_DUONG_CHINH = new Color(0x2563EB);
    public static final Color XANH_LA_NEN_NHAT = new Color(0xE6F4EA);
    public static final Color THANH_CONG_NEN = new Color(0xD1, 0xFA, 0xE5);
    public static final Color THANH_CONG_HOVER = new Color(0xA7, 0xF3, 0xD0);
    public static final Color THANH_CONG_CHU = new Color(0x06, 0x59, 0x42);
    public static final Color XANH_NHAT_CA = new Color(0xDF, 0xEC, 0xF7);
    public static final Color XANH_LA_CA = new Color(0xE2, 0xF3, 0xD8);
    public static final Color VANG_CA = new Color(0xFE, 0xF3, 0xC7);
    public static final Color NEN_THE_NHAN = new Color(0xFA, 0xF7, 0xF2);
    public static final Color NEN_THE_NOI_BAT = new Color(0xFF, 0xF9, 0xF5);
    public static final Color XAM_CHU_DAM = new Color(0x4B, 0x55, 0x63);
    public static final Color XAM_LICH_CHU = new Color(0x47, 0x55, 0x69);

    public static Color mau(int rgb) {
        return new Color(rgb);
    }

    public static Color voiDoTrong(Color mauGoc, int alpha) {
        return new Color(mauGoc.getRed(), mauGoc.getGreen(), mauGoc.getBlue(), alpha);
    }
}
