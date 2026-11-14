package com.petstation.model.banhang;

import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.spa.PhieuTiepNhanSpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class ChiTietHoaDon implements Serializable {

    private static final long serialVersionUID = 1L;
    private HoaDon hoaDon;
    private SanPham sanPham;
    private LoHang loHang;
    private PhieuTiepNhanSpa phieuSpa;
    private HopDongLuuTru hopDongLuuTru;
    private String tenHang;
    private int soLuong;
    private long donGia;
    private long giamGia;
    private double thueGtgt;

    public ChiTietHoaDon() {
        this.soLuong = 1;
    }

    public ChiTietHoaDon(
            HoaDon hoaDon,
            SanPham sanPham,
            LoHang loHang,
            PhieuTiepNhanSpa phieuSpa,
            HopDongLuuTru hopDongLuuTru,
            String tenHang,
            int soLuong,
            long donGia,
            long giamGia,
            double thueGtgt) {
        this();
        setHoaDon(hoaDon);
        setSanPham(sanPham);
        setLoHang(loHang);
        setPhieuSpa(phieuSpa);
        setHopDongLuuTru(hopDongLuuTru);
        setTenHang(tenHang);
        setSoLuong(soLuong);
        setDonGia(donGia);
        setGiamGia(giamGia);
        setThueGtgt(thueGtgt);
    }

    public HoaDon getHoaDon() {
        return this.hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public SanPham getSanPham() {
        return this.sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public LoHang getLoHang() {
        return this.loHang;
    }

    public void setLoHang(LoHang loHang) {
        this.loHang = loHang;
    }

    public PhieuTiepNhanSpa getPhieuSpa() {
        return this.phieuSpa;
    }

    public void setPhieuSpa(PhieuTiepNhanSpa phieuSpa) {
        this.phieuSpa = phieuSpa;
    }

    public HopDongLuuTru getHopDongLuuTru() {
        return this.hopDongLuuTru;
    }

    public void setHopDongLuuTru(HopDongLuuTru hopDongLuuTru) {
        this.hopDongLuuTru = hopDongLuuTru;
    }

    public String getTenHang() {
        return this.tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0)
            throw new IllegalArgumentException("Số lượng chi tiết hóa đơn không được âm.");
        this.soLuong = soLuong;
    }

    public long getDonGia() {
        return this.donGia;
    }

    public void setDonGia(long donGia) {
        if (donGia < 0) throw new IllegalArgumentException("Đơn giá không được âm.");
        this.donGia = donGia;
    }

    public long getGiamGia() {
        return this.giamGia;
    }

    public void setGiamGia(long giamGia) {
        if (giamGia < 0) throw new IllegalArgumentException("Giảm giá dòng hàng không được âm.");
        this.giamGia = giamGia;
    }

    public double getThueGtgt() {
        return this.thueGtgt;
    }

    public void setThueGtgt(double thueGtgt) {
        if (thueGtgt < 0) throw new IllegalArgumentException("Thuế GTGT không được âm.");
        this.thueGtgt = thueGtgt;
    }

    public long tinhThanhTienTruocGiam() {
        return (long) Math.max(0, soLuong) * Math.max(0L, donGia);
    }

    public long tinhTienThueGtgt() {
        long sauGiam = tinhThanhTien();
        if (sauGiam <= 0 || thueGtgt <= 0) {
            return 0L;
        }
        BigDecimal vat = BigDecimal.valueOf(thueGtgt);
        return BigDecimal.valueOf(sauGiam)
                .multiply(vat)
                .divide(BigDecimal.valueOf(100).add(vat), 0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public long tinhThanhTien() {
        return Math.max(0L, tinhThanhTienTruocGiam() - giamGia);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(this.hoaDon, that.hoaDon)
                && Objects.equals(this.sanPham, that.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, sanPham);
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{"
                + "hoaDon="
                + (hoaDon != null ? hoaDon.getMaHoaDon() : null)
                + ", sanPham="
                + (sanPham != null ? sanPham.getMaSanPham() : null)
                + ", loHang="
                + (loHang != null ? loHang.getMaLo() : null)
                + ", soLuong="
                + soLuong
                + ", donGia="
                + donGia
                + ", thanhTien="
                + tinhThanhTien()
                + '}';
    }
}
