package com.petstation.gui.hethong;

import com.petstation.gui.chung.BieuTuong;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.ChinhFrame;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.KhoiTaoUngDung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.Year;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class DangNhapFrame extends JFrame {
    private static final char HIEN_THI_THAY_THE = '\u2022';

    private static final int CHIEU_RONG_BIEU_MAU = 340;

    private static final int KICH_THUOC_LOGO = 160;

    private final KhoiTaoUngDung nguCanh;

    private final JTextField oVanBanNguoiDung = new JTextField("admin");

    private final JPasswordField oVanBanMatKhau = new JPasswordField("PetStation@123");

    private final JCheckBox btnHienThiMatKhau = new JCheckBox("Hiện mật khẩu");

    private final JLabel nhanNhanLoi = new JLabel("");

    private final JLabel nhanNhanChuHoa = new JLabel("");

    private final NutDinhDang btnNutDangNhap =
            new NutDinhDang("Đăng nhập", NutDinhDang.KieuNut.CHINH);

    public DangNhapFrame(KhoiTaoUngDung nguCanh) {
        this.nguCanh = nguCanh;
        setTitle("Pet Station – Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(480, 700);
        setMinimumSize(new Dimension(480, 700));
        setLocationRelativeTo(null);
        setResizable(false);
        JPanel khungGoc = taoNenGradient();
        setContentPane(khungGoc);
        khungGoc.add(taoTrenThanh(), BorderLayout.NORTH);
        JPanel khungGiuaVungBao = new JPanel(new GridBagLayout());
        khungGiuaVungBao.setOpaque(false);
        khungGiuaVungBao.add(taoTheDangNhap(), new GridBagConstraints());
        khungGoc.add(khungGiuaVungBao, BorderLayout.CENTER);
        khungGoc.add(taoChanTrang(), BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnNutDangNhap);
        SwingUtilities.invokeLater(oVanBanNguoiDung::requestFocusInWindow);
    }

    private JPanel taoNenGradient() {
        JPanel khungGoc =
                new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics doHoa) {
                        super.paintComponent(doHoa);
                        Graphics2D doHoa2 = (Graphics2D) doHoa;
                        doHoa2.setPaint(
                                new GradientPaint(
                                        0,
                                        0,
                                        MauUngDung.NAU,
                                        0,
                                        getHeight(),
                                        MauUngDung.mau(0x2A0C02)));
                        doHoa2.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
        khungGoc.setOpaque(false);
        khungGoc.setBorder(BorderFactory.createLineBorder(MauUngDung.NAU_MO, 1));
        return khungGoc;
    }

    private JPanel taoTheDangNhap() {
        BoGocPanel khungThe = new BoGocPanel(16, MauUngDung.NEN_KEM, true);
        khungThe.setLayout(new BoxLayout(khungThe, BoxLayout.Y_AXIS));
        khungThe.setBorder(new EmptyBorder(28, 44, 28, 44));
        khungThe.setAlignmentX(Component.CENTER_ALIGNMENT);
        themPhanDauThe(khungThe);
        cauHinhTruongDangNhap();
        khungThe.add(layNhanTruong("Tên đăng nhập"));
        khungThe.add(Box.createVerticalStrut(8));
        khungThe.add(oVanBanNguoiDung);
        khungThe.add(Box.createVerticalStrut(20));
        khungThe.add(layNhanTruong("Mật khẩu"));
        khungThe.add(Box.createVerticalStrut(8));
        khungThe.add(oVanBanMatKhau);
        khungThe.add(Box.createVerticalStrut(8));
        khungThe.add(taoDongTuyChonMatKhau());
        khungThe.add(layTraiDong(nhanNhanChuHoa));
        khungThe.add(layTraiDong(nhanNhanLoi));
        khungThe.add(Box.createVerticalStrut(24));
        khungThe.add(btnNutDangNhap);
        return khungThe;
    }

    private void themPhanDauThe(JPanel khungThe) {
        JLabel nhanLogoBieuTuong = taoLogo();
        nhanLogoBieuTuong.setAlignmentX(Component.CENTER_ALIGNMENT);
        nhanLogoBieuTuong.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel nhanTieuDe = new JLabel("Pet Station", SwingConstants.CENTER);
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 30));
        nhanTieuDe.setForeground(MauUngDung.DO_THUONG_HIEU);
        nhanTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);
        nhanTieuDe.setBorder(new EmptyBorder(0, 0, 4, 0));
        JLabel nhanTieuDePhu =
                new JLabel("Hệ thống quản lý cửa hàng thú cưng", SwingConstants.CENTER);
        nhanTieuDePhu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhanTieuDePhu.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanTieuDePhu.setAlignmentX(Component.CENTER_ALIGNMENT);
        nhanTieuDePhu.setBorder(new EmptyBorder(0, 0, 24, 0));
        khungThe.add(nhanLogoBieuTuong);
        khungThe.add(nhanTieuDe);
        khungThe.add(nhanTieuDePhu);
    }

    private void cauHinhTruongDangNhap() {
        dinhDangTruong(oVanBanNguoiDung);
        dinhDangTruong(oVanBanMatKhau);
        oVanBanMatKhau.setEchoChar(HIEN_THI_THAY_THE);
        oVanBanNguoiDung.getAccessibleContext().setAccessibleName("Tên đăng nhập");
        oVanBanMatKhau.getAccessibleContext().setAccessibleName("Mật khẩu");
        nhanNhanChuHoa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        nhanNhanChuHoa.setForeground(MauUngDung.CAM);
        oVanBanMatKhau.addKeyListener(
                new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent suKien) {
                        capNhatChuHoaKhoa();
                    }

                    @Override
                    public void keyReleased(KeyEvent suKien) {
                        capNhatChuHoaKhoa();
                    }
                });
        nhanNhanLoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
        nhanNhanLoi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanNhanLoi.setBorder(new EmptyBorder(4, 0, 0, 0));
        nhanNhanLoi.getAccessibleContext().setAccessibleName("Thông báo lỗi đăng nhập");
        btnNutDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNutDangNhap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnNutDangNhap.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        btnNutDangNhap.addActionListener(suKien -> layThucHienDangNhap());
        oVanBanMatKhau.addActionListener(suKien -> layThucHienDangNhap());
        oVanBanNguoiDung.addActionListener(suKien -> oVanBanMatKhau.requestFocusInWindow());
    }

    private JPanel taoDongTuyChonMatKhau() {
        btnHienThiMatKhau.setOpaque(false);
        btnHienThiMatKhau.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnHienThiMatKhau.setForeground(MauUngDung.VAN_BAN_TOI);
        btnHienThiMatKhau.addActionListener(
                suKien ->
                        oVanBanMatKhau.setEchoChar(
                                btnHienThiMatKhau.isSelected() ? (char) 0 : HIEN_THI_THAY_THE));
        JButton btnQuen = new JButton("Quên mật khẩu?");
        btnQuen.setBorderPainted(false);
        btnQuen.setContentAreaFilled(false);
        btnQuen.setFocusPainted(false);
        btnQuen.setForeground(MauUngDung.NAU);
        btnQuen.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnQuen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnQuen.setHorizontalAlignment(SwingConstants.RIGHT);
        btnQuen.setBorder(new EmptyBorder(0, 0, 0, 0));
        btnQuen.setMargin(new Insets(0, 0, 0, 0));
        btnQuen.addActionListener(
                suKien ->
                        HopThoai.hienThiThongTin(
                                this,
                                "Tài khoản do Cửa hàng trưởng quản lý.\n"
                                    + "Hãy liên hệ Cửa hàng trưởng để được cấp lại mật khẩu tạm.",
                                "Quên mật khẩu"));
        JPanel khungTam = new JPanel(new BorderLayout());
        khungTam.setOpaque(false);
        khungTam.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        khungTam.setAlignmentX(Component.CENTER_ALIGNMENT);
        khungTam.add(btnHienThiMatKhau, BorderLayout.WEST);
        khungTam.add(btnQuen, BorderLayout.EAST);
        return khungTam;
    }

    private static JLabel taoChanTrang() {
        JLabel nhanChanTrang =
                new JLabel("© " + Year.now() + " Pet Station – v1.0.0", SwingConstants.CENTER);
        nhanChanTrang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        nhanChanTrang.setForeground(MauUngDung.voiDoTrong(Color.WHITE, 150));
        nhanChanTrang.setBorder(new EmptyBorder(10, 0, 14, 0));
        return nhanChanTrang;
    }

    private JPanel taoTrenThanh() {
        JPanel khungThanh = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        khungThanh.setOpaque(false);
        khungThanh.setPreferredSize(new Dimension(0, 36));
        JButton btnDong = new JButton("×");
        btnDong.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 20));
        btnDong.setForeground(Color.WHITE);
        btnDong.setBorderPainted(false);
        btnDong.setContentAreaFilled(false);
        btnDong.setFocusPainted(false);
        btnDong.setPreferredSize(new Dimension(44, 36));
        btnDong.setToolTipText("Thoát");
        btnDong.getAccessibleContext().setAccessibleName("Thoát ứng dụng");
        btnDong.addActionListener(suKien -> System.exit(0));
        btnDong.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent suKien) {
                        btnDong.setForeground(MauUngDung.VANG);
                    }

                    @Override
                    public void mouseExited(MouseEvent suKien) {
                        btnDong.setForeground(Color.WHITE);
                    }
                });
        khungThanh.add(btnDong);
        MouseAdapter keo =
                new MouseAdapter() {
                    private Point goc;

                    @Override
                    public void mousePressed(MouseEvent suKien) {
                        goc = suKien.getPoint();
                    }

                    @Override
                    public void mouseDragged(MouseEvent suKien) {
                        if (goc == null) return;
                        Point vungDangXet = suKien.getLocationOnScreen();
                        setLocation(vungDangXet.x - goc.x, vungDangXet.y - goc.y);
                    }
                };
        khungThanh.addMouseListener(keo);
        khungThanh.addMouseMotionListener(keo);
        return khungThanh;
    }

    private void capNhatChuHoaKhoa() {
        boolean chuHoa;
        try {
            chuHoa = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
        } catch (UnsupportedOperationException ngoaiLe) {
            chuHoa = false;
        }
        nhanNhanChuHoa.setIcon(
                chuHoa ? new BieuTuong.GiaoDien(BieuTuong.GiaoDien.Kieu.CANH_BAO) : null);
        nhanNhanChuHoa.setText(chuHoa ? "Caps Lock đang bật" : "");
    }

    private void layThucHienDangNhap() {
        String tenDangNhap = oVanBanNguoiDung.getText().trim();
        char[] matKhau = oVanBanMatKhau.getPassword();
        if (tenDangNhap.isEmpty()) {
            hienThiThongBao("Vui lòng nhập tên đăng nhập.", MauUngDung.VAN_BAN_NGUY_HIEM);
            oVanBanNguoiDung.requestFocusInWindow();
            return;
        }
        if (matKhau.length == 0) {
            hienThiThongBao("Vui lòng nhập mật khẩu.", MauUngDung.VAN_BAN_NGUY_HIEM);
            oVanBanMatKhau.requestFocusInWindow();
            return;
        }
        datBieuMauDuocBat(false);
        hienThiThongBao("Đang xác thực...", MauUngDung.VAN_BAN_XAM);
        TacVuNen.chay(
                () -> {
                    try {
                        return nguCanh.layXacThucDichVu().dangNhap(tenDangNhap, matKhau);
                    } finally {
                        Arrays.fill(matKhau, '\0');
                    }
                },
                phienDangNhap -> {
                    PhienDangNhapService.macDinh().datPhienDangNhap(phienDangNhap);
                    dispose();
                    new ChinhFrame(nguCanh).setVisible(true);
                },
                ngoaiLe -> {
                    ngoaiLe.printStackTrace();
                    String thongBao =
                            ngoaiLe instanceof SecurityException
                                    ? ngoaiLe.getMessage()
                                    : ("Lỗi đăng nhập: "
                                            + (ngoaiLe.getMessage() != null
                                                    ? ngoaiLe.getMessage()
                                                    : "Không thể đăng nhập. Vui lòng thử lại."));
                    hienThiThongBao(thongBao, MauUngDung.VAN_BAN_NGUY_HIEM);
                    oVanBanMatKhau.setText("");
                    oVanBanMatKhau.requestFocusInWindow();
                    datBieuMauDuocBat(true);
                });
    }

    private void datBieuMauDuocBat(boolean duocBat) {
        oVanBanNguoiDung.setEnabled(duocBat);
        oVanBanMatKhau.setEnabled(duocBat);
        btnHienThiMatKhau.setEnabled(duocBat);
        btnNutDangNhap.setEnabled(duocBat);
    }

    private JLabel taoLogo() {
        URL taiNguyen = DangNhapFrame.class.getResource("/images/pet-station-logo.png");
        if (taiNguyen == null) {
            JLabel nhanThayThe = new JLabel("🐾", SwingConstants.CENTER);
            nhanThayThe.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 96));
            nhanThayThe.setPreferredSize(new Dimension(KICH_THUOC_LOGO, KICH_THUOC_LOGO));
            nhanThayThe.setAlignmentX(Component.CENTER_ALIGNMENT);
            return nhanThayThe;
        }
        ImageIcon bieuTuongGoc = new ImageIcon(taiNguyen);
        Image anhGoc = bieuTuongGoc.getImage();
        double tiLe =
                (double) KICH_THUOC_LOGO
                        / Math.max(bieuTuongGoc.getIconWidth(), bieuTuongGoc.getIconHeight());
        int chieuRong = Math.max(1, (int) Math.round(bieuTuongGoc.getIconWidth() * tiLe));
        int chieuCao = Math.max(1, (int) Math.round(bieuTuongGoc.getIconHeight() * tiLe));
        Icon bieuTuong =
                new Icon() {
                    @Override
                    public void paintIcon(Component thanhPhan, Graphics doHoa, int x, int y) {
                        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
                        try {
                            doHoa2.setRenderingHint(
                                    RenderingHints.KEY_INTERPOLATION,
                                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                            doHoa2.setRenderingHint(
                                    RenderingHints.KEY_RENDERING,
                                    RenderingHints.VALUE_RENDER_QUALITY);
                            doHoa2.drawImage(
                                    anhGoc,
                                    x + (KICH_THUOC_LOGO - chieuRong) / 2,
                                    y + (KICH_THUOC_LOGO - chieuCao) / 2,
                                    chieuRong,
                                    chieuCao,
                                    thanhPhan);
                        } finally {
                            doHoa2.dispose();
                        }
                    }

                    @Override
                    public int getIconWidth() {
                        return KICH_THUOC_LOGO;
                    }

                    @Override
                    public int getIconHeight() {
                        return KICH_THUOC_LOGO;
                    }
                };
        JLabel nhanHienThi = new JLabel(bieuTuong, SwingConstants.CENTER);
        nhanHienThi.getAccessibleContext().setAccessibleName("Logo Pet Station");
        nhanHienThi.setAlignmentX(Component.CENTER_ALIGNMENT);
        return nhanHienThi;
    }

    private void dinhDangTruong(JTextField oTruongDangXet) {
        oTruongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 14));
        oTruongDangXet.setForeground(MauUngDung.VAN_BAN_TOI);
        oTruongDangXet.setBackground(Color.WHITE);
        oTruongDangXet.setCaretColor(MauUngDung.DO_THUONG_HIEU);
        oTruongDangXet.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN, 1, true),
                        new EmptyBorder(10, 14, 10, 14)));
        oTruongDangXet.setPreferredSize(new Dimension(CHIEU_RONG_BIEU_MAU, 44));
        oTruongDangXet.setMinimumSize(new Dimension(CHIEU_RONG_BIEU_MAU, 44));
        oTruongDangXet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        oTruongDangXet.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JComponent layNhanTruong(String vanBan) {
        JLabel nhanDongDangXet = new JLabel(vanBan);
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanDongDangXet.setForeground(MauUngDung.VAN_BAN_TOI);
        JComponent dong = layTraiDong(nhanDongDangXet);
        dong.setMaximumSize(new Dimension(Integer.MAX_VALUE, dong.getPreferredSize().height));
        return dong;
    }

    private JComponent layTraiDong(JComponent giaTriDangXet) {
        JPanel khungDong = new JPanel(new BorderLayout());
        khungDong.setOpaque(false);
        khungDong.add(giaTriDangXet, BorderLayout.WEST);
        khungDong.setAlignmentX(Component.CENTER_ALIGNMENT);
        khungDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
        return khungDong;
    }

    private void hienThiThongBao(String vanBan, Color mau) {
        nhanNhanLoi.setForeground(mau);
        if (vanBan == null || vanBan.isEmpty()) {
            nhanNhanLoi.setText("");
            return;
        }
        String anToan = vanBan.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        nhanNhanLoi.setText(
                "<html><div style='width:"
                        + CHIEU_RONG_BIEU_MAU
                        + "px'>"
                        + anToan
                        + "</div></html>");
    }
}
