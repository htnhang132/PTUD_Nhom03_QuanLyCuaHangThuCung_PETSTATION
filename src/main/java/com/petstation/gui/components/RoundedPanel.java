package com.petstation.gui.components;

import java.awt.*;

import javax.swing.*;

public class RoundedPanel extends JPanel {
    private int radius;
    private Color background;
    private boolean hasShadow;

    public RoundedPanel(int radius, Color background) {
        this(radius, background, false);
    }

    public RoundedPanel(int radius, Color background, boolean hasShadow) {
        this.radius = radius;
        this.background = background;
        this.hasShadow = hasShadow;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (hasShadow) {
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, radius, radius);
        }

        g2.setColor(background);
        g2.fillRoundRect(
                0,
                0,
                getWidth() - (hasShadow ? 2 : 0),
                getHeight() - (hasShadow ? 2 : 0),
                radius,
                radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
