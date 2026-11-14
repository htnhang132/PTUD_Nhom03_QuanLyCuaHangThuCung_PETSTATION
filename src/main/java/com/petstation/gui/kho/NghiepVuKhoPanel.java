package com.petstation.gui.kho;

import static com.petstation.gui.chung.TruongNghiepVu.docSoLuong;
import static com.petstation.gui.chung.TruongNghiepVu.docSoTien;
import static com.petstation.gui.chung.TruongNghiepVu.layTruongVanBan;
import static com.petstation.gui.chung.TruongNghiepVu.taoLuaChonMuc;

import com.petstation.dto.GioHang;
import com.petstation.dto.MucLuaChon;
import com.petstation.dto.PhienDangNhap;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.Quyen;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.kho.NhapXuatKhoService;
import com.petstation.util.DinhDang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// Lớp nền (Base Panel) dùng chung cho các màn hình nghiệp vụ Kho
public abstract class NghiepVuKhoPanel extends BangNghiepVuPanel {
    private static final Logger LOG = LoggerFactory.getLogger(NghiepVuKhoPanel.class);
    private final NhapXuatKhoService nhapXuatKhoService = new NhapXuatKhoService();

    protected NghiepVuKhoPanel(LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
    }

    // 1. CỤM CHỨC NĂNG NHẬP KHO & CÔNG NỢ NHÀ CUNG CẤP

    // Mở hộp thoại nhập liệu để lập phiếu nhập kho mới
    protected void nhapKho() {
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () ->
                        new Object[] {
                            layNghiepVuService().layLuaChonNhaCungCap(),
                            layNghiepVuService().layLuaChonSanPham()
                        },
                null,
                duLieu -> hienThiHopThoaiNhapKho(duLieu, phien),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    @SuppressWarnings("unchecked")
    private void hienThiHopThoaiNhapKho(Object[] duLieu, PhienDangNhap phien) {
        var dsNcc = (List<MucLuaChon>) duLieu[0];
        var dsSp = (List<MucLuaChon>) duLieu[1];
        String soLoGoiY = "LOTE" + LocalDate.now().format(DinhDang.MA_NGAY);
        String hanDungGoiY = LocalDate.now().plusYears(1).format(DinhDang.NGAY);
        Map<String, String> giaTri =
                BieuMauTruong.layYeuCauNhap(
                        this,
                        "Lập phiếu nhập kho",
                        taoLuaChonMuc("supplier", "Nhà cung cấp", dsNcc),
                        taoLuaChonMuc("product", "Sản phẩm", dsSp),
                        layTruongVanBan("batch", "Số lô", soLoGoiY),
                        layTruongVanBan(
                                "expiry", "Hạn sử dụng (dd/MM/yyyy hoặc yyyy-MM-dd)", hanDungGoiY),
                        layTruongVanBan("qty", "Số lượng", "10"),
                        layTruongVanBan("cost", "Giá nhập / đơn vị (đồng)", "50000"),
                        layTruongVanBan("da_tra", "Đã trả (đồng)", "0"));
        if (giaTri != null) {
            int sl;
            long gia;
            long daTra;
            LocalDate hsd;
            try {
                sl = docSoLuong(giaTri, "qty");
                gia = docSoTien(giaTri, "cost");
                daTra = docSoTien(giaTri, "da_tra");
                hsd = phanTichNgay(giaTri.get("expiry"));
            } catch (NumberFormatException ex) {
                HopThoai.hienThiCanhBao(this, "Số lượng và giá nhập phải là số hợp lệ.");
                return;
            } catch (RuntimeException ex) {
                HopThoai.hienThiCanhBao(this, ex.getMessage());
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
            TacVuNen.chayCoPhien(
                    phien,
                    () -> true,
                    () -> {
                        nhapXuatKhoService.nhapKho(
                                giaTri.get("supplier"),
                                giaTri.get("product"),
                                giaTri.get("batch"),
                                hsd,
                                sl,
                                gia,
                                daTra);
                        return null;
                    },
                    null,
                    v -> {
                        HopThoai.hienThiThanhCong(this, "Đã lập phiếu nhập kho thành công.");
                        lamMoi();
                    },
                    ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
        }
    }

    // Hiển thị bảng chi tiết các mặt hàng và lô hàng của phiếu nhập đang chọn
    protected void layNhapKhoChiTiet() {
        String maPhieu = layNoiDungDongChon(1);
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> nhapXuatKhoService.layNhapKhoChiTiet(maPhieu),
                null,
                cacDong ->
                        hienThiChiTiet(
                                "Chi tiết nhập kho",
                                new String[] {
                                    "Sản phẩm", "Số lô", "Hạn dùng", "Số lượng", "Giá nhập"
                                },
                                cacDong),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    // Mở hộp thoại trả thêm tiền công nợ nhà cung cấp cho phiếu nhập đang chọn
    protected void thanhToanNhaCungCap() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.NHAP_KHO);
        String maPhieu = layNoiDungDongChon(1);
        int dong = layBangDuLieu().convertRowIndexToModel(layBangDuLieu().getSelectedRow());
        long tongTien =
                layMoHinh().getValueAt(dong, 4) instanceof Number n
                        ? n.longValue()
                        : DinhDang.doc(String.valueOf(layMoHinh().getValueAt(dong, 4)));
        long daTraHienTai =
                layMoHinh().getValueAt(dong, 5) instanceof Number n
                        ? n.longValue()
                        : DinhDang.doc(String.valueOf(layMoHinh().getValueAt(dong, 5)));
        if (daTraHienTai >= tongTien) {
            HopThoai.hienThiCanhBao(this, "Không tồn tại công nợ");
            return;
        }
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> nhapXuatKhoService.layCongNoConLai(maPhieu),
                null,
                conLai -> {
                    if (conLai <= 0) {
                        HopThoai.hienThiCanhBao(this, "Không tồn tại công nợ");
                        return;
                    }
                    Map<String, String> giaTri =
                            BieuMauTruong.layYeuCauNhap(
                                    this,
                                    "Trả công nợ",
                                    layTruongVanBan("da_tra", "Số tiền trả thêm (đồng)", ""));
                    if (giaTri == null) return;
                    long soTien = docSoTien(giaTri, "da_tra");
                    TacVuNen.chayCoPhien(
                            phien,
                            () -> true,
                            () -> {
                                nhapXuatKhoService.thanhToanNhaCungCap(maPhieu, soTien);
                                return null;
                            },
                            null,
                            v -> {
                                HopThoai.hienThiThanhCong(this, "Đã cập nhật trả công nợ.");
                                lamMoi();
                            },
                            ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    // Phân tích chuỗi hạn sử dụng người dùng nhập sang LocalDate
    private static LocalDate phanTichNgay(String s) {
        if (s == null || s.isBlank()) return null;
        String trimmed = s.trim();
        DateTimeFormatter[] cacDinhDang =
                new DateTimeFormatter[] {
                    DateTimeFormatter.ISO_LOCAL_DATE,
                    DinhDang.NGAY,
                    DinhDang.NGAY_GACH_NGANG,
                    DinhDang.NGAY_NGAN
                };
        for (var d : cacDinhDang) {
            try {
                return LocalDate.parse(trimmed, d);
            } catch (RuntimeException ex) {
                LOG.debug(
                        "Chuỗi ngày '{}' chưa khớp định dạng {}: {}", trimmed, d, ex.getMessage());
            }
        }
        throw new IllegalArgumentException(
                "Hạn sử dụng không hợp lệ (hỗ trợ dạng dd/MM/yyyy hoặc yyyy-MM-dd).");
    }

    // 2. CỤM CHỨC NĂNG XUẤT HỦY HÀNG HÓA

    // Mở hộp thoại chọn lô hàng, số lượng và lý do để lập phiếu xuất hủy
    protected void ghiHuy() {
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> layNghiepVuService().layLuaChonLoHang(),
                null,
                dsLo -> hienThiHopThoaiGhiHuy(dsLo, phien),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private void hienThiHopThoaiGhiHuy(List<MucLuaChon> dsLo, PhienDangNhap phien) {
        Map<String, String> giaTri =
                BieuMauTruong.layYeuCauNhap(
                        this,
                        "Xuất hủy hàng hóa",
                        taoLuaChonMuc("batch", "Lô hàng", dsLo),
                        layTruongVanBan("qty", "Số lượng", "1"),
                        layTruongVanBan("reason", "Lý do", ""));
        if (giaTri != null) {
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
            TacVuNen.chayCoPhien(
                    phien,
                    () -> true,
                    () -> {
                        nhapXuatKhoService.ghiHuy(giaTri.get("batch"), sl, lyDo.trim());
                        return null;
                    },
                    null,
                    v -> {
                        HopThoai.hienThiThanhCong(
                                this, "Đã lưu phiếu. Phiếu vượt ngưỡng sẽ chờ phê duyệt.");
                        lamMoi();
                    },
                    ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
        }
    }
}
