package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Chi tiết phiếu nhập kho khớp bảng [chi_tiet_phieu_nhap].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính phức hợp là cặp (phieuNhap, sanPham, soLo). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp với PhieuNhapKho và SanPham.
 * - Phương thức tính thành tiền dòng nhập dẫn xuất và equals/hashCode theo khóa chính.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class ChiTietPhieuNhap implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Quan hệ kết hợp: Dòng chi tiết thuộc về một Phiếu nhập kho. */
    private PhieuNhapKho phieuNhap;
    /** Quan hệ kết hợp: Sản phẩm nhập kho. */
    private SanPham sanPham;
    private String soLo;
    private LocalDate hanSuDung;
    private int soLuong;
    private long giaNhap;

    /** Constructor mặc định. */
    public ChiTietPhieuNhap() {
        this.soLuong = 1;
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public ChiTietPhieuNhap(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận đối tượng PhieuNhapKho và SanPham. */
    public ChiTietPhieuNhap(
            PhieuNhapKho phieuNhap,
            SanPham sanPham,
            String soLo,
            LocalDate hanSuDung,
            int soLuong,
            long giaNhap) {
        setPhieuNhap(phieuNhap);
        setSanPham(sanPham);
        setSoLo(soLo);
        setHanSuDung(hanSuDung);
        setSoLuong(soLuong);
        setGiaNhap(giaNhap);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public ChiTietPhieuNhap(
            long id,
            PhieuNhapKho phieuNhap,
            SanPham sanPham,
            String soLo,
            LocalDate hanSuDung,
            int soLuong,
            long giaNhap) {
        this(phieuNhap, sanPham, soLo, hanSuDung, soLuong, giaNhap);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public ChiTietPhieuNhap(
            long id,
            long phieuNhapId,
            long sanPhamId,
            String soLo,
            LocalDate hanSuDung,
            int soLuong,
            long giaNhap) {
        this(id, new PhieuNhapKho(phieuNhapId), new SanPham(sanPhamId), soLo, hanSuDung, soLuong, giaNhap);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /**
     * Tính thành tiền của dòng phiếu nhập kho (thuộc tính dẫn xuất: soLuong * giaNhap).
     */
    public long tinhThanhTien() {
        return (long) Math.max(0, soLuong) * Math.max(0L, giaNhap);
    }

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

    public Long getId() {
        String key = (phieuNhap != null ? phieuNhap.getSoPhieu() : "") + "_" + (sanPham != null ? sanPham.getMaSanPham() : "") + "_" + soLo;
        return DuLieu.layIdCauNoiObj(this, key);
    }

    public long layId() {
        String key = (phieuNhap != null ? phieuNhap.getSoPhieu() : "") + "_" + (sanPham != null ? sanPham.getMaSanPham() : "") + "_" + soLo;
        return DuLieu.layIdCauNoi(this, key);
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

    // ==================== GETTER & SETTER THUỘC TÍNH ====================

    public PhieuNhapKho getPhieuNhap() {
        return this.phieuNhap;
    }

    public PhieuNhapKho layPhieuNhap() {
        return this.phieuNhap;
    }

    public void setPhieuNhap(PhieuNhapKho phieuNhap) {
        this.phieuNhap = phieuNhap;
    }

    public void datPhieuNhap(PhieuNhapKho phieuNhap) {
        setPhieuNhap(phieuNhap);
    }

    public long getPhieuNhapId() {
        return phieuNhap == null ? 0L : phieuNhap.getId();
    }

    public long layPhieuNhapId() {
        return getPhieuNhapId();
    }

    public long phieuNhapId() {
        return getPhieuNhapId();
    }

    public void setPhieuNhapId(long phieuNhapId) {
        this.phieuNhap = new PhieuNhapKho(phieuNhapId);
    }

    public void datPhieuNhapId(long phieuNhapId) {
        setPhieuNhapId(phieuNhapId);
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

    public long getSanPhamId() {
        return (sanPham == null || sanPham.getId() == null) ? 0L : sanPham.getId();
    }

    public long laySanPhamId() {
        return getSanPhamId();
    }

    public long sanPhamId() {
        return getSanPhamId();
    }

    public void setSanPhamId(long sanPhamId) {
        this.sanPham = new SanPham(sanPhamId);
    }

    public void datSanPhamId(long sanPhamId) {
        setSanPhamId(sanPhamId);
    }

    public String getSoLo() {
        return this.soLo;
    }

    public String laySoLo() {
        return this.soLo;
    }

    public String soLo() {
        return this.soLo;
    }

    public void setSoLo(String soLo) {
        this.soLo = soLo == null ? null : soLo.trim();
    }

    public void datSoLo(String soLo) {
        setSoLo(soLo);
    }

    public LocalDate getHanSuDung() {
        return this.hanSuDung;
    }

    public LocalDate layHanSuDung() {
        return this.hanSuDung;
    }

    public LocalDate hanSuDung() {
        return this.hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public void datHanSuDung(LocalDate hanSuDung) {
        setHanSuDung(hanSuDung);
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
            throw new IllegalArgumentException("Số lượng nhập không được âm.");
        this.soLuong = soLuong;
    }

    public void datSoLuong(int soLuong) {
        setSoLuong(soLuong);
    }

    public long getGiaNhap() {
        return this.giaNhap;
    }

    public long layGiaNhap() {
        return this.giaNhap;
    }

    public long giaNhap() {
        return this.giaNhap;
    }

    public void setGiaNhap(long giaNhap) {
        if (giaNhap < 0)
            throw new IllegalArgumentException("Giá nhập không được âm.");
        this.giaNhap = giaNhap;
    }

    public void datGiaNhap(long giaNhap) {
        setGiaNhap(giaNhap);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChiTietPhieuNhap that = (ChiTietPhieuNhap) o;
        return Objects.equals(this.phieuNhap, that.phieuNhap)
                && Objects.equals(this.sanPham, that.sanPham)
                && Objects.equals(this.soLo, that.soLo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuNhap, sanPham, soLo);
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{" + "phieuNhapId=" + getPhieuNhapId() + ", sanPhamId=" + getSanPhamId()
                + ", soLo='" + soLo + '\'' + ", soLuong=" + soLuong + ", giaNhap=" + giaNhap + ", thanhTien="
                + tinhThanhTien() + '}';
    }
}
