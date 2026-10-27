package com.petstation.gui.catalog;

import com.petstation.gui.components.DanhMucNhieuTabPanel;

import javax.swing.JPanel;

/** Cung cấp giao diện và xử lý tương tác cho sản phẩm danh mục vùng hiển thị. */
public final class DanhMucSanPhamPanel extends DanhMucNhieuTabPanel {
    public DanhMucSanPhamPanel() {
        super(
                new String[] {"Danh sách sản phẩm", "Lịch sử giá"},
                new JPanel[] {new DanhSachSanPhamPanel(), new LichSuGiaPanel()});
    }
}
