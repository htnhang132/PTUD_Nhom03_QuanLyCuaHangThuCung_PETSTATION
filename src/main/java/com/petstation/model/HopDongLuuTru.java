package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể hợp đồng lưu trú khớp bảng [hop_dong_luu_tru].
 */
public class HopDongLuuTru implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maHopDong;
    private long khachHangId;
    private long thuCungId;
    private long phongId;
    private String hinhThucGui;
    private LocalDateTime ngayVao;
    private LocalDateTime ngayRaDuKien;
    private LocalDateTime ngayRaThucTe;
    private long tienCoc;
    private String phuongThucCoc;
    private String khoaThuCoc;
    private String trangThai;
    private long giaDem;
    private String ghiChu;
    private Long hoaDonId;
    private boolean daNhanCoc;
    private LocalDateTime nhanCocLuc;
    private Long nhanCocBoi;
    private Long phienNhanCocId;
    private long cocYeuCau;
    private String lyDoDoiCoc;

    public HopDongLuuTru() {}

    public HopDongLuuTru(long id, String maHopDong, long khachHangId, long thuCungId, long phongId, String hinhThucGui, LocalDateTime ngayVao, LocalDateTime ngayRaDuKien, LocalDateTime ngayRaThucTe, long tienCoc, String phuongThucCoc, String khoaThuCoc, String trangThai, long giaDem, String ghiChu, Long hoaDonId, boolean daNhanCoc, LocalDateTime nhanCocLuc, Long nhanCocBoi, Long phienNhanCocId, long cocYeuCau, String lyDoDoiCoc) {
        this.id = id;
        this.maHopDong = maHopDong;
        this.khachHangId = khachHangId;
        this.thuCungId = thuCungId;
        this.phongId = phongId;
        this.hinhThucGui = hinhThucGui;
        this.ngayVao = ngayVao;
        this.ngayRaDuKien = ngayRaDuKien;
        this.ngayRaThucTe = ngayRaThucTe;
        this.tienCoc = tienCoc;
        this.phuongThucCoc = phuongThucCoc;
        this.khoaThuCoc = khoaThuCoc;
        this.trangThai = trangThai;
        this.giaDem = giaDem;
        this.ghiChu = ghiChu;
        this.hoaDonId = hoaDonId;
        this.daNhanCoc = daNhanCoc;
        this.nhanCocLuc = nhanCocLuc;
        this.nhanCocBoi = nhanCocBoi;
        this.phienNhanCocId = phienNhanCocId;
        this.cocYeuCau = cocYeuCau;
        this.lyDoDoiCoc = lyDoDoiCoc;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaHopDong() { return this.maHopDong; }
    public String layMaHopDong() { return this.maHopDong; }
    public String maHopDong() { return this.maHopDong; }
    public void setMaHopDong(String maHopDong) { this.maHopDong = maHopDong; }
    public void datMaHopDong(String maHopDong) { this.maHopDong = maHopDong; }

    public long getKhachHangId() { return this.khachHangId; }
    public long layKhachHangId() { return this.khachHangId; }
    public long khachHangId() { return this.khachHangId; }
    public void setKhachHangId(long khachHangId) { this.khachHangId = khachHangId; }
    public void datKhachHangId(long khachHangId) { this.khachHangId = khachHangId; }

    public long getThuCungId() { return this.thuCungId; }
    public long layThuCungId() { return this.thuCungId; }
    public long thuCungId() { return this.thuCungId; }
    public void setThuCungId(long thuCungId) { this.thuCungId = thuCungId; }
    public void datThuCungId(long thuCungId) { this.thuCungId = thuCungId; }

    public long getPhongId() { return this.phongId; }
    public long layPhongId() { return this.phongId; }
    public long phongId() { return this.phongId; }
    public void setPhongId(long phongId) { this.phongId = phongId; }
    public void datPhongId(long phongId) { this.phongId = phongId; }

    public String getHinhThucGui() { return this.hinhThucGui; }
    public String layHinhThucGui() { return this.hinhThucGui; }
    public String hinhThucGui() { return this.hinhThucGui; }
    public void setHinhThucGui(String hinhThucGui) { this.hinhThucGui = hinhThucGui; }
    public void datHinhThucGui(String hinhThucGui) { this.hinhThucGui = hinhThucGui; }

    public LocalDateTime getNgayVao() { return this.ngayVao; }
    public LocalDateTime layNgayVao() { return this.ngayVao; }
    public LocalDateTime ngayVao() { return this.ngayVao; }
    public void setNgayVao(LocalDateTime ngayVao) { this.ngayVao = ngayVao; }
    public void datNgayVao(LocalDateTime ngayVao) { this.ngayVao = ngayVao; }

    public LocalDateTime getNgayRaDuKien() { return this.ngayRaDuKien; }
    public LocalDateTime layNgayRaDuKien() { return this.ngayRaDuKien; }
    public LocalDateTime ngayRaDuKien() { return this.ngayRaDuKien; }
    public void setNgayRaDuKien(LocalDateTime ngayRaDuKien) { this.ngayRaDuKien = ngayRaDuKien; }
    public void datNgayRaDuKien(LocalDateTime ngayRaDuKien) { this.ngayRaDuKien = ngayRaDuKien; }

    public LocalDateTime getNgayRaThucTe() { return this.ngayRaThucTe; }
    public LocalDateTime layNgayRaThucTe() { return this.ngayRaThucTe; }
    public LocalDateTime ngayRaThucTe() { return this.ngayRaThucTe; }
    public void setNgayRaThucTe(LocalDateTime ngayRaThucTe) { this.ngayRaThucTe = ngayRaThucTe; }
    public void datNgayRaThucTe(LocalDateTime ngayRaThucTe) { this.ngayRaThucTe = ngayRaThucTe; }

    public long getTienCoc() { return this.tienCoc; }
    public long layTienCoc() { return this.tienCoc; }
    public long tienCoc() { return this.tienCoc; }
    public void setTienCoc(long tienCoc) { this.tienCoc = tienCoc; }
    public void datTienCoc(long tienCoc) { this.tienCoc = tienCoc; }

    public String getPhuongThucCoc() { return this.phuongThucCoc; }
    public String layPhuongThucCoc() { return this.phuongThucCoc; }
    public String phuongThucCoc() { return this.phuongThucCoc; }
    public void setPhuongThucCoc(String phuongThucCoc) { this.phuongThucCoc = phuongThucCoc; }
    public void datPhuongThucCoc(String phuongThucCoc) { this.phuongThucCoc = phuongThucCoc; }

    public String getKhoaThuCoc() { return this.khoaThuCoc; }
    public String layKhoaThuCoc() { return this.khoaThuCoc; }
    public String khoaThuCoc() { return this.khoaThuCoc; }
    public void setKhoaThuCoc(String khoaThuCoc) { this.khoaThuCoc = khoaThuCoc; }
    public void datKhoaThuCoc(String khoaThuCoc) { this.khoaThuCoc = khoaThuCoc; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public long getGiaDem() { return this.giaDem; }
    public long layGiaDem() { return this.giaDem; }
    public long giaDem() { return this.giaDem; }
    public void setGiaDem(long giaDem) { this.giaDem = giaDem; }
    public void datGiaDem(long giaDem) { this.giaDem = giaDem; }

    public String getGhiChu() { return this.ghiChu; }
    public String layGhiChu() { return this.ghiChu; }
    public String ghiChu() { return this.ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public void datGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Long getHoaDonId() { return this.hoaDonId; }
    public Long layHoaDonId() { return this.hoaDonId; }
    public Long hoaDonId() { return this.hoaDonId; }
    public void setHoaDonId(Long hoaDonId) { this.hoaDonId = hoaDonId; }
    public void datHoaDonId(Long hoaDonId) { this.hoaDonId = hoaDonId; }

    public boolean isDaNhanCoc() { return this.daNhanCoc; }
    public boolean layDaNhanCoc() { return this.daNhanCoc; }
    public boolean daNhanCoc() { return this.daNhanCoc; }
    public void setDaNhanCoc(boolean daNhanCoc) { this.daNhanCoc = daNhanCoc; }
    public void datDaNhanCoc(boolean daNhanCoc) { this.daNhanCoc = daNhanCoc; }

    public LocalDateTime getNhanCocLuc() { return this.nhanCocLuc; }
    public LocalDateTime layNhanCocLuc() { return this.nhanCocLuc; }
    public LocalDateTime nhanCocLuc() { return this.nhanCocLuc; }
    public void setNhanCocLuc(LocalDateTime nhanCocLuc) { this.nhanCocLuc = nhanCocLuc; }
    public void datNhanCocLuc(LocalDateTime nhanCocLuc) { this.nhanCocLuc = nhanCocLuc; }

    public Long getNhanCocBoi() { return this.nhanCocBoi; }
    public Long layNhanCocBoi() { return this.nhanCocBoi; }
    public Long nhanCocBoi() { return this.nhanCocBoi; }
    public void setNhanCocBoi(Long nhanCocBoi) { this.nhanCocBoi = nhanCocBoi; }
    public void datNhanCocBoi(Long nhanCocBoi) { this.nhanCocBoi = nhanCocBoi; }

    public Long getPhienNhanCocId() { return this.phienNhanCocId; }
    public Long layPhienNhanCocId() { return this.phienNhanCocId; }
    public Long phienNhanCocId() { return this.phienNhanCocId; }
    public void setPhienNhanCocId(Long phienNhanCocId) { this.phienNhanCocId = phienNhanCocId; }
    public void datPhienNhanCocId(Long phienNhanCocId) { this.phienNhanCocId = phienNhanCocId; }

    public long getCocYeuCau() { return this.cocYeuCau; }
    public long layCocYeuCau() { return this.cocYeuCau; }
    public long cocYeuCau() { return this.cocYeuCau; }
    public void setCocYeuCau(long cocYeuCau) { this.cocYeuCau = cocYeuCau; }
    public void datCocYeuCau(long cocYeuCau) { this.cocYeuCau = cocYeuCau; }

    public String getLyDoDoiCoc() { return this.lyDoDoiCoc; }
    public String layLyDoDoiCoc() { return this.lyDoDoiCoc; }
    public String lyDoDoiCoc() { return this.lyDoDoiCoc; }
    public void setLyDoDoiCoc(String lyDoDoiCoc) { this.lyDoDoiCoc = lyDoDoiCoc; }
    public void datLyDoDoiCoc(String lyDoDoiCoc) { this.lyDoDoiCoc = lyDoDoiCoc; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HopDongLuuTru that = (HopDongLuuTru) o;
        return this.id == that.id
                && Objects.equals(maHopDong, that.maHopDong)
                && this.khachHangId == that.khachHangId
                && this.thuCungId == that.thuCungId
                && this.phongId == that.phongId
                && Objects.equals(hinhThucGui, that.hinhThucGui)
                && Objects.equals(ngayVao, that.ngayVao)
                && Objects.equals(ngayRaDuKien, that.ngayRaDuKien)
                && Objects.equals(ngayRaThucTe, that.ngayRaThucTe)
                && this.tienCoc == that.tienCoc
                && Objects.equals(phuongThucCoc, that.phuongThucCoc)
                && Objects.equals(khoaThuCoc, that.khoaThuCoc)
                && Objects.equals(trangThai, that.trangThai)
                && this.giaDem == that.giaDem
                && Objects.equals(ghiChu, that.ghiChu)
                && Objects.equals(hoaDonId, that.hoaDonId)
                && this.daNhanCoc == that.daNhanCoc
                && Objects.equals(nhanCocLuc, that.nhanCocLuc)
                && Objects.equals(nhanCocBoi, that.nhanCocBoi)
                && Objects.equals(phienNhanCocId, that.phienNhanCocId)
                && this.cocYeuCau == that.cocYeuCau
                && Objects.equals(lyDoDoiCoc, that.lyDoDoiCoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maHopDong, khachHangId, thuCungId, phongId, hinhThucGui, ngayVao, ngayRaDuKien, ngayRaThucTe, tienCoc, phuongThucCoc, khoaThuCoc, trangThai, giaDem, ghiChu, hoaDonId, daNhanCoc, nhanCocLuc, nhanCocBoi, phienNhanCocId, cocYeuCau, lyDoDoiCoc);
    }

    @Override
    public String toString() {
        return "HopDongLuuTru{" + "id=" + id + ", " + "maHopDong=" + maHopDong + ", " + "khachHangId=" + khachHangId + ", " + "thuCungId=" + thuCungId + ", " + "phongId=" + phongId + ", " + "hinhThucGui=" + hinhThucGui + ", " + "ngayVao=" + ngayVao + ", " + "ngayRaDuKien=" + ngayRaDuKien + ", " + "ngayRaThucTe=" + ngayRaThucTe + ", " + "tienCoc=" + tienCoc + ", " + "phuongThucCoc=" + phuongThucCoc + ", " + "khoaThuCoc=" + khoaThuCoc + ", " + "trangThai=" + trangThai + ", " + "giaDem=" + giaDem + ", " + "ghiChu=" + ghiChu + ", " + "hoaDonId=" + hoaDonId + ", " + "daNhanCoc=" + daNhanCoc + ", " + "nhanCocLuc=" + nhanCocLuc + ", " + "nhanCocBoi=" + nhanCocBoi + ", " + "phienNhanCocId=" + phienNhanCocId + ", " + "cocYeuCau=" + cocYeuCau + ", " + "lyDoDoiCoc=" + lyDoDoiCoc + "}";
    }
}
