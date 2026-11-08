package com.petstation.gui.processing.sales;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;

import java.util.function.Consumer;

public abstract class NghiepVuBanHangPanel extends BangNghiepVuPanel {
    protected NghiepVuBanHangPanel(
            LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
    }
}
