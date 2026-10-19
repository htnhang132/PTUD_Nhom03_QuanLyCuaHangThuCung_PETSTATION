package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng voucher trong SQL Server. */
public class Voucher {
    /** Id. */
    private Long id;

    /** Mã voucher. */
    private String maVoucher;

    /** Loại voucher. */
    private String loaiVoucher; // CAMPAIGN, LOYALTY_REWARD

    /** Giá trị giảm giá. */
    private BigDecimal giaTriGiamGia;

    /** Kiểu giảm giá. */
    private String kieuGiamGia; // PERCENTAGE, FIXED_AMOUNT

    /** Giá trị đơn hàng tối thiểu. */
    private BigDecimal giaTriDonHangToiThieu;

    /** Số tiền giảm tối đa. */
    private BigDecimal soTienGiamToiDa;

    /** Ngày bắt đầu hiệu lực. */
    private LocalDateTime ngayBatDauHieuLuc;

    /** Ngày hết hiệu lực. */
    private LocalDateTime ngayHetHieuLuc;

    /** Giới hạn sử dụng. */
    private Integer gioiHanSuDung;

    /** Số lượng đã dùng. */
    private Integer soLuongDaDung;

    /** Trạng thái. */
    private String trangThai; // ACTIVE, USED, EXPIRED, CANCELLED

    /** Id khách hàng. */
    private Long idKhachHang; // Dùng để xác định khách hàng nhận thưởng tích điểm

    /** Áp dụng Spa chỉ. */
    private Boolean apDungSpaChi;

    /** Ngày sử dụng. */
    private LocalDateTime ngaySuDung;

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo Voucher với các giá trị được cung cấp. */
    public Voucher() {
        this.soLuongDaDung = 0;
        this.trangThai = "ACTIVE";
        this.apDungSpaChi = false;
    }

    /**
     * Khởi tạo Voucher với các giá trị được cung cấp.
     *
     * @param id id
     * @param maVoucher mã voucher
     * @param loaiVoucher loại voucher
     * @param giaTriGiamGia giá trị giảm giá
     * @param kieuGiamGia kiểu giảm giá
     */
    public Voucher(
            Long id,
            String maVoucher,
            String loaiVoucher,
            BigDecimal giaTriGiamGia,
            String kieuGiamGia) {
        this();
        this.id = id;
        this.maVoucher = maVoucher;
        this.loaiVoucher = loaiVoucher;
        this.giaTriGiamGia = giaTriGiamGia;
        this.kieuGiamGia = kieuGiamGia;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong Voucher.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong Voucher.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã voucher trong Voucher.
     *
     * @return kết quả lấy mã voucher
     */
    public String layMaVoucher() {
        return maVoucher;
    }

    /**
     * Đặt mã voucher trong Voucher.
     *
     * @param maVoucher mã voucher
     */
    public void datMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }

    /**
     * Lấy loại voucher trong Voucher.
     *
     * @return kết quả lấy loại voucher
     */
    public String layLoaiVoucher() {
        return loaiVoucher;
    }

    /**
     * Đặt loại voucher trong Voucher.
     *
     * @param loaiVoucher loại voucher
     */
    public void datLoaiVoucher(String loaiVoucher) {
        this.loaiVoucher = loaiVoucher;
    }

    /**
     * Lấy giá trị giảm giá trong Voucher.
     *
     * @return kết quả lấy giá trị giảm giá
     */
    public BigDecimal layGiaTriGiamGia() {
        return giaTriGiamGia;
    }

    /**
     * Đặt giá trị giảm giá trong Voucher.
     *
     * @param giaTriGiamGia giá trị giảm giá
     */
    public void datGiaTriGiamGia(BigDecimal giaTriGiamGia) {
        this.giaTriGiamGia = giaTriGiamGia;
    }

    /**
     * Lấy kiểu giảm giá trong Voucher.
     *
     * @return kết quả lấy kiểu giảm giá
     */
    public String layKieuGiamGia() {
        return kieuGiamGia;
    }

    /**
     * Đặt kiểu giảm giá trong Voucher.
     *
     * @param kieuGiamGia kiểu giảm giá
     */
    public void datKieuGiamGia(String kieuGiamGia) {
        this.kieuGiamGia = kieuGiamGia;
    }

    /**
     * Lấy giá trị đơn hàng tối thiểu trong Voucher.
     *
     * @return kết quả lấy giá trị đơn hàng tối thiểu
     */
    public BigDecimal layGiaTriDonHangToiThieu() {
        return giaTriDonHangToiThieu;
    }

    /**
     * Đặt giá trị đơn hàng tối thiểu trong Voucher.
     *
     * @param giaTriDonHangToiThieu giá trị đơn hàng tối thiểu
     */
    public void datGiaTriDonHangToiThieu(BigDecimal giaTriDonHangToiThieu) {
        this.giaTriDonHangToiThieu = giaTriDonHangToiThieu;
    }

    /**
     * Lấy số tiền giảm tối đa trong Voucher.
     *
     * @return kết quả lấy số tiền giảm tối đa
     */
    public BigDecimal laySoTienGiamToiDa() {
        return soTienGiamToiDa;
    }

    /**
     * Đặt số tiền giảm tối đa trong Voucher.
     *
     * @param soTienGiamToiDa số tiền giảm tối đa
     */
    public void datSoTienGiamToiDa(BigDecimal soTienGiamToiDa) {
        this.soTienGiamToiDa = soTienGiamToiDa;
    }

    /**
     * Lấy ngày bắt đầu hiệu lực trong Voucher.
     *
     * @return kết quả lấy ngày bắt đầu hiệu lực
     */
    public LocalDateTime layNgayBatDauHieuLuc() {
        return ngayBatDauHieuLuc;
    }

    /**
     * Đặt ngày bắt đầu hiệu lực trong Voucher.
     *
     * @param ngayBatDauHieuLuc ngày bắt đầu hiệu lực
     */
    public void datNgayBatDauHieuLuc(LocalDateTime ngayBatDauHieuLuc) {
        this.ngayBatDauHieuLuc = ngayBatDauHieuLuc;
    }

    /**
     * Lấy ngày hết hiệu lực trong Voucher.
     *
     * @return kết quả lấy ngày hết hiệu lực
     */
    public LocalDateTime layNgayHetHieuLuc() {
        return ngayHetHieuLuc;
    }

    /**
     * Đặt ngày hết hiệu lực trong Voucher.
     *
     * @param ngayHetHieuLuc ngày hết hiệu lực
     */
    public void datNgayHetHieuLuc(LocalDateTime ngayHetHieuLuc) {
        this.ngayHetHieuLuc = ngayHetHieuLuc;
    }

    /**
     * Lấy giới hạn sử dụng trong Voucher.
     *
     * @return kết quả lấy giới hạn sử dụng
     */
    public Integer layGioiHanSuDung() {
        return gioiHanSuDung;
    }

    /**
     * Đặt giới hạn sử dụng trong Voucher.
     *
     * @param gioiHanSuDung giới hạn sử dụng
     */
    public void datGioiHanSuDung(Integer gioiHanSuDung) {
        this.gioiHanSuDung = gioiHanSuDung;
    }

    /**
     * Lấy số lượng đã dùng trong Voucher.
     *
     * @return kết quả lấy số lượng đã dùng
     */
    public Integer laySoLuongDaDung() {
        return soLuongDaDung;
    }

    /**
     * Đặt số lượng đã dùng trong Voucher.
     *
     * @param soLuongDaDung số lượng đã dùng
     */
    public void datSoLuongDaDung(Integer soLuongDaDung) {
        this.soLuongDaDung = soLuongDaDung;
    }

    /**
     * Lấy trạng thái trong Voucher.
     *
     * @return kết quả lấy trạng thái
     */
    public String layTrangThai() {
        return trangThai;
    }

    /**
     * Đặt trạng thái trong Voucher.
     *
     * @param trangThai trạng thái
     */
    public void datTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Lấy id khách hàng trong Voucher.
     *
     * @return kết quả lấy id khách hàng
     */
    public Long layIdKhachHang() {
        return idKhachHang;
    }

    /**
     * Đặt id khách hàng trong Voucher.
     *
     * @param idKhachHang id khách hàng
     */
    public void datIdKhachHang(Long idKhachHang) {
        this.idKhachHang = idKhachHang;
    }

    /**
     * Lấy áp dụng Spa chỉ trong Voucher.
     *
     * @return kết quả lấy áp dụng Spa chỉ
     */
    public Boolean layApDungSpaChi() {
        return apDungSpaChi;
    }

    /**
     * Đặt áp dụng Spa chỉ trong Voucher.
     *
     * @param apDungSpaChi áp dụng Spa chỉ
     */
    public void datApDungSpaChi(Boolean apDungSpaChi) {
        this.apDungSpaChi = apDungSpaChi;
    }

    /**
     * Lấy ngày sử dụng trong Voucher.
     *
     * @return kết quả lấy ngày sử dụng
     */
    public LocalDateTime layNgaySuDung() {
        return ngaySuDung;
    }

    /**
     * Đặt ngày sử dụng trong Voucher.
     *
     * @param ngaySuDung ngày sử dụng
     */
    public void datNgaySuDung(LocalDateTime ngaySuDung) {
        this.ngaySuDung = ngaySuDung;
    }

    /**
     * Lấy thời điểm tạo trong Voucher.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong Voucher.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong Voucher.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong Voucher.
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
        return "Voucher{"
                + "id="
                + id
                + ", voucherCode='"
                + maVoucher
                + '\''
                + ", voucherType='"
                + loaiVoucher
                + '\''
                + ", discountValue="
                + giaTriGiamGia
                + ", status='"
                + trangThai
                + '\''
                + ", customerId="
                + idKhachHang
                + '}';
    }
}
