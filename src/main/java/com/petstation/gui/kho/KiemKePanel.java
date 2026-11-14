package com.petstation.gui.kho;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiBienBanKiemKe;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.kho.KiemKeService;

import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

// Màn hình: Kiểm kê định kỳ kho (Mã: KIEM_KE)
// Luồng xử lý: GUI -> KiemKeService -> KiemKeDAOImpl -> SQL (bien_ban_kiem_ke,
// chi_tiet_kiem_ke, lo_hang)
// Mô tả: Lập biên bản kiểm kê, đối chiếu số lượng tồn sổ sách và thực
// tế, ghi nhận chênh lệch
// Phân hệ: kho
public final class KiemKePanel extends NghiepVuKhoPanel {
    private final KiemKeService kiemKeService = new KiemKeService();
    private DefaultTableModel moHinhChiTiet;
    private JTable bangChiTiet;
    private final JLabel nhanTieuDeChiTiet =
            new JLabel("Chi tiết đợt kiểm kê: (Chưa chọn biên bản)");
    private final JLabel nhanThongKeChiTiet = new JLabel("");
    private NutDinhDang btnCanBang;
    private String bienBanHienTaiMa;

    public KiemKePanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.KIEM_KE, banHang, dieuHuong);
        themNut(
                layKhungThaoTac(),
                "+ Lập đợt kiểm kê",
                NutDinhDang.KieuNut.CHINH,
                this::moKhongGianKiemKe);
        btnCanBang = taoNutCanBangKho();
        ganSuKienDupChuot(this::xemChiTietBienBan);
        JScrollPane scrChiTiet = taoBangChiTietKiemKe();
        ganVungChiTietKiemKe(scrChiTiet);
        dangKyChonDongBienBan(btnCanBang);
    }

    private NutDinhDang taoNutCanBangKho() {
        if (!PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.XU_LY_PHE_DUYET)) {
            return null;
        }
        NutDinhDang btnCanBang =
                themNut(
                        layKhungThaoTac(),
                        "Cân bằng kho (Duyệt)",
                        NutDinhDang.KieuNut.THANH_CONG,
                        this::thucHienCanBangKho);
        btnCanBang.setEnabled(false);
        return btnCanBang;
    }

    private JScrollPane taoBangChiTietKiemKe() {
        String[] cotChiTiet = {
            "ID",
            "Mã SP",
            "Tên sản phẩm",
            "Số lô",
            "Hạn dùng",
            "Tồn hệ thống",
            "Tồn thực tế",
            "Chênh lệch",
            "Lý do chênh lệch"
        };
        moHinhChiTiet = DinhDangBang.taoMoHinhChiDoc(cotChiTiet);
        JScrollPane scrChiTiet = taoBangDinhDang(moHinhChiTiet);
        bangChiTiet = layBang(scrChiTiet);
        bangChiTiet.removeColumn(bangChiTiet.getColumnModel().getColumn(0));
        bangChiTiet.getColumnModel().getColumn(6).setCellRenderer(taoTrinhVeChenhLech());
        return scrChiTiet;
    }

    private static DefaultTableCellRenderer taoTrinhVeChenhLech() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
                Component c =
                        super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected && value != null) {
                    try {
                        int val = Integer.parseInt(value.toString());
                        if (val < 0) {
                            c.setForeground(MauUngDung.DO_BAO_LOI);
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else if (val > 0) {
                            c.setForeground(MauUngDung.XANH_DUONG_CHINH);
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(MauUngDung.XAM_PHU);
                            setFont(getFont().deriveFont(Font.PLAIN));
                        }
                    } catch (NumberFormatException ex) {
                        LoggerFactory.getLogger(KiemKePanel.class)
                                .debug("Giá trị chênh lệch kiểm kê không phải số '{}'.", value, ex);
                    }
                }
                return c;
            }
        };
    }

    private void ganVungChiTietKiemKe(JScrollPane scrChiTiet) {
        JPanel khungDuoi = new JPanel(new BorderLayout(0, 8));
        khungDuoi.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, MauUngDung.XAM_VIEN_NHE),
                        new EmptyBorder(8, 0, 0, 0)));
        JPanel khungTieuDeChiTiet = new JPanel(new BorderLayout());
        nhanTieuDeChiTiet.setFont(nhanTieuDeChiTiet.getFont().deriveFont(Font.BOLD, 13f));
        nhanThongKeChiTiet.setForeground(MauUngDung.XAM_PHU);
        nhanThongKeChiTiet.setFont(nhanThongKeChiTiet.getFont().deriveFont(12f));
        khungTieuDeChiTiet.add(nhanTieuDeChiTiet, BorderLayout.WEST);
        khungTieuDeChiTiet.add(nhanThongKeChiTiet, BorderLayout.EAST);
        khungDuoi.add(khungTieuDeChiTiet, BorderLayout.NORTH);
        khungDuoi.add(scrChiTiet, BorderLayout.CENTER);
        BorderLayout layout = (BorderLayout) getLayout();
        Component centerComp = layout.getLayoutComponent(BorderLayout.CENTER);
        if (centerComp != null) {
            remove(centerComp);
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centerComp, khungDuoi);
            splitPane.setResizeWeight(0.5);
            splitPane.setContinuousLayout(true);
            splitPane.setBorder(null);
            add(splitPane, BorderLayout.CENTER);
        }
    }

    private void dangKyChonDongBienBan(NutDinhDang refBtnCanBang) {
        layBangDuLieu()
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (e.getValueIsAdjusting()) return;
                            if (layBangDuLieu().getSelectedRow() < 0) {
                                if (refBtnCanBang != null) refBtnCanBang.setEnabled(false);
                                return;
                            }
                            if (refBtnCanBang != null) {
                                String tt = layNoiDungDongChon(3);
                                refBtnCanBang.setEnabled(
                                        "DANG_KIEM".equalsIgnoreCase(tt)
                                                || "Đang kiểm".equalsIgnoreCase(tt)
                                                || "Đang kiểm kê".equalsIgnoreCase(tt)
                                                || TrangThaiBienBanKiemKe.CHO_DUYET
                                                        .maDB()
                                                        .equalsIgnoreCase(tt)
                                                || "Chờ duyệt".equalsIgnoreCase(tt));
                            }
                            taiChiTietBienBan(layNoiDungDongChon(1));
                        });
    }

    private void taiChiTietBienBan(String maBienBan) {
        if (maBienBan == null || maBienBan.isBlank()) {
            return;
        }
        bienBanHienTaiMa = maBienBan;
        moHinhChiTiet.setRowCount(0);
        int row = layBangDuLieu().getSelectedRow();
        String soBB = row >= 0 ? layNoiDungDongChon(1) : maBienBan;
        String trangThai = row >= 0 ? layNoiDungDongChon(3) : "";
        nhanTieuDeChiTiet.setText(
                "Chi tiết đợt kiểm kê: "
                        + soBB
                        + " ("
                        + DinhDangBang.tenTrangThai(trangThai)
                        + ")");
        nhanThongKeChiTiet.setText("Đang tải chi tiết...");
        TacVuNen.chayCoPhien(
                this,
                () -> isDisplayable() && Objects.equals(maBienBan, bienBanHienTaiMa),
                () -> kiemKeService.layChiTietKiemKe(maBienBan),
                this::hienThiChiTietBienBan,
                ex ->
                        nhanThongKeChiTiet.setText(
                                "Không thể đọc chi tiết: " + TacVuNen.thongDiepLoi(ex)));
    }

    private void hienThiChiTietBienBan(List<Object[]> cacDong) {
        moHinhChiTiet.setRowCount(0);
        int demLech = 0;
        int tongChenhLech = 0;
        for (Object[] r : cacDong) {
            int chenhLech = ((Number) r[7]).intValue();
            if (chenhLech != 0) {
                demLech++;
                tongChenhLech += chenhLech;
            }
            moHinhChiTiet.addRow(
                    new Object[] {
                        r[0],
                        r[1],
                        r[2],
                        r[3],
                        r[4] == null ? "-" : r[4].toString(),
                        r[5],
                        r[6],
                        chenhLech > 0 ? ("+" + chenhLech) : String.valueOf(chenhLech),
                        r[8]
                    });
        }
        nhanThongKeChiTiet.setText(
                String.format(
                        "Tổng: %d mặt hàng | Lệch: %d mặt hàng (%s%d)",
                        cacDong.size(), demLech, tongChenhLech > 0 ? "+" : "", tongChenhLech));
    }

    private void thucHienCanBangKho() {
        if (!PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.XU_LY_PHE_DUYET)) {
            HopThoai.hienThiCanhBao(
                    this, "Chỉ Cửa hàng trưởng mới có quyền duyệt và cân bằng kho kiểm kê.");
            return;
        }
        if (bienBanHienTaiMa == null || bienBanHienTaiMa.isBlank()) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn một biên bản kiểm kê trong bảng.");
            return;
        }
        int row = layBangDuLieu().getSelectedRow();
        String soBB = row >= 0 ? layNoiDungDongChon(1) : bienBanHienTaiMa;
        String trangThai = row >= 0 ? layNoiDungDongChon(3) : "";
        if (TrangThaiBienBanKiemKe.DA_DUYET.maDB().equalsIgnoreCase(trangThai)
                || "Đã duyệt".equalsIgnoreCase(trangThai)) {
            HopThoai.hienThiThongTin(this, "Biên bản " + soBB + " đã được cân bằng kho trước đó.");
            return;
        }
        if (!HopThoai.xacNhanDongY(
                this,
                "Bạn có chắc muốn duyệt và cân bằng kho cho biên bản "
                        + soBB
                        + "?\n"
                        + "Số lượng tồn trong kho sẽ được điều chỉnh theo"
                        + " số lượng thực tế đã kiểm kê.",
                "Xác nhận cân bằng kho")) {
            return;
        }
        String maCanBang = bienBanHienTaiMa;
        if (btnCanBang != null) btnCanBang.setEnabled(false);
        TacVuNen.chayCoPhien(
                this,
                this::isDisplayable,
                () -> kiemKeService.duyetVaCanBangKho(maCanBang),
                () -> {
                    HopThoai.hienThiThanhCong(
                            this, "Đã cân bằng kho thành công cho biên bản " + soBB);
                    lamMoi();
                    taiChiTietBienBan(maCanBang);
                },
                ex -> {
                    if (btnCanBang != null) btnCanBang.setEnabled(true);
                    HopThoai.hienThiLoi(this, "Lỗi cân bằng kho: " + TacVuNen.thongDiepLoi(ex));
                });
    }

    private void xemChiTietBienBan() {
        if (bienBanHienTaiMa == null || bienBanHienTaiMa.isBlank()) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn một biên bản kiểm kê để xem chi tiết.");
            return;
        }
        taiChiTietBienBan(bienBanHienTaiMa);
    }

    private static final class DuLieuKhoiTaoKiemKe {
        final List<Object[]> dsBanDau;
        final Set<String> cacNhom;
        final boolean macDinhMu;

        DuLieuKhoiTaoKiemKe(List<Object[]> dsBanDau, Set<String> cacNhom, boolean macDinhMu) {
            this.dsBanDau = dsBanDau;
            this.cacNhom = cacNhom;
            this.macDinhMu = macDinhMu;
        }
    }

    private void moKhongGianKiemKe() {
        TacVuNen.chayCoPhien(
                this,
                this::isDisplayable,
                () -> {
                    List<Object[]> dsBanDau =
                            kiemKeService.layDanhSachLoHangKiemKe("TẤT CẢ", false);
                    Set<String> cacNhom = new LinkedHashSet<>();
                    cacNhom.add("TẤT CẢ");
                    for (Object[] r : dsBanDau) {
                        if (r[7] != null && !r[7].toString().isBlank())
                            cacNhom.add(r[7].toString());
                    }
                    boolean macDinhMu =
                            "true"
                                    .equalsIgnoreCase(
                                            CaiDatService.macDinh()
                                                    .layTuyChonHoacMacDinh(
                                                            "MAC_DINH_KIEM_KE_MU", "false")
                                                    .trim());
                    return new DuLieuKhoiTaoKiemKe(dsBanDau, cacNhom, macDinhMu);
                },
                duLieu -> hienThiHopThoaiKiemKe(duLieu.dsBanDau, duLieu.cacNhom, duLieu.macDinhMu),
                ex ->
                        HopThoai.hienThiLoi(
                                this,
                                "Không thể mở không gian kiểm kê: " + TacVuNen.thongDiepLoi(ex)));
    }

    private void hienThiHopThoaiKiemKe(
            List<Object[]> dsBanDau, Set<String> cacNhom, boolean macDinhMu) {
        JDialog dlg =
                new JDialog(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        "WORKSPACE KIỂM KÊ TỒN KHO THEO ĐỢT",
                        true);
        dlg.setSize(1120, 680);
        HopThoai.chuanHoaCuaSo(dlg, dlg.getOwner());
        dlg.setLayout(new BorderLayout(0, 10));
        JTextField oKyKiemKe =
                new JTextField(
                        "Kiểm kê T"
                                + YearMonth.now().getMonthValue()
                                + "/"
                                + YearMonth.now().getYear(),
                        14);
        JComboBox<String> cboNhomHang = new JComboBox<>(cacNhom.toArray(String[]::new));
        JTextField oTuKhoa = new JTextField(12);
        JCheckBox chkSapHetHan = new JCheckBox("Lô sắp hết hạn");
        NutDinhDang btnTaiDanhSach = new NutDinhDang("Lọc danh sách", NutDinhDang.KieuNut.PHU);
        JCheckBox hopChonKiemDemMu = new JCheckBox("Chế độ kiểm kê mù (Blind Count)", macDinhMu);
        JTextField oMaVach = new JTextField(22);
        JLabel nhanTrangThaiQuetMa = new JLabel("Sẵn sàng quét...");
        dlg.add(
                taoDauTrangKiemKe(
                        oKyKiemKe,
                        cboNhomHang,
                        oTuKhoa,
                        chkSapHetHan,
                        btnTaiDanhSach,
                        hopChonKiemDemMu,
                        oMaVach,
                        nhanTrangThaiQuetMa),
                BorderLayout.NORTH);
        DefaultTableModel moHinhWs = taoMoHinhBangKiemKe();
        JTable bangLamViec = taoBangKiemKe(moHinhWs);
        JScrollPane scrWs = new JScrollPane(bangLamViec);
        scrWs.setBorder(new EmptyBorder(0, 14, 0, 14));
        dlg.add(scrWs, BorderLayout.CENTER);
        JLabel nhanThongKeKhongGian = new JLabel("Tổng mặt hàng: 0 | Chênh lệch: 0");
        NutDinhDang btnLuu = new NutDinhDang("Lưu biên bản & Hoàn tất", NutDinhDang.KieuNut.CHINH);
        dlg.add(taoChanTrangKiemKe(dlg, nhanThongKeKhongGian, btnLuu), BorderLayout.SOUTH);
        Runnable capNhatThongKe = () -> capNhatThongKeKiemKe(moHinhWs, nhanThongKeKhongGian);
        ganBoLangNgheBangKiemKe(dlg, moHinhWs, capNhatThongKe);
        Runnable taiDuLieu =
                () ->
                        taiDuLieuKiemKe(
                                dlg, moHinhWs, cboNhomHang, chkSapHetHan, oTuKhoa, capNhatThongKe);
        btnTaiDanhSach.addActionListener(e -> taiDuLieu.run());
        cboNhomHang.addActionListener(e -> taiDuLieu.run());
        chkSapHetHan.addActionListener(e -> taiDuLieu.run());
        oTuKhoa.addActionListener(e -> taiDuLieu.run());
        hopChonKiemDemMu.addActionListener(
                e -> apDungCheDoKiemKeMu(bangLamViec, hopChonKiemDemMu.isSelected()));
        oMaVach.addKeyListener(
                taoBoQuetBarcodeKiemKe(oMaVach, moHinhWs, bangLamViec, nhanTrangThaiQuetMa));
        btnLuu.addActionListener(
                e -> luuBienBanKiemKe(dlg, btnLuu, oKyKiemKe, moHinhWs, bangLamViec));
        dienDuLieuBangKiemKe(
                dsBanDau, moHinhWs, oTuKhoa.getText().trim().toLowerCase(), capNhatThongKe);
        apDungCheDoKiemKeMu(bangLamViec, hopChonKiemDemMu.isSelected());
        dlg.setVisible(true);
    }

    private JPanel taoDauTrangKiemKe(
            JTextField oKyKiemKe,
            JComboBox<String> cboNhomHang,
            JTextField oTuKhoa,
            JCheckBox chkSapHetHan,
            NutDinhDang btnTaiDanhSach,
            JCheckBox hopChonKiemDemMu,
            JTextField oMaVach,
            JLabel nhanTrangThaiQuetMa) {
        JPanel khungDauTrangChinh = new JPanel();
        khungDauTrangChinh.setLayout(new BoxLayout(khungDauTrangChinh, BoxLayout.Y_AXIS));
        khungDauTrangChinh.setBorder(new EmptyBorder(12, 14, 6, 14));
        JPanel khungHang1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        khungHang1.add(new JLabel("Kỳ kiểm kê:"));
        khungHang1.add(oKyKiemKe);
        khungHang1.add(new JLabel("Nhóm hàng:"));
        khungHang1.add(cboNhomHang);
        oTuKhoa.setToolTipText("Lọc theo mã SP, tên SP hoặc số lô");
        khungHang1.add(new JLabel("Tìm nhanh:"));
        khungHang1.add(oTuKhoa);
        khungHang1.add(chkSapHetHan);
        khungHang1.add(btnTaiDanhSach);
        hopChonKiemDemMu.setToolTipText(
                "Ẩn cột Tồn hệ thống và Chênh lệch để nhân viên đếm khách" + " quan");
        khungHang1.add(hopChonKiemDemMu);
        BoGocPanel khungHang2 = new BoGocPanel(10, Color.WHITE, true);
        khungHang2.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));
        khungHang2.setBorder(new EmptyBorder(4, 10, 4, 10));
        JLabel nhanQuetNhanh = new JLabel("Quét Barcode:");
        nhanQuetNhanh.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanQuetNhanh.setForeground(MauUngDung.NAU);
        oMaVach.setFont(new Font("Consolas", Font.PLAIN, 14));
        nhanTrangThaiQuetMa.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 12));
        nhanTrangThaiQuetMa.setForeground(MauUngDung.XAM_PHU);
        khungHang2.add(nhanQuetNhanh);
        khungHang2.add(oMaVach);
        khungHang2.add(nhanTrangThaiQuetMa);
        khungDauTrangChinh.add(khungHang1);
        khungDauTrangChinh.add(khungHang2);
        return khungDauTrangChinh;
    }

    private DefaultTableModel taoMoHinhBangKiemKe() {
        String[] cacCotWs = {
            "ID Lô",
            "Mã SP",
            "Tên sản phẩm",
            "Barcode",
            "Số lô",
            "Hạn dùng",
            "Tồn HT",
            "Tồn thực tế",
            "Chênh lệch",
            "Lý do chênh lệch"
        };
        return new DefaultTableModel(cacCotWs, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7 || column == 9;
            }
        };
    }

    private JTable taoBangKiemKe(DefaultTableModel moHinhWs) {
        JTable bangLamViec = new JTable(moHinhWs);
        bangLamViec.setRowHeight(30);
        bangLamViec.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        bangLamViec.getTableHeader().setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        bangLamViec.getTableHeader().setBackground(MauUngDung.XAM_NEN_NHAT);
        bangLamViec.removeColumn(bangLamViec.getColumnModel().getColumn(0));
        JComboBox<String> cboLyDo =
                new JComboBox<>(
                        new String[] {
                            "",
                            "Hư hỏng / Rách bao bì",
                            "Hết hạn sử dụng",
                            "Mất mát / Thất thoát",
                            "Sai lệch số lượng nhập",
                            "Nhầm lẫn hàng hóa",
                            "Khác"
                        });
        bangLamViec.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(cboLyDo));
        bangLamViec
                .getColumnModel()
                .getColumn(7)
                .setCellRenderer(
                        new DefaultTableCellRenderer() {
                            @Override
                            public Component getTableCellRendererComponent(
                                    JTable table,
                                    Object value,
                                    boolean isSelected,
                                    boolean hasFocus,
                                    int row,
                                    int column) {
                                Component c =
                                        super.getTableCellRendererComponent(
                                                table, value, isSelected, hasFocus, row, column);
                                setHorizontalAlignment(SwingConstants.CENTER);
                                if (!isSelected && value != null) {
                                    try {
                                        int diff = Integer.parseInt(value.toString());
                                        if (diff < 0) {
                                            c.setForeground(MauUngDung.DO_BAO_LOI);
                                            setFont(getFont().deriveFont(Font.BOLD));
                                        } else if (diff > 0) {
                                            c.setForeground(MauUngDung.XANH_DUONG_CHINH);
                                            setFont(getFont().deriveFont(Font.BOLD));
                                        } else {
                                            c.setForeground(MauUngDung.XAM_PHU);
                                            setFont(getFont().deriveFont(Font.PLAIN));
                                        }
                                    } catch (NumberFormatException ex) {
                                        LoggerFactory.getLogger(KiemKePanel.class)
                                                .debug(
                                                        "Giá trị chênh lệch chi tiết không phải số"
                                                                + " '{}'.",
                                                        value,
                                                        ex);
                                    }
                                }
                                return c;
                            }
                        });
        return bangLamViec;
    }

    private JPanel taoChanTrangKiemKe(
            JDialog dlg, JLabel nhanThongKeKhongGian, NutDinhDang btnLuu) {
        JPanel khungChanHopThoai = new JPanel(new BorderLayout(12, 0));
        khungChanHopThoai.setBackground(MauUngDung.NEN_KEM);
        khungChanHopThoai.setBorder(new EmptyBorder(10, 14, 14, 14));
        nhanThongKeKhongGian.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanThongKeKhongGian.setForeground(MauUngDung.NAU);
        khungChanHopThoai.add(nhanThongKeKhongGian, BorderLayout.WEST);
        JPanel khungNutChanTrang = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        khungNutChanTrang.setOpaque(false);
        NutDinhDang btnHuy = new NutDinhDang("Hủy bỏ", NutDinhDang.KieuNut.TRONG_SUOT);
        btnHuy.addActionListener(e -> dlg.dispose());
        khungNutChanTrang.add(btnHuy);
        khungNutChanTrang.add(btnLuu);
        khungChanHopThoai.add(khungNutChanTrang, BorderLayout.EAST);
        return khungChanHopThoai;
    }

    private void capNhatThongKeKiemKe(DefaultTableModel moHinhWs, JLabel nhanThongKeKhongGian) {
        int tongMatHang = moHinhWs.getRowCount();
        int soLuongLech = 0;
        int tongChenhLech = 0;
        for (int i = 0; i < tongMatHang; i++) {
            int tonHT = Integer.parseInt(moHinhWs.getValueAt(i, 6).toString());
            int tonTT = Integer.parseInt(moHinhWs.getValueAt(i, 7).toString());
            int diff = tonTT - tonHT;
            moHinhWs.setValueAt(diff, i, 8);
            if (diff != 0) {
                soLuongLech++;
                tongChenhLech += diff;
            }
        }
        nhanThongKeKhongGian.setText(
                String.format(
                        "Tổng: %d mặt hàng | Lệch: %d mặt hàng (%s%d)",
                        tongMatHang, soLuongLech, tongChenhLech > 0 ? "+" : "", tongChenhLech));
    }

    private void ganBoLangNgheBangKiemKe(
            JDialog dlg, DefaultTableModel moHinhWs, Runnable capNhatThongKe) {
        moHinhWs.addTableModelListener(
                e -> {
                    if (e.getColumn() == 7 && e.getFirstRow() >= 0) {
                        int row = e.getFirstRow();
                        try {
                            int tonTT = Integer.parseInt(moHinhWs.getValueAt(row, 7).toString());
                            if (tonTT < 0) {
                                HopThoai.hienThiCanhBao(dlg, "Tồn thực tế không được âm.");
                                moHinhWs.setValueAt(0, row, 7);
                                return;
                            }
                            int tonHT = Integer.parseInt(moHinhWs.getValueAt(row, 6).toString());
                            moHinhWs.setValueAt(tonTT - tonHT, row, 8);
                        } catch (NumberFormatException ex) {
                            HopThoai.hienThiCanhBao(dlg, "Vui lòng nhập số nguyên hợp lệ.");
                        }
                        capNhatThongKe.run();
                    }
                });
    }

    private void taiDuLieuKiemKe(
            JDialog dlg,
            DefaultTableModel moHinhWs,
            JComboBox<String> cboNhomHang,
            JCheckBox chkSapHetHan,
            JTextField oTuKhoa,
            Runnable capNhatThongKe) {
        String nhom = (String) cboNhomHang.getSelectedItem();
        boolean chiSapHetHan = chkSapHetHan.isSelected();
        String tuKhoa = oTuKhoa.getText().trim().toLowerCase();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                dlg::isDisplayable,
                () -> kiemKeService.layDanhSachLoHangKiemKe(nhom, chiSapHetHan),
                null,
                ds -> dienDuLieuBangKiemKe(ds, moHinhWs, tuKhoa, capNhatThongKe),
                ex ->
                        HopThoai.hienThiCanhBao(
                                dlg, "Không tải được danh sách: " + TacVuNen.thongDiepLoi(ex)));
    }

    private void dienDuLieuBangKiemKe(
            List<Object[]> ds, DefaultTableModel moHinhWs, String tuKhoa, Runnable capNhatThongKe) {
        moHinhWs.setRowCount(0);
        for (Object[] r : ds) {
            String maSP = String.valueOf(r[1]);
            String tenSP = String.valueOf(r[2]);
            String soLo = String.valueOf(r[4]);
            if (!tuKhoa.isEmpty()
                    && !maSP.toLowerCase().contains(tuKhoa)
                    && !tenSP.toLowerCase().contains(tuKhoa)
                    && !soLo.toLowerCase().contains(tuKhoa)) {
                continue;
            }
            int tonHT = ((Number) r[6]).intValue();
            moHinhWs.addRow(
                    new Object[] {
                        r[0],
                        r[1],
                        r[2],
                        r[3],
                        r[4],
                        r[5] == null ? "-" : r[5].toString(),
                        tonHT,
                        tonHT,
                        0,
                        ""
                    });
        }
        capNhatThongKe.run();
    }

    private void apDungCheDoKiemKeMu(JTable bangLamViec, boolean blind) {
        for (int i = 0; i < bangLamViec.getColumnCount(); i++) {
            String name = bangLamViec.getColumnName(i);
            if ("Tồn HT".equals(name) || "Chênh lệch".equals(name)) {
                TableColumn col = bangLamViec.getColumnModel().getColumn(i);
                col.setMinWidth(blind ? 0 : 70);
                col.setMaxWidth(blind ? 0 : 120);
                col.setPreferredWidth(blind ? 0 : 90);
            }
        }
    }

    private KeyAdapter taoBoQuetBarcodeKiemKe(
            JTextField oMaVach,
            DefaultTableModel moHinhWs,
            JTable bangLamViec,
            JLabel nhanTrangThaiQuetMa) {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) return;
                String code = oMaVach.getText().trim();
                if (code.isEmpty()) return;
                boolean timThay = false;
                for (int i = 0; i < moHinhWs.getRowCount(); i++) {
                    String barcode = moHinhWs.getValueAt(i, 3).toString();
                    String maSP = moHinhWs.getValueAt(i, 1).toString();
                    if (code.equalsIgnoreCase(barcode) || code.equalsIgnoreCase(maSP)) {
                        int tonHienTai = Integer.parseInt(moHinhWs.getValueAt(i, 7).toString());
                        moHinhWs.setValueAt(tonHienTai + 1, i, 7);
                        chonVaCuonDong(bangLamViec, i);
                        String tenSP = moHinhWs.getValueAt(i, 2).toString();
                        nhanTrangThaiQuetMa.setText(
                                "[OK] " + tenSP + " (Tồn TT: " + (tonHienTai + 1) + ")");
                        nhanTrangThaiQuetMa.setForeground(MauUngDung.mau(0x16A34A));
                        timThay = true;
                        break;
                    }
                }
                if (!timThay) {
                    nhanTrangThaiQuetMa.setText("[!] Không tìm thấy SP có Barcode: " + code);
                    nhanTrangThaiQuetMa.setForeground(MauUngDung.DO_BAO_LOI);
                }
                oMaVach.setText("");
            }
        };
    }

    private void luuBienBanKiemKe(
            JDialog dlg,
            NutDinhDang btnLuu,
            JTextField oKyKiemKe,
            DefaultTableModel moHinhWs,
            JTable bangLamViec) {
        if (moHinhWs.getRowCount() == 0) {
            HopThoai.hienThiCanhBao(dlg, "Chưa có mặt hàng nào trong danh sách kiểm kê.");
            return;
        }
        String ky = oKyKiemKe.getText().trim();
        if (ky.isEmpty()) {
            HopThoai.hienThiCanhBao(dlg, "Vui lòng nhập tên kỳ kiểm kê.");
            return;
        }
        List<KiemKeService.DongKiemKe> danhSach = new ArrayList<>();
        for (int i = 0; i < moHinhWs.getRowCount(); i++) {
            String maLo = String.valueOf(moHinhWs.getValueAt(i, 0));
            String maSP = moHinhWs.getValueAt(i, 1).toString();
            String tenSP = moHinhWs.getValueAt(i, 2).toString();
            String barcode = moHinhWs.getValueAt(i, 3).toString();
            String soLo = moHinhWs.getValueAt(i, 4).toString();
            String hsdStr = moHinhWs.getValueAt(i, 5).toString();
            LocalDate hsd = "-".equals(hsdStr) ? null : LocalDate.parse(hsdStr);
            int tonHT = Integer.parseInt(moHinhWs.getValueAt(i, 6).toString());
            int tonTT = Integer.parseInt(moHinhWs.getValueAt(i, 7).toString());
            String lyDo =
                    moHinhWs.getValueAt(i, 9) == null
                            ? ""
                            : moHinhWs.getValueAt(i, 9).toString().trim();
            if (tonTT != tonHT && lyDo.isEmpty()) {
                HopThoai.hienThiCanhBao(
                        dlg,
                        "Vui lòng chọn lý do chênh lệch cho sản phẩm: "
                                + maSP
                                + " ("
                                + tenSP
                                + ")");
                chonVaCuonDong(bangLamViec, i);
                return;
            }
            danhSach.add(
                    new KiemKeService.DongKiemKe(
                            maLo, maSP, tenSP, barcode, soLo, hsd, tonHT, tonTT, lyDo));
        }
        btnLuu.setEnabled(false);
        TacVuNen.chayCoPhien(
                dlg,
                dlg::isDisplayable,
                () -> kiemKeService.lapBienBanKiemKe(ky, danhSach),
                maBienBan -> {
                    HopThoai.hienThiThanhCong(
                            dlg,
                            "Đã lưu đợt kiểm kê thành công (ID: "
                                    + maBienBan
                                    + ")!\n"
                                    + "Nếu có mặt hàng chênh lệch, biên bản đã"
                                    + " được chuyển sang trạng thái CHỜ DUYỆT.");
                    dlg.dispose();
                    lamMoi();
                    taiChiTietBienBan(maBienBan);
                },
                ex -> {
                    btnLuu.setEnabled(true);
                    HopThoai.hienThiLoi(
                            dlg, "Lỗi khi lưu đợt kiểm kê: " + TacVuNen.thongDiepLoi(ex));
                });
    }

    private static void chonVaCuonDong(JTable bangLamViec, int modelRow) {
        int viewRow = bangLamViec.convertRowIndexToView(modelRow);
        if (viewRow >= 0) {
            bangLamViec.setRowSelectionInterval(viewRow, viewRow);
            bangLamViec.scrollRectToVisible(bangLamViec.getCellRect(viewRow, 0, true));
        }
    }
}
