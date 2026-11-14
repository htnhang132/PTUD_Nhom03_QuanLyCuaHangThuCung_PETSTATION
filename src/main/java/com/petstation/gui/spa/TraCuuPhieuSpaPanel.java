package com.petstation.gui.spa;

import com.petstation.dto.GioHang;
import com.petstation.dto.SpaDTO;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.BoChonKhoangNgay;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.service.spa.SpaService;
import com.petstation.util.DinhDang;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

// Màn hình: Tra cứu phiếu Spa (Mã: TRA_CUU_PHIEU_SPA)
// Luồng xử lý: GUI -> TiepNhanSpaService -> PhieuTiepNhanSpaDAOImpl -> SQL (phieu_tiep_nhan_spa)
// Mô tả: Tìm kiếm và theo dõi tiến độ thực hiện các phiếu chăm sóc spa thú cưng
// Phân hệ: spa
public final class TraCuuPhieuSpaPanel extends NghiepVuSpaPanel {
    private final BoChonKhoangNgay khoangNgay = new BoChonKhoangNgay();

    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(
                    new String[] {
                        "Tất cả trạng thái",
                        "Tiếp nhận",
                        "Đang làm",
                        "Hoàn tất",
                        "Đã thanh toán",
                        "Đã hủy"
                    });

    private static final String[] MA_TRANG_THAI = {
        "",
        TrangThaiPhieuSpa.TIEP_NHAN.maDB(),
        TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB(),
        TrangThaiPhieuSpa.HOAN_TAT.maDB(),
        TrangThaiPhieuSpa.DA_THANH_TOAN.maDB(),
        TrangThaiPhieuSpa.DA_HUY.maDB()
    };

    private static final class BoLoc {
        final LocalDate tuNgay;
        final LocalDate denNgay;
        final String trangThai;

        private BoLoc(LocalDate tuNgay, LocalDate denNgay, String trangThai) {
            this.tuNgay = tuNgay;
            this.denNgay = denNgay;
            this.trangThai = trangThai;
        }
    }

    private volatile BoLoc boLoc;
    private boolean dangDatLaiBoLoc;
    private NutDinhDang btnMoXuLy;

    public TraCuuPhieuSpaPanel(GioHang gio, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PHIEU_SPA, gio, dieuHuong);

        layOTimKiem().setToolTipText("Lọc nhanh mã phiếu, tên khách, thú cưng, ngày, trạng thái");

        dangDatLaiBoLoc = true;
        try {
            khoangNgay.chonKy("30 ngày qua");
            ghiNhanBoLoc();
        } finally {
            dangDatLaiBoLoc = false;
        }
        khoangNgay.addPropertyChangeListener(
                "dateRange",
                e -> {
                    if (dangDatLaiBoLoc) return;
                    if (ghiNhanBoLoc()) taiLaiNen();
                });

        cboTrangThai.addActionListener(
                e -> {
                    if (dangDatLaiBoLoc) return;
                    if (ghiNhanBoLoc()) taiLaiNen();
                });

        layKhungThaoTac().add(khoangNgay);
        ((ThanhDanhSach) layKhungThaoTac()).themBoLoc("Trạng thái:", cboTrangThai);

        btnMoXuLy =
                themNut(
                        layKhungThaoTac(),
                        "Mở xử lý",
                        () -> {
                            gio.getQuyTrinh().chonSpa(layDongChon());
                            dieuHuong.accept(MaManHinh.QUAN_LY_SPA);
                        });

        layBangDuLieu()
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) capNhatNutMoXuLy();
                        });
        capNhatNutMoXuLy();
        ganSuKienDupChuot(this::laySpaChiTiet);
        thayBoLangNgheTimKiem(this::locKetQua);
    }

    @Override
    protected void lienKetBoLoc(JTextField timKiem, JTable bang) {
        if (Boolean.TRUE.equals(timKiem.getClientProperty("locSpaTuKhoa"))) return;
        timKiem.putClientProperty("locSpaTuKhoa", true);
        themBoLangNgheTaiLieu(timKiem, this::locKetQua);
    }

    @Override
    protected void capNhatThaoTac() {
        super.capNhatThaoTac();
        capNhatNutMoXuLy();
    }

    @Override
    protected void sauKhiTaiLai() {
        if (layOTimKiem() != null) locKetQua();
        capNhatNutMoXuLy();
    }

    @Override
    protected void lamMoi() {
        if (khoangNgay != null && !dangDatLaiBoLoc && !ghiNhanBoLoc()) {
            return;
        }
        super.lamMoi();
    }

    private void capNhatNutMoXuLy() {
        if (btnMoXuLy == null) return;
        int dong = layBangDuLieu().getSelectedRow();
        if (dong < 0) {
            btnMoXuLy.setEnabled(false);
            return;
        }
        int dongMoHinh = layBangDuLieu().convertRowIndexToModel(dong);
        Object val = layMoHinh().getValueAt(dongMoHinh, 7);
        String tt = val == null ? "" : val.toString().trim();
        boolean daKetThuc =
                TrangThaiPhieuSpa.DA_THANH_TOAN.maDB().equalsIgnoreCase(tt)
                        || "Đã thanh toán".equalsIgnoreCase(tt)
                        || TrangThaiPhieuSpa.DA_HUY.maDB().equalsIgnoreCase(tt)
                        || "Đã hủy".equalsIgnoreCase(tt);
        btnMoXuLy.setEnabled(!daKetThuc);
    }

    private boolean ghiNhanBoLoc() {
        LocalDate tuNgay = khoangNgay.layNgayBatDau();
        LocalDate denNgay = khoangNgay.layNgayKetThuc();

        if ((tuNgay == null) != (denNgay == null)) {
            khoangNgay.setBorder(BorderFactory.createLineBorder(MauUngDung.NGUY_HIEM));
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn đủ Từ ngày và Đến ngày.");
            return false;
        }
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            khoangNgay.setBorder(BorderFactory.createLineBorder(MauUngDung.NGUY_HIEM));
            HopThoai.hienThiCanhBao(this, "Từ ngày không được lớn hơn Đến ngày.");
            return false;
        }
        khoangNgay.setBorder(null);
        int idx = cboTrangThai.getSelectedIndex();
        String trangThai = (idx >= 0 && idx < MA_TRANG_THAI.length) ? MA_TRANG_THAI[idx] : "";
        boLoc = new BoLoc(tuNgay, denNgay, trangThai);
        return true;
    }

    @SuppressWarnings("unchecked")
    private void locKetQua() {
        if (layBangDuLieu() == null
                || !(layBangDuLieu().getRowSorter() instanceof TableRowSorter<?> raw)) {
            return;
        }
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) raw;
        String tuKhoa = layOTimKiem().getText().trim().toLowerCase(Locale.ROOT);
        if (tuKhoa.isEmpty() || "tìm trong danh sách...".equals(tuKhoa)) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(
                new RowFilter<>() {
                    @Override
                    public boolean include(Entry<? extends TableModel, ? extends Integer> e) {
                        for (int i = 1; i < e.getValueCount(); i++) {
                            Object val = e.getValue(i);
                            if (val == null) continue;
                            if (val.toString().toLowerCase(Locale.ROOT).contains(tuKhoa)) {
                                return true;
                            }
                            if (i == 7 && nhanTrangThai(val.toString()).contains(tuKhoa)) {
                                return true;
                            }
                            if (i == 8 && dinhDangNgay(val).contains(tuKhoa)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    private static String nhanTrangThai(String ma) {
        String ten = DinhDangBang.tenTrangThai(ma).toLowerCase(Locale.ROOT);
        if (TrangThaiPhieuSpa.TIEP_NHAN.maDB().equalsIgnoreCase(ma)) {
            return ten + " tiếp nhận đã tiếp nhận";
        }
        if (TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB().equalsIgnoreCase(ma)) {
            return ten + " đang làm đang thực hiện đang chăm sóc";
        }
        if (TrangThaiPhieuSpa.HOAN_TAT.maDB().equalsIgnoreCase(ma)) {
            return ten + " hoàn tất hoàn thành";
        }
        return ten;
    }

    private static String dinhDangNgay(Object val) {
        if (val instanceof LocalDateTime dt) return DinhDang.ngayGio(dt).toLowerCase(Locale.ROOT);
        if (val instanceof LocalDate d) return DinhDang.ngay(d).toLowerCase(Locale.ROOT);
        if (val instanceof Date d) {
            return DinhDang.ngayGio(
                            LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(d.getTime()), ZoneId.systemDefault()))
                    .toLowerCase(Locale.ROOT);
        }
        return "";
    }

    @Override
    protected List<Object[]> layDuLieu() {
        SpaService dichVu = super.spaService != null ? super.spaService : new SpaService();
        BoLoc loc = boLoc;
        List<SpaDTO.DongTimKiemSpa> ds =
                (loc == null)
                        ? dichVu.timKiem(LocalDate.now().minusDays(29), LocalDate.now(), "")
                        : dichVu.timKiem(loc.tuNgay, loc.denNgay, loc.trangThai);
        List<Object[]> ketQua = new ArrayList<>(ds.size());
        for (SpaDTO.DongTimKiemSpa d : ds) {
            var p = d.getPhieuSpa();
            var kh = d.getKhachHang();
            var tc = d.getThuCung();
            var nv = d.getNhanVien();
            ketQua.add(
                    new Object[] {
                        d.getMaKhoa(),
                        p != null && p.getMaPhieu() != null ? p.getMaPhieu() : "",
                        kh != null && kh.getHoTen() != null ? kh.getHoTen() : "",
                        tc != null && tc.getTen() != null ? tc.getTen() : "",
                        p != null ? p.getCanNangThucTe() : 0.0,
                        nv != null && nv.getHoTen() != null ? nv.getHoTen() : "",
                        p != null ? p.getTongTien() : 0L,
                        p != null && p.getTrangThai() != null ? p.getTrangThai().maDB() : "",
                        p != null ? p.getTaoLuc() : null
                    });
        }
        return ketQua;
    }

    @Override
    protected void datLaiBoLoc() {
        dangDatLaiBoLoc = true;
        try {
            layOTimKiem().setText("");
            khoangNgay.chonKy("30 ngày qua");
            cboTrangThai.setSelectedIndex(0);
            ghiNhanBoLoc();
        } finally {
            dangDatLaiBoLoc = false;
        }
        locKetQua();
    }
}
