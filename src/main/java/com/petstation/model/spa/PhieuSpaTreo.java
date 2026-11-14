package com.petstation.model.spa;

import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class PhieuSpaTreo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String maPhieu;
    private NhanVien nhanVien;
    private KhachHang khachHang;
    private ThuCung thuCung;
    private GoiDichVuSpa goiDichVu;
    private LichHen lichHen;
    private String soDienThoai;
    private String canNang;
    private String tinhTrang;
    private String yeuCau;
    private LocalDateTime capNhatLuc;

    public PhieuSpaTreo() {
        this.soDienThoai = "";
        this.canNang = "";
        this.tinhTrang = "";
        this.yeuCau = "";
    }

    public PhieuSpaTreo(
            KhachHang khachHang,
            ThuCung thuCung,
            GoiDichVuSpa goiDichVu,
            LichHen lichHen,
            String soDienThoai,
            String canNang,
            String tinhTrang,
            String yeuCau) {
        this.khachHang = khachHang;
        this.thuCung = thuCung;
        this.goiDichVu = goiDichVu;
        this.lichHen = lichHen;
        this.soDienThoai = rongNeuNull(soDienThoai);
        this.canNang = rongNeuNull(canNang);
        this.tinhTrang = rongNeuNull(tinhTrang);
        this.yeuCau = rongNeuNull(yeuCau);
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public ThuCung getThuCung() {
        return thuCung;
    }

    public void setThuCung(ThuCung thuCung) {
        this.thuCung = thuCung;
    }

    public GoiDichVuSpa getGoiDichVu() {
        return goiDichVu;
    }

    public void setGoiDichVu(GoiDichVuSpa goiDichVu) {
        this.goiDichVu = goiDichVu;
    }

    public LichHen getLichHen() {
        return lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = rongNeuNull(soDienThoai);
    }

    public String getCanNang() {
        return canNang;
    }

    public void setCanNang(String canNang) {
        this.canNang = rongNeuNull(canNang);
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = rongNeuNull(tinhTrang);
    }

    public String getYeuCau() {
        return yeuCau;
    }

    public void setYeuCau(String yeuCau) {
        this.yeuCau = rongNeuNull(yeuCau);
    }

    public LocalDateTime getCapNhatLuc() {
        return capNhatLuc;
    }

    public void setCapNhatLuc(LocalDateTime capNhatLuc) {
        this.capNhatLuc = capNhatLuc;
    }

    private static String rongNeuNull(String s) {
        return s == null ? "" : s;
    }

    public boolean coNoiDung() {
        return !((khachHang == null
                        || khachHang.getMaKhachHang() == null
                        || khachHang.getMaKhachHang().isBlank())
                && soDienThoai.isBlank()
                && (thuCung == null
                        || thuCung.getMaThuCung() == null
                        || thuCung.getMaThuCung().isBlank())
                && (goiDichVu == null
                        || goiDichVu.getMaGoi() == null
                        || goiDichVu.getMaGoi().isBlank())
                && (lichHen == null
                        || lichHen.getMaLichHen() == null
                        || lichHen.getMaLichHen().isBlank())
                && canNang.isBlank()
                && tinhTrang.isBlank()
                && yeuCau.isBlank());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhieuSpaTreo that)) return false;
        return Objects.equals(khachHang, that.khachHang)
                && Objects.equals(soDienThoai, that.soDienThoai)
                && Objects.equals(thuCung, that.thuCung)
                && Objects.equals(goiDichVu, that.goiDichVu)
                && Objects.equals(lichHen, that.lichHen)
                && Objects.equals(canNang, that.canNang)
                && Objects.equals(tinhTrang, that.tinhTrang)
                && Objects.equals(yeuCau, that.yeuCau);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                khachHang, soDienThoai, thuCung, goiDichVu, lichHen, canNang, tinhTrang, yeuCau);
    }
}
