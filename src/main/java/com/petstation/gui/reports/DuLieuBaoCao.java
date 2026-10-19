package com.petstation.gui.reports;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.model.KhoangNgay;
import com.petstation.util.TienTe;

import java.util.*;
import java.util.function.Function;

/** Cung cấp giao diện và xử lý tương tác cho báo cáo các kiểu hiển thị. */
public final class DuLieuBaoCao {
    /** Khởi tạo DuLieuBaoCao với các giá trị được cung cấp. */
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
            Function<KhoangNgay, List<Object[]>> truyVan) {
        return new KieuHienThi(
                tieuDe,
                soNgay -> {
                    // Các dòng.
                    List<Object[]> cacDong = truyVan.apply(soNgay);
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
                                soNgay.demSoNgay() + " ngày",
                                soNgay.denNgay()
                                        .format(
                                                java.time.format.DateTimeFormatter.ofPattern(
                                                        "dd/MM/yyyy"))
                            });
                });
    }
}
