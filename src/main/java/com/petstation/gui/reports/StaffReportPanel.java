package com.petstation.gui.reports;

import static com.petstation.gui.reports.ReportViews.view;

import com.petstation.gui.reports.ReportPanel.*;
import com.petstation.service.ReportingService;

import java.util.*;

public final class StaffReportPanel extends ReportPanel {
    public StaffReportPanel() {
        super(
                "Thống kê nhân viên",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê nhân viên".replace(' ', '_'),
                List.of(
                        view(
                                "Lịch làm việc",
                                new String[] {"Mã NV", "Nhân viên", "Chức vụ", "Số ca", "Số giờ"},
                                1,
                                4,
                                false,
                                ReportingService::staff)));
    }
}
