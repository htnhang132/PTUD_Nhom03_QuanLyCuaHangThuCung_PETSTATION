package com.petstation.model.luutru;

import com.petstation.model.banhang.PhienLamViec;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class LichSuThuCocHotel implements Serializable {
    private static final long serialVersionUID = 1L;

    private HopDongLuuTru hopDong;
    private PhienLamViec phienLamViec;
    private NhanVien nhanVienThu;
    private String khoaThuCoc;
    private String phuongThuc;
    private long soTien;
    private long khachDua;
    private LocalDateTime taoLuc;
    private long tienThua;

    public LichSuThuCocHotel() {
        this.taoLuc = LocalDateTime.now();
    }

    public LichSuThuCocHotel(
            HopDongLuuTru hopDong,
            PhienLamViec phienLamViec,
            NhanVien nhanVienThu,
            String khoaThuCoc,
            String phuongThuc,
            long soTien,
            long khachDua,
            LocalDateTime taoLuc,
            long tienThua) {
        this.hopDong = hopDong;
        this.phienLamViec = phienLamViec;
        this.nhanVienThu = nhanVienThu;
        this.khoaThuCoc = khoaThuCoc;
        this.phuongThuc = phuongThuc;
        this.soTien = soTien;
        this.khachDua = khachDua;
        this.taoLuc = taoLuc;
        this.tienThua = tienThua;
    }

    public HopDongLuuTru getHopDong() {
        return this.hopDong;
    }

    public void setHopDong(HopDongLuuTru hopDong) {
        this.hopDong = hopDong;
    }

    public PhienLamViec getPhienLamViec() {
        return this.phienLamViec;
    }

    public void setPhienLamViec(PhienLamViec phienLamViec) {
        this.phienLamViec = phienLamViec;
    }

    public NhanVien getNhanVienThu() {
        return this.nhanVienThu;
    }

    public void setNhanVienThu(NhanVien nhanVienThu) {
        this.nhanVienThu = nhanVienThu;
    }

    public String getKhoaThuCoc() {
        return this.khoaThuCoc;
    }

    public void setKhoaThuCoc(String khoaThuCoc) {
        this.khoaThuCoc = khoaThuCoc;
    }

    public String getPhuongThuc() {
        return this.phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public long getSoTien() {
        return this.soTien;
    }

    public void setSoTien(long soTien) {
        this.soTien = soTien;
    }

    public long getKhachDua() {
        return this.khachDua;
    }

    public void setKhachDua(long khachDua) {
        this.khachDua = khachDua;
    }

    public LocalDateTime getTaoLuc() {
        return this.taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    public long getTienThua() {
        return this.tienThua;
    }

    public void setTienThua(long tienThua) {
        this.tienThua = tienThua;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LichSuThuCocHotel that = (LichSuThuCocHotel) o;
        return Objects.equals(hopDong, that.hopDong)
                && Objects.equals(khoaThuCoc, that.khoaThuCoc)
                && Objects.equals(phuongThuc, that.phuongThuc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hopDong, khoaThuCoc, phuongThuc);
    }

    @Override
    public String toString() {
        return "LichSuThuCocHotel{"
                + "hopDong='"
                + (hopDong != null ? hopDong.getMaHopDong() : null)
                + '\''
                + ", khoaThuCoc='"
                + khoaThuCoc
                + '\''
                + ", phuongThuc='"
                + phuongThuc
                + '\''
                + ", soTien="
                + soTien
                + '}';
    }
}
