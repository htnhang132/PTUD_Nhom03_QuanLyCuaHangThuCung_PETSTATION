package com.petstation.gui.search;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.service.LuuTruService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho Hotel ánh xạ vùng hiển thị. */
public final class TraCuuPhongHotelPanel extends BangNghiepVuPanel {
    /**
     * Khởi tạo TraCuuPhongHotelPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    public TraCuuPhongHotelPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.ROOMS, banHang, dieuHuong);
        themNut(
                pnlCacThaoTac,
                "Dọn phòng xong",
                () -> LuuTruService.hoanTatDonPhong(layDongChon()));
    }
}
