package com.petstation.gui.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

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

    private final int banKinh = 8;

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
                mauNen = new Color(0xF3, 0xF4, 0xF6);
                mauReChuot = new Color(0xE5, 0xE7, 0xEB);
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
                mauNen = new Color(0xD1, 0xFA, 0xE5);
                mauReChuot = new Color(0xA7, 0xF3, 0xD0);
                mauVanBan = new Color(0x06, 0x59, 0x42);
            }
            case TRONG_SUOT -> {
                mauNen = new Color(0xF3, 0xF4, 0xF6);
                mauReChuot = new Color(0xE5, 0xE7, 0xEB);
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

        doHoa2.setColor(!duocBat ? new Color(0xE5, 0xE7, 0xEB) : reChuot ? mauReChuot : mauNen);
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
    public Dimension getPreferredSize() {
        Dimension ngayDangXet = super.getPreferredSize();
        return new Dimension(ngayDangXet.width + 12, Math.max(ngayDangXet.height, 32));
    }
}
