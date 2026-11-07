package com.petstation.gui.navigation;

import com.petstation.model.MaManHinh;
import com.petstation.model.Quyen;

public record DinhNghiaManHinh(
        MaManHinh id, String tieuDe, String goiY, Quyen quyen, boolean daTrienKhai) {}
