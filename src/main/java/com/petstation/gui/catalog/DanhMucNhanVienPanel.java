package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho nhân viên danh mục vùng hiển thị. */
public final class DanhMucNhanVienPanel extends QuanLyDanhMucPanel {
    /** Khởi tạo DanhMucNhanVienPanel với các giá trị được cung cấp. */
    public DanhMucNhanVienPanel() {
        super(taoCauHinh(), taoCacTruong(), DanhMucService.layNhanVien());
    }

    /**
     * Tạo cấu hình trong DanhMucNhanVienPanel.
     *
     * @return kết quả tạo cấu hình
     */
    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục nhân viên", "nhân viên")
                        .datTienTo("NV")
                        .timKiem("Tìm theo tên, SĐT, mã NV...")
                        .datTrangThai(6, "Đang làm", "Nghỉ việc");
        return cauHinh;
    }

    /**
     * Tạo các trường trong DanhMucNhanVienPanel.
     *
     * @return kết quả tạo các trường
     */
    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã NV", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Họ và tên", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Số điện thoại", Kieu.SO_DIEN_THOAI).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("CCCD", Kieu.CCCD).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Chức vụ", Kieu.HOP_CHON).batBuoc().datLuaChon(layTenCacVaiTro()),
                TruongDuLieu.tao("Ngày vào làm", Kieu.NGAY).batBuoc(),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON).datLuaChon("Đang làm", "Nghỉ việc"));
    }
}
