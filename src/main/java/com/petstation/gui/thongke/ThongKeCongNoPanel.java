package com.petstation.gui.thongke;

import static com.petstation.service.thongke.DuLieuBaoCaoService.chiSo;
import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienTai;
import static com.petstation.service.thongke.DuLieuBaoCaoService.taoKieuHienThi;

import com.petstation.service.thongke.ThongKeService;

import java.util.List;

// Màn hình: Thống kê công nợ NCC (Mã: THONG_KE_CONG_NO)
// Luồng xử lý: GUI -> ThongKeService, ChiTietThongKeService -> ThongKeDAOImpl,
// ChiTietThongKeDAOImpl -> SQL (nha_cung_cap, phieu_nhap_kho)
// Mô tả: Quản lý tổng giá trị hàng nhập, số tiền đã thanh toán và dư nợ phải trả theo từng nhà cung
// cấp
// Phân hệ: thongke
public final class ThongKeCongNoPanel extends BaoCaoPanel {
    public ThongKeCongNoPanel() {
        this(new ThongKeService());
    }

    public ThongKeCongNoPanel(ThongKeService thongKeService) {
        super(
                "Công nợ nhà cung cấp",
                "Báo cáo theo kỳ lọc ngày nhập; đã trả và dư nợ là số cập nhật hiện tại.",
                "Công nợ nhà cung cấp".replace(' ', '_'),
                List.of(
                        taoKieuHienThi(
                                        "Công nợ theo kỳ nhập",
                                        new String[] {
                                            "Mã NCC",
                                            "Nhà cung cấp",
                                            "Tổng nhập trong kỳ",
                                            "Đã trả hiện tại",
                                            "Dư nợ trên phiếu hiện tại"
                                        },
                                        1,
                                        4,
                                        true,
                                        chiSo("Số nhà cung cấp", "Tổng dư nợ", "Dư nợ TB/NCC"),
                                        thongKeService::layCongNoNhaCungCap)
                                .chiQuanLy(),
                        taoKieuHienTai(
                                        "Dư nợ hiện tại",
                                        new String[] {
                                            "Mã NCC",
                                            "Nhà cung cấp",
                                            "Tổng nhập",
                                            "Đã trả hiện tại",
                                            "Dư nợ hiện tại"
                                        },
                                        1,
                                        4,
                                        true,
                                        chiSo("Số nhà cung cấp", "Tổng dư nợ"),
                                        thongKeService::layDuNoHienTai)
                                .chiQuanLy()),
                false,
                false);
    }

    @Override
    protected String layTieuDeBieuDo() {
        return "DƯ NỢ THEO NHÀ CUNG CẤP (Nhấp vào cột để xem chứng từ)";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Chọn kiểu báo cáo hoặc khoảng kỳ để xem số liệu. Dư nợ hiện tại tổng hợp các phiếu"
                + " nhập đã ghi nhận. Báo cáo theo kỳ chỉ lọc theo ngày nhập; cột đã trả và dư nợ"
                + " vẫn là số hiện tại. Nhấp vào cột để xem chứng từ; Xuất Excel xuất toàn bộ"
                + " bảng đang hiển thị.";
    }
}
