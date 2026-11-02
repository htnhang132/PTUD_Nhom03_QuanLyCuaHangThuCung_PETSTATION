package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;
import com.petstation.util.TienTe;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.IntConsumer;

import javax.swing.*;

/**
 * Biểu đồ cột có cuộn, trục âm, tự động co giãn theo chiều cao khung chứa và vùng tương tác riêng
 * cho mỗi cột.
 */
public final class BieuDoCot extends JComponent implements Scrollable {
    private String[] nhan = new String[0];
    private double[] giaTri = new double[0];
    private boolean tienTe;
    private String donVi = "";
    private Color mau = MauUngDung.DO_THUONG_HIEU;
    private final java.util.List<Rectangle> vung = new ArrayList<>();
    private IntConsumer chon = i -> {};

    public BieuDoCot() {
        setPreferredSize(new Dimension(400, 240));
        setToolTipText("");
        addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        int i = tim(e.getPoint());
                        if (i >= 0) chon.accept(i);
                    }
                });
    }

    public void khiChon(IntConsumer c) {
        chon = c;
    }

    public void datMauThanh(Color c) {
        mau = c;
        repaint();
    }

    public void datDuLieu(String[] labels, double[] values, boolean money, String unit) {
        nhan = labels.clone();
        giaTri = values.clone();
        tienTe = money;
        donVi = unit == null ? "" : unit;
        int targetH =
                getParent() != null && getParent().getHeight() > 0 ? getParent().getHeight() : 240;
        int vpWidth =
                getParent() != null && getParent().getWidth() > 0 ? getParent().getWidth() : 500;
        int targetW =
                values.length <= 10
                        ? Math.max(vpWidth - 10, values.length * 60 + 40)
                        : Math.max(vpWidth, values.length * 80 + 40);
        setPreferredSize(new Dimension(Math.max(380, targetW), targetH));
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) {
        return 24;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) {
        return r.width;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }

    private String format(double v) {
        return tienTe
                ? TienTe.dinhDang(Math.round(v))
                : String.format(java.util.Locale.ROOT, "%,.1f", v).replace(".0", "") + " " + donVi;
    }

    private int tim(Point p) {
        for (int i = 0; i < vung.size(); i++) if (vung.get(i).contains(p)) return i;
        return -1;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        int i = tim(e.getPoint());
        return i < 0 ? null : DinhDangBang.tenTrangThai(nhan[i]) + ": " + format(giaTri[i]);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        vung.clear();
        if (giaTri.length == 0) {
            g.setColor(Color.GRAY);
            g.drawString("Chưa có dữ liệu", 20, 50);
            g.dispose();
            return;
        }
        double min = 0, max = 0;
        for (double v : giaTri) {
            min = Math.min(min, v);
            max = Math.max(max, v);
        }
        if (max == min) max = min + 1;
        double maxCeil = max > 0 ? max * 1.25 : max;
        if (maxCeil == min) maxCeil = min + 1;
        int top = 22, bottom = Math.max(top + 10, getHeight() - 44);
        double scale = (bottom - top) / (maxCeil - min);
        int zero = top + (int) Math.round((maxCeil - Math.max(0, min)) * scale);
        g.setColor(MauUngDung.VIEN);
        g.drawLine(20, zero, getWidth() - 10, zero);
        double slot = (getWidth() - 40d) / giaTri.length;
        int bw = (int) Math.min(48, Math.max(16, slot * 0.55));
        for (int i = 0; i < giaTri.length; i++) {
            int x = 20 + (int) (i * slot + (slot - bw) / 2),
                    y = top + (int) Math.round((maxCeil - giaTri[i]) * scale);
            Rectangle hit =
                    new Rectangle(
                            x - 5,
                            Math.min(y, zero) - 10,
                            bw + 10,
                            Math.max(20, Math.abs(zero - y) + 20));
            vung.add(hit);
            g.setColor(giaTri[i] < 0 ? new Color(0xB45309) : mau);
            g.fillRoundRect(x, Math.min(y, zero), bw, Math.max(1, Math.abs(y - zero)), 5, 5);
            String value = format(giaTri[i]);
            g.setColor(MauUngDung.VAN_BAN_TOI);
            if (g.getFontMetrics().stringWidth(value) < slot - 2)
                g.drawString(
                        value,
                        x + (bw - g.getFontMetrics().stringWidth(value)) / 2,
                        giaTri[i] >= 0 ? y - 6 : y + 15);
            String label = i < nhan.length ? DinhDangBang.tenTrangThai(nhan[i]) : "";
            while (label.length() > 1 && g.getFontMetrics().stringWidth(label) > slot - 4)
                label = label.substring(0, label.length() - 2) + "…";
            g.setColor(Color.DARK_GRAY);
            g.drawString(
                    label,
                    20 + (int) (i * slot + (slot - g.getFontMetrics().stringWidth(label)) / 2),
                    bottom + 22);
        }
        g.dispose();
    }
}
