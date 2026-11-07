package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class LoHang implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maLo;

    private SanPham sanPham;
    private String soLo;
    private LocalDate ngaySanXuat;
    private LocalDate ngayNhap;
    private LocalDate hanSuDung;
    private long giaNhap;
    private int soLuongNhap;
    private int soLuongTon;
    private LocalDate ngayKiemKe;
    private String trangThai;

    public LoHang() {
        this.ngayNhap = LocalDate.now();
        this.trangThai = "SAN_SANG";
    }

    public LoHang(String maLo) {
        this();
        setMaLo(maLo);
    }

    public LoHang(long id) {
        this();
        setId(id);
    }

    public LoHang(
            String maLo,
            SanPham sanPham,
            String soLo,
            LocalDate ngaySanXuat,
            LocalDate ngayNhap,
            LocalDate hanSuDung,
            long giaNhap,
            int soLuongNhap,
            int soLuongTon,
            LocalDate ngayKiemKe,
            String trangThai) {
        setMaLo(maLo);
        setSanPham(sanPham);
        setSoLo(soLo);
        setNgaySanXuat(ngaySanXuat);
        setNgayNhap(ngayNhap);
        setHanSuDung(hanSuDung);
        setGiaNhap(giaNhap);
        setSoLuongNhap(soLuongNhap);
        setSoLuongTon(soLuongTon);
        setNgayKiemKe(ngayKiemKe);
        setTrangThai(trangThai);
    }

    public LoHang(
            long id,
            String maLo,
            SanPham sanPham,
            String soLo,
            LocalDate ngaySanXuat,
            LocalDate ngayNhap,
            LocalDate hanSuDung,
            long giaNhap,
            int soLuongNhap,
            int soLuongTon,
            LocalDate ngayKiemKe,
            String trangThai) {
        this(maLo, sanPham, soLo, ngaySanXuat, ngayNhap, hanSuDung, giaNhap, soLuongNhap, soLuongTon, ngayKiemKe, trangThai);
        setId(id);
    }

    public LoHang(
            long id,
            String maLo,
            long sanPhamId,
            String soLo,
            LocalDate ngaySanXuat,
            LocalDate ngayNhap,
            LocalDate hanSuDung,
            long giaNhap,
            int soLuongNhap,
            int soLuongTon,
            LocalDate ngayKiemKe,
            String trangThai) {
        this(
                id,
                maLo,
                new SanPham(sanPhamId),
                soLo,
                ngaySanXuat,
                ngayNhap,
                hanSuDung,
                giaNhap,
                soLuongNhap,
                soLuongTon,
                ngayKiemKe,
                trangThai);
    }

    public long tinhGiaTriTonKho() {
        return (long) Math.max(0, soLuongTon) * Math.max(0L, giaNhap);
    }

    public int tinhSoLuongDaXuat() {
        return Math.max(0, soLuongNhap - soLuongTon);
    }

    public long tinhSoNgayConHan() {
        if (hanSuDung == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), hanSuDung);
    }

    public boolean kiemTraHetHan() {
        return hanSuDung != null && hanSuDung.isBefore(LocalDate.now());
    }

    public boolean kiemTraCanDate(int soNgayCanhBao) {
        if (hanSuDung == null)
            return false;
        long conLai = tinhSoNgayConHan();
        return conLai >= 0 && conLai <= soNgayCanhBao;
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maLo);
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, this.maLo);
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maLo == null || this.maLo.isBlank()) && id > 0) {
            this.maLo = DuLieu.taoMaTuId("LO", 8, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    public String getMaLo() {
        return this.maLo;
    }

    public String layMaLo() {
        return this.maLo;
    }

    public String maLo() {
        return this.maLo;
    }

    public void setMaLo(String maLo) {
        this.maLo = maLo == null ? null : maLo.trim();
    }

    public void datMaLo(String maLo) {
        setMaLo(maLo);
    }

    public SanPham getSanPham() {
        return this.sanPham;
    }

    public SanPham laySanPham() {
        return this.sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public void datSanPham(SanPham sanPham) {
        setSanPham(sanPham);
    }

    public long getSanPhamId() {
        return (sanPham == null || sanPham.getId() == null) ? 0L : sanPham.getId();
    }

    public long laySanPhamId() {
        return getSanPhamId();
    }

    public long sanPhamId() {
        return getSanPhamId();
    }

    public void setSanPhamId(long sanPhamId) {
        this.sanPham = new SanPham(sanPhamId);
    }

    public void datSanPhamId(long sanPhamId) {
        setSanPhamId(sanPhamId);
    }

    public String getSoLo() {
        return this.soLo;
    }

    public String laySoLo() {
        return this.soLo;
    }

    public String soLo() {
        return this.soLo;
    }

    public void setSoLo(String soLo) {
        this.soLo = soLo == null ? null : soLo.trim();
    }

    public void datSoLo(String soLo) {
        setSoLo(soLo);
    }

    public LocalDate getNgaySanXuat() {
        return this.ngaySanXuat;
    }

    public LocalDate layNgaySanXuat() {
        return this.ngaySanXuat;
    }

    public LocalDate ngaySanXuat() {
        return this.ngaySanXuat;
    }

    public void setNgaySanXuat(LocalDate ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
    }

    public void datNgaySanXuat(LocalDate ngaySanXuat) {
        setNgaySanXuat(ngaySanXuat);
    }

    public LocalDate getNgayNhap() {
        return this.ngayNhap;
    }

    public LocalDate layNgayNhap() {
        return this.ngayNhap;
    }

    public LocalDate ngayNhap() {
        return this.ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public void datNgayNhap(LocalDate ngayNhap) {
        setNgayNhap(ngayNhap);
    }

    public LocalDate getHanSuDung() {
        return this.hanSuDung;
    }

    public LocalDate layHanSuDung() {
        return this.hanSuDung;
    }

    public LocalDate hanSuDung() {
        return this.hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public void datHanSuDung(LocalDate hanSuDung) {
        setHanSuDung(hanSuDung);
    }

    public long getGiaNhap() {
        return this.giaNhap;
    }

    public long layGiaNhap() {
        return this.giaNhap;
    }

    public long giaNhap() {
        return this.giaNhap;
    }

    public void setGiaNhap(long giaNhap) {
        if (giaNhap < 0)
            throw new IllegalArgumentException("Giá nhập lô hàng không được âm.");
        this.giaNhap = giaNhap;
    }

    public void datGiaNhap(long giaNhap) {
        setGiaNhap(giaNhap);
    }

    public int getSoLuongNhap() {
        return this.soLuongNhap;
    }

    public int laySoLuongNhap() {
        return this.soLuongNhap;
    }

    public int soLuongNhap() {
        return this.soLuongNhap;
    }

    public void setSoLuongNhap(int soLuongNhap) {
        if (soLuongNhap < 0)
            throw new IllegalArgumentException("Số lượng nhập không được âm.");
        this.soLuongNhap = soLuongNhap;
    }

    public void datSoLuongNhap(int soLuongNhap) {
        setSoLuongNhap(soLuongNhap);
    }

    public int getSoLuongTon() {
        return this.soLuongTon;
    }

    public int laySoLuongTon() {
        return this.soLuongTon;
    }

    public int soLuongTon() {
        return this.soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0)
            throw new IllegalArgumentException("Số lượng tồn kho không được âm.");
        this.soLuongTon = soLuongTon;
    }

    public void datSoLuongTon(int soLuongTon) {
        setSoLuongTon(soLuongTon);
    }

    public LocalDate getNgayKiemKe() {
        return this.ngayKiemKe;
    }

    public LocalDate layNgayKiemKe() {
        return this.ngayKiemKe;
    }

    public LocalDate ngayKiemKe() {
        return this.ngayKiemKe;
    }

    public void setNgayKiemKe(LocalDate ngayKiemKe) {
        this.ngayKiemKe = ngayKiemKe;
    }

    public void datNgayKiemKe(LocalDate ngayKiemKe) {
        setNgayKiemKe(ngayKiemKe);
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public String layTrangThai() {
        return this.trangThai;
    }

    public String trangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void datTrangThai(String trangThai) {
        setTrangThai(trangThai);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LoHang that = (LoHang) o;
        return Objects.equals(this.maLo, that.maLo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLo);
    }

    @Override
    public String toString() {
        return "LoHang{" + "maLo='" + maLo + '\'' + ", sanPhamId=" + getSanPhamId() + ", soLo='" + soLo
                + '\'' + ", hanSuDung=" + hanSuDung + ", soLuongTon=" + soLuongTon + ", trangThai='" + trangThai + '\''
                + '}';
    }
}
