package com.petstation.gui.components;

/** Cung cấp giao diện và xử lý tương tác cho thanh công cụ hỗ trợ. */
public interface HoTroThanhCongCu {
    default KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return KhaNangThanhCongCu.taoRong();
    }

    default void xuLyTaoMoi() {}

    default void xuLyLuu() {}

    default void xuLyTimKiem() {}

    default void xuLyIn() {}

    default void xuLyHoanTac() {}

    default void xuLyTroGiup() {}

    /**
     * Xử lý hiển thị trong HoTroThanhCongCu.
     *
     * <p>Gọi mỗi lần màn hình được hiển thị (kể cả khi lấy từ cache) để làm mới dữ liệu.
     */
    default void xuLyHienThi() {}
}
