package com.petstation.model;

import java.math.BigDecimal;

/** Biểu diễn dữ liệu ánh xạ với bảng chi_tiet_phieu_spa trong SQL Server. */
public class ChiTietPhieuSpa {
    /** Id. */
    private Long id;

    /** Id phiếu tiếp nhận Spa. */
    private Long idPhieuTiepNhanSpa;

    /** Id gói dịch vụ. */
    private Long idGoiDichVu; // Để NULL đối với phụ thu

    /** Kiểu chi tiết. */
    private String kieuChiTiet; // SERVICE, SURCHARGE

    /** Mô tả. */
    private String moTa;

    /** Đơn giá. */
    private BigDecimal donGia;

    /** Số lượng. */
    private Integer soLuong;

    /** Thành tiền. */
    private BigDecimal thanhTien;

    /** Ghi chú. */
    private String ghiChu;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo ChiTietPhieuSpa với các giá trị được cung cấp. */
    public ChiTietPhieuSpa() {
        this.soLuong = 1;
    }

    /**
     * Khởi tạo ChiTietPhieuSpa với các giá trị được cung cấp.
     *
     * @param id id
     * @param idPhieuTiepNhanSpa id phiếu tiếp nhận Spa
     * @param kieuChiTiet kiểu chi tiết
     * @param moTa mô tả
     * @param donGia đơn giá
     */
    public ChiTietPhieuSpa(
            Long id, Long idPhieuTiepNhanSpa, String kieuChiTiet, String moTa, BigDecimal donGia) {
        this();
        this.id = id;
        this.idPhieuTiepNhanSpa = idPhieuTiepNhanSpa;
        this.kieuChiTiet = kieuChiTiet;
        this.moTa = moTa;
        this.donGia = donGia;
        tinhThanhTien();
    }

    // Xử lý nghiệp vụ
    /** Tính thành tiền trong ChiTietPhieuSpa. */
    public void tinhThanhTien() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (donGia != null && soLuong != null) {
            this.thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong ChiTietPhieuSpa.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong ChiTietPhieuSpa.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy id phiếu tiếp nhận Spa trong ChiTietPhieuSpa.
     *
     * @return kết quả lấy id phiếu tiếp nhận Spa
     */
    public Long layIdPhieuTiepNhanSpa() {
        return idPhieuTiepNhanSpa;
    }

    /**
     * Đặt id phiếu tiếp nhận Spa trong ChiTietPhieuSpa.
     *
     * @param idPhieuTiepNhanSpa id phiếu tiếp nhận Spa
     */
    public void datIdPhieuTiepNhanSpa(Long idPhieuTiepNhanSpa) {
        this.idPhieuTiepNhanSpa = idPhieuTiepNhanSpa;
    }

    /**
     * Lấy id gói dịch vụ trong ChiTietPhieuSpa.
     *
     * @return kết quả lấy id gói dịch vụ
     */
    public Long layIdGoiDichVu() {
        return idGoiDichVu;
    }

    /**
     * Đặt id gói dịch vụ trong ChiTietPhieuSpa.
     *
     * @param idGoiDichVu id gói dịch vụ
     */
    public void datIdGoiDichVu(Long idGoiDichVu) {
        this.idGoiDichVu = idGoiDichVu;
    }

    /**
     * Lấy kiểu chi tiết trong ChiTietPhieuSpa.
     *
     * @return kết quả lấy kiểu chi tiết
     */
    public String layKieuChiTiet() {
        return kieuChiTiet;
    }

    /**
     * Đặt kiểu chi tiết trong ChiTietPhieuSpa.
     *
     * @param kieuChiTiet kiểu chi tiết
     */
    public void datKieuChiTiet(String kieuChiTiet) {
        this.kieuChiTiet = kieuChiTiet;
    }

    /**
     * Lấy mô tả trong ChiTietPhieuSpa.
     *
     * @return kết quả lấy mô tả
     */
    public String layMoTa() {
        return moTa;
    }

    /**
     * Đặt mô tả trong ChiTietPhieuSpa.
     *
     * @param moTa mô tả
     */
    public void datMoTa(String moTa) {
        this.moTa = moTa;
    }

    /**
     * Lấy đơn giá trong ChiTietPhieuSpa.
     *
     * @return kết quả lấy đơn giá
     */
    public BigDecimal layDonGia() {
        return donGia;
    }

    /**
     * Đặt đơn giá trong ChiTietPhieuSpa.
     *
     * @param donGia đơn giá
     */
    public void datDonGia(BigDecimal donGia) {
        this.donGia = donGia;
        tinhThanhTien();
    }

    /**
     * Lấy số lượng trong ChiTietPhieuSpa.
     *
     * @return kết quả lấy số lượng
     */
    public Integer laySoLuong() {
        return soLuong;
    }

    /**
     * Đặt số lượng trong ChiTietPhieuSpa.
     *
     * @param soLuong số lượng
     */
    public void datSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
        tinhThanhTien();
    }

    /**
     * Lấy thành tiền trong ChiTietPhieuSpa.
     *
     * @return kết quả lấy thành tiền
     */
    public BigDecimal layThanhTien() {
        return thanhTien;
    }

    /**
     * Đặt thành tiền trong ChiTietPhieuSpa.
     *
     * @param thanhTien thành tiền
     */
    public void datThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    /**
     * Lấy ghi chú trong ChiTietPhieuSpa.
     *
     * @return kết quả lấy ghi chú
     */
    public String layGhiChu() {
        return ghiChu;
    }

    /**
     * Đặt ghi chú trong ChiTietPhieuSpa.
     *
     * @param ghiChu ghi chú
     */
    public void datGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Lấy phiên bản bản ghi trong ChiTietPhieuSpa.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong ChiTietPhieuSpa.
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
        return "SpaTicketDetail{"
                + "id="
                + id
                + ", spaTicketId="
                + idPhieuTiepNhanSpa
                + ", detailType='"
                + kieuChiTiet
                + '\''
                + ", description='"
                + moTa
                + '\''
                + ", lineTotal="
                + thanhTien
                + '}';
    }
}
