package com.petstation.gui.main;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.ThanhCongCuChinh;
import com.petstation.gui.components.ThanhMenuChinh;
import com.petstation.gui.components.ThanhQuyTrinh;
import com.petstation.gui.components.ThanhTieuDe;
import com.petstation.gui.components.ThanhTrangThai;
import com.petstation.gui.components.ThayDoiKichThuocCuaSo;
import com.petstation.gui.navigation.DangKyManHinh;
import com.petstation.gui.navigation.DinhNghiaManHinh;
import com.petstation.gui.system.DangNhapFrame;
import com.petstation.model.MaManHinh;
import com.petstation.model.PhienDangNhap;
import com.petstation.service.NhatKyHeThongService;
import com.petstation.service.PhanQuyenException;
import com.petstation.service.PhienDangNhapService;
import com.petstation.util.KhoiTaoUngDung;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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

    private final JPanel pnlLamViecVung = new JPanel(manHinhBoCuc);

    private final Map<MaManHinh, JPanel> manHinhBoNhoDem = new EnumMap<>(MaManHinh.class);

    private final DangKyManHinh manHinhBoDangKy;

    private final ThanhCongCuChinh pnlThanhCongCu;

    private final ThanhTrangThai pnlThanhTrangThai;

    private final ThanhMenuChinh pnlThanhMenu;

    private final ThanhQuyTrinh pnlThanhQuyTrinh;

    private final JPanel pnlGoc = new JPanel(new BorderLayout());

    private final Deque<MaManHinh> lichSu = new ArrayDeque<>();

    private MaManHinh hienTai;

    public ChinhFrame(KhoiTaoUngDung nguCanh) {
        this.nguCanh = nguCanh;
        this.phienDangNhap = nguCanh.layNguCanhPhien().yeuCauHienTai();
        PhienDangNhapService.datPhienDangNhap(phienDangNhap);
        this.manHinhBoDangKy =
                new DangKyManHinh(
                        phienDangNhap, this::dieuHuong, this::quayLaiTuThanhToan);

        setTitle("Pet Station – " + phienDangNhap.docVaiTro().getTenHienThi());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setSize(1366, 768);
        setMinimumSize(new Dimension(1100, 680));
        setMaximizedBounds(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        setLocationRelativeTo(null);

        pnlThanhCongCu =
                new ThanhCongCuChinh(() -> dieuHuong(MaManHinh.TONG_QUAN), this::diQuayLai);
        pnlThanhTrangThai = new ThanhTrangThai(phienDangNhap, manHinhBoDangKy.layCaLamViec());
        pnlThanhQuyTrinh = new ThanhQuyTrinh(this::chuyenBuoc);
        pnlThanhMenu =
                new ThanhMenuChinh(
                        manHinhBoDangKy, this::dieuHuong, this::dangXuat, this::yeuCauThoat);

        JPanel pnlTren = new JPanel();
        pnlTren.setLayout(new BoxLayout(pnlTren, BoxLayout.Y_AXIS));
        pnlTren.add(new ThanhTieuDe(this, this::dieuHuong, this::yeuCauThoat));
        pnlTren.add(pnlThanhMenu);
        pnlTren.add(pnlThanhCongCu);
        pnlTren.add(pnlThanhQuyTrinh);

        pnlLamViecVung.setBackground(MauUngDung.NEN_KEM);

        pnlGoc.setBackground(new Color(0x8C, 0x7A, 0x6E));
        pnlGoc.setBorder(
                BorderFactory.createEmptyBorder(
                        DOI_KICH_THUOC_CANH,
                        DOI_KICH_THUOC_CANH,
                        DOI_KICH_THUOC_CANH,
                        DOI_KICH_THUOC_CANH));
        ThayDoiKichThuocCuaSo.thietLap(this, pnlGoc);
        addWindowStateListener(
                suKien -> {
                    boolean phongTo = (suKien.getNewState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;

                    int canh = phongTo ? 0 : DOI_KICH_THUOC_CANH;
                    pnlGoc.setBorder(BorderFactory.createEmptyBorder(canh, canh, canh, canh));
                });
        pnlGoc.add(pnlTren, BorderLayout.NORTH);
        pnlGoc.add(pnlLamViecVung, BorderLayout.CENTER);
        pnlGoc.add(pnlThanhTrangThai, BorderLayout.SOUTH);
        setContentPane(pnlGoc);

        dangKyCuaSoCacPhimTat();
        addWindowListener(
                new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent suKien) {
                        yeuCauThoat();
                    }
                });

        manHinhBoDangKy
                .layCaLamViec()
                .themBoLangNghe(
                        () -> {
                            if (manHinhBoDangKy.layCaLamViec().kiemTraMo()
                                    && dangChoSauCaLamViec != null) {
                                MaManHinh dich = dangChoSauCaLamViec;
                                dangChoSauCaLamViec = null;
                                manHinhBoDangKy.layCaLamViec().datTieuDeDangCho("");
                                SwingUtilities.invokeLater(() -> dieuHuong(dich));
                            }
                        });

        NhatKyHeThongService.datNguoiDung(phienDangNhap.getTenHienThi());
        NhatKyHeThongService.ghiNhatKy("Đăng nhập", "Hệ thống");
        dieuHuong(MaManHinh.TONG_QUAN);
    }

    private void chuyenBuoc(MaManHinh dich) {
        try {
            com.petstation.service.QuyTrinhService.chuyenBuoc(
                    hienTai.name(), dich.name(), manHinhBoDangKy.layGioHang());
            dieuHuong(dich);
        } catch (RuntimeException loi) {
            HopThoai.hienThiCanhBao(this, loi.getMessage());
        }
    }

    private void quayLaiTuThanhToan(MaManHinh dich) {
        var gio = manHinhBoDangKy.layGioHang();
        String nguon = gio.getKieuNguon();

        boolean quayLaiHotel =
                "HOTEL".equals(nguon) && dich == MaManHinh.TRA_PHONG_LUU_TRU;
        boolean quayLaiSpa =
                "SPA".equals(nguon) && dich == MaManHinh.HOAN_TAT_SPA;
        boolean quayLaiBanHang =
                !"HOTEL".equals(nguon)
                        && !"SPA".equals(nguon)
                        && dich == MaManHinh.LAP_HOA_DON;

        if (hienTai != MaManHinh.THANH_TOAN
                || !(quayLaiHotel || quayLaiSpa || quayLaiBanHang)) {
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
                nguCanh.layPhanQuyenDichVu().yeuCau(dinhNghia.quyen());
            }
        } catch (PhanQuyenException ngoaiLe) {
            HopThoai.hienThiThongBaoHopThoai(
                    this, ngoaiLe.getMessage(), "Không đủ quyền", JOptionPane.WARNING_MESSAGE);
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
        var yeuCau = com.petstation.gui.navigation.NguCanhMoBanGhi.lay();
        if (yeuCau != null && yeuCau.manHinh() == id) {
            var q = manHinhBoDangKy.layGioHang().getQuyTrinh();
            switch (id) {
                case DAT_LICH_HEN_SPA -> q.chonLichHen(yeuCau.id());
                case TIEP_NHAN_SPA -> q.chonSpa(yeuCau.id());
                case TRA_PHONG_LUU_TRU, NHAT_KY_CHAM_SOC -> q.chonHotel(yeuCau.id());
                case DAT_PHONG_LUU_TRU -> q.chonPhongHotel(yeuCau.id(), yeuCau.ma());
                default -> {}
            }
        }
        hienTai = id;

        JPanel pnlManHinh =
                manHinhBoNhoDem.computeIfAbsent(
                        id,
                        khoa -> {
                            JPanel pnlDaTao = manHinhBoDangKy.tao(khoa);
                            pnlLamViecVung.add(pnlDaTao, khoa.name());
                            return pnlDaTao;
                        });
        pnlManHinh.putClientProperty("maManHinh", id);
        manHinhBoCuc.show(pnlLamViecVung, id.name());
        if (id == MaManHinh.DAT_LICH_HEN_SPA
                && pnlManHinh instanceof com.petstation.gui.processing.spa.TiepNhanSpaPanel spa) {
            spa.chonTab(2);
        } else if (id == MaManHinh.QUAN_LY_SPA
                && pnlManHinh instanceof com.petstation.gui.processing.spa.TiepNhanSpaPanel spa) {
            spa.chonTab(0);
        }
        if (pnlManHinh instanceof HoTroThanhCongCu hoTro) hoTro.xuLyHienThi();
        if (yeuCau != null && yeuCau.manHinh() == id)
            com.petstation.gui.navigation.NguCanhMoBanGhi.apDung(pnlManHinh);
        pnlThanhCongCu.datHoatDongVungHienThi(pnlManHinh);
        pnlThanhCongCu.datCoTheDiQuayLai(!lichSu.isEmpty());
        if (yeuCau != null
                && yeuCau.manHinh() == id
                && id == MaManHinh.DAT_PHONG_LUU_TRU
                && pnlManHinh
                        instanceof com.petstation.gui.processing.hotel.NghiepVuHotelPanel dat) {
            com.petstation.gui.navigation.NguCanhMoBanGhi.xoa();
            SwingUtilities.invokeLater(
                    () -> {
                        try {
                            dat.moDonPhong(yeuCau.id());
                        } catch (RuntimeException ex) {
                            HopThoai.hienThiCanhBao(this, ex.getMessage());
                        }
                    });
        }
        pnlThanhQuyTrinh.hienThi(id);
        pnlThanhTrangThai.datGoiY(dinhNghia.goiY());
        setTitle("Pet Station – " + dinhNghia.tieuDe());
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
        pnlThanhCongCu.datCoTheDiQuayLai(false);
    }

    private void dangKyCuaSoCacPhimTat() {
        JRootPane goc = getRootPane();
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
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
                "new",
                () -> pnlThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.MOI));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
                "save",
                () -> pnlThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.LUU));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK),
                "find",
                () -> pnlThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.TIM));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
                "print",
                () -> pnlThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.IN));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK),
                "undo",
                () -> pnlThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.HOAN_TAC));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                "help",
                () -> pnlThanhCongCu.kichHoat(ThanhCongCuChinh.ThaoTac.TRO_GIUP));

        lienKetMenu(goc, KeyEvent.VK_H, 'H');
        lienKetMenu(goc, KeyEvent.VK_D, 'D');
        lienKetMenu(goc, KeyEvent.VK_T, 'T');
        lienKetMenu(goc, KeyEvent.VK_X, 'X');
        lienKetMenu(goc, KeyEvent.VK_K, 'K');
    }

    private void lienKetMenu(JRootPane goc, int maKhoa, char phimTruyCap) {
        lienKet(
                goc,
                KeyStroke.getKeyStroke(maKhoa, KeyEvent.ALT_DOWN_MASK),
                "menu-" + phimTruyCap,
                () -> pnlThanhMenu.moMenu(phimTruyCap));
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

        int ketQua =
                HopThoai.hienThiXacNhanHopThoai(
                        this,
                        "Bạn có chắc muốn đăng xuất?",
                        "Xác nhận đăng xuất",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        if (ketQua == JOptionPane.YES_OPTION) {
            NhatKyHeThongService.ghiNhatKy("Đăng xuất", "Hệ thống");
            manHinhBoDangKy.layGioHang().xoa();
            manHinhBoDangKy.layGioHang().getQuyTrinh().xoa();
            nguCanh.layNguCanhPhien().xoa();
            PhienDangNhapService.datPhienDangNhap(null);
            dispose();
            SwingUtilities.invokeLater(() -> new DangNhapFrame(nguCanh).setVisible(true));
        }
    }

    private void yeuCauThoat() {
        if (manHinhBoDangKy.layGioHang().isDangGhiThanhToan()) {
            HopThoai.hienThiCanhBao(this, "Đang ghi thanh toán, vui lòng chờ.");
            return;
        }

        int ketQua =
                HopThoai.hienThiXacNhanHopThoai(
                        this,
                        "Bạn có chắc muốn thoát Pet Station?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        if (ketQua == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}
