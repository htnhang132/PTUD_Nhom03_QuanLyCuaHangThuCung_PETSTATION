package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Thực thể nhà cung cấp khớp bảng [nha_cung_cap].
 */
public class NhaCungCap implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maNcc;
    private String ten;
    private String soDienThoai;
    private String diaChi;
    private String nguoiDaiDien;
    private String nhomMatHang;
    private String ghiChu;
    private String trangThai;
    private String maSoThue;
    private String dieuKhoan;

    public NhaCungCap() {}

    public NhaCungCap(long id, String maNcc, String ten, String soDienThoai, String diaChi, String nguoiDaiDien, String nhomMatHang, String ghiChu, String trangThai, String maSoThue, String dieuKhoan) {
        this.id = id;
        this.maNcc = maNcc;
        this.ten = ten;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.nguoiDaiDien = nguoiDaiDien;
        this.nhomMatHang = nhomMatHang;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
        this.maSoThue = maSoThue;
        this.dieuKhoan = dieuKhoan;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaNcc() { return this.maNcc; }
    public String layMaNcc() { return this.maNcc; }
    public String maNcc() { return this.maNcc; }
    public void setMaNcc(String maNcc) { this.maNcc = maNcc; }
    public void datMaNcc(String maNcc) { this.maNcc = maNcc; }

    public String getTen() { return this.ten; }
    public String layTen() { return this.ten; }
    public String ten() { return this.ten; }
    public void setTen(String ten) { this.ten = ten; }
    public void datTen(String ten) { this.ten = ten; }

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

    public String getNguoiDaiDien() { return this.nguoiDaiDien; }
    public String layNguoiDaiDien() { return this.nguoiDaiDien; }
    public String nguoiDaiDien() { return this.nguoiDaiDien; }
    public void setNguoiDaiDien(String nguoiDaiDien) { this.nguoiDaiDien = nguoiDaiDien; }
    public void datNguoiDaiDien(String nguoiDaiDien) { this.nguoiDaiDien = nguoiDaiDien; }

    public String getNhomMatHang() { return this.nhomMatHang; }
    public String layNhomMatHang() { return this.nhomMatHang; }
    public String nhomMatHang() { return this.nhomMatHang; }
    public void setNhomMatHang(String nhomMatHang) { this.nhomMatHang = nhomMatHang; }
    public void datNhomMatHang(String nhomMatHang) { this.nhomMatHang = nhomMatHang; }

    public String getGhiChu() { return this.ghiChu; }
    public String layGhiChu() { return this.ghiChu; }
    public String ghiChu() { return this.ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public void datGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMaSoThue() { return this.maSoThue; }
    public String layMaSoThue() { return this.maSoThue; }
    public String maSoThue() { return this.maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }
    public void datMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }

    public String getDieuKhoan() { return this.dieuKhoan; }
    public String layDieuKhoan() { return this.dieuKhoan; }
    public String dieuKhoan() { return this.dieuKhoan; }
    public void setDieuKhoan(String dieuKhoan) { this.dieuKhoan = dieuKhoan; }
    public void datDieuKhoan(String dieuKhoan) { this.dieuKhoan = dieuKhoan; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhaCungCap that = (NhaCungCap) o;
        return this.id == that.id
                && Objects.equals(maNcc, that.maNcc)
                && Objects.equals(ten, that.ten)
                && Objects.equals(soDienThoai, that.soDienThoai)
                && Objects.equals(diaChi, that.diaChi)
                && Objects.equals(nguoiDaiDien, that.nguoiDaiDien)
                && Objects.equals(nhomMatHang, that.nhomMatHang)
                && Objects.equals(ghiChu, that.ghiChu)
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(maSoThue, that.maSoThue)
                && Objects.equals(dieuKhoan, that.dieuKhoan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maNcc, ten, soDienThoai, diaChi, nguoiDaiDien, nhomMatHang, ghiChu, trangThai, maSoThue, dieuKhoan);
    }

    @Override
    public String toString() {
        return "NhaCungCap{" + "id=" + id + ", " + "maNcc=" + maNcc + ", " + "ten=" + ten + ", " + "soDienThoai=" + soDienThoai + ", " + "diaChi=" + diaChi + ", " + "nguoiDaiDien=" + nguoiDaiDien + ", " + "nhomMatHang=" + nhomMatHang + ", " + "ghiChu=" + ghiChu + ", " + "trangThai=" + trangThai + ", " + "maSoThue=" + maSoThue + ", " + "dieuKhoan=" + dieuKhoan + "}";
    }
}
