package com.petstation.gui.system;

import com.petstation.gui.components.DanhMucNhieuTabPanel;

import javax.swing.JPanel;

public final class TaiKhoanPhanQuyenPanel extends DanhMucNhieuTabPanel {
    public TaiKhoanPhanQuyenPanel() {
        super(
                new String[] {"Tài khoản", "Ma trận quyền"},
                new JPanel[] {new DanhSachTaiKhoanPanel(), new MaTranQuyenPanel()});
    }
}
