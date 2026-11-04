package com.petstation.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/** Tiện ích thuần, không mở kết nối và không giữ trạng thái ứng dụng. */
public final class DuLieu {
    private static final DateTimeFormatter DINH_DANG_NGAY_HD =
            DateTimeFormatter.ofPattern("yyMMdd");

    private static final ReferenceQueue<Object> HANG_DOI_THU_HOI = new ReferenceQueue<>();
    private static final Map<KhoaThamChieuYeu, Long> BO_NHO_CAU_NOI = new ConcurrentHashMap<>();

    private static final class KhoaThamChieuYeu extends WeakReference<Object> {
        private final int maBam;

        KhoaThamChieuYeu(Object doiTuong, ReferenceQueue<Object> hangDoi) {
            super(doiTuong, hangDoi);
            this.maBam = System.identityHashCode(doiTuong);
        }

        @Override
        public int hashCode() {
            return maBam;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KhoaThamChieuYeu that)) return false;
            Object a = this.get();
            Object b = that.get();
            return a != null && a == b;
        }
    }

    private static void donDepThamChieuRong() {
        KhoaThamChieuYeu cu;
        while ((cu = (KhoaThamChieuYeu) HANG_DOI_THU_HOI.poll()) != null) {
            BO_NHO_CAU_NOI.remove(cu);
        }
    }

    private DuLieu() {}

    public static void luuIdCauNoi(Object thucThe, Long id) {
        if (thucThe == null) return;
        donDepThamChieuRong();
        KhoaThamChieuYeu khoa = new KhoaThamChieuYeu(thucThe, HANG_DOI_THU_HOI);
        if (id == null) {
            BO_NHO_CAU_NOI.remove(khoa);
        } else {
            BO_NHO_CAU_NOI.put(khoa, id);
        }
    }

    public static Long layIdCauNoiObj(Object thucThe, String maKhoaChinh) {
        if (thucThe != null) {
            donDepThamChieuRong();
            Long daLuu = BO_NHO_CAU_NOI.get(new KhoaThamChieuYeu(thucThe, null));
            if (daLuu != null) {
                return daLuu;
            }
        }
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
        return null;
    }

    public static long layIdCauNoi(Object thucThe, String maKhoaChinh) {
        Long kq = layIdCauNoiObj(thucThe, maKhoaChinh);
        return kq == null ? 0L : kq;
    }

    public static String taoMaTuId(String tienTo, int doRong, Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return (tienTo == null ? "MA" : tienTo) + String.format("%0" + Math.max(1, doRong) + "d", id);
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
