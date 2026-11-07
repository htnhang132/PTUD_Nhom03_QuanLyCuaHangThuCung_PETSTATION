package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class YeuCauPheDuyet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maYeuCau;
    private String loai;
    private TaiKhoan nguoiGui;
    private String loaiDoiTuong;
    private String maDoiTuong;
    private String lyDo;
    private String trangThai;
    private TaiKhoan nguoiXuLy;
    private String phanHoi;
    private LocalDateTime guiLuc;
    private LocalDateTime xuLyLuc;
    private String noiDungTruoc;
    private String noiDungSau;
    private String payload;

    public YeuCauPheDuyet() {
        this.trangThai = "CHO_DUYET";
        this.guiLuc = LocalDateTime.now();
    }

    public YeuCauPheDuyet(String maYeuCau) {
        this();
        setMaYeuCau(maYeuCau);
    }

    public YeuCauPheDuyet(
            String maYeuCau,
            String loai,
            TaiKhoan nguoiGui,
            String loaiDoiTuong,
            String maDoiTuong,
            String lyDo,
            String trangThai,
            TaiKhoan nguoiXuLy,
            String phanHoi,
            LocalDateTime guiLuc,
            LocalDateTime xuLyLuc,
            String noiDungTruoc,
            String noiDungSau,
            String payload) {
        this();
        setMaYeuCau(maYeuCau);
        setLoai(loai);
        setNguoiGui(nguoiGui);
        setLoaiDoiTuong(loaiDoiTuong);
        setMaDoiTuong(maDoiTuong);
        setLyDo(lyDo);
        setTrangThai(trangThai);
        setNguoiXuLy(nguoiXuLy);
        setPhanHoi(phanHoi);
        setGuiLuc(guiLuc);
        setXuLyLuc(xuLyLuc);
        setNoiDungTruoc(noiDungTruoc);
        setNoiDungSau(noiDungSau);
        setPayload(payload);
    }

    public boolean kiemTraDaXuLy() {
        return xuLyLuc != null || (trangThai != null && !"CHO_DUYET".equalsIgnoreCase(trangThai));
    }

    public String getMaYeuCau() {
        return this.maYeuCau;
    }

    public void setMaYeuCau(String maYeuCau) {
        this.maYeuCau = maYeuCau == null ? null : maYeuCau.trim();
    }

    public String getLoai() {
        return this.loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public TaiKhoan getNguoiGui() {
        return this.nguoiGui;
    }

    public void setNguoiGui(TaiKhoan nguoiGui) {
        this.nguoiGui = nguoiGui;
    }

    public String getTenDangNhapNguoiGui() {
        return nguoiGui == null ? null : nguoiGui.getTenDangNhap();
    }

    public void setTenDangNhapNguoiGui(String tenDangNhap) {
        this.nguoiGui = (tenDangNhap == null || tenDangNhap.isBlank()) ? null : new TaiKhoan(tenDangNhap);
    }

    public String getLoaiDoiTuong() {
        return this.loaiDoiTuong;
    }

    public void setLoaiDoiTuong(String loaiDoiTuong) {
        this.loaiDoiTuong = loaiDoiTuong;
    }

    public String getMaDoiTuong() {
        return this.maDoiTuong;
    }

    public void setMaDoiTuong(String maDoiTuong) {
        this.maDoiTuong = maDoiTuong;
    }

    public String getLyDo() {
        return this.lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public TaiKhoan getNguoiXuLy() {
        return this.nguoiXuLy;
    }

    public void setNguoiXuLy(TaiKhoan nguoiXuLy) {
        this.nguoiXuLy = nguoiXuLy;
    }

    public String getTenDangNhapNguoiXuLy() {
        return nguoiXuLy == null ? null : nguoiXuLy.getTenDangNhap();
    }

    public void setTenDangNhapNguoiXuLy(String tenDangNhap) {
        this.nguoiXuLy = (tenDangNhap == null || tenDangNhap.isBlank()) ? null : new TaiKhoan(tenDangNhap);
    }

    public String getPhanHoi() {
        return this.phanHoi;
    }

    public void setPhanHoi(String phanHoi) {
        this.phanHoi = phanHoi;
    }

    public LocalDateTime getGuiLuc() {
        return this.guiLuc;
    }

    public void setGuiLuc(LocalDateTime guiLuc) {
        this.guiLuc = guiLuc;
    }

    public LocalDateTime getXuLyLuc() {
        return this.xuLyLuc;
    }

    public void setXuLyLuc(LocalDateTime xuLyLuc) {
        this.xuLyLuc = xuLyLuc;
    }

    public String getNoiDungTruoc() {
        return this.noiDungTruoc;
    }

    public void setNoiDungTruoc(String noiDungTruoc) {
        this.noiDungTruoc = noiDungTruoc;
    }

    public String getNoiDungSau() {
        return this.noiDungSau;
    }

    public void setNoiDungSau(String noiDungSau) {
        this.noiDungSau = noiDungSau;
    }

    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        YeuCauPheDuyet that = (YeuCauPheDuyet) o;
        return Objects.equals(this.maYeuCau, that.maYeuCau);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maYeuCau);
    }

    @Override
    public String toString() {
        return "YeuCauPheDuyet{" + "maYeuCau='" + maYeuCau + '\'' + ", loai='" + loai + '\''
                + ", nguoiGui=" + (nguoiGui != null ? nguoiGui.getTenDangNhap() : null) + ", trangThai='" + trangThai + '\'' + '}';
    }
}
