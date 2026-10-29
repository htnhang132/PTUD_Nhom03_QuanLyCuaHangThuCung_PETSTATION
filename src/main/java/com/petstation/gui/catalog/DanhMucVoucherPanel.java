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
    @Override
    protected boolean coTheChinhSua() {
        return com.petstation.service.PhienDangNhapService.layPhien()
                .kiemTraCo(com.petstation.model.Quyen.QUAN_LY_KHUYEN_MAI);
    }

    @Override
    protected boolean coTheSuaBanGhi(Object[] dong) {
        return dong != null
                && com.petstation.service.TichDiemService.coTheSuaDanhMuc(String.valueOf(dong[0]));
    }

    public DanhMucVoucherPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.chuanBi(
                        com.petstation.model.LoaiDanhMuc.VOUCHER,
                        !javax.swing.SwingUtilities.isEventDispatchThread()
                                || !com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục voucher", "voucher")
                        .themBoLoc("Chủ sở hữu", d -> String.valueOf(d[7]))
                        .themBoLoc("Phạm vi", d -> String.valueOf(d[8]))
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
                                    if ("Ngừng".equals(trangThai)) return;
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

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã voucher", Kieu.VAN_BAN).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Giá trị (₫)", Kieu.TIEN_TE).batBuoc(),
                TruongDuLieu.tao("Hạn dùng", Kieu.NGAY).batBuoc(),
                TruongDuLieu.tao("Số lượt (voucher cá nhân: 1)", Kieu.SO_NGUYEN)
                        .batBuoc()
                        .datMacDinh("1"),
                TruongDuLieu.tao("Đã dùng", Kieu.SO_NGUYEN).chiDoc().datMacDinh("0"),
                TruongDuLieu.tao("Đơn tối thiểu", Kieu.TIEN_TE).datMacDinh("0"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Còn hiệu lực", "Hết hạn", "Hết lượt", "Ngừng"),
                TruongDuLieu.tao("Khách hàng sở hữu", Kieu.HOP_CHON)
                        .layDong(DanhMucService::layLuaChonKhachHang),
                TruongDuLieu.tao("Phạm vi", Kieu.HOP_CHON).datLuaChon("Chỉ Spa", "Mọi hóa đơn"));
    }
}
