package com.petstation.gui.catalog;

import static com.petstation.gui.components.TruongDanhMuc.*;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh;
import com.petstation.gui.components.QuanLyDanhMucPanel.Kieu;
import com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu;
import com.petstation.service.DanhMucService;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho sản phẩm danh sách vùng hiển thị. */
public final class DanhSachSanPhamPanel extends QuanLyDanhMucPanel {
    public DanhSachSanPhamPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.chuanBi(
                        com.petstation.model.LoaiDanhMuc.PRODUCTS,
                        !javax.swing.SwingUtilities.isEventDispatchThread()
                                || !com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục sản phẩm", "sản phẩm")
                        .themBoLoc("Nhóm hàng", d -> String.valueOf(d[2]))
                        .themBoLoc("Thương hiệu", d -> String.valueOf(d[11]))
                        .themBoLoc(
                                "Tồn kho",
                                d ->
                                        com.petstation.util.TienTe.doc(String.valueOf(d[8])) == 0
                                                ? "Hết hàng"
                                                : com.petstation.util.TienTe.doc(
                                                                        String.valueOf(d[8]))
                                                                <= com.petstation.util.TienTe.doc(
                                                                        String.valueOf(d[6]))
                                                        ? "Dưới tối thiểu"
                                                        : "Còn hàng")
                        .datTienTo("SP")
                        .datTruongDau(10)
                        .themThaoTacBoSung(
                                "Đề xuất giá",
                                dong -> {
                                    javax.swing.JTextField txtGia =
                                            new javax.swing.JTextField(String.valueOf(dong[4]));
                                    javax.swing.JTextField txtLyDo = new javax.swing.JTextField();
                                    if (javax.swing.JOptionPane.showConfirmDialog(
                                                    null,
                                                    new Object[] {
                                                        "Giá đề xuất", txtGia, "Lý do", txtLyDo
                                                    },
                                                    "Đề xuất giá bán",
                                                    javax.swing.JOptionPane.OK_CANCEL_OPTION)
                                            != javax.swing.JOptionPane.OK_OPTION) return null;
                                    var yc =
                                            com.petstation.service.DeXuatGiaService.gui(
                                                    String.valueOf(dong[0]),
                                                    com.petstation.util.TienTe.doc(
                                                            txtGia.getText()),
                                                    txtLyDo.getText());
                                    return "Đã gửi "
                                            + yc.id
                                            + "; giá hiện tại được giữ đến khi quản lý duyệt.";
                                })
                        .themThaoTacBoSung(
                                "Tạo Barcode",
                                dong -> {
                                    String ma = String.valueOf(dong[0]);
                                    String barcodeHienTai =
                                            dong.length > 17 ? String.valueOf(dong[17]).trim() : "";
                                    if (!barcodeHienTai.isBlank()) {
                                        return "Sản phẩm đã có mã vạch: " + barcodeHienTai;
                                    }
                                    String maMoi =
                                            "893"
                                                    + String.format(
                                                            java.util.Locale.ROOT,
                                                            "%09d",
                                                            Math.abs(ma.hashCode() % 1000000000L));
                                    long spId = DanhMucService.layId("san_pham", "ma_san_pham", ma);
                                    DanhMucService.capNhatBarcode(spId, maMoi);
                                    if (dong.length > 17) dong[17] = maMoi;
                                    return "Đã cấp mã vạch mới: " + maMoi;
                                })
                        .datBoKiemTra(
                                dong -> {
                                    long giaBan =
                                            com.petstation.util.TienTe.doc(String.valueOf(dong[4]));
                                    long giaNhap =
                                            com.petstation.util.TienTe.doc(String.valueOf(dong[5]));
                                    if (giaNhap > 0 && giaBan < giaNhap) {
                                        return "Giá bán ("
                                                + com.petstation.util.TienTe.dinhDang(giaBan)
                                                + ") không được thấp hơn giá nhập gần nhất ("
                                                + com.petstation.util.TienTe.dinhDang(giaNhap)
                                                + ").";
                                    }
                                    return null;
                                })
                        .datDieuKienXoa(
                                dong -> {
                                    String ma = String.valueOf(dong[0]);
                                    long spId = DanhMucService.layId("san_pham", "ma_san_pham", ma);
                                    if (DanhMucService.kiemTraSanPhamCoGiaoDich(spId)) {
                                        DanhMucService.ngungKinhDoanhSanPham(spId);
                                        return "Sản phẩm đã có lịch sử giao dịch. Hệ thống"
                                                + " đã tự động chuyển sang 'Ngừng kinh doanh' để"
                                                + " bảo toàn dữ liệu sổ sách.";
                                    }
                                    return null;
                                })
                        .timKiem("Tìm theo mã, tên, mã vạch sản phẩm...")
                        .datTrangThai(9, "Đang kinh doanh", "Ngừng kinh doanh");
        return cauHinh;
    }

    private static TruongDuLieu taoTruongGia() {
        var gia = TruongDuLieu.tao("Giá bán", Kieu.TIEN_TE).batBuoc().datMacDinh("0");
        if (com.petstation.service.PhienDangNhapService.layPhien().docVaiTro()
                != com.petstation.model.VaiTro.STORE_MANAGER) gia.chiDoc();
        return gia;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã SP", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Tên sản phẩm", Kieu.VAN_BAN).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Nhóm hàng", Kieu.HOP_CHON)
                        .batBuoc()
                        .datLuaChon("Thức ăn", "Phụ kiện", "Grooming", "Vệ sinh", "Thuốc"),
                TruongDuLieu.tao("Đơn vị tính", Kieu.VAN_BAN).batBuoc(),
                taoTruongGia(),
                TruongDuLieu.tao("Giá nhập gần nhất", Kieu.TIEN_TE).an(),
                TruongDuLieu.tao("Tồn tối thiểu", Kieu.SO_NGUYEN).batBuoc().an().datMacDinh("5"),
                TruongDuLieu.tao("Quản lý lô / HSD", Kieu.HOP_CHON).datLuaChon("Có", "Không").an(),
                TruongDuLieu.tao("Tồn hiện tại", Kieu.SO_NGUYEN).chiDoc().datMacDinh("0"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang kinh doanh", "Ngừng kinh doanh"),
                TruongDuLieu.tao("Ảnh sản phẩm", Kieu.TUY_CHINH)
                        .an()
                        .datBoSoanThao(com.petstation.gui.components.common.BoChonAnhSanPham::new),
                TruongDuLieu.tao("Thương hiệu", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Quy cách", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Đối tượng sử dụng", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Mô tả", Kieu.VUNG).an(),
                TruongDuLieu.tao("Trang nguồn", Kieu.VAN_BAN).an().chiDoc().anBieuMau(),
                TruongDuLieu.tao("Ngày thu thập", Kieu.VAN_BAN).an().chiDoc().anBieuMau(),
                TruongDuLieu.tao("Mã vạch (Barcode)", Kieu.VAN_BAN).an().datMacDinh(""));
    }
}
