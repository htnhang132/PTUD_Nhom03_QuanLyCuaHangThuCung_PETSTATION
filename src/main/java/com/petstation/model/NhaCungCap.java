package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.util.Objects;

public class NhaCungCap implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maNhaCungCap;
    private String ten;
    private String soDienThoai;
    private String diaChi;
    private String nguoiDaiDien;
    private String nhomMatHang;
    private String ghiChu;
    private String trangThai;
    private String maSoThue;
    private String dieuKhoan;

    public NhaCungCap() {
        this.trangThai = "Đang hợp tác";
    }

    public NhaCungCap(String maNhaCungCap) {
        this();
        setMaNhaCungCap(maNhaCungCap);
    }

    public NhaCungCap(
            String maNhaCungCap,
            String ten,
            String soDienThoai,
            String diaChi,
            String nguoiDaiDien,
            String nhomMatHang,
            String ghiChu,
            String trangThai,
            String maSoThue,
            String dieuKhoan) {
        this();
        setMaNhaCungCap(maNhaCungCap);
        setTen(ten);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setNguoiDaiDien(nguoiDaiDien);
        setNhomMatHang(nhomMatHang);
        setGhiChu(ghiChu);
        setTrangThai(trangThai);
        setMaSoThue(maSoThue);
        setDieuKhoan(dieuKhoan);
    }

    public NhaCungCap(long id) {
        this();
        setId(id);
    }

    public NhaCungCap(
            long id,
            String maNhaCungCap,
            String ten,
            String soDienThoai,
            String diaChi,
            String nguoiDaiDien,
            String nhomMatHang,
            String ghiChu,
            String trangThai,
            String maSoThue,
            String dieuKhoan) {
        this(maNhaCungCap, ten, soDienThoai, diaChi, nguoiDaiDien, nhomMatHang, ghiChu, trangThai, maSoThue, dieuKhoan);
        setId(id);
    }

    public boolean kiemTraDangHopTac() {
        return trangThai == null
                || "DANG_HOP_TAC".equalsIgnoreCase(trangThai)
                || "Đang hợp tác".equalsIgnoreCase(trangThai)
                || (!"Ngừng hợp tác".equalsIgnoreCase(trangThai) && !"NGUNG_HOP_TAC".equalsIgnoreCase(trangThai));
    }

    public String getMaNhaCungCap() {
        return this.maNhaCungCap;
    }

    public void setMaNhaCungCap(String maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap == null ? null : maNhaCungCap.trim();
    }

    public String getMaNcc() {
        return getMaNhaCungCap();
    }

    public void setMaNcc(String maNcc) {
        setMaNhaCungCap(maNcc);
    }

    public String getTen() { return this.ten; }
    public void setTen(String ten) { this.ten = ten == null ? null : ten.trim(); }

    public String getSoDienThoai() { return this.soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai == null ? null : soDienThoai.trim(); }

    public String getDiaChi() { return this.diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getNguoiDaiDien() { return this.nguoiDaiDien; }
    public void setNguoiDaiDien(String nguoiDaiDien) { this.nguoiDaiDien = nguoiDaiDien; }

    public String getNhomMatHang() { return this.nhomMatHang; }
    public void setNhomMatHang(String nhomMatHang) { this.nhomMatHang = nhomMatHang; }

    public String getGhiChu() { return this.ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTrangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMaSoThue() { return this.maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }

    public String getDieuKhoan() { return this.dieuKhoan; }
    public void setDieuKhoan(String dieuKhoan) { this.dieuKhoan = dieuKhoan; }

    public long getId() { return DuLieu.layIdCauNoi(this, maNhaCungCap); }
    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maNhaCungCap == null || this.maNhaCungCap.isBlank()) && id > 0) {
            this.maNhaCungCap = DuLieu.taoMaTuId("NCC", 4, id);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhaCungCap that = (NhaCungCap) o;
        return Objects.equals(this.maNhaCungCap, that.maNhaCungCap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhaCungCap);
    }

    @Override
    public String toString() {
        return "NhaCungCap{" + "maNhaCungCap='" + maNhaCungCap + '\'' + ", ten='" + ten + '\'' + ", soDienThoai='"
                + soDienThoai + '\'' + ", trangThai='" + trangThai + '\'' + '}';
    }
}
