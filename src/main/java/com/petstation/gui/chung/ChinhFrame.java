package com.petstation.gui.chung;

import com.petstation.dto.PhienDangNhap;
import com.petstation.gui.hethong.DangNhapFrame;
import com.petstation.gui.luutru.NghiepVuHotelPanel;
import com.petstation.gui.spa.TiepNhanSpaPanel;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.service.chung.QuyTrinhService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.KhoiTaoUngDung;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public final class ChinhFrame extends JFrame {
    private static final int DOI_KICH_THUOC_CANH = 5;

    private static final int LICH_SU_LON_NHAT = 30;

    private final KhoiTaoUngDung nguCanh;

    private final PhienDangNhap phienDangNhap;

    private final CardLayout manHinhBoCuc = new CardLayout();

    private final JPanel khungVungLamViec = new JPanel(manHinhBoCuc);

    private final Map<MaManHinh, JPanel> manHinhBoNhoDem = new EnumMap<>(MaManHinh.class);

    private final DangKyManHinh manHinhBoDangKy;

    private final ThanhCongCuChinh khungThanhCongCu;

    private final ThanhTrangThai khungThanhTrangThai;

    private final ThanhMenuChinh khungThanhMenu;

    private final ThanhQuyTrinh khungThanhQuyTrinh;

    private final QuyTrinhService quyTrinhService = new QuyTrinhService();

    private final JPanel khungGoc = new JPanel(new BorderLayout());

    private final Deque<MaManHinh> lichSu = new ArrayDeque<>();

    private MaManHinh hienTai;

    private final BoLangNgheSuKien<CaThuNganService> boLangNgheCaLamViec;

    public ChinhFrame(KhoiTaoUngDung nguCanh) {
        this.nguCanh = nguCanh;
        this.phienDangNhap = PhienDangNhapService.macDinh().yeuCauHienTai();
        this.manHinhBoDangKy =
                new DangKyManHinh(phienDangNhap, this::dieuHuong, this::quayLaiTuThanhToan);
        setTitle("Pet Station – " + phienDangNhap.docVaiTro().getTenHienThi());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setSize(1366, 768);
        setMinimumSize(new Dimension(1100, 680));
        setMaximizedBounds(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        setLocationRelativeTo(null);
        khungThanhCongCu =
                new ThanhCongCuChinh(
                        () -> dieuHuong(MaManHinh.TONG_QUAN),
                        this::diQuayLai,
                        this::lamMoiManHinhHienTai);
        khungThanhTrangThai = new ThanhTrangThai(phienDangNhap, manHinhBoDangKy.layCaLamViec());
        khungThanhQuyTrinh = new ThanhQuyTrinh(this::chuyenBuoc);
        khungThanhMenu =
                new ThanhMenuChinh(
                        manHinhBoDangKy, this::dieuHuong, this::dangXuat, this::yeuCauThoat);
        JPanel khungTren = new JPanel();
        khungTren.setLayout(new BoxLayout(khungTren, BoxLayout.Y_AXIS));
        khungTren.add(new ThanhTieuDe(this, this::dieuHuong, this::yeuCauThoat));
        khungTren.add(khungThanhMenu);
        khungTren.add(khungThanhCongCu);
        khungTren.add(khungThanhQuyTrinh);
        khungVungLamViec.setBackground(MauUngDung.NEN_KEM);
        khungGoc.setBackground(MauUngDung.NAU_MO);
        khungGoc.setBorder(
                BorderFactory.createEmptyBorder(
                        DOI_KICH_THUOC_CANH,
                        DOI_KICH_THUOC_CANH,
                        DOI_KICH_THUOC_CANH,
                        DOI_KICH_THUOC_CANH));
        ThayDoiKichThuocCuaSo.thietLap(this, khungGoc);
        addWindowStateListener(
                suKien -> {
                    boolean phongTo = (suKien.getNewState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
                    int canh = phongTo ? 0 : DOI_KICH_THUOC_CANH;
                    khungGoc.setBorder(BorderFactory.createEmptyBorder(canh, canh, canh, canh));
                });
        khungGoc.add(khungTren, BorderLayout.NORTH);
        khungGoc.add(khungVungLamViec, BorderLayout.CENTER);
        khungGoc.add(khungThanhTrangThai, BorderLayout.SOUTH);
        setContentPane(khungGoc);
        dangKyCuaSoCacPhimTat();
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent suKien) {
                        yeuCauThoat();
                    }
                });
        boLangNgheCaLamViec =
                ca -> {
                    if (ca.kiemTraMo() && dangChoSauCaLamViec != null) {
                        MaManHinh dich = dangChoSauCaLamViec;
                        dangChoSauCaLamViec = null;
                        ca.datTieuDeDangCho("");
                        SwingUtilities.invokeLater(() -> dieuHuong(dich));
                    }
                };
        manHinhBoDangKy.layCaLamViec().themBoLangNghe(boLangNgheCaLamViec);
        TacVuNen.chayCoPhien(
                this,
                this::isDisplayable,
                () -> {
                    NhatKyHeThongService.macDinh().ghiNhatKy("Đăng nhập", "Hệ thống");
                    return null;
                },
                v -> {});
        dieuHuong(MaManHinh.TONG_QUAN);
    }

    private void chuyenBuoc(MaManHinh dich) {
        try {
            quyTrinhService.chuyenBuoc(hienTai.name(), dich.name(), manHinhBoDangKy.layGioHang());
            dieuHuong(dich);
        } catch (RuntimeException loi) {
            HopThoai.hienThiCanhBao(this, loi.getMessage());
        }
    }

    private void quayLaiTuThanhToan(MaManHinh dich) {
        var gio = manHinhBoDangKy.layGioHang();
        String nguon = gio.getKieuNguon();
        boolean quayLaiHotel = "HOTEL".equals(nguon) && dich == MaManHinh.TRA_PHONG_LUU_TRU;
        boolean quayLaiSpa = "SPA".equals(nguon) && dich == MaManHinh.HOAN_TAT_SPA;
        boolean quayLaiBanHang =
                !"HOTEL".equals(nguon) && !"SPA".equals(nguon) && dich == MaManHinh.LAP_HOA_DON;
        if (hienTai != MaManHinh.THANH_TOAN || !(quayLaiHotel || quayLaiSpa || quayLaiBanHang)) {
            HopThoai.hienThiCanhBao(
                    this, "Không thể quay lại màn hình này từ luồng thanh toán hiện tại.");
            return;
        }
        if (quayLaiHotel || quayLaiSpa) gio.xoa();
        dieuHuong(dich);
    }

    public void dieuHuong(MaManHinh id) {
        if (manHinhBoDangKy.layGioHang().isDangGhiThanhToan()) {
            HopThoai.hienThiCanhBao(this, "Đang ghi thanh toán, vui lòng chờ.");
            return;
        }
        DinhNghiaManHinh dinhNghia = manHinhBoDangKy.layDinhNghia(id);
        if (dinhNghia == null) return;
        try {
            if (dinhNghia.quyen() != null) {
                PhienDangNhapService.macDinh().yeuCau(dinhNghia.quyen());
            }
        } catch (SecurityException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage(), "Không đủ quyền");
            return;
        }
        if (manHinhBoDangKy.yeuCauMoCaLamViec(id) && !manHinhBoDangKy.layCaLamViec().kiemTraMo()) {
            nhacNhapMoCaLamViec(id, dinhNghia);
            return;
        }
        if (id != MaManHinh.CA_THU_NGAN) {
            dangChoSauCaLamViec = null;
            manHinhBoDangKy.layCaLamViec().datTieuDeDangCho("");
        }
        try {
            hienThi(id, dinhNghia, true);
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiLoi(this, "Không tải được dữ liệu: " + ngoaiLe.getMessage());
        }
    }

    private MaManHinh dangChoSauCaLamViec;

    private void nhacNhapMoCaLamViec(MaManHinh idDich, DinhNghiaManHinh dich) {
        if (!manHinhBoDangKy.coTheTruyCap(MaManHinh.CA_THU_NGAN)) {
            HopThoai.hienThiCanhBao(
                    this,
                    "Chưa có ca làm việc đang mở nên chưa thể dùng \""
                            + dich.tieuDe()
                            + "\".\nHãy nhờ Cửa hàng trưởng mở ca.");
            return;
        }
        dangChoSauCaLamViec = idDich;
        manHinhBoDangKy.layCaLamViec().datTieuDeDangCho(dich.tieuDe());
        hienThi(MaManHinh.CA_THU_NGAN, manHinhBoDangKy.layDinhNghia(MaManHinh.CA_THU_NGAN), true);
    }

    private void hienThi(MaManHinh id, DinhNghiaManHinh dinhNghia, boolean lichSuBanGhi) {
        if (lichSuBanGhi && hienTai != null && hienTai != id) {
            lichSu.push(hienTai);
            while (lichSu.size() > LICH_SU_LON_NHAT) lichSu.removeLast();
        }
        var yeuCau = NguCanhMoBanGhi.lay();
        if (yeuCau != null && yeuCau.manHinh() == id) {
            var q = manHinhBoDangKy.layGioHang().getQuyTrinh();
            switch (id) {
                case DAT_LICH_HEN_SPA -> q.chonLichHen(yeuCau.ma());
                case TIEP_NHAN_SPA -> q.chonSpa(yeuCau.ma());
                case TRA_PHONG_LUU_TRU, NHAT_KY_CHAM_SOC -> q.chonHotel(yeuCau.ma());
                case DAT_PHONG_LUU_TRU -> q.chonPhongHotel(yeuCau.ma());
                default -> {}
            }
        }
        hienTai = id;
        JPanel khungManHinh =
                manHinhBoNhoDem.computeIfAbsent(
                        id,
                        khoa -> {
                            JPanel khungDaTao = manHinhBoDangKy.tao(khoa);
                            khungVungLamViec.add(khungDaTao, khoa.name());
                            return khungDaTao;
                        });
        khungManHinh.putClientProperty("maManHinh", id);
        manHinhBoCuc.show(khungVungLamViec, id.name());
        if (id == MaManHinh.DAT_LICH_HEN_SPA && khungManHinh instanceof TiepNhanSpaPanel spa) {
            spa.chonTab(2);
        } else if (id == MaManHinh.QUAN_LY_SPA && khungManHinh instanceof TiepNhanSpaPanel spa) {
            spa.chonTab(0);
        }
        if (khungManHinh instanceof HoTroThanhCongCu hoTro) hoTro.xuLyHienThi();
        if (yeuCau != null && yeuCau.manHinh() == id) NguCanhMoBanGhi.apDung(khungManHinh);
        khungThanhCongCu.datHoatDongVungHienThi(khungManHinh);
        khungThanhCongCu.datCoTheDiQuayLai(!lichSu.isEmpty());
        if (yeuCau != null
                && yeuCau.manHinh() == id
                && id == MaManHinh.DAT_PHONG_LUU_TRU
                && khungManHinh instanceof NghiepVuHotelPanel dat) {
            NguCanhMoBanGhi.xoa();
            SwingUtilities.invokeLater(
                    () -> {
                        try {
                            dat.moDonPhong(yeuCau.ma());
                        } catch (RuntimeException ex) {
                            HopThoai.hienThiCanhBao(this, ex.getMessage());
                        }
                    });
        }
        khungThanhQuyTrinh.hienThi(id);
        khungThanhTrangThai.datGoiY(dinhNghia.goiY());
        setTitle("Pet Station – " + dinhNghia.tieuDe());
    }

    private void lamMoiManHinhHienTai() {
        if (hienTai == null) return;
        JPanel khungManHinh = manHinhBoNhoDem.get(hienTai);
        if (khungManHinh instanceof HoTroThanhCongCu hoTro) {
            hoTro.xuLyLamMoi();
        }
    }

    private void diQuayLai() {
        while (!lichSu.isEmpty()) {
            MaManHinh truocDo = lichSu.pop();
            DinhNghiaManHinh dinhNghia = manHinhBoDangKy.layDinhNghia(truocDo);
            if (dinhNghia != null && manHinhBoDangKy.coTheTruyCap(truocDo)) {
                hienThi(truocDo, dinhNghia, false);
                return;
            }
        }
        khungThanhCongCu.datCoTheDiQuayLai(false);
    }

    private void dangKyCuaSoCacPhimTat() {
        JRootPane goc = getRootPane();
        dangKyPhimTatDieuHuong(goc);
        dangKyPhimTatThaoTac(goc);
        lienKetMenu(goc, KeyEvent.VK_H, 'H');
        lienKetMenu(goc, KeyEvent.VK_D, 'D');
        lienKetMenu(goc, KeyEvent.VK_T, 'T');
        lienKetMenu(goc, KeyEvent.VK_X, 'X');
        lienKetMenu(goc, KeyEvent.VK_K, 'K');
    }

    private void dangKyPhimTatDieuHuong(JRootPane goc) {
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.ALT_DOWN_MASK),
                "home",
                () -> dieuHuong(MaManHinh.TONG_QUAN));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK),
                "back",
                this::diQuayLai);
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
                "pos",
                () -> dieuHuong(MaManHinh.LAP_HOA_DON));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),
                "orders",
                () -> dieuHuong(MaManHinh.TIM_KIEM_HOA_DON));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0),
                "spa",
                () -> dieuHuong(MaManHinh.QUAN_LY_SPA));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0),
                "hotel",
                () -> dieuHuong(MaManHinh.QUAN_LY_LUU_TRU));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0),
                "returns",
                () -> dieuHuong(MaManHinh.DOI_TRA_HANG_HOA));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),
                "pay",
                () -> dieuHuong(MaManHinh.THANH_TOAN));
    }

    private void dangKyPhimTatThaoTac(JRootPane goc) {
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
                "new",
                () -> khungThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.MOI));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
                "save",
                () -> khungThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.LUU));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK),
                "find",
                () -> khungThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.TIM));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
                "print",
                () -> khungThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.IN));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK),
                "undo",
                () -> khungThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.HOAN_TAC));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                "help",
                () -> khungThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.TRO_GIUP));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
                "refresh",
                this::lamMoiManHinhHienTai);
    }

    private void lienKetMenu(JRootPane goc, int maKhoa, char phimTruyCap) {
        lienKet(
                goc,
                KeyStroke.getKeyStroke(maKhoa, KeyEvent.ALT_DOWN_MASK),
                "menu-" + phimTruyCap,
                () -> khungThanhMenu.moMenu(phimTruyCap));
    }

    private void lienKet(JRootPane goc, KeyStroke khoaNetVe, String ten, Runnable lenh) {
        goc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(khoaNetVe, ten);
        goc.getActionMap()
                .put(
                        ten,
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent suKien) {
                                lenh.run();
                            }
                        });
    }

    private void dangXuat() {
        if (manHinhBoDangKy.layGioHang().isDangGhiThanhToan()) {
            HopThoai.hienThiCanhBao(this, "Đang ghi thanh toán, vui lòng chờ.");
            return;
        }
        if (HopThoai.xacNhanDongY(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận đăng xuất")) {
            var phien = PhienDangNhapService.macDinh().layPhienHienTai();
            TacVuNen.chayCoPhien(
                    phien,
                    () -> true,
                    () -> {
                        NhatKyHeThongService.macDinh().ghiNhatKy("Đăng xuất", "Hệ thống");
                        return null;
                    },
                    null,
                    v -> {},
                    loi -> {});
            manHinhBoDangKy.layGioHang().xoa();
            manHinhBoDangKy.layGioHang().getQuyTrinh().xoa();
            PhienDangNhapService.macDinh().datPhienDangNhap(null);
            dispose();
            SwingUtilities.invokeLater(() -> new DangNhapFrame(nguCanh).setVisible(true));
        }
    }

    private void yeuCauThoat() {
        if (manHinhBoDangKy.layGioHang().isDangGhiThanhToan()) {
            HopThoai.hienThiCanhBao(this, "Đang ghi thanh toán, vui lòng chờ.");
            return;
        }
        if (HopThoai.xacNhanDongY(this, "Bạn có chắc muốn thoát Pet Station?", "Xác nhận thoát")) {
            dispose();
            System.exit(0);
        }
    }

    @Override
    public void dispose() {
        manHinhBoDangKy.layCaLamViec().xoaBoLangNghe(boLangNgheCaLamViec);
        super.dispose();
    }
}
