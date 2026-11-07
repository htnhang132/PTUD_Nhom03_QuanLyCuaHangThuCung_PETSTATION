package com.petstation.gui.system;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.BoChonKhoangNgay;
import com.petstation.gui.components.common.DinhDangBang;
import com.petstation.gui.components.common.XuatExcel;
import com.petstation.service.NhatKyHeThongService;

import java.awt.*;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public final class NhatKyHeThongPanel extends CoSoPanel {
    private JTable tblBang;

    private DefaultTableModel moHinhBang;

    private TableRowSorter<DefaultTableModel> boSapXep;

    private BoChonKhoangNgay pnlKhoangNgayBoChon;

    private JComboBox<String> cboSuKienKieuHopChon;

    private JComboBox<String> cboKetQuaHopChon;

    private JTextField txtTruongNguoiDungTimKiem;

    private JLabel lblNhanCacChiSo;

    public NhatKyHeThongPanel() {
        setLayout(new BorderLayout());

        JPanel pnlNoiDung = new JPanel(new BorderLayout(0, 12));
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlTieuDe = taoTieuDe();
        JPanel pnlBoLocVungHienThi = taoBoLocVungHienThi();
        JPanel pnlDauTrang = taoVungTrongSuot(new BorderLayout(0, 10));
        pnlDauTrang.add(pnlTieuDe, BorderLayout.NORTH);
        pnlDauTrang.add(pnlBoLocVungHienThi, BorderLayout.CENTER);
        pnlNoiDung.add(pnlDauTrang, BorderLayout.NORTH);

        JPanel pnlBangVungHienThi = taoBangVungHienThi();
        pnlNoiDung.add(pnlBangVungHienThi, BorderLayout.CENTER);

        JPanel pnlChanTrang = taoChanTrang();
        pnlNoiDung.add(pnlChanTrang, BorderLayout.SOUTH);

        add(pnlNoiDung, BorderLayout.CENTER);

        pnlKhoangNgayBoChon.chonKy("7 ngày qua");
        cboSuKienKieuHopChon.addActionListener(e -> apDungCacBoLoc());
        cboKetQuaHopChon.addActionListener(e -> apDungCacBoLoc());
        taiLai();
        NhatKyHeThongService.themBoLangNghe(
                () ->
                        SwingUtilities.invokeLater(
                                () -> {
                                    if (isShowing()
                                            && com.petstation.service.PhienDangNhapService
                                                            .layPhienHienTai()
                                                    != null) taiLai();
                                }));
    }

    private boolean dangKhoiTao;

    private JPanel taoTieuDe() {
        JPanel pnlTieuDe = new JPanel(new BorderLayout(12, 8));
        pnlTieuDe.setOpaque(false);

        JPanel pnlTraiMuc = new JPanel();
        pnlTraiMuc.setOpaque(false);
        pnlTraiMuc.setLayout(new BoxLayout(pnlTraiMuc, BoxLayout.Y_AXIS));

        JLabel lblTieuDe = taoTieuDeMuc("Nhật ký hệ thống");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        pnlTraiMuc.add(lblTieuDe);
        pnlTraiMuc.add(Box.createVerticalStrut(5));

        JLabel lblTieuDePhu = new JLabel("Tra cứu lịch sử thao tác và sự kiện bảo mật");
        lblTieuDePhu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        lblTieuDePhu.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlTraiMuc.add(lblTieuDePhu);
        pnlTraiMuc.add(Box.createVerticalStrut(5));

        lblNhanCacChiSo = new JLabel("Đang hiển thị: 0 sự kiện");
        lblNhanCacChiSo.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblNhanCacChiSo.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlTraiMuc.add(lblNhanCacChiSo);

        pnlTieuDe.add(pnlTraiMuc, BorderLayout.WEST);

        JPanel pnlPhaiMuc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlPhaiMuc.setOpaque(false);

        NutDinhDang btnXuatNut = new NutDinhDang("Xuất CSV", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuatNut.setToolTipText("Xuất kết quả tìm kiếm ra file CSV");
        btnXuatNut.addActionListener(suKien -> xuLyXuat());
        pnlPhaiMuc.add(btnXuatNut);

        pnlTieuDe.add(pnlPhaiMuc, BorderLayout.EAST);

        return pnlTieuDe;
    }

    private JPanel taoBoLocVungHienThi() {
        JPanel pnlVungHienThi = layThe(new BorderLayout(0, 10));

        JLabel lblTieuDeBoLoc = new JLabel("Bộ lọc tra cứu");
        lblTieuDeBoLoc.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        pnlVungHienThi.add(lblTieuDeBoLoc, BorderLayout.NORTH);

        JPanel pnlBoLocNoiDung = new JPanel(new GridBagLayout());
        pnlBoLocNoiDung.setOpaque(false);

        GridBagConstraints rangBuocBoCuc = new GridBagConstraints();
        rangBuocBoCuc.insets = new Insets(5, 5, 5, 5);
        rangBuocBoCuc.anchor = GridBagConstraints.WEST;
        rangBuocBoCuc.fill = GridBagConstraints.HORIZONTAL;

        rangBuocBoCuc.gridx = 0;
        rangBuocBoCuc.gridy = 0;
        rangBuocBoCuc.gridwidth = 4;
        rangBuocBoCuc.weightx = 1.0;

        pnlKhoangNgayBoChon = new BoChonKhoangNgay();
        pnlBoLocNoiDung.add(pnlKhoangNgayBoChon, rangBuocBoCuc);

        rangBuocBoCuc.gridy = 1;
        rangBuocBoCuc.gridwidth = 1;
        rangBuocBoCuc.weightx = 0.25;

        JPanel pnlSuKienKieuVungHienThi = new JPanel(new BorderLayout(5, 0));
        pnlSuKienKieuVungHienThi.setOpaque(false);
        pnlSuKienKieuVungHienThi.add(new JLabel("Loại sự kiện:"), BorderLayout.WEST);

        String[] suKienCacKieu = {
            "Tất cả",
            "LOGIN - Đăng nhập / Đăng xuất",
            "CREATE - Thêm mới",
            "UPDATE - Sửa",
            "DELETE - Xóa / Ngừng hoạt động",
            "APPROVAL - Phê duyệt",
            "INVENTORY - Kho",
            "PAYMENT - Thanh toán / Đổi trả",
            "SYSTEM - Hệ thống"
        };
        cboSuKienKieuHopChon = new JComboBox<>(suKienCacKieu);
        cboSuKienKieuHopChon.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        pnlSuKienKieuVungHienThi.add(cboSuKienKieuHopChon, BorderLayout.CENTER);

        rangBuocBoCuc.gridx = 0;
        pnlBoLocNoiDung.add(pnlSuKienKieuVungHienThi, rangBuocBoCuc);

        JPanel pnlKetQuaVungHienThi = new JPanel(new BorderLayout(5, 0));
        pnlKetQuaVungHienThi.setOpaque(false);
        pnlKetQuaVungHienThi.add(new JLabel("Kết quả:"), BorderLayout.WEST);

        String[] cacKetQua = {"Tất cả", "Thành công", "Thất bại", "Cảnh báo"};
        cboKetQuaHopChon = new JComboBox<>(cacKetQua);
        cboKetQuaHopChon.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        pnlKetQuaVungHienThi.add(cboKetQuaHopChon, BorderLayout.CENTER);

        rangBuocBoCuc.gridx = 1;
        pnlBoLocNoiDung.add(pnlKetQuaVungHienThi, rangBuocBoCuc);

        JPanel pnlNguoiDungVungHienThi = new JPanel(new BorderLayout(5, 0));
        pnlNguoiDungVungHienThi.setOpaque(false);
        pnlNguoiDungVungHienThi.add(new JLabel("Người dùng:"), BorderLayout.WEST);

        txtTruongNguoiDungTimKiem = new JTextField();
        txtTruongNguoiDungTimKiem.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        txtTruongNguoiDungTimKiem.setToolTipText("Tìm theo tên người dùng");
        pnlNguoiDungVungHienThi.add(txtTruongNguoiDungTimKiem, BorderLayout.CENTER);

        rangBuocBoCuc.gridx = 2;
        pnlBoLocNoiDung.add(pnlNguoiDungVungHienThi, rangBuocBoCuc);

        JPanel pnlThaoTacVungHienThi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlThaoTacVungHienThi.setOpaque(false);

        NutDinhDang btnXoaNut = new NutDinhDang("Xóa bộ lọc", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXoaNut.addActionListener(suKien -> xoaCacBoLoc());
        pnlThaoTacVungHienThi.add(btnXoaNut);

        NutDinhDang btnApDungNut = new NutDinhDang("Áp dụng", NutDinhDang.KieuNut.CHINH);
        btnApDungNut.addActionListener(suKien -> apDungCacBoLoc());
        pnlThaoTacVungHienThi.add(btnApDungNut);

        rangBuocBoCuc.gridx = 3;
        rangBuocBoCuc.weightx = 0.0;
        rangBuocBoCuc.fill = GridBagConstraints.NONE;
        pnlBoLocNoiDung.add(pnlThaoTacVungHienThi, rangBuocBoCuc);

        pnlVungHienThi.add(pnlBoLocNoiDung, BorderLayout.CENTER);

        return pnlVungHienThi;
    }

    private JPanel taoBangVungHienThi() {
        JPanel pnlVungHienThi = layThe(new BorderLayout(0, 10));

        JPanel pnlBangTieuDe = new JPanel(new BorderLayout());
        pnlBangTieuDe.setOpaque(false);
        pnlBangTieuDe.add(taoTieuDeMuc("Kết quả tra cứu"), BorderLayout.WEST);

        JLabel lblGoiY = new JLabel("Click vào dòng để xem chi tiết");
        lblGoiY.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlBangTieuDe.add(lblGoiY, BorderLayout.EAST);

        pnlVungHienThi.add(pnlBangTieuDe, BorderLayout.NORTH);

        String[] cacCot = {
            "Thời gian", "Người dùng", "Hành động", "Loại sự kiện", "Kết quả", "IP Address"
        };

        Object[][] duLieu = new Object[0][];

        moHinhBang =
                new DefaultTableModel(duLieu, cacCot) {
                    @Override
                    public boolean isCellEditable(int dong, int cot) {
                        return false;
                    }
                };

        tblBang = new JTable(moHinhBang);
        DinhDangBang.apDung(tblBang);

        boSapXep = new TableRowSorter<>(moHinhBang);
        tblBang.setRowSorter(boSapXep);

        tblBang.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        (tblBang2, giaTri, laDaChon, coTieuDiem, dong, cot) -> {
                            JLabel lblNhan = new JLabel(giaTri.toString());
                            lblNhan.setOpaque(true);
                            lblNhan.setHorizontalAlignment(SwingConstants.CENTER);
                            lblNhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));

                            if (giaTri.equals("Thành công")) {
                                lblNhan.setBackground(new Color(212, 237, 218));
                                lblNhan.setForeground(new Color(21, 87, 36));
                            } else
                            if (giaTri.equals("Thất bại")) {
                                lblNhan.setBackground(new Color(248, 215, 218));
                                lblNhan.setForeground(new Color(114, 28, 36));
                            } else
                            if (giaTri.equals("Cảnh báo")) {
                                lblNhan.setBackground(new Color(255, 243, 205));
                                lblNhan.setForeground(new Color(133, 100, 4));
                            }

                            if (laDaChon) {
                                lblNhan.setBackground(tblBang.getSelectionBackground());
                                lblNhan.setForeground(tblBang.getSelectionForeground());
                            }

                            return lblNhan;
                        });

        tblBang.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        (tblBang2, giaTri, laDaChon, coTieuDiem, dong, cot) -> {
                            JLabel lblNhan = new JLabel(giaTri.toString());
                            lblNhan.setOpaque(true);
                            lblNhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));

                            String chuoi = giaTri.toString();
                            if (chuoi.contains("WARNING")
                                    || chuoi.contains("ERROR")
                                    || chuoi.contains("DELETE")) {
                                lblNhan.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
                            } else
                            if (chuoi.contains("LOGIN") || chuoi.contains("LOGOUT")) {
                                lblNhan.setForeground(MauUngDung.CHINH);
                            } else {
                                lblNhan.setForeground(MauUngDung.VAN_BAN_TOI);
                            }

                            if (laDaChon) {
                                lblNhan.setBackground(tblBang.getSelectionBackground());
                                lblNhan.setForeground(tblBang.getSelectionForeground());
                            } else {
                                lblNhan.setBackground(Color.WHITE);
                            }

                            return lblNhan;
                        });

        tblBang.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent suKien) {
                        if (suKien.getClickCount() == 2) {
                            int dong = tblBang.getSelectedRow();
                            if (dong >= 0) {
                                xuLyHienThiChiTiet(dong);
                            }
                        }
                    }
                });

        JScrollPane scrCuonKhung = new JScrollPane(tblBang);
        scrCuonKhung.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlVungHienThi.add(scrCuonKhung, BorderLayout.CENTER);

        return pnlVungHienThi;
    }

    private JPanel taoChanTrang() {
        JPanel pnlChanTrang = new JPanel(new BorderLayout());
        pnlChanTrang.setBackground(MauUngDung.CANH_BAO_NEN);
        pnlChanTrang.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lblGhiChu =
                new JLabel(
                        "Nhật ký được lưu trên SQL Server. Chọn khoảng thời gian và bộ lọc để tra"
                                + " cứu.");
        lblGhiChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblGhiChu.setForeground(MauUngDung.NAU);
        pnlChanTrang.add(lblGhiChu, BorderLayout.WEST);

        return pnlChanTrang;
    }

    private void apDungCacBoLoc() {
        taiLai();
    }

    private void taiLai() {
        LocalDate tu = pnlKhoangNgayBoChon.layNgayBatDau();

        LocalDate den = pnlKhoangNgayBoChon.layNgayKetThuc();

        String kieu = String.valueOf(cboSuKienKieuHopChon.getSelectedItem());

        String kieuKhoa = kieu.startsWith("Tất cả") ? "" : kieu.split(" ")[0];
        String ketQua = String.valueOf(cboKetQuaHopChon.getSelectedItem());

        String nguoiDung = txtTruongNguoiDungTimKiem.getText().trim().toLowerCase();

        moHinhBang.setRowCount(0);

        java.util.List<NhatKyHeThongService.Muc> cacMuc =
                new java.util.ArrayList<>(NhatKyHeThongService.layCacMuc());

        int cacCanhBao = 0;
        for (NhatKyHeThongService.Muc suKien : cacMuc) {
            LocalDate ngayDangXet = suKien.thoiGian().toLocalDate();
            if (tu != null && ngayDangXet.isBefore(tu)) continue;
            if (den != null && ngayDangXet.isAfter(den)) continue;
            if (!kieuKhoa.isEmpty() && !kieuKhoa.equals(suKien.kieu())) continue;
            if (!ketQua.equals("Tất cả") && !ketQua.equals(suKien.ketQua())) continue;
            if (!nguoiDung.isEmpty() && !suKien.nguoiDung().toLowerCase().contains(nguoiDung))
                continue;
            if ("Cảnh báo".equals(suKien.ketQua()) || "Thất bại".equals(suKien.ketQua()))
                cacCanhBao++;
            moHinhBang.addRow(
                    new Object[] {
                        suKien.layVanBanThoiGian(),
                        suKien.nguoiDung(),
                        suKien.thaoTac()
                                + (suKien.doiTuong().isBlank() ? "" : ": " + suKien.doiTuong()),
                        suKien.kieu(),
                        suKien.ketQua(),
                        "Máy hiện tại"
                    });
        }
        lblNhanCacChiSo.setText(
                "Đang hiển thị: "
                        + moHinhBang.getRowCount()
                        + " sự kiện • "
                        + cacCanhBao
                        + " cảnh báo/thất bại");
    }

    private void xoaCacBoLoc() {
        pnlKhoangNgayBoChon.chonKy("7 ngày qua");
        cboSuKienKieuHopChon.setSelectedIndex(0);
        cboKetQuaHopChon.setSelectedIndex(0);
        txtTruongNguoiDungTimKiem.setText("");
        taiLai();
    }

    private void xuLyHienThiChiTiet(int dong) {
        int moHinhDong = tblBang.convertRowIndexToModel(dong);

        String thoiGian = moHinhBang.getValueAt(moHinhDong, 0).toString();

        String nguoiDung = moHinhBang.getValueAt(moHinhDong, 1).toString();

        String thaoTac = moHinhBang.getValueAt(moHinhDong, 2).toString();

        String kieuSuKien = moHinhBang.getValueAt(moHinhDong, 3).toString();
        String ketQua = moHinhBang.getValueAt(moHinhDong, 4).toString();

        String demTrong = moHinhBang.getValueAt(moHinhDong, 5).toString();

        String chiTiet =
                String.format(
                        "CHI TIẾT SỰ KIỆN\n\n"
                                + "═══════════════════════════\n"
                                + "Thời gian: %s\n"
                                + "Người dùng: %s\n"
                                + "Hành động: %s\n"
                                + "Loại: %s\n"
                                + "Kết quả: %s\n"
                                + "IP Address: %s\n"
                                + "═══════════════════════════\n\n"
                                + "Nhật ký chỉ đọc, không thể sửa hoặc xóa.",
                        thoiGian, nguoiDung, thaoTac, kieuSuKien, ketQua, demTrong);

        HopThoai.hienThiThongBaoHopThoai(
                this, chiTiet, "Chi tiết Audit Log", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyXuat() {
        XuatExcel.xuatBang(this, tblBang, "Audit_Log");
    }

    private void dieuHuongQuayLai() {
        HopThoai.hienThiThongBaoHopThoai(
                this,
                "Chức năng quay lại sẽ được triển khai khi có navigation system",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void dieuHuongDenTrangChinh() {
        HopThoai.hienThiThongBaoHopThoai(
                this,
                "Chức năng về màn hình chính sẽ được triển khai khi có navigation system",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void xuLyHienThi() {
        taiLai();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Nhật ký hệ thống";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Hướng dẫn tra cứu Audit Log:\n\n"
                + "【 Bộ lọc 】\n"
                + "• Date Range: BẮT BUỘC phải chọn khoảng thời gian\n"
                + "→ Database có hàng triệu bản ghi, không thể load hết\n"
                + "• Loại sự kiện: Lọc theo LOGIN, PAYMENT, DELETE...\n"
                + "• Kết quả: Lọc Thành công/Thất bại/Cảnh báo\n"
                + "• Người dùng: Tìm theo tên\n\n"
                + "【 Tra soát gian lận 】\n"
                + "Các loại sự kiện quan trọng cần kiểm tra:\n"
                + "• DELETE: Xóa hóa đơn, xóa dữ liệu\n"
                + "• WARNING/ERROR: Cảnh báo bất thường\n"
                + "• LOGIN thất bại nhiều lần: Nghi vấn tấn công\n\n"
                + "【 Export 】\n"
                + "Xuất kết quả ra CSV để phân tích chi tiết\n\n"
                + "Mẹo: Double-click vào dòng để xem chi tiết đầy đủ.";
    }
}
