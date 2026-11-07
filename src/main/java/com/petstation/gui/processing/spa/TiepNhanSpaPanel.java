package com.petstation.gui.processing.spa;

import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.service.PhieuSpaTreoService;
import com.petstation.service.SpaService;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class TiepNhanSpaPanel extends NghiepVuSpaPanel {
    private final JTabbedPane tabWorkspace = new JTabbedPane();
    private final LapPhieuSpaPanel pnlLapPhieu;
    private final LichHenSpaPanel pnlLichHen;
    private DefaultTableModel moHinhPhieuTreo;
    private JTable tblPhieuTreo;

    public TiepNhanSpaPanel(
        GioHang banHang,
        Consumer<MaManHinh> dieuHuong
    ) {
        super(
            LoaiNghiepVu.TIEP_NHAN_SPA,
            banHang,
            dieuHuong
        );

        themNut(
            pnlCacThaoTac,
            "Chi tiết dịch vụ",
            NutDinhDang.KieuNut.TRONG_SUOT,
            this::laySpaChiTiet
        );

        themNut(
            pnlCacThaoTac,
            "Thêm dịch vụ",
            NutDinhDang.KieuNut.PHU,
            this::themDichVu
        );

        themNut(
            pnlCacThaoTac,
            "Bắt đầu làm",
            NutDinhDang.KieuNut.CHINH,
            () -> SpaService.layTrangThaiSpa(
                layDongChon(),
                "DANG_THUC_HIEN"
            )
        );

        themNut(
            pnlCacThaoTac,
            "Hoàn thành",
            NutDinhDang.KieuNut.THANH_CONG,
            () -> SpaService.layTrangThaiSpa(
                layDongChon(),
                "HOAN_TAT"
            )
        );

        themNut(
            pnlCacThaoTac,
            "Thanh toán",
            NutDinhDang.KieuNut.THANH_CONG,
            () -> quyetToan("SPA")
        );

        ganPhimTat(KeyEvent.VK_F4, () -> tabWorkspace.setSelectedIndex(1));

        ganSuKienDupChuot(() -> {
            int row = tblBang.getSelectedRow();
            if (row < 0) return;
            int dong = tblBang.convertRowIndexToModel(row);
            String trangThai = String.valueOf(moHinh.getValueAt(dong, 7));
            switch (trangThai) {
                case "TIEP_NHAN" -> SpaService.layTrangThaiSpa(layDongChon(), "DANG_THUC_HIEN");
                case "DANG_THUC_HIEN" -> SpaService.layTrangThaiSpa(layDongChon(), "HOAN_TAT");
                case "HOAN_TAT" -> quyetToan("SPA");
                default -> laySpaChiTiet();
            }
        });

        ganMenuChuotPhai(row -> {
            int dong = tblBang.convertRowIndexToModel(row);
            String trangThai = String.valueOf(moHinh.getValueAt(dong, 7));
            JPopupMenu popup = new JPopupMenu();
            if ("TIEP_NHAN".equals(trangThai)) {
                JMenuItem itemBatDau = new JMenuItem("Bắt đầu làm");
                itemBatDau.addActionListener(e -> SpaService.layTrangThaiSpa(layDongChon(), "DANG_THUC_HIEN"));
                popup.add(itemBatDau);
                JMenuItem itemThemDv = new JMenuItem("Thêm dịch vụ");
                itemThemDv.addActionListener(e -> themDichVu());
                popup.add(itemThemDv);
            } else if ("DANG_THUC_HIEN".equals(trangThai)) {
                JMenuItem itemHoanThanh = new JMenuItem("Đánh dấu hoàn thành");
                itemHoanThanh.addActionListener(e -> SpaService.layTrangThaiSpa(layDongChon(), "HOAN_TAT"));
                popup.add(itemHoanThanh);
                JMenuItem itemThemDv = new JMenuItem("Thêm dịch vụ");
                itemThemDv.addActionListener(e -> themDichVu());
                popup.add(itemThemDv);
            } else if ("HOAN_TAT".equals(trangThai)) {
                JMenuItem itemThanhToan = new JMenuItem("Thanh toán (POS)");
                itemThanhToan.addActionListener(e -> quyetToan("SPA"));
                popup.add(itemThanhToan);
            }
            if (popup.getComponentCount() > 0) popup.addSeparator();
            JMenuItem itemChiTiet = new JMenuItem("Chi tiết dịch vụ");
            itemChiTiet.addActionListener(e -> laySpaChiTiet());
            popup.add(itemChiTiet);
            JMenuItem itemPhieuCS = new JMenuItem("Phiếu chăm sóc");
            itemPhieuCS.addActionListener(e -> com.petstation.gui.components.common.NhatKyChamSocDialog.mo(this, "SPA", layDongChon()));
            popup.add(itemPhieuCS);
            return popup;
        });

        // Chuyển đổi giao diện danh sách gốc thành Panel riêng trong Tabbed Workspace
        Component compNorth = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
        Component compCenter = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        removeAll();

        JPanel pnlDanhSach = new JPanel(new BorderLayout(0, 12));
        pnlDanhSach.setOpaque(false);
        if (compNorth != null) pnlDanhSach.add(compNorth, BorderLayout.NORTH);
        if (compCenter != null) pnlDanhSach.add(compCenter, BorderLayout.CENTER);

        // Khởi tạo các màn hình thành phần nhúng trực tiếp
        pnlLapPhieu = new LapPhieuSpaPanel(
            id -> {
                banHang.getQuyTrinh().chonSpa(id);
                HopThoai.hienThiThanhCong(this, "Đã tạo phiếu tiếp nhận SPA #" + id + " thành công!");
                lamMoi();
                tabWorkspace.setSelectedIndex(0);
            },
            () -> tabWorkspace.setSelectedIndex(2),
            () -> {
                taiDanhSachPhieuTreo();
                tabWorkspace.setSelectedIndex(3);
            }
        );

        pnlLichHen = new LichHenSpaPanel(
            banHang,
            dieuHuong,
            idLichHen -> {
                pnlLapPhieu.nhanLichHen(idLichHen);
                tabWorkspace.setSelectedIndex(1);
            }
        );

        JPanel pnlVungPhieuTreo = taoVungPhieuTreo();

        tabWorkspace.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        tabWorkspace.addTab("Danh sách phiếu Spa", pnlDanhSach);
        tabWorkspace.addTab("Lập phiếu tiếp nhận mới (F4)", pnlLapPhieu);
        tabWorkspace.addTab("Lịch hẹn Spa", pnlLichHen);
        tabWorkspace.addTab("Phiếu Spa đang treo", pnlVungPhieuTreo);

        tabWorkspace.addChangeListener(e -> {
            int idx = tabWorkspace.getSelectedIndex();
            if (idx == 0) {
                lamMoi();
            } else if (idx == 3) {
                taiDanhSachPhieuTreo();
            }
        });

        add(tabWorkspace, BorderLayout.CENTER);
    }

    private JPanel taoVungPhieuTreo() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);
        pnl.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        moHinhPhieuTreo = new DefaultTableModel(
                new String[] {"ID", "Mã phiếu treo", "Khách hàng", "Số điện thoại", "Thú cưng", "Dịch vụ", "Cập nhật lúc"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JScrollPane scr = taoBangDinhDang(moHinhPhieuTreo);
        tblPhieuTreo = layBang(scr);
        tblPhieuTreo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPhieuTreo.removeColumn(tblPhieuTreo.getColumnModel().getColumn(0));

        JPanel pnlThaoTacTreo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlThaoTacTreo.setOpaque(false);

        NutDinhDang btnMoLai = new NutDinhDang("Mở lại phiếu trên biểu mẫu", NutDinhDang.KieuNut.CHINH);
        btnMoLai.addActionListener(e -> {
            int row = tblPhieuTreo.getSelectedRow();
            if (row < 0) {
                HopThoai.hienThiCanhBao(this, "Vui lòng chọn một phiếu đang treo từ danh sách.");
                return;
            }
            long id = ((Number) moHinhPhieuTreo.getValueAt(tblPhieuTreo.convertRowIndexToModel(row), 0)).longValue();
            pnlLapPhieu.khoiPhucPhieu(id);
            tabWorkspace.setSelectedIndex(1);
        });

        NutDinhDang btnXoa = new NutDinhDang("Xóa phiếu treo", NutDinhDang.KieuNut.NGUY_HIEM);
        btnXoa.addActionListener(e -> {
            int row = tblPhieuTreo.getSelectedRow();
            if (row < 0) {
                HopThoai.hienThiCanhBao(this, "Vui lòng chọn một phiếu đang treo để xóa.");
                return;
            }
            long id = ((Number) moHinhPhieuTreo.getValueAt(tblPhieuTreo.convertRowIndexToModel(row), 0)).longValue();
            if (HopThoai.xacNhan(this, "Bạn có chắc muốn xóa phiếu Spa đang treo này không?")) {
                PhieuSpaTreoService.xoa(id);
                taiDanhSachPhieuTreo();
                HopThoai.hienThiThanhCong(this, "Đã xóa phiếu treo.");
            }
        });

        NutDinhDang btnLamMoiTreo = new NutDinhDang("Làm mới", NutDinhDang.KieuNut.TRONG_SUOT);
        btnLamMoiTreo.addActionListener(e -> taiDanhSachPhieuTreo());

        pnlThaoTacTreo.add(btnMoLai);
        pnlThaoTacTreo.add(btnXoa);
        pnlThaoTacTreo.add(btnLamMoiTreo);

        pnl.add(pnlThaoTacTreo, BorderLayout.NORTH);
        pnl.add(scr, BorderLayout.CENTER);
        return pnl;
    }

    private void taiDanhSachPhieuTreo() {
        if (moHinhPhieuTreo == null) return;
        moHinhPhieuTreo.setRowCount(0);
        try {
            var ds = PhieuSpaTreoService.danhSach();
            for (Object[] r : ds) {
                moHinhPhieuTreo.addRow(r);
            }
        } catch (RuntimeException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
        }
    }

    @Override
    protected void tiepNhan() {
        tabWorkspace.setSelectedIndex(1);
    }
}
