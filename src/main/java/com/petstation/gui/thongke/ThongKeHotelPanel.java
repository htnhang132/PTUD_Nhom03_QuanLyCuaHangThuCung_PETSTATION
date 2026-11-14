package com.petstation.gui.thongke;

import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienTai;
import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienThi;

import com.petstation.service.thongke.ThongKeService;

import java.util.List;

// Màn hình: Thống kê phòng Hotel (Mã: THONG_KE_HOTEL)
// Luồng xử lý: GUI -> ThongKeService, ChiTietThongKeService -> ThongKeDAOImpl,
// ChiTietThongKeDAOImpl -> SQL (hop_dong_luu_tru, phong_hotel)
// Mô tả: Thống kê công suất sử dụng phòng lưu trú, doanh thu tiền phòng và số lượng thú cưng gửi
// qua đêm
// Phân hệ: thongke
public final class ThongKeHotelPanel extends BaoCaoPanel {
    public ThongKeHotelPanel() {
        this(new ThongKeService());
    }

    public ThongKeHotelPanel(ThongKeService thongKeService) {
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
                                thongKeService::layHopDongTrongKy),
                        taoKieuHienThi(
                                "Cọc nhận trong kỳ",
                                new String[] {"Ngày nhận", "Tiền cọc", "Số lần nhận"},
                                0,
                                1,
                                true,
                                thongKeService::layCocTrongKy),
                        taoKieuHienTai(
                                "Trạng thái phòng hiện tại",
                                new String[] {"Trạng thái", "Số phòng"},
                                0,
                                1,
                                false,
                                ngayDangXet -> thongKeService.layTrangThaiPhong()),
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
                                thongKeService::layDuLieuHotel)));
    }
}
