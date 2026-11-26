package com.petstation.gui.banhang;

import com.petstation.dto.DonDoiTreo;
import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.HopThoaiTroGiup;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.banhang.DoiTraService;
import com.petstation.service.banhang.DonDoiTreoService;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class DoiPhuKienDialog extends JDialog {
    // 1. THUỘC TÍNH & DỮ LIỆU HỘP THOẠI

    private final DoiTraService doiTraService = new DoiTraService();
    private final DonDoiTreoService donDoiTreoService = new DonDoiTreoService();
    private final GioHang gio;
    private final Consumer<MaManHinh> dieuHuong;
    private final JTextField timHoaDon = new JTextField(24);
    private final JTextField timHang = new JTextField(24);
    private final JTextField lyDo = new JTextField(30);
    private final JSpinner soLuong = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JSpinner soLuongMoi = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JCheckBox tem = new JCheckBox("Nguyên tem mác và bao bì");
    private final JCheckBox chuaDung = new JCheckBox("Chưa qua sử dụng, không rách bẩn");
    private final JLabel trangThai = new JLabel("Chọn hóa đơn hoặc nhập mã hóa đơn / SĐT.");
    private final JLabel tongKet = new JLabel("Chưa chọn hàng đổi và hàng thay thế.");
    private final JLabel nhanHanDoi =
            goiY(
                    "Đổi phụ kiện trong thời hạn quy định • Hàng thay thế giá bằng hoặc cao hơn •"
                            + " Không hoàn tiền");
    private final NutDinhDang tiepTuc =
            new NutDinhDang("Sang thanh toán", NutDinhDang.KieuNut.CHINH);
    private int soNgayHanDoi = 3;
    private final DefaultTableModel moHinhHoaDon =
            model("Hóa đơn", "Khách hàng", "Ngày lập", "Tổng tiền");
    private final DefaultTableModel moHinhDong =
            model(
                    "Sản phẩm",
                    "Nhóm",
                    "Đã mua",
                    "Đã đổi",
                    "Số lượng còn được đổi",
                    "Giá mua ban đầu");
    private final DefaultTableModel moHinhHang =
            model("Mã phụ kiện", "Tên phụ kiện", "Đơn giá", "Tồn khả dụng");
    private final JTable hoaDonBang = bang(moHinhHoaDon);
    private final JTable dongBang = bang(moHinhDong);
    private final JTable hangBang = bang(moHinhHang);
    private List<Object[]> hoaDons = List.of(), dongs = List.of(), hangs = List.of();
    private boolean dangChay;
    private boolean dangKhoiPhuc;
    private boolean xuLyXong;
    private boolean treoDonFlag;
    private boolean dongBoSoLuongMoi = true;
    private final String maDonTreo;

    private long phien;
    private long giaTriCu = -1;

    // 2. API CÔNG KHAI & HÀM KHỞI TẠO

    public boolean daXuLyXong() {
        return xuLyXong;
    }

    public boolean daTreoDon() {
        return treoDonFlag;
    }

    public DoiPhuKienDialog(Component cha, GioHang gio, Consumer<MaManHinh> dieuHuong) {
        this(cha, gio, dieuHuong, "", null);
    }

    public static void moVoiHoaDon(
            Component cha, GioHang gio, Consumer<MaManHinh> dieuHuong, String maHoaDon) {
        String ma = maHoaDon == null ? "" : maHoaDon.trim();
        DoiPhuKienDialog hopThoai = new DoiPhuKienDialog(cha, gio, dieuHuong, "", null, ma);
        hopThoai.setVisible(true);
    }

    public DoiPhuKienDialog(
            Component cha,
            GioHang gio,
            Consumer<MaManHinh> dieuHuong,
            String maDonTreo,
            DonDoiTreo nhap) {
        this(cha, gio, dieuHuong, maDonTreo, nhap, "");
    }

    private DoiPhuKienDialog(
            Component cha,
            GioHang gio,
            Consumer<MaManHinh> dieuHuong,
            String maDonTreo,
            DonDoiTreo nhap,
            String maHoaDonBanDau) {
        super(
                cha == null ? null : SwingUtilities.getWindowAncestor(cha),
                "Đổi phụ kiện — không hoàn tiền",
                ModalityType.APPLICATION_MODAL);
        this.gio = gio;
        this.dieuHuong = dieuHuong;
        this.maDonTreo = maDonTreo == null ? "" : maDonTreo;
        if (maHoaDonBanDau != null && !maHoaDonBanDau.isBlank()) {
            this.timHoaDon.setText(maHoaDonBanDau);
        }
        getContentPane().setBackground(MauUngDung.NEN_KEM);
        add(taoPhanDauHopThoai(), BorderLayout.NORTH);
        cauHinhTruongNhapDoi();
        add(taoNoiDungChinhHopThoai(), BorderLayout.CENTER);
        add(taoChanHopThoai(), BorderLayout.SOUTH);
        ganSuKienHopThoai(nhap, maHoaDonBanDau);
        thietLapKichThuocHopThoai();
    }

    // 3. XÂY DỰNG GIAO DIỆN & SỰ KIỆN

    private JPanel taoPhanDauHopThoai() {
        JPanel dau = new JPanel(new BorderLayout(0, 4));
        dau.setOpaque(false);
        dau.setBorder(BorderFactory.createEmptyBorder(12, 20, 8, 20));
        JLabel tieuDe = new JLabel("Đổi phụ kiện");
        tieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 20));
        tieuDe.setForeground(MauUngDung.NAU);
        dau.add(tieuDe, BorderLayout.NORTH);
        dau.add(nhanHanDoi, BorderLayout.SOUTH);
        return dau;
    }

    private void cauHinhTruongNhapDoi() {
        timHoaDon.putClientProperty(
                "JTextField.placeholderText", "Mã hóa đơn hoặc số điện thoại khách hàng");
        timHang.putClientProperty("JTextField.placeholderText", "Mã hoặc tên phụ kiện thay thế");
        lyDo.putClientProperty("JTextField.placeholderText", "Nhập lý do khách đổi hàng");
        timHoaDon.getAccessibleContext().setAccessibleName("Tìm hóa đơn gốc");
        timHang.getAccessibleContext().setAccessibleName("Tìm phụ kiện thay thế");
        lyDo.getAccessibleContext().setAccessibleName("Lý do đổi hàng");
        for (JTextField o : new JTextField[] {timHoaDon, timHang}) {
            o.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            o.setPreferredSize(new Dimension(180, 34));
        }
        lyDo.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        lyDo.setPreferredSize(new Dimension(220, 34));
        soLuong.setPreferredSize(new Dimension(80, 34));
        soLuongMoi.setPreferredSize(new Dimension(80, 34));
        tem.setOpaque(false);
        chuaDung.setOpaque(false);
        trangThai.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        trangThai.setForeground(MauUngDung.VAN_BAN_XAM);
    }

    private Component taoNoiDungChinhHopThoai() {
        JPanel cotTrai = new JPanel(new GridBagLayout());
        cotTrai.setOpaque(false);
        GridBagConstraints gc1 = new GridBagConstraints();
        gc1.gridx = 0;
        gc1.gridy = 0;
        gc1.weightx = 1.0;
        gc1.weighty = 0.48;
        gc1.fill = GridBagConstraints.BOTH;
        gc1.insets = new Insets(0, 0, 8, 0);
        cotTrai.add(
                nhom(
                        "1. Chọn hóa đơn gốc",
                        thanhTim(timHoaDon, nut("Tìm hóa đơn", this::timHoaDon)),
                        cuon(hoaDonBang, 118)),
                gc1);

        GridBagConstraints gc2 = new GridBagConstraints();
        gc2.gridx = 0;
        gc2.gridy = 1;
        gc2.weightx = 1.0;
        gc2.weighty = 0.52;
        gc2.fill = GridBagConstraints.BOTH;
        cotTrai.add(taoNhomHangCanDoi(), gc2);

        JPanel cotPhai = new JPanel(new BorderLayout(0, 8));
        cotPhai.setOpaque(false);
        cotPhai.add(
                nhom(
                        "3. Chọn phụ kiện thay thế",
                        thanhTim(timHang, nut("Tìm phụ kiện", this::timPhuKien)),
                        cuon(hangBang, 275),
                        hang(new JLabel("Số lượng thay thế"), soLuongMoi),
                        goiY("Tổng tiền hàng thay thế phải bằng hoặc lớn hơn hàng trả.")),
                BorderLayout.CENTER);

        JPanel haiCot = new JPanel(new GridBagLayout());
        haiCot.setBackground(MauUngDung.NEN_KEM);
        haiCot.setBorder(BorderFactory.createEmptyBorder(0, 20, 8, 20));
        GridBagConstraints gTrai = new GridBagConstraints();
        gTrai.gridx = 0;
        gTrai.gridy = 0;
        gTrai.weightx = 0.52;
        gTrai.weighty = 1.0;
        gTrai.fill = GridBagConstraints.BOTH;
        gTrai.insets = new Insets(0, 0, 0, 7);
        haiCot.add(cotTrai, gTrai);
        GridBagConstraints gPhai = new GridBagConstraints();
        gPhai.gridx = 1;
        gPhai.gridy = 0;
        gPhai.weightx = 0.48;
        gPhai.weighty = 1.0;
        gPhai.fill = GridBagConstraints.BOTH;
        gPhai.insets = new Insets(0, 7, 0, 0);
        haiCot.add(cotPhai, gPhai);
        return haiCot;
    }

    private JPanel taoNhomHangCanDoi() {
        JPanel thongTinDoi = new JPanel(new BorderLayout(12, 0));
        thongTinDoi.setOpaque(false);
        thongTinDoi.add(hang(new JLabel("Số lượng đổi"), soLuong), BorderLayout.WEST);
        JPanel nhapLyDo = new JPanel(new BorderLayout(8, 0));
        nhapLyDo.setOpaque(false);
        JLabel nhanLyDo = new JLabel("Lý do");
        nhanLyDo.setLabelFor(lyDo);
        nhapLyDo.add(nhanLyDo, BorderLayout.WEST);
        nhapLyDo.add(lyDo, BorderLayout.CENTER);
        thongTinDoi.add(nhapLyDo, BorderLayout.CENTER);
        return nhom("2. Chọn hàng cần đổi", cuon(dongBang, 112), thongTinDoi, hang(tem, chuaDung));
    }

    private JPanel taoChanHopThoai() {
        JPanel chan = new JPanel(new BorderLayout(0, 8));
        chan.setBackground(MauUngDung.NEN_KEM);
        chan.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, MauUngDung.VIEN),
                        BorderFactory.createEmptyBorder(10, 20, 12, 20)));
        tongKet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        tongKet.setForeground(MauUngDung.NAU);
        chan.add(
                nhom(
                        "Tổng kết đổi hàng",
                        tongKet,
                        goiY(
                                "Giữ nguyên điểm cũ • Tích điểm trên tiền trả thêm • Không áp dụng"
                                        + " voucher Spa")),
                BorderLayout.NORTH);
        JPanel trangThaiDong = new JPanel(new BorderLayout(0, 6));
        trangThaiDong.setOpaque(false);
        trangThaiDong.add(trangThai, BorderLayout.NORTH);
        JPanel nutDong = new JPanel(new BorderLayout());
        nutDong.setOpaque(false);
        nutDong.add(HopThoaiTroGiup.taoNutTroGiup(this, DoiPhuKienDialog.class), BorderLayout.WEST);
        NutDinhDang btnHuy = new NutDinhDang("Hủy", NutDinhDang.KieuNut.TRONG_SUOT);
        btnHuy.addActionListener(e -> huy());
        nutDong.add(
                hang(btnHuy, nut("Lưu phiếu đổi nháp", this::treoDon), tiepTuc), BorderLayout.EAST);
        trangThaiDong.add(nutDong, BorderLayout.CENTER);
        chan.add(trangThaiDong, BorderLayout.CENTER);
        return chan;
    }

    private void ganSuKienHopThoai(DonDoiTreo nhap, String maHoaDonBanDau) {
        tiepTuc.setEnabled(false);
        tiepTuc.addActionListener(e -> chuyenThanhToan());
        timHoaDon.addActionListener(e -> timHoaDon());
        timHang.addActionListener(e -> timPhuKien());
        apDungToMauDongKhongDoi();
        hoaDonBang
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting() && !dangChay && !dangKhoiPhuc)
                                chonHoaDon();
                        });
        dongBang.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting() && !dangKhoiPhuc) {
                                capNhatGioiHanSoLuongDoi();
                                voHieuGia();
                            }
                        });
        hangBang.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting() && !dangKhoiPhuc) {
                                voHieuGia();
                            }
                        });
        soLuong.addChangeListener(
                e -> {
                    if (!dangKhoiPhuc && dongBoSoLuongMoi) {
                        soLuongMoi.setValue(soLuong.getValue());
                    }
                    voHieuGia();
                });
        soLuongMoi.addChangeListener(
                e -> {
                    if (!dangKhoiPhuc) {
                        dongBoSoLuongMoi =
                                Integer.valueOf((Integer) soLuong.getValue())
                                        .equals(soLuongMoi.getValue());
                    }
                    voHieuGia();
                });
        tem.addActionListener(e -> voHieuGia());
        chuaDung.addActionListener(e -> voHieuGia());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        huy();
                    }
                });
        if (nhap != null) {
            SwingUtilities.invokeLater(() -> khoiPhuc(nhap));
        } else if (DieuKienTaiDuLieu.coTheTachLuong()
                || PhienDangNhapService.macDinh().layPhienHienTai() != null) {
            SwingUtilities.invokeLater(() -> napKhoiTaoMacDinh(maHoaDonBanDau));
        }
    }

    private void apDungToMauDongKhongDoi() {
        DefaultTableCellRenderer gocRenderer =
                (DefaultTableCellRenderer) dongBang.getDefaultRenderer(Object.class);
        dongBang.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column) {
                        Component c =
                                gocRenderer.getTableCellRendererComponent(
                                        table, value, isSelected, hasFocus, row, column);
                        if (!isSelected && row >= 0 && row < dongs.size()) {
                            Object[] d = dongs.get(row);
                            boolean hopLe =
                                    "Phụ kiện".equals(String.valueOf(d[2]))
                                            && so(d[3]) - so(d[4]) > 0;
                            if (!hopLe) {
                                c.setForeground(MauUngDung.VAN_BAN_XAM);
                            }
                        }
                        if (c instanceof JComponent jc && row >= 0 && row < dongs.size()) {
                            Object[] d = dongs.get(row);
                            boolean hopLe =
                                    "Phụ kiện".equals(String.valueOf(d[2]))
                                            && so(d[3]) - so(d[4]) > 0;
                            jc.setToolTipText(
                                    hopLe ? null : "Chỉ đổi được phụ kiện còn hạn mức đổi.");
                        }
                        return c;
                    }
                });
    }

    private void capNhatGioiHanSoLuongDoi() {
        int i = dongBang.getSelectedRow();
        if (i < 0 || i >= dongs.size()) return;
        Object[] d = dongs.get(i);
        long conDoi = Math.max(1L, so(d[3]) - so(d[4]));
        int toiDa = (int) Math.min(100000L, conDoi);
        if (soLuong.getModel() instanceof SpinnerNumberModel m) {
            m.setMaximum(toiDa);
            int hienTai = (Integer) soLuong.getValue();
            if (hienTai > toiDa) {
                soLuong.setValue(toiDa);
            }
        }
    }

    // 4. NẠP DỮ LIỆU, ĐƠN ĐỔI TREO & KHÔI PHỤC

    private void napKhoiTaoMacDinh(String maHoaDonBanDau) {
        if (dangChay) return;
        String khoa = maHoaDonBanDau == null ? "" : maHoaDonBanDau.trim();
        chayNen(
                () -> {
                    int soNgay = doiTraService.laySoNgayHanDoi();
                    List<Object[]> hd = doiTraService.timHoaDon(khoa);
                    List<Object[]> dong = List.of();
                    int chon = -1;
                    if (!khoa.isBlank() && !hd.isEmpty()) {
                        dong = doiTraService.layDongHoaDon(String.valueOf(hd.get(0)[0]));
                        chon = 0;
                    }
                    List<Object[]> hang = doiTraService.timPhuKien("");
                    return new DuLieuNhap(hd, dong, hang, chon, soNgay);
                },
                dl -> {
                    apDungHanDoi(dl.soNgayHanDoi);
                    hoaDons = dl.hoaDon;
                    dongs = dl.dong;
                    hangs = dl.hang;
                    moHinhHoaDon.setRowCount(0);
                    moHinhDong.setRowCount(0);
                    moHinhHang.setRowCount(0);
                    for (Object[] h : hoaDons) {
                        moHinhHoaDon.addRow(new Object[] {h[1], h[2], h[3], tien(h[4])});
                    }
                    for (Object[] d : dongs) {
                        moHinhDong.addRow(
                                new Object[] {
                                    d[1], d[2], d[3], d[4], so(d[3]) - so(d[4]), tien(d[5])
                                });
                    }
                    for (Object[] h : hangs) {
                        moHinhHang.addRow(new Object[] {h[0], h[1], tien(h[2]), h[3]});
                    }
                    if (dl.chonHoaDon >= 0 && dl.chonHoaDon < moHinhHoaDon.getRowCount()) {
                        hoaDonBang.setRowSelectionInterval(dl.chonHoaDon, dl.chonHoaDon);
                    }
                    if (hoaDons.isEmpty()) {
                        trangThai.setText(
                                khoa.isBlank()
                                        ? "Không có hóa đơn đủ điều kiện đổi trong "
                                                + (soNgayHanDoi * 24)
                                                + " giờ gần đây."
                                        : "Không tìm thấy hóa đơn phù hợp.");
                    } else if (dl.chonHoaDon >= 0) {
                        trangThai.setText(
                                "Chọn phụ kiện đã mua và phụ kiện thay thế. Hạn đổi: "
                                        + (soNgayHanDoi * 24)
                                        + " giờ từ lúc thanh toán.");
                    } else {
                        trangThai.setText(
                                "Đã tải "
                                        + hoaDons.size()
                                        + " hóa đơn gần đây đủ điều kiện đổi và "
                                        + hangs.size()
                                        + " phụ kiện đang bán.");
                    }
                });
    }

    private void apDungHanDoi(int soNgay) {
        this.soNgayHanDoi = Math.max(1, soNgay);
        int soGio = this.soNgayHanDoi * 24;
        nhanHanDoi.setText(
                "Đổi trong "
                        + soGio
                        + " giờ ("
                        + this.soNgayHanDoi
                        + " ngày) • Hàng thay thế giá bằng hoặc cao hơn • Không hoàn tiền");
    }

    private void thietLapKichThuocHopThoai() {
        Rectangle vung = getGraphicsConfiguration().getBounds();
        Insets le = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        HopThoai.canChinhKichThuocVuaManHinh(this, 1060, 680);
        setMinimumSize(
                new Dimension(
                        Math.min(900, vung.width - le.left - le.right - 32),
                        Math.min(580, vung.height - le.top - le.bottom - 32)));
        HopThoai.chuanHoaCuaSo(this, getOwner());
        getRootPane()
                .registerKeyboardAction(
                        e -> huy(),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private DonDoiTreo chupNhap() throws ParseException {
        soLuong.commitEdit();
        soLuongMoi.commitEdit();
        int h = hoaDonBang.getSelectedRow(),
                d = dongBang.getSelectedRow(),
                m = hangBang.getSelectedRow();
        return new DonDoiTreo(
                h < 0 ? timHoaDon.getText() : hoaDons.get(h)[1].toString(),
                d < 0 ? "" : String.valueOf(dongs.get(d)[0]),
                m < 0 ? "" : hangs.get(m)[0].toString(),
                timHang.getText(),
                (Integer) soLuong.getValue(),
                (Integer) soLuongMoi.getValue(),
                lyDo.getText(),
                tem.isSelected(),
                chuaDung.isSelected());
    }

    private void treoDon() {
        if (dangChay) return;
        try {
            DonDoiTreo nhap = chupNhap();
            chayNen(
                    () -> donDoiTreoService.treo(maDonTreo, nhap),
                    id -> {
                        xuLyXong = true;
                        treoDonFlag = true;
                        dispose();
                        dieuHuong.accept(MaManHinh.HOA_DON_CHO);
                        HopThoai.hienThiThanhCong(
                                getOwner(), "Đã treo đơn đổi. Mở lại tại Đơn nháp để tiếp tục.");
                    });
        } catch (Exception e) {
            baoLoi(e);
        }
    }

    private static final class DuLieuNhap {
        private final List<Object[]> hoaDon;
        private final List<Object[]> dong;
        private final List<Object[]> hang;
        private final int chonHoaDon;
        private final int soNgayHanDoi;

        private DuLieuNhap(
                List<Object[]> hoaDon,
                List<Object[]> dong,
                List<Object[]> hang,
                int chonHoaDon,
                int soNgayHanDoi) {
            this.hoaDon = hoaDon;
            this.dong = dong;
            this.hang = hang;
            this.chonHoaDon = chonHoaDon;
            this.soNgayHanDoi = soNgayHanDoi;
        }
    }

    private void khoiPhuc(DonDoiTreo nhap) {
        dangKhoiPhuc = true;
        try {
            timHoaDon.setText(nhap.timHoaDon());
            timHang.setText(nhap.timHang());
            lyDo.setText(nhap.lyDo());
            soLuong.setValue(nhap.soLuong());
            soLuongMoi.setValue(nhap.soLuongMoi());
            dongBoSoLuongMoi = nhap.soLuongMoi() == nhap.soLuong();
            tem.setSelected(nhap.nguyenTem());
            chuaDung.setSelected(nhap.chuaDung());
        } finally {
            dangKhoiPhuc = false;
        }
        chayNen(
                () -> {
                    int soNgay = doiTraService.laySoNgayHanDoi();
                    var hd = doiTraService.timHoaDon(nhap.timHoaDon());
                    List<Object[]> dong = List.of();
                    int chon = -1;
                    for (int i = 0; i < hd.size(); i++) {
                        if (nhap.dongGoc().isBlank()) {
                            if (hd.get(i)[1].toString().equals(nhap.timHoaDon())) {
                                dong = doiTraService.layDongHoaDon(String.valueOf(hd.get(i)[0]));
                                chon = i;
                            }
                            continue;
                        }
                        var ds = doiTraService.layDongHoaDon(String.valueOf(hd.get(i)[0]));
                        if (ds.stream()
                                .anyMatch(d -> String.valueOf(d[0]).equals(nhap.dongGoc()))) {
                            dong = ds;
                            chon = i;
                            break;
                        }
                    }
                    var hang =
                            doiTraService.timPhuKien(
                                    nhap.maThayThe().isBlank() ? nhap.timHang() : nhap.maThayThe());
                    return new DuLieuNhap(hd, dong, hang, chon, soNgay);
                },
                dl -> apDungDuLieuKhoiPhuc(nhap, dl));
    }

    private void apDungDuLieuKhoiPhuc(DonDoiTreo nhap, DuLieuNhap dl) {
        dangKhoiPhuc = true;
        try {
            apDungHanDoi(dl.soNgayHanDoi);
            hoaDons = dl.hoaDon;
            dongs = dl.dong;
            hangs = dl.hang;
            moHinhHoaDon.setRowCount(0);
            moHinhDong.setRowCount(0);
            moHinhHang.setRowCount(0);
            for (Object[] h : hoaDons) {
                moHinhHoaDon.addRow(new Object[] {h[1], h[2], h[3], tien(h[4])});
            }
            if (dl.chonHoaDon >= 0) {
                hoaDonBang.setRowSelectionInterval(dl.chonHoaDon, dl.chonHoaDon);
            }
            for (int i = 0; i < dongs.size(); i++) {
                Object[] d = dongs.get(i);
                moHinhDong.addRow(
                        new Object[] {d[1], d[2], d[3], d[4], so(d[3]) - so(d[4]), tien(d[5])});
                if (String.valueOf(d[0]).equals(nhap.dongGoc())) {
                    dongBang.setRowSelectionInterval(i, i);
                }
            }
            for (int i = 0; i < hangs.size(); i++) {
                Object[] h = hangs.get(i);
                moHinhHang.addRow(new Object[] {h[0], h[1], tien(h[2]), h[3]});
                if (h[0].toString().equals(nhap.maThayThe())) {
                    hangBang.setRowSelectionInterval(i, i);
                }
            }
            tem.setSelected(nhap.nguyenTem());
            chuaDung.setSelected(nhap.chuaDung());
            trangThai.setText(
                    "Đã mở đơn đổi treo. Chọn hàng và kiểm tra lại chênh lệch trước khi thanh"
                            + " toán.");
        } finally {
            dangKhoiPhuc = false;
        }
        capNhatGioiHanSoLuongDoi();
        voHieuGia();
    }

    // 5. TÌM KIẾM, TÍNH CHÊNH LỆCH & CHUYỂN THANH TOÁN

    private void timHoaDon() {
        if (dangChay) return;
        voHieuGia();
        hoaDons = List.of();
        dongs = List.of();
        moHinhHoaDon.setRowCount(0);
        moHinhDong.setRowCount(0);
        String tuKhoa = timHoaDon.getText();
        chayNen(
                () -> doiTraService.timHoaDon(tuKhoa),
                ds -> {
                    hoaDons = ds;
                    for (Object[] h : ds)
                        moHinhHoaDon.addRow(new Object[] {h[1], h[2], h[3], tien(h[4])});
                    trangThai.setText(
                            ds.isEmpty()
                                    ? "Không tìm thấy hóa đơn đã thanh toán."
                                    : "Chọn hóa đơn để xem hàng đã mua.");
                    if (!ds.isEmpty()) hoaDonBang.setRowSelectionInterval(0, 0);
                });
    }

    private void chonHoaDon() {
        voHieuGia();
        dongs = List.of();
        moHinhDong.setRowCount(0);
        int i = hoaDonBang.getSelectedRow();
        if (i < 0) return;
        String maHoaDon = String.valueOf(hoaDons.get(i)[0]);
        chayNen(
                () -> doiTraService.layDongHoaDon(maHoaDon),
                ds -> {
                    dongs = ds;
                    for (Object[] d : ds)
                        moHinhDong.addRow(
                                new Object[] {
                                    d[1], d[2], d[3], d[4], so(d[3]) - so(d[4]), tien(d[5])
                                });
                    trangThai.setText(
                            "Chọn hàng và xác nhận tình trạng thực tế. Hạn đổi: "
                                    + (soNgayHanDoi * 24)
                                    + " giờ từ lúc thanh toán.");
                });
    }

    private void timPhuKien() {
        if (dangChay) return;
        voHieuGia();
        hangs = List.of();
        moHinhHang.setRowCount(0);
        String tuKhoa = timHang.getText();
        chayNen(
                () -> doiTraService.timPhuKien(tuKhoa),
                ds -> {
                    hangs = ds;
                    for (Object[] h : ds)
                        moHinhHang.addRow(new Object[] {h[0], h[1], tien(h[2]), h[3]});
                    if (ds.isEmpty()) trangThai.setText("Không tìm thấy phụ kiện đang kinh doanh.");
                });
    }

    private void chuyenThanhToan() {
        if (dangChay || giaTriCu < 0) return;
        try {
            soLuong.commitEdit();
            soLuongMoi.commitEdit();
            kiemTraTinhTrang();
            if (lyDo.getText().isBlank())
                throw new IllegalArgumentException("Vui lòng nhập lý do đổi.");
            if (!gio.kiemTraTrong()) {
                if (HopThoai.xacNhanDongY(
                        this,
                        "Giỏ hàng bán lẻ hiện tại đang có sản phẩm chưa thanh toán.\n"
                                + "Bạn có muốn làm trống giỏ hàng để chuyển sang giỏ đổi hàng"
                                + " ngay không?",
                        "Giỏ hàng đang có sản phẩm")) {
                    gio.xoa();
                } else {
                    return;
                }
            }
            String dong = String.valueOf(dongChon()[0]);
            String ma = hangChon()[0].toString();
            int sl = (Integer) soLuong.getValue();
            int slMoi = (Integer) soLuongMoi.getValue();
            String xacNhan =
                    " | Đã kiểm tra: nguyên tem mác, bao bì; chưa sử dụng, không rách bẩn.";
            String ghiChu =
                    lyDo.getText().trim().endsWith(xacNhan)
                            ? lyDo.getText().trim()
                            : lyDo.getText().trim() + xacNhan;
            chayNen(
                    () -> {
                        donDoiTreoService.chuyenThanhToan(
                                maDonTreo, gio, dong, sl, slMoi, ma, ghiChu);
                        return true;
                    },
                    ok -> {
                        xuLyXong = true;
                        dispose();
                        dieuHuong.accept(MaManHinh.THANH_TOAN);
                    });
        } catch (Exception e) {
            baoLoi(e);
        }
    }

    private void kiemTraTinhTrang() {
        if (!tem.isSelected() || !chuaDung.isSelected())
            throw new IllegalArgumentException(
                    "Chỉ nhận hàng nguyên tem mác, bao bì, chưa sử dụng và không rách bẩn.");
    }

    private Object[] dongChon() {
        int i = dongBang.getSelectedRow();
        if (i < 0) throw new IllegalArgumentException("Chọn hàng trên hóa đơn gốc.");
        return dongs.get(i);
    }

    private Object[] hangChon() {
        int i = hangBang.getSelectedRow();
        if (i < 0) throw new IllegalArgumentException("Chọn phụ kiện thay thế.");
        return hangs.get(i);
    }

    private void voHieuGia() {
        phien++;
        giaTriCu = -1;
        tiepTuc.setEnabled(false);
        capNhatChenhLechTuDong();
    }

    private void capNhatChenhLechTuDong() {
        if (dangChay || dangKhoiPhuc) return;
        int dongChonIdx = dongBang.getSelectedRow();
        int hangChonIdx = hangBang.getSelectedRow();
        if (dongChonIdx < 0
                || dongChonIdx >= dongs.size()
                || hangChonIdx < 0
                || hangChonIdx >= hangs.size()) {
            tongKet.setText("Chọn hàng cần đổi và phụ kiện thay thế để tính chênh lệch.");
            return;
        }
        try {
            soLuong.commitEdit();
            soLuongMoi.commitEdit();
        } catch (ParseException ignored) {
            tongKet.setText("Số lượng chưa hợp lệ.");
            return;
        }
        Object[] cu = dongs.get(dongChonIdx);
        Object[] moi = hangs.get(hangChonIdx);
        int sl = (Integer) soLuong.getValue();
        int slMoi = (Integer) soLuongMoi.getValue();
        long conDoi = so(cu[3]) - so(cu[4]);
        if (sl <= 0 || sl > conDoi) {
            tongKet.setText("Số lượng đổi vượt phần còn đổi (" + conDoi + ").");
            trangThai.setText("Điều chỉnh số lượng đổi trong giới hạn còn lại.");
            return;
        }
        if (slMoi <= 0 || slMoi > so(moi[3])) {
            tongKet.setText("Phụ kiện thay thế không đủ tồn (" + so(moi[3]) + ").");
            trangThai.setText("Chọn số lượng thay thế không vượt tồn khả dụng.");
            return;
        }
        String maDong = String.valueOf(cu[0]);
        long giaMoi = so(moi[2]);
        long luot = ++phien;
        var phienDangNhap = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chay(
                () ->
                        PhienDangNhapService.macDinh()
                                .trongPhien(
                                        phienDangNhap,
                                        () -> doiTraService.layGiaTriDuocDoi(maDong, sl)),
                cuTien -> {
                    if (luot == phien) {
                        hienThiKetQuaChenhLech(cuTien, giaMoi, slMoi);
                    }
                },
                loi -> {
                    if (luot == phien) {
                        trangThai.setText(TacVuNen.thongDiepLoi(loi));
                        tiepTuc.setEnabled(false);
                    }
                });
    }

    private void hienThiKetQuaChenhLech(long cuTien, long giaMoi, int slMoi) {
        long moiTien = Math.multiplyExact(giaMoi, slMoi);
        long chenhlech = moiTien - cuTien;
        if (moiTien < cuTien) {
            giaTriCu = -1;
            tongKet.setText(
                    "Hàng cũ: "
                            + tien(cuTien)
                            + "    Hàng mới: "
                            + tien(moiTien)
                            + "    (Thiếu "
                            + tien(cuTien - moiTien)
                            + ")");
            trangThai.setText("Hàng thay thế phải có tổng tiền bằng hoặc cao hơn hàng cũ.");
            tiepTuc.setEnabled(false);
            return;
        }
        giaTriCu = cuTien;
        tongKet.setText(
                "Hàng cũ: "
                        + tien(cuTien)
                        + "    Hàng mới: "
                        + tien(moiTien)
                        + "    Cần thu: "
                        + tien(chenhlech)
                        + "    Điểm thêm: "
                        + DoiTraService.tinhDiemChenhLech(chenhlech));
        boolean duDieuKien = tem.isSelected() && chuaDung.isSelected();
        trangThai.setText(
                duDieuKien
                        ? "Đủ điều kiện. Bấm \"Sang thanh toán\" để hoàn tất."
                        : "Tích đủ 2 cam kết tình trạng hàng (tem mác & chưa sử dụng)"
                                + " để tiếp tục.");
        tiepTuc.setEnabled(duDieuKien);
    }

    private void huy() {
        if (dangChay) return;
        boolean coDuLieuDangNhap =
                dongBang.getSelectedRow() >= 0
                        || hangBang.getSelectedRow() >= 0
                        || !lyDo.getText().isBlank()
                        || tem.isSelected()
                        || chuaDung.isSelected();
        if (coDuLieuDangNhap) {
            String[] luaChon = {"Lưu phiếu đổi nháp", "Bỏ thay đổi", "Tiếp tục nhập"};
            int chon =
                    HopThoai.tuyChon(
                            this,
                            !maDonTreo.isBlank()
                                    ? "Lưu thay đổi vào đơn treo trước khi đóng? Bản đã lưu vẫn"
                                            + " được giữ."
                                    : "Bạn muốn treo đơn để tiếp tục sau?",
                            "Đóng đơn đổi",
                            luaChon,
                            true);
            if (chon == 0) {
                treoDon();
                return;
            }
            if (chon != 1) return;
        }
        dispose();
    }

    // 6. TÁC VỤ NỀN & TIỆN ÍCH TẠO THÀNH PHẦN

    private <T> void chayNen(Callable<T> viec, Consumer<T> xong) {
        if (dangChay) return;
        dangChay = true;
        tiepTuc.setEnabled(false);
        khoa(getContentPane(), false);
        var phienDangNhap = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chay(
                () -> PhienDangNhapService.macDinh().trongPhien(phienDangNhap, viec),
                kq -> {
                    dangChay = false;
                    khoa(getContentPane(), true);
                    tiepTuc.setEnabled(false);
                    xong.accept(kq);
                },
                loi -> {
                    dangChay = false;
                    khoa(getContentPane(), true);
                    tiepTuc.setEnabled(false);
                    baoLoi(loi);
                });
    }

    private static void khoa(Container c, boolean mo) {
        for (Component v : c.getComponents()) {
            v.setEnabled(mo);
            if (v instanceof Container con) khoa(con, mo);
        }
    }

    private void baoLoi(Throwable loi) {
        String thongDiep = TacVuNen.thongDiepLoi(loi);
        trangThai.setText(thongDiep);
        HopThoai.hienThiCanhBao(this, thongDiep);
    }

    private static long so(Object v) {
        return ((Number) v).longValue();
    }

    private static String tien(Object v) {
        return DinhDang.dinhDang(so(v));
    }

    private static DefaultTableModel model(String... cot) {
        return new DefaultTableModel(cot, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private static JTable bang(DefaultTableModel m) {
        JTable t = new JTable(m);
        DinhDangBang.apDung(t);
        t.setAutoCreateRowSorter(false);
        t.setRowSorter(null);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        for (int i = 0; i < t.getColumnCount(); i++) {
            String ten = t.getColumnName(i);
            int rong =
                    ten.equals("Sản phẩm") || ten.equals("Tên phụ kiện")
                            ? 300
                            : ten.equals("Đã mua")
                                            || ten.equals("Đã đổi")
                                            || ten.equals("Số lượng còn được đổi")
                                    ? 65
                                    : 150;
            t.getColumnModel().getColumn(i).setPreferredWidth(rong);
        }
        return t;
    }

    private static JScrollPane cuon(JTable bang, int cao) {
        JScrollPane s = new JScrollPane(bang);
        s.setPreferredSize(new Dimension(540, cao));
        s.setMinimumSize(new Dimension(240, cao));
        s.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        s.getViewport().setBackground(MauUngDung.THE_NEN);
        s.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return s;
    }

    private static JPanel hang(Component... cac) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        for (Component c : cac) p.add(c);
        return p;
    }

    private static JPanel nhom(String ten, Component... cac) {
        JPanel p = new BoGocPanel(MauUngDung.BAN_KINH_THE, MauUngDung.THE_NEN, true);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JLabel nhan = CoSoPanel.taoTieuDeMuc(ten);
        nhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        nhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, nhan.getPreferredSize().height + 4));
        p.add(nhan);
        for (Component c : cac) {
            p.add(Box.createVerticalStrut(6));
            if (c instanceof JScrollPane cuonBang) {
                cuonBang.setAlignmentX(Component.LEFT_ALIGNMENT);
                cuonBang.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            } else if (c instanceof JComponent j) {
                j.setAlignmentX(Component.LEFT_ALIGNMENT);
                j.setMaximumSize(new Dimension(Integer.MAX_VALUE, j.getPreferredSize().height + 2));
            }
            p.add(c);
        }
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private static JPanel thanhTim(JTextField truong, JButton nut) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.add(truong, BorderLayout.CENTER);
        p.add(nut, BorderLayout.EAST);
        return p;
    }

    private static JLabel goiY(String noiDung) {
        JLabel nhan = new JLabel(noiDung);
        nhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhan.setForeground(MauUngDung.VAN_BAN_XAM);
        return nhan;
    }

    private static JButton nut(String ten, Runnable viec) {
        JButton b = new NutDinhDang(ten, NutDinhDang.KieuNut.PHU);
        b.addActionListener(e -> viec.run());
        return b;
    }
}
