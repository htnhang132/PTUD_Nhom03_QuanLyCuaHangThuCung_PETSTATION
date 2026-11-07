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

    public long layTongGiaTri() {
        return tinhTongGiaTri();
    }

    public long tongGiaTri() {
        return tinhTongGiaTri();
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, getMaPhieuXuat());
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, getMaPhieuXuat());
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maPhieuXuat == null || this.maPhieuXuat.isBlank()) && id > 0) {
            this.maPhieuXuat = DuLieu.taoMaTuId("PX", 8, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    public String getMaPhieuXuat() {
        return this.maPhieuXuat;
    }

    public String layMaPhieuXuat() {
        return this.maPhieuXuat;
    }

    public String maPhieuXuat() {
        return this.maPhieuXuat;
    }

    public void setMaPhieuXuat(String maPhieuXuat) {
        this.maPhieuXuat = maPhieuXuat == null ? null : maPhieuXuat.trim();
    }

    public void datMaPhieuXuat(String maPhieuXuat) {
        setMaPhieuXuat(maPhieuXuat);
    }

    public String getSoPhieu() {
        return getMaPhieuXuat();
    }

    public String laySoPhieu() {
        return getMaPhieuXuat();
    }

    public String soPhieu() {
        return getMaPhieuXuat();
    }

    public void setSoPhieu(String soPhieu) {
        setMaPhieuXuat(soPhieu);
    }

    public void datSoPhieu(String soPhieu) {
        setMaPhieuXuat(soPhieu);
    }

    public TaiKhoan getNguoiLap() {
        return this.nguoiLap;
    }

    public TaiKhoan layNguoiLap() {
        return this.nguoiLap;
    }

    public void setNguoiLap(TaiKhoan nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public void datNguoiLap(TaiKhoan nguoiLap) {
        setNguoiLap(nguoiLap);
    }

    public Long getNguoiLapId() {
        return nguoiLap == null ? null : nguoiLap.getId();
    }

    public Long layNguoiLapId() {
        return getNguoiLapId();
    }

    public Long nguoiLapId() {
        return getNguoiLapId();
    }

    public void setNguoiLapId(Long nguoiLapId) {
        this.nguoiLap = nguoiLapId == null ? null : new TaiKhoan(nguoiLapId);
    }

    public void datNguoiLapId(Long nguoiLapId) {
        setNguoiLapId(nguoiLapId);
    }

    public List<ChiTietPhieuXuat> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public List<ChiTietPhieuXuat> layDanhSachChiTiet() {
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

    public String layLoaiXuat() {
        return this.loaiXuat;
    }

    public String loaiXuat() {
        return this.loaiXuat;
    }

    public void setLoaiXuat(String loaiXuat) {
        this.loaiXuat = loaiXuat;
    }

    public void datLoaiXuat(String loaiXuat) {
        setLoaiXuat(loaiXuat);
    }

    public String getLyDo() {
        return this.lyDo;
    }

    public String layLyDo() {
        return this.lyDo;
    }

    public String lyDo() {
        return this.lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public void datLyDo(String lyDo) {
        setLyDo(lyDo);
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public String layTrangThai() {
        return this.trangThai;
    }

    public String trangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void datTrangThai(String trangThai) {
        setTrangThai(trangThai);
    }

    public LocalDateTime getTaoLuc() {
        return this.taoLuc;
    }

    public LocalDateTime layTaoLuc() {
        return this.taoLuc;
    }

    public LocalDateTime taoLuc() {
        return this.taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }

    public void datTaoLuc(LocalDateTime taoLuc) {
        setTaoLuc(taoLuc);
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
