package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.util.Objects;

/**
 * Thực thể Nhà cung cấp khớp bảng [nha_cung_cap].
 * Khóa chính: maNhaCungCap (alias: maNcc).
 */
public class NhaCungCap implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính (PK): Mã nhà cung cấp (ví dụ: NCC0001). */
    private String maNhaCungCap;
    private String ten;
    private String soDienThoai;
    private String diaChi;
    private String nguoiDaiDien;
    private String nhomMatHang;
    private String ghiChu;
    private String trangThai;
    private String maSoThue;
    private String dieuKhoan;

    /** Constructor mặc định. */
    public NhaCungCap() {
        this.trangThai = "Đang hợp tác";
    }

    /** Constructor chỉ có khóa chính. */
    public NhaCungCap(String maNhaCungCap) {
        this();
        setMaNhaCungCap(maNhaCungCap);
    }

    /** Constructor đầy đủ thuộc tính thường. */
    public NhaCungCap(
            String maNhaCungCap,
            String ten,
            String soDienThoai,
            String diaChi,
            String nguoiDaiDien,
            String nhomMatHang,
            String ghiChu,
            String trangThai,
            String maSoThue,
            String dieuKhoan) {
        this();
        setMaNhaCungCap(maNhaCungCap);
        setTen(ten);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setNguoiDaiDien(nguoiDaiDien);
        setNhomMatHang(nhomMatHang);
        setGhiChu(ghiChu);
        setTrangThai(trangThai);
        setMaSoThue(maSoThue);
        setDieuKhoan(dieuKhoan);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public NhaCungCap(long id) {
        this();
        setId(id);
    }

    public NhaCungCap(
            long id,
            String maNhaCungCap,
            String ten,
            String soDienThoai,
            String diaChi,
            String nguoiDaiDien,
            String nhomMatHang,
            String ghiChu,
            String trangThai,
            String maSoThue,
            String dieuKhoan) {
        this(maNhaCungCap, ten, soDienThoai, diaChi, nguoiDaiDien, nhomMatHang, ghiChu, trangThai, maSoThue, dieuKhoan);
        setId(id);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Kiểm tra nhà cung cấp có đang trong trạng thái hợp tác hay không (dẫn xuất). */
    public boolean kiemTraDangHopTac() {
        return trangThai == null
                || "DANG_HOP_TAC".equalsIgnoreCase(trangThai)
                || "Đang hợp tác".equalsIgnoreCase(trangThai)
                || (!"Ngừng hợp tác".equalsIgnoreCase(trangThai) && !"NGUNG_HOP_TAC".equalsIgnoreCase(trangThai));
    }

    // ==================== GETTER & SETTER ====================

    public String getMaNhaCungCap() {
        return this.maNhaCungCap;
    }

    public String layMaNhaCungCap() {
        return this.maNhaCungCap;
    }

    public String maNhaCungCap() {
        return this.maNhaCungCap;
    }

    public void setMaNhaCungCap(String maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap == null ? null : maNhaCungCap.trim();
    }

    public void datMaNhaCungCap(String maNhaCungCap) {
        setMaNhaCungCap(maNhaCungCap);
    }

    // Alias maNcc tương thích ngược
    public String getMaNcc() {
        return getMaNhaCungCap();
    }

    public String layMaNcc() {
        return getMaNhaCungCap();
    }

    public String maNcc() {
        return getMaNhaCungCap();
    }

    public void setMaNcc(String maNcc) {
        setMaNhaCungCap(maNcc);
    }

    public void datMaNcc(String maNcc) {
        setMaNhaCungCap(maNcc);
    }

    public String getTen() { return this.ten; }
    public String layTen() { return this.ten; }
    public String ten() { return this.ten; }
    public void setTen(String ten) { this.ten = ten == null ? null : ten.trim(); }
    public void datTen(String ten) { setTen(ten); }

    public String getSoDienThoai() { return this.soDienThoai; }
    public String laySoDienThoai() { return this.soDienThoai; }
    public String soDienThoai() { return this.soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai == null ? null : soDienThoai.trim(); }
    public void datSoDienThoai(String soDienThoai) { setSoDienThoai(soDienThoai); }

    public String getDiaChi() { return this.diaChi; }
    public String layDiaChi() { return this.diaChi; }
    public String diaChi() { return this.diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public void datDiaChi(String diaChi) { setDiaChi(diaChi); }

    public String getNguoiDaiDien() { return this.nguoiDaiDien; }
    public String layNguoiDaiDien() { return this.nguoiDaiDien; }
    public String nguoiDaiDien() { return this.nguoiDaiDien; }
    public void setNguoiDaiDien(String nguoiDaiDien) { this.nguoiDaiDien = nguoiDaiDien; }
    public void datNguoiDaiDien(String nguoiDaiDien) { setNguoiDaiDien(nguoiDaiDien); }

    public String getNhomMatHang() { return this.nhomMatHang; }
    public String layNhomMatHang() { return this.nhomMatHang; }
    public String nhomMatHang() { return this.nhomMatHang; }
    public void setNhomMatHang(String nhomMatHang) { this.nhomMatHang = nhomMatHang; }
    public void datNhomMatHang(String nhomMatHang) { setNhomMatHang(nhomMatHang); }

    public String getGhiChu() { return this.ghiChu; }
    public String layGhiChu() { return this.ghiChu; }
    public String ghiChu() { return this.ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public void datGhiChu(String ghiChu) { setGhiChu(ghiChu); }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { setTrangThai(trangThai); }

    public String getMaSoThue() { return this.maSoThue; }
    public String layMaSoThue() { return this.maSoThue; }
    public String maSoThue() { return this.maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }
    public void datMaSoThue(String maSoThue) { setMaSoThue(maSoThue); }

    public String getDieuKhoan() { return this.dieuKhoan; }
    public String layDieuKhoan() { return this.dieuKhoan; }
    public String dieuKhoan() { return this.dieuKhoan; }
    public void setDieuKhoan(String dieuKhoan) { this.dieuKhoan = dieuKhoan; }
    public void datDieuKhoan(String dieuKhoan) { setDieuKhoan(dieuKhoan); }

    // ==================== CẦU NỐI TƯƠNG THÍCH TẦNG DAO ====================

    public long getId() { return DuLieu.layIdCauNoi(this, maNhaCungCap); }
    public long layId() { return getId(); }
    public long id() { return getId(); }
    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maNhaCungCap == null || this.maNhaCungCap.isBlank()) && id > 0) {
            this.maNhaCungCap = DuLieu.taoMaTuId("NCC", 4, id);
        }
    }
    public void datId(long id) { setId(id); }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH (maNhaCungCap) ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhaCungCap that = (NhaCungCap) o;
        return Objects.equals(this.maNhaCungCap, that.maNhaCungCap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhaCungCap);
    }

    @Override
    public String toString() {
        return "NhaCungCap{" + "maNhaCungCap='" + maNhaCungCap + '\'' + ", ten='" + ten + '\'' + ", soDienThoai='"
                + soDienThoai + '\'' + ", trangThai='" + trangThai + '\'' + '}';
    }
}
