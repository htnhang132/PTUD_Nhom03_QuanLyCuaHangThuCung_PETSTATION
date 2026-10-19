package com.petstation.gui.reports;

import static com.petstation.gui.reports.ReportViews.view;

import com.petstation.gui.reports.ReportPanel.*;
import com.petstation.service.ReportingService;

import java.util.*;

public final class RevenueReportPanel extends ReportPanel {
    public RevenueReportPanel() {
        super(
                "Thống kê doanh thu",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê doanh thu".replace(' ', '_'),
                List.of(
                        view(
                                "Theo ngày",
                                new String[] {"Ngày", "Doanh thu sau hoàn trả"},
                                0,
                                1,
                                true,
                                ReportingService::dailyRevenue),
                        view(
                                "Theo thanh toán",
                                new String[] {"Phương thức", "Doanh thu", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                d -> ReportingService.revenue(d, "method")),
                        view(
                                "Theo thu ngân",
                                new String[] {"Nhân viên", "Doanh thu", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                d -> ReportingService.revenue(d, "staff")),
                        view(
                                "Theo nguồn",
                                new String[] {"Nguồn", "Doanh thu", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                d -> ReportingService.revenue(d, "source")),
                        view(
                                "Theo ca",
                                new String[] {"Ca", "Doanh thu", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                d -> ReportingService.revenue(d, "shift"))));
    }
}
