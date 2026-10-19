package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng thu_cung trong SQL Server. */
public class ThuCung {
    /** Id. */
    private Long id;

    /** Id khách hàng. */
    private Long idKhachHang;

    /** Mã thú cưng. */
    private String maThuCung;

    /** Tên. */
    private String ten;

    /** Loài. */
    private String loai; // DOG, CAT

    /** Giống. */
    private String giong;

    /** Giới tính. */
    private String gioiTinh; // MALE, FEMALE

    /** Cân nặng. */
    private BigDecimal canNang;

    /** Ngày sinh. */
    private LocalDate ngaySinh;

    /** Màu lông. */
    private String mauLong;

    /** Tính cách. */
    private String tinhCach;

    /** URL ảnh. */
    private String urlAnh;

    /** Dị ứng. */
    private String diUng;

    /** Đã triệt sản. */
    private Boolean daTrietSan;

    /** Ghi chú sức khỏe. */
    private String ghiChuSucKhoe;

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo ThuCung với các giá trị được cung cấp. */
    public ThuCung() {
        this.daTrietSan = false;
    }

    /**
     * Khởi tạo ThuCung với các giá trị được cung cấp.
     *
     * @param id id
     * @param idKhachHang id khách hàng
     * @param maThuCung mã thú cưng
     * @param ten tên
     * @param loai loài
     */
    public ThuCung(Long id, Long idKhachHang, String maThuCung, String ten, String loai) {
        this();
        this.id = id;
        this.idKhachHang = idKhachHang;
        this.maThuCung = maThuCung;
        this.ten = ten;
        this.loai = loai;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong ThuCung.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong ThuCung.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy id khách hàng trong ThuCung.
     *
     * @return kết quả lấy id khách hàng
     */
    public Long layIdKhachHang() {
        return idKhachHang;
    }

    /**
     * Đặt id khách hàng trong ThuCung.
     *
     * @param idKhachHang id khách hàng
     */
    public void datIdKhachHang(Long idKhachHang) {
        this.idKhachHang = idKhachHang;
    }

    /**
     * Lấy mã thú cưng trong ThuCung.
     *
     * @return kết quả lấy mã thú cưng
     */
    public String layMaThuCung() {
        return maThuCung;
    }

    /**
     * Đặt mã thú cưng trong ThuCung.
     *
     * @param maThuCung mã thú cưng
     */
    public void datMaThuCung(String maThuCung) {
        this.maThuCung = maThuCung;
    }

    /**
     * Lấy tên trong ThuCung.
     *
     * @return kết quả lấy tên
     */
    public String layTen() {
        return ten;
    }

    /**
     * Đặt tên trong ThuCung.
     *
     * @param ten tên
     */
    public void datTen(String ten) {
        this.ten = ten;
    }

    /**
     * Lấy loài trong ThuCung.
     *
     * @return kết quả lấy loài
     */
    public String layLoai() {
        return loai;
    }

    /**
     * Đặt loài trong ThuCung.
     *
     * @param loai loài
     */
    public void datLoai(String loai) {
        this.loai = loai;
    }

    /**
     * Lấy giống trong ThuCung.
     *
     * @return kết quả lấy giống
     */
    public String layGiong() {
        return giong;
    }

    /**
     * Đặt giống trong ThuCung.
     *
     * @param giong giống
     */
    public void datGiong(String giong) {
        this.giong = giong;
    }

    /**
     * Lấy giới tính trong ThuCung.
     *
     * @return kết quả lấy giới tính
     */
    public String layGioiTinh() {
        return gioiTinh;
    }

    /**
     * Đặt giới tính trong ThuCung.
     *
     * @param gioiTinh giới tính
     */
    public void datGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    /**
     * Lấy cân nặng trong ThuCung.
     *
     * @return kết quả lấy cân nặng
     */
    public BigDecimal layCanNang() {
        return canNang;
    }

    /**
     * Đặt cân nặng trong ThuCung.
     *
     * @param canNang cân nặng
     */
    public void datCanNang(BigDecimal canNang) {
        this.canNang = canNang;
    }

    /**
     * Lấy ngày sinh trong ThuCung.
     *
     * @return kết quả lấy ngày sinh
     */
    public LocalDate layNgaySinh() {
        return ngaySinh;
    }

    /**
     * Đặt ngày sinh trong ThuCung.
     *
     * @param ngaySinh ngày sinh
     */
    public void datNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    /**
     * Lấy màu lông trong ThuCung.
     *
     * @return kết quả lấy màu lông
     */
    public String layMauLong() {
        return mauLong;
    }

    /**
     * Đặt màu lông trong ThuCung.
     *
     * @param mauLong màu lông
     */
    public void datMauLong(String mauLong) {
        this.mauLong = mauLong;
    }

    /**
     * Lấy tính cách trong ThuCung.
     *
     * @return kết quả lấy tính cách
     */
    public String layTinhCach() {
        return tinhCach;
    }

    /**
     * Đặt tính cách trong ThuCung.
     *
     * @param tinhCach tính cách
     */
    public void datTinhCach(String tinhCach) {
        this.tinhCach = tinhCach;
    }

    /**
     * Lấy URL ảnh trong ThuCung.
     *
     * @return kết quả lấy URL ảnh
     */
    public String layURLAnh() {
        return urlAnh;
    }

    /**
     * Đặt URL ảnh trong ThuCung.
     *
     * @param urlAnh URL ảnh
     */
    public void datURLAnh(String urlAnh) {
        this.urlAnh = urlAnh;
    }

    /**
     * Lấy dị ứng trong ThuCung.
     *
     * @return kết quả lấy dị ứng
     */
    public String layDiUng() {
        return diUng;
    }

    /**
     * Đặt dị ứng trong ThuCung.
     *
     * @param diUng dị ứng
     */
    public void datDiUng(String diUng) {
        this.diUng = diUng;
    }

    /**
     * Lấy đã triệt sản trong ThuCung.
     *
     * @return kết quả lấy đã triệt sản
     */
    public Boolean layDaTrietSan() {
        return daTrietSan;
    }

    /**
     * Đặt đã triệt sản trong ThuCung.
     *
     * @param daTrietSan đã triệt sản
     */
    public void datDaTrietSan(Boolean daTrietSan) {
        this.daTrietSan = daTrietSan;
    }

    /**
     * Lấy ghi chú sức khỏe trong ThuCung.
     *
     * @return kết quả lấy ghi chú sức khỏe
     */
    public String layGhiChuSucKhoe() {
        return ghiChuSucKhoe;
    }

    /**
     * Đặt ghi chú sức khỏe trong ThuCung.
     *
     * @param ghiChuSucKhoe ghi chú sức khỏe
     */
    public void datGhiChuSucKhoe(String ghiChuSucKhoe) {
        this.ghiChuSucKhoe = ghiChuSucKhoe;
    }

    /**
     * Lấy thời điểm tạo trong ThuCung.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong ThuCung.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong ThuCung.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong ThuCung.
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
        return "Pet{"
                + "id="
                + id
                + ", petCode='"
                + maThuCung
                + '\''
                + ", name='"
                + ten
                + '\''
                + ", species='"
                + loai
                + '\''
                + ", breed='"
                + giong
                + '\''
                + ", weight="
                + canNang
                + '}';
    }
}
