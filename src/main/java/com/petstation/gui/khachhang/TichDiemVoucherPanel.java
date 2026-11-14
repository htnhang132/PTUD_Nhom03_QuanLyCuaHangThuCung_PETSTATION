package com.petstation.gui.khachhang;

import com.petstation.gui.chung.DanhMucNhieuTabPanel;

import javax.swing.JPanel;

// Màn hình Danh mục > Điểm thưởng và voucher
// Màn hình: Tích điểm & Voucher (Mã: TICH_DIEM_VOUCHER)
// Luồng xử lý: GUI -> KhuyenMaiService -> VoucherDAOImpl -> SQL (voucher, khach_hang)
// Mô tả: Đổi điểm thưởng tích lũy lấy mã giảm giá (voucher), tra cứu hạn dùng và tình trạng sử dụng
// Phân hệ: khachhang
public final class TichDiemVoucherPanel extends DanhMucNhieuTabPanel {
    public TichDiemVoucherPanel() {
        super(
                new String[] {"Quy tắc điểm & hạng", "Voucher"},
                new JPanel[] {new QuyTacTichDiemPanel(), new DanhMucVoucherPanel()});
    }
}
