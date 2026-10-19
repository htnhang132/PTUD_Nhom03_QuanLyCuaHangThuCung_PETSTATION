package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho thú cưng danh mục vùng hiển thị. */
public final class DanhMucThuCungPanel extends QuanLyDanhMucPanel {
    /** Khởi tạo DanhMucThuCungPanel với các giá trị được cung cấp. */
    public DanhMucThuCungPanel() {
        super(taoCauHinh(), taoCacTruong(), DanhMucService.layThuCung());
    }

    /**
     * Tạo cấu hình trong DanhMucThuCungPanel.
     *
     * @return kết quả tạo cấu hình
     */
    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Hồ sơ thú cưng", "thú cưng")
                        .datTienTo("TC")
                        .timKiem("Tìm theo tên thú, giống, chủ nuôi...")
                        .datTrangThai(13, "Đang hoạt động", "Ngừng hoạt động")
                        .datBoKiemTra(
                                dongDuLieu -> {
                                    // Chiều rộng.
                                    String chieuRong = String.valueOf(dongDuLieu[6]).trim();
                                    return chieuRong.isEmpty()
                                                    || chieuRong.matches("\\d{1,3}([.,]\\d{1,2})?")
                                            ? null
                                            : "Cân nặng phải là số (kg), ví dụ 5.2.";
                                });
        return cauHinh;
    }

    /**
     * Tạo các trường trong DanhMucThuCungPanel.
     *
     * @return kết quả tạo các trường
     */
    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã thú", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Tên thú cưng", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Loài", Kieu.HOP_CHON).batBuoc().datLuaChon("Chó", "Mèo"),
                TruongDuLieu.tao("Giống", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Giới tính", Kieu.HOP_CHON).datLuaChon("Đực", "Cái").an(),
                TruongDuLieu.tao("Ngày sinh", Kieu.NGAY).an(),
                TruongDuLieu.tao("Cân nặng (kg)", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Màu lông", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Chủ nuôi", Kieu.HOP_CHON)
                        .batBuoc()
                        .layDong(DanhMucService::layLuaChonKhachHang),
                TruongDuLieu.tao("Tiêm chủng", Kieu.VUNG).an(),
                TruongDuLieu.tao("Dị ứng / lưu ý sức khỏe", Kieu.VUNG).an(),
                TruongDuLieu.tao("Tính cách", Kieu.VUNG).an(),
                TruongDuLieu.tao("Ảnh", Kieu.TEP).an(),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
    }
}
