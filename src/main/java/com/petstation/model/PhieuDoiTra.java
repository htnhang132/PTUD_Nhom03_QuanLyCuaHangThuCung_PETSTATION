package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể phiếu đổi trả hàng khớp bảng [phieu_doi_tra].
 */
public class PhieuDoiTra implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maPhieu;
    private long hoaDonId;
    private long nguoiLapId;
    private long chenhLech;
    private String trangThai;
    private LocalDateTime taoLuc;
    private String lyDo;
    private String loaiPhieu;
    private Long hoaDonThayTheId;
    private long giaTriDuocDoi;
    private String khoaThaoTac;

    public PhieuDoiTra() {}

    public PhieuDoiTra(long id, String maPhieu, long hoaDonId, long nguoiLapId, long chenhLech, String trangThai, LocalDateTime taoLuc, String lyDo, String loaiPhieu, Long hoaDonThayTheId, long giaTriDuocDoi, String khoaThaoTac) {
        this.id = id;
        this.maPhieu = maPhieu;
        this.hoaDonId = hoaDonId;
        this.nguoiLapId = nguoiLapId;
        this.chenhLech = chenhLech;
        this.trangThai = trangThai;
        this.taoLuc = taoLuc;
        this.lyDo = lyDo;
        this.loaiPhieu = loaiPhieu;
        this.hoaDonThayTheId = hoaDonThayTheId;
        this.giaTriDuocDoi = giaTriDuocDoi;
        this.khoaThaoTac = khoaThaoTac;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaPhieu() { return this.maPhieu; }
    public String layMaPhieu() { return this.maPhieu; }
    public String maPhieu() { return this.maPhieu; }
    public void setMaPhieu(String maPhieu) { this.maPhieu = maPhieu; }
    public void datMaPhieu(String maPhieu) { this.maPhieu = maPhieu; }

    public long getHoaDonId() { return this.hoaDonId; }
    public long layHoaDonId() { return this.hoaDonId; }
    public long hoaDonId() { return this.hoaDonId; }
    public void setHoaDonId(long hoaDonId) { this.hoaDonId = hoaDonId; }
    public void datHoaDonId(long hoaDonId) { this.hoaDonId = hoaDonId; }

    public long getNguoiLapId() { return this.nguoiLapId; }
    public long layNguoiLapId() { return this.nguoiLapId; }
    public long nguoiLapId() { return this.nguoiLapId; }
    public void setNguoiLapId(long nguoiLapId) { this.nguoiLapId = nguoiLapId; }
    public void datNguoiLapId(long nguoiLapId) { this.nguoiLapId = nguoiLapId; }

    public long getChenhLech() { return this.chenhLech; }
    public long layChenhLech() { return this.chenhLech; }
    public long chenhLech() { return this.chenhLech; }
    public void setChenhLech(long chenhLech) { this.chenhLech = chenhLech; }
    public void datChenhLech(long chenhLech) { this.chenhLech = chenhLech; }

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

    public String getLyDo() { return this.lyDo; }
    public String layLyDo() { return this.lyDo; }
    public String lyDo() { return this.lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public void datLyDo(String lyDo) { this.lyDo = lyDo; }

    public String getLoaiPhieu() { return this.loaiPhieu; }
    public String layLoaiPhieu() { return this.loaiPhieu; }
    public String loaiPhieu() { return this.loaiPhieu; }
    public void setLoaiPhieu(String loaiPhieu) { this.loaiPhieu = loaiPhieu; }
    public void datLoaiPhieu(String loaiPhieu) { this.loaiPhieu = loaiPhieu; }

    public Long getHoaDonThayTheId() { return this.hoaDonThayTheId; }
    public Long layHoaDonThayTheId() { return this.hoaDonThayTheId; }
    public Long hoaDonThayTheId() { return this.hoaDonThayTheId; }
    public void setHoaDonThayTheId(Long hoaDonThayTheId) { this.hoaDonThayTheId = hoaDonThayTheId; }
    public void datHoaDonThayTheId(Long hoaDonThayTheId) { this.hoaDonThayTheId = hoaDonThayTheId; }

    public long getGiaTriDuocDoi() { return this.giaTriDuocDoi; }
    public long layGiaTriDuocDoi() { return this.giaTriDuocDoi; }
    public long giaTriDuocDoi() { return this.giaTriDuocDoi; }
    public void setGiaTriDuocDoi(long giaTriDuocDoi) { this.giaTriDuocDoi = giaTriDuocDoi; }
    public void datGiaTriDuocDoi(long giaTriDuocDoi) { this.giaTriDuocDoi = giaTriDuocDoi; }

    public String getKhoaThaoTac() { return this.khoaThaoTac; }
    public String layKhoaThaoTac() { return this.khoaThaoTac; }
    public String khoaThaoTac() { return this.khoaThaoTac; }
    public void setKhoaThaoTac(String khoaThaoTac) { this.khoaThaoTac = khoaThaoTac; }
    public void datKhoaThaoTac(String khoaThaoTac) { this.khoaThaoTac = khoaThaoTac; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuDoiTra that = (PhieuDoiTra) o;
        return this.id == that.id
                && Objects.equals(maPhieu, that.maPhieu)
                && this.hoaDonId == that.hoaDonId
                && this.nguoiLapId == that.nguoiLapId
                && this.chenhLech == that.chenhLech
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(taoLuc, that.taoLuc)
                && Objects.equals(lyDo, that.lyDo)
                && Objects.equals(loaiPhieu, that.loaiPhieu)
                && Objects.equals(hoaDonThayTheId, that.hoaDonThayTheId)
                && this.giaTriDuocDoi == that.giaTriDuocDoi
                && Objects.equals(khoaThaoTac, that.khoaThaoTac);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maPhieu, hoaDonId, nguoiLapId, chenhLech, trangThai, taoLuc, lyDo, loaiPhieu, hoaDonThayTheId, giaTriDuocDoi, khoaThaoTac);
    }

    @Override
    public String toString() {
        return "PhieuDoiTra{" + "id=" + id + ", " + "maPhieu=" + maPhieu + ", " + "hoaDonId=" + hoaDonId + ", " + "nguoiLapId=" + nguoiLapId + ", " + "chenhLech=" + chenhLech + ", " + "trangThai=" + trangThai + ", " + "taoLuc=" + taoLuc + ", " + "lyDo=" + lyDo + ", " + "loaiPhieu=" + loaiPhieu + ", " + "hoaDonThayTheId=" + hoaDonThayTheId + ", " + "giaTriDuocDoi=" + giaTriDuocDoi + ", " + "khoaThaoTac=" + khoaThaoTac + "}";
    }
}
