package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể tham số cấu hình hệ thống khớp bảng [cai_dat].
 */
public class CaiDat implements Serializable {
    private static final long serialVersionUID = 1L;

    private String khoa;
    private String giaTri;
    private Long nguoiCapNhatId;
    private LocalDateTime ngayCapNhat;

    public CaiDat() {}

    public CaiDat(String khoa, String giaTri, Long nguoiCapNhatId, LocalDateTime ngayCapNhat) {
        this.khoa = khoa;
        this.giaTri = giaTri;
        this.nguoiCapNhatId = nguoiCapNhatId;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getKhoa() { return this.khoa; }
    public String layKhoa() { return this.khoa; }
    public String khoa() { return this.khoa; }
    public void setKhoa(String khoa) { this.khoa = khoa; }
    public void datKhoa(String khoa) { this.khoa = khoa; }

    public String getGiaTri() { return this.giaTri; }
    public String layGiaTri() { return this.giaTri; }
    public String giaTri() { return this.giaTri; }
    public void setGiaTri(String giaTri) { this.giaTri = giaTri; }
    public void datGiaTri(String giaTri) { this.giaTri = giaTri; }

    public Long getNguoiCapNhatId() { return this.nguoiCapNhatId; }
    public Long layNguoiCapNhatId() { return this.nguoiCapNhatId; }
    public Long nguoiCapNhatId() { return this.nguoiCapNhatId; }
    public void setNguoiCapNhatId(Long nguoiCapNhatId) { this.nguoiCapNhatId = nguoiCapNhatId; }
    public void datNguoiCapNhatId(Long nguoiCapNhatId) { this.nguoiCapNhatId = nguoiCapNhatId; }

    public LocalDateTime getNgayCapNhat() { return this.ngayCapNhat; }
    public LocalDateTime layNgayCapNhat() { return this.ngayCapNhat; }
    public LocalDateTime ngayCapNhat() { return this.ngayCapNhat; }
    public void setNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
    public void datNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaiDat that = (CaiDat) o;
        return Objects.equals(khoa, that.khoa)
                && Objects.equals(giaTri, that.giaTri)
                && Objects.equals(nguoiCapNhatId, that.nguoiCapNhatId)
                && Objects.equals(ngayCapNhat, that.ngayCapNhat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(khoa, giaTri, nguoiCapNhatId, ngayCapNhat);
    }

    @Override
    public String toString() {
        return "CaiDat{" + "khoa=" + khoa + ", " + "giaTri=" + giaTri + ", " + "nguoiCapNhatId=" + nguoiCapNhatId + ", " + "ngayCapNhat=" + ngayCapNhat + "}";
    }
}
