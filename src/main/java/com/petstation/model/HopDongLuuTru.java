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

    public long laySoDemLuuTru() {
        return tinhSoDemLuuTru();
    }

    public long getTienPhongDuKien() {
        return tinhTienPhongDuKien();
    }

    public long layTienPhongDuKien() {
        return tinhTienPhongDuKien();
    }

    public long getCocToiThieu50PhanTram() {
        return tinhCocToiThieu50PhanTram();
    }

    public long layCocToiThieu50PhanTram() {
        return tinhCocToiThieu50PhanTram();
    }

    public long getTienConPhaiThu() {
        return tinhTienConPhaiThu();
    }

    public long layTienConPhaiThu() {
        return tinhTienConPhaiThu();
    }

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maHopDong);
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, this.maHopDong);
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maHopDong == null || this.maHopDong.isBlank()) && id > 0) {
            this.maHopDong = DuLieu.taoMaTuId("HDHT", 6, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    public String getMaHopDong() {
        return this.maHopDong;
    }

    public String layMaHopDong() {
        return this.maHopDong;
    }

    public String maHopDong() {
        return this.maHopDong;
    }

    public void setMaHopDong(String maHopDong) {
        this.maHopDong = maHopDong == null ? null : maHopDong.trim();
    }

    public void datMaHopDong(String maHopDong) {
        setMaHopDong(maHopDong);
    }

    public KhachHang getKhachHang() {
        return this.khachHang;
    }

    public KhachHang layKhachHang() {
        return this.khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public void datKhachHang(KhachHang khachHang) {
        setKhachHang(khachHang);
    }

    public long getKhachHangId() {
        return (khachHang == null || khachHang.getId() == null) ? 0L : khachHang.getId();
    }

    public long layKhachHangId() {
        return getKhachHangId();
    }

    public long khachHangId() {
        return getKhachHangId();
    }

    public void setKhachHangId(long khachHangId) {
        this.khachHang = new KhachHang(khachHangId);
    }

    public void datKhachHangId(long khachHangId) {
        setKhachHangId(khachHangId);
    }

    public ThuCung getThuCung() {
        return this.thuCung;
    }

    public ThuCung layThuCung() {
        return this.thuCung;
    }

    public void setThuCung(ThuCung thuCung) {
        this.thuCung = thuCung;
    }

    public void datThuCung(ThuCung thuCung) {
        setThuCung(thuCung);
    }

    public long getThuCungId() {
        return thuCung == null ? 0L : thuCung.getId();
    }

    public long layThuCungId() {
        return getThuCungId();
    }

    public long thuCungId() {
        return getThuCungId();
    }

    public void setThuCungId(long thuCungId) {
        this.thuCung = new ThuCung(thuCungId);
    }

    public void datThuCungId(long thuCungId) {
        setThuCungId(thuCungId);
    }

    public PhongHotel getPhong() {
        return this.phong;
    }

    public PhongHotel layPhong() {
        return this.phong;
    }

    public void setPhong(PhongHotel phong) {
        this.phong = phong;
    }

    public void datPhong(PhongHotel phong) {
        setPhong(phong);
    }

    public long getPhongId() {
        return phong == null ? 0L : phong.getId();
    }

    public long layPhongId() {
        return getPhongId();
    }

    public long phongId() {
        return getPhongId();
    }

    public void setPhongId(long phongId) {
        this.phong = new PhongHotel(phongId);
    }

    public void datPhongId(long phongId) {
        setPhongId(phongId);
    }

    public String getHinhThucGui() {
        return this.hinhThucGui;
    }

    public String layHinhThucGui() {
        return this.hinhThucGui;
    }

    public String hinhThucGui() {
        return this.hinhThucGui;
    }

    public void setHinhThucGui(String hinhThucGui) {
        this.hinhThucGui = hinhThucGui;
    }

    public void datHinhThucGui(String hinhThucGui) {
        setHinhThucGui(hinhThucGui);
    }

    public LocalDateTime getNgayVao() {
        return this.ngayVao;
    }

    public LocalDateTime layNgayVao() {
        return this.ngayVao;
    }

    public LocalDateTime ngayVao() {
        return this.ngayVao;
    }

    public void setNgayVao(LocalDateTime ngayVao) {
        this.ngayVao = ngayVao;
    }

    public void datNgayVao(LocalDateTime ngayVao) {
        setNgayVao(ngayVao);
    }

    public LocalDateTime getNgayRaDuKien() {
        return this.ngayRaDuKien;
    }

    public LocalDateTime layNgayRaDuKien() {
        return this.ngayRaDuKien;
    }

    public LocalDateTime ngayRaDuKien() {
        return this.ngayRaDuKien;
    }

    public void setNgayRaDuKien(LocalDateTime ngayRaDuKien) {
        this.ngayRaDuKien = ngayRaDuKien;
    }

    public void datNgayRaDuKien(LocalDateTime ngayRaDuKien) {
        setNgayRaDuKien(ngayRaDuKien);
    }

    public LocalDateTime getNgayRaThucTe() {
        return this.ngayRaThucTe;
    }

    public LocalDateTime layNgayRaThucTe() {
        return this.ngayRaThucTe;
    }

    public LocalDateTime ngayRaThucTe() {
        return this.ngayRaThucTe;
    }

    public void setNgayRaThucTe(LocalDateTime ngayRaThucTe) {
        this.ngayRaThucTe = ngayRaThucTe;
    }

    public void datNgayRaThucTe(LocalDateTime ngayRaThucTe) {
        setNgayRaThucTe(ngayRaThucTe);
    }

    public long getTienCoc() {
        return this.tienCoc;
    }

    public long layTienCoc() {
        return this.tienCoc;
    }

    public long tienCoc() {
        return this.tienCoc;
    }

    public void setTienCoc(long tienCoc) {
        if (tienCoc < 0)
            throw new IllegalArgumentException("Tiền đặt cọc không được âm.");
        this.tienCoc = tienCoc;
    }

    public void datTienCoc(long tienCoc) {
        setTienCoc(tienCoc);
    }

    public String getPhuongThucCoc() {
        return this.phuongThucCoc;
    }

    public String layPhuongThucCoc() {
        return this.phuongThucCoc;
    }

    public String phuongThucCoc() {
        return this.phuongThucCoc;
    }

    public void setPhuongThucCoc(String phuongThucCoc) {
        this.phuongThucCoc = phuongThucCoc;
    }

    public void datPhuongThucCoc(String phuongThucCoc) {
        setPhuongThucCoc(phuongThucCoc);
    }

    public String getKhoaThuCoc() {
        return this.khoaThuCoc;
    }

    public String layKhoaThuCoc() {
        return this.khoaThuCoc;
    }

    public String khoaThuCoc() {
        return this.khoaThuCoc;
    }

    public void setKhoaThuCoc(String khoaThuCoc) {
        this.khoaThuCoc = khoaThuCoc;
    }

    public void datKhoaThuCoc(String khoaThuCoc) {
        setKhoaThuCoc(khoaThuCoc);
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

    public long getGiaDem() {
        return this.giaDem;
    }

    public long layGiaDem() {
        return this.giaDem;
    }

    public long giaDem() {
        return this.giaDem;
    }

    public void setGiaDem(long giaDem) {
        if (giaDem < 0)
            throw new IllegalArgumentException("Giá phòng theo đêm không được âm.");
        this.giaDem = giaDem;
    }

    public void datGiaDem(long giaDem) {
        setGiaDem(giaDem);
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public String layGhiChu() {
        return this.ghiChu;
    }

    public String ghiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public void datGhiChu(String ghiChu) {
        setGhiChu(ghiChu);
    }

    public HoaDon getHoaDon() {
        return this.hoaDon;
    }

    public HoaDon layHoaDon() {
        return this.hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public void datHoaDon(HoaDon hoaDon) {
        setHoaDon(hoaDon);
    }

    public Long getHoaDonId() {
        return hoaDon == null ? null : hoaDon.getId();
    }

    public Long layHoaDonId() {
        return getHoaDonId();
    }

    public Long hoaDonId() {
        return getHoaDonId();
    }

    public void setHoaDonId(Long hoaDonId) {
        this.hoaDon = hoaDonId == null ? null : new HoaDon(hoaDonId);
    }

    public void datHoaDonId(Long hoaDonId) {
        setHoaDonId(hoaDonId);
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

    public boolean layDaNhanCoc() {
        return this.daNhanCoc;
    }

    public boolean daNhanCoc() {
        return this.daNhanCoc;
    }

    public void setDaNhanCoc(boolean daNhanCoc) {
        this.daNhanCoc = daNhanCoc;
    }

    public void datDaNhanCoc(boolean daNhanCoc) {
        setDaNhanCoc(daNhanCoc);
    }

    public LocalDateTime getNhanCocLuc() {
        return this.nhanCocLuc;
    }

    public LocalDateTime layNhanCocLuc() {
        return this.nhanCocLuc;
    }

    public LocalDateTime nhanCocLuc() {
        return this.nhanCocLuc;
    }

    public void setNhanCocLuc(LocalDateTime nhanCocLuc) {
        this.nhanCocLuc = nhanCocLuc;
    }

    public void datNhanCocLuc(LocalDateTime nhanCocLuc) {
        setNhanCocLuc(nhanCocLuc);
    }

    public TaiKhoan getNguoiNhanCoc() {
        return this.nguoiNhanCoc;
    }

    public TaiKhoan layNguoiNhanCoc() {
        return this.nguoiNhanCoc;
    }

    public void setNguoiNhanCoc(TaiKhoan nguoiNhanCoc) {
        this.nguoiNhanCoc = nguoiNhanCoc;
    }

    public void datNguoiNhanCoc(TaiKhoan nguoiNhanCoc) {
        setNguoiNhanCoc(nguoiNhanCoc);
    }

    public Long getNhanCocBoi() {
        return nguoiNhanCoc == null ? null : nguoiNhanCoc.getId();
    }

    public Long layNhanCocBoi() {
        return getNhanCocBoi();
    }

    public Long nhanCocBoi() {
        return getNhanCocBoi();
    }

    public void setNhanCocBoi(Long nhanCocBoi) {
        this.nguoiNhanCoc = nhanCocBoi == null ? null : new TaiKhoan(nhanCocBoi);
    }

    public void datNhanCocBoi(Long nhanCocBoi) {
        setNhanCocBoi(nhanCocBoi);
    }

    public PhienLamViec getPhienNhanCoc() {
        return this.phienNhanCoc;
    }

    public PhienLamViec layPhienNhanCoc() {
        return this.phienNhanCoc;
    }

    public void setPhienNhanCoc(PhienLamViec phienNhanCoc) {
        this.phienNhanCoc = phienNhanCoc;
    }

    public void datPhienNhanCoc(PhienLamViec phienNhanCoc) {
        setPhienNhanCoc(phienNhanCoc);
    }

    public Long getPhienNhanCocId() {
        return phienNhanCoc == null ? null : phienNhanCoc.getId();
    }

    public Long layPhienNhanCocId() {
        return getPhienNhanCocId();
    }

    public Long phienNhanCocId() {
        return getPhienNhanCocId();
    }

    public void setPhienNhanCocId(Long phienNhanCocId) {
        this.phienNhanCoc = phienNhanCocId == null ? null : new PhienLamViec(phienNhanCocId);
    }

    public void datPhienNhanCocId(Long phienNhanCocId) {
        setPhienNhanCocId(phienNhanCocId);
    }

    public long getCocYeuCau() {
        return this.cocYeuCau;
    }

    public long layCocYeuCau() {
        return this.cocYeuCau;
    }

    public long cocYeuCau() {
        return this.cocYeuCau;
    }

    public void setCocYeuCau(long cocYeuCau) {
        if (cocYeuCau < 0)
            throw new IllegalArgumentException("Tiền cọc yêu cầu không được âm.");
        this.cocYeuCau = cocYeuCau;
    }

    public void datCocYeuCau(long cocYeuCau) {
        setCocYeuCau(cocYeuCau);
    }

    public String getLyDoDoiCoc() {
        return this.lyDoDoiCoc;
    }

    public String layLyDoDoiCoc() {
        return this.lyDoDoiCoc;
    }

    public String lyDoDoiCoc() {
        return this.lyDoDoiCoc;
    }

    public void setLyDoDoiCoc(String lyDoDoiCoc) {
        this.lyDoDoiCoc = lyDoDoiCoc;
    }

    public void datLyDoDoiCoc(String lyDoDoiCoc) {
        setLyDoDoiCoc(lyDoDoiCoc);
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
