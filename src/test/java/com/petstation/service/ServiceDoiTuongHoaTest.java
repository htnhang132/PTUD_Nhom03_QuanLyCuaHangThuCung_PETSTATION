package com.petstation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.petstation.dao.banhang.HoaDonDAO;
import com.petstation.dao.banhang.PhieuDoiTraDAO;
import com.petstation.dao.chung.DanhMucDAO;
import com.petstation.dao.chung.LuaChonNghiepVuDAO;
import com.petstation.dao.chung.NghiepVuDAO;
import com.petstation.dao.chung.NghiepVuDAO.ThongTinBang;
import com.petstation.dao.chung.QuyTrinhDAO;
import com.petstation.dao.hethong.CaiDatDAO;
import com.petstation.dao.hethong.NhatKyHeThongDAO;
import com.petstation.dao.khachhang.ThuCungDAO;
import com.petstation.dao.khachhang.TichDiemDAO;
import com.petstation.dao.thongke.ThongKeDAO;
import com.petstation.dto.ChiTietBaoCao;
import com.petstation.dto.ChiTietBaoCao.DoanhThuTheoNgay;
import com.petstation.dto.DongNghiepVu;
import com.petstation.dto.GioHang;
import com.petstation.dto.HotelDTO;
import com.petstation.dto.KhoangNgay;
import com.petstation.dto.MucLuaChon;
import com.petstation.dto.PhienDangNhap;
import com.petstation.dto.SpaDTO;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.GioHangService;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.service.chung.QuyTrinhService;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.ThuCungService;
import com.petstation.service.khachhang.TichDiemService;
import com.petstation.service.thongke.ChiTietThongKeService;
import com.petstation.service.thongke.ThongKeService;
import com.petstation.util.DinhDang;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

// Kiểm thử đơn vị offline cho các service mới chuyển sang OOP + DI bằng DAO giả lập
class ServiceDoiTuongHoaTest {
    @SuppressWarnings("unchecked")
    private static <T> T giaLap(Class<T> giaoDien, InvocationHandler boXuLy) {
        return (T)
                Proxy.newProxyInstance(
                        giaoDien.getClassLoader(), new Class<?>[] {giaoDien}, boXuLy);
    }

    private static <T> T trong(Class<T> giaoDien) {
        return giaLap(
                giaoDien,
                (proxy, method, args) -> {
                    Class<?> kieuTraVe = method.getReturnType();
                    if (kieuTraVe == boolean.class) return false;
                    if (kieuTraVe == int.class) return 0;
                    if (kieuTraVe == long.class) return 0L;
                    if (kieuTraVe == double.class) return 0.0;
                    if (List.class.isAssignableFrom(kieuTraVe)) return List.of();
                    if (Optional.class.isAssignableFrom(kieuTraVe)) return Optional.empty();
                    return null;
                });
    }

    private static PhienDangNhap taoPhien(VaiTro vaiTro, Set<Quyen> cacQuyen) {
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap("tester");
        tk.setVaiTro(vaiTro);
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien("NV0001");
        nv.setHoTen("Nhân viên kiểm thử");
        return new PhienDangNhap(tk, nv, cacQuyen);
    }

    @AfterEach
    void donDep() {
        PhienDangNhapService.macDinh().datPhienDangNhap(null);
    }

    @Test
    void thongKeVaChiTietThongKeDungDaoInjectedVaPhanQuyen() {
        PhienDangNhapService phienService = new PhienDangNhapService();
        ChiTietBaoCao baoCaoMau = new ChiTietBaoCao(new String[] {"Mã"}, List.of(), 0);
        AtomicReference<String> nhanVienDaTruyen = new AtomicReference<>("__UNSET__");

        ThongKeDAO thongKeDAO =
                giaLap(
                        ThongKeDAO.class,
                        (proxy, method, args) ->
                                switch (method.getName()) {
                                    case "layDoanhThuTheoNgay" -> {
                                        nhanVienDaTruyen.set((String) args[2]);
                                        yield List.of(
                                                new DoanhThuTheoNgay(LocalDate.now(), 250_000));
                                    }
                                    case "layChiTiet" -> {
                                        nhanVienDaTruyen.set((String) args[3]);
                                        yield baoCaoMau;
                                    }
                                    default -> List.of();
                                });

        NhatKyHeThongService nhatKyService =
                new NhatKyHeThongService(trong(NhatKyHeThongDAO.class), phienService);
        CaiDatService caiDatService =
                new CaiDatService(trong(CaiDatDAO.class), nhatKyService, phienService);

        ThongKeService thongKeService = new ThongKeService(thongKeDAO, caiDatService, phienService);
        ChiTietThongKeService chiTietService = new ChiTietThongKeService(thongKeDAO, phienService);

        phienService.datPhienDangNhap(
                taoPhien(VaiTro.NHAN_VIEN_BAN_HANG, EnumSet.of(Quyen.XEM_BAO_CAO_CA)));

        KhoangNgay ky = KhoangNgay.ganDay(7);
        assertThat(thongKeService.layDoanhThuTheoNgay(ky)).hasSize(1);
        assertThat(nhanVienDaTruyen.get()).isEqualTo("NV0001");

        assertThatThrownBy(() -> chiTietService.lay("Công nợ nhà cung cấp", "Hiện tại", ky))
                .isInstanceOf(SecurityException.class);

        phienService.datPhienDangNhap(
                taoPhien(VaiTro.QUAN_LY_CUA_HANG, EnumSet.allOf(Quyen.class)));
        assertThat(chiTietService.lay("Công nợ nhà cung cấp", "Hiện tại", ky)).isSameAs(baoCaoMau);
        assertThat(nhanVienDaTruyen.get()).isNull();
    }

    @Test
    void quyTrinhServiceDungDaoCoKieuVaKiemTraBuocHopLe() {
        PhienDangNhapService phienService = new PhienDangNhapService();
        phienService.datPhienDangNhap(
                taoPhien(VaiTro.QUAN_LY_CUA_HANG, EnumSet.allOf(Quyen.class)));

        PhongHotel phong101 = new PhongHotel("P101", null, List.of(), "VIP", 200_000L, 1, "DANG_O");
        HopDongLuuTru hd99 = new HopDongLuuTru();
        hd99.setMaHopDong("HD99");
        HopDongLuuTru hd1 = new HopDongLuuTru();
        hd1.setMaHopDong("HD1");
        ThuCung thuMilo = new ThuCung();
        thuMilo.setTen("Milo");
        KhachHang khLan = new KhachHang();
        khLan.setHoTen("Lan");

        QuyTrinhDAO quyTrinhDAO =
                giaLap(
                        QuyTrinhDAO.class,
                        (proxy, method, args) ->
                                switch (method.getName()) {
                                    case "laySpa" ->
                                            Optional.of(
                                                    new SpaDTO.ThongTinBuocSpa(
                                                            "CHO_THUC_HIEN", false));
                                    case "layPhong" ->
                                            List.of(
                                                    new HotelDTO.TomTatPhong(
                                                            "P101", phong101, hd99, 1));
                                    case "layPhongChiTiet" -> List.of(phong101);
                                    case "layLichPhong" ->
                                            List.of(
                                                    new HotelDTO.ChiTietLichPhong(
                                                            phong101,
                                                            new HotelDTO.LichPhong(
                                                                    hd1,
                                                                    thuMilo,
                                                                    khLan,
                                                                    LocalDateTime.now()
                                                                            .minusHours(2),
                                                                    LocalDateTime.now()
                                                                            .plusDays(1)),
                                                            "DANG_O"));
                                    default -> List.of();
                                });

        BanHangService banHangService =
                new BanHangService(trong(HoaDonDAO.class), trong(PhieuDoiTraDAO.class));
        QuyTrinhService quyTrinhService =
                new QuyTrinhService(quyTrinhDAO, banHangService, phienService);

        DanhMucService danhMucService =
                new DanhMucService(
                        trong(DanhMucDAO.class),
                        phienService,
                        new NhatKyHeThongService(trong(NhatKyHeThongDAO.class), phienService));
        GioHang gio =
                new GioHangService(
                                banHangService,
                                new TichDiemService(
                                        trong(TichDiemDAO.class),
                                        new NhatKyHeThongService(
                                                trong(NhatKyHeThongDAO.class), phienService),
                                        phienService),
                                danhMucService,
                                phienService)
                        .tao();
        gio.getQuyTrinh().chonSpa("SPA0010");

        assertThatThrownBy(
                        () ->
                                quyTrinhService.chuyenBuoc(
                                        "CHON_GOI_SPA", "CAP_NHAT_TIEN_DO_SPA", gio))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("thêm dịch vụ");

        List<PhongHotel> dsPhong = quyTrinhService.layPhongHotel();
        assertThat(dsPhong).hasSize(1);
        assertThat(dsPhong.get(0).getDangO()).isNotNull();
        assertThat(dsPhong.get(0).getDangO().getHopDong().getMaHopDong()).isEqualTo("HD1");
    }

    @Test
    void nghiepVuServiceDungDaoCoKieuVaLocGioTreoTheoTaiKhoan() {
        PhienDangNhapService phienService = new PhienDangNhapService();
        phienService.datPhienDangNhap(
                taoPhien(VaiTro.NHAN_VIEN_BAN_HANG, EnumSet.allOf(Quyen.class)));
        AtomicReference<String> taiKhoanTruyenVao = new AtomicReference<>("");

        NghiepVuDAO nghiepVuDAO =
                giaLap(
                        NghiepVuDAO.class,
                        (proxy, method, args) ->
                                switch (method.getName()) {
                                    case "layThongTin" ->
                                            new ThongTinBang("Lô hàng", new String[] {"Mã", "Tên"});
                                    case "layCacDong" -> {
                                        taiKhoanTruyenVao.set((String) args[1]);
                                        yield List.of(
                                                new DongNghiepVu("7", new Object[] {"TREO-1"}));
                                    }
                                    default -> null;
                                });
        LuaChonNghiepVuDAO luaChonDAO =
                giaLap(
                        LuaChonNghiepVuDAO.class,
                        (proxy, method, args) -> List.of(new MucLuaChon("TC01", "TC01 - Milo")));

        NghiepVuService service = new NghiepVuService(nghiepVuDAO, luaChonDAO, phienService);

        assertThat(service.layDinhNghia(LoaiNghiepVu.LO_HANG).tieuDe()).isEqualTo("Lô hàng");
        List<DongNghiepVu> dong = service.layCacDong(LoaiNghiepVu.GIO_HANG_TREO);
        assertThat(dong).hasSize(1);
        assertThat(dong.get(0).layMa()).isEqualTo("7");
        assertThat(taiKhoanTruyenVao.get()).isEqualTo("tester");
        assertThat(service.layLuaChonThuCung()).hasSize(1);
    }

    @Test
    void danhMucVaGioHangServiceDungDaoInjectedVaMucKhuyenMaiCoKieu() {
        PhienDangNhapService phienService = new PhienDangNhapService();
        phienService.datPhienDangNhap(
                taoPhien(VaiTro.QUAN_LY_CUA_HANG, EnumSet.allOf(Quyen.class)));

        DateTimeFormatter dinhDang = DinhDang.NGAY_U;
        String tu = LocalDate.now().minusDays(1).format(dinhDang);
        String den = LocalDate.now().plusDays(1).format(dinhDang);

        DanhMucDAO danhMucDAO =
                giaLap(
                        DanhMucDAO.class,
                        (proxy, method, args) ->
                                switch (method.getName()) {
                                    case "nap" -> {
                                        LoaiDanhMuc loai = (LoaiDanhMuc) args[0];
                                        if (loai == LoaiDanhMuc.KHACH_HANG) {
                                            yield List.of(
                                                    new DongNghiepVu(
                                                            new Object[] {
                                                                "KH01",
                                                                "Nguyễn An",
                                                                "0901234567",
                                                                "",
                                                                "",
                                                                "",
                                                                "Vàng",
                                                                "10",
                                                                "Đang hoạt động"
                                                            }));
                                        }
                                        if (loai == LoaiDanhMuc.KHUYEN_MAI) {
                                            yield List.of(
                                                    new DongNghiepVu(
                                                            new Object[] {
                                                                "KM01",
                                                                "Giảm 10%",
                                                                "Giảm %",
                                                                "Cả đơn",
                                                                "10",
                                                                "100000",
                                                                tu,
                                                                den,
                                                                "Đang áp dụng"
                                                            }));
                                        }
                                        yield List.of();
                                    }
                                    default -> null;
                                });

        ThuCungDAO thuCungDAO =
                giaLap(
                        ThuCungDAO.class,
                        (proxy, method, args) -> {
                            if ("timThuCungHoatDong".equals(method.getName())
                                    && "TC01".equals(args[0])) {
                                ThuCung tc = new ThuCung("TC01");
                                tc.setKhachHang(new KhachHang("KH02"));
                                tc.setLoai("Chó");
                                tc.setCanNang(5.0);
                                return tc;
                            }
                            return null;
                        });

        NhatKyHeThongService nhatKyService =
                new NhatKyHeThongService(trong(NhatKyHeThongDAO.class), phienService);

        DanhMucService danhMucService = new DanhMucService(danhMucDAO, phienService, nhatKyService);
        ThuCungService thuCungService = new ThuCungService(thuCungDAO, danhMucService);

        assertThat(danhMucService.layLuaChonKhachHang()).containsExactly("KH01 - Nguyễn An");
        assertThat(thuCungService.layThuCungHoatDong("TC01").getLoai()).isEqualTo("Chó");
        assertThatThrownBy(() -> thuCungService.layThuCungHoatDong("TC404"))
                .isInstanceOf(IllegalStateException.class);

        BanHangService banHangService =
                new BanHangService(trong(HoaDonDAO.class), trong(PhieuDoiTraDAO.class));
        TichDiemService tichDiemService =
                new TichDiemService(trong(TichDiemDAO.class), nhatKyService, phienService);
        GioHang gio =
                new GioHangService(banHangService, tichDiemService, danhMucService, phienService)
                        .tao();
        gio.setCacDong(List.of(new GioHang.DongHang("SP01", "Hạt khô", 200_000L, 1)));

        GioHang.KhuyenMai kmTotNhat = gio.getKhuyenMaiTotNhat();
        assertThat(kmTotNhat).isNotNull();
        assertThat(kmTotNhat.getMa()).isEqualTo("KM01");
        assertThat(kmTotNhat.getSoTien()).isEqualTo(20_000L);
    }
}
