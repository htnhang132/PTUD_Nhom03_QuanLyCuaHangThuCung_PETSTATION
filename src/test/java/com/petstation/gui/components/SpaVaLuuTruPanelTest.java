package com.petstation.gui.components;

import static org.assertj.core.api.Assertions.assertThat;

import com.formdev.flatlaf.FlatLightLaf;
import com.petstation.dao.banhang.PhienLamViecDAO;
import com.petstation.dto.GioHang;
import com.petstation.dto.HotelDTO;
import com.petstation.dto.MucLuaChon;
import com.petstation.dto.PhienDangNhap;
import com.petstation.gui.banhang.CaThuNganPanel;
import com.petstation.gui.banhang.DoiPhuKienDialog;
import com.petstation.gui.banhang.DoiTraPanel;
import com.petstation.gui.banhang.LapHoaDonPanel;
import com.petstation.gui.banhang.ThanhToanPanel;
import com.petstation.gui.banhang.TimKiemHoaDonPanel;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.DangKyManHinh;
import com.petstation.gui.chung.DanhMucNhieuTabPanel;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.gui.hethong.CaiDatPanel;
import com.petstation.gui.hethong.DoiMatKhauPanel;
import com.petstation.gui.hethong.TongQuanPanel;
import com.petstation.gui.khachhang.BoChonKhachHang;
import com.petstation.gui.khachhang.QuyTacTichDiemPanel;
import com.petstation.gui.luutru.DanhMucPhongHotelPanel;
import com.petstation.gui.luutru.DatPhongHotelDialog;
import com.petstation.gui.luutru.LuuTruHotelPanel;
import com.petstation.gui.luutru.TraCuuPhongHotelPanel;
import com.petstation.gui.nhansu.LichTuanPanel;
import com.petstation.gui.pheduyet.TrungTamPheDuyetPanel;
import com.petstation.gui.spa.LapPhieuSpaPanel;
import com.petstation.gui.spa.TiepNhanSpaPanel;
import com.petstation.gui.thongke.BaoCaoPanel;
import com.petstation.model.chung.LoaiPhieuDoiTra;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiPhongHotel;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.model.spa.GoiDichVuSpa;
import com.petstation.model.spa.LichHen;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.banhang.GioHangService;
import com.petstation.service.hethong.PhienDangNhapService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

class SpaVaLuuTruPanelTest {
    @Test
    void troGiupTheoDungTabDangChon() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    DangKyManHinh ds = new DangKyManHinh(phien, id -> {});
                    kiemTraTroGiupCacTab(
                            (CoSoPanel) ds.tao(MaManHinh.QUAN_LY_SPA),
                            "Điều phối phiếu Spa",
                            "Lập phiếu Spa",
                            "Lịch hẹn Spa");
                    kiemTraTroGiupCacTab(
                            (CoSoPanel) ds.tao(MaManHinh.QUAN_LY_LUU_TRU),
                            "Sơ đồ phòng lưu trú",
                            "Hợp đồng lưu trú");
                    kiemTraTroGiupCacTab(
                            new DanhMucNhieuTabPanel(
                                    new String[] {"Mật khẩu", "Cài đặt"},
                                    new JPanel[] {new DoiMatKhauPanel(), new CaiDatPanel(true)}),
                            "Đổi mật khẩu",
                            "Cài đặt hệ thống");
                });
    }

    private static void kiemTraTroGiupCacTab(CoSoPanel manHinh, String... tieuDe) {
        JTabbedPane tabs = timTatCaKieu(manHinh, JTabbedPane.class).get(0);
        assertThat(tabs.getTabCount()).isEqualTo(tieuDe.length);
        for (int i = 0; i < tieuDe.length; i++) {
            tabs.setSelectedIndex(i);
            assertThat(manHinh.layHuongDanTroGiup().tieuDe).isEqualTo(tieuDe[i]);
            assertThat(manHinh.layKhaNangThanhCongCu().coTheTroGiup()).isTrue();
        }
    }

    @Test
    void caiDatGomHaiTabVaThaoTacCoDinhOCuoiTrang() throws Exception {
        LookAndFeel giaoDienCu = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            SwingUtilities.invokeAndWait(
                    () -> {
                        try {
                            CaiDatPanel panel = new CaiDatPanel(true);
                            JTabbedPane tabs = timTatCaKieu(panel, JTabbedPane.class).get(0);
                            assertThat(tabs.getTabCount()).isEqualTo(2);
                            assertThat(tabs.getTitleAt(0)).isEqualTo("Cửa hàng và thanh toán");
                            assertThat(tabs.getTitleAt(1)).isEqualTo("Vận hành");
                            JButton datLai = timNutTheoTen(panel, "Đặt lại cấu hình");
                            JButton luu = timNutTheoTen(panel, "Lưu cài đặt");
                            assertThat(datLai).isNotNull();
                            assertThat(luu).isNotNull();
                            assertThat(datLai.getParent()).isSameAs(luu.getParent());
                            assertThat(datLai.getParent().getComponents())
                                    .containsExactly(datLai, luu);
                            for (int width : new int[] {1665, 1100, 1366, 900}) {
                                for (int tab = 0; tab < tabs.getTabCount(); tab++) {
                                    tabs.setSelectedIndex(tab);
                                    BufferedImage anh = null;
                                    for (int i = 0; i < 4; i++) anh = veLenAnh(panel, width, 680);
                                    assertThat(datLai.getY()).isEqualTo(luu.getY());
                                    assertThat(datLai.getX() + datLai.getWidth())
                                            .isLessThan(luu.getX());
                                    assertThat(luu.getX() + luu.getWidth())
                                            .isLessThanOrEqualTo(luu.getParent().getWidth());
                                    JScrollPane cuon = (JScrollPane) tabs.getSelectedComponent();
                                    kiemTraNhanCaiDatKhongBiRutGon(cuon);
                                    assertThat(cuon.getViewport().getView().getWidth())
                                            .as("Nội dung tab nằm trong cửa sổ %d px", width)
                                            .isLessThanOrEqualTo(cuon.getViewport().getWidth());
                                    List<JPanel> cacCot =
                                            timTatCaKieu(cuon, JPanel.class).stream()
                                                    .filter(
                                                            p ->
                                                                    "cotNhomCaiDat"
                                                                            .equals(p.getName()))
                                                    .toList();
                                    assertThat(cacCot).hasSize(2);
                                    assertThat(cacCot.get(0).getHeight())
                                            .isEqualTo(cacCot.get(1).getHeight());
                                    for (JPanel cot : cacCot) {
                                        Component theCuoi =
                                                cot.getComponent(cot.getComponentCount() - 1);
                                        assertThat(theCuoi.getY() + theCuoi.getHeight())
                                                .as(
                                                        "Đáy thẻ khớp đáy cột ở tab %d, %d px",
                                                        tab, width)
                                                .isEqualTo(cot.getHeight());
                                    }
                                    luuAnhNeuCauHinh(
                                            anh, "cai-dat-tab-" + tab + "-" + width + ".png");
                                }
                            }
                            CaiDatPanel chiXem = new CaiDatPanel(false);
                            assertThat(timNutTheoTen(chiXem, "Đặt lại cấu hình").isEnabled())
                                    .isFalse();
                        } catch (Exception ex) {
                            throw new AssertionError(ex);
                        }
                    });
        } finally {
            UIManager.setLookAndFeel(giaoDienCu);
        }
    }

    private static void kiemTraNhanCaiDatKhongBiRutGon(Container tab) {
        for (JLabel nhan : timTatCaKieu(tab, JLabel.class)) {
            if (nhan.getText() == null || nhan.getText().startsWith("<html>")) continue;
            int rongChu = nhan.getFontMetrics(nhan.getFont()).stringWidth(nhan.getText());
            assertThat(nhan.getWidth() - nhan.getInsets().left - nhan.getInsets().right)
                    .as("Nhãn phải hiện đầy đủ: %s", nhan.getText())
                    .isGreaterThanOrEqualTo(rongChu + 2);
        }
    }

    private PhienDangNhap phien;
    private com.petstation.gui.DongThongBaoKiemThu thongBao;

    @BeforeEach
    void thietLapPhien() {
        thongBao = new com.petstation.gui.DongThongBaoKiemThu();
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap("admin_test");
        tk.setVaiTro(VaiTro.QUAN_LY_CUA_HANG);
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien("NV0001");
        nv.setHoTen("Kiểm thử viên");
        phien = new PhienDangNhap(tk, nv, EnumSet.allOf(Quyen.class));
        PhienDangNhapService.macDinh().datPhienDangNhap(phien);
    }

    @AfterEach
    void donDepPhien() {
        thongBao.close();
        PhienDangNhapService.macDinh().datPhienDangNhap(null);
    }

    @Test
    void nutMuiTenVeIconVaChuyenTrangThaiSachSe() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    NutDinhDang btn = new NutDinhDang("Thao tác khác ▾", NutDinhDang.KieuNut.PHU);
                    assertThat(btn.getText()).isEqualTo("Thao tác khác");
                    assertThat(btn.getIcon()).isNotNull();
                    BufferedImage anh = veLenAnh(btn, 140, 34);
                    assertThat(anh.getRGB(70, 17)).isNotEqualTo(0);

                    btn.setText("Thu gọn ▴");
                    assertThat(btn.getText()).isEqualTo("Thu gọn");
                    assertThat(btn.getIcon()).isNotNull();
                });
    }

    @Test
    void khoiTaoVaChuyenTabQuanLySpaVaLuuTruKhongLoiTrenEdt() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    DangKyManHinh boDangKy = new DangKyManHinh(phien, id -> {});
                    JPanel spaPanel = boDangKy.tao(MaManHinh.QUAN_LY_SPA);
                    assertThat(spaPanel).isInstanceOf(TiepNhanSpaPanel.class);
                    TiepNhanSpaPanel spa = (TiepNhanSpaPanel) spaPanel;
                    spa.chonTab(0);
                    luuAnhNeuCauHinh(veLenAnh(spa, 1146, 700), "spa_dieu_phoi.png");
                    spa.chonTab(1);
                    luuAnhNeuCauHinh(veLenAnh(spa, 1146, 700), "spa_lap_phieu.png");
                    spa.chonTab(2);
                    spa.chonTab(0);

                    JPanel hotelPanel = boDangKy.tao(MaManHinh.QUAN_LY_LUU_TRU);
                    assertThat(hotelPanel).isInstanceOf(LuuTruHotelPanel.class);
                    LuuTruHotelPanel hotel = (LuuTruHotelPanel) hotelPanel;
                    napPhongMauChoSoDo(hotel);
                    luuAnhNeuCauHinh(veLenAnh(hotel, 1146, 760), "luutru_so_do.png");

                    JTabbedPane tabHotel = timTabWorkspace(hotel);
                    assertThat(tabHotel).isNotNull();
                    tabHotel.setSelectedIndex(1);
                    luuAnhNeuCauHinh(veLenAnh(hotel, 1146, 700), "luutru_hop_dong.png");

                    GioHang gio = new GioHangService().tao();
                    TiepNhanSpaPanel spaTrucTiep = new TiepNhanSpaPanel(gio, id -> {});
                    assertThat(spaTrucTiep.getComponentCount()).isPositive();
                });
    }

    @Test
    void kiemTraNutBoChonKhachHangVaCacHopThoaiKichThuocLon() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    GioHang gio = new GioHangService().tao();
                    gio.setKhachHang("KH000001", "Đặng Thu Thảo", 1780);
                    BoChonKhachHang boChon = new BoChonKhachHang(gio, () -> {});
                    luuAnhNeuCauHinh(veLenAnh(boChon, 640, 148), "bo_chon_khach.png");
                    JButton btnXoa = timNutTheoTen(boChon, "Bỏ chọn khách");
                    assertThat(btnXoa).isNotNull();
                    assertThat(btnXoa.getHeight()).isBetween(28, 38);

                    try {
                        Constructor<DatPhongHotelDialog> ctor =
                                DatPhongHotelDialog.class.getDeclaredConstructor(
                                        Component.class, String.class, String.class);
                        ctor.setAccessible(true);
                        DatPhongHotelDialog datPhong = ctor.newInstance(null, null, "P001");
                        Field fHopThoai = DatPhongHotelDialog.class.getDeclaredField("hopThoai");
                        fHopThoai.setAccessible(true);
                        JDialog hopThoai = (JDialog) fHopThoai.get(datPhong);
                        assertThat(hopThoai.getWidth()).isGreaterThanOrEqualTo(760);
                        assertThat(hopThoai.getHeight()).isGreaterThanOrEqualTo(640);
                        luuAnhNeuCauHinh(
                                veLenAnh(
                                        hopThoai.getContentPane(),
                                        hopThoai.getWidth(),
                                        hopThoai.getHeight()),
                                "gia_han_dialog.png");
                        hopThoai.dispose();
                    } catch (Exception ex) {
                        throw new AssertionError("Không khởi tạo được DatPhongHotelDialog", ex);
                    }

                    DoiPhuKienDialog doiPhuKien = new DoiPhuKienDialog(null, gio, id -> {});
                    assertThat(doiPhuKien.getWidth()).isGreaterThanOrEqualTo(1060);
                    assertThat(doiPhuKien.getHeight()).isGreaterThanOrEqualTo(680);
                    luuAnhNeuCauHinh(
                            veLenAnh(
                                    doiPhuKien.getContentPane(),
                                    doiPhuKien.getWidth(),
                                    doiPhuKien.getHeight()),
                            "doi_phu_kien_dialog.png");
                    doiPhuKien.dispose();
                });
    }

    @Test
    void kiemTraDongNhatNutVaPhimTatSpaVaLuuTru() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    DangKyManHinh boDangKy = new DangKyManHinh(phien, id -> {});
                    TiepNhanSpaPanel spa = (TiepNhanSpaPanel) boDangKy.tao(MaManHinh.QUAN_LY_SPA);
                    assertThat(spa.layKhaNangThanhCongCu().coTheTao()).isTrue();
                    assertThat(spa.layKhaNangThanhCongCu().coTheTim()).isTrue();
                    assertThat(spa.layKhaNangThanhCongCu().coTheIn()).isTrue();
                    assertThat(timNutTheoTen(spa, "+ Lập phiếu")).isNotNull();
                    assertThat(timNutTheoTen(spa, "+ Đặt lịch")).isNull();
                    JComboBox<?> cboSpa = timComboChuaMuc(spa, "Đang thực hiện");
                    assertThat(cboSpa).isNotNull();
                    assertThat(layCacMucCombo(cboSpa)).doesNotContain("Đang làm");

                    LapPhieuSpaPanel lapPhieu = new LapPhieuSpaPanel(ma -> {}, () -> {}, null);
                    assertThat(timNutTheoTen(lapPhieu, "Xóa nội dung đã nhập")).isNotNull();
                    assertThat(timNutTheoTen(lapPhieu, "Làm mới")).isNull();

                    LuuTruHotelPanel hotel =
                            (LuuTruHotelPanel) boDangKy.tao(MaManHinh.QUAN_LY_LUU_TRU);
                    assertThat(hotel.layKhaNangThanhCongCu().coTheTao()).isTrue();
                    assertThat(hotel.layKhaNangThanhCongCu().coTheIn()).isTrue();
                    assertThat(timNutTheoTen(hotel, "+ Đặt phòng")).isNotNull();
                    assertThat(timNutTheoTen(hotel, "+ Đặt phòng (F6)")).isNull();
                    assertThat(timNutTheoTen(hotel, "Làm mới")).isNull();

                    JTabbedPane tabHotel = timTabWorkspace(hotel);
                    tabHotel.setSelectedIndex(1);
                    assertThat(hotel.layKhaNangThanhCongCu().coTheTao()).isTrue();
                    assertThat(timNutTheoTen(hotel, "Dọn phòng xong")).isNull();
                    JButton btnThaoTacKhac = timNutTheoTen(hotel, "Thao tác khác");
                    assertThat(btnThaoTacKhac).isNotNull();
                    assertThat(btnThaoTacKhac.isEnabled()).isFalse();

                    DanhMucPhongHotelPanel dmPhong = new DanhMucPhongHotelPanel();
                    JComboBox<?> cboPhong = timComboChuaMuc(dmPhong, "Bảo trì");
                    assertThat(cboPhong).isNotNull();
                    assertThat(layCacMucCombo(cboPhong))
                            .contains("Tất cả", "Trống", "Có thú", "Đang dọn", "Bảo trì")
                            .doesNotContain("Đang khai thác", "Ngừng hoạt động");
                    assertThat(timNutTheoTen(dmPhong, "Xóa")).isNull();
                });
    }

    @Test
    void kiemTraDongNhatToanHeThongVaChupAnhManHinh() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    DangKyManHinh boDangKy = new DangKyManHinh(phien, id -> {});
                    GioHang gioAo =
                            new GioHang(
                                    new GioHang.ChinhSach() {
                                        @Override
                                        public List<GioHang.MucKhuyenMai> layKhuyenMai() {
                                            return List.of();
                                        }

                                        @Override
                                        public long kiemTraVoucher(
                                                String ma,
                                                String khachHang,
                                                String nguon,
                                                long tamTinh) {
                                            return 0;
                                        }

                                        @Override
                                        public GioHang.BienLai thanhToan(
                                                GioHang gio,
                                                PhuongThuc phuongThuc,
                                                long daNhan,
                                                String thuNgan) {
                                            return null;
                                        }
                                    });
                    LapHoaDonPanel lapHoaDon = new LapHoaDonPanel(gioAo, id -> {});
                    assertThat(timNutTheoTen(lapHoaDon, "Lưu đơn nháp (Ctrl+S)")).isNotNull();
                    assertThat(timNutTheoTen(lapHoaDon, "Mở đơn nháp (F8)")).isNotNull();
                    luuAnhNeuCauHinh(veLenAnh(lapHoaDon, 1146, 720), "pos_lap_hoa_don.png");

                    PhienLamViecDAO daoGia =
                            (PhienLamViecDAO)
                                    Proxy.newProxyInstance(
                                            PhienLamViecDAO.class.getClassLoader(),
                                            new Class<?>[] {PhienLamViecDAO.class},
                                            (proxy, method, args) ->
                                                    method.getReturnType().equals(Optional.class)
                                                            ? Optional.empty()
                                                            : null);
                    CaThuNganPanel caThuNgan = new CaThuNganPanel(new CaThuNganService(daoGia));
                    luuAnhNeuCauHinh(veLenAnh(caThuNgan, 1146, 720), "pos_ca_thu_ngan.png");

                    TongQuanPanel tongQuan =
                            new TongQuanPanel(id -> {}, id -> true, new CaThuNganService(daoGia));
                    assertThat(demThanhPhanKieu(tongQuan, BoGocPanel.class))
                            .isGreaterThanOrEqualTo(2);
                    luuAnhNeuCauHinh(veLenAnh(tongQuan, 1146, 720), "hethong_tong_quan.png");

                    DoiMatKhauPanel doiMk = (DoiMatKhauPanel) boDangKy.tao(MaManHinh.DOI_MAT_KHAU);
                    assertThat(timNutTheoTen(doiMk, "Xóa nội dung đã nhập")).isNotNull();
                    assertThat(timNutTheoTen(doiMk, "Hủy")).isNull();
                    luuAnhNeuCauHinh(veLenAnh(doiMk, 1146, 700), "hethong_doi_mat_khau.png");

                    TrungTamPheDuyetPanel pheDuyet =
                            (TrungTamPheDuyetPanel) boDangKy.tao(MaManHinh.PHE_DUYET_YEU_CAU);
                    List<JTextField> oTim = timTatCaKieu(pheDuyet, JTextField.class);
                    assertThat(oTim)
                            .anySatisfy(
                                    f ->
                                            assertThat(
                                                            f.getClientProperty(
                                                                    "JTextField.showClearButton"))
                                                    .isEqualTo(Boolean.TRUE));
                    luuAnhNeuCauHinh(veLenAnh(pheDuyet, 1146, 700), "pheduyet_trung_tam.png");

                    QuyTacTichDiemPanel quyTac = new QuyTacTichDiemPanel();
                    luuAnhNeuCauHinh(veLenAnh(quyTac, 1146, 700), "khachhang_quy_tac_diem.png");

                    LichTuanPanel lichTuan =
                            (LichTuanPanel) boDangKy.tao(MaManHinh.PHAN_LICH_NHAN_VIEN);
                    assertThat(lichTuan).isInstanceOf(CoSoPanel.class);
                    luuAnhNeuCauHinh(veLenAnh(lichTuan, 1146, 720), "nhansu_lich_tuan.png");

                    BaoCaoPanel baoCao = (BaoCaoPanel) boDangKy.tao(MaManHinh.THONG_KE_DOANH_THU);
                    assertThat(baoCao.layKhaNangThanhCongCu().coTheIn()).isTrue();
                    luuAnhNeuCauHinh(veLenAnh(baoCao, 1146, 720), "thongke_bao_cao.png");

                    JPanel dmSanPham = boDangKy.tao(MaManHinh.DANH_MUC_SAN_PHAM);
                    assertThat(dmSanPham).isInstanceOf(QuanLyDanhMucPanel.class);

                    TimKiemHoaDonPanel timHd =
                            (TimKiemHoaDonPanel) boDangKy.tao(MaManHinh.TIM_KIEM_HOA_DON);
                    JButton btnDoiTuHd = timNutTheoTen(timHd, "Đổi phụ kiện");
                    assertThat(btnDoiTuHd).isNotNull();
                    assertThat(btnDoiTuHd.isVisible()).isFalse();
                    luuAnhNeuCauHinh(veLenAnh(timHd, 1146, 720), "banhang_tim_kiem.png");

                    DoiTraPanel doiTra = (DoiTraPanel) boDangKy.tao(MaManHinh.DOI_TRA_HANG_HOA);
                    assertThat(timNutTheoTen(doiTra, "+ Đổi phụ kiện (F2)")).isNotNull();
                    assertThat(timNutTheoTen(doiTra, "Xem hóa đơn thay thế")).isNotNull();
                    assertThat(timComboChuaMuc(doiTra, "Chờ xử lý đổi hàng")).isNotNull();
                    luuAnhNeuCauHinh(veLenAnh(doiTra, 1146, 720), "banhang_doi_tra.png");
                });
    }

    @Test
    void lapPhieuSpaLocVaChonThuCungKhiNhanLichHen() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    try {
                        Method laDangHoatDong =
                                LapPhieuSpaPanel.class.getDeclaredMethod(
                                        "laDangHoatDong", Object.class);
                        laDangHoatDong.setAccessible(true);
                        assertThat((boolean) laDangHoatDong.invoke(null, "Đang hoạt động"))
                                .isTrue();
                        assertThat((boolean) laDangHoatDong.invoke(null, "Hoạt động")).isTrue();
                        assertThat((boolean) laDangHoatDong.invoke(null, "HOAT_DONG")).isTrue();
                        assertThat((boolean) laDangHoatDong.invoke(null, "Ngừng hoạt động"))
                                .isFalse();

                        LapPhieuSpaPanel panel = new LapPhieuSpaPanel(s -> {}, () -> {}, () -> {});
                        Object[] khach = new Object[] {"KH000001", "Nguyễn Văn A", "0901234567"};
                        List<Object[]> dsThu = new ArrayList<>();
                        Object[] thuActive = new Object[14];
                        thuActive[0] = "TC000001";
                        thuActive[1] = "Milo";
                        thuActive[8] = "KH000001 - Nguyễn Văn A";
                        thuActive[13] = "Đang hoạt động";
                        Object[] thuInactive = new Object[14];
                        thuInactive[0] = "TC000002";
                        thuInactive[1] = "Bean";
                        thuInactive[8] = "KH000001 - Nguyễn Văn A";
                        thuInactive[13] = "Ngừng hoạt động";
                        Object[] thuKhac = new Object[14];
                        thuKhac[0] = "TC000003";
                        thuKhac[1] = "Kiki";
                        thuKhac[8] = "KH000002 - Trần B";
                        thuKhac[13] = "Đang hoạt động";
                        dsThu.add(thuActive);
                        dsThu.add(thuInactive);
                        dsThu.add(thuKhac);

                        LichHen lh = new LichHen();
                        lh.setMaLichHen("LH0001");
                        ThuCung tc = new ThuCung();
                        tc.setMaThuCung("TC000001");
                        tc.setTen("Milo");
                        GoiDichVuSpa dv = new GoiDichVuSpa();
                        dv.setMaGoi("DV01");
                        dv.setTenGoi("Tắm cơ bản");
                        lh.setThuCung(tc);
                        lh.setDichVu(dv);

                        Method apDung =
                                LapPhieuSpaPanel.class.getDeclaredMethod(
                                        "apDungNhanLichHen", Object[].class);
                        apDung.setAccessible(true);
                        apDung.invoke(
                                panel,
                                (Object)
                                        new Object[] {
                                            "LH0001",
                                            lh,
                                            khach,
                                            dsThu,
                                            List.of(new MucLuaChon("DV01", "Tắm cơ bản"))
                                        });

                        Field fCboThu = LapPhieuSpaPanel.class.getDeclaredField("cboThuCung");
                        fCboThu.setAccessible(true);
                        JComboBox<?> cboThu = (JComboBox<?>) fCboThu.get(panel);
                        assertThat(cboThu.getItemCount()).isEqualTo(1);
                        BieuMauTruong.LuaChon daChon =
                                (BieuMauTruong.LuaChon) cboThu.getSelectedItem();
                        assertThat(daChon).isNotNull();
                        assertThat(daChon.giaTri()).isEqualTo("TC000001");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    void lapPhieuSpaVanNhapDuocCanNangKhiCuaSoHep() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    try {
                        LapPhieuSpaPanel panel = new LapPhieuSpaPanel(s -> {}, () -> {}, () -> {});
                        Field field = LapPhieuSpaPanel.class.getDeclaredField("oCanNang");
                        field.setAccessible(true);
                        JTextField canNang = (JTextField) field.get(panel);
                        for (int width : new int[] {1146, 900}) {
                            for (int i = 0; i < 3; i++) veLenAnh(panel, width, 700);
                            assertThat(canNang.getWidth()).isGreaterThanOrEqualTo(100);
                            assertThat(canNang.getHeight()).isGreaterThanOrEqualTo(24);
                            luuAnhNeuCauHinh(
                                    veLenAnh(panel, width, 700), "spa_lap_phieu_" + width + ".png");
                        }
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    void quanLyDanhMucKhopTienToVaAnThaoTacPhuKhiThuGon() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    try {
                        var cauHinh =
                                QuanLyDanhMucPanel.CauHinh.tao("Thú cưng", "thú cưng")
                                        .datTruongDau(1)
                                        .themThaoTacBoSung("+ Thêm thú cưng", d -> null);
                        List<QuanLyDanhMucPanel.TruongDuLieu> truong =
                                List.of(
                                        QuanLyDanhMucPanel.TruongDuLieu.chuyenThanhChuoi("Mã")
                                                .batBuoc(),
                                        QuanLyDanhMucPanel.TruongDuLieu.tao(
                                                        "Khách hàng",
                                                        QuanLyDanhMucPanel.Kieu.HOP_CHON)
                                                .datLuaChon(
                                                        "KH000001 - Nguyễn Văn A",
                                                        "KH000002 - Trần B"));
                        List<Object[]> dong = new ArrayList<>();
                        dong.add(new Object[] {"TC0001", "KH000001"});

                        QuanLyDanhMucPanel panel = new QuanLyDanhMucPanel(cauHinh, truong, dong);
                        panel.moSuaTheoKhoa("TC0001");

                        Field fDauVao = QuanLyDanhMucPanel.class.getDeclaredField("cacDauVao");
                        fDauVao.setAccessible(true);
                        Object[] dauVao = (Object[]) fDauVao.get(panel);
                        JComboBox<?> cboKhach = (JComboBox<?>) dauVao[1];
                        assertThat(cboKhach.getSelectedItem()).isEqualTo("KH000001 - Nguyễn Văn A");

                        Field fPhu = QuanLyDanhMucPanel.class.getDeclaredField("khungThaoTacPhu");
                        fPhu.setAccessible(true);
                        JPanel khungPhu = (JPanel) fPhu.get(panel);
                        assertThat(khungPhu.isVisible()).isTrue();

                        panel.batCheDoThuGon(() -> {});
                        assertThat(khungPhu.isVisible()).isFalse();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    void thanhToanPanelCoDanCotNutHuyTheoCheDoDoiHang() throws Exception {
        SwingUtilities.invokeAndWait(
                () -> {
                    try {
                        GioHang gioAo =
                                new GioHang(
                                        new GioHang.ChinhSach() {
                                            @Override
                                            public List<GioHang.MucKhuyenMai> layKhuyenMai() {
                                                return List.of();
                                            }

                                            @Override
                                            public long kiemTraVoucher(
                                                    String ma,
                                                    String khachHang,
                                                    String nguon,
                                                    long tamTinh) {
                                                return 0;
                                            }

                                            @Override
                                            public GioHang.BienLai thanhToan(
                                                    GioHang gio,
                                                    PhuongThuc phuongThuc,
                                                    long daNhan,
                                                    String thuNgan) {
                                                return null;
                                            }
                                        });
                        PhienLamViecDAO daoGia =
                                (PhienLamViecDAO)
                                        Proxy.newProxyInstance(
                                                PhienLamViecDAO.class.getClassLoader(),
                                                new Class<?>[] {PhienLamViecDAO.class},
                                                (proxy, method, args) ->
                                                        method.getReturnType()
                                                                        .equals(Optional.class)
                                                                ? Optional.empty()
                                                                : null);
                        ThanhToanPanel panel =
                                new ThanhToanPanel(
                                        gioAo, new CaThuNganService(daoGia), "NV0001", id -> {});

                        Field fKhungPhu = ThanhToanPanel.class.getDeclaredField("khungThaoTacPhu");
                        fKhungPhu.setAccessible(true);
                        JPanel khungPhu = (JPanel) fKhungPhu.get(panel);
                        Field fTreoDoi = ThanhToanPanel.class.getDeclaredField("btnTreoDoi");
                        fTreoDoi.setAccessible(true);
                        NutDinhDang btnTreoDoi = (NutDinhDang) fTreoDoi.get(panel);

                        assertThat(btnTreoDoi.isVisible()).isFalse();
                        assertThat(((GridLayout) khungPhu.getLayout()).getColumns()).isEqualTo(1);

                        gioAo.setNguon(LoaiPhieuDoiTra.DOI_HANG.maDB(), "HD0001");
                        Method capNhat =
                                ThanhToanPanel.class.getDeclaredMethod("capNhatNhanNutQuayLai");
                        capNhat.setAccessible(true);
                        capNhat.invoke(panel);

                        assertThat(btnTreoDoi.isVisible()).isTrue();
                        assertThat(((GridLayout) khungPhu.getLayout()).getColumns()).isEqualTo(2);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static JComboBox<?> timComboChuaMuc(Container goc, String muc) {
        for (JComboBox<?> c : timTatCaKieu(goc, JComboBox.class)) {
            if (layCacMucCombo(c).contains(muc)) {
                return c;
            }
        }
        return null;
    }

    private static List<String> layCacMucCombo(JComboBox<?> cbo) {
        List<String> kq = new ArrayList<>();
        for (int i = 0; i < cbo.getItemCount(); i++) {
            kq.add(String.valueOf(cbo.getItemAt(i)));
        }
        return kq;
    }

    private static <T extends Component> List<T> timTatCaKieu(Container goc, Class<T> kieu) {
        List<T> ds = new ArrayList<>();
        for (Component c : goc.getComponents()) {
            if (kieu.isInstance(c)) {
                ds.add(kieu.cast(c));
            }
            if (c instanceof Container con) {
                ds.addAll(timTatCaKieu(con, kieu));
            }
        }
        return ds;
    }

    private static int demThanhPhanKieu(Container goc, Class<?> kieu) {
        int dem = 0;
        for (Component c : goc.getComponents()) {
            if (kieu.isInstance(c)) {
                dem++;
            }
            if (c instanceof Container con) {
                dem += demThanhPhanKieu(con, kieu);
            }
        }
        return dem;
    }

    private static JButton timNutTheoTen(Container goc, String ten) {
        for (Component c : goc.getComponents()) {
            if (c instanceof JButton b && ten.equals(b.getText())) {
                return b;
            }
            if (c instanceof Container con) {
                JButton kq = timNutTheoTen(con, ten);
                if (kq != null) {
                    return kq;
                }
            }
        }
        return null;
    }

    private static JTabbedPane timTabWorkspace(Container goc) {
        for (Component c : goc.getComponents()) {
            if (c instanceof JTabbedPane t) {
                return t;
            }
            if (c instanceof Container con) {
                JTabbedPane kq = timTabWorkspace(con);
                if (kq != null) {
                    return kq;
                }
            }
        }
        return null;
    }

    private static void napPhongMauChoSoDo(LuuTruHotelPanel hotel) {
        try {
            Field f = LuuTruHotelPanel.class.getDeclaredField("khungSoDoPhong");
            f.setAccessible(true);
            TraCuuPhongHotelPanel soDo = (TraCuuPhongHotelPanel) f.get(hotel);
            List<PhongHotel> mau = taoDanhSachPhongMau();
            assertThat(mau).hasSize(18);
            Field dsField = TraCuuPhongHotelPanel.class.getDeclaredField("cacPhong");
            dsField.setAccessible(true);
            dsField.set(soDo, mau);
            Field chonField = TraCuuPhongHotelPanel.class.getDeclaredField("maPhongDangChon");
            chonField.setAccessible(true);
            chonField.set(soDo, "P002");
            Method mDem = TraCuuPhongHotelPanel.class.getDeclaredMethod("capNhatSoLuongTrangThai");
            mDem.setAccessible(true);
            mDem.invoke(soDo);
            Method mVe = TraCuuPhongHotelPanel.class.getDeclaredMethod("ve", List.class);
            mVe.setAccessible(true);
            mVe.invoke(soDo, mau);
        } catch (Exception ignored) {
        }
    }

    private static List<PhongHotel> taoDanhSachPhongMau() {
        List<PhongHotel> ds = new ArrayList<>();
        String tc = "Phòng Tiêu Chuẩn";
        ds.add(new PhongHotel("P001", tc, 200_000, 1, "", TrangThaiPhongHotel.TRONG.maDB()));
        PhongHotel p002 =
                new PhongHotel(
                        "P002",
                        tc,
                        200_000,
                        1,
                        "Nhạy cảm tiếng ồn",
                        TrangThaiPhongHotel.DANG_O.maDB());
        p002.setDangO(
                taoLichMau(
                        "HD0012",
                        "Milo",
                        "Nguyễn Minh Anh",
                        "0909123456",
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(2)));
        ds.add(p002);

        PhongHotel p003 =
                new PhongHotel("P003", tc, 200_000, 1, "", TrangThaiPhongHotel.DANG_O.maDB());
        p003.setDangO(
                taoLichMau(
                        "HD0010",
                        "Bông",
                        "Trần Gia Huy",
                        "0912888777",
                        LocalDateTime.now().minusDays(3),
                        LocalDateTime.now().minusHours(5)));
        ds.add(p003);
        ds.add(new PhongHotel("P004", tc, 200_000, 1, "", TrangThaiPhongHotel.DANG_DON.maDB()));
        ds.add(new PhongHotel("P005", tc, 200_000, 1, "", TrangThaiPhongHotel.TRONG.maDB()));
        ds.add(new PhongHotel("P006", tc, 200_000, 1, "", TrangThaiPhongHotel.BAO_TRI.maDB()));

        String vip = "Phòng VIP";
        PhongHotel p007 =
                new PhongHotel("P007", vip, 450_000, 2, "", TrangThaiPhongHotel.DANG_O.maDB());
        p007.setDangO(
                taoLichMau(
                        "HD0015",
                        "Mun",
                        "Lê Ngọc Hà",
                        "0933444555",
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(3)));
        ds.add(p007);
        PhongHotel p008 =
                new PhongHotel("P008", vip, 450_000, 2, "", TrangThaiPhongHotel.TRONG.maDB());
        p008.setDatTruoc(
                List.of(
                        taoLichMau(
                                "HD0018",
                                "Lu",
                                "Đỗ Thanh Tùng",
                                "0977111222",
                                LocalDateTime.now().plusDays(1),
                                LocalDateTime.now().plusDays(3))));
        ds.add(p008);
        ds.add(new PhongHotel("P009", vip, 450_000, 2, "", TrangThaiPhongHotel.DANG_DON.maDB()));
        ds.add(new PhongHotel("P010", vip, 450_000, 2, "", TrangThaiPhongHotel.TRONG.maDB()));
        ds.add(new PhongHotel("P011", vip, 450_000, 2, "", TrangThaiPhongHotel.BAO_TRI.maDB()));
        ds.add(new PhongHotel("P012", vip, 450_000, 2, "", TrangThaiPhongHotel.TRONG.maDB()));

        String ph = "Phòng Penthouse";
        PhongHotel p013 =
                new PhongHotel("P013", ph, 900_000, 3, "", TrangThaiPhongHotel.DANG_O.maDB());
        p013.setDangO(
                taoLichMau(
                        "HD0020",
                        "Coco",
                        "Phạm Hoàng Long",
                        "0988666999",
                        LocalDateTime.now().minusHours(12),
                        LocalDateTime.now().plusDays(2)));
        ds.add(p013);
        ds.add(new PhongHotel("P014", ph, 900_000, 3, "", TrangThaiPhongHotel.TRONG.maDB()));
        ds.add(new PhongHotel("P015", ph, 900_000, 3, "", TrangThaiPhongHotel.TRONG.maDB()));
        ds.add(new PhongHotel("P016", ph, 900_000, 3, "", TrangThaiPhongHotel.TRONG.maDB()));
        ds.add(new PhongHotel("P017", ph, 900_000, 3, "", TrangThaiPhongHotel.DANG_DON.maDB()));
        ds.add(new PhongHotel("P018", ph, 900_000, 3, "", TrangThaiPhongHotel.TRONG.maDB()));
        return ds;
    }

    private static HotelDTO.LichPhong taoLichMau(
            String maHd,
            String tenThu,
            String tenKhach,
            String sdt,
            LocalDateTime vao,
            LocalDateTime ra) {
        HopDongLuuTru hd = new HopDongLuuTru();
        hd.setMaHopDong(maHd);
        hd.setTienCoc(500_000);
        hd.setCocYeuCau(500_000L);
        ThuCung tc = new ThuCung();
        tc.setTen(tenThu);
        KhachHang kh = new KhachHang();
        kh.setHoTen(tenKhach);
        kh.setSoDienThoai(sdt);
        return new HotelDTO.LichPhong(hd, tc, kh, vao, ra);
    }

    private static BufferedImage veLenAnh(Component thanhPhan, int rong, int cao) {
        thanhPhan.setBounds(0, 0, rong, cao);
        layOutDeQuy(thanhPhan);
        BufferedImage anh = new BufferedImage(rong, cao, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = anh.createGraphics();
        thanhPhan.paint(g2);
        g2.dispose();
        return anh;
    }

    private static void layOutDeQuy(Component thanhPhan) {
        thanhPhan.doLayout();
        if (thanhPhan instanceof Container con) {
            for (Component child : con.getComponents()) {
                layOutDeQuy(child);
            }
        }
    }

    private static void luuAnhNeuCauHinh(BufferedImage anh, String tenTep) {
        String thuMuc = System.getProperty("petstation.chupManHinhThuMuc");
        if (thuMuc == null || thuMuc.isBlank()) {
            return;
        }
        try {
            File out = new File(thuMuc, tenTep);
            ImageIO.write(anh, "png", out);
        } catch (IOException ignored) {
        }
    }
}
