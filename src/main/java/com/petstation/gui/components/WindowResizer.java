package com.petstation.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Cho phép kéo cạnh/góc của cửa sổ không viền hệ thống để đổi kích thước. */
public final class WindowResizer extends MouseAdapter {
    private static final int EDGE = 6;
    private static final int N = 1, S = 2, W = 4, E = 8;

    private final JFrame frame;
    private final JComponent host;
    private int direction;
    private Point pressPoint;
    private Rectangle pressBounds;

    private WindowResizer(JFrame frame, JComponent host) {
        this.frame = frame;
        this.host = host;
    }

    /** host là panel có viền dày EDGE px; chỉ vùng viền mới nhận sự kiện chuột. */
    public static void install(JFrame frame, JComponent host) {
        WindowResizer r = new WindowResizer(frame, host);
        host.addMouseListener(r);
        host.addMouseMotionListener(r);
    }

    private int directionAt(Point p) {
        if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) return 0;
        int d = 0;
        if (p.y < EDGE) d |= N;
        if (p.y >= host.getHeight() - EDGE) d |= S;
        if (p.x < EDGE) d |= W;
        if (p.x >= host.getWidth() - EDGE) d |= E;
        return d;
    }

    private static int cursorFor(int d) {
        return switch (d) {
            case N -> Cursor.N_RESIZE_CURSOR;
            case S -> Cursor.S_RESIZE_CURSOR;
            case W -> Cursor.W_RESIZE_CURSOR;
            case E -> Cursor.E_RESIZE_CURSOR;
            case N | W -> Cursor.NW_RESIZE_CURSOR;
            case N | E -> Cursor.NE_RESIZE_CURSOR;
            case S | W -> Cursor.SW_RESIZE_CURSOR;
            case S | E -> Cursor.SE_RESIZE_CURSOR;
            default -> Cursor.DEFAULT_CURSOR;
        };
    }

    @Override public void mouseMoved(MouseEvent e) {
        host.setCursor(Cursor.getPredefinedCursor(cursorFor(directionAt(e.getPoint()))));
    }

    @Override public void mouseExited(MouseEvent e) {
        if (pressPoint == null) host.setCursor(Cursor.getDefaultCursor());
    }

    @Override public void mousePressed(MouseEvent e) {
        direction = directionAt(e.getPoint());
        if (direction == 0) return;
        pressPoint = e.getLocationOnScreen();
        pressBounds = frame.getBounds();
    }

    @Override public void mouseReleased(MouseEvent e) {
        pressPoint = null;
        direction = 0;
    }

    @Override public void mouseDragged(MouseEvent e) {
        if (direction == 0 || pressPoint == null) return;
        Point now = e.getLocationOnScreen();
        int dx = now.x - pressPoint.x;
        int dy = now.y - pressPoint.y;
        Rectangle b = new Rectangle(pressBounds);
        Dimension min = frame.getMinimumSize();
        if ((direction & E) != 0) b.width = Math.max(min.width, pressBounds.width + dx);
        if ((direction & S) != 0) b.height = Math.max(min.height, pressBounds.height + dy);
        if ((direction & W) != 0) {
            int w = Math.max(min.width, pressBounds.width - dx);
            b.x = pressBounds.x + pressBounds.width - w;
            b.width = w;
        }
        if ((direction & N) != 0) {
            int h = Math.max(min.height, pressBounds.height - dy);
            b.y = pressBounds.y + pressBounds.height - h;
            b.height = h;
        }
        frame.setBounds(b);
    }
}
