package com.petstation.gui.banhang;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.DonDoiTreoService;

import java.util.function.Consumer;

// Màn hình: Quản lý đơn treo (Mã: DON_HANG_TREO)
// Luồng xử lý: GUI -> BanHangService -> NghiepVuDAOImpl -> SQL (gio_hang_treo,
// chi_tiet_gio_hang_treo)
// Mô tả: Lưu tạm và phục hồi các giỏ hàng đang chờ xử lý của khách hàng
// Phân hệ: banhang
public final class DonHangTreoPanel extends BangNghiepVuPanel {
    private final DonDoiTreoService donDoiTreoService = new DonDoiTreoService();
    private final BanHangService banHangService = new BanHangService();

    public DonHangTreoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.GIO_HANG_TREO, banHang, dieuHuong);
        NutDinhDang btnMoLai = new NutDinhDang("Mở lại đơn", NutDinhDang.KieuNut.PHU);
        btnMoLai.addActionListener(e -> moLaiDon(banHang, dieuHuong));
        layKhungThaoTac().add(btnMoLai);
        NutDinhDang btnXoa = new NutDinhDang("Xóa đơn treo", NutDinhDang.KieuNut.PHU);
        btnXoa.addActionListener(e -> xoaDonTreo());
        layKhungThaoTac().add(btnXoa);
    }

    private void moLaiDon(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        try {
            String maDon = layNoiDungDongChon(1);
            if (banHang.kiemTraTrong()) {
                moLaiSauKhiXuLyGio(maDon, banHang, dieuHuong, false);
                return;
            }
            int luaChon =
                    HopThoai.tuyChon(
                            this,
                            "Giỏ hàng hiện tại đang có sản phẩm. Bạn muốn xử lý thế nào trước khi"
                                    + " mở đơn treo "
                                    + maDon
                                    + "?",
                            "Giỏ hàng đang có sản phẩm",
                            new String[] {
                                "Treo giỏ hiện tại rồi mở", "Thay thế (xóa giỏ hiện tại)", "Hủy"
                            },
                            true);
            if (luaChon == 0) {
                TacVuNen.chayCoPhien(
                        this,
                        () -> true,
                        () -> banHangService.treo(banHang),
                        () -> {
                            banHang.xoa();
                            taiLaiNen();
                            moLaiSauKhiXuLyGio(maDon, banHang, dieuHuong, false);
                        });
            } else if (luaChon == 1) {
                moLaiSauKhiXuLyGio(maDon, banHang, dieuHuong, true);
            }
        } catch (RuntimeException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
        }
    }

    private void moLaiSauKhiXuLyGio(
            String maDon, GioHang banHang, Consumer<MaManHinh> dieuHuong, boolean xoaGioTruoc) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> donDoiTreoService.lay(maDon),
                donDoi -> {
                    if (xoaGioTruoc) banHang.xoa();
                    if (donDoi != null) {
                        new DoiPhuKienDialog(this, banHang, dieuHuong, maDon, donDoi)
                                .setVisible(true);
                        taiLaiNen();
                        return;
                    }
                    TacVuNen.chayCoPhien(
                            this,
                            () -> true,
                            () -> banHangService.moLai(maDon, banHang),
                            () -> dieuHuong.accept(MaManHinh.LAP_HOA_DON));
                });
    }

    private void xoaDonTreo() {
        try {
            String maDon = layNoiDungDongChon(1);
            if (!HopThoai.xacNhan(this, "Xóa đơn treo đã chọn?")) return;
            TacVuNen.chayCoPhien(
                    this, () -> true, () -> banHangService.xoaTreo(maDon), this::taiLaiNen);
        } catch (RuntimeException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
        }
    }
}
