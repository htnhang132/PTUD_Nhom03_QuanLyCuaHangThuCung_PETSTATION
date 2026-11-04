package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Phiếu tiếp nhận Spa khớp bảng [phieu_tiep_nhan_spa].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính là mã phiếu (maPhieu). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp với LichHen, ThuCung, GoiDichVuSpa, NhanVien, HoaDon.
 * - Có phương thức tính tổng tiền dịch vụ dẫn xuất và equals/hashCode theo khóa chính maPhieu.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class PhieuTiepNhanSpa implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính nghiệp vụ của Phiếu tiếp nhận Spa */
    private String maPhieu;
    /** Quan hệ kết hợp: Lịch hẹn nguồn (null nếu khách đến trực tiếp walk-in). */
    private LichHen lichHen;
    /** Quan hệ kết hợp: Thú cưng sử dụng dịch vụ Spa. */
    private ThuCung thuCung;
    /** Quan hệ kết hợp: Gói dịch vụ Spa áp dụng. */
    private GoiDichVuSpa goiDichVu;
    /** Quan hệ kết hợp: Kỹ thuật viên thực hiện Spa. */
    private NhanVien nhanVien;
    /** Quan hệ kết hợp: Hóa đơn thanh toán (null khi chưa thanh toán). */
    private HoaDon hoaDon;

    private String moTaDichVu;
    private long donGia;
    private int soLuong;
    private double canNangThucTe;
    private long tongTien;
    private String trangThai;
    private LocalDateTime taoLuc;
    private String ghiChu;
    private String anh;

    /** Constructor mặc định. */
    public PhieuTiepNhanSpa() {
        this.soLuong = 1;
        this.trangThai = "WAITING";
        this.taoLuc = LocalDateTime.now();
    }

    /** Constructor theo khóa chính nghiệp vụ maPhieu. */
    public PhieuTiepNhanSpa(String maPhieu) {
        this();
        setMaPhieu(maPhieu);
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public PhieuTiepNhanSpa(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận các đối tượng thực thể liên kết và khóa chính maPhieu. */
    public PhieuTiepNhanSpa(
            String maPhieu,
            LichHen lichHen,
            ThuCung thuCung,
            GoiDichVuSpa goiDichVu,
            String moTaDichVu,
            long donGia,
            int soLuong,
            double canNangThucTe,
            String trangThai,
            LocalDateTime taoLuc,
            NhanVien nhanVien,
            String ghiChu,
            String anh,
            HoaDon hoaDon) {
        setMaPhieu(maPhieu);
        setLichHen(lichHen);
        setThuCung(thuCung);
        setGoiDichVu(goiDichVu);
        setMoTaDichVu(moTaDichVu);
        setDonGia(donGia);
        setSoLuong(soLuong);
        setCanNangThucTe(canNangThucTe);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
        setNhanVien(nhanVien);
        setGhiChu(ghiChu);
        setAnh(anh);
        setHoaDon(hoaDon);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public PhieuTiepNhanSpa(
            long id,
            String maPhieu,
            LichHen lichHen,
            ThuCung thuCung,
            GoiDichVuSpa goiDichVu,
            String moTaDichVu,
            long donGia,
            int soLuong,
            double canNangThucTe,
            String trangThai,
            LocalDateTime taoLuc,
            NhanVien nhanVien,
            String ghiChu,
            String anh,
            HoaDon hoaDon) {
        this(maPhieu, lichHen, thuCung, goiDichVu, moTaDichVu, donGia, soLuong, canNangThucTe, trangThai, taoLuc, nhanVien, ghiChu, anh, hoaDon);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public PhieuTiepNhanSpa(
            long id,
            String maPhieu,
            Long lichHenId,
            long thuCungId,
            long goiId,
            String moTaDichVu,
            long donGia,
            int soLuong,
            double canNangThucTe,
            long tongTien,
            String trangThai,
            LocalDateTime taoLuc,
            Long nhanVienId,
            String ghiChu,
            String anh,
            Long hoaDonId) {
        this(
                id,
                maPhieu,
                lichHenId == null ? null : new LichHen(lichHenId),
                new ThuCung(thuCungId),
                new GoiDichVuSpa(goiId),
                moTaDichVu,
                donGia,
                soLuong,
                canNangThucTe,
                trangThai,
                taoLuc,
                nhanVienId == null ? null : new NhanVien(nhanVienId),
                ghiChu,
                anh,
                hoaDonId == null ? null : new HoaDon(hoaDonId));
        setTongTien(tongTien);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Tính tổng tiền dịch vụ Spa (thuộc tính dẫn xuất: soLuong * donGia). */
    public long tinhTongTien() {
        long thanhTien = (long) Math.max(0, soLuong) * Math.max(0L, donGia);
        return thanhTien > 0 ? thanhTien : Math.max(0L, this.tongTien);
    }

    /** Kiểm tra phiếu Spa đã được lập hóa đơn thanh toán hay chưa (dẫn xuất). */
    public boolean kiemTraDaThanhToan() {
        return hoaDon != null && (hoaDon.getSoHoaDon() != null || (hoaDon.getId() != null && hoaDon.getId() > 0));
    }

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maPhieu);
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, this.maPhieu);
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maPhieu == null || this.maPhieu.isBlank()) && id > 0) {
            this.maPhieu = DuLieu.taoMaTuId("SPA", 8, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    // ==================== GETTER & SETTER THUỘC TÍNH ====================

    public String getMaPhieu() {
        return this.maPhieu;
    }

    public String layMaPhieu() {
        return this.maPhieu;
    }

    public String maPhieu() {
        return this.maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu == null ? null : maPhieu.trim();
    }

    public void datMaPhieu(String maPhieu) {
        setMaPhieu(maPhieu);
    }

    public LichHen getLichHen() {
        return this.lichHen;
    }

    public LichHen layLichHen() {
        return this.lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
    }

    public void datLichHen(LichHen lichHen) {
        setLichHen(lichHen);
    }

    public Long getLichHenId() {
        return lichHen == null ? null : lichHen.getId();
    }

    public Long layLichHenId() {
        return getLichHenId();
    }

    public Long lichHenId() {
        return getLichHenId();
    }

    public void setLichHenId(Long lichHenId) {
        this.lichHen = lichHenId == null ? null : new LichHen(lichHenId);
    }

    public void datLichHenId(Long lichHenId) {
        setLichHenId(lichHenId);
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

    public GoiDichVuSpa getGoiDichVu() {
        return this.goiDichVu;
    }

    public GoiDichVuSpa layGoiDichVu() {
        return this.goiDichVu;
    }

    public void setGoiDichVu(GoiDichVuSpa goiDichVu) {
        this.goiDichVu = goiDichVu;
    }

    public void datGoiDichVu(GoiDichVuSpa goiDichVu) {
        setGoiDichVu(goiDichVu);
    }

    public long getGoiId() {
        return goiDichVu == null ? 0L : goiDichVu.getId();
    }

    public long layGoiId() {
        return getGoiId();
    }

    public long goiId() {
        return getGoiId();
    }

    public void setGoiId(long goiId) {
        this.goiDichVu = new GoiDichVuSpa(goiId);
    }

    public void datGoiId(long goiId) {
        setGoiId(goiId);
    }

    public String getMoTaDichVu() {
        return this.moTaDichVu;
    }

    public String layMoTaDichVu() {
        return this.moTaDichVu;
    }

    public String moTaDichVu() {
        return this.moTaDichVu;
    }

    public void setMoTaDichVu(String moTaDichVu) {
        this.moTaDichVu = moTaDichVu;
    }

    public void datMoTaDichVu(String moTaDichVu) {
        setMoTaDichVu(moTaDichVu);
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
            throw new IllegalArgumentException("Đơn giá dịch vụ Spa không được âm.");
        this.donGia = donGia;
    }

    public void datDonGia(long donGia) {
        setDonGia(donGia);
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
            throw new IllegalArgumentException("Số lượng dịch vụ Spa không được âm.");
        this.soLuong = soLuong;
    }

    public void datSoLuong(int soLuong) {
        setSoLuong(soLuong);
    }

    public double getCanNangThucTe() {
        return this.canNangThucTe;
    }

    public double layCanNangThucTe() {
        return this.canNangThucTe;
    }

    public double canNangThucTe() {
        return this.canNangThucTe;
    }

    public void setCanNangThucTe(double canNangThucTe) {
        if (canNangThucTe < 0)
            throw new IllegalArgumentException("Cân nặng thực tế không được âm.");
        this.canNangThucTe = canNangThucTe;
    }

    public void datCanNangThucTe(double canNangThucTe) {
        setCanNangThucTe(canNangThucTe);
    }

    public long getTongTien() {
        return this.tongTien;
    }

    public long layTongTien() {
        return this.tongTien;
    }

    public long tongTien() {
        return this.tongTien;
    }

    public void setTongTien(long tongTien) {
        if (tongTien < 0)
            throw new IllegalArgumentException("Tổng tiền phiếu Spa không được âm.");
        this.tongTien = tongTien;
    }

    public void datTongTien(long tongTien) {
        setTongTien(tongTien);
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

    public LocalDateTime getTaoLuc() {
        return this.taoLuc;
    }

    public LocalDateTime layTaoLuc() {
        return this.taoLuc;
    }

    public LocalDateTime taoLuc() {
        return this.taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    public void datTaoLuc(LocalDateTime taoLuc) {
        setTaoLuc(taoLuc);
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

    public String getAnh() {
        return this.anh;
    }

    public String layAnh() {
        return this.anh;
    }

    public String anh() {
        return this.anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public void datAnh(String anh) {
        setAnh(anh);
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

    public Long getHoaDonId() {
        return hoaDon == null ? null : hoaDon.getId();
    }

    public Long layHoaDonId() {
        return getHoaDonId();
    }

    public Long hoaDonId() {
        return getHoaDonId();
    }

    public void setHoaDonId(Long hoaDonId) {
        this.hoaDon = hoaDonId == null ? null : new HoaDon(hoaDonId);
    }

    public void datHoaDonId(Long hoaDonId) {
        setHoaDonId(hoaDonId);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PhieuTiepNhanSpa that = (PhieuTiepNhanSpa) o;
        return Objects.equals(this.maPhieu, that.maPhieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieu);
    }

    @Override
    public String toString() {
        return "PhieuTiepNhanSpa{" + "maPhieu='" + maPhieu + '\'' + ", thuCungId=" + getThuCungId()
                + ", goiId=" + getGoiId() + ", tongTien=" + tinhTongTien() + ", trangThai='" + trangThai + '\'' + '}';
    }
}
