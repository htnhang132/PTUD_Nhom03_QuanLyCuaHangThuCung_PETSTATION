package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể hóa đơn bán hàng khớp bảng [hoa_don].
 */
public class HoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String soHoaDon;
    private Long phienLamViecId;
    private Long taiKhoanId;
    private Long khachHangId;
    private long tongTien;
    private String hinhThucThanhToan;
    private long tienKhachDua;
    private long tienThoi;
    private int diemCong;
    private String trangThai;
    private LocalDateTime taoLuc;
    private LocalDateTime thanhToanLuc;
    private String idempotencyKey;
    private long tamTinh;
    private long giamGia;
    private int diemDung;
    private String maVoucher;
    private String ghiChu;
    private String chiTietThanhToan;

    public HoaDon() {}

    public HoaDon(long id, String soHoaDon, Long phienLamViecId, Long taiKhoanId, Long khachHangId, long tongTien, String hinhThucThanhToan, long tienKhachDua, long tienThoi, int diemCong, String trangThai, LocalDateTime taoLuc, LocalDateTime thanhToanLuc, String idempotencyKey, long tamTinh, long giamGia, int diemDung, String maVoucher, String ghiChu, String chiTietThanhToan) {
        this.id = id;
        this.soHoaDon = soHoaDon;
        this.phienLamViecId = phienLamViecId;
        this.taiKhoanId = taiKhoanId;
        this.khachHangId = khachHangId;
        this.tongTien = tongTien;
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.tienKhachDua = tienKhachDua;
        this.tienThoi = tienThoi;
        this.diemCong = diemCong;
        this.trangThai = trangThai;
        this.taoLuc = taoLuc;
        this.thanhToanLuc = thanhToanLuc;
        this.idempotencyKey = idempotencyKey;
        this.tamTinh = tamTinh;
        this.giamGia = giamGia;
        this.diemDung = diemDung;
        this.maVoucher = maVoucher;
        this.ghiChu = ghiChu;
        this.chiTietThanhToan = chiTietThanhToan;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getSoHoaDon() { return this.soHoaDon; }
    public String laySoHoaDon() { return this.soHoaDon; }
    public String soHoaDon() { return this.soHoaDon; }
    public void setSoHoaDon(String soHoaDon) { this.soHoaDon = soHoaDon; }
    public void datSoHoaDon(String soHoaDon) { this.soHoaDon = soHoaDon; }

    public Long getPhienLamViecId() { return this.phienLamViecId; }
    public Long layPhienLamViecId() { return this.phienLamViecId; }
    public Long phienLamViecId() { return this.phienLamViecId; }
    public void setPhienLamViecId(Long phienLamViecId) { this.phienLamViecId = phienLamViecId; }
    public void datPhienLamViecId(Long phienLamViecId) { this.phienLamViecId = phienLamViecId; }

    public Long getTaiKhoanId() { return this.taiKhoanId; }
    public Long layTaiKhoanId() { return this.taiKhoanId; }
    public Long taiKhoanId() { return this.taiKhoanId; }
    public void setTaiKhoanId(Long taiKhoanId) { this.taiKhoanId = taiKhoanId; }
    public void datTaiKhoanId(Long taiKhoanId) { this.taiKhoanId = taiKhoanId; }

    public Long getKhachHangId() { return this.khachHangId; }
    public Long layKhachHangId() { return this.khachHangId; }
    public Long khachHangId() { return this.khachHangId; }
    public void setKhachHangId(Long khachHangId) { this.khachHangId = khachHangId; }
    public void datKhachHangId(Long khachHangId) { this.khachHangId = khachHangId; }

    public long getTongTien() { return this.tongTien; }
    public long layTongTien() { return this.tongTien; }
    public long tongTien() { return this.tongTien; }
    public void setTongTien(long tongTien) { this.tongTien = tongTien; }
    public void datTongTien(long tongTien) { this.tongTien = tongTien; }

    public String getHinhThucThanhToan() { return this.hinhThucThanhToan; }
    public String layHinhThucThanhToan() { return this.hinhThucThanhToan; }
    public String hinhThucThanhToan() { return this.hinhThucThanhToan; }
    public void setHinhThucThanhToan(String hinhThucThanhToan) { this.hinhThucThanhToan = hinhThucThanhToan; }
    public void datHinhThucThanhToan(String hinhThucThanhToan) { this.hinhThucThanhToan = hinhThucThanhToan; }

    public long getTienKhachDua() { return this.tienKhachDua; }
    public long layTienKhachDua() { return this.tienKhachDua; }
    public long tienKhachDua() { return this.tienKhachDua; }
    public void setTienKhachDua(long tienKhachDua) { this.tienKhachDua = tienKhachDua; }
    public void datTienKhachDua(long tienKhachDua) { this.tienKhachDua = tienKhachDua; }

    public long getTienThoi() { return this.tienThoi; }
    public long layTienThoi() { return this.tienThoi; }
    public long tienThoi() { return this.tienThoi; }
    public void setTienThoi(long tienThoi) { this.tienThoi = tienThoi; }
    public void datTienThoi(long tienThoi) { this.tienThoi = tienThoi; }

    public int getDiemCong() { return this.diemCong; }
    public int layDiemCong() { return this.diemCong; }
    public int diemCong() { return this.diemCong; }
    public void setDiemCong(int diemCong) { this.diemCong = diemCong; }
    public void datDiemCong(int diemCong) { this.diemCong = diemCong; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public LocalDateTime getTaoLuc() { return this.taoLuc; }
    public LocalDateTime layTaoLuc() { return this.taoLuc; }
    public LocalDateTime taoLuc() { return this.taoLuc; }
    public void setTaoLuc(LocalDateTime taoLuc) { this.taoLuc = taoLuc; }
    public void datTaoLuc(LocalDateTime taoLuc) { this.taoLuc = taoLuc; }

    public LocalDateTime getThanhToanLuc() { return this.thanhToanLuc; }
    public LocalDateTime layThanhToanLuc() { return this.thanhToanLuc; }
    public LocalDateTime thanhToanLuc() { return this.thanhToanLuc; }
    public void setThanhToanLuc(LocalDateTime thanhToanLuc) { this.thanhToanLuc = thanhToanLuc; }
    public void datThanhToanLuc(LocalDateTime thanhToanLuc) { this.thanhToanLuc = thanhToanLuc; }

    public String getIdempotencyKey() { return this.idempotencyKey; }
    public String layIdempotencyKey() { return this.idempotencyKey; }
    public String idempotencyKey() { return this.idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public void datIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public long getTamTinh() { return this.tamTinh; }
    public long layTamTinh() { return this.tamTinh; }
    public long tamTinh() { return this.tamTinh; }
    public void setTamTinh(long tamTinh) { this.tamTinh = tamTinh; }
    public void datTamTinh(long tamTinh) { this.tamTinh = tamTinh; }

    public long getGiamGia() { return this.giamGia; }
    public long layGiamGia() { return this.giamGia; }
    public long giamGia() { return this.giamGia; }
    public void setGiamGia(long giamGia) { this.giamGia = giamGia; }
    public void datGiamGia(long giamGia) { this.giamGia = giamGia; }

    public int getDiemDung() { return this.diemDung; }
    public int layDiemDung() { return this.diemDung; }
    public int diemDung() { return this.diemDung; }
    public void setDiemDung(int diemDung) { this.diemDung = diemDung; }
    public void datDiemDung(int diemDung) { this.diemDung = diemDung; }

    public String getMaVoucher() { return this.maVoucher; }
    public String layMaVoucher() { return this.maVoucher; }
    public String maVoucher() { return this.maVoucher; }
    public void setMaVoucher(String maVoucher) { this.maVoucher = maVoucher; }
    public void datMaVoucher(String maVoucher) { this.maVoucher = maVoucher; }

    public String getGhiChu() { return this.ghiChu; }
    public String layGhiChu() { return this.ghiChu; }
    public String ghiChu() { return this.ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public void datGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getChiTietThanhToan() { return this.chiTietThanhToan; }
    public String layChiTietThanhToan() { return this.chiTietThanhToan; }
    public String chiTietThanhToan() { return this.chiTietThanhToan; }
    public void setChiTietThanhToan(String chiTietThanhToan) { this.chiTietThanhToan = chiTietThanhToan; }
    public void datChiTietThanhToan(String chiTietThanhToan) { this.chiTietThanhToan = chiTietThanhToan; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon that = (HoaDon) o;
        return this.id == that.id
                && Objects.equals(soHoaDon, that.soHoaDon)
                && Objects.equals(phienLamViecId, that.phienLamViecId)
                && Objects.equals(taiKhoanId, that.taiKhoanId)
                && Objects.equals(khachHangId, that.khachHangId)
                && this.tongTien == that.tongTien
                && Objects.equals(hinhThucThanhToan, that.hinhThucThanhToan)
                && this.tienKhachDua == that.tienKhachDua
                && this.tienThoi == that.tienThoi
                && this.diemCong == that.diemCong
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(taoLuc, that.taoLuc)
                && Objects.equals(thanhToanLuc, that.thanhToanLuc)
                && Objects.equals(idempotencyKey, that.idempotencyKey)
                && this.tamTinh == that.tamTinh
                && this.giamGia == that.giamGia
                && this.diemDung == that.diemDung
                && Objects.equals(maVoucher, that.maVoucher)
                && Objects.equals(ghiChu, that.ghiChu)
                && Objects.equals(chiTietThanhToan, that.chiTietThanhToan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, soHoaDon, phienLamViecId, taiKhoanId, khachHangId, tongTien, hinhThucThanhToan, tienKhachDua, tienThoi, diemCong, trangThai, taoLuc, thanhToanLuc, idempotencyKey, tamTinh, giamGia, diemDung, maVoucher, ghiChu, chiTietThanhToan);
    }

    @Override
    public String toString() {
        return "HoaDon{" + "id=" + id + ", " + "soHoaDon=" + soHoaDon + ", " + "phienLamViecId=" + phienLamViecId + ", " + "taiKhoanId=" + taiKhoanId + ", " + "khachHangId=" + khachHangId + ", " + "tongTien=" + tongTien + ", " + "hinhThucThanhToan=" + hinhThucThanhToan + ", " + "tienKhachDua=" + tienKhachDua + ", " + "tienThoi=" + tienThoi + ", " + "diemCong=" + diemCong + ", " + "trangThai=" + trangThai + ", " + "taoLuc=" + taoLuc + ", " + "thanhToanLuc=" + thanhToanLuc + ", " + "idempotencyKey=" + idempotencyKey + ", " + "tamTinh=" + tamTinh + ", " + "giamGia=" + giamGia + ", " + "diemDung=" + diemDung + ", " + "maVoucher=" + maVoucher + ", " + "ghiChu=" + ghiChu + ", " + "chiTietThanhToan=" + chiTietThanhToan + "}";
    }
}
