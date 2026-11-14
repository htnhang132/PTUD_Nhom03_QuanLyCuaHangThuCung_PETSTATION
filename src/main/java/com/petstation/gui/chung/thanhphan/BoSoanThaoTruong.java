package com.petstation.gui.chung.thanhphan;

import javax.swing.JComponent;

public interface BoSoanThaoTruong {
    JComponent layThanhPhan();

    String lay();

    void dat(String giaTri);

    void datDuocBat(boolean duocBat);
}
