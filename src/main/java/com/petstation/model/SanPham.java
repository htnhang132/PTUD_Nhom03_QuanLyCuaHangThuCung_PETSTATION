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

    // Chuẩn JavaBeans (get/set)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public Long getIdNhomHang() {
        return idNhomHang;
    }

    public void setIdNhomHang(Long idNhomHang) {
        this.idNhomHang = idNhomHang;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    public String getUrlAnh() {
        return urlAnh;
    }

    public void setUrlAnh(String urlAnh) {
        this.urlAnh = urlAnh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Boolean isCanDonThuoc() {
        return canDonThuoc;
    }

    public void setCanDonThuoc(Boolean canDonThuoc) {
        this.canDonThuoc = canDonThuoc;
    }

    public Boolean isDangHoatDong() {
        return dangHoatDong;
    }

    public void setDangHoatDong(Boolean dangHoatDong) {
        this.dangHoatDong = dangHoatDong;
    }

    public LocalDateTime getThoiDiemTao() {
        return thoiDiemTao;
    }

    public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    public byte[] getPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    public void setPhienBanBanGhi(byte[] phienBanBanGhi) {
        this.phienBanBanGhi = phienBanBanGhi;
    }

    // Truy xuất và cập nhật thuộc tính kiểu tiếng Việt (Tương thích mã nguồn hiện có)
    public Long layId() {
        return getId();
    }

    public void datId(Long id) {
        setId(id);
    }

    public String layMaSanPham() {
        return getMaSanPham();
    }

    public void datMaSanPham(String maSanPham) {
        setMaSanPham(maSanPham);
    }

    public String layTenSanPham() {
        return getTenSanPham();
    }

    public void datTenSanPham(String tenSanPham) {
        setTenSanPham(tenSanPham);
    }

    public Long layIdNhomHang() {
        return getIdNhomHang();
    }

    public void datIdNhomHang(Long idNhomHang) {
        setIdNhomHang(idNhomHang);
    }

    public String layDonVi() {
        return getDonVi();
    }

    public void datDonVi(String donVi) {
        setDonVi(donVi);
    }

    public BigDecimal layGiaBan() {
        return getGiaBan();
    }

    public void datGiaBan(BigDecimal giaBan) {
        setGiaBan(giaBan);
    }

    public String layURLAnh() {
        return getUrlAnh();
    }

    public void datURLAnh(String urlAnh) {
        setUrlAnh(urlAnh);
    }

    public String layMoTa() {
        return getMoTa();
    }

    public void datMoTa(String moTa) {
        setMoTa(moTa);
    }

    public Boolean layCanDonThuoc() {
        return isCanDonThuoc();
    }

    public void datCanDonThuoc(Boolean canDonThuoc) {
        setCanDonThuoc(canDonThuoc);
    }

    public Boolean layDangHoatDong() {
        return isDangHoatDong();
    }

    public void datDangHoatDong(Boolean dangHoatDong) {
        setDangHoatDong(dangHoatDong);
    }

    public LocalDateTime layThoiDiemTao() {
        return getThoiDiemTao();
    }

    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        setThoiDiemTao(thoiDiemTao);
    }

    public byte[] layPhienBanBanGhi() {
        return getPhienBanBanGhi();
    }

    public void datPhienBanBanGhi(byte[] phienBanBanGhi) {
        setPhienBanBanGhi(phienBanBanGhi);
    }

    /**
     * Biểu diễn đối tượng dưới dạng chuỗi.
     *
     * @return chuỗi mô tả đối tượng
     */
    @Override
    public String toString() {
        return "SanPham{"
                + "id="
                + id
                + ", maSanPham='"
                + maSanPham
                + '\''
                + ", tenSanPham='"
                + tenSanPham
                + '\''
                + ", giaBan="
                + giaBan
                + ", dangHoatDong="
                + dangHoatDong
                + '}';
    }
}
