package com.petstation.gui.thongke;

import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienTai;
import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienThi;

import com.petstation.service.thongke.ThongKeService;

import java.util.List;

// Màn hình: Thống kê hàng hóa & kho (Mã: THONG_KE_HANG_HOA)
// Luồng xử lý: GUI -> ThongKeService, ChiTietThongKeService -> ThongKeDAOImpl,
// ChiTietThongKeDAOImpl -> SQL (san_pham, lo_hang)
// Mô tả: Phân tích top sản phẩm bán chạy, tỷ trọng doanh số theo nhóm hàng và cảnh báo hàng sắp hết
// hạn/tồn thấp
// Phân hệ: thongke
public final class ThongKeHangHoaPanel extends BaoCaoPanel {
    public ThongKeHangHoaPanel() {
        this(new ThongKeService());
    }

    public ThongKeHangHoaPanel(ThongKeService thongKeService) {
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
                                thongKeService::laySanPhamBanChay),
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
                                ngayDangXet -> thongKeService.layHangTonDong()),
                        taoKieuHienTai(
                                "Sắp hết hạn",
                                new String[] {"Mã SP", "Sản phẩm", "Số lô", "Hạn dùng", "Tồn"},
                                1,
                                4,
                                false,
                                ngayDangXet -> thongKeService.layLoSapHetHan()),
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
                                thongKeService::layHaoHutKiemKe),
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
                                        thongKeService::layBienDongKho)
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
                                ngayDangXet -> thongKeService.layTonKho())));
    }
}
