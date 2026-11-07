package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class LichLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLichLamViec;

    private NhanVien nhanVien;

    private CaLamViec caLamViec;
    private LocalDate ngayLam;
    private String trangThai;
    private String loai;

    public LichLamViec() {
        this.trangThai = "DANG_MO";
    }

    public LichLamViec(String maLichLamViec) {
        this();
        setMaLichLamViec(maLichLamViec);
    }

    public LichLamViec(
            String maLichLamViec,
            NhanVien nhanVien,
            CaLamViec caLamViec,
            LocalDate ngayLam,
            String trangThai,
            String loai) {
        this();
        setMaLichLamViec(maLichLamViec);
        setNhanVien(nhanVien);
        setCaLamViec(caLamViec);
        setNgayLam(ngayLam);
        setTrangThai(trangThai);
        setLoai(loai);
    }

    public LichLamViec(long id) {
        this();
        setId(id);
    }

    public LichLamViec(
            long id,
            NhanVien nhanVien,
            CaLamViec caLamViec,
            LocalDate ngayLam,
            String trangThai,
            String loai) {
        this(DuLieu.taoMaTuId("LLV", 6, id), nhanVien, caLamViec, ngayLam, trangThai, loai);
        setId(id);
    }

    public LichLamViec(
            long id,
            long nhanVienId,
            long caLamViecId,
            LocalDate ngayLam,
            String trangThai,
            String loai) {
        this(id, new NhanVien(nhanVienId), new CaLamViec(caLamViecId), ngayLam, trangThai, loai);
    }

    public boolean kiemTraNgayLamHomNay() {
        return ngayLam != null && ngayLam.equals(LocalDate.now());
    }

    public String getMaLichLamViec() {
        if (this.maLichLamViec != null && !this.maLichLamViec.isBlank()) {
            return this.maLichLamViec;
        }
        String nv = nhanVien != null ? nhanVien.getMaNhanVien() : "";
        String ca = caLamViec != null ? caLamViec.getMaCa() : "";
        String nl = ngayLam != null ? ngayLam.toString() : "";
        return (nv.isEmpty() && ca.isEmpty() && nl.isEmpty()) ? null : (nv + "_" + nl + "_" + ca);
    }
    public void setMaLichLamViec(String maLichLamViec) {
        this.maLichLamViec = maLichLamViec == null ? null : maLichLamViec.trim();
    }

    public NhanVien getNhanVien() { return this.nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public CaLamViec getCaLamViec() { return this.caLamViec; }
    public void setCaLamViec(CaLamViec caLamViec) { this.caLamViec = caLamViec; }

    public LocalDate getNgayLam() { return this.ngayLam; }
    public void setNgayLam(LocalDate ngayLam) { this.ngayLam = ngayLam; }

    public String getTrangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getLoai() { return this.loai; }
    public void setLoai(String loai) { this.loai = loai; }

    public long getId() { return DuLieu.layIdCauNoi(this, maLichLamViec); }
    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maLichLamViec == null || this.maLichLamViec.isBlank()) && id > 0) {
            this.maLichLamViec = DuLieu.taoMaTuId("LLV", 6, id);
        }
    }

    public long getNhanVienId() { return nhanVien == null ? 0L : nhanVien.getId(); }
    public void setNhanVienId(long nhanVienId) { this.nhanVien = new NhanVien(nhanVienId); }

    public long getCaLamViecId() { return caLamViec == null ? 0L : caLamViec.getId(); }
    public void setCaLamViecId(long caLamViecId) { this.caLamViec = new CaLamViec(caLamViecId); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LichLamViec that = (LichLamViec) o;
        if (this.nhanVien != null && that.nhanVien != null
                && this.caLamViec != null && that.caLamViec != null
                && this.ngayLam != null && that.ngayLam != null) {
            return Objects.equals(this.nhanVien, that.nhanVien)
                    && Objects.equals(this.caLamViec, that.caLamViec)
                    && Objects.equals(this.ngayLam, that.ngayLam);
        }
        return Objects.equals(this.getMaLichLamViec(), that.getMaLichLamViec());
    }

    @Override
    public int hashCode() {
        if (nhanVien != null && caLamViec != null && ngayLam != null) {
            return Objects.hash(nhanVien, caLamViec, ngayLam);
        }
        return Objects.hash(getMaLichLamViec());
    }

    @Override
    public String toString() {
        return "LichLamViec{" + "maLichLamViec='" + maLichLamViec + '\''
                + ", nhanVien=" + (nhanVien == null ? null : nhanVien.getMaNhanVien())
                + ", caLamViec=" + (caLamViec == null ? null : caLamViec.getMaCa())
                + ", ngayLam=" + ngayLam + ", trangThai='" + trangThai + '\'' + '}';
    }
}
