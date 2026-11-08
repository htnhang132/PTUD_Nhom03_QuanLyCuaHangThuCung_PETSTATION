package com.petstation.gui.catalog;

import com.petstation.gui.components.DanhMucNhieuTabPanel;

import javax.swing.JPanel;

// Màn hình Danh mục > Điểm thưởng và voucher
public final class TichDiemVoucherPanel extends DanhMucNhieuTabPanel {
    public TichDiemVoucherPanel() {
        super(
                new String[] {"Quy tắc điểm & hạng", "Voucher"},
                new JPanel[] {new QuyTacTichDiemPanel(), new DanhMucVoucherPanel()});
    }
}
