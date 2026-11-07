package com.petstation.gui.processing.sales;

import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.BanHangService;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public final class DoiTraPanel extends NghiepVuBanHangPanel {
    public DoiTraPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.DOI_TRA, banHang, dieuHuong);
        themNut(pnlCacThaoTac, "+ Đổi phụ kiện (F7)", NutDinhDang.KieuNut.CHINH, this::traHang);
        themNut(pnlCacThaoTac, "Xem hóa đơn gốc", NutDinhDang.KieuNut.PHU, this::hienThiHoaDonGoc);

        ganPhimTat(KeyEvent.VK_F7, this::traHang);
        ganSuKienDupChuot(this::hienThiHoaDonGoc);

        ganMenuChuotPhai(row -> {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem itemDoi = new JMenuItem("+ Đổi phụ kiện mới");
            itemDoi.addActionListener(e -> traHang());
            popup.add(itemDoi);
            popup.addSeparator();
            JMenuItem itemGoc = new JMenuItem("Xem hóa đơn gốc");
            itemGoc.addActionListener(e -> hienThiHoaDonGoc());
            popup.add(itemGoc);
            return popup;
        });
    }

    private void hienThiHoaDonGoc() {
        if (tblBang.getSelectedRow() < 0) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn một phiếu đổi hàng để xem hóa đơn gốc.");
            return;
        }
        String maHoaDon = layNoiDungDongChon(2);
        banHang.setBienLaiCuoi(BanHangService.layBienLai(maHoaDon));
        dieuHuong.accept(MaManHinh.IN_HOA_DON);
    }

    @Override
    protected void traHang() {
        if (!banHang.kiemTraTrong()) {
            int luaChon = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Giỏ hàng hiện tại đang có sản phẩm chưa thanh toán.\nBạn có muốn làm trống giỏ hàng để bắt đầu đổi hàng ngay không?",
                    "Giỏ hàng đang có sản phẩm",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (luaChon == javax.swing.JOptionPane.YES_OPTION) {
                banHang.xoa();
            } else {
                return;
            }
        }

        new DoiPhuKienDialog(
            this,
            banHang,
            dieuHuong
        ).setVisible(true);
    }
}
