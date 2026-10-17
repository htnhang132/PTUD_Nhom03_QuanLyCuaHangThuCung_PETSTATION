package com.petstation.gui.components.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import com.petstation.gui.components.AppColors;

/** Biểu đồ cột đơn giản vẽ bằng Java2D (không cần thư viện ngoài). */
public final class SimpleBarChart extends JComponent {
    private String[] labels = new String[0];
    private double[] values = new double[0];
    private String unit = "";
    private boolean money = true;
    private Color color = AppColors.RED_BRAND;

    public SimpleBarChart() {
        setPreferredSize(new Dimension(400, 240));
        setOpaque(false);
    }

    public void setData(String[] labels, double[] values, boolean money, String unit) {
        this.labels = labels;
        this.values = values;
        this.money = money;
        this.unit = unit == null ? "" : unit;
        repaint();
    }

    public void setBarColor(Color c) { this.color = c; repaint(); }

    private String format(double v) {
        if (!money) return v == Math.rint(v) ? String.valueOf((long) v) : String.format("%.1f", v);
        if (v >= 1_000_000) return String.format("%.1ftr", v / 1_000_000).replace(".0tr", "tr");
        if (v >= 1_000) return String.format("%.0fk", v / 1_000);
        return String.valueOf((long) v);
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        if (values.length == 0) {
            g.setColor(AppColors.TEXT_GRAY);
            g.setFont(AppColors.font(Font.PLAIN, 13));
            g.drawString("Chưa có dữ liệu", w / 2 - 40, h / 2);
            g.dispose();
            return;
        }
        int left = 10, right = 10, top = 26, bottom = 30;
        double max = 0;
        for (double v : values) max = Math.max(max, v);
        if (max <= 0) max = 1;
        g.setFont(AppColors.font(Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();
        int chartH = h - top - bottom;
        g.setColor(AppColors.BORDER);
        g.setStroke(new BasicStroke(1f));
        g.drawLine(left, top + chartH, w - right, top + chartH);
        int n = values.length;
        double slot = (w - left - right) / (double) n;
        int barW = (int) Math.max(8, Math.min(60, slot * 0.6));
        for (int i = 0; i < n; i++) {
            int bh = (int) Math.round(values[i] / max * (chartH - 4));
            int x = (int) (left + slot * i + (slot - barW) / 2);
            int y = top + chartH - bh;
            g.setColor(color);
            g.fillRoundRect(x, y, barW, Math.max(bh, 1), 6, 6);
            g.setColor(AppColors.TEXT_DARK);
            String v = format(values[i]) + (unit.isEmpty() ? "" : " " + unit);
            g.drawString(v, x + (barW - fm.stringWidth(v)) / 2, y - 4);
            g.setColor(AppColors.TEXT_GRAY);
            String l = i < labels.length ? labels[i] : "";
            int maxChars = Math.max(4, (int) (slot / 6.5));
            if (l.length() > maxChars) l = l.substring(0, Math.max(1, maxChars - 1)) + "…";
            g.drawString(l, (int) (left + slot * i + (slot - fm.stringWidth(l)) / 2), top + chartH + 16);
        }
        g.dispose();
    }
}
