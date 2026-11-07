package com.petstation.gui.components.common;

import javax.swing.JComponent;

public interface BoSoanThaoTruong {
    JComponent layThanhPhan();

    String lay();

    void dat(String giaTri);

    void datDuocBat(boolean duocBat);
}
