package com.petstation.gui.khachhang;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.model.chung.Quyen;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.KhachHangService;
import com.petstation.service.khachhang.ThuCungService;

import java.awt.Component;
import java.awt.Dialog;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

// Màn hình: Hồ sơ thú cưng (Mã: DANH_MUC_THU_CUNG)
// Luồng xử lý: GUI -> KhachHangService -> ThuCungDAOImpl -> SQL (thu_cung, khach_hang)
// Mô tả: Quản lý hồ sơ lý lịch thú cưng (giống loài, độ tuổi, cân nặng, tiền sử bệnh án)
// Phân hệ: khachhang
public final class DanhMucThuCungPanel extends QuanLyDanhMucPanel {
    public DanhMucThuCungPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                ThuCungService.macDinh()
                        .chuanBi(
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
    }

    public static Object[] moThem(Component cha, String maKhachHang) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_THU_CUNG);

        JDialog hopThoai =
                new JDialog(
                        SwingUtilities.getWindowAncestor(cha),
                        "Thêm thú cưng",
                        Dialog.ModalityType.APPLICATION_MODAL);

        Object[][] ketQua = new Object[1][];
        var cacTruong = taoCacTruong();
        if (maKhachHang != null && !maKhachHang.isBlank()) {
            cacTruong.get(8).datMacDinh(maKhachHang);
        }
        var cauHinh =
                taoCauHinh()
                        .xuLySauLuu(
                                (truoc, daLuu) -> {
                                    ketQua[0] = daLuu.clone();
                                    hopThoai.dispose();
                                });
        var bieuMau =
                new QuanLyDanhMucPanel(cauHinh, cacTruong, ThuCungService.macDinh().layDanhMuc());
        hopThoai.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        bieuMau.batCheDoThuGon(hopThoai::dispose);
        hopThoai.setContentPane(bieuMau);
        hopThoai.setSize(540, 580);
        HopThoai.chuanHoaCuaSo(hopThoai, cha);
        bieuMau.moTaoMoi();
        hopThoai.setVisible(true);
        return ketQua[0];
    }

    public static Object[] moSua(Component cha, String maThuCung) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_THU_CUNG);
        JDialog hopThoai =
                new JDialog(
                        SwingUtilities.getWindowAncestor(cha),
                        "Cập nhật hồ sơ & Sổ tiêm thú cưng — " + maThuCung,
                        Dialog.ModalityType.APPLICATION_MODAL);
        Object[][] ketQua = new Object[1][];
        var cacTruong = taoCacTruong();
        var cauHinh =
                taoCauHinh()
                        .xuLySauLuu(
                                (truoc, daLuu) -> {
                                    ketQua[0] = daLuu.clone();
                                    hopThoai.dispose();
                                });
        var bieuMau =
                new QuanLyDanhMucPanel(cauHinh, cacTruong, ThuCungService.macDinh().layDanhMuc());
        hopThoai.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        bieuMau.batCheDoThuGon(hopThoai::dispose);
        hopThoai.setContentPane(bieuMau);
        hopThoai.setSize(560, 680);
        HopThoai.chuanHoaCuaSo(hopThoai, cha);
        bieuMau.moSuaTheoKhoa(maThuCung);
        hopThoai.setVisible(true);
        return ketQua[0];
    }

    private static CauHinh taoCauHinh() {
        CauHinh cauHinh =
                CauHinh.tao("Hồ sơ thú cưng", "thú cưng")
                        .themBoLoc("Loài", d -> String.valueOf(d[2]))
                        .themBoLoc("Chủ nuôi", d -> String.valueOf(d[8]))
                        .datTienTo("TC")
                        .timKiem("Tìm theo tên thú, giống, chủ nuôi...")
                        .datTrangThai(13, "Đang hoạt động", "Ngừng hoạt động")
                        .datBoKiemTra(
                                dongDuLieu ->
                                        ThuCungService.kiemTraCanNangThuCung(
                                                String.valueOf(dongDuLieu[6])));
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
                TruongDuLieu.tao("Chủ nuôi", Kieu.HOP_CHON)
                        .layDong(() -> KhachHangService.macDinh().layLuaChonKhachHang())
                        .batBuoc(),
                TruongDuLieu.tao("Lịch sử tiêm phòng", Kieu.TUY_CHINH)
                        .datBoSoanThao(BoSoanThaoTiemPhong::new)
                        .an(),
                TruongDuLieu.tao("Dị ứng / Tiền sử bệnh lý", Kieu.VUNG).an(),
                TruongDuLieu.tao("Tính cách / Lưu ý chăm sóc", Kieu.VUNG).an(),
                TruongDuLieu.tao("Ảnh đại diện", Kieu.TEP).an(),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
    }
}
