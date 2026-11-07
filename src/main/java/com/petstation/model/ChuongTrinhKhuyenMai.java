package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChuongTrinhKhuyenMai implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maChuongTrinh;
    private String ten;
    private String loaiGiam;
    private double giaTri;
    private LocalDateTime batDau;
    private LocalDateTime ketThuc;
    private String nguon;
    private String trangThai;
    private long donToiThieu;

    public ChuongTrinhKhuyenMai() {
        this.trangThai = "Đang áp dụng";
    }

    public ChuongTrinhKhuyenMai(String maChuongTrinh) {
        this();
        setMaChuongTrinh(maChuongTrinh);
    }

    public ChuongTrinhKhuyenMai(
            String maChuongTrinh,
            String ten,
            String loaiGiam,
            double giaTri,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String nguon,
            String trangThai,
            long donToiThieu) {
        this();
        setMaChuongTrinh(maChuongTrinh);
        setTen(ten);
        setLoaiGiam(loaiGiam);
        setGiaTri(giaTri);
        setBatDau(batDau);
        setKetThuc(ketThuc);
        setNguon(nguon);
        setTrangThai(trangThai);
        setDonToiThieu(donToiThieu);
    }

    public ChuongTrinhKhuyenMai(long id) {
        this();
        setId(id);
    }

    public ChuongTrinhKhuyenMai(
            long id,
            String maChuongTrinh,
            String ten,
            String loaiGiam,
            double giaTri,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String nguon,
            String trangThai,
            long donToiThieu) {
        this(maChuongTrinh, ten, loaiGiam, giaTri, batDau, ketThuc, nguon, trangThai, donToiThieu);
        setId(id);
    }

    public boolean kiemTraDangHieuLuc() {
        LocalDateTime hienTai = LocalDateTime.now();
        return !"Ngừng áp dụng".equalsIgnoreCase(trangThai)
                && (batDau == null || !hienTai.isBefore(batDau))
                && (ketThuc == null || !hienTai.isAfter(ketThuc));
    }

    public long tinhSoTienGiam(long tienCoSo) {
        if (tienCoSo <= 0 || tienCoSo < donToiThieu || !kiemTraDangHieuLuc()) {
            return 0L;
        }
        if ("Giảm %".equalsIgnoreCase(loaiGiam) || "PERCENT".equalsIgnoreCase(loaiGiam)) {
            return Math.min(tienCoSo, Math.round(tienCoSo * giaTri / 100.0));
        }
        return Math.min(tienCoSo, Math.round(giaTri));
    }

    public String getMaChuongTrinh() {
        return this.maChuongTrinh;
    }

    public String layMaChuongTrinh() {
        return this.maChuongTrinh;
    }

    public String maChuongTrinh() {
        return this.maChuongTrinh;
    }

    public void setMaChuongTrinh(String maChuongTrinh) {
        this.maChuongTrinh = maChuongTrinh == null ? null : maChuongTrinh.trim();
    }

    public void datMaChuongTrinh(String maChuongTrinh) {
        setMaChuongTrinh(maChuongTrinh);
    }

    public String getTen() {
        return this.ten;
    }

    public String layTen() {
        return this.ten;
    }

    public String ten() {
        return this.ten;
    }

    public void setTen(String ten) {
        this.ten = ten == null ? null : ten.trim();
    }

    public void datTen(String ten) {
        setTen(ten);
    }

    public String getLoaiGiam() {
        return this.loaiGiam;
    }

    public String layLoaiGiam() {
        return this.loaiGiam;
    }

    public String loaiGiam() {
        return this.loaiGiam;
    }

    public void setLoaiGiam(String loaiGiam) {
        this.loaiGiam = loaiGiam;
    }

    public void datLoaiGiam(String loaiGiam) {
        setLoaiGiam(loaiGiam);
    }

    public double getGiaTri() {
        return this.giaTri;
    }

    public double layGiaTri() {
        return this.giaTri;
    }

    public double giaTri() {
        return this.giaTri;
    }

    public void setGiaTri(double giaTri) {
        if (giaTri < 0)
            throw new IllegalArgumentException("Giá trị giảm không được âm.");
        this.giaTri = giaTri;
    }

    public void datGiaTri(double giaTri) {
        setGiaTri(giaTri);
    }

    public LocalDateTime getBatDau() {
        return this.batDau;
    }

    public LocalDateTime layBatDau() {
        return this.batDau;
    }

    public LocalDateTime batDau() {
        return this.batDau;
    }

    public void setBatDau(LocalDateTime batDau) {
        this.batDau = batDau;
    }

    public void datBatDau(LocalDateTime batDau) {
        setBatDau(batDau);
    }

    public LocalDateTime getKetThuc() {
        return this.ketThuc;
    }

    public LocalDateTime layKetThuc() {
        return this.ketThuc;
    }

    public LocalDateTime ketThuc() {
        return this.ketThuc;
    }

    public void setKetThuc(LocalDateTime ketThuc) {
        this.ketThuc = ketThuc;
    }

    public void datKetThuc(LocalDateTime ketThuc) {
        setKetThuc(ketThuc);
    }

    public String getNguon() {
        return this.nguon;
    }

    public String layNguon() {
        return this.nguon;
    }

    public String nguon() {
        return this.nguon;
    }

    public void setNguon(String nguon) {
        this.nguon = nguon;
    }

    public void datNguon(String nguon) {
        setNguon(nguon);
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
        setTrangThai(trangThai);
    }

    public long getDonToiThieu() {
        return this.donToiThieu;
    }

    public long layDonToiThieu() {
        return this.donToiThieu;
    }

    public long donToiThieu() {
        return this.donToiThieu;
    }

    public void setDonToiThieu(long donToiThieu) {
        if (donToiThieu < 0)
            throw new IllegalArgumentException("Đơn tối thiểu không được âm.");
        this.donToiThieu = donToiThieu;
    }

    public void datDonToiThieu(long donToiThieu) {
        setDonToiThieu(donToiThieu);
    }

    public long getId() {
        return DuLieu.layIdCauNoi(this, maChuongTrinh);
    }

    public long layId() {
        return getId();
    }

    public long id() {
        return getId();
    }

    public void setId(long id) {
        if (id < 0)
            throw new IllegalArgumentException("Mã định danh khuyến mãi không hợp lệ.");
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maChuongTrinh == null || this.maChuongTrinh.isBlank()) && id > 0) {
            this.maChuongTrinh = DuLieu.taoMaTuId("KM", 6, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChuongTrinhKhuyenMai that = (ChuongTrinhKhuyenMai) o;
        return Objects.equals(this.maChuongTrinh, that.maChuongTrinh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChuongTrinh);
    }

    @Override
    public String toString() {
        return "ChuongTrinhKhuyenMai{" + "maChuongTrinh='" + maChuongTrinh + '\'' + ", ten='" + ten
                + '\'' + ", loaiGiam='" + loaiGiam + '\'' + ", giaTri=" + giaTri + ", trangThai='" + trangThai + '\''
                + '}';
    }
}
