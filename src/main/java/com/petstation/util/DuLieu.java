package com.petstation.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class DuLieu {
    private static final DateTimeFormatter DINH_DANG_NGAY_HD =
            DateTimeFormatter.ofPattern("yyMMdd");

    private DuLieu() {}

    public static long layIdCauNoi(Object thucThe, String maKhoaChinh) {
        if (maKhoaChinh != null && !maKhoaChinh.isBlank()) {
            StringBuilder chuSo = new StringBuilder();
            for (int i = maKhoaChinh.length() - 1; i >= 0; i--) {
                char c = maKhoaChinh.charAt(i);
                if (Character.isDigit(c)) {
                    chuSo.insert(0, c);
                } else if (chuSo.length() > 0) {
                    break;
                }
            }
            if (chuSo.length() > 0) {
                try {
                    return Long.parseLong(chuSo.toString());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0L;
    }

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
