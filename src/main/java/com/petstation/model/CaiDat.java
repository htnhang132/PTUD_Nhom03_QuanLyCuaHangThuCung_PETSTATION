package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể Tham số cấu hình hệ thống khớp bảng [cai_dat].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính chuẩn hóa là mã cài đặt (maCaiDat / khoa).
 * - Quan hệ kết hợp với TaiKhoan (người cập nhật).
 * - Equals/hashCode chỉ theo khóa chính.
 */
public class CaiDat implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính chuẩn hóa: mã cài đặt (ví dụ: VAT_RATE, MAX_LOGIN_ATTEMPTS) */
    private String maCaiDat;
    private String giaTri;
    /** Quan hệ kết hợp: Tài khoản quản trị viên cập nhật tham số. */
    private TaiKhoan nguoiCapNhat;
    private LocalDateTime ngayCapNhat;

    /** Constructor mặc định. */
    public CaiDat() {
    }

    /** Constructor chỉ có khóa chính. */
    public CaiDat(String maCaiDat) {
        setMaCaiDat(maCaiDat);
    }

    /** Constructor chuẩn OOP nhận đối tượng TaiKhoan. */
    public CaiDat(String maCaiDat, String giaTri, TaiKhoan nguoiCapNhat, LocalDateTime ngayCapNhat) {
        setMaCaiDat(maCaiDat);
        setGiaTri(giaTri);
        setNguoiCapNhat(nguoiCapNhat);
        setNgayCapNhat(ngayCapNhat);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public CaiDat(String maCaiDat, String giaTri, Long nguoiCapNhatId, LocalDateTime ngayCapNhat) {
        this(maCaiDat, giaTri, nguoiCapNhatId == null ? null : new TaiKhoan(nguoiCapNhatId), ngayCapNhat);
    }

    public String getMaCaiDat() {
        return this.maCaiDat;
    }

    public String layMaCaiDat() {
        return this.maCaiDat;
    }

    public String maCaiDat() {
        return this.maCaiDat;
    }

    public void setMaCaiDat(String maCaiDat) {
        this.maCaiDat = maCaiDat == null ? null : maCaiDat.trim();
    }

    public void datMaCaiDat(String maCaiDat) {
        setMaCaiDat(maCaiDat);
    }

    // Alias khóa tương thích ngược
    public String getKhoa() {
        return getMaCaiDat();
    }

    public String layKhoa() {
        return getMaCaiDat();
    }

    public String khoa() {
        return getMaCaiDat();
    }

    public void setKhoa(String khoa) {
        setMaCaiDat(khoa);
    }

    public void datKhoa(String khoa) {
        setMaCaiDat(khoa);
    }

    public String getGiaTri() {
        return this.giaTri;
    }

    public String layGiaTri() {
        return this.giaTri;
    }

    public String giaTri() {
        return this.giaTri;
    }

    public void setGiaTri(String giaTri) {
        this.giaTri = giaTri;
    }

    public void datGiaTri(String giaTri) {
        setGiaTri(giaTri);
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

    public Long layNguoiCapNhatId() {
        return getNguoiCapNhatId();
    }

    public Long nguoiCapNhatId() {
        return getNguoiCapNhatId();
    }

    public void setNguoiCapNhatId(Long nguoiCapNhatId) {
        this.nguoiCapNhat = nguoiCapNhatId == null ? null : new TaiKhoan(nguoiCapNhatId);
    }

    public void datNguoiCapNhatId(Long nguoiCapNhatId) {
        setNguoiCapNhatId(nguoiCapNhatId);
    }

    public LocalDateTime getNgayCapNhat() {
        return this.ngayCapNhat;
    }

    public LocalDateTime layNgayCapNhat() {
        return this.ngayCapNhat;
    }

    public LocalDateTime ngayCapNhat() {
        return this.ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public void datNgayCapNhat(LocalDateTime ngayCapNhat) {
        setNgayCapNhat(ngayCapNhat);
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
