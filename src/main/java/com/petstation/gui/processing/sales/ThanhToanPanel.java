package com.petstation.gui.processing.sales;

import com.petstation.gui.components.BieuMauPanel;
import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.KhaNangThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.service.CaThuNganService;
import com.petstation.util.TienTe;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho thanh toán vùng hiển thị.
 *
 * <p>Thanh toán hóa đơn đang lập: chọn phương thức, áp voucher, nhập tiền khách đưa, tính tiền thừa
 * tự động.
 */
public final class ThanhToanPanel extends CoSoPanel {
    /** Bán hàng. */
    private final GioHang banHang;

    /** Ca làm việc. */
    private final CaThuNganService caLamViec;

    /** Thu ngân. */
    private final String thuNgan;

    /** Bộ điều hướng. */
    private final Consumer<MaManHinh> boDieuHuong;

    /** Các thẻ. */
    private final CardLayout cacThe = new CardLayout();

    /** Gốc. */
    private final JPanel pnlGoc = new JPanel(cacThe);

    /** Dòng vùng chứa. */
    private final JPanel pnlDongVungChua = taoVungTrongSuot(new BorderLayout());

    /** Tạm tính. */
    private final JLabel lblTamTinh = new JLabel();

    /** Giảm giá. */
    private final JLabel lblGiamGia = new JLabel();

    /** Tổng. */
    private final JLabel lblTong = new JLabel();

    /** Trường voucher. */
    private final JTextField txtTruongVoucher = BieuMauPanel.layTruongVanBan("");

    /** Voucher thông báo. */
    private final JLabel lblVoucherThongBao = new JLabel("");

    /** Tiền mặt. */
    private final JRadioButton btnTienMat = new JRadioButton("Tiền mặt", true);

    /** Chuyển khoản. */
    private final JRadioButton btnChuyenKhoan = new JRadioButton("Chuyển khoản / QR");

    /** Thẻ thanh toán. */
    private final JRadioButton btnTheThanhToan = new JRadioButton("Thẻ");

    /** Đã nhận. */
    private final JTextField txtDaNhan = BieuMauPanel.layTruongVanBan("");

    /** Nhanh các số tiền. */
    private final JPanel pnlNhanhCacSoTien = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));

    /** Nhãn thay đổi. */
    private final JLabel lblNhanThayDoi = new JLabel("");

    /** Xác nhận. */
    private final NutDinhDang btnXacNhan =
            new NutDinhDang("Xác nhận thanh toán (F9)", NutDinhDang.KieuNut.CHINH);

    /** Đang xử lý. */
    private boolean dangXuLy;

    /**
     * Khởi tạo ThanhToanPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param caLamViec ca làm việc
     * @param thuNgan thu ngân
     * @param boDieuHuong bộ điều hướng
     */
    public ThanhToanPanel(
            GioHang banHang,
            CaThuNganService caLamViec,
            String thuNgan,
            Consumer<MaManHinh> boDieuHuong) {
        this.banHang = banHang;
        this.caLamViec = caLamViec;
        this.thuNgan = thuNgan;
        this.boDieuHuong = boDieuHuong;
        setLayout(new BorderLayout(0, 14));

        // Tiêu đề.
        JLabel lblTieuDe = taoTieuDeMuc("Thanh toán");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        // Gợi ý.
        JLabel lblGoiY = new JLabel("Kiểm tra hóa đơn, chọn phương thức và xác nhận.");
        lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        // Đầu.
        JPanel pnlDau = new JPanel();
        pnlDau.setOpaque(false);
        pnlDau.setLayout(new BoxLayout(pnlDau, BoxLayout.Y_AXIS));
        pnlDau.add(lblTieuDe);
        pnlDau.add(Box.createVerticalStrut(3));
        pnlDau.add(lblGoiY);
        add(pnlDau, BorderLayout.NORTH);

        pnlGoc.setOpaque(false);
        pnlGoc.add(taoBieuMau(), "form");
        // Quay lại.
        NutDinhDang btnQuayLai = new NutDinhDang("Về lập đơn hàng", NutDinhDang.KieuNut.CHINH);
        btnQuayLai.addActionListener(suKien -> boDieuHuong.accept(MaManHinh.POS));
        pnlGoc.add(
                layTrongTrangThai(
                        "Chưa có hóa đơn để thanh toán",
                        "Hãy thêm sản phẩm ở màn Lập đơn hàng trước.",
                        btnQuayLai),
                "empty");
        add(pnlGoc, BorderLayout.CENTER);
        lamMoi();
    }

    /**
     * Tạo biểu mẫu trong ThanhToanPanel.
     *
     * @return kết quả tạo biểu mẫu
     */
    private JPanel taoBieuMau() {
        // Biểu mẫu.
        JPanel pnlBieuMau = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlBieuMau.setOpaque(false);

        // Trái.
        JPanel pnlTrai = layThe(new BorderLayout(0, 12));
        pnlTrai.add(taoTieuDeMuc("Hóa đơn"), BorderLayout.NORTH);
        pnlTrai.add(pnlDongVungChua, BorderLayout.CENTER);
        // Các tổng.
        JPanel pnlCacTong = new JPanel(new GridLayout(3, 2, 0, 4));
        pnlCacTong.setOpaque(false);
        pnlCacTong.add(new JLabel("Tạm tính"));
        lblTamTinh.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlCacTong.add(lblTamTinh);
        pnlCacTong.add(new JLabel("Giảm giá và voucher"));
        lblGiamGia.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlCacTong.add(lblGiamGia);
        // Văn bản tổng.
        JLabel lblVanBanTong = new JLabel("Cần thanh toán");
        lblVanBanTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        pnlCacTong.add(lblVanBanTong);
        lblTong.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 24));
        lblTong.setForeground(MauUngDung.DO_THUONG_HIEU);
        pnlCacTong.add(lblTong);
        pnlTrai.add(pnlCacTong, BorderLayout.SOUTH);

        // Phải.
        JPanel pnlPhai = layThe(new BorderLayout(0, 12));
        pnlPhai.add(taoTieuDeMuc("Phương thức thanh toán"), BorderLayout.NORTH);
        // Nội dung.
        JPanel pnlNoiDung = new JPanel();
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.setLayout(new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS));

        // Nhóm.
        ButtonGroup nhom = new ButtonGroup();
        // Các phương thức.
        JPanel pnlCacPhuongThuc = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlCacPhuongThuc.setOpaque(false);
        for (JRadioButton btnDongDuLieu :
                new JRadioButton[] {btnTienMat, btnChuyenKhoan, btnTheThanhToan}) {
            btnDongDuLieu.setOpaque(false);
            btnDongDuLieu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            btnDongDuLieu.addActionListener(suKien -> capNhatPhuongThuc());
            nhom.add(btnDongDuLieu);
            pnlCacPhuongThuc.add(btnDongDuLieu);
        }
        pnlCacPhuongThuc.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCacPhuongThuc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        pnlNoiDung.add(pnlCacPhuongThuc);
        pnlNoiDung.add(Box.createVerticalStrut(14));

        pnlNoiDung.add(layNhan("Mã voucher"));
        // Voucher dòng.
        JPanel pnlVoucherDong = new JPanel(new BorderLayout(8, 0));
        pnlVoucherDong.setOpaque(false);
        txtTruongVoucher.setPreferredSize(new Dimension(180, 36));
        // Áp dụng.
        NutDinhDang btnApDung = new NutDinhDang("Áp dụng", NutDinhDang.KieuNut.TRONG_SUOT);
        btnApDung.addActionListener(suKien -> apDungVoucher());
        txtTruongVoucher.addActionListener(suKien -> apDungVoucher());
        pnlVoucherDong.add(txtTruongVoucher, BorderLayout.CENTER);
        pnlVoucherDong.add(btnApDung, BorderLayout.EAST);
        pnlVoucherDong.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlVoucherDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        pnlNoiDung.add(pnlVoucherDong);
        lblVoucherThongBao.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblVoucherThongBao.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblVoucherThongBao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        pnlNoiDung.add(lblVoucherThongBao);
        pnlNoiDung.add(Box.createVerticalStrut(10));

        pnlNoiDung.add(layNhan("Tiền khách đưa"));
        txtDaNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtDaNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDaNhan.getAccessibleContext().setAccessibleName("Tiền khách đưa");
        txtDaNhan
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            /**
                             * Thêm cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void insertUpdate(javax.swing.event.DocumentEvent suKien) {
                                kiemTraDauVao();
                            }

                            /**
                             * Xóa cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void removeUpdate(javax.swing.event.DocumentEvent suKien) {
                                kiemTraDauVao();
                            }

                            /**
                             * Lấy đã thay đổi cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void changedUpdate(javax.swing.event.DocumentEvent suKien) {
                                kiemTraDauVao();
                            }
                        });
        pnlNoiDung.add(txtDaNhan);
        pnlNoiDung.add(Box.createVerticalStrut(6));
        pnlNhanhCacSoTien.setOpaque(false);
        pnlNhanhCacSoTien.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlNhanhCacSoTien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        pnlNoiDung.add(pnlNhanhCacSoTien);
        pnlNoiDung.add(Box.createVerticalStrut(12));
        lblNhanThayDoi.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        lblNhanThayDoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlNoiDung.add(lblNhanThayDoi);
        pnlNoiDung.add(Box.createVerticalGlue());
        // Nội dung vùng chứa.
        JPanel pnlNoiDungVungChua = taoVungTrongSuot(new BorderLayout());
        pnlNoiDungVungChua.add(pnlNoiDung, BorderLayout.NORTH);
        pnlPhai.add(pnlNoiDungVungChua, BorderLayout.CENTER);

        btnXacNhan.setPreferredSize(new Dimension(0, 46));
        btnXacNhan.addActionListener(suKien -> xacNhanThanhToan());
        // Hủy.
        NutDinhDang btnHuy = new NutDinhDang("Quay lại sửa đơn", NutDinhDang.KieuNut.TRONG_SUOT);
        btnHuy.addActionListener(suKien -> boDieuHuong.accept(MaManHinh.POS));
        // Các thao tác.
        JPanel pnlCacThaoTac = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlCacThaoTac.setOpaque(false);
        pnlCacThaoTac.add(btnHuy);
        pnlCacThaoTac.add(btnXacNhan);
        pnlPhai.add(pnlCacThaoTac, BorderLayout.SOUTH);

        pnlBieuMau.add(pnlTrai);
        pnlBieuMau.add(pnlPhai);
        return pnlBieuMau;
    }

    /**
     * Lấy nhãn trong ThanhToanPanel.
     *
     * @param vanBan văn bản
     * @return kết quả lấy nhãn
     */
    private JLabel layNhan(String vanBan) {
        // Dòng đang xét.
        JLabel lblDongDangXet = new JLabel(vanBan);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        lblDongDangXet.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDongDangXet.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lblDongDangXet;
    }

    /**
     * Lấy phương thức trong ThanhToanPanel.
     *
     * @return kết quả lấy phương thức
     */
    private CaThuNganService.PhuongThuc layPhuongThuc() {
        return btnChuyenKhoan.isSelected()
                ? CaThuNganService.PhuongThuc.TRANSFER
                : btnTheThanhToan.isSelected()
                        ? CaThuNganService.PhuongThuc.CARD
                        : CaThuNganService.PhuongThuc.CASH;
    }

    /** Áp dụng voucher trong ThanhToanPanel. */
    private void apDungVoucher() {
        // Lỗi.
        String loi = banHang.apDungVoucher(txtTruongVoucher.getText());
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (loi != null) {
            lblVoucherThongBao.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            lblVoucherThongBao.setText(loi);
        } else // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (banHang.layMaVoucher().isEmpty()) {
            lblVoucherThongBao.setText("");
        } else {
            lblVoucherThongBao.setForeground(MauUngDung.XANH_LA);
            lblVoucherThongBao.setText(
                    "Đã áp dụng "
                            + banHang.layMaVoucher()
                            + ": −"
                            + TienTe.dinhDang(banHang.laySoTienVoucher()));
        }
        lamMoiCacTong();
    }

    /** Làm mới trong ThanhToanPanel. */
    private void lamMoi() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (banHang.kiemTraTrong()) {
            cacThe.show(pnlGoc, "empty");
            return;
        }
        cacThe.show(pnlGoc, "form");
        // Mô hình.
        DefaultTableModel moHinh =
                new DefaultTableModel(new String[] {"Sản phẩm", "SL", "Đơn giá", "Thành tiền"}, 0) {
                    /**
                     * Kiểm tra ô có thể sửa trong lớp xử lý giao diện.
                     *
                     * @param dongDuLieu dòng dữ liệu
                     * @param giaTriDangXet giá trị đang xét
                     * @return kết quả kiểm tra điều kiện xử lý
                     */
                    @Override
                    public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                        return false;
                    }
                };
        for (GioHang.DongHang dongDangXet : banHang.layCacDong()) {
            moHinh.addRow(
                    new Object[] {
                        dongDangXet.ten(),
                        dongDangXet.soLuong(),
                        TienTe.dinhDang(dongDangXet.donGia()),
                        TienTe.dinhDang(dongDangXet.docSoTien())
                    });
        }
        pnlDongVungChua.removeAll();
        pnlDongVungChua.add(taoBangDinhDang(moHinh), BorderLayout.CENTER);
        pnlDongVungChua.revalidate();
        txtTruongVoucher.setText(banHang.layMaVoucher());
        lblVoucherThongBao.setText("");
        txtDaNhan.setText("");
        dangXuLy = false;
        btnTienMat.setSelected(true);
        capNhatPhuongThuc();
        lamMoiCacTong();
    }

    /** Làm mới các tổng trong ThanhToanPanel. */
    private void lamMoiCacTong() {
        lblTamTinh.setText(TienTe.dinhDang(banHang.tinhTamTinh()));
        // Ngày đang xét.
        long ngayDangXet = banHang.tinhGiamGia();
        lblGiamGia.setText(ngayDangXet == 0 ? "0₫" : "−" + TienTe.dinhDang(ngayDangXet));
        lblTong.setText(TienTe.dinhDang(banHang.tinhTong()));
        dungLaiNhanhCacSoTien();
        kiemTraDauVao();
    }

    /** Dựng lại nhanh các số tiền trong ThanhToanPanel. */
    private void dungLaiNhanhCacSoTien() {
        pnlNhanhCacSoTien.removeAll();
        // Giá trị tạm.
        long giaTriTam = banHang.tinhTong();
        // Các lựa chọn.
        Set<Long> cacLuaChon = new LinkedHashSet<>();
        cacLuaChon.add(giaTriTam);
        for (long buoc : new long[] {50_000, 100_000, 500_000}) {
            // Lên.
            long len = ((giaTriTam + buoc - 1) / buoc) * buoc;
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (len >= giaTriTam) cacLuaChon.add(len);
        }
        for (long soTien : cacLuaChon) {
            // Giá trị sau.
            JButton btnGiaTriSau =
                    new JButton(soTien == giaTriTam ? "Đủ tiền" : TienTe.dinhDang(soTien));
            btnGiaTriSau.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            btnGiaTriSau.setFocusPainted(false);
            btnGiaTriSau.addActionListener(suKien -> txtDaNhan.setText(String.valueOf(soTien)));
            pnlNhanhCacSoTien.add(btnGiaTriSau);
        }
        pnlNhanhCacSoTien.revalidate();
        pnlNhanhCacSoTien.repaint();
    }

    /** Cập nhật phương thức trong ThanhToanPanel. */
    private void capNhatPhuongThuc() {
        // Là tiền mặt.
        boolean laTienMat = layPhuongThuc() == CaThuNganService.PhuongThuc.CASH;
        txtDaNhan.setEnabled(laTienMat);
        for (Component giaTriDangXet : pnlNhanhCacSoTien.getComponents())
            giaTriDangXet.setEnabled(laTienMat);
        kiemTraDauVao();
    }

    /** Kiểm tra đầu vào trong ThanhToanPanel. */
    private void kiemTraDauVao() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (banHang.kiemTraTrong()) return;
        // Giá trị tạm.
        long giaTriTam = banHang.tinhTong();
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (layPhuongThuc() != CaThuNganService.PhuongThuc.CASH) {
            lblNhanThayDoi.setForeground(MauUngDung.VAN_BAN_XAM);
            lblNhanThayDoi.setText(
                    "Thu đúng "
                            + TienTe.dinhDang(giaTriTam)
                            + "qua "
                            + (layPhuongThuc() == CaThuNganService.PhuongThuc.TRANSFER
                                    ? "chuyển khoản/QR"
                                    : "thẻ"));
            btnXacNhan.setEnabled(!dangXuLy);
            return;
        }
        // Dòng dữ liệu.
        long dongDuLieu = TienTe.doc(txtDaNhan.getText());
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (txtDaNhan.getText().isBlank()) {
            lblNhanThayDoi.setForeground(MauUngDung.VAN_BAN_XAM);
            lblNhanThayDoi.setText("Nhập số tiền khách đưa");
            btnXacNhan.setEnabled(false);
        } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (dongDuLieu < 0) {
            lblNhanThayDoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            lblNhanThayDoi.setText("Số tiền không hợp lệ");
            btnXacNhan.setEnabled(false);
        } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (dongDuLieu < giaTriTam) {
            lblNhanThayDoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            lblNhanThayDoi.setText("Còn thiếu " + TienTe.dinhDang(giaTriTam - dongDuLieu));
            btnXacNhan.setEnabled(false);
        } else {
            lblNhanThayDoi.setForeground(MauUngDung.XANH_LA);
            lblNhanThayDoi.setText(
                    "Tiền thừa trả khách: " + TienTe.dinhDang(dongDuLieu - giaTriTam));
            btnXacNhan.setEnabled(!dangXuLy);
        }
    }

    /** Xác nhận thanh toán trong ThanhToanPanel. */
    private void xacNhanThanhToan() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (dangXuLy || banHang.kiemTraTrong()) return;
        // Giá trị tạm.
        long giaTriTam = banHang.tinhTong();
        // Dòng dữ liệu.
        long dongDuLieu =
                layPhuongThuc() == CaThuNganService.PhuongThuc.CASH
                        ? TienTe.doc(txtDaNhan.getText())
                        : giaTriTam;
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (dongDuLieu < giaTriTam) return;
        // Đồng ý.
        int dongY =
                HopThoai.hienThiXacNhanHopThoai(
                        this,
                        "Xác nhận thu " + TienTe.dinhDang(giaTriTam) + "?",
                        "Xác nhận thanh toán",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (dongY != JOptionPane.OK_OPTION) return;
        dangXuLy = true;
        btnXacNhan.setEnabled(false);
        try {
            // Biên lai.
            GioHang.BienLai bienLai = banHang.hoanTat(layPhuongThuc(), dongDuLieu, thuNgan);
            caLamViec.ghiNhanBanHang(layPhuongThuc(), bienLai.tong());
            banHang.xoa();
            boDieuHuong.accept(MaManHinh.PRINT_INVOICE);
        } catch (RuntimeException ngoaiLe) {
            dangXuLy = false;
            kiemTraDauVao();
            HopThoai.hienThiCanhBao(this, "Chưa thanh toán: " + ngoaiLe.getMessage());
        }
    }

    /**
     * Lấy khả năng thanh công cụ trong ThanhToanPanel.
     *
     * @return kết quả lấy khả năng thanh công cụ
     */
    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(false, false, false, false, false, true);
    }

    /** Xử lý hiển thị trong ThanhToanPanel. */
    @Override
    public void xuLyHienThi() {
        lamMoi();
    }

    /** Xử lý lưu trong ThanhToanPanel. */
    @Override
    public void xuLyLuu() {}

    /**
     * Lấy nội dung trợ giúp trong ThanhToanPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Chọn phương thức, nhập tiền khách đưa (nút gợi ý bên dưới ô nhập).\n"
                + "Nút xác nhận chỉ bật khi số tiền đủ. Voucher được kiểm tra theo hạn dùng, số"
                + " lượt và điều kiện trong SQL Server.";
    }

    /**
     * Lấy tiêu đề trợ giúp trong ThanhToanPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Thanh toán";
    }
}
