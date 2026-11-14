package com.petstation.model.kho;

import com.petstation.model.chung.TrangThaiLoHang;
import com.petstation.model.chung.TrangThaiSanPham;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SanPham implements Serializable {

    private static final long serialVersionUID = 1L;
    private String maSanPham;
    private List<LoHang> danhSachLoHang = new ArrayList<>();
    private String barcode;
    private String tenSanPham;
    private String nhomHang;
    private String donVi;
    private BigDecimal giaBan;
    private BigDecimal giaNhapGanNhat;
    private int tonToiThieu;
    private boolean theoDoiHsd;
    private String thuongHieu;
    private String urlAnh;
    private String moTa;
    private String trangThai;

    public SanPham() {
        this.trangThai = TrangThaiSanPham.DANG_BAN.maDB();
        this.theoDoiHsd = false;
        this.giaBan = BigDecimal.ZERO;
        this.giaNhapGanNhat = BigDecimal.ZERO;
    }

    public SanPham(String maSanPham) {
        this();
        setMaSanPham(maSanPham);
    }

    public SanPham(
            String maSanPham, String tenSanPham, String nhomHang, String donVi, BigDecimal giaBan) {
        this();
        setMaSanPham(maSanPham);
        setTenSanPham(tenSanPham);
        setNhomHang(nhomHang);
        setDonVi(donVi);
        setGiaBan(giaBan);
    }

    public SanPham(
            String maSanPham, String tenSanPham, String nhomHang, String donVi, long giaBan) {
        this(maSanPham, tenSanPham, nhomHang, donVi, BigDecimal.valueOf(giaBan));
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham == null ? null : maSanPham.trim();
    }

    public List<LoHang> getDanhSachLoHang() {
        return danhSachLoHang;
    }

    public void setDanhSachLoHang(List<LoHang> danhSachLoHang) {
        this.danhSachLoHang =
                danhSachLoHang == null ? new ArrayList<>() : new ArrayList<>(danhSachLoHang);
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode == null ? null : barcode.trim();
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham == null ? null : tenSanPham.trim();
    }

    public String getNhomHang() {
        return nhomHang;
    }

    public void setNhomHang(String nhomHang) {
        this.nhomHang = nhomHang;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        if (giaBan != null && giaBan.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá bán không được âm.");
        }
        this.giaBan = giaBan;
    }

    public void setGiaBan(long giaBan) {
        setGiaBan(BigDecimal.valueOf(giaBan));
    }

    public long getGia() {
        return giaBan == null ? 0L : giaBan.longValue();
    }

    public BigDecimal getGiaNhapGanNhat() {
        return giaNhapGanNhat;
    }

    public void setGiaNhapGanNhat(BigDecimal giaNhapGanNhat) {
        if (giaNhapGanNhat != null && giaNhapGanNhat.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá nhập không được âm.");
        }
        this.giaNhapGanNhat = giaNhapGanNhat;
    }

    public void setGiaNhapGanNhat(long giaNhapGanNhat) {
        setGiaNhapGanNhat(BigDecimal.valueOf(giaNhapGanNhat));
    }

    public int getTonToiThieu() {
        return tonToiThieu;
    }

    public void setTonToiThieu(int tonToiThieu) {
        if (tonToiThieu < 0) {
            throw new IllegalArgumentException("Tồn tối thiểu không được âm.");
        }
        this.tonToiThieu = tonToiThieu;
    }

    public boolean isTheoDoiHsd() {
        return theoDoiHsd;
    }

    public void setTheoDoiHsd(boolean theoDoiHsd) {
        this.theoDoiHsd = theoDoiHsd;
    }

    public String getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(String thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public String getUrlAnh() {
        return urlAnh;
    }

    public void setUrlAnh(String urlAnh) {
        this.urlAnh = urlAnh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public TrangThaiSanPham getTrangThaiEnum() {
        TrangThaiSanPham kq = TrangThaiSanPham.tuMaDB(this.trangThai);
        return kq != null ? kq : TrangThaiSanPham.NGUNG_BAN;
    }

    public int tinhTongTonKhoKhaDung() {
        if (danhSachLoHang == null || danhSachLoHang.isEmpty()) {
            return 0;
        }
        LocalDate homNay = LocalDate.now();
        int tong = 0;
        for (LoHang lo : danhSachLoHang) {
            if (lo != null
                    && TrangThaiLoHang.SAN_SANG.maDB().equalsIgnoreCase(lo.getTrangThai())
                    && lo.getSoLuongTon() > 0
                    && (lo.getHanSuDung() == null || !lo.getHanSuDung().isBefore(homNay))) {
                tong += lo.getSoLuongTon();
            }
        }
        return tong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham that = (SanPham) o;
        return Objects.equals(this.maSanPham, that.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }

    @Override
    public String toString() {
        return "SanPham{"
                + "maSanPham='"
                + maSanPham
                + '\''
                + ", tenSanPham='"
                + tenSanPham
                + '\''
                + ", giaBan="
                + giaBan
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
