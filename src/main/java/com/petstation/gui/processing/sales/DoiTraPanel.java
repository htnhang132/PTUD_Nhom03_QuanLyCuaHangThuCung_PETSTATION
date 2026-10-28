package com.petstation.gui.processing.sales;

import static com.petstation.util.DuLieu.*;

import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho đổi trả vùng hiển thị. */
public final class DoiTraPanel extends NghiepVuBanHangPanel {
    public DoiTraPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.RETURNS, banHang, dieuHuong);
        themNut(pnlCacThaoTac, "+ Đổi phụ kiện", this::traHang);
    }
}
