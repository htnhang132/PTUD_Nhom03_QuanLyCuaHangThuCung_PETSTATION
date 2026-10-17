package com.petstation.gui.screens;

public final class PendingInvoicesPanel extends WorkflowPanel {
    public PendingInvoicesPanel() {
        super(new Config("Đơn chờ", "Danh sách hóa đơn đang tạm lưu để tiếp tục xử lý", new String[]{"Đơn chờ|4", "Tổng tiền|8.450.000₫", "Hôm nay|3", "Quá 24h|1"}, new String[]{"Mã đơn", "Khách hàng", "Thời gian", "Số SP", "Tổng tiền", "Thao tác"}, new Object[][]{new Object[]{"HD-2409-021", "Trần Thị Bích", "10:15 hôm nay", "3", "450.000₫", "Tiếp tục"}, new Object[]{"HD-2409-022", "Khách lẻ", "11:02 hôm nay", "5", "1.250.000₫", "Tiếp tục"}, new Object[]{"HD-2409-018", "Nguyễn Văn Hùng", "21/09 16:20", "2", "680.000₫", "Xem lại"}}, "Tạo đơn mới", "Xóa đơn chọn", "Mock UI hóa đơn chờ; chưa lưu vào transaction database."));
    }
}


