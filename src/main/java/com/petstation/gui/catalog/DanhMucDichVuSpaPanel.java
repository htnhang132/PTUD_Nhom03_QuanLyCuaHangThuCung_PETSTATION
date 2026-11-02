package com.petstation.gui.catalog;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.common.BoSoanThaoBangGia;
import com.petstation.model.LoaiDanhMuc;
import com.petstation.service.DanhMucService;
import com.petstation.service.DieuKienTaiDuLieu;
import com.petstation.util.TienTe;

import java.util.List;

import javax.swing.SwingUtilities;

/** Danh mục Spa: dùng chung giao diện, CRUD và kết nối dữ liệu của nhóm. */
public final class DanhMucDichVuSpaPanel extends QuanLyDanhMucPanel {
    public DanhMucDichVuSpaPanel() {
        super(
                cauHinh(),
                cacTruong(),
                DanhMucService.chuanBi(
                        LoaiDanhMuc.DICH_VU_SPA,
                        !SwingUtilities.isEventDispatchThread()
                                || !DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh cauHinh() {
        return CauHinh.tao("Danh mục dịch vụ Spa", "dịch vụ Spa")
                .datTienTo("SPA")
                .timKiem("Tìm theo mã, tên dịch vụ...")
                .datTrangThai(5, "Đang hoạt động", "Ngừng hoạt động")
                .datBoKiemTra(
                        d ->
                                BoSoanThaoBangGia.layGiaNhoNhat(String.valueOf(d[4])) <= 0
                                        ? "Bảng giá cần ít nhất một mức giá lớn hơn 0."
                                        : null);
    }

    private static List<TruongDuLieu> cacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã DV", Kieu.VAN_BAN).chiDoc().datDoRong(70, 85, 110),
                TruongDuLieu.tao("Tên dịch vụ", Kieu.VAN_BAN).batBuoc().kiemTraDuyNhat().datDoRong(140, 180, 240),
                TruongDuLieu.tao("Mô tả", Kieu.VUNG).an(),
                TruongDuLieu.tao("Thời gian dự kiến (phút)", Kieu.SO_NGUYEN).batBuoc().datDoRong(170, 185, 220),
                TruongDuLieu.tao("Bảng giá theo loài / cân nặng", Kieu.TUY_CHINH)
                        .datDoRong(210, 240, 320)
                        .datBoSoanThao(BoSoanThaoBangGia::new)
                        .hienThi(
                                v -> {
                                    long gia = BoSoanThaoBangGia.layGiaNhoNhat(v);
                                    return gia < 0 ? "—" : "Từ " + TienTe.dinhDang(gia);
                                }),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datDoRong(110, 130, 160)
                        .datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
    }
}
