package com.petstation.gui.nhansu;

import static com.petstation.model.chung.VaiTro.layCacTenHienThi;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.nhansu.NhanVienService;
import com.petstation.util.DinhDang;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.SwingUtilities;

// Màn hình: Danh sách nhân viên (Mã: DANH_MUC_NHAN_VIEN)
// Luồng xử lý: GUI -> NhanVienService -> NhanVienDAOImpl -> SQL (nhan_vien, tai_khoan)
// Mô tả: Quản lý hồ sơ nhân sự, vị trí chức danh, số điện thoại và trạng thái làm việc
// Phân hệ: nhansu
public final class DanhMucNhanVienPanel extends QuanLyDanhMucPanel {
    private final Map<String, String> nhanTaiKhoan = new HashMap<>();

    @Override
    protected String layNhanThaoTacBoSung(String nhan, Object[] dong) {
        if (!"Đặt lại mật khẩu".equals(nhan) || dong == null || nhanTaiKhoan == null) return nhan;
        String ma = String.valueOf(dong[0]);
        if (!nhanTaiKhoan.containsKey(ma)) {
            nhanTaiKhoan.put(ma, "Cấp hoặc đặt lại tài khoản");
            TacVuNen.chayCoPhien(
                    this,
                    () -> true,
                    () -> NhanVienService.macDinh().layTenDangNhapTheoNhanVien(ma),
                    ten -> {
                        nhanTaiKhoan.put(
                                ma,
                                ten == null || ten.isBlank()
                                        ? "Cấp tài khoản"
                                        : "Đặt lại mật khẩu");
                        capNhatTrangThaiNut();
                    });
        }
        return nhanTaiKhoan.get(ma);
    }

    @Override
    protected void sauThaoTacBoSung(String nhan) {
        nhanTaiKhoan.clear();
    }

    public DanhMucNhanVienPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                NhanVienService.macDinh()
                        .chuanBi(
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        CauHinh cauHinh =
                CauHinh.tao("Danh mục nhân viên", "nhân viên")
                        .themBoLoc("Chức vụ", d -> String.valueOf(d[4]))
                        .datTienTo("NV")
                        .timKiem("Tên, điện thoại, số căn cước hoặc mã nhân viên")
                        .datBoKiemTra(
                                dong ->
                                        NhanVienService.kiemTraNhanVien(
                                                String.valueOf(dong[1]),
                                                String.valueOf(dong[2]),
                                                String.valueOf(dong[3]),
                                                String.valueOf(dong[5])))
                        .themThaoTacBoSungKhongLuu(
                                "Đặt lại mật khẩu",
                                dong -> {
                                    if ("Nghỉ việc".equals(String.valueOf(dong[6]))) {
                                        throw new IllegalStateException(
                                                "Nhân viên đã nghỉ việc, không thể cấp hoặc đặt"
                                                        + " lại mật khẩu.");
                                    }
                                    String maNv = String.valueOf(dong[0]);
                                    Window cha =
                                            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                                    .getActiveWindow();
                                    if (!HopThoai.xacNhan(
                                            cha,
                                            "Xác nhận cấp/đặt lại mật khẩu mặc định cho nhân"
                                                    + " viên "
                                                    + dong[1]
                                                    + " ("
                                                    + maNv
                                                    + ")?")) {
                                        return null;
                                    }
                                    String tenDangNhap =
                                            NhanVienService.macDinh()
                                                    .layTenDangNhapTheoNhanVien(maNv);
                                    String passMoi =
                                            "PS-" + UUID.randomUUID().toString().substring(0, 12);
                                    String vaiTro =
                                            NhanVienService.suyRaVaiTroTuChucVu(
                                                    String.valueOf(dong[4]));
                                    String tenMoi =
                                            tenDangNhap == null || tenDangNhap.isBlank()
                                                    ? maNv.toLowerCase()
                                                    : tenDangNhap;
                                    NhanVienService.macDinh()
                                            .capNhatHoacTaoTaiKhoan(maNv, tenMoi, passMoi, vaiTro);
                                    HopThoai.hienThiMatKhauTam(
                                            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                                    .getActiveWindow(),
                                            tenMoi,
                                            passMoi);
                                    return "Đã cấp/đặt lại mật khẩu cho tài khoản " + tenMoi + ".";
                                })
                        .datTrangThai(6, "Đang làm", "Nghỉ việc");
        return cauHinh;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã NV", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Họ và tên", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Số điện thoại", Kieu.SO_DIEN_THOAI).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("CCCD", Kieu.CCCD).batBuoc().kiemTraDuyNhat().an(),
                TruongDuLieu.tao("Chức vụ", Kieu.HOP_CHON).batBuoc().datLuaChon(layCacTenHienThi()),
                TruongDuLieu.tao("Ngày vào làm", Kieu.NGAY)
                        .batBuoc()
                        .an()
                        .datMacDinh(LocalDate.now().format(DinhDang.NGAY_U)),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON).datLuaChon("Đang làm", "Nghỉ việc"));
    }
}
