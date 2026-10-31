package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;
import com.petstation.util.TienTe;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;

/** Chú giải được cuộn riêng; mục Khác giữ các chỉ số gốc để mở đầy đủ chi tiết. */
public final class BieuDoTron extends JPanel {
    private final Color[] mau = {
        new Color(0xCF1730),
        new Color(0x2563EB),
        new Color(0x059669),
        new Color(0xD97706),
        new Color(0x7C3AED),
        new Color(0x64748B)
    };
    private final List<Arc2D> cung = new ArrayList<>();
    private final List<int[]> dong = new ArrayList<>();
    private double[] so = new double[0];
    private double tong;
    private boolean hopLe;
    private Consumer<int[]> chon = r -> {};
    private final JPanel chuGiai = new JPanel();
    private final JComponent hinh =
            new JComponent() {
                @Override
                protected void paintComponent(Graphics g0) {
                    Graphics2D g = (Graphics2D) g0.create();
                    g.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    cung.clear();
                    if (!hopLe) {
                        g.setColor(Color.GRAY);
                        g.drawString(
                                tong == 0
                                        ? "Chưa có dữ liệu tỷ trọng"
                                        : "Dữ liệu âm: xem biểu đồ cột",
                                10,
                                50);
                        g.dispose();
                        return;
                    }
                    int d = Math.max(10, Math.min(getWidth() - 16, getHeight() - 16));
                    double angle = 90;
                    for (int i = 0; i < so.length; i++) {
                        double a = so[i] / tong * 360;
                        Arc2D arc =
                                new Arc2D.Double(
                                        (getWidth() - d) / 2d,
                                        (getHeight() - d) / 2d,
                                        d,
                                        d,
                                        angle,
                                        -a,
                                        Arc2D.PIE);
                        cung.add(arc);
                        g.setColor(mau[i % mau.length]);
                        g.fill(arc);
                        angle -= a;
                    }
                    g.dispose();
                }
            };

    public BieuDoTron() {
        super(new BorderLayout(8, 4));
        setBackground(Color.WHITE);
        hinh.setPreferredSize(new Dimension(180, 180));
        hinh.setMinimumSize(new Dimension(120, 120));
        hinh.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        for (int i = 0; i < cung.size(); i++)
                            if (cung.get(i).contains(e.getPoint())) {
                                chon.accept(dong.get(i));
                                break;
                            }
                    }
                });
        chuGiai.setLayout(new BoxLayout(chuGiai, BoxLayout.Y_AXIS));
        chuGiai.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(chuGiai);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(180, 0));
        JLabel lblTieuDe = new JLabel("Cơ cấu trong phạm vi báo cáo");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        lblTieuDe.setForeground(new Color(51, 65, 85));
        add(lblTieuDe, BorderLayout.NORTH);
        add(hinh, BorderLayout.CENTER);
        add(scroll, BorderLayout.EAST);
    }

    public void khiChon(Consumer<int[]> c) {
        chon = c;
    }

    public void datDuLieu(String[] labels, double[] values, boolean money) {
        chuGiai.removeAll();
        dong.clear();
        tong = Arrays.stream(values).sum();
        hopLe = tong > 0 && Arrays.stream(values).allMatch(v -> Double.isFinite(v) && v >= 0);
        if (!hopLe) {
            so = new double[0];
            revalidate();
            repaint();
            return;
        }
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < values.length; i++) if (values[i] > 0) order.add(i);
        order.sort((a, b) -> Double.compare(values[b], values[a]));
        List<Double> sums = new ArrayList<>();
        List<String> names = new ArrayList<>();
        int direct = order.size() > 6 ? 5 : order.size();
        for (int j = 0; j < direct; j++) {
            int i = order.get(j);
            sums.add(values[i]);
            names.add(labels[i]);
            dong.add(new int[] {i});
        }
        if (order.size() > 6) {
            int[] other =
                    order.subList(5, order.size()).stream().mapToInt(Integer::intValue).toArray();
            dong.add(other);
            sums.add(Arrays.stream(other).mapToDouble(i -> values[i]).sum());
            names.add("Khác");
        }
        so = sums.stream().mapToDouble(Double::doubleValue).toArray();
        for (int i = 0; i < so.length; i++) {
            final int k = i;
            String text =
                    DinhDangBang.tenTrangThai(names.get(i))
                            + " · "
                            + String.format(Locale.ROOT, "%.1f%%", so[i] / tong * 100)
                            + " · "
                            + (money
                                    ? TienTe.dinhDang(Math.round(so[i]))
                                    : String.format(Locale.ROOT, "%,.1f", so[i]));
            JButton label = new JButton(text);
            label.setForeground(mau[i]);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setToolTipText(text);
            label.addActionListener(e -> chon.accept(dong.get(k)));
            chuGiai.add(label);
        }
        revalidate();
        repaint();
    }
}
