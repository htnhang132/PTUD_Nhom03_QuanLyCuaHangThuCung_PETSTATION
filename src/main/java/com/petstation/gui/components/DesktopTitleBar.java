package com.petstation.gui.components;

import com.petstation.gui.navigation.ScreenId;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public final class DesktopTitleBar extends JPanel {
    private final JFrame frame;
    private Point dragOrigin;
    private Rectangle normalBounds;

    public DesktopTitleBar(JFrame frame, Consumer<ScreenId> navigator, Runnable closeAction) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 34));
        setBackground(AppColors.BROWN);

        JButton brand = new JButton("Pet Station – Quản lý cửa hàng thú cưng");
        brand.setIcon(loadLogo());
        brand.setFont(AppColors.font(Font.BOLD, 13));
        brand.setForeground(Color.WHITE);
        brand.setHorizontalAlignment(SwingConstants.LEFT);
        brand.setBorder(new EmptyBorder(0, 10, 0, 10));
        brand.setOpaque(false);
        brand.setContentAreaFilled(false);
        brand.setFocusPainted(false);
        brand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brand.setToolTipText("Về màn hình chính (Alt+Home)");
        brand.addActionListener(event -> navigator.accept(ScreenId.DASHBOARD));
        add(brand, BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(1, 3));
        controls.setOpaque(false);
        controls.add(windowButton("–", event -> frame.setState(Frame.ICONIFIED), false));
        controls.add(windowButton("□", event -> toggleMaximize(), false));
        controls.add(windowButton("×", event -> closeAction.run(), true));
        add(controls, BorderLayout.EAST);

        MouseAdapter dragHandler =
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent event) {
                        dragOrigin = event.getPoint();
                    }

                    @Override
                    public void mouseDragged(MouseEvent event) {
                        if (dragOrigin == null || isMaximized()) return;
                        Point screen = event.getLocationOnScreen();
                        frame.setLocation(screen.x - dragOrigin.x, screen.y - dragOrigin.y);
                    }

                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.getClickCount() == 2) toggleMaximize();
                    }
                };
        addMouseListener(dragHandler);
        addMouseMotionListener(dragHandler);
        brand.addMouseListener(dragHandler);
        brand.addMouseMotionListener(dragHandler);
    }

    private JButton windowButton(
            String text, java.awt.event.ActionListener listener, boolean close) {
        JButton button =
                new JButton(text) {
                    @Override
                    protected void paintComponent(Graphics graphics) {
                        if (getModel().isRollover()) {
                            graphics.setColor(
                                    close
                                            ? new Color(0xDC, 0x26, 0x26)
                                            : new Color(255, 255, 255, 28));
                            graphics.fillRect(0, 0, getWidth(), getHeight());
                        }
                        super.paintComponent(graphics);
                    }
                };
        button.setPreferredSize(new Dimension(46, 34));
        button.setFont(AppColors.font(Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setBorder(null);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    private void toggleMaximize() {
        if (isMaximized()) {
            frame.setExtendedState(Frame.NORMAL);
            if (normalBounds != null) frame.setBounds(normalBounds);
        } else {
            normalBounds = frame.getBounds();
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }

    private boolean isMaximized() {
        return (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
    }

    private Icon loadLogo() {
        URL resource = DesktopTitleBar.class.getResource("/images/pet-station-logo.png");
        if (resource == null) return null;
        Image image =
                new ImageIcon(resource).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
}
