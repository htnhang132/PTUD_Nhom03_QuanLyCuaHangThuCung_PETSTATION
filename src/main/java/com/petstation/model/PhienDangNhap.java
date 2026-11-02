package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/** Biểu diễn dữ liệu phiên đăng nhập dùng trong nghiệp vụ cửa hàng. */
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
    public TaiKhoan layTaiKhoan() { return this.taiKhoan; }
    public TaiKhoan taiKhoan() { return this.taiKhoan; }
    public void setTaiKhoan(TaiKhoan taiKhoan) { this.taiKhoan = taiKhoan; }
    public void datTaiKhoan(TaiKhoan taiKhoan) { this.taiKhoan = taiKhoan; }

    public NhanVien getNhanVien() { return this.nhanVien; }
    public NhanVien layNhanVien() { return this.nhanVien; }
    public NhanVien nhanVien() { return this.nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public void datNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public java.util.Set<Quyen> getCacQuyen() { return this.cacQuyen; }
    public java.util.Set<Quyen> layCacQuyen() { return this.cacQuyen; }
    public java.util.Set<Quyen> cacQuyen() { return this.cacQuyen; }
    public void setCacQuyen(java.util.Set<Quyen> cacQuyen) { this.cacQuyen = cacQuyen; }
    public void datCacQuyen(java.util.Set<Quyen> cacQuyen) { this.cacQuyen = cacQuyen; }

    public VaiTro docVaiTro() {
        return taiKhoan != null ? taiKhoan.vaiTro() : null;
    }
    
    public String layTenHienThi() {
        return nhanVien != null ? nhanVien.hoTen() : "";
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
