package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class NhatKyHeThong implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maNhatKy;
    private TaiKhoan nguoiThucHien;
    private String thaoTac;
    private String kieuThucThe;
    private String idThucThe;
    private String giaTriCu;
    private String giaTriMoi;
    private String ghiChu;
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
            TaiKhoan nguoiThucHien,
            String thaoTac,
            String kieuThucThe,
            String idThucThe,
            String giaTriCu,
            String giaTriMoi,
            String ghiChu,
            LocalDateTime thoiDiemXayRa) {
        this();
        setMaNhatKy(maNhatKy);
        setNguoiThucHien(nguoiThucHien);
        setThaoTac(thaoTac);
        setKieuThucThe(kieuThucThe);
        setIdThucThe(idThucThe);
        setGiaTriCu(giaTriCu);
        setGiaTriMoi(giaTriMoi);
        setGhiChu(ghiChu);
        setThoiDiemXayRa(thoiDiemXayRa);
    }

    public String getMaNhatKy() {
        return this.maNhatKy;
    }

    public void setMaNhatKy(String maNhatKy) {
        this.maNhatKy = maNhatKy == null ? null : maNhatKy.trim();
    }

    public TaiKhoan getNguoiThucHien() {
        return this.nguoiThucHien;
    }

    public void setNguoiThucHien(TaiKhoan nguoiThucHien) {
        this.nguoiThucHien = nguoiThucHien;
    }

    public String getTenDangNhapNguoiThucHien() {
        return nguoiThucHien == null ? null : nguoiThucHien.getTenDangNhap();
    }

    public void setTenDangNhapNguoiThucHien(String tenDangNhap) {
        this.nguoiThucHien = (tenDangNhap == null || tenDangNhap.isBlank()) ? null : new TaiKhoan(tenDangNhap);
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

    public String getGiaTriCu() {
        return this.giaTriCu;
    }

    public void setGiaTriCu(String giaTriCu) {
        this.giaTriCu = giaTriCu;
    }

    public String getGiaTriMoi() {
        return this.giaTriMoi;
    }

    public void setGiaTriMoi(String giaTriMoi) {
        this.giaTriMoi = giaTriMoi;
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getThoiDiemXayRa() {
        return this.thoiDiemXayRa;
    }

    public void setThoiDiemXayRa(LocalDateTime thoiDiemXayRa) {
        this.thoiDiemXayRa = thoiDiemXayRa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NhatKyHeThong that = (NhatKyHeThong) o;
        return Objects.equals(this.maNhatKy, that.maNhatKy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhatKy);
    }

    @Override
    public String toString() {
        return "NhatKyHeThong{" + "maNhatKy='" + maNhatKy + '\'' + ", nguoiThucHien=" + (nguoiThucHien != null ? nguoiThucHien.getTenDangNhap() : null)
                + ", thaoTac='" + thaoTac + '\'' + ", kieuThucThe='" + kieuThucThe + '\'' + '}';
    }
}
