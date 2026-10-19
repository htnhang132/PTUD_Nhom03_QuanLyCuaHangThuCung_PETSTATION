package com.petstation.gui.components;

/** Cung cấp giao diện và xử lý tương tác cho thanh công cụ hỗ trợ. */
public interface HoTroThanhCongCu {
    /**
     * Lấy khả năng thanh công cụ trong HoTroThanhCongCu.
     *
     * @return kết quả lấy khả năng thanh công cụ
     */
    default KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return KhaNangThanhCongCu.taoRong();
    }

    /** Xử lý tạo mới trong HoTroThanhCongCu. */
    default void xuLyTaoMoi() {}

    /** Xử lý lưu trong HoTroThanhCongCu. */
    default void xuLyLuu() {}

    /** Xử lý tìm kiếm trong HoTroThanhCongCu. */
    default void xuLyTimKiem() {}

    /** Xử lý in trong HoTroThanhCongCu. */
    default void xuLyIn() {}

    /** Xử lý hoàn tác trong HoTroThanhCongCu. */
    default void xuLyHoanTac() {}

    /** Xử lý trợ giúp trong HoTroThanhCongCu. */
    default void xuLyTroGiup() {}

    /**
     * Xử lý hiển thị trong HoTroThanhCongCu.
     *
     * <p>Gọi mỗi lần màn hình được hiển thị (kể cả khi lấy từ cache) để làm mới dữ liệu.
     */
    default void xuLyHienThi() {}
}
