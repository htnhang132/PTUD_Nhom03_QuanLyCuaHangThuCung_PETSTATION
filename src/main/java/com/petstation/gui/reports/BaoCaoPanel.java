package com.petstation.gui.reports;

import com.petstation.gui.components.*;
import com.petstation.gui.components.common.*;
import com.petstation.model.KhoangNgay;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BaoCaoPanel extends CoSoPanel implements HoTroThanhCongCu {
    public record DuLieu(
            String[] bieuDoCacNhan,
            double[] bieuDoCacGiaTri,
            boolean tienTe,
            String donVi,
            String[] cacCot,
            Object[][] cacDong,
            String[] theCacTieuDe,
            String[] theCacGiaTri) {}

    /**
     * Cung cấp giao diện và xử lý tương tác cho hiển thị.
     *
     * @param ten tên
     * @param tao tạo
     */
    public record KieuHienThi(
            String ten,
            Function<KhoangNgay, DuLieu> tao,
            boolean hienTai,
            com.petstation.model.Quyen quyen) {
        public KieuHienThi(String ten, Function<KhoangNgay, DuLieu> tao) {
            this(ten, tao, false, null);
        }

        public KieuHienThi(String ten, Function<KhoangNgay, DuLieu> tao, boolean hienTai) {
            this(ten, tao, hienTai, null);
        }

        public KieuHienThi chiQuanLy() {
            return new KieuHienThi(ten, tao, hienTai, com.petstation.model.Quyen.XEM_BAO_CAO_CHUNG);
        }
    }

    private final List<KieuHienThi> cacKieuHienThi;
    private final BoChonKhoangNgay cboKy = new BoChonKhoangNgay();
    private final JComboBox<String> cboHienThiHop = new JComboBox<>();
    private final JComboBox<String> cboTongHop =
            new JComboBox<>(new String[] {"Tự động", "Ngày", "Tuần", "Tháng"});
    private final JPanel pnlCacThe = new JPanel(new GridLayout(1, 4, 8, 0));
    private final BieuDoCot bieuDo = new BieuDoCot();
    private final BieuDoTron tron = new BieuDoTron();
    private final DefaultTableModel moHinh =
            new DefaultTableModel() {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
    private final JTable tblBang = new JTable(moHinh);
    private final JLabel lblKhoang = new JLabel();
    private final String tenXuat;
    private KhoangNgay khoangDaApDung = KhoangNgay.ganDay(30);
    private DuLieu duLieu;
    private TongHopBieuDo.KetQua tongHop;
    private int phienTai;
    private com.petstation.model.ChiTietBaoCao chungTu;
    private KhoangNgay kyDuLieu = khoangDaApDung;
    private java.util.List<int[]> pieDocs;

    private record KetQuaTai(DuLieu duLieu, com.petstation.model.ChiTietBaoCao chungTu) {}

    private final JButton xuat = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);

    public BaoCaoPanel(String tieuDe, String phu, String tenXuat, List<KieuHienThi> cacKieu) {
        this.tenXuat = tenXuat;
        this.cacKieuHienThi =
                cacKieu.stream()
                        .filter(
                                k ->
                                        k.quyen() == null
                                                || com.petstation.service.PhienDangNhapService
                                                        .layPhien()
                                                        .kiemTraCo(k.quyen()))
                        .toList();
        setLayout(new BorderLayout(0, 10));
        cboKy.chonKy("30 ngày qua");
        for (var k : cacKieuHienThi) cboHienThiHop.addItem(k.ten());
        ThanhDanhSach tools = new ThanhDanhSach();
        tools.themBoLoc(cboKy);
        tools.themBoLoc("Xem theo:", cboHienThiHop);
        tools.themBoLoc("Gộp:", cboTongHop);
        JButton xem = new NutDinhDang("Xem thống kê", NutDinhDang.KieuNut.CHINH);
        tools.add(xem);
        tools.add(xuat);
        xem.addActionListener(
                e -> {
                    try {
                        khoangDaApDung =
                                new KhoangNgay(cboKy.layNgayBatDau(), cboKy.layNgayKetThuc());
                        dungLai();
                    } catch (RuntimeException ex) {
                        HopThoai.hienThiCanhBao(this, ex.getMessage());
                    }
                });
        xuat.addActionListener(
                e -> XuatExcel.xuatBang(this, tblBang, tenXuat + "_" + lblKhoang.getText()));
        cboHienThiHop.addActionListener(e -> dungLai());
        cboTongHop.addActionListener(e -> veBieuDo());
        JPanel header = taoVungTrongSuot(new BorderLayout(0, 8));
        JPanel title = taoVungTrongSuot(new BorderLayout(0, 4));
        title.add(taoTieuDeMuc(tieuDe), BorderLayout.NORTH);
        JLabel note = new JLabel(phu);
        note.setForeground(MauUngDung.VAN_BAN_XAM);
        title.add(note);
        header.add(title, BorderLayout.NORTH);
        header.add(tools);
        lblKhoang.setForeground(MauUngDung.VAN_BAN_XAM);
        header.add(lblKhoang, BorderLayout.SOUTH);
        JPanel top = taoVungTrongSuot(new BorderLayout(0, 8));
        top.add(header, BorderLayout.NORTH);
        pnlCacThe.setOpaque(false);
        top.add(pnlCacThe);
        add(top, BorderLayout.NORTH);
        DinhDangBang.apDung(tblBang);

        // Biểu đồ cột có thanh cuộn ngang khi nhiều mốc thời gian, không cuộn dọc
        JScrollPane bar =
                new JScrollPane(
                        bieuDo,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        bar.setBorder(BorderFactory.createEmptyBorder());
        bar.getViewport().setBackground(Color.WHITE);
        bar.getHorizontalScrollBar().setUnitIncrement(24);

        // Card bao bọc biểu đồ cột
        JPanel pnlCot = layThe(new BorderLayout(0, 8));
        pnlCot.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        JLabel lblTieuDeCot =
                new JLabel("BIỂU ĐỒ DIỄN BIẾN (Nhấp vào cột để xem chứng từ chi tiết)");
        lblTieuDeCot.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        lblTieuDeCot.setForeground(new Color(51, 65, 85));
        pnlCot.add(lblTieuDeCot, BorderLayout.NORTH);
        pnlCot.add(bar, BorderLayout.CENTER);

        // Card bao bọc biểu đồ tròn
        JPanel pnlTron = layThe(new BorderLayout(0, 8));
        pnlTron.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        pnlTron.add(tron, BorderLayout.CENTER);

        JSplitPane splitBieuDo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlCot, pnlTron);
        splitBieuDo.setResizeWeight(0.58);
        splitBieuDo.setContinuousLayout(true);
        splitBieuDo.setBorder(null);
        splitBieuDo.setDividerSize(6);

        JScrollPane scrBang = new JScrollPane(tblBang);
        scrBang.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrBang.getViewport().setBackground(Color.WHITE);

        JSplitPane splitChinh =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitBieuDo, scrBang);
        splitChinh.setResizeWeight(0.62);
        splitChinh.setContinuousLayout(true);
        splitChinh.setBorder(null);
        splitChinh.setDividerSize(6);
        add(splitChinh, BorderLayout.CENTER);

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
                                java.util.Arrays.stream(indices)
                                        .flatMap(i -> java.util.Arrays.stream(pieDocs.get(i)))
                                        .toArray();
                        moBang(
                                chungTu.cot(),
                                java.util.Arrays.stream(rows)
                                        .mapToObj(i -> chungTu.dong().get(i))
                                        .toArray(Object[][]::new));
                    } else
                        chiTiet(
                                java.util.Arrays.stream(indices)
                                        .flatMap(
                                                i ->
                                                        java.util.Arrays.stream(
                                                                tongHop.dongGoc().get(i)))
                                        .toArray());
                });
        dungLai();
    }

    private void dungLai() {
        if (cacKieuHienThi.isEmpty()) return;
        int version = ++phienTai;
        KieuHienThi k = cacKieuHienThi.get(Math.max(0, cboHienThiHop.getSelectedIndex()));
        KhoangNgay ky = khoangDaApDung;
        choPhep(cboKy, !k.hienTai());
        lblKhoang.setText("Đang tải…");
        xuat.setEnabled(false);
        duLieu = null;
        var session = com.petstation.service.PhienDangNhapService.layPhienHienTai();
        new SwingWorker<KetQuaTai, Void>() {
            protected KetQuaTai doInBackground() {
                return new KetQuaTai(
                        k.tao().apply(ky),
                        com.petstation.service.ChiTietThongKeService.lay(
                                tenXuat.replace('_', ' '), k.ten(), ky));
            }

            protected void done() {
                if (version != phienTai
                        || com.petstation.service.PhienDangNhapService.layPhienHienTai() != session)
                    return;
                try {
                    KetQuaTai result = get();
                    duLieu = result.duLieu();
                    chungTu = result.chungTu();
                    kyDuLieu = ky;
                    moHinh.setDataVector(duLieu.cacDong(), duLieu.cacCot());
                    DinhDangBang.apDung(tblBang);
                    lblKhoang.setText(
                            k.hienTai()
                                    ? "Hiện tại (không theo kỳ)"
                                    : "Đã áp dụng: "
                                            + ky.tuNgay()
                                                    .format(
                                                            DateTimeFormatter.ofPattern(
                                                                    "dd/MM/yyyy"))
                                            + " – "
                                            + ky.denNgay()
                                                    .format(
                                                            DateTimeFormatter.ofPattern(
                                                                    "dd/MM/yyyy")));
                    pnlCacThe.removeAll();
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
                                                java.util.stream.IntStream.range(
                                                                0,
                                                                duLieu == null
                                                                        ? 0
                                                                        : duLieu.cacDong().length)
                                                        .toArray()));
                        pnlCacThe.add(card);
                    }
                    veBieuDo();
                    pnlCacThe.revalidate();
                    pnlCacThe.repaint();
                    xuat.setEnabled(true);
                } catch (Exception ex) {
                    lblKhoang.setText("Không tải được báo cáo; bấm Xem thống kê để thử lại");
                    HopThoai.hienThiCanhBao(
                            BaoCaoPanel.this,
                            ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage());
                }
            }
        }.execute();
    }

    private void veBieuDo() {
        if (duLieu == null) return;
        tongHop =
                TongHopBieuDo.tao(
                        duLieu.bieuDoCacNhan(),
                        duLieu.bieuDoCacGiaTri(),
                        kyDuLieu,
                        String.valueOf(cboTongHop.getSelectedItem()));
        bieuDo.datDuLieu(tongHop.nhan(), tongHop.giaTri(), duLieu.tienTe(), duLieu.donVi());
        pieDocs = null;
        boolean temporal =
                duLieu.bieuDoCacNhan().length > 0
                        && duLieu.bieuDoCacNhan()[0].matches("[0-9]{4}-[0-9]{2}-[0-9]{2}.*");
        boolean goods =
                tenXuat.contains("hàng_hóa")
                        && "Sản phẩm bán chạy".equals(cboHienThiHop.getSelectedItem());
        if ((temporal || goods) && chungTu != null && !chungTu.dong().isEmpty()) {
            int group = goods ? 6 : 2;
            int amount = goods ? 5 : tenXuat.contains("doanh_thu") ? 6 : 3;
            java.util.Set<String> included = new java.util.HashSet<>();
            for (Object[] row : duLieu.cacDong()) included.add(khoa(row[0]));
            java.util.Map<String, Double> sums = new java.util.LinkedHashMap<>();
            java.util.Map<String, java.util.List<Integer>> rows = new java.util.LinkedHashMap<>();
            for (int i = 0; i < chungTu.dong().size(); i++) {
                Object[] r = chungTu.dong().get(i);
                if (goods && !included.contains(khoa(r[1]))) continue;
                String key = String.valueOf(r[group]);
                sums.merge(key, ((Number) r[amount]).doubleValue(), Double::sum);
                rows.computeIfAbsent(key, v -> new java.util.ArrayList<>()).add(i);
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

    private static String khoa(Object value) {
        return value instanceof java.sql.Timestamp t
                ? t.toLocalDateTime().toLocalDate().toString()
                : String.valueOf(value);
    }

    private void chiTiet(int[] rows) {
        if (duLieu == null) return;
        if (chungTu != null) {
            boolean movement = "Biến động trong kỳ".equals(cboHienThiHop.getSelectedItem());
            java.util.Set<String> keys = new java.util.HashSet<>();
            for (int i : rows)
                if (i < duLieu.cacDong().length)
                    keys.add(
                            khoa(duLieu.cacDong()[i][0])
                                    + (movement ? "|" + duLieu.cacDong()[i][2] : ""));
            moBang(
                    chungTu.cot(),
                    chungTu.dong().stream()
                            .filter(
                                    r ->
                                            keys.contains(
                                                    khoa(r[chungTu.cotNhom()])
                                                            + (movement ? "|" + r[3] : "")))
                            .toArray(Object[][]::new));
        } else
            moBang(
                    duLieu.cacCot(),
                    java.util.Arrays.stream(rows)
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
        export.addActionListener(e -> XuatExcel.xuatBang(this, t, tenXuat + "_chi_tiet"));
        p.add(export, BorderLayout.NORTH);
        p.add(new JScrollPane(t));
        p.setPreferredSize(new Dimension(950, 400));
        HopThoai.hienThiThongBaoHopThoai(
                this, p, "Chứng từ chi tiết · " + lblKhoang.getText(), JOptionPane.PLAIN_MESSAGE);
    }

    private static void choPhep(Component c, boolean enabled) {
        c.setEnabled(enabled);
        if (c instanceof Container p)
            for (Component child : p.getComponents()) choPhep(child, enabled);
    }

    private static void ganBam(Component c, Runnable action) {
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        c.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
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
    protected String layTieuDeTroGiup() {
        return "Thống kê";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Chọn kỳ rồi bấm Xem thống kê. Gộp ngày/tuần/tháng chỉ thay đổi biểu đồ. Bấm cột"
                + " hoặc chú giải để xem chi tiết. Xuất Excel giữ đủ bảng đang hiển thị.";
    }
}
