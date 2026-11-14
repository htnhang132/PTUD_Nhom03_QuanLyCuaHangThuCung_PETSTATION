package com.petstation.dto;

import com.petstation.model.chung.PhuongThuc;
import com.petstation.util.DinhDang;
import com.petstation.util.DuLieu;

import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class GioHang {
    public static final class MucKhuyenMai implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String ma;
        private final String ten;
        private final String loai;
        private final String phamVi;
        private final long giaTri;
        private final long donToiThieu;
        private final String tuNgay;
        private final String denNgay;
        private final String trangThai;

        public MucKhuyenMai(
                String ma,
                String ten,
                String loai,
                String phamVi,
                long giaTri,
                long donToiThieu,
                String tuNgay,
                String denNgay,
                String trangThai) {
            this.ma = ma == null ? "" : ma;
            this.ten = ten == null ? "" : ten;
            this.loai = loai == null ? "" : loai;
            this.phamVi = phamVi == null ? "" : phamVi;
            this.giaTri = giaTri;
            this.donToiThieu = donToiThieu;
            this.tuNgay = tuNgay == null ? "" : tuNgay;
            this.denNgay = denNgay == null ? "" : denNgay;
            this.trangThai = trangThai == null ? "" : trangThai;
        }

        public String getMa() {
            return ma;
        }

        public String getTen() {
            return ten;
        }

        public String getLoai() {
            return loai;
        }

        public String getPhamVi() {
            return phamVi;
        }

        public long getGiaTri() {
            return giaTri;
        }

        public long getDonToiThieu() {
            return donToiThieu;
        }

        public String getTuNgay() {
            return tuNgay;
        }

        public String getDenNgay() {
            return denNgay;
        }

        public String getTrangThai() {
            return trangThai;
        }
    }

    public interface ChinhSach {
        List<MucKhuyenMai> layKhuyenMai();

        long kiemTraVoucher(String ma, String khachHang, String nguon, long tamTinh);

        BienLai thanhToan(GioHang gio, PhuongThuc phuongThuc, long daNhan, String thuNgan);
    }

    private final ChinhSach chinhSach;

    public GioHang(ChinhSach chinhSach) {
        this.chinhSach = Objects.requireNonNull(chinhSach);
    }

    public static class DongHang implements Serializable {
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

        public String getMa() {
            return ma;
        }

        public String getTen() {
            return ten;
        }

        public long getDonGia() {
            return donGia;
        }

        public int getSoLuong() {
            return soLuong;
        }

        public long docSoTien() {
            return donGia * soLuong;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DongHang dongHang = (DongHang) o;
            return donGia == dongHang.donGia
                    && soLuong == dongHang.soLuong
                    && Objects.equals(ma, dongHang.ma)
                    && Objects.equals(ten, dongHang.ten);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ma, ten, donGia, soLuong);
        }

        @Override
        public String toString() {
            return "DongHang{"
                    + "ma='"
                    + ma
                    + '\''
                    + ", ten='"
                    + ten
                    + '\''
                    + ", donGia="
                    + donGia
                    + ", soLuong="
                    + soLuong
                    + '}';
        }
    }

    public static class BienLai implements Serializable {
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
        private int vatPhanTram;

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
                String thuNgan) {
            this(
                    soHoaDon,
                    thoiGian,
                    cacDong,
                    tamTinh,
                    giamGia,
                    tong,
                    phuongThuc,
                    daNhan,
                    thayDoi,
                    khachHang,
                    voucher,
                    thuNgan,
                    0);
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
                String thuNgan,
                int vatPhanTram) {
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
            this.vatPhanTram = Math.max(0, vatPhanTram);
        }

        public String getSoHoaDon() {
            return soHoaDon;
        }

        public LocalDateTime getThoiGian() {
            return thoiGian;
        }

        public List<DongHang> getCacDong() {
            return cacDong;
        }

        public long getTamTinh() {
            return tamTinh;
        }

        public long getGiamGia() {
            return giamGia;
        }

        public long getTong() {
            return tong;
        }

        public PhuongThuc getPhuongThuc() {
            return phuongThuc;
        }

        public long getDaNhan() {
            return daNhan;
        }

        public long getThayDoi() {
            return thayDoi;
        }

        public String getKhachHang() {
            return khachHang;
        }

        public String getVoucher() {
            return voucher;
        }

        public String getThuNgan() {
            return thuNgan;
        }

        public int getVatPhanTram() {
            return vatPhanTram;
        }

        public long tinhTienThueGtgt() {
            return GioHang.tinhThueBaoGom(tong, vatPhanTram);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BienLai bienLai = (BienLai) o;
            return tamTinh == bienLai.tamTinh
                    && giamGia == bienLai.giamGia
                    && tong == bienLai.tong
                    && daNhan == bienLai.daNhan
                    && thayDoi == bienLai.thayDoi
                    && vatPhanTram == bienLai.vatPhanTram
                    && Objects.equals(soHoaDon, bienLai.soHoaDon)
                    && Objects.equals(thoiGian, bienLai.thoiGian)
                    && Objects.equals(cacDong, bienLai.cacDong)
                    && phuongThuc == bienLai.phuongThuc
                    && Objects.equals(khachHang, bienLai.khachHang)
                    && Objects.equals(voucher, bienLai.voucher)
                    && Objects.equals(thuNgan, bienLai.thuNgan);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    soHoaDon,
                    thoiGian,
                    cacDong,
                    tamTinh,
                    giamGia,
                    tong,
                    phuongThuc,
                    daNhan,
                    thayDoi,
                    khachHang,
                    voucher,
                    thuNgan,
                    vatPhanTram);
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
    private final List<Consumer<GioHang>> cacBoLangNghe = new CopyOnWriteArrayList<>();
    private volatile boolean dangGhiThanhToan;

    public void themBoLangNghe(Consumer<GioHang> boLangNghe) {
        if (boLangNghe != null && !cacBoLangNghe.contains(boLangNghe)) {
            cacBoLangNghe.add(boLangNghe);
        }
    }

    public void xoaBoLangNghe(Consumer<GioHang> boLangNghe) {
        cacBoLangNghe.remove(boLangNghe);
    }

    public void thongBaoThayDoi() {
        for (Consumer<GioHang> boLangNghe : cacBoLangNghe) {
            try {
                boLangNghe.accept(this);
            } catch (RuntimeException ex) {
                LoggerFactory.getLogger(GioHang.class)
                        .warn("Lỗi cập nhật bộ lắng nghe giỏ hàng.", ex);
            }
        }
    }

    public boolean isDangGhiThanhToan() {
        return dangGhiThanhToan;
    }

    private int giamGiaPhanTram;

    private String maVoucher = "";

    private String maKhachHang = "";

    private String tenKhachHang = "";

    private long khachHangDiem;

    private long diemDaDung;

    private long giaTriVoucher;

    private BienLai bienLaiCuoi;

    private String khoaThanhToan =
            "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);

    private String maHoaDonDuKien;

    private String kieuNguon = "";

    private String maNguon = "";

    public static class YeuCauDoiHang implements Serializable {
        private static final long serialVersionUID = 1L;
        private String dongGoc;
        private int soLuong;
        private String lyDo;
        private long giaTriDuocDoi;

        public YeuCauDoiHang() {}

        public YeuCauDoiHang(String dongGoc, int soLuong, String lyDo, long giaTriDuocDoi) {
            this.dongGoc = dongGoc == null ? "" : dongGoc;
            this.soLuong = soLuong;
            this.lyDo = lyDo;
            this.giaTriDuocDoi = giaTriDuocDoi;
        }

        public String getDongGoc() {
            return dongGoc;
        }

        public int getSoLuong() {
            return soLuong;
        }

        public String getLyDo() {
            return lyDo;
        }

        public long getGiaTriDuocDoi() {
            return giaTriDuocDoi;
        }
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

    public int getGiamGiaPhanTram() {
        return giamGiaPhanTram;
    }

    public String getMaVoucher() {
        return maVoucher;
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

    // Cấp mã một lần cho giỏ hiện tại: nội dung QR chuyển khoản và hóa đơn ghi sổ
    // dùng chung mã.
    public String getMaHoaDonDuKien() {
        if (maHoaDonDuKien == null) maHoaDonDuKien = DuLieu.taoMa("HD");
        return maHoaDonDuKien;
    }

    public String getKieuNguon() {
        return kieuNguon;
    }

    public String getMaNguon() {
        return maNguon;
    }

    public void setNguon(String kieu, String maNguon) {
        this.kieuNguon = kieu;
        this.maNguon = maNguon == null ? "" : maNguon;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public long getKhachHangDiem() {
        return khachHangDiem;
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
        for (MucKhuyenMai muc : chinhSach.layKhuyenMai()) {
            String tt = muc.getTrangThai();
            if ("Ngừng áp dụng".equalsIgnoreCase(tt)
                    || "NGUNG_HOAT_DONG".equalsIgnoreCase(tt)
                    || !kiemTraTrongKhoangNgay(muc.getTuNgay(), muc.getDenNgay())) continue;
            if (tamTinh < muc.getDonToiThieu()) continue;
            long coSo = 0;
            String phamVi = muc.getPhamVi();
            for (DongHang dongDangXet : cacDong) {
                boolean laDichVu =
                        "SPA".equals(kieuNguon)
                                || "HOTEL".equals(kieuNguon)
                                || "LUU_TRU".equals(kieuNguon);
                if ("Cả đơn".equals(phamVi)
                        || "ALL".equalsIgnoreCase(phamVi)
                        || (("Sản phẩm".equals(phamVi) || "PRODUCT".equalsIgnoreCase(phamVi))
                                && !laDichVu)
                        || (("Dịch vụ".equals(phamVi) || "SERVICE".equalsIgnoreCase(phamVi))
                                && laDichVu)) coSo += dongDangXet.docSoTien();
            }
            long giaTri = muc.getGiaTri();
            String loai = muc.getLoai() == null ? "" : muc.getLoai();
            long soTien =
                    switch (loai) {
                        case "Giảm %", "PHAN_TRAM" -> tinhPhanTram(coSo, giaTri);
                        case "Giảm tiền", "SO_TIEN" -> coSo > 0 ? Math.min(giaTri, coSo) : 0;
                        default -> 0;
                    };
            if (soTien > 0 && (totNhat == null || soTien > totNhat.getSoTien()))
                totNhat = new KhuyenMai(muc.getMa(), muc.getTen(), soTien);
        }
        return totNhat;
    }

    private static boolean kiemTraTrongKhoangNgay(String tu, String den) {
        LocalDate homNay = LocalDate.now();
        try {
            LocalDate ngayTu = parseNgayKhuyenMai(tu);
            LocalDate ngayDen = parseNgayKhuyenMai(den);
            if (ngayTu.isAfter(homNay)) return false;
            return !ngayDen.isBefore(homNay);
        } catch (DateTimeParseException loi) {
            return false;
        }
    }

    private static LocalDate parseNgayKhuyenMai(String giaTri) {
        String text = String.valueOf(giaTri).trim();
        try {
            return LocalDate.parse(text, DinhDang.NGAY_CHUAN);
        } catch (DateTimeParseException ex) {
            return LocalDate.parse(text, DinhDang.NGAY_ISO);
        }
    }

    public static class KhuyenMai implements Serializable {
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

        public String getMa() {
            return ma;
        }

        public String getTen() {
            return ten;
        }

        public long getSoTien() {
            return soTien;
        }
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
        long theoPhanTram = tinhPhanTram(tinhTamTinh(), giamGiaPhanTram);
        return Math.min(
                tinhTamTinh(),
                theoPhanTram + getKhuyenMaiGiamGia() + getSoTienVoucher() + getGiaTriDiem());
    }

    public long tinhTong() {
        return tinhTamTinh() - tinhGiamGia();
    }

    public static long tinhPhanTram(long coSo, long phanTram) {
        if (coSo <= 0 || phanTram <= 0) return 0L;
        return BigDecimal.valueOf(coSo)
                .multiply(BigDecimal.valueOf(phanTram))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public static long tinhThueBaoGom(long tongSauGiam, int vatPhanTram) {
        if (tongSauGiam <= 0 || vatPhanTram <= 0) return 0L;
        return BigDecimal.valueOf(tongSauGiam)
                .multiply(BigDecimal.valueOf(vatPhanTram))
                .divide(BigDecimal.valueOf(100L + vatPhanTram), 0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public long tinhTienThueGtgtBaoGom(int vatPhanTram) {
        return tinhThueBaoGom(tinhTong(), vatPhanTram);
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

    public void xoa() {
        khoaThanhToan = "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        maHoaDonDuKien = null;
        kieuNguon = "";
        maNguon = "";
        doiHang = null;
        xacNhanNhanTien = "";
        cacDong.clear();
        giamGiaPhanTram = 0;
        maVoucher = "";
        giaTriVoucher = 0;
        maKhachHang = "";
        tenKhachHang = "";
        khachHangDiem = 0;
        diemDaDung = 0;
        thongBaoThayDoi();
    }

    public static final class NguCanhQuyTrinh {
        private String lichHen = "";
        private String phieuSpa = "";
        private String hopDongHotel = "";
        private String maPhongHotel = "";

        public String getLichHen() {
            return lichHen;
        }

        public String getPhieuSpa() {
            return phieuSpa;
        }

        public String getHopDongHotel() {
            return hopDongHotel;
        }

        public String getMaPhongHotel() {
            return maPhongHotel;
        }

        public void chonPhongHotel(String ma) {
            maPhongHotel = ma == null ? "" : ma;
        }

        public void chonLichHen(String ma) {
            String giaTri = ma == null ? "" : ma;
            if (!Objects.equals(lichHen, giaTri)) phieuSpa = "";
            lichHen = giaTri;
        }

        public void chonSpa(String ma) {
            String giaTri = ma == null ? "" : ma;
            if (!Objects.equals(phieuSpa, giaTri)) lichHen = "";
            phieuSpa = giaTri;
        }

        public void chonHotel(String ma) {
            hopDongHotel = ma == null ? "" : ma;
        }

        public void ketThuc(String nguon) {
            if ("SPA".equals(nguon)) {
                lichHen = "";
                phieuSpa = "";
            }
            if ("HOTEL".equals(nguon) || "LUU_TRU".equals(nguon)) {
                hopDongHotel = "";
                maPhongHotel = "";
            }
        }

        public void xoa() {
            lichHen = "";
            phieuSpa = "";
            hopDongHotel = "";
            maPhongHotel = "";
        }
    }
}
