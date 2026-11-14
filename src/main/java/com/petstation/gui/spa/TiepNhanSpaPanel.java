package com.petstation.gui.spa;

import com.petstation.dto.GioHang;
import com.petstation.dto.SpaDTO;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.luutru.NhatKyChamSocDialog;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.spa.PhieuSpaTreoService;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

// Màn hình: Tiếp nhận & Lập phiếu Spa (Mã: TIEP_NHAN_SPA)
// Luồng xử lý: GUI -> TiepNhanSpaService, PhieuSpaTreoService -> PhieuTiepNhanSpaDAOImpl -> SQL
// (phieu_tiep_nhan_spa)
// Mô tả: Tiếp nhận thú cưng làm dịch vụ spa, chọn gói chăm sóc, cân nặng và ghi chú yêu cầu
// Phân hệ: spa
public final class TiepNhanSpaPanel extends NghiepVuSpaPanel {
    private final PhieuSpaTreoService phieuSpaTreoService = new PhieuSpaTreoService();
    private final JTabbedPane tabWorkspace = new JTabbedPane();
    private LapPhieuSpaPanel khungLapPhieu;
    private LichHenSpaPanel khungLichHen;
    private DefaultTableModel moHinhPhieuTreo;
    private JTable bangPhieuTreo;

    public TiepNhanSpaPanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.TIEP_NHAN_SPA, banHang, dieuHuong);
        cauHinhThanhTacVuVaSuKien(banHang, dieuHuong);
    }

    private void cauHinhThanhTacVuVaSuKien(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        themNut(
                layKhungThaoTac(),
                "Chi tiết dịch vụ",
                NutDinhDang.KieuNut.TRONG_SUOT,
                this::laySpaChiTiet);
        themNut(layKhungThaoTac(), "Thêm dịch vụ", NutDinhDang.KieuNut.PHU, this::themDichVu);
        themNut(
                layKhungThaoTac(),
                "Bắt đầu làm",
                NutDinhDang.KieuNut.CHINH,
                () -> capNhatTrangThaiSpaNen(TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB()));
        themNut(
                layKhungThaoTac(),
                "Hoàn thành",
                NutDinhDang.KieuNut.THANH_CONG,
                () -> capNhatTrangThaiSpaNen(TrangThaiPhieuSpa.HOAN_TAT.maDB()));
        themNut(
                layKhungThaoTac(),
                "Thanh toán",
                NutDinhDang.KieuNut.THANH_CONG,
                () -> quyetToan("SPA"));
        ganPhimTat(KeyEvent.VK_F4, () -> tabWorkspace.setSelectedIndex(1));
        ganSuKienDupChuot(this::xuLyDupChuotBangSpa);
        ganMenuChuotPhai(this::taoMenuChuotPhaiSpa);
        xayDungCacTabSpa(banHang, dieuHuong);
    }

    private void xuLyDupChuotBangSpa() {
        int row = layBangDuLieu().getSelectedRow();
        if (row < 0) return;
        String trangThai =
                String.valueOf(
                        layMoHinh().getValueAt(layBangDuLieu().convertRowIndexToModel(row), 7));
        if (TrangThaiPhieuSpa.TIEP_NHAN.maDB().equals(trangThai)) {
            capNhatTrangThaiSpaNen(TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB());
        } else if (TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB().equals(trangThai)) {
            capNhatTrangThaiSpaNen(TrangThaiPhieuSpa.HOAN_TAT.maDB());
        } else if (TrangThaiPhieuSpa.HOAN_TAT.maDB().equals(trangThai)) {
            quyetToan("SPA");
        } else {
            laySpaChiTiet();
        }
    }

    private JPopupMenu taoMenuChuotPhaiSpa(int row) {
        String trangThai =
                String.valueOf(
                        layMoHinh().getValueAt(layBangDuLieu().convertRowIndexToModel(row), 7));
        JPopupMenu popup = new JPopupMenu();
        if (TrangThaiPhieuSpa.TIEP_NHAN.maDB().equals(trangThai)) {
            popup.add(
                    taoMucMenu(
                            "Bắt đầu làm",
                            () -> capNhatTrangThaiSpaNen(TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB())));
            popup.add(taoMucMenu("Thêm dịch vụ", this::themDichVu));
        } else if (TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB().equals(trangThai)) {
            popup.add(
                    taoMucMenu(
                            "Đánh dấu hoàn thành",
                            () -> capNhatTrangThaiSpaNen(TrangThaiPhieuSpa.HOAN_TAT.maDB())));
            popup.add(taoMucMenu("Thêm dịch vụ", this::themDichVu));
        } else if (TrangThaiPhieuSpa.HOAN_TAT.maDB().equals(trangThai)) {
            popup.add(taoMucMenu("Thanh toán (POS)", () -> quyetToan("SPA")));
        }
        if (popup.getComponentCount() > 0) popup.addSeparator();
        popup.add(taoMucMenu("Chi tiết dịch vụ", this::laySpaChiTiet));
        popup.add(
                taoMucMenu(
                        "Phiếu chăm sóc",
                        () -> {
                            NhatKyChamSocDialog.mo(this, "SPA", layDongChon());
                            lamMoi();
                        }));
        return popup;
    }

    private JMenuItem taoMucMenu(String nhan, Runnable hanhDong) {
        JMenuItem item = new JMenuItem(nhan);
        item.addActionListener(
                e -> {
                    try {
                        hanhDong.run();
                    } catch (RuntimeException loi) {
                        HopThoai.hienThiCanhBao(this, loi.getMessage());
                    }
                });
        return item;
    }

    private void xayDungCacTabSpa(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        Component compNorth = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
        Component compCenter = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        removeAll();
        JPanel khungDanhSach = new JPanel(new BorderLayout(0, 12));
        khungDanhSach.setOpaque(false);
        if (compNorth != null) khungDanhSach.add(compNorth, BorderLayout.NORTH);
        if (compCenter != null) khungDanhSach.add(compCenter, BorderLayout.CENTER);
        khungLapPhieu =
                new LapPhieuSpaPanel(
                        maPhieu -> {
                            layBanHang().getQuyTrinh().chonSpa(maPhieu);
                            HopThoai.hienThiThanhCong(
                                    this,
                                    "Đã tạo phiếu tiếp nhận SPA #" + maPhieu + " thành công!");
                            lamMoi();
                            tabWorkspace.setSelectedIndex(0);
                        },
                        () -> tabWorkspace.setSelectedIndex(2),
                        () -> {
                            taiDanhSachPhieuTreo();
                            tabWorkspace.setSelectedIndex(3);
                        });
        khungLichHen =
                new LichHenSpaPanel(
                        banHang,
                        dieuHuong,
                        maLichHen -> {
                            khungLapPhieu.nhanLichHen(maLichHen);
                            tabWorkspace.setSelectedIndex(1);
                        });
        tabWorkspace.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        tabWorkspace.addTab("Danh sách phiếu Spa", khungDanhSach);
        tabWorkspace.addTab("Lập phiếu tiếp nhận mới (F4)", khungLapPhieu);
        tabWorkspace.addTab("Lịch hẹn Spa", khungLichHen);
        tabWorkspace.addTab("Phiếu Spa đang treo", taoVungPhieuTreo());
        tabWorkspace.addChangeListener(e -> khiChuyenTabSpa());
        add(tabWorkspace, BorderLayout.CENTER);
    }

    @Override
    public void xuLyHienThi() {
        khiChuyenTabSpa();
    }

    @Override
    public void xuLyLamMoi() {
        int idx = tabWorkspace.getSelectedIndex();
        if (idx == 0) super.xuLyLamMoi();
        else if (idx == 1) khungLapPhieu.xuLyLamMoi();
        else if (idx == 2) khungLichHen.xuLyLamMoi();
        else if (idx == 3) taiDanhSachPhieuTreo();
    }

    private void khiChuyenTabSpa() {
        int idx = tabWorkspace.getSelectedIndex();
        if (idx == 0) lamMoi();
        else if (idx == 1) khungLapPhieu.xuLyHienThi();
        else if (idx == 2) khungLichHen.xuLyHienThi();
        else if (idx == 3) taiDanhSachPhieuTreo();
    }

    private JPanel taoVungPhieuTreo() {
        JPanel khungTam = new JPanel(new BorderLayout(0, 10));
        khungTam.setOpaque(false);
        khungTam.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        moHinhPhieuTreo =
                new DefaultTableModel(
                        new String[] {
                            "ID",
                            "Mã phiếu treo",
                            "Khách hàng",
                            "Số điện thoại",
                            "Thú cưng",
                            "Dịch vụ",
                            "Cập nhật lúc"
                        },
                        0) {
                    @Override
                    public boolean isCellEditable(int r, int c) {
                        return false;
                    }
                };
        JScrollPane scr = taoBangDinhDang(moHinhPhieuTreo);
        bangPhieuTreo = layBang(scr);
        bangPhieuTreo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangPhieuTreo.removeColumn(bangPhieuTreo.getColumnModel().getColumn(0));
        JPanel khungThaoTacTreo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        khungThaoTacTreo.setOpaque(false);
        NutDinhDang btnMoLai =
                new NutDinhDang("Mở lại phiếu trên biểu mẫu", NutDinhDang.KieuNut.CHINH);
        btnMoLai.addActionListener(e -> moLaiPhieuTreoChon());
        NutDinhDang btnXoa = new NutDinhDang("Xóa phiếu treo", NutDinhDang.KieuNut.NGUY_HIEM);
        btnXoa.addActionListener(e -> xoaPhieuTreoChon());
        khungThaoTacTreo.add(btnMoLai);
        khungThaoTacTreo.add(btnXoa);
        khungTam.add(khungThaoTacTreo, BorderLayout.NORTH);
        khungTam.add(scr, BorderLayout.CENTER);
        return khungTam;
    }

    private String layMaPhieuTreoChon(String thongBaoChon) {
        int row = bangPhieuTreo.getSelectedRow();
        if (row < 0) {
            HopThoai.hienThiCanhBao(this, thongBaoChon);
            return null;
        }
        return String.valueOf(
                moHinhPhieuTreo.getValueAt(bangPhieuTreo.convertRowIndexToModel(row), 0));
    }

    private void moLaiPhieuTreoChon() {
        String ma = layMaPhieuTreoChon("Vui lòng chọn một phiếu đang treo từ danh sách.");
        if (ma == null) return;
        khungLapPhieu.khoiPhucPhieu(ma);
        tabWorkspace.setSelectedIndex(1);
    }

    private void capNhatTrangThaiSpaNen(String trangThaiMoi) {
        int dongChon = layBangDuLieu().getSelectedRow();
        if (dongChon < 0) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn một phiếu Spa cần cập nhật.");
            return;
        }
        String ma = layDongChon();
        String trangThaiHienTai =
                String.valueOf(
                        layMoHinh()
                                .getValueAt(layBangDuLieu().convertRowIndexToModel(dongChon), 7));
        boolean batDauLam = TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB().equals(trangThaiMoi);
        String yeuCauHienTai =
                batDauLam
                        ? TrangThaiPhieuSpa.TIEP_NHAN.maDB()
                        : TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB();
        if (!yeuCauHienTai.equals(trangThaiHienTai)) {
            HopThoai.hienThiCanhBao(
                    this,
                    batDauLam
                            ? "Chỉ phiếu ở trạng thái Tiếp nhận mới có thể Bắt đầu làm."
                            : "Chỉ phiếu Đang thực hiện mới có thể đánh dấu Hoàn thành.");
            return;
        }
        String loiNhac =
                batDauLam
                        ? "Xác nhận bắt đầu thực hiện phiếu Spa #" + ma + "?"
                        : "Xác nhận đã hoàn thành phiếu Spa #" + ma + "?";
        if (!HopThoai.xacNhan(this, loiNhac)) return;
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> {
                    spaService.layTrangThaiSpa(ma, trangThaiMoi);
                    return null;
                },
                null,
                v -> lamMoi(),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private void xoaPhieuTreoChon() {
        String ma = layMaPhieuTreoChon("Vui lòng chọn một phiếu đang treo để xóa.");
        if (ma == null) return;
        if (HopThoai.xacNhan(this, "Bạn có chắc muốn xóa phiếu Spa đang treo này không?")) {
            var phien = PhienDangNhapService.macDinh().layPhienHienTai();
            TacVuNen.chayCoPhien(
                    phien,
                    () -> true,
                    () -> {
                        phieuSpaTreoService.xoa(ma);
                        return null;
                    },
                    null,
                    v -> {
                        taiDanhSachPhieuTreo();
                        HopThoai.hienThiThanhCong(this, "Đã xóa phiếu treo.");
                    },
                    ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
        }
    }

    private void taiDanhSachPhieuTreo() {
        if (moHinhPhieuTreo == null) return;
        moHinhPhieuTreo.setRowCount(0);
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                phieuSpaTreoService::danhSach,
                null,
                ds -> {
                    moHinhPhieuTreo.setRowCount(0);
                    for (SpaDTO.DongPhieuSpaTreo d : ds) {
                        var p = d.getPhieuTreo();
                        String maPhieu = p != null && p.getMaPhieu() != null ? p.getMaPhieu() : "";
                        String hoTenKhach =
                                p != null
                                                && p.getKhachHang() != null
                                                && p.getKhachHang().getHoTen() != null
                                                && !p.getKhachHang().getHoTen().isBlank()
                                        ? p.getKhachHang().getHoTen()
                                        : "Chưa chọn khách";
                        String sdt =
                                p != null && p.getSoDienThoai() != null ? p.getSoDienThoai() : "";
                        String tenThuCung =
                                p != null
                                                && p.getThuCung() != null
                                                && p.getThuCung().getTen() != null
                                                && !p.getThuCung().getTen().isBlank()
                                        ? p.getThuCung().getTen()
                                        : "Chưa chọn thú cưng";
                        String tenGoi =
                                p != null
                                                && p.getGoiDichVu() != null
                                                && p.getGoiDichVu().getTenGoi() != null
                                                && !p.getGoiDichVu().getTenGoi().isBlank()
                                        ? p.getGoiDichVu().getTenGoi()
                                        : "Chưa chọn dịch vụ";
                        moHinhPhieuTreo.addRow(
                                new Object[] {
                                    d.getMaKhoa(),
                                    maPhieu,
                                    hoTenKhach,
                                    sdt,
                                    tenThuCung,
                                    tenGoi,
                                    p != null ? p.getCapNhatLuc() : null
                                });
                    }
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    public void chonTab(int index) {
        if (index >= 0 && index < tabWorkspace.getTabCount()) {
            tabWorkspace.setSelectedIndex(index);
        }
    }

    @Override
    protected void tiepNhan() {
        tabWorkspace.setSelectedIndex(1);
    }
}
