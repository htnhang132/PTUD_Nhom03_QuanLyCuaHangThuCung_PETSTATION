package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho nhân viên danh mục vùng hiển thị. */
public final class DanhMucNhanVienPanel extends QuanLyDanhMucPanel {
    private static final DateTimeFormatter DINH_DANG_NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    public DanhMucNhanVienPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.chuanBi(
                        com.petstation.model.LoaiDanhMuc.NHAN_VIEN,
                        !javax.swing.SwingUtilities.isEventDispatchThread()
                                || !com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        CauHinh cauHinh =
                CauHinh.tao("Danh mục nhân viên", "nhân viên")
                        .themBoLoc("Chức vụ", d -> String.valueOf(d[4]))
                        .datTienTo("NV")
                        .timKiem("Tìm theo tên, SĐT, CCCD, mã NV...")
                        .datBoKiemTra(
                                dong -> {
                                    String hoTen = String.valueOf(dong[1]).trim();
                                    if (hoTen.split("\\s+").length < 2
                                            || hoTen.matches(".*\\d.*")) {
                                        return "Họ và tên nhân viên phải gồm ít nhất 2 từ và không chứa chữ số.";
                                    }
                                    String sdt = String.valueOf(dong[2]).replaceAll("[\\s.]", "");
                                    if (!sdt.matches("0\\d{9}")) {
                                        return "Số điện thoại phải gồm đúng 10 chữ số và bắt đầu bằng số 0.";
                                    }
                                    String cccd = String.valueOf(dong[3]).trim();
                                    if (!cccd.matches("\\d{12}")) {
                                        return "Số CCCD phải gồm đúng 12 chữ số.";
                                    }
                                    int maTheKy = cccd.charAt(3) - '0';
                                    int haiSoNam = Integer.parseInt(cccd.substring(4, 6));
                                    int namSinh =
                                            (maTheKy == 0 || maTheKy == 1)
                                                    ? (1900 + haiSoNam)
                                                    : (maTheKy == 2 || maTheKy == 3)
                                                            ? (2000 + haiSoNam)
                                                            : -1;
                                    int namHienTai = LocalDate.now().getYear();
                                    if (namSinh > 0 && (namHienTai - namSinh < 18)) {
                                        return "Nhân viên phải đủ từ 18 tuổi trở lên (theo năm sinh trên CCCD: "
                                                + namSinh
                                                + ").";
                                    }
                                    try {
                                        LocalDate ngayVao =
                                                LocalDate.parse(
                                                        String.valueOf(dong[5]).trim(),
                                                        DINH_DANG_NGAY);
                                        if (ngayVao.isAfter(LocalDate.now())) {
                                            return "Ngày vào làm không được lớn hơn ngày hiện tại.";
                                        }
                                    } catch (Exception ignored) {
                                    }
                                    return null;
                                })
                        .themThaoTacBoSung(
                                "Đặt lại MK",
                                dong -> {
                                    String maNv = String.valueOf(dong[0]);
                                    long idNv =
                                            DanhMucService.layId("nhan_vien", "ma_nhan_vien", maNv);
                                    String tenDangNhap =
                                            DanhMucService.layTenDangNhapTheoNhanVien(idNv);
                                    String passMoi = "PetStation@" + maNv;
                                    String cv = String.valueOf(dong[4]);
                                    String vaiTro =
                                            cv.contains("trưởng")
                                                    ? "QUAN_LY_CUA_HANG"
                                                    : cv.contains("kho")
                                                            ? "NHAN_VIEN_KHO"
                                                            : cv.contains("Spa")
                                                                    ? "NHAN_VIEN_SPA_HOTEL"
                                                                    : "NHAN_VIEN_BAN_HANG";
                                    String tenMoi =
                                            tenDangNhap == null || tenDangNhap.isBlank()
                                                    ? maNv.toLowerCase()
                                                    : tenDangNhap;
                                    DanhMucService.capNhatHoacTaoTaiKhoan(
                                            idNv, tenMoi, passMoi, vaiTro);
                                    return "Đã cấp/đặt lại mật khẩu tài khoản "
                                            + tenMoi
                                            + " thành: "
                                            + passMoi;
                                })
                        .datTrangThai(6, "Đang làm", "Nghỉ việc");

        return cauHinh;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã NV", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Họ và tên", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Số điện thoại", Kieu.SO_DIEN_THOAI).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("CCCD", Kieu.CCCD).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Chức vụ", Kieu.HOP_CHON).batBuoc().datLuaChon(layTenCacVaiTro()),
                TruongDuLieu.tao("Ngày vào làm", Kieu.NGAY)
                        .batBuoc()
                        .datMacDinh(LocalDate.now().format(DINH_DANG_NGAY)),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON).datLuaChon("Đang làm", "Nghỉ việc"));
    }
}
