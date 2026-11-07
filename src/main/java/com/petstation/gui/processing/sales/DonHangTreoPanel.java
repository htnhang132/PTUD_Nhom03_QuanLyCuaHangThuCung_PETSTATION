package com.petstation.gui.processing.sales;

import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.BanHangService;
import com.petstation.service.DonDoiTreoService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public final class DonHangTreoPanel extends BangNghiepVuPanel {
    public DonHangTreoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.GIO_HANG_TREO, banHang, dieuHuong);

        themNut(
                pnlCacThaoTac,
                "Mở lại đơn",
                () -> {
                    if (!banHang.kiemTraTrong())
                        throw new IllegalArgumentException(
                                "Hãy treo hoặc thanh toán giỏ hiện tại trước.");
                    long id = layDongChon();
                    var donDoi = DonDoiTreoService.lay(id);
                    if (donDoi != null) {
                        new DoiPhuKienDialog(this, banHang, dieuHuong, id, donDoi).setVisible(true);
                        return;
                    }
                    BanHangService.moLai(id, banHang);
                    dieuHuong.accept(MaManHinh.LAP_HOA_DON);
                });
        themNut(
                pnlCacThaoTac,
                "Xóa đơn treo",
                () -> {
                    if (HopThoai.xacNhan(this, "Xóa đơn treo đã chọn?"))
                        BanHangService.xoaTreo(layDongChon());
                });
    }
}
