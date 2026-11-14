package com.petstation.integration;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.laySo;
import static com.petstation.database.ThaoTacSQL.thucHienGiaoDich;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.petstation.database.CauHinhCSDL;
import com.petstation.database.KetNoiCSDL;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.PhienDangNhap;
import com.petstation.gui.spa.LapPhieuSpaPanel;
import com.petstation.model.chung.Quyen;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.model.spa.GoiDichVuSpa;
import com.petstation.model.spa.PhieuSpaTreo;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.spa.PhieuSpaTreoService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.EnumSet;
import java.util.UUID;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

@EnabledIfEnvironmentVariable(named = "PETSTATION_SPA_HOLD_SQL_TESTS", matches = "true")
class PhieuSpaTreoSQLTest {
    private static class HoanTac extends RuntimeException {}

    private final PhieuSpaTreoService phieuSpaTreoService = new PhieuSpaTreoService();
    private String maKhach, maThu, maGoi;

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
                                    + " VALUES(?,N'Test Spa',?,N'Spa',GETDATE())",
                                maNhanVien,
                                ma);
                        capNhat(
                                "INSERT INTO"
                                    + " tai_khoan(ma_nhan_vien,ten_dang_nhap,mat_khau_hash,vai_tro)"
                                    + " VALUES(?,?,?,'NHAN_VIEN_SPA_HOTEL')",
                                maNhanVien,
                                ma,
                                "test-hash");
                        TaiKhoan taiKhoan = new TaiKhoan(ma);
                        NhanVien nhanVien = new NhanVien(maNhanVien);
                        PhienDangNhapService.macDinh()
                                .datPhienDangNhap(
                                        new PhienDangNhap(
                                                taiKhoan, nhanVien, EnumSet.allOf(Quyen.class)));
                        maKhach = "KH" + ma;
                        maThu = "TC" + ma;
                        maGoi = "GOI" + ma;
                        capNhat(
                                "INSERT INTO khach_hang(ma_khach_hang,ho_ten,so_dien_thoai)"
                                        + " VALUES(?,N'Test Spa',?)",
                                maKhach,
                                "09" + ma);
                        capNhat(
                                "INSERT INTO"
                                    + " thu_cung(ma_thu_cung,ma_khach_hang,ten,loai,ngay_sinh,so_mui_tiem,ngay_tiem_gan_nhat)"
                                    + " VALUES(?,?,N'Test"
                                    + " pet',N'Chó',DATEADD(month,-6,GETDATE()),2,DATEADD(day,-30,GETDATE()))",
                                maThu,
                                maKhach);
                        capNhat(
                                "INSERT INTO"
                                    + " goi_dich_vu_spa(ma_goi,ten_goi,thoi_gian_du_kien,bang_gia)"
                                    + " VALUES(?,?,60,'DOG:0-100:150000')",
                                maGoi,
                                "Test service " + ma);
                        viec.run();
                        throw new HoanTac();
                    });
        } catch (HoanTac duKien) {
        } finally {
            PhienDangNhapService.macDinh().datPhienDangNhap(null);
        }
    }

    private PhieuSpaTreo nhap() {
        return new PhieuSpaTreo(
                new KhachHang(maKhach),
                new ThuCung(maThu),
                new GoiDichVuSpa(maGoi),
                null,
                "0901234567",
                "5,2",
                "Chưa tắm\nDị ứng \"sữa tắm\"",
                "Giữ vòng cổ • gọi chủ trước khi làm");
    }

    @Test
    void treoNhapDoKhongTaoPhieuChinhThucVaCapNhatCungBanGhi() {
        tinhHuong(
                () -> {
                    var p =
                            new PhieuSpaTreo(
                                    null,
                                    null,
                                    null,
                                    null,
                                    "090",
                                    "đang cân",
                                    "Ghi chú".repeat(200),
                                    "");
                    String id = phieuSpaTreoService.treo("", p);
                    assertThat(phieuSpaTreoService.lay(id)).isEqualTo(p);
                    assertThat(phieuSpaTreoService.treo(id, nhap())).isEqualTo(id);
                    assertThat(phieuSpaTreoService.lay(id)).isEqualTo(nhap());
                    assertThat(phieuSpaTreoService.danhSach())
                            .anySatisfy(
                                    d -> {
                                        assertThat(d.getMaKhoa()).isEqualTo(id);
                                        assertThat(
                                                        d.getPhieuTreo() != null
                                                                        && d.getPhieuTreo()
                                                                                        .getKhachHang()
                                                                                != null
                                                                ? d.getPhieuTreo()
                                                                        .getKhachHang()
                                                                        .getHoTen()
                                                                : "")
                                                .isEqualTo("Test Spa");
                                    });
                    assertThat(laySo("SELECT COUNT(*) FROM phieu_tiep_nhan_spa")).isZero();
                    assertThat(laySo("SELECT COUNT(*) FROM hoa_don")).isZero();
                });
    }

    @Test
    void loiLuuVaLoiTaoPhieuGiuNguyenNhap() {
        tinhHuong(
                () -> {
                    String id = phieuSpaTreoService.treo("", nhap());
                    var sai =
                            new PhieuSpaTreo(
                                    new KhachHang(maKhach),
                                    new ThuCung(maThu),
                                    new GoiDichVuSpa(maGoi),
                                    null,
                                    "0".repeat(101),
                                    "5",
                                    "Bình thường",
                                    "");
                    assertThatThrownBy(() -> phieuSpaTreoService.treo(id, sai))
                            .isInstanceOf(RuntimeException.class);
                    assertThat(phieuSpaTreoService.lay(id)).isEqualTo(nhap());
                    assertThatThrownBy(
                                    () ->
                                            phieuSpaTreoService.taoPhieu(
                                                    id, maThu, maGoi, "", -1, "Bình thường", ""))
                            .hasMessageContaining("Cân nặng");
                    assertThat(phieuSpaTreoService.lay(id)).isEqualTo(nhap());
                    assertThat(laySo("SELECT COUNT(*) FROM phieu_tiep_nhan_spa")).isZero();
                });
    }

    @Test
    void taoPhieuThanhCongMoiXoaNhapVaKhongChoTaoTrung() {
        tinhHuong(
                () -> {
                    String id = phieuSpaTreoService.treo("", nhap());
                    String phieu =
                            phieuSpaTreoService.taoPhieu(
                                    id, maThu, maGoi, "", 5.2, "Bình thường", "Giữ vòng cổ");
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM phieu_tiep_nhan_spa WHERE"
                                                    + " ma_phieu = ?",
                                            phieu))
                            .isEqualTo(1);
                    assertThatThrownBy(() -> phieuSpaTreoService.lay(id))
                            .hasMessageContaining("đã được tạo hoặc xóa");
                    assertThatThrownBy(
                                    () ->
                                            phieuSpaTreoService.taoPhieu(
                                                    id, maThu, maGoi, "", 5.2, "Bình thường", ""))
                            .hasMessageContaining("đã được tạo hoặc xóa");
                    assertThat(laySo("SELECT COUNT(*) FROM phieu_tiep_nhan_spa")).isEqualTo(1);
                });
    }

    @Test
    void moLaiFormGiuLuaChonVaNoiDungNhungYeuCauKiemTraSucKhoeLai() throws Exception {
        SwingUtilities.invokeAndWait(
                () ->
                        tinhHuong(
                                () -> {
                                    String id = phieuSpaTreoService.treo("", nhap());
                                    var form = new LapPhieuSpaPanel(p -> {}, () -> {}, () -> {});
                                    try {
                                        var sucKhoe =
                                                form.getClass().getDeclaredField("chkSucKhoe");
                                        sucKhoe.setAccessible(true);
                                        ((JCheckBox) sucKhoe.get(form)).setSelected(true);
                                        var tai =
                                                form.getClass()
                                                        .getDeclaredMethod(
                                                                "taiDuLieuKhoiPhuc", String.class);
                                        tai.setAccessible(true);
                                        Object duLieu = tai.invoke(form, id);
                                        var apDung =
                                                form.getClass()
                                                        .getDeclaredMethod(
                                                                "apDungKhoiPhucPhieu",
                                                                String.class,
                                                                Object[].class);
                                        apDung.setAccessible(true);
                                        apDung.invoke(form, id, (Object) duLieu);
                                        var chup = form.getClass().getDeclaredMethod("chupNhap");
                                        chup.setAccessible(true);
                                        assertThat(chup.invoke(form)).isEqualTo(nhap());
                                        assertThat(((JCheckBox) sucKhoe.get(form)).isSelected())
                                                .isFalse();
                                    } catch (ReflectiveOperationException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));
    }

    @Test
    void xoaNhapVaChanNguoiKhongCoQuyenSpa() {
        tinhHuong(
                () -> {
                    String id = phieuSpaTreoService.treo("", nhap());
                    phieuSpaTreoService.xoa(id);
                    assertThatThrownBy(() -> phieuSpaTreoService.lay(id))
                            .hasMessageContaining("đã được tạo hoặc xóa");
                    PhienDangNhapService.macDinh()
                            .layPhien()
                            .setCacQuyen(EnumSet.of(Quyen.BAN_HANG));
                    assertThatThrownBy(() -> phieuSpaTreoService.treo("", nhap()))
                            .isInstanceOf(SecurityException.class);
                });
    }
}
