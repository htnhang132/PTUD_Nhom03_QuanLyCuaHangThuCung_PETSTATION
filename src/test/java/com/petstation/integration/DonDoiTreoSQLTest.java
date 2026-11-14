package com.petstation.integration;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.laySo;
import static com.petstation.database.ThaoTacSQL.thucHienGiaoDich;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.petstation.database.CauHinhCSDL;
import com.petstation.database.KetNoiCSDL;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.DonDoiTreo;
import com.petstation.dto.GioHang;
import com.petstation.dto.PhienDangNhap;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.DoiTraService;
import com.petstation.service.banhang.DonDoiTreoService;
import com.petstation.service.banhang.GioHangService;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.service.hethong.PhienDangNhapService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@EnabledIfEnvironmentVariable(named = "PETSTATION_EXCHANGE_HOLD_SQL_TESTS", matches = "true")
class DonDoiTreoSQLTest {
    private static class HoanTac extends RuntimeException {}

    private final BanHangService banHangService = new BanHangService();
    private final DoiTraService doiTraService = new DoiTraService();
    private final DonDoiTreoService donDoiTreoService = new DonDoiTreoService();
    private final GioHangService gioHangService = new GioHangService();
    private final NghiepVuService nghiepVuService = new NghiepVuService();

    private String dong, maHang, soHoaDon;

    @BeforeAll
    static void cauHinh() {
        var cfg = CauHinhCSDL.nap();
        assertThat(cfg.url()).contains("IntegrationTest");
        ThaoTacSQL.cauHinh(new KetNoiCSDL(cfg));
    }

    private void tinhHuong(Runnable viec) {
        try {
            thucHienGiaoDich(
                    c -> {
                        String ma = UUID.randomUUID().toString().substring(0, 8);
                        String maNhanVien = "NV" + ma;
                        capNhat(
                                "INSERT INTO"
                                    + " nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,chuc_vu,ngay_vao_lam)"
                                    + " VALUES(?,N'Test hold',?,N'Thu ngân',GETDATE())",
                                maNhanVien,
                                ma);
                        capNhat(
                                "INSERT INTO"
                                    + " tai_khoan(ma_nhan_vien,ten_dang_nhap,mat_khau_hash,vai_tro)"
                                    + " VALUES(?,?,?,'NHAN_VIEN_BAN_HANG')",
                                maNhanVien,
                                ma,
                                "test-hash");
                        TaiKhoan taiKhoan = new TaiKhoan(ma);
                        NhanVien nhanVien = new NhanVien(maNhanVien);
                        PhienDangNhapService.macDinh()
                                .datPhienDangNhap(
                                        new PhienDangNhap(
                                                taiKhoan, nhanVien, EnumSet.allOf(Quyen.class)));
                        maHang = "SP" + ma;
                        capNhat(
                                "INSERT INTO"
                                        + " san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban)"
                                        + " VALUES(?,?,N'Phụ kiện',N'Cái',120000)",
                                maHang,
                                "Test " + ma);
                        soHoaDon = "HD" + ma;
                        capNhat(
                                "INSERT INTO"
                                    + " hoa_don(ma_hoa_don,ma_nhan_vien,tam_tinh,tong_tien,trang_thai,thanh_toan_luc)"
                                    + " VALUES(?,?,200000,200000,'DA_THANH_TOAN',SYSDATETIME())",
                                soHoaDon,
                                maNhanVien);
                        capNhat(
                                "INSERT INTO"
                                    + " chi_tiet_hoa_don(ma_hoa_don,ma_san_pham,so_luong,don_gia)"
                                    + " VALUES(?,?,2,100000)",
                                soHoaDon,
                                maHang);
                        dong = soHoaDon + ":" + maHang;
                        viec.run();
                        throw new HoanTac();
                    });
        } catch (HoanTac duKien) {
        } finally {
            PhienDangNhapService.macDinh().datPhienDangNhap(null);
        }
    }

    @Test
    void treoNhapChuaChonHangGiuTiengVietVaCapNhatCungDon() {
        tinhHuong(
                () -> {
                    var nhap =
                            new DonDoiTreo(
                                    soHoaDon,
                                    "",
                                    "",
                                    "vòng cổ",
                                    1,
                                    "Khách muốn đổi \"màu đỏ\"\nđang đi lựa",
                                    true,
                                    false);
                    String id = donDoiTreoService.treo("", nhap);
                    assertThat(donDoiTreoService.lay(id)).isEqualTo(nhap);
                    var sua = new DonDoiTreo(soHoaDon, dong, maHang, "", 2, "Đổi cỡ", true, true);
                    assertThat(donDoiTreoService.treo(id, sua)).isEqualTo(id);
                    assertThat(donDoiTreoService.lay(id)).isEqualTo(sua);
                    assertThat(laySo("SELECT COUNT(*) FROM phieu_doi_tra")).isZero();
                    assertThat(nghiepVuService.layCacDong(LoaiNghiepVu.GIO_HANG_TREO))
                            .anySatisfy(
                                    d -> {
                                        assertThat(d.layChuoi(1)).isEqualTo(id);
                                        assertThat(d.layChuoi(7)).isEqualTo("Đổi phụ kiện");
                                    });
                });
    }

    @Test
    void treoTaiThanhToanGiaiPhongGioVaMoLaiVoiGiaMoi() {
        tinhHuong(
                () -> {
                    GioHang gio = gioHangService.tao();
                    doiTraService.lapDoiHang(gio, dong, 1, maHang, 1, "Đổi cỡ");
                    gio.xacNhanDaNhanTien(PhuongThuc.CHUYEN_KHOAN, true);
                    String id = banHangService.treo(gio);
                    assertThat(gio.getKieuNguon()).isEqualTo("DOI_HANG");
                    gio.xoa();
                    var nhap = donDoiTreoService.lay(id);
                    assertThat(nhap.dongGoc()).isEqualTo(dong);
                    assertThat(nhap.maThayThe()).isEqualTo(maHang);
                    assertThatThrownBy(() -> banHangService.moLai(id, gio))
                            .hasMessageContaining("đơn đổi");
                    assertThat(donDoiTreoService.lay(id)).isNotNull();
                    capNhat("UPDATE san_pham SET gia_ban=140000 WHERE ma_san_pham=?", maHang);
                    donDoiTreoService.chuyenThanhToan(
                            id, gio, nhap.dongGoc(), nhap.soLuong(), nhap.maThayThe(), nhap.lyDo());
                    assertThat(gio.getKieuNguon()).isEqualTo("DOI_HANG");
                    assertThat(gio.tinhTong()).isEqualTo(40000);
                    assertThat(gio.kiemTraDaNhanTien(PhuongThuc.CHUYEN_KHOAN)).isFalse();
                    assertThat(donDoiTreoService.lay(id)).isNull();
                    assertThat(laySo("SELECT COUNT(*) FROM phieu_doi_tra")).isZero();
                    assertThatThrownBy(
                                    () ->
                                            donDoiTreoService.chuyenThanhToan(
                                                    id,
                                                    gioHangService.tao(),
                                                    dong,
                                                    1,
                                                    maHang,
                                                    "Đổi"))
                            .hasMessageContaining("đã được mở hoặc xóa");
                });
    }

    @Test
    void hetHanHoacGioDangCoKhachKhacKhongLamMatDonTreo() {
        tinhHuong(
                () -> {
                    String id =
                            donDoiTreoService.treo(
                                    "",
                                    new DonDoiTreo(
                                            soHoaDon, dong, maHang, "", 1, "Đổi", true, true));
                    GioHang gio = gioHangService.tao();
                    gio.setCacDong(List.of(new GioHang.DongHang(maHang, "Khách khác", 120000, 1)));
                    assertThatThrownBy(
                                    () ->
                                            donDoiTreoService.chuyenThanhToan(
                                                    id, gio, dong, 1, maHang, "Đổi"))
                            .hasMessageContaining("giỏ hiện tại");
                    assertThat(gio.getCacDong()).hasSize(1);
                    assertThat(donDoiTreoService.lay(id)).isNotNull();
                    gio.xoa();
                    capNhat(
                            "UPDATE hoa_don SET thanh_toan_luc=DATEADD(hour,-73,SYSDATETIME())"
                                    + " WHERE ma_hoa_don = ?",
                            soHoaDon);
                    assertThatThrownBy(
                                    () ->
                                            donDoiTreoService.chuyenThanhToan(
                                                    id, gio, dong, 1, maHang, "Đổi"))
                            .hasMessageContaining("72 giờ");
                    assertThat(donDoiTreoService.lay(id)).isNotNull();
                    assertThat(gio.kiemTraTrong()).isTrue();
                });
    }

    @Test
    void donBanHangVanTreoVaMoLaiBinhThuong() {
        tinhHuong(
                () -> {
                    GioHang gio = gioHangService.tao();
                    gio.setCacDong(List.of(new GioHang.DongHang(maHang, "Phụ kiện", 120000, 1)));
                    String id = banHangService.treo(gio);
                    capNhat(
                            "UPDATE hoa_don SET ghi_chu=N'Khách đang đi lựa' WHERE ma_hoa_don = ?",
                            id);
                    assertThat(donDoiTreoService.lay(id)).isNull();
                    gio.xoa();
                    banHangService.moLai(id, gio);
                    assertThat(gio.getKieuNguon()).isEmpty();
                    assertThat(gio.getCacDong()).hasSize(1);
                    assertThat(gio.getCacDong().get(0).getMa()).isEqualTo(maHang);
                });
    }

    @Test
    void loiLuuKhongCatNoiDungVaKhongTaoThemDon() {
        tinhHuong(
                () -> {
                    var nhap = new DonDoiTreo(soHoaDon, dong, "", "", 1, "Đổi", false, false);
                    String id = donDoiTreoService.treo("", nhap);
                    long dem = laySo("SELECT COUNT(*) FROM hoa_don WHERE trang_thai='GIO_TREO'");
                    var dai =
                            new DonDoiTreo(
                                    soHoaDon, dong, "", "", 1, "á".repeat(1100), false, false);
                    assertThatThrownBy(() -> donDoiTreoService.treo(id, dai))
                            .hasMessageContaining("quá dài");
                    assertThat(donDoiTreoService.lay(id)).isEqualTo(nhap);
                    assertThatThrownBy(() -> donDoiTreoService.treo("", dai))
                            .hasMessageContaining("quá dài");
                    assertThat(laySo("SELECT COUNT(*) FROM hoa_don WHERE trang_thai='GIO_TREO'"))
                            .isEqualTo(dem);
                    banHangService.xoaTreo(id);
                    assertThat(donDoiTreoService.lay(id)).isNull();
                });
    }
}
