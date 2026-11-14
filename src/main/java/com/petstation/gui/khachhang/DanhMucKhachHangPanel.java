package com.petstation.gui.khachhang;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.model.chung.Quyen;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.KhachHangService;

import java.awt.Component;
import java.awt.Dialog;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

// Màn hình: Quản lý khách hàng (Mã: DANH_MUC_KHACH_HANG)
// Luồng xử lý: GUI -> KhachHangService -> KhachHangDAOImpl -> SQL (khach_hang)
// Mô tả: Quản lý thông tin định danh khách hàng, số điện thoại, hạng thành viên và điểm tích lũy
// Phân hệ: khachhang
public final class DanhMucKhachHangPanel extends QuanLyDanhMucPanel {
    public DanhMucKhachHangPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                KhachHangService.macDinh()
                        .chuanBi(
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
    }

    public static Object[] moThem(Component cha, String soDienThoai) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_KHACH_HANG);
        JDialog hopThoai =
                new JDialog(
                        SwingUtilities.getWindowAncestor(cha),
                        "Thêm khách hàng",
                        Dialog.ModalityType.APPLICATION_MODAL);
        Object[][] ketQua = new Object[1][];
        var cacTruong = taoCacTruong();
        cacTruong.get(2).datMacDinh(soDienThoai);
        var cauHinh =
                taoCauHinh()
                        .xuLySauLuu(
                                (truoc, daLuu) -> {
                                    ketQua[0] = daLuu.clone();
                                    hopThoai.dispose();
                                });
        var bieuMau =
                new QuanLyDanhMucPanel(cauHinh, cacTruong, KhachHangService.macDinh().layDanhMuc());
        hopThoai.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        bieuMau.batCheDoThuGon(hopThoai::dispose);
        hopThoai.setContentPane(bieuMau);
        hopThoai.setSize(520, 480);
        HopThoai.chuanHoaCuaSo(hopThoai, cha);
        bieuMau.moTaoMoi();
        hopThoai.setVisible(true);
        return ketQua[0];
    }

    private static CauHinh taoCauHinh() {
        return CauHinh.tao("Danh mục khách hàng", "khách hàng")
                .themBoLoc("Hạng thành viên", d -> String.valueOf(d[6]))
                .datTienTo("KH")
                .timKiem("Tìm theo tên, SĐT, Email, Địa chỉ, mã khách...")
                .datTrangThai(8, "Đang hoạt động", "Ngừng hoạt động")
                .datBoKiemTra(
                        d -> {
                            String ten = d[1] == null ? "" : d[1].toString().trim();
                            if (ten.length() < 2 || ten.matches("\\d+")) {
                                return "Họ và tên phải có ít nhất 2 ký tự và không chỉ gồm chữ"
                                        + " số.";
                            }
                            return null;
                        })
                .datDieuKienXoa(
                        d ->
                                DanhMucService.macDinh()
                                        .kiemTraDieuKienXoaKhachHang(String.valueOf(d[0])));
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã khách", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Họ và tên", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Số điện thoại", Kieu.SO_DIEN_THOAI).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Email", Kieu.EMAIL).an(),
                TruongDuLieu.tao("Địa chỉ", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Ghi chú", Kieu.VUNG).an(),
                TruongDuLieu.tao("Hạng", Kieu.VAN_BAN).chiDoc().datMacDinh("Đồng"),
                TruongDuLieu.tao("Điểm", Kieu.SO_NGUYEN).chiDoc().datMacDinh("0"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
    }
}
