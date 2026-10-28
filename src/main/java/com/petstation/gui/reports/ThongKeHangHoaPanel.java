package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.*;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.*;

/** Cung cấp giao diện và xử lý tương tác cho hàng hóa báo cáo vùng hiển thị. */
public final class ThongKeHangHoaPanel extends BaoCaoPanel {
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
                        taoKieuHienTai(
                                "Hàng tồn đọng / chôn vốn",
                                new String[] {
                                    "Mã SP",
                                    "Sản phẩm",
                                    "Tồn kho",
                                    "Giá nhập gần nhất",
                                    "Tổng vốn chôn"
                                },
                                1,
                                4,
                                true,
                                ngayDangXet -> ThongKeService.layHangTonDong()),
                        taoKieuHienTai(
                                "Sắp hết hạn",
                                new String[] {"Mã SP", "Sản phẩm", "Số lô", "Hạn dùng", "Tồn"},
                                1,
                                4,
                                false,
                                ngayDangXet -> ThongKeService.layLoSapHetHan()),
                        taoKieuHienThi(
                                "Hao hụt từ kiểm kê",
                                new String[] {
                                    "Mã SP",
                                    "Sản phẩm",
                                    "Số lô",
                                    "Chênh lệch",
                                    "Giá trị lệch",
                                    "Lý do",
                                    "Ngày tạo"
                                },
                                1,
                                4,
                                true,
                                ThongKeService::layHaoHutKiemKe),
                        taoKieuHienThi(
                                        "Biến động trong kỳ",
                                        new String[] {
                                            "Mã SP",
                                            "Sản phẩm",
                                            "Loại giao dịch",
                                            "Số lượng (+ nhập / − xuất)"
                                        },
                                        1,
                                        3,
                                        false,
                                        ThongKeService::layBienDongKho)
                                .chiQuanLy(),
                        taoKieuHienTai(
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
                                ngayDangXet -> ThongKeService.layTonKho())));
    }
}
