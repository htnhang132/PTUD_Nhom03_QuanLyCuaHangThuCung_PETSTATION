package com.petstation.gui.reports;

import static com.petstation.gui.reports.ReportViews.view;

import com.petstation.gui.reports.ReportPanel.*;
import com.petstation.service.ReportingService;

import java.util.*;

public final class CustomerReportPanel extends ReportPanel {
    public CustomerReportPanel() {
        super(
                "Thống kê khách hàng",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê khách hàng".replace(' ', '_'),
                List.of(
                        view(
                                "Chi tiêu theo kỳ",
                                new String[] {"Mã KH", "Khách hàng", "Hạng", "Điểm", "Chi tiêu"},
                                1,
                                4,
                                true,
                                ReportingService::customers),
                        view(
                                "Sử dụng voucher",
                                new String[] {
                                    "Mã voucher", "Mệnh giá", "Đã dùng", "Số lượng", "Hạn dùng"
                                },
                                0,
                                2,
                                false,
                                d -> ReportingService.vouchers())));
    }
}
