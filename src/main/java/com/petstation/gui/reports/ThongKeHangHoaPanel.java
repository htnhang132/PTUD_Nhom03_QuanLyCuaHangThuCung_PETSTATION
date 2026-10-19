package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.taoKieuHienThi;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho hàng hóa báo cáo vùng hiển thị. */
public final class ThongKeHangHoaPanel extends BaoCaoPanel {
    /** Khởi tạo ThongKeHangHoaPanel với các giá trị được cung cấp. */
    public ThongKeHangHoaPanel() {
        super(
                "Thống kê hàng hóa",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê hàng hóa".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Sản phẩm bán chạy",
                                new String[] {"Mã SP", "Sản phẩm", "Số bán sau trả", "Doanh thu"},
                                1,
                                3,
                                true,
                                ThongKeService::laySanPhamBanChay),
                        taoKieuHienThi(
                                "Tồn kho hiện tại",
                                new String[] {
                                    "Mã SP",
                                    "Sản phẩm",
                                    "Tồn kho",
                                    "Tồn tối thiểu",
                                    "Giá nhập gần nhất"
                                },
                                1,
                                2,
                                false,
                                ngayDangXet -> ThongKeService.layTonKho()),
                        taoKieuHienThi(
                                "Sắp hết hạn",
                                new String[] {"Mã SP", "Sản phẩm", "Số lô", "Hạn dùng", "Tồn"},
                                1,
                                4,
                                false,
                                ngayDangXet -> ThongKeService.layLoSapHetHan())));
    }
}
