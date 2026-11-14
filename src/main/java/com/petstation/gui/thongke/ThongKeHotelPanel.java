package com.petstation.gui.thongke;

import static com.petstation.service.thongke.DuLieuBaoCaoService.chiSo;
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
                                    "Tổng đơn giá phòng/đêm",
                                    "Cọc đã nhận"
                                },
                                0,
                                1,
                                false,
                                chiSo("Số trạng thái", "Tổng hợp đồng", "TB/trạng thái"),
                                thongKeService::layHopDongTrongKy),
                        taoKieuHienThi(
                                "Cọc nhận trong kỳ",
                                new String[] {"Ngày nhận", "Tiền cọc", "Số lần nhận"},
                                0,
                                1,
                                true,
                                chiSo("Số ngày nhận cọc", "Tổng tiền cọc", "TB/ngày"),
                                thongKeService::layCocTrongKy),
                        taoKieuHienTai(
                                "Trạng thái phòng hiện tại",
                                new String[] {"Trạng thái", "Số phòng"},
                                0,
                                1,
                                false,
                                chiSo("Số trạng thái", "Tổng số phòng"),
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
                                chiSo("Số loại phòng", "Tổng doanh thu", "TB/loại phòng"),
                                thongKeService::layDuLieuHotel)));
    }
}
