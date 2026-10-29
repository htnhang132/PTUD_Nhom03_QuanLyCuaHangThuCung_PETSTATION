package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể voucher / phiếu giảm giá khớp bảng [voucher].
 */
public class Voucher implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maVoucher;
    private Long khachHangId;
    private long menhGia;
    private LocalDateTime hieuLucDen;
    private LocalDateTime suDungLuc;
    private boolean chiApDungSpa;
    private int soLuong;
    private int daDung;
    private long donToiThieu;
    private String trangThai;
    private String khoaDoiDiem;

    public Voucher() {}

    public Voucher(long id, String maVoucher, Long khachHangId, long menhGia, LocalDateTime hieuLucDen, LocalDateTime suDungLuc, boolean chiApDungSpa, int soLuong, int daDung, long donToiThieu, String trangThai, String khoaDoiDiem) {
        this.id = id;
        this.maVoucher = maVoucher;
        this.khachHangId = khachHangId;
        this.menhGia = menhGia;
        this.hieuLucDen = hieuLucDen;
        this.suDungLuc = suDungLuc;
        this.chiApDungSpa = chiApDungSpa;
        this.soLuong = soLuong;
        this.daDung = daDung;
        this.donToiThieu = donToiThieu;
        this.trangThai = trangThai;
        this.khoaDoiDiem = khoaDoiDiem;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaVoucher() { return this.maVoucher; }
    public String layMaVoucher() { return this.maVoucher; }
    public String maVoucher() { return this.maVoucher; }
    public void setMaVoucher(String maVoucher) { this.maVoucher = maVoucher; }
    public void datMaVoucher(String maVoucher) { this.maVoucher = maVoucher; }

    public Long getKhachHangId() { return this.khachHangId; }
    public Long layKhachHangId() { return this.khachHangId; }
    public Long khachHangId() { return this.khachHangId; }
    public void setKhachHangId(Long khachHangId) { this.khachHangId = khachHangId; }
    public void datKhachHangId(Long khachHangId) { this.khachHangId = khachHangId; }

    public long getMenhGia() { return this.menhGia; }
    public long layMenhGia() { return this.menhGia; }
    public long menhGia() { return this.menhGia; }
    public void setMenhGia(long menhGia) { this.menhGia = menhGia; }
    public void datMenhGia(long menhGia) { this.menhGia = menhGia; }

    public LocalDateTime getHieuLucDen() { return this.hieuLucDen; }
    public LocalDateTime layHieuLucDen() { return this.hieuLucDen; }
    public LocalDateTime hieuLucDen() { return this.hieuLucDen; }
    public void setHieuLucDen(LocalDateTime hieuLucDen) { this.hieuLucDen = hieuLucDen; }
    public void datHieuLucDen(LocalDateTime hieuLucDen) { this.hieuLucDen = hieuLucDen; }

    public LocalDateTime getSuDungLuc() { return this.suDungLuc; }
    public LocalDateTime laySuDungLuc() { return this.suDungLuc; }
    public LocalDateTime suDungLuc() { return this.suDungLuc; }
    public void setSuDungLuc(LocalDateTime suDungLuc) { this.suDungLuc = suDungLuc; }
    public void datSuDungLuc(LocalDateTime suDungLuc) { this.suDungLuc = suDungLuc; }

    public boolean isChiApDungSpa() { return this.chiApDungSpa; }
    public boolean layChiApDungSpa() { return this.chiApDungSpa; }
    public boolean chiApDungSpa() { return this.chiApDungSpa; }
    public void setChiApDungSpa(boolean chiApDungSpa) { this.chiApDungSpa = chiApDungSpa; }
    public void datChiApDungSpa(boolean chiApDungSpa) { this.chiApDungSpa = chiApDungSpa; }

    public int getSoLuong() { return this.soLuong; }
    public int laySoLuong() { return this.soLuong; }
    public int soLuong() { return this.soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void datSoLuong(int soLuong) { this.soLuong = soLuong; }

    public int getDaDung() { return this.daDung; }
    public int layDaDung() { return this.daDung; }
    public int daDung() { return this.daDung; }
    public void setDaDung(int daDung) { this.daDung = daDung; }
    public void datDaDung(int daDung) { this.daDung = daDung; }

    public long getDonToiThieu() { return this.donToiThieu; }
    public long layDonToiThieu() { return this.donToiThieu; }
    public long donToiThieu() { return this.donToiThieu; }
    public void setDonToiThieu(long donToiThieu) { this.donToiThieu = donToiThieu; }
    public void datDonToiThieu(long donToiThieu) { this.donToiThieu = donToiThieu; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getKhoaDoiDiem() { return this.khoaDoiDiem; }
    public String layKhoaDoiDiem() { return this.khoaDoiDiem; }
    public String khoaDoiDiem() { return this.khoaDoiDiem; }
    public void setKhoaDoiDiem(String khoaDoiDiem) { this.khoaDoiDiem = khoaDoiDiem; }
    public void datKhoaDoiDiem(String khoaDoiDiem) { this.khoaDoiDiem = khoaDoiDiem; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voucher that = (Voucher) o;
        return this.id == that.id
                && Objects.equals(maVoucher, that.maVoucher)
                && Objects.equals(khachHangId, that.khachHangId)
                && this.menhGia == that.menhGia
                && Objects.equals(hieuLucDen, that.hieuLucDen)
                && Objects.equals(suDungLuc, that.suDungLuc)
                && this.chiApDungSpa == that.chiApDungSpa
                && this.soLuong == that.soLuong
                && this.daDung == that.daDung
                && this.donToiThieu == that.donToiThieu
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(khoaDoiDiem, that.khoaDoiDiem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maVoucher, khachHangId, menhGia, hieuLucDen, suDungLuc, chiApDungSpa, soLuong, daDung, donToiThieu, trangThai, khoaDoiDiem);
    }

    @Override
    public String toString() {
        return "Voucher{" + "id=" + id + ", " + "maVoucher=" + maVoucher + ", " + "khachHangId=" + khachHangId + ", " + "menhGia=" + menhGia + ", " + "hieuLucDen=" + hieuLucDen + ", " + "suDungLuc=" + suDungLuc + ", " + "chiApDungSpa=" + chiApDungSpa + ", " + "soLuong=" + soLuong + ", " + "daDung=" + daDung + ", " + "donToiThieu=" + donToiThieu + ", " + "trangThai=" + trangThai + ", " + "khoaDoiDiem=" + khoaDoiDiem + "}";
    }
}
