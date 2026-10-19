package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.*;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho khách hàng báo cáo vùng hiển thị. */
public final class ThongKeKhachHangPanel extends BaoCaoPanel {
    /** Khởi tạo ThongKeKhachHangPanel với các giá trị được cung cấp. */
    public ThongKeKhachHangPanel() {
        super(
                "Thống kê khách hàng",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê khách hàng".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Khách mới / quay lại",
                                new String[] {
                                    "Nhóm khách", "Số khách", "Lượt mua", "Tiền thanh toán"
                                },
                                0,
                                1,
                                false,
                                ThongKeService::layLuotKhach),
                        taoKieuHienThi(
                                "Lượt dùng voucher trong kỳ",
                                new String[] {"Voucher", "Khách hàng", "Lượt dùng", "Giảm giá"},
                                0,
                                2,
                                false,
                                ThongKeService::laySuDungVoucher),
                        taoKieuHienThi(
                                "Chi tiêu theo kỳ",
                                new String[] {"Mã KH", "Khách hàng", "Hạng", "Điểm", "Chi tiêu"},
                                1,
                                4,
                                true,
                                ThongKeService::layKhachHang),
                        taoKieuHienTai(
                                "Sử dụng voucher",
                                new String[] {
                                    "Mã voucher", "Mệnh giá", "Đã dùng", "Số lượng", "Hạn dùng"
                                },
                                0,
                                2,
                                false,
                                ngayDangXet -> ThongKeService.layVoucher())));
    }
}
