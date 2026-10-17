package com.petstation.gui.screens;

public final class SpaClosePanel extends WorkflowPanel {
    public SpaClosePanel() {
        super(new Config("Đóng phiếu Spa", "Tổng kết dịch vụ, phụ phí, voucher và thanh toán", new String[]{"Phiếu chờ đóng|2", "Dịch vụ|450.000₫", "Phụ phí|0₫", "Tổng phải thu|450.000₫"}, new String[]{"Khoản mục", "Mô tả", "Số tiền", "Ghi chú"}, new Object[][]{new Object[]{"Dịch vụ", "Tắm & cắt lông", "250.000₫", "Hoàn thành"}, new Object[]{"Dịch vụ", "Thêm lông khô mùi", "100.000₫", "Hoàn thành"}, new Object[]{"Voucher", "SPA20K", "-20.000₫", "Đã áp dụng"}, new Object[]{"Tổng cộng", "—", "330.000₫", "Chờ thanh toán"}}, "Đóng phiếu & thanh toán ▸", null, "Mock UI đóng phiếu; chưa tạo hóa đơn thanh toán."));
    }
}


