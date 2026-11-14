package com.petstation.gui.nhansu;

import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.model.chung.LoaiLichLamViec;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.pheduyet.PheDuyetService;
import com.petstation.util.DinhDang;

import java.awt.Component;
import java.time.LocalDate;
import java.time.format.ResolverStyle;

public final class ThaoTacNhanh {
    private ThaoTacNhanh() {}

    public static void layNghiPhep(Component cha) {
        var cacGiaTri =
                BieuMauTruong.layYeuCauNhap(
                        cha,
                        "Đăng ký nghỉ phép",
                        BieuMauTruong.TruongDuLieu.chuyenThanhChuoi(
                                "date",
                                "Ngày nghỉ (dd/MM/yyyy)",
                                LocalDate.now().format(DinhDang.NGAY)),
                        BieuMauTruong.TruongDuLieu.chuyenThanhChuoi("reason", "Lý do", ""));
        if (cacGiaTri == null) return;
        try {
            LocalDate ngay =
                    LocalDate.parse(
                            cacGiaTri.get("date"),
                            DinhDang.NGAY_U.withResolverStyle(ResolverStyle.STRICT));
            String lyDo = cacGiaTri.get("reason");
            String maNv = PhienDangNhapService.macDinh().layMaNhanVien();
            TacVuNen.chayCoPhien(
                    cha,
                    () -> true,
                    () ->
                            new PheDuyetService()
                                    .guiThaoTac(
                                            PheDuyetService.Loai.NGHI_PHEP,
                                            lyDo,
                                            "Lịch làm việc ngày " + ngay,
                                            "Nghỉ phép",
                                            LoaiLichLamViec.NGHI_PHEP.maDB(),
                                            maNv,
                                            ngay.toString()),
                    () -> HopThoai.hienThiThanhCong(cha, "Đã gửi yêu cầu nghỉ phép."));
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(cha, ngoaiLe.getMessage());
        }
    }
}
