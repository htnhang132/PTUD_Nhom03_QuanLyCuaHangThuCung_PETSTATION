package com.petstation.gui.search;

import com.petstation.gui.components.common.BoChonKhoangNgay;
import com.petstation.gui.components.ThanhDanhSach;
import com.petstation.gui.processing.spa.NghiepVuSpaPanel;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.SpaService;

import java.time.LocalDate;
import java.awt.BorderLayout;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

public final class TraCuuPhieuSpaPanel extends NghiepVuSpaPanel {
    private final BoChonKhoangNgay khoangNgay =
        new BoChonKhoangNgay();

    private final JComboBox<String> cboTrangThai =
        new JComboBox<>(new String[] {
            "Tất cả trạng thái",
            "Đã tiếp nhận",
            "Đang thực hiện",
            "Hoàn thành",
            "Đã thanh toán",
            "Đã hủy"
        });

    private static final String[] MA_TRANG_THAI = {
        "", "TIEP_NHAN", "DANG_THUC_HIEN", "HOAN_TAT", "DA_THANH_TOAN", "DA_HUY"
    };

    private record BoLoc(
        LocalDate tuNgay,
        LocalDate denNgay,
        String trangThai) {}

    private volatile BoLoc boLoc;

    public TraCuuPhieuSpaPanel(
        GioHang gio,
        Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PHIEU_SPA, gio, dieuHuong);

        txtTimKiem.setToolTipText(
            "Lọc nhanh mã phiếu, tên khách, thú cưng trong kết quả");

        khoangNgay.chonKy("30 ngày qua");
        khoangNgay.addPropertyChangeListener("dateRange", e -> {
            ghiNhanBoLoc();
            taiLaiNen();
        });

        cboTrangThai.addActionListener(e -> {
            ghiNhanBoLoc();
            taiLaiNen();
        });

        pnlCacThaoTac.add(khoangNgay);
        pnlCacThaoTac.add(cboTrangThai);

        ghiNhanBoLoc();

        JPanel pnlThaoTacPhieu = new ThanhDanhSach();
        pnlThaoTacPhieu.setBorder(BorderFactory.createEmptyBorder());
        JPanel pnlDauTrang = (JPanel) pnlCacThaoTac.getParent();
        JPanel pnlHaiHang = new JPanel(new BorderLayout(0, 8));
        pnlHaiHang.setOpaque(false);
        pnlDauTrang.remove(pnlCacThaoTac);
        pnlHaiHang.add(pnlCacThaoTac, BorderLayout.CENTER);
        pnlHaiHang.add(pnlThaoTacPhieu, BorderLayout.SOUTH);
        pnlDauTrang.add(pnlHaiHang, BorderLayout.CENTER);

        themNut(
            pnlThaoTacPhieu,
            "Chi tiết dịch vụ",
            this::laySpaChiTiet);

        themNut(pnlThaoTacPhieu, "Mở xử lý", () -> {
            gio.layQuyTrinh().chonSpa(layDongChon());
            dieuHuong.accept(MaManHinh.CAP_NHAT_TIEN_DO_SPA);
        });

        themNut(pnlThaoTacPhieu, "In danh sách", () -> {
            try {
                tblBang.print();
            } catch (java.awt.print.PrinterException ex) {
                throw new IllegalStateException(
                    "Không thể in: " + ex.getMessage(), ex);
            }
        });
    }

    private void ghiNhanBoLoc() {
        LocalDate tuNgay = khoangNgay.layNgayBatDau();
        LocalDate denNgay = khoangNgay.layNgayKetThuc();

        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            return;
        }

        int idx = cboTrangThai.getSelectedIndex();
        String trangThai = (idx >= 0 && idx < MA_TRANG_THAI.length) ? MA_TRANG_THAI[idx] : "";
        boLoc = new BoLoc(tuNgay, denNgay, trangThai);
    }

    @Override
    protected List<Object[]> layDuLieu() {
        BoLoc loc = boLoc;

        if (loc == null) {
            LocalDate homNay = LocalDate.now();
            return SpaService.timKiem(homNay.minusDays(29), homNay, "");
        }

        return SpaService.timKiem(
            loc.tuNgay(),
            loc.denNgay(),
            loc.trangThai());
    }

    @Override
    protected void datLaiBoLoc() {
        txtTimKiem.setText("");
        khoangNgay.chonKy("30 ngày qua");
        cboTrangThai.setSelectedIndex(0);
        ghiNhanBoLoc();
        taiLaiNen();
        revalidate();
        repaint();
    }
}
