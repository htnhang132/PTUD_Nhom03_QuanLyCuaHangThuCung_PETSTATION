package com.petstation.gui.system;

import com.petstation.gui.components.DanhMucNhieuTabPanel;

import javax.swing.JPanel;

/** Cung cấp giao diện và xử lý tương tác cho tài khoản quyền vùng hiển thị. */
public final class TaiKhoanPhanQuyenPanel extends DanhMucNhieuTabPanel {
    /** Khởi tạo TaiKhoanPhanQuyenPanel với các giá trị được cung cấp. */
    public TaiKhoanPhanQuyenPanel() {
        super(
                new String[] {"Tài khoản", "Ma trận quyền"},
                new JPanel[] {new DanhSachTaiKhoanPanel(), new MaTranQuyenPanel()});
    }
}
