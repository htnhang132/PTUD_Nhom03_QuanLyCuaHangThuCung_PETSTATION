package com.petstation.gui.reports;

import static com.petstation.gui.reports.ReportViews.view;

import com.petstation.gui.reports.ReportPanel.*;
import com.petstation.service.ReportingService;

import java.util.*;

public final class GoodsReportPanel extends ReportPanel {
    public GoodsReportPanel() {
        super(
                "Thống kê hàng hóa",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê hàng hóa".replace(' ', '_'),
                List.of(
                        view(
                                "Sản phẩm bán chạy",
                                new String[] {"Mã SP", "Sản phẩm", "Số bán sau trả", "Doanh thu"},
                                1,
                                3,
                                true,
                                ReportingService::topProducts),
                        view(
                                "Tồn kho hiện tại",
                                new String[] {
                                    "Mã SP",
                                    "Sản phẩm",
                                    "Tồn kho",
                                    "Tồn tối thiểu",
                                    "Giá nhập gần nhất"
                                },
                                1,
                                2,
                                false,
                                d -> ReportingService.inventory()),
                        view(
                                "Sắp hết hạn",
                                new String[] {"Mã SP", "Sản phẩm", "Số lô", "Hạn dùng", "Tồn"},
                                1,
                                4,
                                false,
                                d -> ReportingService.expiry())));
    }
}
