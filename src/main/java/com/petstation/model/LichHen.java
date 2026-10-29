package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể lịch hẹn dịch vụ khớp bảng [lich_hen].
 */
public class LichHen implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maLichHen;
    private long khachHangId;
    private long thuCungId;
    private String loaiDichVu;
    private LocalDateTime batDau;
    private LocalDateTime ketThuc;
    private String trangThai;
    private String ghiChu;
    private Long nhanVienId;
    private Long dichVuId;

    public LichHen() {}

    public LichHen(long id, String maLichHen, long khachHangId, long thuCungId, String loaiDichVu, LocalDateTime batDau, LocalDateTime ketThuc, String trangThai, String ghiChu, Long nhanVienId, Long dichVuId) {
        this.id = id;
        this.maLichHen = maLichHen;
        this.khachHangId = khachHangId;
        this.thuCungId = thuCungId;
        this.loaiDichVu = loaiDichVu;
        this.batDau = batDau;
        this.ketThuc = ketThuc;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.nhanVienId = nhanVienId;
        this.dichVuId = dichVuId;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaLichHen() { return this.maLichHen; }
    public String layMaLichHen() { return this.maLichHen; }
    public String maLichHen() { return this.maLichHen; }
    public void setMaLichHen(String maLichHen) { this.maLichHen = maLichHen; }
    public void datMaLichHen(String maLichHen) { this.maLichHen = maLichHen; }

    public long getKhachHangId() { return this.khachHangId; }
    public long layKhachHangId() { return this.khachHangId; }
    public long khachHangId() { return this.khachHangId; }
    public void setKhachHangId(long khachHangId) { this.khachHangId = khachHangId; }
    public void datKhachHangId(long khachHangId) { this.khachHangId = khachHangId; }

    public long getThuCungId() { return this.thuCungId; }
    public long layThuCungId() { return this.thuCungId; }
    public long thuCungId() { return this.thuCungId; }
    public void setThuCungId(long thuCungId) { this.thuCungId = thuCungId; }
    public void datThuCungId(long thuCungId) { this.thuCungId = thuCungId; }

    public String getLoaiDichVu() { return this.loaiDichVu; }
    public String layLoaiDichVu() { return this.loaiDichVu; }
    public String loaiDichVu() { return this.loaiDichVu; }
    public void setLoaiDichVu(String loaiDichVu) { this.loaiDichVu = loaiDichVu; }
    public void datLoaiDichVu(String loaiDichVu) { this.loaiDichVu = loaiDichVu; }

    public LocalDateTime getBatDau() { return this.batDau; }
    public LocalDateTime layBatDau() { return this.batDau; }
    public LocalDateTime batDau() { return this.batDau; }
    public void setBatDau(LocalDateTime batDau) { this.batDau = batDau; }
    public void datBatDau(LocalDateTime batDau) { this.batDau = batDau; }

    public LocalDateTime getKetThuc() { return this.ketThuc; }
    public LocalDateTime layKetThuc() { return this.ketThuc; }
    public LocalDateTime ketThuc() { return this.ketThuc; }
    public void setKetThuc(LocalDateTime ketThuc) { this.ketThuc = ketThuc; }
    public void datKetThuc(LocalDateTime ketThuc) { this.ketThuc = ketThuc; }

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

    public Long getNhanVienId() { return this.nhanVienId; }
    public Long layNhanVienId() { return this.nhanVienId; }
    public Long nhanVienId() { return this.nhanVienId; }
    public void setNhanVienId(Long nhanVienId) { this.nhanVienId = nhanVienId; }
    public void datNhanVienId(Long nhanVienId) { this.nhanVienId = nhanVienId; }

    public Long getDichVuId() { return this.dichVuId; }
    public Long layDichVuId() { return this.dichVuId; }
    public Long dichVuId() { return this.dichVuId; }
    public void setDichVuId(Long dichVuId) { this.dichVuId = dichVuId; }
    public void datDichVuId(Long dichVuId) { this.dichVuId = dichVuId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LichHen that = (LichHen) o;
        return this.id == that.id
                && Objects.equals(maLichHen, that.maLichHen)
                && this.khachHangId == that.khachHangId
                && this.thuCungId == that.thuCungId
                && Objects.equals(loaiDichVu, that.loaiDichVu)
                && Objects.equals(batDau, that.batDau)
                && Objects.equals(ketThuc, that.ketThuc)
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(ghiChu, that.ghiChu)
                && Objects.equals(nhanVienId, that.nhanVienId)
                && Objects.equals(dichVuId, that.dichVuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maLichHen, khachHangId, thuCungId, loaiDichVu, batDau, ketThuc, trangThai, ghiChu, nhanVienId, dichVuId);
    }

    @Override
    public String toString() {
        return "LichHen{" + "id=" + id + ", " + "maLichHen=" + maLichHen + ", " + "khachHangId=" + khachHangId + ", " + "thuCungId=" + thuCungId + ", " + "loaiDichVu=" + loaiDichVu + ", " + "batDau=" + batDau + ", " + "ketThuc=" + ketThuc + ", " + "trangThai=" + trangThai + ", " + "ghiChu=" + ghiChu + ", " + "nhanVienId=" + nhanVienId + ", " + "dichVuId=" + dichVuId + "}";
    }
}
