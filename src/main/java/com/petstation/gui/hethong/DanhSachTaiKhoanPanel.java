package com.petstation.gui.hethong;

import static com.petstation.gui.chung.TruongDanhMuc.layTenCacVaiTro;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.awt.Component;
import java.util.List;
import java.util.UUID;

import javax.swing.SwingUtilities;

public final class DanhSachTaiKhoanPanel extends QuanLyDanhMucPanel {
    public DanhSachTaiKhoanPanel() {
        this(new Component[1]);
    }

    private DanhSachTaiKhoanPanel(Component[] chaRef) {
        super(
                taoCauHinh(chaRef),
                taoCacTruong(),
                DanhMucService.macDinh()
                        .chuanBi(
                                LoaiDanhMuc.TAI_KHOAN,
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
        chaRef[0] = this;
    }

    private static CauHinh taoCauHinh(Component[] chaRef) {
        CauHinh cauHinh =
                CauHinh.tao("Tài khoản", "tài khoản")
                        .timKiem("Tìm theo tên đăng nhập, nhân viên...")
                        .datTrangThai(3, "Hoạt động", "Đã khóa")
                        .datDieuKienXoa(
                                dongDuLieu ->
                                        "admin".equalsIgnoreCase(String.valueOf(dongDuLieu[0]))
                                                ? "Không thể xóa tài khoản quản trị mặc định"
                                                        + " (admin)."
                                                : null)
                        .themThaoTacBoSung(
                                "Khóa / Mở khóa",
                                dongDuLieu -> {
                                    String tenTk = String.valueOf(dongDuLieu[0]);
                                    if ("admin".equalsIgnoreCase(tenTk)) {
                                        throw new SecurityException(
                                                "Không thể khóa hoặc thay đổi trạng thái tài"
                                                        + " khoản admin.");
                                    }
                                    boolean daKhoa = "Đã khóa".equals(dongDuLieu[3]);
                                    String tenDangNhapHienTai =
                                            PhienDangNhapService.macDinh().layTenDangNhap();
                                    if (!daKhoa
                                            && tenDangNhapHienTai != null
                                            && tenTk.equalsIgnoreCase(tenDangNhapHienTai))
                                        throw new SecurityException(
                                                "Không thể tự khóa tài khoản đang đăng nhập.");
                                    dongDuLieu[3] = daKhoa ? "Hoạt động" : "Đã khóa";
                                    return (daKhoa ? "Đã mở khóa " : "Đã khóa ") + tenTk + ".";
                                })
                        .themThaoTacBoSung(
                                "Đặt lại mật khẩu",
                                dongDuLieu -> datLaiMatKhau(chaRef[0], dongDuLieu));
        return cauHinh;
    }

    private static String datLaiMatKhau(Component cha, Object[] dongDuLieu) {
        String tenTk = String.valueOf(dongDuLieu[0]);
        if (!HopThoai.xacNhan(
                cha, "Bạn có chắc muốn đặt lại mật khẩu cho tài khoản \"" + tenTk + "\" không?")) {
            return null;
        }
        String tam = "PS-" + UUID.randomUUID().toString().substring(0, 12);
        TacVuNen.chayCoPhien(
                cha,
                () -> true,
                () -> {
                    DanhMucService.macDinh().datLaiMatKhau(tenTk, tam);
                    NhatKyHeThongService.macDinh()
                            .ghiNhatKy("Đặt lại mật khẩu", "Tài khoản " + tenTk);
                },
                () ->
                        HopThoai.hienThiThongTin(
                                cha,
                                "Đã đặt lại mật khẩu cho "
                                        + tenTk
                                        + ".\n"
                                        + "Mật khẩu tạm của "
                                        + tenTk
                                        + ": "
                                        + tam
                                        + "\n"
                                        + "Tài khoản phải đổi mật khẩu ở lần đăng nhập kế tiếp."));
        return null;
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
                TruongDuLieu.tao(
                                "Mật khẩu mới (tối thiểu 8 ký tự, bắt buộc khi thêm tài khoản)",
                                Kieu.MAT_KHAU)
                        .an());
    }
}
