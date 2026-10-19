package com.petstation.gui.search;

import com.petstation.gui.components.LichTuanPanel;

/** Cung cấp giao diện và xử lý tương tác cho cá nhân lịch làm việc vùng hiển thị. */
public final class LichCaNhanPanel extends LichTuanPanel {
    /**
     * Khởi tạo LichCaNhanPanel với các giá trị được cung cấp.
     *
     * @param maNhanVien mã nhân viên
     */
    public LichCaNhanPanel(String maNhanVien) {
        super(CheDo.CA_NHAN, maNhanVien);
    }
}
