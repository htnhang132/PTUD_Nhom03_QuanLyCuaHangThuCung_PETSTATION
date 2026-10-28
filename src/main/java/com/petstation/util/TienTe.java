package com.petstation.util;

import java.text.NumberFormat;
import java.util.Locale;

/** Định dạng, đọc và tính các giá trị tiền bằng đồng Việt Nam. */
public final class TienTe {
    /** Định dạng. */
    private static final NumberFormat DINH_DANG =
            NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

    private TienTe() {}

    public static String dinhDang(long soTien) {
        synchronized (DINH_DANG) {
            return DINH_DANG.format(soTien) + "₫";
        }
    }

    /**
     * Đọc trong TienTe.
     *
     * <p>Đọc "1.250.000", "1250000đ", "1.250.000₫"... Trả về -1 nếu không hợp lệ.
     *
     * @param vanBan văn bản
     * @return kết quả đọc
     */
    public static long doc(String vanBan) {
        if (vanBan == null) return -1;
        // Các chữ số.
        String cacChuSo = vanBan.replaceAll("[^0-9]", "");
        if (cacChuSo.isEmpty() || cacChuSo.length() > 12) return -1;
        return Long.parseLong(cacChuSo);
    }
}
