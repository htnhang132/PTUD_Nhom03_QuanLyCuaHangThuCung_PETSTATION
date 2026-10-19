package com.petstation.gui.processing.staff;

import com.petstation.gui.components.LichTuanPanel;

/** Cung cấp giao diện và xử lý tương tác cho nhân viên lịch làm việc vùng hiển thị. */
public final class PhanLichNhanVienPanel extends LichTuanPanel {
    /**
     * Khởi tạo PhanLichNhanVienPanel với các giá trị được cung cấp.
     *
     * @param maNhanVien mã nhân viên
     */
    public PhanLichNhanVienPanel(String maNhanVien) {
        super(CheDo.SUA, maNhanVien);
    }
}
