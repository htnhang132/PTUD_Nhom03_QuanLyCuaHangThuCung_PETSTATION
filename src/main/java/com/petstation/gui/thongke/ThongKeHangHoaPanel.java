package com.petstation.gui.thongke;

import static com.petstation.service.thongke.DuLieuBaoCaoService.chiSo;
import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienTai;
import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienThi;

import com.petstation.dto.DuLieuBaoCao.KieuHienThi;
import com.petstation.service.thongke.ThongKeService;

import java.util.ArrayList;
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
                taoDanhSachBaoCao(thongKeService));
    }

    private static List<KieuHienThi> taoDanhSachBaoCao(ThongKeService thongKeService) {
        List<KieuHienThi> ds = new ArrayList<>();
        ds.add(
                taoKieuHienThi(
                        "Sản phẩm bán chạy",
                        new String[] {"Mã SP", "Sản phẩm", "Số bán sau trả", "Doanh thu"},
                        1,
                        3,
                        true,
                        chiSo("Số sản phẩm", "Tổng doanh thu", "TB/sản phẩm"),
                        thongKeService::laySanPhamBanChay));
        ds.add(
                taoKieuHienTai(
                        "Hàng tồn đọng / chôn vốn",
                        new String[] {
                            "Mã SP", "Sản phẩm", "Tồn kho", "Giá nhập gần nhất", "Tổng vốn chôn"
                        },
                        1,
                        4,
                        true,
                        chiSo("Số mặt hàng", "Tổng vốn chôn"),
                        ngayDangXet -> thongKeService.layHangTonDong()));
        ds.add(
                taoKieuHienTai(
                        "Sắp hết hạn",
                        new String[] {"Mã SP", "Sản phẩm", "Số lô", "Hạn dùng", "Tồn"},
                        1,
                        4,
                        false,
                        chiSo("Số lô hàng", "Tổng SL tồn"),
                        ngayDangXet -> thongKeService.layLoSapHetHan()));
        themBaoCaoKiemKeVaTonKho(ds, thongKeService);
        return ds;
    }

    private static void themBaoCaoKiemKeVaTonKho(
            List<KieuHienThi> ds, ThongKeService thongKeService) {
        ds.add(
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
                        chiSo("Số lượt hao hụt", "Tổng giá trị lệch", "TB/lượt"),
                        thongKeService::layHaoHutKiemKe));
        ds.add(
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
                                chiSo("Số nhóm biến động", "Chênh lệch ròng", "TB/nhóm"),
                                thongKeService::layBienDongKho)
                        .chiQuanLy());
        ds.add(
                taoKieuHienTai(
                        "Tồn kho hiện tại",
                        new String[] {
                            "Mã SP", "Sản phẩm", "Tồn kho", "Tồn tối thiểu", "Giá nhập gần nhất"
                        },
                        1,
                        2,
                        false,
                        chiSo("Số mặt hàng", "Tổng tồn kho"),
                        ngayDangXet -> thongKeService.layTonKho()));
    }
}
