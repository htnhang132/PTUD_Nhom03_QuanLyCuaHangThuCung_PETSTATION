package com.petstation.gui.reports;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.util.TienTe;

import java.util.*;
import java.util.function.IntFunction;

/** Cung cấp giao diện và xử lý tương tác cho báo cáo các kiểu hiển thị. */
public final class DuLieuBaoCao {
    /** Khởi tạo DuLieuBaoCao với các giá trị được cung cấp. */
    private DuLieuBaoCao() {}

    /**
     * Tạo kiểu hiển thị trong DuLieuBaoCao.
     *
     * @param tieuDe tiêu đề
     * @param cacCot các cột
     * @param nhan nhãn
     * @param chiSo chỉ số
     * @param tienTe tiền tệ
     * @param truyVan truy vấn
     * @return kết quả tạo kiểu hiển thị
     */
    public static KieuHienThi taoKieuHienThi(
            String tieuDe,
            String[] cacCot,
            int nhan,
            int chiSo,
            boolean tienTe,
            IntFunction<List<Object[]>> truyVan) {
        return new KieuHienThi(
                tieuDe,
                soNgay -> {
                    // Các dòng.
                    List<Object[]> cacDong = truyVan.apply((int) soNgay);
                    // Các nhãn.
                    String[] cacNhan = new String[cacDong.size()];
                    // Các giá trị.
                    double[] cacGiaTri = new double[cacDong.size()];
                    // Tổng.
                    double tong = 0;
                    for (int chiSo2 = 0; chiSo2 < cacDong.size(); chiSo2++) {
                        cacNhan[chiSo2] = String.valueOf(cacDong.get(chiSo2)[nhan]);
                        // Giá trị.
                        Object giaTri = cacDong.get(chiSo2)[chiSo];
                        cacGiaTri[chiSo2] = giaTri == null ? 0 : ((Number) giaTri).doubleValue();
                        tong += cacGiaTri[chiSo2];
                    }
                    return new DuLieu(
                            cacNhan,
                            cacGiaTri,
                            tienTe,
                            "",
                            cacCot,
                            cacDong.toArray(Object[][]::new),
                            new String[] {"Số bản ghi", "Tổng", "Kỳ báo cáo", "Đến ngày"},
                            new String[] {
                                "" + cacDong.size(),
                                tienTe
                                        ? TienTe.dinhDang(Math.round(tong))
                                        : String.format(java.util.Locale.ROOT, "%.0f", tong),
                                (int) soNgay + " ngày",
                                java.time.LocalDate.now()
                                        .format(
                                                java.time.format.DateTimeFormatter.ofPattern(
                                                        "dd/MM/yyyy"))
                            });
                });
    }
}
