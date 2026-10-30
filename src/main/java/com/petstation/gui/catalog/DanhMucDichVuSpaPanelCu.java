package com.petstation.gui.catalog;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.common.BoSoanThaoBangGia;
import com.petstation.service.DanhMucService;
import com.petstation.util.TienTe;

import java.util.List;

/** Cung cấp giao diện và xử lý tương tác cho dịch vụ Spa danh mục vùng hiển thị. */
public final class DanhMucDichVuSpaPanelCu extends QuanLyDanhMucPanel {
    public DanhMucDichVuSpaPanelCu() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                DanhMucService.chuanBi(
                        com.petstation.model.LoaiDanhMuc.DICH_VU_SPA,
                        !javax.swing.SwingUtilities.isEventDispatchThread()
                                || !com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh taoCauHinh() {
        // Cấu hình.
        CauHinh cauHinh =
                CauHinh.tao("Danh mục dịch vụ Spa", "dịch vụ Spa")
                        .datTienTo("DV")
                        .timKiem("Tìm theo mã, tên dịch vụ...")
                        .datTrangThai(5, "Đang hoạt động", "Ngừng hoạt động")
                        .datBoKiemTra(
                                dongDuLieu ->
                                        BoSoanThaoBangGia.layGiaNhoNhat(
                                                                String.valueOf(dongDuLieu[4]))
                                                        < 0
                                                ? "Bảng giá cần ít nhất 1 dòng có giá."
                                                : null);
        return cauHinh;
    }

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã DV", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Tên dịch vụ", Kieu.VAN_BAN).batBuoc().kiemTraDuyNhat(),
                TruongDuLieu.tao("Mô tả", Kieu.VUNG).an(),
                TruongDuLieu.tao("Thời gian dự kiến (phút)", Kieu.SO_NGUYEN).batBuoc(),
                TruongDuLieu.tao("Bảng giá theo loài / cân nặng", Kieu.TUY_CHINH)
                        .datBoSoanThao(BoSoanThaoBangGia::new)
                        .hienThi(
                                giaTri -> {
                                    // Mô hình đang xét.
                                    long moHinhDangXet = BoSoanThaoBangGia.layGiaNhoNhat(giaTri);
                                    return moHinhDangXet < 0
                                            ? "—"
                                            : "Từ " + TienTe.dinhDang(moHinhDangXet);
                                }),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
    }
}
