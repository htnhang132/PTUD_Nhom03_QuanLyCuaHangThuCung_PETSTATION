package com.petstation.integration;

import static com.petstation.database.ThaoTacSQL.*;

import static org.assertj.core.api.Assertions.*;

import com.petstation.dao.banhang.impl.HoaDonDAOImpl;
import com.petstation.dao.banhang.impl.PhieuDoiTraDAOImpl;
import com.petstation.database.CauHinhCSDL;
import com.petstation.database.KetNoiCSDL;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.GioHang;
import com.petstation.dto.PhienDangNhap;
import com.petstation.model.chung.*;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.GioHangService;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.luutru.LuuTruService;
import com.petstation.service.spa.LapPhieuSpaService;
import com.petstation.service.spa.SpaService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/** Regression suite for the natural-key schema; every fixture rolls back. */
@EnabledIfEnvironmentVariable(named = "PETSTATION_SQL_TESTS", matches = "true")
class TichHopSQLServerTest {
    private static final class HoanTac extends RuntimeException {}

    private final BanHangService banHang = new BanHangService();
    private final HoaDonDAOImpl hoaDonDAO = new HoaDonDAOImpl();
    private final SpaService spa = new SpaService();
    private final LuuTruService hotel = new LuuTruService();
    private String nv, kh, sp, lo1, lo2, thu, goi1, goi2, phong, vc;

    @BeforeAll
    static void cauHinh() {
        CauHinhCSDL cfg = CauHinhCSDL.nap();
        assertThat(cfg.url())
                .containsPattern(
                        "(?i)databaseName=PetStationIntegrationTest(?:_[A-Za-z0-9]+)?(?:;|$)");
        ThaoTacSQL.cauHinh(new KetNoiCSDL(cfg));
    }

    private void tinhHuong(Runnable kiemThu) {
        try {
            thucHienGiaoDich(
                    c -> {
                        fixture();
                        kiemThu.run();
                        throw new HoanTac();
                    });
        } catch (HoanTac duKien) {
            // Fixtures are rolled back, including successful nested service transactions.
        } finally {
            PhienDangNhapService.macDinh().datPhienDangNhap(null);
        }
    }

    private void fixture() {
        String s = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        nv = "NV" + s;
        kh = "KH" + s;
        sp = "SP" + s;
        lo1 = "L1" + s;
        lo2 = "L2" + s;
        thu = "TC" + s;
        goi1 = "G1" + s;
        goi2 = "G2" + s;
        phong = "P" + s;
        vc = ("VC" + s).toUpperCase(java.util.Locale.ROOT);
        capNhat(
                "INSERT nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,chuc_vu,ngay_vao_lam)"
                        + " VALUES(?,N'Test Manager',?,N'Cửa hàng trưởng',GETDATE())",
                nv,
                s);
        capNhat(
                "INSERT"
                    + " tai_khoan(ma_nhan_vien,ten_dang_nhap,mat_khau_hash,vai_tro,bat_buoc_doi_mat_khau)"
                    + " VALUES(?,?,?,'QUAN_LY_CUA_HANG',0)",
                nv,
                nv,
                "test-hash");
        capNhat(
                "INSERT"
                    + " phien_lam_viec(ma_phien,ma_nhan_vien,ma_ca,ten_ca,mo_luc,tien_dau_ca,trang_thai)"
                    + " VALUES(?,?,'CA01',N'Ca sáng',SYSDATETIME(),0,'DANG_MO')",
                "CA" + s,
                nv);
        capNhat(
                "INSERT khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,diem_tich_luy)"
                        + " VALUES(?,N'Test Customer',?,500)",
                kh,
                "0" + s);
        capNhat(
                "INSERT san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat)"
                        + " VALUES(?,?,N'Phụ kiện',N'Cái',120000,50000)",
                sp,
                "Test " + s);
        capNhat(
                "INSERT"
                    + " lo_hang(ma_lo,ma_san_pham,so_lo,ngay_nhap,han_su_dung,so_luong_nhap,so_luong_ton)"
                    + " VALUES(?,?,?,GETDATE(),DATEADD(day,30,GETDATE()),2,2)",
                lo1,
                sp,
                lo1);
        capNhat(
                "INSERT"
                    + " lo_hang(ma_lo,ma_san_pham,so_lo,ngay_nhap,han_su_dung,so_luong_nhap,so_luong_ton)"
                    + " VALUES(?,?,?,GETDATE(),DATEADD(day,60,GETDATE()),8,8)",
                lo2,
                sp,
                lo2);
        capNhat(
                "INSERT thu_cung(ma_thu_cung,ma_khach_hang,ten,loai,ngay_sinh,can_nang)"
                        + " VALUES(?,?,N'Test pet',N'Chó',DATEADD(year,-1,GETDATE()),4)",
                thu,
                kh);
        capNhat(
                "INSERT lich_su_tiem_phong(ma_thu_cung,ten_vaccine,ngay_tiem) VALUES(?,N'Mũi"
                        + " 1',DATEADD(day,-90,GETDATE())),(?,N'Mũi 2',DATEADD(day,-30,GETDATE()))",
                thu,
                thu);
        capNhat(
                "INSERT goi_dich_vu_spa(ma_goi,ten_goi,thoi_gian_du_kien,bang_gia)"
                        + " VALUES(?,?,45,'DOG:0-100:150000'),(?,?,30,'DOG:0-100:200000')",
                goi1,
                "Bath " + s,
                goi2,
                "Groom " + s);
        capNhat(
                "INSERT phong_hotel(ma_phong,loai_phong,gia_dem,suc_chua) VALUES(?,N'Tiêu"
                        + " chuẩn',200000,1)",
                phong);
        capNhat(
                "INSERT"
                    + " voucher(ma_voucher,menh_gia,hieu_luc_den,so_luong,ma_khach_hang,chi_ap_dung_spa)"
                    + " VALUES(?,10000,DATEADD(day,30,GETDATE()),1,?,0)",
                vc,
                kh);
        datPhien(EnumSet.allOf(Quyen.class));
    }

    private void datPhien(EnumSet<Quyen> quyen) {
        TaiKhoan tk = new TaiKhoan(nv);
        tk.setVaiTro(VaiTro.QUAN_LY_CUA_HANG);
        PhienDangNhapService.macDinh()
                .datPhienDangNhap(new PhienDangNhap(tk, new NhanVien(nv), quyen));
    }

    private GioHang gio(int soLuong) {
        GioHang g = new GioHangService().tao();
        g.setCacDong(List.of(new GioHang.DongHang(sp, "Test product", 120000, soLuong)));
        g.setKhachHang(kh, "Test Customer", 500);
        return g;
    }

    private String thu(GioHang g) {
        g.xacNhanDaNhanTien(PhuongThuc.TIEN_MAT, true);
        return banHang.thanhToan(g, PhuongThuc.TIEN_MAT, g.tinhTong(), nv).getSoHoaDon();
    }

    private String phieuSpa() {
        return new LapPhieuSpaService()
                .luuNhieuDichVu(thu, List.of(goi1, goi2), null, 4, "Bình thường", "", false);
    }

    @Test
    void thanhToanSanPhamPhanBoHaiLo() {
        tinhHuong(
                () -> {
                    String hd = thu(gio(5));
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE ma_lo=?", lo1))
                            .isZero();
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE ma_lo=?", lo2))
                            .isEqualTo(5);
                    assertThat(
                                    laySo(
                                            "SELECT SUM(a.so_luong) FROM phan_bo_lo_hoa_don a JOIN"
                                                + " chi_tiet_hoa_don c ON c.ma_dong=a.ma_dong WHERE"
                                                + " c.ma_hoa_don=?",
                                            hd))
                            .isEqualTo(5);
                });
    }

    @Test
    void huyHoaDonHoanDungTungLo() {
        tinhHuong(
                () -> {
                    String hd = thu(gio(5));
                    banHang.huyHoaDon(hd);
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE ma_lo=?", lo1))
                            .isEqualTo(2);
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE ma_lo=?", lo2))
                            .isEqualTo(8);
                });
    }

    @Test
    void thuLapKhongTruTonLanHai() {
        tinhHuong(
                () -> {
                    GioHang g = gio(5);
                    String hd = thu(g);
                    assertThat(thu(g)).isEqualTo(hd);
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE ma_lo=?", lo2))
                            .isEqualTo(5);
                });
    }

    @Test
    void thieuTonRollbackHoaDonVaVoucher() {
        tinhHuong(
                () -> {
                    GioHang g = gio(11);
                    assertThat(g.apDungVoucher(vc)).isNull();
                    assertThatThrownBy(() -> thu(g)).isInstanceOf(IllegalStateException.class);
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM hoa_don WHERE idempotency_key=?",
                                            g.getKhoaThanhToan()))
                            .isZero();
                    assertThat(laySo("SELECT da_dung FROM voucher WHERE ma_voucher=?", vc))
                            .isZero();
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE ma_lo=?", lo1))
                            .isEqualTo(2);
                });
    }

    @Test
    void giaThayDoiChanThanhToan() {
        tinhHuong(
                () -> {
                    GioHang g = gio(1);
                    capNhat("UPDATE san_pham SET gia_ban=130000 WHERE ma_san_pham=?", sp);
                    assertThatThrownBy(() -> thu(g))
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessageContaining("Giá");
                });
    }

    @Test
    void loHetHanKhongDuocBan() {
        tinhHuong(
                () -> {
                    capNhat(
                            "UPDATE lo_hang SET han_su_dung=DATEADD(day,-1,GETDATE()) WHERE"
                                    + " ma_lo=?",
                            lo1);
                    thu(gio(3));
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE ma_lo=?", lo1))
                            .isEqualTo(2);
                });
    }

    @Test
    void doiHangDocDungSoLuongTungLo() {
        tinhHuong(
                () -> {
                    String hd = thu(gio(5));
                    var lo = new PhieuDoiTraDAOImpl().laySoLuongLoConDuocDoi(hd, sp);
                    assertThat(lo).extracting(v -> v.getSoLuongTon()).containsExactly(2, 3);
                });
    }

    @Test
    void huyChanDongLichSuChuaDoiChieu() {
        tinhHuong(
                () -> {
                    String hd = thu(gio(5));
                    capNhat(
                            "DELETE a FROM phan_bo_lo_hoa_don a JOIN chi_tiet_hoa_don c ON"
                                    + " c.ma_dong=a.ma_dong WHERE c.ma_hoa_don=?",
                            hd);
                    assertThatThrownBy(() -> banHang.huyHoaDon(hd))
                            .hasMessageContaining("đối chiếu");
                    assertThatThrownBy(
                                    () -> new PhieuDoiTraDAOImpl().laySoLuongLoConDuocDoi(hd, sp))
                            .hasMessageContaining("đối chiếu");
                });
    }

    @Test
    void baoCaoDungGiaVonDaChotVaKhongDoanGiaVonLichSu() {
        tinhHuong(
                () -> {
                    String hd = thu(gio(5));
                    capNhat("UPDATE san_pham SET gia_nhap_gan_nhat=90000 WHERE ma_san_pham=?", sp);
                    var dao = new com.petstation.dao.thongke.impl.ThongKeDAOImpl();
                    java.time.LocalDate homNay = java.time.LocalDate.now();
                    var baoCao = dao.layDoanhThuHoanTat(homNay, homNay.plusDays(1), nv);
                    assertThat(baoCao).hasSize(1);
                    double doanhThuThuan = ((Number) baoCao.get(0).thanhHang()[5]).doubleValue();
                    assertThat(baoCao.get(0).thanhHang()[6]).isEqualTo(doanhThuThuan - 250000.0);
                    capNhat(
                            "DELETE a FROM phan_bo_lo_hoa_don a JOIN chi_tiet_hoa_don c ON"
                                    + " c.ma_dong=a.ma_dong WHERE c.ma_hoa_don=?",
                            hd);
                    assertThat(
                                    dao.layDoanhThuHoanTat(homNay, homNay.plusDays(1), nv)
                                            .get(0)
                                            .thanhHang()[6])
                            .isNull();
                });
    }

    @Test
    void donTreoLuuBenGhiChuVaVoucher() {
        tinhHuong(
                () -> {
                    GioHang g = gio(2);
                    g.setGhiChu("Khách quay lại lúc 17 giờ");
                    assertThat(g.apDungVoucher(vc)).isNull();
                    String ma = banHang.treo(g);
                    GioHang moi = new GioHangService().tao();
                    new BanHangService().moLai(ma, moi);
                    assertThat(moi.getGhiChu()).isEqualTo(g.getGhiChu());
                    assertThat(moi.getMaVoucher()).isEqualTo(vc);
                    assertThat(moi.getCacDong()).hasSize(1);
                });
    }

    @Test
    void spaLuuTungDichVuVaGiaSnapshot() {
        tinhHuong(
                () -> {
                    String ma = phieuSpa();
                    capNhat(
                            "UPDATE goi_dich_vu_spa SET ten_goi=?,bang_gia='DOG:0-100:900000' WHERE"
                                    + " ma_goi=?",
                            "Changed " + ma,
                            goi1);
                    var rows = spa.layPhieuSpaChiTiet(ma);
                    assertThat(rows).hasSize(2);
                    assertThat(rows)
                            .extracting(v -> v.getDonGia())
                            .containsExactly(150000L, 200000L);
                    assertThat(rows.get(0).getTenDichVu()).startsWith("Bath ");
                });
    }

    @Test
    void xoaDichVuKhongDinhGiaLaiDichVuCon() {
        tinhHuong(
                () -> {
                    String ma = phieuSpa();
                    capNhat(
                            "UPDATE goi_dich_vu_spa SET bang_gia='DOG:0-100:900000' WHERE ma_goi=?",
                            goi2);
                    spa.xoaDichVuSpa(ma, 0);
                    assertThat(spa.layPhieuSpaChiTiet(ma))
                            .singleElement()
                            .satisfies(v -> assertThat(v.getDonGia()).isEqualTo(200000));
                    assertThat(
                                    laySo(
                                            "SELECT tong_tien FROM phieu_tiep_nhan_spa WHERE"
                                                    + " ma_phieu=?",
                                            ma))
                            .isEqualTo(200000);
                });
    }

    @Test
    void khongXoaDichVuCuoiCung() {
        tinhHuong(
                () -> {
                    String ma = phieuSpa();
                    spa.xoaDichVuSpa(ma, 0);
                    assertThatThrownBy(() -> spa.xoaDichVuSpa(ma, 0))
                            .hasMessageContaining("ít nhất");
                });
    }

    @Test
    void thanhToanSpaNhieuDongKhongCanSanPhamGia() {
        tinhHuong(
                () -> {
                    String ma = phieuSpa();
                    spa.layTrangThaiSpa(ma, "DANG_THUC_HIEN");
                    spa.layTrangThaiSpa(ma, "HOAN_TAT");
                    GioHang g = new GioHangService().tao();
                    banHang.quyetToan("SPA", ma, g);
                    String hd = thu(g);
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM chi_tiet_hoa_don WHERE"
                                                    + " ma_hoa_don=? AND ma_san_pham IS NULL",
                                            hd))
                            .isEqualTo(2);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT trang_thai FROM phieu_tiep_nhan_spa WHERE"
                                                    + " ma_phieu=?",
                                            ma))
                            .isEqualTo("DA_THANH_TOAN");
                });
    }

    @Test
    void khongSuaDichVuSauThanhToan() {
        tinhHuong(
                () -> {
                    String ma = phieuSpa();
                    spa.layTrangThaiSpa(ma, "DANG_THUC_HIEN");
                    spa.layTrangThaiSpa(ma, "HOAN_TAT");
                    assertThatThrownBy(() -> spa.themDichVuSpa(ma, goi1))
                            .isInstanceOf(IllegalArgumentException.class);
                });
    }

    @Test
    void hotelThuCocHaiLanVaKhongThuTrung() {
        tinhHuong(
                () -> {
                    LocalDateTime batDau = LocalDateTime.now().plusMinutes(1);
                    String ma = hotel.giuPhong(thu, phong, batDau, batDau.plusDays(2), "");
                    assertThat(new com.petstation.dao.chung.impl.QuyTrinhDAOImpl().layLichPhong())
                            .anySatisfy(
                                    lich -> {
                                        assertThat(lich.getLichPhong().getHopDong().getMaHopDong())
                                                .isEqualTo(ma);
                                        assertThat(lich.getLichPhong().getHopDong().getGiaDem())
                                                .isEqualTo(200000L);
                                    });
                    hotel.thuCoc(ma, PhuongThuc.TIEN_MAT, 100000, 100000, "COC-1");
                    hotel.thuCoc(ma, PhuongThuc.TIEN_MAT, 100000, 100000, "COC-1");
                    hotel.thuCoc(ma, PhuongThuc.CHUYEN_KHOAN, 100000, 100000, "COC-2");
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_tien) FROM lich_su_thu_coc_hotel WHERE"
                                                    + " ma_hop_dong=?",
                                            ma))
                            .isEqualTo(200000);
                    assertThat(hotel.layMucCoc(ma)).containsExactly(200000, 200000);
                });
    }

    @Test
    void hotelChanThuQuaCoc() {
        tinhHuong(
                () -> {
                    LocalDateTime batDau = LocalDateTime.now().plusMinutes(1);
                    String ma = hotel.giuPhong(thu, phong, batDau, batDau.plusDays(2), "");
                    assertThatThrownBy(
                                    () ->
                                            hotel.thuCoc(
                                                    ma,
                                                    PhuongThuc.TIEN_MAT,
                                                    200001,
                                                    200001,
                                                    "COC-X"))
                            .isInstanceOf(IllegalArgumentException.class);
                });
    }

    @Test
    void hotelQuyetToanTruCocDungMotLan() {
        tinhHuong(
                () -> {
                    LocalDateTime batDau = LocalDateTime.now().minusMinutes(1);
                    String ma = hotel.giuPhong(thu, phong, batDau, batDau.plusDays(2), "");
                    hotel.thuCoc(ma, PhuongThuc.TIEN_MAT, 200000, 200000, "COC-QT");
                    hotel.kiemTraVao(ma);
                    GioHang g = new GioHangService().tao();
                    banHang.quyetToan("HOTEL", ma, g);
                    String hd = thu(g);
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM chi_tiet_hoa_don WHERE"
                                                    + " ma_hoa_don=? AND ma_san_pham IS NULL",
                                            hd))
                            .isEqualTo(1);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT trang_thai FROM hop_dong_luu_tru WHERE"
                                                    + " ma_hop_dong=?",
                                            ma))
                            .isEqualTo("DA_TRA_PHONG");
                    assertThatThrownBy(
                                    () ->
                                            banHang.quyetToan(
                                                    "HOTEL", ma, new GioHangService().tao()))
                            .isInstanceOf(RuntimeException.class);
                });
    }

    @Test
    void khongDocKhoKhiChiCoQuyenSpa() {
        tinhHuong(
                () -> {
                    datPhien(EnumSet.of(Quyen.QUAN_LY_SPA));
                    assertThatThrownBy(
                                    () ->
                                            new NghiepVuService()
                                                    .layCacDong(LoaiNghiepVu.PHIEU_NHAP_KHO))
                            .isInstanceOf(SecurityException.class);
                });
    }

    @Test
    void thanhToanKhongQuyenBiTuChoi() {
        tinhHuong(
                () -> {
                    GioHang g = gio(1);
                    datPhien(EnumSet.of(Quyen.BAN_HANG));
                    assertThatThrownBy(() -> thu(g)).isInstanceOf(SecurityException.class);
                });
    }

    @Test
    void khongThanhToanKhiDaDongCa() {
        tinhHuong(
                () -> {
                    GioHang g = gio(1);
                    capNhat(
                            "UPDATE phien_lam_viec SET trang_thai='DA_BAN_GIAO' WHERE"
                                    + " ma_nhan_vien=?",
                            nv);
                    assertThatThrownBy(() -> thu(g))
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessageContaining("mở ca");
                });
    }

    @Test
    void duLieuMauTonKhopChungTu() {
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM lo_hang l WHERE l.so_luong_ton<>"
                                    + " COALESCE((SELECT SUM(c.so_luong) FROM chi_tiet_phieu_nhap c"
                                    + " JOIN phieu_nhap_kho p ON p.ma_phieu_nhap=c.ma_phieu_nhap"
                                    + " WHERE c.ma_lo=l.ma_lo AND"
                                    + " p.trang_thai='DA_NHAP'),0)-COALESCE((SELECT SUM(a.so_luong)"
                                    + " FROM phan_bo_lo_hoa_don a JOIN chi_tiet_hoa_don c ON"
                                    + " c.ma_dong=a.ma_dong JOIN hoa_don h ON"
                                    + " h.ma_hoa_don=c.ma_hoa_don WHERE a.ma_lo=l.ma_lo AND"
                                    + " h.trang_thai='DA_THANH_TOAN'),0)-COALESCE((SELECT"
                                    + " SUM(c.so_luong) FROM chi_tiet_phieu_xuat c JOIN"
                                    + " phieu_xuat_kho p ON p.ma_phieu_xuat=c.ma_phieu_xuat WHERE"
                                    + " c.ma_lo=l.ma_lo AND"
                                    + " p.trang_thai='DA_XUAT'),0)+COALESCE((SELECT SUM(c.so_luong)"
                                    + " FROM chi_tiet_doi_tra c JOIN phieu_doi_tra p ON"
                                    + " p.ma_phieu=c.ma_phieu WHERE c.ma_lo=l.ma_lo AND"
                                    + " p.trang_thai='HOAN_TAT'),0)+COALESCE((SELECT"
                                    + " SUM(c.chenh_lech) FROM chi_tiet_kiem_ke c JOIN"
                                    + " bien_ban_kiem_ke p ON p.ma_bien_ban=c.ma_bien_ban WHERE"
                                    + " c.ma_lo=l.ma_lo AND p.trang_thai='DA_DUYET'),0)"))
                .isZero();
    }

    @Test
    void duLieuMauTienCocKhopLichSu() {
        assertThat(
                        laySo(
                                "SELECT COUNT(*) FROM hop_dong_luu_tru h WHERE"
                                        + " h.tien_coc<>COALESCE((SELECT SUM(c.so_tien) FROM"
                                        + " lich_su_thu_coc_hotel c WHERE"
                                        + " c.ma_hop_dong=h.ma_hop_dong),0)"))
                .isZero();
    }

    @Test
    void maSanPhamKhongTonTaiBiFkChan() {
        tinhHuong(
                () -> {
                    assertThatThrownBy(
                                    () ->
                                            capNhat(
                                                    "INSERT"
                                                        + " chi_tiet_hoa_don(ma_hoa_don,ma_san_pham,so_luong,don_gia)"
                                                        + " VALUES('MISSING','MISSING',1,1)"))
                            .isInstanceOf(RuntimeException.class);
                });
    }
}
