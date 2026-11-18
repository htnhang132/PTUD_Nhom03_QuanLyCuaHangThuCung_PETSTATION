package com.petstation.gui.chung;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class NutDinhDang extends JButton {
    public enum KieuNut {
        CHINH,
        PHU,
        NGUY_HIEM,
        THANH_CONG,
        TRONG_SUOT
    }

    private Color mauNen;

    private Color mauReChuot;

    private Color mauVanBan;

    private Color mauVien;

    private final int banKinh = MauUngDung.BAN_KINH_NUT;

    public NutDinhDang(String vanBan, KieuNut kieu) {
        super(vanBan);
        apDungKieu(kieu);
        setOpaque(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent suKien) {
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent suKien) {
                        repaint();
                    }
                });
    }

    private void apDungKieu(KieuNut giaTri) {
        mauVien = null;
        switch (giaTri) {
            case CHINH -> {
                mauNen = MauUngDung.DO_THUONG_HIEU;
                mauReChuot = MauUngDung.DO_RE_CHUOT;
                mauVanBan = Color.WHITE;
            }
            case PHU -> {
                mauNen = MauUngDung.XAM_NEN_TRUNG;
                mauReChuot = MauUngDung.XAM_VIEN_THE;
                mauVien = MauUngDung.VIEN;
                mauVanBan = MauUngDung.NAU;
            }
            case NGUY_HIEM -> {
                mauNen = Color.WHITE;
                mauReChuot = MauUngDung.KHONG_HOP_LE_NEN;
                mauVanBan = MauUngDung.VAN_BAN_NGUY_HIEM;
                mauVien = MauUngDung.VAN_BAN_NGUY_HIEM;
            }
            case THANH_CONG -> {
                mauNen = MauUngDung.THANH_CONG_NEN;
                mauReChuot = MauUngDung.THANH_CONG_HOVER;
                mauVanBan = MauUngDung.THANH_CONG_CHU;
            }
            case TRONG_SUOT -> {
                mauNen = MauUngDung.THE_NEN;
                mauReChuot = MauUngDung.KEM_NEN_NHAN;
                mauVanBan = MauUngDung.VAN_BAN_TOI;
                mauVien = MauUngDung.VIEN;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics doHoa) {
        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
        doHoa2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean duocBat = isEnabled();
        boolean reChuot = duocBat && getModel().isRollover();
        int x = 1;
        int y = 1;
        int w = getWidth() - 2;
        int h = getHeight() - 2;
        doHoa2.setColor(!duocBat ? MauUngDung.XAM_VIEN_THE : reChuot ? mauReChuot : mauNen);
        doHoa2.fillRoundRect(x, y, w, h, banKinh, banKinh);
        if (mauVien != null && duocBat) {
            doHoa2.setColor(mauVien);
            doHoa2.drawRoundRect(x, y, w - 1, h - 1, banKinh, banKinh);
        }
        if (isFocusOwner() && duocBat) {
            doHoa2.setColor(MauUngDung.VANG);
            doHoa2.setStroke(new BasicStroke(2));
            doHoa2.drawRoundRect(x, y, w - 1, h - 1, banKinh, banKinh);
        }
        doHoa2.dispose();
        setForeground(duocBat ? mauVanBan : MauUngDung.VO_HIEU_HOA);
        super.paintComponent(doHoa);
    }

    @Override
    public void setText(String vanBan) {
        if (vanBan != null && (vanBan.endsWith(" ▾") || vanBan.endsWith(" ▴"))) {
            boolean xuong = vanBan.endsWith(" ▾");
            super.setText(vanBan.substring(0, vanBan.length() - 2));
            setIcon(new IconMuiTen(xuong));
            setHorizontalTextPosition(SwingConstants.LEADING);
            setIconTextGap(6);
            return;
        }
        if (getIcon() instanceof IconMuiTen) {
            setIcon(null);
        }
        super.setText(vanBan);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension ngayDangXet = super.getPreferredSize();
        return new Dimension(
                ngayDangXet.width + 12, Math.max(ngayDangXet.height, MauUngDung.CAO_NUT));
    }

    private static final class IconMuiTen implements Icon {
        private final boolean xuong;

        private IconMuiTen(boolean xuong) {
            this.xuong = xuong;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            int[] xs = {x, x + 8, x + 4};
            int[] ys = xuong ? new int[] {y + 1, y + 1, y + 6} : new int[] {y + 6, y + 6, y + 1};
            g2.fillPolygon(xs, ys, 3);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 8;
        }

        @Override
        public int getIconHeight() {
            return 7;
        }
    }
}
