package com.petstation.gui.hethong;

import static com.petstation.gui.chung.TruongDanhMuc.layTenCacVaiTro;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.List;
import java.util.UUID;

public final class DanhSachTaiKhoanPanel extends QuanLyDanhMucPanel {
    public DanhSachTaiKhoanPanel() {
        super(taoCauHinh(), taoCacTruong(), DanhMucService.macDinh().layTaiKhoan());
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
                                        throw new SecurityException(
                                                "Không thể khóa tài khoản admin.");
                                    String tenDangNhapHienTai =
                                            PhienDangNhapService.macDinh().layTenDangNhap();
                                    if (!daKhoa
                                            && tenDangNhapHienTai != null
                                            && tenTk.equalsIgnoreCase(tenDangNhapHienTai))
                                        throw new SecurityException(
                                                "Không thể tự khóa tài khoản đang đăng nhập.");
                                    dongDuLieu[3] = daKhoa ? "Hoạt động" : "Đã khóa";
                                    TacVuNen.chayCoPhien(
                                            null,
                                            () -> true,
                                            () ->
                                                    NhatKyHeThongService.macDinh()
                                                            .ghiNhatKy(
                                                                    daKhoa ? "Mở khóa" : "Khóa",
                                                                    "Tài khoản " + tenTk),
                                            null);
                                    return (daKhoa ? "Đã mở khóa " : "Đã khóa ") + tenTk + ".";
                                })
                        .themThaoTacBoSung(
                                "Đặt lại mật khẩu",
                                dongDuLieu -> {
                                    String tenTk = String.valueOf(dongDuLieu[0]);
                                    if (!HopThoai.xacNhan(
                                            null,
                                            "Bạn có chắc muốn đặt lại mật khẩu cho tài khoản \""
                                                    + tenTk
                                                    + "\" không?")) {
                                        return "Đã hủy đặt lại mật khẩu.";
                                    }
                                    String tam =
                                            "PS-" + UUID.randomUUID().toString().substring(0, 12);
                                    TacVuNen.chayCoPhien(
                                            null,
                                            () -> true,
                                            () -> {
                                                DanhMucService.macDinh().datLaiMatKhau(tenTk, tam);
                                                NhatKyHeThongService.macDinh()
                                                        .ghiNhatKy(
                                                                "Đặt lại mật khẩu",
                                                                "Tài khoản " + tenTk);
                                            },
                                            () ->
                                                    HopThoai.hienThiThongTin(
                                                            null,
                                                            "Mật khẩu tạm của "
                                                                    + tenTk
                                                                    + ": "
                                                                    + tam
                                                                    + "\n"
                                                                    + "Tài khoản phải đổi mật khẩu"
                                                                    + " ở lần đăng nhập kế tiếp."));
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
                        .layDong(() -> DanhMucService.macDinh().layLuaChonNhanVien()),
                TruongDuLieu.tao("Vai trò", Kieu.HOP_CHON).batBuoc().datLuaChon(layTenCacVaiTro()),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON).datLuaChon("Hoạt động", "Đã khóa"),
                TruongDuLieu.tao("Mật khẩu mới (bắt buộc khi thêm tài khoản)", Kieu.MAT_KHAU).an());
    }
}
