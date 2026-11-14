package com.petstation.model.khachhang;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class KhachHang implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private Integer diemTichLuy;
    private String hangThanhVien;
    private String email = "";
    private String ghiChu = "";
    private boolean dangHoatDong = true;
    private LocalDateTime thoiDiemTao;

    public KhachHang() {
        this.diemTichLuy = 0;
        this.hangThanhVien = "Thường";
        this.thoiDiemTao = LocalDateTime.now();
        this.dangHoatDong = true;
    }

    public KhachHang(String maKhachHang) {
        this();
        setMaKhachHang(maKhachHang);
    }

    public KhachHang(
            String maKhachHang,
            String hoTen,
            String soDienThoai,
            String diaChi,
            Integer diemTichLuy,
            String hangThanhVien,
            LocalDateTime thoiDiemTao) {
        this();
        setMaKhachHang(maKhachHang);
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setDiemTichLuy(diemTichLuy);
        setHangThanhVien(hangThanhVien);
        setThoiDiemTao(thoiDiemTao);
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang == null ? null : maKhachHang.trim();
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen == null ? null : hoTen.trim();
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai == null ? null : soDienThoai.trim();
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public Integer getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(Integer diemTichLuy) {
        if (diemTichLuy != null && diemTichLuy < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm.");
        }
        this.diemTichLuy = diemTichLuy == null ? 0 : diemTichLuy;
    }

    public String getHangThanhVien() {
        return hangThanhVien;
    }

    public void setHangThanhVien(String hangThanhVien) {
        this.hangThanhVien =
                (hangThanhVien == null || hangThanhVien.isBlank()) ? "Đồng" : hangThanhVien.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? "" : email.trim();
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu == null ? "" : ghiChu;
    }

    public boolean isDangHoatDong() {
        return dangHoatDong;
    }

    public void setDangHoatDong(boolean dangHoatDong) {
        this.dangHoatDong = dangHoatDong;
    }

    public LocalDateTime getThoiDiemTao() {
        return thoiDiemTao;
    }

    public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang that = (KhachHang) o;
        return Objects.equals(this.maKhachHang, that.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhachHang);
    }

    @Override
    public String toString() {
        return "KhachHang{"
                + "maKhachHang='"
                + maKhachHang
                + '\''
                + ", hoTen='"
                + hoTen
                + '\''
                + ", soDienThoai='"
                + soDienThoai
                + '\''
                + ", hangThanhVien='"
                + hangThanhVien
                + '\''
                + ", diemTichLuy="
                + diemTichLuy
                + '}';
    }
}
