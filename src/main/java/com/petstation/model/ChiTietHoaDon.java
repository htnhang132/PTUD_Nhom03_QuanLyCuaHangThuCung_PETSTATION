package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Chi tiết hóa đơn khớp bảng [chi_tiet_hoa_don].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính phức hợp theo thực thể yếu là cặp (hoaDon, sanPham).
 * - Quan hệ kết hợp với đối tượng HoaDon, SanPham, LoHang.
 * - Thuộc tính dẫn xuất: thanhTien được tính động qua getThanhTien() / tinhThanhTien()
 *   tuyệt đối KHÔNG có setter công khai.
 * - Equals/hashCode theo cặp khóa chính phức hợp (hoaDon, sanPham).
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class ChiTietHoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Quan hệ kết hợp (Association): Dòng chi tiết thuộc về một Hóa đơn (Thành phần khóa chính 1). */
    private HoaDon hoaDon;
    /** Quan hệ kết hợp: Sản phẩm hoặc dịch vụ bán ra (Thành phần khóa chính 2). */
    private SanPham sanPham;
    /** Quan hệ kết hợp: Lô hàng xuất kho theo FEFO (có thể null nếu là dịch vụ Spa/Hotel). */
    private LoHang loHang;

    private int soLuong;
    private long donGia;
    private long giamGia;
    private double thueGtgt;
    private String maDichVu;
    private String tenHang;

    /** Constructor mặc định. */
    public ChiTietHoaDon() {
        this.soLuong = 1;
    }

    /** Constructor theo mã cầu nối. */
    public ChiTietHoaDon(String maChiTietHoaDon) {
        this();
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public ChiTietHoaDon(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận đối tượng HoaDon, SanPham, LoHang. */
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

    /** Constructor tương thích ngược có tham số maChiTietHoaDon. */
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

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
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

    /** Constructor cầu nối tương thích DAO cũ với foreign key ids. */
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

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /**
     * Tính thành tiền trước giảm giá của dòng hàng (thuộc tính dẫn xuất: soLuong * donGia).
     */
    public long tinhThanhTienTruocGiam() {
        return (long) Math.max(0, soLuong) * Math.max(0L, donGia);
    }

    public long getThanhTienTruocGiam() {
        return tinhThanhTienTruocGiam();
    }

    public long layThanhTienTruocGiam() {
        return tinhThanhTienTruocGiam();
    }

    /** Tính tiền thuế GTGT của dòng hàng (thuộc tính dẫn xuất). */
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

    /**
     * Tính thành tiền cuối cùng của dòng chi tiết hóa đơn (thuộc tính dẫn xuất).
     * Tuyệt đối KHÔNG có setter công khai.
     */
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

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

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

    // ==================== GETTER & SETTER THUỘC TÍNH ====================

    /** Mã tổng hợp đại diện dòng chi tiết (hoaDon-sanPham) tương thích ngược */
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
        // Tương thích ngược, khóa chính thực sự là (hoaDon, sanPham)
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

    // ==================== EQUALS & HASHCODE THEO KHÓA PHỨC HỢP (hoaDon + sanPham) ====================

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
