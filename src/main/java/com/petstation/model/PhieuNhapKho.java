package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class PhieuNhapKho implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieuNhap;

    private NhaCungCap nhaCungCap;

    private TaiKhoan nguoiLap;

    private List<ChiTietPhieuNhap> danhSachChiTiet = new ArrayList<>();

    private LocalDateTime ngayNhap;
    private long tongTien;
    private String trangThai;
    private String ghiChu;
    private long daTra;

    public PhieuNhapKho() {
        this.ngayNhap = LocalDateTime.now();
        this.trangThai = "DA_NHAP";
    }

    public PhieuNhapKho(String maPhieuNhap) {
        this();
        setMaPhieuNhap(maPhieuNhap);
    }

    public PhieuNhapKho(long id) {
        this();
        setId(id);
    }

    public PhieuNhapKho(
            String maPhieuNhap,
            NhaCungCap nhaCungCap,
            TaiKhoan nguoiLap,
            LocalDateTime ngayNhap,
            String trangThai,
            String ghiChu,
            long daTra) {
        setMaPhieuNhap(maPhieuNhap);
        setNhaCungCap(nhaCungCap);
        setNguoiLap(nguoiLap);
        setNgayNhap(ngayNhap);
        setTrangThai(trangThai);
        setGhiChu(ghiChu);
        setDaTra(daTra);
    }

    public PhieuNhapKho(
            long id,
            String maPhieuNhap,
            NhaCungCap nhaCungCap,
            TaiKhoan nguoiLap,
            LocalDateTime ngayNhap,
            String trangThai,
            String ghiChu,
            long daTra) {
        this(maPhieuNhap, nhaCungCap, nguoiLap, ngayNhap, trangThai, ghiChu, daTra);
        setId(id);
    }

    public PhieuNhapKho(
            long id,
            String maPhieuNhap,
            long nhaCungCapId,
            long nguoiLapId,
            LocalDateTime ngayNhap,
            long tongTien,
            String trangThai,
            String ghiChu,
            long daTra) {
        this(
                id,
                maPhieuNhap,
                new NhaCungCap(nhaCungCapId),
                new TaiKhoan(nguoiLapId),
                ngayNhap,
                trangThai,
                ghiChu,
                daTra);
        this.tongTien = Math.max(0L, tongTien);
    }

    public long tinhTongTien() {
        if (danhSachChiTiet != null && !danhSachChiTiet.isEmpty()) {
            long tong = 0L;
            for (ChiTietPhieuNhap ct : danhSachChiTiet) {
                if (ct != null) {
                    tong += ct.tinhThanhTien();
                }
            }
            return tong;
        }
        return Math.max(0L, this.tongTien);
    }

    public long getTongTien() {
        return tinhTongTien();
    }

    public void napTongTienSnapshot(long tongTien) {
        this.tongTien = Math.max(0L, tongTien);
    }

    public long tinhCongNoConLai() {
        return Math.max(0L, tinhTongTien() - daTra);
    }

    public long getCongNoConLai() {
        return tinhCongNoConLai();
    }

    public boolean kiemTraDaTatToanCongNo() {
        return tinhCongNoConLai() == 0L;
    }

    public void themChiTietPhieuNhap(ChiTietPhieuNhap chiTiet) {
        if (chiTiet == null)
            return;
        if (this.danhSachChiTiet == null) {
            this.danhSachChiTiet = new ArrayList<>();
        }
        chiTiet.setPhieuNhap(this);
        this.danhSachChiTiet.add(chiTiet);
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, getMaPhieuNhap());
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maPhieuNhap == null || this.maPhieuNhap.isBlank()) && id > 0) {
            this.maPhieuNhap = DuLieu.taoMaTuId("PN", 8, id);
        }
    }

    public String getMaPhieuNhap() {
        return this.maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap == null ? null : maPhieuNhap.trim();
    }

    public String getSoPhieu() {
        return getMaPhieuNhap();
    }

    public void setSoPhieu(String soPhieu) {
        setMaPhieuNhap(soPhieu);
    }

    public NhaCungCap getNhaCungCap() {
        return this.nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public String getMaNhaCungCap() {
        return nhaCungCap == null ? null : nhaCungCap.getMaNhaCungCap();
    }

    public void setMaNhaCungCap(String maNhaCungCap) {
        this.nhaCungCap = (maNhaCungCap == null || maNhaCungCap.isBlank()) ? null : new NhaCungCap(maNhaCungCap);
    }

    public String getMaNcc() {
        return getMaNhaCungCap();
    }

    public void setMaNcc(String maNcc) {
        setMaNhaCungCap(maNcc);
    }

    public long getNhaCungCapId() {
        return nhaCungCap == null ? 0L : nhaCungCap.getId();
    }

    public void setNhaCungCapId(long nhaCungCapId) {
        this.nhaCungCap = new NhaCungCap(nhaCungCapId);
    }

    public TaiKhoan getNguoiLap() {
        return this.nguoiLap;
    }

    public void setNguoiLap(TaiKhoan nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public long getNguoiLapId() {
        return nguoiLap == null ? 0L : nguoiLap.getId();
    }

    public void setNguoiLapId(long nguoiLapId) {
        this.nguoiLap = new TaiKhoan(nguoiLapId);
    }

    public List<ChiTietPhieuNhap> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietPhieuNhap> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
    }

    public LocalDateTime getNgayNhap() {
        return this.ngayNhap;
    }

    public void setNgayNhap(LocalDateTime ngayNhap) {
        this.ngayNhap = ngayNhap;
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

    public long getDaTra() {
        return this.daTra;
    }

    public void setDaTra(long daTra) {
        if (daTra < 0)
            throw new IllegalArgumentException("Số tiền đã trả không được âm.");
        this.daTra = daTra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PhieuNhapKho that = (PhieuNhapKho) o;
        return Objects.equals(this.getMaPhieuNhap(), that.getMaPhieuNhap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaPhieuNhap());
    }

    @Override
    public String toString() {
        return "PhieuNhapKho{" + "maPhieuNhap='" + getMaPhieuNhap() + '\'' + ", nhaCungCap=" + getMaNhaCungCap()
                + ", tongTien=" + tinhTongTien() + ", congNoConLai=" + tinhCongNoConLai() + ", trangThai='" + trangThai
                + '\'' + '}';
    }
}
