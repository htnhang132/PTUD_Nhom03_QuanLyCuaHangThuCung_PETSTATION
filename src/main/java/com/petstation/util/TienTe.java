package com.petstation.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class TienTe {
    private static final NumberFormat DINH_DANG =
            NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

    private TienTe() {}

    public static String dinhDang(long soTien) {
        synchronized (DINH_DANG) {
            return DINH_DANG.format(soTien) + "₫";
        }
    }

    public static long doc(String vanBan) {
        if (vanBan == null) return -1;

        String cacChuSo = vanBan.replaceAll("[^0-9]", "");
        if (cacChuSo.isEmpty() || cacChuSo.length() > 12) return -1;
        return Long.parseLong(cacChuSo);
    }
}
