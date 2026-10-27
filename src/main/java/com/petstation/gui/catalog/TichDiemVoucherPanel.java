package com.petstation.gui.catalog;

import com.petstation.gui.components.DanhMucNhieuTabPanel;

import javax.swing.JPanel;

/** Cung cấp giao diện và xử lý tương tác cho tích lũy voucher vùng hiển thị. */
public final class TichDiemVoucherPanel extends DanhMucNhieuTabPanel {
    public TichDiemVoucherPanel() {
        super(
                new String[] {"Quy tắc điểm & hạng", "Voucher"},
                new JPanel[] {new QuyTacTichDiemPanel(), new DanhMucVoucherPanel()});
    }
}
