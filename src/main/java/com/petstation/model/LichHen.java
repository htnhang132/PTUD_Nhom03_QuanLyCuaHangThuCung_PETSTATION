package com.petstation.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class LichHen implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLichHen;

    private KhachHang khachHang;

    private ThuCung thuCung;

    private NhanVien nhanVien;

    private GoiDichVuSpa dichVu;

    private String loaiDichVu;
    private LocalDateTime batDau;
    private LocalDateTime ketThuc;
    private String trangThai;
    private String ghiChu;

    public LichHen() {
        this.trangThai = "DA_XAC_NHAN";
    }

    public LichHen(String maLichHen) {
        this();
        setMaLichHen(maLichHen);
    }

    public LichHen(long id) {
        this();
        setId(id);
    }

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

    public long tinhThoiLuongPhut() {
        if (batDau == null || ketThuc == null || !ketThuc.isAfter(batDau)) {
            return 0L;
        }
        return Duration.between(batDau, ketThuc).toMinutes();
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maLichHen);
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maLichHen == null || this.maLichHen.isBlank()) && id > 0) {
            this.maLichHen = DuLieu.taoMaTuId("LH", 6, id);
        }
    }

    public String getMaLichHen() {
        return this.maLichHen;
    }

    public void setMaLichHen(String maLichHen) {
        this.maLichHen = maLichHen == null ? null : maLichHen.trim();
    }

    public KhachHang getKhachHang() {
        return this.khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public long getKhachHangId() {
        return (khachHang == null || khachHang.getId() == null) ? 0L : khachHang.getId();
    }

    public void setKhachHangId(long khachHangId) {
        this.khachHang = new KhachHang(khachHangId);
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

    public String getLoaiDichVu() {
        return this.loaiDichVu;
    }

    public void setLoaiDichVu(String loaiDichVu) {
        this.loaiDichVu = loaiDichVu;
    }

    public LocalDateTime getBatDau() {
        return this.batDau;
    }

    public void setBatDau(LocalDateTime batDau) {
        this.batDau = batDau;
    }

    public LocalDateTime getKetThuc() {
        return this.ketThuc;
    }

    public void setKetThuc(LocalDateTime ketThuc) {
        this.ketThuc = ketThuc;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
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

    public GoiDichVuSpa getDichVu() {
        return this.dichVu;
    }

    public void setDichVu(GoiDichVuSpa dichVu) {
        this.dichVu = dichVu;
    }

    public Long getDichVuId() {
        return dichVu == null ? null : dichVu.getId();
    }

    public void setDichVuId(Long dichVuId) {
        this.dichVu = dichVuId == null ? null : new GoiDichVuSpa(dichVuId);
    }

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
