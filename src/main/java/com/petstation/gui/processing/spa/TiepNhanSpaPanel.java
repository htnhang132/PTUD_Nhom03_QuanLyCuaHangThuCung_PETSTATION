package com.petstation.gui.processing.spa;

import static com.petstation.util.DuLieu.*;

import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.SpaService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho Spa tiếp nhận vùng hiển thị. */
public final class TiepNhanSpaPanel extends NghiepVuSpaPanel {
    public TiepNhanSpaPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.TIEP_NHAN_SPA, banHang, dieuHuong);

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
