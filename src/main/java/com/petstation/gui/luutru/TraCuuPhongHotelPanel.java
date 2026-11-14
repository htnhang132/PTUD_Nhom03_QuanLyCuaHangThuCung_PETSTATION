package com.petstation.gui.luutru;

import com.petstation.dto.GioHang;
import com.petstation.dto.HotelDTO;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiPhongHotel;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.service.chung.DieuHuongBanGhiService;
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
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
    private final JTextField oLoc;
    private final JComboBox<String> cboLoai = new JComboBox<>();
    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(
                    new String[] {
                        "Tất cả",
                        "Trống",
                        "Có thú",
                        "Quá hạn trả",
                        "Đang dọn",
                        "Bảo trì",
                        "Ngừng hoạt động"
                    });
    private final JLabel nhanKetQua = new JLabel();
    private List<PhongHotel> cacPhong = List.of();
    private List<PhongHotel> cacPhongHienThi = List.of();
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
        this.oLoc = taoOTimKiem("Tìm phòng, loại phòng, khách, thú cưng...");
        setLayout(new BorderLayout(0, 14));
        khungPhong.setOpaque(false);
        add(taoPhanDauSoDo(), BorderLayout.NORTH);
        JScrollPane cuon = taoVungCuonSoDo();
        add(cuon, BorderLayout.CENTER);
        add(taoNhanHuongDanSoDo(), BorderLayout.SOUTH);
        ganSuKienBoLocVaCoGian(cuon);
        nap();
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(false, false, oLoc != null, true, false, true);
    }

    @Override
    public void xuLyIn() {
        inSoDo();
    }

    private JPanel taoPhanDauSoDo() {
        JPanel dau = taoVungTrongSuot(new BorderLayout());
        dau.add(
                ThanhDanhSach.tieuDe(taoTieuDeManHinh("Sơ đồ phòng lưu trú"), nhanKetQua),
                BorderLayout.NORTH);
        ThanhDanhSach loc = new ThanhDanhSach();
        oLoc.setName("timPhong");
        oLoc.setPreferredSize(new Dimension(220, 34));
        cboLoai.setName("loaiPhong");
        cboLoai.setPreferredSize(new Dimension(130, 34));
        cboTrangThai.setName("trangThaiPhong");
        cboTrangThai.setPreferredSize(new Dimension(130, 34));
        loc.add(oLoc);
        loc.themBoLoc("Loại:", cboLoai);
        loc.themBoLoc("Trạng thái:", cboTrangThai);
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
        dau.add(loc, BorderLayout.CENTER);
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

    private void datLaiBoLoc() {
        dangNap = true;
        try {
            oLoc.setText("");
            if (cboLoai.getItemCount() > 0) cboLoai.setSelectedIndex(0);
            if (cboTrangThai.getItemCount() > 0) cboTrangThai.setSelectedIndex(0);
        } finally {
            dangNap = false;
        }
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
        oLoc.addActionListener(
                e -> {
                    loc();
                    if (khungPhong.getComponentCount() == 1
                            && khungPhong.getComponent(0) instanceof JButton duyNhat) {
                        duyNhat.doClick();
                    } else if (khungPhong.getComponentCount() > 0) {
                        khungPhong.getComponent(0).requestFocusInWindow();
                    }
                });
        cboLoai.addActionListener(
                e -> {
                    if (!dangNap) loc();
                });
        cboTrangThai.addActionListener(
                e -> {
                    if (!dangNap) loc();
                });
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
                    cacPhong = dsPhong == null ? List.of() : dsPhong;
                    Object daChon = cboLoai.getSelectedItem();
                    dangNap = true;
                    cboLoai.removeAllItems();
                    cboLoai.addItem("Tất cả");
                    cacPhong.stream()
                            .map(PhongHotel::getLoaiPhong)
                            .filter(Objects::nonNull)
                            .distinct()
                            .sorted()
                            .forEach(cboLoai::addItem);
                    if (daChon != null) cboLoai.setSelectedItem(daChon);
                    dangNap = false;
                    loc();
                });
    }

    private void loc() {
        if (dangNap) return;
        anChiTiet();
        khungPhong.removeAll();
        String tu = Objects.toString(oLoc.getText(), "").trim().toLowerCase(Locale.ROOT);
        String loaiChon =
                cboLoai.getSelectedIndex() > 0
                        ? Objects.toString(cboLoai.getSelectedItem(), "")
                        : "";
        String ttChon =
                cboTrangThai.getSelectedIndex() > 0
                        ? Objects.toString(cboTrangThai.getSelectedItem(), "")
                        : "";
        List<PhongHotel> dsLoc = new ArrayList<>();
        for (var p : cacPhong) {
            if (p == null) continue;
            if (!khopTuKhoa(p, tu)) continue;
            if (!loaiChon.isEmpty() && !loaiChon.equals(p.getLoaiPhong())) continue;
            if ("Quá hạn trả".equals(ttChon)) {
                if (!laQuaHan(p)) continue;
            } else if (!ttChon.isEmpty() && !ttChon.equals(p.getNhanTrangThai())) {
                continue;
            }
            khungPhong.add(taoThe(p));
            dsLoc.add(p);
        }
        cacPhongHienThi = List.copyOf(dsLoc);
        nhanKetQua.setText(ThanhDanhSach.soKetQua(dsLoc.size(), cacPhong.size()));
        khungPhong.revalidate();
        khungPhong.repaint();
    }

    private static boolean khopTuKhoa(PhongHotel p, String tu) {
        if (tu.isEmpty()) return true;
        if (chuaTuKhoa(p.getMaPhong(), tu)
                || chuaTuKhoa(p.getLoaiPhong(), tu)
                || chuaTuKhoa(p.getNhanTrangThai(), tu)) {
            return true;
        }
        if (laQuaHan(p) && "quá hạn trả".contains(tu)) return true;
        if (khopLich(p.getDangO(), tu)) return true;
        if (p.getDatTruoc() != null) {
            for (var lich : p.getDatTruoc()) {
                if (khopLich(lich, tu)) return true;
            }
        }
        return false;
    }

    private static boolean khopLich(HotelDTO.LichPhong lich, String tu) {
        if (lich == null) return false;
        String tenKhach = lich.getKhachHang() == null ? "" : lich.getKhachHang().getHoTen();
        String tenThu = lich.getThuCung() == null ? "" : lich.getThuCung().getTen();
        return chuaTuKhoa(tenKhach, tu) || chuaTuKhoa(tenThu, tu);
    }

    private static boolean chuaTuKhoa(String chuoi, String tu) {
        return chuoi != null && chuoi.toLowerCase(Locale.ROOT).contains(tu);
    }

    private static boolean laQuaHan(PhongHotel phong) {
        return phong != null
                && TrangThaiPhongHotel.DANG_O.maDB().equals(phong.getTrangThai())
                && phong.getDangO() != null
                && phong.getDangO().getNgayRa() != null
                && phong.getDangO().getNgayRa().isBefore(LocalDateTime.now());
    }

    public void lamMoiSoDo() {
        nap();
    }

    @Override
    public void xuLyHienThi() {
        nap();
    }

    @Override
    public void xuLyLamMoi() {
        datLaiBoLoc();
        nap();
    }

    // 4. DỰNG THẺ PHÒNG (ROOM CARD) & MÀU SẮC THEO TRẠNG THÁI

    private JButton taoThe(PhongHotel phong) {
        JButton the = new JButton();
        String maPhong = Objects.toString(phong.getMaPhong(), "");
        the.setName("phong:" + maPhong);
        the.setLayout(new BorderLayout(0, 10));
        boolean quaHan = laQuaHan(phong);
        the.setBackground(quaHan ? MauUngDung.KHONG_HOP_LE_NEN : Color.WHITE);
        Color mauNhan = mauTrangThai(phong);
        the.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(MauUngDung.mau(0xE6E1DA), 1, true),
                                BorderFactory.createMatteBorder(0, 4, 0, 0, mauNhan)),
                        BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        the.setPreferredSize(new Dimension(250, 165));
        the.setHorizontalAlignment(SwingConstants.LEFT);
        the.add(taoPhanDauThePhong(phong, mauNhan, quaHan), BorderLayout.NORTH);
        the.add(taoThongTinThePhong(phong, quaHan), BorderLayout.CENTER);
        JLabel gia = new JLabel(DinhDang.dinhDang(phong.getGiaDem()) + " / ngày");
        gia.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        gia.setForeground(MauUngDung.NAU);
        the.add(gia, BorderLayout.SOUTH);
        ganTuongTacThePhong(the, phong);
        return the;
    }

    private JPanel taoPhanDauThePhong(PhongHotel phong, Color mauNhan, boolean quaHan) {
        JPanel dau = taoVungTrongSuot(new BorderLayout(8, 0));
        JLabel ma = new JLabel(Objects.toString(phong.getMaPhong(), ""));
        ma.setFont(MauUngDung.taoPhongChu(Font.BOLD, 20));
        ma.setForeground(MauUngDung.NAU);
        dau.add(ma, BorderLayout.WEST);
        String nhan = quaHan ? "Quá hạn trả" : Objects.toString(phong.getNhanTrangThai(), "");
        JLabel trangThai = new JLabel(nhan);
        trangThai.setForeground(mauNhan);
        trangThai.setOpaque(true);
        trangThai.setBackground(MauUngDung.phaTrang90(mauNhan));
        trangThai.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        trangThai.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        dau.add(trangThai, BorderLayout.EAST);
        return dau;
    }

    private static JLabel taoThongTinThePhong(PhongHotel phong, boolean quaHan) {
        String tenThuCung =
                phong.getDangO() != null
                                && phong.getDangO().getThuCung() != null
                                && phong.getDangO().getThuCung().getTen() != null
                        ? phong.getDangO().getThuCung().getTen()
                        : "";
        String thu;
        if (quaHan) {
            thu =
                    "<span style='color:#B91C1C; font-weight:bold;'>⚠ Quá hạn trả: "
                            + html(tenThuCung)
                            + "</span>";
        } else if (phong.getDangO() != null) {
            thu =
                    "<span style='color:#1D4ED8; font-weight:bold;'>🐾 Đang ở: "
                            + html(tenThuCung)
                            + "</span>";
        } else {
            thu =
                    "<span style='color:#57534E;'>Sức chứa: "
                            + phong.getSucChua()
                            + " thú cưng</span>";
        }
        int soDatTruoc = phong.getDatTruoc() == null ? 0 : phong.getDatTruoc().size();
        return new JLabel(
                "<html><div style='font-family:Segoe UI, sans-serif; font-size:12px;"
                        + " line-height:1.45;'><b style='color:#292524;'>"
                        + html(phong.getLoaiPhong())
                        + "</b><br>"
                        + thu
                        + "<br>"
                        + "<span style='color:#78716C;'>Đặt trước: <b>"
                        + soDatTruoc
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

    private static Color mauTrangThai(PhongHotel phong) {
        if (phong == null || phong.getTrangThai() == null || phong.getTrangThai().isBlank()) {
            return MauUngDung.XAM_PHU;
        }
        try {
            return switch (TrangThaiPhongHotel.tuMaDB(phong.getTrangThai())) {
                case TRONG -> MauUngDung.mau(0x22C55E);
                case DANG_O ->
                        laQuaHan(phong) ? MauUngDung.mau(0xDC2626) : MauUngDung.mau(0x2563EB);
                case DANG_DON -> MauUngDung.mau(0xF59E0B);
                case BAO_TRI -> MauUngDung.XAM_PHU;
            };
        } catch (IllegalArgumentException ignored) {
            return MauUngDung.XAM_PHU;
        }
    }

    // 5. XỬ LÝ SỰ KIỆN KHI BẤM VÀO PHÒNG THEO TRẠNG THÁI NGHIỆP VỤ

    public void moPhong(PhongHotel phong) {
        if (phong == null) return;
        String trangThai = Objects.toString(phong.getTrangThai(), "");
        String maPhong = Objects.toString(phong.getMaPhong(), "");
        if (TrangThaiPhongHotel.DANG_DON.maDB().equals(trangThai)) {
            xacNhanDonPhong(maPhong);
            return;
        }
        if (!List.of(TrangThaiPhongHotel.TRONG.maDB(), TrangThaiPhongHotel.DANG_O.maDB())
                .contains(trangThai)) {
            HopThoai.hienThiThongTin(
                    this,
                    "Phòng "
                            + maPhong
                            + " đang "
                            + Objects.toString(phong.getNhanTrangThai(), "")
                                    .toLowerCase(Locale.ROOT)
                            + ". Chưa thể đặt hoặc nhận phòng.");
            return;
        }
        if (gio != null) gio.getQuyTrinh().chonPhongHotel(maPhong);
        if (khiChonPhong != null) {
            khiChonPhong.accept(phong);
            return;
        }
        if (TrangThaiPhongHotel.DANG_O.maDB().equals(trangThai)) {
            if (phong.getDangO() == null || phong.getDangO().getHopDong() == null) {
                HopThoai.hienThiCanhBao(this, "Chưa tìm thấy hợp đồng đang ở. Hãy làm mới sơ đồ.");
                return;
            }
            String maHd = phong.getDangO().getHopDong().getMaHopDong();
            if (gio != null) gio.getQuyTrinh().chonHotel(maHd);
            DieuHuongBanGhiService.macDinh().dat(MaManHinh.NHAT_KY_CHAM_SOC, maHd);
            if (dieuHuong != null) dieuHuong.accept(MaManHinh.NHAT_KY_CHAM_SOC);
            return;
        }
        if (gio != null) gio.getQuyTrinh().chonHotel("");
        if (dieuHuong != null) {
            DieuHuongBanGhiService.macDinh().dat(MaManHinh.QUAN_LY_LUU_TRU, maPhong);
            dieuHuong.accept(MaManHinh.DAT_PHONG_LUU_TRU);
        } else {
            DatPhongHotelDialog.mo(this, null, maPhong);
            nap();
        }
    }

    private void xacNhanDonPhong(String maPhong) {
        if (!HopThoai.xacNhan(
                this,
                "Phòng "
                        + maPhong
                        + " đang chờ dọn vệ sinh.\n"
                        + "Bạn có muốn xác nhận đã dọn phòng xong để chuyển sang trạng thái Trống"
                        + " không?")) {
            return;
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> new LuuTruService().hoanTatDonPhong(maPhong),
                () -> {
                    nap();
                    HopThoai.hienThiThanhCong(this, "Đã xác nhận dọn xong phòng " + maPhong + ".");
                });
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
        if (phong == null) return "";
        String trangThaiNhan = Objects.toString(phong.getNhanTrangThai(), "");
        if (laQuaHan(phong)) {
            trangThaiNhan +=
                    " <span style='color:#B91C1C; font-weight:bold;'>(Quá hạn trả phòng!)</span>";
        }
        StringBuilder s =
                new StringBuilder(
                        "<html><body style='width:270px'><b>"
                                + html(phong.getMaPhong())
                                + " — "
                                + html(phong.getLoaiPhong())
                                + "</b><br>"
                                + trangThaiNhan
                                + "<br>Sức chứa: "
                                + phong.getSucChua()
                                + "<br>Giá: "
                                + DinhDang.dinhDang(phong.getGiaDem())
                                + " / ngày");
        if (phong.getDangO() != null)
            s.append("<hr><b>Đang lưu trú</b><br>").append(layLich(phong.getDangO()));
        if (phong.getDatTruoc() != null && !phong.getDatTruoc().isEmpty())
            s.append("<hr><b>Lịch đặt gần nhất (tổng ")
                    .append(phong.getDatTruoc().size())
                    .append(")</b><br>")
                    .append(layLich(phong.getDatTruoc().get(0)));
        return s.append("</body></html>").toString();
    }

    private static String layLich(HotelDTO.LichPhong lich) {
        if (lich == null) return "";
        var d = DinhDang.NGAY_GIO;
        String tenThuCung =
                lich.getThuCung() != null && lich.getThuCung().getTen() != null
                        ? lich.getThuCung().getTen()
                        : "";
        String tenKhach =
                lich.getKhachHang() != null && lich.getKhachHang().getHoTen() != null
                        ? lich.getKhachHang().getHoTen()
                        : "";
        String vao = lich.getNgayVao() == null ? "" : lich.getNgayVao().format(d);
        String ra = lich.getNgayRa() == null ? "" : lich.getNgayRa().format(d);
        return "Thú cưng: "
                + html(tenThuCung)
                + "<br>Chủ nuôi: "
                + html(tenKhach)
                + "<br>Vào: "
                + vao
                + "<br>Ra dự kiến: "
                + ra;
    }

    private static String html(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // 7. CHỨC NĂNG IN SƠ ĐỒ PHÒNG LƯU TRÚ RA MÁY IN / PDF

    private void inSoDo() {
        List<PhongHotel> dsIn = cacPhongHienThi;
        if (dsIn.isEmpty()) {
            HopThoai.hienThiCanhBao(this, "Không có phòng nào để in.");
            return;
        }
        List<BufferedImage> anhCacHang = chupAnhCacHangPhong(dsIn);
        var congViec = PrinterJob.getPrinterJob();
        congViec.setJobName("Sơ đồ phòng Pet Station");
        congViec.setPrintable(taoBanInSoDo(anhCacHang));
        if (congViec.printDialog()) {
            TacVuNen.chay(
                    () -> {
                        congViec.print();
                        return null;
                    },
                    v -> HopThoai.hienThiThanhCong(this, "Đã gửi lệnh in sơ đồ phòng."),
                    loi -> HopThoai.hienThiLoi(this, TacVuNen.thongDiepLoi(loi)));
        }
    }

    private List<BufferedImage> chupAnhCacHangPhong(List<PhongHotel> dsIn) {
        int cot = 3;
        int rongHang = 790;
        int caoHang = 170;
        List<BufferedImage> ketQua = new ArrayList<>();
        for (int i = 0; i < dsIn.size(); i += cot) {
            JPanel hang = new JPanel(new GridLayout(1, cot, 14, 0));
            hang.setBackground(Color.WHITE);
            for (int j = 0; j < cot; j++) {
                if (i + j < dsIn.size()) {
                    hang.add(taoThe(dsIn.get(i + j)));
                } else {
                    JPanel trong = new JPanel();
                    trong.setOpaque(false);
                    hang.add(trong);
                }
            }
            hang.setBounds(0, 0, rongHang, caoHang);
            layoutDeQuy(hang);
            BufferedImage anh =
                    new BufferedImage(rongHang * 2, caoHang * 2, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = anh.createGraphics();
            try {
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, anh.getWidth(), anh.getHeight());
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.scale(2.0, 2.0);
                hang.printAll(g2);
            } finally {
                g2.dispose();
            }
            ketQua.add(anh);
        }
        return ketQua;
    }

    private static void layoutDeQuy(Container cha) {
        cha.doLayout();
        for (Component con : cha.getComponents()) {
            if (con instanceof Container sub) layoutDeQuy(sub);
        }
    }

    private static Printable taoBanInSoDo(List<BufferedImage> anhCacHang) {
        return (g, khung, trang) -> {
            double rongHang = 790.0;
            double caoHang = 170.0;
            double tiLe = Math.min(1.0, khung.getImageableWidth() / rongHang);
            double caoTrenTrang = caoHang * tiLe;
            double khoangCach = 14.0 * tiLe;
            int soHangMoiTrang =
                    Math.max(
                            1,
                            (int)
                                    Math.floor(
                                            (khung.getImageableHeight() + khoangCach)
                                                    / (caoTrenTrang + khoangCach)));
            int batDau = trang * soHangMoiTrang;
            if (batDau >= anhCacHang.size()) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                int ketThuc = Math.min(anhCacHang.size(), batDau + soHangMoiTrang);
                double x = khung.getImageableX();
                double y = khung.getImageableY();
                int w = (int) Math.round(rongHang * tiLe);
                int h = (int) Math.round(caoTrenTrang);
                for (int i = batDau; i < ketThuc; i++) {
                    g2.drawImage(
                            anhCacHang.get(i),
                            (int) Math.round(x),
                            (int) Math.round(y),
                            w,
                            h,
                            null);
                    y += caoTrenTrang + khoangCach;
                }
            } finally {
                g2.dispose();
            }
            return Printable.PAGE_EXISTS;
        };
    }
}
