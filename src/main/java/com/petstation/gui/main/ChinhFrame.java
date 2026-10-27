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

/** Cửa sổ chính tổ chức menu chức năng, thanh công cụ và điều hướng giữa các màn hình. */
public final class ChinhFrame extends JFrame {
    /** Đổi kích thước cạnh. */
    private static final int DOI_KICH_THUOC_CANH = 5;

    /** Lịch sử lớn nhất. */
    private static final int LICH_SU_LON_NHAT = 30;

    /** Ngữ cảnh. */
    private final KhoiTaoUngDung nguCanh;

    /** Phiên đăng nhập. */
    private final PhienDangNhap phienDangNhap;

    /** Màn hình bố cục. */
    private final CardLayout manHinhBoCuc = new CardLayout();

    /** Làm việc vùng. */
    private final JPanel pnlLamViecVung = new JPanel(manHinhBoCuc);

    /** Màn hình bộ nhớ đệm. */
    private final Map<MaManHinh, JPanel> manHinhBoNhoDem = new EnumMap<>(MaManHinh.class);

    /** Màn hình bộ đăng ký. */
    private final DangKyManHinh manHinhBoDangKy;

    /** Thanh công cụ. */
    private final ThanhCongCuChinh pnlThanhCongCu;

    /** Thanh trạng thái. */
    private final ThanhTrangThai pnlThanhTrangThai;

    /** Thanh menu. */
    private final ThanhMenuChinh pnlThanhMenu;

    /** Thanh quy trình. */
    private final ThanhQuyTrinh pnlThanhQuyTrinh;

    /** Gốc. */
    private final JPanel pnlGoc = new JPanel(new BorderLayout());

    /** Lịch sử. */
    private final Deque<MaManHinh> lichSu = new ArrayDeque<>();

    /** Hiện tại. */
    private MaManHinh hienTai;

    public ChinhFrame(KhoiTaoUngDung nguCanh) {
        this.nguCanh = nguCanh;
        this.phienDangNhap = nguCanh.layNguCanhPhien().yeuCauHienTai();
        PhienDangNhapService.datPhienDangNhap(phienDangNhap);
        this.manHinhBoDangKy = new DangKyManHinh(phienDangNhap, this::dieuHuong);

        setTitle("Pet Station – " + phienDangNhap.docVaiTro().layTenHienThi());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setSize(1366, 768);
        setMinimumSize(new Dimension(1100, 680));
        setMaximizedBounds(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        setLocationRelativeTo(null);

        pnlThanhCongCu =
                new ThanhCongCuChinh(() -> dieuHuong(MaManHinh.DASHBOARD), this::diQuayLai);
        pnlThanhTrangThai = new ThanhTrangThai(phienDangNhap, manHinhBoDangKy.layCaLamViec());
        pnlThanhQuyTrinh = new ThanhQuyTrinh(this::chuyenBuoc);
        pnlThanhMenu =
                new ThanhMenuChinh(
                        manHinhBoDangKy, this::dieuHuong, this::dangXuat, this::yeuCauThoat);

        // Trên.
        JPanel pnlTren = new JPanel();
        pnlTren.setLayout(new BoxLayout(pnlTren, BoxLayout.Y_AXIS));
        pnlTren.add(new ThanhTieuDe(this, this::dieuHuong, this::yeuCauThoat));
        pnlTren.add(pnlThanhMenu);
        pnlTren.add(pnlThanhCongCu);
        pnlTren.add(pnlThanhQuyTrinh);

        pnlLamViecVung.setBackground(MauUngDung.NEN_KEM);

        // Viền 5px là vùng kéo đổi kích thước (cửa sổ không dùng viền hệ thống).
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
                    // Phóng to.
                    boolean phongTo = (suKien.getNewState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
                    // Cạnh.
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
                    /**
                     * Lấy cửa sổ kết ca trong lớp xử lý giao diện.
                     *
                     * @param suKien sự kiện
                     */
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
                                // Đích.
                                MaManHinh dich = dangChoSauCaLamViec;
                                dangChoSauCaLamViec = null;
                                manHinhBoDangKy.layCaLamViec().datTieuDeDangCho("");
                                SwingUtilities.invokeLater(() -> dieuHuong(dich));
                            }
                        });

        NhatKyHeThongService.datNguoiDung(phienDangNhap.layTenHienThi());
        NhatKyHeThongService.ghiNhatKy("Đăng nhập", "Hệ thống");
        dieuHuong(MaManHinh.DASHBOARD);
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

    public void dieuHuong(MaManHinh id) {
        if (manHinhBoDangKy.layGioHang().dangGhiThanhToan()) {
            HopThoai.hienThiCanhBao(this, "Đang ghi thanh toán, vui lòng chờ.");
            return;
        }

        // Định nghĩa.
        DinhNghiaManHinh dinhNghia = manHinhBoDangKy.layDinhNghia(id);
        if (dinhNghia == null) return;
        try {
            // Giới hạn thao tác theo quyền và phiên đăng nhập hiện tại.
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

        if (id != MaManHinh.CASH_SHIFT) {
            dangChoSauCaLamViec = null;
            manHinhBoDangKy.layCaLamViec().datTieuDeDangCho("");
        }
        try {
            hienThi(id, dinhNghia, true);
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiLoi(this, "Không tải được dữ liệu: " + ngoaiLe.getMessage());
        }
    }

    /** Đang chờ sau ca làm việc. */
    private MaManHinh dangChoSauCaLamViec;

    private void nhacNhapMoCaLamViec(MaManHinh idDich, DinhNghiaManHinh dich) {
        if (!manHinhBoDangKy.coTheTruyCap(MaManHinh.CASH_SHIFT)) {
            HopThoai.hienThiCanhBao(
                    this,
                    "Chưa có ca làm việc đang mở nên chưa thể dùng \""
                            + dich.tieuDe()
                            + "\".\nHãy nhờ Cửa hàng trưởng mở ca.");
            return;
        }
        // Đi thẳng tới màn Mở ca (không hỏi lại) và nhớ chức năng đang muốn vào để tự quay về sau
        // khi mở ca.
        dangChoSauCaLamViec = idDich;
        manHinhBoDangKy.layCaLamViec().datTieuDeDangCho(dich.tieuDe());
        hienThi(MaManHinh.CASH_SHIFT, manHinhBoDangKy.layDinhNghia(MaManHinh.CASH_SHIFT), true);
    }

    private void hienThi(MaManHinh id, DinhNghiaManHinh dinhNghia, boolean lichSuBanGhi) {
        if (lichSuBanGhi && hienTai != null && hienTai != id) {
            lichSu.push(hienTai);
            while (lichSu.size() > LICH_SU_LON_NHAT) lichSu.removeLast();
        }
        var yeuCau = com.petstation.gui.navigation.NguCanhMoBanGhi.lay();
        if (yeuCau != null && yeuCau.manHinh() == id) {
            var q = manHinhBoDangKy.layGioHang().layQuyTrinh();
            switch (id) {
                case SPA_SCHEDULE -> q.chonLichHen(yeuCau.id());
                case SPA_INTAKE -> q.chonSpa(yeuCau.id());
                case CHECKOUT, HOTEL_JOURNAL -> q.chonHotel(yeuCau.id());
                case HOTEL_BOOKING -> q.chonPhongHotel(yeuCau.id(), yeuCau.ma());
                default -> {}
            }
        }
        hienTai = id;
        // Màn hình.
        JPanel pnlManHinh =
                manHinhBoNhoDem.computeIfAbsent(
                        id,
                        khoa -> {
                            // Đã tạo.
                            JPanel pnlDaTao = manHinhBoDangKy.tao(khoa);
                            pnlLamViecVung.add(pnlDaTao, khoa.name());
                            return pnlDaTao;
                        });
        pnlManHinh.putClientProperty("maManHinh", id);
        manHinhBoCuc.show(pnlLamViecVung, id.name());
        if (pnlManHinh instanceof HoTroThanhCongCu hoTro) hoTro.xuLyHienThi();
        if (yeuCau != null && yeuCau.manHinh() == id)
            com.petstation.gui.navigation.NguCanhMoBanGhi.apDung(pnlManHinh);
        pnlThanhCongCu.datHoatDongVungHienThi(pnlManHinh);
        pnlThanhCongCu.datCoTheDiQuayLai(!lichSu.isEmpty());
        if (yeuCau != null
                && yeuCau.manHinh() == id
                && id == MaManHinh.HOTEL_BOOKING
                && pnlManHinh
                        instanceof com.petstation.gui.processing.hotel.DatPhongHotelPanel dat) {
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
            // Trước đó.
            MaManHinh truocDo = lichSu.pop();
            // Định nghĩa.
            DinhNghiaManHinh dinhNghia = manHinhBoDangKy.layDinhNghia(truocDo);
            if (dinhNghia != null && manHinhBoDangKy.coTheTruyCap(truocDo)) {
                hienThi(truocDo, dinhNghia, false);
                return;
            }
        }
        pnlThanhCongCu.datCoTheDiQuayLai(false);
    }

    private void dangKyCuaSoCacPhimTat() {
        // Gốc.
        JRootPane goc = getRootPane();
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.ALT_DOWN_MASK),
                "home",
                () -> dieuHuong(MaManHinh.DASHBOARD));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK),
                "back",
                this::diQuayLai);
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
                "pos",
                () -> dieuHuong(MaManHinh.POS));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),
                "orders",
                () -> dieuHuong(MaManHinh.ORDERS));
        lienKet(
                goc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0),
                "pay",
                () -> dieuHuong(MaManHinh.PAYMENT));
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
                            /**
                             * Lấy thao tác đã thực hiện trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void actionPerformed(ActionEvent suKien) {
                                lenh.run();
                            }
                        });
    }

    private void dangXuat() {
        if (manHinhBoDangKy.layGioHang().dangGhiThanhToan()) {
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
            manHinhBoDangKy.layGioHang().layQuyTrinh().xoa();
            nguCanh.layNguCanhPhien().xoa();
            PhienDangNhapService.datPhienDangNhap(null);
            dispose();
            SwingUtilities.invokeLater(() -> new DangNhapFrame(nguCanh).setVisible(true));
        }
    }

    private void yeuCauThoat() {
        if (manHinhBoDangKy.layGioHang().dangGhiThanhToan()) {
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
