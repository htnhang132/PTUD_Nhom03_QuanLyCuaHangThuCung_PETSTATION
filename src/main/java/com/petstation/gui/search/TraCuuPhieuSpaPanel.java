package com.petstation.gui.search;

import com.petstation.gui.components.common.BoChonKhoangNgay;
import com.petstation.gui.processing.spa.NghiepVuSpaPanel;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.SpaService;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public final class TraCuuPhieuSpaPanel extends NghiepVuSpaPanel {

    private final JCheckBox chkTheoNgay =
        new JCheckBox("Lọc theo ngày");

    private final BoChonKhoangNgay khoangNgay =
        new BoChonKhoangNgay();

    private final JComboBox<String> cboTrangThai =
        new JComboBox<>(new String[] {
            "Tất cả trạng thái",
            "Chờ tiếp nhận",
            "Đang thực hiện",
            "Hoàn thành",
            "Đã đóng"
        });

    private static final String[] MA_TRANG_THAI = {
        "", "RECEIVED", "IN_PROGRESS", "COMPLETED", "PAID"
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

        chkTheoNgay.setOpaque(false);
        khoangNgay.setVisible(false);

        chkTheoNgay.addActionListener(e -> {
            khoangNgay.setVisible(chkTheoNgay.isSelected());
            revalidate();
            repaint();
        });

        pnlCacThaoTac.add(chkTheoNgay);
        pnlCacThaoTac.add(khoangNgay);
        pnlCacThaoTac.add(cboTrangThai);

        themNut(pnlCacThaoTac, "Tìm kiếm", this::ghiNhanBoLoc);

        themNut(
            pnlCacThaoTac,
            "Chi tiết dịch vụ",
            this::laySpaChiTiet);

        themNut(pnlCacThaoTac, "Mở xử lý", () -> {
            gio.layQuyTrinh().chonSpa(layDongChon());
            dieuHuong.accept(MaManHinh.CAP_NHAT_TIEN_DO_SPA);
        });

        themNut(pnlCacThaoTac, "In danh sách", () -> {
            try {
                tblBang.print();
            } catch (java.awt.print.PrinterException ex) {
                throw new IllegalStateException(
                    "Không thể in: " + ex.getMessage(), ex);
            }
        });
    }

    private void ghiNhanBoLoc() {
        LocalDate tuNgay = null;
        LocalDate denNgay = null;

        if (chkTheoNgay.isSelected()) {
            tuNgay = khoangNgay.layNgayBatDau();
            denNgay = khoangNgay.layNgayKetThuc();

            if (tuNgay == null || denNgay == null) {
                throw new IllegalArgumentException(
                    "Vui lòng chọn đủ khoảng ngày.");
            }

            if (tuNgay.isAfter(denNgay)) {
                throw new IllegalArgumentException(
                    "Ngày bắt đầu không được sau ngày kết thúc.");
            }
        }

        boLoc = new BoLoc(
            tuNgay,
            denNgay,
            MA_TRANG_THAI[cboTrangThai.getSelectedIndex()]);
    }

    @Override
    protected List<Object[]> layDuLieu() {
        BoLoc loc = boLoc;

        // Chưa áp dụng bộ lọc: lấy toàn bộ lịch sử.
        if (loc == null) {
            return SpaService.timKiem(null, null, "");
        }

        return SpaService.timKiem(
            loc.tuNgay(),
            loc.denNgay(),
            loc.trangThai());
    }

    @Override
    protected void datLaiBoLoc() {
        txtTimKiem.setText("");

        chkTheoNgay.setSelected(false);
        khoangNgay.datLai();
        khoangNgay.setVisible(false);

        cboTrangThai.setSelectedIndex(0);
        boLoc = null;

        revalidate();
        repaint();
    }
}
