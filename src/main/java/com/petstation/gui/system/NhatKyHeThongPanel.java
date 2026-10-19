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

/**
 * Cung cấp giao diện và xử lý tương tác cho nhật ký hệ thống nhật ký vùng hiển thị.
 *
 * <p>Panel Nhật ký hệ thống (Audit Log) Với Date Range Picker và Event Kieu Filter - BẮT BUỘC cho
 * database lớn
 */
public final class NhatKyHeThongPanel extends CoSoPanel {
    /** Bảng. */
    private JTable tblBang;

    /** Mô hình bảng. */
    private DefaultTableModel moHinhBang;

    /** Bộ sắp xếp. */
    private TableRowSorter<DefaultTableModel> boSapXep;

    /** Khoảng ngày bộ chọn. */
    private BoChonKhoangNgay pnlKhoangNgayBoChon;

    /** Sự kiện kiểu hộp chọn. */
    private JComboBox<String> cboSuKienKieuHopChon;

    /** Kết quả hộp chọn. */
    private JComboBox<String> cboKetQuaHopChon;

    /** Trường người dùng tìm kiếm. */
    private JTextField txtTruongNguoiDungTimKiem;

    /** Nhãn các chỉ số. */
    private JLabel lblNhanCacChiSo;

    /** Khởi tạo NhatKyHeThongPanel với các giá trị được cung cấp. */
    public NhatKyHeThongPanel() {
        setLayout(new BorderLayout());

        // Nội dung chính
        // Nội dung.
        JPanel pnlNoiDung = new JPanel(new BorderLayout(0, 12));
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tiêu đề và các chỉ số thống kê
        // Tiêu đề.
        JPanel pnlTieuDe = taoTieuDe();
        pnlNoiDung.add(pnlTieuDe, BorderLayout.NORTH);

        // Khung bộ lọc
        // Bộ lọc vùng hiển thị.
        JPanel pnlBoLocVungHienThi = taoBoLocVungHienThi();
        pnlNoiDung.add(pnlBoLocVungHienThi, BorderLayout.NORTH);

        // Bảng dữ liệu
        // Bảng vùng hiển thị.
        JPanel pnlBangVungHienThi = taoBangVungHienThi();
        pnlNoiDung.add(pnlBangVungHienThi, BorderLayout.CENTER);

        // Chân trang với ghi chú
        // Chân trang.
        JPanel pnlChanTrang = taoChanTrang();
        pnlNoiDung.add(pnlChanTrang, BorderLayout.SOUTH);

        add(pnlNoiDung, BorderLayout.CENTER);

        taiLai();
        NhatKyHeThongService.themBoLangNghe(() -> SwingUtilities.invokeLater(this::taiLai));
    }

    /** Đang khởi tạo. */
    private boolean dangKhoiTao;

    /**
     * Tạo tiêu đề trong NhatKyHeThongPanel.
     *
     * @return kết quả tạo tiêu đề
     */
    private JPanel taoTieuDe() {
        // Tiêu đề.
        JPanel pnlTieuDe = new JPanel(new BorderLayout(12, 8));
        pnlTieuDe.setOpaque(false);

        // Tiêu đề và số liệu thống kê
        // Trái mục.
        JPanel pnlTraiMuc = new JPanel();
        pnlTraiMuc.setOpaque(false);
        pnlTraiMuc.setLayout(new BoxLayout(pnlTraiMuc, BoxLayout.Y_AXIS));

        // Tiêu đề.
        JLabel lblTieuDe = taoTieuDeMuc("Nhật ký hệ thống");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        pnlTraiMuc.add(lblTieuDe);
        pnlTraiMuc.add(Box.createVerticalStrut(5));

        // Tiêu đề phụ.
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

        // Nút xuất dữ liệu
        // Phải mục.
        JPanel pnlPhaiMuc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlPhaiMuc.setOpaque(false);

        // Xuất nút.
        NutDinhDang btnXuatNut = new NutDinhDang("Xuất CSV", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuatNut.setToolTipText("Xuất kết quả tìm kiếm ra file CSV");
        btnXuatNut.addActionListener(suKien -> xuLyXuat());
        pnlPhaiMuc.add(btnXuatNut);

        pnlTieuDe.add(pnlPhaiMuc, BorderLayout.EAST);

        return pnlTieuDe;
    }

    /**
     * Tạo bộ lọc vùng hiển thị trong NhatKyHeThongPanel.
     *
     * @return kết quả tạo bộ lọc vùng hiển thị
     */
    private JPanel taoBoLocVungHienThi() {
        // Vùng hiển thị.
        JPanel pnlVungHienThi = layThe(new BorderLayout(0, 10));

        // Tiêu đề bộ lọc.
        JLabel lblTieuDeBoLoc = new JLabel("Bộ lọc tra cứu");
        lblTieuDeBoLoc.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        pnlVungHienThi.add(lblTieuDeBoLoc, BorderLayout.NORTH);

        // Bộ lọc nội dung.
        JPanel pnlBoLocNoiDung = new JPanel(new GridBagLayout());
        pnlBoLocNoiDung.setOpaque(false);
        // Ràng buộc bố cục.
        GridBagConstraints rangBuocBoCuc = new GridBagConstraints();
        rangBuocBoCuc.insets = new Insets(5, 5, 5, 5);
        rangBuocBoCuc.anchor = GridBagConstraints.WEST;
        rangBuocBoCuc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: chọn khoảng ngày
        rangBuocBoCuc.gridx = 0;
        rangBuocBoCuc.gridy = 0;
        rangBuocBoCuc.gridwidth = 4;
        rangBuocBoCuc.weightx = 1.0;

        pnlKhoangNgayBoChon = new BoChonKhoangNgay();
        pnlBoLocNoiDung.add(pnlKhoangNgayBoChon, rangBuocBoCuc);

        // Dòng 2: loại sự kiện, kết quả và tìm người dùng
        rangBuocBoCuc.gridy = 1;
        rangBuocBoCuc.gridwidth = 1;
        rangBuocBoCuc.weightx = 0.25;

        // Bộ lọc loại sự kiện
        // Sự kiện kiểu vùng hiển thị.
        JPanel pnlSuKienKieuVungHienThi = new JPanel(new BorderLayout(5, 0));
        pnlSuKienKieuVungHienThi.setOpaque(false);
        pnlSuKienKieuVungHienThi.add(new JLabel("Loại sự kiện:"), BorderLayout.WEST);

        // Sự kiện các kiểu.
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

        // Bộ lọc kết quả
        // Kết quả vùng hiển thị.
        JPanel pnlKetQuaVungHienThi = new JPanel(new BorderLayout(5, 0));
        pnlKetQuaVungHienThi.setOpaque(false);
        pnlKetQuaVungHienThi.add(new JLabel("Kết quả:"), BorderLayout.WEST);

        // Các kết quả.
        String[] cacKetQua = {"Tất cả", "Thành công", "Thất bại", "Cảnh báo"};
        cboKetQuaHopChon = new JComboBox<>(cacKetQua);
        cboKetQuaHopChon.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        pnlKetQuaVungHienThi.add(cboKetQuaHopChon, BorderLayout.CENTER);

        rangBuocBoCuc.gridx = 1;
        pnlBoLocNoiDung.add(pnlKetQuaVungHienThi, rangBuocBoCuc);

        // Tìm người dùng
        // Người dùng vùng hiển thị.
        JPanel pnlNguoiDungVungHienThi = new JPanel(new BorderLayout(5, 0));
        pnlNguoiDungVungHienThi.setOpaque(false);
        pnlNguoiDungVungHienThi.add(new JLabel("Người dùng:"), BorderLayout.WEST);

        txtTruongNguoiDungTimKiem = new JTextField();
        txtTruongNguoiDungTimKiem.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        txtTruongNguoiDungTimKiem.setToolTipText("Tìm theo tên người dùng");
        pnlNguoiDungVungHienThi.add(txtTruongNguoiDungTimKiem, BorderLayout.CENTER);

        rangBuocBoCuc.gridx = 2;
        pnlBoLocNoiDung.add(pnlNguoiDungVungHienThi, rangBuocBoCuc);

        // Các nút thao tác
        // Thao tác vùng hiển thị.
        JPanel pnlThaoTacVungHienThi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlThaoTacVungHienThi.setOpaque(false);

        // Xóa nút.
        NutDinhDang btnXoaNut = new NutDinhDang("Xóa bộ lọc", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXoaNut.addActionListener(suKien -> xoaCacBoLoc());
        pnlThaoTacVungHienThi.add(btnXoaNut);

        // Áp dụng nút.
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

    /**
     * Tạo bảng vùng hiển thị trong NhatKyHeThongPanel.
     *
     * @return kết quả tạo bảng vùng hiển thị
     */
    private JPanel taoBangVungHienThi() {
        // Vùng hiển thị.
        JPanel pnlVungHienThi = layThe(new BorderLayout(0, 10));

        // Tiêu đề bảng
        // Bảng tiêu đề.
        JPanel pnlBangTieuDe = new JPanel(new BorderLayout());
        pnlBangTieuDe.setOpaque(false);
        pnlBangTieuDe.add(taoTieuDeMuc("Kết quả tra cứu"), BorderLayout.WEST);

        // Gợi ý.
        JLabel lblGoiY = new JLabel("Click vào dòng để xem chi tiết");
        lblGoiY.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlBangTieuDe.add(lblGoiY, BorderLayout.EAST);

        pnlVungHienThi.add(pnlBangTieuDe, BorderLayout.NORTH);

        // Tạo bảng dữ liệu
        // Các cột.
        String[] cacCot = {
            "Thời gian", "Người dùng", "Hành động", "Loại sự kiện", "Kết quả", "IP Address"
        };
        // Dữ liệu.
        Object[][] duLieu = new Object[0][];

        moHinhBang =
                new DefaultTableModel(duLieu, cacCot) {
                    /**
                     * Kiểm tra ô có thể sửa trong lớp xử lý giao diện.
                     *
                     * @param dong dòng
                     * @param cot cột
                     * @return kết quả kiểm tra điều kiện xử lý
                     */
                    @Override
                    public boolean isCellEditable(int dong, int cot) {
                        return false; // Chỉ đọc
                    }
                };

        tblBang = new JTable(moHinhBang);
        DinhDangBang.apDung(tblBang);

        // Thiết lập sắp xếp bảng
        boSapXep = new TableRowSorter<>(moHinhBang);
        tblBang.setRowSorter(boSapXep);

        // Định dạng ô trong cột kết quả
        tblBang.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        (tblBang2, giaTri, laDaChon, coTieuDiem, dong, cot) -> {
                            // Nhãn.
                            JLabel lblNhan = new JLabel(giaTri.toString());
                            lblNhan.setOpaque(true);
                            lblNhan.setHorizontalAlignment(SwingConstants.CENTER);
                            lblNhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));

                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (giaTri.equals("Thành công")) {
                                lblNhan.setBackground(new Color(212, 237, 218));
                                lblNhan.setForeground(new Color(21, 87, 36));
                            } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (giaTri.equals("Thất bại")) {
                                lblNhan.setBackground(new Color(248, 215, 218));
                                lblNhan.setForeground(new Color(114, 28, 36));
                            } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (giaTri.equals("Cảnh báo")) {
                                lblNhan.setBackground(new Color(255, 243, 205));
                                lblNhan.setForeground(new Color(133, 100, 4));
                            }

                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (laDaChon) {
                                lblNhan.setBackground(tblBang.getSelectionBackground());
                                lblNhan.setForeground(tblBang.getSelectionForeground());
                            }

                            return lblNhan;
                        });

        // Định dạng ô trong cột loại sự kiện
        tblBang.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        (tblBang2, giaTri, laDaChon, coTieuDiem, dong, cot) -> {
                            // Nhãn.
                            JLabel lblNhan = new JLabel(giaTri.toString());
                            lblNhan.setOpaque(true);
                            lblNhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));

                            // Chuỗi.
                            String chuoi = giaTri.toString();
                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (chuoi.contains("WARNING")
                                    || chuoi.contains("ERROR")
                                    || chuoi.contains("DELETE")) {
                                lblNhan.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
                            } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (chuoi.contains("LOGIN") || chuoi.contains("LOGOUT")) {
                                lblNhan.setForeground(MauUngDung.CHINH);
                            } else {
                                lblNhan.setForeground(MauUngDung.VAN_BAN_TOI);
                            }

                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (laDaChon) {
                                lblNhan.setBackground(tblBang.getSelectionBackground());
                                lblNhan.setForeground(tblBang.getSelectionForeground());
                            } else {
                                lblNhan.setBackground(Color.WHITE);
                            }

                            return lblNhan;
                        });

        // Xử lý khi bấm đúp chuột
        tblBang.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    /**
                     * Lấy chuột đã bấm trong lớp xử lý giao diện.
                     *
                     * @param suKien sự kiện
                     */
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent suKien) {
                        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                        if (suKien.getClickCount() == 2) {
                            // Dòng.
                            int dong = tblBang.getSelectedRow();
                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (dong >= 0) {
                                xuLyHienThiChiTiet(dong);
                            }
                        }
                    }
                });

        // Cuộn khung.
        JScrollPane scrCuonKhung = new JScrollPane(tblBang);
        scrCuonKhung.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlVungHienThi.add(scrCuonKhung, BorderLayout.CENTER);

        return pnlVungHienThi;
    }

    /**
     * Tạo chân trang trong NhatKyHeThongPanel.
     *
     * @return kết quả tạo chân trang
     */
    private JPanel taoChanTrang() {
        // Chân trang.
        JPanel pnlChanTrang = new JPanel(new BorderLayout());
        pnlChanTrang.setBackground(MauUngDung.CANH_BAO_NEN);
        pnlChanTrang.setBorder(new EmptyBorder(8, 12, 8, 12));

        // Ghi chú.
        JLabel lblGhiChu =
                new JLabel(
                        "Nhật ký được lưu trên SQL Server. Chọn khoảng thời gian và bộ lọc để tra"
                                + " cứu.");
        lblGhiChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblGhiChu.setForeground(MauUngDung.NAU);
        pnlChanTrang.add(lblGhiChu, BorderLayout.WEST);

        return pnlChanTrang;
    }

    // === EVENT HANDLERS ===

    /** Áp dụng các bộ lọc trong NhatKyHeThongPanel. */
    private void apDungCacBoLoc() {
        taiLai();
    }

    /**
     * Tải lại trong NhatKyHeThongPanel.
     *
     * <p>Nạp lại bảng từ nhật ký thật của phiên theo bộ lọc hiện tại (mới nhất ở trên).
     */
    private void taiLai() {
        // Từ.
        LocalDate tu = pnlKhoangNgayBoChon.layNgayBatDau();
        // Đến.
        LocalDate den = pnlKhoangNgayBoChon.layNgayKetThuc();
        // Kiểu.
        String kieu = String.valueOf(cboSuKienKieuHopChon.getSelectedItem());
        // Kiểu khóa.
        String kieuKhoa = kieu.startsWith("Tất cả") ? "" : kieu.split(" ")[0];
        // Kết quả.
        String ketQua = String.valueOf(cboKetQuaHopChon.getSelectedItem());
        // Người dùng.
        String nguoiDung = txtTruongNguoiDungTimKiem.getText().trim().toLowerCase();

        moHinhBang.setRowCount(0);
        // Các mục.
        java.util.List<NhatKyHeThongService.Muc> cacMuc =
                new java.util.ArrayList<>(NhatKyHeThongService.layCacMuc());

        // Các cảnh báo.
        int cacCanhBao = 0;
        for (NhatKyHeThongService.Muc suKien : cacMuc) {
            // Ngày đang xét.
            LocalDate ngayDangXet = suKien.thoiGian().toLocalDate();
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (tu != null && ngayDangXet.isBefore(tu)) continue;
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (den != null && ngayDangXet.isAfter(den)) continue;
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (!kieuKhoa.isEmpty() && !kieuKhoa.equals(suKien.kieu())) continue;
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (!ketQua.equals("Tất cả") && !ketQua.equals(suKien.ketQua())) continue;
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (!nguoiDung.isEmpty() && !suKien.nguoiDung().toLowerCase().contains(nguoiDung))
                continue;
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
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

    /** Xóa các bộ lọc trong NhatKyHeThongPanel. */
    private void xoaCacBoLoc() {
        pnlKhoangNgayBoChon.datLai();
        cboSuKienKieuHopChon.setSelectedIndex(0);
        cboKetQuaHopChon.setSelectedIndex(0);
        txtTruongNguoiDungTimKiem.setText("");
        taiLai();
    }

    /**
     * Xử lý hiển thị chi tiết trong NhatKyHeThongPanel.
     *
     * @param dong dòng
     */
    private void xuLyHienThiChiTiet(int dong) {
        // Mô hình dòng.
        int moHinhDong = tblBang.convertRowIndexToModel(dong);

        // Thời gian.
        String thoiGian = moHinhBang.getValueAt(moHinhDong, 0).toString();
        // Người dùng.
        String nguoiDung = moHinhBang.getValueAt(moHinhDong, 1).toString();
        // Thao tác.
        String thaoTac = moHinhBang.getValueAt(moHinhDong, 2).toString();
        // Kiểu sự kiện.
        String kieuSuKien = moHinhBang.getValueAt(moHinhDong, 3).toString();
        // Kết quả.
        String ketQua = moHinhBang.getValueAt(moHinhDong, 4).toString();
        // Đệm trong.
        String demTrong = moHinhBang.getValueAt(moHinhDong, 5).toString();

        // Chi tiết.
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

    /** Xử lý xuất trong NhatKyHeThongPanel. */
    private void xuLyXuat() {
        XuatExcel.xuatBang(this, tblBang, "Audit_Log");
    }

    /** Điều hướng quay lại trong NhatKyHeThongPanel. */
    private void dieuHuongQuayLai() {
        HopThoai.hienThiThongBaoHopThoai(
                this,
                "Chức năng quay lại sẽ được triển khai khi có navigation system",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /** Điều hướng đến trang chính trong NhatKyHeThongPanel. */
    private void dieuHuongDenTrangChinh() {
        HopThoai.hienThiThongBaoHopThoai(
                this,
                "Chức năng về màn hình chính sẽ được triển khai khi có navigation system",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Lấy tiêu đề trợ giúp trong NhatKyHeThongPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Nhật ký hệ thống";
    }

    /**
     * Lấy nội dung trợ giúp trong NhatKyHeThongPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
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
