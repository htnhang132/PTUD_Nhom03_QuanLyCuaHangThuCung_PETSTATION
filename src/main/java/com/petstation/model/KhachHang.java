package com.petstation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng khach_hang trong SQL Server. */
public class KhachHang {
    /** Id. */
    private Long id;

    /** Mã khách hàng. */
    private String maKhachHang;

    /** Họ tên. */
    private String hoTen;

    /** Số điện thoại. */
    private String soDienThoai;

    /** Địa chỉ. */
    private String diaChi;

    /** Ngày sinh. */
    private LocalDate ngaySinh;

    /** Điểm tích lũy. */
    private Integer diemTichLuy;

    /** Hạng thành viên. */
    private String hangThanhVien; // REGULAR, SILVER, GOLD, PLATINUM

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    public KhachHang() {
        this.diemTichLuy = 0;
        this.hangThanhVien = "REGULAR";
    }

    public KhachHang(Long id, String maKhachHang, String hoTen, String soDienThoai) {
        this();
        this.id = id;
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }

    // Truy xuất và cập nhật thuộc tính
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

    /**
     * Biểu diễn đối tượng dưới dạng chuỗi.
     *
     * @return chuỗi mô tả đối tượng
     */
    @Override
    public String toString() {
        return "Customer{"
                + "id="
                + id
                + ", customerCode='"
                + maKhachHang
                + '\''
                + ", fullName='"
                + hoTen
                + '\''
                + ", phoneNumber='"
                + soDienThoai
                + '\''
                + ", membershipTier='"
                + hangThanhVien
                + '\''
                + ", loyaltyPoints="
                + diemTichLuy
                + '}';
    }
}
