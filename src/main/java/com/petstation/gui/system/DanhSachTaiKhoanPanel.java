package com.petstation.gui.system;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;
import com.petstation.service.NhatKyHeThongService;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho tài khoản danh sách vùng hiển thị. */
public final class DanhSachTaiKhoanPanel extends QuanLyDanhMucPanel {
    public DanhSachTaiKhoanPanel() {
        super(taoCauHinh(), taoCacTruong(), DanhMucService.layTaiKhoan());
    }

    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Tài khoản", "tài khoản")
                        .timKiem("Tìm theo tên đăng nhập, nhân viên...")
                        .datTrangThai(3, "Hoạt động", "Đã khóa")
                        .themThaoTacBoSung(
                                "Khóa / Mở khóa",
                                dongDuLieu -> {
                                    // Đã khóa.
                                    boolean daKhoa = "Đã khóa".equals(dongDuLieu[3]);
                                    if ("admin".equals(dongDuLieu[0]) && !daKhoa)
                                        return "Không thể khóa tài khoản admin.";
                                    dongDuLieu[3] = daKhoa ? "Hoạt động" : "Đã khóa";
                                    NhatKyHeThongService.ghiNhatKy(
                                            daKhoa ? "Mở khóa" : "Khóa",
                                            "Tài khoản " + dongDuLieu[0]);
                                    return (daKhoa ? "Đã mở khóa " : "Đã khóa ")
                                            + dongDuLieu[0]
                                            + ".";
                                })
                        .themThaoTacBoSung(
                                "Đặt lại mật khẩu",
                                dongDuLieu -> {
                                    // Tạm.
                                    String tam =
                                            "PS-"
                                                    + java.util
                                                            .UUID
                                                            .randomUUID()
                                                            .toString()
                                                            .substring(0, 12);
                                    DanhMucService.datLaiMatKhau(
                                            String.valueOf(dongDuLieu[0]), tam);
                                    NhatKyHeThongService.ghiNhatKy(
                                            "Đặt lại mật khẩu", "Tài khoản " + dongDuLieu[0]);
                                    com.petstation.gui.components.HopThoai.hienThiThongTin(
                                            null,
                                            "Mật khẩu tạm của "
                                                    + dongDuLieu[0]
                                                    + ": "
                                                    + tam
                                                    + "\n"
                                                    + "Tài khoản phải đổi mật khẩu ở lần đăng nhập"
                                                    + " kế tiếp.");
                                    return "Đã đặt lại mật khẩu cho " + dongDuLieu[0] + ".";
                                });
        return cauHinh;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Tên đăng nhập", Kieu.VAN_BAN).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Nhân viên", Kieu.HOP_CHON)
                        .batBuoc()
                        .kiemTraDuyNhat()
                        .layDong(DanhMucService::layLuaChonNhanVien),
                TruongDuLieu.tao("Vai trò", Kieu.HOP_CHON).batBuoc().datLuaChon(layTenCacVaiTro()),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON).datLuaChon("Hoạt động", "Đã khóa"),
                TruongDuLieu.tao("Mật khẩu mới (bắt buộc khi thêm tài khoản)", Kieu.MAT_KHAU).an());
    }
}
