package com.petstation.gui.components.common;

import java.awt.*;

import javax.swing.*;

public class TruongMatKhauAnHien extends JPanel {
    private JPasswordField txtTruongMatKhau;

    private JToggleButton btnNutBatTat;

    private boolean matKhauHienThi = false;

    public TruongMatKhauAnHien() {
        this(20);
    }

    public TruongMatKhauAnHien(int cacCot) {
        setLayout(new BorderLayout(5, 0));

        txtTruongMatKhau = new JPasswordField(cacCot);

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

        add(txtTruongMatKhau, BorderLayout.CENTER);
        txtTruongMatKhau.putClientProperty("JTextField.trailingComponent", btnNutBatTat);

        setBorder(BorderFactory.createEmptyBorder());
    }

    private void batTatMatKhauHienThi() {
        matKhauHienThi = !matKhauHienThi;

        if (matKhauHienThi) {
            txtTruongMatKhau.setEchoChar((char) 0);
            btnNutBatTat.setToolTipText("Ẩn mật khẩu");
            btnNutBatTat.setSelected(true);
        } else {
            txtTruongMatKhau.setEchoChar('●');
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
                    net.setColor(c.isEnabled() ? new Color(97, 52, 29) : Color.GRAY);
                    net.setStroke(
                            new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    java.awt.geom.Path2D mat = new java.awt.geom.Path2D.Double();
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

    public char[] layMatKhau() {
        return txtTruongMatKhau.getPassword();
    }

    public String layVanBanMatKhau() {
        return new String(txtTruongMatKhau.getPassword());
    }

    public void datMatKhau(String matKhau) {
        txtTruongMatKhau.setText(matKhau);
    }

    public void xoa() {
        txtTruongMatKhau.setText("");
    }

    public JPasswordField layTruongMatKhau() {
        return txtTruongMatKhau;
    }

    @Override
    public void setEnabled(boolean duocBat) {
        super.setEnabled(duocBat);
        txtTruongMatKhau.setEnabled(duocBat);
        btnNutBatTat.setEnabled(duocBat);
    }

    public void datCacCot(int cacCot) {
        txtTruongMatKhau.setColumns(cacCot);
    }

    public void datHienThiThayTheKyTu(char giaTriDangXet) {
        if (!matKhauHienThi) {
            txtTruongMatKhau.setEchoChar(giaTriDangXet);
        }
    }

    public void themBoLangNgheThaoTac(java.awt.event.ActionListener boLangNghe) {
        txtTruongMatKhau.addActionListener(boLangNghe);
    }

    @Override
    public void requestFocus() {
        txtTruongMatKhau.requestFocus();
    }
}
