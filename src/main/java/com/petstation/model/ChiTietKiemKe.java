package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Chi tiết kiểm kê khớp bảng [chi_tiet_kiem_ke].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính phức hợp là cặp (bienBan, loHang). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp với BienBanKiemKe và LoHang.
 * - Có phương thức tính độ lệch tồn kho dẫn xuất và equals/hashCode theo khóa chính.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class ChiTietKiemKe implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Quan hệ kết hợp: Dòng chi tiết thuộc về một Biên bản kiểm kê. */
    private BienBanKiemKe bienBan;
    /** Quan hệ kết hợp: Lô hàng được kiểm đếm. */
    private LoHang loHang;
    private int tonHeThong;
    private int tonThucTe;
    private String nguyenNhan;

    /** Constructor mặc định. */
    public ChiTietKiemKe() {
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public ChiTietKiemKe(long id) {
        setId(id);
    }

    /** Constructor chuẩn OOP nhận đối tượng BienBanKiemKe và LoHang. */
    public ChiTietKiemKe(
            BienBanKiemKe bienBan,
            LoHang loHang,
            int tonHeThong,
            int tonThucTe,
            String nguyenNhan) {
        setBienBan(bienBan);
        setLoHang(loHang);
        setTonHeThong(tonHeThong);
        setTonThucTe(tonThucTe);
        setNguyenNhan(nguyenNhan);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public ChiTietKiemKe(
            long id,
            BienBanKiemKe bienBan,
            LoHang loHang,
            int tonHeThong,
            int tonThucTe,
            String nguyenNhan) {
        this(bienBan, loHang, tonHeThong, tonThucTe, nguyenNhan);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public ChiTietKiemKe(
            long id,
            long bienBanId,
            long loHangId,
            int tonHeThong,
            int tonThucTe,
            String nguyenNhan) {
        this(id, new BienBanKiemKe(bienBanId), new LoHang(loHangId), tonHeThong, tonThucTe, nguyenNhan);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Tính chênh lệch kiểm kê (thuộc tính dẫn xuất: tonThucTe - tonHeThong). */
    public int tinhChenhLech() {
        return tonThucTe - tonHeThong;
    }

    /**
     * Kiểm tra lô hàng có bị lệch tồn kho thực tế so với sổ sách hay không (dẫn xuất).
     */
    public boolean kiemTraCoLechKho() {
        return tonThucTe != tonHeThong;
    }

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

    public Long getId() {
        String key = (bienBan != null ? bienBan.getSoBienBan() : "") + "_" + (loHang != null ? loHang.getMaLo() : "");
        return DuLieu.layIdCauNoiObj(this, key);
    }

    public long layId() {
        String key = (bienBan != null ? bienBan.getSoBienBan() : "") + "_" + (loHang != null ? loHang.getMaLo() : "");
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

    public BienBanKiemKe getBienBan() {
        return this.bienBan;
    }

    public BienBanKiemKe layBienBan() {
        return this.bienBan;
    }

    public void setBienBan(BienBanKiemKe bienBan) {
        this.bienBan = bienBan;
    }

    public void datBienBan(BienBanKiemKe bienBan) {
        setBienBan(bienBan);
    }

    public long getBienBanId() {
        return bienBan == null ? 0L : bienBan.getId();
    }

    public long layBienBanId() {
        return getBienBanId();
    }

    public long bienBanId() {
        return getBienBanId();
    }

    public void setBienBanId(long bienBanId) {
        this.bienBan = new BienBanKiemKe(bienBanId);
    }

    public void datBienBanId(long bienBanId) {
        setBienBanId(bienBanId);
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

    public int getTonHeThong() {
        return this.tonHeThong;
    }

    public int layTonHeThong() {
        return this.tonHeThong;
    }

    public int tonHeThong() {
        return this.tonHeThong;
    }

    public void setTonHeThong(int tonHeThong) {
        if (tonHeThong < 0)
            throw new IllegalArgumentException("Tồn hệ thống không được âm.");
        this.tonHeThong = tonHeThong;
    }

    public void datTonHeThong(int tonHeThong) {
        setTonHeThong(tonHeThong);
    }

    public int getTonThucTe() {
        return this.tonThucTe;
    }

    public int layTonThucTe() {
        return this.tonThucTe;
    }

    public int tonThucTe() {
        return this.tonThucTe;
    }

    public void setTonThucTe(int tonThucTe) {
        if (tonThucTe < 0)
            throw new IllegalArgumentException("Tồn thực tế không được âm.");
        this.tonThucTe = tonThucTe;
    }

    public void datTonThucTe(int tonThucTe) {
        setTonThucTe(tonThucTe);
    }

    public String getNguyenNhan() {
        return this.nguyenNhan;
    }

    public String layNguyenNhan() {
        return this.nguyenNhan;
    }

    public String nguyenNhan() {
        return this.nguyenNhan;
    }

    public void setNguyenNhan(String nguyenNhan) {
        this.nguyenNhan = nguyenNhan;
    }

    public void datNguyenNhan(String nguyenNhan) {
        setNguyenNhan(nguyenNhan);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChiTietKiemKe that = (ChiTietKiemKe) o;
        return Objects.equals(this.bienBan, that.bienBan)
                && Objects.equals(this.loHang, that.loHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bienBan, loHang);
    }

    @Override
    public String toString() {
        return "ChiTietKiemKe{" + "bienBanId=" + getBienBanId() + ", loHangId=" + getLoHangId()
                + ", tonHeThong=" + tonHeThong + ", tonThucTe=" + tonThucTe + ", chenhLech=" + tinhChenhLech() + '}';
    }
}
