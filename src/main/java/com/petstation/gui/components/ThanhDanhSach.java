package com.petstation.gui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Thanh công cụ một hoặc nhiều hàng có tự động xuống dòng và căn chỉnh đồng bộ chiều cao các nút. */
public final class ThanhDanhSach extends JPanel {
    private static final int CAO_HANG = 36;
    private static final int KHOANG_CACH_NGANG = 8;
    private static final int KHOANG_CACH_DOC = 8;
    private int chieuCaoDaTinh = -1;

    public ThanhDanhSach() {
        setOpaque(false);
        setLayout(null);
        addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        int caoMoi = sapXep(false);
                        if (caoMoi != chieuCaoDaTinh) {
                            chieuCaoDaTinh = caoMoi;
                            revalidate();
                        }
                    }
                });
    }

    public void themBoLoc(Component c) {
        add(c);
    }

    public void themBoLoc(String nhan, JComponent c) {
        JPanel nhom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        nhom.setOpaque(false);
        JLabel label = new JLabel(nhan);
        label.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        label.setForeground(MauUngDung.VAN_BAN_TOI);
        label.setLabelFor(c);
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, CAO_HANG));
        nhom.add(label);
        nhom.add(c);
        themBoLoc(nhom);
    }

    @Override
    public Dimension getPreferredSize() {
        int rong = getWidth();
        if (rong <= 0 && getParent() != null && getParent().getWidth() > 0) {
            rong = getParent().getWidth();
        }
        return new Dimension(rong > 0 ? rong : 700, sapXep(false));
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(380, CAO_HANG);
    }

    @Override
    public void doLayout() {
        chieuCaoDaTinh = sapXep(true);
    }

    private int sapXep(boolean datViTri) {
        int rong = getWidth();
        if (rong <= 0 && getParent() != null && getParent().getWidth() > 0) {
            rong = getParent().getWidth();
        }
        if (rong <= 0) rong = 960;

        int x = 0;
        int y = 0;

        for (Component c : getComponents()) {
            if (!c.isVisible()) continue;
            Dimension d = c.getPreferredSize();
            int cw = (d != null) ? d.width : 0;
            if (c instanceof JTextField && ("timKiemDanhMuc".equals(c.getName()) || cw >= 200)) {
                cw = Math.min(rong, Math.max(220, Math.min(280, cw)));
            } else if (c instanceof AbstractButton) {
                cw = Math.min(rong, Math.max(76, cw));
            } else {
                cw = Math.min(rong, cw);
            }

            int ch = CAO_HANG;

            if (x > 0 && x + cw > rong) {
                x = 0;
                y += CAO_HANG + KHOANG_CACH_DOC;
            }

            if (datViTri) {
                c.setBounds(x, y, cw, ch);
            }
            x += cw + KHOANG_CACH_NGANG;
        }

        return y + CAO_HANG;
    }

    public static JPanel tieuDe(JLabel title, JLabel count) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.add(title);
        count.setForeground(MauUngDung.VAN_BAN_XAM);
        p.add(count);
        return p;
    }

    public static String soKetQua(int shown, int total) {
        return "· " + (shown == total ? shown : shown + "/" + total) + " kết quả";
    }
}
