package com.petstation.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class PhienLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhien;
    private LichLamViec lichLamViec;
    private TaiKhoan taiKhoan;
    private LocalDateTime moLuc;
    private LocalDateTime dongLuc;
    private long tienDauCa;
    private long tienThucTe;
    private long chenhLech;
    private String giaiTrinh;
    private String trangThai;
    private String tenCa;
    private TaiKhoan nguoiNhanBanGiao;
    private String hinhThucBanGiao;

    public PhienLamViec() {
        this.trangThai = "DANG_MO";
        this.moLuc = LocalDateTime.now();
    }

    public PhienLamViec(String maPhien) {
        this();
        setMaPhien(maPhien);
    }

    public PhienLamViec(
            String maPhien,
            LichLamViec lichLamViec,
            TaiKhoan taiKhoan,
            LocalDateTime moLuc,
            LocalDateTime dongLuc,
            long tienDauCa,
            long tienThucTe,
            long chenhLech,
            String giaiTrinh,
            String trangThai,
            String tenCa,
            TaiKhoan nguoiNhanBanGiao,
            String hinhThucBanGiao) {
        this();
        setMaPhien(maPhien);
        setLichLamViec(lichLamViec);
        setTaiKhoan(taiKhoan);
        setMoLuc(moLuc);
        setDongLuc(dongLuc);
        setTienDauCa(tienDauCa);
        setTienThucTe(tienThucTe);
        setChenhLech(chenhLech);
        setGiaiTrinh(giaiTrinh);
        setTrangThai(trangThai);
        setTenCa(tenCa);
        setNguoiNhanBanGiao(nguoiNhanBanGiao);
        setHinhThucBanGiao(hinhThucBanGiao);
    }

    public long tinhChenhLechKetTien(long tongThuTienMatTrongCa) {
        return tienThucTe - (tienDauCa + tongThuTienMatTrongCa);
    }

    public long tinhSoPhutPhien() {
        if (moLuc == null) return 0L;
        LocalDateTime ketThuc = dongLuc != null ? dongLuc : LocalDateTime.now();
        return Math.max(0L, Duration.between(moLuc, ketThuc).toMinutes());
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

    public String getMaLichLamViec() {
        return lichLamViec == null ? null : lichLamViec.getMaLichLamViec();
    }

    public void setMaLichLamViec(String maLichLamViec) {
        this.lichLamViec = (maLichLamViec == null || maLichLamViec.isBlank()) ? null : new LichLamViec(maLichLamViec);
    }

    public TaiKhoan getTaiKhoan() {
        return this.taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getTenDangNhap() {
        return taiKhoan == null ? null : taiKhoan.getTenDangNhap();
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.taiKhoan = (tenDangNhap == null || tenDangNhap.isBlank()) ? null : new TaiKhoan(tenDangNhap);
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

    public long getChenhLech() {
        return this.chenhLech;
    }

    public void setChenhLech(long chenhLech) {
        this.chenhLech = chenhLech;
    }

    public String getGiaiTrinh() {
        return this.giaiTrinh;
    }

    public void setGiaiTrinh(String giaiTrinh) {
        this.giaiTrinh = giaiTrinh;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getTenCa() {
        return this.tenCa;
    }

    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }

    public TaiKhoan getNguoiNhanBanGiao() {
        return this.nguoiNhanBanGiao;
    }

    public void setNguoiNhanBanGiao(TaiKhoan nguoiNhanBanGiao) {
        this.nguoiNhanBanGiao = nguoiNhanBanGiao;
    }

    public String getTenDangNhapNguoiNhanBanGiao() {
        return nguoiNhanBanGiao == null ? null : nguoiNhanBanGiao.getTenDangNhap();
    }

    public void setTenDangNhapNguoiNhanBanGiao(String tenDangNhap) {
        this.nguoiNhanBanGiao = (tenDangNhap == null || tenDangNhap.isBlank()) ? null : new TaiKhoan(tenDangNhap);
    }

    public String getHinhThucBanGiao() {
        return this.hinhThucBanGiao;
    }

    public void setHinhThucBanGiao(String hinhThucBanGiao) {
        this.hinhThucBanGiao = hinhThucBanGiao;
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
        return "PhienLamViec{" + "maPhien='" + maPhien + '\''
                + ", taiKhoan=" + (taiKhoan == null ? null : taiKhoan.getTenDangNhap())
                + ", trangThai='" + trangThai + '\'' + '}';
    }
}
