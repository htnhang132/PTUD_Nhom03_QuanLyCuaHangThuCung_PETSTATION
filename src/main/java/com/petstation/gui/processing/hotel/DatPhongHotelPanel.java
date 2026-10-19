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

/** Cung cấp giao diện và xử lý tương tác cho hợp đồng lưu trú vùng hiển thị. */
public final class DatPhongHotelPanel extends NghiepVuHotelPanel {
    /**
     * Khởi tạo DatPhongHotelPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    public DatPhongHotelPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOTEL_BOOKING, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Đặt phòng", this::datPhong);
        themNut(
                pnlCacThaoTac,
                "Dọn phòng xong",
                () -> {
                    var giaTri =
                            com.petstation.gui.components.common.BieuMauSQL.layYeuCauNhap(
                                    this,
                                    "Hoàn thành dọn phòng",
                                    com.petstation.gui.components.common.BieuMauSQL.TruongDuLieu
                                            .layLuaChon(
                                                    "room",
                                                    "Phòng đang dọn",
                                                    LuuTruService.layPhongDangDon()));
                    if (giaTri != null)
                        LuuTruService.hoanTatDonPhong(Long.parseLong(giaTri.get("room")));
                });
        themNut(pnlCacThaoTac, "Nhận phòng", () -> LuuTruService.kiemTraVao(layDongChon()));
        themNut(pnlCacThaoTac, "Ghi chăm sóc", this::ghiNhatKy);
        themNut(pnlCacThaoTac, "Xem nhật ký", this::layNhatKyChiTiet);
        themNut(pnlCacThaoTac, "Quyết toán", () -> quyetToan("HOTEL"));
    }
}
