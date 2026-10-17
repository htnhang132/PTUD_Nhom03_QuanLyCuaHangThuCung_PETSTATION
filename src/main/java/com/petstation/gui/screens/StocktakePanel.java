package com.petstation.gui.screens;

public final class StocktakePanel extends WorkflowPanel {
    public StocktakePanel() {
        super(new Config("Kiểm kê tồn kho", "Tạo đợt kiểm kê, nhập số thực tế và tính chênh lệch", new String[]{"Đợt đang kiểm|1", "Sản phẩm|24", "Chênh lệch|3", "Đã chốt|5"}, new String[]{"Mã SP", "Tên sản phẩm", "Tồn hệ thống", "Tồn thực tế", "Chênh lệch", "Ghi chú"}, new Object[][]{new Object[]{"SP001", "Pate Whiskas Cá ngừ", "24", "23", "-1", "Thiếu 1"}, new Object[]{"SP002", "Hạt Royal Canin Puppy", "12", "12", "0", "Khớp"}, new Object[]{"SP003", "Sữa tắm Beaphar", "8", "9", "+1", "Dư 1"}, new Object[]{"SP004", "Frontline", "3", "3", "0", "Khớp"}}, "Gửi duyệt chênh lệch", "Tạo phiếu kiểm kê", "Mock UI kiểm kê; chưa chốt adjustment vào kho."));
    }
}


