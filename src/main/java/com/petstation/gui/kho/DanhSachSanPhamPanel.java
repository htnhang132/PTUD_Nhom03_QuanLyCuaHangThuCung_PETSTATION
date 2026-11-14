package com.petstation.gui.kho;

import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.model.chung.Quyen;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.kho.SanPhamService;
import com.petstation.util.DinhDang;

import java.util.List;

import javax.swing.SwingUtilities;

// Màn hình: Danh mục sản phẩm (Mã: DANH_MUC_SAN_PHAM)
// Luồng xử lý: GUI -> SanPhamService -> SanPhamDAOImpl -> SQL (san_pham, lo_hang)
// Mô tả: Quản lý thông tin chi tiết sản phẩm, mã vạch barcode, giá bán, giá vốn và hạn dùng
// Phân hệ: kho
public class DanhSachSanPhamPanel extends QuanLyDanhMucPanel {
    public DanhSachSanPhamPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.macDinh()
                        .chuanBi(
                                LoaiDanhMuc.SAN_PHAM,
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        CauHinh cauHinh =
                CauHinh.tao("Danh mục sản phẩm", "sản phẩm")
                        .themBoLoc("Nhóm hàng", d -> String.valueOf(d[2]))
                        .themBoLoc("Thương hiệu", d -> String.valueOf(d[11]))
                        .themBoLoc(
                                "Tồn kho",
                                d ->
                                        DinhDang.doc(String.valueOf(d[8])) == 0
                                                ? "Hết hàng"
                                                : DinhDang.doc(String.valueOf(d[8]))
                                                                <= DinhDang.doc(
                                                                        String.valueOf(d[6]))
                                                        ? "Dưới tối thiểu"
                                                        : "Còn hàng")
                        .datTienTo("SP")
                        .datTruongDau(10)
                        .themThaoTacBoSungKhongLuu(
                                "Tạo Barcode",
                                dong -> {
                                    String ma = String.valueOf(dong[0]);
                                    String barcodeHienTai =
                                            dong.length > 13 ? String.valueOf(dong[13]).trim() : "";
                                    if (!barcodeHienTai.isBlank()) {
                                        return "Sản phẩm đã có mã vạch: " + barcodeHienTai;
                                    }
                                    String maMoi = SanPhamService.taoBarcodeEan13(ma);
                                    DanhMucService.macDinh()
                                            .xacNhanTonTai("san_pham", "ma_san_pham", ma);
                                    DanhMucService.macDinh().capNhatBarcode(ma, maMoi);
                                    if (dong.length > 13) dong[13] = maMoi;
                                    return "Đã cấp mã vạch EAN-13 mới: " + maMoi;
                                })
                        .datBoKiemTra(
                                dong -> {
                                    if (DinhDang.doc(String.valueOf(dong[6])) < 0) {
                                        return "Tồn tối thiểu không được âm.";
                                    }
                                    return SanPhamService.kiemTraThongTinSanPham(
                                            DinhDang.doc(String.valueOf(dong[4])),
                                            DinhDang.doc(String.valueOf(dong[5])),
                                            dong.length > 13 ? String.valueOf(dong[13]) : "",
                                            PhienDangNhapService.macDinh()
                                                    .layPhien()
                                                    .kiemTraCo(Quyen.DUYET_GIA));
                                })
                        .timKiem("Tìm mã, tên, mã vạch...")
                        .datTrangThai(9, "Đang kinh doanh", "Ngừng kinh doanh");
        return cauHinh;
    }

    private static TruongDuLieu taoTruongGia() {
        var gia = TruongDuLieu.tao("Giá bán", Kieu.TIEN_TE).batBuoc().datMacDinh("0");
        if (!PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.DUYET_GIA)) {
            gia.chiDocKhiSua();
        }
        return gia;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã SP", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Tên sản phẩm", Kieu.VAN_BAN)
                        .batBuoc()
                        .kiemTraDuyNhat()
                        .nhieuDong(),
                TruongDuLieu.tao("Nhóm hàng", Kieu.HOP_CHON)
                        .batBuoc()
                        .datLuaChon(
                                "Thức ăn",
                                "Phụ kiện",
                                "Chuồng - Nệm",
                                "Vệ sinh",
                                "Grooming",
                                "Thuốc & Sức khỏe"),
                TruongDuLieu.tao("Đơn vị tính", Kieu.VAN_BAN).batBuoc().an(),
                taoTruongGia(),
                TruongDuLieu.tao("Giá nhập gần nhất", Kieu.TIEN_TE).an(),
                TruongDuLieu.tao("Tồn tối thiểu", Kieu.SO_NGUYEN).batBuoc().an().datMacDinh("5"),
                TruongDuLieu.tao("Quản lý lô / HSD", Kieu.HOP_CHON).datLuaChon("Có", "Không").an(),
                TruongDuLieu.tao("Tồn kho", Kieu.SO_NGUYEN).chiDoc().datMacDinh("0"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang kinh doanh", "Ngừng kinh doanh"),
                TruongDuLieu.tao("Ảnh sản phẩm", Kieu.TUY_CHINH)
                        .an()
                        .anNhan()
                        .datBoSoanThao(BoChonAnhSanPham::new),
                TruongDuLieu.tao("Thương hiệu", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Mô tả", Kieu.VUNG).an(),
                TruongDuLieu.tao("Mã vạch (Barcode)", Kieu.VAN_BAN)
                        .an()
                        .kiemTraDuyNhat()
                        .datMacDinh(""));
    }
}
