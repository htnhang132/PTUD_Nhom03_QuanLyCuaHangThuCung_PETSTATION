package com.petstation.gui.processing.hotel;

import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.NutDinhDang;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.LuuTruService;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class LuuTruHotelPanel extends NghiepVuHotelPanel {
    public LuuTruHotelPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.HOP_DONG_LUU_TRU, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Đặt phòng (F6)", NutDinhDang.KieuNut.CHINH, this::datPhong);
        themNut(pnlCacThaoTac, "Nhận phòng", NutDinhDang.KieuNut.THANH_CONG, () -> LuuTruService.kiemTraVao(layDongChon()));
        themNut(pnlCacThaoTac, "Quyết toán", NutDinhDang.KieuNut.THANH_CONG, () -> quyetToan("HOTEL"));
        themNut(pnlCacThaoTac, "Sửa đặt phòng", NutDinhDang.KieuNut.PHU, this::suaDatPhong);
        themNut(pnlCacThaoTac, "Xem nhật ký", NutDinhDang.KieuNut.PHU, this::layNhatKyChiTiet);
        themNut(pnlCacThaoTac, "Hủy đặt phòng", NutDinhDang.KieuNut.NGUY_HIEM, this::huyDatPhong);

        ganPhimTat(KeyEvent.VK_F6, this::datPhong);

        ganSuKienDupChuot(() -> {
            int row = tblBang.getSelectedRow();
            if (row < 0) return;
            int dong = tblBang.convertRowIndexToModel(row);
            String trangThai = String.valueOf(moHinh.getValueAt(dong, 9));
            if ("DA_DAT".equals(trangThai)) {
                if (cocConThieu() <= 0) {
                    LuuTruService.kiemTraVao(layDongChon());
                } else {
                    thuCoc();
                }
            } else if ("DANG_O".equals(trangThai)) {
                layNhatKyChiTiet();
            }
        });

        ganMenuChuotPhai(row -> {
            int dong = tblBang.convertRowIndexToModel(row);
            String trangThai = String.valueOf(moHinh.getValueAt(dong, 9));
            JPopupMenu popup = new JPopupMenu();
            if ("DA_DAT".equals(trangThai)) {
                JMenuItem itemNhanPhong = new JMenuItem("Nhận phòng (Check-in)");
                itemNhanPhong.addActionListener(e -> LuuTruService.kiemTraVao(layDongChon()));
                popup.add(itemNhanPhong);
                if (cocConThieu() > 0) {
                    JMenuItem itemThuCoc = new JMenuItem("Thu cọc phòng");
                    itemThuCoc.addActionListener(e -> thuCoc());
                    popup.add(itemThuCoc);
                }
                JMenuItem itemSua = new JMenuItem("Sửa thông tin đặt");
                itemSua.addActionListener(e -> suaDatPhong());
                popup.add(itemSua);
                popup.addSeparator();
                JMenuItem itemHuy = new JMenuItem("Hủy đặt phòng");
                itemHuy.addActionListener(e -> huyDatPhong());
                popup.add(itemHuy);
            } else if ("DANG_O".equals(trangThai)) {
                JMenuItem itemNhatKy = new JMenuItem("Xem nhật ký chăm sóc");
                itemNhatKy.addActionListener(e -> layNhatKyChiTiet());
                popup.add(itemNhatKy);
                JMenuItem itemPhieuCS = new JMenuItem("Nhập nhật ký hôm nay");
                itemPhieuCS.addActionListener(e -> com.petstation.gui.components.common.NhatKyChamSocDialog.mo(this, "HOTEL", layDongChon()));
                popup.add(itemPhieuCS);
                popup.addSeparator();
                JMenuItem itemTraPhong = new JMenuItem("Trả phòng & Quyết toán (POS)");
                itemTraPhong.addActionListener(e -> quyetToan("HOTEL"));
                popup.add(itemTraPhong);
            }
            return popup;
        });
    }
}
