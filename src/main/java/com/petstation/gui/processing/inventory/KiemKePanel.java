package com.petstation.gui.processing.inventory;

import static com.petstation.util.DuLieu.*;

import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho kiểm kê vùng hiển thị. */
public final class KiemKePanel extends NghiepVuKhoPanel {
    public KiemKePanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.STOCKTAKE, banHang, dieuHuong);
        themNut(pnlCacThaoTac, "+ Kiểm kê lô", this::kiemKe);
    }
}
