package com.petstation.model.banhang;

import com.petstation.model.chung.TrangThaiPhienLamViec;
import com.petstation.model.nhansu.LichLamViec;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class PhienLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhien;
    private LichLamViec lichLamViec;
    private NhanVien nhanVien;
    private NhanVien nguoiNhanBanGiao;
    private String tenCa;
    private LocalDateTime moLuc;
    private LocalDateTime dongLuc;
    private long tienDauCa;
    private long tienThucTe;
    private String giaiTrinh;
    private String hinhThucBanGiao;
    private String trangThai;
    private long chenhLech;

    public PhienLamViec() {
        this.trangThai = TrangThaiPhienLamViec.DANG_MO.maDB();
        this.moLuc = LocalDateTime.now();
    }

    public PhienLamViec(String maPhien) {
        this();
        setMaPhien(maPhien);
    }

    public PhienLamViec(
            String maPhien,
            LichLamViec lichLamViec,
            NhanVien nhanVien,
            NhanVien nguoiNhanBanGiao,
            String tenCa,
            LocalDateTime moLuc,
            LocalDateTime dongLuc,
            long tienDauCa,
            long tienThucTe,
            String giaiTrinh,
            String hinhThucBanGiao,
            String trangThai,
            long chenhLech) {
        this();
        setMaPhien(maPhien);
        setLichLamViec(lichLamViec);
        setNhanVien(nhanVien);
        setNguoiNhanBanGiao(nguoiNhanBanGiao);
        setTenCa(tenCa);
        setMoLuc(moLuc);
        setDongLuc(dongLuc);
        setTienDauCa(tienDauCa);
        setTienThucTe(tienThucTe);
        setGiaiTrinh(giaiTrinh);
        setHinhThucBanGiao(hinhThucBanGiao);
        setTrangThai(trangThai);
        setChenhLech(chenhLech);
    }

    public String getMaPhien() {
        return this.maPhien;
    }

    public void setMaPhien(String maPhien) {
        this.maPhien = maPhien == null ? null : maPhien.trim();
    }

    public LichLamViec getLichLamViec() {
        return this.lichLamViec;
    }

    public void setLichLamViec(LichLamViec lichLamViec) {
        this.lichLamViec = lichLamViec;
    }

    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public NhanVien getNguoiNhanBanGiao() {
        return this.nguoiNhanBanGiao;
    }

    public void setNguoiNhanBanGiao(NhanVien nguoiNhanBanGiao) {
        this.nguoiNhanBanGiao = nguoiNhanBanGiao;
    }

    public String getTenCa() {
        return this.tenCa;
    }

    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }

    public LocalDateTime getMoLuc() {
        return this.moLuc;
    }

    public void setMoLuc(LocalDateTime moLuc) {
        this.moLuc = moLuc;
    }

    public LocalDateTime getDongLuc() {
        return this.dongLuc;
    }

    public void setDongLuc(LocalDateTime dongLuc) {
        this.dongLuc = dongLuc;
    }

    public long getTienDauCa() {
        return this.tienDauCa;
    }

    public void setTienDauCa(long tienDauCa) {
        if (tienDauCa < 0) throw new IllegalArgumentException("Tiền đầu ca không được âm.");
        this.tienDauCa = tienDauCa;
    }

    public long getTienThucTe() {
        return this.tienThucTe;
    }

    public void setTienThucTe(long tienThucTe) {
        if (tienThucTe < 0) throw new IllegalArgumentException("Tiền thực tế không được âm.");
        this.tienThucTe = tienThucTe;
    }

    public String getGiaiTrinh() {
        return this.giaiTrinh;
    }

    public void setGiaiTrinh(String giaiTrinh) {
        this.giaiTrinh = giaiTrinh;
    }

    public String getHinhThucBanGiao() {
        return this.hinhThucBanGiao;
    }

    public void setHinhThucBanGiao(String hinhThucBanGiao) {
        this.hinhThucBanGiao = hinhThucBanGiao;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public long getChenhLech() {
        return this.chenhLech;
    }

    public void setChenhLech(long chenhLech) {
        this.chenhLech = chenhLech;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhienLamViec that = (PhienLamViec) o;
        return Objects.equals(this.maPhien, that.maPhien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhien);
    }

    @Override
    public String toString() {
        return "PhienLamViec{"
                + "maPhien='"
                + maPhien
                + '\''
                + ", nhanVien="
                + (nhanVien == null ? null : nhanVien.getMaNhanVien())
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
