package com.petstation.gui.components;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/** Thanh một hàng. Thành phần gốc được giữ để quyền và trạng thái không bị sao chép. */
public final class ThanhDanhSach extends JPanel {
    private final List<Component> boLoc = new ArrayList<>();
    private final JButton locThem = new JButton("Lọc thêm");
    private final JButton them = new JButton("⋯");

    public ThanhDanhSach() {
        setOpaque(false);
        setLayout(null);
        add(locThem);
        add(them);
        locThem.addActionListener(e -> moBoLoc());
        them.setToolTipText("Các thao tác khác");
        them.addActionListener(
                e -> {
                    JPopupMenu menu = new JPopupMenu();
                    for (Component c : getComponents())
                        if (c instanceof AbstractButton b
                                && c != them
                                && c != locThem
                                && !boLoc.contains(c)
                                && !c.isVisible()) {
                            JMenuItem item = new JMenuItem(b.getText(), b.getIcon());
                            item.setEnabled(b.isEnabled());
                            item.setToolTipText(b.getToolTipText());
                            item.addActionListener(ev -> b.doClick());
                            menu.add(item);
                        }
                    menu.show(them, 0, them.getHeight());
                });
    }

    public void themBoLoc(Component c) {
        add(c);
        boLoc.add(c);
    }

    public void themBoLoc(String nhan, JComponent c) {
        JPanel nhom = new JPanel(new BorderLayout(4, 0));
        nhom.setOpaque(false);
        JLabel label = new JLabel(nhan);
        label.setLabelFor(c);
        nhom.add(label, BorderLayout.WEST);
        nhom.add(c);
        themBoLoc(nhom);
    }

    private void moBoLoc() {
        JPopupMenu menu = new JPopupMenu();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        List<Component> an = boLoc.stream().filter(c -> !c.isVisible()).toList();
        for (Component c : an) {
            c.setVisible(true);
            menu.add(c);
        }
        menu.addPopupMenuListener(
                new PopupMenuListener() {
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

                    public void popupMenuCanceled(PopupMenuEvent e) {
                        traLai();
                    }

                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        traLai();
                    }

                    private void traLai() {
                        for (Component c : an) if (c.getParent() == menu) ThanhDanhSach.this.add(c);
                        revalidate();
                        repaint();
                    }
                });
        menu.show(locThem, 0, locThem.getHeight());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(700, 36);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(380, 36);
    }

    @Override
    public void doLayout() {
        int w = getWidth(), gap = 8;
        List<Component> left = new ArrayList<>(), right = new ArrayList<>();
        Component search = null;
        for (Component c : getComponents()) {
            if (c == locThem || c == them) continue;
            if (c instanceof JTextField && search == null) {
                search = c;
                continue;
            }
            (boLoc.contains(c) || !(c instanceof AbstractButton) ? left : right).add(c);
            c.setVisible(true);
        }
        Component export =
                right.stream()
                        .filter(c -> ((AbstractButton) c).getText().contains("Xuất"))
                        .findFirst()
                        .orElse(null);
        if (export != null) {
            right.remove(export);
            right.add(export);
        }
        Component primary =
                right.stream()
                        .filter(c -> ((AbstractButton) c).getText().startsWith("+"))
                        .findFirst()
                        .orElseGet(
                                () ->
                                        right.stream()
                                                .filter(c -> c != export)
                                                .findFirst()
                                                .orElse(null));
        if (primary != null) {
            right.remove(primary);
            right.add(0, primary);
        }
        int searchW = search == null ? 0 : Math.min(260, Math.max(150, w / 5));
        int total = searchW + (search == null ? 0 : gap);
        for (Component c : left) total += c.getPreferredSize().width + gap;
        for (Component c : right) total += c.getPreferredSize().width + gap;
        if (search != null && total > w) {
            int shrink = Math.min(total - w, searchW - 150);
            searchW -= shrink;
            total -= shrink;
        }
        boolean hiddenFilters = false, hiddenActions = false;
        for (int i = left.size() - 1; total > w && i >= 0; i--) {
            Component c = left.get(i);
            c.setVisible(false);
            total -= c.getPreferredSize().width + gap;
            if (!hiddenFilters) {
                total += 96;
                hiddenFilters = true;
            }
        }
        for (int i = right.size() - 1; total > w && i >= 0; i--) {
            Component c = right.get(i);
            if (c == export || c == primary) continue;
            c.setVisible(false);
            total -= c.getPreferredSize().width + gap;
            if (!hiddenActions) {
                total += 44;
                hiddenActions = true;
            }
        }
        locThem.setVisible(hiddenFilters);
        them.setVisible(hiddenActions);
        int x = 0;
        if (search != null) {
            search.setBounds(0, 0, searchW, 36);
            x = searchW + gap;
        }
        for (Component c : left)
            if (c.isVisible()) {
                int cw = c.getPreferredSize().width;
                c.setBounds(x, 0, cw, 36);
                x += cw + gap;
            }
        if (hiddenFilters) locThem.setBounds(x, 0, 88, 36);
        int end = w;
        if (hiddenActions) {
            them.setBounds(end - 36, 0, 36, 36);
            end -= 44;
        }
        for (int i = right.size() - 1; i >= 0; i--) {
            Component c = right.get(i);
            if (!c.isVisible()) continue;
            int cw = c.getPreferredSize().width;
            end -= cw;
            c.setBounds(end, 0, cw, 36);
            end -= gap;
        }
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
