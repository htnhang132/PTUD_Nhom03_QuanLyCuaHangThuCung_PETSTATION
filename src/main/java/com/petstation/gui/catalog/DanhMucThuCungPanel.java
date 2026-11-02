package com.petstation.gui.catalog;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho thú cưng danh mục vùng hiển thị. */
public final class DanhMucThuCungPanel extends QuanLyDanhMucPanel {
    public DanhMucThuCungPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.chuanBi(
                        com.petstation.model.LoaiDanhMuc.THU_CUNG,
                        !javax.swing.SwingUtilities.isEventDispatchThread()
                                || !com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        CauHinh cauHinh =
                CauHinh.tao("Hồ sơ thú cưng", "thú cưng")
                        .themBoLoc("Loài", d -> String.valueOf(d[2]))
                        .themBoLoc("Chủ nuôi", d -> String.valueOf(d[8]))
                        .datTienTo("TC")
                        .timKiem("Tìm theo tên thú, giống, chủ nuôi...")
                        .datTrangThai(13, "Đang hoạt động", "Ngừng hoạt động")
                        .datBoKiemTra(
                                dongDuLieu -> {
                                    String chieuRong = String.valueOf(dongDuLieu[6]).trim();
                                    return chieuRong.isEmpty()
                                                    || chieuRong.matches("\\d{1,3}([.,]\\d{1,2})?")
                                            ? null
                                            : "Cân nặng phải là số (kg), ví dụ 5.2.";
                                });
        return cauHinh;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã thú cưng", Kieu.VAN_BAN).chiDoc().kiemTraDuyNhat().batBuoc(),
                TruongDuLieu.tao("Tên thú cưng", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Loài", Kieu.HOP_CHON).datLuaChon("Chó", "Mèo").batBuoc(),
                TruongDuLieu.tao("Giống", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Giới tính", Kieu.HOP_CHON).datLuaChon("Đực", "Cái"),
                TruongDuLieu.tao("Ngày sinh", Kieu.NGAY),
                TruongDuLieu.tao("Cân nặng (kg)", Kieu.VAN_BAN),
                TruongDuLieu.tao("Màu lông", Kieu.VAN_BAN),
                TruongDuLieu.tao("Chủ nuôi", Kieu.HOP_CHON).layDong(DanhMucService::layLuaChonKhachHang).batBuoc(),
                TruongDuLieu.tao("Tính trạng sức khỏe", Kieu.VUNG),
                TruongDuLieu.tao("Dị ứng / Bệnh lý", Kieu.VUNG),
                TruongDuLieu.tao("Tính cách", Kieu.VUNG),
                TruongDuLieu.tao("Ảnh đại diện", Kieu.TEP).an(),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON).datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
    }
}
