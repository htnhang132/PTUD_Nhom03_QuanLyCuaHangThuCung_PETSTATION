package com.petstation.model.kho;

import com.petstation.model.chung.TrangThaiPhieuNhap;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhieuNhapKho implements Serializable {

    private static final long serialVersionUID = 1L;
    private String maPhieuNhap;
    private NhaCungCap nhaCungCap;
    private NhanVien nguoiLap;
    private List<ChiTietPhieuNhap> danhSachChiTiet = new ArrayList<>();
    private LocalDateTime ngayNhap;
    private long daTra;
    private String trangThai;
    private long tongTien;

    public PhieuNhapKho() {
        this.ngayNhap = LocalDateTime.now();
        this.trangThai = TrangThaiPhieuNhap.DA_NHAP.maDB();
    }

    public PhieuNhapKho(String maPhieuNhap) {
        this();
        setMaPhieuNhap(maPhieuNhap);
    }

    public PhieuNhapKho(
            String maPhieuNhap,
            NhaCungCap nhaCungCap,
            NhanVien nguoiLap,
            LocalDateTime ngayNhap,
            String trangThai,
            long daTra) {
        this();
        setMaPhieuNhap(maPhieuNhap);
        setNhaCungCap(nhaCungCap);
        setNguoiLap(nguoiLap);
        setNgayNhap(ngayNhap);
        setTrangThai(trangThai);
        setDaTra(daTra);
    }

    public String getMaPhieuNhap() {
        return this.maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap == null ? null : maPhieuNhap.trim();
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
        this.nhaCungCap =
                (maNhaCungCap == null || maNhaCungCap.isBlank())
                        ? null
                        : new NhaCungCap(maNhaCungCap);
    }

    public NhanVien getNguoiLap() {
        return this.nguoiLap;
    }

    public void setNguoiLap(NhanVien nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public List<ChiTietPhieuNhap> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public LocalDateTime getNgayNhap() {
        return this.ngayNhap;
    }

    public void setNgayNhap(LocalDateTime ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public long getDaTra() {
        return this.daTra;
    }

    public void setDaTra(long daTra) {
        if (daTra < 0) throw new IllegalArgumentException("Số tiền đã trả không được âm.");
        this.daTra = daTra;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public long getTongTien() {
        return this.tongTien;
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

    public long tinhCongNoConLai() {
        return Math.max(0L, tinhTongTien() - daTra);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuNhapKho that = (PhieuNhapKho) o;
        return Objects.equals(this.getMaPhieuNhap(), that.getMaPhieuNhap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaPhieuNhap());
    }

    @Override
    public String toString() {
        return "PhieuNhapKho{"
                + "maPhieuNhap='"
                + getMaPhieuNhap()
                + '\''
                + ", nhaCungCap="
                + getMaNhaCungCap()
                + ", tongTien="
                + tinhTongTien()
                + ", congNoConLai="
                + tinhCongNoConLai()
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
