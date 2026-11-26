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

    public static final class NhanChiSo {
        private final String nhanDem;
        private final String nhanTong;
        private final String nhanTrungBinh;

        public NhanChiSo(String nhanDem, String nhanTong, String nhanTrungBinh) {
            this.nhanDem = nhanDem;
            this.nhanTong = nhanTong;
            this.nhanTrungBinh = nhanTrungBinh;
        }

        public String nhanDem() {
            return nhanDem;
        }

        public String nhanTong() {
            return nhanTong;
        }

        public String nhanTrungBinh() {
            return nhanTrungBinh;
        }
    }

    public static NhanChiSo chiSo(String nhanDem, String nhanTong) {
        return new NhanChiSo(nhanDem, nhanTong, null);
    }

    public static NhanChiSo chiSo(String nhanDem, String nhanTong, String nhanTrungBinh) {
        return new NhanChiSo(nhanDem, nhanTong, nhanTrungBinh);
    }

    /** Chỉ số cột nguồn, độc lập với tên và vị trí hiển thị của cột. */
    public static final class TongHop {
        private final int cotSoDon;
        private final int cotLoiNhuan;
        public static final TongHop THEO_DONG = new TongHop(-1, -1);

        public TongHop(int cotSoDon, int cotLoiNhuan) {
            this.cotSoDon = cotSoDon;
            this.cotLoiNhuan = cotLoiNhuan;
        }

        public int cotSoDon() {
            return cotSoDon;
        }

        public int cotLoiNhuan() {
            return cotLoiNhuan;
        }
    }

    // 1. KHỞI TẠO CHẾ ĐỘ XEM THEO KỲ (CÓ LỌC KHOẢNG NGÀY)

    public static <T extends HangBaoCao> KieuHienThi taoKieuHienThi(
            String tieuDe,
            String[] cacCot,
            int nhan,
            int chiSo,
            boolean tienTe,
            Function<KhoangNgay, List<T>> truyVan) {
        return taoKieuHienThi(tieuDe, cacCot, nhan, chiSo, tienTe, null, truyVan);
    }

    public static <T extends HangBaoCao> KieuHienThi taoKieuHienThi(
            String tieuDe,
            String[] cacCot,
            int nhan,
            int chiSo,
            boolean tienTe,
            NhanChiSo nhanChiSo,
            Function<KhoangNgay, List<T>> truyVan) {
        return taoKieuHienThi(
                tieuDe, cacCot, nhan, chiSo, tienTe, nhanChiSo, TongHop.THEO_DONG, truyVan);
    }

    public static <T extends HangBaoCao> KieuHienThi taoKieuHienThi(
            String tieuDe,
            String[] cacCot,
            int nhan,
            int chiSo,
            boolean tienTe,
            NhanChiSo nhanChiSo,
            TongHop tongHop,
            Function<KhoangNgay, List<T>> truyVan) {
        boolean baoCaoCongNo = false;

        return new KieuHienThi(
                tieuDe,
                khoang ->
                        xayDungDuLieuBaoCao(
                                cacCot,
                                nhan,
                                chiSo,
                                tienTe,
                                baoCaoCongNo,
                                nhanChiSo,
                                tongHop,
                                truyVan.apply(khoang),
                                khoang));
    }

    private static <T extends HangBaoCao> DuLieu xayDungDuLieuBaoCao(
            String[] cacCot,
            int nhan,
            int chiSo,
            boolean tienTe,
            boolean baoCaoCongNo,
            NhanChiSo nhanChiSo,
            TongHop tongHop,
            List<T> cacHang,
            KhoangNgay khoang) {
        Object[][] cacDong = new Object[cacHang.size()][];
        String[] cacNhan = new String[cacHang.size()];
        double[] cacGiaTri = new double[cacHang.size()];
        double tong = 0;
        long tongDon = 0;
        Double tongLoiNhuanGop = 0.0;
        boolean coLoiNhuanGop = tongHop.cotLoiNhuan() >= 0;
        int cotSoDon = tongHop.cotSoDon();
        for (int i = 0; i < cacHang.size(); i++) {
            Object[] dong = cacHang.get(i).thanhHang();
            cacDong[i] = dong;
            cacNhan[i] = String.valueOf(dong[nhan]);
            Object giaTri = dong[chiSo];
            cacGiaTri[i] = giaTri == null ? 0 : ((Number) giaTri).doubleValue();
            tong += cacGiaTri[i];
            if (cotSoDon >= 0 && dong[cotSoDon] instanceof Number nDon) tongDon += nDon.longValue();
            if (coLoiNhuanGop) {
                if (!(dong[tongHop.cotLoiNhuan()] instanceof Number)) tongLoiNhuanGop = null;
                else if (tongLoiNhuanGop != null)
                    tongLoiNhuanGop += ((Number) dong[tongHop.cotLoiNhuan()]).doubleValue();
            }
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
                nhanChiSo,
                cotSoDon,
                tong,
                tongDon,
                cacHang.size(),
                khoang);
    }

    private static DuLieu taoDuLieuLoiNhuanGop(
            String[] cacNhan,
            double[] cacGiaTri,
            boolean tienTe,
            String[] cacCot,
            Object[][] cacDong,
            double tong,
            long tongDon,
            Double tongLoiNhuanGop) {
        long aov = tongDon > 0 ? Math.round(tong / tongDon) : 0;
        double tySuat = tong > 0 && tongLoiNhuanGop != null ? tongLoiNhuanGop * 100.0 / tong : 0;
        return new DuLieu(
                cacNhan,
                cacGiaTri,
                tienTe,
                "",
                cacCot,
                cacDong,
                new String[] {
                    "Tổng số đơn", "Doanh thu thuần", "Giá trị trung bình mỗi đơn", "Lợi nhuận gộp"
                },
                new String[] {
                    tongDon + " đơn",
                    DinhDang.dinhDang(Math.round(tong)),
                    DinhDang.dinhDang(aov),
                    tongLoiNhuanGop == null
                            ? "Cần đối chiếu giá vốn"
                            : DinhDang.dinhDang(Math.round(tongLoiNhuanGop))
                                    + String.format(
                                            Locale.forLanguageTag("vi-VN"), " (%.1f%%)", tySuat)
                });
    }

    private static DuLieu taoDuLieuThongThuong(
            String[] cacNhan,
            double[] cacGiaTri,
            boolean tienTe,
            String[] cacCot,
            Object[][] cacDong,
            boolean baoCaoCongNo,
            NhanChiSo nhanChiSo,
            int cotSoDon,
            double tong,
            long tongDon,
            int soMuc,
            KhoangNgay khoang) {
        double trungBinh = tongDon > 0 ? tong / tongDon : 0;
        String nhanThe1 =
                nhanChiSo != null && nhanChiSo.nhanDem() != null
                        ? nhanChiSo.nhanDem()
                        : cotSoDon >= 0
                                ? "Tổng số đơn"
                                : baoCaoCongNo ? "Số nhà cung cấp" : "Số mục";
        String nhanThe2 =
                nhanChiSo != null && nhanChiSo.nhanTong() != null
                        ? nhanChiSo.nhanTong()
                        : baoCaoCongNo ? "Tổng dư nợ" : "Tổng giá trị";
        String nhanThe3 =
                nhanChiSo != null && nhanChiSo.nhanTrungBinh() != null
                        ? nhanChiSo.nhanTrungBinh()
                        : baoCaoCongNo
                                ? "Dư nợ trung bình mỗi nhà cung cấp"
                                : cotSoDon >= 0
                                        ? "Giá trị TB/đơn"
                                        : tienTe ? "Giá trị TB" : "Trung bình";
        return new DuLieu(
                cacNhan,
                cacGiaTri,
                tienTe,
                "",
                cacCot,
                cacDong,
                new String[] {nhanThe1, nhanThe2, nhanThe3, "Kỳ báo cáo"},
                new String[] {
                    "" + (cotSoDon >= 0 ? tongDon : soMuc),
                    tienTe
                            ? DinhDang.dinhDang(Math.round(tong))
                            : String.format(Locale.forLanguageTag("vi-VN"), "%.0f", tong),
                    tienTe
                            ? DinhDang.dinhDang(Math.round(trungBinh))
                            : String.format(Locale.forLanguageTag("vi-VN"), "%.1f", trungBinh),
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
        return taoKieuHienTai(ten, cot, nhan, chiSo, tienTe, null, truyVan);
    }

    public static <T extends HangBaoCao> KieuHienThi taoKieuHienTai(
            String ten,
            String[] cot,
            int nhan,
            int chiSo,
            boolean tienTe,
            NhanChiSo nhanChiSo,
            Function<KhoangNgay, List<T>> truyVan) {
        KieuHienThi goc = taoKieuHienThi(ten, cot, nhan, chiSo, tienTe, nhanChiSo, truyVan);
        boolean baoCaoCongNo = false;
        String nhanThe1 =
                nhanChiSo != null && nhanChiSo.nhanDem() != null
                        ? nhanChiSo.nhanDem()
                        : baoCaoCongNo ? "Số nhà cung cấp" : "Số mục";
        String nhanThe2 =
                nhanChiSo != null && nhanChiSo.nhanTong() != null
                        ? nhanChiSo.nhanTong()
                        : baoCaoCongNo ? "Tổng dư nợ" : "Tổng";
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
                            new String[] {nhanThe1, nhanThe2, "Phạm vi", "Thời điểm"},
                            new String[] {
                                d.theCacGiaTri()[0],
                                d.theCacGiaTri()[1],
                                "Hiện tại",
                                LocalDate.now().format(DinhDang.NGAY)
                            });
                },
                true);
    }
}
