package com.petstation.gui.thongke;

import static com.petstation.service.thongke.DuLieuBaoCaoService.chiSo;
import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienThi;

import com.petstation.service.thongke.ThongKeService;

import java.util.List;

// Màn hình Thống kê > Nhân viên
// Màn hình: Thống kê nhân viên (Mã: THONG_KE_NHAN_VIEN)
// Luồng xử lý: GUI -> ThongKeService, ChiTietThongKeService -> ThongKeDAOImpl,
// ChiTietThongKeDAOImpl -> SQL (nhan_vien, hoa_don)
// Mô tả: Đánh giá năng suất bán hàng, số giờ trực ca và doanh số đóng góp của từng nhân sự
// Phân hệ: thongke
public final class ThongKeNhanVienPanel extends BaoCaoPanel {
    public ThongKeNhanVienPanel() {
        this(new ThongKeService());
    }

    public ThongKeNhanVienPanel(ThongKeService thongKeService) {
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
                                    "Tổng thu trong ca (HĐ + Cọc)",
                                    "Thực đếm",
                                    "Chênh lệch"
                                },
                                0,
                                7,
                                true,
                                chiSo("Số ca thu ngân", "Tổng thu trong ca", "TB thu/ca"),
                                thongKeService::layTongHopCa),

                        // 2. CHẾ ĐỘ XEM: LỊCH LÀM VIỆC & SỐ GIỜ CÔNG NHÂN VIÊN
                        taoKieuHienThi(
                                "Lịch làm việc",
                                new String[] {"Mã NV", "Nhân viên", "Chức vụ", "Số ca", "Số giờ"},
                                1,
                                4,
                                false,
                                chiSo("Số nhân viên", "Tổng số giờ", "TB/nhân viên"),
                                thongKeService::layNhanVien)));
    }
}
