package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Thực thể chi tiết kiểm kê khớp bảng [chi_tiet_kiem_ke].
 */
public class ChiTietKiemKe implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long bienBanId;
    private long loHangId;
    private int tonHeThong;
    private int tonThucTe;
    private String nguyenNhan;

    public ChiTietKiemKe() {}

    public ChiTietKiemKe(long id, long bienBanId, long loHangId, int tonHeThong, int tonThucTe, String nguyenNhan) {
        this.id = id;
        this.bienBanId = bienBanId;
        this.loHangId = loHangId;
        this.tonHeThong = tonHeThong;
        this.tonThucTe = tonThucTe;
        this.nguyenNhan = nguyenNhan;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public long getBienBanId() { return this.bienBanId; }
    public long layBienBanId() { return this.bienBanId; }
    public long bienBanId() { return this.bienBanId; }
    public void setBienBanId(long bienBanId) { this.bienBanId = bienBanId; }
    public void datBienBanId(long bienBanId) { this.bienBanId = bienBanId; }

    public long getLoHangId() { return this.loHangId; }
    public long layLoHangId() { return this.loHangId; }
    public long loHangId() { return this.loHangId; }
    public void setLoHangId(long loHangId) { this.loHangId = loHangId; }
    public void datLoHangId(long loHangId) { this.loHangId = loHangId; }

    public int getTonHeThong() { return this.tonHeThong; }
    public int layTonHeThong() { return this.tonHeThong; }
    public int tonHeThong() { return this.tonHeThong; }
    public void setTonHeThong(int tonHeThong) { this.tonHeThong = tonHeThong; }
    public void datTonHeThong(int tonHeThong) { this.tonHeThong = tonHeThong; }

    public int getTonThucTe() { return this.tonThucTe; }
    public int layTonThucTe() { return this.tonThucTe; }
    public int tonThucTe() { return this.tonThucTe; }
    public void setTonThucTe(int tonThucTe) { this.tonThucTe = tonThucTe; }
    public void datTonThucTe(int tonThucTe) { this.tonThucTe = tonThucTe; }

    public String getNguyenNhan() { return this.nguyenNhan; }
    public String layNguyenNhan() { return this.nguyenNhan; }
    public String nguyenNhan() { return this.nguyenNhan; }
    public void setNguyenNhan(String nguyenNhan) { this.nguyenNhan = nguyenNhan; }
    public void datNguyenNhan(String nguyenNhan) { this.nguyenNhan = nguyenNhan; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietKiemKe that = (ChiTietKiemKe) o;
        return this.id == that.id
                && this.bienBanId == that.bienBanId
                && this.loHangId == that.loHangId
                && this.tonHeThong == that.tonHeThong
                && this.tonThucTe == that.tonThucTe
                && Objects.equals(nguyenNhan, that.nguyenNhan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bienBanId, loHangId, tonHeThong, tonThucTe, nguyenNhan);
    }

    @Override
    public String toString() {
        return "ChiTietKiemKe{" + "id=" + id + ", " + "bienBanId=" + bienBanId + ", " + "loHangId=" + loHangId + ", " + "tonHeThong=" + tonHeThong + ", " + "tonThucTe=" + tonThucTe + ", " + "nguyenNhan=" + nguyenNhan + "}";
    }
}
