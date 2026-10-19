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
    /** Khởi tạo SanPham với các giá trị được cung cấp. */
    public SanPham() {
        this.canDonThuoc = false;
        this.dangHoatDong = true;
    }

    /**
     * Khởi tạo SanPham với các giá trị được cung cấp.
     *
     * @param id id
     * @param maSanPham mã sản phẩm
     * @param tenSanPham tên sản phẩm
     * @param giaBan giá bán
     */
    public SanPham(Long id, String maSanPham, String tenSanPham, BigDecimal giaBan) {
        this();
        this.id = id;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.giaBan = giaBan;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong SanPham.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong SanPham.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã sản phẩm trong SanPham.
     *
     * @return kết quả lấy mã sản phẩm
     */
    public String layMaSanPham() {
        return maSanPham;
    }

    /**
     * Đặt mã sản phẩm trong SanPham.
     *
     * @param maSanPham mã sản phẩm
     */
    public void datMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    /**
     * Lấy tên sản phẩm trong SanPham.
     *
     * @return kết quả lấy tên sản phẩm
     */
    public String layTenSanPham() {
        return tenSanPham;
    }

    /**
     * Đặt tên sản phẩm trong SanPham.
     *
     * @param tenSanPham tên sản phẩm
     */
    public void datTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    /**
     * Lấy id nhóm hàng trong SanPham.
     *
     * @return kết quả lấy id nhóm hàng
     */
    public Long layIdNhomHang() {
        return idNhomHang;
    }

    /**
     * Đặt id nhóm hàng trong SanPham.
     *
     * @param idNhomHang id nhóm hàng
     */
    public void datIdNhomHang(Long idNhomHang) {
        this.idNhomHang = idNhomHang;
    }

    /**
     * Lấy đơn vị trong SanPham.
     *
     * @return kết quả lấy đơn vị
     */
    public String layDonVi() {
        return donVi;
    }

    /**
     * Đặt đơn vị trong SanPham.
     *
     * @param donVi đơn vị
     */
    public void datDonVi(String donVi) {
        this.donVi = donVi;
    }

    /**
     * Lấy giá bán trong SanPham.
     *
     * @return kết quả lấy giá bán
     */
    public BigDecimal layGiaBan() {
        return giaBan;
    }

    /**
     * Đặt giá bán trong SanPham.
     *
     * @param giaBan giá bán
     */
    public void datGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    /**
     * Lấy URL ảnh trong SanPham.
     *
     * @return kết quả lấy URL ảnh
     */
    public String layURLAnh() {
        return urlAnh;
    }

    /**
     * Đặt URL ảnh trong SanPham.
     *
     * @param urlAnh URL ảnh
     */
    public void datURLAnh(String urlAnh) {
        this.urlAnh = urlAnh;
    }

    /**
     * Lấy mô tả trong SanPham.
     *
     * @return kết quả lấy mô tả
     */
    public String layMoTa() {
        return moTa;
    }

    /**
     * Đặt mô tả trong SanPham.
     *
     * @param moTa mô tả
     */
    public void datMoTa(String moTa) {
        this.moTa = moTa;
    }

    /**
     * Lấy cần đơn thuốc trong SanPham.
     *
     * @return kết quả lấy cần đơn thuốc
     */
    public Boolean layCanDonThuoc() {
        return canDonThuoc;
    }

    /**
     * Đặt cần đơn thuốc trong SanPham.
     *
     * @param canDonThuoc cần đơn thuốc
     */
    public void datCanDonThuoc(Boolean canDonThuoc) {
        this.canDonThuoc = canDonThuoc;
    }

    /**
     * Lấy đang hoạt động trong SanPham.
     *
     * @return kết quả lấy đang hoạt động
     */
    public Boolean layDangHoatDong() {
        return dangHoatDong;
    }

    /**
     * Đặt đang hoạt động trong SanPham.
     *
     * @param dangHoatDong đang hoạt động
     */
    public void datDangHoatDong(Boolean dangHoatDong) {
        this.dangHoatDong = dangHoatDong;
    }

    /**
     * Lấy thời điểm tạo trong SanPham.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong SanPham.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong SanPham.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong SanPham.
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
