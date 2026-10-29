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
    public DanhMucKhuyenMaiPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.chuanBi(
                        com.petstation.model.LoaiDanhMuc.KHUYEN_MAI,
                        !javax.swing.SwingUtilities.isEventDispatchThread()
                                || !com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục khuyến mãi", "khuyến mãi")
                        .themBoLoc("Loại giảm", d -> String.valueOf(d[2]))
                        .themBoLoc("Phạm vi", d -> String.valueOf(d[3]))
                        .themBoLoc(
                                "Hiệu lực",
                                d -> {
                                    LocalDate tu = docNgay(d[6]), den = docNgay(d[7]);
                                    if ("Ngừng áp dụng".equals(d[8])) return "Đã tắt";
                                    if (den != null && den.isBefore(LocalDate.now()))
                                        return "Hết hạn";
                                    if (tu != null && tu.isAfter(LocalDate.now()))
                                        return "Chưa đến ngày";
                                    return "Trong thời gian áp dụng";
                                })
                        .datTienTo("KM")
                        .timKiem("Tìm theo mã, tên chương trình...")
                        .datTrangThai(8, "Đang áp dụng", "Ngừng áp dụng")
                        .datBoKiemTra(
                                dongDuLieu -> {
                                    // Từ.
                                    LocalDate tu = docNgay(dongDuLieu[6]);
                                    // Đến.
                                    LocalDate den = docNgay(dongDuLieu[7]);
                                    if (tu != null && den != null && den.isBefore(tu))
                                        return "Đến ngày phải sau hoặc bằng Từ ngày.";
                                    if ("Giảm %".equals(dongDuLieu[2])) {
                                        long giaTri = TienTe.doc(String.valueOf(dongDuLieu[4]));
                                        if (giaTri < 1 || giaTri > 100)
                                            return "Giá trị giảm % phải từ 1 đến 100.";
                                    }
                                    return null;
                                });
        return cauHinh;
    }

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
