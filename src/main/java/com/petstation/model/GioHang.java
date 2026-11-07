package com.petstation.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class GioHang {
    public interface ChinhSach {
        List<Object[]> layKhuyenMai();

        long kiemTraVoucher(String ma, String khachHang, String nguon, long tamTinh);

        BienLai thanhToan(GioHang gio, PhuongThuc phuongThuc, long daNhan, String thuNgan);

        BienLai thanhToan(GioHang gio, List<DongThuTien> dong, String thuNgan);
    }

    private final ChinhSach chinhSach;

    public GioHang(ChinhSach chinhSach) {
        this.chinhSach = java.util.Objects.requireNonNull(chinhSach);
    }

    public static class DongHang implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String ma;
        private String ten;
        private long donGia;
        private int soLuong;

        public DongHang() {}

        public DongHang(String ma, String ten, long donGia, int soLuong) {
            if (ma == null || ma.isBlank() || ten == null || donGia < 0 || soLuong <= 0)
                throw new IllegalArgumentException("Dòng hàng không hợp lệ.");
            this.ma = ma;
            this.ten = ten;
            this.donGia = donGia;
            this.soLuong = soLuong;
        }

        public String getMa() { return ma; }
        public void setMa(String ma) {
            if (ma == null || ma.isBlank()) throw new IllegalArgumentException("Mã hàng không được trống.");
            this.ma = ma.trim();
        }

        public String getTen() { return ten; }
        public void setTen(String ten) {
            if (ten == null || ten.isBlank()) throw new IllegalArgumentException("Tên hàng không được trống.");
            this.ten = ten.trim();
        }

        public long getDonGia() { return donGia; }
        public void setDonGia(long donGia) {
            if (donGia < 0) throw new IllegalArgumentException("Đơn giá không được âm.");
            this.donGia = donGia;
        }

        public int getSoLuong() { return soLuong; }
        public void setSoLuong(int soLuong) {
            if (soLuong <= 0) throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
            this.soLuong = soLuong;
        }

        public long docSoTien() {
            return donGia * soLuong;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DongHang dongHang = (DongHang) o;
            return donGia == dongHang.donGia && soLuong == dongHang.soLuong && java.util.Objects.equals(ma, dongHang.ma) && java.util.Objects.equals(ten, dongHang.ten);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(ma, ten, donGia, soLuong);
        }

        @Override
        public String toString() {
            return "DongHang{" + "ma='" + ma + '\'' + ", ten='" + ten + '\'' + ", donGia=" + donGia + ", soLuong=" + soLuong + '}';
        }
    }

    public static class BienLai implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String soHoaDon;
        private LocalDateTime thoiGian;
        private List<DongHang> cacDong;
        private long tamTinh;
        private long giamGia;
        private long tong;
        private PhuongThuc phuongThuc;
        private long daNhan;
        private long thayDoi;
        private String khachHang;
        private String voucher;
        private String thuNgan;
        private List<DongThuTien> cacKhoanThu;

        public BienLai() {}

        public BienLai(
                String soHoaDon,
                LocalDateTime thoiGian,
                List<DongHang> cacDong,
                long tamTinh,
                long giamGia,
                long tong,
                PhuongThuc phuongThuc,
                long daNhan,
                long thayDoi,
                String khachHang,
                String voucher,
                String thuNgan,
                List<DongThuTien> cacKhoanThu) {
            this.soHoaDon = soHoaDon;
            this.thoiGian = thoiGian;
            this.cacDong = cacDong;
            this.tamTinh = tamTinh;
            this.giamGia = giamGia;
            this.tong = tong;
            this.phuongThuc = phuongThuc;
            this.daNhan = daNhan;
            this.thayDoi = thayDoi;
            this.khachHang = khachHang;
            this.voucher = voucher;
            this.thuNgan = thuNgan;
            this.cacKhoanThu = cacKhoanThu != null ? cacKhoanThu : List.of();
        }

        public BienLai(
                String soHoaDon,
                LocalDateTime thoiGian,
                List<DongHang> cacDong,
                long tamTinh,
                long giamGia,
                long tong,
                PhuongThuc phuongThuc,
                long daNhan,
                long thayDoi,
                String khachHang,
                String voucher,
                String thuNgan) {
            this(soHoaDon, thoiGian, cacDong, tamTinh, giamGia, tong, phuongThuc, daNhan, thayDoi, khachHang, voucher, thuNgan, List.of());
        }

        public String getSoHoaDon() { return soHoaDon; }
        public void setSoHoaDon(String soHoaDon) { this.soHoaDon = soHoaDon; }

        public LocalDateTime getThoiGian() { return thoiGian; }
        public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

        public List<DongHang> getCacDong() { return cacDong; }
        public void setCacDong(List<DongHang> cacDong) { this.cacDong = cacDong; }

        public long getTamTinh() { return tamTinh; }
        public void setTamTinh(long tamTinh) { this.tamTinh = tamTinh; }

        public long getGiamGia() { return giamGia; }
        public void setGiamGia(long giamGia) { this.giamGia = giamGia; }

        public long getTong() { return tong; }
        public void setTong(long tong) { this.tong = tong; }

        public PhuongThuc getPhuongThuc() { return phuongThuc; }
        public void setPhuongThuc(PhuongThuc phuongThuc) { this.phuongThuc = phuongThuc; }

        public long getDaNhan() { return daNhan; }
        public void setDaNhan(long daNhan) { this.daNhan = daNhan; }

        public long getThayDoi() { return thayDoi; }
        public void setThayDoi(long thayDoi) { this.thayDoi = thayDoi; }

        public String getKhachHang() { return khachHang; }
        public void setKhachHang(String khachHang) { this.khachHang = khachHang; }

        public String getVoucher() { return voucher; }
        public void setVoucher(String voucher) { this.voucher = voucher; }

        public String getThuNgan() { return thuNgan; }
        public void setThuNgan(String thuNgan) { this.thuNgan = thuNgan; }

        public List<DongThuTien> getCacKhoanThu() { return cacKhoanThu; }
        public void setCacKhoanThu(List<DongThuTien> cacKhoanThu) { this.cacKhoanThu = cacKhoanThu; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BienLai bienLai = (BienLai) o;
            return tamTinh == bienLai.tamTinh && giamGia == bienLai.giamGia && tong == bienLai.tong && daNhan == bienLai.daNhan && thayDoi == bienLai.thayDoi && java.util.Objects.equals(soHoaDon, bienLai.soHoaDon) && java.util.Objects.equals(thoiGian, bienLai.thoiGian) && java.util.Objects.equals(cacDong, bienLai.cacDong) && phuongThuc == bienLai.phuongThuc && java.util.Objects.equals(khachHang, bienLai.khachHang) && java.util.Objects.equals(voucher, bienLai.voucher) && java.util.Objects.equals(thuNgan, bienLai.thuNgan) && java.util.Objects.equals(cacKhoanThu, bienLai.cacKhoanThu);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(soHoaDon, thoiGian, cacDong, tamTinh, giamGia, tong, phuongThuc, daNhan, thayDoi, khachHang, voucher, thuNgan, cacKhoanThu);
        }

        @Override
        public String toString() {
            return "BienLai{" + "soHoaDon='" + soHoaDon + '\'' + ", tong=" + tong + '}';
        }
    }

    private final NguCanhQuyTrinh quyTrinh = new NguCanhQuyTrinh();

    public NguCanhQuyTrinh getQuyTrinh() {
        return quyTrinh;
    }

    private final List<DongHang> cacDong = new ArrayList<>();
    private final List<Runnable> cacBoLangNghe = new java.util.concurrent.CopyOnWriteArrayList<>();
    private volatile boolean dangGhiThanhToan;

    public void themBoLangNghe(Runnable boLangNghe) {
        if (boLangNghe != null) {
            cacBoLangNghe.add(boLangNghe);
        }
    }

    public void xoaBoLangNghe(Runnable boLangNghe) {
        cacBoLangNghe.remove(boLangNghe);
    }

    public void thongBaoThayDoi() {
        for (Runnable boLangNghe : cacBoLangNghe) {
            try {
                boLangNghe.run();
            } catch (Exception ignored) {
            }
        }
    }

    public boolean isDangGhiThanhToan() {
        return dangGhiThanhToan;
    }

    public void setDangGhiThanhToan(boolean value) {
        this.dangGhiThanhToan = value;
    }

    private String khachHangSoDienThoai = "";

    private int giamGiaPhanTram;

    private String maVoucher = "";

    private String maKhachHang = "";

    private String tenKhachHang = "";

    private long khachHangDiem;

    private long diemDaDung;

    private long giaTriVoucher;

    private BienLai bienLaiCuoi;

    private String khoaThanhToan =
            "PAY-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 20);

    private String maHoaDonDuKien = com.petstation.util.DuLieu.taoMa("HD");

    private String kieuNguon = "";

    private long idNguon;

    public static class YeuCauDoiHang implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private long dongGoc;
        private int soLuong;
        private String lyDo;
        private long giaTriDuocDoi;
        private String maKhachHang;

        public YeuCauDoiHang() {}

        public YeuCauDoiHang(long dongGoc, int soLuong, String lyDo, long giaTriDuocDoi, String maKhachHang) {
            this.dongGoc = dongGoc;
            this.soLuong = soLuong;
            this.lyDo = lyDo;
            this.giaTriDuocDoi = giaTriDuocDoi;
            this.maKhachHang = maKhachHang;
        }

        public long getDongGoc() { return dongGoc; }
        public void setDongGoc(long dongGoc) { this.dongGoc = dongGoc; }

        public int getSoLuong() { return soLuong; }
        public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

        public String getLyDo() { return lyDo; }
        public void setLyDo(String lyDo) { this.lyDo = lyDo; }

        public long getGiaTriDuocDoi() { return giaTriDuocDoi; }
        public void setGiaTriDuocDoi(long giaTriDuocDoi) { this.giaTriDuocDoi = giaTriDuocDoi; }

        public String getMaKhachHang() { return maKhachHang; }
        public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    }

    private YeuCauDoiHang doiHang;
    private String xacNhanNhanTien = "";

    private String taoKhoaNhanTien(PhuongThuc pt) {
        return pt + "|" + khoaThanhToan + "|" + tinhTong() + "|" + maVoucher;
    }

    public void xacNhanDaNhanTien(PhuongThuc pt, boolean daNhan) {
        xacNhanNhanTien = daNhan ? taoKhoaNhanTien(pt) : "";
    }

    public boolean kiemTraDaNhanTien(PhuongThuc pt) {
        return pt == PhuongThuc.TIEN_MAT || xacNhanNhanTien.equals(taoKhoaNhanTien(pt));
    }

    public YeuCauDoiHang getDoiHang() {
        return doiHang;
    }

    public void setDoiHang(YeuCauDoiHang giaTri) {
        this.doiHang = giaTri;
        thongBaoThayDoi();
    }

    public List<DongHang> getCacDong() {
        return List.copyOf(cacDong);
    }

    public void setCacDong(List<DongHang> moiCacDong) {
        this.cacDong.clear();
        if (moiCacDong != null) {
            this.cacDong.addAll(moiCacDong);
        }
        thongBaoThayDoi();
    }

    public String getKhachHangSoDienThoai() {
        return khachHangSoDienThoai;
    }

    public void setKhachHangSoDienThoai(String soDienThoai) {
        this.khachHangSoDienThoai = soDienThoai == null ? "" : soDienThoai.trim();
        thongBaoThayDoi();
    }

    public int getGiamGiaPhanTram() {
        return giamGiaPhanTram;
    }

    public void setGiamGiaPhanTram(int phanTram) {
        this.giamGiaPhanTram = Math.max(0, Math.min(phanTram, 100));
        thongBaoThayDoi();
    }

    public String getMaVoucher() {
        return maVoucher;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher == null ? "" : maVoucher;
        thongBaoThayDoi();
    }

    public BienLai getBienLaiCuoi() {
        return bienLaiCuoi;
    }

    public void setBienLaiCuoi(BienLai bienLai) {
        this.bienLaiCuoi = bienLai;
    }

    public String getKhoaThanhToan() {
        return khoaThanhToan;
    }

    public void setKhoaThanhToan(String khoaThanhToan) {
        this.khoaThanhToan = khoaThanhToan;
    }

    public String getMaHoaDonDuKien() {
        return maHoaDonDuKien;
    }

    public void setMaHoaDonDuKien(String maHoaDonDuKien) {
        this.maHoaDonDuKien = maHoaDonDuKien;
    }

    public String getKieuNguon() {
        return kieuNguon;
    }

    public void setKieuNguon(String kieuNguon) {
        this.kieuNguon = kieuNguon;
    }

    public long getIdNguon() {
        return idNguon;
    }

    public void setIdNguon(long idNguon) {
        this.idNguon = idNguon;
    }

    public void setNguon(String kieu, long id) {
        this.kieuNguon = kieu;
        this.idNguon = id;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang == null ? "" : maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang == null ? "" : tenKhachHang;
    }

    public long getKhachHangDiem() {
        return khachHangDiem;
    }

    public void setKhachHangDiem(long khachHangDiem) {
        this.khachHangDiem = Math.max(0, khachHangDiem);
    }

    public long getDiemDaDung() {
        return diemDaDung;
    }

    public void setDiemDaDung(long diem) {
        if (diem != 0) {
            throw new IllegalArgumentException(
                    "Điểm chỉ dùng để đổi voucher Spa; không trừ trực tiếp vào hóa đơn.");
        }
        this.diemDaDung = 0;
    }

    public void setKhachHang(String ma, String ten, long diem) {
        this.maKhachHang = ma == null ? "" : ma;
        this.tenKhachHang = ten == null ? "" : ten;
        this.khachHangDiem = Math.max(0, diem);
        this.maVoucher = "";
        this.giaTriVoucher = 0;
        this.diemDaDung = 0;
        thongBaoThayDoi();
    }

    public long getGiaTriDiem() {
        return 0;
    }

    public boolean kiemTraTrong() {
        return cacDong.isEmpty();
    }

    public KhuyenMai getKhuyenMaiTotNhat() {
        KhuyenMai totNhat = null;
        long tamTinh = tinhTamTinh();
        for (Object[] dongDuLieu : chinhSach.layKhuyenMai()) {
            if ("Ngừng áp dụng".equals(dongDuLieu[8])
                    || !kiemTraTrongKhoangNgay(dongDuLieu[6], dongDuLieu[7])) continue;
            if (tamTinh < com.petstation.util.TienTe.doc(String.valueOf(dongDuLieu[5]))) continue;
            long coSo = 0;

            String phamVi = String.valueOf(dongDuLieu[3]);
            for (DongHang dongDangXet : cacDong) {
                boolean laDichVu = "SPA".equals(kieuNguon) || "HOTEL".equals(kieuNguon);
                if (phamVi.equals("Cả đơn")
                        || (phamVi.equals("Sản phẩm") && !laDichVu)
                        || (phamVi.equals("Dịch vụ") && laDichVu)) coSo += dongDangXet.docSoTien();
            }
            long giaTri = com.petstation.util.TienTe.doc(String.valueOf(dongDuLieu[4]));

            long soTien =
                    switch (String.valueOf(dongDuLieu[2])) {
                        case "Giảm %" -> Math.round(coSo * giaTri / 100.0);
                        case "Giảm tiền" -> coSo > 0 ? Math.min(giaTri, coSo) : 0;
                        default -> 0;
                    };
            if (soTien > 0 && (totNhat == null || soTien > totNhat.getSoTien()))
                totNhat =
                        new KhuyenMai(
                                String.valueOf(dongDuLieu[0]),
                                String.valueOf(dongDuLieu[1]),
                                soTien);
        }
        return totNhat;
    }

    private static boolean kiemTraTrongKhoangNgay(Object tu, Object den) {
        java.time.format.DateTimeFormatter truongDangXet =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        .withResolverStyle(java.time.format.ResolverStyle.STRICT);

        java.time.LocalDate homNay = java.time.LocalDate.now();
        try {
            if (java.time.LocalDate.parse(String.valueOf(tu), truongDangXet).isAfter(homNay))
                return false;
            return !java.time.LocalDate.parse(String.valueOf(den), truongDangXet).isBefore(homNay);
        } catch (java.time.format.DateTimeParseException loi) {
            return false;
        }
    }

    public static class KhuyenMai implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String ma;
        private String ten;
        private long soTien;

        public KhuyenMai() {}

        public KhuyenMai(String ma, String ten, long soTien) {
            this.ma = ma;
            this.ten = ten;
            this.soTien = soTien;
        }

        public String getMa() { return ma; }
        public void setMa(String ma) { this.ma = ma; }

        public String getTen() { return ten; }
        public void setTen(String ten) { this.ten = ten; }

        public long getSoTien() { return soTien; }
        public void setSoTien(long soTien) { this.soTien = soTien; }
    }

    public long tinhTamTinh() {
        long tong = 0;
        for (DongHang dongDangXet : cacDong) tong += dongDangXet.docSoTien();
        return tong;
    }

    public long getKhuyenMaiGiamGia() {
        KhuyenMai vungDangXet = getKhuyenMaiTotNhat();
        return vungDangXet == null ? 0 : vungDangXet.getSoTien();
    }

    public long getSoTienVoucher() {
        return maVoucher.isEmpty()
                ? 0
                : Math.min(giaTriVoucher, Math.max(0, tinhTamTinh() - getKhuyenMaiGiamGia()));
    }

    public long tinhGiamGia() {
        if ("DOI_HANG".equals(kieuNguon)) return doiHang == null ? 0 : doiHang.getGiaTriDuocDoi();

        long theoPhanTram = Math.round(tinhTamTinh() * giamGiaPhanTram / 100.0);
        return Math.min(
                tinhTamTinh(),
                theoPhanTram + getKhuyenMaiGiamGia() + getSoTienVoucher() + getGiaTriDiem());
    }

    public long tinhTong() {
        return tinhTamTinh() - tinhGiamGia();
    }

    public String apDungVoucher(String ma) {
        if ("DOI_HANG".equals(kieuNguon) && ma != null && !ma.isBlank())
            return "Đổi hàng không áp dụng thêm voucher.";
        String giaTri = ma == null ? "" : ma.trim().toUpperCase(Locale.ROOT);
        if (giaTri.isEmpty()) {
            maVoucher = "";
            giaTriVoucher = 0;
            thongBaoThayDoi();
            return null;
        }
        try {
            long soTien = chinhSach.kiemTraVoucher(giaTri, maKhachHang, kieuNguon, tinhTamTinh());
            maVoucher = giaTri;
            giaTriVoucher = soTien;
            thongBaoThayDoi();
            return null;
        } catch (IllegalArgumentException loi) {
            return loi.getMessage();
        }
    }

    public BienLai hoanTat(PhuongThuc phuongThuc, long daNhan, String thuNgan) {
        bienLaiCuoi = chinhSach.thanhToan(this, phuongThuc, daNhan, thuNgan);
        quyTrinh.ketThuc(kieuNguon);
        return bienLaiCuoi;
    }

    public BienLai hoanTat(List<DongThuTien> cacKhoanThu, String thuNgan) {
        bienLaiCuoi = chinhSach.thanhToan(this, cacKhoanThu, thuNgan);
        quyTrinh.ketThuc(kieuNguon);
        return bienLaiCuoi;
    }

    public void xoa() {
        khoaThanhToan =
                "PAY-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        maHoaDonDuKien = com.petstation.util.DuLieu.taoMa("HD");
        kieuNguon = "";
        idNguon = 0;
        doiHang = null;
        xacNhanNhanTien = "";
        cacDong.clear();
        khachHangSoDienThoai = "";
        giamGiaPhanTram = 0;
        maVoucher = "";
        giaTriVoucher = 0;
        maKhachHang = "";
        tenKhachHang = "";
        khachHangDiem = 0;
        diemDaDung = 0;
        thongBaoThayDoi();
    }
}
