package com.petstation.gui.catalog;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.service.DanhMucService;

import java.util.List;

public final class DanhMucKhachHangPanel extends QuanLyDanhMucPanel {
        public DanhMucKhachHangPanel() {
                super(
                                taoCauHinh(),
                                taoCacTruong(),
                                DanhMucService.chuanBi(
                                                com.petstation.model.LoaiDanhMuc.KHACH_HANG,
                                                !javax.swing.SwingUtilities.isEventDispatchThread()
                                                                || !com.petstation.service.DieuKienTaiDuLieu
                                                                                .coTheTachLuong()));
        }

        public static Object[] moThem(
                        java.awt.Component cha,
                        String soDienThoai) {
                com.petstation.service.PhienDangNhapService.yeuCau(
                                com.petstation.model.Quyen.QUAN_LY_KHACH_HANG);

                javax.swing.JDialog hopThoai = new javax.swing.JDialog(
                                javax.swing.SwingUtilities.getWindowAncestor(cha),
                                "Thêm khách hàng",
                                java.awt.Dialog.ModalityType.APPLICATION_MODAL);

                Object[][] ketQua = new Object[1][];

                var cacTruong = taoCacTruong();

                cacTruong.get(2).datMacDinh(soDienThoai);

                var cauHinh = taoCauHinh().xuLySauLuu(
                                (truoc, daLuu) -> {
                                        ketQua[0] = daLuu.clone();
                                        hopThoai.dispose();
                                });

                var bieuMau = new QuanLyDanhMucPanel(
                                cauHinh,
                                cacTruong,
                                DanhMucService.layKhachHang());

                hopThoai.setDefaultCloseOperation(
                                javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

                bieuMau.batCheDoThuGon(hopThoai::dispose);
                hopThoai.setContentPane(bieuMau);
                hopThoai.setSize(520, 480);
                com.petstation.gui.components.HopThoai.chuanHoaCuaSo(hopThoai, cha);

                bieuMau.moTaoMoi();
                hopThoai.setVisible(true);

                return ketQua[0];
        }

        private static CauHinh taoCauHinh() {
                CauHinh cauHinh = CauHinh.tao("Danh mục khách hàng", "khách hàng")
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
                                TruongDuLieu.tao("Địa chỉ", Kieu.VAN_BAN).an(),
                                TruongDuLieu.tao("Ghi chú", Kieu.VUNG).an(),
                                TruongDuLieu.tao("Hạng", Kieu.VAN_BAN).chiDoc().datMacDinh("Đồng"),
                                TruongDuLieu.tao("Điểm", Kieu.SO_NGUYEN).chiDoc().datMacDinh("0"),
                                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                                                .datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
        }
}
