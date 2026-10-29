package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;

/** Thông tin in bổ sung; số dư điểm lịch sử để null nếu không xác định được. */
public class ChiTietBienLai implements Serializable {
    private static final long serialVersionUID = 1L;

    private GioHang.BienLai bienLai;
    private String tenCuaHang;
    private String diaChi;
    private String dienThoaiCuaHang;
    private String dienThoaiKhach;
    private int diemCong;
    private Long soDuDiemSau;
    private String loaiNguon;
    private String maNguon;
    private long daCanTru;
    private String hoaDonGoc;

    public ChiTietBienLai() {}

    public ChiTietBienLai(GioHang.BienLai bienLai, String tenCuaHang, String diaChi, String dienThoaiCuaHang, String dienThoaiKhach, int diemCong, Long soDuDiemSau, String loaiNguon, String maNguon, long daCanTru, String hoaDonGoc) {
        this.bienLai = bienLai;
        this.tenCuaHang = tenCuaHang;
        this.diaChi = diaChi;
        this.dienThoaiCuaHang = dienThoaiCuaHang;
        this.dienThoaiKhach = dienThoaiKhach;
        this.diemCong = diemCong;
        this.soDuDiemSau = soDuDiemSau;
        this.loaiNguon = loaiNguon;
        this.maNguon = maNguon;
        this.daCanTru = daCanTru;
        this.hoaDonGoc = hoaDonGoc;
    }

    public GioHang.BienLai getBienLai() { return this.bienLai; }
    public GioHang.BienLai layBienLai() { return this.bienLai; }
    public GioHang.BienLai bienLai() { return this.bienLai; }
    public void setBienLai(GioHang.BienLai bienLai) { this.bienLai = bienLai; }
    public void datBienLai(GioHang.BienLai bienLai) { this.bienLai = bienLai; }

    public String getTenCuaHang() { return this.tenCuaHang; }
    public String layTenCuaHang() { return this.tenCuaHang; }
    public String tenCuaHang() { return this.tenCuaHang; }
    public void setTenCuaHang(String tenCuaHang) { this.tenCuaHang = tenCuaHang; }
    public void datTenCuaHang(String tenCuaHang) { this.tenCuaHang = tenCuaHang; }

    public String getDiaChi() { return this.diaChi; }
    public String layDiaChi() { return this.diaChi; }
    public String diaChi() { return this.diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public void datDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getDienThoaiCuaHang() { return this.dienThoaiCuaHang; }
    public String layDienThoaiCuaHang() { return this.dienThoaiCuaHang; }
    public String dienThoaiCuaHang() { return this.dienThoaiCuaHang; }
    public void setDienThoaiCuaHang(String dienThoaiCuaHang) { this.dienThoaiCuaHang = dienThoaiCuaHang; }
    public void datDienThoaiCuaHang(String dienThoaiCuaHang) { this.dienThoaiCuaHang = dienThoaiCuaHang; }

    public String getDienThoaiKhach() { return this.dienThoaiKhach; }
    public String layDienThoaiKhach() { return this.dienThoaiKhach; }
    public String dienThoaiKhach() { return this.dienThoaiKhach; }
    public void setDienThoaiKhach(String dienThoaiKhach) { this.dienThoaiKhach = dienThoaiKhach; }
    public void datDienThoaiKhach(String dienThoaiKhach) { this.dienThoaiKhach = dienThoaiKhach; }

    public int getDiemCong() { return this.diemCong; }
    public int layDiemCong() { return this.diemCong; }
    public int diemCong() { return this.diemCong; }
    public void setDiemCong(int diemCong) { this.diemCong = diemCong; }
    public void datDiemCong(int diemCong) { this.diemCong = diemCong; }

    public Long getSoDuDiemSau() { return this.soDuDiemSau; }
    public Long laySoDuDiemSau() { return this.soDuDiemSau; }
    public Long soDuDiemSau() { return this.soDuDiemSau; }
    public void setSoDuDiemSau(Long soDuDiemSau) { this.soDuDiemSau = soDuDiemSau; }
    public void datSoDuDiemSau(Long soDuDiemSau) { this.soDuDiemSau = soDuDiemSau; }

    public String getLoaiNguon() { return this.loaiNguon; }
    public String layLoaiNguon() { return this.loaiNguon; }
    public String loaiNguon() { return this.loaiNguon; }
    public void setLoaiNguon(String loaiNguon) { this.loaiNguon = loaiNguon; }
    public void datLoaiNguon(String loaiNguon) { this.loaiNguon = loaiNguon; }

    public String getMaNguon() { return this.maNguon; }
    public String layMaNguon() { return this.maNguon; }
    public String maNguon() { return this.maNguon; }
    public void setMaNguon(String maNguon) { this.maNguon = maNguon; }
    public void datMaNguon(String maNguon) { this.maNguon = maNguon; }

    public long getDaCanTru() { return this.daCanTru; }
    public long layDaCanTru() { return this.daCanTru; }
    public long daCanTru() { return this.daCanTru; }
    public void setDaCanTru(long daCanTru) { this.daCanTru = daCanTru; }
    public void datDaCanTru(long daCanTru) { this.daCanTru = daCanTru; }

    public String getHoaDonGoc() { return this.hoaDonGoc; }
    public String layHoaDonGoc() { return this.hoaDonGoc; }
    public String hoaDonGoc() { return this.hoaDonGoc; }
    public void setHoaDonGoc(String hoaDonGoc) { this.hoaDonGoc = hoaDonGoc; }
    public void datHoaDonGoc(String hoaDonGoc) { this.hoaDonGoc = hoaDonGoc; }

    public int tongSoLuong() {
        return bienLai != null && bienLai.cacDong() != null 
            ? bienLai.cacDong().stream().mapToInt(GioHang.DongHang::soLuong).sum() 
            : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietBienLai that = (ChiTietBienLai) o;
        return Objects.equals(bienLai, that.bienLai)
                && Objects.equals(tenCuaHang, that.tenCuaHang)
                && Objects.equals(diaChi, that.diaChi)
                && Objects.equals(dienThoaiCuaHang, that.dienThoaiCuaHang)
                && Objects.equals(dienThoaiKhach, that.dienThoaiKhach)
                && this.diemCong == that.diemCong
                && Objects.equals(soDuDiemSau, that.soDuDiemSau)
                && Objects.equals(loaiNguon, that.loaiNguon)
                && Objects.equals(maNguon, that.maNguon)
                && this.daCanTru == that.daCanTru
                && Objects.equals(hoaDonGoc, that.hoaDonGoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bienLai, tenCuaHang, diaChi, dienThoaiCuaHang, dienThoaiKhach, diemCong, soDuDiemSau, loaiNguon, maNguon, daCanTru, hoaDonGoc);
    }

    @Override
    public String toString() {
        return "ChiTietBienLai{" + "bienLai=" + bienLai + ", " + "tenCuaHang=" + tenCuaHang + ", " + "diaChi=" + diaChi + ", " + "dienThoaiCuaHang=" + dienThoaiCuaHang + ", " + "dienThoaiKhach=" + dienThoaiKhach + ", " + "diemCong=" + diemCong + ", " + "soDuDiemSau=" + soDuDiemSau + ", " + "loaiNguon=" + loaiNguon + ", " + "maNguon=" + maNguon + ", " + "daCanTru=" + daCanTru + ", " + "hoaDonGoc=" + hoaDonGoc + "}";
    }
}
