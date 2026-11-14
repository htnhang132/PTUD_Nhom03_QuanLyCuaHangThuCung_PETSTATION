package com.petstation.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Tiện ích định dạng chung cho tiền tệ (VND), ngày/giờ và tính tuổi. */
public final class DinhDang {
    private static final NumberFormat DINH_DANG_TIEN =
            NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

    public static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter NGAY_U = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    public static final DateTimeFormatter NGAY_CHUAN =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public static final DateTimeFormatter NGAY_GACH_NGANG =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static final DateTimeFormatter NGAY_NGAN = DateTimeFormatter.ofPattern("d/M/yyyy");

    public static final DateTimeFormatter NGAY_ISO =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    public static final DateTimeFormatter NGAY_GACH_CHEO_NGUOC =
            DateTimeFormatter.ofPattern("uuuu/MM/dd").withResolverStyle(ResolverStyle.STRICT);

    public static final DateTimeFormatter NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static final DateTimeFormatter NGAY_GIO_U =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    public static final DateTimeFormatter NGAY_GIO_ISO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final DateTimeFormatter NGAY_GIO_ISO_U =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    public static final DateTimeFormatter NGAY_GIO_SQL =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter NGAY_GIO_SQL_U =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    public static final DateTimeFormatter GIO_PHUT = DateTimeFormatter.ofPattern("HH:mm");

    public static final DateTimeFormatter NGAY_THANG = DateTimeFormatter.ofPattern("dd/MM");

    public static final DateTimeFormatter NGAY_THANG_GIO_PHUT =
            DateTimeFormatter.ofPattern("dd/MM HH:mm");

    public static final DateTimeFormatter NGAY_THANG_GIO_GIAY =
            DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");

    public static final DateTimeFormatter THANG_NAM = DateTimeFormatter.ofPattern("MM/yyyy");

    public static final DateTimeFormatter MA_NGAY = DateTimeFormatter.ofPattern("yyMMdd");

    public static final DateTimeFormatter TEP_THOI_GIAN =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static final DateTimeFormatter THU_NGAY_VI =
            DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.forLanguageTag("vi-VN"));

    public static final DateTimeFormatter THU_NGAN_VI =
            DateTimeFormatter.ofPattern("EEE dd/MM", Locale.forLanguageTag("vi"));

    private DinhDang() {}

    public static String dinhDang(long soTien) {
        synchronized (DINH_DANG_TIEN) {
            return DINH_DANG_TIEN.format(soTien) + "₫";
        }
    }

    public static long doc(String vanBan) {
        if (vanBan == null) return -1;
        String daTrim = vanBan.trim().replace("−", "-");
        boolean am = daTrim.startsWith("-");
        String cacChuSo = daTrim.replaceAll("[^0-9]", "");
        if (cacChuSo.isEmpty() || cacChuSo.length() > 12) return -1;
        long ketQua = Long.parseLong(cacChuSo);
        return am ? -ketQua : ketQua;
    }

    public static String ngay(LocalDate d) {
        return d == null ? "" : d.format(NGAY);
    }

    public static String ngayGio(LocalDateTime dt) {
        return dt == null ? "" : dt.format(NGAY_GIO);
    }

    public static String gio(LocalTime t) {
        return t == null ? "" : t.format(GIO_PHUT);
    }
}
