package com.petstation.gui.components;

public interface ToolbarAware {
    default ToolbarCapabilities toolbarCapabilities() {
        return ToolbarCapabilities.none();
    }

    default void onNew() {}

    default void onSave() {}

    default void onFind() {}

    default void onPrint() {}

    default void onUndo() {}

    default void onHelp() {}

    /** Gọi mỗi lần màn hình được hiển thị (kể cả khi lấy từ cache) để làm mới dữ liệu. */
    default void onShow() {}
}
