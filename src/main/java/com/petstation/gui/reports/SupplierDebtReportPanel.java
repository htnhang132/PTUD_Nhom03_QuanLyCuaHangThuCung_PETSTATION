package com.petstation.gui.reports;

import static com.petstation.gui.reports.ReportViews.view;

import com.petstation.gui.reports.ReportPanel.*;
import com.petstation.service.ReportingService;

import java.util.*;

public final class SupplierDebtReportPanel extends ReportPanel {
    public SupplierDebtReportPanel() {
        super(
                "Công nợ nhà cung cấp",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Công nợ nhà cung cấp".replace(' ', '_'),
                List.of(
                        view(
                                "Công nợ theo kỳ nhập",
                                new String[] {
                                    "Mã NCC", "Nhà cung cấp", "Tổng nhập", "Đã trả", "Còn nợ"
                                },
                                1,
                                4,
                                true,
                                ReportingService::supplierDebt)));
    }
}
