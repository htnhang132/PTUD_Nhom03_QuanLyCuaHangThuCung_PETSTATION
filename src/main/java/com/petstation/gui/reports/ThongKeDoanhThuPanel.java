package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.*;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho doanh thu báo cáo vùng hiển thị. */
public final class ThongKeDoanhThuPanel extends BaoCaoPanel {
    public ThongKeDoanhThuPanel() {
        super(
                "Thống kê doanh thu",
                "Tiền thu trong kỳ gồm thanh toán và cọc lưu trú; doanh thu hoàn tất xem riêng.",
                "Thống kê doanh thu".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Doanh thu hoàn tất",
                                new String[] {
                                    "Ngày", "Doanh thu hoàn tất (gồm cọc đã cấn trừ)", "Số hóa đơn"
                                },
                                0,
                                1,
                                true,
                                ThongKeService::layDoanhThuHoanTat),
                        taoKieuHienThi(
                                "Theo ngày",
                                new String[] {"Ngày", "Tiền thu ròng (gồm cọc)"},
                                0,
                                1,
                                true,
                                ThongKeService::layDoanhThuTheoNgay),
                        taoKieuHienThi(
                                "Theo thanh toán",
                                new String[] {"Phương thức", "Tiền thu ròng", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                ngayDangXet -> ThongKeService.layDoanhThu(ngayDangXet, "method")),
                        taoKieuHienThi(
                                "Theo thu ngân",
                                new String[] {"Nhân viên", "Tiền thu ròng", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                ngayDangXet -> ThongKeService.layDoanhThu(ngayDangXet, "staff")),
                        taoKieuHienThi(
                                "Theo nguồn",
                                new String[] {"Nguồn", "Tiền thu ròng", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                ngayDangXet -> ThongKeService.layDoanhThu(ngayDangXet, "source")),
                        taoKieuHienThi(
                                "Theo ca",
                                new String[] {"Ca", "Tiền thu ròng", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                ngayDangXet -> ThongKeService.layDoanhThu(ngayDangXet, "shift"))));
    }
}
