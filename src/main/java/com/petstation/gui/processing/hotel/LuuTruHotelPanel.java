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

public final class LuuTruHotelPanel extends NghiepVuHotelPanel {
    public LuuTruHotelPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOP_DONG_LUU_TRU, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Đặt phòng", this::datPhong);
        themNut(pnlCacThaoTac, "Sửa đặt phòng", this::suaDatPhong);
        themNut(pnlCacThaoTac, "Hủy đặt phòng", this::huyDatPhong);
        themNut(pnlCacThaoTac, "Nhận phòng", () -> LuuTruService.kiemTraVao(layDongChon()));
        themNut(pnlCacThaoTac, "Xem nhật ký", this::layNhatKyChiTiet);
        themNut(pnlCacThaoTac, "Quyết toán", () -> quyetToan("HOTEL"));
    }
}
