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

public final class ThanhDanhSach extends JPanel {
    private static final int CAO_HANG = 32;
    private static final int KHOANG_CACH_NGANG = 5;
    private static final int KHOANG_CACH_DOC = 6;
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
        JPanel nhom = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        nhom.setOpaque(false);
        JLabel label = new JLabel(nhan);
        label.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        label.setForeground(MauUngDung.VAN_BAN_TOI);
        label.setLabelFor(c);
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
        int caoMoi = sapXep(true);
        for (Component c : getComponents()) {
            if (c instanceof java.awt.Container cont) {
                cont.doLayout();
            }
        }
        if (caoMoi != chieuCaoDaTinh || (getHeight() > 0 && Math.abs(getHeight() - caoMoi) > 2)) {
            chieuCaoDaTinh = caoMoi;
            javax.swing.SwingUtilities.invokeLater(
                    () -> {
                        revalidate();
                        if (getParent() != null)
                            getParent().revalidate();
                    });
        }
    }

    private static void chuanHoaKichThuocCon(Component c) {
        if (c instanceof javax.swing.JComboBox<?> cbo) {
            java.awt.FontMetrics fm = cbo.getFontMetrics(cbo.getFont());
            int maxW = 96;
            for (int i = 0; i < cbo.getItemCount(); i++) {
                Object item = cbo.getItemAt(i);
                if (item != null) {
                    maxW = Math.max(maxW, fm.stringWidth(item.toString()) + 38);
                }
            }
            Object sel = cbo.getSelectedItem();
            if (sel != null) {
                maxW = Math.max(maxW, fm.stringWidth(sel.toString()) + 38);
            }
            int w = Math.min(215, Math.max(96, maxW));
            Dimension cur = cbo.getPreferredSize();
            if (cur == null || cur.width != w || cur.height != CAO_HANG) {
                cbo.setPreferredSize(new Dimension(w, CAO_HANG));
            }
        } else if (c instanceof JPanel pnl) {
            for (Component child : pnl.getComponents()) {
                chuanHoaKichThuocCon(child);
            }
        }
    }

    private int sapXep(boolean datViTri) {
        int rong = getWidth();
        if (rong <= 0 && getParent() != null && getParent().getWidth() > 0) {
            rong = getParent().getWidth();
        }
        if (rong <= 0)
            rong = 960;

        java.util.List<Component> cacPhanTu = new java.util.ArrayList<>();
        for (Component c : getComponents()) {
            if (c.isVisible()) {
                chuanHoaKichThuocCon(c);
                cacPhanTu.add(c);
            }
        }

        java.awt.Insets le = getInsets();
        int rongNoiDung = Math.max(1, rong - le.left - le.right);
        int caoHang = CAO_HANG;
        for (Component c : cacPhanTu) {
            caoHang = Math.max(caoHang, c.getPreferredSize().height);
        }

        int x = le.left;
        int y = le.top;
        for (Component c : cacPhanTu) {
            Dimension d = c.getPreferredSize();
            int cw = d.width;
            if (c instanceof JTextField txt && ("timKiemDanhMuc".equals(c.getName()) || cw >= 140)) {
                String goiY = "";
                Object ph = txt.getClientProperty("JTextField.placeholderText");
                if (ph != null)
                    goiY = ph.toString();
                else if (txt.getAccessibleContext() != null
                        && txt.getAccessibleContext().getAccessibleName() != null) {
                    goiY = txt.getAccessibleContext().getAccessibleName();
                }
                int wGoiY = goiY.isEmpty()
                        ? 190
                        : txt.getFontMetrics(txt.getFont()).stringWidth(goiY) + 30;
                cw = Math.max(190, Math.min(245, wGoiY));
            } else if (c instanceof AbstractButton) {
                cw = Math.max(54, cw);
            }
            cw = Math.min(rongNoiDung, cw);
            int ch = Math.max(CAO_HANG, d.height);
            if (x > le.left && x + cw > rong - le.right) {
                x = le.left;
                y += caoHang + KHOANG_CACH_DOC;
            }
            if (datViTri) {
                c.setBounds(x, y + (caoHang - ch) / 2, cw, ch);
            }
            x += cw + KHOANG_CACH_NGANG;
        }

        return y + caoHang + le.bottom;
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
