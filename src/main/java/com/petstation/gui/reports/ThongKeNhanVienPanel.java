package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.*;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

// Màn hình Thống kê > Nhân viên
public final class ThongKeNhanVienPanel extends BaoCaoPanel {
    public ThongKeNhanVienPanel() {
        super(
                "Thống kê nhân viên",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê nhân viên".replace(' ', '_'),
                List.of(
                        // 1. CHẾ ĐỘ XEM: THU TIỀN VÀ CHÊNH LỆCH CA THU NGÂN
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
                                5,
                                true,
                                ThongKeService::layTongHopCa),

                        // 2. CHẾ ĐỘ XEM: LỊCH LÀM VIỆC & SỐ GIỜ CÔNG NHÂN VIÊN
                        taoKieuHienThi(
                                "Lịch làm việc",
                                new String[] {"Mã NV", "Nhân viên", "Chức vụ", "Số ca", "Số giờ"},
                                1,
                                4,
                                false,
                                ThongKeService::layNhanVien)));
    }
}
