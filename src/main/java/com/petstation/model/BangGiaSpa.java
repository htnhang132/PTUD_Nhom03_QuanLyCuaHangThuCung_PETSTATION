package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng bang_gia_spa trong SQL Server. */
public class BangGiaSpa {
    /** Id. */
    private Long id;

    /** Id gói dịch vụ. */
    private Long idGoiDichVu;

    /** Loài. */
    private String loai; // DOG, CAT

    /** Cân nặng từ. */
    private BigDecimal canNangTu;

    /** Cân nặng đến. */
    private BigDecimal canNangDen;

    /** Giá. */
    private BigDecimal gia;

    /** Thời lượng dự kiến. */
    private Integer thoiLuongDuKien; // Đơn vị phút

    /** Ngày hiệu lực. */
    private LocalDateTime ngayHieuLuc;

    /** Hạn sử dụng. */
    private LocalDateTime hanSuDung;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo BangGiaSpa với các giá trị được cung cấp. */
    public BangGiaSpa() {}

    /**
     * Khởi tạo BangGiaSpa với các giá trị được cung cấp.
     *
     * @param id id
     * @param idGoiDichVu id gói dịch vụ
     * @param loai loài
     * @param canNangTu cân nặng từ
     * @param canNangDen cân nặng đến
     * @param gia giá
     */
    public BangGiaSpa(
            Long id,
            Long idGoiDichVu,
            String loai,
            BigDecimal canNangTu,
            BigDecimal canNangDen,
            BigDecimal gia) {
        this.id = id;
        this.idGoiDichVu = idGoiDichVu;
        this.loai = loai;
        this.canNangTu = canNangTu;
        this.canNangDen = canNangDen;
        this.gia = gia;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong BangGiaSpa.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong BangGiaSpa.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy id gói dịch vụ trong BangGiaSpa.
     *
     * @return kết quả lấy id gói dịch vụ
     */
    public Long layIdGoiDichVu() {
        return idGoiDichVu;
    }

    /**
     * Đặt id gói dịch vụ trong BangGiaSpa.
     *
     * @param idGoiDichVu id gói dịch vụ
     */
    public void datIdGoiDichVu(Long idGoiDichVu) {
        this.idGoiDichVu = idGoiDichVu;
    }

    /**
     * Lấy loài trong BangGiaSpa.
     *
     * @return kết quả lấy loài
     */
    public String layLoai() {
        return loai;
    }

    /**
     * Đặt loài trong BangGiaSpa.
     *
     * @param loai loài
     */
    public void datLoai(String loai) {
        this.loai = loai;
    }

    /**
     * Lấy cân nặng từ trong BangGiaSpa.
     *
     * @return kết quả lấy cân nặng từ
     */
    public BigDecimal layCanNangTu() {
        return canNangTu;
    }

    /**
     * Đặt cân nặng từ trong BangGiaSpa.
     *
     * @param canNangTu cân nặng từ
     */
    public void datCanNangTu(BigDecimal canNangTu) {
        this.canNangTu = canNangTu;
    }

    /**
     * Lấy cân nặng đến trong BangGiaSpa.
     *
     * @return kết quả lấy cân nặng đến
     */
    public BigDecimal layCanNangDen() {
        return canNangDen;
    }

    /**
     * Đặt cân nặng đến trong BangGiaSpa.
     *
     * @param canNangDen cân nặng đến
     */
    public void datCanNangDen(BigDecimal canNangDen) {
        this.canNangDen = canNangDen;
    }

    /**
     * Lấy giá trong BangGiaSpa.
     *
     * @return kết quả lấy giá
     */
    public BigDecimal layGia() {
        return gia;
    }

    /**
     * Đặt giá trong BangGiaSpa.
     *
     * @param gia giá
     */
    public void datGia(BigDecimal gia) {
        this.gia = gia;
    }

    /**
     * Lấy thời lượng dự kiến trong BangGiaSpa.
     *
     * @return kết quả lấy thời lượng dự kiến
     */
    public Integer layThoiLuongDuKien() {
        return thoiLuongDuKien;
    }

    /**
     * Đặt thời lượng dự kiến trong BangGiaSpa.
     *
     * @param thoiLuongDuKien thời lượng dự kiến
     */
    public void datThoiLuongDuKien(Integer thoiLuongDuKien) {
        this.thoiLuongDuKien = thoiLuongDuKien;
    }

    /**
     * Lấy ngày hiệu lực trong BangGiaSpa.
     *
     * @return kết quả lấy ngày hiệu lực
     */
    public LocalDateTime layNgayHieuLuc() {
        return ngayHieuLuc;
    }

    /**
     * Đặt ngày hiệu lực trong BangGiaSpa.
     *
     * @param ngayHieuLuc ngày hiệu lực
     */
    public void datNgayHieuLuc(LocalDateTime ngayHieuLuc) {
        this.ngayHieuLuc = ngayHieuLuc;
    }

    /**
     * Lấy hạn sử dụng trong BangGiaSpa.
     *
     * @return kết quả lấy hạn sử dụng
     */
    public LocalDateTime layHanSuDung() {
        return hanSuDung;
    }

    /**
     * Đặt hạn sử dụng trong BangGiaSpa.
     *
     * @param hanSuDung hạn sử dụng
     */
    public void datHanSuDung(LocalDateTime hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    /**
     * Lấy phiên bản bản ghi trong BangGiaSpa.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong BangGiaSpa.
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
        return "SpaPriceMatrix{"
                + "id="
                + id
                + ", packageId="
                + idGoiDichVu
                + ", species='"
                + loai
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
