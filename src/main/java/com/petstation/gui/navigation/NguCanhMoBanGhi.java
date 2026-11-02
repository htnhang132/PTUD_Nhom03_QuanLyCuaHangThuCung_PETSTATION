package com.petstation.gui.navigation;

import com.petstation.model.MaManHinh;
import com.petstation.service.DieuHuongBanGhiService;
import com.petstation.service.DieuHuongBanGhiService.YeuCau;

import java.awt.*;

import javax.swing.*;

/** Yêu cầu mở bản ghi được giữ qua bước mở ca và qua lần tải dữ liệu nền. */
public final class NguCanhMoBanGhi {
    private NguCanhMoBanGhi() {}

    public static void dat(MaManHinh screen, long id, String ma) {
        DieuHuongBanGhiService.dat(screen, id, ma);
    }

    public static YeuCau lay() {
        return DieuHuongBanGhiService.lay();
    }

    public static void xoa() {
        DieuHuongBanGhiService.xoa();
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
                boolean match =
                        key instanceof Number n && n.longValue() == r.id()
                                || r.ma() != null && r.ma().equals(String.valueOf(key));
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
