package com.petstation.gui.chung;

import com.petstation.gui.chung.thanhphan.DinhDangBang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;

public abstract class CoSoPanel extends JPanel implements HoTroThanhCongCu {
    private JTextField oTimKiemChinh;

    private JTable bangChinhBang;

    private JTextField oChuaRangBuocTimKiem;

    public CoSoPanel() {
        theoDoiCuon(this);
        setBackground(MauUngDung.NEN_KEM);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 24, 20, 24));
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(
                false, false, oTimKiemChinh != null, bangChinhBang != null, false, true);
    }

    public boolean coTheRoiManHinh() {
        return true;
    }

    protected void datBangChinh(JTable bang) {
        this.bangChinhBang = bang;
    }

    @Override
    public void xuLyTimKiem() {
        if (oTimKiemChinh != null) {
            oTimKiemChinh.requestFocusInWindow();
            oTimKiemChinh.selectAll();
        }
    }

    @Override
    public void xuLyIn() {
        if (bangChinhBang == null) return;
        JTable bangTam = bangChinhBang;
        try {
            Printable noiDungIn =
                    bangTam.getPrintable(
                            JTable.PrintMode.FIT_WIDTH,
                            new MessageFormat(layTieuDeTroGiup()),
                            new MessageFormat("Trang {0}"));
            PrinterJob congViec = PrinterJob.getPrinterJob();
            congViec.setPrintable(noiDungIn);
            if (!congViec.printDialog()) return;
            TacVuNen.chay(
                    () -> {
                        congViec.print();
                        return null;
                    },
                    v -> {},
                    ngoaiLe ->
                            HopThoai.hienThiCanhBao(
                                    this, "Không thể in: " + TacVuNen.thongDiepLoi(ngoaiLe), "In"));
        } catch (HeadlessException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, "Không thể in: " + ngoaiLe.getMessage(), "In");
        }
    }

    @Override
    public void xuLyTroGiup() {
        HopThoaiTroGiup.hienThi(this, layTieuDeTroGiup(), layNoiDungTroGiup());
    }

    protected String layTieuDeTroGiup() {
        return "Trợ giúp";
    }

    protected String layNoiDungTroGiup() {
        return oTimKiemChinh != null
                ? "Gõ vào ô tìm kiếm (Ctrl+F) để lọc danh sách. Bấm tiêu đề cột để sắp xếp."
                : "Dùng các nút trên màn hình để thao tác.";
    }

    protected JPanel layChiSoThe(String nhan, String giaTri, String ghiChuPhu, Color mauNhan) {
        BoGocPanel khungThe = new BoGocPanel(12, MauUngDung.THE_NEN, true);
        khungThe.setLayout(new BorderLayout(0, 4));
        khungThe.setBorder(new EmptyBorder(14, 18, 14, 18));
        JLabel nhanHienThi = new JLabel(nhan);
        nhanHienThi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanHienThi.setForeground(MauUngDung.VAN_BAN_XAM);
        JLabel nhanGiaTri = new JLabel(giaTri);
        nhanGiaTri.setFont(MauUngDung.taoPhongChu(Font.BOLD, 22));
        nhanGiaTri.setForeground(mauNhan == null ? MauUngDung.VAN_BAN_TOI : mauNhan);
        khungThe.add(nhanHienThi, BorderLayout.NORTH);
        khungThe.add(nhanGiaTri, BorderLayout.CENTER);
        if (ghiChuPhu != null && !ghiChuPhu.isBlank()) {
            JLabel nhanPhu = new JLabel(ghiChuPhu);
            nhanPhu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
            nhanPhu.setForeground(MauUngDung.VAN_BAN_XAM);
            khungThe.add(nhanPhu, BorderLayout.SOUTH);
        }
        return khungThe;
    }

    protected JLabel taoTieuDeManHinh(String vanBan) {
        JLabel nhan = new JLabel(vanBan);
        nhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 20));
        nhan.setForeground(MauUngDung.NAU);
        return nhan;
    }

    protected JPanel taoTieuDeManHinh(String tieuDe, String moTa) {
        JPanel khung = taoVungTrongSuot(new BorderLayout(0, 3));
        khung.add(taoTieuDeManHinh(tieuDe), BorderLayout.NORTH);
        if (moTa != null && !moTa.isBlank()) {
            JLabel nhanMoTa = new JLabel(moTa);
            nhanMoTa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            nhanMoTa.setForeground(MauUngDung.VAN_BAN_XAM);
            khung.add(nhanMoTa, BorderLayout.SOUTH);
        }
        return khung;
    }

    protected JPanel layChiSoThe(String nhan, String giaTri) {
        return layChiSoThe(nhan, giaTri, null, MauUngDung.VAN_BAN_TOI);
    }

    protected JLabel taoTieuDeMuc(String vanBan) {
        JLabel nhanDongDangXet = new JLabel(vanBan);
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        nhanDongDangXet.setForeground(MauUngDung.NAU);
        return nhanDongDangXet;
    }

    protected JScrollPane taoBangDinhDang(String[] cacCot, Object[][] duLieu) {
        DefaultTableModel moHinh = DinhDangBang.taoMoHinhChiDoc(duLieu, cacCot);
        return taoBangDinhDang(moHinh);
    }

    protected JScrollPane taoBangDinhDang(TableModel moHinh) {
        JTable bangBang = new JTable(moHinh);
        DinhDangBang.apDung(bangBang);
        JScrollPane scrKhungCuon = new JScrollPane(bangBang);
        scrKhungCuon.setColumnHeaderView(bangBang.getTableHeader());
        scrKhungCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN, 1));
        scrKhungCuon.getViewport().setBackground(MauUngDung.THE_NEN);
        if (bangChinhBang == null) bangChinhBang = bangBang;
        if (oChuaRangBuocTimKiem != null) {
            lienKetBoLoc(oChuaRangBuocTimKiem, bangBang);
            oChuaRangBuocTimKiem = null;
        }
        return scrKhungCuon;
    }

    protected static JTable layBang(JScrollPane scrCuon) {
        return (JTable) scrCuon.getViewport().getView();
    }

    protected void lienKetBoLoc(JTextField oTimKiem, JTable bangBang) {
        themBoLangNgheTaiLieu(
                oTimKiem,
                () -> {
                    String tuKhoa = oTimKiem.getText().trim();
                    @SuppressWarnings("unchecked")
                    TableRowSorter<TableModel> boSapXep =
                            (TableRowSorter<TableModel>) bangBang.getRowSorter();
                    if (boSapXep == null) return;
                    boSapXep.setRowFilter(
                            tuKhoa.isEmpty()
                                    ? null
                                    : RowFilter.regexFilter("(?iu)" + Pattern.quote(tuKhoa)));
                });
    }

    private static final class BoLangNgheTaiLieuDonGian implements DocumentListener {
        private final Runnable hanhDong;

        private BoLangNgheTaiLieuDonGian(Runnable hanhDong) {
            this.hanhDong = hanhDong;
        }

        @Override
        public void insertUpdate(DocumentEvent suKien) {
            hanhDong.run();
        }

        @Override
        public void removeUpdate(DocumentEvent suKien) {
            hanhDong.run();
        }

        @Override
        public void changedUpdate(DocumentEvent suKien) {
            hanhDong.run();
        }
    }

    public static void themBoLangNgheTaiLieu(JTextComponent oNhap, Runnable hanhDong) {
        oNhap.getDocument().addDocumentListener(new BoLangNgheTaiLieuDonGian(hanhDong));
    }

    public static void thayBoLangNgheTaiLieu(JTextComponent oNhap, Runnable hanhDong) {
        if (oNhap.getDocument() instanceof AbstractDocument taiLieu) {
            for (DocumentListener langNghe : taiLieu.getDocumentListeners()) {
                if (langNghe instanceof BoLangNgheTaiLieuDonGian) {
                    oNhap.getDocument().removeDocumentListener(langNghe);
                }
            }
        }
        themBoLangNgheTaiLieu(oNhap, hanhDong);
    }

    protected JTextField taoOTimKiem(String goiYNhap) {
        JTextField oTruongDangXet = new JTextField();
        oTruongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oTruongDangXet.setForeground(MauUngDung.VAN_BAN_TOI);
        oTruongDangXet.setPreferredSize(new Dimension(220, 34));
        oTruongDangXet.putClientProperty("JTextField.placeholderText", goiYNhap);
        oTruongDangXet.putClientProperty("JTextField.showClearButton", true);
        oTruongDangXet.getAccessibleContext().setAccessibleName(goiYNhap);
        if (oTimKiemChinh == null) oTimKiemChinh = oTruongDangXet;
        oChuaRangBuocTimKiem = oTruongDangXet;
        return oTruongDangXet;
    }

    protected JLabel layNhan(String vanBan, Color nen, Color chu) {
        JLabel nhanDongDangXet =
                new JLabel(vanBan) {
                    @Override
                    protected void paintComponent(Graphics doHoa) {
                        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
                        doHoa2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        doHoa2.setColor(nen);
                        doHoa2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        doHoa2.dispose();
                        super.paintComponent(doHoa);
                    }
                };
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        nhanDongDangXet.setForeground(chu);
        nhanDongDangXet.setBorder(new EmptyBorder(3, 8, 3, 8));
        nhanDongDangXet.setOpaque(false);
        return nhanDongDangXet;
    }

    protected BoGocPanel layThe(LayoutManager boCuc) {
        BoGocPanel khungVungDangXet = new BoGocPanel(12, MauUngDung.THE_NEN, true);
        khungVungDangXet.setLayout(boCuc);
        khungVungDangXet.setBorder(new EmptyBorder(18, 20, 18, 20));
        return khungVungDangXet;
    }

    protected JPanel taoVungTrongSuot(LayoutManager boCuc) {
        JPanel khungVungDangXet = new JPanel(boCuc);
        khungVungDangXet.setOpaque(false);
        return khungVungDangXet;
    }

    protected JPanel layTrongTrangThai(String tieuDe, String thongBao, JButton btnThaoTac) {
        JPanel khungHop = new JPanel();
        khungHop.setOpaque(false);
        khungHop.setLayout(new BoxLayout(khungHop, BoxLayout.Y_AXIS));
        JLabel nhanGiaTriTam = new JLabel(tieuDe);
        nhanGiaTriTam.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        nhanGiaTriTam.setForeground(MauUngDung.VAN_BAN_TOI);
        nhanGiaTriTam.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nhanMoHinhDangXet = new JLabel(thongBao);
        nhanMoHinhDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhanMoHinhDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanMoHinhDangXet.setAlignmentX(Component.CENTER_ALIGNMENT);
        khungHop.add(nhanGiaTriTam);
        khungHop.add(Box.createVerticalStrut(6));
        khungHop.add(nhanMoHinhDangXet);
        if (btnThaoTac != null) {
            btnThaoTac.setAlignmentX(Component.CENTER_ALIGNMENT);
            khungHop.add(Box.createVerticalStrut(16));
            khungHop.add(btnThaoTac);
        }
        JPanel khungGiua = new JPanel(new GridBagLayout());
        khungGiua.setOpaque(false);
        khungGiua.add(khungHop);
        return khungGiua;
    }

    private static void theoDoiCuon(Container vung) {
        if (vung instanceof JComponent thanhPhan) {
            if (Boolean.TRUE.equals(thanhPhan.getClientProperty("petstation.cuon"))) return;
            thanhPhan.putClientProperty("petstation.cuon", true);
        }
        if (vung instanceof JScrollPane cuon) {
            Runnable capNhat =
                    () -> {
                        Component noiDung = cuon.getViewport().getView();
                        int buoc = noiDung instanceof JTable bang ? bang.getRowHeight() : 24;
                        cuon.getVerticalScrollBar().setUnitIncrement(buoc);
                        cuon.getVerticalScrollBar()
                                .setBlockIncrement(
                                        Math.max(buoc, cuon.getViewport().getHeight() - buoc));
                    };
            cuon.getViewport().addChangeListener(e -> capNhat.run());
            cuon.addHierarchyListener(e -> capNhat.run());
            capNhat.run();
        }
        vung.addContainerListener(
                new ContainerAdapter() {
                    @Override
                    public void componentAdded(ContainerEvent suKien) {
                        if (suKien.getChild() instanceof Container con) theoDoiCuon(con);
                    }
                });
        for (Component con : vung.getComponents()) {
            if (con instanceof Container nhom) theoDoiCuon(nhom);
        }
    }
}
