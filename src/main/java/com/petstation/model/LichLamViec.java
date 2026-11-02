package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Thực thể phân ca / lịch làm việc khớp bảng [lich_lam_viec].
 */
public class LichLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long nhanVienId;
    private long caLamViecId;
    private LocalDate ngayLam;
    private String trangThai;
    private String loai;

    public LichLamViec() {}

    public LichLamViec(long id, long nhanVienId, long caLamViecId, LocalDate ngayLam, String trangThai, String loai) {
        this.id = id;
        this.nhanVienId = nhanVienId;
        this.caLamViecId = caLamViecId;
        this.ngayLam = ngayLam;
        this.trangThai = trangThai;
        this.loai = loai;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public long getNhanVienId() { return this.nhanVienId; }
    public long layNhanVienId() { return this.nhanVienId; }
    public long nhanVienId() { return this.nhanVienId; }
    public void setNhanVienId(long nhanVienId) { this.nhanVienId = nhanVienId; }
    public void datNhanVienId(long nhanVienId) { this.nhanVienId = nhanVienId; }

    public long getCaLamViecId() { return this.caLamViecId; }
    public long layCaLamViecId() { return this.caLamViecId; }
    public long caLamViecId() { return this.caLamViecId; }
    public void setCaLamViecId(long caLamViecId) { this.caLamViecId = caLamViecId; }
    public void datCaLamViecId(long caLamViecId) { this.caLamViecId = caLamViecId; }

    public LocalDate getNgayLam() { return this.ngayLam; }
    public LocalDate layNgayLam() { return this.ngayLam; }
    public LocalDate ngayLam() { return this.ngayLam; }
    public void setNgayLam(LocalDate ngayLam) { this.ngayLam = ngayLam; }
    public void datNgayLam(LocalDate ngayLam) { this.ngayLam = ngayLam; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getLoai() { return this.loai; }
    public String layLoai() { return this.loai; }
    public String loai() { return this.loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public void datLoai(String loai) { this.loai = loai; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LichLamViec that = (LichLamViec) o;
        return this.id == that.id
                && this.nhanVienId == that.nhanVienId
                && this.caLamViecId == that.caLamViecId
                && Objects.equals(ngayLam, that.ngayLam)
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(loai, that.loai);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nhanVienId, caLamViecId, ngayLam, trangThai, loai);
    }

    @Override
    public String toString() {
        return "LichLamViec{" + "id=" + id + ", " + "nhanVienId=" + nhanVienId + ", " + "caLamViecId=" + caLamViecId + ", " + "ngayLam=" + ngayLam + ", " + "trangThai=" + trangThai + ", " + "loai=" + loai + "}";
    }
}
