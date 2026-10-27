package com.petstation.gui.components;

import java.awt.*;

import javax.swing.*;

public final class BieuTuongGiaoDien implements Icon {
    public enum Kieu {
        CHO_PHEP,
        KHONG_CHO_PHEP,
        TRUOC,
        SAU,
        CANH_BAO
    }

    private final Kieu kieu;

    public BieuTuongGiaoDien(Kieu kieu) {
        this.kieu = kieu;
    }

    public int getIconWidth() {
        return 18;
    }

    public int getIconHeight() {
        return 18;
    }

    public void paintIcon(Component thanhPhan, Graphics doHoa, int ngang, int doc) {
        Graphics2D net = (Graphics2D) doHoa.create();
        try {
            net.translate(ngang, doc);
            net.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            net.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            net.setColor(
                    kieu == Kieu.CHO_PHEP
                            ? MauUngDung.THANH_CONG
                            : kieu == Kieu.CANH_BAO ? MauUngDung.CANH_BAO : MauUngDung.NAU);
            switch (kieu) {
                case CHO_PHEP -> {
                    net.drawOval(1, 1, 15, 15);
                    net.drawLine(5, 9, 8, 12);
                    net.drawLine(8, 12, 13, 6);
                }
                case KHONG_CHO_PHEP -> {
                    net.setColor(MauUngDung.VAN_BAN_XAM);
                    net.drawLine(5, 9, 13, 9);
                }
                case TRUOC -> {
                    net.drawLine(12, 3, 6, 9);
                    net.drawLine(6, 9, 12, 15);
                }
                case SAU -> {
                    net.drawLine(6, 3, 12, 9);
                    net.drawLine(12, 9, 6, 15);
                }
                case CANH_BAO -> {
                    net.drawPolygon(new int[] {9, 1, 17}, new int[] {1, 16, 16}, 3);
                    net.drawLine(9, 6, 9, 10);
                    net.fillOval(8, 12, 2, 2);
                }
            }
        } finally {
            net.dispose();
        }
    }
}
