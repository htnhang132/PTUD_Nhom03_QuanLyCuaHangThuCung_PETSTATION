package com.petstation.gui.thongke;

import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienTai;
import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienThi;

import com.petstation.service.thongke.ThongKeService;

import java.util.List;

// Màn hình: Thống kê khách hàng (Mã: THONG_KE_KHACH_HANG)
// Luồng xử lý: GUI -> ThongKeService, ChiTietThongKeService -> ThongKeDAOImpl,
// ChiTietThongKeDAOImpl -> SQL (khach_hang, hoa_don)
// Mô tả: Phân nhóm khách hàng theo hạng thành viên (Đồng/Bạc/Vàng/Kim Cương), tần suất quay lại và
// mức chi tiêu
// Phân hệ: thongke
public final class ThongKeKhachHangPanel extends BaoCaoPanel {
    public ThongKeKhachHangPanel() {
        this(new ThongKeService());
    }

    public ThongKeKhachHangPanel(ThongKeService thongKeService) {
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
                                thongKeService::layLuotKhach),
                        taoKieuHienThi(
                                "Lượt dùng voucher trong kỳ",
                                new String[] {"Voucher", "Khách hàng", "Lượt dùng", "Giảm giá"},
                                0,
                                2,
                                false,
                                thongKeService::laySuDungVoucher),
                        taoKieuHienThi(
                                "Chi tiêu theo kỳ",
                                new String[] {"Mã KH", "Khách hàng", "Hạng", "Điểm", "Chi tiêu"},
                                1,
                                4,
                                true,
                                thongKeService::layKhachHang),
                        taoKieuHienTai(
                                "Sử dụng voucher",
                                new String[] {
                                    "Mã voucher", "Mệnh giá", "Đã dùng", "Số lượng", "Hạn dùng"
                                },
                                0,
                                2,
                                false,
                                ngayDangXet -> thongKeService.layVoucher())));
    }
}
