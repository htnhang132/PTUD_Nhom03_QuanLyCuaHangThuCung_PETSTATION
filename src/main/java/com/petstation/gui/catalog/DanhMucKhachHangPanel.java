package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho khách hàng danh mục vùng hiển thị. */
public final class DanhMucKhachHangPanel extends QuanLyDanhMucPanel {
    public DanhMucKhachHangPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.chuanBi(
                        com.petstation.model.LoaiDanhMuc.KHACH_HANG,
                        !javax.swing.SwingUtilities.isEventDispatchThread()
                                || !com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()));
    }

    /**
     * Mở biểu mẫu khách hàng hiện có.
     * Trả về khách vừa lưu; trả null nếu đóng cửa sổ.
     */
    public static Object[] moThem(
        java.awt.Component cha,
        String soDienThoai
    ) {
        com.petstation.service.PhienDangNhapService.yeuCau(
            com.petstation.model.Quyen.QUAN_LY_KHACH_HANG
        );

        javax.swing.JDialog hopThoai = new javax.swing.JDialog(
            javax.swing.SwingUtilities.getWindowAncestor(cha),
            "Thêm khách hàng",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL
        );

        Object[][] ketQua = new Object[1][];

        var cacTruong = taoCacTruong();

        // Trường số điện thoại nằm ở vị trí 2
        // trong cấu hình danh mục khách hàng hiện tại.
        cacTruong.get(2).datMacDinh(soDienThoai);

        var cauHinh = taoCauHinh().xuLySauLuu(
            (truoc, daLuu) -> {
                ketQua[0] = daLuu.clone();
                hopThoai.dispose();
            }
        );

        var bieuMau = new QuanLyDanhMucPanel(
            cauHinh,
            cacTruong,
            DanhMucService.layKhachHang()
        );

        hopThoai.setDefaultCloseOperation(
            javax.swing.WindowConstants.DISPOSE_ON_CLOSE
        );

        hopThoai.setContentPane(bieuMau);
        hopThoai.setSize(1000, 680);
        hopThoai.setLocationRelativeTo(null);

        bieuMau.moTaoMoi();
        hopThoai.setVisible(true);

        return ketQua[0];
    }

    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục khách hàng", "khách hàng")
                        .themBoLoc("Hạng thành viên", d -> String.valueOf(d[6]))
                        .datTienTo("KH")
                        .timKiem("Tìm theo tên, SĐT, mã khách...")
                        .datTrangThai(8, "Đang hoạt động", "Ngừng hoạt động");
        return cauHinh;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã khách", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Họ và tên", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Số điện thoại", Kieu.SO_DIEN_THOAI).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Email", Kieu.EMAIL).an(),
                TruongDuLieu.tao("Địa chỉ", Kieu.VAN_BAN),
                TruongDuLieu.tao("Ghi chú", Kieu.VUNG).an(),
                TruongDuLieu.tao("Hạng", Kieu.VAN_BAN).chiDoc().datMacDinh("Đồng"),
                TruongDuLieu.tao("Điểm", Kieu.SO_NGUYEN).chiDoc().datMacDinh("0"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
    }
}
