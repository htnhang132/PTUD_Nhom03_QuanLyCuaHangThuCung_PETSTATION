package com.petstation.gui.luutru;

import com.petstation.dto.GioHang;
import com.petstation.dto.HotelDTO;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiPhongHotel;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.service.chung.QuyTrinhService;
import com.petstation.service.luutru.LuuTruService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

// Màn hình Tìm kiếm > Sơ đồ phòng lưu trú
// Màn hình: Sơ đồ phòng lưu trú (Mã: TRA_CUU_PHONG_HOTEL)
// Luồng xử lý: GUI -> QuyTrinhService, LuuTruService -> PhongHotelDAOImpl -> SQL (phong_hotel,
// hop_dong_luu_tru)
// Mô tả: Hiển thị trực quan sơ đồ phòng, trạng thái trống/đang ở và thông tin đặt trước
// Phân hệ: luutru
public final class TraCuuPhongHotelPanel extends CoSoPanel {

    // 1. KHAI BÁO CÁC THÀNH PHẦN GIAO DIỆN & TRẠNG THÁI SƠ ĐỒ PHÒNG

    private final GioHang gio;
    private final Consumer<MaManHinh> dieuHuong;
    private final Consumer<PhongHotel> khiChonPhong;
    private final QuyTrinhService quyTrinhService = new QuyTrinhService();
    private final JPanel khungPhong = new JPanel(new GridLayout(0, 3, 14, 14));
    private final JTextField oLoc = new JTextField(16);
    private final JComboBox<String> cboLoai = new JComboBox<>();
    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(
                    new String[] {
                        "Tất cả", "Trống", "Có thú", "Đang dọn", "Bảo trì", "Ngừng hoạt động"
                    });
    private final JLabel nhanKetQua = new JLabel();
    private List<PhongHotel> cacPhong = List.of();
    private boolean dangNap;
    private int luotTai;
    private JWindow chiTiet;
    private Timer choHienThi;

    // 2. KHỞI TẠO GIAO DIỆN, THANH BỘ LỌC & LƯỚI SƠ ĐỒ PHÒNG TỰ CO GIÃN

    public TraCuuPhongHotelPanel(GioHang gio, Consumer<MaManHinh> dieuHuong) {
        this(gio, dieuHuong, null);
    }

    public TraCuuPhongHotelPanel(
            GioHang gio, Consumer<MaManHinh> dieuHuong, Consumer<PhongHotel> khiChonPhong) {
        this.gio = gio;
        this.dieuHuong = dieuHuong;
        this.khiChonPhong = khiChonPhong;
        setLayout(new BorderLayout(0, 14));
        khungPhong.setOpaque(false);
        add(taoPhanDauSoDo(), BorderLayout.NORTH);
        JScrollPane cuon = taoVungCuonSoDo();
        add(cuon, BorderLayout.CENTER);
        add(taoNhanHuongDanSoDo(), BorderLayout.SOUTH);
        ganSuKienBoLocVaCoGian(cuon);
        nap();
    }

    private JPanel taoPhanDauSoDo() {
        JPanel dau = taoVungTrongSuot(new BorderLayout(0, 12));
        dau.add(
                ThanhDanhSach.tieuDe(taoTieuDeManHinh("Sơ đồ phòng lưu trú"), nhanKetQua),
                BorderLayout.NORTH);
        ThanhDanhSach loc = new ThanhDanhSach();
        oLoc.setName("timPhong");
        oLoc.putClientProperty("JTextField.placeholderText", "Tìm mã phòng...");
        oLoc.setPreferredSize(new Dimension(170, 34));
        cboLoai.setName("loaiPhong");
        cboLoai.setPreferredSize(new Dimension(130, 34));
        cboTrangThai.setName("trangThaiPhong");
        cboTrangThai.setPreferredSize(new Dimension(130, 34));
        loc.add(oLoc);
        loc.themBoLoc("Loại:", cboLoai);
        loc.themBoLoc("Trạng thái:", cboTrangThai);
        loc.themBoLoc(
                taoNutThanhSoDo(
                        "Xóa bộ lọc", 95, NutDinhDang.KieuNut.TRONG_SUOT, null, e -> xoaBoLoc()));
        loc.add(taoNutThanhSoDo("In sơ đồ", 90, NutDinhDang.KieuNut.PHU, null, e -> inSoDo()));
        loc.add(
                taoNutThanhSoDo(
                        "+ Đặt phòng",
                        115,
                        NutDinhDang.KieuNut.CHINH,
                        "Mở hộp thoại đặt phòng Pet Hotel mới",
                        e -> {
                            DatPhongHotelDialog.mo(this, null, null);
                            nap();
                        }));
        dau.add(loc, BorderLayout.SOUTH);
        return dau;
    }

    private static JButton taoNutThanhSoDo(
            String nhan, int rong, NutDinhDang.KieuNut kieu, String goiY, ActionListener hanhDong) {
        JButton nut = new NutDinhDang(nhan, kieu);
        nut.setPreferredSize(new Dimension(rong, 34));
        if (goiY != null) nut.setToolTipText(goiY);
        nut.addActionListener(hanhDong);
        return nut;
    }

    private void xoaBoLoc() {
        oLoc.setText("");
        cboLoai.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        loc();
    }

    private JScrollPane taoVungCuonSoDo() {
        JPanel vung = taoVungTrongSuot(new BorderLayout());
        vung.setBorder(BorderFactory.createEmptyBorder(4, 4, 16, 4));
        vung.add(khungPhong, BorderLayout.NORTH);
        JScrollPane cuon = new JScrollPane(vung);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getVerticalScrollBar().setUnitIncrement(16);
        cuon.getViewport().setBackground(MauUngDung.NEN_KEM);
        return cuon;
    }

    private static JLabel taoNhanHuongDanSoDo() {
        JLabel nhanHuongDan =
                new JLabel(
                        "Rê chuột hoặc dùng Tab để xem chi tiết. Bấm phòng để mở thao tác phù hợp"
                                + " với trạng thái.");
        nhanHuongDan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanHuongDan.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanHuongDan.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return nhanHuongDan;
    }

    private void ganSuKienBoLocVaCoGian(JScrollPane cuon) {
        themBoLangNgheTaiLieu(oLoc, this::loc);
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
                                GridLayout luoi = (GridLayout) khungPhong.getLayout();
                                if (luoi.getColumns() != cot) {
                                    luoi.setColumns(cot);
                                    khungPhong.revalidate();
                                }
                            }
                        });
        addHierarchyListener(
                e -> {
                    if (!isShowing()) anChiTiet();
                });
    }

    // 3. NẠP DỮ LIỆU PHÒNG TỪ SERVICE & LỌC THEO MÃ / LOẠI / TRẠNG THÁI

    private void nap() {
        anChiTiet();
        int luot = ++luotTai;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                quyTrinhService::layPhongHotel,
                dsPhong -> {
                    cacPhong = dsPhong;
                    Object daChon = cboLoai.getSelectedItem();
                    dangNap = true;
                    cboLoai.removeAllItems();
                    cboLoai.addItem("Tất cả");
                    cacPhong.stream()
                            .map(PhongHotel::getLoaiPhong)
                            .distinct()
                            .sorted()
                            .forEach(cboLoai::addItem);
                    if (daChon != null) cboLoai.setSelectedItem(daChon);
                    dangNap = false;
                    loc();
                });
    }

    private void loc() {
        anChiTiet();
        khungPhong.removeAll();
        String tu = oLoc.getText().trim().toLowerCase(Locale.ROOT);
        int so = 0;
        for (var p : cacPhong) {
            if (!p.getMaPhong().toLowerCase(Locale.ROOT).contains(tu)) continue;
            if (cboLoai.getSelectedIndex() > 0
                    && !p.getLoaiPhong().equals(cboLoai.getSelectedItem())) continue;
            if (cboTrangThai.getSelectedIndex() > 0
                    && !p.getNhanTrangThai().equals(cboTrangThai.getSelectedItem())) continue;
            khungPhong.add(taoThe(p));
            so++;
        }
        nhanKetQua.setText(ThanhDanhSach.soKetQua(so, cacPhong.size()));
        khungPhong.revalidate();
        khungPhong.repaint();
    }

    public void lamMoiSoDo() {
        nap();
    }

    @Override
    public void xuLyHienThi() {
        nap();
    }

    // 4. DỰNG THẺ PHÒNG (ROOM CARD) & MÀU SẮC THEO TRẠNG THÁI

    private JButton taoThe(PhongHotel phong) {
        JButton the = new JButton();
        the.setName("phong:" + phong.getMaPhong());
        the.setLayout(new BorderLayout(0, 10));
        the.setBackground(Color.WHITE);
        Color mauNhan = mauTrangThai(phong.getTrangThai());
        the.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(MauUngDung.mau(0xE6E1DA), 1, true),
                                BorderFactory.createMatteBorder(0, 4, 0, 0, mauNhan)),
                        BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        the.setPreferredSize(new Dimension(250, 165));
        the.setHorizontalAlignment(SwingConstants.LEFT);
        the.add(taoPhanDauThePhong(phong, mauNhan), BorderLayout.NORTH);
        the.add(taoThongTinThePhong(phong), BorderLayout.CENTER);
        JLabel gia = new JLabel(DinhDang.dinhDang(phong.getGiaDem()) + " / ngày");
        gia.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        gia.setForeground(MauUngDung.NAU);
        the.add(gia, BorderLayout.SOUTH);
        ganTuongTacThePhong(the, phong);
        return the;
    }

    private JPanel taoPhanDauThePhong(PhongHotel phong, Color mauNhan) {
        JPanel dau = taoVungTrongSuot(new BorderLayout(8, 0));
        JLabel ma = new JLabel(phong.getMaPhong());
        ma.setFont(MauUngDung.taoPhongChu(Font.BOLD, 20));
        ma.setForeground(MauUngDung.NAU);
        dau.add(ma, BorderLayout.WEST);
        JLabel trangThai = new JLabel(phong.getNhanTrangThai());
        trangThai.setForeground(mauNhan);
        trangThai.setOpaque(true);
        trangThai.setBackground(MauUngDung.phaTrang90(mauNhan));
        trangThai.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        trangThai.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        dau.add(trangThai, BorderLayout.EAST);
        return dau;
    }

    private static JLabel taoThongTinThePhong(PhongHotel phong) {
        String tenThuCung =
                phong.getDangO() != null
                                && phong.getDangO().getThuCung() != null
                                && phong.getDangO().getThuCung().getTen() != null
                        ? phong.getDangO().getThuCung().getTen()
                        : "";
        String thu =
                phong.getDangO() != null
                        ? "<span style='color:#B91C1C; font-weight:bold;'>🐾 Đang ở: "
                                + html(tenThuCung)
                                + "</span>"
                        : "<span style='color:#57534E;'>Sức chứa: "
                                + phong.getSucChua()
                                + " thú cưng</span>";
        return new JLabel(
                "<html><div style='font-family:Segoe UI, sans-serif; font-size:12px;"
                        + " line-height:1.45;'><b style='color:#292524;'>"
                        + html(phong.getLoaiPhong())
                        + "</b><br>"
                        + thu
                        + "<br>"
                        + "<span style='color:#78716C;'>Đặt trước: <b>"
                        + phong.getDatTruoc().size()
                        + "</b></span>"
                        + "</div></html>");
    }

    private void ganTuongTacThePhong(JButton the, PhongHotel phong) {
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
    }

    private static void ganChuotChoNoiDung(Container cha, MouseAdapter chuot) {
        for (Component con : cha.getComponents()) {
            con.addMouseListener(chuot);
            if (con instanceof Container vung) ganChuotChoNoiDung(vung, chuot);
        }
    }

    private static Color mauTrangThai(String s) {
        if (s == null || s.isBlank()) return MauUngDung.XAM_PHU;
        try {
            return switch (TrangThaiPhongHotel.tuMaDB(s)) {
                case TRONG -> MauUngDung.mau(0x22C55E);
                case DANG_O -> MauUngDung.mau(0xEF4444);
                case DANG_DON -> MauUngDung.mau(0xF59E0B);
                case BAO_TRI -> MauUngDung.XAM_PHU;
            };
        } catch (IllegalArgumentException ignored) {
            return MauUngDung.XAM_PHU;
        }
    }

    // 5. XỬ LÝ SỰ KIỆN KHI BẤM VÀO PHÒNG THEO TRẠNG THÁI NGHIỆP VỤ

    public void moPhong(PhongHotel phong) {
        if (TrangThaiPhongHotel.DANG_DON.maDB().equals(phong.getTrangThai())) {
            if (HopThoai.xacNhan(
                    this,
                    "Phòng "
                            + phong.getMaPhong()
                            + " đang chờ dọn vệ sinh.\n"
                            + "Bạn có muốn xác nhận đã dọn phòng xong để chuyển sang trạng thái"
                            + " Trống không?")) {
                String maPhong = phong.getMaPhong();
                TacVuNen.chayCoPhien(
                        this,
                        () -> true,
                        () -> new LuuTruService().hoanTatDonPhong(maPhong),
                        () -> {
                            nap();
                            HopThoai.hienThiThanhCong(
                                    this, "Đã xác nhận dọn xong phòng " + maPhong + ".");
                        });
            }
            return;
        }
        if (!List.of(TrangThaiPhongHotel.TRONG.maDB(), TrangThaiPhongHotel.DANG_O.maDB())
                .contains(phong.getTrangThai())) {
            HopThoai.hienThiThongTin(
                    this,
                    "Phòng "
                            + phong.getMaPhong()
                            + " đang "
                            + phong.getNhanTrangThai().toLowerCase(Locale.ROOT)
                            + ". Chưa thể đặt hoặc nhận phòng.");
            return;
        }
        gio.getQuyTrinh().chonPhongHotel(phong.getMaPhong());
        if (khiChonPhong != null) {
            khiChonPhong.accept(phong);
            return;
        }
        if (phong.getTrangThai().equals(TrangThaiPhongHotel.DANG_O.maDB())) {
            if (phong.getDangO() == null || phong.getDangO().getHopDong() == null) {
                HopThoai.hienThiCanhBao(this, "Chưa tìm thấy hợp đồng đang ở. Hãy làm mới sơ đồ.");
                return;
            }
            gio.getQuyTrinh().chonHotel(phong.getDangO().getHopDong().getMaHopDong());
            dieuHuong.accept(MaManHinh.NHAT_KY_CHAM_SOC);
        } else {
            gio.getQuyTrinh().chonHotel("");
            dieuHuong.accept(MaManHinh.DAT_PHONG_LUU_TRU);
        }
    }

    // 6. HIỂN THỊ POPUP XEM NHANH CHI TIẾT PHÒNG KHI RÊ CHUỘT / FOCUS (HOVER)

    private void henChiTiet(JComponent the, PhongHotel phong) {
        anChiTiet();
        choHienThi = new Timer(300, e -> hienChiTiet(the, phong));
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

    private void hienChiTiet(JComponent the, PhongHotel phong) {
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

    public static String layChiTiet(PhongHotel phong) {
        StringBuilder s =
                new StringBuilder(
                        "<html><body style='width:270px'><b>"
                                + html(phong.getMaPhong())
                                + " — "
                                + html(phong.getLoaiPhong())
                                + "</b><br>"
                                + phong.getNhanTrangThai()
                                + "<br>Sức chứa: "
                                + phong.getSucChua()
                                + "<br>Giá: "
                                + DinhDang.dinhDang(phong.getGiaDem())
                                + " / ngày");
        if (phong.getDangO() != null)
            s.append("<hr><b>Đang lưu trú</b><br>").append(layLich(phong.getDangO()));
        if (!phong.getDatTruoc().isEmpty())
            s.append("<hr><b>Lịch đặt gần nhất (tổng ")
                    .append(phong.getDatTruoc().size())
                    .append(")</b><br>")
                    .append(layLich(phong.getDatTruoc().get(0)));
        return s.append("</body></html>").toString();
    }

    private static String layLich(HotelDTO.LichPhong lich) {
        var d = DinhDang.NGAY_GIO;
        String tenThuCung =
                lich.getThuCung() != null && lich.getThuCung().getTen() != null
                        ? lich.getThuCung().getTen()
                        : "";
        String tenKhach =
                lich.getKhachHang() != null && lich.getKhachHang().getHoTen() != null
                        ? lich.getKhachHang().getHoTen()
                        : "";
        return "Thú cưng: "
                + html(tenThuCung)
                + "<br>Chủ nuôi: "
                + html(tenKhach)
                + "<br>Vào: "
                + lich.getNgayVao().format(d)
                + "<br>Ra dự kiến: "
                + lich.getNgayRa().format(d);
    }

    private static String html(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // 7. CHỨC NĂNG IN SƠ ĐỒ PHÒNG LƯU TRÚ RA MÁY IN / PDF

    private void inSoDo() {
        var congViec = PrinterJob.getPrinterJob();
        congViec.setJobName("Sơ đồ phòng Pet Station");
        congViec.setPrintable(
                (g, khung, trang) -> {
                    double tiLe =
                            Math.min(
                                    1,
                                    khung.getImageableWidth() / Math.max(1, khungPhong.getWidth()));
                    double cao = khung.getImageableHeight() / tiLe;
                    if (trang * cao >= khungPhong.getHeight()) return Printable.NO_SUCH_PAGE;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.translate(khung.getImageableX(), khung.getImageableY());
                    g2.scale(tiLe, tiLe);
                    g2.translate(0, -trang * cao);
                    khungPhong.printAll(g2);
                    g2.dispose();
                    return Printable.PAGE_EXISTS;
                });
        if (congViec.printDialog()) {
            TacVuNen.chay(
                    () -> {
                        congViec.print();
                        return null;
                    },
                    v -> {},
                    loi -> HopThoai.hienThiLoi(this, TacVuNen.thongDiepLoi(loi)));
        }
    }
}
