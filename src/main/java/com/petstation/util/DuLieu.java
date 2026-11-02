package com.petstation.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** Tiện ích thuần, không mở kết nối và không giữ trạng thái ứng dụng. */
public final class DuLieu {
    private static final DateTimeFormatter DINH_DANG_NGAY_HD =
            DateTimeFormatter.ofPattern("yyMMdd");

    private DuLieu() {}

    public static String taoMa(String tienTo) {
        String p = tienTo == null ? "MA" : tienTo.trim().toUpperCase();
        if ("HT".equals(p)) p = "HDHT";
        else if ("VC".equals(p)) p = "VOUCHER";
        else if ("DV".equals(p)) p = "SPA";
        else if ("P".equals(p)) p = "PH";
        else if ("DH".equals(p)) p = "HD";

        return switch (p) {
            case "HD" ->
                    "HD"
                            + LocalDate.now().format(DINH_DANG_NGAY_HD)
                            + String.format("%04d", ThreadLocalRandom.current().nextInt(1, 10000));
            case "NV", "NCC" ->
                    p + String.format("%04d", ThreadLocalRandom.current().nextInt(1, 10000));
            case "KH", "TC", "LH", "SP", "HDHT", "VOUCHER", "KM", "TREO", "GIO" ->
                    p + String.format("%06d", ThreadLocalRandom.current().nextInt(1, 1000000));
            default ->
                    p + String.format("%08d", ThreadLocalRandom.current().nextInt(1, 100000000));
        };
    }

    public static String chuyenThanhChuoi(Object giaTri) {
        return giaTri == null ? "" : giaTri.toString();
    }
}
