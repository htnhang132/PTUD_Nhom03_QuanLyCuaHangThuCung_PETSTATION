package com.petstation.gui.processing.spa;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.service.SpaService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho Spa tiến độ vùng hiển thị. */
public final class TienDoSpaPanel extends NghiepVuSpaPanel {
    /**
     * Khởi tạo TienDoSpaPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    public TienDoSpaPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.SPA_PROGRESS, banHang, dieuHuong);

        themNut(pnlCacThaoTac, "+ Tiếp nhận", this::tiepNhan);
        themNut(pnlCacThaoTac, "Thêm dịch vụ", this::themDichVu);
        themNut(
                pnlCacThaoTac,
                "Bắt đầu làm",
                () -> SpaService.layTrangThaiSpa(layDongChon(), "IN_PROGRESS"));
        themNut(
                pnlCacThaoTac,
                "Hoàn thành",
                () -> SpaService.layTrangThaiSpa(layDongChon(), "COMPLETED"));
        themNut(pnlCacThaoTac, "Thanh toán", () -> quyetToan("SPA"));
        themNut(pnlCacThaoTac, "Chi tiết dịch vụ", this::laySpaChiTiet);
    }
}
