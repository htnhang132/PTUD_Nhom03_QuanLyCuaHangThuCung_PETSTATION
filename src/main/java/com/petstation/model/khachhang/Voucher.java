package com.petstation.model.khachhang;

import com.petstation.model.chung.TrangThaiVoucher;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;
    private String maVoucher;
    private KhachHang khachHang;
    private long menhGia;
    private LocalDateTime hieuLucDen;
    private LocalDateTime suDungLuc;
    private boolean chiApDungSpa;
    private int soLuong;
    private int daDung;
    private long donToiThieu;
    private String trangThai;
    private String khoaDoiDiem;

    public Voucher() {
        this.soLuong = 1;
        this.trangThai = TrangThaiVoucher.CON_HAN.maDB();
    }

    public Voucher(String maVoucher) {
        this();
        setMaVoucher(maVoucher);
    }

    public Voucher(
            String maVoucher,
            KhachHang khachHang,
            long menhGia,
            LocalDateTime hieuLucDen,
            LocalDateTime suDungLuc,
            boolean chiApDungSpa,
            int soLuong,
            int daDung,
            long donToiThieu,
            String trangThai,
            String khoaDoiDiem) {
        this();
        setMaVoucher(maVoucher);
        setKhachHang(khachHang);
        setMenhGia(menhGia);
        setHieuLucDen(hieuLucDen);
        setSuDungLuc(suDungLuc);
        setChiApDungSpa(chiApDungSpa);
        setSoLuong(soLuong);
        setDaDung(daDung);
        setDonToiThieu(donToiThieu);
        setTrangThai(trangThai);
        setKhoaDoiDiem(khoaDoiDiem);
    }

    public String getMaVoucher() {
        return this.maVoucher;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher == null ? null : maVoucher.trim();
    }

    public KhachHang getKhachHang() {
        return this.khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public long getMenhGia() {
        return this.menhGia;
    }

    public void setMenhGia(long menhGia) {
        if (menhGia < 0) throw new IllegalArgumentException("Mệnh giá voucher không được âm.");
        this.menhGia = menhGia;
    }

    public LocalDateTime getHieuLucDen() {
        return this.hieuLucDen;
    }

    public void setHieuLucDen(LocalDateTime hieuLucDen) {
        this.hieuLucDen = hieuLucDen;
    }

    public LocalDateTime getSuDungLuc() {
        return this.suDungLuc;
    }

    public void setSuDungLuc(LocalDateTime suDungLuc) {
        this.suDungLuc = suDungLuc;
    }

    public boolean isChiApDungSpa() {
        return this.chiApDungSpa;
    }

    public void setChiApDungSpa(boolean chiApDungSpa) {
        this.chiApDungSpa = chiApDungSpa;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0) throw new IllegalArgumentException("Số lượng phát hành không được âm.");
        this.soLuong = soLuong;
    }

    public int getDaDung() {
        return this.daDung;
    }

    public void setDaDung(int daDung) {
        if (daDung < 0) throw new IllegalArgumentException("Số lượt đã dùng không được âm.");
        this.daDung = daDung;
    }

    public long getDonToiThieu() {
        return this.donToiThieu;
    }

    public void setDonToiThieu(long donToiThieu) {
        if (donToiThieu < 0) throw new IllegalArgumentException("Đơn tối thiểu không được âm.");
        this.donToiThieu = donToiThieu;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getKhoaDoiDiem() {
        return this.khoaDoiDiem;
    }

    public void setKhoaDoiDiem(String khoaDoiDiem) {
        this.khoaDoiDiem = khoaDoiDiem;
    }

    public int tinhSoLuotConLai() {
        return Math.max(0, soLuong - daDung);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voucher that = (Voucher) o;
        return Objects.equals(this.maVoucher, that.maVoucher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maVoucher);
    }

    @Override
    public String toString() {
        return "Voucher{"
                + "maVoucher='"
                + maVoucher
                + '\''
                + ", menhGia="
                + menhGia
                + ", soLuotConLai="
                + tinhSoLuotConLai()
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
