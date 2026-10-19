package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;
import com.petstation.util.TienTe;

import java.time.LocalDate;
import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho khuyến mãi danh mục vùng hiển thị. */
public final class DanhMucKhuyenMaiPanel extends QuanLyDanhMucPanel {
    /** Khởi tạo DanhMucKhuyenMaiPanel với các giá trị được cung cấp. */
    public DanhMucKhuyenMaiPanel() {
        super(taoCauHinh(), taoCacTruong(), DanhMucService.layKhuyenMai());
    }

    /**
     * Tạo cấu hình trong DanhMucKhuyenMaiPanel.
     *
     * @return kết quả tạo cấu hình
     */
    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục khuyến mãi", "khuyến mãi")
                        .datTienTo("KM")
                        .timKiem("Tìm theo mã, tên chương trình...")
                        .datTrangThai(8, "Đang áp dụng", "Ngừng áp dụng")
                        .datBoChuanHoa(
                                dongDuLieu -> {
                                    // Hôm nay.
                                    LocalDate homNay = LocalDate.now();
                                    // Từ.
                                    LocalDate tu = docNgay(dongDuLieu[6]);
                                    // Đến.
                                    LocalDate den = docNgay(dongDuLieu[7]);
                                    // Trạng thái.
                                    String trangThai = String.valueOf(dongDuLieu[8]);
                                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                                    if (den != null && den.isBefore(homNay))
                                        dongDuLieu[8] = "Ngừng áp dụng";
                                    else // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang
                                    // xét.
                                    if (!"Ngừng áp dụng".equals(trangThai))
                                        dongDuLieu[8] =
                                                tu != null && tu.isAfter(homNay)
                                                        ? "Chưa áp dụng"
                                                        : "Đang áp dụng";
                                })
                        .datBoKiemTra(
                                dongDuLieu -> {
                                    // Từ.
                                    LocalDate tu = docNgay(dongDuLieu[6]);
                                    // Đến.
                                    LocalDate den = docNgay(dongDuLieu[7]);
                                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                                    if (tu != null && den != null && den.isBefore(tu))
                                        return "Đến ngày phải sau hoặc bằng Từ ngày.";
                                    // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                                    if ("Giảm %".equals(dongDuLieu[2])) {
                                        // Giá trị.
                                        long giaTri = TienTe.doc(String.valueOf(dongDuLieu[4]));
                                        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                                        if (giaTri < 1 || giaTri > 100)
                                            return "Giá trị giảm % phải từ 1 đến 100.";
                                    }
                                    return null;
                                });
        return cauHinh;
    }

    /**
     * Tạo các trường trong DanhMucKhuyenMaiPanel.
     *
     * @return kết quả tạo các trường
     */
    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã KM", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Tên chương trình", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Loại", Kieu.HOP_CHON).datLuaChon("Giảm %", "Giảm tiền"),
                TruongDuLieu.tao("Phạm vi", Kieu.HOP_CHON)
                        .datLuaChon("Sản phẩm", "Dịch vụ", "Cả đơn"),
                TruongDuLieu.tao("Giá trị (% hoặc ₫)", Kieu.SO_NGUYEN).batBuoc().an(),
                TruongDuLieu.tao("Đơn tối thiểu", Kieu.TIEN_TE).an().datMacDinh("0"),
                TruongDuLieu.tao("Từ ngày", Kieu.NGAY).batBuoc(),
                TruongDuLieu.tao("Đến ngày", Kieu.NGAY).batBuoc(),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Chưa áp dụng", "Đang áp dụng", "Ngừng áp dụng"));
    }
}
