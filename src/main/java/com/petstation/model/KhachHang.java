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
    /** Khởi tạo KhachHang với các giá trị được cung cấp. */
    public KhachHang() {
        this.diemTichLuy = 0;
        this.hangThanhVien = "REGULAR";
    }

    /**
     * Khởi tạo KhachHang với các giá trị được cung cấp.
     *
     * @param id id
     * @param maKhachHang mã khách hàng
     * @param hoTen họ tên
     * @param soDienThoai số điện thoại
     */
    public KhachHang(Long id, String maKhachHang, String hoTen, String soDienThoai) {
        this();
        this.id = id;
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong KhachHang.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong KhachHang.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã khách hàng trong KhachHang.
     *
     * @return kết quả lấy mã khách hàng
     */
    public String layMaKhachHang() {
        return maKhachHang;
    }

    /**
     * Đặt mã khách hàng trong KhachHang.
     *
     * @param maKhachHang mã khách hàng
     */
    public void datMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    /**
     * Lấy họ tên trong KhachHang.
     *
     * @return kết quả lấy họ tên
     */
    public String layHoTen() {
        return hoTen;
    }

    /**
     * Đặt họ tên trong KhachHang.
     *
     * @param hoTen họ tên
     */
    public void datHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    /**
     * Lấy số điện thoại trong KhachHang.
     *
     * @return kết quả lấy số điện thoại
     */
    public String laySoDienThoai() {
        return soDienThoai;
    }

    /**
     * Đặt số điện thoại trong KhachHang.
     *
     * @param soDienThoai số điện thoại
     */
    public void datSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    /**
     * Lấy địa chỉ trong KhachHang.
     *
     * @return kết quả lấy địa chỉ
     */
    public String layDiaChi() {
        return diaChi;
    }

    /**
     * Đặt địa chỉ trong KhachHang.
     *
     * @param diaChi địa chỉ
     */
    public void datDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    /**
     * Lấy ngày sinh trong KhachHang.
     *
     * @return kết quả lấy ngày sinh
     */
    public LocalDate layNgaySinh() {
        return ngaySinh;
    }

    /**
     * Đặt ngày sinh trong KhachHang.
     *
     * @param ngaySinh ngày sinh
     */
    public void datNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    /**
     * Lấy điểm tích lũy trong KhachHang.
     *
     * @return kết quả lấy điểm tích lũy
     */
    public Integer layDiemTichLuy() {
        return diemTichLuy;
    }

    /**
     * Đặt điểm tích lũy trong KhachHang.
     *
     * @param diemTichLuy điểm tích lũy
     */
    public void datDiemTichLuy(Integer diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    /**
     * Lấy hạng thành viên trong KhachHang.
     *
     * @return kết quả lấy hạng thành viên
     */
    public String layHangThanhVien() {
        return hangThanhVien;
    }

    /**
     * Đặt hạng thành viên trong KhachHang.
     *
     * @param hangThanhVien hạng thành viên
     */
    public void datHangThanhVien(String hangThanhVien) {
        this.hangThanhVien = hangThanhVien;
    }

    /**
     * Lấy thời điểm tạo trong KhachHang.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong KhachHang.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong KhachHang.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong KhachHang.
     *
     * @param phienBanBanGhi phiên bản bản ghi
     */
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
