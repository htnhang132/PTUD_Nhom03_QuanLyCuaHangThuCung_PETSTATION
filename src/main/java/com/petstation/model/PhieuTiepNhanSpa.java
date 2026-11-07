package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class PhieuTiepNhanSpa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieu;

    private LichHen lichHen;

    private ThuCung thuCung;

    private GoiDichVuSpa goiDichVu;

    private NhanVien nhanVien;

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

    public PhieuTiepNhanSpa() {
        this.soLuong = 1;
        this.trangThai = "TIEP_NHAN";
        this.taoLuc = LocalDateTime.now();
    }

    public PhieuTiepNhanSpa(String maPhieu) {
        this();
        setMaPhieu(maPhieu);
    }

    public PhieuTiepNhanSpa(long id) {
        this();
        setId(id);
    }

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
        this.tongTien = Math.max(0L, tongTien);
    }

    public long tinhTongTien() {
        long thanhTien = (long) Math.max(0, soLuong) * Math.max(0L, donGia);
        return thanhTien > 0 ? thanhTien : Math.max(0L, this.tongTien);
    }

    public boolean kiemTraDaThanhToan() {
        return hoaDon != null && (hoaDon.getSoHoaDon() != null || (hoaDon.getId() != null && hoaDon.getId() > 0));
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maPhieu);
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maPhieu == null || this.maPhieu.isBlank()) && id > 0) {
            this.maPhieu = DuLieu.taoMaTuId("SPA", 8, id);
        }
    }

    public String getMaPhieu() {
        return this.maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu == null ? null : maPhieu.trim();
    }

    public LichHen getLichHen() {
        return this.lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
    }

    public Long getLichHenId() {
        return lichHen == null ? null : lichHen.getId();
    }

    public void setLichHenId(Long lichHenId) {
        this.lichHen = lichHenId == null ? null : new LichHen(lichHenId);
    }

    public ThuCung getThuCung() {
        return this.thuCung;
    }

    public void setThuCung(ThuCung thuCung) {
        this.thuCung = thuCung;
    }

    public long getThuCungId() {
        return thuCung == null ? 0L : thuCung.getId();
    }

    public void setThuCungId(long thuCungId) {
        this.thuCung = new ThuCung(thuCungId);
    }

    public GoiDichVuSpa getGoiDichVu() {
        return this.goiDichVu;
    }

    public void setGoiDichVu(GoiDichVuSpa goiDichVu) {
        this.goiDichVu = goiDichVu;
    }

    public long getGoiId() {
        return goiDichVu == null ? 0L : goiDichVu.getId();
    }

    public void setGoiId(long goiId) {
        this.goiDichVu = new GoiDichVuSpa(goiId);
    }

    public String getMoTaDichVu() {
        return this.moTaDichVu;
    }

    public void setMoTaDichVu(String moTaDichVu) {
        this.moTaDichVu = moTaDichVu;
    }

    public long getDonGia() {
        return this.donGia;
    }

    public void setDonGia(long donGia) {
        if (donGia < 0)
            throw new IllegalArgumentException("Đơn giá dịch vụ Spa không được âm.");
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0)
            throw new IllegalArgumentException("Số lượng dịch vụ Spa không được âm.");
        this.soLuong = soLuong;
    }

    public double getCanNangThucTe() {
        return this.canNangThucTe;
    }

    public void setCanNangThucTe(double canNangThucTe) {
        if (canNangThucTe < 0)
            throw new IllegalArgumentException("Cân nặng thực tế không được âm.");
        this.canNangThucTe = canNangThucTe;
    }

    public long getTongTien() {
        return tinhTongTien();
    }

    void napTongTienSnapshot(long tongTien) {
        this.tongTien = Math.max(0L, tongTien);
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getTaoLuc() {
        return this.taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Long getNhanVienId() {
        return nhanVien == null ? null : nhanVien.getId();
    }

    public void setNhanVienId(Long nhanVienId) {
        this.nhanVien = nhanVienId == null ? null : new NhanVien(nhanVienId);
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getAnh() {
        return this.anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public HoaDon getHoaDon() {
        return this.hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public Long getHoaDonId() {
        return hoaDon == null ? null : hoaDon.getId();
    }

    public void setHoaDonId(Long hoaDonId) {
        this.hoaDon = hoaDonId == null ? null : new HoaDon(hoaDonId);
    }

    public String getMaHoaDon() {
        return hoaDon == null ? null : hoaDon.getMaHoaDon();
    }

    public void setMaHoaDon(String maHoaDon) {
        this.hoaDon = (maHoaDon == null || maHoaDon.isBlank()) ? null : new HoaDon(maHoaDon);
    }

    public String getSoHoaDon() {
        return getMaHoaDon();
    }

    public void setSoHoaDon(String soHoaDon) {
        setMaHoaDon(soHoaDon);
    }

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
