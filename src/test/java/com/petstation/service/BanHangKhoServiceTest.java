package com.petstation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.petstation.dao.banhang.HoaDonDAO;
import com.petstation.dao.banhang.PhienLamViecDAO;
import com.petstation.dao.banhang.PhieuDoiTraDAO;
import com.petstation.dao.kho.KiemKeDAO;
import com.petstation.dao.kho.NhapXuatKhoDAO;
import com.petstation.dao.kho.SanPhamDAO;
import com.petstation.dto.DonDoiTreo;
import com.petstation.dto.GioHang;
import com.petstation.dto.PhienDangNhap;
import com.petstation.model.banhang.ChiTietDoiTra;
import com.petstation.model.banhang.ChiTietHoaDon;
import com.petstation.model.banhang.HoaDon;
import com.petstation.model.banhang.PhienLamViec;
import com.petstation.model.banhang.PhieuDoiTra;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiLoHang;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.kho.ChiTietKiemKe;
import com.petstation.model.kho.ChiTietPhieuNhap;
import com.petstation.model.kho.ChiTietPhieuXuat;
import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.banhang.DoiTraService;
import com.petstation.service.banhang.DonDoiTreoService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.kho.KiemKeService;
import com.petstation.service.kho.NhapXuatKhoService;
import com.petstation.service.kho.SanPhamService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// Kiểm thử đơn vị offline cho các service banhang/kho thông qua constructor DI với fake DAO
class BanHangKhoServiceTest {
    @BeforeEach
    void thietLapPhien() {
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap("admin_test");
        tk.setVaiTro(VaiTro.QUAN_LY_CUA_HANG);
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien("NV0001");
        nv.setHoTen("Kiểm thử viên");
        PhienDangNhapService.macDinh()
                .datPhienDangNhap(new PhienDangNhap(tk, nv, EnumSet.allOf(Quyen.class)));
    }

    @AfterEach
    void donDepPhien() {
        PhienDangNhapService.macDinh().datPhienDangNhap(null);
    }

    @Test
    void sanPhamServiceDungDaoCoKieu() {
        SanPham sp = new SanPham();
        sp.setMaSanPham("SP0001");
        sp.setTenSanPham("Pate mèo");
        sp.setGiaBan(25_000L);

        SanPhamDAO fakeDao =
                new SanPhamDAO() {
                    @Override
                    public List<SanPham> layTatCa() {
                        return List.of(sp);
                    }

                    @Override
                    public SanPham timTheoMa(String maSanPham) {
                        return "SP0001".equals(maSanPham) ? sp : null;
                    }

                    @Override
                    public List<SanPham> layDanhSachSanPham() {
                        return List.of(sp);
                    }

                    @Override
                    public int capNhatBarcode(String maSanPham, String barcode) {
                        return 1;
                    }

                    @Override
                    public boolean kiemTraSanPhamCoGiaoDich(String maSanPham) {
                        return false;
                    }

                    @Override
                    public int ngungKinhDoanhSanPham(String maSanPham) {
                        return 1;
                    }
                };

        SanPhamService service = new SanPhamService(fakeDao);
        assertThat(service.layTatCa()).containsExactly(sp);
        assertThat(service.layDanhSachSanPham()).containsExactly(sp);
        assertThat(service.timTheoMa("SP0001")).isSameAs(sp);
        assertThatThrownBy(() -> service.capNhatBarcode("SP0001", "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void kiemKeServiceApDungKiemKeVaBaoLoiKhiTonThayDoi() {
        SanPham sp = new SanPham();
        sp.setMaSanPham("SP0002");
        sp.setTenSanPham("Hạt cho chó");
        LoHang lo = new LoHang();
        lo.setMaLo("LO0002");
        lo.setSoLo("SL02");
        lo.setSanPham(sp);
        lo.setHanSuDung(LocalDate.of(2027, 1, 1));
        ChiTietKiemKe ct = new ChiTietKiemKe(null, lo, 10, 8, "Hư hỏng");

        AtomicInteger ketQuaCapNhat = new AtomicInteger(1);
        AtomicReference<String> daDuyetBienBan = new AtomicReference<>();

        KiemKeDAO fakeDao =
                new KiemKeDAO() {
                    @Override
                    public List<LoHang> layDanhSachLoHangKiemKe() {
                        return List.of(lo);
                    }

                    @Override
                    public Optional<Integer> timKiemKe(String maLo) {
                        return Optional.of(10);
                    }

                    @Override
                    public String themKiemKe(
                            String maBienBan,
                            String tenDangNhap,
                            String kyKiemKe,
                            String trangThai) {
                        return maBienBan;
                    }

                    @Override
                    public int capNhatKiemKe(
                            String maBienBan,
                            String maLo,
                            int tonHeThong,
                            int tonThucTe,
                            String nguyenNhan) {
                        return 1;
                    }

                    @Override
                    public List<ChiTietKiemKe> layChiTietKiemKe(String maBienBan) {
                        return List.of(ct);
                    }

                    @Override
                    public long demApDungKiemKe(String maBienBan) {
                        return 1L;
                    }

                    @Override
                    public List<ChiTietKiemKe> timApDungKiemKe(String maBienBan) {
                        return List.of(ct);
                    }

                    @Override
                    public int capNhatApDungKiemKe(int tonThucTe, String maLo, int tonHeThong) {
                        return ketQuaCapNhat.get();
                    }

                    @Override
                    public int danhDauKiemKeDaDuyet(String maBienBan) {
                        daDuyetBienBan.set(maBienBan);
                        return 1;
                    }
                };

        KiemKeService service = new KiemKeService(fakeDao);
        List<Object[]> chiTiet = service.layChiTietKiemKe("KK0007");
        assertThat(chiTiet).hasSize(1);
        assertThat(chiTiet.get(0)[2]).isEqualTo("Hạt cho chó");
        assertThat(chiTiet.get(0)[7]).isEqualTo(-2);

        service.apDungKiemKe("KK0007");
        assertThat(daDuyetBienBan.get()).isEqualTo("KK0007");

        ketQuaCapNhat.set(0);
        assertThatThrownBy(() -> service.apDungKiemKe("KK0007"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void nhapXuatKhoServiceDocChiTietVaKiemTraDauVao() {
        SanPham sp = new SanPham();
        sp.setTenSanPham("Cát vệ sinh");
        ChiTietPhieuNhap ctNhap = new ChiTietPhieuNhap();
        ctNhap.setSanPham(sp);
        LoHang loNhap = new LoHang();
        loNhap.setMaLo("LO0009");
        loNhap.setHanSuDung(LocalDate.of(2027, 5, 1));
        ctNhap.setLoHang(loNhap);
        ctNhap.setSoLuong(12);
        ctNhap.setGiaNhap(45_000L);

        NhapXuatKhoDAO fakeDao =
                new NhapXuatKhoDAO() {
                    @Override
                    public long demNhapKho(String maSanPham) {
                        return 0;
                    }

                    @Override
                    public long demNhaCungCapDangHopTac(String maNhaCungCap) {
                        return 1;
                    }

                    @Override
                    public String themNhapKho(
                            String maPhieu,
                            String maNhaCungCap,
                            String tenDangNhap,
                            long tongTien,
                            long daTra) {
                        return maPhieu;
                    }

                    @Override
                    public int themChiTietNhapKho(
                            String maPhieuNhap,
                            String maSanPham,
                            String maLo,
                            LocalDate hanSuDung,
                            int soLuong,
                            long giaNhap) {
                        return 1;
                    }

                    @Override
                    public Optional<LoHang> timNhapKho(String maSanPham, String maHoacSoLo) {
                        return Optional.empty();
                    }

                    @Override
                    public int themLoHang(
                            String maSanPham, String soLo, LocalDate hanSuDung, int soLuong) {
                        return 1;
                    }

                    @Override
                    public int capNhatNhapKho(int soLuong, String maLo) {
                        return 1;
                    }

                    @Override
                    public int capNhatGiaNhapGanNhat(long giaNhap, String maSanPham) {
                        return 1;
                    }

                    @Override
                    public List<ChiTietPhieuNhap> layNhapKhoChiTiet(String maPhieuNhap) {
                        return List.of(ctNhap);
                    }

                    @Override
                    public long layCongNoConLai(String maPhieuNhap) {
                        return 200_000L;
                    }

                    @Override
                    public int themThanhToanNhaCungCap(String maPhieuNhap, long daTra) {
                        return 1;
                    }

                    @Override
                    public Optional<LoHang> timXuatHuy(String maLo) {
                        return Optional.empty();
                    }

                    @Override
                    public String themXuatHuy(
                            String maPhieu, String tenDangNhap, String lyDo, long tongGiaTri) {
                        return maPhieu;
                    }

                    @Override
                    public int capNhatXuatHuy(
                            String maPhieuXuat, String maLo, int soLuong, long giaTri) {
                        return 1;
                    }

                    @Override
                    public long demApDungXuatHuy(String maPhieuXuat) {
                        return 0;
                    }

                    @Override
                    public List<ChiTietPhieuXuat> timApDungXuatHuy(String maPhieuXuat) {
                        return List.of();
                    }

                    @Override
                    public int truTonXuatHuy(int soLuong, String maLo) {
                        return 0;
                    }

                    @Override
                    public int capNhatApDungXuatHuy(String maPhieuXuat) {
                        return 0;
                    }
                };

        NhapXuatKhoService service = new NhapXuatKhoService(fakeDao);
        assertThat(service.layCongNoConLai("PNK0003")).isEqualTo(200_000L);
        List<Object[]> chiTiet = service.layNhapKhoChiTiet("PNK0003");
        assertThat(chiTiet).hasSize(1);
        assertThat(chiTiet.get(0)[0]).isEqualTo("Cát vệ sinh");
        assertThat(chiTiet.get(0)[3]).isEqualTo(12);
        assertThatThrownBy(() -> service.ghiHuy("LO0001", 1, " "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.apDungXuatHuy("PXK0005"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void caThuNganServiceTinhTienMatDuKienVaNguoiNhan() {
        PhienLamViec phien = new PhienLamViec();
        phien.setMaPhien("MPI0001");
        phien.setTenCa("Ca sáng");
        phien.setMoLuc(LocalDateTime.of(2026, 11, 13, 8, 0));
        phien.setTienDauCa(1_000_000L);

        NhanVien nv = new NhanVien();
        nv.setMaNhanVien("NV0002");
        nv.setHoTen("Nguyễn Văn B");

        PhienLamViecDAO fakeDao =
                new PhienLamViecDAO() {
                    @Override
                    public long layCocHotel(String maPhien, String phuongThuc) {
                        return "TIEN_MAT".equals(phuongThuc) ? 300_000L : 0L;
                    }

                    @Override
                    public List<NhanVien> layNguoiNhan(String maNhanVienHienTai) {
                        return List.of(nv);
                    }

                    @Override
                    public int dongCoBanGiao(
                            long thucDem,
                            long chenhLech,
                            String lyDo,
                            String hinhThuc,
                            String nguoiNhanStr,
                            String maPhien) {
                        return 1;
                    }

                    @Override
                    public long layTongCocDaNhan(String maPhien) {
                        return 300_000L;
                    }

                    @Override
                    public long demDong(String maPhien) {
                        return 1L;
                    }

                    @Override
                    public Optional<PhienLamViec> timHienTai(String taiKhoanHoacTenDangNhap) {
                        return Optional.of(phien);
                    }

                    @Override
                    public long demBanHang(String maPhien, PhuongThuc mucTieu) {
                        return mucTieu == PhuongThuc.TIEN_MAT ? 500_000L : 200_000L;
                    }

                    @Override
                    public long demSoLuongHoaDon(String maPhien) {
                        return 4L;
                    }

                    @Override
                    public long demDuKienTienMat(String maPhien) {
                        return 100_000L;
                    }

                    @Override
                    public int capNhatMo(String tenDangNhap, String tenCa, long tienDauCa) {
                        return 1;
                    }
                };

        CaThuNganService service = new CaThuNganService(fakeDao);
        assertThat(service.kiemTraMo()).isTrue();
        assertThat(service.layTenCa()).isEqualTo("Ca sáng");
        assertThat(service.layTienMatDuKien()).isEqualTo(1_700_000L);
        assertThat(service.layNguoiNhan()).hasSize(1);
        assertThat(service.layNguoiNhan().get(0).maNhanVien()).isEqualTo("NV0002");
    }

    @Test
    void doiTraVaBanHangServiceSuDungDaoCoKieuVaBaoVeDonTreo() {
        SanPham pk = new SanPham();
        pk.setMaSanPham("SP0010");
        pk.setTenSanPham("Vòng cổ da");
        pk.setGiaBan(120_000L);
        pk.setNhomHang("Phụ kiện");
        LoHang loTon = new LoHang();
        loTon.setTrangThaiEnum(TrangThaiLoHang.SAN_SANG);
        loTon.setSoLuongTon(9);
        pk.setDanhSachLoHang(List.of(loTon));

        HoaDon hdTim = new HoaDon();
        hdTim.setMaHoaDon("HD0010");
        hdTim.napSnapshot(0L, 240_000L, 0L, 0);
        KhachHang kh = new KhachHang("KH0001");
        kh.setHoTen("Trần Thị C");
        hdTim.setKhachHang(kh);
        hdTim.setTaoLuc(LocalDateTime.of(2026, 11, 13, 9, 30));

        PhieuDoiTraDAO fakeDoiTraDao =
                new PhieuDoiTraDAO() {
                    @Override
                    public List<HoaDon> timHoaDon(String tuKhoa) {
                        return List.of(hdTim);
                    }

                    @Override
                    public List<ChiTietDoiTra> layDongHoaDon(String maHoaDon) {
                        ChiTietHoaDon ctHd = new ChiTietHoaDon();
                        ctHd.setSanPham(pk);
                        ctHd.setSoLuong(2);
                        ctHd.setDonGia(120_000L);
                        ChiTietDoiTra ctDt = new ChiTietDoiTra();
                        ctDt.setChiTietHoaDon(ctHd);
                        ctDt.setSoLuong(1);
                        return List.of(ctDt);
                    }

                    @Override
                    public List<SanPham> timPhuKien(String tuKhoa) {
                        return List.of(pk);
                    }

                    @Override
                    public Optional<ChiTietHoaDon> timDongGoc(String maHoaDon, String maSanPham) {
                        return Optional.empty();
                    }

                    @Override
                    public Optional<SanPham> timThayThe(String ma) {
                        return Optional.of(pk);
                    }

                    @Override
                    public String taoPhieu(
                            String maHoaDon,
                            String tenDangNhap,
                            long chenhLech,
                            String lyDo,
                            long giaTri,
                            String khoa) {
                        return "DT0001";
                    }

                    @Override
                    public void hoanTat(String maPhieu, String maHoaDonThayThe) {}

                    @Override
                    public long layChenhLechTruoc(String maHoaDon) {
                        return 0;
                    }

                    @Override
                    public boolean laHoaDonThayThe(String maHoaDon) {
                        return false;
                    }

                    @Override
                    public int capNhatApDungDoiTra(int soLuong, String maLo) {
                        return 1;
                    }

                    @Override
                    public long demDoiTraMuc(String maHoaDon, String maSanPham) {
                        return 0;
                    }

                    @Override
                    public List<LoHang> laySoLuongLoConDuocDoi(String maHoaDon, String maSanPham) {
                        return List.of();
                    }

                    @Override
                    public int capNhatDoiTraMuc(
                            String maPhieu,
                            String maHoaDon,
                            String maSanPham,
                            String maLo,
                            int soLuong,
                            long tienHoan) {
                        return 1;
                    }
                };

        DoiTraService doiTraService = new DoiTraService(fakeDoiTraDao);
        assertThat(doiTraService.timHoaDon("HD0010")).hasSize(1);
        assertThat(doiTraService.layDongHoaDon("HD0010").get(0)[0]).isEqualTo("HD0010:SP0010");
        assertThat(doiTraService.timPhuKien("Vòng")).hasSize(1);
        assertThat(doiTraService.timPhuKien("Vòng").get(0)[3]).isEqualTo(9);

        PhieuDoiTra nguon = new PhieuDoiTra();
        nguon.setLoaiPhieu("SPA");
        nguon.setMaPhieu("SPA001");
        nguon.setGiaTriDuocDoi(0L);

        HoaDon hdBienLai = new HoaDon();
        hdBienLai.setMaHoaDon("HD0010");
        hdBienLai.setThanhToanLuc(LocalDateTime.of(2026, 11, 13, 9, 45));
        hdBienLai.napSnapshot(240_000L, 240_000L, 10_000L, 24);
        hdBienLai.setHinhThucThanhToan("TIEN_MAT");
        hdBienLai.setTienKhachDua(250_000L);
        hdBienLai.setKhachHang(kh);

        SanPham spBienLai = new SanPham();
        spBienLai.setMaSanPham("SP0010");
        ChiTietHoaDon dongBienLai = new ChiTietHoaDon();
        dongBienLai.setSanPham(spBienLai);
        dongBienLai.setTenHang("Vòng cổ da");
        dongBienLai.setDonGia(120_000L);
        dongBienLai.setSoLuong(2);

        HoaDonDAO fakeHoaDonDao =
                new HoaDonDAO() {
                    @Override
                    public Optional<PhieuDoiTra> layNguonBienLai(String soHoaDon) {
                        return Optional.of(nguon);
                    }

                    @Override
                    public Optional<String> timHoaDonTheoKhoaThu(String khoaThanhToan) {
                        return Optional.empty();
                    }

                    @Override
                    public int capNhatThanhToan(int diemDung, long diemCong, String maKhachHang) {
                        return 1;
                    }

                    @Override
                    public int capNhatCapNhatHang(String hangThanhVien, String maKhachHang) {
                        return 1;
                    }

                    @Override
                    public long demMoLai(String maHoaDon) {
                        return 0;
                    }

                    @Override
                    public int capNhatXoaTreo(String maHoaDon) {
                        return 0;
                    }

                    @Override
                    public int xoaGioHangTreo(String maHoaDon, String maNhanVien) {
                        return 0;
                    }

                    @Override
                    public Optional<String> layQuyetToan(String maHopDong) {
                        return Optional.empty();
                    }

                    @Override
                    public Optional<KhachHang> timQuyetToan(String maKhachHang) {
                        return Optional.of(kh);
                    }

                    @Override
                    public long demHuyHoaDon(String maHoaDon) {
                        return 0;
                    }

                    @Override
                    public long demHopDongGanHoaDon(String maHoaDon) {
                        return 0;
                    }

                    @Override
                    public int capNhatHuyHoaDon(int soLuong, String maLo) {
                        return 0;
                    }

                    @Override
                    public int hoanDiemKhachHang(long diemMoi, String maKhachHang) {
                        return 0;
                    }

                    @Override
                    public int danhDauHuyHoaDon(String maHoaDon) {
                        return 0;
                    }

                    @Override
                    public int danhDauHuyDonHang(String maHoaDon) {
                        return 0;
                    }

                    @Override
                    public Optional<String> layMaPhienDangMo(String tenHoacMaNhanVien) {
                        return Optional.of("CA0001");
                    }

                    @Override
                    public long demNgayDaChot() {
                        return 0L;
                    }

                    @Override
                    public Optional<KhachHang> timThanhToan(String maKhachHang) {
                        return Optional.of(kh);
                    }

                    @Override
                    public int ghiNhanSuDungVoucher(
                            String maVoucher, long tamTinh, String maKhachHang, String kieuNguon) {
                        return 1;
                    }

                    @Override
                    public Optional<SanPham> khoaSanPhamDeBan(String maSanPham) {
                        return Optional.of(pk);
                    }

                    @Override
                    public long themDongDonHang(
                            String maHoaDon,
                            String maSanPham,
                            String maDichVu,
                            String tenHang,
                            int soLuong,
                            long donGia,
                            double thueGtgt) {
                        return 1;
                    }

                    @Override
                    public List<LoHang> layLoTheoFEFO(String maSanPham) {
                        return List.of();
                    }

                    @Override
                    public int truTonLo(int soLuong, String maLo) {
                        return 0;
                    }

                    @Override
                    public int ghiPhanBoLo(
                            String maHoaDon, String maSanPham, String maLo, int soLuong) {
                        return 0;
                    }

                    @Override
                    public String themHoaDon(
                            String maHoaDon,
                            String maPhien,
                            String maNhanVien,
                            long tongTien,
                            PhuongThuc phuongThuc,
                            long daNhan,
                            long tienThua,
                            long diemCong,
                            String khoaThanhToan,
                            LocalDateTime thanhToanLuc,
                            long tamTinh,
                            long giamGia,
                            int diemDung,
                            String maVoucher,
                            String maKhachHang) {
                        return maHoaDon;
                    }

                    @Override
                    public long layTienSpaChoThanhToan(String maPhieuSpa) {
                        return 0;
                    }

                    @Override
                    public int ganHoaDonSpa(String maHoaDon, String maPhieuSpa) {
                        return 0;
                    }

                    @Override
                    public int ganHoaDonHotel(String maHoaDon, String maHopDong) {
                        return 0;
                    }

                    @Override
                    public int chuyenPhongChoDon(String maHopDong) {
                        return 0;
                    }

                    @Override
                    public long demCapNhatHang(String maKhachHang) {
                        return 0;
                    }

                    @Override
                    public Optional<HoaDon> timBienLai(String maHoaDon) {
                        return Optional.of(hdBienLai);
                    }

                    @Override
                    public List<ChiTietHoaDon> layDongBienLai(String maHoaDon) {
                        return List.of(dongBienLai);
                    }

                    @Override
                    public String themTreo(
                            String maHoaDonTreo,
                            String maPhien,
                            String maNhanVien,
                            String maKhachHang) {
                        return maHoaDonTreo;
                    }

                    @Override
                    public int capNhatTreo(
                            String maHoaDon,
                            String maSanPham,
                            String tenHang,
                            long donGia,
                            int soLuong) {
                        return 0;
                    }

                    @Override
                    public long demGioTreoCuaTaiKhoan(String maHoaDon, String maNhanVien) {
                        return 0;
                    }

                    @Override
                    public List<ChiTietHoaDon> timMoLai(String maHoaDon) {
                        return List.of();
                    }

                    @Override
                    public Optional<KhachHang> layKhachCuaGioTreo(String maHoaDon) {
                        return Optional.empty();
                    }

                    @Override
                    public long demXoaTreo(String maHoaDon, String maNhanVien) {
                        return 0;
                    }

                    @Override
                    public long demQuyetToan(String maPhieuSpa) {
                        return 1;
                    }

                    @Override
                    public List<ChiTietHoaDon> layDichVuSpaQuyetToan(String maPhieuSpa) {
                        return List.of(dongBienLai);
                    }

                    @Override
                    public Optional<String> layKhachCuaPhieuSpa(String maPhieuSpa) {
                        return Optional.of("KH0001");
                    }

                    @Override
                    public Optional<HoaDon> timHuyHoaDon(String maHoaDon) {
                        return Optional.empty();
                    }

                    @Override
                    public long demPhieuDoiTraGanHoaDon(String maHoaDon) {
                        return 0;
                    }

                    @Override
                    public List<LoHang> layPhanBoCanHoanTon(String maHoaDon) {
                        return List.of();
                    }

                    @Override
                    public long khoaDiemKhachHang(String maKhachHang) {
                        return 0;
                    }

                    @Override
                    public int hoanLuotVoucher(String maVoucher) {
                        return 0;
                    }

                    @Override
                    public List<SanPham> layCoTheBanCacSanPham() {
                        return List.of(pk);
                    }

                    @Override
                    public Optional<HoaDon> layThongTinIn(String soHoaDon) {
                        return Optional.empty();
                    }

                    @Override
                    public DonDoiTreo layDonDoiTreo(String maHoaDon) {
                        return new DonDoiTreo(
                                "HD0010",
                                "HD0010:SP0010",
                                "SP0010",
                                "SP0010",
                                1,
                                "Đổi size",
                                true,
                                true);
                    }

                    @Override
                    public void luuDonDoiTreo(String maHoaDon, DonDoiTreo don) {}

                    @Override
                    public String layMaHoaDonGoc(String maDong) {
                        return "HD0010";
                    }
                };

        BanHangService banHangService = new BanHangService(fakeHoaDonDao, fakeDoiTraDao);
        assertThat(banHangService.layNguonBienLai("HD0010").ma()).isEqualTo("SPA001");
        assertThat(banHangService.layBienLai("HD0010").getThayDoi()).isEqualTo(10_000L);
        assertThat(banHangService.layCoTheBanCacSanPham()).hasSize(1);

        GioHang gio =
                new GioHang(
                        new GioHang.ChinhSach() {
                            @Override
                            public List<GioHang.MucKhuyenMai> layKhuyenMai() {
                                return List.of();
                            }

                            @Override
                            public long kiemTraVoucher(
                                    String ma, String khachHang, String nguon, long tamTinh) {
                                return 0L;
                            }

                            @Override
                            public GioHang.BienLai thanhToan(
                                    GioHang g, PhuongThuc phuongThuc, long daNhan, String thuNgan) {
                                return null;
                            }
                        });
        banHangService.quyetToan("SPA", "SPA0001", gio);
        assertThat(gio.getCacDong()).hasSize(1);
        assertThat(gio.getMaKhachHang()).isEqualTo("KH0001");

        DonDoiTreoService donDoiTreoService = new DonDoiTreoService(fakeHoaDonDao);
        assertThat(donDoiTreoService.lay("TREO001").timHoaDon()).isEqualTo("HD0010");
    }
}
