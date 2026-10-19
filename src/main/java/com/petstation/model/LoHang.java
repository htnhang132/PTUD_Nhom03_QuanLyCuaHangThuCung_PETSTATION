package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng lo_hang trong SQL Server. */
public class LoHang {
    /** Id. */
    private Long id;

    /** Mã lô hàng. */
    private String maLoHang;

    /** Id sản phẩm. */
    private Long idSanPham;

    /** Ngày sản xuất. */
    private LocalDate ngaySanXuat;

    /** Hạn sử dụng. */
    private LocalDate hanSuDung;

    /** Số lượng. */
    private Integer soLuong;

    /** Giá giá nhập. */
    private BigDecimal giaGiaNhap;

    /** Id nhà cung cấp. */
    private Long idNhaCungCap;

    /** Trạng thái. */
    private String trangThai; // IN_STOCK, LOW_STOCK, EXPIRED, SOLD_OUT

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo LoHang với các giá trị được cung cấp. */
    public LoHang() {
        this.trangThai = "IN_STOCK";
    }

    /**
     * Khởi tạo LoHang với các giá trị được cung cấp.
     *
     * @param id id
     * @param maLoHang mã lô hàng
     * @param idSanPham id sản phẩm
     * @param soLuong số lượng
     * @param giaGiaNhap giá giá nhập
     */
    public LoHang(
            Long id, String maLoHang, Long idSanPham, Integer soLuong, BigDecimal giaGiaNhap) {
        this();
        this.id = id;
        this.maLoHang = maLoHang;
        this.idSanPham = idSanPham;
        this.soLuong = soLuong;
        this.giaGiaNhap = giaGiaNhap;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong LoHang.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong LoHang.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã lô hàng trong LoHang.
     *
     * @return kết quả lấy mã lô hàng
     */
    public String layMaLoHang() {
        return maLoHang;
    }

    /**
     * Đặt mã lô hàng trong LoHang.
     *
     * @param maLoHang mã lô hàng
     */
    public void datMaLoHang(String maLoHang) {
        this.maLoHang = maLoHang;
    }

    /**
     * Lấy id sản phẩm trong LoHang.
     *
     * @return kết quả lấy id sản phẩm
     */
    public Long layIdSanPham() {
        return idSanPham;
    }

    /**
     * Đặt id sản phẩm trong LoHang.
     *
     * @param idSanPham id sản phẩm
     */
    public void datIdSanPham(Long idSanPham) {
        this.idSanPham = idSanPham;
    }

    /**
     * Lấy ngày sản xuất trong LoHang.
     *
     * @return kết quả lấy ngày sản xuất
     */
    public LocalDate layNgaySanXuat() {
        return ngaySanXuat;
    }

    /**
     * Đặt ngày sản xuất trong LoHang.
     *
     * @param ngaySanXuat ngày sản xuất
     */
    public void datNgaySanXuat(LocalDate ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
    }

    /**
     * Lấy hạn sử dụng trong LoHang.
     *
     * @return kết quả lấy hạn sử dụng
     */
    public LocalDate layHanSuDung() {
        return hanSuDung;
    }

    /**
     * Đặt hạn sử dụng trong LoHang.
     *
     * @param hanSuDung hạn sử dụng
     */
    public void datHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    /**
     * Lấy số lượng trong LoHang.
     *
     * @return kết quả lấy số lượng
     */
    public Integer laySoLuong() {
        return soLuong;
    }

    /**
     * Đặt số lượng trong LoHang.
     *
     * @param soLuong số lượng
     */
    public void datSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    /**
     * Lấy giá giá nhập trong LoHang.
     *
     * @return kết quả lấy giá giá nhập
     */
    public BigDecimal layGiaGiaNhap() {
        return giaGiaNhap;
    }

    /**
     * Đặt giá giá nhập trong LoHang.
     *
     * @param giaGiaNhap giá giá nhập
     */
    public void datGiaGiaNhap(BigDecimal giaGiaNhap) {
        this.giaGiaNhap = giaGiaNhap;
    }

    /**
     * Lấy id nhà cung cấp trong LoHang.
     *
     * @return kết quả lấy id nhà cung cấp
     */
    public Long layIdNhaCungCap() {
        return idNhaCungCap;
    }

    /**
     * Đặt id nhà cung cấp trong LoHang.
     *
     * @param idNhaCungCap id nhà cung cấp
     */
    public void datIdNhaCungCap(Long idNhaCungCap) {
        this.idNhaCungCap = idNhaCungCap;
    }

    /**
     * Lấy trạng thái trong LoHang.
     *
     * @return kết quả lấy trạng thái
     */
    public String layTrangThai() {
        return trangThai;
    }

    /**
     * Đặt trạng thái trong LoHang.
     *
     * @param trangThai trạng thái
     */
    public void datTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Lấy thời điểm tạo trong LoHang.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong LoHang.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong LoHang.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong LoHang.
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
        return "Batch{"
                + "id="
                + id
                + ", batchCode='"
                + maLoHang
                + '\''
                + ", productId="
                + idSanPham
                + ", quantity="
                + soLuong
                + ", expiryDate="
                + hanSuDung
                + ", status='"
                + trangThai
                + '\''
                + '}';
    }
}
