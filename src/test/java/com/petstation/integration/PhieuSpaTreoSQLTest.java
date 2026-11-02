package com.petstation.integration;

import static com.petstation.database.ThaoTacSQL.*;
import static org.assertj.core.api.Assertions.*;
import com.petstation.database.*;
import com.petstation.model.*;
import com.petstation.service.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "PETSTATION_SPA_HOLD_SQL_TESTS", matches = "true")
class PhieuSpaTreoSQLTest {
    private static class HoanTac extends RuntimeException {}
    private String maKhach, maThu, maGoi;

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
                long nv = them("INSERT nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,chuc_vu,ngay_vao_lam)"
                    + " VALUES(?,N'Test Spa',?,N'Spa',GETDATE())", "NV" + ma, ma);
                long tk = them("INSERT tai_khoan(nhan_vien_id,ten_dang_nhap,mat_khau_hash,vai_tro)"
                    + " VALUES(?,?,?,'NHAN_VIEN_SPA_HOTEL')", nv, ma, "test-hash");
                TaiKhoan taiKhoan = new TaiKhoan(); taiKhoan.setId(tk);
                NhanVien nhanVien = new NhanVien(); nhanVien.setId(nv);
                PhienDangNhapService.datPhienDangNhap(new PhienDangNhap(taiKhoan, nhanVien, EnumSet.allOf(Quyen.class)));
                maKhach = "KH" + ma; maThu = "TC" + ma; maGoi = "GOI" + ma;
                long khach = them("INSERT khach_hang(ma_khach_hang,ho_ten,so_dien_thoai) VALUES(?,N'Test Spa',?)",
                    maKhach, "09" + ma);
                them("INSERT thu_cung(ma_thu_cung,khach_hang_id,ten,loai,ngay_sinh,so_mui_tiem,ngay_tiem_gan_nhat)"
                    + " VALUES(?,?,N'Test pet','DOG',DATEADD(month,-6,GETDATE()),2,DATEADD(day,-30,GETDATE()))", maThu, khach);
                them("INSERT goi_dich_vu_spa(ma_goi,ten_goi,thoi_gian_du_kien,bang_gia) VALUES(?,?,60,'DOG:0-100:150000')",
                    maGoi, "Test service " + ma);
                viec.run();
                throw new HoanTac();
            });
        } catch (HoanTac duKien) {
        } finally { PhienDangNhapService.datPhienDangNhap(null); }
    }

    private PhieuSpaTreo nhap() {
        return new PhieuSpaTreo(maKhach, "0901234567", maThu, maGoi, "", "5,2",
            "Chưa tắm\nDị ứng \"sữa tắm\"", "Giữ vòng cổ • gọi chủ trước khi làm");
    }

    @Test
    void treoNhapDoKhongTaoPhieuChinhThucVaCapNhatCungBanGhi() {
        tinhHuong(() -> {
            var p = new PhieuSpaTreo("", "090", "", "", "", "đang cân", "Ghi chú".repeat(200), "");
            long id = PhieuSpaTreoService.treo(0, p);
            assertThat(PhieuSpaTreoService.lay(id)).isEqualTo(p);
            assertThat(PhieuSpaTreoService.treo(id, nhap())).isEqualTo(id);
            assertThat(PhieuSpaTreoService.lay(id)).isEqualTo(nhap());
            assertThat(PhieuSpaTreoService.danhSach()).anySatisfy(d -> {
                assertThat(d[0]).isEqualTo(id); assertThat(d[2]).isEqualTo("Test Spa");
            });
            assertThat(laySo("SELECT COUNT(*) FROM phieu_tiep_nhan_spa")).isZero();
            assertThat(laySo("SELECT COUNT(*) FROM hoa_don")).isZero();
        });
    }

    @Test
    void loiLuuVaLoiTaoPhieuGiuNguyenNhap() {
        tinhHuong(() -> {
            long id = PhieuSpaTreoService.treo(0, nhap());
            var sai = new PhieuSpaTreo(maKhach, "0".repeat(101), maThu, maGoi, "", "5", "Bình thường", "");
            assertThatThrownBy(() -> PhieuSpaTreoService.treo(id, sai)).isInstanceOf(RuntimeException.class);
            assertThat(PhieuSpaTreoService.lay(id)).isEqualTo(nhap());
            assertThatThrownBy(() -> PhieuSpaTreoService.taoPhieu(id, maThu, maGoi, "", -1, "Bình thường", ""))
                .hasMessageContaining("Cân nặng");
            assertThat(PhieuSpaTreoService.lay(id)).isEqualTo(nhap());
            assertThat(laySo("SELECT COUNT(*) FROM phieu_tiep_nhan_spa")).isZero();
        });
    }

    @Test
    void taoPhieuThanhCongMoiXoaNhapVaKhongChoTaoTrung() {
        tinhHuong(() -> {
            long id = PhieuSpaTreoService.treo(0, nhap());
            long phieu = PhieuSpaTreoService.taoPhieu(id, maThu, maGoi, "", 5.2, "Bình thường", "Giữ vòng cổ");
            assertThat(laySo("SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE id=?", phieu)).isEqualTo(1);
            assertThatThrownBy(() -> PhieuSpaTreoService.lay(id)).hasMessageContaining("đã được tạo hoặc xóa");
            assertThatThrownBy(() -> PhieuSpaTreoService.taoPhieu(id, maThu, maGoi, "", 5.2, "Bình thường", ""))
                .hasMessageContaining("đã được tạo hoặc xóa");
            assertThat(laySo("SELECT COUNT(*) FROM phieu_tiep_nhan_spa")).isEqualTo(1);
        });
    }

    @Test
    void moLaiFormGiuLuaChonVaNoiDungNhungYeuCauKiemTraSucKhoeLai() throws Exception {
        javax.swing.SwingUtilities.invokeAndWait(() -> tinhHuong(() -> {
            long id = PhieuSpaTreoService.treo(0, nhap());
            var form = new com.petstation.gui.processing.spa.LapPhieuSpaPanel(p -> {}, () -> {}, () -> {});
            try {
                var sucKhoe = form.getClass().getDeclaredField("chkSucKhoe");
                sucKhoe.setAccessible(true);
                ((javax.swing.JCheckBox) sucKhoe.get(form)).setSelected(true);
                var moLai = form.getClass().getDeclaredMethod("khoiPhucPhieu", long.class);
                moLai.setAccessible(true);
                moLai.invoke(form, id);
                var chup = form.getClass().getDeclaredMethod("chupNhap");
                chup.setAccessible(true);
                assertThat(chup.invoke(form)).isEqualTo(nhap());
                assertThat(((javax.swing.JCheckBox) sucKhoe.get(form)).isSelected()).isFalse();
            } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
        }));
    }

    @Test
    void xoaNhapVaChanNguoiKhongCoQuyenSpa() {
        tinhHuong(() -> {
            long id = PhieuSpaTreoService.treo(0, nhap());
            PhieuSpaTreoService.xoa(id);
            assertThatThrownBy(() -> PhieuSpaTreoService.lay(id)).hasMessageContaining("đã được tạo hoặc xóa");
            PhienDangNhapService.layPhien().setCacQuyen(EnumSet.of(Quyen.BAN_HANG));
            assertThatThrownBy(() -> PhieuSpaTreoService.treo(0, nhap())).isInstanceOf(PhanQuyenException.class);
        });
    }
}
