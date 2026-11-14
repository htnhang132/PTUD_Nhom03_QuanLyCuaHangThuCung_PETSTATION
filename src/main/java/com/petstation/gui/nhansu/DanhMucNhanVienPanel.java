package com.petstation.gui.nhansu;

import static com.petstation.gui.chung.TruongDanhMuc.layTenCacVaiTro;

import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.nhansu.NhanVienService;
import com.petstation.util.DinhDang;

import java.time.LocalDate;
import java.util.List;

import javax.swing.SwingUtilities;

// Màn hình: Danh sách nhân viên (Mã: DANH_MUC_NHAN_VIEN)
// Luồng xử lý: GUI -> NhanVienService -> NhanVienDAOImpl -> SQL (nhan_vien, tai_khoan)
// Mô tả: Quản lý hồ sơ nhân sự, vị trí chức danh, số điện thoại và trạng thái làm việc
// Phân hệ: nhansu
public final class DanhMucNhanVienPanel extends QuanLyDanhMucPanel {

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
                        .timKiem("Tìm theo tên, SĐT, CCCD, mã NV...")
                        .datBoKiemTra(
                                dong ->
                                        NhanVienService.kiemTraNhanVien(
                                                String.valueOf(dong[1]),
                                                String.valueOf(dong[2]),
                                                String.valueOf(dong[3]),
                                                String.valueOf(dong[5])))
                        .themThaoTacBoSung(
                                "Đặt lại MK",
                                dong -> {
                                    String maNv = String.valueOf(dong[0]);
                                    String tenDangNhap =
                                            NhanVienService.macDinh()
                                                    .layTenDangNhapTheoNhanVien(maNv);
                                    String passMoi = "PetStation@" + maNv;
                                    String vaiTro =
                                            NhanVienService.suyRaVaiTroTuChucVu(
                                                    String.valueOf(dong[4]));
                                    String tenMoi =
                                            tenDangNhap == null || tenDangNhap.isBlank()
                                                    ? maNv.toLowerCase()
                                                    : tenDangNhap;
                                    NhanVienService.macDinh()
                                            .capNhatHoacTaoTaiKhoan(maNv, tenMoi, passMoi, vaiTro);
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
                TruongDuLieu.tao("CCCD", Kieu.CCCD).batBuoc().kiemTraDuyNhat().an(),
                TruongDuLieu.tao("Chức vụ", Kieu.HOP_CHON).batBuoc().datLuaChon(layTenCacVaiTro()),
                TruongDuLieu.tao("Ngày vào làm", Kieu.NGAY)
                        .batBuoc()
                        .an()
                        .datMacDinh(LocalDate.now().format(DinhDang.NGAY_U)),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON).datLuaChon("Đang làm", "Nghỉ việc"));
    }
}
