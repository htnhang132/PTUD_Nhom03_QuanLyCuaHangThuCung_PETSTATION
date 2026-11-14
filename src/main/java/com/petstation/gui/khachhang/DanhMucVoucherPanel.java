package com.petstation.gui.khachhang;

import static com.petstation.gui.chung.TruongDanhMuc.docNgay;

import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.model.chung.Quyen;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.TichDiemService;
import com.petstation.service.khachhang.VoucherService;
import com.petstation.util.DinhDang;

import java.time.LocalDate;
import java.util.List;

import javax.swing.SwingUtilities;

// Màn hình con (Tab 2) trong Danh mục > Điểm thưởng và voucher
public final class DanhMucVoucherPanel extends QuanLyDanhMucPanel {
    private final TichDiemService tichDiemService = new TichDiemService();

    // 1. KIỂM TRA PHÂN QUYỀN & RÀNG BUỘC CHỈNH SỬA BẢN GHI VOUCHER

    @Override
    protected boolean coTheChinhSua() {
        return PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.QUAN_LY_KHUYEN_MAI);
    }

    @Override
    protected boolean coTheSuaBanGhi(Object[] dong) {
        return dong != null && tichDiemService.coTheSuaDanhMuc(String.valueOf(dong[0]));
    }

    // 2. KHỞI TẠO MÀN HÌNH & CẤU HÌNH BỘ LỌC / CHUẨN HÓA TRẠNG THÁI VOUCHER

    public DanhMucVoucherPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                VoucherService.macDinh()
                        .chuanBi(
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        CauHinh cauHinh =
                CauHinh.tao("Danh mục voucher", "voucher")
                        .datTienTo("VOUCHER")
                        .themBoLoc("Chủ sở hữu", d -> String.valueOf(d[7]))
                        .themBoLoc("Phạm vi", d -> String.valueOf(d[8]))
                        .timKiem("Tìm theo mã voucher...")
                        .datTrangThai(6, "Còn hiệu lực", "Ngừng")
                        .datBoChuanHoa(
                                dongDuLieu -> {
                                    LocalDate hanDung = docNgay(dongDuLieu[2]);
                                    String trangThai = String.valueOf(dongDuLieu[6]);
                                    long soLuong = DinhDang.doc(String.valueOf(dongDuLieu[3]));
                                    long daDung = DinhDang.doc(String.valueOf(dongDuLieu[4]));
                                    if ("Ngừng".equals(trangThai)) return;
                                    if (hanDung != null && hanDung.isBefore(LocalDate.now()))
                                        dongDuLieu[6] = "Hết hạn";
                                    else if (soLuong >= 0 && daDung >= soLuong)
                                        dongDuLieu[6] = "Hết lượt";
                                    else dongDuLieu[6] = "Còn hiệu lực";
                                })
                        .datBoKiemTra(
                                dongDuLieu ->
                                        VoucherService.kiemTraVoucherSoLuong(
                                                DinhDang.doc(String.valueOf(dongDuLieu[3])),
                                                DinhDang.doc(String.valueOf(dongDuLieu[4]))));
        return cauHinh;
    }

    // 3. ĐỊNH NGHĨA CÁC CỘT BẢNG & TRƯỜNG NHẬP LIỆU VOUCHER

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
                TruongDuLieu.tao("Khách hàng sở hữu", Kieu.TUY_CHINH)
                        .batBuoc()
                        .datBoSoanThao(BoChonNhieuKhachHang::new),
                TruongDuLieu.tao("Phạm vi", Kieu.HOP_CHON).datLuaChon("Chỉ Spa", "Mọi hóa đơn"));
    }
}
