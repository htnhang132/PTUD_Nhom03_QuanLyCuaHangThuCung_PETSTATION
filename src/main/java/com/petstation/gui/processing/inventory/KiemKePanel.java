package com.petstation.gui.processing.inventory;

import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.MaManHinh;
import com.petstation.model.Quyen;
import com.petstation.service.CaiDatService;
import com.petstation.service.KhoService;
import com.petstation.service.PhienDangNhapService;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Cung cấp giao diện Master-Detail Split-Pane cho kiểm kê tồn kho và Modal Workspace kiểm kê đa sản
 * phẩm với hỗ trợ máy quét Barcode và chế độ kiểm kê mù (Blind Count).
 */
public final class KiemKePanel extends NghiepVuKhoPanel {
    private final DefaultTableModel moHinhChiTiet;
    private final JTable tblChiTiet;
    private final JLabel lblTieuDeChiTiet =
            new JLabel("Chi tiết đợt kiểm kê: (Chưa chọn biên bản)");
    private final JLabel lblThongKeChiTiet = new JLabel("");
    private long bienBanHienTaiId = -1;

    public KiemKePanel(GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(LoaiNghiepVu.KIEM_KE, banHang, dieuHuong);

        // 1. Thêm các nút chức năng trên thanh công cụ
        themNut(pnlCacThaoTac, "+ Lập đợt kiểm kê", this::moWorkspaceKiemKe);
        if (PhienDangNhapService.layPhien().kiemTraCo(Quyen.XU_LY_PHE_DUYET)) {
            themNut(pnlCacThaoTac, "Cân bằng kho (Duyệt)", this::thucHienCanBangKho);
        }
        themNut(pnlCacThaoTac, "Xem chi tiết", this::xemChiTietBienBan);

        // 2. Chuẩn bị bảng Chi tiết kiểm kê ở Master-Detail
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
        moHinhChiTiet =
                new DefaultTableModel(cotChiTiet, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        JScrollPane scrChiTiet = taoBangDinhDang(moHinhChiTiet);
        tblChiTiet = layBang(scrChiTiet);
        tblChiTiet.removeColumn(tblChiTiet.getColumnModel().getColumn(0)); // Ẩn ID chi tiết

        // Renderer tô màu cho cột chênh lệch
        DefaultTableCellRenderer rendererChenhLech =
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
                                int val = Integer.parseInt(value.toString());
                                if (val < 0) {
                                    c.setForeground(new Color(220, 38, 38)); // Đỏ: Thất thoát
                                    setFont(getFont().deriveFont(Font.BOLD));
                                } else if (val > 0) {
                                    c.setForeground(new Color(37, 99, 235)); // Xanh dương: Dư thừa
                                    setFont(getFont().deriveFont(Font.BOLD));
                                } else {
                                    c.setForeground(new Color(100, 116, 139));
                                    setFont(getFont().deriveFont(Font.PLAIN));
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        return c;
                    }
                };
        // Cột chênh lệch là cột thứ 6 sau khi ẩn cột 0 ID
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(rendererChenhLech);

        // Panel chi tiết bên dưới
        JPanel pnlDuoi = new JPanel(new BorderLayout(0, 8));
        pnlDuoi.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                        new EmptyBorder(8, 0, 0, 0)));

        JPanel pnlTieuDeChiTiet = new JPanel(new BorderLayout());
        lblTieuDeChiTiet.setFont(lblTieuDeChiTiet.getFont().deriveFont(Font.BOLD, 13f));
        lblThongKeChiTiet.setForeground(new Color(100, 116, 139));
        lblThongKeChiTiet.setFont(lblThongKeChiTiet.getFont().deriveFont(12f));
        pnlTieuDeChiTiet.add(lblTieuDeChiTiet, BorderLayout.WEST);
        pnlTieuDeChiTiet.add(lblThongKeChiTiet, BorderLayout.EAST);

        pnlDuoi.add(pnlTieuDeChiTiet, BorderLayout.NORTH);
        pnlDuoi.add(scrChiTiet, BorderLayout.CENTER);

        // 3. Tái cấu trúc layout thành Split-Pane Master-Detail
        BorderLayout layout = (BorderLayout) getLayout();
        Component centerComp = layout.getLayoutComponent(BorderLayout.CENTER);
        if (centerComp != null) {
            remove(centerComp);
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centerComp, pnlDuoi);
            splitPane.setResizeWeight(0.5);
            splitPane.setContinuousLayout(true);
            splitPane.setBorder(null);
            add(splitPane, BorderLayout.CENTER);
        }

        // 4. Lắng nghe chọn dòng ở bảng biên bản kiểm kê để tải chi tiết
        tblBang.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (e.getValueIsAdjusting() || tblBang.getSelectedRow() < 0) return;
                            long id = layDongChon();
                            taiChiTietBienBan(id);
                        });
    }

    private void taiChiTietBienBan(long bienBanId) {
        bienBanHienTaiId = bienBanId;
        moHinhChiTiet.setRowCount(0);
        try {
            int row = tblBang.getSelectedRow();
            String soBB = row >= 0 ? tblBang.getValueAt(row, 0).toString() : ("#" + bienBanId);
            String trangThai = row >= 0 ? tblBang.getValueAt(row, 2).toString() : "";
            lblTieuDeChiTiet.setText("Chi tiết đợt kiểm kê: " + soBB + " (" + trangThai + ")");

            List<Object[]> cacDong = KhoService.layChiTietKiemKe(bienBanId);
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
            lblThongKeChiTiet.setText(
                    String.format(
                            "Tổng: %d mặt hàng | Lệch: %d mặt hàng (%s%d)",
                            cacDong.size(), demLech, tongChenhLech > 0 ? "+" : "", tongChenhLech));
        } catch (Exception ex) {
            lblThongKeChiTiet.setText("Không thể đọc chi tiết: " + ex.getMessage());
        }
    }

    private void thucHienCanBangKho() {
        if (!PhienDangNhapService.layPhien().kiemTraCo(Quyen.XU_LY_PHE_DUYET)) {
            HopThoai.hienThiCanhBao(
                    this, "Chỉ Cửa hàng trưởng mới có quyền duyệt và cân bằng kho kiểm kê.");
            return;
        }
        if (bienBanHienTaiId <= 0) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn một biên bản kiểm kê trong bảng.");
            return;
        }

        int row = tblBang.getSelectedRow();
        String soBB = row >= 0 ? tblBang.getValueAt(row, 0).toString() : ("#" + bienBanHienTaiId);
        String trangThai = row >= 0 ? tblBang.getValueAt(row, 2).toString() : "";

        if ("APPROVED".equalsIgnoreCase(trangThai) || "Đã duyệt".equalsIgnoreCase(trangThai)) {
            HopThoai.hienThiThongTin(this, "Biên bản " + soBB + " đã được cân bằng kho trước đó.");
            return;
        }

        int xacNhan =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn duyệt và cân bằng kho cho biên bản "
                                + soBB
                                + "?\n"
                                + "Số lượng tồn trong kho sẽ được điều chỉnh theo số lượng thực tế"
                                + " đã kiểm kê.",
                        "Xác nhận cân bằng kho",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

        if (xacNhan == JOptionPane.YES_OPTION) {
            try {
                KhoService.duyetVaCanBangKho(bienBanHienTaiId);
                HopThoai.hienThiThanhCong(this, "Đã cân bằng kho thành công cho biên bản " + soBB);
                lamMoi();
                taiChiTietBienBan(bienBanHienTaiId);
            } catch (Exception ex) {
                HopThoai.hienThiLoi(this, "Lỗi cân bằng kho: " + ex.getMessage());
            }
        }
    }

    private void xemChiTietBienBan() {
        if (bienBanHienTaiId <= 0) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn một biên bản kiểm kê để xem chi tiết.");
            return;
        }
        taiChiTietBienBan(bienBanHienTaiId);
    }

    /**
     * Mở Modal Workspace Kiểm kê hàng loạt với tính năng lọc phạm vi, quét Barcode liên tục và chế
     * độ kiểm kê mù (Blind Count).
     */
    private void moWorkspaceKiemKe() {
        JDialog dlg =
                new JDialog(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        "WORKSPACE KIỂM KÊ TỒN KHO THEO ĐỢT",
                        true);
        dlg.setSize(1120, 680);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(0, 10));

        // 1. Header Toolbar
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setBorder(new EmptyBorder(12, 14, 6, 14));

        // Hàng 1: Kỳ kiểm kê + Phạm vi
        JPanel pnlHang1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JTextField txtKyKiemKe =
                new JTextField(
                        "Kiểm kê T"
                                + YearMonth.now().getMonthValue()
                                + "/"
                                + YearMonth.now().getYear(),
                        14);
        pnlHang1.add(new JLabel("Kỳ kiểm kê:"));
        pnlHang1.add(txtKyKiemKe);

        Set<String> cacNhom = new LinkedHashSet<>();
        cacNhom.add("TẤT CẢ");
        for (Object[] r : KhoService.layDanhSachLoHangKiemKe("TẤT CẢ", false)) {
            if (r[7] != null && !r[7].toString().isBlank()) {
                cacNhom.add(r[7].toString());
            }
        }
        JComboBox<String> cboNhomHang = new JComboBox<>(cacNhom.toArray(String[]::new));
        pnlHang1.add(new JLabel("Nhóm hàng:"));
        pnlHang1.add(cboNhomHang);

        JTextField txtTuKhoa = new JTextField(12);
        txtTuKhoa.setToolTipText("Lọc theo mã SP, tên SP hoặc số lô");
        pnlHang1.add(new JLabel("Tìm nhanh:"));
        pnlHang1.add(txtTuKhoa);

        JCheckBox chkSapHetHan = new JCheckBox("Lô sắp hết hạn");
        pnlHang1.add(chkSapHetHan);

        NutDinhDang btnTaiDanhSach = new NutDinhDang("Lọc danh sách", NutDinhDang.KieuNut.PHU);
        pnlHang1.add(btnTaiDanhSach);

        boolean macDinhMu =
                "true".equalsIgnoreCase(
                        CaiDatService.layTuyChonHoacMacDinh("MAC_DINH_KIEM_KE_MU", "false").trim());
        JCheckBox chkBlindCount = new JCheckBox("Chế độ kiểm kê mù (Blind Count)", macDinhMu);
        chkBlindCount.setToolTipText(
                "Ẩn cột Tồn hệ thống và Chênh lệch để nhân viên đếm khách quan");
        pnlHang1.add(chkBlindCount);

        // Hàng 2: Barcode Scanner input
        JPanel pnlHang2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        pnlHang2.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        "Quét Barcode sản phẩm nhanh",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12),
                        new Color(30, 41, 59)));

        JTextField txtBarcode = new JTextField(22);
        txtBarcode.setFont(new Font("Consolas", Font.PLAIN, 14));
        JLabel lblBarcodeStatus = new JLabel("Sẵn sàng quét...");
        lblBarcodeStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblBarcodeStatus.setForeground(new Color(100, 116, 139));

        pnlHang2.add(new JLabel("Mã Barcode:"));
        pnlHang2.add(txtBarcode);
        pnlHang2.add(lblBarcodeStatus);

        pnlHeader.add(pnlHang1);
        pnlHeader.add(pnlHang2);
        dlg.add(pnlHeader, BorderLayout.NORTH);

        // 2. Bảng dữ liệu kiểm kê nhiều sản phẩm
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

        DefaultTableModel moHinhWs =
                new DefaultTableModel(cacCotWs, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column == 7 || column == 9;
                    }
                };

        JTable tblWs = new JTable(moHinhWs);
        tblWs.setRowHeight(28);
        tblWs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblWs.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblWs.getTableHeader().setBackground(new Color(241, 245, 249));

        // Ẩn cột ID Lô
        tblWs.removeColumn(tblWs.getColumnModel().getColumn(0));

        // Đặt combobox editor cho cột Lý do chênh lệch (sau khi ẩn ID Lô, cột Lý do là index 8)
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
        tblWs.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(cboLyDo));

        // Renderer tô màu chênh lệch
        tblWs.getColumnModel()
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
                                            c.setForeground(new Color(220, 38, 38));
                                            setFont(getFont().deriveFont(Font.BOLD));
                                        } else if (diff > 0) {
                                            c.setForeground(new Color(37, 99, 235));
                                            setFont(getFont().deriveFont(Font.BOLD));
                                        } else {
                                            c.setForeground(new Color(100, 116, 139));
                                            setFont(getFont().deriveFont(Font.PLAIN));
                                        }
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                                return c;
                            }
                        });

        JScrollPane scrWs = new JScrollPane(tblWs);
        scrWs.setBorder(new EmptyBorder(0, 14, 0, 14));
        dlg.add(scrWs, BorderLayout.CENTER);

        // Footer Toolbar
        JPanel pnlFooter = new JPanel(new BorderLayout(12, 0));
        pnlFooter.setBorder(new EmptyBorder(10, 14, 14, 14));

        JLabel lblThongKeWs = new JLabel("Tổng mặt hàng: 0 | Chênh lệch: 0");
        lblThongKeWs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlFooter.add(lblThongKeWs, BorderLayout.WEST);

        JPanel pnlNutFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        NutDinhDang btnHuy = new NutDinhDang("Hủy bỏ", NutDinhDang.KieuNut.TRONG_SUOT);
        NutDinhDang btnLuu = new NutDinhDang("Lưu biên bản & Hoàn tất", NutDinhDang.KieuNut.CHINH);
        btnHuy.addActionListener(e -> dlg.dispose());
        pnlNutFooter.add(btnHuy);
        pnlNutFooter.add(btnLuu);
        pnlFooter.add(pnlNutFooter, BorderLayout.EAST);

        dlg.add(pnlFooter, BorderLayout.SOUTH);

        // Helper tính toán chênh lệch và cập nhật thống kê
        Runnable capNhatThongKe =
                () -> {
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
                    lblThongKeWs.setText(
                            String.format(
                                     "Tổng: %d mặt hàng | Lệch: %d mặt hàng (%s%d)",
                                    tongMatHang,
                                    soLuongLech,
                                    tongChenhLech > 0 ? "+" : "",
                                    tongChenhLech));
                };

        // Lắng nghe sự kiện sửa cột Tồn thực tế trong bảng
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

        // Hàm tải danh sách lô hàng theo bộ lọc
        Runnable taiDuLieu =
                () -> {
                    moHinhWs.setRowCount(0);
                    String nhom = (String) cboNhomHang.getSelectedItem();
                    boolean chiSapHetHan = chkSapHetHan.isSelected();
                    String tuKhoa = txtTuKhoa.getText().trim().toLowerCase();
                    List<Object[]> cacLo = KhoService.layDanhSachLoHangKiemKe(nhom, chiSapHetHan);
                    for (Object[] r : cacLo) {
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
                };

        btnTaiDanhSach.addActionListener(e -> taiDuLieu.run());
        cboNhomHang.addActionListener(e -> taiDuLieu.run());
        chkSapHetHan.addActionListener(e -> taiDuLieu.run());
        txtTuKhoa.addActionListener(e -> taiDuLieu.run());

        // Xử lý bật/tắt Chế độ kiểm kê mù (Blind count)
        Runnable capNhatBlindCount =
                () -> {
                    boolean blind = chkBlindCount.isSelected();
                    for (int i = 0; i < tblWs.getColumnCount(); i++) {
                        String name = tblWs.getColumnName(i);
                        if ("Tồn HT".equals(name) || "Chênh lệch".equals(name)) {
                            TableColumn col = tblWs.getColumnModel().getColumn(i);
                            if (blind) {
                                col.setMinWidth(0);
                                col.setMaxWidth(0);
                                col.setPreferredWidth(0);
                            } else {
                                col.setMinWidth(70);
                                col.setMaxWidth(120);
                                col.setPreferredWidth(90);
                            }
                        }
                    }
                };
        chkBlindCount.addActionListener(e -> capNhatBlindCount.run());

        // Xử lý quét Barcode
        txtBarcode.addKeyListener(
                new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            String code = txtBarcode.getText().trim();
                            if (code.isEmpty()) return;

                            boolean timThay = false;
                            for (int i = 0; i < moHinhWs.getRowCount(); i++) {
                                String barcode = moHinhWs.getValueAt(i, 3).toString();
                                String maSP = moHinhWs.getValueAt(i, 1).toString();
                                if (code.equalsIgnoreCase(barcode) || code.equalsIgnoreCase(maSP)) {
                                    int tonHienTai =
                                            Integer.parseInt(moHinhWs.getValueAt(i, 7).toString());
                                    moHinhWs.setValueAt(tonHienTai + 1, i, 7);

                                    int viewRow = tblWs.convertRowIndexToView(i);
                                    tblWs.setRowSelectionInterval(viewRow, viewRow);
                                    tblWs.scrollRectToVisible(tblWs.getCellRect(viewRow, 0, true));

                                    String tenSP = moHinhWs.getValueAt(i, 2).toString();
                                    lblBarcodeStatus.setText(
                                            "✓ " + tenSP + " (Tồn TT: " + (tonHienTai + 1) + ")");
                                    lblBarcodeStatus.setForeground(new Color(22, 163, 74));
                                    timThay = true;
                                    break;
                                }
                            }

                            if (!timThay) {
                                lblBarcodeStatus.setText("⚠ Không tìm thấy SP có Barcode: " + code);
                                lblBarcodeStatus.setForeground(new Color(220, 38, 38));
                            }

                            txtBarcode.setText("");
                        }
                    }
                });

        // Xử lý lưu biên bản
        btnLuu.addActionListener(
                e -> {
                    if (moHinhWs.getRowCount() == 0) {
                        HopThoai.hienThiCanhBao(
                                dlg, "Chưa có mặt hàng nào trong danh sách kiểm kê.");
                        return;
                    }

                    String ky = txtKyKiemKe.getText().trim();
                    if (ky.isEmpty()) {
                        HopThoai.hienThiCanhBao(dlg, "Vui lòng nhập tên kỳ kiểm kê.");
                        return;
                    }

                    List<KhoService.DongKiemKe> danhSach = new ArrayList<>();
                    for (int i = 0; i < moHinhWs.getRowCount(); i++) {
                        long loHangId = ((Number) moHinhWs.getValueAt(i, 0)).longValue();
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
                            int viewRow = tblWs.convertRowIndexToView(i);
                            tblWs.setRowSelectionInterval(viewRow, viewRow);
                            tblWs.scrollRectToVisible(tblWs.getCellRect(viewRow, 0, true));
                            return;
                        }

                        danhSach.add(
                                new KhoService.DongKiemKe(
                                        loHangId, maSP, tenSP, barcode, soLo, hsd, tonHT, tonTT,
                                        lyDo));
                    }

                    try {
                        long bienBanId = KhoService.lapBienBanKiemKe(ky, danhSach);
                        HopThoai.hienThiThanhCong(
                                dlg,
                                "Đã lưu đợt kiểm kê thành công (ID: "
                                        + bienBanId
                                        + ")!\n"
                                        + "Nếu có mặt hàng chênh lệch, biên bản đã được chuyển sang"
                                        + " trạng thái CHỜ DUYỆT (PENDING).");
                        dlg.dispose();
                        lamMoi();
                        taiChiTietBienBan(bienBanId);
                    } catch (Exception ex) {
                        HopThoai.hienThiLoi(dlg, "Lỗi khi lưu đợt kiểm kê: " + ex.getMessage());
                    }
                });

        // Tải dữ liệu ban đầu và áp dụng chế độ mù mặc định từ Cài đặt
        taiDuLieu.run();
        capNhatBlindCount.run();
        dlg.setVisible(true);
    }
}
