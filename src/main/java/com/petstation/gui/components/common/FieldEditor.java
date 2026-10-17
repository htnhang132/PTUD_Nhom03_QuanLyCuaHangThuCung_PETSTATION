package com.petstation.gui.components.common;

import javax.swing.JComponent;

/** Ô nhập tùy biến cho {@link com.petstation.gui.screens.EntityPanel} (ví dụ bảng giá Spa). */
public interface FieldEditor {
    JComponent component();
    String get();
    void set(String value);
    void setEnabled(boolean enabled);
}
