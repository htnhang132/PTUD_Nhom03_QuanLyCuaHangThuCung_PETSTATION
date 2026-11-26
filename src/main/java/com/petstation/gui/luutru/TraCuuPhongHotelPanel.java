package com.petstation.gui.luutru;

import com.petstation.dto.GioHang;
import com.petstation.dto.HotelDTO;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.MucLocTrangThai;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiPhongHotel;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.chung.DieuHuongBanGhiService;
import com.petstation.service.chung.QuyTrinhService;
import com.petstation.service.luutru.LuuTruService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public final class TraCuuPhongHotelPanel extends CoSoPanel {
    // 1. HẰNG SỐ & THUỘC TÍNH SƠ ĐỒ PHÒNG

    private static final String TAT_CA = "Tất cả";
    private static final String QUA_HAN = "QUA_HAN";

    private final GioHang gio;
    private final Consumer<MaManHinh> dieuHuong;
    private final Consumer<PhongHotel> khiChonPhong;
    private final QuyTrinhService quyTrinhService = new QuyTrinhService();
    private final LuuTruService luuTruService = new LuuTruService();
    private final BanHangService banHangService = new BanHangService();

    private final JTextField oLoc;
    private final JComboBox<String> cboLoai = new JComboBox<>();
    private final List<MucLocTrangThai> cacMucTrangThai =
            List.of(
                    new MucLocTrangThai(TAT_CA, TAT_CA),
                    new MucLocTrangThai(TrangThaiPhongHotel.TRONG.maDB(), "Trống"),
                    new MucLocTrangThai(TrangThaiPhongHotel.DANG_O.maDB(), "Đang ở"),
                    new MucLocTrangThai(QUA_HAN, "Quá hạn"),
                    new MucLocTrangThai(TrangThaiPhongHotel.DANG_DON.maDB(), "Đang dọn"),
                    new MucLocTrangThai(TrangThaiPhongHotel.BAO_TRI.maDB(), "Bảo trì"));
    private final JComboBox<MucLocTrangThai> cboTrangThai =
            new JComboBox<>(cacMucTrangThai.toArray(MucLocTrangThai[]::new));
    private final JLabel nhanThongKeDau = new JLabel("Đang tải sơ đồ phòng...");
    private final JPanel khungLuoiNhom = new JPanel();
    private final JPanel khungChiTietNoiDung = new JPanel();
    private JScrollPane cuonLuoi;

    private NghiepVuHotelPanel hoTroHopDong;
    private Runnable khiDuLieuThayDoi;
    private String trangThaiLoc = TAT_CA;
    private String maPhongDangChon;
    private List<PhongHotel> cacPhong = List.of();
    private List<PhongHotel> cacPhongHienThi = List.of();
    private boolean dangNap;
    private int luotTai;
    private int soCotHienTai = 3;
    private final Map<String, ThePhongPanel> theTheoMaPhong = new LinkedHashMap<>();
    private final Map<String, Color> mauGocTheoMaPhong = new LinkedHashMap<>();
    private Timer henGioLoc;

    // 2. HÀM KHỞI TẠO & VÒNG ĐỜI PANEL

    public TraCuuPhongHotelPanel(GioHang gio, Consumer<MaManHinh> dieuHuong) {
        this(gio, dieuHuong, null);
    }

    public TraCuuPhongHotelPanel(
            GioHang gio, Consumer<MaManHinh> dieuHuong, Consumer<PhongHotel> khiChonPhong) {
        this.gio = gio;
        this.dieuHuong = dieuHuong;
        this.khiChonPhong = khiChonPhong;
        this.oLoc = taoOTimKiem("Phòng, khách hàng hoặc thú cưng");
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setBackground(MauUngDung.NEN_KEM);
        khungLuoiNhom.setLayout(new BoxLayout(khungLuoiNhom, BoxLayout.Y_AXIS));
        khungLuoiNhom.setOpaque(false);

        add(taoPhanDauSoDo(), BorderLayout.NORTH);
        add(taoThanSoDo(), BorderLayout.CENTER);
        ganSuKienBoLocVaCoGian();
        nap();
    }

    public void datBoDieuKhienHopDong(NghiepVuHotelPanel hoTro, Runnable khiThayDoi) {
        this.hoTroHopDong = hoTro;
        this.khiDuLieuThayDoi = khiThayDoi;
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(true, false, oLoc != null, true, false, true);
    }

    @Override
    public void xuLyTaoMoi() {
        datPhongTrongHoacChon(timPhongTheoMa(maPhongDangChon));
    }

    @Override
    public void xuLyTimKiem() {
        if (oLoc != null) {
            oLoc.requestFocusInWindow();
            oLoc.selectAll();
        }
    }

    @Override
    public void xuLyIn() {
        inSoDo();
    }

    @Override
    public void xuLyHienThi() {
        lamMoiSoDo();
    }

    @Override
    public void xuLyLamMoi() {
        lamMoiSoDo();
    }

    public void lamMoiSoDo() {
        nap();
    }

    // 3. XÂY DỰNG SƠ ĐỒ, BỘ LỌC & TẢI DỮ LIỆU

    private JPanel taoPhanDauSoDo() {
        JPanel khungDau = taoVungTrongSuot(new BorderLayout());
        khungDau.add(
                ThanhDanhSach.tieuDe(taoTieuDeManHinh("Sơ đồ phòng lưu trú"), nhanThongKeDau),
                BorderLayout.NORTH);

        ThanhDanhSach thanh = new ThanhDanhSach();
        oLoc.setName("timPhong");
        oLoc.setPreferredSize(new Dimension(220, 34));
        cboLoai.setName("loaiPhong");
        cboLoai.addItem(TAT_CA);
        thanh.add(oLoc);
        thanh.themBoLoc("Loại phòng:", cboLoai);
        thanh.themBoLoc("Trạng thái:", cboTrangThai);

        thanh.add(
                taoNut(
                        "+ Đặt phòng",
                        NutDinhDang.KieuNut.CHINH,
                        () -> datPhongTrongHoacChon(timPhongTheoMa(maPhongDangChon))));

        NutDinhDang btnIn = taoNut("In sơ đồ", NutDinhDang.KieuNut.TRONG_SUOT, this::inSoDo);
        btnIn.setToolTipText("In sơ đồ phòng (Ctrl+P)");
        thanh.add(btnIn);

        khungDau.add(thanh, BorderLayout.CENTER);
        return khungDau;
    }

    private Component taoThanSoDo() {
        cuonLuoi = new JScrollPane(khungLuoiNhom);
        cuonLuoi.setBorder(null);
        cuonLuoi.getViewport().setBackground(MauUngDung.NEN_KEM);
        cuonLuoi.getVerticalScrollBar().setUnitIncrement(18);

        BoGocPanel khungChiTietBenPhai = new BoGocPanel(12, Color.WHITE, true);
        khungChiTietBenPhai.setLayout(new BorderLayout());
        khungChiTietBenPhai.setBorder(new EmptyBorder(14, 16, 14, 16));
        khungChiTietBenPhai.setPreferredSize(new Dimension(350, 0));

        khungChiTietNoiDung.setLayout(new BorderLayout());
        khungChiTietNoiDung.setOpaque(false);
        JScrollPane cuonChiTiet = new JScrollPane(khungChiTietNoiDung);
        cuonChiTiet.setBorder(null);
        cuonChiTiet.getViewport().setBackground(Color.WHITE);
        cuonChiTiet.getVerticalScrollBar().setUnitIncrement(16);
        khungChiTietBenPhai.add(cuonChiTiet, BorderLayout.CENTER);

        JSplitPane chiaDoi =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cuonLuoi, khungChiTietBenPhai);
        chiaDoi.setDividerSize(8);
        chiaDoi.setResizeWeight(1.0);
        chiaDoi.setContinuousLayout(true);
        chiaDoi.setBorder(null);
        chiaDoi.setOpaque(false);
        return chiaDoi;
    }

    private void ganSuKienBoLocVaCoGian() {
        henGioLoc = new Timer(150, e -> ve(cacPhong));
        henGioLoc.setRepeats(false);
        themBoLangNgheTaiLieu(oLoc, () -> henGioLoc.restart());
        cboLoai.addActionListener(
                e -> {
                    if (!dangNap) {
                        ve(cacPhong);
                    }
                });
        cboTrangThai.addActionListener(
                e -> {
                    MucLocTrangThai muc = (MucLocTrangThai) cboTrangThai.getSelectedItem();
                    trangThaiLoc = muc == null ? TAT_CA : muc.layMa();
                    if (!dangNap) {
                        ve(cacPhong);
                    }
                });
        cuonLuoi.getViewport()
                .addComponentListener(
                        new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                capNhatSoCotHienTai();
                            }
                        });
    }

    private void capNhatSoCotHienTai() {
        int rong = cuonLuoi.getViewport().getWidth();
        int cotMoi = rong >= 760 ? 3 : (rong >= 490 ? 2 : 1);
        if (cotMoi != soCotHienTai) {
            soCotHienTai = cotMoi;
            if (!cacPhong.isEmpty()) {
                ve(cacPhong);
            }
        }
    }

    private void nap() {
        int luot = ++luotTai;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                quyTrinhService::layPhongHotel,
                ds -> {
                    if (luot != luotTai) {
                        return;
                    }
                    cacPhong = ds == null ? List.of() : ds;
                    napLoai();
                    capNhatSoLuongTrangThai();
                    ve(cacPhong);
                });
    }

    private void napLoai() {
        dangNap = true;
        String chonCu = Objects.toString(cboLoai.getSelectedItem(), TAT_CA);
        cboLoai.removeAllItems();
        cboLoai.addItem(TAT_CA);
        cacPhong.stream()
                .map(PhongHotel::getLoaiPhong)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .forEach(cboLoai::addItem);
        cboLoai.setSelectedItem(chonCu);
        if (cboLoai.getSelectedIndex() < 0) {
            cboLoai.setSelectedItem(TAT_CA);
        }
        dangNap = false;
    }

    private void capNhatSoLuongTrangThai() {
        int tong = cacPhong.size();
        int trong = 0;
        int dangO = 0;
        int quaHan = 0;
        int dangDon = 0;
        int baoTri = 0;
        for (PhongHotel p : cacPhong) {
            String tt = Objects.toString(p.getTrangThai(), "");
            if (TrangThaiPhongHotel.TRONG.maDB().equals(tt)) trong++;
            else if (TrangThaiPhongHotel.DANG_O.maDB().equals(tt)) {
                dangO++;
                if (laQuaHan(p)) quaHan++;
            } else if (TrangThaiPhongHotel.DANG_DON.maDB().equals(tt)) dangDon++;
            else if (TrangThaiPhongHotel.BAO_TRI.maDB().equals(tt)) baoTri++;
        }
        nhanThongKeDau.setText(
                trong
                        + " trống · "
                        + dangO
                        + " đang ở · "
                        + quaHan
                        + " quá hạn · "
                        + (dangDon + baoTri)
                        + " chờ xử lý");
        datSoLuongMuc(TAT_CA, tong);
        datSoLuongMuc(TrangThaiPhongHotel.TRONG.maDB(), trong);
        datSoLuongMuc(TrangThaiPhongHotel.DANG_O.maDB(), dangO);
        datSoLuongMuc(QUA_HAN, quaHan);
        datSoLuongMuc(TrangThaiPhongHotel.DANG_DON.maDB(), dangDon);
        datSoLuongMuc(TrangThaiPhongHotel.BAO_TRI.maDB(), baoTri);
        cboTrangThai.repaint();
    }

    private void datSoLuongMuc(String ma, int so) {
        for (MucLocTrangThai muc : cacMucTrangThai) {
            if (ma.equals(muc.layMa())) {
                muc.datSoLuong(so);
                return;
            }
        }
    }

    private void ve(List<PhongHotel> ds) {
        khungLuoiNhom.removeAll();
        theTheoMaPhong.clear();
        mauGocTheoMaPhong.clear();
        String tuKhoa = Objects.toString(oLoc.getText(), "").trim().toLowerCase(Locale.ROOT);
        String loaiChon = Objects.toString(cboLoai.getSelectedItem(), TAT_CA);
        cacPhongHienThi = ds.stream().filter(p -> khopBoLocPhong(p, tuKhoa, loaiChon)).toList();

        if (cacPhongHienThi.isEmpty()) {
            JLabel rong = new JLabel("Không có phòng phù hợp với bộ lọc hiện tại.");
            rong.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 13));
            rong.setForeground(MauUngDung.VAN_BAN_XAM);
            rong.setBorder(BorderFactory.createEmptyBorder(24, 12, 12, 12));
            khungLuoiNhom.add(rong);
        } else {
            veCacNhomPhong(cacPhongHienThi);
        }
        chonMacDinhSauKhiLoc();
        khungLuoiNhom.revalidate();
        khungLuoiNhom.repaint();
    }

    private boolean khopBoLocPhong(PhongHotel p, String tuKhoa, String loaiChon) {
        if (!TAT_CA.equals(loaiChon)
                && !loaiChon.equalsIgnoreCase(Objects.toString(p.getLoaiPhong(), ""))) {
            return false;
        }
        String tt = Objects.toString(p.getTrangThai(), "");
        if (QUA_HAN.equals(trangThaiLoc)) {
            if (!laQuaHan(p)) return false;
        } else if (!TAT_CA.equals(trangThaiLoc) && !trangThaiLoc.equals(tt)) {
            return false;
        }
        return khopTuKhoaPhong(p, tuKhoa);
    }

    private boolean khopTuKhoaPhong(PhongHotel p, String tuKhoa) {
        if (tuKhoa.isEmpty()) {
            return true;
        }
        if (chuaChuoi(p.getMaPhong(), tuKhoa) || chuaChuoi(p.getLoaiPhong(), tuKhoa)) {
            return true;
        }
        if (khopLich(p.getDangO(), tuKhoa)) {
            return true;
        }
        return p.getDatTruoc() != null
                && p.getDatTruoc().stream().anyMatch(l -> khopLich(l, tuKhoa));
    }

    private static boolean khopLich(HotelDTO.LichPhong l, String tuKhoa) {
        if (l == null) {
            return false;
        }
        String maHd = l.getHopDong() != null ? l.getHopDong().getMaHopDong() : "";
        String tenThu = l.getThuCung() != null ? l.getThuCung().getTen() : "";
        String tenKhach = l.getKhachHang() != null ? l.getKhachHang().getHoTen() : "";
        String sdt = l.getKhachHang() != null ? l.getKhachHang().getSoDienThoai() : "";
        return chuaChuoi(maHd, tuKhoa)
                || chuaChuoi(tenThu, tuKhoa)
                || chuaChuoi(tenKhach, tuKhoa)
                || chuaChuoi(sdt, tuKhoa);
    }

    private static boolean chuaChuoi(String nguon, String tuKhoa) {
        return nguon != null && nguon.toLowerCase(Locale.ROOT).contains(tuKhoa);
    }

    // 4. VẼ NHÓM PHÒNG & THẺ PHÒNG

    private void veCacNhomPhong(List<PhongHotel> dsHienThi) {
        Map<String, List<PhongHotel>> nhomTheoLoai = new LinkedHashMap<>();
        for (PhongHotel p : dsHienThi) {
            String loai =
                    p.getLoaiPhong() == null || p.getLoaiPhong().isBlank()
                            ? "Khác"
                            : p.getLoaiPhong();
            nhomTheoLoai.computeIfAbsent(loai, k -> new ArrayList<>()).add(p);
        }
        for (Map.Entry<String, List<PhongHotel>> entry : nhomTheoLoai.entrySet()) {
            khungLuoiNhom.add(taoKhoiNhomPhong(entry.getKey(), entry.getValue()));
            khungLuoiNhom.add(Box.createVerticalStrut(14));
        }
    }

    private JPanel taoKhoiNhomPhong(String tenLoai, List<PhongHotel> dsPhong) {
        JPanel khoi = new JPanel(new BorderLayout(0, 8));
        khoi.setOpaque(false);
        khoi.setAlignmentX(Component.LEFT_ALIGNMENT);

        long soTrong =
                dsPhong.stream()
                        .filter(p -> TrangThaiPhongHotel.TRONG.maDB().equals(p.getTrangThai()))
                        .count();
        long gia = dsPhong.isEmpty() ? 0 : dsPhong.get(0).getGiaDem();
        JLabel nhanLoai = taoTieuDeMuc(tenLoai);

        JLabel nhanThongKe =
                new JLabel(
                        soTrong
                                + " trống / "
                                + dsPhong.size()
                                + " phòng · "
                                + DinhDang.dinhDang(gia)
                                + "/ngày");
        nhanThongKe.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanThongKe.setForeground(MauUngDung.VAN_BAN_XAM);

        JPanel dauNhom = new JPanel(new BorderLayout(8, 0));
        dauNhom.setOpaque(false);
        dauNhom.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN),
                        BorderFactory.createEmptyBorder(0, 2, 5, 2)));
        dauNhom.add(nhanLoai, BorderLayout.WEST);
        dauNhom.add(nhanThongKe, BorderLayout.EAST);

        JPanel luoi = new JPanel(new GridLayout(0, soCotHienTai, 12, 12));
        luoi.setOpaque(false);
        for (PhongHotel p : dsPhong) {
            luoi.add(taoThe(p));
        }

        khoi.add(dauNhom, BorderLayout.NORTH);
        khoi.add(luoi, BorderLayout.CENTER);
        return khoi;
    }

    private JPanel taoThe(PhongHotel phong) {
        boolean dangChon = Objects.equals(maPhongDangChon, phong.getMaPhong());
        Color[] bangMau = layBangMauTrangThai(phong);
        Color mauNen = dangChon ? MauUngDung.KEM_NEN_NHAN : bangMau[1];
        ThePhongPanel the = new ThePhongPanel(mauNen, dangChon);
        if (phong.getMaPhong() != null) {
            theTheoMaPhong.put(phong.getMaPhong(), the);
            mauGocTheoMaPhong.put(phong.getMaPhong(), bangMau[1]);
        }
        the.setLayout(new BorderLayout());
        the.setPreferredSize(new Dimension(210, 88));
        the.setBorder(new EmptyBorder(10, 12, 10, 12));
        the.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        the.add(taoNoiDungThe(phong, bangMau[0]), BorderLayout.CENTER);
        the.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            return;
                        }
                        chonPhongVaoPanelChiTiet(phong);
                        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                            moPhong(phong);
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        hienThiMenuNeuChuotPhai(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        hienThiMenuNeuChuotPhai(e);
                    }

                    private void hienThiMenuNeuChuotPhai(MouseEvent e) {
                        if (!e.isPopupTrigger()) {
                            return;
                        }
                        chonPhongVaoPanelChiTiet(phong);
                        taoMenuNguCanhPhong(phong).show(e.getComponent(), e.getX(), e.getY());
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        the.datReChuot(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        the.datReChuot(false);
                    }
                });
        return the;
    }

    private JPopupMenu taoMenuNguCanhPhong(PhongHotel phong) {
        JPopupMenu popup = new JPopupMenu();
        String tt = Objects.toString(phong.getTrangThai(), "");
        String maPhong = Objects.toString(phong.getMaPhong(), "");
        if (TrangThaiPhongHotel.TRONG.maDB().equals(tt)) {
            JMenuItem dat = new JMenuItem("Đặt phòng " + maPhong);
            dat.addActionListener(e -> datPhongTrongHoacChon(phong));
            popup.add(dat);
        } else if (TrangThaiPhongHotel.DANG_DON.maDB().equals(tt)) {
            JMenuItem don = new JMenuItem("Xác nhận dọn phòng xong");
            don.addActionListener(e -> xacNhanDonPhong(maPhong));
            popup.add(don);
        } else if (TrangThaiPhongHotel.BAO_TRI.maDB().equals(tt)) {
            JMenuItem baoTri = new JMenuItem("Hoàn tất bảo trì");
            baoTri.addActionListener(e -> xacNhanHoanTatBaoTri(maPhong));
            popup.add(baoTri);
        }
        if (phong.getDangO() != null && phong.getDangO().getHopDong() != null) {
            String maHd = phong.getDangO().getHopDong().getMaHopDong();
            if (popup.getComponentCount() > 0) {
                popup.addSeparator();
            }
            JMenuItem qt = new JMenuItem("Trả phòng & Quyết toán");
            qt.setEnabled(coQuyenThanhToan());
            qt.addActionListener(e -> quyetToanHopDong(maHd));
            popup.add(qt);
            JMenuItem cs = new JMenuItem("Phiếu chăm sóc");
            cs.addActionListener(
                    e -> {
                        NhatKyChamSocDialog.mo(this, "HOTEL", maHd);
                        lamMoiVaThongBao();
                    });
            popup.add(cs);
            JMenuItem giaHan = new JMenuItem("Gia hạn phòng");
            giaHan.addActionListener(e -> suaHopDong(maHd));
            popup.add(giaHan);
        }
        if (phong.getDatTruoc() != null && !phong.getDatTruoc().isEmpty()) {
            themMucDatTruocVaoMenu(popup, phong, phong.getDatTruoc().get(0));
        }
        return popup;
    }

    private void themMucDatTruocVaoMenu(
            JPopupMenu popup, PhongHotel phong, HotelDTO.LichPhong lich) {
        HopDongLuuTru hd = lich.getHopDong();
        if (hd == null) {
            return;
        }
        if (popup.getComponentCount() > 0) {
            popup.addSeparator();
        }
        String maHd = hd.getMaHopDong();
        boolean duDieuKienVao =
                tinhCocConThieu(hd) <= 0 && phong != null && phong.kiemTraPhongTrong();
        JMenuItem nhan = new JMenuItem("Nhận phòng (Check-in) #" + maHd);
        nhan.setEnabled(duDieuKienVao);
        nhan.addActionListener(e -> nhanPhongHopDong(maHd));
        popup.add(nhan);
        if (tinhCocConThieu(hd) > 0) {
            JMenuItem thuCoc = new JMenuItem("Thu cọc #" + maHd);
            thuCoc.addActionListener(e -> thuCocHopDong(maHd));
            popup.add(thuCoc);
        }
        JMenuItem sua = new JMenuItem("Sửa đặt phòng #" + maHd);
        sua.addActionListener(e -> suaHopDong(maHd));
        popup.add(sua);
        JMenuItem huy = new JMenuItem("Hủy đặt phòng #" + maHd);
        huy.setEnabled(hd.getTienCoc() == 0);
        huy.addActionListener(e -> huyDatHopDong(maHd));
        popup.add(huy);
    }

    private JPanel taoNoiDungThe(PhongHotel phong, Color mauTrangThai) {
        JPanel noiDung = new JPanel();
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));
        noiDung.setOpaque(false);

        JPanel dongDau = new JPanel(new BorderLayout(6, 0));
        dongDau.setOpaque(false);
        dongDau.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel nhanMa = new JLabel(phong.getMaPhong());
        nhanMa.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        nhanMa.setForeground(MauUngDung.VAN_BAN_TOI);

        dongDau.add(nhanMa, BorderLayout.WEST);
        dongDau.add(taoChiBaoTrangThai(layNhanHuyHieu(phong), mauTrangThai), BorderLayout.EAST);
        dongDau.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        noiDung.add(dongDau);
        noiDung.add(Box.createVerticalStrut(6));
        noiDung.add(taoDongHaiThe(phong));
        noiDung.add(Box.createVerticalStrut(3));
        noiDung.add(taoDongBaThe(phong));
        return noiDung;
    }

    private static JLabel taoChiBaoTrangThai(String vanBan, Color mau) {
        Icon chamTron =
                new Icon() {
                    @Override
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(mau);
                        g2.fillOval(x, y, 8, 8);
                        g2.dispose();
                    }

                    @Override
                    public int getIconWidth() {
                        return 8;
                    }

                    @Override
                    public int getIconHeight() {
                        return 8;
                    }
                };
        JLabel nhan = new JLabel(vanBan, chamTron, SwingConstants.RIGHT);
        nhan.setIconTextGap(5);
        nhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        nhan.setForeground(mau);
        return nhan;
    }

    private static final class ThePhongPanel extends JPanel {
        private static final int BAN_KINH = 8;

        private Color mauNen;
        private boolean dangChon;
        private boolean dangReChuot;

        private ThePhongPanel(Color mauNen, boolean dangChon) {
            this.mauNen = mauNen;
            this.dangChon = dangChon;
            setOpaque(false);
        }

        private void datTrangThaiChon(Color mauNenMoi, boolean dangChonMoi) {
            if (!Objects.equals(this.mauNen, mauNenMoi) || this.dangChon != dangChonMoi) {
                this.mauNen = mauNenMoi;
                this.dangChon = dangChonMoi;
                repaint();
            }
        }

        private void datReChuot(boolean reChuot) {
            if (this.dangReChuot != reChuot) {
                this.dangReChuot = reChuot;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics doHoa) {
            Graphics2D g2 = (Graphics2D) doHoa.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int rong = getWidth();
            int cao = getHeight();
            RoundRectangle2D.Float khung =
                    new RoundRectangle2D.Float(0.5f, 0.5f, rong - 1f, cao - 1f, BAN_KINH, BAN_KINH);
            g2.setColor(mauNen);
            g2.fill(khung);
            g2.setColor(
                    dangChon
                            ? MauUngDung.DO_THUONG_HIEU
                            : (dangReChuot ? MauUngDung.NAU_MO : MauUngDung.VIEN));
            g2.draw(khung);
            if (dangChon) {
                g2.draw(
                        new RoundRectangle2D.Float(
                                1.5f, 1.5f, rong - 3f, cao - 3f, BAN_KINH - 2f, BAN_KINH - 2f));
            }
            g2.dispose();
        }
    }

    private static JLabel taoDongHaiThe(PhongHotel phong) {
        boolean dangO =
                TrangThaiPhongHotel.DANG_O.maDB().equals(phong.getTrangThai())
                        && phong.getDangO() != null;
        String vanBan;
        if (dangO) {
            String tenThu =
                    phong.getDangO().getThuCung() != null
                            ? phong.getDangO().getThuCung().getTen()
                            : "";
            String tenKhach =
                    phong.getDangO().getKhachHang() != null
                            ? phong.getDangO().getKhachHang().getHoTen()
                            : "";
            vanBan = tenThu + (tenKhach.isBlank() ? "" : " · " + tenKhach);
        } else {
            vanBan =
                    DinhDang.dinhDang(phong.getGiaDem()) + "/ngày · Sức chứa " + phong.getSucChua();
        }
        JLabel l = new JLabel(vanBan);
        l.setFont(MauUngDung.taoPhongChu(dangO ? Font.BOLD : Font.PLAIN, 12));
        l.setForeground(MauUngDung.VAN_BAN_TOI);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JLabel taoDongBaThe(PhongHotel phong) {
        JLabel l = new JLabel();
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        boolean quaHan = laQuaHan(phong);
        l.setFont(MauUngDung.taoPhongChu(quaHan ? Font.BOLD : Font.PLAIN, 12));
        l.setForeground(quaHan ? MauUngDung.VAN_BAN_NGUY_HIEM : MauUngDung.VAN_BAN_XAM);

        int soDatTruoc = phong.getDatTruoc() == null ? 0 : phong.getDatTruoc().size();
        if (TrangThaiPhongHotel.DANG_O.maDB().equals(phong.getTrangThai())
                && phong.getDangO() != null) {
            LocalDateTime ra = phong.getDangO().getNgayRa();
            String chuRa = ra == null ? "" : "Trả: " + ra.format(DinhDang.NGAY_THANG_GIO_PHUT);
            l.setText(chuRa + (soDatTruoc > 0 ? " · +" + soDatTruoc + " đặt" : ""));
        } else if (soDatTruoc > 0) {
            LocalDateTime vao = phong.getDatTruoc().get(0).getNgayVao();
            String chuVao = vao == null ? "" : vao.format(DinhDang.NGAY_THANG_GIO_PHUT);
            l.setText("Đặt tới: " + chuVao + " (" + soDatTruoc + ")");
            l.setForeground(MauUngDung.CAM);
        } else if (TrangThaiPhongHotel.DANG_DON.maDB().equals(phong.getTrangThai())) {
            l.setText("Chờ xác nhận dọn phòng");
        } else if (TrangThaiPhongHotel.BAO_TRI.maDB().equals(phong.getTrangThai())) {
            l.setText("Tạm ngưng phục vụ");
        } else {
            l.setText("Sẵn sàng đón khách");
        }
        return l;
    }

    // 5. PANEL CHI TIẾT & THAO TÁC HỢP ĐỒNG PHÒNG

    private void chonMacDinhSauKhiLoc() {
        PhongHotel phongChon = timPhongTheoMa(maPhongDangChon);
        if (phongChon == null && !cacPhongHienThi.isEmpty()) {
            phongChon = cacPhongHienThi.get(0);
            maPhongDangChon = phongChon.getMaPhong();
        }
        capNhatPanelChiTiet(phongChon);
    }

    private void chonPhongVaoPanelChiTiet(PhongHotel phong) {
        String maCu = maPhongDangChon;
        String maMoi = phong == null ? null : phong.getMaPhong();
        maPhongDangChon = maMoi;
        if (maCu != null && !Objects.equals(maCu, maMoi)) {
            ThePhongPanel theCu = theTheoMaPhong.get(maCu);
            if (theCu != null) {
                theCu.datTrangThaiChon(
                        mauGocTheoMaPhong.getOrDefault(maCu, MauUngDung.THE_NEN), false);
            }
        }
        if (maMoi != null) {
            ThePhongPanel theMoi = theTheoMaPhong.get(maMoi);
            if (theMoi != null) {
                theMoi.datTrangThaiChon(MauUngDung.KEM_NEN_NHAN, true);
            }
        }
        capNhatPanelChiTiet(phong);
    }

    private PhongHotel timPhongTheoMa(String maPhong) {
        if (maPhong == null) {
            return null;
        }
        return cacPhongHienThi.stream()
                .filter(p -> maPhong.equals(p.getMaPhong()))
                .findFirst()
                .orElse(null);
    }

    private void capNhatPanelChiTiet(PhongHotel phong) {
        khungChiTietNoiDung.removeAll();
        if (phong == null) {
            khungChiTietNoiDung.add(
                    layTrongTrangThai(
                            "Chưa chọn phòng",
                            "Nhấp vào một thẻ phòng bên trái để xem chi tiết.",
                            null),
                    BorderLayout.CENTER);
        } else {
            JPanel vung = new JPanel();
            vung.setLayout(new BoxLayout(vung, BoxLayout.Y_AXIS));
            vung.setOpaque(false);
            veNoiDungChiTietPhong(vung, phong);
            khungChiTietNoiDung.add(vung, BorderLayout.NORTH);
        }
        khungChiTietNoiDung.revalidate();
        khungChiTietNoiDung.repaint();
    }

    private void veNoiDungChiTietPhong(JPanel vung, PhongHotel phong) {
        Color[] bangMau = layBangMauTrangThai(phong);
        JPanel dau = new JPanel(new BorderLayout(8, 0));
        dau.setOpaque(false);
        dau.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel nhanMa = new JLabel("Phòng " + phong.getMaPhong());
        nhanMa.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        nhanMa.setForeground(MauUngDung.NAU);
        dau.add(nhanMa, BorderLayout.WEST);
        dau.add(taoHuyHieu(layNhanHuyHieu(phong), bangMau[2], bangMau[3]), BorderLayout.EAST);
        vung.add(dau);

        JLabel thongTinChung =
                new JLabel(
                        Objects.toString(phong.getLoaiPhong(), "")
                                + " · Sức chứa "
                                + phong.getSucChua()
                                + " · "
                                + DinhDang.dinhDang(phong.getGiaDem())
                                + "/ngày");
        thongTinChung.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        thongTinChung.setForeground(MauUngDung.VAN_BAN_XAM);
        thongTinChung.setAlignmentX(Component.LEFT_ALIGNMENT);
        vung.add(Box.createVerticalStrut(4));
        vung.add(thongTinChung);

        if (phong.getGhiChu() != null && !phong.getGhiChu().isBlank()) {
            JLabel ghiChu =
                    new JLabel(
                            "<html>Ghi chú phòng: "
                                    + DinhDang.thoatHtml(phong.getGhiChu())
                                    + "</html>");
            ghiChu.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 12));
            ghiChu.setForeground(MauUngDung.VAN_BAN_XAM);
            ghiChu.setAlignmentX(Component.LEFT_ALIGNMENT);
            vung.add(Box.createVerticalStrut(4));
            vung.add(ghiChu);
        }

        veHanhDongTheoTrangThaiPhong(vung, phong);
        if (phong.getDangO() != null) {
            veKhoiHopDongDangO(vung, phong, phong.getDangO());
        }
        if (phong.getDatTruoc() != null && !phong.getDatTruoc().isEmpty()) {
            veKhoiDatTruoc(vung, phong, phong.getDatTruoc());
        }
    }

    private void veHanhDongTheoTrangThaiPhong(JPanel vung, PhongHotel phong) {
        String tt = Objects.toString(phong.getTrangThai(), "");
        String maPhong = phong.getMaPhong();
        if (TrangThaiPhongHotel.TRONG.maDB().equals(tt)) {
            themNutRong(
                    vung,
                    "+ Đặt phòng này",
                    NutDinhDang.KieuNut.CHINH,
                    40,
                    () -> datPhongTrongHoacChon(phong));
        } else if (TrangThaiPhongHotel.DANG_DON.maDB().equals(tt)) {
            themNutRong(
                    vung,
                    "Xác nhận dọn phòng xong",
                    NutDinhDang.KieuNut.THANH_CONG,
                    40,
                    () -> xacNhanDonPhong(maPhong));
        } else if (TrangThaiPhongHotel.BAO_TRI.maDB().equals(tt)) {
            themNutRong(
                    vung,
                    "Hoàn tất bảo trì",
                    NutDinhDang.KieuNut.CHINH,
                    40,
                    () -> xacNhanHoanTatBaoTri(maPhong));
        }
    }

    private void veKhoiHopDongDangO(JPanel vung, PhongHotel phong, HotelDTO.LichPhong lich) {
        HopDongLuuTru hd = lich.getHopDong();
        if (hd == null) {
            return;
        }
        String maHd = hd.getMaHopDong();
        vung.add(Box.createVerticalStrut(12));
        vung.add(taoTheThongTinLich("Đang lưu trú · #" + maHd, lich, laQuaHan(phong)));
        NutDinhDang btnQuyetToan =
                themNutRong(
                        vung,
                        "Trả phòng & Quyết toán",
                        NutDinhDang.KieuNut.THANH_CONG,
                        40,
                        () -> quyetToanHopDong(maHd));
        if (!coQuyenThanhToan()) {
            btnQuyetToan.setEnabled(false);
            btnQuyetToan.setToolTipText("Bạn không có quyền thanh toán / quyết toán.");
        }

        JPanel luoiPhu = new JPanel(new GridLayout(1, 2, 8, 0));
        luoiPhu.setOpaque(false);
        luoiPhu.setAlignmentX(Component.LEFT_ALIGNMENT);
        NutDinhDang btnChamSoc =
                taoNut(
                        "Phiếu chăm sóc",
                        NutDinhDang.KieuNut.PHU,
                        () -> {
                            NhatKyChamSocDialog.mo(this, "HOTEL", maHd);
                            lamMoiVaThongBao();
                        });
        NutDinhDang btnGiaHan =
                taoNut("Gia hạn phòng", NutDinhDang.KieuNut.PHU, () -> suaHopDong(maHd));
        luoiPhu.add(btnChamSoc);
        luoiPhu.add(btnGiaHan);
        vung.add(Box.createVerticalStrut(8));
        vung.add(luoiPhu);

        if (tinhCocConThieu(hd) > 0) {
            themNutRong(
                    vung,
                    "Thu cọc còn thiếu",
                    NutDinhDang.KieuNut.PHU,
                    36,
                    () -> thuCocHopDong(maHd));
        }

        themNutRong(
                vung, "Lịch sử cọc", NutDinhDang.KieuNut.TRONG_SUOT, 34, () -> xemLichSuCoc(maHd));
    }

    private void veKhoiDatTruoc(
            JPanel vung, PhongHotel phong, List<HotelDTO.LichPhong> dsDatTruoc) {
        vung.add(Box.createVerticalStrut(14));
        JLabel tieuDe = taoTieuDeMuc("Lịch đặt trước (" + dsDatTruoc.size() + ")");
        tieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);
        vung.add(tieuDe);

        JComboBox<String> cboDatTruoc = new JComboBox<>();
        for (HotelDTO.LichPhong l : dsDatTruoc) {
            String ma = l.getHopDong() != null ? l.getHopDong().getMaHopDong() : "";
            String thu = l.getThuCung() != null ? l.getThuCung().getTen() : "";
            String vao =
                    l.getNgayVao() != null
                            ? l.getNgayVao().format(DinhDang.NGAY_THANG_GIO_PHUT)
                            : "";
            cboDatTruoc.addItem("#" + ma + " · " + thu + " (" + vao + ")");
        }
        cboDatTruoc.setMaximumSize(new Dimension(Integer.MAX_VALUE, MauUngDung.CAO_DAU_VAO));
        cboDatTruoc.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel vungChiTietDat = new JPanel();
        vungChiTietDat.setLayout(new BoxLayout(vungChiTietDat, BoxLayout.Y_AXIS));
        vungChiTietDat.setOpaque(false);
        vungChiTietDat.setAlignmentX(Component.LEFT_ALIGNMENT);

        Runnable capNhatChonDat =
                () -> {
                    int idx = Math.max(0, cboDatTruoc.getSelectedIndex());
                    vungChiTietDat.removeAll();
                    veChiTietMotDatTruoc(vungChiTietDat, phong, dsDatTruoc.get(idx));
                    vungChiTietDat.revalidate();
                    vungChiTietDat.repaint();
                };
        cboDatTruoc.addActionListener(e -> capNhatChonDat.run());
        if (dsDatTruoc.size() > 1) {
            vung.add(Box.createVerticalStrut(6));
            vung.add(cboDatTruoc);
        }
        vung.add(Box.createVerticalStrut(6));
        vung.add(vungChiTietDat);
        capNhatChonDat.run();
    }

    private void veChiTietMotDatTruoc(JPanel vung, PhongHotel phong, HotelDTO.LichPhong lich) {
        HopDongLuuTru hd = lich.getHopDong();
        if (hd == null) {
            return;
        }
        String maHd = hd.getMaHopDong();
        vung.add(taoTheThongTinLich("Đặt trước · #" + maHd, lich, false));
        NutDinhDang btnNhanPhong =
                themNutRong(
                        vung,
                        "Nhận phòng (Check-in)",
                        NutDinhDang.KieuNut.CHINH,
                        40,
                        () -> nhanPhongHopDong(maHd));
        long thieuCoc = tinhCocConThieu(hd);
        boolean phongTrong = phong != null && phong.kiemTraPhongTrong();
        if (thieuCoc > 0 || !phongTrong) {
            btnNhanPhong.setEnabled(false);
            btnNhanPhong.setToolTipText(
                    thieuCoc > 0
                            ? "Cần thu đủ tiền cọc yêu cầu trước khi nhận phòng."
                            : "Phòng hiện chưa sẵn sàng (chưa ở trạng thái Trống).");
        }

        JPanel luoiPhu = new JPanel(new GridLayout(1, 2, 8, 0));
        luoiPhu.setOpaque(false);
        luoiPhu.setAlignmentX(Component.LEFT_ALIGNMENT);
        NutDinhDang btnSua =
                taoNut("Sửa đặt phòng", NutDinhDang.KieuNut.PHU, () -> suaHopDong(maHd));
        if (thieuCoc > 0) {
            NutDinhDang btnThuCoc =
                    taoNut("Thu cọc", NutDinhDang.KieuNut.PHU, () -> thuCocHopDong(maHd));
            luoiPhu.add(btnThuCoc);
            luoiPhu.add(btnSua);
        } else {
            NutDinhDang btnCoc =
                    taoNut("Lịch sử cọc", NutDinhDang.KieuNut.PHU, () -> xemLichSuCoc(maHd));
            luoiPhu.add(btnSua);
            luoiPhu.add(btnCoc);
        }
        vung.add(Box.createVerticalStrut(8));
        vung.add(luoiPhu);
        if (thieuCoc > 0) {
            themNutRong(
                    vung,
                    "Lịch sử cọc",
                    NutDinhDang.KieuNut.TRONG_SUOT,
                    MauUngDung.CAO_NUT,
                    () -> xemLichSuCoc(maHd));
        }

        JSeparator vach = new JSeparator(SwingConstants.HORIZONTAL);
        vach.setForeground(MauUngDung.VIEN);
        vach.setAlignmentX(Component.LEFT_ALIGNMENT);
        vung.add(Box.createVerticalStrut(12));
        vung.add(vach);
        NutDinhDang btnHuyDat =
                themNutRong(
                        vung,
                        "Hủy đặt phòng",
                        NutDinhDang.KieuNut.NGUY_HIEM,
                        MauUngDung.CAO_NUT,
                        () -> huyDatHopDong(maHd));
        if (hd.getTienCoc() > 0) {
            btnHuyDat.setEnabled(false);
            btnHuyDat.setToolTipText(
                    "Hợp đồng đã thu tiền cọc ("
                            + DinhDang.dinhDang(hd.getTienCoc())
                            + "), không thể hủy trực tiếp.");
        }
    }

    private JPanel taoTheThongTinLich(String tieuDe, HotelDTO.LichPhong lich, boolean quaHan) {
        Color mauNen = quaHan ? MauUngDung.KHONG_HOP_LE_NEN : MauUngDung.KEM_NEN_NHAN;
        Color mauVien = quaHan ? MauUngDung.VAN_BAN_NGUY_HIEM : MauUngDung.VIEN;
        BoGocPanel the =
                new BoGocPanel(10, mauNen, false) {
                    @Override
                    protected void paintBorder(Graphics doHoa) {
                        Graphics2D g2 = (Graphics2D) doHoa.create();
                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(mauVien);
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                        g2.dispose();
                    }
                };
        the.setLayout(new GridBagLayout());
        the.setAlignmentX(Component.LEFT_ALIGNMENT);
        the.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel nhanTieuDe = new JLabel(tieuDe);
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanTieuDe.setForeground(quaHan ? MauUngDung.VAN_BAN_NGUY_HIEM : MauUngDung.NAU);
        GridBagConstraints gDau = new GridBagConstraints();
        gDau.gridx = 0;
        gDau.gridy = 0;
        gDau.gridwidth = 2;
        gDau.weightx = 1;
        gDau.fill = GridBagConstraints.HORIZONTAL;
        gDau.insets = new Insets(0, 0, 6, 0);
        the.add(nhanTieuDe, gDau);

        HopDongLuuTru hd = lich.getHopDong();
        String tenThu = lich.getThuCung() != null ? lich.getThuCung().getTen() : "—";
        String tenKhach = lich.getKhachHang() != null ? lich.getKhachHang().getHoTen() : "—";
        String sdt = lich.getKhachHang() != null ? lich.getKhachHang().getSoDienThoai() : "";
        String chuHienThi = sdt != null && !sdt.isBlank() ? tenKhach + " · " + sdt : tenKhach;
        long daThu = hd == null ? 0 : hd.getTienCoc();
        long yeuCau = hd == null || hd.getCocYeuCau() == null ? 0 : hd.getCocYeuCau();

        themDongThongTin(the, 1, "Thú cưng", tenThu, true);
        themDongThongTin(the, 2, "Chủ nuôi", chuHienThi, false);
        themDongThongTin(the, 3, "Nhận phòng", DinhDang.ngayGio(lich.getNgayVao()), false);
        themDongThongTin(the, 4, "Trả dự kiến", DinhDang.ngayGio(lich.getNgayRa()), quaHan);
        themDongThongTin(
                the,
                5,
                "Tiền cọc",
                DinhDang.dinhDang(daThu) + " / " + DinhDang.dinhDang(yeuCau),
                false);
        return the;
    }

    private static void themDongThongTin(
            JPanel bang, int hang, String nhan, String giaTri, boolean nhanManh) {
        GridBagConstraints gTrai = new GridBagConstraints();
        gTrai.gridx = 0;
        gTrai.gridy = hang;
        gTrai.anchor = GridBagConstraints.WEST;
        gTrai.insets = new Insets(2, 0, 2, 10);
        JLabel nhanMuc = new JLabel(nhan);
        nhanMuc.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanMuc.setForeground(MauUngDung.VAN_BAN_XAM);
        bang.add(nhanMuc, gTrai);

        GridBagConstraints gPhai = new GridBagConstraints();
        gPhai.gridx = 1;
        gPhai.gridy = hang;
        gPhai.weightx = 1;
        gPhai.fill = GridBagConstraints.HORIZONTAL;
        gPhai.insets = new Insets(2, 0, 2, 0);
        JLabel nhanGiaTri = new JLabel(giaTri);
        nhanGiaTri.setFont(MauUngDung.taoPhongChu(nhanManh ? Font.BOLD : Font.PLAIN, 12));
        nhanGiaTri.setForeground(MauUngDung.VAN_BAN_TOI);
        bang.add(nhanGiaTri, gPhai);
    }

    private static NutDinhDang themNutRong(
            JPanel vung, String nhan, NutDinhDang.KieuNut kieu, int chieuCao, Runnable hanhDong) {
        NutDinhDang btn = taoNut(nhan, kieu, hanhDong);
        btn.setPreferredSize(new Dimension(280, chieuCao));
        btn.setMinimumSize(new Dimension(120, chieuCao));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, chieuCao));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        vung.add(Box.createVerticalStrut(8));
        vung.add(btn);
        return btn;
    }

    private static NutDinhDang taoNut(String nhan, NutDinhDang.KieuNut kieu, Runnable hanhDong) {
        NutDinhDang btn = new NutDinhDang(nhan, kieu);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (hanhDong != null) {
            btn.addActionListener(e -> hanhDong.run());
        }
        return btn;
    }

    private JLabel taoHuyHieu(String vanBan, Color nen, Color chu) {
        return layNhan(vanBan, nen, chu);
    }

    private void datPhongTrongHoacChon(PhongHotel phong) {
        String maPhongMacDinh =
                phong != null && TrangThaiPhongHotel.TRONG.maDB().equals(phong.getTrangThai())
                        ? phong.getMaPhong()
                        : null;
        if (gio != null) {
            gio.getQuyTrinh().chonHotel("");
            if (maPhongMacDinh != null) {
                gio.getQuyTrinh().chonPhongHotel(maPhongMacDinh);
            }
        }
        if (DatPhongHotelDialog.mo(this, null, maPhongMacDinh) != null) {
            lamMoiVaThongBao();
        }
    }

    private void thuCocHopDong(String maHd) {
        if (hoTroHopDong != null) {
            hoTroHopDong.thuCocChoHopDong(maHd, maHd, this::lamMoiVaThongBao);
            return;
        }
        HopThoai.hienThiThongTin(this, "Vui lòng thu cọc tại tab Danh sách hợp đồng.");
    }

    private void nhanPhongHopDong(String maHd) {
        if (!HopThoai.xacNhan(this, "Xác nhận check-in (nhận phòng) cho hợp đồng #" + maHd + "?")) {
            return;
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.kiemTraVao(maHd),
                () -> {
                    lamMoiVaThongBao();
                    HopThoai.hienThiThanhCong(this, "Đã nhận phòng hợp đồng #" + maHd + ".");
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private void quyetToanHopDong(String maHd) {
        if (!coQuyenThanhToan()) {
            HopThoai.hienThiCanhBao(this, "Bạn không có quyền thanh toán / quyết toán.");
            return;
        }
        if (hoTroHopDong != null) {
            hoTroHopDong.quyetToanChoHopDong("HOTEL", maHd);
            return;
        }
        if (gio != null && !gio.kiemTraTrong()) {
            if (!HopThoai.xacNhanDongY(
                    this,
                    "Giỏ hàng đang có sản phẩm. Làm trống để chuyển sang quyết toán lưu trú?",
                    "Giỏ hàng đang có sản phẩm")) {
                return;
            }
            gio.xoa();
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> banHangService.quyetToan("HOTEL", maHd, gio),
                () -> {
                    if (dieuHuong != null) {
                        dieuHuong.accept(MaManHinh.THANH_TOAN);
                    }
                });
    }

    private void suaHopDong(String maHd) {
        if (DatPhongHotelDialog.mo(this, maHd, null) != null) {
            lamMoiVaThongBao();
        }
    }

    private void huyDatHopDong(String maHd) {
        if (hoTroHopDong != null) {
            hoTroHopDong.huyDatPhongChoHopDong(maHd, this::lamMoiVaThongBao);
            return;
        }
        String lyDo =
                HopThoai.nhapChuoiThuan(
                        this, "Nhập lý do hủy đặt phòng #" + maHd + ":", "Hủy đặt phòng #" + maHd);
        if (lyDo == null) {
            return;
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.huyDatPhong(maHd, lyDo),
                this::lamMoiVaThongBao);
    }

    private void xemLichSuCoc(String maHd) {
        if (hoTroHopDong != null) {
            hoTroHopDong.lichSuCocChoHopDong(maHd);
        }
    }

    private void lamMoiVaThongBao() {
        nap();
        if (khiDuLieuThayDoi != null) {
            khiDuLieuThayDoi.run();
        }
    }

    private static long tinhCocConThieu(HopDongLuuTru hd) {
        if (hd == null || hd.getCocYeuCau() == null) {
            return 0;
        }
        return Math.max(0, hd.getCocYeuCau() - hd.getTienCoc());
    }

    public void moPhong(PhongHotel phong) {
        if (phong == null) {
            return;
        }
        String trangThai = Objects.toString(phong.getTrangThai(), "");
        String maPhong = Objects.toString(phong.getMaPhong(), "");
        if (TrangThaiPhongHotel.DANG_DON.maDB().equals(trangThai)) {
            xacNhanDonPhong(maPhong);
            return;
        }
        if (TrangThaiPhongHotel.BAO_TRI.maDB().equals(trangThai)) {
            xacNhanHoanTatBaoTri(maPhong);
            return;
        }
        if (!List.of(TrangThaiPhongHotel.TRONG.maDB(), TrangThaiPhongHotel.DANG_O.maDB())
                .contains(trangThai)) {
            HopThoai.hienThiThongTin(this, "Phòng " + maPhong + " hiện chưa sẵn sàng thao tác.");
            return;
        }
        if (gio != null) {
            gio.getQuyTrinh().chonPhongHotel(maPhong);
        }
        if (khiChonPhong != null) {
            khiChonPhong.accept(phong);
            return;
        }
        if (TrangThaiPhongHotel.DANG_O.maDB().equals(trangThai)) {
            moThaoTacPhongDangO(phong, maPhong);
            return;
        }
        datPhongTrongHoacChon(phong);
    }

    private void moThaoTacPhongDangO(PhongHotel phong, String maPhong) {
        if (phong.getDangO() == null || phong.getDangO().getHopDong() == null) {
            HopThoai.hienThiCanhBao(this, "Chưa tìm thấy hợp đồng đang ở. Hãy làm mới sơ đồ.");
            return;
        }
        String maHd = phong.getDangO().getHopDong().getMaHopDong();
        if (gio != null) {
            gio.getQuyTrinh().chonHotel(maHd);
        }
        int luaChon =
                HopThoai.tuyChon(
                        this,
                        "Phòng " + maPhong + " đang phục vụ hợp đồng #" + maHd + ".",
                        "Thao tác phòng đang lưu trú",
                        new String[] {"Xem / Check-out", "Nhật ký chăm sóc", "Đóng"},
                        false);
        if (luaChon == 0) {
            DieuHuongBanGhiService.macDinh().dat(MaManHinh.QUAN_LY_LUU_TRU, maHd);
            if (dieuHuong != null) {
                dieuHuong.accept(MaManHinh.QUAN_LY_LUU_TRU);
            }
        } else if (luaChon == 1) {
            NhatKyChamSocDialog.mo(this, "HOTEL", maHd);
        }
    }

    private void xacNhanDonPhong(String maPhong) {
        if (!HopThoai.xacNhan(
                this, "Xác nhận phòng " + maPhong + " đã dọn xong và chuyển sang Trống?")) {
            return;
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.hoanTatDonPhong(maPhong),
                () -> {
                    lamMoiVaThongBao();
                    HopThoai.hienThiThanhCong(
                            this, "Phòng " + maPhong + " đã dọn xong và sẵn sàng đón khách.");
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private void xacNhanHoanTatBaoTri(String maPhong) {
        if (!HopThoai.xacNhan(
                this,
                "Phòng "
                        + maPhong
                        + " đang ở trạng thái Bảo trì.\n"
                        + "Bạn có muốn chuyển phòng sang Trống để sẵn sàng đón khách không?")) {
            return;
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.hoanTatBaoTriPhong(maPhong),
                () -> {
                    lamMoiVaThongBao();
                    HopThoai.hienThiThanhCong(
                            this, "Đã chuyển phòng " + maPhong + " sang trạng thái Trống.");
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private static Color[] layBangMauTrangThai(PhongHotel phong) {
        String tt = Objects.toString(phong.getTrangThai(), "");
        if (TrangThaiPhongHotel.TRONG.maDB().equals(tt)) {
            return new Color[] {
                MauUngDung.XANH_LA,
                MauUngDung.THE_NEN,
                MauUngDung.XANH_LA_NEN_NHAT,
                MauUngDung.XANH_LA
            };
        }
        if (TrangThaiPhongHotel.DANG_DON.maDB().equals(tt)) {
            return new Color[] {
                MauUngDung.CAM, MauUngDung.THE_NEN, MauUngDung.CANH_BAO_NEN, MauUngDung.CAM
            };
        }
        if (TrangThaiPhongHotel.BAO_TRI.maDB().equals(tt)) {
            return new Color[] {
                MauUngDung.VAN_BAN_XAM,
                MauUngDung.THE_NEN,
                MauUngDung.XAM_NEN_NHAT,
                MauUngDung.VAN_BAN_XAM
            };
        }
        if (laQuaHan(phong)) {
            return new Color[] {
                MauUngDung.VAN_BAN_NGUY_HIEM,
                MauUngDung.THE_NEN,
                MauUngDung.KHONG_HOP_LE_NEN,
                MauUngDung.VAN_BAN_NGUY_HIEM
            };
        }
        return new Color[] {
            MauUngDung.NAU, MauUngDung.THE_NEN, MauUngDung.KEM_NEN_NHAN, MauUngDung.NAU
        };
    }

    private static String layNhanHuyHieu(PhongHotel phong) {
        String tt = Objects.toString(phong.getTrangThai(), "");
        if (TrangThaiPhongHotel.TRONG.maDB().equals(tt)) return "Trống";
        if (TrangThaiPhongHotel.DANG_DON.maDB().equals(tt)) return "Đang dọn";
        if (TrangThaiPhongHotel.BAO_TRI.maDB().equals(tt)) return "Bảo trì";
        return laQuaHan(phong) ? "Quá hạn" : "Đang ở";
    }

    private static boolean laQuaHan(PhongHotel p) {
        return p != null
                && TrangThaiPhongHotel.DANG_O.maDB().equals(p.getTrangThai())
                && p.getDangO() != null
                && p.getDangO().getNgayRa() != null
                && p.getDangO().getNgayRa().isBefore(LocalDateTime.now());
    }

    // 6. ĐỊNH DẠNG CHI TIẾT, MÀU TRẠNG THÁI & IN SƠ ĐỒ

    public static String layChiTiet(PhongHotel phong) {
        if (phong == null) {
            return "";
        }
        String trangThaiNhan = Objects.toString(phong.getNhanTrangThai(), "");
        if (laQuaHan(phong)) {
            trangThaiNhan +=
                    " <span style='color:#B91C1C; font-weight:bold;'>(Quá hạn trả phòng!)</span>";
        }
        StringBuilder s =
                new StringBuilder("<html><body><b>Phòng ")
                        .append(DinhDang.thoatHtml(phong.getMaPhong()))
                        .append("</b><br>Loại: ")
                        .append(DinhDang.thoatHtml(phong.getLoaiPhong()))
                        .append("<br>Trạng thái: ")
                        .append(trangThaiNhan)
                        .append("<br>Sức chứa: ")
                        .append(phong.getSucChua())
                        .append("<br>Giá mỗi ngày (₫): ")
                        .append(DinhDang.dinhDang(phong.getGiaDem()));
        if (phong.getDangO() != null) {
            s.append("<hr><b>Đang lưu trú</b><br>").append(layLich(phong.getDangO()));
        }
        if (phong.getDatTruoc() != null && !phong.getDatTruoc().isEmpty()) {
            s.append("<hr><b>Lịch đặt gần nhất (tổng ")
                    .append(phong.getDatTruoc().size())
                    .append(")</b><br>")
                    .append(layLich(phong.getDatTruoc().get(0)));
        }
        return s.append("</body></html>").toString();
    }

    private static String layLich(HotelDTO.LichPhong lich) {
        if (lich == null) {
            return "";
        }
        var d = DinhDang.NGAY_GIO;
        String tenThu = lich.getThuCung() != null ? lich.getThuCung().getTen() : "";
        String tenKhach = lich.getKhachHang() != null ? lich.getKhachHang().getHoTen() : "";
        String vao = lich.getNgayVao() != null ? lich.getNgayVao().format(d) : "";
        String ra = lich.getNgayRa() != null ? lich.getNgayRa().format(d) : "";
        return "Thú cưng: "
                + DinhDang.thoatHtml(tenThu)
                + "<br>Chủ nuôi: "
                + DinhDang.thoatHtml(tenKhach)
                + "<br>Vào: "
                + vao
                + "<br>Ra dự kiến: "
                + ra;
    }

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
        int caoHang = 135;
        List<BufferedImage> ketQua = new ArrayList<>();
        for (int i = 0; i < dsIn.size(); i += cot) {
            JPanel hang = new JPanel(new GridLayout(1, cot, 12, 0));
            hang.setBackground(Color.WHITE);
            for (int j = 0; j < cot; j++) {
                if (i + j < dsIn.size()) {
                    hang.add(taoThe(dsIn.get(i + j)));
                } else {
                    JPanel oTrong = new JPanel();
                    oTrong.setOpaque(false);
                    hang.add(oTrong);
                }
            }
            hang.setSize(rongHang, caoHang);
            hang.doLayout();
            BufferedImage anh = new BufferedImage(rongHang, caoHang, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = anh.createGraphics();
            hang.paint(g2);
            g2.dispose();
            ketQua.add(anh);
        }
        return ketQua;
    }

    private static Printable taoBanInSoDo(List<BufferedImage> anhCacHang) {
        return (g, khung, trang) -> {
            double rongHang = 790.0;
            double caoHang = 135.0;
            double tiLe = Math.min(1.0, khung.getImageableWidth() / rongHang);
            double caoTrenTrang = caoHang * tiLe;
            double khoangCach = 12.0 * tiLe;
            int soHangMoiTrang =
                    Math.max(
                            1,
                            (int)
                                    ((khung.getImageableHeight() + khoangCach)
                                            / (caoTrenTrang + khoangCach)));
            int batDau = trang * soHangMoiTrang;
            if (batDau >= anhCacHang.size()) {
                return Printable.NO_SUCH_PAGE;
            }
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
                            (int) Math.round(y + (i - batDau) * (caoTrenTrang + khoangCach)),
                            w,
                            h,
                            null);
                }
            } finally {
                g2.dispose();
            }
            return Printable.PAGE_EXISTS;
        };
    }
}
