package com.petstation.gui.banhang;

import com.petstation.gui.chung.MauUngDung;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public final class BangChiTietHoaDon {
    private BangChiTietHoaDon() {}

    public static void apDung(JTable bang) {
        DefaultTableCellRenderer soLuong = new DefaultTableCellRenderer();
        soLuong.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer tien = new DefaultTableCellRenderer();
        tien.setHorizontalAlignment(SwingConstants.RIGHT);
        bang.getColumnModel().getColumn(0).setPreferredWidth(300);
        bang.getColumnModel().getColumn(1).setPreferredWidth(45);
        bang.getColumnModel().getColumn(1).setCellRenderer(soLuong);
        for (int cot = 2; cot < 4; cot++) {
            bang.getColumnModel().getColumn(cot).setPreferredWidth(105);
            bang.getColumnModel().getColumn(cot).setCellRenderer(tien);
        }
        bang.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        new TableCellRenderer() {
                            private final JTextArea oTen = new JTextArea();

                            public Component getTableCellRendererComponent(
                                    JTable bangDuLieu,
                                    Object value,
                                    boolean selected,
                                    boolean focus,
                                    int row,
                                    int col) {
                                oTen.setText(String.valueOf(value));
                                oTen.setFont(bangDuLieu.getFont());
                                oTen.setLineWrap(true);
                                oTen.setWrapStyleWord(true);
                                oTen.setBorder(BorderFactory.createEmptyBorder(7, 8, 7, 8));
                                oTen.setForeground(
                                        selected
                                                ? bangDuLieu.getSelectionForeground()
                                                : MauUngDung.VAN_BAN_TOI);
                                oTen.setBackground(
                                        selected
                                                ? bangDuLieu.getSelectionBackground()
                                                : MauUngDung.TRANG);
                                oTen.setSize(
                                        Math.max(
                                                40,
                                                bangDuLieu
                                                        .getColumnModel()
                                                        .getColumn(col)
                                                        .getWidth()),
                                        Short.MAX_VALUE);
                                return oTen;
                            }
                        });
        bang.getColumnModel()
                .addColumnModelListener(
                        new TableColumnModelListener() {
                            public void columnAdded(TableColumnModelEvent e) {}

                            public void columnRemoved(TableColumnModelEvent e) {}

                            public void columnMoved(TableColumnModelEvent e) {}

                            public void columnSelectionChanged(ListSelectionEvent e) {}

                            public void columnMarginChanged(ChangeEvent e) {
                                capNhatChieuCao(bang);
                            }
                        });
        bang.getModel().addTableModelListener(e -> capNhatChieuCao(bang));
        if (bang.getRowSorter() != null)
            bang.getRowSorter().addRowSorterListener(e -> capNhatChieuCao(bang));
        capNhatChieuCao(bang);
    }

    private static void capNhatChieuCao(JTable bang) {
        if (bang.getColumnCount() == 0) return;
        JTextArea oDo = new JTextArea();
        oDo.setFont(bang.getFont());
        oDo.setLineWrap(true);
        oDo.setWrapStyleWord(true);
        oDo.setBorder(BorderFactory.createEmptyBorder(7, 8, 7, 8));
        int rong = Math.max(40, bang.getColumnModel().getColumn(0).getWidth());
        for (int dong = 0; dong < bang.getRowCount(); dong++) {
            oDo.setText(String.valueOf(bang.getValueAt(dong, 0)));
            oDo.setSize(rong, Short.MAX_VALUE);
            int cao = Math.max(38, oDo.getPreferredSize().height);
            if (bang.getRowHeight(dong) != cao) bang.setRowHeight(dong, cao);
        }
    }
}
