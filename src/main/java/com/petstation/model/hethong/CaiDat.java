package com.petstation.model.hethong;

import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CaiDat implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maCaiDat;
    private NhanVien nguoiCapNhat;
    private String giaTri;
    private LocalDateTime ngayCapNhat;

    public CaiDat() {}

    public CaiDat(
            String maCaiDat, NhanVien nguoiCapNhat, String giaTri, LocalDateTime ngayCapNhat) {
        this.maCaiDat = maCaiDat;
        this.nguoiCapNhat = nguoiCapNhat;
        this.giaTri = giaTri;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaCaiDat() {
        return maCaiDat;
    }

    public void setMaCaiDat(String maCaiDat) {
        this.maCaiDat = maCaiDat;
    }

    public NhanVien getNguoiCapNhat() {
        return nguoiCapNhat;
    }

    public void setNguoiCapNhat(NhanVien nguoiCapNhat) {
        this.nguoiCapNhat = nguoiCapNhat;
    }

    public String getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(String giaTri) {
        this.giaTri = giaTri;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }
}
