package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể phiếu tiếp nhận spa khớp bảng [phieu_tiep_nhan_spa].
 */
public class PhieuTiepNhanSpa implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maPhieu;
    private Long lichHenId;
    private long thuCungId;
    private long goiId;
    private String moTaDichVu;
    private long donGia;
    private int soLuong;
    private double canNangThucTe;
    private long tongTien;
    private String trangThai;
    private LocalDateTime taoLuc;
    private Long nhanVienId;
    private String ghiChu;
    private String anh;
    private Long hoaDonId;

    public PhieuTiepNhanSpa() {
    }

    public PhieuTiepNhanSpa(long id, String maPhieu, Long lichHenId, long thuCungId, long goiId, String moTaDichVu, long donGia, int soLuong, double canNangThucTe, long tongTien, String trangThai, LocalDateTime taoLuc, Long nhanVienId, String ghiChu, String anh, Long hoaDonId) {
        this.id = id;
        this.maPhieu = maPhieu;
        this.lichHenId = lichHenId;
        this.thuCungId = thuCungId;
        this.goiId = goiId;
        this.moTaDichVu = moTaDichVu;
        this.donGia = donGia;
        this.soLuong = soLuong;
        this.canNangThucTe = canNangThucTe;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.taoLuc = taoLuc;
        this.nhanVienId = nhanVienId;
        this.ghiChu = ghiChu;
        this.anh = anh;
        this.hoaDonId = hoaDonId;
    }

    public long getId() {
        return this.id;
    }

    public long layId() {
        return this.id;
    }

    public long id() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void datId(long id) {
        this.id = id;
    }

    public String getMaPhieu() {
        return this.maPhieu;
    }

    public String layMaPhieu() {
        return this.maPhieu;
    }

    public String maPhieu() {
        return this.maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public void datMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public Long getLichHenId() {
        return this.lichHenId;
    }

    public Long layLichHenId() {
        return this.lichHenId;
    }

    public Long lichHenId() {
        return this.lichHenId;
    }

    public void setLichHenId(Long lichHenId) {
        this.lichHenId = lichHenId;
    }

    public void datLichHenId(Long lichHenId) {
        this.lichHenId = lichHenId;
    }

    public long getThuCungId() {
        return this.thuCungId;
    }

    public long layThuCungId() {
        return this.thuCungId;
    }

    public long thuCungId() {
        return this.thuCungId;
    }

    public void setThuCungId(long thuCungId) {
        this.thuCungId = thuCungId;
    }

    public void datThuCungId(long thuCungId) {
        this.thuCungId = thuCungId;
    }

    public long getGoiId() {
        return this.goiId;
    }

    public long layGoiId() {
        return this.goiId;
    }

    public long goiId() {
        return this.goiId;
    }

    public void setGoiId(long goiId) {
        this.goiId = goiId;
    }

    public void datGoiId(long goiId) {
        this.goiId = goiId;
    }

    public String getMoTaDichVu() {
        return this.moTaDichVu;
    }

    public String layMoTaDichVu() {
        return this.moTaDichVu;
    }

    public String moTaDichVu() {
        return this.moTaDichVu;
    }

    public void setMoTaDichVu(String moTaDichVu) {
        this.moTaDichVu = moTaDichVu;
    }

    public void datMoTaDichVu(String moTaDichVu) {
        this.moTaDichVu = moTaDichVu;
    }

    public long getDonGia() {
        return this.donGia;
    }

    public long layDonGia() {
        return this.donGia;
    }

    public long donGia() {
        return this.donGia;
    }

    public void setDonGia(long donGia) {
        this.donGia = donGia;
    }

    public void datDonGia(long donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public int laySoLuong() {
        return this.soLuong;
    }

    public int soLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public void datSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getCanNangThucTe() {
        return this.canNangThucTe;
    }

    public double layCanNangThucTe() {
        return this.canNangThucTe;
    }

    public double canNangThucTe() {
        return this.canNangThucTe;
    }

    public void setCanNangThucTe(double canNangThucTe) {
        this.canNangThucTe = canNangThucTe;
    }

    public void datCanNangThucTe(double canNangThucTe) {
        this.canNangThucTe = canNangThucTe;
    }

    public long getTongTien() {
        return this.tongTien;
    }

    public long layTongTien() {
        return this.tongTien;
    }

    public long tongTien() {
        return this.tongTien;
    }

    public void setTongTien(long tongTien) {
        this.tongTien = tongTien;
    }

    public void datTongTien(long tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public String layTrangThai() {
        return this.trangThai;
    }

    public String trangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void datTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getTaoLuc() {
        return this.taoLuc;
    }

    public LocalDateTime layTaoLuc() {
        return this.taoLuc;
    }

    public LocalDateTime taoLuc() {
        return this.taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    public void datTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    public Long getNhanVienId() {
        return this.nhanVienId;
    }

    public Long layNhanVienId() {
        return this.nhanVienId;
    }

    public Long nhanVienId() {
        return this.nhanVienId;
    }

    public void setNhanVienId(Long nhanVienId) {
        this.nhanVienId = nhanVienId;
    }

    public void datNhanVienId(Long nhanVienId) {
        this.nhanVienId = nhanVienId;
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public String layGhiChu() {
        return this.ghiChu;
    }

    public String ghiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public void datGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getAnh() {
        return this.anh;
    }

    public String layAnh() {
        return this.anh;
    }

    public String anh() {
        return this.anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public void datAnh(String anh) {
        this.anh = anh;
    }

    public Long getHoaDonId() {
        return this.hoaDonId;
    }

    public Long layHoaDonId() {
        return this.hoaDonId;
    }

    public Long hoaDonId() {
        return this.hoaDonId;
    }

    public void setHoaDonId(Long hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public void datHoaDonId(Long hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuTiepNhanSpa that = (PhieuTiepNhanSpa) o;
        return this.id == that.id
            && Objects.equals(maPhieu, that.maPhieu)
            && Objects.equals(lichHenId, that.lichHenId)
            && this.thuCungId == that.thuCungId
            && this.goiId == that.goiId
            && Objects.equals(moTaDichVu, that.moTaDichVu)
            && this.donGia == that.donGia
            && this.soLuong == that.soLuong
            && this.canNangThucTe == that.canNangThucTe
            && this.tongTien == that.tongTien
            && Objects.equals(trangThai, that.trangThai)
            && Objects.equals(taoLuc, that.taoLuc)
            && Objects.equals(nhanVienId, that.nhanVienId)
            && Objects.equals(ghiChu, that.ghiChu)
            && Objects.equals(anh, that.anh)
            && Objects.equals(hoaDonId, that.hoaDonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maPhieu, lichHenId, thuCungId, goiId, moTaDichVu, donGia, soLuong, canNangThucTe, tongTien, trangThai, taoLuc, nhanVienId, ghiChu, anh, hoaDonId);
    }

    @Override
    public String toString() {
        return "PhieuTiepNhanSpa{" + "id=" + id + ", " + "maPhieu=" + maPhieu + ", " + "lichHenId=" + lichHenId + ", " + "thuCungId=" + thuCungId + ", " + "goiId=" + goiId + ", " + "moTaDichVu=" + moTaDichVu + ", " + "donGia=" + donGia + ", " + "soLuong=" + soLuong + ", " + "canNangThucTe=" + canNangThucTe + ", " + "tongTien=" + tongTien + ", " + "trangThai=" + trangThai + ", " + "taoLuc=" + taoLuc + ", " + "nhanVienId=" + nhanVienId + ", " + "ghiChu=" + ghiChu + ", " + "anh=" + anh + ", " + "hoaDonId=" + hoaDonId + "}";
    }
}
