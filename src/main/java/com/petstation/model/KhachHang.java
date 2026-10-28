package com.petstation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng khach_hang trong SQL Server. */
public class KhachHang {
    /** Id. */
    private Long id;

    /** Mã khách hàng. */
    private String maKhachHang;

    /** Họ tên. */
    private String hoTen;

    /** Số điện thoại. */
    private String soDienThoai;

    /** Địa chỉ. */
    private String diaChi;

    /** Ngày sinh. */
    private LocalDate ngaySinh;

    /** Điểm tích lũy. */
    private Integer diemTichLuy;

    /** Hạng thành viên. */
    private String hangThanhVien; // REGULAR, SILVER, GOLD, PLATINUM

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    public KhachHang() {
        this.diemTichLuy = 0;
        this.hangThanhVien = "REGULAR";
    }

    public KhachHang(Long id, String maKhachHang, String hoTen, String soDienThoai) {
        this();
        this.id = id;
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }

    // Chuẩn JavaBeans (get/set)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public Integer getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(Integer diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public String getHangThanhVien() {
        return hangThanhVien;
    }

    public void setHangThanhVien(String hangThanhVien) {
        this.hangThanhVien = hangThanhVien;
    }

    public LocalDateTime getThoiDiemTao() {
        return thoiDiemTao;
    }

    public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    public byte[] getPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    public void setPhienBanBanGhi(byte[] phienBanBanGhi) {
        this.phienBanBanGhi = phienBanBanGhi;
    }

    // Truy xuất và cập nhật thuộc tính kiểu tiếng Việt (Tương thích mã nguồn hiện có)
    public Long layId() {
        return getId();
    }

    public void datId(Long id) {
        setId(id);
    }

    public String layMaKhachHang() {
        return getMaKhachHang();
    }

    public void datMaKhachHang(String maKhachHang) {
        setMaKhachHang(maKhachHang);
    }

    public String layHoTen() {
        return getHoTen();
    }

    public void datHoTen(String hoTen) {
        setHoTen(hoTen);
    }

    public String laySoDienThoai() {
        return getSoDienThoai();
    }

    public void datSoDienThoai(String soDienThoai) {
        setSoDienThoai(soDienThoai);
    }

    public String layDiaChi() {
        return getDiaChi();
    }

    public void datDiaChi(String diaChi) {
        setDiaChi(diaChi);
    }

    public LocalDate layNgaySinh() {
        return getNgaySinh();
    }

    public void datNgaySinh(LocalDate ngaySinh) {
        setNgaySinh(ngaySinh);
    }

    public Integer layDiemTichLuy() {
        return getDiemTichLuy();
    }

    public void datDiemTichLuy(Integer diemTichLuy) {
        setDiemTichLuy(diemTichLuy);
    }

    public String layHangThanhVien() {
        return getHangThanhVien();
    }

    public void datHangThanhVien(String hangThanhVien) {
        setHangThanhVien(hangThanhVien);
    }

    public LocalDateTime layThoiDiemTao() {
        return getThoiDiemTao();
    }

    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        setThoiDiemTao(thoiDiemTao);
    }

    public byte[] layPhienBanBanGhi() {
        return getPhienBanBanGhi();
    }

    public void datPhienBanBanGhi(byte[] phienBanBanGhi) {
        setPhienBanBanGhi(phienBanBanGhi);
    }

    /**
     * Biểu diễn đối tượng dưới dạng chuỗi.
     *
     * @return chuỗi mô tả đối tượng
     */
    @Override
    public String toString() {
        return "KhachHang{"
                + "id="
                + id
                + ", maKhachHang='"
                + maKhachHang
                + '\''
                + ", hoTen='"
                + hoTen
                + '\''
                + ", soDienThoai='"
                + soDienThoai
                + '\''
                + ", hangThanhVien='"
                + hangThanhVien
                + '\''
                + ", diemTichLuy="
                + diemTichLuy
                + '}';
    }
}
