package com.petstation.gui.search;

import com.petstation.gui.components.*;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.*;
import com.petstation.service.QuyTrinhService;
import com.petstation.util.TienTe;

import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;

/** Sơ đồ phòng dùng ID ổn định, thông tin hover được nạp cùng dữ liệu phòng. */
public final class TraCuuPhongHotelPanel extends CoSoPanel {
    private final GioHang gio;
    private final Consumer<MaManHinh> dieuHuong;
    private final JPanel pnlPhong = new JPanel(new GridLayout(0, 3, 14, 14));
    private final JTextField txtLoc = new JTextField(16);
    private final JComboBox<String> cboLoai = new JComboBox<>();
    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(
                    new String[] {
                        "Tất cả", "Trống", "Có thú", "Đang dọn", "Bảo trì", "Ngừng hoạt động"
                    });
    private final JLabel lblKetQua = new JLabel();
    private List<ThongTinPhong> cacPhong = List.of();
    private boolean dangNap;
    private JWindow chiTiet;
    private javax.swing.Timer choHienThi;

    public TraCuuPhongHotelPanel(GioHang gio, Consumer<MaManHinh> dieuHuong) {
        this.gio = gio;
        this.dieuHuong = dieuHuong;
        setLayout(new BorderLayout(0, 14));
        pnlPhong.setOpaque(false);
        JPanel dau = taoVungTrongSuot(new BorderLayout(0, 12));
        dau.add(taoTieuDeMuc("Sơ đồ phòng lưu trú"), BorderLayout.NORTH);
        JPanel loc = taoVungTrongSuot(new FlowLayout(FlowLayout.LEFT, 8, 2));
        txtLoc.setName("timPhong");
        txtLoc.setPreferredSize(new Dimension(190, 34));
        cboLoai.setName("loaiPhong");
        cboTrangThai.setName("trangThaiPhong");
        loc.add(new JLabel("Mã phòng:"));
        loc.add(txtLoc);
        loc.add(new JLabel("Loại:"));
        loc.add(cboLoai);
        loc.add(new JLabel("Trạng thái:"));
        loc.add(cboTrangThai);
        JButton xoa = new JButton("Xóa bộ lọc");
        xoa.addActionListener(
                e -> {
                    txtLoc.setText("");
                    cboLoai.setSelectedIndex(0);
                    cboTrangThai.setSelectedIndex(0);
                    loc();
                });
        loc.add(xoa);
        JButton moi = new JButton("Làm mới");
        moi.setToolTipText("Đọc lại trạng thái phòng từ SQL Server");
        moi.addActionListener(e -> nap());
        loc.add(moi);
        JButton in = new JButton("In sơ đồ");
        in.addActionListener(e -> inSoDo());
        loc.add(in);
        loc.add(lblKetQua);
        dau.add(loc, BorderLayout.SOUTH);
        add(dau, BorderLayout.NORTH);
        JPanel vung = taoVungTrongSuot(new BorderLayout());
        vung.add(pnlPhong, BorderLayout.NORTH);
        JScrollPane cuon = new JScrollPane(vung);
        cuon.setBorder(null);
        cuon.getViewport().setBackground(MauUngDung.NEN_KEM);
        add(cuon, BorderLayout.CENTER);
        add(
                new JLabel(
                        "Rê chuột hoặc dùng Tab để xem chi tiết. Bấm phòng để mở thao tác phù hợp"
                            + " với trạng thái."),
                BorderLayout.SOUTH);
        txtLoc.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                loc();
                            }

                            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                loc();
                            }

                            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                loc();
                            }
                        });
        cboLoai.addActionListener(
                e -> {
                    if (!dangNap) loc();
                });
        cboTrangThai.addActionListener(e -> loc());
        cuon.getViewport()
                .addComponentListener(
                        new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                int cot =
                                        Math.max(
                                                1,
                                                Math.min(5, cuon.getViewport().getWidth() / 265));
                                GridLayout luoi = (GridLayout) pnlPhong.getLayout();
                                if (luoi.getColumns() != cot) {
                                    luoi.setColumns(cot);
                                    pnlPhong.revalidate();
                                }
                            }
                        });
        addHierarchyListener(
                e -> {
                    if (!isShowing()) anChiTiet();
                });
        nap();
    }

    private void nap() {
        anChiTiet();
        cacPhong = QuyTrinhService.layThongTinPhong();
        Object daChon = cboLoai.getSelectedItem();
        dangNap = true;
        cboLoai.removeAllItems();
        cboLoai.addItem("Tất cả");
        cacPhong.stream().map(ThongTinPhong::loai).distinct().sorted().forEach(cboLoai::addItem);
        if (daChon != null) cboLoai.setSelectedItem(daChon);
        dangNap = false;
        loc();
    }

    private void loc() {
        anChiTiet();
        pnlPhong.removeAll();
        String tu = txtLoc.getText().trim().toLowerCase(Locale.ROOT);
        int so = 0;
        for (var p : cacPhong) {
            if (!p.ma().toLowerCase(Locale.ROOT).contains(tu)) continue;
            if (cboLoai.getSelectedIndex() > 0 && !p.loai().equals(cboLoai.getSelectedItem()))
                continue;
            if (cboTrangThai.getSelectedIndex() > 0
                    && !p.layNhanTrangThai().equals(cboTrangThai.getSelectedItem())) continue;
            pnlPhong.add(taoThe(p));
            so++;
        }
        lblKetQua.setText(so + " kết quả");
        pnlPhong.revalidate();
        pnlPhong.repaint();
    }

    private JButton taoThe(ThongTinPhong phong) {
        JButton the = new JButton();
        the.setName("phong:" + phong.id());
        the.setLayout(new BorderLayout(0, 10));
        the.setBackground(Color.WHITE);
        the.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        BorderFactory.createEmptyBorder(15, 16, 15, 16)));
        the.setPreferredSize(new Dimension(250, 165));
        the.setHorizontalAlignment(SwingConstants.LEFT);
        JPanel dau = taoVungTrongSuot(new BorderLayout(8, 0));
        JLabel ma = new JLabel(phong.ma());
        ma.setFont(MauUngDung.taoPhongChu(Font.BOLD, 20));
        ma.setForeground(MauUngDung.NAU);
        dau.add(ma, BorderLayout.WEST);
        JLabel trangThai = new JLabel(phong.layNhanTrangThai());
        Color mau = mauTrangThai(phong.trangThai());
        trangThai.setForeground(mau);
        trangThai.setOpaque(true);
        trangThai.setBackground(
                new Color(
                        (mau.getRed() + 9 * 255) / 10,
                        (mau.getGreen() + 9 * 255) / 10,
                        (mau.getBlue() + 9 * 255) / 10));
        trangThai.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        trangThai.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        dau.add(trangThai, BorderLayout.EAST);
        the.add(dau, BorderLayout.NORTH);
        String thu =
                phong.dangO() == null
                        ? "Sức chứa: " + phong.sucChua() + " thú cưng"
                        : "Đang ở: " + phong.dangO().thuCung();
        JLabel thongTin =
                new JLabel(
                        "<html>"
                                + html(phong.loai())
                                + "<br>"
                                + html(thu)
                                + "<br>Đặt trước: "
                                + phong.datTruoc().size()
                                + "</html>");
        the.add(thongTin, BorderLayout.CENTER);
        JLabel gia = new JLabel(TienTe.dinhDang(phong.gia()) + " / ngày");
        gia.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        gia.setForeground(MauUngDung.NAU);
        the.add(gia, BorderLayout.SOUTH);
        MouseAdapter chuot =
                new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        henChiTiet(the, phong);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        Point viTri =
                                SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), the);
                        if (!the.contains(viTri)) anChiTiet();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getComponent() != the && SwingUtilities.isLeftMouseButton(e))
                            the.doClick();
                    }
                };
        ganChuotChoNoiDung(the, chuot);
        the.addMouseListener(chuot);
        the.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        henChiTiet(the, phong);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        anChiTiet();
                    }
                });
        the.addActionListener(
                e -> {
                    anChiTiet();
                    moPhong(phong);
                });
        return the;
    }

    /** Nhãn bên trong thẻ cũng nhận thao tác như toàn bộ nút phòng. */
    private static void ganChuotChoNoiDung(Container cha, MouseAdapter chuot) {
        for (Component con : cha.getComponents()) {
            con.addMouseListener(chuot);
            if (con instanceof Container vung) ganChuotChoNoiDung(vung, chuot);
        }
    }

    public void moPhong(ThongTinPhong phong) {
        if (!List.of("AVAILABLE", "OCCUPIED").contains(phong.trangThai())) {
            HopThoai.hienThiThongTin(
                    this,
                    "Phòng "
                            + phong.ma()
                            + " đang "
                            + phong.layNhanTrangThai().toLowerCase(Locale.ROOT)
                            + ". Chưa thể đặt hoặc nhận phòng.");
            return;
        }
        gio.layQuyTrinh().chonPhongHotel(phong.id(), phong.ma());
        if (phong.trangThai().equals("OCCUPIED")) {
            if (phong.dangO() == null) {
                HopThoai.hienThiCanhBao(this, "Chưa tìm thấy hợp đồng đang ở. Hãy làm mới sơ đồ.");
                return;
            }
            gio.layQuyTrinh().chonHotel(phong.dangO().id());
            dieuHuong.accept(MaManHinh.HOTEL_JOURNAL);
        } else {
            gio.layQuyTrinh().chonHotel(0);
            dieuHuong.accept(MaManHinh.HOTEL_BOOKING);
        }
    }

    private void henChiTiet(JComponent the, ThongTinPhong phong) {
        anChiTiet();
        choHienThi = new javax.swing.Timer(300, e -> hienChiTiet(the, phong));
        choHienThi.setRepeats(false);
        choHienThi.start();
    }

    private void anChiTiet() {
        if (choHienThi != null) choHienThi.stop();
        if (chiTiet != null) {
            chiTiet.dispose();
            chiTiet = null;
        }
    }

    private void hienChiTiet(JComponent the, ThongTinPhong phong) {
        if (!the.isShowing()) return;
        chiTiet = new JWindow(SwingUtilities.getWindowAncestor(this));
        JLabel noiDung = new JLabel(layChiTiet(phong));
        noiDung.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        noiDung.setOpaque(true);
        noiDung.setBackground(MauUngDung.NEN_KEM);
        chiTiet.add(noiDung);
        chiTiet.pack();
        Rectangle manHinh = the.getGraphicsConfiguration().getBounds();
        Insets le = Toolkit.getDefaultToolkit().getScreenInsets(the.getGraphicsConfiguration());
        manHinh =
                new Rectangle(
                        manHinh.x + le.left,
                        manHinh.y + le.top,
                        manHinh.width - le.left - le.right,
                        manHinh.height - le.top - le.bottom);
        Point viTri = the.getLocationOnScreen();
        int x = viTri.x + the.getWidth() + 8;
        if (x + chiTiet.getWidth() > manHinh.x + manHinh.width)
            x = viTri.x - chiTiet.getWidth() - 8;
        int y =
                Math.max(
                        manHinh.y,
                        Math.min(viTri.y, manHinh.y + manHinh.height - chiTiet.getHeight()));
        chiTiet.setLocation(Math.max(manHinh.x, x), y);
        chiTiet.setFocusableWindowState(false);
        chiTiet.setVisible(true);
    }

    public static String layChiTiet(ThongTinPhong phong) {
        StringBuilder s =
                new StringBuilder(
                        "<html><body style='width:270px'><b>"
                                + html(phong.ma())
                                + " — "
                                + html(phong.loai())
                                + "</b><br>"
                                + phong.layNhanTrangThai()
                                + "<br>Sức chứa: "
                                + phong.sucChua()
                                + "<br>Giá: "
                                + TienTe.dinhDang(phong.gia())
                                + " / ngày");
        if (phong.dangO() != null)
            s.append("<hr><b>Đang lưu trú</b><br>").append(layLich(phong.dangO()));
        if (!phong.datTruoc().isEmpty())
            s.append("<hr><b>Lịch đặt gần nhất (tổng ")
                    .append(phong.datTruoc().size())
                    .append(")</b><br>")
                    .append(layLich(phong.datTruoc().get(0)));
        return s.append("</body></html>").toString();
    }

    private static String layLich(ThongTinPhong.LichPhong lich) {
        var d = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "Thú cưng: "
                + html(lich.thuCung())
                + "<br>Chủ nuôi: "
                + html(lich.khachHang())
                + "<br>Vào: "
                + lich.ngayVao().format(d)
                + "<br>Ra dự kiến: "
                + lich.ngayRa().format(d);
    }

    private static String html(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static Color mauTrangThai(String s) {
        return switch (s) {
            case "AVAILABLE" -> MauUngDung.THANH_CONG;
            case "OCCUPIED" -> MauUngDung.DO_THUONG_HIEU;
            case "CLEANING" -> MauUngDung.CANH_BAO;
            default -> MauUngDung.VAN_BAN_XAM;
        };
    }

    private void inSoDo() {
        var congViec = java.awt.print.PrinterJob.getPrinterJob();
        congViec.setJobName("Sơ đồ phòng Pet Station");
        congViec.setPrintable(
                (g, khung, trang) -> {
                    double tiLe =
                            Math.min(
                                    1,
                                    khung.getImageableWidth() / Math.max(1, pnlPhong.getWidth()));
                    double cao = khung.getImageableHeight() / tiLe;
                    if (trang * cao >= pnlPhong.getHeight())
                        return java.awt.print.Printable.NO_SUCH_PAGE;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.translate(khung.getImageableX(), khung.getImageableY());
                    g2.scale(tiLe, tiLe);
                    g2.translate(0, -trang * cao);
                    pnlPhong.printAll(g2);
                    g2.dispose();
                    return java.awt.print.Printable.PAGE_EXISTS;
                });
        if (congViec.printDialog())
            try {
                congViec.print();
            } catch (java.awt.print.PrinterException loi) {
                com.petstation.gui.components.HopThoai.hienThiLoi(this, loi.getMessage());
            }
    }

    @Override
    public void xuLyHienThi() {
        nap();
    }
}
