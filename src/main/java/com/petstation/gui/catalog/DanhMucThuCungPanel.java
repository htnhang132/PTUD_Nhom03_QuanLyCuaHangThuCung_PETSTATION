package com.petstation.gui.catalog;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.service.DanhMucService;

import java.util.List;

public final class DanhMucThuCungPanel extends QuanLyDanhMucPanel {
        public DanhMucThuCungPanel() {
                super(
                                taoCauHinh(),
                                taoCacTruong(),
                                DanhMucService.chuanBi(
                                                com.petstation.model.LoaiDanhMuc.THU_CUNG,
                                                !javax.swing.SwingUtilities.isEventDispatchThread()
                                                                || !com.petstation.service.DieuKienTaiDuLieu
                                                                                .coTheTachLuong()));
        }

        public static Object[] moThem(
                        java.awt.Component cha,
                        String maKhachHang) {
                com.petstation.service.PhienDangNhapService.yeuCau(
                                com.petstation.model.Quyen.QUAN_LY_THU_CUNG);

                javax.swing.JDialog hopThoai = new javax.swing.JDialog(
                                javax.swing.SwingUtilities.getWindowAncestor(cha),
                                "Thêm thú cưng",
                                java.awt.Dialog.ModalityType.APPLICATION_MODAL);

                Object[][] ketQua = new Object[1][];
                var cacTruong = taoCacTruong();
                if (maKhachHang != null && !maKhachHang.isBlank()) {
                        cacTruong.get(8).datMacDinh(maKhachHang);
                }

                var cauHinh = taoCauHinh().xuLySauLuu(
                                (truoc, daLuu) -> {
                                        ketQua[0] = daLuu.clone();
                                        hopThoai.dispose();
                                });

                var bieuMau = new QuanLyDanhMucPanel(
                                cauHinh,
                                cacTruong,
                                DanhMucService.layThuCung());

                hopThoai.setDefaultCloseOperation(
                                javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

                bieuMau.batCheDoThuGon(hopThoai::dispose);
                hopThoai.setContentPane(bieuMau);
                hopThoai.setSize(540, 580);
                com.petstation.gui.components.HopThoai.chuanHoaCuaSo(hopThoai, cha);

                bieuMau.moTaoMoi();
                hopThoai.setVisible(true);

                return ketQua[0];
        }

        private static CauHinh taoCauHinh() {
                CauHinh cauHinh = CauHinh.tao("Hồ sơ thú cưng", "thú cưng")
                                .themBoLoc("Loài", d -> String.valueOf(d[2]))
                                .themBoLoc("Chủ nuôi", d -> String.valueOf(d[8]))
                                .datTienTo("TC")
                                .timKiem("Tìm theo tên thú, giống, chủ nuôi...")
                                .datTrangThai(13, "Đang hoạt động", "Ngừng hoạt động")
                                .datBoKiemTra(
                                                dongDuLieu -> {
                                                        String chieuRong = String.valueOf(dongDuLieu[6]).trim();
                                                        return chieuRong.isEmpty()
                                                                        || chieuRong.matches("\\d{1,3}([.,]\\d{1,2})?")
                                                                                        ? null
                                                                                        : "Cân nặng phải là số (kg), ví dụ 5.2.";
                                                });
                return cauHinh;
        }

        private static List<TruongDuLieu> taoCacTruong() {
                return List.of(
                                TruongDuLieu.tao("Mã thú cưng", Kieu.VAN_BAN).chiDoc().kiemTraDuyNhat().batBuoc(),
                                TruongDuLieu.tao("Tên thú cưng", Kieu.VAN_BAN).batBuoc(),
                                TruongDuLieu.tao("Loài", Kieu.HOP_CHON).datLuaChon("Chó", "Mèo").batBuoc(),
                                TruongDuLieu.tao("Giống", Kieu.VAN_BAN).batBuoc(),
                                TruongDuLieu.tao("Giới tính", Kieu.HOP_CHON).datLuaChon("Đực", "Cái").an(),
                                TruongDuLieu.tao("Ngày sinh", Kieu.NGAY).an(),
                                TruongDuLieu.tao("Cân nặng (kg)", Kieu.VAN_BAN).an(),
                                TruongDuLieu.tao("Màu lông", Kieu.VAN_BAN).an(),
                                TruongDuLieu.tao("Chủ nuôi", Kieu.HOP_CHON).layDong(DanhMucService::layLuaChonKhachHang)
                                                .batBuoc(),
                                TruongDuLieu.tao("Tính trạng sức khỏe", Kieu.VUNG).an(),
                                TruongDuLieu.tao("Dị ứng / Bệnh lý", Kieu.VUNG).an(),
                                TruongDuLieu.tao("Tính cách", Kieu.VUNG).an(),
                                TruongDuLieu.tao("Ảnh đại diện", Kieu.TEP).an(),
                                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON).datLuaChon("Đang hoạt động",
                                                "Ngừng hoạt động"));
        }
}
