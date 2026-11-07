package com.petstation.gui.catalog;

import com.petstation.gui.components.CoSoPanel;

import java.awt.BorderLayout;

public final class DanhMucSanPhamPanel extends CoSoPanel {
    private final DanhSachSanPhamPanel pnlDanhSach = new DanhSachSanPhamPanel();

    public DanhMucSanPhamPanel() {
        setLayout(new BorderLayout());
        setBorder(null);
        add(pnlDanhSach, BorderLayout.CENTER);
    }
}
