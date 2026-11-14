package com.petstation.gui.thongke;

import com.petstation.dto.ChiTietBaoCao;
import com.petstation.dto.DuLieuBaoCao.DuLieu;
import com.petstation.dto.DuLieuBaoCao.KieuHienThi;
import com.petstation.dto.KhoangNgay;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.BieuDoCot;
import com.petstation.gui.chung.thanhphan.BieuDoTron;
import com.petstation.gui.chung.thanhphan.BoChonKhoangNgay;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.gui.chung.thanhphan.XuatExcelDialog;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.thongke.ChiTietThongKeService;
import com.petstation.util.DinhDang;

import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

// Khung giao diện báo cáo thống kê dùng chung cho tất cả màn hình Thống kê
public class BaoCaoPanel extends CoSoPanel {

    // 1. CẤU TRÚC DỮ LIỆU & THÀNH PHẦN GIAO DIỆN

    private static final class KetQuaTai {
        final DuLieu duLieu;
        final ChiTietBaoCao chungTu;

        private KetQuaTai(DuLieu duLieu, ChiTietBaoCao chungTu) {
            this.duLieu = duLieu;
            this.chungTu = chungTu;
        }
    }

    private final List<KieuHienThi> cacKieuHienThi;
    private final ChiTietThongKeService chiTietThongKeService = new ChiTietThongKeService();
    private final BoChonKhoangNgay cboKy = new BoChonKhoangNgay();
    private final JComboBox<String> cboHienThiHop = new JComboBox<>();
    private final JComboBox<String> cboTongHop =
            new JComboBox<>(new String[] {"Ngày", "Tuần", "Tháng"});
    private final JPanel khungCacThe = new JPanel(new GridLayout(1, 4, 8, 0));
    private final BieuDoCot bieuDo = new BieuDoCot();
    private final BieuDoTron tron = new BieuDoTron();
    private final DefaultTableModel moHinh =
            new DefaultTableModel() {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
    private final JTable bangBang = new JTable(moHinh);
    private final JLabel nhanKhoang = new JLabel();
    private final String tenXuat;
    private KhoangNgay khoangDaApDung = KhoangNgay.ganDay(30);
    private DuLieu duLieu;
    private TongHopBieuDo.KetQua tongHop;
    private int phienTai;
    private ChiTietBaoCao chungTu;
    private KhoangNgay kyDuLieu = khoangDaApDung;
    private List<int[]> pieDocs;
    private final JButton xuat = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);
    private boolean dangDatLaiBoLoc;

    // 2. KHỞI TẠO BỐ CỤC MÀN HÌNH & GẮN SỰ KIỆN BỘ LỌC

    public BaoCaoPanel(String tieuDe, String phu, String tenXuat, List<KieuHienThi> cacKieu) {
        this(tieuDe, phu, tenXuat, cacKieu, true, false);
    }

    public BaoCaoPanel(
            String tieuDe,
            String phu,
            String tenXuat,
            List<KieuHienThi> cacKieu,
            boolean hienBoGop,
            boolean uuTienBang) {
        this.tenXuat = tenXuat;
        this.cacKieuHienThi =
                cacKieu.stream()
                        .filter(
                                k ->
                                        k.quyen() == null
                                                || PhienDangNhapService.macDinh()
                                                        .layPhien()
                                                        .kiemTraCo(k.quyen()))
                        .toList();
        setLayout(new BorderLayout(0, 10));
        cboKy.chonKy("30 ngày qua");
        for (var k : cacKieuHienThi) cboHienThiHop.addItem(k.ten());
        JPanel top = taoPhanDauBaoCao(tieuDe, phu, taoThanhCongCu(hienBoGop));
        DinhDangBang.apDung(bangBang);
        JScrollPane bar = taoKhungBieuDoNgang();
        JPanel khuVucBieuDo = taoKhuVucBieuDo(bar);
        JScrollPane scrBang = taoKhungBangDuLieu();
        JPanel khuVucNoiDung = taoKhuVucNoiDung(uuTienBang, scrBang, khuVucBieuDo);
        JScrollPane scrTrang = taoKhungCuonTrang(top, khuVucNoiDung);
        chuyenTiepCuonChuot(scrBang, scrTrang);
        chuyenTiepCuonChuot(bar, scrTrang);
        add(scrTrang, BorderLayout.CENTER);
        ganSuKienBieuDo();
        dungLai();
    }

    private ThanhDanhSach taoThanhCongCu(boolean hienBoGop) {
        ThanhDanhSach tools = new ThanhDanhSach();
        tools.themBoLoc(cboKy);
        tools.themBoLoc("Xem theo:", cboHienThiHop);
        if (hienBoGop) {
            tools.themBoLoc("Biểu đồ:", cboTongHop);
            if (cboTongHop.getParent() != null) cboTongHop.getParent().setVisible(false);
        }
        tools.add(xuat);
        xuat.addActionListener(
                e ->
                        XuatExcelDialog.xuatBang(
                                this, bangBang, tenXuat + "_" + nhanKhoang.getText()));
        cboKy.addPropertyChangeListener(
                "dateRange",
                e -> {
                    if (dangDatLaiBoLoc) return;
                    try {
                        if (apDungKhoangNgayNeuHopLe()) dungLai();
                    } catch (RuntimeException ex) {
                        LoggerFactory.getLogger(BaoCaoPanel.class)
                                .debug(
                                        "Bỏ qua cập nhật báo cáo khi khoảng ngày chưa hoàn chỉnh:"
                                                + " {}",
                                        ex.getMessage());
                    }
                });
        cboHienThiHop.addActionListener(
                e -> {
                    if (dangDatLaiBoLoc) return;
                    apDungKhoangNgayNeuHopLe();
                    dungLai();
                });
        cboTongHop.addActionListener(
                e -> {
                    if (!dangDatLaiBoLoc) veBieuDo();
                });
        return tools;
    }

    private boolean apDungKhoangNgayNeuHopLe() {
        var tu = cboKy.layNgayBatDau();
        var den = cboKy.layNgayKetThuc();
        if (tu == null || den == null) return false;
        if (tu.isAfter(den)) {
            nhanKhoang.setText("Khoảng ngày chưa hợp lệ: Từ ngày đang lớn hơn Đến ngày");
            return false;
        }
        khoangDaApDung = new KhoangNgay(tu, den);
        return true;
    }

    private JPanel taoPhanDauBaoCao(String tieuDe, String phu, ThanhDanhSach tools) {
        JPanel header = taoVungTrongSuot(new BorderLayout(0, 8));
        header.add(taoTieuDeManHinh(tieuDe, phu), BorderLayout.NORTH);
        header.add(tools);
        nhanKhoang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanKhoang.setForeground(MauUngDung.VAN_BAN_XAM);
        header.add(nhanKhoang, BorderLayout.SOUTH);
        JPanel top = taoVungTrongSuot(new BorderLayout(0, 8));
        top.add(header, BorderLayout.NORTH);
        khungCacThe.setOpaque(false);
        top.add(khungCacThe);
        return top;
    }

    private JScrollPane taoKhungBieuDoNgang() {
        JScrollPane bar =
                new JScrollPane(
                        bieuDo,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        bar.setBorder(BorderFactory.createEmptyBorder());
        bar.getViewport().setBackground(Color.WHITE);
        bar.getHorizontalScrollBar().setUnitIncrement(24);
        return bar;
    }

    private JPanel taoKhuVucBieuDo(JScrollPane bar) {
        JPanel khungCot = taoTheCoVien(new BorderLayout(0, 8));
        JLabel nhanTieuDeCot = new JLabel(layTieuDeBieuDo());
        nhanTieuDeCot.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanTieuDeCot.setForeground(MauUngDung.XAM_DAM);
        khungCot.add(nhanTieuDeCot, BorderLayout.NORTH);
        khungCot.add(bar, BorderLayout.CENTER);
        JPanel khungTron = taoTheCoVien(new BorderLayout(0, 8));
        khungTron.add(tron, BorderLayout.CENTER);
        JPanel khuVucBieuDo = taoVungTrongSuot(new GridBagLayout());
        khuVucBieuDo.setMinimumSize(new Dimension(300, 220));
        khuVucBieuDo.setPreferredSize(new Dimension(760, 235));
        themCotBieuDo(khuVucBieuDo, khungCot, 0, 0.58, new Insets(0, 0, 0, 5));
        themCotBieuDo(khuVucBieuDo, khungTron, 1, 0.42, new Insets(0, 5, 0, 0));
        return khuVucBieuDo;
    }

    private JPanel taoTheCoVien(BorderLayout layout) {
        JPanel khungTam = layThe(layout);
        khungTam.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        return khungTam;
    }

    private static void themCotBieuDo(JPanel cha, JPanel con, int cot, double trongSoX, Insets le) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = cot;
        gbc.gridy = 0;
        gbc.weightx = trongSoX;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = le;
        cha.add(con, gbc);
    }

    private JScrollPane taoKhungBangDuLieu() {
        int chieuCao6Dong = bangBang.getRowHeight() * 6;
        int chieuCaoBangToiThieu =
                chieuCao6Dong + bangBang.getTableHeader().getPreferredSize().height + 6;
        bangBang.setPreferredScrollableViewportSize(new Dimension(760, chieuCao6Dong));
        bangBang.getTableHeader().setOpaque(true);
        JScrollPane scrBang =
                new JScrollPane(
                        bangBang,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrBang.setColumnHeaderView(bangBang.getTableHeader());
        scrBang.setBorder(BorderFactory.createLineBorder(MauUngDung.XAM_VIEN_NHE));
        scrBang.getViewport().setBackground(Color.WHITE);
        if (scrBang.getColumnHeader() != null)
            scrBang.getColumnHeader().setBackground(MauUngDung.KEM_NEN_NHAN);
        scrBang.setMinimumSize(new Dimension(300, chieuCaoBangToiThieu));
        scrBang.setPreferredSize(new Dimension(760, Math.max(220, chieuCaoBangToiThieu)));
        return scrBang;
    }

    private JPanel taoKhuVucNoiDung(boolean uuTienBang, JScrollPane scrBang, JPanel khuVucBieuDo) {
        JPanel khuVucNoiDung = taoVungTrongSuot(new GridBagLayout());
        themVungTheoTyLe(khuVucNoiDung, uuTienBang ? scrBang : khuVucBieuDo, 0, 0.56);
        themVungTheoTyLe(khuVucNoiDung, uuTienBang ? khuVucBieuDo : scrBang, 1, 0.44);
        return khuVucNoiDung;
    }

    private static JScrollPane taoKhungCuonTrang(JPanel top, JPanel khuVucNoiDung) {
        TrangCuonPanel khungTrang = new TrangCuonPanel(new BorderLayout(0, 10));
        khungTrang.setOpaque(false);
        khungTrang.add(top, BorderLayout.NORTH);
        khungTrang.add(khuVucNoiDung, BorderLayout.CENTER);
        JScrollPane scrTrang =
                new JScrollPane(
                        khungTrang,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrTrang.setBorder(BorderFactory.createEmptyBorder());
        scrTrang.setOpaque(false);
        scrTrang.getViewport().setOpaque(false);
        scrTrang.getVerticalScrollBar().setUnitIncrement(24);
        return scrTrang;
    }

    private void ganSuKienBieuDo() {
        bieuDo.khiChon(
                i -> {
                    if (tongHop != null && i < tongHop.dongGoc().size())
                        chiTiet(tongHop.dongGoc().get(i));
                });
        tron.khiChon(
                indices -> {
                    if (tongHop == null) return;
                    if (pieDocs != null && chungTu != null) {
                        int[] rows =
                                Arrays.stream(indices)
                                        .flatMap(i -> Arrays.stream(pieDocs.get(i)))
                                        .toArray();
                        moBang(
                                chungTu.getCot(),
                                Arrays.stream(rows)
                                        .mapToObj(i -> chungTu.getDong().get(i))
                                        .toArray(Object[][]::new));
                    } else {
                        chiTiet(
                                Arrays.stream(indices)
                                        .flatMap(i -> Arrays.stream(tongHop.dongGoc().get(i)))
                                        .toArray());
                    }
                });
    }

    // 3. TẢI DỮ LIỆU BẤT ĐỒNG BỘ (SWINGWORKER) & CẬP NHẬT BẢNG / BIỂU ĐỒ

    private void dungLai() {
        if (cacKieuHienThi.isEmpty()) return;
        int phienBan = ++phienTai;
        KieuHienThi k = cacKieuHienThi.get(Math.max(0, cboHienThiHop.getSelectedIndex()));
        KhoangNgay ky = khoangDaApDung;
        choPhep(cboKy, !k.hienTai());
        nhanKhoang.setText("Đang tải…");
        xuat.setEnabled(false);
        duLieu = null;
        var phienDangNhap = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chay(
                () ->
                        new KetQuaTai(
                                k.tao().apply(ky),
                                chiTietThongKeService.lay(tenXuat.replace('_', ' '), k.ten(), ky)),
                ketQua -> {
                    if (phienBan != phienTai
                            || PhienDangNhapService.macDinh().layPhienHienTai() != phienDangNhap)
                        return;
                    duLieu = ketQua.duLieu;
                    chungTu = ketQua.chungTu;
                    kyDuLieu = ky;
                    moHinh.setDataVector(duLieu.cacDong(), duLieu.cacCot());
                    DinhDangBang.apDung(bangBang);
                    nhanKhoang.setText(
                            k.hienTai()
                                    ? "Hiện tại (không theo kỳ)"
                                    : "Đã áp dụng: "
                                            + ky.getTuNgay().format(DinhDang.NGAY)
                                            + " – "
                                            + ky.getDenNgay().format(DinhDang.NGAY));
                    khungCacThe.removeAll();
                    for (int i = 0; i < duLieu.theCacTieuDe().length; i++) {
                        JPanel card =
                                layChiSoThe(
                                        duLieu.theCacTieuDe()[i],
                                        duLieu.theCacGiaTri()[i],
                                        "",
                                        MauUngDung.NAU);
                        ganBam(
                                card,
                                () ->
                                        chiTiet(
                                                IntStream.range(
                                                                0,
                                                                duLieu == null
                                                                        ? 0
                                                                        : duLieu.cacDong().length)
                                                        .toArray()));
                        khungCacThe.add(card);
                    }
                    veBieuDo();
                    khungCacThe.revalidate();
                    khungCacThe.repaint();
                    xuat.setEnabled(true);
                },
                ex -> {
                    if (phienBan != phienTai
                            || PhienDangNhapService.macDinh().layPhienHienTai() != phienDangNhap)
                        return;
                    nhanKhoang.setText("Không tải được báo cáo; nhấn Làm mới (F5) để thử lại");
                    HopThoai.hienThiCanhBao(BaoCaoPanel.this, TacVuNen.thongDiepLoi(ex));
                });
    }

    private void veBieuDo() {
        if (duLieu == null) return;
        boolean temporal =
                duLieu.bieuDoCacNhan().length > 0
                        && duLieu.bieuDoCacNhan()[0].matches("[0-9]{4}-[0-9]{2}-[0-9]{2}.*");
        Container nhomTongHop = cboTongHop.getParent();
        if (nhomTongHop != null) {
            boolean doiTrangThai = nhomTongHop.isVisible() != temporal;
            nhomTongHop.setVisible(temporal);
            cboTongHop.setEnabled(temporal);
            if (doiTrangThai && nhomTongHop.getParent() != null) {
                nhomTongHop.getParent().revalidate();
                nhomTongHop.getParent().repaint();
            }
        }
        tongHop =
                TongHopBieuDo.tao(
                        duLieu.bieuDoCacNhan(),
                        duLieu.bieuDoCacGiaTri(),
                        kyDuLieu,
                        String.valueOf(cboTongHop.getSelectedItem()));
        bieuDo.datDuLieu(tongHop.nhan(), tongHop.giaTri(), duLieu.tienTe(), duLieu.donVi());
        pieDocs = null;
        boolean goods =
                tenXuat.contains("hàng_hóa")
                        && "Sản phẩm bán chạy".equals(cboHienThiHop.getSelectedItem());
        if ((temporal || goods) && chungTu != null && !chungTu.getDong().isEmpty()) {
            int group = goods ? 6 : 2;
            int amount = goods ? 5 : tenXuat.contains("doanh_thu") ? 6 : 3;
            Set<String> included = new HashSet<>();
            for (Object[] row : duLieu.cacDong()) included.add(khoa(row[0]));
            Map<String, Double> sums = new LinkedHashMap<>();
            Map<String, List<Integer>> rows = new LinkedHashMap<>();
            for (int i = 0; i < chungTu.getDong().size(); i++) {
                Object[] r = chungTu.getDong().get(i);
                if (goods && !included.contains(khoa(r[1]))) continue;
                String key = String.valueOf(r[group]);
                sums.merge(key, ((Number) r[amount]).doubleValue(), Double::sum);
                rows.computeIfAbsent(key, v -> new ArrayList<>()).add(i);
            }
            pieDocs =
                    rows.values().stream()
                            .map(r -> r.stream().mapToInt(Integer::intValue).toArray())
                            .toList();
            tron.datDuLieu(
                    sums.keySet().toArray(String[]::new),
                    sums.values().stream().mapToDouble(Double::doubleValue).toArray(),
                    duLieu.tienTe());
        } else tron.datDuLieu(tongHop.nhan(), tongHop.giaTri(), duLieu.tienTe());
    }

    // 4. HIỂN THỊ HỘP THOẠI CHỨNG TỪ CHI TIẾT (DRILL-DOWN) & XUẤT EXCEL CHI TIẾT

    private static String khoa(Object value) {
        return value instanceof Date d
                ? Instant.ofEpochMilli(d.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toString()
                : String.valueOf(value);
    }

    private void chiTiet(int[] rows) {
        if (duLieu == null) return;
        if (chungTu != null) {
            boolean movement = "Biến động trong kỳ".equals(cboHienThiHop.getSelectedItem());
            Set<String> keys = new HashSet<>();
            for (int i : rows)
                if (i < duLieu.cacDong().length)
                    keys.add(
                            khoa(duLieu.cacDong()[i][0])
                                    + (movement ? "|" + duLieu.cacDong()[i][2] : ""));
            moBang(
                    chungTu.getCot(),
                    chungTu.getDong().stream()
                            .filter(
                                    r ->
                                            keys.contains(
                                                    khoa(r[chungTu.getCotNhom()])
                                                            + (movement ? "|" + r[3] : "")))
                            .toArray(Object[][]::new));
        } else
            moBang(
                    duLieu.cacCot(),
                    Arrays.stream(rows)
                            .filter(i -> i < duLieu.cacDong().length)
                            .mapToObj(i -> duLieu.cacDong()[i])
                            .toArray(Object[][]::new));
    }

    private void moBang(String[] columns, Object[][] rows) {
        JTable t =
                new JTable(
                        new DefaultTableModel(rows, columns) {
                            public boolean isCellEditable(int r, int c) {
                                return false;
                            }
                        });
        DinhDangBang.apDung(t);
        JPanel p = new JPanel(new BorderLayout(0, 8));
        JButton export = new JButton("Xuất Excel");
        export.addActionListener(e -> XuatExcelDialog.xuatBang(this, t, tenXuat + "_chi_tiet"));
        p.add(export, BorderLayout.NORTH);
        p.add(new JScrollPane(t));
        p.setPreferredSize(new Dimension(950, 400));
        HopThoai.hienThiNoiDung(this, p, "Chứng từ chi tiết · " + nhanKhoang.getText());
    }

    // 5. CÁC HÀM HỖ TRỢ CUỘN TRANG, BỐ CỤC TỶ LỆ & TRỢ GIÚP MÀN HÌNH

    private static void themVungTheoTyLe(
            JPanel parent, Component component, int row, double weightY) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        gbc.weighty = weightY;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3, 0, 3, 0);
        parent.add(component, gbc);
    }

    private static void chuyenTiepCuonChuot(JScrollPane scrCon, JScrollPane scrCha) {
        MouseWheelListener[] danhSachCu = scrCon.getMouseWheelListeners();
        for (MouseWheelListener l : danhSachCu) {
            scrCon.removeMouseWheelListener(l);
        }
        scrCon.addMouseWheelListener(
                e -> {
                    JScrollBar thanhDoc = scrCon.getVerticalScrollBar();
                    int giaTriTruoc = thanhDoc != null ? thanhDoc.getValue() : 0;
                    boolean coTheCuonDoc =
                            thanhDoc != null
                                    && thanhDoc.isVisible()
                                    && ((e.getWheelRotation() < 0
                                                    && giaTriTruoc > thanhDoc.getMinimum())
                                            || (e.getWheelRotation() > 0
                                                    && giaTriTruoc + thanhDoc.getVisibleAmount()
                                                            < thanhDoc.getMaximum()));
                    if (coTheCuonDoc) {
                        for (MouseWheelListener l : danhSachCu) {
                            l.mouseWheelMoved(e);
                        }
                    } else {
                        scrCha.dispatchEvent(SwingUtilities.convertMouseEvent(scrCon, e, scrCha));
                    }
                });
    }

    private static final class TrangCuonPanel extends JPanel implements Scrollable {
        private TrangCuonPanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(
                Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(
                Rectangle visibleRect, int orientation, int direction) {
            return Math.max(24, visibleRect.height - 48);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent() instanceof JViewport vp
                    && vp.getHeight() > getPreferredSize().height;
        }
    }

    protected String layTieuDeBieuDo() {
        return "BIỂU ĐỒ DIỄN BIẾN (Nhấp vào cột để xem chứng từ chi tiết)";
    }

    private static void choPhep(Component c, boolean enabled) {
        c.setEnabled(enabled);
        if (c instanceof Container p)
            for (Component child : p.getComponents()) choPhep(child, enabled);
    }

    private static void ganBam(Component c, Runnable action) {
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        c.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        action.run();
                    }
                });
        if (c instanceof Container p)
            for (Component child : p.getComponents()) ganBam(child, action);
    }

    @Override
    public void xuLyHienThi() {
        dungLai();
    }

    @Override
    public void xuLyLamMoi() {
        dangDatLaiBoLoc = true;
        try {
            cboKy.chonKy("30 ngày qua");
            if (cboHienThiHop.getItemCount() > 0) cboHienThiHop.setSelectedIndex(0);
            if (cboTongHop.getItemCount() > 0) cboTongHop.setSelectedIndex(0);
            apDungKhoangNgayNeuHopLe();
        } finally {
            dangDatLaiBoLoc = false;
        }
        dungLai();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Thống kê";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Chọn kỳ hoặc kiểu xem để tự động tải báo cáo (hoặc nhấn Làm mới / F5). Mục Biểu"
                + " đồ (Ngày/Tuần/Tháng) chỉ hiện khi xem theo thời gian để đổi độ chia cột. Bấm"
                + " cột hoặc chú giải để xem chi tiết. Xuất Excel giữ đủ bảng đang hiển thị.";
    }
}
