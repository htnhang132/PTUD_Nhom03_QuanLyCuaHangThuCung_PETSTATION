package com.petstation.gui.chung.thanhphan;

import com.petstation.gui.chung.MauUngDung;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JToggleButton;

public class TruongMatKhauAnHien extends JPanel {
    private JPasswordField oTruongMatKhau;

    private JToggleButton btnNutBatTat;

    private boolean matKhauHienThi = false;

    public TruongMatKhauAnHien() {
        this(20);
    }

    public TruongMatKhauAnHien(int cacCot) {
        setLayout(new BorderLayout(5, 0));
        oTruongMatKhau = new JPasswordField(cacCot);
        btnNutBatTat = new JToggleButton();
        btnNutBatTat.setIcon(taoBieuTuongMat(false));
        btnNutBatTat.setSelectedIcon(taoBieuTuongMat(true));
        btnNutBatTat.setPreferredSize(new Dimension(34, 28));
        btnNutBatTat.setBorderPainted(false);
        btnNutBatTat.setContentAreaFilled(false);
        btnNutBatTat.getAccessibleContext().setAccessibleName("Hiển thị hoặc ẩn mật khẩu");
        btnNutBatTat.setFocusPainted(false);
        btnNutBatTat.setToolTipText("Hiển thị/Ẩn mật khẩu");
        btnNutBatTat.addActionListener(suKien -> batTatMatKhauHienThi());
        add(oTruongMatKhau, BorderLayout.CENTER);
        oTruongMatKhau.putClientProperty("JTextField.trailingComponent", btnNutBatTat);
        setBorder(BorderFactory.createEmptyBorder());
    }

    private void batTatMatKhauHienThi() {
        matKhauHienThi = !matKhauHienThi;
        if (matKhauHienThi) {
            oTruongMatKhau.setEchoChar((char) 0);
            btnNutBatTat.setToolTipText("Ẩn mật khẩu");
            btnNutBatTat.setSelected(true);
        } else {
            oTruongMatKhau.setEchoChar('●');
            btnNutBatTat.setToolTipText("Hiển thị mật khẩu");
            btnNutBatTat.setSelected(false);
        }
    }

    private static Icon taoBieuTuongMat(boolean an) {
        return new Icon() {
            public int getIconWidth() {
                return 20;
            }

            public int getIconHeight() {
                return 20;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D net = (Graphics2D) g.create();
                try {
                    net.translate(x, y);
                    net.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    net.setColor(c.isEnabled() ? MauUngDung.NAU : Color.GRAY);
                    net.setStroke(
                            new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    Path2D mat = new Path2D.Double();
                    mat.moveTo(1, 10);
                    mat.curveTo(6, 2, 14, 2, 19, 10);
                    mat.curveTo(14, 18, 6, 18, 1, 10);
                    net.draw(mat);
                    net.drawOval(7, 7, 6, 6);
                    if (an) net.drawLine(2, 2, 18, 18);
                } finally {
                    net.dispose();
                }
            }
        };
    }

    public String layVanBanMatKhau() {
        return new String(oTruongMatKhau.getPassword());
    }

    public void xoa() {
        oTruongMatKhau.setText("");
    }

    public JPasswordField layTruongMatKhau() {
        return oTruongMatKhau;
    }

    @Override
    public void setEnabled(boolean duocBat) {
        super.setEnabled(duocBat);
        oTruongMatKhau.setEnabled(duocBat);
        btnNutBatTat.setEnabled(duocBat);
    }

    @Override
    public void requestFocus() {
        oTruongMatKhau.requestFocus();
    }
}
