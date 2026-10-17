package com.petstation.gui.screens;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.petstation.gui.data.RolePermissions;
import com.petstation.model.Permission;
import com.petstation.model.Role;

/** Ma trận quyền theo chức năng cho 4 vai trò (chỉ xem; quyền gán theo vai trò). */
public final class PermissionMatrixPanel extends BasePanel {
    public PermissionMatrixPanel() {
        setLayout(new BorderLayout(0, 10));
        Role[] roles = Role.values();
        String[] cols = new String[roles.length + 1];
        cols[0] = "Chức năng";
        for (int i = 0; i < roles.length; i++) cols[i + 1] = roles[i].displayName();
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Permission p : Permission.values()) {
            Object[] row = new Object[cols.length];
            row[0] = RolePermissions.label(p);
            for (int i = 0; i < roles.length; i++) row[i + 1] = RolePermissions.of(roles[i]).contains(p) ? "✔" : "";
            model.addRow(row);
        }
        JScrollPane scroll = styledTable(model);
        JTable table = tableOf(scroll);
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(null);
        javax.swing.table.DefaultTableCellRenderer center = new javax.swing.table.DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);
        add(sectionTitle("Ma trận quyền theo vai trò (4 vai trò)"), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    @Override protected String helpTitle() { return "Ma trận quyền"; }
    @Override protected String helpText() { return "Mỗi tài khoản có đúng 1 vai trò; quyền theo vai trò như bảng."; }
}
