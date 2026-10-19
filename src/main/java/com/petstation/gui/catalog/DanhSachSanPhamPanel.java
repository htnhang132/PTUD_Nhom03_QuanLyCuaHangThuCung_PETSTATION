package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho sản phẩm danh sách vùng hiển thị. */
public final class DanhSachSanPhamPanel extends QuanLyDanhMucPanel {
    /** Khởi tạo DanhSachSanPhamPanel với các giá trị được cung cấp. */
    public DanhSachSanPhamPanel() {
        super(taoCauHinh(), taoCacTruong(), DanhMucService.laySanPham());
    }

    /**
     * Tạo cấu hình trong DanhSachSanPhamPanel.
     *
     * @return kết quả tạo cấu hình
     */
    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục sản phẩm", "sản phẩm")
                        .datTienTo("SP")
                        .timKiem("Tìm theo mã, tên sản phẩm...")
                        .datTrangThai(9, "Đang kinh doanh", "Ngừng kinh doanh");
        return cauHinh;
    }

    /**
     * Tạo các trường trong DanhSachSanPhamPanel.
     *
     * @return kết quả tạo các trường
     */
    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã SP", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Tên sản phẩm", Kieu.VAN_BAN).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Nhóm hàng", Kieu.HOP_CHON)
                        .batBuoc()
                        .datLuaChon("Thức ăn", "Phụ kiện", "Grooming", "Vệ sinh", "Thuốc"),
                TruongDuLieu.tao("Đơn vị tính", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Giá bán", Kieu.TIEN_TE).batBuoc(),
                TruongDuLieu.tao("Giá nhập tham khảo", Kieu.TIEN_TE).an(),
                TruongDuLieu.tao("Tồn tối thiểu", Kieu.SO_NGUYEN).batBuoc().an().datMacDinh("5"),
                TruongDuLieu.tao("Quản lý lô / HSD", Kieu.HOP_CHON).datLuaChon("Có", "Không").an(),
                TruongDuLieu.tao("Tồn hiện tại (chỉ đổi qua Nhập, Xuất, Kiểm kê)", Kieu.SO_NGUYEN)
                        .chiDoc()
                        .datMacDinh("0"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang kinh doanh", "Ngừng kinh doanh"));
    }
}
