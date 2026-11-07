package com.petstation.gui.catalog;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.service.DanhMucService;

import java.util.List;

public final class DanhMucPhongHotelPanel extends QuanLyDanhMucPanel {
        public DanhMucPhongHotelPanel() {
                super(
                                taoCauHinh(),
                                taoCacTruong(),
                                DanhMucService.chuanBi(
                                                com.petstation.model.LoaiDanhMuc.PHONG_LUU_TRU,
                                                !javax.swing.SwingUtilities.isEventDispatchThread()
                                                                || !com.petstation.service.DieuKienTaiDuLieu
                                                                                .coTheTachLuong()));
        }

        private static CauHinh taoCauHinh() {
                CauHinh cauHinh = CauHinh.tao("Danh mục phòng lưu trú", "phòng")
                                .themBoLoc("Loại phòng", d -> String.valueOf(d[1]))
                                .timKiem("Tìm theo mã phòng, loại phòng...")
                                .datTrangThai(5, "Trống", "Ngừng hoạt động")
                                .datDieuKienXoa(
                                                dongDuLieu -> {
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
                                                .datLuaChon("Tiêu chuẩn", "VIP", "Penthouse", "Mèo"),
                                TruongDuLieu.tao("Sức chứa (thú)", Kieu.SO_NGUYEN).batBuoc().datMacDinh("1"),
                                TruongDuLieu.tao("Giá / ngày", Kieu.TIEN_TE).batBuoc(),
                                TruongDuLieu.tao("Ghi chú", Kieu.VAN_BAN).an(),
                                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                                                .datLuaChon("Trống", "Bảo trì")
                                                .choSuaKhi(
                                                                giaTri -> giaTri.equals("Trống")
                                                                                || giaTri.equals("Bảo trì")
                                                                                || giaTri.equals("Ngừng hoạt động")));
        }
}
