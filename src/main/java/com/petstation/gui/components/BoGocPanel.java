package com.petstation.gui.components;

import java.awt.*;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho bo góc vùng hiển thị. */
public class BoGocPanel extends JPanel {
    /** Bán kính. */
    private int banKinh;

    /** Nền. */
    private Color nen;

    /** Có bóng. */
    private boolean coBong;

    public BoGocPanel(int banKinh, Color nen) {
        this(banKinh, nen, false);
    }

    public BoGocPanel(int banKinh, Color nen, boolean coBong) {
        this.banKinh = banKinh;
        this.nen = nen;
        this.coBong = coBong;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics doHoa) {
        // Đồ họa 2.
        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
        doHoa2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (coBong) {
            doHoa2.setColor(new Color(0, 0, 0, 20));
            doHoa2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, banKinh, banKinh);
        }

        doHoa2.setColor(nen);
        doHoa2.fillRoundRect(
                0,
                0,
                getWidth() - (coBong ? 2 : 0),
                getHeight() - (coBong ? 2 : 0),
                banKinh,
                banKinh);
        doHoa2.dispose();
        super.paintComponent(doHoa);
    }
}
