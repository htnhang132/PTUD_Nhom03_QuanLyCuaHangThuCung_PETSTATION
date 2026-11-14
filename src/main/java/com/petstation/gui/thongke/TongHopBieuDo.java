package com.petstation.gui.thongke;

import com.petstation.dto.KhoangNgay;
import com.petstation.util.DinhDang;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TongHopBieuDo {
    private TongHopBieuDo() {}

    public static final class KetQua {
        private final String[] nhan;
        private final double[] giaTri;
        private final List<int[]> dongGoc;

        public KetQua(String[] nhan, double[] giaTri, List<int[]> dongGoc) {
            this.nhan = nhan;
            this.giaTri = giaTri;
            this.dongGoc = dongGoc;
        }

        public String[] nhan() {
            return nhan;
        }

        public double[] giaTri() {
            return giaTri;
        }

        public List<int[]> dongGoc() {
            return dongGoc;
        }
    }

    public static KetQua tao(String[] cacNhan, double[] cacGiaTri, KhoangNgay ky, String muc) {
        if (cacNhan.length != cacGiaTri.length)
            throw new IllegalArgumentException("Nhãn và số liệu lệch nhau");
        boolean ngay = cacNhan.length > 0;
        for (String nhan : cacNhan)
            try {
                LocalDate.parse(nhan.substring(0, 10));
            } catch (RuntimeException e) {
                ngay = false;
                break;
            }
        String nhom = muc == null ? "Ngày" : muc;
        Map<String, Double> tong = new LinkedHashMap<>();
        Map<String, List<Integer>> cacDong = new LinkedHashMap<>();
        if (ngay)
            for (LocalDate d = ky.getTuNgay(); !d.isAfter(ky.getDenNgay()); d = d.plusDays(1)) {
                String khoaNgay = khoa(d, nhom);
                tong.putIfAbsent(khoaNgay, 0d);
                cacDong.putIfAbsent(khoaNgay, new ArrayList<>());
            }
        for (int i = 0; i < cacNhan.length; i++) {
            String khoaMuc =
                    ngay ? khoa(LocalDate.parse(cacNhan[i].substring(0, 10)), nhom) : cacNhan[i];
            tong.merge(khoaMuc, cacGiaTri[i], Double::sum);
            cacDong.computeIfAbsent(khoaMuc, k -> new ArrayList<>()).add(i);
        }
        return new KetQua(
                tong.keySet().toArray(String[]::new),
                tong.values().stream().mapToDouble(Double::doubleValue).toArray(),
                cacDong.values().stream()
                        .map(dong -> dong.stream().mapToInt(Integer::intValue).toArray())
                        .toList());
    }

    private static String khoa(LocalDate d, String muc) {
        return switch (muc) {
            case "Tháng" -> d.format(DinhDang.THANG_NAM);
            case "Tuần" ->
                    "Tuần " + d.minusDays(d.getDayOfWeek().getValue() - 1).format(DinhDang.NGAY);
            default -> d.format(DinhDang.NGAY);
        };
    }
}
