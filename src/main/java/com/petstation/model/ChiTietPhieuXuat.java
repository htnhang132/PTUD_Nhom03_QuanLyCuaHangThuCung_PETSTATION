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

    public long layId() {
        String key = (phieuXuat != null ? phieuXuat.getSoPhieu() : "") + "_" + (loHang != null ? loHang.getMaLo() : "");
        return DuLieu.layIdCauNoi(this, key);
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
    }

    public void datId(long id) {
        setId(id);
    }

    public PhieuXuatKho getPhieuXuat() {
        return this.phieuXuat;
    }

    public PhieuXuatKho layPhieuXuat() {
        return this.phieuXuat;
    }

    public void setPhieuXuat(PhieuXuatKho phieuXuat) {
        this.phieuXuat = phieuXuat;
    }

    public void datPhieuXuat(PhieuXuatKho phieuXuat) {
        setPhieuXuat(phieuXuat);
    }

    public long getPhieuXuatId() {
        return phieuXuat == null ? 0L : phieuXuat.getId();
    }

    public long layPhieuXuatId() {
        return getPhieuXuatId();
    }

    public long phieuXuatId() {
        return getPhieuXuatId();
    }

    public void setPhieuXuatId(long phieuXuatId) {
        this.phieuXuat = new PhieuXuatKho(phieuXuatId);
    }

    public void datPhieuXuatId(long phieuXuatId) {
        setPhieuXuatId(phieuXuatId);
    }

    public LoHang getLoHang() {
        return this.loHang;
    }

    public LoHang layLoHang() {
        return this.loHang;
    }

    public void setLoHang(LoHang loHang) {
        this.loHang = loHang;
    }

    public void datLoHang(LoHang loHang) {
        setLoHang(loHang);
    }

    public long getLoHangId() {
        return loHang == null ? 0L : loHang.getId();
    }

    public long layLoHangId() {
        return getLoHangId();
    }

    public long loHangId() {
        return getLoHangId();
    }

    public void setLoHangId(long loHangId) {
        this.loHang = new LoHang(loHangId);
    }

    public void datLoHangId(long loHangId) {
        setLoHangId(loHangId);
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public int laySoLuong() {
        return this.soLuong;
    }

    public int soLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0)
            throw new IllegalArgumentException("Số lượng xuất kho không được âm.");
        this.soLuong = soLuong;
    }

    public void datSoLuong(int soLuong) {
        setSoLuong(soLuong);
    }

    public long getGiaTri() {
        return tinhGiaTriXuat();
    }

    public long layGiaTri() {
        return tinhGiaTriXuat();
    }

    public long giaTri() {
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
