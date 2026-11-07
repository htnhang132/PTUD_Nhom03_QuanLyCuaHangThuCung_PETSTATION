package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class PhieuXuatKho implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieuXuat;

    private TaiKhoan nguoiLap;

    private List<ChiTietPhieuXuat> danhSachChiTiet = new ArrayList<>();

    private String loaiXuat;
    private String lyDo;
    private long tongGiaTri;
    private String trangThai;
    private LocalDateTime taoLuc;

    public PhieuXuatKho() {
        this.trangThai = "DA_XUAT";
        this.taoLuc = LocalDateTime.now();
    }

    public PhieuXuatKho(String maPhieuXuat) {
        this();
        setMaPhieuXuat(maPhieuXuat);
    }

    public PhieuXuatKho(long id) {
        this();
        setId(id);
    }

    public PhieuXuatKho(
            String maPhieuXuat,
            TaiKhoan nguoiLap,
            String loaiXuat,
            String lyDo,
            String trangThai,
            LocalDateTime taoLuc) {
        setMaPhieuXuat(maPhieuXuat);
        setNguoiLap(nguoiLap);
        setLoaiXuat(loaiXuat);
        setLyDo(lyDo);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
    }

    public PhieuXuatKho(
            long id,
            String maPhieuXuat,
            TaiKhoan nguoiLap,
            String loaiXuat,
            String lyDo,
            String trangThai,
            LocalDateTime taoLuc) {
        this(maPhieuXuat, nguoiLap, loaiXuat, lyDo, trangThai, taoLuc);
        setId(id);
    }

    public PhieuXuatKho(
            long id,
            String maPhieuXuat,
            long nguoiLapId,
            String loaiXuat,
            String lyDo,
            long tongGiaTri,
            String trangThai,
            LocalDateTime taoLuc) {
        this(id, maPhieuXuat, new TaiKhoan(nguoiLapId), loaiXuat, lyDo, trangThai, taoLuc);
        this.tongGiaTri = Math.max(0L, tongGiaTri);
    }

    public long tinhTongGiaTri() {
        if (danhSachChiTiet != null && !danhSachChiTiet.isEmpty()) {
            long tong = 0L;
            for (ChiTietPhieuXuat ct : danhSachChiTiet) {
                if (ct != null) {
                    tong += ct.tinhGiaTriXuat();
                }
            }
            return tong;
        }
        return Math.max(0L, this.tongGiaTri);
    }

    public long getTongGiaTri() {
        return tinhTongGiaTri();
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, getMaPhieuXuat());
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maPhieuXuat == null || this.maPhieuXuat.isBlank()) && id > 0) {
            this.maPhieuXuat = DuLieu.taoMaTuId("PX", 8, id);
        }
    }

    public String getMaPhieuXuat() {
        return this.maPhieuXuat;
    }

    public void setMaPhieuXuat(String maPhieuXuat) {
        this.maPhieuXuat = maPhieuXuat == null ? null : maPhieuXuat.trim();
    }

    public String getSoPhieu() {
        return getMaPhieuXuat();
    }

    public void setSoPhieu(String soPhieu) {
        setMaPhieuXuat(soPhieu);
    }

    public TaiKhoan getNguoiLap() {
        return this.nguoiLap;
    }

    public void setNguoiLap(TaiKhoan nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public Long getNguoiLapId() {
        return nguoiLap == null ? null : nguoiLap.getId();
    }

    public void setNguoiLapId(Long nguoiLapId) {
        this.nguoiLap = nguoiLapId == null ? null : new TaiKhoan(nguoiLapId);
    }

    public List<ChiTietPhieuXuat> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietPhieuXuat> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
    }

    public void themChiTietPhieuXuat(ChiTietPhieuXuat chiTiet) {
        if (chiTiet == null) return;
        if (this.danhSachChiTiet == null) this.danhSachChiTiet = new ArrayList<>();
        chiTiet.setPhieuXuat(this);
        this.danhSachChiTiet.add(chiTiet);
    }

    public String getLoaiXuat() {
        return this.loaiXuat;
    }

    public void setLoaiXuat(String loaiXuat) {
        this.loaiXuat = loaiXuat;
    }

    public String getLyDo() {
        return this.lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getTaoLuc() {
        return this.taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PhieuXuatKho that = (PhieuXuatKho) o;
        return Objects.equals(this.getMaPhieuXuat(), that.getMaPhieuXuat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaPhieuXuat());
    }

    @Override
    public String toString() {
        return "PhieuXuatKho{" + "maPhieuXuat='" + getMaPhieuXuat() + '\'' + ", nguoiLapId=" + getNguoiLapId()
                + ", loaiXuat='" + loaiXuat + '\'' + ", tongGiaTri=" + tinhTongGiaTri() + '}';
    }
}
