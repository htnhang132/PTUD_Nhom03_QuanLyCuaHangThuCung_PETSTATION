package com.petstation.gui.reports;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.model.KhoangNgay;
import com.petstation.util.TienTe;

import java.util.*;
import java.util.function.Function;

/** Cung cấp giao diện và xử lý tương tác cho báo cáo các kiểu hiển thị. */
public final class DuLieuBaoCao {
    private DuLieuBaoCao() {}

    public static KieuHienThi taoKieuHienTai(
            String ten,
            String[] cot,
            int nhan,
            int chiSo,
            boolean tienTe,
            Function<KhoangNgay, List<Object[]>> truyVan) {
        KieuHienThi goc = taoKieuHienThi(ten, cot, nhan, chiSo, tienTe, truyVan);
        return new KieuHienThi(
                ten,
                khoang -> {
                    DuLieu d = goc.tao().apply(KhoangNgay.ganDay(1));
                    return new DuLieu(
                            d.bieuDoCacNhan(),
                            d.bieuDoCacGiaTri(),
                            d.tienTe(),
                            d.donVi(),
                            d.cacCot(),
                            d.cacDong(),
                            new String[] {"Số bản ghi", "Tổng", "Phạm vi", "Thời điểm"},
                            new String[] {
                                d.theCacGiaTri()[0],
                                d.theCacGiaTri()[1],
                                "Hiện tại",
                                java.time.LocalDate.now().toString()
                            });
                },
                true);
    }

    public static KieuHienThi taoKieuHienThi(
            String tieuDe,
            String[] cacCot,
            int nhan,
            int chiSo,
            boolean tienTe,
            Function<KhoangNgay, List<Object[]>> truyVan) {
        return new KieuHienThi(
                tieuDe,
                soNgay -> {
                    List<Object[]> cacDong = truyVan.apply(soNgay);
                    // Các nhãn.
                    String[] cacNhan = new String[cacDong.size()];
                    // Các giá trị.
                    double[] cacGiaTri = new double[cacDong.size()];
                    // Tổng.
                    double tong = 0;
                    long tongDon = 0;
                    double tongLoiNhuanGop = 0;
                    boolean coLoiNhuanGop =
                            cacCot.length >= 7 && "Lợi nhuận gộp".equals(cacCot[6]);
                    int cotSoDon = -1;
                    for (int c = 0; c < cacCot.length; c++) {
                        if ("Số đơn".equals(cacCot[c]) || "Số hóa đơn".equals(cacCot[c])) {
                            cotSoDon = c;
                            break;
                        }
                    }
                    for (int chiSo2 = 0; chiSo2 < cacDong.size(); chiSo2++) {
                        Object[] dong = cacDong.get(chiSo2);
                        cacNhan[chiSo2] = String.valueOf(dong[nhan]);
                        Object giaTri = dong[chiSo];
                        cacGiaTri[chiSo2] = giaTri == null ? 0 : ((Number) giaTri).doubleValue();
                        tong += cacGiaTri[chiSo2];
                        if (cotSoDon >= 0 && dong[cotSoDon] instanceof Number nDon) {
                            tongDon += nDon.longValue();
                        }
                        if (coLoiNhuanGop && dong[6] instanceof Number nLng) {
                            tongLoiNhuanGop += nLng.doubleValue();
                        }
                    }
                    if (cotSoDon < 0) {
                        tongDon = cacDong.size();
                    }
                    if (coLoiNhuanGop) {
                        long aov = tongDon > 0 ? Math.round(tong / tongDon) : 0;
                        double tySuat = tong > 0 ? (tongLoiNhuanGop * 100.0 / tong) : 0;
                        return new DuLieu(
                                cacNhan,
                                cacGiaTri,
                                tienTe,
                                "",
                                cacCot,
                                cacDong.toArray(Object[][]::new),
                                new String[] {
                                    "Tổng số đơn",
                                    "Doanh thu thuần",
                                    "Giá trị TB/đơn (AOV)",
                                    "Lợi nhuận gộp"
                                },
                                new String[] {
                                    tongDon + " đơn",
                                    TienTe.dinhDang(Math.round(tong)),
                                    TienTe.dinhDang(aov),
                                    TienTe.dinhDang(Math.round(tongLoiNhuanGop))
                                            + String.format(
                                                    java.util.Locale.ROOT, " (%.1f%%)", tySuat)
                                });
                    }
                    long aov = tongDon > 0 ? Math.round(tong / tongDon) : 0;
                    return new DuLieu(
                            cacNhan,
                            cacGiaTri,
                            tienTe,
                            "",
                            cacCot,
                            cacDong.toArray(Object[][]::new),
                            new String[] {
                                cotSoDon >= 0 ? "Tổng số đơn" : "Số bản ghi",
                                "Tổng giá trị",
                                tienTe ? "Giá trị TB (AOV)" : "Trung bình",
                                "Kỳ báo cáo"
                            },
                            new String[] {
                                "" + (cotSoDon >= 0 ? tongDon : cacDong.size()),
                                tienTe
                                        ? TienTe.dinhDang(Math.round(tong))
                                        : String.format(java.util.Locale.ROOT, "%.0f", tong),
                                tienTe
                                        ? TienTe.dinhDang(aov)
                                        : String.format(java.util.Locale.ROOT, "%.1f", (double) aov),
                                soNgay.demSoNgay() + " ngày"
                            });
                });
    }
}
