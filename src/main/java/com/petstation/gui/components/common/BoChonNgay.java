package com.petstation.gui.components.common;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import javax.swing.*;

public class BoChonNgay extends JPanel {
    private JTextField txtTruongVanBan;

    private JButton btnNutLich;

    private JDialog lichHopThoai;

    private SimpleDateFormat ngayDinhDang = new SimpleDateFormat("dd/MM/yyyy");

    private LocalDate ngayDuocChon;

    private JLabel lblNhanThangNam;

    private JPanel pnlSoNgayVungHienThi;

    private int hienTaiThang;

    private int hienTaiNam;

    public BoChonNgay() {
        this(LocalDate.now());
    }

    public BoChonNgay(LocalDate ngayBanDau) {
        setLayout(new BorderLayout(5, 0));
        this.ngayDuocChon = ngayBanDau;
        this.hienTaiThang = (ngayBanDau == null ? LocalDate.now() : ngayBanDau).getMonthValue();
        this.hienTaiNam = (ngayBanDau == null ? LocalDate.now() : ngayBanDau).getYear();

        txtTruongVanBan = new JTextField(10);
        txtTruongVanBan.setEditable(false);
        txtTruongVanBan.setBackground(Color.WHITE);
        if (ngayDuocChon != null) {
            txtTruongVanBan.setText(dinhDangNgay(ngayDuocChon));
        }

        btnNutLich =
                new JButton(
                        new com.petstation.gui.components.BieuTuongNet(
                                com.petstation.gui.components.BieuTuongNet.Kieu.LICH,
                                18,
                                com.petstation.gui.components.MauUngDung.NAU));
        btnNutLich.setToolTipText("Chọn ngày");
        btnNutLich.getAccessibleContext().setAccessibleName("Chọn ngày");
        btnNutLich.setPreferredSize(new Dimension(35, 25));
        btnNutLich.setFocusPainted(false);
        btnNutLich.addActionListener(suKien -> hienThiLich());

        add(txtTruongVanBan, BorderLayout.CENTER);
        add(btnNutLich, BorderLayout.EAST);
    }

    private void hienThiLich() {
        if (lichHopThoai == null) {
            taoLichHopThoai();
        }

        if (ngayDuocChon != null) {
            hienTaiThang = ngayDuocChon.getMonthValue();
            hienTaiNam = ngayDuocChon.getYear();
        }

        capNhatLich();

        Point viTri = txtTruongVanBan.getLocationOnScreen();
        lichHopThoai.pack();
        GraphicsConfiguration cauHinhDoHoa = txtTruongVanBan.getGraphicsConfiguration();
        if (cauHinhDoHoa == null) {
            cauHinhDoHoa = getGraphicsConfiguration();
        }
        Rectangle manHinh = cauHinhDoHoa.getBounds();
        Insets le = Toolkit.getDefaultToolkit().getScreenInsets(cauHinhDoHoa);
        manHinh =
                new Rectangle(
                        manHinh.x + le.left,
                        manHinh.y + le.top,
                        manHinh.width - le.left - le.right,
                        manHinh.height - le.top - le.bottom);
        int doc = viTri.y + txtTruongVanBan.getHeight();
        if (doc + lichHopThoai.getHeight() > manHinh.y + manHinh.height)
            doc = viTri.y - lichHopThoai.getHeight();
        lichHopThoai.setLocation(
                Math.max(
                        manHinh.x,
                        Math.min(viTri.x, manHinh.x + manHinh.width - lichHopThoai.getWidth())),
                Math.max(manHinh.y, doc));
        lichHopThoai.setVisible(true);
    }

    private void taoLichHopThoai() {
        Window cuaSo =
                SwingUtilities.getWindowAncestor(txtTruongVanBan != null ? txtTruongVanBan : this);
        if (cuaSo instanceof Frame) {
            lichHopThoai = new JDialog((Frame) cuaSo, "Chọn ngày", true);
        } else
        if (cuaSo instanceof Dialog) {
            lichHopThoai = new JDialog((Dialog) cuaSo, "Chọn ngày", true);
        } else {
            lichHopThoai = new JDialog((Frame) null, "Chọn ngày", true);
        }

        lichHopThoai.setLayout(new BorderLayout());
        lichHopThoai.setUndecorated(false);

        JPanel pnlTieuDeVungHienThi = new JPanel(new BorderLayout());
        pnlTieuDeVungHienThi.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnNutTruoc =
                new JButton(
                        new com.petstation.gui.components.BieuTuongGiaoDien(
                                com.petstation.gui.components.BieuTuongGiaoDien.Kieu.TRUOC));
        btnNutTruoc.setToolTipText("Tháng trước");
        btnNutTruoc.getAccessibleContext().setAccessibleName("Tháng trước");
        btnNutTruoc.addActionListener(
                suKien -> {
                    hienTaiThang--;
                    if (hienTaiThang < 1) {
                        hienTaiThang = 12;
                        hienTaiNam--;
                    }
                    capNhatLich();
                });

        JButton btnNutTiepTheo =
                new JButton(
                        new com.petstation.gui.components.BieuTuongGiaoDien(
                                com.petstation.gui.components.BieuTuongGiaoDien.Kieu.SAU));
        btnNutTiepTheo.setToolTipText("Tháng sau");
        btnNutTiepTheo.getAccessibleContext().setAccessibleName("Tháng sau");
        btnNutTiepTheo.addActionListener(
                suKien -> {
                    hienTaiThang++;
                    if (hienTaiThang > 12) {
                        hienTaiThang = 1;
                        hienTaiNam++;
                    }
                    capNhatLich();
                });

        lblNhanThangNam = new JLabel("", SwingConstants.CENTER);
        lblNhanThangNam.setFont(lblNhanThangNam.getFont().deriveFont(Font.BOLD, 14f));

        pnlTieuDeVungHienThi.add(btnNutTruoc, BorderLayout.WEST);
        pnlTieuDeVungHienThi.add(lblNhanThangNam, BorderLayout.CENTER);
        pnlTieuDeVungHienThi.add(btnNutTiepTheo, BorderLayout.EAST);

        pnlSoNgayVungHienThi = new JPanel(new GridLayout(7, 7, 4, 4));
        pnlSoNgayVungHienThi.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] ngayCacTen = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        for (String tenNgay : ngayCacTen) {
            JLabel lblNhan = new JLabel(tenNgay, SwingConstants.CENTER);
            lblNhan.setFont(lblNhan.getFont().deriveFont(Font.BOLD));
            lblNhan.setForeground(com.petstation.gui.components.MauUngDung.NAU);
            pnlSoNgayVungHienThi.add(lblNhan);
        }

        JPanel pnlChanTrangVungHienThi = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnNutHomNay = new JButton("Hôm nay");
        btnNutHomNay.addActionListener(
                suKien -> {
                    LocalDate homNay = LocalDate.now();
                    chonNgay(homNay);
                });

        JButton btnNutXoa = new JButton("Xóa");
        btnNutXoa.addActionListener(
                suKien -> {
                    datNgayDuocChon(null);
                    lichHopThoai.setVisible(false);
                });

        pnlChanTrangVungHienThi.add(btnNutHomNay);
        pnlChanTrangVungHienThi.add(btnNutXoa);

        lichHopThoai.add(pnlTieuDeVungHienThi, BorderLayout.NORTH);
        lichHopThoai.add(pnlSoNgayVungHienThi, BorderLayout.CENTER);
        lichHopThoai.add(pnlChanTrangVungHienThi, BorderLayout.SOUTH);

        lichHopThoai.pack();
        lichHopThoai.setResizable(false);
        lichHopThoai
                .getRootPane()
                .registerKeyboardAction(
                        e -> lichHopThoai.setVisible(false),
                        KeyStroke.getKeyStroke("ESCAPE"),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void capNhatLich() {
        lblNhanThangNam.setText(String.format("Tháng %d/%d", hienTaiThang, hienTaiNam));

        Component[] cacThanhPhan = pnlSoNgayVungHienThi.getComponents();
        for (int chiSo = cacThanhPhan.length - 1; chiSo >= 7; chiSo--) {
            pnlSoNgayVungHienThi.remove(chiSo);
        }

        LocalDate dauTienNgay = LocalDate.of(hienTaiNam, hienTaiThang, 1);

        int dauTienNgayCuaTuan = dauTienNgay.getDayOfWeek().getValue() % 7;

        for (int chiSo = 0; chiSo < dauTienNgayCuaTuan; chiSo++) {
            pnlSoNgayVungHienThi.add(new JLabel(""));
        }

        int soNgayVaoThang = dauTienNgay.lengthOfMonth();
        for (int ngay = 1; ngay <= soNgayVaoThang; ngay++) {
            final LocalDate ngay2 = LocalDate.of(hienTaiNam, hienTaiThang, ngay);

            JButton btnNutNgay = new JButton(String.valueOf(ngay));
            btnNutNgay.setFocusPainted(true);
            btnNutNgay.setMargin(new Insets(2, 2, 2, 2));
            int canh =
                    Math.max(
                            com.formdev.flatlaf.util.UIScale.scale(34),
                            btnNutNgay.getFontMetrics(btnNutNgay.getFont()).stringWidth("31")
                                    + com.formdev.flatlaf.util.UIScale.scale(16));
            btnNutNgay.setPreferredSize(new Dimension(canh, canh));
            btnNutNgay.setName("ngay:" + ngay);

            if (ngayDuocChon != null && ngay2.equals(ngayDuocChon)) {
                btnNutNgay.setBackground(com.petstation.gui.components.MauUngDung.DO_THUONG_HIEU);
                btnNutNgay.setForeground(Color.WHITE);
                btnNutNgay.setOpaque(true);
            }

            if (ngay2.equals(LocalDate.now())) {
                btnNutNgay.setBorder(
                        BorderFactory.createLineBorder(
                                com.petstation.gui.components.MauUngDung.DO_THUONG_HIEU, 2));
            }

            btnNutNgay.addActionListener(suKien -> chonNgay(ngay2));
            pnlSoNgayVungHienThi.add(btnNutNgay);
        }

        while (pnlSoNgayVungHienThi.getComponentCount() < 49)
            pnlSoNgayVungHienThi.add(new JLabel(""));
        pnlSoNgayVungHienThi.revalidate();
        pnlSoNgayVungHienThi.repaint();
    }

    private void chonNgay(LocalDate ngay) {
        datNgayDuocChon(ngay);
        lichHopThoai.setVisible(false);
    }

    private String dinhDangNgay(LocalDate ngay) {
        return String.format(
                "%02d/%02d/%d", ngay.getDayOfMonth(), ngay.getMonthValue(), ngay.getYear());
    }

    public LocalDate layNgayDuocChon() {
        return ngayDuocChon;
    }

    public void datNgayDuocChon(LocalDate ngay) {
        LocalDate cu = this.ngayDuocChon;
        this.ngayDuocChon = ngay;
        if (ngay != null) {
            txtTruongVanBan.setText(dinhDangNgay(ngay));
            hienTaiThang = ngay.getMonthValue();
            hienTaiNam = ngay.getYear();
        } else {
            txtTruongVanBan.setText("");
        }
        firePropertyChange("date", cu, ngay);
    }

    public Date layNgay() {
        if (ngayDuocChon == null) return null;
        return Date.from(ngayDuocChon.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void datNgay(Date ngay) {
        if (ngay == null) {
            datNgayDuocChon(null);
        } else {
            datNgayDuocChon(ngay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

    public java.sql.Date layNgaySQL() {
        if (ngayDuocChon == null) return null;
        return java.sql.Date.valueOf(ngayDuocChon);
    }

    public void datNgaySQL(java.sql.Date ngay) {
        if (ngay == null) {
            datNgayDuocChon(null);
        } else {
            datNgayDuocChon(ngay.toLocalDate());
        }
    }

    public void xoa() {
        datNgayDuocChon(null);
    }

    @Override
    public void setEnabled(boolean duocBat) {
        super.setEnabled(duocBat);
        txtTruongVanBan.setEnabled(duocBat);
        btnNutLich.setEnabled(duocBat);
    }

    public static JButton taoNutLich(
            JTextField txtTruong, java.time.format.DateTimeFormatter dinhDang) {
        BoChonNgay boChon = new BoChonNgay((LocalDate) null);
        boChon.txtTruongVanBan = txtTruong;
        JButton btn = boChon.btnNutLich;
        for (ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
        btn.addActionListener(
                suKien -> {
                    String vanBan = txtTruong.getText().trim();
                    LocalDate ngay = docNgayLinhHoat(vanBan, dinhDang);
                    boChon.ngayDuocChon = ngay;
                    LocalDate moc = ngay != null ? ngay : LocalDate.now();
                    boChon.hienTaiThang = moc.getMonthValue();
                    boChon.hienTaiNam = moc.getYear();
                    boChon.hienThiLich();
                });
        return btn;
    }

    public static LocalDate docNgayLinhHoat(
            String vanBan, java.time.format.DateTimeFormatter dinhDang) {
        if (vanBan == null || vanBan.isBlank()) {
            return null;
        }
        String chuoi = vanBan.trim();
        for (java.time.format.DateTimeFormatter fmt :
                new java.time.format.DateTimeFormatter[] {
                    dinhDang,
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu")
                            .withResolverStyle(java.time.format.ResolverStyle.STRICT),
                    java.time.format.DateTimeFormatter.ofPattern("uuuu/MM/dd")
                            .withResolverStyle(java.time.format.ResolverStyle.STRICT),
                    java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd")
                            .withResolverStyle(java.time.format.ResolverStyle.STRICT)
                }) {
            if (fmt == null) continue;
            try {
                return LocalDate.parse(chuoi, fmt);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
