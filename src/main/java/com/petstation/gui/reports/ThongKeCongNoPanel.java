package com.petstation.gui.reports;

import static com.petstation.gui.reports.DuLieuBaoCao.*;

import com.petstation.gui.reports.BaoCaoPanel.*;
import com.petstation.service.ThongKeService;

import java.util.List;

public final class ThongKeCongNoPanel extends BaoCaoPanel {
    public ThongKeCongNoPanel() {
        super(
                "Công nợ nhà cung cấp",
                "Báo cáo theo kỳ lọc ngày nhập; đã trả và dư nợ là số cập nhật hiện tại.",
                "Công nợ nhà cung cấp".replace(' ', '_'),
                List.of(
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
                                        ThongKeService::layDuNoHienTai)
                                .chiQuanLy(),
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
                                        ThongKeService::layCongNoNhaCungCap)
                                .chiQuanLy()),
                false,
                true);
    }

    @Override
    protected String layTieuDeBieuDo() {
        return "DƯ NỢ THEO NHÀ CUNG CẤP (Nhấp vào cột để xem chứng từ)";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Chọn kiểu báo cáo rồi bấm Xem thống kê. Dư nợ hiện tại tổng hợp các phiếu "
                + "nhập đã ghi nhận. Báo cáo theo kỳ chỉ lọc theo ngày nhập; cột đã trả và "
                + "dư nợ vẫn là số hiện tại. Nhấp vào cột để xem chứng từ; Xuất Excel xuất "
                + "toàn bộ bảng đang hiển thị.";
    }
}
