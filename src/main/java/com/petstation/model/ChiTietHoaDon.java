package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class ChiTietHoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    private HoaDon hoaDon;

    private SanPham sanPham;

    private LoHang loHang;

    private int soLuong;
    private long donGia;
    private long giamGia;
    private double thueGtgt;
    private String maDichVu;
    private String tenHang;

    public ChiTietHoaDon() {
        this.soLuong = 1;
    }

    public ChiTietHoaDon(String maChiTietHoaDon) {
        this();
    }

    public ChiTietHoaDon(long id) {
        this();
        setId(id);
    }

    public ChiTietHoaDon(
            HoaDon hoaDon,
            SanPham sanPham,
            LoHang loHang,
            int soLuong,
            long donGia,
            long giamGia,
            double thueGtgt,
            String maDichVu,
            String tenHang) {
        setHoaDon(hoaDon);
        setSanPham(sanPham);
        setLoHang(loHang);
        setSoLuong(soLuong);
        setDonGia(donGia);
        setGiamGia(giamGia);
        setThueGtgt(thueGtgt);
        setMaDichVu(maDichVu);
        setTenHang(tenHang);
    }

    public ChiTietHoaDon(
            String maChiTietHoaDon,
            HoaDon hoaDon,
            SanPham sanPham,
            LoHang loHang,
            int soLuong,
            long donGia,
            long giamGia,
            double thueGtgt,
            String maDichVu,
            String tenHang) {
        this(hoaDon, sanPham, loHang, soLuong, donGia, giamGia, thueGtgt, maDichVu, tenHang);
    }

    public ChiTietHoaDon(
            long id,
            HoaDon hoaDon,
            SanPham sanPham,
            LoHang loHang,
            int soLuong,
            long donGia,
            long giamGia,
            double thueGtgt,
            String maDichVu,
            String tenHang) {
        this(hoaDon, sanPham, loHang, soLuong, donGia, giamGia, thueGtgt, maDichVu, tenHang);
        setId(id);
    }

    public ChiTietHoaDon(
            long id,
            long hoaDonId,
            Long sanPhamId,
            Long loHangId,
            int soLuong,
            long donGia,
            long giamGia,
            double thueGtgt,
            String maDichVu,
            String tenHang) {
        this(
                id,
                new HoaDon(hoaDonId),
                sanPhamId == null ? null : new SanPham(sanPhamId),
                loHangId == null ? null : new LoHang(loHangId),
                soLuong,
                donGia,
                giamGia,
                thueGtgt,
                maDichVu,
                tenHang);
    }

    public long tinhThanhTienTruocGiam() {
        return (long) Math.max(0, soLuong) * Math.max(0L, donGia);
    }

    public long getThanhTienTruocGiam() {
        return tinhThanhTienTruocGiam();
    }

    public long layThanhTienTruocGiam() {
        return tinhThanhTienTruocGiam();
    }

    public long tinhTienThueGtgt() {
        long sauGiam = Math.max(0L, tinhThanhTienTruocGiam() - giamGia);
        if (thueGtgt <= 0) {
            return 0L;
        }
        return Math.round(sauGiam * thueGtgt / 100.0);
    }

    public long getTienThueGtgt() {
        return tinhTienThueGtgt();
    }

    public long layTienThueGtgt() {
        return tinhTienThueGtgt();
    }

    public long tinhThanhTien() {
        return Math.max(0L, tinhThanhTienTruocGiam() - giamGia) + tinhTienThueGtgt();
    }

    public long getThanhTien() {
        return tinhThanhTien();
    }

    public long layThanhTien() {
        return tinhThanhTien();
    }

    public long thanhTien() {
        return tinhThanhTien();
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, getMaChiTietHoaDon());
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, getMaChiTietHoaDon());
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
    }

    public void datId(long id) {
        setId(id);
    }

    public String getMaChiTietHoaDon() {
        String hd = (hoaDon != null && hoaDon.getMaHoaDon() != null) ? hoaDon.getMaHoaDon() : "";
        String sp = (sanPham != null && sanPham.getMaSanPham() != null) ? sanPham.getMaSanPham() : (maDichVu != null ? maDichVu : "");
        String lo = (loHang != null && loHang.getMaLo() != null) ? loHang.getMaLo() : "";
        return hd + "-" + sp + (lo.isEmpty() ? "" : "-" + lo);
    }

    public String layMaChiTietHoaDon() {
        return getMaChiTietHoaDon();
    }

    public String maChiTietHoaDon() {
        return getMaChiTietHoaDon();
    }

    public void setMaChiTietHoaDon(String maChiTietHoaDon) {
    }

    public void datMaChiTietHoaDon(String maChiTietHoaDon) {
        setMaChiTietHoaDon(maChiTietHoaDon);
    }

    public HoaDon getHoaDon() {
        return this.hoaDon;
    }

    public HoaDon layHoaDon() {
        return this.hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public void datHoaDon(HoaDon hoaDon) {
        setHoaDon(hoaDon);
    }

    public String getMaHoaDon() {
        return hoaDon == null ? null : hoaDon.getMaHoaDon();
    }

    public void setMaHoaDon(String maHoaDon) {
        this.hoaDon = (maHoaDon == null || maHoaDon.isBlank()) ? null : new HoaDon(maHoaDon);
    }

    public long getHoaDonId() {
        return hoaDon == null ? 0L : hoaDon.getId();
    }

    public long layHoaDonId() {
        return getHoaDonId();
    }

    public long hoaDonId() {
        return getHoaDonId();
    }

    public void setHoaDonId(long hoaDonId) {
        this.hoaDon = new HoaDon(hoaDonId);
    }

    public void datHoaDonId(long hoaDonId) {
        setHoaDonId(hoaDonId);
    }

    public SanPham getSanPham() {
        return this.sanPham;
    }

    public SanPham laySanPham() {
        return this.sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public void datSanPham(SanPham sanPham) {
        setSanPham(sanPham);
    }

    public String getMaSanPham() {
        return sanPham == null ? null : sanPham.getMaSanPham();
    }

    public void setMaSanPham(String maSanPham) {
        this.sanPham = (maSanPham == null || maSanPham.isBlank()) ? null : new SanPham(maSanPham);
    }

    public Long getSanPhamId() {
        return sanPham == null ? null : sanPham.getId();
    }

    public Long laySanPhamId() {
        return getSanPhamId();
    }

    public Long sanPhamId() {
        return getSanPhamId();
    }

    public void setSanPhamId(Long sanPhamId) {
        this.sanPham = sanPhamId == null ? null : new SanPham(sanPhamId);
    }

    public void datSanPhamId(Long sanPhamId) {
        setSanPhamId(sanPhamId);
    }

    public LoHang getLoHang() {
        return this.loHang;
    }

    public LoHang layLoHang() {
        return this.loHang;
    }

    public void setLoHang(LoHang loHang) {
        this.loHang = loHang;
    }

    public void datLoHang(LoHang loHang) {
        setLoHang(loHang);
    }

    public String getMaLo() {
        return loHang == null ? null : loHang.getMaLo();
    }

    public void setMaLo(String maLo) {
        this.loHang = (maLo == null || maLo.isBlank()) ? null : new LoHang(maLo);
    }

    public Long getLoHangId() {
        return loHang == null ? null : loHang.getId();
    }

    public Long layLoHangId() {
        return getLoHangId();
    }

    public Long loHangId() {
        return getLoHangId();
    }

    public void setLoHangId(Long loHangId) {
        this.loHang = loHangId == null ? null : new LoHang(loHangId);
    }

    public void datLoHangId(Long loHangId) {
        setLoHangId(loHangId);
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public int laySoLuong() {
        return this.soLuong;
    }

    public int soLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0)
            throw new IllegalArgumentException("Số lượng chi tiết hóa đơn không được âm.");
        this.soLuong = soLuong;
    }

    public void datSoLuong(int soLuong) {
        setSoLuong(soLuong);
    }

    public long getDonGia() {
        return this.donGia;
    }

    public long layDonGia() {
        return this.donGia;
    }

    public long donGia() {
        return this.donGia;
    }

    public void setDonGia(long donGia) {
        if (donGia < 0)
            throw new IllegalArgumentException("Đơn giá không được âm.");
        this.donGia = donGia;
    }

    public void datDonGia(long donGia) {
        setDonGia(donGia);
    }

    public long getGiamGia() {
        return this.giamGia;
    }

    public long layGiamGia() {
        return this.giamGia;
    }

    public long giamGia() {
        return this.giamGia;
    }

    public void setGiamGia(long giamGia) {
        if (giamGia < 0)
            throw new IllegalArgumentException("Giảm giá dòng hàng không được âm.");
        this.giamGia = giamGia;
    }

    public void datGiamGia(long giamGia) {
        setGiamGia(giamGia);
    }

    public double getThueGtgt() {
        return this.thueGtgt;
    }

    public double layThueGtgt() {
        return this.thueGtgt;
    }

    public double thueGtgt() {
        return this.thueGtgt;
    }

    public void setThueGtgt(double thueGtgt) {
        if (thueGtgt < 0)
            throw new IllegalArgumentException("Thuế GTGT không được âm.");
        this.thueGtgt = thueGtgt;
    }

    public void datThueGtgt(double thueGtgt) {
        setThueGtgt(thueGtgt);
    }

    public String getMaDichVu() {
        return this.maDichVu;
    }

    public String layMaDichVu() {
        return this.maDichVu;
    }

    public String maDichVu() {
        return this.maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public void datMaDichVu(String maDichVu) {
        setMaDichVu(maDichVu);
    }

    public String getTenHang() {
        return this.tenHang;
    }

    public String layTenHang() {
        return this.tenHang;
    }

    public String tenHang() {
        return this.tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }

    public void datTenHang(String tenHang) {
        setTenHang(tenHang);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(this.hoaDon, that.hoaDon)
                && Objects.equals(this.sanPham, that.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, sanPham);
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" + "hoaDon=" + (hoaDon != null ? hoaDon.getMaHoaDon() : null)
                + ", sanPham=" + (sanPham != null ? sanPham.getMaSanPham() : null)
                + ", loHang=" + (loHang != null ? loHang.getMaLo() : null)
                + ", soLuong=" + soLuong + ", donGia=" + donGia + ", thanhTien=" + tinhThanhTien() + '}';
    }
}
