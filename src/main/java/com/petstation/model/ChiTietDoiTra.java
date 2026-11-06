package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Chi tiết phiếu đổi trả khớp bảng [chi_tiet_doi_tra].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính phức hợp là cặp (phieuDoiTra, chiTietHoaDon, loHang). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp với PhieuDoiTra, ChiTietHoaDon, LoHang.
 * - Có phương thức tính tiền hoàn dẫn xuất và equals/hashCode theo khóa chính.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class ChiTietDoiTra implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Quan hệ kết hợp: Dòng chi tiết thuộc về một Phiếu đổi trả. */
    private PhieuDoiTra phieuDoiTra;
    /** Quan hệ kết hợp: Dòng chi tiết hóa đơn gốc được đổi trả. */
    private ChiTietHoaDon chiTietHoaDon;
    /** Quan hệ kết hợp: Lô hàng nhận lại vào kho (có thể null). */
    private LoHang loHang;
    private int soLuong;
    private long tienHoan;

    /** Constructor mặc định. */
    public ChiTietDoiTra() {
        this.soLuong = 1;
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public ChiTietDoiTra(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận đối tượng PhieuDoiTra, ChiTietHoaDon, LoHang. */
    public ChiTietDoiTra(
            PhieuDoiTra phieuDoiTra,
            ChiTietHoaDon chiTietHoaDon,
            LoHang loHang,
            int soLuong,
            long tienHoan) {
        setPhieuDoiTra(phieuDoiTra);
        setChiTietHoaDon(chiTietHoaDon);
        setLoHang(loHang);
        setSoLuong(soLuong);
        this.tienHoan = Math.max(0L, tienHoan);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public ChiTietDoiTra(
            long id,
            PhieuDoiTra phieuDoiTra,
            ChiTietHoaDon chiTietHoaDon,
            LoHang loHang,
            int soLuong,
            long tienHoan) {
        this(phieuDoiTra, chiTietHoaDon, loHang, soLuong, tienHoan);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public ChiTietDoiTra(
            long id,
            long phieuDoiTraId,
            long chiTietHoaDonId,
            Long loHangId,
            int soLuong,
            long tienHoan) {
        this(
                id,
                new PhieuDoiTra(phieuDoiTraId),
                new ChiTietHoaDon(chiTietHoaDonId),
                loHangId == null ? null : new LoHang(loHangId),
                soLuong,
                tienHoan);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /**
     * Tính số tiền hoàn của dòng đổi trả (thuộc tính dẫn xuất từ đơn giá dòng hóa đơn gốc * số lượng trả).
     */
    public long tinhTienHoan() {
        if (chiTietHoaDon != null && chiTietHoaDon.getDonGia() > 0 && soLuong > 0) {
            return (long) soLuong * chiTietHoaDon.getDonGia();
        }
        return Math.max(0L, this.tienHoan);
    }

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

    public Long getId() {
        String key = (phieuDoiTra != null ? phieuDoiTra.getMaPhieu() : "") + "_" 
                + (chiTietHoaDon != null ? chiTietHoaDon.getMaChiTietHoaDon() : "") + "_" 
                + (loHang != null ? loHang.getMaLo() : "");
        return DuLieu.layIdCauNoiObj(this, key);
    }

    public long layId() {
        String key = (phieuDoiTra != null ? phieuDoiTra.getMaPhieu() : "") + "_" 
                + (chiTietHoaDon != null ? chiTietHoaDon.getMaChiTietHoaDon() : "") + "_" 
                + (loHang != null ? loHang.getMaLo() : "");
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

    public PhieuDoiTra getPhieuDoiTra() {
        return this.phieuDoiTra;
    }

    public PhieuDoiTra layPhieuDoiTra() {
        return this.phieuDoiTra;
    }

    public void setPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        this.phieuDoiTra = phieuDoiTra;
    }

    public void datPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        setPhieuDoiTra(phieuDoiTra);
    }

    public long getPhieuDoiTraId() {
        return phieuDoiTra == null ? 0L : phieuDoiTra.getId();
    }

    public long layPhieuDoiTraId() {
        return getPhieuDoiTraId();
    }

    public long phieuDoiTraId() {
        return getPhieuDoiTraId();
    }

    public void setPhieuDoiTraId(long phieuDoiTraId) {
        this.phieuDoiTra = new PhieuDoiTra(phieuDoiTraId);
    }

    public void datPhieuDoiTraId(long phieuDoiTraId) {
        setPhieuDoiTraId(phieuDoiTraId);
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return this.chiTietHoaDon;
    }

    public ChiTietHoaDon layChiTietHoaDon() {
        return this.chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public void datChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        setChiTietHoaDon(chiTietHoaDon);
    }

    public long getChiTietHoaDonId() {
        return chiTietHoaDon == null ? 0L : chiTietHoaDon.getId();
    }

    public long layChiTietHoaDonId() {
        return getChiTietHoaDonId();
    }

    public long chiTietHoaDonId() {
        return getChiTietHoaDonId();
    }

    public void setChiTietHoaDonId(long chiTietHoaDonId) {
        this.chiTietHoaDon = new ChiTietHoaDon(chiTietHoaDonId);
    }

    public void datChiTietHoaDonId(long chiTietHoaDonId) {
        setChiTietHoaDonId(chiTietHoaDonId);
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
            throw new IllegalArgumentException("Số lượng đổi trả không được âm.");
        this.soLuong = soLuong;
    }

    public void datSoLuong(int soLuong) {
        setSoLuong(soLuong);
    }

    public long getTienHoan() {
        return tinhTienHoan();
    }

    public long layTienHoan() {
        return tinhTienHoan();
    }

    public long tienHoan() {
        return tinhTienHoan();
    }

    /** Phương thức nội bộ gán snapshot từ CSDL (không cung cấp setter công khai vì thuộc tính dẫn xuất). */
    void napTienHoanSnapshot(long tienHoan) {
        this.tienHoan = Math.max(0L, tienHoan);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChiTietDoiTra that = (ChiTietDoiTra) o;
        return Objects.equals(this.phieuDoiTra, that.phieuDoiTra)
                && Objects.equals(this.chiTietHoaDon, that.chiTietHoaDon)
                && Objects.equals(this.loHang, that.loHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuDoiTra, chiTietHoaDon, loHang);
    }

    @Override
    public String toString() {
        return "ChiTietDoiTra{" + "phieuDoiTraId=" + getPhieuDoiTraId() + ", chiTietHoaDonId="
                + getChiTietHoaDonId() + ", soLuong=" + soLuong + ", tienHoan=" + tinhTienHoan() + '}';
    }
}
