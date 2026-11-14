package com.petstation.model.hethong;

import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class NhatKyHeThong implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maNhatKy;
    private NhanVien nguoiThucHien;
    private String thaoTac;
    private String kieuThucThe;
    private String idThucThe;
    private String ghiChu;
    private String loaiSuKien;
    private String ketQua;
    private LocalDateTime thoiDiemXayRa;

    public NhatKyHeThong() {
        this.thoiDiemXayRa = LocalDateTime.now();
    }

    public NhatKyHeThong(String maNhatKy) {
        this();
        setMaNhatKy(maNhatKy);
    }

    public NhatKyHeThong(
            String maNhatKy,
            NhanVien nguoiThucHien,
            String thaoTac,
            String kieuThucThe,
            String idThucThe,
            String ghiChu,
            String loaiSuKien,
            String ketQua,
            LocalDateTime thoiDiemXayRa) {
        this();
        setMaNhatKy(maNhatKy);
        setNguoiThucHien(nguoiThucHien);
        setThaoTac(thaoTac);
        setKieuThucThe(kieuThucThe);
        setIdThucThe(idThucThe);
        setGhiChu(ghiChu);
        setLoaiSuKien(loaiSuKien);
        setKetQua(ketQua);
        setThoiDiemXayRa(thoiDiemXayRa);
    }

    public String getMaNhatKy() {
        return this.maNhatKy;
    }

    public void setMaNhatKy(String maNhatKy) {
        this.maNhatKy = maNhatKy == null ? null : maNhatKy.trim();
    }

    public NhanVien getNguoiThucHien() {
        return this.nguoiThucHien;
    }

    public void setNguoiThucHien(NhanVien nguoiThucHien) {
        this.nguoiThucHien = nguoiThucHien;
    }

    public String getThaoTac() {
        return this.thaoTac;
    }

    public void setThaoTac(String thaoTac) {
        this.thaoTac = thaoTac;
    }

    public String getKieuThucThe() {
        return this.kieuThucThe;
    }

    public void setKieuThucThe(String kieuThucThe) {
        this.kieuThucThe = kieuThucThe;
    }

    public String getIdThucThe() {
        return this.idThucThe;
    }

    public void setIdThucThe(String idThucThe) {
        this.idThucThe = idThucThe;
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getLoaiSuKien() {
        return this.loaiSuKien;
    }

    public void setLoaiSuKien(String loaiSuKien) {
        this.loaiSuKien = loaiSuKien;
    }

    public String getKetQua() {
        return this.ketQua;
    }

    public void setKetQua(String ketQua) {
        this.ketQua = ketQua;
    }

    public LocalDateTime getThoiDiemXayRa() {
        return this.thoiDiemXayRa;
    }

    public void setThoiDiemXayRa(LocalDateTime thoiDiemXayRa) {
        this.thoiDiemXayRa = thoiDiemXayRa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhatKyHeThong that = (NhatKyHeThong) o;
        return Objects.equals(this.maNhatKy, that.maNhatKy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhatKy);
    }

    @Override
    public String toString() {
        return "NhatKyHeThong{"
                + "maNhatKy='"
                + maNhatKy
                + '\''
                + ", nguoiThucHien="
                + (nguoiThucHien != null ? nguoiThucHien.getMaNhanVien() : null)
                + ", thaoTac='"
                + thaoTac
                + '\''
                + ", kieuThucThe='"
                + kieuThucThe
                + '\''
                + '}';
    }
}
