package com.petstation.gui.search;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho lô hàng tồn kho tìm kiếm vùng hiển thị. */
public final class TimKiemTonTheoLoPanel extends BangNghiepVuPanel {
    /**
     * Khởi tạo TimKiemTonTheoLoPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    public TimKiemTonTheoLoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.BATCHES, banHang, dieuHuong);
    }
}
