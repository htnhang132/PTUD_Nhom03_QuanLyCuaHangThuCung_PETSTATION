package com.petstation.model.pheduyet;

import com.petstation.model.chung.TrangThaiPheDuyet;
import com.petstation.model.kho.SanPham;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DeXuatGia implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maYeuCau;
    private SanPham sanPham;
    private NhanVien nguoiGui;
    private long giaCu;
    private long giaMoi;
    private String lyDo;
    private TrangThaiPheDuyet trangThai;
    private LocalDateTime guiLuc;

    public DeXuatGia() {
        this.trangThai = TrangThaiPheDuyet.CHO_DUYET;
    }

    public String getMaYeuCau() {
        return maYeuCau;
    }

    public void setMaYeuCau(String maYeuCau) {
        this.maYeuCau = maYeuCau;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public NhanVien getNguoiGui() {
        return nguoiGui;
    }

    public void setNguoiGui(NhanVien nguoiGui) {
        this.nguoiGui = nguoiGui;
    }

    public long getGiaCu() {
        return giaCu;
    }

    public void setGiaCu(long giaCu) {
        this.giaCu = giaCu;
    }

    public long getGiaMoi() {
        return giaMoi;
    }

    public void setGiaMoi(long giaMoi) {
        this.giaMoi = giaMoi;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
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
}
