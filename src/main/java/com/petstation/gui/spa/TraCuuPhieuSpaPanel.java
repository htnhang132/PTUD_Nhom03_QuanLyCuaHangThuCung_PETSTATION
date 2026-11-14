package com.petstation.gui.spa;

import com.petstation.dto.GioHang;
import com.petstation.dto.SpaDTO;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.BoChonKhoangNgay;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.service.spa.SpaService;

import java.awt.BorderLayout;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;

// Màn hình: Tra cứu phiếu Spa (Mã: TRA_CUU_PHIEU_SPA)
// Luồng xử lý: GUI -> TiepNhanSpaService -> PhieuTiepNhanSpaDAOImpl -> SQL (phieu_tiep_nhan_spa)
// Mô tả: Tìm kiếm và theo dõi tiến độ thực hiện các phiếu chăm sóc spa thú cưng
// Phân hệ: spa
public final class TraCuuPhieuSpaPanel extends NghiepVuSpaPanel {
    private final SpaService spaService = new SpaService();
    private final BoChonKhoangNgay khoangNgay = new BoChonKhoangNgay();

    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(
                    new String[] {
                        "Tất cả trạng thái",
                        "Đã tiếp nhận",
                        "Đang thực hiện",
                        "Hoàn thành",
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

    public TraCuuPhieuSpaPanel(GioHang gio, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.PHIEU_SPA, gio, dieuHuong);

        layOTimKiem().setToolTipText("Lọc nhanh mã phiếu, tên khách, thú cưng trong kết quả");

        khoangNgay.chonKy("30 ngày qua");
        khoangNgay.addPropertyChangeListener(
                "dateRange",
                e -> {
                    if (dangDatLaiBoLoc) return;
                    ghiNhanBoLoc();
                    taiLaiNen();
                });

        cboTrangThai.addActionListener(
                e -> {
                    if (dangDatLaiBoLoc) return;
                    ghiNhanBoLoc();
                    taiLaiNen();
                });

        layKhungThaoTac().add(khoangNgay);
        ((ThanhDanhSach) layKhungThaoTac()).themBoLoc("Trạng thái:", cboTrangThai);

        ghiNhanBoLoc();

        JPanel khungThaoTacPhieu = new ThanhDanhSach();
        khungThaoTacPhieu.setBorder(BorderFactory.createEmptyBorder());
        JPanel khungDauTrang = (JPanel) layKhungThaoTac().getParent();
        JPanel khungHaiHang = new JPanel(new BorderLayout(0, 8));
        khungHaiHang.setOpaque(false);
        khungDauTrang.remove(layKhungThaoTac());
        khungHaiHang.add(layKhungThaoTac(), BorderLayout.CENTER);
        khungHaiHang.add(khungThaoTacPhieu, BorderLayout.SOUTH);
        khungDauTrang.add(khungHaiHang, BorderLayout.CENTER);

        var btnChiTiet = themNut(khungThaoTacPhieu, "Chi tiết dịch vụ", this::laySpaChiTiet);

        var btnMoXuLy =
                themNut(
                        khungThaoTacPhieu,
                        "Mở xử lý",
                        () -> {
                            gio.getQuyTrinh().chonSpa(layDongChon());
                            dieuHuong.accept(MaManHinh.QUAN_LY_SPA);
                        });

        themNut(khungThaoTacPhieu, "In danh sách", this::inDanhSach);

        Runnable capNhatNut =
                () -> {
                    boolean coChon = layBangDuLieu().getSelectedRow() >= 0;
                    btnChiTiet.setEnabled(coChon);
                    btnMoXuLy.setEnabled(coChon);
                };
        layBangDuLieu()
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting()) capNhatNut.run();
                        });
        capNhatNut.run();
        ganSuKienDupChuot(this::laySpaChiTiet);
    }

    private void inDanhSach() {
        try {
            var job = PrinterJob.getPrinterJob();
            job.setJobName("Danh sách phiếu Spa");
            job.setPrintable(
                    layBangDuLieu()
                            .getPrintable(
                                    JTable.PrintMode.FIT_WIDTH,
                                    new MessageFormat("Danh sách phiếu Spa"),
                                    new MessageFormat("Trang {0}")));
            if (!job.printDialog()) return;
            TacVuNen.chay(
                    () -> {
                        job.print();
                        return null;
                    },
                    v -> {},
                    ex ->
                            HopThoai.hienThiCanhBao(
                                    this, "Không thể in: " + TacVuNen.thongDiepLoi(ex)));
        } catch (Exception ex) {
            HopThoai.hienThiCanhBao(this, "Không thể in: " + ex.getMessage());
        }
    }

    private void ghiNhanBoLoc() {
        LocalDate tuNgay = khoangNgay.layNgayBatDau();
        LocalDate denNgay = khoangNgay.layNgayKetThuc();

        if ((tuNgay == null) != (denNgay == null)) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn đủ Từ ngày và Đến ngày.");
            return;
        }
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            HopThoai.hienThiCanhBao(this, "Từ ngày không được lớn hơn Đến ngày.");
            return;
        }

        int idx = cboTrangThai.getSelectedIndex();
        String trangThai = (idx >= 0 && idx < MA_TRANG_THAI.length) ? MA_TRANG_THAI[idx] : "";
        boLoc = new BoLoc(tuNgay, denNgay, trangThai);
    }

    @Override
    protected List<Object[]> layDuLieu() {
        BoLoc loc = boLoc;
        List<SpaDTO.DongTimKiemSpa> ds =
                (loc == null)
                        ? spaService.timKiem(LocalDate.now().minusDays(29), LocalDate.now(), "")
                        : spaService.timKiem(loc.tuNgay, loc.denNgay, loc.trangThai);
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
    }
}
