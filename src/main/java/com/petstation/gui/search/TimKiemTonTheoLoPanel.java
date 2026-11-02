package com.petstation.gui.search;

import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho lô hàng tồn kho tìm kiếm vùng hiển thị. */
public final class TimKiemTonTheoLoPanel extends BangNghiepVuPanel {
    public TimKiemTonTheoLoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.LO_HANG, banHang, dieuHuong);
    }
}
