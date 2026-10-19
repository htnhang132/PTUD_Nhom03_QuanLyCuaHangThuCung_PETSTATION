package com.petstation.model;

import java.math.BigDecimal;

/** Biểu diễn dữ liệu ánh xạ với bảng chi_tiet_don_hang trong SQL Server. */
public class ChiTietDonHang {
    /** Id. */
    private Long id;

    /** Id hóa đơn. */
    private Long idHoaDon;

    /** Id sản phẩm. */
    private Long idSanPham;

    /** Id lô hàng. */
    private Long idLoHang;

    /** Số lượng. */
    private Integer soLuong;

    /** Đơn giá. */
    private BigDecimal donGia;

    /** Tiền giảm giá. */
    private BigDecimal tienGiamGia;

    /** Thành tiền. */
    private BigDecimal thanhTien;

    /** Ghi chú. */
    private String ghiChu;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo ChiTietDonHang với các giá trị được cung cấp. */
    public ChiTietDonHang() {
        this.tienGiamGia = BigDecimal.ZERO;
    }

    /**
     * Khởi tạo ChiTietDonHang với các giá trị được cung cấp.
     *
     * @param id id
     * @param idHoaDon id hóa đơn
     * @param idSanPham id sản phẩm
     * @param soLuong số lượng
     * @param donGia đơn giá
     */
    public ChiTietDonHang(
            Long id, Long idHoaDon, Long idSanPham, Integer soLuong, BigDecimal donGia) {
        this();
        this.id = id;
        this.idHoaDon = idHoaDon;
        this.idSanPham = idSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
        tinhThanhTien();
    }

    // Xử lý nghiệp vụ
    /** Tính thành tiền trong ChiTietDonHang. */
    public void tinhThanhTien() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (soLuong != null && donGia != null) {
            // Tạm tính.
            BigDecimal tamTinh = donGia.multiply(BigDecimal.valueOf(soLuong));
            this.thanhTien = tamTinh.subtract(tienGiamGia != null ? tienGiamGia : BigDecimal.ZERO);
        }
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong ChiTietDonHang.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong ChiTietDonHang.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy id hóa đơn trong ChiTietDonHang.
     *
     * @return kết quả lấy id hóa đơn
     */
    public Long layIdHoaDon() {
        return idHoaDon;
    }

    /**
     * Đặt id hóa đơn trong ChiTietDonHang.
     *
     * @param idHoaDon id hóa đơn
     */
    public void datIdHoaDon(Long idHoaDon) {
        this.idHoaDon = idHoaDon;
    }

    /**
     * Lấy id sản phẩm trong ChiTietDonHang.
     *
     * @return kết quả lấy id sản phẩm
     */
    public Long layIdSanPham() {
        return idSanPham;
    }

    /**
     * Đặt id sản phẩm trong ChiTietDonHang.
     *
     * @param idSanPham id sản phẩm
     */
    public void datIdSanPham(Long idSanPham) {
        this.idSanPham = idSanPham;
    }

    /**
     * Lấy id lô hàng trong ChiTietDonHang.
     *
     * @return kết quả lấy id lô hàng
     */
    public Long layIdLoHang() {
        return idLoHang;
    }

    /**
     * Đặt id lô hàng trong ChiTietDonHang.
     *
     * @param idLoHang id lô hàng
     */
    public void datIdLoHang(Long idLoHang) {
        this.idLoHang = idLoHang;
    }

    /**
     * Lấy số lượng trong ChiTietDonHang.
     *
     * @return kết quả lấy số lượng
     */
    public Integer laySoLuong() {
        return soLuong;
    }

    /**
     * Đặt số lượng trong ChiTietDonHang.
     *
     * @param soLuong số lượng
     */
    public void datSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
        tinhThanhTien();
    }

    /**
     * Lấy đơn giá trong ChiTietDonHang.
     *
     * @return kết quả lấy đơn giá
     */
    public BigDecimal layDonGia() {
        return donGia;
    }

    /**
     * Đặt đơn giá trong ChiTietDonHang.
     *
     * @param donGia đơn giá
     */
    public void datDonGia(BigDecimal donGia) {
        this.donGia = donGia;
        tinhThanhTien();
    }

    /**
     * Lấy tiền giảm giá trong ChiTietDonHang.
     *
     * @return kết quả lấy tiền giảm giá
     */
    public BigDecimal layTienGiamGia() {
        return tienGiamGia;
    }

    /**
     * Đặt tiền giảm giá trong ChiTietDonHang.
     *
     * @param tienGiamGia tiền giảm giá
     */
    public void datTienGiamGia(BigDecimal tienGiamGia) {
        this.tienGiamGia = tienGiamGia;
        tinhThanhTien();
    }

    /**
     * Lấy thành tiền trong ChiTietDonHang.
     *
     * @return kết quả lấy thành tiền
     */
    public BigDecimal layThanhTien() {
        return thanhTien;
    }

    /**
     * Đặt thành tiền trong ChiTietDonHang.
     *
     * @param thanhTien thành tiền
     */
    public void datThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    /**
     * Lấy ghi chú trong ChiTietDonHang.
     *
     * @return kết quả lấy ghi chú
     */
    public String layGhiChu() {
        return ghiChu;
    }

    /**
     * Đặt ghi chú trong ChiTietDonHang.
     *
     * @param ghiChu ghi chú
     */
    public void datGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Lấy phiên bản bản ghi trong ChiTietDonHang.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong ChiTietDonHang.
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
        return "InvoiceItem{"
                + "id="
                + id
                + ", invoiceId="
                + idHoaDon
                + ", productId="
                + idSanPham
                + ", quantity="
                + soLuong
                + ", unitPrice="
                + donGia
                + ", lineTotal="
                + thanhTien
                + '}';
    }
}
