package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietHoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    private HoaDon hoaDon;
    private SanPham sanPham;
    private LoHang loHang;
    private int soLuong;
    private long donGia;
    private long giamGia;
    private double thueGtgt;
    private String maDichVu;
    private String tenHang;

    public ChiTietHoaDon() {
        this.soLuong = 1;
    }

    public ChiTietHoaDon(String maChiTietHoaDon) {
        this();
    }

    public ChiTietHoaDon(
            HoaDon hoaDon,
            SanPham sanPham,
            LoHang loHang,
            int soLuong,
            long donGia,
            long giamGia,
            double thueGtgt,
            String maDichVu,
            String tenHang) {
        this();
        setHoaDon(hoaDon);
        setSanPham(sanPham);
        setLoHang(loHang);
        setSoLuong(soLuong);
        setDonGia(donGia);
        setGiamGia(giamGia);
        setThueGtgt(thueGtgt);
        setMaDichVu(maDichVu);
        setTenHang(tenHang);
    }

    public ChiTietHoaDon(
            String maChiTietHoaDon,
            HoaDon hoaDon,
            SanPham sanPham,
            LoHang loHang,
            int soLuong,
            long donGia,
            long giamGia,
            double thueGtgt,
            String maDichVu,
            String tenHang) {
        this(hoaDon, sanPham, loHang, soLuong, donGia, giamGia, thueGtgt, maDichVu, tenHang);
    }

    public long tinhThanhTienTruocGiam() {
        return (long) Math.max(0, soLuong) * Math.max(0L, donGia);
    }

    public long getThanhTienTruocGiam() {
        return tinhThanhTienTruocGiam();
    }

    public long tinhTienThueGtgt() {
        long sauGiam = Math.max(0L, tinhThanhTienTruocGiam() - giamGia);
        if (thueGtgt <= 0) {
            return 0L;
        }
        return Math.round(sauGiam * thueGtgt / 100.0);
    }

    public long getTienThueGtgt() {
        return tinhTienThueGtgt();
    }

    public long tinhThanhTien() {
        return Math.max(0L, tinhThanhTienTruocGiam() - giamGia) + tinhTienThueGtgt();
    }

    public long getThanhTien() {
        return tinhThanhTien();
    }

    public String getMaChiTietHoaDon() {
        String hd = (hoaDon != null && hoaDon.getMaHoaDon() != null) ? hoaDon.getMaHoaDon() : "";
        String sp = (sanPham != null && sanPham.getMaSanPham() != null) ? sanPham.getMaSanPham() : (maDichVu != null ? maDichVu : "");
        String lo = (loHang != null && loHang.getMaLo() != null) ? loHang.getMaLo() : "";
        return hd + "-" + sp + (lo.isEmpty() ? "" : "-" + lo);
    }

    public void setMaChiTietHoaDon(String maChiTietHoaDon) {
    }

    public HoaDon getHoaDon() {
        return this.hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public String getMaHoaDon() {
        return hoaDon == null ? null : hoaDon.getMaHoaDon();
    }

    public void setMaHoaDon(String maHoaDon) {
        this.hoaDon = (maHoaDon == null || maHoaDon.isBlank()) ? null : new HoaDon(maHoaDon);
    }

    public SanPham getSanPham() {
        return this.sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public String getMaSanPham() {
        return sanPham == null ? null : sanPham.getMaSanPham();
    }

    public void setMaSanPham(String maSanPham) {
        this.sanPham = (maSanPham == null || maSanPham.isBlank()) ? null : new SanPham(maSanPham);
    }

    public LoHang getLoHang() {
        return this.loHang;
    }

    public void setLoHang(LoHang loHang) {
        this.loHang = loHang;
    }

    public String getMaLo() {
        return loHang == null ? null : loHang.getMaLo();
    }

    public void setMaLo(String maLo) {
        this.loHang = (maLo == null || maLo.isBlank()) ? null : new LoHang(maLo);
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
        if (donGia < 0)
            throw new IllegalArgumentException("Đơn giá không được âm.");
        this.donGia = donGia;
    }

    public long getGiamGia() {
        return this.giamGia;
    }

    public void setGiamGia(long giamGia) {
        if (giamGia < 0)
            throw new IllegalArgumentException("Giảm giá dòng hàng không được âm.");
        this.giamGia = giamGia;
    }

    public double getThueGtgt() {
        return this.thueGtgt;
    }

    public void setThueGtgt(double thueGtgt) {
        if (thueGtgt < 0)
            throw new IllegalArgumentException("Thuế GTGT không được âm.");
        this.thueGtgt = thueGtgt;
    }

    public String getMaDichVu() {
        return this.maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getTenHang() {
        return this.tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
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
        return "ChiTietHoaDon{" + "hoaDon=" + (hoaDon != null ? hoaDon.getMaHoaDon() : null)
                + ", sanPham=" + (sanPham != null ? sanPham.getMaSanPham() : null)
                + ", loHang=" + (loHang != null ? loHang.getMaLo() : null)
                + ", soLuong=" + soLuong + ", donGia=" + donGia + ", thanhTien=" + tinhThanhTien() + '}';
    }
}
