package com.petstation.gui.components;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

import javax.swing.*;

public final class LineIcon implements Icon {
    public enum Type {
        HOME,
        NEW,
        SAVE,
        SEARCH,
        PRINT,
        UNDO,
        HELP,
        CART,
        USERS,
        PAW,
        BED,
        BOX,
        TRUCK,
        CHART,
        CALENDAR,
        CHECK,
        LOCK,
        CASH,
        TAG,
        RETURN,
        FILE,
        DROPLET,
        GEAR,
        CLOCK,
        BACK,
        BELL,
        SHIELD,
        GIFT,
        TRASH,
        CLIPBOARD,
        KEY
    }

    private final Type type;
    private final int size;
    private final Color color;

    public LineIcon(Type type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x, y);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(component.isEnabled() ? color : AppColors.DISABLED);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int m = 3;
        int e = size - 4;
        if (type.ordinal() > Type.HELP.ordinal()) {
            double scale = size / 24.0;
            g.scale(scale, scale);
            g.setStroke(
                    new BasicStroke(
                            (float) (1.8 / scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawGlyph(g);
            g.dispose();
            return;
        }
        switch (type) {
            case HOME -> {
                Path2D path = new Path2D.Double();
                path.moveTo(m, size / 2.0);
                path.lineTo(size / 2.0, m);
                path.lineTo(e, size / 2.0);
                g.draw(path);
                g.drawRect(5, size / 2, size - 10, size / 2 - 4);
            }
            case NEW -> {
                g.drawLine(size / 2, m, size / 2, e);
                g.drawLine(m, size / 2, e, size / 2);
            }
            case SAVE -> {
                g.drawRect(m, m, size - 7, size - 7);
                g.drawRect(6, m, size - 12, 5);
                g.drawRect(6, size - 9, size - 12, 6);
            }
            case SEARCH -> {
                g.drawOval(m, m, size - 10, size - 10);
                g.drawLine(size - 8, size - 8, e, e);
            }
            case PRINT -> {
                g.drawRect(5, m, size - 10, 6);
                g.drawRect(m, 8, size - 6, 8);
                g.drawRect(6, 13, size - 12, size - 17);
            }
            case UNDO -> {
                g.draw(new Arc2D.Double(4, 4, size - 8, size - 8, 30, 250, Arc2D.OPEN));
                g.drawLine(4, 8, 4, 3);
                g.drawLine(4, 8, 9, 8);
            }
            case HELP -> {
                g.drawOval(m, m, size - 7, size - 7);
                g.drawString("?", size / 2 - 3, size - 5);
            }
        }
        g.dispose();
    }

    private static Path2D path(double... p) {
        Path2D path = new Path2D.Double();
        path.moveTo(p[0], p[1]);
        for (int i = 2; i < p.length; i += 2) path.lineTo(p[i], p[i + 1]);
        return path;
    }

    private static void line(Graphics2D g, double x1, double y1, double x2, double y2) {
        g.draw(new java.awt.geom.Line2D.Double(x1, y1, x2, y2));
    }

    private static void oval(Graphics2D g, double cx, double cy, double r) {
        g.draw(new java.awt.geom.Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
    }

    /** Vẽ biểu tượng trên lưới 24x24. */
    private void drawGlyph(Graphics2D g) {
        switch (type) {
            case CART -> {
                g.draw(path(3, 4, 6, 4, 8.5, 15, 19, 15, 21, 7, 7, 7));
                oval(g, 10, 19, 1.6);
                oval(g, 17, 19, 1.6);
            }
            case USERS -> {
                oval(g, 12, 8, 4);
                Path2D body = new Path2D.Double();
                body.moveTo(4, 21);
                body.quadTo(4, 13.5, 12, 13.5);
                body.quadTo(20, 13.5, 20, 21);
                g.draw(body);
            }
            case PAW -> {
                oval(g, 5.5, 11, 2);
                oval(g, 9.5, 6.5, 2);
                oval(g, 14.5, 6.5, 2);
                oval(g, 18.5, 11, 2);
                g.draw(new java.awt.geom.Ellipse2D.Double(7, 13, 10, 8));
            }
            case BED -> {
                line(g, 3, 19, 3, 7);
                g.draw(path(3, 15, 21, 15, 21, 19));
                g.draw(path(21, 15, 21, 11, 10, 11, 10, 15));
                oval(g, 6.5, 12, 1.8);
            }
            case BOX -> {
                g.draw(path(12, 3, 20, 7, 20, 17, 12, 21, 4, 17, 4, 7, 12, 3));
                g.draw(path(4, 7, 12, 11, 20, 7));
                line(g, 12, 11, 12, 21);
            }
            case TRUCK -> {
                g.drawRect(2, 6, 12, 10);
                g.draw(path(14, 10, 18, 10, 21, 13, 21, 16, 14, 16));
                oval(g, 7, 18, 2);
                oval(g, 17, 18, 2);
            }
            case CHART -> {
                line(g, 4, 20, 20, 20);
                g.drawRect(6, 12, 3, 8);
                g.drawRect(11, 7, 3, 13);
                g.drawRect(16, 3, 3, 17);
            }
            case CALENDAR -> {
                g.draw(new java.awt.geom.RoundRectangle2D.Double(4, 5, 16, 15, 3, 3));
                line(g, 4, 10, 20, 10);
                line(g, 8, 3, 8, 7);
                line(g, 16, 3, 16, 7);
            }
            case CHECK -> {
                oval(g, 12, 12, 9);
                g.draw(path(8, 12, 11, 15, 16, 9));
            }
            case LOCK -> {
                g.draw(new java.awt.geom.RoundRectangle2D.Double(5, 11, 14, 10, 2, 2));
                Path2D shackle = new Path2D.Double();
                shackle.moveTo(8, 11);
                shackle.lineTo(8, 8);
                shackle.append(new Arc2D.Double(8, 4, 8, 8, 180, -180, Arc2D.OPEN), true);
                shackle.lineTo(16, 11);
                g.draw(shackle);
            }
            case CASH -> {
                g.draw(new java.awt.geom.RoundRectangle2D.Double(2, 6, 20, 12, 2, 2));
                oval(g, 12, 12, 2.8);
                line(g, 5.5, 12, 6, 12);
                line(g, 18, 12, 18.5, 12);
            }
            case TAG -> {
                g.draw(path(3, 12, 12, 3, 20, 3, 20, 11, 11, 20, 3, 12));
                oval(g, 15.5, 7.5, 1.2);
            }
            case RETURN -> {
                g.draw(new Arc2D.Double(5, 6, 14, 14, 90, 250, Arc2D.OPEN));
                g.draw(path(5, 3, 5, 8, 10, 8));
            }
            case FILE -> {
                g.draw(path(6, 3, 14, 3, 19, 8, 19, 21, 6, 21, 6, 3));
                g.draw(path(14, 3, 14, 8, 19, 8));
                line(g, 9, 13, 16, 13);
                line(g, 9, 17, 16, 17);
            }
            case DROPLET -> {
                Path2D drop = new Path2D.Double();
                drop.moveTo(12, 3);
                drop.quadTo(19, 11, 19, 15);
                drop.append(new Arc2D.Double(5, 8, 14, 14, 0, -180, Arc2D.OPEN), true);
                drop.quadTo(5, 11, 12, 3);
                g.draw(drop);
            }
            case GEAR -> {
                oval(g, 12, 12, 3);
                oval(g, 12, 12, 7);
                for (int i = 0; i < 8; i++) {
                    double a = Math.toRadians(i * 45);
                    line(
                            g,
                            12 + 7 * Math.cos(a),
                            12 + 7 * Math.sin(a),
                            12 + 9.5 * Math.cos(a),
                            12 + 9.5 * Math.sin(a));
                }
            }
            case CLOCK -> {
                oval(g, 12, 12, 9);
                g.draw(path(12, 7, 12, 12, 16, 14));
            }
            case BACK -> g.draw(path(15, 5, 8, 12, 15, 19));
            case BELL -> {
                Path2D bell = new Path2D.Double();
                bell.moveTo(6, 17);
                bell.lineTo(6, 11);
                bell.append(new Arc2D.Double(6, 5, 12, 12, 180, -180, Arc2D.OPEN), true);
                bell.lineTo(18, 17);
                bell.lineTo(20, 19);
                bell.lineTo(4, 19);
                bell.closePath();
                g.draw(bell);
                line(g, 10, 21.5, 14, 21.5);
            }
            case SHIELD -> {
                Path2D shield = new Path2D.Double();
                shield.moveTo(12, 3);
                shield.lineTo(20, 6);
                shield.lineTo(20, 12);
                shield.quadTo(20, 18, 12, 21);
                shield.quadTo(4, 18, 4, 12);
                shield.lineTo(4, 6);
                shield.closePath();
                g.draw(shield);
            }
            case GIFT -> {
                g.drawRect(4, 10, 16, 10);
                g.drawRect(3, 7, 18, 3);
                line(g, 12, 7, 12, 20);
                g.draw(new java.awt.geom.Ellipse2D.Double(7.5, 3, 4.5, 4));
                g.draw(new java.awt.geom.Ellipse2D.Double(12, 3, 4.5, 4));
            }
            case TRASH -> {
                line(g, 4, 7, 20, 7);
                g.draw(path(9, 7, 9, 4, 15, 4, 15, 7));
                g.draw(path(6, 7, 7.5, 21, 16.5, 21, 18, 7));
                line(g, 10, 11, 10, 17);
                line(g, 14, 11, 14, 17);
            }
            case CLIPBOARD -> {
                g.draw(new java.awt.geom.RoundRectangle2D.Double(5, 4, 14, 17, 2, 2));
                g.drawRect(9, 2, 6, 4);
                g.draw(path(8.5, 12, 10.5, 14, 15, 10));
                line(g, 8.5, 18, 15.5, 18);
            }
            case KEY -> {
                oval(g, 8, 15, 4);
                line(g, 11, 12, 20, 3);
                line(g, 17, 6, 20, 9);
            }
            default -> {}
        }
    }
}
