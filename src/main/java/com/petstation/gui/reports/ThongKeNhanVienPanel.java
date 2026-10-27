package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.*;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho nhân viên báo cáo vùng hiển thị. */
public final class ThongKeNhanVienPanel extends BaoCaoPanel {
    public ThongKeNhanVienPanel() {
        super(
                "Thống kê nhân viên",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê nhân viên".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Thu tiền và chênh lệch ca",
                                new String[] {
                                    "Nhân viên",
                                    "Ca",
                                    "Mở lúc",
                                    "Đóng lúc",
                                    "Tiền đầu ca",
                                    "Thu hóa đơn",
                                    "Cọc lưu trú",
                                    "Thực đếm",
                                    "Chênh lệch"
                                },
                                0,
                                8,
                                true,
                                ThongKeService::layTongHopCa),
                        taoKieuHienThi(
                                "Lịch làm việc",
                                new String[] {"Mã NV", "Nhân viên", "Chức vụ", "Số ca", "Số giờ"},
                                1,
                                4,
                                false,
                                ThongKeService::layNhanVien)));
    }
}
