package com.petstation.gui.search;

import com.petstation.gui.components.LichTuanPanel;

/** Cung cấp giao diện và xử lý tương tác cho lịch làm việc tìm kiếm vùng hiển thị. */
public final class TraCuuLichLamViecPanel extends LichTuanPanel {
    /**
     * Khởi tạo TraCuuLichLamViecPanel với các giá trị được cung cấp.
     *
     * @param maNhanVien mã nhân viên
     */
    public TraCuuLichLamViecPanel(String maNhanVien) {
        super(CheDo.HIEN_THI_TAT_CA, maNhanVien);
    }
}
