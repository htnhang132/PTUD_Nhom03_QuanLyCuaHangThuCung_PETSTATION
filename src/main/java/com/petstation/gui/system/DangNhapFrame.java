package com.petstation.gui.system;

import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.main.ChinhFrame;
import com.petstation.model.PhienDangNhap;
import com.petstation.service.XacThucException;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

public class DangNhapFrame extends JFrame {
    private static final char HIEN_THI_THAY_THE = '\u2022';

    private static final int CHIEU_RONG_BIEU_MAU = 340;

    private static final int KICH_THUOC_LOGO = 160;

    private final KhoiTaoUngDung nguCanh;

    private final JTextField txtVanBanNguoiDung = new JTextField("admin");

    private final JPasswordField txtVanBanMatKhau = new JPasswordField("PetStation@123");

    private final JCheckBox btnHienThiMatKhau = new JCheckBox("Hiện mật khẩu");

    private final JLabel lblNhanLoi = new JLabel("");

    private final JLabel lblNhanChuHoa = new JLabel("");

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

        JPanel pnlGoc =
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
                                        new Color(0x2A, 0x0C, 0x02)));
                        doHoa2.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
        pnlGoc.setOpaque(false);
        pnlGoc.setBorder(BorderFactory.createLineBorder(new Color(0x8C, 0x7A, 0x6E), 1));
        setContentPane(pnlGoc);

        pnlGoc.add(taoTrenThanh(), BorderLayout.NORTH);

        JPanel pnlGiuaVungBao = new JPanel(new GridBagLayout());
        pnlGiuaVungBao.setOpaque(false);

        JPanel pnlThe = new JPanel();
        pnlThe.setBackground(MauUngDung.NEN_KEM);
        pnlThe.setLayout(new BoxLayout(pnlThe, BoxLayout.Y_AXIS));
        pnlThe.setBorder(new EmptyBorder(28, 48, 28, 48));
        pnlThe.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLogoBieuTuong = taoLogo();
        lblLogoBieuTuong.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogoBieuTuong.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel lblTieuDe = new JLabel("Pet Station", SwingConstants.CENTER);
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 30));
        lblTieuDe.setForeground(MauUngDung.DO_THUONG_HIEU);
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTieuDe.setBorder(new EmptyBorder(0, 0, 4, 0));

        JLabel lblTieuDePhu =
                new JLabel("Hệ thống quản lý cửa hàng thú cưng", SwingConstants.CENTER);
        lblTieuDePhu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        lblTieuDePhu.setForeground(MauUngDung.VAN_BAN_XAM);
        lblTieuDePhu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTieuDePhu.setBorder(new EmptyBorder(0, 0, 24, 0));

        dinhDangTruong(txtVanBanNguoiDung);
        dinhDangTruong(txtVanBanMatKhau);
        txtVanBanMatKhau.setEchoChar(HIEN_THI_THAY_THE);
        txtVanBanNguoiDung.getAccessibleContext().setAccessibleName("Tên đăng nhập");
        txtVanBanMatKhau.getAccessibleContext().setAccessibleName("Mật khẩu");

        btnHienThiMatKhau.setOpaque(false);
        btnHienThiMatKhau.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnHienThiMatKhau.setForeground(MauUngDung.VAN_BAN_TOI);
        btnHienThiMatKhau.addActionListener(
                suKien ->
                        txtVanBanMatKhau.setEchoChar(
                                btnHienThiMatKhau.isSelected() ? (char) 0 : HIEN_THI_THAY_THE));

        lblNhanChuHoa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblNhanChuHoa.setForeground(MauUngDung.CAM);
        txtVanBanMatKhau.addKeyListener(
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

        lblNhanLoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
        lblNhanLoi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblNhanLoi.setBorder(new EmptyBorder(4, 0, 0, 0));
        lblNhanLoi.getAccessibleContext().setAccessibleName("Thông báo lỗi đăng nhập");

        btnNutDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNutDangNhap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnNutDangNhap.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        btnNutDangNhap.addActionListener(suKien -> layThucHienDangNhap());
        txtVanBanMatKhau.addActionListener(suKien -> layThucHienDangNhap());
        txtVanBanNguoiDung.addActionListener(suKien -> txtVanBanMatKhau.requestFocusInWindow());

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
                        HopThoai.hienThiThongBaoHopThoai(
                                this,
                                "Tài khoản do Cửa hàng trưởng quản lý.\n"
                                    + "Hãy liên hệ Cửa hàng trưởng để được cấp lại mật khẩu tạm.",
                                "Quên mật khẩu",
                                JOptionPane.INFORMATION_MESSAGE));

        pnlThe.add(lblLogoBieuTuong);
        pnlThe.add(lblTieuDe);
        pnlThe.add(lblTieuDePhu);

        pnlThe.add(layNhanTruong("Tên đăng nhập"));
        pnlThe.add(Box.createVerticalStrut(8));
        pnlThe.add(txtVanBanNguoiDung);
        pnlThe.add(Box.createVerticalStrut(20));

        pnlThe.add(layNhanTruong("Mật khẩu"));
        pnlThe.add(Box.createVerticalStrut(8));
        pnlThe.add(txtVanBanMatKhau);
        pnlThe.add(Box.createVerticalStrut(8));

        JPanel pnlCacLuaChonDong = new JPanel(new BorderLayout());
        pnlCacLuaChonDong.setOpaque(false);
        pnlCacLuaChonDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        pnlCacLuaChonDong.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlCacLuaChonDong.add(btnHienThiMatKhau, BorderLayout.WEST);
        pnlCacLuaChonDong.add(btnQuen, BorderLayout.EAST);
        pnlThe.add(pnlCacLuaChonDong);
        pnlThe.add(layTraiDong(lblNhanChuHoa));
        pnlThe.add(layTraiDong(lblNhanLoi));
        pnlThe.add(Box.createVerticalStrut(24));
        pnlThe.add(btnNutDangNhap);

        GridBagConstraints rangBuocBoCuc = new GridBagConstraints();
        rangBuocBoCuc.gridx = 0;
        rangBuocBoCuc.gridy = 0;
        rangBuocBoCuc.anchor = GridBagConstraints.CENTER;
        pnlGiuaVungBao.add(pnlThe, rangBuocBoCuc);

        pnlGoc.add(pnlGiuaVungBao, BorderLayout.CENTER);

        JLabel lblChanTrang =
                new JLabel("© " + Year.now() + " Pet Station – v1.0.0", SwingConstants.CENTER);
        lblChanTrang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblChanTrang.setForeground(new Color(255, 255, 255, 150));
        lblChanTrang.setBorder(new EmptyBorder(10, 0, 14, 0));
        pnlGoc.add(lblChanTrang, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnNutDangNhap);
        SwingUtilities.invokeLater(txtVanBanNguoiDung::requestFocusInWindow);
    }

    private JPanel taoTrenThanh() {
        JPanel pnlThanh = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlThanh.setOpaque(false);
        pnlThanh.setPreferredSize(new Dimension(0, 36));

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

        pnlThanh.add(btnDong);

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
        pnlThanh.addMouseListener(keo);
        pnlThanh.addMouseMotionListener(keo);
        return pnlThanh;
    }

    private void capNhatChuHoaKhoa() {
        boolean chuHoa;
        try {
            chuHoa = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
        } catch (UnsupportedOperationException ngoaiLe) {
            chuHoa = false;
        }
        lblNhanChuHoa.setIcon(
                chuHoa
                        ? new com.petstation.gui.components.BieuTuongGiaoDien(
                                com.petstation.gui.components.BieuTuongGiaoDien.Kieu.CANH_BAO)
                        : null);
        lblNhanChuHoa.setText(chuHoa ? "Caps Lock đang bật" : "");
    }

    private void layThucHienDangNhap() {
        String tenDangNhap = txtVanBanNguoiDung.getText().trim();

        char[] matKhau = txtVanBanMatKhau.getPassword();
        if (tenDangNhap.isEmpty()) {
            hienThiThongBao("Vui lòng nhập tên đăng nhập.", MauUngDung.VAN_BAN_NGUY_HIEM);
            txtVanBanNguoiDung.requestFocusInWindow();
            return;
        }
        if (matKhau.length == 0) {
            hienThiThongBao("Vui lòng nhập mật khẩu.", MauUngDung.VAN_BAN_NGUY_HIEM);
            txtVanBanMatKhau.requestFocusInWindow();
            return;
        }

        datBieuMauDuocBat(false);
        hienThiThongBao("Đang xác thực...", MauUngDung.VAN_BAN_XAM);
        new SwingWorker<PhienDangNhap, Void>() {
            @Override
            protected PhienDangNhap doInBackground() {
                try {
                    return nguCanh.layXacThucDichVu().dangNhap(tenDangNhap, matKhau);
                } finally {
                    Arrays.fill(matKhau, '\0');
                }
            }

            @Override
            protected void done() {
                try {
                    PhienDangNhap phienDangNhap = get();
                    nguCanh.layNguCanhPhien().batDau(phienDangNhap);
                    dispose();
                    new ChinhFrame(nguCanh).setVisible(true);
                } catch (Exception ngoaiLe) {
                    Throwable nguyenNhan =
                            ngoaiLe.getCause() == null ? ngoaiLe : ngoaiLe.getCause();
                    nguyenNhan.printStackTrace();

                    String thongBao =
                            nguyenNhan instanceof XacThucException
                                    ? nguyenNhan.getMessage()
                                    : ("Lỗi đăng nhập: " + (nguyenNhan.getMessage() != null ? nguyenNhan.getMessage() : "Không thể đăng nhập. Vui lòng thử lại."));
                    hienThiThongBao(thongBao, MauUngDung.VAN_BAN_NGUY_HIEM);
                    txtVanBanMatKhau.setText("");
                    txtVanBanMatKhau.requestFocusInWindow();
                    datBieuMauDuocBat(true);
                }
            }
        }.execute();
    }

    private void datBieuMauDuocBat(boolean duocBat) {
        txtVanBanNguoiDung.setEnabled(duocBat);
        txtVanBanMatKhau.setEnabled(duocBat);
        btnHienThiMatKhau.setEnabled(duocBat);
        btnNutDangNhap.setEnabled(duocBat);
    }

    private JLabel taoLogo() {
        URL taiNguyen = DangNhapFrame.class.getResource("/images/pet-station-logo.png");
        if (taiNguyen == null) {
            JLabel lblThayThe = new JLabel("🐾", SwingConstants.CENTER);
            lblThayThe.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 96));
            lblThayThe.setPreferredSize(new Dimension(KICH_THUOC_LOGO, KICH_THUOC_LOGO));
            lblThayThe.setAlignmentX(Component.CENTER_ALIGNMENT);
            return lblThayThe;
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
        JLabel lblNhan = new JLabel(bieuTuong, SwingConstants.CENTER);
        lblNhan.getAccessibleContext().setAccessibleName("Logo Pet Station");
        lblNhan.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lblNhan;
    }

    private void dinhDangTruong(JTextField txtTruongDangXet) {
        txtTruongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 14));
        txtTruongDangXet.setForeground(MauUngDung.VAN_BAN_TOI);
        txtTruongDangXet.setBackground(Color.WHITE);
        txtTruongDangXet.setCaretColor(MauUngDung.DO_THUONG_HIEU);
        txtTruongDangXet.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN, 1, true),
                        new EmptyBorder(10, 14, 10, 14)));
        txtTruongDangXet.setPreferredSize(new Dimension(CHIEU_RONG_BIEU_MAU, 44));
        txtTruongDangXet.setMinimumSize(new Dimension(CHIEU_RONG_BIEU_MAU, 44));
        txtTruongDangXet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        txtTruongDangXet.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JComponent layNhanTruong(String vanBan) {
        JLabel lblDongDangXet = new JLabel(vanBan);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        lblDongDangXet.setForeground(MauUngDung.VAN_BAN_TOI);
        JComponent dong = layTraiDong(lblDongDangXet);
        dong.setMaximumSize(new Dimension(Integer.MAX_VALUE, dong.getPreferredSize().height));
        return dong;
    }

    private JComponent layTraiDong(JComponent giaTriDangXet) {
        JPanel pnlDong = new JPanel(new BorderLayout());
        pnlDong.setOpaque(false);
        pnlDong.add(giaTriDangXet, BorderLayout.WEST);
        pnlDong.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
        return pnlDong;
    }

    private void hienThiThongBao(String vanBan, Color mau) {
        lblNhanLoi.setForeground(mau);
        if (vanBan == null || vanBan.isEmpty()) {
            lblNhanLoi.setText("");
            return;
        }

        String anToan = vanBan.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        lblNhanLoi.setText(
                "<html><div style='width:"
                        + CHIEU_RONG_BIEU_MAU
                        + "px'>"
                        + anToan
                        + "</div></html>");
    }
}
