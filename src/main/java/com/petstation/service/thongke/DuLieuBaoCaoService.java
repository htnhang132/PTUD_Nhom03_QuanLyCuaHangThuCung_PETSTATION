package com.petstation.service.thongke;

import com.petstation.dto.ChiTietBaoCao.HangBaoCao;
import com.petstation.dto.DuLieuBaoCao.DuLieu;
import com.petstation.dto.DuLieuBaoCao.KieuHienThi;
import com.petstation.dto.KhoangNgay;
import com.petstation.util.DinhDang;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

// Tiện ích chuyển đổi dữ liệu truy vấn SQL (List<Object[]>) thành cấu trúc hiển thị của BaoCaoPanel
public final class DuLieuBaoCaoService {
    private DuLieuBaoCaoService() {}

    // 1. KHỞI TẠO CHẾ ĐỘ XEM THEO KỲ (CÓ LỌC KHOẢNG NGÀY)

    public static <T extends HangBaoCao> KieuHienThi taoKieuHienThi(
            String tieuDe,
            String[] cacCot,
            int nhan,
            int chiSo,
            boolean tienTe,
            Function<KhoangNgay, List<T>> truyVan) {
        boolean baoCaoCongNo = laBaoCaoCongNo(tieuDe);
        return new KieuHienThi(
                tieuDe,
                khoang ->
                        xayDungDuLieuBaoCao(
                                tieuDe,
                                cacCot,
                                nhan,
                                chiSo,
                                tienTe,
                                baoCaoCongNo,
                                truyVan.apply(khoang),
                                khoang));
    }

    private static <T extends HangBaoCao> DuLieu xayDungDuLieuBaoCao(
            String tieuDe,
            String[] cacCot,
            int nhan,
            int chiSo,
            boolean tienTe,
            boolean baoCaoCongNo,
            List<T> cacHang,
            KhoangNgay khoang) {
        Object[][] cacDong = new Object[cacHang.size()][];
        String[] cacNhan = new String[cacHang.size()];
        double[] cacGiaTri = new double[cacHang.size()];
        double tong = 0;
        long tongDon = 0;
        double tongLoiNhuanGop = 0;
        boolean coLoiNhuanGop = cacCot.length >= 7 && "Lợi nhuận gộp".equals(cacCot[6]);
        int cotSoDon = timCotSoDon(cacCot);
        for (int i = 0; i < cacHang.size(); i++) {
            Object[] dong = cacHang.get(i).thanhHang();
            cacDong[i] = dong;
            cacNhan[i] = String.valueOf(dong[nhan]);
            Object giaTri = dong[chiSo];
            cacGiaTri[i] = giaTri == null ? 0 : ((Number) giaTri).doubleValue();
            tong += cacGiaTri[i];
            if (cotSoDon >= 0 && dong[cotSoDon] instanceof Number nDon) tongDon += nDon.longValue();
            if (coLoiNhuanGop && dong[6] instanceof Number nLng)
                tongLoiNhuanGop += nLng.doubleValue();
        }
        if (cotSoDon < 0) tongDon = cacHang.size();
        if (coLoiNhuanGop) {
            return taoDuLieuLoiNhuanGop(
                    cacNhan, cacGiaTri, tienTe, cacCot, cacDong, tong, tongDon, tongLoiNhuanGop);
        }
        return taoDuLieuThongThuong(
                cacNhan,
                cacGiaTri,
                tienTe,
                cacCot,
                cacDong,
                baoCaoCongNo,
                cotSoDon,
                tong,
                tongDon,
                cacHang.size(),
                khoang);
    }

    private static int timCotSoDon(String[] cacCot) {
        for (int c = 0; c < cacCot.length; c++) {
            if ("Số đơn".equals(cacCot[c]) || "Số hóa đơn".equals(cacCot[c])) return c;
        }
        return -1;
    }

    private static DuLieu taoDuLieuLoiNhuanGop(
            String[] cacNhan,
            double[] cacGiaTri,
            boolean tienTe,
            String[] cacCot,
            Object[][] cacDong,
            double tong,
            long tongDon,
            double tongLoiNhuanGop) {
        long aov = tongDon > 0 ? Math.round(tong / tongDon) : 0;
        double tySuat = tong > 0 ? tongLoiNhuanGop * 100.0 / tong : 0;
        return new DuLieu(
                cacNhan,
                cacGiaTri,
                tienTe,
                "",
                cacCot,
                cacDong,
                new String[] {
                    "Tổng số đơn", "Doanh thu thuần", "Giá trị TB/đơn (AOV)", "Lợi nhuận gộp"
                },
                new String[] {
                    tongDon + " đơn",
                    DinhDang.dinhDang(Math.round(tong)),
                    DinhDang.dinhDang(aov),
                    DinhDang.dinhDang(Math.round(tongLoiNhuanGop))
                            + String.format(Locale.ROOT, " (%.1f%%)", tySuat)
                });
    }

    private static DuLieu taoDuLieuThongThuong(
            String[] cacNhan,
            double[] cacGiaTri,
            boolean tienTe,
            String[] cacCot,
            Object[][] cacDong,
            boolean baoCaoCongNo,
            int cotSoDon,
            double tong,
            long tongDon,
            int soBanGhi,
            KhoangNgay khoang) {
        double trungBinh = tongDon > 0 ? tong / tongDon : 0;
        return new DuLieu(
                cacNhan,
                cacGiaTri,
                tienTe,
                "",
                cacCot,
                cacDong,
                new String[] {
                    cotSoDon >= 0 ? "Tổng số đơn" : baoCaoCongNo ? "Số nhà cung cấp" : "Số bản ghi",
                    baoCaoCongNo ? "Tổng dư nợ" : "Tổng giá trị",
                    baoCaoCongNo ? "Dư nợ TB/NCC" : tienTe ? "Giá trị TB" : "Trung bình",
                    "Kỳ báo cáo"
                },
                new String[] {
                    "" + (cotSoDon >= 0 ? tongDon : soBanGhi),
                    tienTe
                            ? DinhDang.dinhDang(Math.round(tong))
                            : String.format(Locale.ROOT, "%.0f", tong),
                    tienTe
                            ? DinhDang.dinhDang(Math.round(trungBinh))
                            : String.format(Locale.ROOT, "%.1f", trungBinh),
                    khoang.demSoNgay() + " ngày"
                });
    }

    // 2. KHỞI TẠO CHẾ ĐỘ XEM HIỆN TẠI (KHÔNG PHỤ THUỘC KỲ BÁO CÁO)

    public static <T extends HangBaoCao> KieuHienThi taoKieuHienTai(
            String ten,
            String[] cot,
            int nhan,
            int chiSo,
            boolean tienTe,
            Function<KhoangNgay, List<T>> truyVan) {
        KieuHienThi goc = taoKieuHienThi(ten, cot, nhan, chiSo, tienTe, truyVan);
        boolean baoCaoCongNo = laBaoCaoCongNo(ten);
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
                            new String[] {
                                baoCaoCongNo ? "Số nhà cung cấp" : "Số bản ghi",
                                baoCaoCongNo ? "Tổng dư nợ" : "Tổng",
                                "Phạm vi",
                                "Thời điểm"
                            },
                            new String[] {
                                d.theCacGiaTri()[0],
                                d.theCacGiaTri()[1],
                                "Hiện tại",
                                LocalDate.now().toString()
                            });
                },
                true);
    }

    // 3. HÀM HỖ TRỢ NHẬN DIỆN LOẠI BÁO CÁO

    private static boolean laBaoCaoCongNo(String ten) {
        String chuDe = ten == null ? "" : ten.toLowerCase(Locale.ROOT);
        return chuDe.contains("công nợ") || chuDe.contains("dư nợ");
    }
}
