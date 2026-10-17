package com.petstation.gui.screens;

public final class CheckoutPanel extends WorkflowPanel {
    public CheckoutPanel() {
        super(new Config("Trả phòng & quyết toán", "Tổng số ngày, phụ phí, giảm giá và thanh toán", new String[]{"Số ngày|3", "Tiền phòng|1.050.000₫", "Phụ phí|100.000₫", "Tổng phải thu|1.150.000₫"}, new String[]{"Khoản mục", "Mô tả", "Số tiền", "Trạng thái"}, new Object[][]{new Object[]{"Tiền phòng", "K03 VIP x 3 đêm", "1.050.000₫", "Tạm tính"}, new Object[]{"Phụ phí", "Quá hạn 1 ngày", "350.000₫", "Cần xác nhận"}, new Object[]{"Giảm giá", "Voucher thành viên", "-250.000₫", "Đã áp dụng"}, new Object[]{"Tổng cộng", "–", "1.150.000₫", "Chờ thanh toán"}}, "Quyết toán & thanh toán ▸", "◀ Nhật ký lưu trú", "Mock UI checkout; chưa tạo invoice/refund record."));
    }
}
