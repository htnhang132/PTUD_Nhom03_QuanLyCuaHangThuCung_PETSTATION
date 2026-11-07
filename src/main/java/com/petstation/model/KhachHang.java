package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

public class KhachHang implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private LocalDate ngaySinh;
    private Integer diemTichLuy;
    private String hangThanhVien;
    private LocalDateTime thoiDiemTao;
    private byte[] phienBanBanGhi;

    public KhachHang() {
        this.diemTichLuy = 0;
        this.hangThanhVien = "Đồng";
        this.thoiDiemTao = LocalDateTime.now();
    }

    public KhachHang(String maKhachHang) {
        this();
        setMaKhachHang(maKhachHang);
    }

    public KhachHang(
            String maKhachHang,
            String hoTen,
            String soDienThoai,
            String diaChi,
            LocalDate ngaySinh,
            Integer diemTichLuy,
            String hangThanhVien,
            LocalDateTime thoiDiemTao) {
        this();
        setMaKhachHang(maKhachHang);
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setNgaySinh(ngaySinh);
        setDiemTichLuy(diemTichLuy);
        setHangThanhVien(hangThanhVien);
        setThoiDiemTao(thoiDiemTao);
    }

    public KhachHang(Long id) {
        this();
        setId(id);
    }

    public KhachHang(Long id, String maKhachHang, String hoTen, String soDienThoai) {
        this();
        setId(id);
        setMaKhachHang(maKhachHang);
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
    }

    public KhachHang(
            Long id,
            String maKhachHang,
            String hoTen,
            String soDienThoai,
            String diaChi,
            LocalDate ngaySinh,
            Integer diemTichLuy,
            String hangThanhVien,
            LocalDateTime thoiDiemTao) {
        this(maKhachHang, hoTen, soDienThoai, diaChi, ngaySinh, diemTichLuy, hangThanhVien, thoiDiemTao);
        setId(id);
    }

    public int tinhTuoi() {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now())) {
            return 0;
        }
        return Period.between(ngaySinh, LocalDate.now()).getYears();
    }

    public boolean kiemTraDuDiemDoiVoucher() {
        return diemTichLuy != null && diemTichLuy >= 200;
    }

    public int tinhSoVoucherCoTheDoi() {
        return diemTichLuy == null ? 0 : Math.max(0, diemTichLuy / 200);
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public String layMaKhachHang() {
        return maKhachHang;
    }

    public String maKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang == null ? null : maKhachHang.trim();
    }

    public void datMaKhachHang(String maKhachHang) {
        setMaKhachHang(maKhachHang);
    }

    public String getHoTen() {
        return hoTen;
    }

    public String layHoTen() {
        return hoTen;
    }

    public String hoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen == null ? null : hoTen.trim();
    }

    public void datHoTen(String hoTen) {
        setHoTen(hoTen);
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public String laySoDienThoai() {
        return soDienThoai;
    }

    public String soDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai == null ? null : soDienThoai.trim();
    }

    public void datSoDienThoai(String soDienThoai) {
        setSoDienThoai(soDienThoai);
    }

    public String getDiaChi() {
        return diaChi;
    }

    public String layDiaChi() {
        return diaChi;
    }

    public String diaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public void datDiaChi(String diaChi) {
        setDiaChi(diaChi);
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public LocalDate layNgaySinh() {
        return ngaySinh;
    }

    public LocalDate ngaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh != null && ngaySinh.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không được lớn hơn ngày hiện tại.");
        }
        this.ngaySinh = ngaySinh;
    }

    public void datNgaySinh(LocalDate ngaySinh) {
        setNgaySinh(ngaySinh);
    }

    public Integer getDiemTichLuy() {
        return diemTichLuy;
    }

    public Integer layDiemTichLuy() {
        return diemTichLuy;
    }

    public Integer diemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(Integer diemTichLuy) {
        if (diemTichLuy != null && diemTichLuy < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm.");
        }
        this.diemTichLuy = diemTichLuy == null ? 0 : diemTichLuy;
    }

    public void datDiemTichLuy(Integer diemTichLuy) {
        setDiemTichLuy(diemTichLuy);
    }

    public String getHangThanhVien() {
        return hangThanhVien;
    }

    public String layHangThanhVien() {
        return hangThanhVien;
    }

    public String hangThanhVien() {
        return hangThanhVien;
    }

    public void setHangThanhVien(String hangThanhVien) {
        this.hangThanhVien = (hangThanhVien == null || hangThanhVien.isBlank()) ? "Đồng" : hangThanhVien.trim();
    }

    public void datHangThanhVien(String hangThanhVien) {
        setHangThanhVien(hangThanhVien);
    }

    public LocalDateTime getThoiDiemTao() {
        return thoiDiemTao;
    }

    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        setThoiDiemTao(thoiDiemTao);
    }

    public byte[] getPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    public void setPhienBanBanGhi(byte[] phienBanBanGhi) {
        this.phienBanBanGhi = phienBanBanGhi;
    }

    public void datPhienBanBanGhi(byte[] phienBanBanGhi) {
        setPhienBanBanGhi(phienBanBanGhi);
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, maKhachHang);
    }

    public Long layId() {
        return getId();
    }

    public Long id() {
        return getId();
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("Mã định danh không hợp lệ.");
        }
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maKhachHang == null || this.maKhachHang.isBlank()) && id != null && id > 0) {
            this.maKhachHang = DuLieu.taoMaTuId("KH", 6, id);
        }
    }

    public void datId(Long id) {
        setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang that = (KhachHang) o;
        return Objects.equals(this.maKhachHang, that.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhachHang);
    }

    @Override
    public String toString() {
        return "KhachHang{"
                + "maKhachHang='" + maKhachHang + '\''
                + ", hoTen='" + hoTen + '\''
                + ", soDienThoai='" + soDienThoai + '\''
                + ", hangThanhVien='" + hangThanhVien + '\''
                + ", diemTichLuy=" + diemTichLuy
                + '}';
    }
}
