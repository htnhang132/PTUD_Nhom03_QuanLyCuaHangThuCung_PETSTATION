package com.petstation.gui.khachhang;

import com.petstation.gui.chung.DanhMucNhieuTabPanel;

import javax.swing.JPanel;

// Màn hình Danh mục > Voucher và điểm thưởng
// Màn hình: Voucher & Điểm thưởng (Mã: TICH_DIEM_VOUCHER)
// Luồng xử lý: GUI -> KhuyenMaiService -> VoucherDAOImpl -> SQL (voucher, khach_hang)
// Mô tả: Quản lý mã giảm giá (voucher), quy tắc tích điểm và hạng thành viên
// Phân hệ: khachhang
public final class TichDiemVoucherPanel extends DanhMucNhieuTabPanel {
    public TichDiemVoucherPanel() {
        super(
                new String[] {"Voucher", "Quy tắc điểm & hạng"},
                new JPanel[] {new DanhMucVoucherPanel(), new QuyTacTichDiemPanel()});
    }
}
