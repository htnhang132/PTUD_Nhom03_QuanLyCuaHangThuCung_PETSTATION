package com.petstation.gui.components;

import com.petstation.model.VaiTro;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Cung cấp giao diện và xử lý tương tác cho danh mục các trường. */
public final class TruongDanhMuc {
    private TruongDanhMuc() {}

    /** Ngày. */
    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(java.time.format.ResolverStyle.STRICT);

    public static LocalDate docNgay(Object giaTri) {
        try {
            return LocalDate.parse(String.valueOf(giaTri), NGAY);
        } catch (DateTimeParseException loi) {
            return null;
        }
    }

    public static String[] layTenCacVaiTro() {
        // Các vai trò.
        VaiTro[] cacVaiTro = VaiTro.values();
        // Ra.
        String[] ra = new String[cacVaiTro.length];
        for (int chiSo = 0; chiSo < cacVaiTro.length; chiSo++)
            ra[chiSo] = cacVaiTro[chiSo].layTenHienThi();
        return ra;
    }
}
