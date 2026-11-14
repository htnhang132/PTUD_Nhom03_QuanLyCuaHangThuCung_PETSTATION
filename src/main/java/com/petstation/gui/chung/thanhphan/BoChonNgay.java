package com.petstation.gui.chung.thanhphan;

import com.formdev.flatlaf.util.UIScale;
import com.petstation.gui.chung.BieuTuong;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.util.DinhDang;

import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class BoChonNgay extends JPanel {
    private JTextField oTruongVanBan;

    private JButton btnNutLich;

    private JDialog lichHopThoai;

    private LocalDate ngayDuocChon;

    private JLabel nhanNhanThangNam;

    private JPanel khungSoNgayVungHienThi;

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
        oTruongVanBan = new JTextField(10);
        oTruongVanBan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oTruongVanBan.setEditable(false);
        oTruongVanBan.setBackground(Color.WHITE);
        if (ngayDuocChon != null) {
            oTruongVanBan.setText(dinhDangNgay(ngayDuocChon));
        }
        btnNutLich = new JButton(new BieuTuong.Net(BieuTuong.Net.Kieu.LICH, 18, MauUngDung.NAU));
        btnNutLich.setToolTipText("Chọn ngày");
        btnNutLich.getAccessibleContext().setAccessibleName("Chọn ngày");
        btnNutLich.setPreferredSize(new Dimension(34, 34));
        btnNutLich.setFocusPainted(false);
        btnNutLich.addActionListener(suKien -> hienThiLich());
        add(oTruongVanBan, BorderLayout.CENTER);
        add(btnNutLich, BorderLayout.EAST);
    }

    private void hienThiLich() {
        if (oTruongVanBan == null || !oTruongVanBan.isShowing()) {
            return;
        }
        if (lichHopThoai == null) {
            taoLichHopThoai();
        }
        if (ngayDuocChon != null) {
            hienTaiThang = ngayDuocChon.getMonthValue();
            hienTaiNam = ngayDuocChon.getYear();
        }
        capNhatLich();
        Point viTri = oTruongVanBan.getLocationOnScreen();
        lichHopThoai.pack();
        GraphicsConfiguration cauHinhDoHoa = oTruongVanBan.getGraphicsConfiguration();
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
        int doc = viTri.y + oTruongVanBan.getHeight();
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
                SwingUtilities.getWindowAncestor(oTruongVanBan != null ? oTruongVanBan : this);
        if (cuaSo instanceof Frame frame) {
            lichHopThoai = new JDialog(frame, "Chọn ngày", true);
        } else if (cuaSo instanceof Dialog dialog) {
            lichHopThoai = new JDialog(dialog, "Chọn ngày", true);
        } else {
            lichHopThoai = new JDialog((Frame) null, "Chọn ngày", true);
        }
        lichHopThoai.setLayout(new BorderLayout());
        lichHopThoai.setUndecorated(false);
        lichHopThoai.add(taoPhanDauLich(), BorderLayout.NORTH);
        lichHopThoai.add(taoLuoiNgayLich(), BorderLayout.CENTER);
        lichHopThoai.add(taoChanTrangLich(), BorderLayout.SOUTH);
        lichHopThoai.pack();
        lichHopThoai.setResizable(false);
        lichHopThoai
                .getRootPane()
                .registerKeyboardAction(
                        e -> lichHopThoai.setVisible(false),
                        KeyStroke.getKeyStroke("ESCAPE"),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private JPanel taoPhanDauLich() {
        JPanel khungTieuDe = new JPanel(new BorderLayout());
        khungTieuDe.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton btnTruoc = new JButton(new BieuTuong.GiaoDien(BieuTuong.GiaoDien.Kieu.TRUOC));
        btnTruoc.setToolTipText("Tháng trước");
        btnTruoc.getAccessibleContext().setAccessibleName("Tháng trước");
        btnTruoc.addActionListener(e -> doiThangLich(-1));
        JButton btnSau = new JButton(new BieuTuong.GiaoDien(BieuTuong.GiaoDien.Kieu.SAU));
        btnSau.setToolTipText("Tháng sau");
        btnSau.getAccessibleContext().setAccessibleName("Tháng sau");
        btnSau.addActionListener(e -> doiThangLich(1));
        nhanNhanThangNam = new JLabel("", SwingConstants.CENTER);
        nhanNhanThangNam.setFont(nhanNhanThangNam.getFont().deriveFont(Font.BOLD, 14f));
        khungTieuDe.add(btnTruoc, BorderLayout.WEST);
        khungTieuDe.add(nhanNhanThangNam, BorderLayout.CENTER);
        khungTieuDe.add(btnSau, BorderLayout.EAST);
        return khungTieuDe;
    }

    private void doiThangLich(int buoc) {
        hienTaiThang += buoc;
        if (hienTaiThang < 1) {
            hienTaiThang = 12;
            hienTaiNam--;
        } else if (hienTaiThang > 12) {
            hienTaiThang = 1;
            hienTaiNam++;
        }
        capNhatLich();
    }

    private JPanel taoLuoiNgayLich() {
        khungSoNgayVungHienThi = new JPanel(new GridLayout(7, 7, 4, 4));
        khungSoNgayVungHienThi.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        for (String tenNgay : new String[] {"CN", "T2", "T3", "T4", "T5", "T6", "T7"}) {
            JLabel nhanHienThi = new JLabel(tenNgay, SwingConstants.CENTER);
            nhanHienThi.setFont(nhanHienThi.getFont().deriveFont(Font.BOLD));
            nhanHienThi.setForeground(MauUngDung.NAU);
            khungSoNgayVungHienThi.add(nhanHienThi);
        }
        return khungSoNgayVungHienThi;
    }

    private JPanel taoChanTrangLich() {
        JPanel khungChanTrang = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnHomNay = new JButton("Hôm nay");
        btnHomNay.addActionListener(e -> chonNgay(LocalDate.now()));
        JButton btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(
                e -> {
                    datNgayDuocChon(null);
                    lichHopThoai.setVisible(false);
                });
        khungChanTrang.add(btnHomNay);
        khungChanTrang.add(btnXoa);
        return khungChanTrang;
    }

    private void capNhatLich() {
        nhanNhanThangNam.setText(String.format("Tháng %d/%d", hienTaiThang, hienTaiNam));
        Component[] cacThanhPhan = khungSoNgayVungHienThi.getComponents();
        for (int chiSo = cacThanhPhan.length - 1; chiSo >= 7; chiSo--) {
            khungSoNgayVungHienThi.remove(chiSo);
        }
        LocalDate dauTienNgay = LocalDate.of(hienTaiNam, hienTaiThang, 1);
        int dauTienNgayCuaTuan = dauTienNgay.getDayOfWeek().getValue() % 7;
        for (int chiSo = 0; chiSo < dauTienNgayCuaTuan; chiSo++) {
            khungSoNgayVungHienThi.add(new JLabel(""));
        }
        int soNgayVaoThang = dauTienNgay.lengthOfMonth();
        for (int ngay = 1; ngay <= soNgayVaoThang; ngay++) {
            final LocalDate ngay2 = LocalDate.of(hienTaiNam, hienTaiThang, ngay);
            JButton btnNutNgay = new JButton(String.valueOf(ngay));
            btnNutNgay.setFocusPainted(true);
            btnNutNgay.setMargin(new Insets(2, 2, 2, 2));
            int canh =
                    Math.max(
                            UIScale.scale(34),
                            btnNutNgay.getFontMetrics(btnNutNgay.getFont()).stringWidth("31")
                                    + UIScale.scale(16));
            btnNutNgay.setPreferredSize(new Dimension(canh, canh));
            btnNutNgay.setName("ngay:" + ngay);
            if (ngayDuocChon != null && ngay2.equals(ngayDuocChon)) {
                btnNutNgay.setBackground(MauUngDung.DO_THUONG_HIEU);
                btnNutNgay.setForeground(Color.WHITE);
                btnNutNgay.setOpaque(true);
            }
            if (ngay2.equals(LocalDate.now())) {
                btnNutNgay.setBorder(BorderFactory.createLineBorder(MauUngDung.DO_THUONG_HIEU, 2));
            }
            btnNutNgay.addActionListener(suKien -> chonNgay(ngay2));
            khungSoNgayVungHienThi.add(btnNutNgay);
        }
        while (khungSoNgayVungHienThi.getComponentCount() < 49)
            khungSoNgayVungHienThi.add(new JLabel(""));
        khungSoNgayVungHienThi.revalidate();
        khungSoNgayVungHienThi.repaint();
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
            oTruongVanBan.setText(dinhDangNgay(ngay));
            hienTaiThang = ngay.getMonthValue();
            hienTaiNam = ngay.getYear();
        } else {
            oTruongVanBan.setText("");
        }
        firePropertyChange("date", cu, ngay);
    }

    @Override
    public void setEnabled(boolean duocBat) {
        super.setEnabled(duocBat);
        oTruongVanBan.setEnabled(duocBat);
        btnNutLich.setEnabled(duocBat);
    }

    public static JButton taoNutLich(JTextField oTruong, DateTimeFormatter dinhDang) {
        BoChonNgay boChon = new BoChonNgay((LocalDate) null);
        boChon.oTruongVanBan = oTruong;
        JButton btn = boChon.btnNutLich;
        for (ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
        btn.addActionListener(
                suKien -> {
                    String vanBan = oTruong.getText().trim();
                    LocalDate ngay = docNgayLinhHoat(vanBan, dinhDang);
                    boChon.ngayDuocChon = ngay;
                    LocalDate moc = ngay != null ? ngay : LocalDate.now();
                    boChon.hienTaiThang = moc.getMonthValue();
                    boChon.hienTaiNam = moc.getYear();
                    boChon.hienThiLich();
                });
        return btn;
    }

    public static LocalDate docNgayLinhHoat(String vanBan, DateTimeFormatter dinhDang) {
        if (vanBan == null || vanBan.isBlank()) {
            return null;
        }
        String chuoi = vanBan.trim();
        for (DateTimeFormatter fmt :
                new DateTimeFormatter[] {
                    dinhDang, DinhDang.NGAY_CHUAN, DinhDang.NGAY_GACH_CHEO_NGUOC, DinhDang.NGAY_ISO
                }) {
            if (fmt == null) continue;
            try {
                return LocalDate.parse(chuoi, fmt);
            } catch (RuntimeException ex) {
                LoggerFactory.getLogger(BoChonNgay.class)
                        .debug(
                                "Chuỗi '{}' chưa khớp định dạng ngày {}: {}",
                                chuoi,
                                fmt,
                                ex.getMessage());
            }
        }
        return null;
    }
}
