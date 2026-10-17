package com.petstation.gui.screens;

public final class GoodsWriteOffPanel extends WorkflowPanel {
    public GoodsWriteOffPanel() {
        super(new Config("Xuất kho / Xuất hủy", "Lập phiếu xuất hủy, lý do và quy trình phê duyệt", new String[]{"Chờ duyệt|2", "Đã duyệt|7", "Tháng này|9", "Giá trị hủy|520.000₫"}, new String[]{"Mã phiếu", "Khoản mục", "Số lượng", "Lý do", "Người lập", "Trạng thái"}, new Object[][]{new Object[]{"XH-2409-001", "Frontline LO-2409", "2", "Hết hạn", "Hoàng Văn Nam", "Chờ duyệt"}, new Object[]{"XH-2409-002", "Pate Whiskas LO-2403", "3", "Hư hỏng", "Hoàng Văn Nam", "Đã duyệt"}, new Object[]{"XH-2409-003", "Dây dắt", "1", "Lỗi sản xuất", "Nguyễn Văn Bình", "Đã duyệt"}}, "Tạo phiếu xuất hủy", "Gửi phê duyệt", "Mock UI xuất hủy; chưa cập nhật stock movement."));
    }
}


