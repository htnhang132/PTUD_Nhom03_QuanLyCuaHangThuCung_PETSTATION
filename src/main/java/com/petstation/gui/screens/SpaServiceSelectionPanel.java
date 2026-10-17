package com.petstation.gui.screens;

public final class SpaServiceSelectionPanel extends WorkflowPanel {
    public SpaServiceSelectionPanel() {
        super(new Config("Chọn dịch vụ Spa", "Chọn một hoặc nhiều dịch vụ cho phiếu tiếp nhận", new String[]{"Dịch vụ|6", "Đã chọn|2", "Thời lượng|150 phút", "Tạm tính|450.000₫"}, new String[]{"Dịch vụ", "Thời lượng", "Đơn giá", "Đã chọn"}, new Object[][]{new Object[]{"Tắm & cắt lông", "90 phút", "250.000₫", "Có"}, new Object[]{"Gói Spa toàn diện", "150 phút", "450.000₫", "Có"}, new Object[]{"Tắm & sấy khô", "60 phút", "150.000₫", "Không"}, new Object[]{"Vệ sinh tai, móng", "30 phút", "80.000₫", "Không"}}, "Xác nhận dịch vụ ▸ Tiến độ", null, "Mock UI chọn dịch vụ; chưa nối bảng giá dịch vụ."));
    }
}


