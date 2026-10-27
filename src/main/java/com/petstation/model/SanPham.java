package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng san_pham trong SQL Server. */
public class SanPham {
    /** Id. */
    private Long id;

    /** Mã sản phẩm. */
    private String maSanPham;

    /** Tên sản phẩm. */
    private String tenSanPham;

    /** Id nhóm hàng. */
    private Long idNhomHang;

    /** Đơn vị. */
    private String donVi;

    /** Giá bán. */
    private BigDecimal giaBan;

    /** URL ảnh. */
    private String urlAnh;

    /** Mô tả. */
    private String moTa;

    /** Cần đơn thuốc. */
    private Boolean canDonThuoc;

    /** Đang hoạt động. */
    private Boolean dangHoatDong;

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    public SanPham() {
        this.canDonThuoc = false;
        this.dangHoatDong = true;
    }

    public SanPham(Long id, String maSanPham, String tenSanPham, BigDecimal giaBan) {
        this();
        this.id = id;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.giaBan = giaBan;
    }

    // Truy xuất và cập nhật thuộc tính
    public Long layId() {
        return id;
    }

    public void datId(Long id) {
        this.id = id;
    }

    public String layMaSanPham() {
        return maSanPham;
    }

    public void datMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String layTenSanPham() {
        return tenSanPham;
    }

    public void datTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public Long layIdNhomHang() {
        return idNhomHang;
    }

    public void datIdNhomHang(Long idNhomHang) {
        this.idNhomHang = idNhomHang;
    }

    public String layDonVi() {
        return donVi;
    }

    public void datDonVi(String donVi) {
        this.donVi = donVi;
    }

    public BigDecimal layGiaBan() {
        return giaBan;
    }

    public void datGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    public String layURLAnh() {
        return urlAnh;
    }

    public void datURLAnh(String urlAnh) {
        this.urlAnh = urlAnh;
    }

    public String layMoTa() {
        return moTa;
    }

    public void datMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Boolean layCanDonThuoc() {
        return canDonThuoc;
    }

    public void datCanDonThuoc(Boolean canDonThuoc) {
        this.canDonThuoc = canDonThuoc;
    }

    public Boolean layDangHoatDong() {
        return dangHoatDong;
    }

    public void datDangHoatDong(Boolean dangHoatDong) {
        this.dangHoatDong = dangHoatDong;
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
        return "Product{"
                + "id="
                + id
                + ", productCode='"
                + maSanPham
                + '\''
                + ", productName='"
                + tenSanPham
                + '\''
                + ", sellingPrice="
                + giaBan
                + ", isActive="
                + dangHoatDong
                + '}';
    }
}
