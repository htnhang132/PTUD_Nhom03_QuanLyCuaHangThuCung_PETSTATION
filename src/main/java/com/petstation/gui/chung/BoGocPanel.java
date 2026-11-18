package com.petstation.gui.chung;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class BoGocPanel extends JPanel {
    private int banKinh;

    private Color nen;

    private boolean coBong;

    private boolean coVien;

    public BoGocPanel() {
        this(MauUngDung.BAN_KINH_THE, MauUngDung.THE_NEN, true, true);
    }

    public BoGocPanel(int banKinh, Color nen) {
        this(banKinh, nen, false, false);
    }

    public BoGocPanel(int banKinh, Color nen, boolean coBong) {
        this(banKinh, nen, coBong, coBong);
    }

    public BoGocPanel(int banKinh, Color nen, boolean coBong, boolean coVien) {
        this.banKinh = banKinh;
        this.nen = nen;
        this.coBong = coBong;
        this.coVien = coVien;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics doHoa) {
        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
        doHoa2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int rong = getWidth() - (coBong ? 3 : 1);
        int cao = getHeight() - (coBong ? 3 : 1);
        if (rong > 0 && cao > 0) {
            if (coBong) {
                doHoa2.setColor(MauUngDung.voiDoTrong(Color.BLACK, 18));
                doHoa2.fillRoundRect(2, 3, rong, cao, banKinh, banKinh);
            }
            doHoa2.setColor(nen);
            doHoa2.fillRoundRect(0, 0, rong, cao, banKinh, banKinh);
            if (coVien) {
                doHoa2.setColor(MauUngDung.VIEN);
                doHoa2.drawRoundRect(0, 0, rong, cao, banKinh, banKinh);
            }
        }
        doHoa2.dispose();
        super.paintComponent(doHoa);
    }
}
