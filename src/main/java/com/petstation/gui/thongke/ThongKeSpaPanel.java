package com.petstation.gui.thongke;

import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienThi;

import com.petstation.service.thongke.ThongKeService;

import java.util.List;

// Màn hình: Thống kê dịch vụ Spa (Mã: THONG_KE_SPA)
// Luồng xử lý: GUI -> ThongKeService, ChiTietThongKeService -> ThongKeDAOImpl,
// ChiTietThongKeDAOImpl -> SQL (phieu_tiep_nhan_spa, goi_dich_vu_spa)
// Mô tả: Đánh giá hiệu suất phục vụ dịch vụ spa, gói dịch vụ ưa chuộng và doanh thu theo chuyên
// viên
// Phân hệ: thongke
public final class ThongKeSpaPanel extends BaoCaoPanel {
    public ThongKeSpaPanel() {
        this(new ThongKeService());
    }

    public ThongKeSpaPanel(ThongKeService thongKeService) {
        super(
                "Thống kê dịch vụ Spa",
                "Chọn kỳ để xem giao dịch đã lưu",
                "Thống kê dịch vụ Spa".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Phiếu theo trạng thái",
                                new String[] {"Trạng thái", "Số phiếu"},
                                0,
                                1,
                                false,
                                thongKeService::layPhieuSpaTheoTrangThai),
                        taoKieuHienThi(
                                "Dịch vụ đã thanh toán",
                                new String[] {"Mã DV", "Dịch vụ", "Số lượt", "Giá trị dịch vụ"},
                                1,
                                3,
                                true,
                                thongKeService::layDuLieuSpa),
                        taoKieuHienThi(
                                "Trạng thái lịch hẹn",
                                new String[] {"Trạng thái", "Số lịch"},
                                0,
                                1,
                                false,
                                thongKeService::layLichHen)));
    }
}
