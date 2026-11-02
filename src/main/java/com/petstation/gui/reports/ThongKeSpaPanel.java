package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.*;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho Spa báo cáo vùng hiển thị. */
public final class ThongKeSpaPanel extends BaoCaoPanel {
    public ThongKeSpaPanel() {
        super(
                "Thống kê dịch vụ Spa",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê dịch vụ Spa".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Phiếu theo trạng thái",
                                new String[] {"Trạng thái", "Số phiếu"},
                                0,
                                1,
                                false,
                                ThongKeService::layPhieuSpaTheoTrangThai),
                        taoKieuHienThi(
                                "Dịch vụ đã thanh toán",
                                new String[] {"Mã DV", "Dịch vụ", "Số lượt", "Giá trị dịch vụ"},
                                1,
                                3,
                                true,
                                ThongKeService::layDuLieuSpa),
                        taoKieuHienThi(
                                "Trạng thái lịch hẹn",
                                new String[] {"Trạng thái", "Số lịch"},
                                0,
                                1,
                                false,
                                ThongKeService::layLichHen)));
    }
}
