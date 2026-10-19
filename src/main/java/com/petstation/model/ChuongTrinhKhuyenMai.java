package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng chuong_trinh_khuyen_mai trong SQL Server. */
public class ChuongTrinhKhuyenMai {
    /** Id. */
    private Long id;

    /** Mã khuyến mãi. */
    private String maKhuyenMai;

    /** Tên khuyến mãi. */
    private String tenKhuyenMai;

    /** Kiểu giảm giá. */
    private String kieuGiamGia; // PERCENTAGE, FIXED_AMOUNT

    /** Giá trị giảm giá. */
    private BigDecimal giaTriGiamGia;

    /** Ngày bắt đầu. */
    private LocalDateTime ngayBatDau;

    /** Ngày kết thúc. */
    private LocalDateTime ngayKetThuc;

    /** Áp dụng được phạm vi. */
    private String apDungDuocPhamVi; // ALL_PRODUCTS, CATEGORY, SPECIFIC_PRODUCTS

    /** Trạng thái. */
    private String trangThai; // ACTIVE, INACTIVE, EXPIRED

    /** Mô tả. */
    private String moTa;

    /** Đã tạo theo. */
    private Long daTaoTheo;

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo ChuongTrinhKhuyenMai với các giá trị được cung cấp. */
    public ChuongTrinhKhuyenMai() {
        this.trangThai = "ACTIVE";
    }

    /**
     * Khởi tạo ChuongTrinhKhuyenMai với các giá trị được cung cấp.
     *
     * @param id id
     * @param maKhuyenMai mã khuyến mãi
     * @param tenKhuyenMai tên khuyến mãi
     * @param kieuGiamGia kiểu giảm giá
     * @param giaTriGiamGia giá trị giảm giá
     */
    public ChuongTrinhKhuyenMai(
            Long id,
            String maKhuyenMai,
            String tenKhuyenMai,
            String kieuGiamGia,
            BigDecimal giaTriGiamGia) {
        this();
        this.id = id;
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.kieuGiamGia = kieuGiamGia;
        this.giaTriGiamGia = giaTriGiamGia;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong ChuongTrinhKhuyenMai.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã khuyến mãi trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy mã khuyến mãi
     */
    public String layMaKhuyenMai() {
        return maKhuyenMai;
    }

    /**
     * Đặt mã khuyến mãi trong ChuongTrinhKhuyenMai.
     *
     * @param maKhuyenMai mã khuyến mãi
     */
    public void datMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    /**
     * Lấy tên khuyến mãi trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy tên khuyến mãi
     */
    public String layTenKhuyenMai() {
        return tenKhuyenMai;
    }

    /**
     * Đặt tên khuyến mãi trong ChuongTrinhKhuyenMai.
     *
     * @param tenKhuyenMai tên khuyến mãi
     */
    public void datTenKhuyenMai(String tenKhuyenMai) {
        this.tenKhuyenMai = tenKhuyenMai;
    }

    /**
     * Lấy kiểu giảm giá trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy kiểu giảm giá
     */
    public String layKieuGiamGia() {
        return kieuGiamGia;
    }

    /**
     * Đặt kiểu giảm giá trong ChuongTrinhKhuyenMai.
     *
     * @param kieuGiamGia kiểu giảm giá
     */
    public void datKieuGiamGia(String kieuGiamGia) {
        this.kieuGiamGia = kieuGiamGia;
    }

    /**
     * Lấy giá trị giảm giá trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy giá trị giảm giá
     */
    public BigDecimal layGiaTriGiamGia() {
        return giaTriGiamGia;
    }

    /**
     * Đặt giá trị giảm giá trong ChuongTrinhKhuyenMai.
     *
     * @param giaTriGiamGia giá trị giảm giá
     */
    public void datGiaTriGiamGia(BigDecimal giaTriGiamGia) {
        this.giaTriGiamGia = giaTriGiamGia;
    }

    /**
     * Lấy ngày bắt đầu trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy ngày bắt đầu
     */
    public LocalDateTime layNgayBatDau() {
        return ngayBatDau;
    }

    /**
     * Đặt ngày bắt đầu trong ChuongTrinhKhuyenMai.
     *
     * @param ngayBatDau ngày bắt đầu
     */
    public void datNgayBatDau(LocalDateTime ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    /**
     * Lấy ngày kết thúc trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy ngày kết thúc
     */
    public LocalDateTime layNgayKetThuc() {
        return ngayKetThuc;
    }

    /**
     * Đặt ngày kết thúc trong ChuongTrinhKhuyenMai.
     *
     * @param ngayKetThuc ngày kết thúc
     */
    public void datNgayKetThuc(LocalDateTime ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    /**
     * Lấy áp dụng được phạm vi trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy áp dụng được phạm vi
     */
    public String layApDungDuocPhamVi() {
        return apDungDuocPhamVi;
    }

    /**
     * Đặt áp dụng được phạm vi trong ChuongTrinhKhuyenMai.
     *
     * @param apDungDuocPhamVi áp dụng được phạm vi
     */
    public void datApDungDuocPhamVi(String apDungDuocPhamVi) {
        this.apDungDuocPhamVi = apDungDuocPhamVi;
    }

    /**
     * Lấy trạng thái trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy trạng thái
     */
    public String layTrangThai() {
        return trangThai;
    }

    /**
     * Đặt trạng thái trong ChuongTrinhKhuyenMai.
     *
     * @param trangThai trạng thái
     */
    public void datTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Lấy mô tả trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy mô tả
     */
    public String layMoTa() {
        return moTa;
    }

    /**
     * Đặt mô tả trong ChuongTrinhKhuyenMai.
     *
     * @param moTa mô tả
     */
    public void datMoTa(String moTa) {
        this.moTa = moTa;
    }

    /**
     * Lấy đã tạo theo trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy đã tạo theo
     */
    public Long layDaTaoTheo() {
        return daTaoTheo;
    }

    /**
     * Đặt đã tạo theo trong ChuongTrinhKhuyenMai.
     *
     * @param daTaoTheo đã tạo theo
     */
    public void datDaTaoTheo(Long daTaoTheo) {
        this.daTaoTheo = daTaoTheo;
    }

    /**
     * Lấy thời điểm tạo trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong ChuongTrinhKhuyenMai.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong ChuongTrinhKhuyenMai.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong ChuongTrinhKhuyenMai.
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
        return "Promotion{"
                + "id="
                + id
                + ", promotionCode='"
                + maKhuyenMai
                + '\''
                + ", promotionName='"
                + tenKhuyenMai
                + '\''
                + ", discountType='"
                + kieuGiamGia
                + '\''
                + ", discountValue="
                + giaTriGiamGia
                + ", status='"
                + trangThai
                + '\''
                + '}';
    }
}
