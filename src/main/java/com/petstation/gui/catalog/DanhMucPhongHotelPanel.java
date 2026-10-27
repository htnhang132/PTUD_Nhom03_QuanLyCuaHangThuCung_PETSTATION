package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho phòng danh mục vùng hiển thị. */
public final class DanhMucPhongHotelPanel extends QuanLyDanhMucPanel {
    public DanhMucPhongHotelPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.chuanBi(
                        com.petstation.model.LoaiDanhMuc.ROOMS,
                        !javax.swing.SwingUtilities.isEventDispatchThread()
                                || !com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục phòng lưu trú", "phòng")
                        .themBoLoc("Loại phòng", d -> String.valueOf(d[1]))
                        .timKiem("Tìm theo mã phòng, loại phòng...")
                        .datTrangThai(5, "Trống", "Ngừng hoạt động")
                        .datDieuKienXoa(
                                dongDuLieu -> {
                                    // Trạng thái.
                                    String trangThai = String.valueOf(dongDuLieu[5]);
                                    return "Có thú".equals(trangThai)
                                                    || "Đang dọn".equals(trangThai)
                                            ? "Phòng "
                                                    + dongDuLieu[0]
                                                    + " đang \""
                                                    + trangThai
                                                    + "\", chưa thể ngừng hoạt động."
                                            : null;
                                });
        return cauHinh;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã phòng", Kieu.VAN_BAN).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Loại phòng", Kieu.HOP_CHON)
                        .batBuoc()
                        .datLuaChon("Tiêu chuẩn", "VIP", "Mèo"),
                TruongDuLieu.tao("Sức chứa (thú)", Kieu.SO_NGUYEN).batBuoc().datMacDinh("1"),
                TruongDuLieu.tao("Giá / ngày", Kieu.TIEN_TE).batBuoc(),
                TruongDuLieu.tao("Ghi chú", Kieu.VAN_BAN),
                TruongDuLieu.tao("Trạng thái (chỉ chỉnh tay Trống / Bảo trì)", Kieu.HOP_CHON)
                        .datLuaChon("Trống", "Bảo trì")
                        .choSuaKhi(
                                giaTri ->
                                        giaTri.equals("Trống")
                                                || giaTri.equals("Bảo trì")
                                                || giaTri.equals("Ngừng hoạt động")));
    }
}
