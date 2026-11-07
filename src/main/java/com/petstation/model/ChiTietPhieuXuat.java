package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class ChiTietPhieuXuat implements Serializable {
    private static final long serialVersionUID = 1L;

    private PhieuXuatKho phieuXuat;

    private LoHang loHang;
    private int soLuong;
    private long giaTri;

    public ChiTietPhieuXuat() {
        this.soLuong = 1;
    }

    public ChiTietPhieuXuat(long id) {
        this();
        setId(id);
    }

    public ChiTietPhieuXuat(PhieuXuatKho phieuXuat, LoHang loHang, int soLuong, long giaTri) {
        setPhieuXuat(phieuXuat);
        setLoHang(loHang);
        setSoLuong(soLuong);
        this.giaTri = Math.max(0L, giaTri);
    }

    public ChiTietPhieuXuat(long id, PhieuXuatKho phieuXuat, LoHang loHang, int soLuong, long giaTri) {
        this(phieuXuat, loHang, soLuong, giaTri);
        setId(id);
    }

    public ChiTietPhieuXuat(long id, long phieuXuatId, long loHangId, int soLuong, long giaTri) {
        this(id, new PhieuXuatKho(phieuXuatId), new LoHang(loHangId), soLuong, giaTri);
    }

    public long tinhGiaTriXuat() {
        if (loHang != null && loHang.getGiaNhap() > 0 && soLuong > 0) {
            return (long) soLuong * loHang.getGiaNhap();
        }
        return Math.max(0L, this.giaTri);
    }

    public Long getId() {
        String key = (phieuXuat != null ? phieuXuat.getSoPhieu() : "") + "_" + (loHang != null ? loHang.getMaLo() : "");
        return DuLieu.layIdCauNoiObj(this, key);
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
    }

    public PhieuXuatKho getPhieuXuat() {
        return this.phieuXuat;
    }

    public void setPhieuXuat(PhieuXuatKho phieuXuat) {
        this.phieuXuat = phieuXuat;
    }

    public long getPhieuXuatId() {
        return phieuXuat == null ? 0L : phieuXuat.getId();
    }

    public void setPhieuXuatId(long phieuXuatId) {
        this.phieuXuat = new PhieuXuatKho(phieuXuatId);
    }

    public LoHang getLoHang() {
        return this.loHang;
    }

    public void setLoHang(LoHang loHang) {
        this.loHang = loHang;
    }

    public long getLoHangId() {
        return loHang == null ? 0L : loHang.getId();
    }

    public void setLoHangId(long loHangId) {
        this.loHang = new LoHang(loHangId);
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0)
            throw new IllegalArgumentException("Số lượng xuất kho không được âm.");
        this.soLuong = soLuong;
    }

    public long getGiaTri() {
        return tinhGiaTriXuat();
    }

    void napGiaTriSnapshot(long giaTri) {
        this.giaTri = Math.max(0L, giaTri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChiTietPhieuXuat that = (ChiTietPhieuXuat) o;
        return Objects.equals(this.phieuXuat, that.phieuXuat)
                && Objects.equals(this.loHang, that.loHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuXuat, loHang);
    }

    @Override
    public String toString() {
        return "ChiTietPhieuXuat{" + "phieuXuatId=" + getPhieuXuatId() + ", loHangId=" + getLoHangId()
                + ", soLuong=" + soLuong + ", giaTri=" + tinhGiaTriXuat() + '}';
    }
}
