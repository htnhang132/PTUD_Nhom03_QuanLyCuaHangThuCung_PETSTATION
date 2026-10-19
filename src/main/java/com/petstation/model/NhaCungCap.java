package com.petstation.model;

import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng nha_cung_cap trong SQL Server. */
public class NhaCungCap {
    /** Id. */
    private Long id;

    /** Mã nhà cung cấp. */
    private String maNhaCungCap;

    /** Tên nhà cung cấp. */
    private String tenNhaCungCap;

    /** Liên hệ người. */
    private String lienHeNguoi;

    /** Số điện thoại. */
    private String soDienThoai;

    /** Email. */
    private String email;

    /** Địa chỉ. */
    private String diaChi;

    /** Mã thuế. */
    private String maThue;

    /** Đang hoạt động. */
    private Boolean dangHoatDong;

    /** Ghi chú. */
    private String ghiChu;

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo NhaCungCap với các giá trị được cung cấp. */
    public NhaCungCap() {
        this.dangHoatDong = true;
    }

    /**
     * Khởi tạo NhaCungCap với các giá trị được cung cấp.
     *
     * @param id id
     * @param maNhaCungCap mã nhà cung cấp
     * @param tenNhaCungCap tên nhà cung cấp
     * @param soDienThoai số điện thoại
     */
    public NhaCungCap(Long id, String maNhaCungCap, String tenNhaCungCap, String soDienThoai) {
        this();
        this.id = id;
        this.maNhaCungCap = maNhaCungCap;
        this.tenNhaCungCap = tenNhaCungCap;
        this.soDienThoai = soDienThoai;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong NhaCungCap.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong NhaCungCap.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã nhà cung cấp trong NhaCungCap.
     *
     * @return kết quả lấy mã nhà cung cấp
     */
    public String layMaNhaCungCap() {
        return maNhaCungCap;
    }

    /**
     * Đặt mã nhà cung cấp trong NhaCungCap.
     *
     * @param maNhaCungCap mã nhà cung cấp
     */
    public void datMaNhaCungCap(String maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
    }

    /**
     * Lấy tên nhà cung cấp trong NhaCungCap.
     *
     * @return kết quả lấy tên nhà cung cấp
     */
    public String layTenNhaCungCap() {
        return tenNhaCungCap;
    }

    /**
     * Đặt tên nhà cung cấp trong NhaCungCap.
     *
     * @param tenNhaCungCap tên nhà cung cấp
     */
    public void datTenNhaCungCap(String tenNhaCungCap) {
        this.tenNhaCungCap = tenNhaCungCap;
    }

    /**
     * Lấy liên hệ người trong NhaCungCap.
     *
     * @return kết quả lấy liên hệ người
     */
    public String layLienHeNguoi() {
        return lienHeNguoi;
    }

    /**
     * Đặt liên hệ người trong NhaCungCap.
     *
     * @param lienHeNguoi liên hệ người
     */
    public void datLienHeNguoi(String lienHeNguoi) {
        this.lienHeNguoi = lienHeNguoi;
    }

    /**
     * Lấy số điện thoại trong NhaCungCap.
     *
     * @return kết quả lấy số điện thoại
     */
    public String laySoDienThoai() {
        return soDienThoai;
    }

    /**
     * Đặt số điện thoại trong NhaCungCap.
     *
     * @param soDienThoai số điện thoại
     */
    public void datSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    /**
     * Lấy email trong NhaCungCap.
     *
     * @return kết quả lấy email
     */
    public String layEmail() {
        return email;
    }

    /**
     * Đặt email trong NhaCungCap.
     *
     * @param email email
     */
    public void datEmail(String email) {
        this.email = email;
    }

    /**
     * Lấy địa chỉ trong NhaCungCap.
     *
     * @return kết quả lấy địa chỉ
     */
    public String layDiaChi() {
        return diaChi;
    }

    /**
     * Đặt địa chỉ trong NhaCungCap.
     *
     * @param diaChi địa chỉ
     */
    public void datDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    /**
     * Lấy mã thuế trong NhaCungCap.
     *
     * @return kết quả lấy mã thuế
     */
    public String layMaThue() {
        return maThue;
    }

    /**
     * Đặt mã thuế trong NhaCungCap.
     *
     * @param maThue mã thuế
     */
    public void datMaThue(String maThue) {
        this.maThue = maThue;
    }

    /**
     * Lấy đang hoạt động trong NhaCungCap.
     *
     * @return kết quả lấy đang hoạt động
     */
    public Boolean layDangHoatDong() {
        return dangHoatDong;
    }

    /**
     * Đặt đang hoạt động trong NhaCungCap.
     *
     * @param dangHoatDong đang hoạt động
     */
    public void datDangHoatDong(Boolean dangHoatDong) {
        this.dangHoatDong = dangHoatDong;
    }

    /**
     * Lấy ghi chú trong NhaCungCap.
     *
     * @return kết quả lấy ghi chú
     */
    public String layGhiChu() {
        return ghiChu;
    }

    /**
     * Đặt ghi chú trong NhaCungCap.
     *
     * @param ghiChu ghi chú
     */
    public void datGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Lấy thời điểm tạo trong NhaCungCap.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong NhaCungCap.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong NhaCungCap.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong NhaCungCap.
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
        return "Supplier{"
                + "id="
                + id
                + ", supplierCode='"
                + maNhaCungCap
                + '\''
                + ", supplierName='"
                + tenNhaCungCap
                + '\''
                + ", phoneNumber='"
                + soDienThoai
                + '\''
                + ", isActive="
                + dangHoatDong
                + '}';
    }
}
