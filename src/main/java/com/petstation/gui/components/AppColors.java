package com.petstation.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * Bảng màu và font dùng chung toàn ứng dụng.
 *
 * <p>Quy ước màu: {@link #RED_BRAND} chỉ dùng cho hành động chính (nút chính, mục đang chọn).
 * Số liệu thường dùng {@link #TEXT_DARK}; trạng thái dùng {@link #GREEN}, {@link #ORANGE}, {@link #DANGER_TEXT}.
 */
public final class AppColors {
    private AppColors() {}

    public static final Color BG_CREAM    = new Color(0xFD, 0xF9, 0xED);
    public static final Color RED_BRAND   = new Color(0xCE, 0x1D, 0x2D);
    public static final Color RED_HOVER   = new Color(0xAD, 0x16, 0x25);
    public static final Color RED_TINT    = new Color(0xFC, 0xE8, 0xEA);
    public static final Color YELLOW      = new Color(0xFD, 0xD6, 0x31);
    public static final Color BROWN       = new Color(0x61, 0x34, 0x1D);
    public static final Color SIDEBAR_BG  = new Color(0x3D, 0x1A, 0x0A);
    public static final Color SIDEBAR_HOVER = new Color(0x5A, 0x28, 0x12);
    public static final Color SIDEBAR_ACTIVE = new Color(0xCE, 0x1D, 0x2D);
    public static final Color TEXT_DARK   = new Color(0x1A, 0x0A, 0x00);
    public static final Color TEXT_GRAY   = new Color(0x5B, 0x63, 0x70);
    public static final Color WHITE       = Color.WHITE;
    public static final Color BORDER      = new Color(0xD0, 0xD7, 0xDE);
    public static final Color SURFACE_ALT = new Color(0xF6, 0xF8, 0xFA);
    public static final Color ROW_ALT     = new Color(0xFB, 0xF8, 0xF1);
    public static final Color GREEN       = new Color(0x15, 0x80, 0x3D);
    public static final Color ORANGE      = new Color(0xB4, 0x5F, 0x06);
    public static final Color BLUE        = new Color(0x25, 0x63, 0xEB);
    public static final Color DANGER_TEXT = new Color(0xB9, 0x1C, 0x1C);
    public static final Color SUCCESS     = GREEN;
    public static final Color SUCCESS_TEXT = GREEN;
    public static final Color WARNING     = ORANGE;
    public static final Color DANGER      = DANGER_TEXT;
    public static final Color PRIMARY     = RED_BRAND;
    public static final Color DISABLED    = new Color(0x9A, 0xA4, 0xAF);
    public static final Color CARD_BG     = Color.WHITE;
    public static final Color WARN_BG     = new Color(0xFF, 0xF7, 0xE6);
    public static final Color OK_BG       = new Color(0xDC, 0xFC, 0xE7);
    public static final Color BAD_BG      = new Color(0xFE, 0xE2, 0xE2);

    private static final String FAMILY = pickFamily();

    private static String pickFamily() {
        try {
            for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
                if ("Segoe UI".equals(name)) return name;
            }
        } catch (Throwable ignored) {
            // môi trường không có đồ họa: dùng font logic
        }
        return Font.SANS_SERIF;
    }

    /** Font duy nhất của ứng dụng: Segoe UI nếu có, ngược lại SansSerif. */
    public static Font font(int style, int size) {
        return new Font(FAMILY, style, size);
    }

    /** Màu chữ theo ngữ nghĩa trạng thái hiển thị trong bảng/thẻ. */
    public static Color toneColor(String text) {
        if (text == null) return TEXT_DARK;
        String t = text.toLowerCase();
        if (containsAny(t, "hết hàng", "từ chối", "đã huỷ", "đã hủy", "quá hạn", "lỗi", "khóa", "bảo trì", "chênh lệch")) return DANGER_TEXT;
        if (containsAny(t, "chờ", "sắp hết", "đang xử lý", "tạm tính", "cần", "nghỉ phép", "chưa")) return ORANGE;
        if (containsAny(t, "hoàn thành", "đã nhập", "đủ hàng", "đã duyệt", "đã hoàn tiền", "hoạt động", "đang làm", "đã xác nhận", "đã ghi", "đã áp dụng", "đang làm")) return GREEN;
        return TEXT_DARK;
    }

    private static boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) return true;
        }
        return false;
    }
}
