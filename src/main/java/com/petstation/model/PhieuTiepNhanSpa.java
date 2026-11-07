package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class PhieuTiepNhanSpa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieu;
    private LichHen lichHen;
    private ThuCung thuCung;
    private GoiDichVuSpa goiDichVu;
    private NhanVien nhanVien;
    private HoaDon hoaDon;
    private String moTaDichVu;
    private long donGia;
    private int soLuong;
    private double canNangThucTe;
    private long tongTien;
    private String trangThai;
    private LocalDateTime taoLuc;
    private String ghiChu;
    private String anh;

    public PhieuTiepNhanSpa() {
        this.soLuong = 1;
        this.trangThai = "TIEP_NHAN";
        this.taoLuc = LocalDateTime.now();
    }

    public PhieuTiepNhanSpa(String maPhieu) {
        this();
        setMaPhieu(maPhieu);
    }

    public PhieuTiepNhanSpa(
            String maPhieu,
            LichHen lichHen,
            ThuCung thuCung,
            GoiDichVuSpa goiDichVu,
            String moTaDichVu,
            long donGia,
            int soLuong,
            double canNangThucTe,
            String trangThai,
            LocalDateTime taoLuc,
            NhanVien nhanVien,
            String ghiChu,
            String anh,
            HoaDon hoaDon) {
        this();
        setMaPhieu(maPhieu);
        setLichHen(lichHen);
        setThuCung(thuCung);
        setGoiDichVu(goiDichVu);
        setMoTaDichVu(moTaDichVu);
        setDonGia(donGia);
        setSoLuong(soLuong);
        setCanNangThucTe(canNangThucTe);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
        setNhanVien(nhanVien);
        setGhiChu(ghiChu);
        setAnh(anh);
        setHoaDon(hoaDon);
    }

    public long tinhTongTien() {
        long thanhTien = (long) Math.max(0, soLuong) * Math.max(0L, donGia);
        return thanhTien > 0 ? thanhTien : Math.max(0L, this.tongTien);
    }

    public boolean kiemTraDaThanhToan() {
        return hoaDon != null && hoaDon.getMaHoaDon() != null && !hoaDon.getMaHoaDon().isBlank();
    }

    public String getMaPhieu() {
        return this.maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu == null ? null : maPhieu.trim();
    }

    public LichHen getLichHen() {
        return this.lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
    }

    public String getMaLichHen() {
        return lichHen == null ? null : lichHen.getMaLichHen();
    }

    public void setMaLichHen(String maLichHen) {
        this.lichHen = (maLichHen == null || maLichHen.isBlank()) ? null : new LichHen(maLichHen);
    }

    public ThuCung getThuCung() {
        return this.thuCung;
    }

    public void setThuCung(ThuCung thuCung) {
        this.thuCung = thuCung;
    }

    public String getMaThuCung() {
        return thuCung == null ? null : thuCung.getMaThuCung();
    }

    public void setMaThuCung(String maThuCung) {
        this.thuCung = (maThuCung == null || maThuCung.isBlank()) ? null : new ThuCung(maThuCung);
    }

    public GoiDichVuSpa getGoiDichVu() {
        return this.goiDichVu;
    }

    public void setGoiDichVu(GoiDichVuSpa goiDichVu) {
        this.goiDichVu = goiDichVu;
    }

    public String getMaGoi() {
        return goiDichVu == null ? null : goiDichVu.getMaGoi();
    }

    public void setMaGoi(String maGoi) {
        this.goiDichVu = (maGoi == null || maGoi.isBlank()) ? null : new GoiDichVuSpa(maGoi);
    }

    public String getMoTaDichVu() {
        return this.moTaDichVu;
    }

    public void setMoTaDichVu(String moTaDichVu) {
        this.moTaDichVu = moTaDichVu;
    }

    public long getDonGia() {
        return this.donGia;
    }

    public void setDonGia(long donGia) {
        if (donGia < 0)
            throw new IllegalArgumentException("Đơn giá dịch vụ Spa không được âm.");
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0)
            throw new IllegalArgumentException("Số lượng dịch vụ Spa không được âm.");
        this.soLuong = soLuong;
    }

    public double getCanNangThucTe() {
        return this.canNangThucTe;
    }

    public void setCanNangThucTe(double canNangThucTe) {
        if (canNangThucTe < 0)
            throw new IllegalArgumentException("Cân nặng thực tế không được âm.");
        this.canNangThucTe = canNangThucTe;
    }

    public long getTongTien() {
        return tinhTongTien();
    }

    public void setTongTien(long tongTien) {
        this.tongTien = Math.max(0L, tongTien);
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getTaoLuc() {
        return this.taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public String getMaNhanVien() {
        return nhanVien == null ? null : nhanVien.getMaNhanVien();
    }

    public void setMaNhanVien(String maNhanVien) {
        this.nhanVien = (maNhanVien == null || maNhanVien.isBlank()) ? null : new NhanVien(maNhanVien);
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getAnh() {
        return this.anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public HoaDon getHoaDon() {
        return this.hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public String getMaHoaDon() {
        return hoaDon == null ? null : hoaDon.getMaHoaDon();
    }

    public void setMaHoaDon(String maHoaDon) {
        this.hoaDon = (maHoaDon == null || maHoaDon.isBlank()) ? null : new HoaDon(maHoaDon);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PhieuTiepNhanSpa that = (PhieuTiepNhanSpa) o;
        return Objects.equals(this.maPhieu, that.maPhieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieu);
    }

    @Override
    public String toString() {
        return "PhieuTiepNhanSpa{" + "maPhieu='" + maPhieu + '\'' + ", thuCung=" + (thuCung != null ? thuCung.getMaThuCung() : null)
                + ", goi=" + (goiDichVu != null ? goiDichVu.getMaGoi() : null) + ", tongTien=" + tinhTongTien() + ", trangThai='" + trangThai + '\'' + '}';
    }
}
