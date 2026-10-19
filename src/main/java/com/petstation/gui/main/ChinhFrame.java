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
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.gui.system.DangNhapFrame;
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

    /**
     * Khởi tạo ChinhFrame với các giá trị được cung cấp.
     *
     * @param nguCanh ngữ cảnh
     */
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
                            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
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

    /**
     * Điều hướng trong ChinhFrame.
     *
     * @param id id
     */
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
        // Định nghĩa.
        DinhNghiaManHinh dinhNghia = manHinhBoDangKy.layDinhNghia(id);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
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

        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (manHinhBoDangKy.yeuCauMoCaLamViec(id) && !manHinhBoDangKy.layCaLamViec().kiemTraMo()) {
            nhacNhapMoCaLamViec(id, dinhNghia);
            return;
        }

        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
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

    /**
     * Nhắc nhập mở ca làm việc trong ChinhFrame.
     *
     * @param idDich id đích
     * @param dich đích
     */
    private void nhacNhapMoCaLamViec(MaManHinh idDich, DinhNghiaManHinh dich) {
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
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

    /**
     * Hiển thị trong ChinhFrame.
     *
     * @param id id
     * @param dinhNghia định nghĩa
     * @param lichSuBanGhi lịch sử bản ghi
     */
    private void hienThi(MaManHinh id, DinhNghiaManHinh dinhNghia, boolean lichSuBanGhi) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (lichSuBanGhi && hienTai != null && hienTai != id) {
            lichSu.push(hienTai);
            while (lichSu.size() > LICH_SU_LON_NHAT) lichSu.removeLast();
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
        manHinhBoCuc.show(pnlLamViecVung, id.name());
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (pnlManHinh instanceof HoTroThanhCongCu hoTro) hoTro.xuLyHienThi();
        pnlThanhCongCu.datHoatDongVungHienThi(pnlManHinh);
        pnlThanhCongCu.datCoTheDiQuayLai(!lichSu.isEmpty());
        pnlThanhQuyTrinh.hienThi(id);
        pnlThanhTrangThai.datGoiY(dinhNghia.goiY());
        setTitle("Pet Station – " + dinhNghia.tieuDe());
    }

    /** Đi quay lại trong ChinhFrame. */
    private void diQuayLai() {
        while (!lichSu.isEmpty()) {
            // Trước đó.
            MaManHinh truocDo = lichSu.pop();
            // Định nghĩa.
            DinhNghiaManHinh dinhNghia = manHinhBoDangKy.layDinhNghia(truocDo);
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (dinhNghia != null && manHinhBoDangKy.coTheTruyCap(truocDo)) {
                hienThi(truocDo, dinhNghia, false);
                return;
            }
        }
        pnlThanhCongCu.datCoTheDiQuayLai(false);
    }

    /** Đăng ký cửa sổ các phím tắt trong ChinhFrame. */
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

    /**
     * Liên kết menu trong ChinhFrame.
     *
     * @param goc gốc
     * @param maKhoa mã khóa
     * @param phimTruyCap phím truy cập
     */
    private void lienKetMenu(JRootPane goc, int maKhoa, char phimTruyCap) {
        lienKet(
                goc,
                KeyStroke.getKeyStroke(maKhoa, KeyEvent.ALT_DOWN_MASK),
                "menu-" + phimTruyCap,
                () -> pnlThanhMenu.moMenu(phimTruyCap));
    }

    /**
     * Liên kết trong ChinhFrame.
     *
     * @param goc gốc
     * @param khoaNetVe khóa nét vẽ
     * @param ten tên
     * @param lenh lệnh
     */
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

    /** Kết thúc phiên đăng nhập trong ChinhFrame. */
    private void dangXuat() {
        // Kết quả.
        int ketQua =
                HopThoai.hienThiXacNhanHopThoai(
                        this,
                        "Bạn có chắc muốn đăng xuất?",
                        "Xác nhận đăng xuất",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
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

    /** Yêu cầu thoát trong ChinhFrame. */
    private void yeuCauThoat() {
        // Kết quả.
        int ketQua =
                HopThoai.hienThiXacNhanHopThoai(
                        this,
                        "Bạn có chắc muốn thoát Pet Station?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (ketQua == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}
