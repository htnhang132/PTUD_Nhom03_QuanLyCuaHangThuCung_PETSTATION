package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class NhanVien implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maNhanVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String diaChi;
    private String viTri;
    private LocalDate ngayBatDau;
    private boolean hoatDong;

    public NhanVien() {
        this.hoatDong = true;
    }

    public NhanVien(String maNhanVien) {
        this();
        setMaNhanVien(maNhanVien);
    }

    public NhanVien(
            String maNhanVien,
            String hoTen,
            LocalDate ngaySinh,
            String soDienThoai,
            String diaChi,
            String viTri,
            LocalDate ngayBatDau,
            boolean hoatDong) {
        this();
        setMaNhanVien(maNhanVien);
        setHoTen(hoTen);
        setNgaySinh(ngaySinh);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setViTri(viTri);
        setNgayBatDau(ngayBatDau);
        setHoatDong(hoatDong);
    }

    public int tinhTuoi() {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now())) {
            return 0;
        }
        return Period.between(ngaySinh, LocalDate.now()).getYears();
    }

    public int getTuoi() {
        return tinhTuoi();
    }

    public int tinhThamNienNam() {
        if (ngayBatDau == null || ngayBatDau.isAfter(LocalDate.now())) {
            return 0;
        }
        return Period.between(ngayBatDau, LocalDate.now()).getYears();
    }

    public int getThamNienNam() {
        return tinhThamNienNam();
    }

    public String getMaNhanVien() {
        return this.maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien == null ? null : maNhanVien.trim();
    }

    public String getHoTen() {
        return this.hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen == null ? null : hoTen.trim();
    }

    public LocalDate getNgaySinh() {
        return this.ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSoDienThoai() {
        return this.soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai == null ? null : soDienThoai.trim();
    }

    public String getDiaChi() {
        return this.diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getViTri() {
        return this.viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public LocalDate getNgayBatDau() {
        return this.ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public boolean isHoatDong() {
        return this.hoatDong;
    }

    public void setHoatDong(boolean hoatDong) {
        this.hoatDong = hoatDong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien that = (NhanVien) o;
        return Objects.equals(this.maNhanVien, that.maNhanVien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhanVien);
    }

    @Override
    public String toString() {
        return "NhanVien{" + "maNhanVien='" + maNhanVien + '\'' + ", hoTen='" + hoTen + '\''
                + ", viTri='" + viTri + '\'' + ", hoatDong=" + hoatDong + '}';
    }
}
