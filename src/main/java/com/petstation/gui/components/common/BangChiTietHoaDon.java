package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.*;

/** Giữ tên hàng đầy đủ, căn số lượng và tiền trên bảng thanh toán. */
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
                            private final JTextArea txtTen = new JTextArea();

                            public Component getTableCellRendererComponent(
                                    JTable tbl,
                                    Object value,
                                    boolean selected,
                                    boolean focus,
                                    int row,
                                    int col) {
                                txtTen.setText(String.valueOf(value));
                                txtTen.setFont(tbl.getFont());
                                txtTen.setLineWrap(true);
                                txtTen.setWrapStyleWord(true);
                                txtTen.setBorder(BorderFactory.createEmptyBorder(7, 8, 7, 8));
                                txtTen.setForeground(
                                        selected
                                                ? tbl.getSelectionForeground()
                                                : MauUngDung.VAN_BAN_TOI);
                                txtTen.setBackground(
                                        selected ? tbl.getSelectionBackground() : MauUngDung.TRANG);
                                txtTen.setSize(
                                        Math.max(
                                                40, tbl.getColumnModel().getColumn(col).getWidth()),
                                        Short.MAX_VALUE);
                                return txtTen;
                            }
                        });
        bang.getColumnModel().addColumnModelListener(new javax.swing.event.TableColumnModelListener() {
            public void columnAdded(javax.swing.event.TableColumnModelEvent e) {}
            public void columnRemoved(javax.swing.event.TableColumnModelEvent e) {}
            public void columnMoved(javax.swing.event.TableColumnModelEvent e) {}
            public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {}
            public void columnMarginChanged(javax.swing.event.ChangeEvent e) { capNhatChieuCao(bang); }
        });
        bang.getModel().addTableModelListener(e -> capNhatChieuCao(bang));
        if(bang.getRowSorter()!=null) bang.getRowSorter().addRowSorterListener(e -> capNhatChieuCao(bang));
        capNhatChieuCao(bang);
    }

    /** Tính trước khi vẽ để khung cắt của ô đã đủ cao ngay từ lần hiển thị đầu. */
    private static void capNhatChieuCao(JTable bang) {
        if(bang.getColumnCount()==0)return;
        JTextArea txtDo=new JTextArea();txtDo.setFont(bang.getFont());
        txtDo.setLineWrap(true);txtDo.setWrapStyleWord(true);
        txtDo.setBorder(BorderFactory.createEmptyBorder(7,8,7,8));
        int rong=Math.max(40,bang.getColumnModel().getColumn(0).getWidth());
        for(int dong=0;dong<bang.getRowCount();dong++) {
            txtDo.setText(String.valueOf(bang.getValueAt(dong,0)));txtDo.setSize(rong,Short.MAX_VALUE);
            int cao=Math.max(38,txtDo.getPreferredSize().height);
            if(bang.getRowHeight(dong)!=cao)bang.setRowHeight(dong,cao);
        }
    }
}
