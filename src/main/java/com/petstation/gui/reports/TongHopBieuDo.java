package com.petstation.gui.reports;

import com.petstation.model.KhoangNgay;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Tổng hợp chỉ cho biểu đồ; không rút gọn bảng hoặc dữ liệu xuất. */
public final class TongHopBieuDo {
    private TongHopBieuDo() {}

    public record KetQua(String[] nhan, double[] giaTri, List<int[]> dongGoc) {}

    public static KetQua tao(String[] labels, double[] values, KhoangNgay ky, String muc) {
        if (labels.length != values.length)
            throw new IllegalArgumentException("Nhãn và số liệu lệch nhau");
        boolean ngay = labels.length > 0;
        for (String label : labels)
            try {
                LocalDate.parse(label.substring(0, 10));
            } catch (RuntimeException e) {
                ngay = false;
                break;
            }
        String nhom =
                "Tự động".equals(muc)
                        ? (ky.demSoNgay() <= 31 ? "Ngày" : ky.demSoNgay() <= 180 ? "Tuần" : "Tháng")
                        : muc;
        Map<String, Double> tong = new LinkedHashMap<>();
        Map<String, List<Integer>> rows = new LinkedHashMap<>();
        if (ngay)
            for (LocalDate d = ky.tuNgay(); !d.isAfter(ky.denNgay()); d = d.plusDays(1)) {
                String key = khoa(d, nhom);
                tong.putIfAbsent(key, 0d);
                rows.putIfAbsent(key, new ArrayList<>());
            }
        for (int i = 0; i < labels.length; i++) {
            String key = ngay ? khoa(LocalDate.parse(labels[i].substring(0, 10)), nhom) : labels[i];
            tong.merge(key, values[i], Double::sum);
            rows.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
        }
        return new KetQua(
                tong.keySet().toArray(String[]::new),
                tong.values().stream().mapToDouble(Double::doubleValue).toArray(),
                rows.values().stream()
                        .map(r -> r.stream().mapToInt(Integer::intValue).toArray())
                        .toList());
    }

    private static String khoa(LocalDate d, String muc) {
        return switch (muc) {
            case "Tháng" -> d.format(DateTimeFormatter.ofPattern("MM/yyyy"));
            case "Tuần" ->
                    "Tuần "
                            + d.minusDays(d.getDayOfWeek().getValue() - 1)
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            default -> d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        };
    }
}
