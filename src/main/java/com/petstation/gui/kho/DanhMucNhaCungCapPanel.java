package com.petstation.gui.kho;

import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DieuKienTaiDuLieu;

import java.util.List;

import javax.swing.SwingUtilities;

// Màn hình: Danh mục nhà cung cấp (Mã: DANH_MUC_NHA_CUNG_CAP)
// Luồng xử lý: GUI -> SanPhamService -> DanhMucDAOImpl -> SQL (nha_cung_cap)
// Mô tả: Quản lý danh sách các nhà cung ứng hàng hóa, thông tin liên hệ và địa chỉ
// Phân hệ: kho
public final class DanhMucNhaCungCapPanel extends QuanLyDanhMucPanel {
    public DanhMucNhaCungCapPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.macDinh()
                        .chuanBi(
                                LoaiDanhMuc.NHA_CUNG_CAP,
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        CauHinh cauHinh =
                CauHinh.tao("Danh mục nhà cung cấp", "nhà cung cấp")
                        .datTienTo("NCC")
                        .timKiem("Tìm theo tên, người liên hệ, MST...")
                        .datTrangThai(7, "Đang hợp tác", "Ngừng hợp tác");
        return cauHinh;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã NCC", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Tên nhà cung cấp", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Người liên hệ", Kieu.VAN_BAN),
                TruongDuLieu.tao("Số điện thoại", Kieu.SO_DIEN_THOAI).batBuoc(),
                TruongDuLieu.tao("Địa chỉ", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Mã số thuế", Kieu.VAN_BAN).kiemTraDuyNhat().an(),
                TruongDuLieu.tao("Điều khoản thanh toán", Kieu.HOP_CHON)
                        .an()
                        .datLuaChon("Trả ngay", "15 ngày", "30 ngày", "45 ngày"),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang hợp tác", "Ngừng hợp tác"),
                TruongDuLieu.tao("Nhóm mặt hàng cung cấp", Kieu.VAN_BAN).an(),
                TruongDuLieu.tao("Ghi chú", Kieu.VAN_BAN).an());
    }
}
