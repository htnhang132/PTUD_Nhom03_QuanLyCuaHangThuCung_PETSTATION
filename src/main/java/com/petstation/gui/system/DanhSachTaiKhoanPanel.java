package com.petstation.gui.system;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;
import com.petstation.service.NhatKyHeThongService;

import java.util.List;

public final class DanhSachTaiKhoanPanel extends QuanLyDanhMucPanel {
    public DanhSachTaiKhoanPanel() {
        super(taoCauHinh(), taoCacTruong(), DanhMucService.layTaiKhoan());
    }

    private static CauHinh taoCauHinh() {
        CauHinh cauHinh =
                CauHinh.tao("Tài khoản", "tài khoản")
                        .timKiem("Tìm theo tên đăng nhập, nhân viên...")
                        .datTrangThai(3, "Hoạt động", "Đã khóa")
                        .themThaoTacBoSung(
                                "Khóa / Mở khóa",
                                dongDuLieu -> {
                                    String tenTk = String.valueOf(dongDuLieu[0]);
                                    boolean daKhoa = "Đã khóa".equals(dongDuLieu[3]);
                                    if ("admin".equalsIgnoreCase(tenTk) && !daKhoa)
                                        throw new IllegalArgumentException("Không thể khóa tài khoản admin.");
                                    String tenDangNhapHienTai = com.petstation.service.PhienDangNhapService.layTenDangNhap();
                                    if (!daKhoa && tenDangNhapHienTai != null && tenTk.equalsIgnoreCase(tenDangNhapHienTai))
                                        throw new IllegalArgumentException("Không thể tự khóa tài khoản đang đăng nhập.");
                                    dongDuLieu[3] = daKhoa ? "Hoạt động" : "Đã khóa";
                                    NhatKyHeThongService.ghiNhatKy(
                                            daKhoa ? "Mở khóa" : "Khóa",
                                            "Tài khoản " + tenTk);
                                    return (daKhoa ? "Đã mở khóa " : "Đã khóa ")
                                            + tenTk
                                            + ".";
                                })
                        .themThaoTacBoSung(
                                "Đặt lại mật khẩu",
                                dongDuLieu -> {
                                    String tenTk = String.valueOf(dongDuLieu[0]);
                                    if (!com.petstation.gui.components.HopThoai.xacNhan(
                                            null,
                                            "Bạn có chắc muốn đặt lại mật khẩu cho tài khoản \""
                                                    + tenTk
                                                    + "\" không?")) {
                                        return "Đã hủy đặt lại mật khẩu.";
                                    }
                                    String tam =
                                            "PS-"
                                                    + java.util
                                                            .UUID
                                                            .randomUUID()
                                                            .toString()
                                                            .substring(0, 12);
                                    DanhMucService.datLaiMatKhau(tenTk, tam);
                                    NhatKyHeThongService.ghiNhatKy(
                                            "Đặt lại mật khẩu", "Tài khoản " + tenTk);
                                    com.petstation.gui.components.HopThoai.hienThiThongTin(
                                            null,
                                            "Mật khẩu tạm của "
                                                    + tenTk
                                                    + ": "
                                                    + tam
                                                    + "\n"
                                                    + "Tài khoản phải đổi mật khẩu ở lần đăng nhập"
                                                    + " kế tiếp.");
                                    return "Đã đặt lại mật khẩu cho " + tenTk + ".";
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
