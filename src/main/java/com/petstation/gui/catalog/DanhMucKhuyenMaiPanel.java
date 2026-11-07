package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.docNgay;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.service.DanhMucService;
import com.petstation.util.TienTe;

import java.time.LocalDate;
import java.util.List;

public final class DanhMucKhuyenMaiPanel extends QuanLyDanhMucPanel {
    public DanhMucKhuyenMaiPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.chuanBi(
                        com.petstation.model.LoaiDanhMuc.KHUYEN_MAI,
                        !javax.swing.SwingUtilities.isEventDispatchThread()
                                || !com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()));
        ganHauToTheoTruong(2, 4, loai -> "Giảm %".equals(loai) ? "%" : "đ");
    }

    private static CauHinh taoCauHinh() {
        CauHinh cauHinh = CauHinh.tao("Danh mục khuyến mãi", "khuyến mãi")
                .themBoLoc("Loại giảm", d -> String.valueOf(d[2]))
                .themBoLoc("Phạm vi", d -> String.valueOf(d[3]))
                .themBoLoc(
                        "Hiệu lực",
                        d -> {
                            LocalDate tu = docNgay(d[6]), den = docNgay(d[7]);
                            if ("Ngừng áp dụng".equals(d[8]))
                                return "Đã tắt";
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
                            LocalDate tu = docNgay(dongDuLieu[6]);

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
                TruongDuLieu.tao("Giá trị", Kieu.SO_NGUYEN)
                        .batBuoc()
                        .hienThi(
                                v -> {
                                    if (v == null || v.isBlank())
                                        return "—";
                                    long so = TienTe.doc(v);
                                    return so <= 100 ? so + "%" : TienTe.dinhDang(so);
                                }),
                TruongDuLieu.tao("Đơn tối thiểu", Kieu.TIEN_TE).an().datMacDinh("0"),
                TruongDuLieu.tao("Từ ngày", Kieu.NGAY).batBuoc().an(),
                TruongDuLieu.tao("Đến ngày", Kieu.NGAY).batBuoc().an(),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Chưa áp dụng", "Đang áp dụng", "Ngừng áp dụng"));
    }
}
