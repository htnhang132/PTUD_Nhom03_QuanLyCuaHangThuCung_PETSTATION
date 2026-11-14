package com.petstation.model.nhansu;

import com.petstation.model.chung.TrangThaiLichLamViec;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class LichLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final class ThongTinPhanCa {
        private final LocalDate ngayLam;
        private final String maCa;
        private final String maNhanVien;
        private final String loai;

        public ThongTinPhanCa(LocalDate ngayLam, String maCa, String maNhanVien, String loai) {
            this.ngayLam = ngayLam;
            this.maCa = maCa;
            this.maNhanVien = maNhanVien;
            this.loai = loai;
        }

        public LocalDate ngayLam() {
            return ngayLam;
        }

        public String maCa() {
            return maCa;
        }

        public String maNhanVien() {
            return maNhanVien;
        }

        public String loai() {
            return loai;
        }
    }

    private NhanVien nhanVien;
    private CaLamViec caLamViec;
    private LocalDate ngayLam;
    private String trangThai;
    private String loai;

    public LichLamViec() {
        this.trangThai = TrangThaiLichLamViec.DANG_MO.maDB();
    }

    public LichLamViec(
            NhanVien nhanVien,
            CaLamViec caLamViec,
            LocalDate ngayLam,
            String trangThai,
            String loai) {
        this();
        setNhanVien(nhanVien);
        setCaLamViec(caLamViec);
        setNgayLam(ngayLam);
        setTrangThai(trangThai);
        setLoai(loai);
    }

    public NhanVien getNhanVien() {
        return this.nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public CaLamViec getCaLamViec() {
        return this.caLamViec;
    }

    public void setCaLamViec(CaLamViec caLamViec) {
        this.caLamViec = caLamViec;
    }

    public LocalDate getNgayLam() {
        return this.ngayLam;
    }

    public void setNgayLam(LocalDate ngayLam) {
        this.ngayLam = ngayLam;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getLoai() {
        return this.loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public String getMaLichLamViec() {
        String nv = nhanVien != null ? nhanVien.getMaNhanVien() : "";
        String ca = caLamViec != null ? caLamViec.getMaCa() : "";
        String nl = ngayLam != null ? ngayLam.toString() : "";
        return (nv.isEmpty() && ca.isEmpty() && nl.isEmpty()) ? null : (nv + "_" + nl + "_" + ca);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LichLamViec that = (LichLamViec) o;
        return Objects.equals(this.nhanVien, that.nhanVien)
                && Objects.equals(this.caLamViec, that.caLamViec)
                && Objects.equals(this.ngayLam, that.ngayLam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nhanVien, caLamViec, ngayLam);
    }

    @Override
    public String toString() {
        return "LichLamViec{"
                + "nhanVien="
                + (nhanVien == null ? null : nhanVien.getMaNhanVien())
                + ", caLamViec="
                + (caLamViec == null ? null : caLamViec.getMaCa())
                + ", ngayLam="
                + ngayLam
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
