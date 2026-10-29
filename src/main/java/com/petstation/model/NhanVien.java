package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Biểu diễn dữ liệu ánh xạ với bảng nhan_vien trong SQL Server.
 *
 * @param id id
 * @param maNhanVien mã nhân viên
 * @param hoTen họ tên
 * @param ngaySinh ngày sinh
 * @param soDienThoai số điện thoại
 * @param diaChi địa chỉ
 * @param viTri vị trí
 * @param ngayBatDau ngày bắt đầu
 * @param hoatDong hoạt động
 */
public class NhanVien implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maNhanVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String diaChi;
    private String viTri;
    private LocalDate ngayBatDau;
    private boolean hoatDong;

    public NhanVien() {}

    public NhanVien(long id, String maNhanVien, String hoTen, LocalDate ngaySinh, String soDienThoai, String diaChi, String viTri, LocalDate ngayBatDau, boolean hoatDong) {
        this.id = id;
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.viTri = viTri;
        this.ngayBatDau = ngayBatDau;
        this.hoatDong = hoatDong;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaNhanVien() { return this.maNhanVien; }
    public String layMaNhanVien() { return this.maNhanVien; }
    public String maNhanVien() { return this.maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public void datMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getHoTen() { return this.hoTen; }
    public String layHoTen() { return this.hoTen; }
    public String hoTen() { return this.hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public void datHoTen(String hoTen) { this.hoTen = hoTen; }

    public LocalDate getNgaySinh() { return this.ngaySinh; }
    public LocalDate layNgaySinh() { return this.ngaySinh; }
    public LocalDate ngaySinh() { return this.ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public void datNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getSoDienThoai() { return this.soDienThoai; }
    public String laySoDienThoai() { return this.soDienThoai; }
    public String soDienThoai() { return this.soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public void datSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getDiaChi() { return this.diaChi; }
    public String layDiaChi() { return this.diaChi; }
    public String diaChi() { return this.diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public void datDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getViTri() { return this.viTri; }
    public String layViTri() { return this.viTri; }
    public String viTri() { return this.viTri; }
    public void setViTri(String viTri) { this.viTri = viTri; }
    public void datViTri(String viTri) { this.viTri = viTri; }

    public LocalDate getNgayBatDau() { return this.ngayBatDau; }
    public LocalDate layNgayBatDau() { return this.ngayBatDau; }
    public LocalDate ngayBatDau() { return this.ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public void datNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public boolean isHoatDong() { return this.hoatDong; }
    public boolean layHoatDong() { return this.hoatDong; }
    public boolean hoatDong() { return this.hoatDong; }
    public void setHoatDong(boolean hoatDong) { this.hoatDong = hoatDong; }
    public void datHoatDong(boolean hoatDong) { this.hoatDong = hoatDong; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien that = (NhanVien) o;
        return this.id == that.id
                && Objects.equals(maNhanVien, that.maNhanVien)
                && Objects.equals(hoTen, that.hoTen)
                && Objects.equals(ngaySinh, that.ngaySinh)
                && Objects.equals(soDienThoai, that.soDienThoai)
                && Objects.equals(diaChi, that.diaChi)
                && Objects.equals(viTri, that.viTri)
                && Objects.equals(ngayBatDau, that.ngayBatDau)
                && this.hoatDong == that.hoatDong;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maNhanVien, hoTen, ngaySinh, soDienThoai, diaChi, viTri, ngayBatDau, hoatDong);
    }

    @Override
    public String toString() {
        return "NhanVien{" + "id=" + id + ", " + "maNhanVien=" + maNhanVien + ", " + "hoTen=" + hoTen + ", " + "ngaySinh=" + ngaySinh + ", " + "soDienThoai=" + soDienThoai + ", " + "diaChi=" + diaChi + ", " + "viTri=" + viTri + ", " + "ngayBatDau=" + ngayBatDau + ", " + "hoatDong=" + hoatDong + "}";
    }
}
