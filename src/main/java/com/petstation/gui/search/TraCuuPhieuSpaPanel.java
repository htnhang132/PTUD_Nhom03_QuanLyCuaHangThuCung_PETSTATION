package com.petstation.gui.search;

import com.petstation.gui.processing.spa.NghiepVuSpaPanel;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;

import java.util.function.Consumer;

/** Phiếu Spa trong ngày; thao tác thay đổi nằm ở các màn xử lý. */
public final class TraCuuPhieuSpaPanel extends NghiepVuSpaPanel {
    public TraCuuPhieuSpaPanel(GioHang gio, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PHIEU_SPA, gio, dieuHuong);
        themNut(pnlCacThaoTac, "Chi tiết dịch vụ", this::laySpaChiTiet);
        themNut(
                pnlCacThaoTac,
                "Mở xử lý",
                () -> {
                    gio.layQuyTrinh().chonSpa(layDongChon());
                    dieuHuong.accept(MaManHinh.CAP_NHAT_TIEN_DO_SPA);
                });
        themNut(
                pnlCacThaoTac,
                "In danh sách",
                () -> {
                    try {
                        tblBang.print();
                    } catch (java.awt.print.PrinterException loi) {
                        throw new IllegalStateException(loi.getMessage(), loi);
                    }
                });
    }
}
