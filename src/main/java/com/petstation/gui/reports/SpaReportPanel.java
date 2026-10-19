package com.petstation.gui.reports;

import static com.petstation.gui.reports.ReportViews.view;

import com.petstation.gui.reports.ReportPanel.*;
import com.petstation.service.ReportingService;

import java.util.*;

public final class SpaReportPanel extends ReportPanel {
    public SpaReportPanel() {
        super(
                "Thống kê dịch vụ Spa",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê dịch vụ Spa".replace(' ', '_'),
                List.of(
                        view(
                                "Dịch vụ đã thanh toán",
                                new String[] {"Mã DV", "Dịch vụ", "Số lượt", "Giá trị dịch vụ"},
                                1,
                                3,
                                true,
                                ReportingService::spa),
                        view(
                                "Trạng thái lịch hẹn",
                                new String[] {"Trạng thái", "Số lịch"},
                                0,
                                1,
                                false,
                                ReportingService::appointments)));
    }
}
