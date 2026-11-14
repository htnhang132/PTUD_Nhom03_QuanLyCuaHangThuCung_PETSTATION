package com.petstation.gui.khachhang;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BoChonNgay;
import com.petstation.model.chung.Quyen;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.KhachHangService;
import com.petstation.service.khachhang.ThuCungService;
import com.petstation.util.DinhDang;

import java.awt.Component;
import java.awt.Dialog;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

// Màn hình: Hồ sơ thú cưng (Mã: DANH_MUC_THU_CUNG)
// Luồng xử lý: GUI -> KhachHangService -> ThuCungDAOImpl -> SQL (thu_cung, khach_hang)
// Mô tả: Quản lý hồ sơ lý lịch thú cưng (giống loài, độ tuổi, cân nặng, tiền sử bệnh án)
// Phân hệ: khachhang
public final class DanhMucThuCungPanel extends QuanLyDanhMucPanel {
    private final AtomicReference<List<String>> cacheChuNuoi;

    public DanhMucThuCungPanel() {
        this(taoCacheBanDau());
    }

    private DanhMucThuCungPanel(AtomicReference<List<String>> cacheChuNuoi) {
        super(
                taoCauHinh(),
                taoCacTruong(cacheChuNuoi),
                ThuCungService.macDinh()
                        .chuanBi(
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
        this.cacheChuNuoi = cacheChuNuoi;
        if (SwingUtilities.isEventDispatchThread() && DieuKienTaiDuLieu.coTheTachLuong()) {
            napCacheChuNuoiNen();
        }
    }

    private static AtomicReference<List<String>> taoCacheBanDau() {
        if (!SwingUtilities.isEventDispatchThread() || !DieuKienTaiDuLieu.coTheTachLuong()) {
            return new AtomicReference<>(KhachHangService.macDinh().layLuaChonKhachHang());
        }
        return new AtomicReference<>(List.of());
    }

    private void napCacheChuNuoiNen() {
        if (!SwingUtilities.isEventDispatchThread() || !DieuKienTaiDuLieu.coTheTachLuong()) {
            cacheChuNuoi.set(KhachHangService.macDinh().layLuaChonKhachHang());
            lamMoiLuaChonTruong(8);
            return;
        }
        TacVuNen.chay(
                () -> KhachHangService.macDinh().layLuaChonKhachHang(),
                ds -> {
                    cacheChuNuoi.set(ds);
                    lamMoiLuaChonTruong(8);
                },
                ex -> {});
    }

    @Override
    protected void khiLamMoiDuLieu() {
        napCacheChuNuoiNen();
    }

    public static Object[] moThem(Component cha, String maKhachHang) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_THU_CUNG);

        JDialog hopThoai =
                new JDialog(
                        SwingUtilities.getWindowAncestor(cha),
                        "Thêm thú cưng",
                        Dialog.ModalityType.APPLICATION_MODAL);

        Object[][] ketQua = new Object[1][];
        var cacheModal = new AtomicReference<>(KhachHangService.macDinh().layLuaChonKhachHang());
        var cacTruong = taoCacTruong(cacheModal);
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
        hopThoai.setSize(540, 610);
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
        var cacheModal = new AtomicReference<>(KhachHangService.macDinh().layLuaChonKhachHang());
        var cacTruong = taoCacTruong(cacheModal);
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
        hopThoai.setSize(560, 700);
        HopThoai.chuanHoaCuaSo(hopThoai, cha);
        bieuMau.moSuaTheoKhoa(maThuCung);
        hopThoai.setVisible(true);
        return ketQua[0];
    }

    private static CauHinh taoCauHinh() {
        return CauHinh.tao("Hồ sơ thú cưng", "thú cưng")
                .themBoLoc("Loài", d -> String.valueOf(d[2]))
                .themBoLoc("Chủ nuôi", d -> String.valueOf(d[8]))
                .datTienTo("TC")
                .timKiem("Tìm theo tên thú, giống, chủ nuôi...")
                .datTrangThai(13, "Đang hoạt động", "Ngừng hoạt động")
                .datBoKiemTra(DanhMucThuCungPanel::kiemTraHoSoThuCung);
    }

    private static String kiemTraHoSoThuCung(Object[] dongDuLieu) {
        String ngaySinhStr = String.valueOf(dongDuLieu[5]).trim();
        if (!ngaySinhStr.isEmpty()) {
            LocalDate ngaySinh = BoChonNgay.docNgayLinhHoat(ngaySinhStr, DinhDang.NGAY_CHUAN);
            if (ngaySinh != null && ngaySinh.isAfter(LocalDate.now())) {
                return "Ngày sinh thú cưng không được lớn hơn ngày hiện tại.";
            }
        }
        String loiCanNang = DanhMucService.kiemTraCanNangThuCung(String.valueOf(dongDuLieu[6]));
        if (loiCanNang != null) return loiCanNang;
        if (String.valueOf(dongDuLieu[8]).isBlank()) {
            return "Vui lòng chọn chủ nuôi cho thú cưng.";
        }
        return null;
    }

    private static List<TruongDuLieu> taoCacTruong(AtomicReference<List<String>> cacheChuNuoi) {
        return List.of(
                TruongDuLieu.tao("Mã thú cưng", Kieu.VAN_BAN).chiDoc().kiemTraDuyNhat().batBuoc(),
                TruongDuLieu.tao("Tên thú cưng", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Loài", Kieu.HOP_CHON).datLuaChon("Chó", "Mèo").batBuoc(),
                TruongDuLieu.tao("Giống", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Giới tính", Kieu.HOP_CHON).datLuaChon("Đực", "Cái").an(),
                TruongDuLieu.tao("Ngày sinh", Kieu.NGAY).an(),
                TruongDuLieu.tao("Cân nặng (kg)", Kieu.VAN_BAN).sapXepSo().an(),
                TruongDuLieu.tao("Màu lông", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Chủ nuôi", Kieu.HOP_CHON)
                        .coTheTimNhanh()
                        .layDong(cacheChuNuoi::get)
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
