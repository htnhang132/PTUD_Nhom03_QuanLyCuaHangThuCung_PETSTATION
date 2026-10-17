package com.petstation.gui.screens;

public final class SpaSchedulePanel extends WorkflowPanel {
    public SpaSchedulePanel() {
        super(new Config("Lịch hẹn Spa", "Theo dõi lịch hẹn theo ngày, trạng thái và nhân viên", new String[]{"Lịch hôm nay|5", "Hoàn thành|1", "Đang làm|1", "Chờ xử lý|2"}, new String[]{"Mã lịch", "Giờ", "Thú cưng", "Dịch vụ", "Nhân viên", "Trạng thái"}, new Object[][]{new Object[]{"SP-001", "09:00", "Mochi", "Tắm & cắt lông", "Hoa", "Hoàn thành"}, new Object[]{"SP-002", "10:30", "Bống", "Tắm & sấy khô", "Lan", "Đang làm"}, new Object[]{"SP-003", "13:00", "Lucky", "Cắt móng + vệ sinh tai", "Nam", "Chờ xử lý"}, new Object[]{"SP-004", "14:30", "Miu", "Tắm & tạo kiểu", "Hoa", "Chờ xử lý"}}, "Lưu lịch hẹn", null, "Mock UI lịch hẹn; chưa nối AppointmentService."));
    }
}


