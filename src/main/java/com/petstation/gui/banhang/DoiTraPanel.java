package com.petstation.gui.banhang;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.banhang.BanHangService;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

// Màn hình: Đổi trả hàng hóa (Mã: DOI_TRA)
// Luồng xử lý: GUI -> DoiTraService, DonDoiTreoService -> DoiTraDAOImpl -> SQL (phieu_doi_tra,
// chi_tiet_doi_tra)
// Mô tả: Tiếp nhận và xử lý yêu cầu đổi/trả hàng theo hóa đơn gốc và chính sách bảo hành
// Phân hệ: banhang
public final class DoiTraPanel extends BangNghiepVuPanel {
    public DoiTraPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.DOI_TRA, banHang, dieuHuong);
        themNut(layKhungThaoTac(), "+ Đổi phụ kiện (F7)", NutDinhDang.KieuNut.CHINH, this::traHang);
        var btnXemGoc =
                themNut(
                        layKhungThaoTac(),
                        "Xem hóa đơn gốc",
                        NutDinhDang.KieuNut.PHU,
                        this::hienThiHoaDonGoc);
        btnXemGoc.setEnabled(layBangDuLieu().getSelectedRow() >= 0);
        layBangDuLieu()
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) {
                                btnXemGoc.setEnabled(layBangDuLieu().getSelectedRow() >= 0);
                            }
                        });
        ganPhimTat(KeyEvent.VK_F7, this::traHang);
        ganSuKienDupChuot(this::hienThiHoaDonGoc);
        ganMenuChuotPhai(
                row -> {
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
        if (layBangDuLieu().getSelectedRow() < 0) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn một phiếu đổi hàng để xem hóa đơn gốc.");
            return;
        }
        String maHoaDon = layNoiDungDongChon(2);
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> new BanHangService().layBienLai(maHoaDon),
                bienLai -> {
                    layBanHang().setBienLaiCuoi(bienLai);
                    layDieuHuong().accept(MaManHinh.IN_HOA_DON);
                });
    }

    private void traHang() {
        if (!layBanHang().kiemTraTrong()) {
            if (HopThoai.xacNhanDongY(
                    this,
                    "Giỏ hàng hiện tại đang có sản phẩm chưa thanh toán.\n"
                            + "Bạn có muốn làm trống giỏ hàng để bắt đầu đổi hàng ngay không?",
                    "Giỏ hàng đang có sản phẩm")) {
                layBanHang().xoa();
            } else {
                return;
            }
        }
        new DoiPhuKienDialog(this, layBanHang(), layDieuHuong()).setVisible(true);
        lamMoi();
    }
}
