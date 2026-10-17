package com.petstation.gui.screens;

public final class HotelJournalPanel extends WorkflowPanel {
    public HotelJournalPanel() {
        super(new Config("Nhật ký lưu trú", "Theo dõi ăn uống, chăm sóc, sức khỏe và sự kiện", new String[]{"Mục hôm nay|2", "Tổng mục|4", "Ảnh đính kèm|1", "Cảnh báo|1"}, new String[]{"Thời gian", "Loại", "Nhân viên", "Nội dung", "Trạng thái"}, new Object[][]{new Object[]{"22/09 08:30", "Bữa ăn", "Lan", "Ăn sáng đầy đủ, vui vẻ", "Đã ghi"}, new Object[]{"22/09 12:00", "Sức khỏe", "Nam", "Bình thường", "Đã ghi"}, new Object[]{"21/09 17:00", "Vui chơi", "Hoa", "Chơi 30 phút", "Có ảnh"}, new Object[]{"21/09 08:00", "Bữa ăn", "Lan", "Ăn theo chế độ", "Đã ghi"}}, "Ghi nhật ký", "Trả phòng ▸", "Mock UI nhật ký; chưa lưu media/sự kiện vào backend."));
    }
}


