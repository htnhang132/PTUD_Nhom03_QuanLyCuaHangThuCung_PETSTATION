package com.petstation.gui.components;

import java.text.NumberFormat;
import java.util.Locale;

/** Định dạng và đọc số tiền VND (ví dụ 1.250.000₫). */
public final class Money {
    private static final NumberFormat FORMAT = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

    private Money() {}

    public static String format(long amount) {
        synchronized (FORMAT) {
            return FORMAT.format(amount) + "₫";
        }
    }

    /** Đọc "1.250.000", "1250000đ", "1.250.000₫"... Trả về -1 nếu không hợp lệ. */
    public static long parse(String text) {
        if (text == null) return -1;
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty() || digits.length() > 12) return -1;
        return Long.parseLong(digits);
    }
}
