package com.petstation.model.kho;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietPhieuXuat implements Serializable {

    private static final long serialVersionUID = 1L;
    private PhieuXuatKho phieuXuat;
    private LoHang loHang;
    private int soLuong;
    private long giaTri;

    public ChiTietPhieuXuat() {
        this.soLuong = 1;
    }

    public ChiTietPhieuXuat(PhieuXuatKho phieuXuat, LoHang loHang, int soLuong, long giaTri) {
        setPhieuXuat(phieuXuat);
        setLoHang(loHang);
        setSoLuong(soLuong);
        this.giaTri = Math.max(0L, giaTri);
    }

    public PhieuXuatKho getPhieuXuat() {
        return this.phieuXuat;
    }

    public void setPhieuXuat(PhieuXuatKho phieuXuat) {
        this.phieuXuat = phieuXuat;
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
        if (soLuong < 0) throw new IllegalArgumentException("Số lượng xuất kho không được âm.");
        this.soLuong = soLuong;
    }

    public long getGiaTri() {
        return this.giaTri;
    }

    public long tinhGiaTriXuat() {
        if (loHang != null && loHang.getGiaNhap() > 0 && soLuong > 0) {
            return (long) soLuong * loHang.getGiaNhap();
        }
        return Math.max(0L, this.giaTri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
        return "ChiTietPhieuXuat{"
                + "phieuXuat="
                + (phieuXuat != null ? phieuXuat.getMaPhieuXuat() : null)
                + ", loHang="
                + (loHang != null ? loHang.getMaLo() : null)
                + ", soLuong="
                + soLuong
                + ", giaTri="
                + tinhGiaTriXuat()
                + '}';
    }
}
