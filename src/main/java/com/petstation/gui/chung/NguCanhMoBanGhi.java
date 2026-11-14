package com.petstation.gui.chung;

import com.petstation.model.chung.MaManHinh;
import com.petstation.service.chung.DieuHuongBanGhiService;
import com.petstation.service.chung.DieuHuongBanGhiService.YeuCau;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;

public final class NguCanhMoBanGhi {
    private NguCanhMoBanGhi() {}

    public static YeuCau lay() {
        return DieuHuongBanGhiService.macDinh().lay();
    }

    public static void xoa() {
        DieuHuongBanGhiService.macDinh().xoa();
    }

    public static void apDung(Component root) {
        YeuCau r = lay();
        if (r == null
                || !(root instanceof JComponent view)
                || view.getClientProperty("maManHinh") != r.manHinh()
                || r.manHinh() == MaManHinh.DAT_PHONG_LUU_TRU) return;
        if (!chon(root, r)) {
            xoaLoc(root, r);
            if (chon(root, r)) xoa();
        } else xoa();
    }

    private static void xoaLoc(Component c, YeuCau r) {
        if (c instanceof JTextField t && "timKiemDanhMuc".equals(t.getName()))
            t.setText(r.ma() == null ? "" : r.ma());
        if (c instanceof JComboBox<?> box
                && box.getName() != null
                && box.getName().startsWith("loc:"))
            for (int i = 0; i < box.getItemCount(); i++)
                if ("Tất cả".equals(box.getItemAt(i))) {
                    box.setSelectedIndex(i);
                    break;
                }
        if (c instanceof Container p) for (Component child : p.getComponents()) xoaLoc(child, r);
    }

    private static boolean chon(Component c, YeuCau r) {
        if (c instanceof JTable t) {
            var m = t.getModel();
            for (int i = 0; i < m.getRowCount(); i++) {
                Object key = m.getValueAt(i, 0);
                boolean match = r.ma() != null && r.ma().equals(String.valueOf(key));
                if (match) {
                    int row = t.convertRowIndexToView(i);
                    if (row >= 0) {
                        t.setRowSelectionInterval(row, row);
                        t.scrollRectToVisible(t.getCellRect(row, 0, true));
                        return true;
                    }
                }
            }
        }
        if (c instanceof Container p)
            for (Component child : p.getComponents()) if (chon(child, r)) return true;
        return false;
    }
}
