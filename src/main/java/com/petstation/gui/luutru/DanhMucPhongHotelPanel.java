package com.petstation.gui.luutru;

import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DieuKienTaiDuLieu;

import java.util.List;

import javax.swing.SwingUtilities;

// Màn hình: Danh mục phòng Hotel (Mã: DANH_MUC_PHONG_HOTEL)
// Luồng xử lý: GUI -> DanhMucService -> DanhMucDAOImpl -> SQL (phong_hotel)
// Mô tả: Quản lý danh sách phòng, loại phòng, sức chứa, giá niêm yết theo ngày/đêm
// Phân hệ: luutru
public final class DanhMucPhongHotelPanel extends QuanLyDanhMucPanel {
    public DanhMucPhongHotelPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.macDinh()
                        .chuanBi(
                                LoaiDanhMuc.PHONG_LUU_TRU,
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        CauHinh cauHinh =
                CauHinh.tao("Danh mục phòng lưu trú", "phòng")
                        .datTienTo("P")
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
                                giaTri ->
                                        giaTri.equals("Trống")
                                                || giaTri.equals("Bảo trì")
                                                || giaTri.equals("Ngừng hoạt động")));
    }
}
