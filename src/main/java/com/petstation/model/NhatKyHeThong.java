package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import com.petstation.util.DuLieu;

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

    public NhatKyHeThong(long id) {
        this();
        setId(id);
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

    public NhatKyHeThong(
            long id,
            TaiKhoan nguoiThucHien,
            String thaoTac,
            String kieuThucThe,
            String idThucThe,
            String giaTriCu,
            String giaTriMoi,
            String ghiChu,
            LocalDateTime thoiDiemXayRa) {
        this(DuLieu.taoMaTuId("SYS", 8, id), nguoiThucHien, thaoTac, kieuThucThe, idThucThe, giaTriCu, giaTriMoi, ghiChu, thoiDiemXayRa);
        setId(id);
    }

    public NhatKyHeThong(
            long id,
            Long idNguoiThucHienTaiKhoan,
            String thaoTac,
            String kieuThucThe,
            String idThucThe,
            String giaTriCu,
            String giaTriMoi,
            String ghiChu,
            LocalDateTime thoiDiemXayRa) {
        this(
                id,
                idNguoiThucHienTaiKhoan == null ? null : new TaiKhoan(idNguoiThucHienTaiKhoan),
                thaoTac,
                kieuThucThe,
                idThucThe,
                giaTriCu,
                giaTriMoi,
                ghiChu,
                thoiDiemXayRa);
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maNhatKy);
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maNhatKy == null || this.maNhatKy.isBlank()) && id > 0) {
            this.maNhatKy = DuLieu.taoMaTuId("SYS", 8, id);
        }
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

    public Long getIdNguoiThucHienTaiKhoan() {
        return nguoiThucHien == null ? null : nguoiThucHien.getId();
    }

    public void setIdNguoiThucHienTaiKhoan(Long idNguoiThucHienTaiKhoan) {
        this.nguoiThucHien = idNguoiThucHienTaiKhoan == null ? null : new TaiKhoan(idNguoiThucHienTaiKhoan);
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
        return "NhatKyHeThong{" + "maNhatKy='" + maNhatKy + '\'' + ", idNguoiThucHienTaiKhoan=" + getIdNguoiThucHienTaiKhoan()
                + ", thaoTac='" + thaoTac + '\'' + ", kieuThucThe='" + kieuThucThe + '\'' + '}';
    }
}
