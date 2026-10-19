package com.petstation.gui.processing.sales;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.service.BanHangService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Cung cấp giao diện và xử lý tương tác cho đang chờ các hóa đơn vùng hiển thị. */
public final class DonHangTreoPanel extends BangNghiepVuPanel {
    /**
     * Khởi tạo DonHangTreoPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param dieuHuong điều hướng
     */
    public DonHangTreoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PENDING, banHang, dieuHuong);

        themNut(
                pnlCacThaoTac,
                "Mở lại đơn",
                () -> {
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (!banHang.kiemTraTrong())
                        throw new IllegalArgumentException(
                                "Hãy treo hoặc thanh toán giỏ hiện tại trước.");
                    BanHangService.moLai(layDongChon(), banHang);
                    dieuHuong.accept(MaManHinh.POS);
                });
        themNut(
                pnlCacThaoTac,
                "Xóa đơn treo",
                () -> {
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (HopThoai.xacNhan(this, "Xóa đơn treo đã chọn?"))
                        BanHangService.xoaTreo(layDongChon());
                });
    }
}
