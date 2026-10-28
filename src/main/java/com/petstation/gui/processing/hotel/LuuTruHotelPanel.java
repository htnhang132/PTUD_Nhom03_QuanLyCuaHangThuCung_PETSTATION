package com.petstation.gui.processing.hotel;

import static com.petstation.util.DuLieu.*;

import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.LuuTruService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho Hotel vùng hiển thị. */
public final class LuuTruHotelPanel extends NghiepVuHotelPanel {
    public LuuTruHotelPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOTEL_STAY, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Đặt phòng", this::datPhong);
        themNut(pnlCacThaoTac, "Nhận phòng", () -> LuuTruService.kiemTraVao(layDongChon()));
        themNut(pnlCacThaoTac, "Ghi chăm sóc", this::ghiNhatKy);
        themNut(pnlCacThaoTac, "Xem nhật ký", this::layNhatKyChiTiet);
        themNut(pnlCacThaoTac, "Quyết toán", () -> quyetToan("HOTEL"));
    }
}
