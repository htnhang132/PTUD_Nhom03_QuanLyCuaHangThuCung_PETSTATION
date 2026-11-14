package com.petstation.gui.banhang;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.BieuMauPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.LoaiPhieuDoiTra;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.banhang.DonDoiTreoService;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.khachhang.TichDiemService;
import com.petstation.util.DinhDang;
import com.petstation.util.TaoMaVietQR;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

// Màn hình: Thanh toán & Thu tiền (Mã: THANH_TOAN)
// Luồng xử lý: GUI -> BanHangService, GioHangService -> HoaDonDAOImpl, KhoanThuDAOImpl -> SQL
// (hoa_don, dong_thanh_toan_hoa_don)
// Mô tả: Giao diện xử lý thanh toán đa phương thức (tiền mặt, chuyển khoản QR, voucher)
// Phân hệ: banhang
public final class ThanhToanPanel extends CoSoPanel {
    private final GioHang banHang;
    private final CaThuNganService caLamViec;
    private final String thuNgan;
    private final Consumer<MaManHinh> boDieuHuong;
    private final TichDiemService tichDiemService = new TichDiemService();
    private final Consumer<MaManHinh> quayLaiTuThanhToan;

    private final CardLayout cacThe = new CardLayout();
    private final JPanel khungGoc = new JPanel(cacThe);
    private final JPanel khungDongVungChua = taoVungTrongSuot(new BorderLayout());

    private final JLabel nhanTamTinh = new JLabel();
    private final JLabel nhanGiamGia = new JLabel();
    private final JLabel nhanTong = new JLabel();

    private final JTextField oTruongVoucher = BieuMauPanel.layTruongVanBan("");
    private final List<JComponent> dieuKhienVoucher = new ArrayList<>();
    private final JLabel nhanVoucherThongBao = new JLabel("");

    private final JRadioButton btnTienMat = new JRadioButton("Tiền mặt", true);
    private final JRadioButton btnChuyenKhoan = new JRadioButton("Chuyển khoản / QR");
    private final JRadioButton btnTheThanhToan = new JRadioButton("Thẻ");

    private final NutDinhDang btnChonVoucher =
            new NutDinhDang("Chọn voucher của khách", NutDinhDang.KieuNut.TRONG_SUOT);

    private final JTextField oDaNhan = BieuMauPanel.layTruongVanBan("");
    private final JPanel khungNhanhCacSoTien = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private final JPanel khungNhapTienMat = new JPanel();

    private final JLabel nhanNhanThayDoi = new JLabel("");
    private final NutDinhDang btnXacNhan =
            new NutDinhDang("Xác nhận thanh toán (F9)", NutDinhDang.KieuNut.CHINH);
    private final NutDinhDang btnQuayLaiTrangThaiRong =
            new NutDinhDang("Về lập đơn hàng", NutDinhDang.KieuNut.CHINH);

    private final NutDinhDang btnHuy =
            new NutDinhDang("Quay lại sửa đơn", NutDinhDang.KieuNut.TRONG_SUOT);
    private final NutDinhDang btnTreoDoi =
            new NutDinhDang("Treo đơn đổi", NutDinhDang.KieuNut.TRONG_SUOT);

    private final JPanel khungQRThanhToan = new JPanel();
    private final JLabel nhanQrLon = new JLabel();
    private final JLabel nhanThongTinQR = new JLabel();
    private final JLabel nhanNoiDungQR = new JLabel();
    private final JLabel nhanTrangThaiQR = new JLabel();
    private final NutDinhDang btnSaoChepNoiDungQR =
            new NutDinhDang("Sao chép nội dung", NutDinhDang.KieuNut.TRONG_SUOT);

    private final JCheckBox chkDaNhanTien = new JCheckBox("Tôi đã kiểm tra và nhận đủ tiền");
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
        this.boLangNgheGioHang =
                gio -> {
                    if (SwingUtilities.isEventDispatchThread()) {
                        lamMoi();
                    } else {
                        SwingUtilities.invokeLater(this::lamMoi);
                    }
                };
        setLayout(new BorderLayout(0, 14));
        add(
                taoTieuDeManHinh("Thanh toán", "Kiểm tra hóa đơn, chọn phương thức và xác nhận."),
                BorderLayout.NORTH);
        khungGoc.setOpaque(false);
        khungGoc.add(taoBieuMau(), "form");
        btnQuayLaiTrangThaiRong.addActionListener(suKien -> quayLaiManHinhNguon());
        khungGoc.add(
                layTrongTrangThai(
                        "Chưa có hóa đơn để thanh toán",
                        "Hãy thêm sản phẩm ở màn Lập đơn hàng trước.",
                        btnQuayLaiTrangThaiRong),
                "empty");
        add(khungGoc, BorderLayout.CENTER);
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "xacNhanVietQRSpace");
        am.put(
                "xacNhanVietQRSpace",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Component focusOwner =
                                KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                        .getFocusOwner();
                        if (focusOwner instanceof JTextComponent) {
                            return;
                        }
                        if (layPhuongThuc() == PhuongThuc.CHUYEN_KHOAN
                                && chkDaNhanTien.isVisible()
                                && chkDaNhanTien.isEnabled()) {
                            chkDaNhanTien.setSelected(!chkDaNhanTien.isSelected());
                            banHang.xacNhanDaNhanTien(layPhuongThuc(), chkDaNhanTien.isSelected());
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
        if (LoaiPhieuDoiTra.DOI_HANG.maDB().equals(banHang.getKieuNguon())) {
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
            DonDoiTreoService donDoiTreoService = new DonDoiTreoService();
            String maDon = donDoiTreoService.treo(banHang);
            var nhap = suaLai ? donDoiTreoService.lay(maDon) : null;
            banHang.xoa();
            boDieuHuong.accept(suaLai ? MaManHinh.DOI_TRA_HANG_HOA : MaManHinh.LAP_HOA_DON);
            if (suaLai) {
                new DoiPhuKienDialog(this, banHang, boDieuHuong, maDon, nhap).setVisible(true);
            } else {
                HopThoai.hienThiThanhCong(
                        this, "Đã treo đơn đổi. Mở lại tại Đơn đang treo để tiếp tục.");
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
        btnTreoDoi.setVisible(LoaiPhieuDoiTra.DOI_HANG.maDB().equals(nguon));
        if ("HOTEL".equals(nguon) || "LUU_TRU".equals(nguon)) {
            btnQuayLaiTrangThaiRong.setText("Về trả phòng Hotel");
            btnHuy.setText("Quay lại trả phòng");
        } else if ("SPA".equals(nguon)) {
            btnQuayLaiTrangThaiRong.setText("Về đóng phiếu Spa");
            btnHuy.setText("Quay lại đóng phiếu");
        } else if (LoaiPhieuDoiTra.DOI_HANG.maDB().equals(nguon)) {
            btnHuy.setText("Sửa đơn đổi");
            btnQuayLaiTrangThaiRong.setText("Về đổi phụ kiện");
        } else {
            btnQuayLaiTrangThaiRong.setText("Về lập đơn hàng");
            btnHuy.setText("Quay lại sửa đơn");
        }
    }

    private JPanel taoBieuMau() {
        JPanel khungBieuMau = new JPanel(new GridLayout(1, 2, 16, 0));
        khungBieuMau.setOpaque(false);
        khungBieuMau.add(taoCotTraiHoaDon());
        khungBieuMau.add(taoCotPhaiThanhToan());
        return khungBieuMau;
    }

    private JPanel taoCotTraiHoaDon() {
        JPanel khungTrai = layThe(new BorderLayout(0, 12));
        khungTrai.add(taoTieuDeMuc("Hóa đơn"), BorderLayout.NORTH);
        khungTrai.add(khungDongVungChua, BorderLayout.CENTER);
        JPanel khungCacTong = new JPanel(new GridLayout(3, 2, 0, 4));
        khungCacTong.setOpaque(false);
        khungCacTong.add(new JLabel("Tạm tính"));
        nhanTamTinh.setHorizontalAlignment(SwingConstants.RIGHT);
        khungCacTong.add(nhanTamTinh);
        khungCacTong.add(new JLabel("Giảm giá và voucher"));
        nhanGiamGia.setHorizontalAlignment(SwingConstants.RIGHT);
        khungCacTong.add(nhanGiamGia);
        JLabel nhanVanBanTong = new JLabel("Cần thanh toán");
        nhanVanBanTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        khungCacTong.add(nhanVanBanTong);
        nhanTong.setHorizontalAlignment(SwingConstants.RIGHT);
        nhanTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 24));
        nhanTong.setForeground(MauUngDung.DO_THUONG_HIEU);
        khungCacTong.add(nhanTong);
        khungTrai.add(khungCacTong, BorderLayout.SOUTH);
        return khungTrai;
    }

    private JPanel taoCotPhaiThanhToan() {
        JPanel khungPhai = layThe(new BorderLayout(0, 12));
        khungPhai.add(taoTieuDeMuc("Phương thức thanh toán"), BorderLayout.NORTH);
        JPanel khungNoiDung = new JPanel();
        khungNoiDung.setOpaque(false);
        khungNoiDung.setLayout(new BoxLayout(khungNoiDung, BoxLayout.Y_AXIS));
        khungNoiDung.setBorder(new EmptyBorder(8, 10, 8, 10));
        khungNoiDung.add(taoNhomPhuongThuc());
        khungNoiDung.add(Box.createVerticalStrut(14));
        themKhuVucVoucher(khungNoiDung);
        themKhuVucTienMatVaQR(khungNoiDung);
        khungNoiDung.add(Box.createVerticalGlue());
        JPanel khungNoiDungVungChua = taoVungTrongSuot(new BorderLayout());
        khungNoiDungVungChua.add(khungNoiDung, BorderLayout.NORTH);
        JScrollPane scrThanhToan = new JScrollPane(khungNoiDungVungChua);
        scrThanhToan.setBorder(null);
        scrThanhToan.getVerticalScrollBar().setUnitIncrement(16);
        khungPhai.add(scrThanhToan, BorderLayout.CENTER);
        khungPhai.add(taoThanhThaoTacThanhToan(), BorderLayout.SOUTH);
        return khungPhai;
    }

    private JPanel taoNhomPhuongThuc() {
        ButtonGroup nhom = new ButtonGroup();
        JPanel khungCacPhuongThuc = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        khungCacPhuongThuc.setOpaque(false);
        for (JRadioButton btnDongDuLieu :
                new JRadioButton[] {btnTienMat, btnChuyenKhoan, btnTheThanhToan}) {
            btnDongDuLieu.setOpaque(false);
            btnDongDuLieu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            btnDongDuLieu.addActionListener(suKien -> capNhatPhuongThuc());
            nhom.add(btnDongDuLieu);
            khungCacPhuongThuc.add(btnDongDuLieu);
        }
        khungCacPhuongThuc.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungCacPhuongThuc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return khungCacPhuongThuc;
    }

    private void themKhuVucVoucher(JPanel khungNoiDung) {
        khungNoiDung.add(layNhan("Mã voucher"));
        JPanel khungVoucherDong = new JPanel(new BorderLayout(8, 0));
        khungVoucherDong.setOpaque(false);
        oTruongVoucher.setPreferredSize(new Dimension(180, 36));
        NutDinhDang btnApDung = new NutDinhDang("Áp dụng", NutDinhDang.KieuNut.TRONG_SUOT);
        btnApDung.addActionListener(suKien -> apDungVoucher());
        oTruongVoucher.addActionListener(suKien -> apDungVoucher());
        khungVoucherDong.add(oTruongVoucher, BorderLayout.CENTER);
        khungVoucherDong.add(btnApDung, BorderLayout.EAST);
        khungVoucherDong.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungVoucherDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        khungNoiDung.add(khungVoucherDong);
        nhanVoucherThongBao.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        nhanVoucherThongBao.setAlignmentX(Component.LEFT_ALIGNMENT);
        nhanVoucherThongBao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        khungNoiDung.add(nhanVoucherThongBao);
        khungNoiDung.add(Box.createVerticalStrut(8));
        JPanel khungVoucherKhach = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        khungVoucherKhach.setOpaque(false);
        khungVoucherKhach.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnChonVoucher.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnChonVoucher.setEnabled(false);
        btnChonVoucher.setToolTipText("Vui lòng chọn khách hàng ở màn hình Lập hóa đơn trước.");
        btnChonVoucher.addActionListener(suKien -> chonVoucher());
        khungVoucherKhach.add(btnChonVoucher);
        khungNoiDung.add(khungVoucherKhach);
        khungNoiDung.add(Box.createVerticalStrut(12));
        dieuKhienVoucher.addAll(List.of(oTruongVoucher, btnApDung, btnChonVoucher));
    }

    private void themKhuVucTienMatVaQR(JPanel khungNoiDung) {
        khungNhapTienMat.setOpaque(false);
        khungNhapTienMat.setLayout(new BoxLayout(khungNhapTienMat, BoxLayout.Y_AXIS));
        khungNhapTienMat.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungNhapTienMat.add(layNhan("Tiền khách đưa"));
        oDaNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        oDaNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        oDaNhan.getAccessibleContext().setAccessibleName("Tiền khách đưa");
        themBoLangNgheTaiLieu(oDaNhan, this::kiemTraDauVao);
        khungNhapTienMat.add(oDaNhan);
        khungNhapTienMat.add(Box.createVerticalStrut(6));
        khungNhanhCacSoTien.setOpaque(false);
        khungNhanhCacSoTien.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungNhanhCacSoTien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        khungNhapTienMat.add(khungNhanhCacSoTien);
        khungNhapTienMat.add(Box.createVerticalStrut(12));
        nhanNhanThayDoi.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        nhanNhanThayDoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungNhapTienMat.add(nhanNhanThayDoi);
        khungNoiDung.add(khungNhapTienMat);
        taoKhuVucQR();
        khungNoiDung.add(khungQRThanhToan);
        chkDaNhanTien.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkDaNhanTien.setOpaque(false);
        chkDaNhanTien.addActionListener(
                suKien -> {
                    banHang.xacNhanDaNhanTien(layPhuongThuc(), chkDaNhanTien.isSelected());
                    kiemTraDauVao();
                });
        khungNoiDung.add(Box.createVerticalStrut(8));
        khungNoiDung.add(chkDaNhanTien);
    }

    private JPanel taoThanhThaoTacThanhToan() {
        btnXacNhan.setPreferredSize(new Dimension(0, 46));
        btnXacNhan.addActionListener(suKien -> xacNhanThanhToan());
        btnHuy.addActionListener(suKien -> quayLaiManHinhNguon());
        btnTreoDoi.addActionListener(e -> treoDonDoi(false));
        JPanel khungCacThaoTac = new JPanel(new BorderLayout(0, 8));
        khungCacThaoTac.setOpaque(false);
        JPanel khungThaoTacPhu = new JPanel(new GridLayout(1, 2, 8, 0));
        khungThaoTacPhu.setOpaque(false);
        khungThaoTacPhu.add(btnHuy);
        khungThaoTacPhu.add(btnTreoDoi);
        khungCacThaoTac.add(khungThaoTacPhu, BorderLayout.NORTH);
        khungCacThaoTac.add(btnXacNhan, BorderLayout.SOUTH);
        return khungCacThaoTac;
    }

    private void taoKhuVucQR() {
        khungQRThanhToan.setOpaque(true);
        khungQRThanhToan.setBackground(MauUngDung.NEN_KEM);
        khungQRThanhToan.setLayout(new BoxLayout(khungQRThanhToan, BoxLayout.Y_AXIS));
        khungQRThanhToan.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungQRThanhToan.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.XAM_VIEN_TRUNG, 1, true),
                        new EmptyBorder(12, 12, 12, 12)));
        JLabel nhanTieuDeQR = new JLabel("Quét mã bằng ứng dụng ngân hàng");
        nhanTieuDeQR.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanTieuDeQR.setForeground(MauUngDung.NAU);
        nhanTieuDeQR.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungQRThanhToan.add(nhanTieuDeQR);
        khungQRThanhToan.add(Box.createVerticalStrut(8));
        nhanQrLon.setHorizontalAlignment(SwingConstants.CENTER);
        nhanQrLon.setPreferredSize(new Dimension(220, 220));
        nhanQrLon.setMinimumSize(new Dimension(220, 220));
        nhanQrLon.setMaximumSize(new Dimension(220, 220));
        JPanel khungCanGiuaQR = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        khungCanGiuaQR.setOpaque(false);
        khungCanGiuaQR.add(nhanQrLon);
        khungCanGiuaQR.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungCanGiuaQR.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        khungQRThanhToan.add(khungCanGiuaQR);
        khungQRThanhToan.add(Box.createVerticalStrut(8));
        nhanThongTinQR.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanThongTinQR.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungQRThanhToan.add(nhanThongTinQR);
        khungQRThanhToan.add(Box.createVerticalStrut(8));
        JLabel nhanNhanNoiDung = new JLabel("Nội dung chuyển khoản");
        nhanNhanNoiDung.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        nhanNhanNoiDung.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanNhanNoiDung.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungQRThanhToan.add(nhanNhanNoiDung);
        JPanel khungDongNoiDung = new JPanel(new BorderLayout(8, 0));
        khungDongNoiDung.setOpaque(false);
        khungDongNoiDung.setAlignmentX(Component.LEFT_ALIGNMENT);
        nhanNoiDungQR.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanNoiDungQR.setForeground(MauUngDung.DO_THUONG_HIEU);
        khungDongNoiDung.add(nhanNoiDungQR, BorderLayout.CENTER);
        btnSaoChepNoiDungQR.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        btnSaoChepNoiDungQR.addActionListener(suKien -> saoChepNoiDungQR());
        khungDongNoiDung.add(btnSaoChepNoiDungQR, BorderLayout.EAST);
        khungQRThanhToan.add(khungDongNoiDung);
        khungQRThanhToan.add(Box.createVerticalStrut(8));
        nhanTrangThaiQR.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        nhanTrangThaiQR.setForeground(MauUngDung.CAM);
        nhanTrangThaiQR.setAlignmentX(Component.LEFT_ALIGNMENT);
        nhanTrangThaiQR.setText(
                "Chưa xác nhận giao dịch. Thu ngân cần kiểm tra tài khoản nhận tiền.");
        khungQRThanhToan.add(nhanTrangThaiQR);
        khungQRThanhToan.setVisible(false);
    }

    private JLabel layNhan(String vanBan) {
        JLabel nhanDongDangXet = new JLabel(vanBan);
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanDongDangXet.setAlignmentX(Component.LEFT_ALIGNMENT);
        nhanDongDangXet.setBorder(new EmptyBorder(0, 0, 4, 0));
        return nhanDongDangXet;
    }

    private PhuongThuc layPhuongThuc() {
        return btnChuyenKhoan.isSelected()
                ? PhuongThuc.CHUYEN_KHOAN
                : btnTheThanhToan.isSelected() ? PhuongThuc.THE_NGAN_HANG : PhuongThuc.TIEN_MAT;
    }

    private void capNhatTrangThaiVoucherKhach() {
        boolean coKhachHang =
                banHang.getMaKhachHang() != null && !banHang.getMaKhachHang().isBlank();
        btnChonVoucher.setEnabled(coKhachHang);
        btnChonVoucher.setToolTipText(
                coKhachHang
                        ? "Chọn voucher đang sở hữu của khách hàng."
                        : "Vui lòng chọn khách hàng ở màn hình Lập hóa đơn trước.");
    }

    private void chonVoucher() {
        String maKhach = banHang.getMaKhachHang();
        if (maKhach == null || maKhach.isBlank()) {
            HopThoai.hienThiCanhBao(this, "Cần chọn khách hàng ở màn hình Lập hóa đơn trước.");
            capNhatTrangThaiVoucherKhach();
            return;
        }
        btnChonVoucher.setEnabled(false);
        TacVuNen.chayCoPhien(
                this,
                this::isDisplayable,
                () -> tichDiemService.layVoucher(maKhach),
                cacVoucher -> {
                    capNhatTrangThaiVoucherKhach();
                    if (cacVoucher.isEmpty()) {
                        HopThoai.hienThiThongTin(this, "Khách hàng chưa có voucher riêng.");
                        return;
                    }
                    String[] cacMa =
                            cacVoucher.stream()
                                    .map(
                                            v ->
                                                    v.getMaVoucher()
                                                            + " — "
                                                            + DinhDang.dinhDang(v.getMenhGia())
                                                            + " — "
                                                            + v.getTrangThai()
                                                            + " — hạn "
                                                            + v.getHieuLucDen())
                                    .toArray(String[]::new);
                    String chon =
                            (String)
                                    HopThoai.chonMuc(
                                            this,
                                            "Chọn voucher của khách (voucher Spa chỉ áp dụng hóa"
                                                    + " đơn Spa)",
                                            "Voucher",
                                            cacMa,
                                            cacMa[0]);
                    if (chon != null) {
                        oTruongVoucher.setText(chon.split(" — ", 2)[0]);
                        apDungVoucher();
                    }
                },
                loi -> {
                    capNhatTrangThaiVoucherKhach();
                    HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(loi));
                });
    }

    private void apDungVoucher() {
        String loi = banHang.apDungVoucher(oTruongVoucher.getText());
        if (loi != null) {
            nhanVoucherThongBao.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            nhanVoucherThongBao.setText(loi);
        } else if (banHang.getMaVoucher().isEmpty()) {
            nhanVoucherThongBao.setText("");
        } else {
            nhanVoucherThongBao.setForeground(MauUngDung.XANH_LA);
            nhanVoucherThongBao.setText(
                    "Đã áp dụng "
                            + banHang.getMaVoucher()
                            + ": −"
                            + DinhDang.dinhDang(banHang.getSoTienVoucher()));
        }
        lamMoiCacTong();
    }

    private void lamMoi() {
        capNhatNhanNutQuayLai();
        capNhatTrangThaiVoucherKhach();
        if (banHang.kiemTraTrong()) {
            cacThe.show(khungGoc, "empty");
            return;
        }
        cacThe.show(khungGoc, "form");
        DefaultTableModel moHinh =
                DinhDangBang.taoMoHinhChiDoc(
                        new String[] {"Sản phẩm", "SL", "Đơn giá", "Thành tiền"});
        for (GioHang.DongHang dongDangXet : banHang.getCacDong()) {
            moHinh.addRow(
                    new Object[] {
                        dongDangXet.getTen(),
                        dongDangXet.getSoLuong(),
                        DinhDang.dinhDang(dongDangXet.getDonGia()),
                        DinhDang.dinhDang(dongDangXet.docSoTien())
                    });
        }
        khungDongVungChua.removeAll();
        JScrollPane scrHang = taoBangDinhDang(moHinh);
        BangChiTietHoaDon.apDung(layBang(scrHang));
        khungDongVungChua.add(scrHang, BorderLayout.CENTER);
        khungDongVungChua.revalidate();
        oTruongVoucher.setText(banHang.getMaVoucher());
        nhanVoucherThongBao.setText("");
        boolean doiHang = LoaiPhieuDoiTra.DOI_HANG.maDB().equals(banHang.getKieuNguon());
        dieuKhienVoucher.forEach(c -> c.setEnabled(!doiHang));
        if (!doiHang) {
            capNhatTrangThaiVoucherKhach();
        }
        if (doiHang) {
            nhanVoucherThongBao.setForeground(MauUngDung.VAN_BAN_XAM);
            nhanVoucherThongBao.setText("Voucher Spa không áp dụng cho đổi phụ kiện.");
        }
        oDaNhan.setText(doiHang && banHang.tinhTong() == 0 ? "0" : "");
        dangXuLy = false;
        btnTienMat.setSelected(true);
        capNhatPhuongThuc();
        lamMoiCacTong();
    }

    private void lamMoiCacTong() {
        nhanTamTinh.setText(DinhDang.dinhDang(banHang.tinhTamTinh()));
        long ngayDangXet = banHang.tinhGiamGia();
        nhanGiamGia.setText(ngayDangXet == 0 ? "0₫" : "−" + DinhDang.dinhDang(ngayDangXet));
        nhanTong.setText(DinhDang.dinhDang(banHang.tinhTong()));
        capNhatQR();
        dungLaiNhanhCacSoTien();
        kiemTraDauVao();
    }

    private void dungLaiNhanhCacSoTien() {
        khungNhanhCacSoTien.removeAll();
        long giaTriTam = banHang.tinhTong();
        Set<Long> cacLuaChon = new LinkedHashSet<>();
        cacLuaChon.add(giaTriTam);
        for (long buoc : new long[] {50_000, 100_000, 500_000}) {
            long len = ((giaTriTam + buoc - 1) / buoc) * buoc;
            if (len >= giaTriTam) cacLuaChon.add(len);
        }
        for (long soTien : cacLuaChon) {
            NutDinhDang btnGiaTriSau =
                    new NutDinhDang(
                            soTien == giaTriTam ? "Đủ tiền" : DinhDang.dinhDang(soTien),
                            NutDinhDang.KieuNut.TRONG_SUOT);
            btnGiaTriSau.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            if (khungNhanhCacSoTien.getComponentCount() > 0) {
                khungNhanhCacSoTien.add(Box.createHorizontalStrut(8));
            }
            btnGiaTriSau.addActionListener(suKien -> oDaNhan.setText(String.valueOf(soTien)));
            khungNhanhCacSoTien.add(btnGiaTriSau);
        }
        khungNhanhCacSoTien.revalidate();
        khungNhanhCacSoTien.repaint();
    }

    private void capNhatQR() {
        boolean laTienMat = layPhuongThuc() == PhuongThuc.TIEN_MAT;
        boolean chuyenKhoan = layPhuongThuc() == PhuongThuc.CHUYEN_KHOAN;
        khungNhapTienMat.setVisible(laTienMat);
        khungQRThanhToan.setVisible(chuyenKhoan);
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
        if (chuyenKhoan) napMaVietQR();
        khungNoiDungVeLai();
    }

    private void napMaVietQR() {
        try {
            String bin = CaiDatService.macDinh().layTuyChon("BANK_BIN");
            String taiKhoan = CaiDatService.macDinh().layTuyChon("BANK_ACCOUNT");
            String tenChuTaiKhoan = CaiDatService.macDinh().layTuyChon("BANK_ACCOUNT_NAME");
            TaoMaVietQR.kiemTraTaiKhoan(bin, taiKhoan, tenChuTaiKhoan);
            String maHoaDon = banHang.getMaHoaDonDuKien();
            long soTienCanChuyen = banHang.tinhTong();
            String duLieuQR =
                    TaoMaVietQR.taoNoiDung(
                            bin, taiKhoan, tenChuTaiKhoan, soTienCanChuyen, maHoaDon);
            nhanQrLon.setText("");
            nhanQrLon.setIcon(new ImageIcon(TaoMaVietQR.taoAnh(duLieuQR, 220)));
            String tenCuaHang = CaiDatService.macDinh().lay("STORE_NAME");
            nhanThongTinQR.setText(
                    "<html><div style='width:250px'><b>"
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
                            + thoatHTML(DinhDang.dinhDang(soTienCanChuyen))
                            + "</b></font><br></div></html>");
            noiDungChuyenKhoanQR = maHoaDon;
            nhanNoiDungQR.setText(maHoaDon);
            nhanTrangThaiQR.setForeground(MauUngDung.CAM);
            nhanTrangThaiQR.setText(
                    "<html><div style='width:250px'>Đang chờ xác nhận. QR đã điền sẵn số tiền và"
                            + " mã hóa đơn; thu ngân cần kiểm tra giao dịch thực tế trước khi"
                            + " xác nhận.</div></html>");
            btnSaoChepNoiDungQR.setEnabled(true);
            qrHopLe = true;
        } catch (RuntimeException loi) {
            nhanQrLon.setIcon(null);
            nhanQrLon.setText("Không tạo được mã QR");
            nhanThongTinQR.setText(
                    "<html><div style='width:250px'>"
                            + thoatHTML(loi.getMessage())
                            + "<br>Kiểm tra cấu hình ngân hàng trong Cài đặt.</div></html>");
            nhanNoiDungQR.setText("");
            nhanTrangThaiQR.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            nhanTrangThaiQR.setText("Chưa thể tạo QR thanh toán.");
        }
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
            nhanTrangThaiQR.setForeground(MauUngDung.XANH_LA);
            nhanTrangThaiQR.setText("Đã sao chép nội dung chuyển khoản: " + noiDungChuyenKhoanQR);
        } catch (RuntimeException loi) {
            HopThoai.hienThiCanhBao(this, "Không thể sao chép nội dung: " + loi.getMessage());
        }
    }

    private static String thoatHTML(String giaTri) {
        if (giaTri == null) return "";
        return giaTri.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private void khungNoiDungVeLai() {
        khungQRThanhToan.revalidate();
        khungQRThanhToan.repaint();
        revalidate();
        repaint();
    }

    private void capNhatPhuongThuc() {
        boolean laTienMat = layPhuongThuc() == PhuongThuc.TIEN_MAT;
        oDaNhan.setEnabled(laTienMat);
        for (Component thanhPhan : khungNhanhCacSoTien.getComponents()) {
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
            boolean chuyenKhoan = phuongThuc == PhuongThuc.CHUYEN_KHOAN;
            chkDaNhanTien.setEnabled(!chuyenKhoan || qrHopLe);
            if (chuyenKhoan) {
                nhanTrangThaiQR.setForeground(
                        chkDaNhanTien.isSelected() ? MauUngDung.XANH_LA : MauUngDung.CAM);
                nhanTrangThaiQR.setText(
                        chkDaNhanTien.isSelected()
                                ? "Thu ngân đã xác nhận thủ công đã nhận đúng tiền cho hóa đơn "
                                        + banHang.getMaHoaDonDuKien()
                                : "Đang chờ thu ngân kiểm tra giao dịch thực tế.");
            }
            btnXacNhan.setEnabled(
                    !dangXuLy && chkDaNhanTien.isSelected() && (!chuyenKhoan || qrHopLe));
            return;
        }
        long tienKhachDua = DinhDang.doc(oDaNhan.getText());
        if (oDaNhan.getText().isBlank()) {
            nhanNhanThayDoi.setForeground(MauUngDung.VAN_BAN_XAM);
            nhanNhanThayDoi.setText("Nhập số tiền khách đưa");
            btnXacNhan.setEnabled(false);
        } else if (tienKhachDua < 0) {
            nhanNhanThayDoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            nhanNhanThayDoi.setText("Số tiền không hợp lệ");
            btnXacNhan.setEnabled(false);
        } else if (tienKhachDua < giaTriTam) {
            nhanNhanThayDoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            nhanNhanThayDoi.setText("Còn thiếu " + DinhDang.dinhDang(giaTriTam - tienKhachDua));
            btnXacNhan.setEnabled(false);
        } else {
            nhanNhanThayDoi.setForeground(MauUngDung.XANH_LA);
            nhanNhanThayDoi.setText(
                    "Tiền thừa trả khách: " + DinhDang.dinhDang(tienKhachDua - giaTriTam));
            btnXacNhan.setEnabled(!dangXuLy);
        }
    }

    private void xacNhanThanhToan() {
        if (dangXuLy || banHang.kiemTraTrong()) return;
        if (!banHang.kiemTraDaNhanTien(layPhuongThuc())) {
            HopThoai.hienThiCanhBao(
                    this, "Hãy kiểm tra và xác nhận đã nhận đủ tiền trước khi hoàn tất.");
            return;
        }
        long giaTriTam = banHang.tinhTong();
        long daNhan =
                layPhuongThuc() == PhuongThuc.TIEN_MAT
                        ? DinhDang.doc(oDaNhan.getText())
                        : giaTriTam;
        if (daNhan < giaTriTam) return;
        if (!caLamViec.kiemTraMo()) {
            if (HopThoai.xacNhanDongY(
                    this,
                    "Ca thu ngân hiện chưa mở. Bạn cần mở ca trước khi thanh toán và ghi"
                            + " nhận doanh thu.\n"
                            + "Bạn có muốn chuyển sang màn hình Ca thu ngân để mở ca ngay"
                            + " không?",
                    "Chưa mở ca làm việc")) {
                boDieuHuong.accept(MaManHinh.CA_THU_NGAN);
            }
            return;
        }
        String thongBao =
                "Xác nhận thu "
                        + DinhDang.dinhDang(giaTriTam)
                        + " cho hóa đơn "
                        + banHang.getMaHoaDonDuKien()
                        + "?";
        if (layPhuongThuc() == PhuongThuc.CHUYEN_KHOAN) {
            thongBao +=
                    "\n"
                        + "Chỉ tiếp tục sau khi đã kiểm tra giao dịch thực tế trên tài khoản ngân"
                        + " hàng.";
        }
        if (!HopThoai.xacNhanOk(this, thongBao, "Xác nhận thanh toán")) return;
        PhuongThuc phuongThuc = layPhuongThuc();
        dangXuLy = true;
        btnXacNhan.setEnabled(false);
        TacVuNen.chay(
                () -> {
                    GioHang.BienLai bienLai = banHang.hoanTat(phuongThuc, daNhan, thuNgan);
                    caLamViec.ghiNhanBanHang(phuongThuc, bienLai.getTong());
                    return bienLai;
                },
                bienLai -> {
                    banHang.xoa();
                    boDieuHuong.accept(MaManHinh.IN_HOA_DON);
                },
                ex -> {
                    dangXuLy = false;
                    kiemTraDauVao();
                    String loi = TacVuNen.thongDiepLoi(ex);
                    if (loi.contains("Cần mở ca")) {
                        if (HopThoai.xacNhanDongY(
                                ThanhToanPanel.this,
                                loi
                                        + "\n"
                                        + "Bạn có muốn chuyển sang màn hình Ca thu ngân để"
                                        + " mở ca ngay không?",
                                "Chưa mở ca làm việc")) {
                            boDieuHuong.accept(MaManHinh.CA_THU_NGAN);
                        }
                    } else {
                        HopThoai.hienThiCanhBao(ThanhToanPanel.this, "Chưa thanh toán: " + loi);
                    }
                });
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(false, false, false, false, false, true);
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

    private final Consumer<GioHang> boLangNgheGioHang;

    @Override
    public void addNotify() {
        super.addNotify();
        banHang.themBoLangNghe(boLangNgheGioHang);
    }

    @Override
    public void removeNotify() {
        banHang.xoaBoLangNghe(boLangNgheGioHang);
        super.removeNotify();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Thanh toán";
    }
}
