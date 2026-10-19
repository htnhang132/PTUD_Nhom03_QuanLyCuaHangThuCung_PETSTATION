package com.petstation.gui.processing.inventory;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho kiểm kê vùng hiển thị. */
public final class KiemKePanel extends NghiepVuKhoPanel {
    /**
     * Khởi tạo KiemKePanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    public KiemKePanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.STOCKTAKE, banHang, dieuHuong);
        themNut(pnlCacThaoTac, "+ Kiểm kê lô", this::kiemKe);
    }
}
