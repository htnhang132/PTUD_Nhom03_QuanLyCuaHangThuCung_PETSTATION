package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class PhieuDoiTra implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maPhieu;

    private HoaDon hoaDonGoc;

    private TaiKhoan nguoiLap;

    private HoaDon hoaDonThayThe;

    private List<ChiTietDoiTra> danhSachChiTiet = new ArrayList<>();

    private long chenhLech;
    private String trangThai;
    private LocalDateTime taoLuc;
    private String lyDo;
    private String loaiPhieu;
    private long giaTriDuocDoi;
    private String khoaThaoTac;

    public PhieuDoiTra() {
        this.trangThai = "HOAN_TAT";
        this.taoLuc = LocalDateTime.now();
    }

    public PhieuDoiTra(String maPhieu) {
        this();
        setMaPhieu(maPhieu);
    }

    public PhieuDoiTra(long id) {
        this();
        setId(id);
    }

    public PhieuDoiTra(
            String maPhieu,
            HoaDon hoaDonGoc,
            TaiKhoan nguoiLap,
            long chenhLech,
            String trangThai,
            LocalDateTime taoLuc,
            String lyDo,
            String loaiPhieu,
            HoaDon hoaDonThayThe,
            long giaTriDuocDoi,
            String khoaThaoTac) {
        setMaPhieu(maPhieu);
        setHoaDonGoc(hoaDonGoc);
        setNguoiLap(nguoiLap);
        setChenhLech(chenhLech);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
        setLyDo(lyDo);
        setLoaiPhieu(loaiPhieu);
        setHoaDonThayThe(hoaDonThayThe);
        setGiaTriDuocDoi(giaTriDuocDoi);
        setKhoaThaoTac(khoaThaoTac);
    }

    public PhieuDoiTra(
            long id,
            String maPhieu,
            HoaDon hoaDonGoc,
            TaiKhoan nguoiLap,
            long chenhLech,
            String trangThai,
            LocalDateTime taoLuc,
            String lyDo,
            String loaiPhieu,
            HoaDon hoaDonThayThe,
            long giaTriDuocDoi,
            String khoaThaoTac) {
        this(maPhieu, hoaDonGoc, nguoiLap, chenhLech, trangThai, taoLuc, lyDo, loaiPhieu, hoaDonThayThe, giaTriDuocDoi, khoaThaoTac);
        setId(id);
    }

    public PhieuDoiTra(
            long id,
            String maPhieu,
            long hoaDonId,
            long nguoiLapId,
            long chenhLech,
            String trangThai,
            LocalDateTime taoLuc,
            String lyDo,
            String loaiPhieu,
            Long hoaDonThayTheId,
            long giaTriDuocDoi,
            String khoaThaoTac) {
        this(
                id,
                maPhieu,
                new HoaDon(hoaDonId),
                new TaiKhoan(nguoiLapId),
                chenhLech,
                trangThai,
                taoLuc,
                lyDo,
                loaiPhieu,
                hoaDonThayTheId == null ? null : new HoaDon(hoaDonThayTheId),
                giaTriDuocDoi,
                khoaThaoTac);
    }

    public long tinhTongGiaTriHoan() {
        if (danhSachChiTiet != null && !danhSachChiTiet.isEmpty()) {
            long tong = 0L;
            for (ChiTietDoiTra ct : danhSachChiTiet) {
                if (ct != null) {
                    tong += ct.tinhTienHoan();
                }
            }
            return tong;
        }
        return Math.max(0L, this.giaTriDuocDoi);
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maPhieu);
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maPhieu == null || this.maPhieu.isBlank()) && id > 0) {
            this.maPhieu = DuLieu.taoMaTuId("DT", 8, id);
        }
    }

    public String getMaPhieu() {
        return this.maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu == null ? null : maPhieu.trim();
    }

    public HoaDon getHoaDonGoc() {
        return this.hoaDonGoc;
    }

    public void setHoaDonGoc(HoaDon hoaDonGoc) {
        this.hoaDonGoc = hoaDonGoc;
    }

    public long getHoaDonId() {
        return hoaDonGoc == null ? 0L : hoaDonGoc.getId();
    }

    public void setHoaDonId(long hoaDonId) {
        this.hoaDonGoc = new HoaDon(hoaDonId);
    }

    public TaiKhoan getNguoiLap() {
        return this.nguoiLap;
    }

    public void setNguoiLap(TaiKhoan nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public long getNguoiLapId() {
        return nguoiLap == null ? 0L : nguoiLap.getId();
    }

    public void setNguoiLapId(long nguoiLapId) {
        this.nguoiLap = new TaiKhoan(nguoiLapId);
    }

    public List<ChiTietDoiTra> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietDoiTra> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
    }

    public long getChenhLech() {
        return this.chenhLech;
    }

    public void setChenhLech(long chenhLech) {
        this.chenhLech = chenhLech;
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

    public String getLyDo() {
        return this.lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getLoaiPhieu() {
        return this.loaiPhieu;
    }

    public void setLoaiPhieu(String loaiPhieu) {
        this.loaiPhieu = loaiPhieu;
    }

    public HoaDon getHoaDonThayThe() {
        return this.hoaDonThayThe;
    }

    public void setHoaDonThayThe(HoaDon hoaDonThayThe) {
        this.hoaDonThayThe = hoaDonThayThe;
    }

    public Long getHoaDonThayTheId() {
        return hoaDonThayThe == null ? null : hoaDonThayThe.getId();
    }

    public void setHoaDonThayTheId(Long hoaDonThayTheId) {
        this.hoaDonThayThe = hoaDonThayTheId == null ? null : new HoaDon(hoaDonThayTheId);
    }

    public long getGiaTriDuocDoi() {
        return this.giaTriDuocDoi;
    }

    public void setGiaTriDuocDoi(long giaTriDuocDoi) {
        if (giaTriDuocDoi < 0)
            throw new IllegalArgumentException("Giá trị được đổi không được âm.");
        this.giaTriDuocDoi = giaTriDuocDoi;
    }

    public String getKhoaThaoTac() {
        return this.khoaThaoTac;
    }

    public void setKhoaThaoTac(String khoaThaoTac) {
        this.khoaThaoTac = khoaThaoTac;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PhieuDoiTra that = (PhieuDoiTra) o;
        return Objects.equals(this.maPhieu, that.maPhieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieu);
    }

    @Override
    public String toString() {
        return "PhieuDoiTra{" + "maPhieu='" + maPhieu + '\'' + ", hoaDonId=" + getHoaDonId()
                + ", chenhLech=" + chenhLech + ", trangThai='" + trangThai + '\'' + '}';
    }
}
