package com.petstation.gui.components;

import com.petstation.model.VaiTro;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class TruongDanhMuc {
    private TruongDanhMuc() {}

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
        VaiTro[] cacVaiTro = VaiTro.values();

        String[] ra = new String[cacVaiTro.length];
        for (int chiSo = 0; chiSo < cacVaiTro.length; chiSo++)
            ra[chiSo] = cacVaiTro[chiSo].layTenHienThi();
        return ra;
    }
}
