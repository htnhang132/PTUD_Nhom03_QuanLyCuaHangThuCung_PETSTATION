package com.petstation.model;

import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng goi_dich_vu_spa trong SQL Server. */
public class GoiDichVuSpa {
    /** Id. */
    private Long id;

    /** Mã gói dịch vụ. */
    private String maGoiDichVu;

    /** Tên gói dịch vụ. */
    private String tenGoiDichVu;

    /** Mô tả. */
    private String moTa;

    /** Loại gói dịch vụ. */
    private String loaiGoiDichVu; // Mã loại: COMBO hoặc SINGLE

    /** Trạng thái. */
    private String trangThai; // ACTIVE, INACTIVE

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo GoiDichVuSpa với các giá trị được cung cấp. */
    public GoiDichVuSpa() {
        this.trangThai = "ACTIVE";
    }

    /**
     * Khởi tạo GoiDichVuSpa với các giá trị được cung cấp.
     *
     * @param id id
     * @param maGoiDichVu mã gói dịch vụ
     * @param tenGoiDichVu tên gói dịch vụ
     * @param loaiGoiDichVu loại gói dịch vụ
     */
    public GoiDichVuSpa(Long id, String maGoiDichVu, String tenGoiDichVu, String loaiGoiDichVu) {
        this();
        this.id = id;
        this.maGoiDichVu = maGoiDichVu;
        this.tenGoiDichVu = tenGoiDichVu;
        this.loaiGoiDichVu = loaiGoiDichVu;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong GoiDichVuSpa.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong GoiDichVuSpa.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã gói dịch vụ trong GoiDichVuSpa.
     *
     * @return kết quả lấy mã gói dịch vụ
     */
    public String layMaGoiDichVu() {
        return maGoiDichVu;
    }

    /**
     * Đặt mã gói dịch vụ trong GoiDichVuSpa.
     *
     * @param maGoiDichVu mã gói dịch vụ
     */
    public void datMaGoiDichVu(String maGoiDichVu) {
        this.maGoiDichVu = maGoiDichVu;
    }

    /**
     * Lấy tên gói dịch vụ trong GoiDichVuSpa.
     *
     * @return kết quả lấy tên gói dịch vụ
     */
    public String layTenGoiDichVu() {
        return tenGoiDichVu;
    }

    /**
     * Đặt tên gói dịch vụ trong GoiDichVuSpa.
     *
     * @param tenGoiDichVu tên gói dịch vụ
     */
    public void datTenGoiDichVu(String tenGoiDichVu) {
        this.tenGoiDichVu = tenGoiDichVu;
    }

    /**
     * Lấy mô tả trong GoiDichVuSpa.
     *
     * @return kết quả lấy mô tả
     */
    public String layMoTa() {
        return moTa;
    }

    /**
     * Đặt mô tả trong GoiDichVuSpa.
     *
     * @param moTa mô tả
     */
    public void datMoTa(String moTa) {
        this.moTa = moTa;
    }

    /**
     * Lấy loại gói dịch vụ trong GoiDichVuSpa.
     *
     * @return kết quả lấy loại gói dịch vụ
     */
    public String layLoaiGoiDichVu() {
        return loaiGoiDichVu;
    }

    /**
     * Đặt loại gói dịch vụ trong GoiDichVuSpa.
     *
     * @param loaiGoiDichVu loại gói dịch vụ
     */
    public void datLoaiGoiDichVu(String loaiGoiDichVu) {
        this.loaiGoiDichVu = loaiGoiDichVu;
    }

    /**
     * Lấy trạng thái trong GoiDichVuSpa.
     *
     * @return kết quả lấy trạng thái
     */
    public String layTrangThai() {
        return trangThai;
    }

    /**
     * Đặt trạng thái trong GoiDichVuSpa.
     *
     * @param trangThai trạng thái
     */
    public void datTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Lấy thời điểm tạo trong GoiDichVuSpa.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong GoiDichVuSpa.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong GoiDichVuSpa.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong GoiDichVuSpa.
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
        return "SpaPackage{"
                + "id="
                + id
                + ", packageCode='"
                + maGoiDichVu
                + '\''
                + ", packageName='"
                + tenGoiDichVu
                + '\''
                + ", packageType='"
                + loaiGoiDichVu
                + '\''
                + ", status='"
                + trangThai
                + '\''
                + '}';
    }
}
