package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Thực thể chi tiết phiếu nhập kho khớp bảng [chi_tiet_phieu_nhap].
 */
public class ChiTietPhieuNhap implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long phieuNhapId;
    private long sanPhamId;
    private String soLo;
    private LocalDate hanSuDung;
    private int soLuong;
    private long giaNhap;

    public ChiTietPhieuNhap() {}

    public ChiTietPhieuNhap(long id, long phieuNhapId, long sanPhamId, String soLo, LocalDate hanSuDung, int soLuong, long giaNhap) {
        this.id = id;
        this.phieuNhapId = phieuNhapId;
        this.sanPhamId = sanPhamId;
        this.soLo = soLo;
        this.hanSuDung = hanSuDung;
        this.soLuong = soLuong;
        this.giaNhap = giaNhap;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public long getPhieuNhapId() { return this.phieuNhapId; }
    public long layPhieuNhapId() { return this.phieuNhapId; }
    public long phieuNhapId() { return this.phieuNhapId; }
    public void setPhieuNhapId(long phieuNhapId) { this.phieuNhapId = phieuNhapId; }
    public void datPhieuNhapId(long phieuNhapId) { this.phieuNhapId = phieuNhapId; }

    public long getSanPhamId() { return this.sanPhamId; }
    public long laySanPhamId() { return this.sanPhamId; }
    public long sanPhamId() { return this.sanPhamId; }
    public void setSanPhamId(long sanPhamId) { this.sanPhamId = sanPhamId; }
    public void datSanPhamId(long sanPhamId) { this.sanPhamId = sanPhamId; }

    public String getSoLo() { return this.soLo; }
    public String laySoLo() { return this.soLo; }
    public String soLo() { return this.soLo; }
    public void setSoLo(String soLo) { this.soLo = soLo; }
    public void datSoLo(String soLo) { this.soLo = soLo; }

    public LocalDate getHanSuDung() { return this.hanSuDung; }
    public LocalDate layHanSuDung() { return this.hanSuDung; }
    public LocalDate hanSuDung() { return this.hanSuDung; }
    public void setHanSuDung(LocalDate hanSuDung) { this.hanSuDung = hanSuDung; }
    public void datHanSuDung(LocalDate hanSuDung) { this.hanSuDung = hanSuDung; }

    public int getSoLuong() { return this.soLuong; }
    public int laySoLuong() { return this.soLuong; }
    public int soLuong() { return this.soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void datSoLuong(int soLuong) { this.soLuong = soLuong; }

    public long getGiaNhap() { return this.giaNhap; }
    public long layGiaNhap() { return this.giaNhap; }
    public long giaNhap() { return this.giaNhap; }
    public void setGiaNhap(long giaNhap) { this.giaNhap = giaNhap; }
    public void datGiaNhap(long giaNhap) { this.giaNhap = giaNhap; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuNhap that = (ChiTietPhieuNhap) o;
        return this.id == that.id
                && this.phieuNhapId == that.phieuNhapId
                && this.sanPhamId == that.sanPhamId
                && Objects.equals(soLo, that.soLo)
                && Objects.equals(hanSuDung, that.hanSuDung)
                && this.soLuong == that.soLuong
                && this.giaNhap == that.giaNhap;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phieuNhapId, sanPhamId, soLo, hanSuDung, soLuong, giaNhap);
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{" + "id=" + id + ", " + "phieuNhapId=" + phieuNhapId + ", " + "sanPhamId=" + sanPhamId + ", " + "soLo=" + soLo + ", " + "hanSuDung=" + hanSuDung + ", " + "soLuong=" + soLuong + ", " + "giaNhap=" + giaNhap + "}";
    }
}
