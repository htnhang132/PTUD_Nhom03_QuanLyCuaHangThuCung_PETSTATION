package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng lich_su_gia trong SQL Server. */
public class LichSuGia {
    /** Id. */
    private Long id;

    /** Id sản phẩm. */
    private Long idSanPham;

    /** Giá cũ. */
    private BigDecimal giaCu;

    /** Giá mới. */
    private BigDecimal giaMoi;

    /** Lý do. */
    private String lyDo;

    /** Đã thay đổi theo. */
    private Long daThayDoiTheo;

    /** Đã thay đổi tại. */
    private LocalDateTime daThayDoiTai;

    /** Kiểu thay đổi. */
    private String kieuThayDoi; // MANUAL, PROMOTION, EXPIRY_DISCOUNT, COST_ADJUSTMENT

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo LichSuGia với các giá trị được cung cấp. */
    public LichSuGia() {
        this.kieuThayDoi = "MANUAL";
    }

    /**
     * Khởi tạo LichSuGia với các giá trị được cung cấp.
     *
     * @param id id
     * @param idSanPham id sản phẩm
     * @param giaCu giá cũ
     * @param giaMoi giá mới
     * @param daThayDoiTheo đã thay đổi theo
     */
    public LichSuGia(
            Long id, Long idSanPham, BigDecimal giaCu, BigDecimal giaMoi, Long daThayDoiTheo) {
        this();
        this.id = id;
        this.idSanPham = idSanPham;
        this.giaCu = giaCu;
        this.giaMoi = giaMoi;
        this.daThayDoiTheo = daThayDoiTheo;
        this.daThayDoiTai = LocalDateTime.now();
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong LichSuGia.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong LichSuGia.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy id sản phẩm trong LichSuGia.
     *
     * @return kết quả lấy id sản phẩm
     */
    public Long layIdSanPham() {
        return idSanPham;
    }

    /**
     * Đặt id sản phẩm trong LichSuGia.
     *
     * @param idSanPham id sản phẩm
     */
    public void datIdSanPham(Long idSanPham) {
        this.idSanPham = idSanPham;
    }

    /**
     * Lấy giá cũ trong LichSuGia.
     *
     * @return kết quả lấy giá cũ
     */
    public BigDecimal layGiaCu() {
        return giaCu;
    }

    /**
     * Đặt giá cũ trong LichSuGia.
     *
     * @param giaCu giá cũ
     */
    public void datGiaCu(BigDecimal giaCu) {
        this.giaCu = giaCu;
    }

    /**
     * Lấy giá mới trong LichSuGia.
     *
     * @return kết quả lấy giá mới
     */
    public BigDecimal layGiaMoi() {
        return giaMoi;
    }

    /**
     * Đặt giá mới trong LichSuGia.
     *
     * @param giaMoi giá mới
     */
    public void datGiaMoi(BigDecimal giaMoi) {
        this.giaMoi = giaMoi;
    }

    /**
     * Lấy lý do trong LichSuGia.
     *
     * @return kết quả lấy lý do
     */
    public String layLyDo() {
        return lyDo;
    }

    /**
     * Đặt lý do trong LichSuGia.
     *
     * @param lyDo lý do
     */
    public void datLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    /**
     * Lấy đã thay đổi theo trong LichSuGia.
     *
     * @return kết quả lấy đã thay đổi theo
     */
    public Long layDaThayDoiTheo() {
        return daThayDoiTheo;
    }

    /**
     * Đặt đã thay đổi theo trong LichSuGia.
     *
     * @param daThayDoiTheo đã thay đổi theo
     */
    public void datDaThayDoiTheo(Long daThayDoiTheo) {
        this.daThayDoiTheo = daThayDoiTheo;
    }

    /**
     * Lấy đã thay đổi tại trong LichSuGia.
     *
     * @return kết quả lấy đã thay đổi tại
     */
    public LocalDateTime layDaThayDoiTai() {
        return daThayDoiTai;
    }

    /**
     * Đặt đã thay đổi tại trong LichSuGia.
     *
     * @param daThayDoiTai đã thay đổi tại
     */
    public void datDaThayDoiTai(LocalDateTime daThayDoiTai) {
        this.daThayDoiTai = daThayDoiTai;
    }

    /**
     * Lấy kiểu thay đổi trong LichSuGia.
     *
     * @return kết quả lấy kiểu thay đổi
     */
    public String layKieuThayDoi() {
        return kieuThayDoi;
    }

    /**
     * Đặt kiểu thay đổi trong LichSuGia.
     *
     * @param kieuThayDoi kiểu thay đổi
     */
    public void datKieuThayDoi(String kieuThayDoi) {
        this.kieuThayDoi = kieuThayDoi;
    }

    /**
     * Lấy phiên bản bản ghi trong LichSuGia.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong LichSuGia.
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
        return "PriceHistory{"
                + "id="
                + id
                + ", productId="
                + idSanPham
                + ", oldPrice="
                + giaCu
                + ", newPrice="
                + giaMoi
                + ", changeType='"
                + kieuThayDoi
                + '\''
                + ", changedAt="
                + daThayDoiTai
                + '}';
    }
}
