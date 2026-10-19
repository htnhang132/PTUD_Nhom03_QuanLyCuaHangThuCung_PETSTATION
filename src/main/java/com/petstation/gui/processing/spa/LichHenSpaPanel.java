package com.petstation.gui.processing.spa;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho Spa lịch làm việc vùng hiển thị. */
public final class LichHenSpaPanel extends NghiepVuSpaPanel {
    /**
     * Khởi tạo LichHenSpaPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    public LichHenSpaPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.APPOINTMENTS, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Đặt lịch hẹn", this::datLichHen);
        themNut(pnlCacThaoTac, "Tiếp nhận", this::tiepNhan);
        themNut(pnlCacThaoTac, "Hủy lịch", this::huyLichHen);
    }
}
