package com.petstation.gui.processing.inventory;

import static com.petstation.gui.components.TruongNghiepVu.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.gui.components.BangNghiepVuPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.common.BieuMauSQL;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.KhoService;
import com.petstation.service.LuaChonNghiepVuService;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

public abstract class NghiepVuKhoPanel extends BangNghiepVuPanel {
    protected NghiepVuKhoPanel(LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
    }

    protected void nhapKho() {
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Lập phiếu nhập kho",
                        taoLuaChon(
                                "supplier",
                                "Nhà cung cấp",
                                LuaChonNghiepVuService.layLuaChonNhaCungCap()),
                        taoLuaChon(
                                "product", "Sản phẩm", LuaChonNghiepVuService.layLuaChonSanPham()),
                        layTruongVanBan("batch", "Số lô", ""),
                        layTruongVanBan("expiry", "Hạn sử dụng (dd/MM/yyyy hoặc yyyy-MM-dd)", ""),
                        layTruongVanBan("qty", "Số lượng", "1"),
                        layTruongVanBan("cost", "Giá nhập / đơn vị (đồng)", ""),
                        layTruongVanBan("da_tra", "Đã trả (đồng)", "0"));
        if (giaTri != null) {
            try {
                int sl;
                long gia;
                try {
                    sl = docSoLuong(giaTri, "qty");
                    gia = docSoTien(giaTri, "cost");
                } catch (Exception ex) {
                    HopThoai.hienThiCanhBao(this, "Số lượng và giá nhập phải là số hợp lệ.");
                    return;
                }
                if (sl <= 0) {
                    HopThoai.hienThiCanhBao(this, "Số lượng nhập kho phải lớn hơn 0.");
                    return;
                }
                if (gia <= 0) {
                    HopThoai.hienThiCanhBao(this, "Giá nhập mỗi đơn vị phải lớn hơn 0 đồng.");
                    return;
                }
                KhoService.nhapKho(
                        giaTri.get("supplier"),
                        giaTri.get("product"),
                        giaTri.get("batch"),
                        phanTichNgay(giaTri.get("expiry")),
                        sl,
                        gia,
                        docSoTien(giaTri, "da_tra"));
                HopThoai.hienThiThanhCong(this, "Đã lập phiếu nhập kho thành công.");
                lamMoi();
            } catch (RuntimeException ex) {
                HopThoai.hienThiCanhBao(this, ex.getMessage());
            }
        }
    }

    private static LocalDate phanTichNgay(String s) {
        if (s == null || s.isBlank()) return null;
        String trimmed = s.trim();
        java.time.format.DateTimeFormatter[] cacDinhDang = new java.time.format.DateTimeFormatter[] {
                java.time.format.DateTimeFormatter.ISO_LOCAL_DATE,
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy")
        };
        for (var d : cacDinhDang) {
            try {
                return LocalDate.parse(trimmed, d);
            } catch (Exception ignored) {}
        }
        throw new IllegalArgumentException("Hạn sử dụng không hợp lệ (hỗ trợ dạng dd/MM/yyyy hoặc yyyy-MM-dd).");
    }

    protected void thanhToanNhaCungCap() {
        yeuCau(com.petstation.model.Quyen.NHAP_KHO);

        long id = layDongChon();
        int dong = tblBang.convertRowIndexToModel(tblBang.getSelectedRow());
        long tongTien =
                moHinh.getValueAt(dong, 4) instanceof Number n
                        ? n.longValue()
                        : com.petstation.util.TienTe.doc(String.valueOf(moHinh.getValueAt(dong, 4)));
        long daTraHienTai =
                moHinh.getValueAt(dong, 5) instanceof Number n
                        ? n.longValue()
                        : com.petstation.util.TienTe.doc(String.valueOf(moHinh.getValueAt(dong, 5)));
        if (daTraHienTai >= tongTien || KhoService.layCongNoConLai(id) <= 0) {
            HopThoai.hienThiCanhBao(this, "Không tồn tại công nợ");
            return;
        }
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Trả công nợ",
                        layTruongVanBan("da_tra", "Số tiền trả thêm (đồng)", ""));
        if (giaTri == null) return;
        KhoService.thanhToanNhaCungCap(id, docSoTien(giaTri, "da_tra"));
    }

    protected void ghiHuy() {
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Xuất hủy hàng hóa",
                        layCacLoHang(),
                        layTruongVanBan("qty", "Số lượng", "1"),
                        layTruongVanBan("reason", "Lý do", ""));
        if (giaTri != null) {
            try {
                int sl;
                try {
                    sl = docSoLuong(giaTri, "qty");
                } catch (Exception ex) {
                    HopThoai.hienThiCanhBao(this, "Số lượng xuất hủy phải là số nguyên.");
                    return;
                }
                if (sl <= 0) {
                    HopThoai.hienThiCanhBao(this, "Số lượng xuất hủy phải lớn hơn 0.");
                    return;
                }
                String lyDo = giaTri.get("reason");
                if (lyDo == null || lyDo.trim().isBlank()) {
                    HopThoai.hienThiCanhBao(this, "Vui lòng nhập lý do xuất hủy hàng hóa.");
                    return;
                }
                KhoService.ghiHuy(
                        docSoTien(giaTri, "batch"), sl, lyDo.trim());
                HopThoai.hienThiThanhCong(this, "Đã lưu phiếu. Phiếu vượt ngưỡng sẽ chờ phê duyệt.");
                lamMoi();
            } catch (RuntimeException ex) {
                HopThoai.hienThiCanhBao(this, ex.getMessage());
            }
        }
    }

    protected void kiemKe() {
        Map<String, String> giaTri =
                BieuMauSQL.layYeuCauNhap(
                        this,
                        "Kiểm kê",
                        layCacLoHang(),
                        layTruongVanBan("actual", "Tồn thực tế", "0"),
                        layTruongVanBan("reason", "Lý do chênh lệch", ""));
        if (giaTri != null) {
            try {
                int ton;
                try {
                    ton = docSoLuong(giaTri, "actual");
                } catch (Exception ex) {
                    HopThoai.hienThiCanhBao(this, "Tồn thực tế phải là số nguyên.");
                    return;
                }
                if (ton < 0) {
                    HopThoai.hienThiCanhBao(this, "Tồn thực tế không được là số âm.");
                    return;
                }
                KhoService.kiemKe(
                        docSoTien(giaTri, "batch"), ton, giaTri.get("reason"));
                HopThoai.hienThiThanhCong(
                        this, "Đã lưu biên bản. Chênh lệch chỉ điều chỉnh sau phê duyệt.");
                lamMoi();
            } catch (RuntimeException ex) {
                HopThoai.hienThiCanhBao(this, ex.getMessage());
            }
        }
    }

    protected void layNhapKhoChiTiet() {
        hienThiChiTiet(
                "Chi tiết nhập kho",
                new String[] {"Sản phẩm", "Số lô", "Hạn dùng", "Số lượng", "Giá nhập"},
                KhoService.layNhapKhoChiTiet(layDongChon()));
    }
}
