package com.petstation.gui.chung.thanhphan;

import com.petstation.gui.chung.MauUngDung;
import com.petstation.util.DinhDang;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public final class BieuDoTron extends JPanel {
    private static final Color[] MAU = {
        MauUngDung.mau(0xCF1730),
        MauUngDung.XANH_DUONG_CHINH,
        MauUngDung.mau(0x059669),
        MauUngDung.mau(0xD97706),
        MauUngDung.mau(0x7C3AED),
        MauUngDung.XAM_PHU
    };

    private final List<Arc2D> cung = new ArrayList<>();
    private final List<int[]> dongGocTheoLat = new ArrayList<>();
    private final List<String> nhanHienThi = new ArrayList<>();

    private double[] giaTri = new double[0];
    private double tong;
    private boolean tienTe;
    private boolean hopLe;
    private int latDangHover = -1;
    private Consumer<int[]> chon = indices -> {};

    private final JLabel tieuDe = new JLabel("Cơ cấu trong phạm vi báo cáo");

    private final JPanel chuGiai = new JPanel(new GridLayout(0, 3, 4, 1));

    private final JComponent hinh =
            new JComponent() {
                @Override
                protected void paintComponent(Graphics g0) {
                    super.paintComponent(g0);
                    Graphics2D g = (Graphics2D) g0.create();
                    g.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    cung.clear();
                    if (!hopLe || giaTri.length == 0) {
                        g.setColor(MauUngDung.XAM_PHU);
                        String thongBao =
                                tong == 0
                                        ? "Chưa có dữ liệu tỷ trọng"
                                        : "Dữ liệu âm: xem biểu đồ cột";
                        FontMetrics fm = g.getFontMetrics();
                        g.drawString(
                                thongBao,
                                Math.max(8, (getWidth() - fm.stringWidth(thongBao)) / 2),
                                Math.max(24, getHeight() / 2));
                        g.dispose();
                        return;
                    }
                    int duongKinh = Math.min(getWidth() - 24, getHeight() - 24);
                    if (duongKinh < 24) {
                        g.dispose();
                        return;
                    }
                    double x = (getWidth() - duongKinh) / 2.0;
                    double y = (getHeight() - duongKinh) / 2.0;
                    double gocBatDau = 90.0;
                    double tongGoc = 0.0;
                    for (int i = 0; i < giaTri.length; i++) {
                        double goc =
                                (i == giaTri.length - 1)
                                        ? 360.0 - tongGoc
                                        : giaTri[i] / tong * 360.0;
                        Arc2D lat =
                                new Arc2D.Double(
                                        x, y, duongKinh, duongKinh, gocBatDau, -goc, Arc2D.PIE);
                        cung.add(lat);
                        g.setColor(MAU[i % MAU.length]);
                        g.fill(lat);
                        g.setColor(Color.WHITE);
                        g.setStroke(new BasicStroke(2.0f));
                        g.draw(lat);
                        if (i == latDangHover) {
                            g.setColor(MauUngDung.VANG);
                            g.setStroke(new BasicStroke(3.0f));
                            g.draw(lat);
                        }
                        gocBatDau -= goc;
                        tongGoc += goc;
                    }
                    g.dispose();
                }

                @Override
                public String getToolTipText(MouseEvent e) {
                    int index = timLat(e.getPoint());
                    if (index < 0) return null;
                    String ten = escapeHtml(nhanHienThi.get(index));
                    double phanTram = giaTri[index] / tong * 100.0;
                    return "<html><b>"
                            + ten
                            + "</b><br>Giá trị: "
                            + escapeHtml(dinhDang(giaTri[index]))
                            + "<br>Tỷ trọng: "
                            + String.format(Locale.ROOT, "%.1f%%", phanTram)
                            + "</html>";
                }
            };

    public BieuDoTron() {
        super(new BorderLayout(8, 4));
        setBackground(Color.WHITE);
        setOpaque(true);
        tieuDe.setForeground(MauUngDung.XAM_DAM);
        tieuDe.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(2, 4, 4, 4));
        hinh.setOpaque(false);
        hinh.setPreferredSize(new Dimension(240, 190));
        hinh.setMinimumSize(new Dimension(140, 110));
        hinh.setToolTipText("");
        hinh.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int index = timLat(e.getPoint());
                        if (index >= 0) {
                            chon.accept(dongGocTheoLat.get(index).clone());
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (latDangHover != -1) {
                            latDangHover = -1;
                            hinh.repaint();
                        }
                    }
                });
        hinh.addMouseMotionListener(
                new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        int index = timLat(e.getPoint());
                        if (index != latDangHover) {
                            latDangHover = index;
                            hinh.setCursor(
                                    index >= 0
                                            ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                            : Cursor.getDefaultCursor());
                            hinh.repaint();
                        }
                    }
                });
        chuGiai.setOpaque(false);
        chuGiai.setBorder(BorderFactory.createEmptyBorder(0, 4, 2, 4));
        add(tieuDe, BorderLayout.NORTH);
        add(hinh, BorderLayout.CENTER);
        add(chuGiai, BorderLayout.SOUTH);
    }

    public void khiChon(Consumer<int[]> callback) {
        chon = callback == null ? indices -> {} : callback;
    }

    public void datDuLieu(String[] labels, double[] values, boolean money) {
        String[] ten = labels == null ? new String[0] : labels;
        double[] soGoc = values == null ? new double[0] : values;
        datLaiTrangThaiBieuDo(money);
        if (!kiemTraDuLieuBieuDo(ten, soGoc)) {
            giaTri = new double[0];
            hinh.setToolTipText("");
            lamMoiGiaoDienBieuDo();
            return;
        }
        taoCacLatBieuDo(ten, soGoc);
        taoChuGiaiBieuDo();
        lamMoiGiaoDienBieuDo();
    }

    private void datLaiTrangThaiBieuDo(boolean money) {
        dongGocTheoLat.clear();
        nhanHienThi.clear();
        chuGiai.removeAll();
        cung.clear();
        latDangHover = -1;
        tienTe = money;
        tong = 0.0;
    }

    private boolean kiemTraDuLieuBieuDo(String[] ten, double[] soGoc) {
        hopLe = soGoc.length > 0 && ten.length >= soGoc.length;
        for (double value : soGoc) {
            if (!Double.isFinite(value) || value < 0) {
                hopLe = false;
                return false;
            }
            tong += value;
        }
        if (!Double.isFinite(tong) || tong <= 0) hopLe = false;
        return hopLe;
    }

    private void taoCacLatBieuDo(String[] ten, double[] soGoc) {
        List<Integer> thuTu = new ArrayList<>();
        for (int i = 0; i < soGoc.length; i++) if (soGoc[i] > 0) thuTu.add(i);
        thuTu.sort((a, b) -> Double.compare(soGoc[b], soGoc[a]));
        List<Double> tongTheoLat = new ArrayList<>();
        int soLatTrucTiep = thuTu.size() > 6 ? 5 : thuTu.size();
        for (int j = 0; j < soLatTrucTiep; j++) {
            int chiSoGoc = thuTu.get(j);
            tongTheoLat.add(soGoc[chiSoGoc]);
            nhanHienThi.add(
                    DinhDangBang.tenTrangThai(
                            ten[chiSoGoc] == null ? "Mục " + (chiSoGoc + 1) : ten[chiSoGoc]));
            dongGocTheoLat.add(new int[] {chiSoGoc});
        }
        if (thuTu.size() > 6) {
            int[] cacChiSoKhac =
                    thuTu.subList(5, thuTu.size()).stream().mapToInt(Integer::intValue).toArray();
            tongTheoLat.add(Arrays.stream(cacChiSoKhac).mapToDouble(i -> soGoc[i]).sum());
            nhanHienThi.add("Khác");
            dongGocTheoLat.add(cacChiSoKhac);
        }
        giaTri = tongTheoLat.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private void taoChuGiaiBieuDo() {
        for (int i = 0; i < nhanHienThi.size(); i++) {
            final int index = i;
            String tenDayDu = nhanHienThi.get(i);
            JButton muc = new JButton(rutGon(tenDayDu));
            muc.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 10));
            muc.setForeground(MauUngDung.mau(0x475569));
            muc.setIcon(new OVuongMau(MAU[i % MAU.length]));
            muc.setHorizontalAlignment(SwingConstants.LEFT);
            muc.setIconTextGap(4);
            muc.setMargin(new Insets(0, 2, 0, 2));
            muc.setBorderPainted(false);
            muc.setContentAreaFilled(false);
            muc.setFocusPainted(false);
            muc.setToolTipText(tenDayDu);
            muc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            muc.addActionListener(e -> chon.accept(dongGocTheoLat.get(index).clone()));
            chuGiai.add(muc);
        }
    }

    private void lamMoiGiaoDienBieuDo() {
        chuGiai.revalidate();
        chuGiai.repaint();
        hinh.revalidate();
        hinh.repaint();
    }

    private static String rutGon(String ten) {
        if (ten == null) return "";
        final int gioiHan = 13;
        return ten.length() <= gioiHan ? ten : ten.substring(0, gioiHan - 1) + "…";
    }

    private static final class OVuongMau implements Icon {
        private final Color color;

        private OVuongMau(Color color) {
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return 8;
        }

        @Override
        public int getIconHeight() {
            return 8;
        }

        @Override
        public void paintIcon(Component component, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRoundRect(x, y, 8, 8, 2, 2);
        }
    }

    private int timLat(Point point) {
        for (int i = 0; i < cung.size(); i++) {
            if (cung.get(i).contains(point)) return i;
        }
        return -1;
    }

    private String dinhDang(double value) {
        if (tienTe) return DinhDang.dinhDang(Math.round(value));
        return String.format(Locale.ROOT, "%,.1f", value).replaceAll("\\.0$", "");
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
