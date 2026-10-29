package com.petstation.gui.catalog;

import com.petstation.gui.components.CoSoPanel;

import java.awt.BorderLayout;

/** Cung cấp giao diện cho danh mục sản phẩm (hiển thị trực tiếp danh sách sản phẩm). */
public final class DanhMucSanPhamPanel extends CoSoPanel {
    private final DanhSachSanPhamPanel pnlDanhSach = new DanhSachSanPhamPanel();

    public DanhMucSanPhamPanel() {
        setLayout(new BorderLayout());
        add(pnlDanhSach, BorderLayout.CENTER);
    }
}
