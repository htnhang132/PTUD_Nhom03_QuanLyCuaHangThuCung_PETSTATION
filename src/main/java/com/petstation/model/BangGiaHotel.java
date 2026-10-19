package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng bang_gia_hotel trong SQL Server. */
public class BangGiaHotel {
    /** Id. */
    private Long id;

    /** Id phòng. */
    private Long idPhong;

    /** Khung thời gian. */
    private String khungThoiGian; // HOURLY, DAILY, OVERNIGHT

    /** Cân nặng từ. */
    private BigDecimal canNangTu;

    /** Cân nặng đến. */
    private BigDecimal canNangDen;

    /** Giá. */
    private BigDecimal gia;

    /** Ngày hiệu lực. */
    private LocalDateTime ngayHieuLuc;

    /** Hạn sử dụng. */
    private LocalDateTime hanSuDung;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo BangGiaHotel với các giá trị được cung cấp. */
    public BangGiaHotel() {}

    /**
     * Khởi tạo BangGiaHotel với các giá trị được cung cấp.
     *
     * @param id id
     * @param idPhong id phòng
     * @param khungThoiGian khung thời gian
     * @param canNangTu cân nặng từ
     * @param canNangDen cân nặng đến
     * @param gia giá
     */
    public BangGiaHotel(
            Long id,
            Long idPhong,
            String khungThoiGian,
            BigDecimal canNangTu,
            BigDecimal canNangDen,
            BigDecimal gia) {
        this.id = id;
        this.idPhong = idPhong;
        this.khungThoiGian = khungThoiGian;
        this.canNangTu = canNangTu;
        this.canNangDen = canNangDen;
        this.gia = gia;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong BangGiaHotel.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong BangGiaHotel.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy id phòng trong BangGiaHotel.
     *
     * @return kết quả lấy id phòng
     */
    public Long layIdPhong() {
        return idPhong;
    }

    /**
     * Đặt id phòng trong BangGiaHotel.
     *
     * @param idPhong id phòng
     */
    public void datIdPhong(Long idPhong) {
        this.idPhong = idPhong;
    }

    /**
     * Lấy khung thời gian trong BangGiaHotel.
     *
     * @return kết quả lấy khung thời gian
     */
    public String layKhungThoiGian() {
        return khungThoiGian;
    }

    /**
     * Đặt khung thời gian trong BangGiaHotel.
     *
     * @param khungThoiGian khung thời gian
     */
    public void datKhungThoiGian(String khungThoiGian) {
        this.khungThoiGian = khungThoiGian;
    }

    /**
     * Lấy cân nặng từ trong BangGiaHotel.
     *
     * @return kết quả lấy cân nặng từ
     */
    public BigDecimal layCanNangTu() {
        return canNangTu;
    }

    /**
     * Đặt cân nặng từ trong BangGiaHotel.
     *
     * @param canNangTu cân nặng từ
     */
    public void datCanNangTu(BigDecimal canNangTu) {
        this.canNangTu = canNangTu;
    }

    /**
     * Lấy cân nặng đến trong BangGiaHotel.
     *
     * @return kết quả lấy cân nặng đến
     */
    public BigDecimal layCanNangDen() {
        return canNangDen;
    }

    /**
     * Đặt cân nặng đến trong BangGiaHotel.
     *
     * @param canNangDen cân nặng đến
     */
    public void datCanNangDen(BigDecimal canNangDen) {
        this.canNangDen = canNangDen;
    }

    /**
     * Lấy giá trong BangGiaHotel.
     *
     * @return kết quả lấy giá
     */
    public BigDecimal layGia() {
        return gia;
    }

    /**
     * Đặt giá trong BangGiaHotel.
     *
     * @param gia giá
     */
    public void datGia(BigDecimal gia) {
        this.gia = gia;
    }

    /**
     * Lấy ngày hiệu lực trong BangGiaHotel.
     *
     * @return kết quả lấy ngày hiệu lực
     */
    public LocalDateTime layNgayHieuLuc() {
        return ngayHieuLuc;
    }

    /**
     * Đặt ngày hiệu lực trong BangGiaHotel.
     *
     * @param ngayHieuLuc ngày hiệu lực
     */
    public void datNgayHieuLuc(LocalDateTime ngayHieuLuc) {
        this.ngayHieuLuc = ngayHieuLuc;
    }

    /**
     * Lấy hạn sử dụng trong BangGiaHotel.
     *
     * @return kết quả lấy hạn sử dụng
     */
    public LocalDateTime layHanSuDung() {
        return hanSuDung;
    }

    /**
     * Đặt hạn sử dụng trong BangGiaHotel.
     *
     * @param hanSuDung hạn sử dụng
     */
    public void datHanSuDung(LocalDateTime hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    /**
     * Lấy phiên bản bản ghi trong BangGiaHotel.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong BangGiaHotel.
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
        return "HotelPricing{"
                + "id="
                + id
                + ", roomId="
                + idPhong
                + ", timeFrame='"
                + khungThoiGian
                + '\''
                + ", weightFrom="
                + canNangTu
                + ", weightTo="
                + canNangDen
                + ", price="
                + gia
                + '}';
    }
}
