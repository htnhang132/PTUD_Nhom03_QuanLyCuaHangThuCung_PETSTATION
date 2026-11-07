package com.petstation.gui.catalog;

import com.petstation.gui.components.QuanLyDanhMucPanel;
import com.petstation.gui.components.common.BoSoanThaoBangGia;
import com.petstation.model.LoaiDanhMuc;
import com.petstation.service.DanhMucService;
import com.petstation.service.DieuKienTaiDuLieu;
import com.petstation.util.TienTe;

import java.util.List;

import javax.swing.SwingUtilities;

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
                                                d -> BoSoanThaoBangGia.layGiaNhoNhat(String.valueOf(d[4])) <= 0
                                                                ? "Bảng giá cần ít nhất một mức giá lớn hơn 0."
                                                                : null);
        }

        private static List<TruongDuLieu> cacTruong() {
                return List.of(
                                TruongDuLieu.tao("Mã DV", Kieu.VAN_BAN).chiDoc().datDoRong(88, 108, 0),
                                TruongDuLieu.tao("Tên dịch vụ", Kieu.VAN_BAN).batBuoc().kiemTraDuyNhat().datDoRong(180,
                                                260, 0),
                                TruongDuLieu.tao("Mô tả", Kieu.VUNG).an(),
                                TruongDuLieu.tao("Thời gian (phút)", Kieu.SO_NGUYEN).batBuoc().datDoRong(95, 115, 0),
                                TruongDuLieu.tao("Bảng giá theo loài / cân nặng", Kieu.TUY_CHINH)
                                                .datDoRong(130, 165, 0)
                                                .datBoSoanThao(BoSoanThaoBangGia::new)
                                                .hienThi(
                                                                v -> {
                                                                        long gia = BoSoanThaoBangGia.layGiaNhoNhat(v);
                                                                        return gia < 0 ? "—"
                                                                                        : "Từ " + TienTe.dinhDang(gia);
                                                                }),
                                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                                                .datDoRong(95, 118, 0)
                                                .datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
        }
}
