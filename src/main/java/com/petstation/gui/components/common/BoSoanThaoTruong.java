package com.petstation.gui.components.common;

import javax.swing.JComponent;

/**
 * Cung cấp giao diện và xử lý tương tác cho trường bộ soạn thảo.
 *
 * <p>Ô nhập tùy biến cho {@link com.petstation.gui.components.QuanLyDanhMucPanel} (ví dụ bảng giá
 * Spa).
 */
public interface BoSoanThaoTruong {
    JComponent layThanhPhan();

    String lay();

    void dat(String giaTri);

    void datDuocBat(boolean duocBat);
}
