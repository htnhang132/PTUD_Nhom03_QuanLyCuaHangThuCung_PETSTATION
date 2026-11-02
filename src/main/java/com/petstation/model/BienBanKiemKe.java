package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể biên bản kiểm kê khớp bảng [bien_ban_kiem_ke].
 */
public class BienBanKiemKe implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String soBienBan;
    private long nguoiLapId;
    private String kyKiemKe;
    private String trangThai;
    private LocalDateTime taoLuc;

    public BienBanKiemKe() {}

    public BienBanKiemKe(long id, String soBienBan, long nguoiLapId, String kyKiemKe, String trangThai, LocalDateTime taoLuc) {
        this.id = id;
        this.soBienBan = soBienBan;
        this.nguoiLapId = nguoiLapId;
        this.kyKiemKe = kyKiemKe;
        this.trangThai = trangThai;
        this.taoLuc = taoLuc;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getSoBienBan() { return this.soBienBan; }
    public String laySoBienBan() { return this.soBienBan; }
    public String soBienBan() { return this.soBienBan; }
    public void setSoBienBan(String soBienBan) { this.soBienBan = soBienBan; }
    public void datSoBienBan(String soBienBan) { this.soBienBan = soBienBan; }

    public long getNguoiLapId() { return this.nguoiLapId; }
    public long layNguoiLapId() { return this.nguoiLapId; }
    public long nguoiLapId() { return this.nguoiLapId; }
    public void setNguoiLapId(long nguoiLapId) { this.nguoiLapId = nguoiLapId; }
    public void datNguoiLapId(long nguoiLapId) { this.nguoiLapId = nguoiLapId; }

    public String getKyKiemKe() { return this.kyKiemKe; }
    public String layKyKiemKe() { return this.kyKiemKe; }
    public String kyKiemKe() { return this.kyKiemKe; }
    public void setKyKiemKe(String kyKiemKe) { this.kyKiemKe = kyKiemKe; }
    public void datKyKiemKe(String kyKiemKe) { this.kyKiemKe = kyKiemKe; }

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
        BienBanKiemKe that = (BienBanKiemKe) o;
        return this.id == that.id
                && Objects.equals(soBienBan, that.soBienBan)
                && this.nguoiLapId == that.nguoiLapId
                && Objects.equals(kyKiemKe, that.kyKiemKe)
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(taoLuc, that.taoLuc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, soBienBan, nguoiLapId, kyKiemKe, trangThai, taoLuc);
    }

    @Override
    public String toString() {
        return "BienBanKiemKe{" + "id=" + id + ", " + "soBienBan=" + soBienBan + ", " + "nguoiLapId=" + nguoiLapId + ", " + "kyKiemKe=" + kyKiemKe + ", " + "trangThai=" + trangThai + ", " + "taoLuc=" + taoLuc + "}";
    }
}
