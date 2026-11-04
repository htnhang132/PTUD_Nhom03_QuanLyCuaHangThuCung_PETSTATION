package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể Tham số cấu hình hệ thống khớp bảng [cai_dat].
 * Tuân thủ chuẩn OOP môn PTUD: quan hệ kết hợp với TaiKhoan (người cập nhật)
 * và equals/hashCode chỉ theo khóa chính (khoa).
 */
public class CaiDat implements Serializable {
    private static final long serialVersionUID = 1L;

    private String khoa;
    private String giaTri;
    /** Quan hệ kết hợp: Tài khoản quản trị viên cập nhật tham số. */
    private TaiKhoan nguoiCapNhat;
    private LocalDateTime ngayCapNhat;

    /** Constructor mặc định. */
    public CaiDat() {
    }

    /** Constructor chỉ có khóa chính. */
    public CaiDat(String khoa) {
        setKhoa(khoa);
    }

    /** Constructor chuẩn OOP nhận đối tượng TaiKhoan. */
    public CaiDat(String khoa, String giaTri, TaiKhoan nguoiCapNhat, LocalDateTime ngayCapNhat) {
        setKhoa(khoa);
        setGiaTri(giaTri);
        setNguoiCapNhat(nguoiCapNhat);
        setNgayCapNhat(ngayCapNhat);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public CaiDat(String khoa, String giaTri, Long nguoiCapNhatId, LocalDateTime ngayCapNhat) {
        this(khoa, giaTri, nguoiCapNhatId == null ? null : new TaiKhoan(nguoiCapNhatId), ngayCapNhat);
    }

    public String getKhoa() {
        return this.khoa;
    }

    public String layKhoa() {
        return this.khoa;
    }

    public String khoa() {
        return this.khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa == null ? null : khoa.trim();
    }

    public void datKhoa(String khoa) {
        setKhoa(khoa);
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
        return Objects.equals(khoa, that.khoa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(khoa);
    }

    @Override
    public String toString() {
        return "CaiDat{" + "khoa='" + khoa + '\'' + ", giaTri='" + giaTri + '\'' + '}';
    }
}
