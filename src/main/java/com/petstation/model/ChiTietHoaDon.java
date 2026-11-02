package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Thực thể chi tiết hóa đơn khớp bảng [chi_tiet_hoa_don].
 */
public class ChiTietHoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long hoaDonId;
    private Long sanPhamId;
    private Long loHangId;
    private int soLuong;
    private long donGia;
    private long giamGia;
    private double thueGtgt;
    private String maDichVu;
    private String tenHang;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(long id, long hoaDonId, Long sanPhamId, Long loHangId, int soLuong, long donGia, long giamGia, double thueGtgt, String maDichVu, String tenHang) {
        this.id = id;
        this.hoaDonId = hoaDonId;
        this.sanPhamId = sanPhamId;
        this.loHangId = loHangId;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.giamGia = giamGia;
        this.thueGtgt = thueGtgt;
        this.maDichVu = maDichVu;
        this.tenHang = tenHang;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public long getHoaDonId() { return this.hoaDonId; }
    public long layHoaDonId() { return this.hoaDonId; }
    public long hoaDonId() { return this.hoaDonId; }
    public void setHoaDonId(long hoaDonId) { this.hoaDonId = hoaDonId; }
    public void datHoaDonId(long hoaDonId) { this.hoaDonId = hoaDonId; }

    public Long getSanPhamId() { return this.sanPhamId; }
    public Long laySanPhamId() { return this.sanPhamId; }
    public Long sanPhamId() { return this.sanPhamId; }
    public void setSanPhamId(Long sanPhamId) { this.sanPhamId = sanPhamId; }
    public void datSanPhamId(Long sanPhamId) { this.sanPhamId = sanPhamId; }

    public Long getLoHangId() { return this.loHangId; }
    public Long layLoHangId() { return this.loHangId; }
    public Long loHangId() { return this.loHangId; }
    public void setLoHangId(Long loHangId) { this.loHangId = loHangId; }
    public void datLoHangId(Long loHangId) { this.loHangId = loHangId; }

    public int getSoLuong() { return this.soLuong; }
    public int laySoLuong() { return this.soLuong; }
    public int soLuong() { return this.soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void datSoLuong(int soLuong) { this.soLuong = soLuong; }

    public long getDonGia() { return this.donGia; }
    public long layDonGia() { return this.donGia; }
    public long donGia() { return this.donGia; }
    public void setDonGia(long donGia) { this.donGia = donGia; }
    public void datDonGia(long donGia) { this.donGia = donGia; }

    public long getGiamGia() { return this.giamGia; }
    public long layGiamGia() { return this.giamGia; }
    public long giamGia() { return this.giamGia; }
    public void setGiamGia(long giamGia) { this.giamGia = giamGia; }
    public void datGiamGia(long giamGia) { this.giamGia = giamGia; }

    public double getThueGtgt() { return this.thueGtgt; }
    public double layThueGtgt() { return this.thueGtgt; }
    public double thueGtgt() { return this.thueGtgt; }
    public void setThueGtgt(double thueGtgt) { this.thueGtgt = thueGtgt; }
    public void datThueGtgt(double thueGtgt) { this.thueGtgt = thueGtgt; }

    public String getMaDichVu() { return this.maDichVu; }
    public String layMaDichVu() { return this.maDichVu; }
    public String maDichVu() { return this.maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }
    public void datMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }

    public String getTenHang() { return this.tenHang; }
    public String layTenHang() { return this.tenHang; }
    public String tenHang() { return this.tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }
    public void datTenHang(String tenHang) { this.tenHang = tenHang; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return this.id == that.id
                && this.hoaDonId == that.hoaDonId
                && Objects.equals(sanPhamId, that.sanPhamId)
                && Objects.equals(loHangId, that.loHangId)
                && this.soLuong == that.soLuong
                && this.donGia == that.donGia
                && this.giamGia == that.giamGia
                && this.thueGtgt == that.thueGtgt
                && Objects.equals(maDichVu, that.maDichVu)
                && Objects.equals(tenHang, that.tenHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hoaDonId, sanPhamId, loHangId, soLuong, donGia, giamGia, thueGtgt, maDichVu, tenHang);
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" + "id=" + id + ", " + "hoaDonId=" + hoaDonId + ", " + "sanPhamId=" + sanPhamId + ", " + "loHangId=" + loHangId + ", " + "soLuong=" + soLuong + ", " + "donGia=" + donGia + ", " + "giamGia=" + giamGia + ", " + "thueGtgt=" + thueGtgt + ", " + "maDichVu=" + maDichVu + ", " + "tenHang=" + tenHang + "}";
    }
}
