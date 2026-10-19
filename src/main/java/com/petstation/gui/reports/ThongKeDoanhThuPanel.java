package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.taoKieuHienThi;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho doanh thu báo cáo vùng hiển thị. */
public final class ThongKeDoanhThuPanel extends BaoCaoPanel {
    /** Khởi tạo ThongKeDoanhThuPanel với các giá trị được cung cấp. */
    public ThongKeDoanhThuPanel() {
        super(
                "Thống kê doanh thu",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê doanh thu".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Theo ngày",
                                new String[] {"Ngày", "Doanh thu sau hoàn trả"},
                                0,
                                1,
                                true,
                                ThongKeService::layDoanhThuTheoNgay),
                        taoKieuHienThi(
                                "Theo thanh toán",
                                new String[] {"Phương thức", "Doanh thu", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                ngayDangXet -> ThongKeService.layDoanhThu(ngayDangXet, "method")),
                        taoKieuHienThi(
                                "Theo thu ngân",
                                new String[] {"Nhân viên", "Doanh thu", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                ngayDangXet -> ThongKeService.layDoanhThu(ngayDangXet, "staff")),
                        taoKieuHienThi(
                                "Theo nguồn",
                                new String[] {"Nguồn", "Doanh thu", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                ngayDangXet -> ThongKeService.layDoanhThu(ngayDangXet, "source")),
                        taoKieuHienThi(
                                "Theo ca",
                                new String[] {"Ca", "Doanh thu", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                ngayDangXet -> ThongKeService.layDoanhThu(ngayDangXet, "shift"))));
    }
}
