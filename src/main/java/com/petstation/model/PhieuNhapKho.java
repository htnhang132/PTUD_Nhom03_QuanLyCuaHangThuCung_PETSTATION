package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể phiếu nhập kho khớp bảng [phieu_nhap_kho].
 */
public class PhieuNhapKho implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String soPhieu;
    private long nhaCungCapId;
    private long nguoiLapId;
    private LocalDateTime ngayNhap;
    private long tongTien;
    private String trangThai;
    private String ghiChu;
    private long daTra;

    public PhieuNhapKho() {}

    public PhieuNhapKho(long id, String soPhieu, long nhaCungCapId, long nguoiLapId, LocalDateTime ngayNhap, long tongTien, String trangThai, String ghiChu, long daTra) {
        this.id = id;
        this.soPhieu = soPhieu;
        this.nhaCungCapId = nhaCungCapId;
        this.nguoiLapId = nguoiLapId;
        this.ngayNhap = ngayNhap;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.daTra = daTra;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getSoPhieu() { return this.soPhieu; }
    public String laySoPhieu() { return this.soPhieu; }
    public String soPhieu() { return this.soPhieu; }
    public void setSoPhieu(String soPhieu) { this.soPhieu = soPhieu; }
    public void datSoPhieu(String soPhieu) { this.soPhieu = soPhieu; }

    public long getNhaCungCapId() { return this.nhaCungCapId; }
    public long layNhaCungCapId() { return this.nhaCungCapId; }
    public long nhaCungCapId() { return this.nhaCungCapId; }
    public void setNhaCungCapId(long nhaCungCapId) { this.nhaCungCapId = nhaCungCapId; }
    public void datNhaCungCapId(long nhaCungCapId) { this.nhaCungCapId = nhaCungCapId; }

    public long getNguoiLapId() { return this.nguoiLapId; }
    public long layNguoiLapId() { return this.nguoiLapId; }
    public long nguoiLapId() { return this.nguoiLapId; }
    public void setNguoiLapId(long nguoiLapId) { this.nguoiLapId = nguoiLapId; }
    public void datNguoiLapId(long nguoiLapId) { this.nguoiLapId = nguoiLapId; }

    public LocalDateTime getNgayNhap() { return this.ngayNhap; }
    public LocalDateTime layNgayNhap() { return this.ngayNhap; }
    public LocalDateTime ngayNhap() { return this.ngayNhap; }
    public void setNgayNhap(LocalDateTime ngayNhap) { this.ngayNhap = ngayNhap; }
    public void datNgayNhap(LocalDateTime ngayNhap) { this.ngayNhap = ngayNhap; }

    public long getTongTien() { return this.tongTien; }
    public long layTongTien() { return this.tongTien; }
    public long tongTien() { return this.tongTien; }
    public void setTongTien(long tongTien) { this.tongTien = tongTien; }
    public void datTongTien(long tongTien) { this.tongTien = tongTien; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return this.ghiChu; }
    public String layGhiChu() { return this.ghiChu; }
    public String ghiChu() { return this.ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public void datGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public long getDaTra() { return this.daTra; }
    public long layDaTra() { return this.daTra; }
    public long daTra() { return this.daTra; }
    public void setDaTra(long daTra) { this.daTra = daTra; }
    public void datDaTra(long daTra) { this.daTra = daTra; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuNhapKho that = (PhieuNhapKho) o;
        return this.id == that.id
                && Objects.equals(soPhieu, that.soPhieu)
                && this.nhaCungCapId == that.nhaCungCapId
                && this.nguoiLapId == that.nguoiLapId
                && Objects.equals(ngayNhap, that.ngayNhap)
                && this.tongTien == that.tongTien
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(ghiChu, that.ghiChu)
                && this.daTra == that.daTra;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, soPhieu, nhaCungCapId, nguoiLapId, ngayNhap, tongTien, trangThai, ghiChu, daTra);
    }

    @Override
    public String toString() {
        return "PhieuNhapKho{" + "id=" + id + ", " + "soPhieu=" + soPhieu + ", " + "nhaCungCapId=" + nhaCungCapId + ", " + "nguoiLapId=" + nguoiLapId + ", " + "ngayNhap=" + ngayNhap + ", " + "tongTien=" + tongTien + ", " + "trangThai=" + trangThai + ", " + "ghiChu=" + ghiChu + ", " + "daTra=" + daTra + "}";
    }
}
