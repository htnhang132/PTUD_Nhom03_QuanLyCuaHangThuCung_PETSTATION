package com.petstation.gui.system;

import com.petstation.gui.components.TabbedCatalogPanel;

import javax.swing.JPanel;

public final class AccountPermissionPanel extends TabbedCatalogPanel {
    public AccountPermissionPanel() {
        super(
                new String[] {"Tài khoản", "Ma trận quyền"},
                new JPanel[] {new AccountListPanel(), new PermissionMatrixPanel()});
    }
}
