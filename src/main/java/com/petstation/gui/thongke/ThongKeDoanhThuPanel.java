package com.petstation.gui.thongke;

import static com.petstation.service.thongke.DuLieuBaoCaoService.chiSo;
import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienThi;

import com.petstation.service.thongke.ThongKeService;

import java.util.List;

// Màn hình: Thống kê doanh thu (Mã: THONG_KE_DOANH_THU)
// Luồng xử lý: GUI -> ThongKeService, ChiTietThongKeService -> ThongKeDAOImpl,
// ChiTietThongKeDAOImpl -> SQL (hoa_don, dong_thanh_toan_hoa_don)
// Mô tả: Báo cáo phân tích doanh thu bán hàng và dịch vụ theo thời gian, hình thức thanh toán và ca
// làm việc
// Phân hệ: thongke
public final class ThongKeDoanhThuPanel extends BaoCaoPanel {
    public ThongKeDoanhThuPanel() {
        this(new ThongKeService());
    }

    public ThongKeDoanhThuPanel(ThongKeService thongKeService) {
        super(
                "Thống kê doanh thu",
                "Tiền thu trong kỳ gồm thanh toán và cọc lưu trú; doanh thu hoàn tất kèm lợi nhuận"
                        + " gộp.",
                "Thống kê doanh thu".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                "Doanh thu hoàn tất",
                                new String[] {
                                    "Ngày",
                                    "Số đơn",
                                    "Doanh thu gốc",
                                    "Chiết khấu/Voucher",
                                    "Hoàn trả",
                                    "Doanh thu thuần",
                                    "Lợi nhuận gộp"
                                },
                                0,
                                5,
                                true,
                                thongKeService::layDoanhThuHoanTat),
                        taoKieuHienThi(
                                "Theo ngày",
                                new String[] {"Ngày", "Tiền thu ròng (gồm cọc)"},
                                0,
                                1,
                                true,
                                chiSo("Số ngày có thu", "Tổng tiền thu", "TB/ngày phát sinh"),
                                thongKeService::layDoanhThuTheoNgay),
                        taoKieuHienThi(
                                "Theo thanh toán",
                                new String[] {"Phương thức", "Tiền thu ròng", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                chiSo("Tổng số đơn", "Tổng tiền thu", "Thu ròng TB/HĐ (gồm cọc)"),
                                ngayDangXet -> thongKeService.layDoanhThu(ngayDangXet, "method")),
                        taoKieuHienThi(
                                "Theo thu ngân",
                                new String[] {"Nhân viên", "Tiền thu ròng", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                chiSo("Tổng số đơn", "Tổng tiền thu", "Thu ròng TB/HĐ (gồm cọc)"),
                                ngayDangXet -> thongKeService.layDoanhThu(ngayDangXet, "staff")),
                        taoKieuHienThi(
                                "Theo nguồn",
                                new String[] {"Nguồn", "Tiền thu ròng", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                chiSo("Tổng số đơn", "Tổng tiền thu", "Thu ròng TB/HĐ (gồm cọc)"),
                                ngayDangXet -> thongKeService.layDoanhThu(ngayDangXet, "source")),
                        taoKieuHienThi(
                                "Theo ca",
                                new String[] {"Ca", "Tiền thu ròng", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                chiSo("Tổng số đơn", "Tổng tiền thu", "Thu ròng TB/HĐ (gồm cọc)"),
                                ngayDangXet -> thongKeService.layDoanhThu(ngayDangXet, "shift")),
                        taoKieuHienThi(
                                "Theo khung giờ",
                                new String[] {"Khung giờ", "Doanh thu", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                chiSo("Tổng số đơn", "Tổng doanh thu", "Giá trị TB/đơn"),
                                thongKeService::layDoanhThuTheoGio),
                        taoKieuHienThi(
                                "Theo thứ trong tuần",
                                new String[] {"Thứ", "Doanh thu", "Số hóa đơn"},
                                0,
                                1,
                                true,
                                chiSo("Tổng số đơn", "Tổng doanh thu", "Giá trị TB/đơn"),
                                thongKeService::layDoanhThuTheoThu)));
    }
}
