package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng hoa_don trong SQL Server. */
public class HoaDon {
    /** Id. */
    private Long id;

    /** Mã hóa đơn. */
    private String maHoaDon;

    /** Id khách hàng. */
    private Long idKhachHang;

    /** Id nhân viên. */
    private Long idNhanVien;

    /** Id phiên ca. */
    private Long idPhienCa;

    /** Ngày lập hóa đơn. */
    private LocalDateTime ngayLapHoaDon;

    /** Tạm tính. */
    private BigDecimal tamTinh;

    /** Tiền giảm giá. */
    private BigDecimal tienGiamGia;

    /** Tiền giảm từ điểm. */
    private BigDecimal tienGiamTuDiem;

    /** Tiền thuế. */
    private BigDecimal tienThue;

    /** Tổng tiền. */
    private BigDecimal tongTien;

    /** Khách hàng đã trả. */
    private BigDecimal khachHangDaTra;

    /** Số tiền thay đổi. */
    private BigDecimal soTienThayDoi;

    /** Phương thức thanh toán. */
    private String phuongThucThanhToan; // CASH, BANK_TRANSFER, CARD, MIXED

    /** Loại hóa đơn. */
    private String loaiHoaDon; // RETAIL, SPA, HOTEL

    /** Trạng thái. */
    private String trangThai; // DRAFT, COMPLETED, CANCELLED, REFUNDED

    /** Ghi chú. */
    private String ghiChu;

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo HoaDon với các giá trị được cung cấp. */
    public HoaDon() {
        this.trangThai = "DRAFT";
        this.phuongThucThanhToan = "CASH";
        this.tienGiamTuDiem = BigDecimal.ZERO;
    }

    /**
     * Khởi tạo HoaDon với các giá trị được cung cấp.
     *
     * @param id id
     * @param maHoaDon mã hóa đơn
     * @param idKhachHang id khách hàng
     * @param idNhanVien id nhân viên
     */
    public HoaDon(Long id, String maHoaDon, Long idKhachHang, Long idNhanVien) {
        this();
        this.id = id;
        this.maHoaDon = maHoaDon;
        this.idKhachHang = idKhachHang;
        this.idNhanVien = idNhanVien;
        this.ngayLapHoaDon = LocalDateTime.now();
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong HoaDon.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong HoaDon.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã hóa đơn trong HoaDon.
     *
     * @return kết quả lấy mã hóa đơn
     */
    public String layMaHoaDon() {
        return maHoaDon;
    }

    /**
     * Đặt mã hóa đơn trong HoaDon.
     *
     * @param maHoaDon mã hóa đơn
     */
    public void datMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    /**
     * Lấy id khách hàng trong HoaDon.
     *
     * @return kết quả lấy id khách hàng
     */
    public Long layIdKhachHang() {
        return idKhachHang;
    }

    /**
     * Đặt id khách hàng trong HoaDon.
     *
     * @param idKhachHang id khách hàng
     */
    public void datIdKhachHang(Long idKhachHang) {
        this.idKhachHang = idKhachHang;
    }

    /**
     * Lấy id nhân viên trong HoaDon.
     *
     * @return kết quả lấy id nhân viên
     */
    public Long layIdNhanVien() {
        return idNhanVien;
    }

    /**
     * Đặt id nhân viên trong HoaDon.
     *
     * @param idNhanVien id nhân viên
     */
    public void datIdNhanVien(Long idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    /**
     * Lấy id phiên ca trong HoaDon.
     *
     * @return kết quả lấy id phiên ca
     */
    public Long layIdPhienCa() {
        return idPhienCa;
    }

    /**
     * Đặt id phiên ca trong HoaDon.
     *
     * @param idPhienCa id phiên ca
     */
    public void datIdPhienCa(Long idPhienCa) {
        this.idPhienCa = idPhienCa;
    }

    /**
     * Lấy ngày lập hóa đơn trong HoaDon.
     *
     * @return kết quả lấy ngày lập hóa đơn
     */
    public LocalDateTime layNgayLapHoaDon() {
        return ngayLapHoaDon;
    }

    /**
     * Đặt ngày lập hóa đơn trong HoaDon.
     *
     * @param ngayLapHoaDon ngày lập hóa đơn
     */
    public void datNgayLapHoaDon(LocalDateTime ngayLapHoaDon) {
        this.ngayLapHoaDon = ngayLapHoaDon;
    }

    /**
     * Lấy tạm tính trong HoaDon.
     *
     * @return kết quả lấy tạm tính
     */
    public BigDecimal layTamTinh() {
        return tamTinh;
    }

    /**
     * Đặt tạm tính trong HoaDon.
     *
     * @param tamTinh tạm tính
     */
    public void datTamTinh(BigDecimal tamTinh) {
        this.tamTinh = tamTinh;
    }

    /**
     * Lấy tiền giảm giá trong HoaDon.
     *
     * @return kết quả lấy tiền giảm giá
     */
    public BigDecimal layTienGiamGia() {
        return tienGiamGia;
    }

    /**
     * Đặt tiền giảm giá trong HoaDon.
     *
     * @param tienGiamGia tiền giảm giá
     */
    public void datTienGiamGia(BigDecimal tienGiamGia) {
        this.tienGiamGia = tienGiamGia;
    }

    /**
     * Lấy tiền giảm từ điểm trong HoaDon.
     *
     * @return kết quả lấy tiền giảm từ điểm
     */
    public BigDecimal layTienGiamTuDiem() {
        return tienGiamTuDiem;
    }

    /**
     * Đặt tiền giảm từ điểm trong HoaDon.
     *
     * @param tienGiamTuDiem tiền giảm từ điểm
     */
    public void datTienGiamTuDiem(BigDecimal tienGiamTuDiem) {
        this.tienGiamTuDiem = tienGiamTuDiem;
    }

    /**
     * Lấy tiền thuế trong HoaDon.
     *
     * @return kết quả lấy tiền thuế
     */
    public BigDecimal layTienThue() {
        return tienThue;
    }

    /**
     * Đặt tiền thuế trong HoaDon.
     *
     * @param tienThue tiền thuế
     */
    public void datTienThue(BigDecimal tienThue) {
        this.tienThue = tienThue;
    }

    /**
     * Lấy tổng tiền trong HoaDon.
     *
     * @return kết quả lấy tổng tiền
     */
    public BigDecimal layTongTien() {
        return tongTien;
    }

    /**
     * Đặt tổng tiền trong HoaDon.
     *
     * @param tongTien tổng tiền
     */
    public void datTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    /**
     * Lấy khách hàng đã trả trong HoaDon.
     *
     * @return kết quả lấy khách hàng đã trả
     */
    public BigDecimal layKhachHangDaTra() {
        return khachHangDaTra;
    }

    /**
     * Đặt khách hàng đã trả trong HoaDon.
     *
     * @param khachHangDaTra khách hàng đã trả
     */
    public void datKhachHangDaTra(BigDecimal khachHangDaTra) {
        this.khachHangDaTra = khachHangDaTra;
    }

    /**
     * Lấy số tiền thay đổi trong HoaDon.
     *
     * @return kết quả lấy số tiền thay đổi
     */
    public BigDecimal laySoTienThayDoi() {
        return soTienThayDoi;
    }

    /**
     * Đặt số tiền thay đổi trong HoaDon.
     *
     * @param soTienThayDoi số tiền thay đổi
     */
    public void datSoTienThayDoi(BigDecimal soTienThayDoi) {
        this.soTienThayDoi = soTienThayDoi;
    }

    /**
     * Lấy phương thức thanh toán trong HoaDon.
     *
     * @return kết quả lấy phương thức thanh toán
     */
    public String layPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    /**
     * Đặt phương thức thanh toán trong HoaDon.
     *
     * @param phuongThucThanhToan phương thức thanh toán
     */
    public void datPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    /**
     * Lấy loại hóa đơn trong HoaDon.
     *
     * @return kết quả lấy loại hóa đơn
     */
    public String layLoaiHoaDon() {
        return loaiHoaDon;
    }

    /**
     * Đặt loại hóa đơn trong HoaDon.
     *
     * @param loaiHoaDon loại hóa đơn
     */
    public void datLoaiHoaDon(String loaiHoaDon) {
        this.loaiHoaDon = loaiHoaDon;
    }

    /**
     * Lấy trạng thái trong HoaDon.
     *
     * @return kết quả lấy trạng thái
     */
    public String layTrangThai() {
        return trangThai;
    }

    /**
     * Đặt trạng thái trong HoaDon.
     *
     * @param trangThai trạng thái
     */
    public void datTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Lấy ghi chú trong HoaDon.
     *
     * @return kết quả lấy ghi chú
     */
    public String layGhiChu() {
        return ghiChu;
    }

    /**
     * Đặt ghi chú trong HoaDon.
     *
     * @param ghiChu ghi chú
     */
    public void datGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Lấy thời điểm tạo trong HoaDon.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong HoaDon.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong HoaDon.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong HoaDon.
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
        return "Invoice{"
                + "id="
                + id
                + ", invoiceCode='"
                + maHoaDon
                + '\''
                + ", customerId="
                + idKhachHang
                + ", totalAmount="
                + tongTien
                + ", paymentMethod='"
                + phuongThucThanhToan
                + '\''
                + ", status='"
                + trangThai
                + '\''
                + '}';
    }
}
