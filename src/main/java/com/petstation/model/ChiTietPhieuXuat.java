package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Chi tiết phiếu xuất kho khớp bảng [chi_tiet_phieu_xuat].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính phức hợp là cặp (phieuXuat, loHang). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp với PhieuXuatKho và LoHang.
 * - Phương thức tính giá trị xuất dẫn xuất và equals/hashCode theo khóa chính.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class ChiTietPhieuXuat implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Quan hệ kết hợp: Dòng chi tiết thuộc về một Phiếu xuất kho. */
    private PhieuXuatKho phieuXuat;
    /** Quan hệ kết hợp: Lô hàng được xuất kho. */
    private LoHang loHang;
    private int soLuong;
    private long giaTri;

    /** Constructor mặc định. */
    public ChiTietPhieuXuat() {
        this.soLuong = 1;
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public ChiTietPhieuXuat(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận đối tượng PhieuXuatKho và LoHang. */
    public ChiTietPhieuXuat(PhieuXuatKho phieuXuat, LoHang loHang, int soLuong, long giaTri) {
        setPhieuXuat(phieuXuat);
        setLoHang(loHang);
        setSoLuong(soLuong);
        setGiaTri(giaTri);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public ChiTietPhieuXuat(long id, PhieuXuatKho phieuXuat, LoHang loHang, int soLuong, long giaTri) {
        this(phieuXuat, loHang, soLuong, giaTri);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public ChiTietPhieuXuat(long id, long phieuXuatId, long loHangId, int soLuong, long giaTri) {
        this(id, new PhieuXuatKho(phieuXuatId), new LoHang(loHangId), soLuong, giaTri);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Tính giá trị dòng xuất kho theo giá vốn lô hàng (thuộc tính dẫn xuất). */
    public long tinhGiaTriXuat() {
        if (loHang != null && loHang.getGiaNhap() > 0 && soLuong > 0) {
            return (long) soLuong * loHang.getGiaNhap();
        }
        return Math.max(0L, this.giaTri);
    }

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

    public Long getId() {
        String key = (phieuXuat != null ? phieuXuat.getSoPhieu() : "") + "_" + (loHang != null ? loHang.getMaLo() : "");
        return DuLieu.layIdCauNoiObj(this, key);
    }

    public long layId() {
        String key = (phieuXuat != null ? phieuXuat.getSoPhieu() : "") + "_" + (loHang != null ? loHang.getMaLo() : "");
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

    public PhieuXuatKho getPhieuXuat() {
        return this.phieuXuat;
    }

    public PhieuXuatKho layPhieuXuat() {
        return this.phieuXuat;
    }

    public void setPhieuXuat(PhieuXuatKho phieuXuat) {
        this.phieuXuat = phieuXuat;
    }

    public void datPhieuXuat(PhieuXuatKho phieuXuat) {
        setPhieuXuat(phieuXuat);
    }

    public long getPhieuXuatId() {
        return phieuXuat == null ? 0L : phieuXuat.getId();
    }

    public long layPhieuXuatId() {
        return getPhieuXuatId();
    }

    public long phieuXuatId() {
        return getPhieuXuatId();
    }

    public void setPhieuXuatId(long phieuXuatId) {
        this.phieuXuat = new PhieuXuatKho(phieuXuatId);
    }

    public void datPhieuXuatId(long phieuXuatId) {
        setPhieuXuatId(phieuXuatId);
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

    public long getLoHangId() {
        return loHang == null ? 0L : loHang.getId();
    }

    public long layLoHangId() {
        return getLoHangId();
    }

    public long loHangId() {
        return getLoHangId();
    }

    public void setLoHangId(long loHangId) {
        this.loHang = new LoHang(loHangId);
    }

    public void datLoHangId(long loHangId) {
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
            throw new IllegalArgumentException("Số lượng xuất kho không được âm.");
        this.soLuong = soLuong;
    }

    public void datSoLuong(int soLuong) {
        setSoLuong(soLuong);
    }

    public long getGiaTri() {
        return this.giaTri;
    }

    public long layGiaTri() {
        return this.giaTri;
    }

    public long giaTri() {
        return this.giaTri;
    }

    public void setGiaTri(long giaTri) {
        if (giaTri < 0)
            throw new IllegalArgumentException("Giá trị xuất kho không được âm.");
        this.giaTri = giaTri;
    }

    public void datGiaTri(long giaTri) {
        setGiaTri(giaTri);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChiTietPhieuXuat that = (ChiTietPhieuXuat) o;
        return Objects.equals(this.phieuXuat, that.phieuXuat)
                && Objects.equals(this.loHang, that.loHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuXuat, loHang);
    }

    @Override
    public String toString() {
        return "ChiTietPhieuXuat{" + "phieuXuatId=" + getPhieuXuatId() + ", loHangId=" + getLoHangId()
                + ", soLuong=" + soLuong + ", giaTri=" + tinhGiaTriXuat() + '}';
    }
}
