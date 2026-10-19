package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.*;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho nhà cung cấp công nợ báo cáo vùng hiển thị. */
public final class ThongKeCongNoPanel extends BaoCaoPanel {
    /** Khởi tạo ThongKeCongNoPanel với các giá trị được cung cấp. */
    public ThongKeCongNoPanel() {
        super(
                "Công nợ nhà cung cấp",
                "Kỳ lọc ngày nhập chứng từ; số đã trả và dư nợ là số cập nhật hiện tại.",
                "Công nợ nhà cung cấp".replace(' ', '_'),
                List.of(
                        taoKieuHienTai(
                                "Dư nợ hiện tại",
                                new String[] {
                                    "Mã NCC",
                                    "Nhà cung cấp",
                                    "Tổng nhập",
                                    "Đã trả hiện tại",
                                    "Dư nợ hiện tại"
                                },
                                1,
                                4,
                                true,
                                ThongKeService::layDuNoHienTai),
                        taoKieuHienThi(
                                "Công nợ theo kỳ nhập",
                                new String[] {
                                    "Mã NCC",
                                    "Nhà cung cấp",
                                    "Tổng nhập trong kỳ",
                                    "Đã trả hiện tại",
                                    "Dư nợ chứng từ hiện tại"
                                },
                                1,
                                4,
                                true,
                                ThongKeService::layCongNoNhaCungCap)));
    }
}
