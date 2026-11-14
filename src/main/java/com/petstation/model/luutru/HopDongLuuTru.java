package com.petstation.model.luutru;

import com.petstation.model.banhang.HoaDon;
import com.petstation.model.banhang.PhienLamViec;
import com.petstation.model.chung.TrangThaiHopDong;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.nhansu.NhanVien;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class HopDongLuuTru implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maHopDong;
    private KhachHang khachHang;
    private ThuCung thuCung;
    private PhongHotel phongHotel;
    private HoaDon hoaDon;
    private NhanVien nhanCocBoi;
    private PhienLamViec phienNhanCoc;
    private LocalDateTime ngayVao;
    private LocalDateTime ngayRaDuKien;
    private LocalDateTime ngayRaThucTe;
    private long giaDem;
    private long tienCoc;
    private Long cocYeuCau;
    private String phuongThucCoc;
    private String khoaThuCoc;
    private boolean daNhanCoc;
    private LocalDateTime nhanCocLuc;
    private String lyDoDoiCoc;
    private String ghiChu;
    private TrangThaiHopDong trangThai;

    public HopDongLuuTru() {
        this.phuongThucCoc = "TIEN_MAT";
        this.trangThai = TrangThaiHopDong.DA_DAT;
        this.ghiChu = "";
    }

    public String getMaHopDong() {
        return maHopDong;
    }

    public void setMaHopDong(String maHopDong) {
        this.maHopDong = maHopDong;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public ThuCung getThuCung() {
        return thuCung;
    }

    public void setThuCung(ThuCung thuCung) {
        this.thuCung = thuCung;
    }

    public PhongHotel getPhongHotel() {
        return phongHotel;
    }

    public void setPhongHotel(PhongHotel phongHotel) {
        this.phongHotel = phongHotel;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public NhanVien getNhanCocBoi() {
        return nhanCocBoi;
    }

    public void setNhanCocBoi(NhanVien nhanCocBoi) {
        this.nhanCocBoi = nhanCocBoi;
    }

    public PhienLamViec getPhienNhanCoc() {
        return phienNhanCoc;
    }

    public void setPhienNhanCoc(PhienLamViec phienNhanCoc) {
        this.phienNhanCoc = phienNhanCoc;
    }

    public LocalDateTime getNgayVao() {
        return ngayVao;
    }

    public void setNgayVao(LocalDateTime ngayVao) {
        this.ngayVao = ngayVao;
    }

    public LocalDateTime getNgayRaDuKien() {
        return ngayRaDuKien;
    }

    public void setNgayRaDuKien(LocalDateTime ngayRaDuKien) {
        this.ngayRaDuKien = ngayRaDuKien;
    }

    public LocalDateTime getNgayRaThucTe() {
        return ngayRaThucTe;
    }

    public void setNgayRaThucTe(LocalDateTime ngayRaThucTe) {
        this.ngayRaThucTe = ngayRaThucTe;
    }

    public long getGiaDem() {
        return giaDem;
    }

    public void setGiaDem(long giaDem) {
        this.giaDem = giaDem;
    }

    public long getTienCoc() {
        return tienCoc;
    }

    public void setTienCoc(long tienCoc) {
        this.tienCoc = tienCoc;
    }

    public Long getCocYeuCau() {
        return cocYeuCau;
    }

    public void setCocYeuCau(Long cocYeuCau) {
        this.cocYeuCau = cocYeuCau;
    }

    public String getPhuongThucCoc() {
        return phuongThucCoc;
    }

    public void setPhuongThucCoc(String phuongThucCoc) {
        this.phuongThucCoc = phuongThucCoc;
    }

    public String getKhoaThuCoc() {
        return khoaThuCoc;
    }

    public void setKhoaThuCoc(String khoaThuCoc) {
        this.khoaThuCoc = khoaThuCoc;
    }

    public boolean isDaNhanCoc() {
        return daNhanCoc;
    }

    public void setDaNhanCoc(boolean daNhanCoc) {
        this.daNhanCoc = daNhanCoc;
    }

    public LocalDateTime getNhanCocLuc() {
        return nhanCocLuc;
    }

    public void setNhanCocLuc(LocalDateTime nhanCocLuc) {
        this.nhanCocLuc = nhanCocLuc;
    }

    public String getLyDoDoiCoc() {
        return lyDoDoiCoc;
    }

    public void setLyDoDoiCoc(String lyDoDoiCoc) {
        this.lyDoDoiCoc = lyDoDoiCoc;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public TrangThaiHopDong getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHopDong trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HopDongLuuTru that = (HopDongLuuTru) o;
        return Objects.equals(maHopDong, that.maHopDong);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHopDong);
    }

    @Override
    public String toString() {
        return maHopDong != null ? maHopDong : "";
    }
}
