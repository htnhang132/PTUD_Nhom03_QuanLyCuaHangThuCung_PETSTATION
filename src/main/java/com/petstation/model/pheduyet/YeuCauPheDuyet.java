package com.petstation.model.pheduyet;

import com.petstation.model.chung.TrangThaiPheDuyet;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;

public class YeuCauPheDuyet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maYeuCau;
    private NhanVien nguoiGui;
    private NhanVien nguoiXuLy;
    private String loai;
    private String loaiDoiTuong;
    private String maDoiTuong;
    private String lyDo;
    private String phanHoi;
    private TrangThaiPheDuyet trangThai;
    private LocalDateTime guiLuc;
    private LocalDateTime xuLyLuc;
    private String noiDungTruoc;
    private String noiDungSau;
    private String payload;

    public YeuCauPheDuyet() {
        this.trangThai = TrangThaiPheDuyet.CHO_DUYET;
    }

    public String getMaYeuCau() {
        return maYeuCau;
    }

    public void setMaYeuCau(String maYeuCau) {
        this.maYeuCau = maYeuCau;
    }

    public NhanVien getNguoiGui() {
        return nguoiGui;
    }

    public void setNguoiGui(NhanVien nguoiGui) {
        this.nguoiGui = nguoiGui;
    }

    public NhanVien getNguoiXuLy() {
        return nguoiXuLy;
    }

    public void setNguoiXuLy(NhanVien nguoiXuLy) {
        this.nguoiXuLy = nguoiXuLy;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public String getLoaiDoiTuong() {
        return loaiDoiTuong;
    }

    public void setLoaiDoiTuong(String loaiDoiTuong) {
        this.loaiDoiTuong = loaiDoiTuong;
    }

    public String getMaDoiTuong() {
        return maDoiTuong;
    }

    public void setMaDoiTuong(String maDoiTuong) {
        this.maDoiTuong = maDoiTuong;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getPhanHoi() {
        return phanHoi;
    }

    public void setPhanHoi(String phanHoi) {
        this.phanHoi = phanHoi;
    }

    public TrangThaiPheDuyet getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiPheDuyet trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getGuiLuc() {
        return guiLuc;
    }

    public void setGuiLuc(LocalDateTime guiLuc) {
        this.guiLuc = guiLuc;
    }

    public LocalDateTime getXuLyLuc() {
        return xuLyLuc;
    }

    public void setXuLyLuc(LocalDateTime xuLyLuc) {
        this.xuLyLuc = xuLyLuc;
    }

    public String getNoiDungTruoc() {
        return noiDungTruoc;
    }

    public void setNoiDungTruoc(String noiDungTruoc) {
        this.noiDungTruoc = noiDungTruoc;
    }

    public String getNoiDungSau() {
        return noiDungSau;
    }

    public void setNoiDungSau(String noiDungSau) {
        this.noiDungSau = noiDungSau;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
