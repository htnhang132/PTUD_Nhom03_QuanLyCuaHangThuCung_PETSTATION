package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể phiếu xuất kho khớp bảng [phieu_xuat_kho].
 */
public class PhieuXuatKho implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String soPhieu;
    private long nguoiLapId;
    private String loaiXuat;
    private String lyDo;
    private long tongGiaTri;
    private String trangThai;
    private LocalDateTime taoLuc;

    public PhieuXuatKho() {}

    public PhieuXuatKho(long id, String soPhieu, long nguoiLapId, String loaiXuat, String lyDo, long tongGiaTri, String trangThai, LocalDateTime taoLuc) {
        this.id = id;
        this.soPhieu = soPhieu;
        this.nguoiLapId = nguoiLapId;
        this.loaiXuat = loaiXuat;
        this.lyDo = lyDo;
        this.tongGiaTri = tongGiaTri;
        this.trangThai = trangThai;
        this.taoLuc = taoLuc;
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

    public long getNguoiLapId() { return this.nguoiLapId; }
    public long layNguoiLapId() { return this.nguoiLapId; }
    public long nguoiLapId() { return this.nguoiLapId; }
    public void setNguoiLapId(long nguoiLapId) { this.nguoiLapId = nguoiLapId; }
    public void datNguoiLapId(long nguoiLapId) { this.nguoiLapId = nguoiLapId; }

    public String getLoaiXuat() { return this.loaiXuat; }
    public String layLoaiXuat() { return this.loaiXuat; }
    public String loaiXuat() { return this.loaiXuat; }
    public void setLoaiXuat(String loaiXuat) { this.loaiXuat = loaiXuat; }
    public void datLoaiXuat(String loaiXuat) { this.loaiXuat = loaiXuat; }

    public String getLyDo() { return this.lyDo; }
    public String layLyDo() { return this.lyDo; }
    public String lyDo() { return this.lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public void datLyDo(String lyDo) { this.lyDo = lyDo; }

    public long getTongGiaTri() { return this.tongGiaTri; }
    public long layTongGiaTri() { return this.tongGiaTri; }
    public long tongGiaTri() { return this.tongGiaTri; }
    public void setTongGiaTri(long tongGiaTri) { this.tongGiaTri = tongGiaTri; }
    public void datTongGiaTri(long tongGiaTri) { this.tongGiaTri = tongGiaTri; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public LocalDateTime getTaoLuc() { return this.taoLuc; }
    public LocalDateTime layTaoLuc() { return this.taoLuc; }
    public LocalDateTime taoLuc() { return this.taoLuc; }
    public void setTaoLuc(LocalDateTime taoLuc) { this.taoLuc = taoLuc; }
    public void datTaoLuc(LocalDateTime taoLuc) { this.taoLuc = taoLuc; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuXuatKho that = (PhieuXuatKho) o;
        return this.id == that.id
                && Objects.equals(soPhieu, that.soPhieu)
                && this.nguoiLapId == that.nguoiLapId
                && Objects.equals(loaiXuat, that.loaiXuat)
                && Objects.equals(lyDo, that.lyDo)
                && this.tongGiaTri == that.tongGiaTri
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(taoLuc, that.taoLuc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, soPhieu, nguoiLapId, loaiXuat, lyDo, tongGiaTri, trangThai, taoLuc);
    }

    @Override
    public String toString() {
        return "PhieuXuatKho{" + "id=" + id + ", " + "soPhieu=" + soPhieu + ", " + "nguoiLapId=" + nguoiLapId + ", " + "loaiXuat=" + loaiXuat + ", " + "lyDo=" + lyDo + ", " + "tongGiaTri=" + tongGiaTri + ", " + "trangThai=" + trangThai + ", " + "taoLuc=" + taoLuc + "}";
    }
}
