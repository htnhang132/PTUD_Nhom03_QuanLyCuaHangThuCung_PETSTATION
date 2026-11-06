package com.petstation.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Lịch hẹn dịch vụ khớp bảng [lich_hen].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính là mã lịch hẹn (maLichHen). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp với KhachHang, ThuCung, NhanVien, GoiDichVuSpa.
 * - Có phương thức tính thời lượng lịch hẹn dẫn xuất và equals/hashCode theo khóa chính maLichHen.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class LichHen implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính nghiệp vụ của Lịch hẹn */
    private String maLichHen;
    /** Quan hệ kết hợp: Khách hàng đặt lịch. */
    private KhachHang khachHang;
    /** Quan hệ kết hợp: Thú cưng sử dụng dịch vụ. */
    private ThuCung thuCung;
    /** Quan hệ kết hợp: Nhân viên được phân công phụ trách (có thể null). */
    private NhanVien nhanVien;
    /** Quan hệ kết hợp: Gói dịch vụ Spa đăng ký (có thể null nếu hẹn khám/lưu trú). */
    private GoiDichVuSpa dichVu;

    private String loaiDichVu;
    private LocalDateTime batDau;
    private LocalDateTime ketThuc;
    private String trangThai;
    private String ghiChu;

    /** Constructor mặc định. */
    public LichHen() {
        this.trangThai = "DA_XAC_NHAN";
    }

    /** Constructor theo khóa chính nghiệp vụ maLichHen. */
    public LichHen(String maLichHen) {
        this();
        setMaLichHen(maLichHen);
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public LichHen(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận các đối tượng thực thể liên kết và khóa chính maLichHen. */
    public LichHen(
            String maLichHen,
            KhachHang khachHang,
            ThuCung thuCung,
            String loaiDichVu,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String trangThai,
            String ghiChu,
            NhanVien nhanVien,
            GoiDichVuSpa dichVu) {
        setMaLichHen(maLichHen);
        setKhachHang(khachHang);
        setThuCung(thuCung);
        setLoaiDichVu(loaiDichVu);
        setBatDau(batDau);
        setKetThuc(ketThuc);
        setTrangThai(trangThai);
        setGhiChu(ghiChu);
        setNhanVien(nhanVien);
        setDichVu(dichVu);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public LichHen(
            long id,
            String maLichHen,
            KhachHang khachHang,
            ThuCung thuCung,
            String loaiDichVu,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String trangThai,
            String ghiChu,
            NhanVien nhanVien,
            GoiDichVuSpa dichVu) {
        this(maLichHen, khachHang, thuCung, loaiDichVu, batDau, ketThuc, trangThai, ghiChu, nhanVien, dichVu);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public LichHen(
            long id,
            String maLichHen,
            long khachHangId,
            long thuCungId,
            String loaiDichVu,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String trangThai,
            String ghiChu,
            Long nhanVienId,
            Long dichVuId) {
        this(
                id,
                maLichHen,
                new KhachHang(khachHangId),
                new ThuCung(thuCungId),
                loaiDichVu,
                batDau,
                ketThuc,
                trangThai,
                ghiChu,
                nhanVienId == null ? null : new NhanVien(nhanVienId),
                dichVuId == null ? null : new GoiDichVuSpa(dichVuId));
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /**
     * Tính thời lượng khung giờ hẹn theo số phút (thuộc tính dẫn xuất từ batDau và ketThuc).
     */
    public long tinhThoiLuongPhut() {
        if (batDau == null || ketThuc == null || !ketThuc.isAfter(batDau)) {
            return 0L;
        }
        return Duration.between(batDau, ketThuc).toMinutes();
    }

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maLichHen);
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, this.maLichHen);
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maLichHen == null || this.maLichHen.isBlank()) && id > 0) {
            this.maLichHen = DuLieu.taoMaTuId("LH", 6, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    // ==================== GETTER & SETTER THUỘC TÍNH ====================

    public String getMaLichHen() {
        return this.maLichHen;
    }

    public String layMaLichHen() {
        return this.maLichHen;
    }

    public String maLichHen() {
        return this.maLichHen;
    }

    public void setMaLichHen(String maLichHen) {
        this.maLichHen = maLichHen == null ? null : maLichHen.trim();
    }

    public void datMaLichHen(String maLichHen) {
        setMaLichHen(maLichHen);
    }

    public KhachHang getKhachHang() {
        return this.khachHang;
    }

    public KhachHang layKhachHang() {
        return this.khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public void datKhachHang(KhachHang khachHang) {
        setKhachHang(khachHang);
    }

    public long getKhachHangId() {
        return (khachHang == null || khachHang.getId() == null) ? 0L : khachHang.getId();
    }

    public long layKhachHangId() {
        return getKhachHangId();
    }

    public long khachHangId() {
        return getKhachHangId();
    }

    public void setKhachHangId(long khachHangId) {
        this.khachHang = new KhachHang(khachHangId);
    }

    public void datKhachHangId(long khachHangId) {
        setKhachHangId(khachHangId);
    }

    public ThuCung getThuCung() {
        return this.thuCung;
    }

    public ThuCung layThuCung() {
        return this.thuCung;
    }

    public void setThuCung(ThuCung thuCung) {
        this.thuCung = thuCung;
    }

    public void datThuCung(ThuCung thuCung) {
        setThuCung(thuCung);
    }

    public long getThuCungId() {
        return thuCung == null ? 0L : thuCung.getId();
    }

    public long layThuCungId() {
        return getThuCungId();
    }

    public long thuCungId() {
        return getThuCungId();
    }

    public void setThuCungId(long thuCungId) {
        this.thuCung = new ThuCung(thuCungId);
    }

    public void datThuCungId(long thuCungId) {
        setThuCungId(thuCungId);
    }

    public String getLoaiDichVu() {
        return this.loaiDichVu;
    }

    public String layLoaiDichVu() {
        return this.loaiDichVu;
    }

    public String loaiDichVu() {
        return this.loaiDichVu;
    }

    public void setLoaiDichVu(String loaiDichVu) {
        this.loaiDichVu = loaiDichVu;
    }

    public void datLoaiDichVu(String loaiDichVu) {
        setLoaiDichVu(loaiDichVu);
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

    public String getGhiChu() {
        return this.ghiChu;
    }

    public String layGhiChu() {
        return this.ghiChu;
    }

    public String ghiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public void datGhiChu(String ghiChu) {
        setGhiChu(ghiChu);
    }

    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    public NhanVien layNhanVien() {
        return this.nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public void datNhanVien(NhanVien nhanVien) {
        setNhanVien(nhanVien);
    }

    public Long getNhanVienId() {
        return nhanVien == null ? null : nhanVien.getId();
    }

    public Long layNhanVienId() {
        return getNhanVienId();
    }

    public Long nhanVienId() {
        return getNhanVienId();
    }

    public void setNhanVienId(Long nhanVienId) {
        this.nhanVien = nhanVienId == null ? null : new NhanVien(nhanVienId);
    }

    public void datNhanVienId(Long nhanVienId) {
        setNhanVienId(nhanVienId);
    }

    public GoiDichVuSpa getDichVu() {
        return this.dichVu;
    }

    public GoiDichVuSpa layDichVu() {
        return this.dichVu;
    }

    public void setDichVu(GoiDichVuSpa dichVu) {
        this.dichVu = dichVu;
    }

    public void datDichVu(GoiDichVuSpa dichVu) {
        setDichVu(dichVu);
    }

    public Long getDichVuId() {
        return dichVu == null ? null : dichVu.getId();
    }

    public Long layDichVuId() {
        return getDichVuId();
    }

    public Long dichVuId() {
        return getDichVuId();
    }

    public void setDichVuId(Long dichVuId) {
        this.dichVu = dichVuId == null ? null : new GoiDichVuSpa(dichVuId);
    }

    public void datDichVuId(Long dichVuId) {
        setDichVuId(dichVuId);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LichHen that = (LichHen) o;
        return Objects.equals(this.maLichHen, that.maLichHen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLichHen);
    }

    @Override
    public String toString() {
        return "LichHen{" + "maLichHen='" + maLichHen + '\'' + ", khachHangId=" + getKhachHangId()
                + ", thuCungId=" + getThuCungId() + ", trangThai='" + trangThai + '\'' + '}';
    }
}
