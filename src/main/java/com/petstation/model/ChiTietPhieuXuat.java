package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Thực thể chi tiết phiếu xuất kho khớp bảng [chi_tiet_phieu_xuat].
 */
public class ChiTietPhieuXuat implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long phieuXuatId;
    private long loHangId;
    private int soLuong;
    private long giaTri;

    public ChiTietPhieuXuat() {}

    public ChiTietPhieuXuat(long id, long phieuXuatId, long loHangId, int soLuong, long giaTri) {
        this.id = id;
        this.phieuXuatId = phieuXuatId;
        this.loHangId = loHangId;
        this.soLuong = soLuong;
        this.giaTri = giaTri;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public long getPhieuXuatId() { return this.phieuXuatId; }
    public long layPhieuXuatId() { return this.phieuXuatId; }
    public long phieuXuatId() { return this.phieuXuatId; }
    public void setPhieuXuatId(long phieuXuatId) { this.phieuXuatId = phieuXuatId; }
    public void datPhieuXuatId(long phieuXuatId) { this.phieuXuatId = phieuXuatId; }

    public long getLoHangId() { return this.loHangId; }
    public long layLoHangId() { return this.loHangId; }
    public long loHangId() { return this.loHangId; }
    public void setLoHangId(long loHangId) { this.loHangId = loHangId; }
    public void datLoHangId(long loHangId) { this.loHangId = loHangId; }

    public int getSoLuong() { return this.soLuong; }
    public int laySoLuong() { return this.soLuong; }
    public int soLuong() { return this.soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void datSoLuong(int soLuong) { this.soLuong = soLuong; }

    public long getGiaTri() { return this.giaTri; }
    public long layGiaTri() { return this.giaTri; }
    public long giaTri() { return this.giaTri; }
    public void setGiaTri(long giaTri) { this.giaTri = giaTri; }
    public void datGiaTri(long giaTri) { this.giaTri = giaTri; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuXuat that = (ChiTietPhieuXuat) o;
        return this.id == that.id
                && this.phieuXuatId == that.phieuXuatId
                && this.loHangId == that.loHangId
                && this.soLuong == that.soLuong
                && this.giaTri == that.giaTri;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phieuXuatId, loHangId, soLuong, giaTri);
    }

    @Override
    public String toString() {
        return "ChiTietPhieuXuat{" + "id=" + id + ", " + "phieuXuatId=" + phieuXuatId + ", " + "loHangId=" + loHangId + ", " + "soLuong=" + soLuong + ", " + "giaTri=" + giaTri + "}";
    }
}
