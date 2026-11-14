package com.petstation.dto;

import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class PhienDangNhap implements Serializable {
    private static final long serialVersionUID = 1L;

    private TaiKhoan taiKhoan;
    private NhanVien nhanVien;
    private Set<Quyen> cacQuyen;

    public PhienDangNhap() {}

    public PhienDangNhap(TaiKhoan taiKhoan, NhanVien nhanVien, Set<Quyen> cacQuyen) {
        this.taiKhoan = taiKhoan;
        this.nhanVien = nhanVien;
        this.cacQuyen = cacQuyen;
    }

    public TaiKhoan getTaiKhoan() {
        return this.taiKhoan;
    }

    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    public void setCacQuyen(Set<Quyen> cacQuyen) {
        this.cacQuyen = cacQuyen;
    }

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
        return "PhienDangNhap{"
                + "taiKhoan="
                + taiKhoan
                + ", "
                + "nhanVien="
                + nhanVien
                + ", "
                + "cacQuyen="
                + cacQuyen
                + "}";
    }
}
