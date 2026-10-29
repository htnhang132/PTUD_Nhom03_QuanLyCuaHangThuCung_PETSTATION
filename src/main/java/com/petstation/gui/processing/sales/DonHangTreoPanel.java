package com.petstation.gui.processing.sales;

import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.BanHangService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho đang chờ các hóa đơn vùng hiển thị. */
public final class DonHangTreoPanel extends BangNghiepVuPanel {
    public DonHangTreoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.GIO_HANG_TREO, banHang, dieuHuong);

        themNut(
                pnlCacThaoTac,
                "Mở lại đơn",
                () -> {
                    if (!banHang.kiemTraTrong())
                        throw new IllegalArgumentException(
                                "Hãy treo hoặc thanh toán giỏ hiện tại trước.");
                    BanHangService.moLai(layDongChon(), banHang);
                    dieuHuong.accept(MaManHinh.LAP_HOA_DON);
                });
        themNut(
                pnlCacThaoTac,
                "Xóa đơn treo",
                () -> {
                    if (HopThoai.xacNhan(this, "Xóa đơn treo đã chọn?"))
                        BanHangService.xoaTreo(layDongChon());
                });
    }
}
