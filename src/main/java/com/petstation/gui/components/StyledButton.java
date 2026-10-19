package com.petstation.gui.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

/**
 * Nút bấm thống nhất. PRIMARY: hành động chính (nền đỏ). DANGER: hành động nguy hiểm (viền đỏ, nền
 * trắng - khác hẳn PRIMARY).
 */
public class StyledButton extends JButton {
    public enum Variant {
        PRIMARY,
        SECONDARY,
        DANGER,
        SUCCESS,
        GHOST
    }

    private Color bgColor;
    private Color hoverColor;
    private Color textColor;
    private Color borderColor;
    private final int radius = 10;

    public StyledButton(String text, Variant variant) {
        super(text);
        applyVariant(variant);
        setOpaque(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setFont(AppColors.font(Font.BOLD, 13));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        repaint();
                    }
                });
    }

    private void applyVariant(Variant v) {
        borderColor = null;
        switch (v) {
            case PRIMARY -> {
                bgColor = AppColors.RED_BRAND;
                hoverColor = AppColors.RED_HOVER;
                textColor = Color.WHITE;
            }
            case SECONDARY -> {
                bgColor = AppColors.YELLOW;
                hoverColor = new Color(0xE5, 0xBF, 0x2A);
                textColor = AppColors.BROWN;
            }
            case DANGER -> {
                bgColor = Color.WHITE;
                hoverColor = AppColors.BAD_BG;
                textColor = AppColors.DANGER_TEXT;
                borderColor = AppColors.DANGER_TEXT;
            }
            case SUCCESS -> {
                bgColor = new Color(0xD1, 0xFA, 0xE5);
                hoverColor = new Color(0xA7, 0xF3, 0xD0);
                textColor = new Color(0x06, 0x59, 0x42);
            }
            case GHOST -> {
                bgColor = new Color(0xF3, 0xF4, 0xF6);
                hoverColor = new Color(0xE5, 0xE7, 0xEB);
                textColor = AppColors.TEXT_DARK;
                borderColor = AppColors.BORDER;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean enabled = isEnabled();
        boolean hover = enabled && getModel().isRollover();
        g2.setColor(!enabled ? new Color(0xE5, 0xE7, 0xEB) : hover ? hoverColor : bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        if (borderColor != null && enabled) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        if (isFocusOwner() && enabled) {
            g2.setColor(AppColors.YELLOW);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
        }
        g2.dispose();
        setForeground(enabled ? textColor : AppColors.DISABLED);
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width + 20, Math.max(d.height, 36));
    }
}
