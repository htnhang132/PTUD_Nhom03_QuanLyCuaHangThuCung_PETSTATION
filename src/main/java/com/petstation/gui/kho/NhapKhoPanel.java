package com.petstation.gui.kho;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.util.DinhDang;

import java.util.function.Consumer;

// Màn hình Xử lý > Lập phiếu nhập kho
// Màn hình: Nhập kho hàng hóa (Mã: NHAP_KHO)
// Luồng xử lý: GUI -> NhapXuatKhoService -> NhapXuatKhoDAOImpl -> SQL (phieu_nhap_kho,
// chi_tiet_phieu_nhap, lo_hang)
// Mô tả: Lập phiếu nhập kho từ nhà cung cấp, ghi nhận số lô, hạn sử dụng và cập nhật giá vốn
// Phân hệ: kho
public final class NhapKhoPanel extends NghiepVuKhoPanel {
    public NhapKhoPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PHIEU_NHAP_KHO, banHang, dieuHuong);

        // 1. KHỞI TẠO CÁC NÚT THAO TÁC TRÊN THANH CÔNG CỤ
        themNut(
                layKhungThaoTac(),
                "+ Lập phiếu nhập kho",
                NutDinhDang.KieuNut.CHINH,
                this::nhapKho);
        var btnChiTiet =
                themNut(
                        layKhungThaoTac(),
                        "Chi tiết",
                        NutDinhDang.KieuNut.PHU,
                        this::layNhapKhoChiTiet);
        var btnTraNo =
                themNut(
                        layKhungThaoTac(),
                        "Trả công nợ",
                        NutDinhDang.KieuNut.THANH_CONG,
                        this::thanhToanNhaCungCap);

        // 2. XỬ LÝ SỰ KIỆN CHỌN DÒNG & KÍCH HOẠT NÚT TRẢ CÔNG NỢ / XEM CHI TIẾT
        Runnable capNhatNut =
                () -> {
                    int row = layBangDuLieu().getSelectedRow();
                    if (row < 0) {
                        btnChiTiet.setEnabled(false);
                        btnTraNo.setEnabled(false);
                        return;
                    }
                    btnChiTiet.setEnabled(true);
                    int dong = layBangDuLieu().convertRowIndexToModel(row);
                    long tongTien =
                            layMoHinh().getValueAt(dong, 4) instanceof Number n
                                    ? n.longValue()
                                    : DinhDang.doc(String.valueOf(layMoHinh().getValueAt(dong, 4)));
                    long daTra =
                            layMoHinh().getValueAt(dong, 5) instanceof Number n
                                    ? n.longValue()
                                    : DinhDang.doc(String.valueOf(layMoHinh().getValueAt(dong, 5)));
                    btnTraNo.setEnabled(tongTien > daTra);
                };
        layBangDuLieu()
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) capNhatNut.run();
                        });
        capNhatNut.run();
        ganSuKienDupChuot(this::layNhapKhoChiTiet);
    }
}
