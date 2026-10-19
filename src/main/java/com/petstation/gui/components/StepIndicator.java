package com.petstation.gui.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.IntConsumer;

import javax.swing.*;

/** Thanh các bước của một quy trình. Bước đã qua hiện dấu tick, bước hiện tại tô đỏ. */
public final class StepIndicator extends JComponent {
    private final List<String> steps;
    private int current;
    private IntConsumer onClick;

    public StepIndicator(List<String> steps) {
        this.steps = List.copyOf(steps);
        setPreferredSize(new Dimension(0, 44));
        setOpaque(false);
        addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (onClick == null) return;
                        int index = indexAt(e.getX());
                        if (index >= 0) onClick.accept(index);
                    }
                });
    }

    public void setCurrent(int index) {
        this.current = Math.max(0, Math.min(index, steps.size() - 1));
        repaint();
    }

    public int current() {
        return current;
    }

    /** Cho phép bấm vào từng bước (dùng trong thanh luồng của khung chính). */
    public void setOnClick(IntConsumer onClick) {
        this.onClick = onClick;
        setCursor(
                onClick == null
                        ? Cursor.getDefaultCursor()
                        : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private int indexAt(int x) {
        if (steps.isEmpty()) return -1;
        int cell = getWidth() / steps.size();
        int index = x / Math.max(cell, 1);
        return index >= 0 && index < steps.size() ? index : -1;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int n = steps.size();
        if (n == 0) {
            g.dispose();
            return;
        }
        int cell = getWidth() / n;
        int cy = getHeight() / 2;
        int r = 11;
        g.setFont(AppColors.font(Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i < n; i++) {
            int left = i * cell;
            int cx = left + 16 + r;
            boolean done = i < current;
            boolean active = i == current;
            if (i < n - 1) {
                g.setColor(done ? AppColors.GREEN : AppColors.BORDER);
                g.setStroke(new BasicStroke(2f));
                int labelEnd = cx + r + 8 + fm.stringWidth(steps.get(i));
                g.drawLine(Math.min(labelEnd + 8, left + cell - 12), cy, left + cell - 6, cy);
            }
            g.setColor(active ? AppColors.RED_BRAND : done ? AppColors.GREEN : Color.WHITE);
            g.fillOval(cx - r, cy - r, 2 * r, 2 * r);
            g.setColor(active || done ? Color.WHITE : AppColors.TEXT_GRAY);
            if (!active && !done) {
                g.setColor(AppColors.BORDER);
                g.setStroke(new BasicStroke(1.5f));
                g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
                g.setColor(AppColors.TEXT_GRAY);
            }
            if (done) {
                g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(cx - 4, cy, cx - 1, cy + 3);
                g.drawLine(cx - 1, cy + 3, cx + 5, cy - 3);
            } else {
                String number = String.valueOf(i + 1);
                g.drawString(number, cx - fm.stringWidth(number) / 2, cy + fm.getAscent() / 2 - 1);
            }
            g.setColor(active ? AppColors.TEXT_DARK : AppColors.TEXT_GRAY);
            g.drawString(steps.get(i), cx + r + 8, cy + fm.getAscent() / 2 - 1);
        }
        g.dispose();
    }
}
