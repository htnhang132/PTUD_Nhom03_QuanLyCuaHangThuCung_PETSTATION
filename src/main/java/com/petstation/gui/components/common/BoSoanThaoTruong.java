package com.petstation.gui.components.common;

import javax.swing.JComponent;

/**
 * Cung cấp giao diện và xử lý tương tác cho trường bộ soạn thảo.
 *
 * <p>Ô nhập tùy biến cho {@link com.petstation.gui.components.QuanLyDanhMucPanel} (ví dụ bảng giá
 * Spa).
 */
public interface BoSoanThaoTruong {
    /**
     * Lấy thành phần trong BoSoanThaoTruong.
     *
     * @return kết quả lấy thành phần
     */
    JComponent layThanhPhan();

    /**
     * Lấy trong BoSoanThaoTruong.
     *
     * @return kết quả lấy
     */
    String lay();

    /**
     * Đặt trong BoSoanThaoTruong.
     *
     * @param giaTri giá trị
     */
    void dat(String giaTri);

    /**
     * Đặt được bật trong BoSoanThaoTruong.
     *
     * @param duocBat được bật
     */
    void datDuocBat(boolean duocBat);
}
