package com.petstation.gui.components;

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

    default void xuLyHienThi() {}
}
