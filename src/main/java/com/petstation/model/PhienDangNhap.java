package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class PhienDangNhap implements Serializable {
    private static final long serialVersionUID = 1L;

    private TaiKhoan taiKhoan;
    private NhanVien nhanVien;
    private java.util.Set<Quyen> cacQuyen;

    public PhienDangNhap() {}

    public PhienDangNhap(TaiKhoan taiKhoan, NhanVien nhanVien, java.util.Set<Quyen> cacQuyen) {
        this.taiKhoan = taiKhoan;
        this.nhanVien = nhanVien;
        this.cacQuyen = cacQuyen;
    }

    public TaiKhoan getTaiKhoan() { return this.taiKhoan; }
    public void setTaiKhoan(TaiKhoan taiKhoan) { this.taiKhoan = taiKhoan; }

    public NhanVien getNhanVien() { return this.nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public java.util.Set<Quyen> getCacQuyen() { return this.cacQuyen; }
    public void setCacQuyen(java.util.Set<Quyen> cacQuyen) { this.cacQuyen = cacQuyen; }

    public String getTenHienThi() {
        return nhanVien != null ? nhanVien.getHoTen() : "";
    }

    public VaiTro docVaiTro() {
        return taiKhoan != null ? taiKhoan.getVaiTro() : null;
    }

    public boolean kiemTraCo(Quyen quyen) {
        return cacQuyen != null && cacQuyen.contains(quyen);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhienDangNhap that = (PhienDangNhap) o;
        return Objects.equals(taiKhoan, that.taiKhoan)
                && Objects.equals(nhanVien, that.nhanVien)
                && Objects.equals(cacQuyen, that.cacQuyen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taiKhoan, nhanVien, cacQuyen);
    }

    @Override
    public String toString() {
        return "PhienDangNhap{" + "taiKhoan=" + taiKhoan + ", " + "nhanVien=" + nhanVien + ", " + "cacQuyen=" + cacQuyen + "}";
    }
}
