package com.petstation.integration;

import static com.petstation.database.ThaoTacSQL.*;
import static org.assertj.core.api.Assertions.*;

import com.petstation.database.*;
import com.petstation.model.*;
import com.petstation.service.*;
import java.util.EnumSet;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "PETSTATION_EXCHANGE_HOLD_SQL_TESTS", matches = "true")
class DonDoiTreoSQLTest {
    private static class HoanTac extends RuntimeException {}
    private long dong, hoaDon;
    private String maHang, soHoaDon;

    @BeforeAll
    static void cauHinh() {
        var cfg = CauHinhCSDL.nap();
        assertThat(cfg.url()).contains("IntegrationTest");
        ThaoTacSQL.cauHinh(new KetNoiCSDL(cfg));
    }

    private void tinhHuong(Runnable viec) {
        try {
            thucHienGiaoDich(c -> {
                String ma = UUID.randomUUID().toString().substring(0, 8);
                String maNhanVien = "NV" + ma;
                capNhat("INSERT INTO nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,chuc_vu,ngay_vao_lam)"
                    + " VALUES(?,N'Test hold',?,N'Thu ngân',GETDATE())", maNhanVien, ma);
                long nv = com.petstation.util.DuLieu.layIdCauNoi(null, maNhanVien);
                capNhat("INSERT INTO tai_khoan(ma_nhan_vien,ten_dang_nhap,mat_khau_hash,vai_tro)"
                    + " VALUES(?,?,?,'NHAN_VIEN_BAN_HANG')", maNhanVien, ma, "test-hash");
                long tk = com.petstation.util.DuLieu.layIdCauNoi(null, ma);
                TaiKhoan taiKhoan = new TaiKhoan(); taiKhoan.setId(tk); taiKhoan.setTenDangNhap(ma);
                NhanVien nhanVien = new NhanVien(); nhanVien.setId(nv); nhanVien.setMaNhanVien(maNhanVien);
                PhienDangNhapService.datPhienDangNhap(new PhienDangNhap(taiKhoan,
                    nhanVien, EnumSet.allOf(Quyen.class)));
                maHang = "SP" + ma;
                capNhat("INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban)"
                    + " VALUES(?,?,N'Phụ kiện',N'Cái',120000)", maHang, "Test " + ma);
                long sp = com.petstation.util.DuLieu.layIdCauNoi(null, maHang);
                soHoaDon = "HD" + ma;
                capNhat("INSERT INTO hoa_don(ma_hoa_don,ten_dang_nhap,tam_tinh,tong_tien,trang_thai,thanh_toan_luc)"
                    + " VALUES(?,?,200000,200000,'DA_THANH_TOAN',SYSDATETIME())", soHoaDon, ma);
                hoaDon = com.petstation.util.DuLieu.layIdCauNoi(null, soHoaDon);
                capNhat("INSERT INTO chi_tiet_hoa_don(ma_hoa_don,ma_san_pham,so_luong,don_gia)"
                    + " VALUES(?,?,2,100000)", soHoaDon, maHang);
                dong = sp;
                viec.run();
                throw new HoanTac();
            });
        } catch (HoanTac duKien) {
        } finally {
            PhienDangNhapService.datPhienDangNhap(null);
        }
    }

    @Test
    void treoNhapChuaChonHangGiuTiengVietVaCapNhatCungDon() {
        tinhHuong(() -> {
            var nhap = new DonDoiTreo(soHoaDon, 0, "", "vòng cổ", 1,
                "Khách muốn đổi \"màu đỏ\"\nđang đi lựa", true, false);
            long id = DonDoiTreoService.treo(0, nhap);
            assertThat(DonDoiTreoService.lay(id)).isEqualTo(nhap);
            var sua = new DonDoiTreo(soHoaDon, dong, maHang, "", 2, "Đổi cỡ", true, true);
            assertThat(DonDoiTreoService.treo(id, sua)).isEqualTo(id);
            assertThat(DonDoiTreoService.lay(id)).isEqualTo(sua);
            assertThat(laySo("SELECT COUNT(*) FROM phieu_doi_tra")).isZero();
            assertThat(NghiepVuService.layCacDong(LoaiNghiepVu.GIO_HANG_TREO))
                .anySatisfy(d -> { assertThat(d[0]).isEqualTo(id); assertThat(d[7]).isEqualTo("Đổi phụ kiện"); });
        });
    }

    @Test
    void treoTaiThanhToanGiaiPhongGioVaMoLaiVoiGiaMoi() {
        tinhHuong(() -> {
            GioHang gio = GioHangService.tao();
            DoiTraService.lapDoiHang(gio, dong, 1, maHang, 1, "Đổi cỡ");
            gio.xacNhanDaNhanTien(PhuongThuc.CHUYEN_KHOAN, true);
            long id = BanHangService.treo(gio);
            assertThat(gio.layKieuNguon()).isEqualTo("DOI_HANG");
            gio.xoa();
            var nhap = DonDoiTreoService.lay(id);
            assertThat(nhap.dongGoc()).isEqualTo(dong);
            assertThat(nhap.maThayThe()).isEqualTo(maHang);
            assertThatThrownBy(() -> BanHangService.moLai(id, gio)).hasMessageContaining("đơn đổi");
            assertThat(DonDoiTreoService.lay(id)).isNotNull();
            capNhat("UPDATE san_pham SET gia_ban=140000 WHERE ma_san_pham=?", maHang);
            DonDoiTreoService.chuyenThanhToan(id, gio, nhap.dongGoc(), nhap.soLuong(), nhap.maThayThe(), nhap.lyDo());
            assertThat(gio.layKieuNguon()).isEqualTo("DOI_HANG");
            assertThat(gio.tinhTong()).isEqualTo(40000);
            assertThat(gio.kiemTraDaNhanTien(PhuongThuc.CHUYEN_KHOAN)).isFalse();
            assertThat(DonDoiTreoService.lay(id)).isNull();
            assertThat(laySo("SELECT COUNT(*) FROM phieu_doi_tra")).isZero();
            assertThatThrownBy(() -> DonDoiTreoService.chuyenThanhToan(id, GioHangService.tao(),
                dong, 1, maHang, "Đổi")).hasMessageContaining("đã được mở hoặc xóa");
        });
    }

    @Test
    void hetHanHoacGioDangCoKhachKhacKhongLamMatDonTreo() {
        tinhHuong(() -> {
            long id = DonDoiTreoService.treo(0,
                new DonDoiTreo(soHoaDon, dong, maHang, "", 1, "Đổi", true, true));
            GioHang gio = GioHangService.tao();
            gio.datCacDong(java.util.List.of(new GioHang.DongHang(maHang, "Khách khác", 120000, 1)));
            assertThatThrownBy(() -> DonDoiTreoService.chuyenThanhToan(id, gio, dong, 1, maHang, "Đổi"))
                .hasMessageContaining("giỏ hiện tại");
            assertThat(gio.layCacDong()).hasSize(1);
            assertThat(DonDoiTreoService.lay(id)).isNotNull();
            gio.xoa();
            capNhat("UPDATE hoa_don SET thanh_toan_luc=DATEADD(hour,-73,SYSDATETIME()) WHERE (ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", hoaDon, hoaDon);
            assertThatThrownBy(() -> DonDoiTreoService.chuyenThanhToan(id, gio, dong, 1, maHang, "Đổi"))
                .hasMessageContaining("72 giờ");
            assertThat(DonDoiTreoService.lay(id)).isNotNull();
            assertThat(gio.kiemTraTrong()).isTrue();
        });
    }

    @Test
    void donBanHangVanTreoVaMoLaiBinhThuong() {
        tinhHuong(() -> {
            GioHang gio = GioHangService.tao();
            gio.datCacDong(java.util.List.of(new GioHang.DongHang(maHang, "Phụ kiện", 120000, 1)));
            long id = BanHangService.treo(gio);
            capNhat("UPDATE hoa_don SET ghi_chu=N'Khách đang đi lựa' WHERE (ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", id, id);
            assertThat(DonDoiTreoService.lay(id)).isNull();
            gio.xoa();
            BanHangService.moLai(id, gio);
            assertThat(gio.layKieuNguon()).isEmpty();
            assertThat(gio.layCacDong()).hasSize(1);
            assertThat(gio.layCacDong().get(0).ma()).isEqualTo(maHang);
        });
    }

    @Test
    void loiLuuKhongCatNoiDungVaKhongTaoThemDon() {
        tinhHuong(() -> {
            var nhap = new DonDoiTreo(soHoaDon, dong, "", "", 1, "Đổi", false, false);
            long id = DonDoiTreoService.treo(0, nhap);
            long dem = laySo("SELECT COUNT(*) FROM hoa_don WHERE trang_thai='GIO_TREO'");
            var dai = new DonDoiTreo(soHoaDon, dong, "", "", 1, "á".repeat(1100), false, false);
            assertThatThrownBy(() -> DonDoiTreoService.treo(id, dai)).hasMessageContaining("quá dài");
            assertThat(DonDoiTreoService.lay(id)).isEqualTo(nhap);
            assertThatThrownBy(() -> DonDoiTreoService.treo(0, dai)).hasMessageContaining("quá dài");
            assertThat(laySo("SELECT COUNT(*) FROM hoa_don WHERE trang_thai='GIO_TREO'")).isEqualTo(dem);
            BanHangService.xoaTreo(id);
            assertThat(DonDoiTreoService.lay(id)).isNull();
        });
    }
}
