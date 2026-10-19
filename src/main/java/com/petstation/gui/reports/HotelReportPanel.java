package com.petstation.gui.reports;

import static com.petstation.gui.reports.ReportViews.view;

import com.petstation.gui.reports.ReportPanel.*;
import com.petstation.service.ReportingService;

import java.util.*;

public final class HotelReportPanel extends ReportPanel {
    public HotelReportPanel() {
        super(
                "Thống kê Pet Hotel",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê Pet Hotel".replace(' ', '_'),
                List.of(
                        view(
                                "Trạng thái phòng hiện tại",
                                new String[] {"Trạng thái", "Số phòng"},
                                0,
                                1,
                                false,
                                d -> ReportingService.roomStatus()),
                        view(
                                "Lưu trú đã quyết toán",
                                new String[] {
                                    "Loại phòng", "Lượt lưu trú", "Số ngày", "Tiền thu gồm cọc"
                                },
                                0,
                                3,
                                true,
                                ReportingService::hotel)));
    }
}
