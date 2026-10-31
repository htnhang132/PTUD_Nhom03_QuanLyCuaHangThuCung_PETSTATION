package com.petstation.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.Icon;

/** Biểu tượng vector nhỏ dùng cho năm nhóm menu chính. */
final class BieuTuongMenu implements Icon {
    private static final int KICH_THUOC = 18;
    private final char nhom;

    private BieuTuongMenu(char nhom) {
        this.nhom = Character.toUpperCase(nhom);
    }

    static Icon choNhom(char nhom) {
        return new BieuTuongMenu(nhom);
    }

    @Override
    public int getIconWidth() {
        return KICH_THUOC;
    }

    @Override
    public int getIconHeight() {
        return KICH_THUOC;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.translate(x, y);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setStroke(new BasicStroke(1.65f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            boolean hover = component instanceof AbstractButton button
                    && button.getModel().isRollover();
            g.setColor(hover ? MauUngDung.DO_THUONG_HIEU : MauUngDung.NAU);

            switch (nhom) {
                case 'H' -> veHeThong(g);
                case 'D' -> veDanhMuc(g);
                case 'T' -> veTimKiem(g);
                case 'X' -> veXuLy(g);
                case 'K' -> veThongKe(g);
                default -> veMacDinh(g);
            }
        } finally {
            g.dispose();
        }
    }

    /** Bánh răng: Hệ thống. */
    private static void veHeThong(Graphics2D g) {
        g.draw(new Ellipse2D.Double(5.0, 5.0, 8.0, 8.0));
        g.draw(new Ellipse2D.Double(7.6, 7.6, 2.8, 2.8));
        g.draw(new Line2D.Double(9, 1.5, 9, 4.0));
        g.draw(new Line2D.Double(9, 14.0, 9, 16.5));
        g.draw(new Line2D.Double(1.5, 9, 4.0, 9));
        g.draw(new Line2D.Double(14.0, 9, 16.5, 9));
        g.draw(new Line2D.Double(3.7, 3.7, 5.4, 5.4));
        g.draw(new Line2D.Double(12.6, 12.6, 14.3, 14.3));
        g.draw(new Line2D.Double(12.6, 5.4, 14.3, 3.7));
        g.draw(new Line2D.Double(3.7, 14.3, 5.4, 12.6));
    }

    /** Lưới bốn ô: Danh mục. */
    private static void veDanhMuc(Graphics2D g) {
        g.draw(new RoundRectangle2D.Double(2.0, 2.0, 6.0, 6.0, 1.4, 1.4));
        g.draw(new RoundRectangle2D.Double(10.0, 2.0, 6.0, 6.0, 1.4, 1.4));
        g.draw(new RoundRectangle2D.Double(2.0, 10.0, 6.0, 6.0, 1.4, 1.4));
        g.draw(new RoundRectangle2D.Double(10.0, 10.0, 6.0, 6.0, 1.4, 1.4));
    }

    /** Kính lúp: Tìm kiếm. */
    private static void veTimKiem(Graphics2D g) {
        g.draw(new Ellipse2D.Double(2.0, 2.0, 10.0, 10.0));
        g.draw(new Line2D.Double(10.0, 10.0, 16.0, 16.0));
    }

    /** Bảng công việc có dấu kiểm: Xử lý. */
    private static void veXuLy(Graphics2D g) {
        g.draw(new RoundRectangle2D.Double(3.0, 3.0, 12.0, 13.0, 2.0, 2.0));
        g.draw(new RoundRectangle2D.Double(6.0, 1.5, 6.0, 3.0, 1.2, 1.2));
        Path2D check = new Path2D.Double();
        check.moveTo(5.3, 10.0);
        check.lineTo(7.6, 12.2);
        check.lineTo(12.8, 7.0);
        g.draw(check);
    }

    /** Các cột tăng trưởng: Thống kê. */
    private static void veThongKe(Graphics2D g) {
        g.draw(new Line2D.Double(2.0, 16.0, 16.0, 16.0));
        g.draw(new RoundRectangle2D.Double(3.0, 10.0, 2.8, 6.0, 0.8, 0.8));
        g.draw(new RoundRectangle2D.Double(7.7, 6.5, 2.8, 9.5, 0.8, 0.8));
        g.draw(new RoundRectangle2D.Double(12.4, 2.5, 2.8, 13.5, 0.8, 0.8));
    }

    private static void veMacDinh(Graphics2D g) {
        g.draw(new Ellipse2D.Double(3.0, 3.0, 12.0, 12.0));
    }
}
