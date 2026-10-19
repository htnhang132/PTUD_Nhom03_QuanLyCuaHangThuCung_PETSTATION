package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.taoKieuHienThi;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho Hotel báo cáo vùng hiển thị. */
public final class ThongKeHotelPanel extends BaoCaoPanel {
    /** Khởi tạo ThongKeHotelPanel với các giá trị được cung cấp. */
    public ThongKeHotelPanel() {
        super(
                "Thống kê Pet Hotel",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê Pet Hotel".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Trạng thái phòng hiện tại",
                                new String[] {"Trạng thái", "Số phòng"},
                                0,
                                1,
                                false,
                                ngayDangXet -> ThongKeService.layTrangThaiPhong()),
                        taoKieuHienThi(
                                "Lưu trú đã quyết toán",
                                new String[] {
                                    "Loại phòng", "Lượt lưu trú", "Số ngày", "Tiền thu gồm cọc"
                                },
                                0,
                                3,
                                true,
                                ThongKeService::layDuLieuHotel)));
    }
}
