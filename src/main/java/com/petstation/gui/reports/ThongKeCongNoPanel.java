package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.taoKieuHienThi;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho nhà cung cấp công nợ báo cáo vùng hiển thị. */
public final class ThongKeCongNoPanel extends BaoCaoPanel {
    /** Khởi tạo ThongKeCongNoPanel với các giá trị được cung cấp. */
    public ThongKeCongNoPanel() {
        super(
                "Công nợ nhà cung cấp",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Công nợ nhà cung cấp".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Công nợ theo kỳ nhập",
                                new String[] {
                                    "Mã NCC", "Nhà cung cấp", "Tổng nhập", "Đã trả", "Còn nợ"
                                },
                                1,
                                4,
                                true,
                                ThongKeService::layCongNoNhaCungCap)));
    }
}
