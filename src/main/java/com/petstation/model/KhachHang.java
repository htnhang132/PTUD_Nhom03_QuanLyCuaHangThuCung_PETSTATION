package com.petstation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng khach_hang trong SQL Server. */
public class KhachHang {
    private Long id;
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private LocalDate ngaySinh;
    private Integer diemTichLuy;
    private String hangThanhVien;
    private LocalDateTime thoiDiemTao;
    private byte[] phienBanBanGhi;

    public KhachHang() {
        this.diemTichLuy = 0;
        this.hangThanhVien = "Thường";
    }

    public KhachHang(Long id, String maKhachHang, String hoTen, String soDienThoai) {
        this();
        this.id = id;
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }

    public Long layId() {
        return id;
    }

    public void datId(Long id) {
        this.id = id;
    }

    public String layMaKhachHang() {
        return maKhachHang;
    }

    public void datMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String layHoTen() {
        return hoTen;
    }

    public void datHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String laySoDienThoai() {
        return soDienThoai;
    }

    public void datSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String layDiaChi() {
        return diaChi;
    }

    public void datDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public LocalDate layNgaySinh() {
        return ngaySinh;
    }

    public void datNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public Integer layDiemTichLuy() {
        return diemTichLuy;
    }

    public void datDiemTichLuy(Integer diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public String layHangThanhVien() {
        return hangThanhVien;
    }

    public void datHangThanhVien(String hangThanhVien) {
        this.hangThanhVien = hangThanhVien;
    }

    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    public void datPhienBanBanGhi(byte[] phienBanBanGhi) {
        this.phienBanBanGhi = phienBanBanGhi;
    }

    @Override
    public String toString() {
        return "KhachHang{"
                + "id="
                + id
                + ", maKhachHang='"
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
