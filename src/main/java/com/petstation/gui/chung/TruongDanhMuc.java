package com.petstation.gui.chung;

import com.petstation.model.chung.VaiTro;
import com.petstation.util.DinhDang;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class TruongDanhMuc {
    private TruongDanhMuc() {}

    public static LocalDate docNgay(Object giaTri) {
        try {
            return LocalDate.parse(String.valueOf(giaTri), DinhDang.NGAY_CHUAN);
        } catch (DateTimeParseException loi) {
            return null;
        }
    }

    public static String[] layTenCacVaiTro() {
        VaiTro[] cacVaiTro = VaiTro.values();
        String[] ra = new String[cacVaiTro.length];
        for (int chiSo = 0; chiSo < cacVaiTro.length; chiSo++)
            ra[chiSo] = cacVaiTro[chiSo].getTenHienThi();
        return ra;
    }
}
