package com.petstation.gui.processing.hotel;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.service.LuuTruService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho Hotel nhật ký vùng hiển thị. */
public final class NhatKyLuuTruPanel extends NghiepVuHotelPanel {
    /**
     * Khởi tạo NhatKyLuuTruPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    public NhatKyLuuTruPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOTEL_JOURNAL, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Đặt phòng", this::datPhong);
        themNut(pnlCacThaoTac, "Nhận phòng", () -> LuuTruService.kiemTraVao(layDongChon()));
        themNut(pnlCacThaoTac, "Ghi chăm sóc", this::ghiNhatKy);
        themNut(pnlCacThaoTac, "Xem nhật ký", this::layNhatKyChiTiet);
        themNut(pnlCacThaoTac, "Quyết toán", () -> quyetToan("HOTEL"));
    }
}
