package com.petstation.gui.search;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.navigation.MaManHinh;
import com.petstation.gui.processing.sales.NghiepVuBanHangPanel;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.service.PheDuyetService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho hóa đơn tìm kiếm vùng hiển thị. */
public final class TimKiemHoaDonPanel extends NghiepVuBanHangPanel {
    /**
     * Khởi tạo TimKiemHoaDonPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    public TimKiemHoaDonPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.INVOICES, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "Xem / In lại", this::hienThiHoaDon);
        themNut(
                pnlCacThaoTac,
                "Yêu cầu sửa / hủy",
                () -> {
                    PheDuyetService.daChonHoaDon = layNoiDungDongChon(1);
                    dieuHuong.accept(MaManHinh.INVOICE_EDIT_REQUEST);
                });
    }
}
