package com.petstation.gui.processing.sales;

import com.petstation.gui.components.BieuMauPanel;
import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.KhaNangThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.model.GioHang;
import com.petstation.model.MaManHinh;
import com.petstation.model.PhuongThuc;
import com.petstation.service.CaThuNganService;
import com.petstation.service.DonDoiTreoService;
import com.petstation.util.TienTe;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public final class ThanhToanPanel extends CoSoPanel {
    private final GioHang banHang;
    private final CaThuNganService caLamViec;
    private final String thuNgan;
    private final Consumer<MaManHinh> boDieuHuong;
    private final Consumer<MaManHinh> quayLaiTuThanhToan;

    private final CardLayout cacThe = new CardLayout();
    private final JPanel pnlGoc = new JPanel(cacThe);
    private final JPanel pnlDongVungChua = taoVungTrongSuot(new BorderLayout());

    private final JLabel lblTamTinh = new JLabel();
    private final JLabel lblGiamGia = new JLabel();
    private final JLabel lblTong = new JLabel();

    private final JTextField txtTruongVoucher = BieuMauPanel.layTruongVanBan("");
    private final java.util.List<JComponent> dieuKhienVoucher =
        new java.util.ArrayList<>();
    private final JLabel lblVoucherThongBao = new JLabel("");

    private final JRadioButton btnTienMat = new JRadioButton("Tiền mặt", true);
    private final JRadioButton btnChuyenKhoan = new JRadioButton("Chuyển khoản / QR");
    private final JRadioButton btnTheThanhToan = new JRadioButton("Thẻ");

    private final JButton btnChonVoucher = new JButton("Chọn voucher của khách");

    private final JTextField txtDaNhan = BieuMauPanel.layTruongVanBan("");
    private final JPanel pnlNhanhCacSoTien =
            new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private final JPanel pnlNhapTienMat = new JPanel();

    private final JLabel lblNhanThayDoi = new JLabel("");
    private final NutDinhDang btnXacNhan =
            new NutDinhDang("Xác nhận thanh toán (F9)", NutDinhDang.KieuNut.CHINH);
    private final NutDinhDang btnQuayLaiTrangThaiRong =
            new NutDinhDang("Về lập đơn hàng", NutDinhDang.KieuNut.CHINH);

    private final NutDinhDang btnHuy =
            new NutDinhDang("Quay lại sửa đơn", NutDinhDang.KieuNut.TRONG_SUOT);
    private final NutDinhDang btnTreoDoi =
            new NutDinhDang("Treo đơn đổi", NutDinhDang.KieuNut.TRONG_SUOT);

    private final JPanel pnlQRThanhToan = new JPanel();
    private final JLabel lblQR = new JLabel();
    private final JLabel lblThongTinQR = new JLabel();
    private final JLabel lblNoiDungQR = new JLabel();
    private final JLabel lblTrangThaiQR = new JLabel();
    private final JButton btnSaoChepNoiDungQR = new JButton("Sao chép nội dung");

    private final JCheckBox chkDaNhanTien =
            new JCheckBox("Tôi đã kiểm tra và nhận đủ tiền");
    private boolean dangXuLy;
    private boolean qrHopLe;
    private String noiDungChuyenKhoanQR = "";

    public ThanhToanPanel(
            GioHang banHang,
            CaThuNganService caLamViec,
            String thuNgan,
            Consumer<MaManHinh> boDieuHuong) {
        this(banHang, caLamViec, thuNgan, boDieuHuong, boDieuHuong);
    }

    public ThanhToanPanel(
            GioHang banHang,
            CaThuNganService caLamViec,
            String thuNgan,
            Consumer<MaManHinh> boDieuHuong,
            Consumer<MaManHinh> quayLaiTuThanhToan) {
        this.banHang = banHang;
        this.caLamViec = caLamViec;
        this.thuNgan = thuNgan;
        this.boDieuHuong = boDieuHuong;
        this.quayLaiTuThanhToan = quayLaiTuThanhToan;
        this.banHang.themBoLangNghe(() -> {
            if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                lamMoi();
            } else {
                javax.swing.SwingUtilities.invokeLater(this::lamMoi);
            }
        });
        setLayout(new BorderLayout(0, 14));

        JLabel lblTieuDe = taoTieuDeMuc("Thanh toán");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));

        JLabel lblGoiY =
                new JLabel("Kiểm tra hóa đơn, chọn phương thức và xác nhận.");
        lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);

        JPanel pnlDau = new JPanel();
        pnlDau.setOpaque(false);
        pnlDau.setLayout(new BoxLayout(pnlDau, BoxLayout.Y_AXIS));
        pnlDau.add(lblTieuDe);
        pnlDau.add(Box.createVerticalStrut(3));
        pnlDau.add(lblGoiY);
        add(pnlDau, BorderLayout.NORTH);

        pnlGoc.setOpaque(false);
        pnlGoc.add(taoBieuMau(), "form");

        btnQuayLaiTrangThaiRong.addActionListener(
                suKien -> quayLaiManHinhNguon());

        pnlGoc.add(
                layTrongTrangThai(
                        "Chưa có hóa đơn để thanh toán",
                        "Hãy thêm sản phẩm ở màn Lập đơn hàng trước.",
                        btnQuayLaiTrangThaiRong),
                "empty");
        add(pnlGoc, BorderLayout.CENTER);

        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0), "xacNhanVietQRSpace");
        am.put(
                "xacNhanVietQRSpace",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        Component focusOwner =
                                java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                        if (focusOwner instanceof javax.swing.text.JTextComponent) {
                            return;
                        }
                        if (layPhuongThuc() == PhuongThuc.CHUYEN_KHOAN
                                && chkDaNhanTien.isVisible()
                                && chkDaNhanTien.isEnabled()) {
                            chkDaNhanTien.setSelected(!chkDaNhanTien.isSelected());
                            banHang.xacNhanDaNhanTien(
                                    layPhuongThuc(), chkDaNhanTien.isSelected());
                            kiemTraDauVao();
                        }
                    }
                });

        lamMoi();
    }

    private MaManHinh layManHinhQuayLaiTheoNguon() {
        String nguon = banHang.getKieuNguon();

        if ("HOTEL".equals(nguon)) {
            return MaManHinh.TRA_PHONG_LUU_TRU;
        }

        if ("SPA".equals(nguon)) {
            return MaManHinh.HOAN_TAT_SPA;
        }

        return MaManHinh.LAP_HOA_DON;
    }

    private void quayLaiManHinhNguon() {
        if ("DOI_HANG".equals(banHang.getKieuNguon())) {
            treoDonDoi(true);
            return;
        }
        MaManHinh manHinhDich = layManHinhQuayLaiTheoNguon();
        quayLaiTuThanhToan.accept(manHinhDich);
    }

    private void treoDonDoi(boolean suaLai) {
        if (dangXuLy || banHang.isDangGhiThanhToan()) return;
        dangXuLy = true;
        btnTreoDoi.setEnabled(false);
        btnHuy.setEnabled(false);
        btnXacNhan.setEnabled(false);
        try {
            long id = DonDoiTreoService.treo(banHang);
            var nhap = suaLai ? DonDoiTreoService.lay(id) : null;

            banHang.xoa();
            boDieuHuong.accept(suaLai ? MaManHinh.DOI_TRA_HANG_HOA : MaManHinh.LAP_HOA_DON);
            if (suaLai) {
                new DoiPhuKienDialog(this, banHang, boDieuHuong, id, nhap).setVisible(true);
            } else {
                HopThoai.hienThiThanhCong(this, "Đã treo đơn đổi. Mở lại tại Đơn đang treo để tiếp tục.");
            }
        } catch (RuntimeException loi) {
            HopThoai.hienThiCanhBao(this, "Chưa treo được đơn đổi: " + loi.getMessage());
        } finally {
            dangXuLy = false;
            btnTreoDoi.setEnabled(true);
            btnHuy.setEnabled(true);
            kiemTraDauVao();
        }
    }

    private void capNhatNhanNutQuayLai() {
        String nguon = banHang.getKieuNguon();
        btnTreoDoi.setVisible("DOI_HANG".equals(nguon));

        if ("HOTEL".equals(nguon) || "LUU_TRU".equals(nguon)) {
            btnQuayLaiTrangThaiRong.setText("Về trả phòng Hotel");
            btnHuy.setText("Quay lại trả phòng");
        } else if ("SPA".equals(nguon)) {
            btnQuayLaiTrangThaiRong.setText("Về đóng phiếu Spa");
            btnHuy.setText("Quay lại đóng phiếu");
        } else if ("DOI_HANG".equals(nguon)) {
            btnHuy.setText("Sửa đơn đổi");
            btnQuayLaiTrangThaiRong.setText("Về đổi phụ kiện");
        } else {
            btnQuayLaiTrangThaiRong.setText("Về lập đơn hàng");
            btnHuy.setText("Quay lại sửa đơn");
        }
    }

    private JPanel taoBieuMau() {
        JPanel pnlBieuMau = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlBieuMau.setOpaque(false);

        JPanel pnlTrai = layThe(new BorderLayout(0, 12));
        pnlTrai.add(taoTieuDeMuc("Hóa đơn"), BorderLayout.NORTH);
        pnlTrai.add(pnlDongVungChua, BorderLayout.CENTER);

        JPanel pnlCacTong = new JPanel(new GridLayout(3, 2, 0, 4));
        pnlCacTong.setOpaque(false);
        pnlCacTong.add(new JLabel("Tạm tính"));
        lblTamTinh.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlCacTong.add(lblTamTinh);

        pnlCacTong.add(new JLabel("Giảm giá và voucher"));
        lblGiamGia.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlCacTong.add(lblGiamGia);

        JLabel lblVanBanTong = new JLabel("Cần thanh toán");
        lblVanBanTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        pnlCacTong.add(lblVanBanTong);

        lblTong.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 24));
        lblTong.setForeground(MauUngDung.DO_THUONG_HIEU);
        pnlCacTong.add(lblTong);
        pnlTrai.add(pnlCacTong, BorderLayout.SOUTH);

        JPanel pnlPhai = layThe(new BorderLayout(0, 12));
        pnlPhai.add(taoTieuDeMuc("Phương thức thanh toán"), BorderLayout.NORTH);

        JPanel pnlNoiDung = new JPanel();
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.setLayout(new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS));

        pnlNoiDung.setBorder(new EmptyBorder(8, 10, 8, 10));

        ButtonGroup nhom = new ButtonGroup();
        JPanel pnlCacPhuongThuc =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlCacPhuongThuc.setOpaque(false);

        for (JRadioButton btnDongDuLieu :
                new JRadioButton[] {
                    btnTienMat, btnChuyenKhoan, btnTheThanhToan
                }) {
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

        JPanel pnlVoucherDong = new JPanel(new BorderLayout(8, 0));
        pnlVoucherDong.setOpaque(false);
        txtTruongVoucher.setPreferredSize(new Dimension(180, 36));

        NutDinhDang btnApDung =
                new NutDinhDang("Áp dụng", NutDinhDang.KieuNut.TRONG_SUOT);
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

        pnlNoiDung.add(Box.createVerticalStrut(8));

        JPanel pnlVoucherKhach =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlVoucherKhach.setOpaque(false);
        pnlVoucherKhach.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnChonVoucher.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnChonVoucher.setFocusPainted(false);
        btnChonVoucher.setEnabled(false);
        btnChonVoucher.setToolTipText(
                "Vui lòng chọn khách hàng ở màn hình Lập hóa đơn trước.");
        btnChonVoucher.addActionListener(suKien -> chonVoucher());

        pnlVoucherKhach.add(btnChonVoucher);
        pnlNoiDung.add(pnlVoucherKhach);
        pnlNoiDung.add(Box.createVerticalStrut(12));

        dieuKhienVoucher.addAll(
            java.util.List.of(
                txtTruongVoucher,
                btnApDung,
                btnChonVoucher));

        pnlNhapTienMat.setOpaque(false);
        pnlNhapTienMat.setLayout(new BoxLayout(pnlNhapTienMat, BoxLayout.Y_AXIS));
        pnlNhapTienMat.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlNhapTienMat.add(layNhan("Tiền khách đưa"));
        txtDaNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtDaNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDaNhan.getAccessibleContext().setAccessibleName("Tiền khách đưa");

        txtDaNhan.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent suKien) {
                        kiemTraDauVao();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent suKien) {
                        kiemTraDauVao();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent suKien) {
                        kiemTraDauVao();
                    }
                });

        pnlNhapTienMat.add(txtDaNhan);
        pnlNhapTienMat.add(Box.createVerticalStrut(6));

        pnlNhanhCacSoTien.setOpaque(false);
        pnlNhanhCacSoTien.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlNhanhCacSoTien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        pnlNhapTienMat.add(pnlNhanhCacSoTien);
        pnlNhapTienMat.add(Box.createVerticalStrut(12));

        lblNhanThayDoi.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        lblNhanThayDoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlNhapTienMat.add(lblNhanThayDoi);

        pnlNoiDung.add(pnlNhapTienMat);

        taoKhuVucQR();
        pnlNoiDung.add(pnlQRThanhToan);

        chkDaNhanTien.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkDaNhanTien.setOpaque(false);
        chkDaNhanTien.addActionListener(
                suKien -> {
                    banHang.xacNhanDaNhanTien(
                            layPhuongThuc(), chkDaNhanTien.isSelected());
                    kiemTraDauVao();
                });
        pnlNoiDung.add(Box.createVerticalStrut(8));
        pnlNoiDung.add(chkDaNhanTien);

        pnlNoiDung.add(Box.createVerticalGlue());

        JPanel pnlNoiDungVungChua = taoVungTrongSuot(new BorderLayout());
        pnlNoiDungVungChua.add(pnlNoiDung, BorderLayout.NORTH);

        JScrollPane scrThanhToan = new JScrollPane(pnlNoiDungVungChua);
        scrThanhToan.setBorder(null);
        scrThanhToan.getVerticalScrollBar().setUnitIncrement(16);
        pnlPhai.add(scrThanhToan, BorderLayout.CENTER);

        btnXacNhan.setPreferredSize(new Dimension(0, 46));
        btnXacNhan.addActionListener(suKien -> xacNhanThanhToan());

        btnHuy.addActionListener(
                suKien -> quayLaiManHinhNguon());

        btnTreoDoi.addActionListener(e -> treoDonDoi(false));
        JPanel pnlCacThaoTac = new JPanel(new BorderLayout(0, 8));
        pnlCacThaoTac.setOpaque(false);
        JPanel pnlThaoTacPhu = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlThaoTacPhu.setOpaque(false);
        pnlThaoTacPhu.add(btnHuy);
        pnlThaoTacPhu.add(btnTreoDoi);
        pnlCacThaoTac.add(pnlThaoTacPhu, BorderLayout.NORTH);
        pnlCacThaoTac.add(btnXacNhan, BorderLayout.SOUTH);
        pnlPhai.add(pnlCacThaoTac, BorderLayout.SOUTH);

        pnlBieuMau.add(pnlTrai);
        pnlBieuMau.add(pnlPhai);
        return pnlBieuMau;
    }

    private void taoKhuVucQR() {
        pnlQRThanhToan.setOpaque(false);
        pnlQRThanhToan.setLayout(new BoxLayout(pnlQRThanhToan, BoxLayout.Y_AXIS));
        pnlQRThanhToan.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlQRThanhToan.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(10, 10, 10, 10)));

        JLabel lblTieuDeQR = new JLabel("Quét mã bằng ứng dụng ngân hàng");
        lblTieuDeQR.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        lblTieuDeQR.setForeground(MauUngDung.NAU);
        lblTieuDeQR.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlQRThanhToan.add(lblTieuDeQR);
        pnlQRThanhToan.add(Box.createVerticalStrut(8));

        lblQR.setHorizontalAlignment(SwingConstants.CENTER);
        lblQR.setPreferredSize(new Dimension(220, 220));
        lblQR.setMinimumSize(new Dimension(220, 220));
        lblQR.setMaximumSize(new Dimension(220, 220));

        JPanel pnlCanGiuaQR = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlCanGiuaQR.setOpaque(false);
        pnlCanGiuaQR.add(lblQR);
        pnlCanGiuaQR.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCanGiuaQR.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        pnlQRThanhToan.add(pnlCanGiuaQR);
        pnlQRThanhToan.add(Box.createVerticalStrut(8));

        lblThongTinQR.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblThongTinQR.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlQRThanhToan.add(lblThongTinQR);
        pnlQRThanhToan.add(Box.createVerticalStrut(8));

        JLabel lblNhanNoiDung = new JLabel("Nội dung chuyển khoản");
        lblNhanNoiDung.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        lblNhanNoiDung.setForeground(MauUngDung.VAN_BAN_XAM);
        lblNhanNoiDung.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlQRThanhToan.add(lblNhanNoiDung);

        JPanel pnlDongNoiDung = new JPanel(new BorderLayout(8, 0));
        pnlDongNoiDung.setOpaque(false);
        pnlDongNoiDung.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblNoiDungQR.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblNoiDungQR.setForeground(MauUngDung.DO_THUONG_HIEU);
        pnlDongNoiDung.add(lblNoiDungQR, BorderLayout.CENTER);

        btnSaoChepNoiDungQR.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        btnSaoChepNoiDungQR.setFocusPainted(false);
        btnSaoChepNoiDungQR.addActionListener(
                suKien -> saoChepNoiDungQR());
        pnlDongNoiDung.add(btnSaoChepNoiDungQR, BorderLayout.EAST);
        pnlQRThanhToan.add(pnlDongNoiDung);
        pnlQRThanhToan.add(Box.createVerticalStrut(8));

        lblTrangThaiQR.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblTrangThaiQR.setForeground(MauUngDung.CAM);
        lblTrangThaiQR.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTrangThaiQR.setText(
                "Chưa xác nhận giao dịch. Thu ngân cần kiểm tra tài khoản nhận tiền.");
        pnlQRThanhToan.add(lblTrangThaiQR);

        pnlQRThanhToan.setVisible(false);
    }

    private JLabel layNhan(String vanBan) {
        JLabel lblDongDangXet = new JLabel(vanBan);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        lblDongDangXet.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDongDangXet.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lblDongDangXet;
    }

    private PhuongThuc layPhuongThuc() {
        return btnChuyenKhoan.isSelected()
                ? PhuongThuc.CHUYEN_KHOAN
                : btnTheThanhToan.isSelected()
                        ? PhuongThuc.THE_NGAN_HANG
                        : PhuongThuc.TIEN_MAT;
    }

    private void capNhatTrangThaiVoucherKhach() {
        boolean coKhachHang =
                banHang.getMaKhachHang() != null
                        && !banHang.getMaKhachHang().isBlank();

        btnChonVoucher.setEnabled(coKhachHang);
        btnChonVoucher.setToolTipText(
                coKhachHang
                        ? "Chọn voucher đang sở hữu của khách hàng."
                        : "Vui lòng chọn khách hàng ở màn hình Lập hóa đơn trước.");
    }

    private void chonVoucher() {
        if (banHang.getMaKhachHang().isBlank()) {
            HopThoai.hienThiCanhBao(
                    this, "Cần chọn khách hàng ở màn hình Lập hóa đơn trước.");
            capNhatTrangThaiVoucherKhach();
            return;
        }

        try {
            var cacVoucher =
                    com.petstation.service.TichDiemService.layVoucher(
                            banHang.getMaKhachHang());

            if (cacVoucher.isEmpty()) {
                HopThoai.hienThiThongTin(
                        this, "Khách hàng chưa có voucher riêng.");
                return;
            }

            String[] cacMa =
                    cacVoucher.stream()
                            .map(
                                    v ->
                                            v[0]
                                                    + " — "
                                                    + TienTe.dinhDang(
                                                            ((Number) v[1]).longValue())
                                                    + " — "
                                                    + v[3]
                                                    + " — hạn "
                                                    + v[2])
                            .toArray(String[]::new);

            String chon =
                    (String)
                            JOptionPane.showInputDialog(
                                    this,
                                    "Chọn voucher của khách (voucher Spa chỉ áp dụng hóa đơn Spa)",
                                    "Voucher",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    cacMa,
                                    cacMa[0]);

            if (chon != null) {
                txtTruongVoucher.setText(chon.split(" — ", 2)[0]);
                apDungVoucher();
            }
        } catch (RuntimeException loi) {
            HopThoai.hienThiCanhBao(this, loi.getMessage());
        }
    }

    private void apDungVoucher() {
        String loi = banHang.apDungVoucher(txtTruongVoucher.getText());

        if (loi != null) {
            lblVoucherThongBao.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            lblVoucherThongBao.setText(loi);
        } else if (banHang.getMaVoucher().isEmpty()) {
            lblVoucherThongBao.setText("");
        } else {
            lblVoucherThongBao.setForeground(MauUngDung.XANH_LA);
            lblVoucherThongBao.setText(
                    "Đã áp dụng "
                            + banHang.getMaVoucher()
                            + ": −"
                            + TienTe.dinhDang(banHang.getSoTienVoucher()));
        }

        lamMoiCacTong();
    }

    private void lamMoi() {
        capNhatNhanNutQuayLai();
        capNhatTrangThaiVoucherKhach();

        if (banHang.kiemTraTrong()) {
            cacThe.show(pnlGoc, "empty");
            return;
        }

        cacThe.show(pnlGoc, "form");

        DefaultTableModel moHinh =
                new DefaultTableModel(
                        new String[] {"Sản phẩm", "SL", "Đơn giá", "Thành tiền"}, 0) {
                    @Override
                    public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                        return false;
                    }
                };

        for (GioHang.DongHang dongDangXet : banHang.getCacDong()) {
            moHinh.addRow(
                    new Object[] {
                        dongDangXet.getTen(),
                        dongDangXet.getSoLuong(),
                        TienTe.dinhDang(dongDangXet.getDonGia()),
                        TienTe.dinhDang(dongDangXet.docSoTien())
                    });
        }

        pnlDongVungChua.removeAll();
        JScrollPane scrHang = taoBangDinhDang(moHinh);
        com.petstation.gui.components.common.BangChiTietHoaDon.apDung(
                layBang(scrHang));
        pnlDongVungChua.add(scrHang, BorderLayout.CENTER);
        pnlDongVungChua.revalidate();

        txtTruongVoucher.setText(banHang.getMaVoucher());
        lblVoucherThongBao.setText("");

        boolean doiHang =
            "DOI_HANG".equals(banHang.getKieuNguon());

        dieuKhienVoucher.forEach(
            c -> c.setEnabled(!doiHang));

        if (!doiHang) {
            capNhatTrangThaiVoucherKhach();
        }

        if (doiHang) {
            lblVoucherThongBao.setForeground(MauUngDung.VAN_BAN_XAM);
            lblVoucherThongBao.setText(
                "Voucher Spa không áp dụng cho đổi phụ kiện.");
        }

        txtDaNhan.setText(
            doiHang && banHang.tinhTong() == 0 ? "0" : "");

        dangXuLy = false;
        btnTienMat.setSelected(true);
        capNhatPhuongThuc();
        lamMoiCacTong();
    }

    private void lamMoiCacTong() {
        lblTamTinh.setText(TienTe.dinhDang(banHang.tinhTamTinh()));

        long ngayDangXet = banHang.tinhGiamGia();
        lblGiamGia.setText(
                ngayDangXet == 0 ? "0₫" : "−" + TienTe.dinhDang(ngayDangXet));

        lblTong.setText(TienTe.dinhDang(banHang.tinhTong()));

        capNhatQR();
        dungLaiNhanhCacSoTien();
        kiemTraDauVao();
    }

    private void dungLaiNhanhCacSoTien() {
        pnlNhanhCacSoTien.removeAll();

        long giaTriTam = banHang.tinhTong();
        Set<Long> cacLuaChon = new LinkedHashSet<>();
        cacLuaChon.add(giaTriTam);

        for (long buoc : new long[] {50_000, 100_000, 500_000}) {
            long len = ((giaTriTam + buoc - 1) / buoc) * buoc;
            if (len >= giaTriTam) cacLuaChon.add(len);
        }

        for (long soTien : cacLuaChon) {
            JButton btnGiaTriSau =
                    new JButton(
                            soTien == giaTriTam
                                    ? "Đủ tiền"
                                    : TienTe.dinhDang(soTien));

            btnGiaTriSau.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            btnGiaTriSau.setFocusPainted(false);

            if (pnlNhanhCacSoTien.getComponentCount() > 0) {
                pnlNhanhCacSoTien.add(Box.createHorizontalStrut(8));
            }

            btnGiaTriSau.addActionListener(
                    suKien -> txtDaNhan.setText(String.valueOf(soTien)));
            pnlNhanhCacSoTien.add(btnGiaTriSau);
        }

        pnlNhanhCacSoTien.revalidate();
        pnlNhanhCacSoTien.repaint();
    }

    private void capNhatQR() {
        boolean laTienMat = layPhuongThuc() == PhuongThuc.TIEN_MAT;
        boolean chuyenKhoan =
                layPhuongThuc() == PhuongThuc.CHUYEN_KHOAN;

        pnlNhapTienMat.setVisible(laTienMat);
        pnlQRThanhToan.setVisible(chuyenKhoan);
        chkDaNhanTien.setVisible(!laTienMat);

        chkDaNhanTien.setText(
                chuyenKhoan
                        ? "Tôi đã kiểm tra và nhận đủ tiền trên tài khoản ngân hàng (Phím Space)"
                        : "Tôi đã xác nhận giao dịch thẻ thành công");

        chkDaNhanTien.setSelected(false);
        banHang.xacNhanDaNhanTien(layPhuongThuc(), false);

        qrHopLe = false;
        noiDungChuyenKhoanQR = "";
        btnSaoChepNoiDungQR.setEnabled(false);

        if (!chuyenKhoan) {
            pnlNoiDungRepaint();
            return;
        }

        try {
            String bin =
                    com.petstation.service.CaiDatService.layTuyChon("BANK_BIN");
            String taiKhoan =
                    com.petstation.service.CaiDatService.layTuyChon("BANK_ACCOUNT");
            String tenChuTaiKhoan =
                    com.petstation.service.CaiDatService.layTuyChon(
                            "BANK_ACCOUNT_NAME");

            com.petstation.util.TaoMaVietQR.kiemTraTaiKhoan(
                    bin, taiKhoan, tenChuTaiKhoan);

            String maHoaDon = banHang.getMaHoaDonDuKien();
            long soTienCanChuyen = banHang.tinhTong();

            String duLieuQR =
                    com.petstation.util.TaoMaVietQR.taoNoiDung(
                            bin, taiKhoan, soTienCanChuyen, maHoaDon);

            lblQR.setText("");
            lblQR.setIcon(
                    new ImageIcon(
                            com.petstation.util.TaoMaVietQR.taoAnh(
                                    duLieuQR, 220)));

            String tenCuaHang =
                    com.petstation.service.CaiDatService.lay("STORE_NAME");

            lblThongTinQR.setText(
                    "<html><div style='width:250px'>"
                            + "<b>"
                            + thoatHTML(tenCuaHang)
                            + "</b><br>"
                            + "Người nhận: "
                            + thoatHTML(tenChuTaiKhoan)
                            + "<br>"
                            + "Số tài khoản: "
                            + thoatHTML(taiKhoan)
                            + "<br>"
                            + "BIN ngân hàng: "
                            + thoatHTML(bin)
                            + "<br><br>"
                            + "<font color='#15803D'><b>SỐ TIỀN CẦN CHUYỂN: "
                            + thoatHTML(TienTe.dinhDang(soTienCanChuyen))
                            + "</b></font><br>"
                            + "</div></html>");

            noiDungChuyenKhoanQR = maHoaDon;
            lblNoiDungQR.setText(maHoaDon);
            lblTrangThaiQR.setForeground(MauUngDung.CAM);
            lblTrangThaiQR.setText(
                    "<html><div style='width:250px'>"
                            + "Đang chờ xác nhận. QR đã điền sẵn số tiền và mã hóa đơn; "
                            + "thu ngân cần kiểm tra giao dịch thực tế trước khi xác nhận."
                            + "</div></html>");

            btnSaoChepNoiDungQR.setEnabled(true);
            qrHopLe = true;
        } catch (RuntimeException loi) {
            lblQR.setIcon(null);
            lblQR.setText("Không tạo được mã QR");
            lblThongTinQR.setText(
                    "<html><div style='width:250px'>"
                            + thoatHTML(loi.getMessage())
                            + "<br>Kiểm tra cấu hình ngân hàng trong Cài đặt."
                            + "</div></html>");
            lblNoiDungQR.setText("");
            lblTrangThaiQR.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            lblTrangThaiQR.setText("Chưa thể tạo QR thanh toán.");
        }

        pnlNoiDungRepaint();
    }

    private void saoChepNoiDungQR() {
        if (noiDungChuyenKhoanQR == null || noiDungChuyenKhoanQR.isBlank()) {
            HopThoai.hienThiCanhBao(this, "Chưa có nội dung chuyển khoản để sao chép.");
            return;
        }

        try {
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(noiDungChuyenKhoanQR), null);

            lblTrangThaiQR.setForeground(MauUngDung.XANH_LA);
            lblTrangThaiQR.setText(
                    "Đã sao chép nội dung chuyển khoản: " + noiDungChuyenKhoanQR);
        } catch (RuntimeException loi) {
            HopThoai.hienThiCanhBao(
                    this, "Không thể sao chép nội dung: " + loi.getMessage());
        }
    }

    private static String thoatHTML(String giaTri) {
        if (giaTri == null) return "";
        return giaTri.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private void pnlNoiDungRepaint() {
        pnlQRThanhToan.revalidate();
        pnlQRThanhToan.repaint();
        revalidate();
        repaint();
    }

    private void capNhatPhuongThuc() {
        boolean laTienMat =
                layPhuongThuc() == PhuongThuc.TIEN_MAT;

        txtDaNhan.setEnabled(laTienMat);

        for (Component thanhPhan : pnlNhanhCacSoTien.getComponents()) {
            thanhPhan.setEnabled(laTienMat);
        }

        capNhatQR();
        kiemTraDauVao();
    }

    private void kiemTraDauVao() {
        if (banHang.kiemTraTrong()) return;

        long giaTriTam = banHang.tinhTong();
        PhuongThuc phuongThuc = layPhuongThuc();

        if (phuongThuc != PhuongThuc.TIEN_MAT) {
            boolean chuyenKhoan =
                    phuongThuc == PhuongThuc.CHUYEN_KHOAN;

            chkDaNhanTien.setEnabled(!chuyenKhoan || qrHopLe);

            if (chuyenKhoan) {
                lblTrangThaiQR.setForeground(
                        chkDaNhanTien.isSelected()
                                ? MauUngDung.XANH_LA
                                : MauUngDung.CAM);
                lblTrangThaiQR.setText(
                        chkDaNhanTien.isSelected()
                                ? "Thu ngân đã xác nhận thủ công đã nhận đúng tiền cho hóa đơn "
                                        + banHang.getMaHoaDonDuKien()
                                : "Đang chờ thu ngân kiểm tra giao dịch thực tế.");
            }

            btnXacNhan.setEnabled(
                    !dangXuLy
                            && chkDaNhanTien.isSelected()
                            && (!chuyenKhoan || qrHopLe));
            return;
        }

        long tienKhachDua = TienTe.doc(txtDaNhan.getText());

        if (txtDaNhan.getText().isBlank()) {
            lblNhanThayDoi.setForeground(MauUngDung.VAN_BAN_XAM);
            lblNhanThayDoi.setText("Nhập số tiền khách đưa");
            btnXacNhan.setEnabled(false);
        } else if (tienKhachDua < 0) {
            lblNhanThayDoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            lblNhanThayDoi.setText("Số tiền không hợp lệ");
            btnXacNhan.setEnabled(false);
        } else if (tienKhachDua < giaTriTam) {
            lblNhanThayDoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            lblNhanThayDoi.setText(
                    "Còn thiếu "
                            + TienTe.dinhDang(giaTriTam - tienKhachDua));
            btnXacNhan.setEnabled(false);
        } else {
            lblNhanThayDoi.setForeground(MauUngDung.XANH_LA);
            lblNhanThayDoi.setText(
                    "Tiền thừa trả khách: "
                            + TienTe.dinhDang(tienKhachDua - giaTriTam));
            btnXacNhan.setEnabled(!dangXuLy);
        }
    }

    private void xacNhanThanhToan() {
        if (dangXuLy || banHang.kiemTraTrong()) return;

        if (!banHang.kiemTraDaNhanTien(layPhuongThuc())) {
            HopThoai.hienThiCanhBao(
                    this,
                    "Hãy kiểm tra và xác nhận đã nhận đủ tiền trước khi hoàn tất.");
            return;
        }

        long giaTriTam = banHang.tinhTong();
        long daNhan =
                layPhuongThuc() == PhuongThuc.TIEN_MAT
                        ? TienTe.doc(txtDaNhan.getText())
                        : giaTriTam;

        if (daNhan < giaTriTam) return;

        if (!caLamViec.kiemTraMo()) {
            int luaChon = JOptionPane.showConfirmDialog(
                    this,
                    "Ca thu ngân hiện chưa mở. Bạn cần mở ca trước khi thanh toán và ghi nhận doanh thu.\n"
                            + "Bạn có muốn chuyển sang màn hình Ca thu ngân để mở ca ngay không?",
                    "Chưa mở ca làm việc",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (luaChon == JOptionPane.YES_OPTION) {
                boDieuHuong.accept(MaManHinh.CA_THU_NGAN);
            }
            return;
        }

        String thongBao =
                "Xác nhận thu "
                        + TienTe.dinhDang(giaTriTam)
                        + " cho hóa đơn "
                        + banHang.getMaHoaDonDuKien()
                        + "?";

        if (layPhuongThuc() == PhuongThuc.CHUYEN_KHOAN) {
            thongBao +=
                    "\nChỉ tiếp tục sau khi đã kiểm tra giao dịch thực tế trên tài khoản ngân hàng.";
        }

        int dongY =
                HopThoai.hienThiXacNhanHopThoai(
                        this,
                        thongBao,
                        "Xác nhận thanh toán",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

        if (dongY != JOptionPane.OK_OPTION) return;

        dangXuLy = true;
        btnXacNhan.setEnabled(false);

        try {
            GioHang.BienLai bienLai =
                    banHang.hoanTat(layPhuongThuc(), daNhan, thuNgan);
            caLamViec.ghiNhanBanHang(layPhuongThuc(), bienLai.getTong());
            banHang.xoa();
            boDieuHuong.accept(MaManHinh.IN_HOA_DON);
        } catch (RuntimeException ngoaiLe) {
            dangXuLy = false;
            kiemTraDauVao();
            if (ngoaiLe.getMessage() != null && ngoaiLe.getMessage().contains("Cần mở ca")) {
                int luaChon = JOptionPane.showConfirmDialog(
                        this,
                        ngoaiLe.getMessage() + "\nBạn có muốn chuyển sang màn hình Ca thu ngân để mở ca ngay không?",
                        "Chưa mở ca làm việc",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (luaChon == JOptionPane.YES_OPTION) {
                    boDieuHuong.accept(MaManHinh.CA_THU_NGAN);
                }
            } else {
                HopThoai.hienThiCanhBao(
                        this, "Chưa thanh toán: " + ngoaiLe.getMessage());
            }
        }
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(
                false, false, false, false, false, true);
    }

    @Override
    public void xuLyHienThi() {
        lamMoi();
    }

    @Override
    public void xuLyLuu() {}

    @Override
    protected String layNoiDungTroGiup() {
        return "Chọn phương thức thanh toán. Với tiền mặt, nhập số tiền khách đưa. "
                + "Với QR, mã chứa sẵn số tiền phải trả và mã hóa đơn; thu ngân phải kiểm tra "
                + "giao dịch thực tế trên tài khoản ngân hàng trước khi xác nhận. "
                + "Ứng dụng hiện chưa tự nhận thông báo tiền vào từ ngân hàng.";
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Thanh toán";
    }
}
