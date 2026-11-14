package com.petstation.gui.spa;

import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.spa.GoiDichVuSpaService;
import com.petstation.util.DinhDang;

import java.util.List;

import javax.swing.SwingUtilities;

// Màn hình: Danh mục gói dịch vụ Spa (Mã: DANH_MUC_DICH_VU_SPA)
// Luồng xử lý: GUI -> DanhMucSpaService -> GoiDichVuSpaDAOImpl -> SQL (goi_dich_vu_spa)
// Mô tả: Quản lý bảng giá, định mức thời gian và điều kiện áp dụng các gói dịch vụ Spa
// Phân hệ: spa
public final class DanhMucDichVuSpaPanel extends QuanLyDanhMucPanel {
    public DanhMucDichVuSpaPanel() {
        super(
                cauHinh(),
                cacTruong(),
                GoiDichVuSpaService.macDinh()
                        .chuanBi(
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
    }

    private static CauHinh cauHinh() {
        return CauHinh.tao("Danh mục dịch vụ Spa", "dịch vụ Spa")
                .datTienTo("SPA")
                .timKiem("Tìm theo mã, tên dịch vụ...")
                .datTrangThai(5, "Đang hoạt động", "Ngừng hoạt động")
                .datBoKiemTra(
                        d -> {
                            if (DinhDang.doc(String.valueOf(d[3])) <= 0) {
                                return "Thời gian thực hiện phải lớn hơn 0 phút.";
                            }
                            if (BoSoanThaoBangGia.layGiaNhoNhat(String.valueOf(d[4])) <= 0) {
                                return "Bảng giá cần ít nhất một mức giá lớn hơn 0.";
                            }
                            return null;
                        });
    }

    private static List<TruongDuLieu> cacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã DV", Kieu.VAN_BAN).chiDoc().datDoRong(88, 108, 0),
                TruongDuLieu.tao("Tên dịch vụ", Kieu.VAN_BAN)
                        .batBuoc()
                        .kiemTraDuyNhat()
                        .datDoRong(180, 260, 0),
                TruongDuLieu.tao("Mô tả", Kieu.VUNG).an(),
                TruongDuLieu.tao("Thời gian (phút)", Kieu.SO_NGUYEN)
                        .batBuoc()
                        .datDoRong(95, 115, 0),
                TruongDuLieu.tao("Bảng giá theo loài / cân nặng", Kieu.TUY_CHINH)
                        .datDoRong(130, 165, 0)
                        .datBoSoanThao(BoSoanThaoBangGia::new)
                        .hienThi(
                                v -> {
                                    long gia = BoSoanThaoBangGia.layGiaNhoNhat(v);
                                    return gia < 0 ? "—" : "Từ " + DinhDang.dinhDang(gia);
                                }),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datDoRong(95, 118, 0)
                        .datLuaChon("Đang hoạt động", "Ngừng hoạt động"));
    }
}
