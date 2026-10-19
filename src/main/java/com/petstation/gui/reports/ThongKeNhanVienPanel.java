package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.taoKieuHienThi;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho nhân viên báo cáo vùng hiển thị. */
public final class ThongKeNhanVienPanel extends BaoCaoPanel {
    /** Khởi tạo ThongKeNhanVienPanel với các giá trị được cung cấp. */
    public ThongKeNhanVienPanel() {
        super(
                "Thống kê nhân viên",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê nhân viên".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Lịch làm việc",
                                new String[] {"Mã NV", "Nhân viên", "Chức vụ", "Số ca", "Số giờ"},
                                1,
                                4,
                                false,
                                ThongKeService::layNhanVien)));
    }
}
