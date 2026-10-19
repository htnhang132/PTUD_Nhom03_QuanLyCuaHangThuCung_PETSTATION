package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng chot_ngay trong SQL Server. */
public class ChotNgay {
    /** Id. */
    private Long id;

    /** Ngày chốt. */
    private LocalDate ngayChot;

    /** Người khóa. */
    private Long nguoiKhoa;

    /** Thời điểm khóa. */
    private LocalDateTime thoiDiemKhoa;

    /** Tổng doanh thu. */
    private BigDecimal tongDoanhThu;

    /** Tổng tiền mặt. */
    private BigDecimal tongTienMat;

    /** Tổng tiền chuyển khoản. */
    private BigDecimal tongTienChuyenKhoan;

    /** Tổng tiền thẻ. */
    private BigDecimal tongTienThe;

    /** Số lượng hóa đơn. */
    private Integer soLuongHoaDon;

    /** Ghi chú. */
    private String ghiChu;

    /** Trạng thái. */
    private String trangThai; // LOCKED, REOPENED

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo ChotNgay với các giá trị được cung cấp. */
    public ChotNgay() {
        this.trangThai = "LOCKED";
        this.tongDoanhThu = BigDecimal.ZERO;
        this.tongTienMat = BigDecimal.ZERO;
        this.tongTienChuyenKhoan = BigDecimal.ZERO;
        this.tongTienThe = BigDecimal.ZERO;
        this.soLuongHoaDon = 0;
    }

    /**
     * Khởi tạo ChotNgay với các giá trị được cung cấp.
     *
     * @param id id
     * @param ngayChot ngày chốt
     * @param nguoiKhoa người khóa
     */
    public ChotNgay(Long id, LocalDate ngayChot, Long nguoiKhoa) {
        this();
        this.id = id;
        this.ngayChot = ngayChot;
        this.nguoiKhoa = nguoiKhoa;
        this.thoiDiemKhoa = LocalDateTime.now();
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong ChotNgay.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong ChotNgay.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy ngày chốt trong ChotNgay.
     *
     * @return kết quả lấy ngày chốt
     */
    public LocalDate layNgayChot() {
        return ngayChot;
    }

    /**
     * Đặt ngày chốt trong ChotNgay.
     *
     * @param ngayChot ngày chốt
     */
    public void datNgayChot(LocalDate ngayChot) {
        this.ngayChot = ngayChot;
    }

    /**
     * Lấy người khóa trong ChotNgay.
     *
     * @return kết quả lấy người khóa
     */
    public Long layNguoiKhoa() {
        return nguoiKhoa;
    }

    /**
     * Đặt người khóa trong ChotNgay.
     *
     * @param nguoiKhoa người khóa
     */
    public void datNguoiKhoa(Long nguoiKhoa) {
        this.nguoiKhoa = nguoiKhoa;
    }

    /**
     * Lấy thời điểm khóa trong ChotNgay.
     *
     * @return kết quả lấy thời điểm khóa
     */
    public LocalDateTime layThoiDiemKhoa() {
        return thoiDiemKhoa;
    }

    /**
     * Đặt thời điểm khóa trong ChotNgay.
     *
     * @param thoiDiemKhoa thời điểm khóa
     */
    public void datThoiDiemKhoa(LocalDateTime thoiDiemKhoa) {
        this.thoiDiemKhoa = thoiDiemKhoa;
    }

    /**
     * Lấy tổng doanh thu trong ChotNgay.
     *
     * @return kết quả lấy tổng doanh thu
     */
    public BigDecimal layTongDoanhThu() {
        return tongDoanhThu;
    }

    /**
     * Đặt tổng doanh thu trong ChotNgay.
     *
     * @param tongDoanhThu tổng doanh thu
     */
    public void datTongDoanhThu(BigDecimal tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }

    /**
     * Lấy tổng tiền mặt trong ChotNgay.
     *
     * @return kết quả lấy tổng tiền mặt
     */
    public BigDecimal layTongTienMat() {
        return tongTienMat;
    }

    /**
     * Đặt tổng tiền mặt trong ChotNgay.
     *
     * @param tongTienMat tổng tiền mặt
     */
    public void datTongTienMat(BigDecimal tongTienMat) {
        this.tongTienMat = tongTienMat;
    }

    /**
     * Lấy tổng tiền chuyển khoản trong ChotNgay.
     *
     * @return kết quả lấy tổng tiền chuyển khoản
     */
    public BigDecimal layTongTienChuyenKhoan() {
        return tongTienChuyenKhoan;
    }

    /**
     * Đặt tổng tiền chuyển khoản trong ChotNgay.
     *
     * @param tongTienChuyenKhoan tổng tiền chuyển khoản
     */
    public void datTongTienChuyenKhoan(BigDecimal tongTienChuyenKhoan) {
        this.tongTienChuyenKhoan = tongTienChuyenKhoan;
    }

    /**
     * Lấy tổng tiền thẻ trong ChotNgay.
     *
     * @return kết quả lấy tổng tiền thẻ
     */
    public BigDecimal layTongTienThe() {
        return tongTienThe;
    }

    /**
     * Đặt tổng tiền thẻ trong ChotNgay.
     *
     * @param tongTienThe tổng tiền thẻ
     */
    public void datTongTienThe(BigDecimal tongTienThe) {
        this.tongTienThe = tongTienThe;
    }

    /**
     * Lấy số lượng hóa đơn trong ChotNgay.
     *
     * @return kết quả lấy số lượng hóa đơn
     */
    public Integer laySoLuongHoaDon() {
        return soLuongHoaDon;
    }

    /**
     * Đặt số lượng hóa đơn trong ChotNgay.
     *
     * @param soLuongHoaDon số lượng hóa đơn
     */
    public void datSoLuongHoaDon(Integer soLuongHoaDon) {
        this.soLuongHoaDon = soLuongHoaDon;
    }

    /**
     * Lấy ghi chú trong ChotNgay.
     *
     * @return kết quả lấy ghi chú
     */
    public String layGhiChu() {
        return ghiChu;
    }

    /**
     * Đặt ghi chú trong ChotNgay.
     *
     * @param ghiChu ghi chú
     */
    public void datGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Lấy trạng thái trong ChotNgay.
     *
     * @return kết quả lấy trạng thái
     */
    public String layTrangThai() {
        return trangThai;
    }

    /**
     * Đặt trạng thái trong ChotNgay.
     *
     * @param trangThai trạng thái
     */
    public void datTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Lấy phiên bản bản ghi trong ChotNgay.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong ChotNgay.
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
        return "DayLock{"
                + "id="
                + id
                + ", lockDate="
                + ngayChot
                + ", totalRevenue="
                + tongDoanhThu
                + ", invoiceCount="
                + soLuongHoaDon
                + ", status='"
                + trangThai
                + '\''
                + '}';
    }
}
