package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho khách hàng danh mục vùng hiển thị. */
public final class DanhMucKhachHangPanel extends QuanLyDanhMucPanel {
    /** Khởi tạo DanhMucKhachHangPanel với các giá trị được cung cấp. */
    public DanhMucKhachHangPanel() {
        super(taoCauHinh(), taoCacTruong(), DanhMucService.layKhachHang());
    }

    /**
     * Tạo cấu hình trong DanhMucKhachHangPanel.
     *
     * @return kết quả tạo cấu hình
     */
    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục khách hàng", "khách hàng")
                        .themBoLoc("Hạng thành viên", d -> String.valueOf(d[6]))
                        .datTienTo("KH")
                        .timKiem("Tìm theo tên, SĐT, mã khách...")
                        .datTrangThai(8, "Đang hoạt động", "Ngừng hoạt động");
        return cauHinh;
    }

    /**
     * Tạo các trường trong DanhMucKhachHangPanel.
     *
     * @return kết quả tạo các trường
     */
    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã khách", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Họ và tên", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Số điện thoại", Kieu.SO_DIEN_THOAI).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Email", Kieu.EMAIL).an(),
                TruongDuLieu.tao("Địa chỉ", Kieu.VAN_BAN),
                TruongDuLieu.tao("Ghi chú", Kieu.VUNG).an(),
                TruongDuLieu.tao("Hạng", Kieu.VAN_BAN).chiDoc().datMacDinh("Đồng"),
                TruongDuLieu.tao("Điểm", Kieu.SO_NGUYEN).chiDoc().datMacDinh("0"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
    }
}
