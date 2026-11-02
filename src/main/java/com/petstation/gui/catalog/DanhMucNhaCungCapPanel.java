package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho nhà cung cấp danh mục vùng hiển thị. */
public final class DanhMucNhaCungCapPanel extends QuanLyDanhMucPanel {
    public DanhMucNhaCungCapPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.chuanBi(
                        com.petstation.model.LoaiDanhMuc.NHA_CUNG_CAP,
                        !javax.swing.SwingUtilities.isEventDispatchThread()
                                || !com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục nhà cung cấp", "nhà cung cấp")
                        .datTienTo("NCC")
                        .timKiem("Tìm theo tên, người liên hệ, MST...")
                        .datTrangThai(7, "Đang hợp tác", "Ngừng hợp tác");
        return cauHinh;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã NCC", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Tên nhà cung cấp", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Người liên hệ", Kieu.VAN_BAN),
                TruongDuLieu.tao("Số điện thoại", Kieu.SO_DIEN_THOAI).batBuoc(),
                TruongDuLieu.tao("Địa chỉ", Kieu.VAN_BAN),
                TruongDuLieu.tao("Mã số thuế", Kieu.VAN_BAN).kiemTraDuyNhat(),
                TruongDuLieu.tao("Điều khoản thanh toán", Kieu.HOP_CHON)
                        .datLuaChon("Trả ngay", "15 ngày", "30 ngày", "45 ngày"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang hợp tác", "Ngừng hợp tác"),
                TruongDuLieu.tao("Nhóm mặt hàng cung cấp", Kieu.VAN_BAN),
                TruongDuLieu.tao("Ghi chú", Kieu.VAN_BAN));
    }
}
