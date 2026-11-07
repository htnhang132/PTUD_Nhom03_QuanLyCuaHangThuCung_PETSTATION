package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import com.petstation.util.DuLieu;

public class HopDongLuuTru implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maHopDong;

    private KhachHang khachHang;

    private ThuCung thuCung;

    private PhongHotel phong;

    private HoaDon hoaDon;

    private TaiKhoan nguoiNhanCoc;

    private PhienLamViec phienNhanCoc;

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
    private boolean daNhanCoc;
    private LocalDateTime nhanCocLuc;
    private long cocYeuCau;
    private String lyDoDoiCoc;

    public HopDongLuuTru() {
        this.trangThai = "DA_DAT";
    }

    public HopDongLuuTru(String maHopDong) {
        this();
        setMaHopDong(maHopDong);
    }

    public HopDongLuuTru(long id) {
        this();
        setId(id);
    }

    public HopDongLuuTru(
            String maHopDong,
            KhachHang khachHang,
            ThuCung thuCung,
            PhongHotel phong,
            String hinhThucGui,
            LocalDateTime ngayVao,
            LocalDateTime ngayRaDuKien,
            LocalDateTime ngayRaThucTe,
            long tienCoc,
            String phuongThucCoc,
            String khoaThuCoc,
            String trangThai,
            long giaDem,
            String ghiChu,
            HoaDon hoaDon,
            boolean daNhanCoc,
            LocalDateTime nhanCocLuc,
            TaiKhoan nguoiNhanCoc,
            PhienLamViec phienNhanCoc,
            long cocYeuCau,
            String lyDoDoiCoc) {
        setMaHopDong(maHopDong);
        setKhachHang(khachHang);
        setThuCung(thuCung);
        setPhong(phong);
        setHinhThucGui(hinhThucGui);
        setNgayVao(ngayVao);
        setNgayRaDuKien(ngayRaDuKien);
        setNgayRaThucTe(ngayRaThucTe);
        setTienCoc(tienCoc);
        setPhuongThucCoc(phuongThucCoc);
        setKhoaThuCoc(khoaThuCoc);
        setTrangThai(trangThai);
        setGiaDem(giaDem);
        setGhiChu(ghiChu);
        setHoaDon(hoaDon);
        setDaNhanCoc(daNhanCoc);
        setNhanCocLuc(nhanCocLuc);
        setNguoiNhanCoc(nguoiNhanCoc);
        setPhienNhanCoc(phienNhanCoc);
        setCocYeuCau(cocYeuCau);
        setLyDoDoiCoc(lyDoDoiCoc);
    }

    public HopDongLuuTru(
            long id,
            String maHopDong,
            KhachHang khachHang,
            ThuCung thuCung,
            PhongHotel phong,
            String hinhThucGui,
            LocalDateTime ngayVao,
            LocalDateTime ngayRaDuKien,
            LocalDateTime ngayRaThucTe,
            long tienCoc,
            String phuongThucCoc,
            String khoaThuCoc,
            String trangThai,
            long giaDem,
            String ghiChu,
            HoaDon hoaDon,
            boolean daNhanCoc,
            LocalDateTime nhanCocLuc,
            TaiKhoan nguoiNhanCoc,
            PhienLamViec phienNhanCoc,
            long cocYeuCau,
            String lyDoDoiCoc) {
        this(maHopDong, khachHang, thuCung, phong, hinhThucGui, ngayVao, ngayRaDuKien, ngayRaThucTe, tienCoc, phuongThucCoc, khoaThuCoc, trangThai, giaDem, ghiChu, hoaDon, daNhanCoc, nhanCocLuc, nguoiNhanCoc, phienNhanCoc, cocYeuCau, lyDoDoiCoc);
        setId(id);
    }

    public HopDongLuuTru(
            long id,
            String maHopDong,
            long khachHangId,
            long thuCungId,
            long phongId,
            String hinhThucGui,
            LocalDateTime ngayVao,
            LocalDateTime ngayRaDuKien,
            LocalDateTime ngayRaThucTe,
            long tienCoc,
            String phuongThucCoc,
            String khoaThuCoc,
            String trangThai,
            long giaDem,
            String ghiChu,
            Long hoaDonId,
            boolean daNhanCoc,
            LocalDateTime nhanCocLuc,
            Long nhanCocBoi,
            Long phienNhanCocId,
            long cocYeuCau,
            String lyDoDoiCoc) {
        this(
                id,
                maHopDong,
                new KhachHang(khachHangId),
                new ThuCung(thuCungId),
                new PhongHotel(phongId),
                hinhThucGui,
                ngayVao,
                ngayRaDuKien,
                ngayRaThucTe,
                tienCoc,
                phuongThucCoc,
                khoaThuCoc,
                trangThai,
                giaDem,
                ghiChu,
                hoaDonId == null ? null : new HoaDon(hoaDonId),
                daNhanCoc,
                nhanCocLuc,
                nhanCocBoi == null ? null : new TaiKhoan(nhanCocBoi),
                phienNhanCocId == null ? null : new PhienLamViec(phienNhanCocId),
                cocYeuCau,
                lyDoDoiCoc);
    }

    public long tinhSoDemLuuTru() {
        if (ngayVao == null) {
            return 1L;
        }
        LocalDateTime mocRa = ngayRaThucTe != null ? ngayRaThucTe : ngayRaDuKien;
        if (mocRa == null || !mocRa.isAfter(ngayVao)) {
            return 1L;
        }
        long soNgay = ChronoUnit.DAYS.between(ngayVao.toLocalDate(), mocRa.toLocalDate());
        return Math.max(1L, soNgay);
    }

    public long tinhTienPhongDuKien() {
        return tinhSoDemLuuTru() * Math.max(0L, giaDem);
    }

    public long tinhCocToiThieu50PhanTram() {
        return Math.round(tinhTienPhongDuKien() * 0.5);
    }

    public long tinhTienConPhaiThu() {
        long cocDaThu = daNhanCoc ? Math.max(0L, tienCoc) : 0L;
        return Math.max(0L, tinhTienPhongDuKien() - cocDaThu);
    }

    public long getSoDemLuuTru() {
        return tinhSoDemLuuTru();
    }

    public long getTienPhongDuKien() {
        return tinhTienPhongDuKien();
    }

    public long getCocToiThieu50PhanTram() {
        return tinhCocToiThieu50PhanTram();
    }

    public long getTienConPhaiThu() {
        return tinhTienConPhaiThu();
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maHopDong);
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maHopDong == null || this.maHopDong.isBlank()) && id > 0) {
            this.maHopDong = DuLieu.taoMaTuId("HDHT", 6, id);
        }
    }

    public String getMaHopDong() {
        return this.maHopDong;
    }

    public void setMaHopDong(String maHopDong) {
        this.maHopDong = maHopDong == null ? null : maHopDong.trim();
    }

    public KhachHang getKhachHang() {
        return this.khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public long getKhachHangId() {
        return (khachHang == null || khachHang.getId() == null) ? 0L : khachHang.getId();
    }

    public void setKhachHangId(long khachHangId) {
        this.khachHang = new KhachHang(khachHangId);
    }

    public ThuCung getThuCung() {
        return this.thuCung;
    }

    public void setThuCung(ThuCung thuCung) {
        this.thuCung = thuCung;
    }

    public long getThuCungId() {
        return thuCung == null ? 0L : thuCung.getId();
    }

    public void setThuCungId(long thuCungId) {
        this.thuCung = new ThuCung(thuCungId);
    }

    public PhongHotel getPhong() {
        return this.phong;
    }

    public void setPhong(PhongHotel phong) {
        this.phong = phong;
    }

    public long getPhongId() {
        return phong == null ? 0L : phong.getId();
    }

    public void setPhongId(long phongId) {
        this.phong = new PhongHotel(phongId);
    }

    public String getHinhThucGui() {
        return this.hinhThucGui;
    }

    public void setHinhThucGui(String hinhThucGui) {
        this.hinhThucGui = hinhThucGui;
    }

    public LocalDateTime getNgayVao() {
        return this.ngayVao;
    }

    public void setNgayVao(LocalDateTime ngayVao) {
        this.ngayVao = ngayVao;
    }

    public LocalDateTime getNgayRaDuKien() {
        return this.ngayRaDuKien;
    }

    public void setNgayRaDuKien(LocalDateTime ngayRaDuKien) {
        this.ngayRaDuKien = ngayRaDuKien;
    }

    public LocalDateTime getNgayRaThucTe() {
        return this.ngayRaThucTe;
    }

    public void setNgayRaThucTe(LocalDateTime ngayRaThucTe) {
        this.ngayRaThucTe = ngayRaThucTe;
    }

    public long getTienCoc() {
        return this.tienCoc;
    }

    public void setTienCoc(long tienCoc) {
        if (tienCoc < 0)
            throw new IllegalArgumentException("Tiền đặt cọc không được âm.");
        this.tienCoc = tienCoc;
    }

    public String getPhuongThucCoc() {
        return this.phuongThucCoc;
    }

    public void setPhuongThucCoc(String phuongThucCoc) {
        this.phuongThucCoc = phuongThucCoc;
    }

    public String getKhoaThuCoc() {
        return this.khoaThuCoc;
    }

    public void setKhoaThuCoc(String khoaThuCoc) {
        this.khoaThuCoc = khoaThuCoc;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public long getGiaDem() {
        return this.giaDem;
    }

    public void setGiaDem(long giaDem) {
        if (giaDem < 0)
            throw new IllegalArgumentException("Giá phòng theo đêm không được âm.");
        this.giaDem = giaDem;
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public HoaDon getHoaDon() {
        return this.hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public Long getHoaDonId() {
        return hoaDon == null ? null : hoaDon.getId();
    }

    public void setHoaDonId(Long hoaDonId) {
        this.hoaDon = hoaDonId == null ? null : new HoaDon(hoaDonId);
    }

    public String getMaHoaDon() {
        return hoaDon == null ? null : hoaDon.getMaHoaDon();
    }

    public void setMaHoaDon(String maHoaDon) {
        this.hoaDon = (maHoaDon == null || maHoaDon.isBlank()) ? null : new HoaDon(maHoaDon);
    }

    public String getSoHoaDon() {
        return getMaHoaDon();
    }

    public void setSoHoaDon(String soHoaDon) {
        setMaHoaDon(soHoaDon);
    }

    public boolean isDaNhanCoc() {
        return this.daNhanCoc;
    }

    public void setDaNhanCoc(boolean daNhanCoc) {
        this.daNhanCoc = daNhanCoc;
    }

    public LocalDateTime getNhanCocLuc() {
        return this.nhanCocLuc;
    }

    public void setNhanCocLuc(LocalDateTime nhanCocLuc) {
        this.nhanCocLuc = nhanCocLuc;
    }

    public TaiKhoan getNguoiNhanCoc() {
        return this.nguoiNhanCoc;
    }

    public void setNguoiNhanCoc(TaiKhoan nguoiNhanCoc) {
        this.nguoiNhanCoc = nguoiNhanCoc;
    }

    public Long getNhanCocBoi() {
        return nguoiNhanCoc == null ? null : nguoiNhanCoc.getId();
    }

    public void setNhanCocBoi(Long nhanCocBoi) {
        this.nguoiNhanCoc = nhanCocBoi == null ? null : new TaiKhoan(nhanCocBoi);
    }

    public PhienLamViec getPhienNhanCoc() {
        return this.phienNhanCoc;
    }

    public void setPhienNhanCoc(PhienLamViec phienNhanCoc) {
        this.phienNhanCoc = phienNhanCoc;
    }

    public Long getPhienNhanCocId() {
        return phienNhanCoc == null ? null : phienNhanCoc.getId();
    }

    public void setPhienNhanCocId(Long phienNhanCocId) {
        this.phienNhanCoc = phienNhanCocId == null ? null : new PhienLamViec(phienNhanCocId);
    }

    public long getCocYeuCau() {
        return this.cocYeuCau;
    }

    public void setCocYeuCau(long cocYeuCau) {
        if (cocYeuCau < 0)
            throw new IllegalArgumentException("Tiền cọc yêu cầu không được âm.");
        this.cocYeuCau = cocYeuCau;
    }

    public String getLyDoDoiCoc() {
        return this.lyDoDoiCoc;
    }

    public void setLyDoDoiCoc(String lyDoDoiCoc) {
        this.lyDoDoiCoc = lyDoDoiCoc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HopDongLuuTru that = (HopDongLuuTru) o;
        return Objects.equals(this.maHopDong, that.maHopDong);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHopDong);
    }

    @Override
    public String toString() {
        return "HopDongLuuTru{" + "maHopDong='" + maHopDong + '\'' + ", khachHangId=" + getKhachHangId()
                + ", thuCungId=" + getThuCungId() + ", phongId=" + getPhongId() + ", soDem=" + tinhSoDemLuuTru()
                + ", trangThai='" + trangThai + '\'' + '}';
    }
}
