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

/** Cung cấp giao diện và xử lý tương tác cho voucher danh mục vùng hiển thị. */
public final class DanhMucVoucherPanel extends QuanLyDanhMucPanel {
    /** Khởi tạo DanhMucVoucherPanel với các giá trị được cung cấp. */
    public DanhMucVoucherPanel() {
        super(taoCauHinh(), taoCacTruong(), DanhMucService.layVoucher());
    }

    /**
     * Tạo cấu hình trong DanhMucVoucherPanel.
     *
     * @return kết quả tạo cấu hình
     */
    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục voucher", "voucher")
                        .timKiem("Tìm theo mã voucher...")
                        .datTrangThai(6, "Còn hiệu lực", "Ngừng")
                        .datBoChuanHoa(
                                dongDuLieu -> {
                                    // Hạn dùng.
                                    LocalDate hanDung = docNgay(dongDuLieu[2]);
                                    // Trạng thái.
                                    String trangThai = String.valueOf(dongDuLieu[6]);
                                    // Số lượng.
                                    long soLuong = TienTe.doc(String.valueOf(dongDuLieu[3]));
                                    // Đã dùng.
                                    long daDung = TienTe.doc(String.valueOf(dongDuLieu[4]));
                                    // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
                                    if ("Ngừng".equals(trangThai)) return;
                                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                                    if (hanDung != null && hanDung.isBefore(LocalDate.now()))
                                        dongDuLieu[6] = "Hết hạn";
                                    else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp
                                    // ứng.
                                    if (soLuong >= 0 && daDung >= soLuong)
                                        dongDuLieu[6] = "Hết lượt";
                                    else dongDuLieu[6] = "Còn hiệu lực";
                                })
                        .datBoKiemTra(
                                dongDuLieu ->
                                        TienTe.doc(String.valueOf(dongDuLieu[3]))
                                                        < TienTe.doc(String.valueOf(dongDuLieu[4]))
                                                ? "Số lượng không được nhỏ hơn số đã dùng."
                                                : null);
        return cauHinh;
    }

    /**
     * Tạo các trường trong DanhMucVoucherPanel.
     *
     * @return kết quả tạo các trường
     */
    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã voucher", Kieu.VAN_BAN).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Giá trị (₫)", Kieu.TIEN_TE).batBuoc(),
                TruongDuLieu.tao("Hạn dùng", Kieu.NGAY).batBuoc(),
                TruongDuLieu.tao("Số lượng", Kieu.SO_NGUYEN).batBuoc(),
                TruongDuLieu.tao("Đã dùng", Kieu.SO_NGUYEN).chiDoc().datMacDinh("0"),
                TruongDuLieu.tao("Đơn tối thiểu", Kieu.TIEN_TE).datMacDinh("0"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Còn hiệu lực", "Hết hạn", "Hết lượt", "Ngừng"));
    }
}
