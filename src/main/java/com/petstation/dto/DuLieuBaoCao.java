package com.petstation.dto;

import com.petstation.model.chung.Quyen;

import java.util.function.Function;

public final class DuLieuBaoCao {
    private DuLieuBaoCao() {}

    public static final class DuLieu {
        private final String[] bieuDoCacNhan;
        private final double[] bieuDoCacGiaTri;
        private final boolean tienTe;
        private final String donVi;
        private final String[] cacCot;
        private final Object[][] cacDong;
        private final String[] theCacTieuDe;
        private final String[] theCacGiaTri;

        public DuLieu(
                String[] bieuDoCacNhan,
                double[] bieuDoCacGiaTri,
                boolean tienTe,
                String donVi,
                String[] cacCot,
                Object[][] cacDong,
                String[] theCacTieuDe,
                String[] theCacGiaTri) {
            this.bieuDoCacNhan = bieuDoCacNhan;
            this.bieuDoCacGiaTri = bieuDoCacGiaTri;
            this.tienTe = tienTe;
            this.donVi = donVi;
            this.cacCot = cacCot;
            this.cacDong = cacDong;
            this.theCacTieuDe = theCacTieuDe;
            this.theCacGiaTri = theCacGiaTri;
        }

        public String[] bieuDoCacNhan() {
            return bieuDoCacNhan;
        }

        public double[] bieuDoCacGiaTri() {
            return bieuDoCacGiaTri;
        }

        public boolean tienTe() {
            return tienTe;
        }

        public String donVi() {
            return donVi;
        }

        public String[] cacCot() {
            return cacCot;
        }

        public Object[][] cacDong() {
            return cacDong;
        }

        public String[] theCacTieuDe() {
            return theCacTieuDe;
        }

        public String[] theCacGiaTri() {
            return theCacGiaTri;
        }
    }

    public static final class KieuHienThi {
        private final String ten;
        private final Function<KhoangNgay, DuLieu> tao;
        private final boolean hienTai;
        private final Quyen quyen;

        public KieuHienThi(
                String ten, Function<KhoangNgay, DuLieu> tao, boolean hienTai, Quyen quyen) {
            this.ten = ten;
            this.tao = tao;
            this.hienTai = hienTai;
            this.quyen = quyen;
        }

        public KieuHienThi(String ten, Function<KhoangNgay, DuLieu> tao) {
            this(ten, tao, false, null);
        }

        public KieuHienThi(String ten, Function<KhoangNgay, DuLieu> tao, boolean hienTai) {
            this(ten, tao, hienTai, null);
        }

        public String ten() {
            return ten;
        }

        public Function<KhoangNgay, DuLieu> tao() {
            return tao;
        }

        public boolean hienTai() {
            return hienTai;
        }

        public Quyen quyen() {
            return quyen;
        }

        public KieuHienThi chiQuanLy() {
            return new KieuHienThi(ten, tao, hienTai, Quyen.XEM_BAO_CAO_CHUNG);
        }
    }
}
