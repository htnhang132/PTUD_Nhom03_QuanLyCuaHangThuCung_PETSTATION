package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể yêu cầu phê duyệt khớp bảng [yeu_cau_phe_duyet].
 */
public class YeuCauPheDuyet implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maYeuCau;
    private String loai;
    private long nguoiGuiId;
    private String loaiDoiTuong;
    private String maDoiTuong;
    private String lyDo;
    private String trangThai;
    private Long nguoiXuLyId;
    private String phanHoi;
    private LocalDateTime guiLuc;
    private LocalDateTime xuLyLuc;
    private String noiDungTruoc;
    private String noiDungSau;
    private String payload;

    public YeuCauPheDuyet() {}

    public YeuCauPheDuyet(long id, String maYeuCau, String loai, long nguoiGuiId, String loaiDoiTuong, String maDoiTuong, String lyDo, String trangThai, Long nguoiXuLyId, String phanHoi, LocalDateTime guiLuc, LocalDateTime xuLyLuc, String noiDungTruoc, String noiDungSau, String payload) {
        this.id = id;
        this.maYeuCau = maYeuCau;
        this.loai = loai;
        this.nguoiGuiId = nguoiGuiId;
        this.loaiDoiTuong = loaiDoiTuong;
        this.maDoiTuong = maDoiTuong;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
        this.nguoiXuLyId = nguoiXuLyId;
        this.phanHoi = phanHoi;
        this.guiLuc = guiLuc;
        this.xuLyLuc = xuLyLuc;
        this.noiDungTruoc = noiDungTruoc;
        this.noiDungSau = noiDungSau;
        this.payload = payload;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaYeuCau() { return this.maYeuCau; }
    public String layMaYeuCau() { return this.maYeuCau; }
    public String maYeuCau() { return this.maYeuCau; }
    public void setMaYeuCau(String maYeuCau) { this.maYeuCau = maYeuCau; }
    public void datMaYeuCau(String maYeuCau) { this.maYeuCau = maYeuCau; }

    public String getLoai() { return this.loai; }
    public String layLoai() { return this.loai; }
    public String loai() { return this.loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public void datLoai(String loai) { this.loai = loai; }

    public long getNguoiGuiId() { return this.nguoiGuiId; }
    public long layNguoiGuiId() { return this.nguoiGuiId; }
    public long nguoiGuiId() { return this.nguoiGuiId; }
    public void setNguoiGuiId(long nguoiGuiId) { this.nguoiGuiId = nguoiGuiId; }
    public void datNguoiGuiId(long nguoiGuiId) { this.nguoiGuiId = nguoiGuiId; }

    public String getLoaiDoiTuong() { return this.loaiDoiTuong; }
    public String layLoaiDoiTuong() { return this.loaiDoiTuong; }
    public String loaiDoiTuong() { return this.loaiDoiTuong; }
    public void setLoaiDoiTuong(String loaiDoiTuong) { this.loaiDoiTuong = loaiDoiTuong; }
    public void datLoaiDoiTuong(String loaiDoiTuong) { this.loaiDoiTuong = loaiDoiTuong; }

    public String getMaDoiTuong() { return this.maDoiTuong; }
    public String layMaDoiTuong() { return this.maDoiTuong; }
    public String maDoiTuong() { return this.maDoiTuong; }
    public void setMaDoiTuong(String maDoiTuong) { this.maDoiTuong = maDoiTuong; }
    public void datMaDoiTuong(String maDoiTuong) { this.maDoiTuong = maDoiTuong; }

    public String getLyDo() { return this.lyDo; }
    public String layLyDo() { return this.lyDo; }
    public String lyDo() { return this.lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public void datLyDo(String lyDo) { this.lyDo = lyDo; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Long getNguoiXuLyId() { return this.nguoiXuLyId; }
    public Long layNguoiXuLyId() { return this.nguoiXuLyId; }
    public Long nguoiXuLyId() { return this.nguoiXuLyId; }
    public void setNguoiXuLyId(Long nguoiXuLyId) { this.nguoiXuLyId = nguoiXuLyId; }
    public void datNguoiXuLyId(Long nguoiXuLyId) { this.nguoiXuLyId = nguoiXuLyId; }

    public String getPhanHoi() { return this.phanHoi; }
    public String layPhanHoi() { return this.phanHoi; }
    public String phanHoi() { return this.phanHoi; }
    public void setPhanHoi(String phanHoi) { this.phanHoi = phanHoi; }
    public void datPhanHoi(String phanHoi) { this.phanHoi = phanHoi; }

    public LocalDateTime getGuiLuc() { return this.guiLuc; }
    public LocalDateTime layGuiLuc() { return this.guiLuc; }
    public LocalDateTime guiLuc() { return this.guiLuc; }
    public void setGuiLuc(LocalDateTime guiLuc) { this.guiLuc = guiLuc; }
    public void datGuiLuc(LocalDateTime guiLuc) { this.guiLuc = guiLuc; }

    public LocalDateTime getXuLyLuc() { return this.xuLyLuc; }
    public LocalDateTime layXuLyLuc() { return this.xuLyLuc; }
    public LocalDateTime xuLyLuc() { return this.xuLyLuc; }
    public void setXuLyLuc(LocalDateTime xuLyLuc) { this.xuLyLuc = xuLyLuc; }
    public void datXuLyLuc(LocalDateTime xuLyLuc) { this.xuLyLuc = xuLyLuc; }

    public String getNoiDungTruoc() { return this.noiDungTruoc; }
    public String layNoiDungTruoc() { return this.noiDungTruoc; }
    public String noiDungTruoc() { return this.noiDungTruoc; }
    public void setNoiDungTruoc(String noiDungTruoc) { this.noiDungTruoc = noiDungTruoc; }
    public void datNoiDungTruoc(String noiDungTruoc) { this.noiDungTruoc = noiDungTruoc; }

    public String getNoiDungSau() { return this.noiDungSau; }
    public String layNoiDungSau() { return this.noiDungSau; }
    public String noiDungSau() { return this.noiDungSau; }
    public void setNoiDungSau(String noiDungSau) { this.noiDungSau = noiDungSau; }
    public void datNoiDungSau(String noiDungSau) { this.noiDungSau = noiDungSau; }

    public String getPayload() { return this.payload; }
    public String layPayload() { return this.payload; }
    public String payload() { return this.payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public void datPayload(String payload) { this.payload = payload; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YeuCauPheDuyet that = (YeuCauPheDuyet) o;
        return this.id == that.id
                && Objects.equals(maYeuCau, that.maYeuCau)
                && Objects.equals(loai, that.loai)
                && this.nguoiGuiId == that.nguoiGuiId
                && Objects.equals(loaiDoiTuong, that.loaiDoiTuong)
                && Objects.equals(maDoiTuong, that.maDoiTuong)
                && Objects.equals(lyDo, that.lyDo)
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(nguoiXuLyId, that.nguoiXuLyId)
                && Objects.equals(phanHoi, that.phanHoi)
                && Objects.equals(guiLuc, that.guiLuc)
                && Objects.equals(xuLyLuc, that.xuLyLuc)
                && Objects.equals(noiDungTruoc, that.noiDungTruoc)
                && Objects.equals(noiDungSau, that.noiDungSau)
                && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maYeuCau, loai, nguoiGuiId, loaiDoiTuong, maDoiTuong, lyDo, trangThai, nguoiXuLyId, phanHoi, guiLuc, xuLyLuc, noiDungTruoc, noiDungSau, payload);
    }

    @Override
    public String toString() {
        return "YeuCauPheDuyet{" + "id=" + id + ", " + "maYeuCau=" + maYeuCau + ", " + "loai=" + loai + ", " + "nguoiGuiId=" + nguoiGuiId + ", " + "loaiDoiTuong=" + loaiDoiTuong + ", " + "maDoiTuong=" + maDoiTuong + ", " + "lyDo=" + lyDo + ", " + "trangThai=" + trangThai + ", " + "nguoiXuLyId=" + nguoiXuLyId + ", " + "phanHoi=" + phanHoi + ", " + "guiLuc=" + guiLuc + ", " + "xuLyLuc=" + xuLyLuc + ", " + "noiDungTruoc=" + noiDungTruoc + ", " + "noiDungSau=" + noiDungSau + ", " + "payload=" + payload + "}";
    }
}
