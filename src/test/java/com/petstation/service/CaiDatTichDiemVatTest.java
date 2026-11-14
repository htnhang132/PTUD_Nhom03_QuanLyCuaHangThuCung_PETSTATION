package com.petstation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.petstation.dao.hethong.CaiDatDAO;
import com.petstation.dao.hethong.NhatKyHeThongDAO;
import com.petstation.dao.hethong.TaiKhoanDAO;
import com.petstation.dao.nhansu.NhanVienDAO;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.database.ThaoTacSQLTest;
import com.petstation.dto.DongNhatKyHeThong;
import com.petstation.dto.GioHang;
import com.petstation.dto.PhienDangNhap;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiTaiKhoan;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.hethong.CaiDat;
import com.petstation.model.hethong.NhatKyHeThong;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.hethong.XacThucService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// Kiểm thử đơn vị offline cho cài đặt điểm tích lũy, thuế VAT bao gồm, chặn điểm âm và xác thực
class CaiDatTichDiemVatTest {
    private final Map<String, String> khoCaiDat = new HashMap<>();

    @BeforeEach
    void thietLapPhien() {
        TaiKhoan tk = new TaiKhoan("admin_test");
        tk.setVaiTro(VaiTro.QUAN_LY_CUA_HANG);
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien("NV0001");
        nv.setHoTen("Cửa hàng trưởng");
        nv.setDangLamViec(true);
        PhienDangNhapService.macDinh()
                .datPhienDangNhap(new PhienDangNhap(tk, nv, EnumSet.allOf(Quyen.class)));
    }

    @AfterEach
    void donDep() {
        PhienDangNhapService.macDinh().datPhienDangNhap(null);
        khoCaiDat.clear();
    }

    private CaiDatService taoCaiDatService() {
        CaiDatDAO fakeDao =
                new CaiDatDAO() {
                    @Override
                    public String timGiaTri(String maCaiDat) {
                        return khoCaiDat.get(maCaiDat);
                    }

                    @Override
                    public Optional<CaiDat> timCaiDat(String maCaiDat) {
                        return Optional.empty();
                    }

                    @Override
                    public int themGiaTri(String maCaiDat, String giaTri, String tenDangNhap) {
                        khoCaiDat.put(maCaiDat, giaTri);
                        return 1;
                    }

                    @Override
                    public int capNhatGiaTri(String giaTri, String tenDangNhap, String maCaiDat) {
                        if (!khoCaiDat.containsKey(maCaiDat)) return 0;
                        khoCaiDat.put(maCaiDat, giaTri);
                        return 1;
                    }
                };
        NhatKyHeThongDAO fakeNhatKyDao =
                new NhatKyHeThongDAO() {
                    @Override
                    public void them(Connection ketNoi, NhatKyHeThong muc) {}

                    @Override
                    public int capNhatNhatKy(
                            String tenDangNhap,
                            String hanhDong,
                            String ghiChu,
                            String loaiSuKien,
                            String ketQua) {
                        return 1;
                    }

                    @Override
                    public List<DongNhatKyHeThong> timCacMuc() {
                        return List.of();
                    }
                };
        return new CaiDatService(
                fakeDao,
                new NhatKyHeThongService(fakeNhatKyDao, PhienDangNhapService.macDinh()),
                PhienDangNhapService.macDinh());
    }

    @Test
    void caiDatSoTienMoiDiemTichLuyDocVaCapNhatDong() throws Exception {
        CaiDatService service = taoCaiDatService();
        assertThat(service.laySoTienChoMoiDiemTichLuy()).isEqualTo(10_000L);

        try (AutoCloseable ignored = ThaoTacSQLTest.chayTrongKetNoiGia()) {
            service.capNhatTichLuy(
                    25_000L, 1_000L, new long[] {0L, 2_000_000L, 5_000_000L, 10_000_000L});
        }

        assertThat(khoCaiDat.get("SO_TIEN_MOI_DIEM_TICH")).isEqualTo("25000");
        assertThat(service.laySoTienChoMoiDiemTichLuy()).isEqualTo(25_000L);
        assertThat(service.laySoTienChoMoiDiemTichLuy()).isNotEqualTo(10_000L);

        khoCaiDat.put("SO_TIEN_MOI_DIEM_TICH", "0");
        assertThatThrownBy(service::laySoTienChoMoiDiemTichLuy)
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(
                        () ->
                                service.capNhatTichLuy(
                                        0L,
                                        0L,
                                        new long[] {0L, 1_000_000L, 2_000_000L, 3_000_000L}))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void thueVatBaoGomLamTronBigDecimalChinhXac() {
        assertThat(GioHang.tinhThueBaoGom(108_000L, 8)).isEqualTo(8_000L);
        assertThat(GioHang.tinhThueBaoGom(10_000L, 8)).isEqualTo(741L);
        assertThat(GioHang.tinhThueBaoGom(0L, 8)).isZero();
        assertThat(GioHang.tinhThueBaoGom(100_000L, 0)).isZero();

        GioHang.ChinhSach chinhSachTrong =
                (GioHang.ChinhSach)
                        Proxy.newProxyInstance(
                                GioHang.ChinhSach.class.getClassLoader(),
                                new Class<?>[] {GioHang.ChinhSach.class},
                                (proxy, method, args) ->
                                        method.getReturnType() == long.class ? 0L : List.of());
        GioHang gio = new GioHang(chinhSachTrong);
        gio.setCacDong(List.of(new GioHang.DongHang("SP0001", "Pate", 54_000L, 2)));
        assertThat(gio.tinhTong()).isEqualTo(108_000L);
        assertThat(gio.tinhTienThueGtgtBaoGom(8)).isEqualTo(8_000L);

        GioHang.BienLai bienLai =
                new GioHang.BienLai(
                        "HD0001",
                        LocalDateTime.now(),
                        gio.getCacDong(),
                        108_000L,
                        0L,
                        108_000L,
                        PhuongThuc.TIEN_MAT,
                        108_000L,
                        0L,
                        "Khách lẻ",
                        "",
                        "NV0001",
                        8);
        assertThat(bienLai.tinhTienThueGtgt()).isEqualTo(8_000L);
    }

    @Test
    void chanDiemTichLuyAmVaDoiDiemTrucTiepTrenGioHang() {
        KhachHang kh = new KhachHang();
        assertThatThrownBy(() -> kh.setDiemTichLuy(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("không được âm");

        GioHang.ChinhSach chinhSachTrong =
                (GioHang.ChinhSach)
                        Proxy.newProxyInstance(
                                GioHang.ChinhSach.class.getClassLoader(),
                                new Class<?>[] {GioHang.ChinhSach.class},
                                (proxy, method, args) ->
                                        method.getReturnType() == long.class ? 0L : List.of());
        GioHang gio = new GioHang(chinhSachTrong);
        assertThatThrownBy(() -> gio.setDiemDaDung(10))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> gio.setDiemDaDung(-5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void xacThucServiceTraThongDiepDongNhatKhiSaiTaiKhoanHoacMatKhau() throws Exception {
        String bamMatKhauDung = BCrypt.hashpw("MatKhauDung@1", BCrypt.gensalt(4));
        TaiKhoan tkHopLe = new TaiKhoan("thungan1");
        tkHopLe.setMaBamMatKhau(bamMatKhauDung);
        tkHopLe.setVaiTro(VaiTro.NHAN_VIEN_BAN_HANG);
        tkHopLe.setTrangThai(TrangThaiTaiKhoan.HOAT_DONG);
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien("NV0002");
        nv.setDangLamViec(true);
        tkHopLe.setNhanVien(nv);

        TaiKhoanDAO fakeTaiKhoanDao =
                new TaiKhoanDAO() {
                    @Override
                    public Optional<TaiKhoan> timTheoTenDangNhap(
                            Connection ketNoi, String tenDangNhap) {
                        return "thungan1".equals(tenDangNhap)
                                ? Optional.of(tkHopLe)
                                : Optional.empty();
                    }

                    @Override
                    public Set<Quyen> timCacQuyen(Connection ketNoi, String tenDangNhap) {
                        return Set.of(Quyen.BAN_HANG);
                    }

                    @Override
                    public List<String> timCua(String maVaiTro) {
                        return List.of();
                    }

                    @Override
                    public void ghiNhanDangNhapThatBai(
                            Connection ketNoi,
                            String tenDangNhap,
                            int soLuongThatBai,
                            LocalDateTime khoaDenLuc) {
                        tkHopLe.setSoLanDangNhapSai(soLuongThatBai);
                        tkHopLe.setKhoaDenLuc(khoaDenLuc);
                    }

                    @Override
                    public void ghiNhanDangNhapThanhCong(Connection ketNoi, String tenDangNhap) {}

                    @Override
                    public String layMaBamMatKhau(String tenDangNhap) {
                        return bamMatKhauDung;
                    }

                    @Override
                    public int capNhatMatKhau(String matKhauMoi, String tenDangNhap) {
                        return 1;
                    }
                };
        NhanVienDAO fakeNhanVienDao =
                new NhanVienDAO() {
                    @Override
                    public Optional<NhanVien> timTheoId(Connection ketNoi, String maNhanVien) {
                        return Optional.of(nv);
                    }

                    @Override
                    public Optional<NhanVien> timTheoMa(Connection ketNoi, String maNhanVien) {
                        return Optional.of(nv);
                    }
                };
        NhatKyHeThongDAO fakeNhatKyDao =
                new NhatKyHeThongDAO() {
                    @Override
                    public void them(Connection ketNoi, NhatKyHeThong muc) {}

                    @Override
                    public int capNhatNhatKy(
                            String tenDangNhap,
                            String hanhDong,
                            String ghiChu,
                            String loaiSuKien,
                            String ketQua) {
                        return 1;
                    }

                    @Override
                    public List<DongNhatKyHeThong> timCacMuc() {
                        return List.of();
                    }
                };
        XacThucService xacThucService =
                new XacThucService(
                        QuanLyGiaoDich.macDinh(), fakeTaiKhoanDao, fakeNhanVienDao, fakeNhatKyDao);

        try (AutoCloseable ignored = ThaoTacSQLTest.chayTrongKetNoiGia()) {
            assertThatThrownBy(
                            () -> xacThucService.dangNhap("khong_ton_tai", "bat_ky".toCharArray()))
                    .isInstanceOf(SecurityException.class)
                    .hasMessage("Tên đăng nhập hoặc mật khẩu không đúng.");

            assertThatThrownBy(() -> xacThucService.dangNhap("thungan1", "Sai@123".toCharArray()))
                    .isInstanceOf(SecurityException.class)
                    .hasMessage("Tên đăng nhập hoặc mật khẩu không đúng.");
        }
    }
}
