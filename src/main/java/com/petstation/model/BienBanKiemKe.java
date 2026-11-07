package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class BienBanKiemKe implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maBienBan;

    private TaiKhoan nguoiLap;

    private List<ChiTietKiemKe> danhSachChiTiet = new ArrayList<>();

    private String kyKiemKe;
    private String trangThai;
    private LocalDateTime taoLuc;

    public BienBanKiemKe() {
        this.trangThai = "DA_DUYET";
        this.taoLuc = LocalDateTime.now();
    }

    public BienBanKiemKe(String maBienBan) {
        this();
        setMaBienBan(maBienBan);
    }

    public BienBanKiemKe(long id) {
        this();
        setId(id);
    }

    public BienBanKiemKe(
            String soBienBan,
            TaiKhoan nguoiLap,
            String kyKiemKe,
            String trangThai,
            LocalDateTime taoLuc) {
        setSoBienBan(soBienBan);
        setNguoiLap(nguoiLap);
        setKyKiemKe(kyKiemKe);
        setTrangThai(trangThai);
        setTaoLuc(taoLuc);
    }

    public BienBanKiemKe(
            long id,
            String soBienBan,
            TaiKhoan nguoiLap,
            String kyKiemKe,
            String trangThai,
            LocalDateTime taoLuc) {
        this(soBienBan, nguoiLap, kyKiemKe, trangThai, taoLuc);
        setId(id);
    }

    public BienBanKiemKe(
            long id,
            String soBienBan,
            long nguoiLapId,
            String kyKiemKe,
            String trangThai,
            LocalDateTime taoLuc) {
        this(id, soBienBan, new TaiKhoan(nguoiLapId), kyKiemKe, trangThai, taoLuc);
    }

    public int tinhSoLoLechKho() {
        if (danhSachChiTiet == null || danhSachChiTiet.isEmpty()) {
            return 0;
        }
        int dem = 0;
        for (ChiTietKiemKe ct : danhSachChiTiet) {
            if (ct != null && ct.kiemTraCoLechKho()) {
                dem++;
            }
        }
        return dem;
    }

    public int getSoLoLechKho() {
        return tinhSoLoLechKho();
    }

    public int laySoLoLechKho() {
        return tinhSoLoLechKho();
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, getMaBienBan());
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, getMaBienBan());
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maBienBan == null || this.maBienBan.isBlank()) && id > 0) {
            this.maBienBan = DuLieu.taoMaTuId("KK", 8, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    public String getMaBienBan() {
        return this.maBienBan;
    }

    public String layMaBienBan() {
        return this.maBienBan;
    }

    public String maBienBan() {
        return this.maBienBan;
    }

    public void setMaBienBan(String maBienBan) {
        this.maBienBan = maBienBan == null ? null : maBienBan.trim();
    }

    public void datMaBienBan(String maBienBan) {
        setMaBienBan(maBienBan);
    }

    public String getSoBienBan() {
        return getMaBienBan();
    }

    public String laySoBienBan() {
        return getMaBienBan();
    }

    public String soBienBan() {
        return getMaBienBan();
    }

    public void setSoBienBan(String soBienBan) {
        setMaBienBan(soBienBan);
    }

    public void datSoBienBan(String soBienBan) {
        setMaBienBan(soBienBan);
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

    public long getNguoiLapId() {
        return nguoiLap == null ? 0L : nguoiLap.getId();
    }

    public long layNguoiLapId() {
        return getNguoiLapId();
    }

    public long nguoiLapId() {
        return getNguoiLapId();
    }

    public void setNguoiLapId(long nguoiLapId) {
        this.nguoiLap = new TaiKhoan(nguoiLapId);
    }

    public void datNguoiLapId(long nguoiLapId) {
        setNguoiLapId(nguoiLapId);
    }

    public List<ChiTietKiemKe> getDanhSachChiTiet() {
        return this.danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietKiemKe> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet == null ? new ArrayList<>() : new ArrayList<>(danhSachChiTiet);
    }

    public String getKyKiemKe() {
        return this.kyKiemKe;
    }

    public String layKyKiemKe() {
        return this.kyKiemKe;
    }

    public String kyKiemKe() {
        return this.kyKiemKe;
    }

    public void setKyKiemKe(String kyKiemKe) {
        this.kyKiemKe = kyKiemKe;
    }

    public void datKyKiemKe(String kyKiemKe) {
        setKyKiemKe(kyKiemKe);
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
        BienBanKiemKe that = (BienBanKiemKe) o;
        return Objects.equals(this.getMaBienBan(), that.getMaBienBan());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaBienBan());
    }

    @Override
    public String toString() {
        return "BienBanKiemKe{" + "maBienBan='" + getMaBienBan() + '\'' + ", nguoiLapId=" + getNguoiLapId() + ", kyKiemKe='" + kyKiemKe + '\'' + ", trangThai='" + trangThai + '\'' + '}';
    }
}
