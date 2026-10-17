package com.petstation.gui.screens;

public final class SpaProgressPanel extends WorkflowPanel {
    public SpaProgressPanel() {
        super(new Config("Tiến độ Spa", "Theo dõi dịch vụ đang làm, nhân viên và trạng thái", new String[]{"Đang làm|1", "Chờ xử lý|2", "Hoàn thành|1", "Tổng phiếu|4"}, new String[]{"Mã phiếu", "Thú cưng", "Dịch vụ", "Nhân viên", "Tiến độ", "Trạng thái"}, new Object[][]{new Object[]{"SP-002", "Bống", "Tắm & sấy khô", "Lan", "60%", "Đang làm"}, new Object[]{"SP-003", "Lucky", "Cắt móng + vệ sinh tai", "Nam", "0%", "Chờ xử lý"}, new Object[]{"SP-004", "Miu", "Tắm & tạo kiểu", "Hoa", "0%", "Chờ xử lý"}, new Object[]{"SP-001", "Mochi", "Tắm & cắt lông", "Hoa", "100%", "Hoàn thành"}}, "Cập nhật trạng thái", null, "Mock UI tiến độ; chưa cập nhật realtime/service."));
    }
}


