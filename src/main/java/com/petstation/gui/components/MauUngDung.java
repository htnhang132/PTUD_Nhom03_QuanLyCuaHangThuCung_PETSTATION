package com.petstation.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * Cung cấp giao diện và xử lý tương tác cho ứng dụng màu sắc.
 *
 * <p>Bảng màu và font dùng chung toàn ứng dụng.
 *
 * <p>Quy ước màu: {@link #RED_BRAND} chỉ dùng cho hành động chính (nút chính, mục đang chọn). Số
 * liệu thường dùng {@link #TEXT_DARK}; trạng thái dùng {@link #GREEN}, {@link #ORANGE}, {@link
 * #DANGER_TEXT}.
 */
public final class MauUngDung {
    /** Khởi tạo MauUngDung với các giá trị được cung cấp. */
    private MauUngDung() {}

    /** Nền kem. */
    public static final Color NEN_KEM = new Color(0xFD, 0xF9, 0xED);

    /** Đỏ thương hiệu. */
    public static final Color DO_THUONG_HIEU = new Color(0xCE, 0x1D, 0x2D);

    /** Đỏ rê chuột. */
    public static final Color DO_RE_CHUOT = new Color(0xAD, 0x16, 0x25);

    /** Đỏ nhạt. */
    public static final Color DO_NHAT = new Color(0xFC, 0xE8, 0xEA);

    /** Vàng. */
    public static final Color VANG = new Color(0xFD, 0xD6, 0x31);

    /** Nâu. */
    public static final Color NAU = new Color(0x61, 0x34, 0x1D);

    /** Thanh bên nền. */
    public static final Color THANH_BEN_NEN = new Color(0x3D, 0x1A, 0x0A);

    /** Thanh bên rê chuột. */
    public static final Color THANH_BEN_RE_CHUOT = new Color(0x5A, 0x28, 0x12);

    /** Thanh bên hoạt động. */
    public static final Color THANH_BEN_HOAT_DONG = new Color(0xCE, 0x1D, 0x2D);

    /** Văn bản tối. */
    public static final Color VAN_BAN_TOI = new Color(0x1A, 0x0A, 0x00);

    /** Văn bản xám. */
    public static final Color VAN_BAN_XAM = new Color(0x5B, 0x63, 0x70);

    /** Trắng. */
    public static final Color TRANG = Color.WHITE;

    /** Viền. */
    public static final Color VIEN = new Color(0xD0, 0xD7, 0xDE);

    /** Bề mặt xen kẽ. */
    public static final Color BE_MAT_XEN_KE = new Color(0xF6, 0xF8, 0xFA);

    /** Dòng xen kẽ. */
    public static final Color DONG_XEN_KE = new Color(0xFB, 0xF8, 0xF1);

    /** Xanh lá. */
    public static final Color XANH_LA = new Color(0x15, 0x80, 0x3D);

    /** Cam. */
    public static final Color CAM = new Color(0xB4, 0x5F, 0x06);

    /** Xanh dương. */
    public static final Color XANH_DUONG = new Color(0x25, 0x63, 0xEB);

    /** Văn bản nguy hiểm. */
    public static final Color VAN_BAN_NGUY_HIEM = new Color(0xB9, 0x1C, 0x1C);

    /** Thành công. */
    public static final Color THANH_CONG = XANH_LA;

    /** Văn bản thành công. */
    public static final Color VAN_BAN_THANH_CONG = XANH_LA;

    /** Cảnh báo. */
    public static final Color CANH_BAO = CAM;

    /** Nguy hiểm. */
    public static final Color NGUY_HIEM = VAN_BAN_NGUY_HIEM;

    /** Chính. */
    public static final Color CHINH = DO_THUONG_HIEU;

    /** Vô hiệu hóa. */
    public static final Color VO_HIEU_HOA = new Color(0x9A, 0xA4, 0xAF);

    /** Thẻ nền. */
    public static final Color THE_NEN = Color.WHITE;

    /** Cảnh báo nền. */
    public static final Color CANH_BAO_NEN = new Color(0xFF, 0xF7, 0xE6);

    /** Đồng ý nền. */
    public static final Color DONG_Y_NEN = new Color(0xDC, 0xFC, 0xE7);

    /** Không hợp lệ nền. */
    public static final Color KHONG_HOP_LE_NEN = new Color(0xFE, 0xE2, 0xE2);

    /** Nhóm. */
    private static final String NHOM = chonNhom();

    /**
     * Chọn nhóm trong MauUngDung.
     *
     * @return kết quả chọn nhóm
     */
    private static String chonNhom() {
        try {
            for (String ten :
                    GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .getAvailableFontFamilyNames()) {
                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                if ("Segoe UI".equals(ten)) return ten;
            }
        } catch (Throwable boQua) {
            // môi trường không có đồ họa: dùng font logic
        }
        return Font.SANS_SERIF;
    }

    /**
     * Tạo phông chữ trong MauUngDung.
     *
     * <p>Font duy nhất của ứng dụng: Segoe UI nếu có, ngược lại SansSerif.
     *
     * @param kieuDang kiểu dáng
     * @param kichThuoc kích thước
     * @return kết quả tạo phông chữ
     */
    public static Font taoPhongChu(int kieuDang, int kichThuoc) {
        return new Font(NHOM, kieuDang, kichThuoc);
    }

    /**
     * Lấy màu tông màu trong MauUngDung.
     *
     * <p>Màu chữ theo ngữ nghĩa trạng thái hiển thị trong bảng/thẻ.
     *
     * @param vanBan văn bản
     * @return kết quả lấy màu tông màu
     */
    public static Color layMauTongMau(String vanBan) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (vanBan == null) return VAN_BAN_TOI;
        // Giá trị tạm.
        String giaTriTam = vanBan.toLowerCase();
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
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
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (layChuaBatKy(
                giaTriTam, "chờ", "sắp hết", "đang xử lý", "tạm tính", "cần", "nghỉ phép", "chưa"))
            return CAM;
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (layChuaBatKy(
                giaTriTam,
                "hoàn thành",
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

    /**
     * Lấy chứa bất kỳ trong MauUngDung.
     *
     * @param vanBan văn bản
     * @param cacTuKhoa các từ khóa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    private static boolean layChuaBatKy(String vanBan, String... cacTuKhoa) {
        for (String tuKhoa : cacTuKhoa) {
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
            if (vanBan.contains(tuKhoa)) return true;
        }
        return false;
    }
}
