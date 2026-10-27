package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.*;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho Hotel báo cáo vùng hiển thị. */
public final class ThongKeHotelPanel extends BaoCaoPanel {
    public ThongKeHotelPanel() {
        super(
                "Thống kê lưu trú",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê lưu trú".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Hợp đồng nhận trong kỳ",
                                new String[] {
                                    "Trạng thái",
                                    "Số hợp đồng",
                                    "Đơn giá phòng cộng gộp",
                                    "Cọc đã nhận"
                                },
                                0,
                                1,
                                false,
                                ThongKeService::layHopDongTrongKy),
                        taoKieuHienThi(
                                "Cọc nhận trong kỳ",
                                new String[] {"Ngày nhận", "Tiền cọc", "Số lần nhận"},
                                0,
                                1,
                                true,
                                ThongKeService::layCocTrongKy),
                        taoKieuHienTai(
                                "Trạng thái phòng hiện tại",
                                new String[] {"Trạng thái", "Số phòng"},
                                0,
                                1,
                                false,
                                ngayDangXet -> ThongKeService.layTrangThaiPhong()),
                        taoKieuHienThi(
                                "Lưu trú đã quyết toán",
                                new String[] {
                                    "Loại phòng",
                                    "Lượt lưu trú",
                                    "Số ngày",
                                    "Doanh thu hoàn tất (gồm cọc)"
                                },
                                0,
                                3,
                                true,
                                ThongKeService::layDuLieuHotel)));
    }
}
