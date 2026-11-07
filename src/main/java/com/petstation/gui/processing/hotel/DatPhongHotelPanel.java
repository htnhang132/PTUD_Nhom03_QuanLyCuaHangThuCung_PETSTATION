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

public final class DatPhongHotelPanel extends NghiepVuHotelPanel {
    public DatPhongHotelPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.DAT_PHONG_LUU_TRU, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Đặt phòng", this::datPhong);
        themNut(pnlCacThaoTac, "Sửa đặt phòng", this::suaDatPhong);
        themNut(pnlCacThaoTac, "Hủy đặt phòng", this::huyDatPhong);
        themNut(pnlCacThaoTac, "Dọn phòng xong", () -> moDonPhong(0));
        themNut(pnlCacThaoTac, "Nhận phòng", () -> LuuTruService.kiemTraVao(layDongChon()));
        themNut(pnlCacThaoTac, "Xem nhật ký", this::layNhatKyChiTiet);
        themNut(pnlCacThaoTac, "Quyết toán", () -> quyetToan("HOTEL"));
    }

    public void moDonPhong(long id) {
        var field =
                com.petstation.gui.components.common.BieuMauSQL.TruongDuLieu.layLuaChon(
                        "room", "Phòng đang dọn", LuuTruService.layPhongDangDon());
        if (id > 0)
            field =
                    new com.petstation.gui.components.common.BieuMauSQL.TruongDuLieu(
                            field.khoa(), field.nhan(), String.valueOf(id), field.cacLuaChon());
        var values =
                com.petstation.gui.components.common.BieuMauSQL.layYeuCauNhap(
                        this, "Hoàn thành dọn phòng", field);
        if (values != null) {
            LuuTruService.hoanTatDonPhong(Long.parseLong(values.get("room")));
            taiLaiNen();
        }
    }
}
