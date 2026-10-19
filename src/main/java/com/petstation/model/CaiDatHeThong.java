package com.petstation.model;

import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng cai_dat trong SQL Server. */
public class CaiDatHeThong {
    /** Id. */
    private Long id;

    /** Khóa. */
    private String khoa;

    /** Giá trị. */
    private String giaTri;

    /** Kiểu dữ liệu. */
    private String kieuDuLieu; // STRING, INTEGER, DECIMAL, BOOLEAN, DATE

    /** Nhóm. */
    private String nhom;

    /** Mô tả. */
    private String moTa;

    /** Người cập nhật cuối. */
    private Long nguoiCapNhatCuoi;

    /** Thời điểm cập nhật cuối. */
    private LocalDateTime thoiDiemCapNhatCuoi;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo CaiDatHeThong với các giá trị được cung cấp. */
    public CaiDatHeThong() {
        this.kieuDuLieu = "STRING";
    }

    /**
     * Khởi tạo CaiDatHeThong với các giá trị được cung cấp.
     *
     * @param id id
     * @param khoa khóa
     * @param giaTri giá trị
     * @param nhom nhóm
     */
    public CaiDatHeThong(Long id, String khoa, String giaTri, String nhom) {
        this();
        this.id = id;
        this.khoa = khoa;
        this.giaTri = giaTri;
        this.nhom = nhom;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong CaiDatHeThong.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong CaiDatHeThong.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy khóa trong CaiDatHeThong.
     *
     * @return kết quả lấy khóa
     */
    public String layKhoa() {
        return khoa;
    }

    /**
     * Đặt khóa trong CaiDatHeThong.
     *
     * @param khoa khóa
     */
    public void datKhoa(String khoa) {
        this.khoa = khoa;
    }

    /**
     * Lấy giá trị trong CaiDatHeThong.
     *
     * @return kết quả lấy giá trị
     */
    public String layGiaTri() {
        return giaTri;
    }

    /**
     * Đặt giá trị trong CaiDatHeThong.
     *
     * @param giaTri giá trị
     */
    public void datGiaTri(String giaTri) {
        this.giaTri = giaTri;
    }

    /**
     * Lấy kiểu dữ liệu trong CaiDatHeThong.
     *
     * @return kết quả lấy kiểu dữ liệu
     */
    public String layKieuDuLieu() {
        return kieuDuLieu;
    }

    /**
     * Đặt kiểu dữ liệu trong CaiDatHeThong.
     *
     * @param kieuDuLieu kiểu dữ liệu
     */
    public void datKieuDuLieu(String kieuDuLieu) {
        this.kieuDuLieu = kieuDuLieu;
    }

    /**
     * Lấy nhóm trong CaiDatHeThong.
     *
     * @return kết quả lấy nhóm
     */
    public String layNhom() {
        return nhom;
    }

    /**
     * Đặt nhóm trong CaiDatHeThong.
     *
     * @param nhom nhóm
     */
    public void datNhom(String nhom) {
        this.nhom = nhom;
    }

    /**
     * Lấy mô tả trong CaiDatHeThong.
     *
     * @return kết quả lấy mô tả
     */
    public String layMoTa() {
        return moTa;
    }

    /**
     * Đặt mô tả trong CaiDatHeThong.
     *
     * @param moTa mô tả
     */
    public void datMoTa(String moTa) {
        this.moTa = moTa;
    }

    /**
     * Lấy người cập nhật cuối trong CaiDatHeThong.
     *
     * @return kết quả lấy người cập nhật cuối
     */
    public Long layNguoiCapNhatCuoi() {
        return nguoiCapNhatCuoi;
    }

    /**
     * Đặt người cập nhật cuối trong CaiDatHeThong.
     *
     * @param nguoiCapNhatCuoi người cập nhật cuối
     */
    public void datNguoiCapNhatCuoi(Long nguoiCapNhatCuoi) {
        this.nguoiCapNhatCuoi = nguoiCapNhatCuoi;
    }

    /**
     * Lấy thời điểm cập nhật cuối trong CaiDatHeThong.
     *
     * @return kết quả lấy thời điểm cập nhật cuối
     */
    public LocalDateTime layThoiDiemCapNhatCuoi() {
        return thoiDiemCapNhatCuoi;
    }

    /**
     * Đặt thời điểm cập nhật cuối trong CaiDatHeThong.
     *
     * @param thoiDiemCapNhatCuoi thời điểm cập nhật cuối
     */
    public void datThoiDiemCapNhatCuoi(LocalDateTime thoiDiemCapNhatCuoi) {
        this.thoiDiemCapNhatCuoi = thoiDiemCapNhatCuoi;
    }

    /**
     * Lấy phiên bản bản ghi trong CaiDatHeThong.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong CaiDatHeThong.
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
        return "SystemSettings{"
                + "id="
                + id
                + ", key='"
                + khoa
                + '\''
                + ", value='"
                + giaTri
                + '\''
                + ", group='"
                + nhom
                + '\''
                + ", dataType='"
                + kieuDuLieu
                + '\''
                + '}';
    }
}
