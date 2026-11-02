package com.petstation.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Thực thể chi tiết phiếu đổi trả khớp bảng [chi_tiet_doi_tra].
 */
public class ChiTietDoiTra implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long phieuDoiTraId;
    private long chiTietHoaDonId;
    private Long loHangId;
    private int soLuong;
    private long tienHoan;

    public ChiTietDoiTra() {}

    public ChiTietDoiTra(long id, long phieuDoiTraId, long chiTietHoaDonId, Long loHangId, int soLuong, long tienHoan) {
        this.id = id;
        this.phieuDoiTraId = phieuDoiTraId;
        this.chiTietHoaDonId = chiTietHoaDonId;
        this.loHangId = loHangId;
        this.soLuong = soLuong;
        this.tienHoan = tienHoan;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public long getPhieuDoiTraId() { return this.phieuDoiTraId; }
    public long layPhieuDoiTraId() { return this.phieuDoiTraId; }
    public long phieuDoiTraId() { return this.phieuDoiTraId; }
    public void setPhieuDoiTraId(long phieuDoiTraId) { this.phieuDoiTraId = phieuDoiTraId; }
    public void datPhieuDoiTraId(long phieuDoiTraId) { this.phieuDoiTraId = phieuDoiTraId; }

    public long getChiTietHoaDonId() { return this.chiTietHoaDonId; }
    public long layChiTietHoaDonId() { return this.chiTietHoaDonId; }
    public long chiTietHoaDonId() { return this.chiTietHoaDonId; }
    public void setChiTietHoaDonId(long chiTietHoaDonId) { this.chiTietHoaDonId = chiTietHoaDonId; }
    public void datChiTietHoaDonId(long chiTietHoaDonId) { this.chiTietHoaDonId = chiTietHoaDonId; }

    public Long getLoHangId() { return this.loHangId; }
    public Long layLoHangId() { return this.loHangId; }
    public Long loHangId() { return this.loHangId; }
    public void setLoHangId(Long loHangId) { this.loHangId = loHangId; }
    public void datLoHangId(Long loHangId) { this.loHangId = loHangId; }

    public int getSoLuong() { return this.soLuong; }
    public int laySoLuong() { return this.soLuong; }
    public int soLuong() { return this.soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void datSoLuong(int soLuong) { this.soLuong = soLuong; }

    public long getTienHoan() { return this.tienHoan; }
    public long layTienHoan() { return this.tienHoan; }
    public long tienHoan() { return this.tienHoan; }
    public void setTienHoan(long tienHoan) { this.tienHoan = tienHoan; }
    public void datTienHoan(long tienHoan) { this.tienHoan = tienHoan; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietDoiTra that = (ChiTietDoiTra) o;
        return this.id == that.id
                && this.phieuDoiTraId == that.phieuDoiTraId
                && this.chiTietHoaDonId == that.chiTietHoaDonId
                && Objects.equals(loHangId, that.loHangId)
                && this.soLuong == that.soLuong
                && this.tienHoan == that.tienHoan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phieuDoiTraId, chiTietHoaDonId, loHangId, soLuong, tienHoan);
    }

    @Override
    public String toString() {
        return "ChiTietDoiTra{" + "id=" + id + ", " + "phieuDoiTraId=" + phieuDoiTraId + ", " + "chiTietHoaDonId=" + chiTietHoaDonId + ", " + "loHangId=" + loHangId + ", " + "soLuong=" + soLuong + ", " + "tienHoan=" + tienHoan + "}";
    }
}
