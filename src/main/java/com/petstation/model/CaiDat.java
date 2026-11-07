package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class CaiDat implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maCaiDat;
    private String giaTri;

    private TaiKhoan nguoiCapNhat;
    private LocalDateTime ngayCapNhat;

    public CaiDat() {
    }

    public CaiDat(String maCaiDat) {
        setMaCaiDat(maCaiDat);
    }

    public CaiDat(String maCaiDat, String giaTri, TaiKhoan nguoiCapNhat, LocalDateTime ngayCapNhat) {
        setMaCaiDat(maCaiDat);
        setGiaTri(giaTri);
        setNguoiCapNhat(nguoiCapNhat);
        setNgayCapNhat(ngayCapNhat);
    }

    public CaiDat(String maCaiDat, String giaTri, Long nguoiCapNhatId, LocalDateTime ngayCapNhat) {
        this(maCaiDat, giaTri, nguoiCapNhatId == null ? null : new TaiKhoan(nguoiCapNhatId), ngayCapNhat);
    }

    public String getMaCaiDat() {
        return this.maCaiDat;
    }

    public void setMaCaiDat(String maCaiDat) {
        this.maCaiDat = maCaiDat == null ? null : maCaiDat.trim();
    }

    public String getKhoa() {
        return getMaCaiDat();
    }

    public void setKhoa(String khoa) {
        setMaCaiDat(khoa);
    }

    public String getGiaTri() {
        return this.giaTri;
    }

    public void setGiaTri(String giaTri) {
        this.giaTri = giaTri;
    }

    public TaiKhoan getNguoiCapNhat() {
        return this.nguoiCapNhat;
    }

    public void setNguoiCapNhat(TaiKhoan nguoiCapNhat) {
        this.nguoiCapNhat = nguoiCapNhat;
    }

    public Long getNguoiCapNhatId() {
        return nguoiCapNhat == null ? null : nguoiCapNhat.getId();
    }

    public void setNguoiCapNhatId(Long nguoiCapNhatId) {
        this.nguoiCapNhat = nguoiCapNhatId == null ? null : new TaiKhoan(nguoiCapNhatId);
    }

    public LocalDateTime getNgayCapNhat() {
        return this.ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CaiDat that = (CaiDat) o;
        return Objects.equals(this.maCaiDat, that.maCaiDat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.maCaiDat);
    }

    @Override
    public String toString() {
        return "CaiDat{" + "maCaiDat='" + maCaiDat + '\'' + ", giaTri='" + giaTri + '\'' + '}';
    }
}
