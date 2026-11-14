package com.petstation.gui.hethong;

import com.petstation.gui.chung.DanhMucNhieuTabPanel;

import javax.swing.JPanel;

// Màn hình: Tài khoản & Phân quyền (Mã: TAI_KHOAN_PHAN_QUYEN)
// Luồng xử lý: GUI -> XacThucService, TaiKhoanService -> TaiKhoanDAOImpl, NhanVienDAOImpl -> SQL
// (tai_khoan, nhan_vien)
// Mô tả: Cấp phát tài khoản đăng nhập, gán vai trò quyền hạn và quản lý khóa/mở tài khoản người
// dùng
// Phân hệ: hethong
public final class TaiKhoanPhanQuyenPanel extends DanhMucNhieuTabPanel {
    public TaiKhoanPhanQuyenPanel() {
        super(
                new String[] {"Tài khoản", "Ma trận quyền"},
                new JPanel[] {new DanhSachTaiKhoanPanel(), new MaTranQuyenPanel()});
    }
}
