package com.petstation.gui.spa;

import com.petstation.dto.MucLuaChon;
import com.petstation.dto.SpaDTO;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuMauTruong.LuaChon;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.gui.khachhang.DanhMucKhachHangPanel;
import com.petstation.gui.khachhang.DanhMucThuCungPanel;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiTaiKhoan;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.spa.GoiDichVuSpa;
import com.petstation.model.spa.LichHen;
import com.petstation.model.spa.PhieuSpaTreo;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.service.chung.QuyTrinhService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.TiemPhongService;
import com.petstation.service.spa.LapPhieuSpaService;
import com.petstation.service.spa.PhieuSpaTreoService;
import com.petstation.service.spa.SpaService;
import com.petstation.util.DinhDang;

import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class LapPhieuSpaPanel extends CoSoPanel {
    // 1. KIỂU NỘI BỘ & THUỘC TÍNH

    private static final class GoiDaChon {
        final String maGoi;
        final String tenGoi;
        long donGia;

        GoiDaChon(String maGoi, String tenGoi, long donGia) {
            this.maGoi = maGoi;
            this.tenGoi = tenGoi;
            this.donGia = donGia;
        }
    }

    private final JTextField oSoDienThoai = new JTextField(16);
    private final JLabel nhanKhachHang = new JLabel("Chưa chọn khách hàng");
    private final JComboBox<LuaChon> cboThuCung = new JComboBox<>();
    private final JTextField oCanNang = new JTextField(6);
    private final JTextArea oGhiChu = new JTextArea(2, 24);
    private final JComboBox<LuaChon> cboDichVu = new JComboBox<>();
    private final JLabel nhanGia = new JLabel("Chọn gói và nhập cân nặng để xem đơn giá");
    private final JLabel nhanTongTamTinh = new JLabel("0 ₫");
    private JButton btnThemThu;
    private JButton btnTreo;
    private int luotBaoGia;
    private int luotBaoGiaNhom;
    private int luotHoSo;
    private Timer henGioCanNang;
    private final JLabel nhanNguon = new JLabel("Khách đến trực tiếp");
    private final JLabel nhanTrangThaiNhap = new JLabel("");
    private final JTextArea oYeuCau = new JTextArea(3, 24);

    private final JLabel nhanHuyHieuHoSo = new JLabel("CHƯA CHỌN THÚ CƯNG");
    private final JLabel nhanChiTietHoSo =
            new JLabel("Chọn thú cưng để kiểm tra điều kiện tuổi và tiêm phòng.");
    private final JPanel khungTheHoSo =
            new BoGocPanel(10, MauUngDung.XAM_SIEU_NHAT, false) {
                @Override
                protected void paintBorder(Graphics doHoa) {
                    Graphics2D g2 = (Graphics2D) doHoa.create();
                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(vienKhungHoSo);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    g2.dispose();
                }
            };
    private Color vienKhungHoSo = MauUngDung.VIEN;
    private final JLabel nhanTomTatSucKhoe =
            new JLabel("Mặc định: Bình thường — Đủ điều kiện tiếp nhận");
    private final JPanel khungChiTietSucKhoe = new JPanel(new GridLayout(3, 2, 10, 6));
    private final List<GoiDaChon> dsGoiChon = new ArrayList<>();
    private final DefaultTableModel moHinhDichVu =
            new DefaultTableModel(new String[] {"Gói dịch vụ", "Đơn giá", ""}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable bangDichVuChon = new JTable(moHinhDichVu);

    private final TiemPhongService tiemPhongService = new TiemPhongService();
    private final LapPhieuSpaService lapPhieuSpaService = new LapPhieuSpaService();
    private final NghiepVuService nghiepVuService = new NghiepVuService();
    private final PhieuSpaTreoService phieuSpaTreoService = new PhieuSpaTreoService();
    private final QuyTrinhService quyTrinhService = new QuyTrinhService();
    private final SpaService spaService = new SpaService();

    private final JComboBox<String> cboKiemTraDa =
            new JComboBox<>(
                    new String[] {
                        "Không thấy bất thường",
                        "Có vết trầy xước / Vết thương cũ",
                        "Dấu hiệu nghi nấm / Gàu ngứa",
                        "Khác (mô tả bên dưới)"
                    });
    private final JComboBox<String> cboKiemTraVe =
            new JComboBox<>(new String[] {"Không phát hiện", "Có ve / Rận / Bọ chét"});
    private final JComboBox<String> cboKiemTraMatTai =
            new JComboBox<>(
                    new String[] {
                        "Sạch sẽ, bình thường",
                        "Mắt đỏ / Tai hôi / Chảy dịch",
                        "Khác (mô tả bên dưới)"
                    });
    private final JComboBox<String> cboKiemTraLong =
            new JComboBox<>(
                    new String[] {
                        "Lông mượt, không rối",
                        "Rối nhẹ",
                        "Rối vừa",
                        "Bết dính nặng (Cần gỡ rối/cạo)"
                    });
    private final JComboBox<String> cboKetLuanTiepNhan =
            new JComboBox<>(
                    new String[] {
                        "Đủ điều kiện tiếp nhận",
                        "Cần điều chỉnh dịch vụ / Chăm sóc đặc biệt",
                        "Từ chối tiếp nhận (có vết thương hở / sức khỏe yếu)"
                    });

    private final Consumer<String> khiLuu;
    private final Runnable khiTreo;
    private JButton btnCapNhatSoTiem;
    private JButton btnChiTietSucKhoe;
    private JButton btnPhieuTreo;
    private JButton btnXoaHetGoi;
    private JButton btnTaoPhieu;
    private JButton btnTaoVaLamNgay;
    private Object[] khachHang;
    private String maPhieuTreo = "";
    private String maHen = "";
    private long donGiaHienTai;
    private boolean boQuaSuKienThuCung;
    private boolean dangTaiKhach;
    private boolean moChiTietSucKhoe;
    private boolean dangNhanLichHen;
    private int luotNhanLichHen;

    private static boolean laDangHoatDong(Object giaTri) {
        if (giaTri == null) return false;
        String chuoi = giaTri.toString().trim();
        return "Đang hoạt động".equalsIgnoreCase(chuoi)
                || "Hoạt động".equalsIgnoreCase(chuoi)
                || TrangThaiTaiKhoan.HOAT_DONG.maDB().equalsIgnoreCase(chuoi);
    }

    // 2. HÀM KHỞI TẠO & VÒNG ĐỜI PANEL

    public LapPhieuSpaPanel(Consumer<String> khiLuu, Runnable chonLich, Runnable khiTreo) {
        this.khiLuu = khiLuu;
        this.khiTreo = khiTreo;
        khoiTaoThanhPhanNhapLieu();
        JPanel haiCot = new JPanel(new GridBagLayout());
        haiCot.setOpaque(false);
        GridBagConstraints gTrai = new GridBagConstraints();
        gTrai.gridx = 0;
        gTrai.gridy = 0;
        gTrai.weightx = 0.5;
        gTrai.weighty = 1.0;
        gTrai.fill = GridBagConstraints.BOTH;
        gTrai.insets = new Insets(0, 0, 0, 8);
        haiCot.add(taoCotTrai(chonLich), gTrai);

        GridBagConstraints gPhai = new GridBagConstraints();
        gPhai.gridx = 1;
        gPhai.gridy = 0;
        gPhai.weightx = 0.5;
        gPhai.weighty = 1.0;
        gPhai.fill = GridBagConstraints.BOTH;
        gPhai.insets = new Insets(0, 8, 0, 0);
        haiCot.add(taoCotPhai(), gPhai);

        JPanel baoNoiDung = taoKhungCuonTheoChieuRong();
        baoNoiDung.setLayout(new BorderLayout(0, 12));
        baoNoiDung.setOpaque(false);
        baoNoiDung.setBorder(BorderFactory.createEmptyBorder(2, 2, 12, 2));
        baoNoiDung.add(haiCot, BorderLayout.CENTER);
        setLayout(new BorderLayout(0, 8));
        setBackground(MauUngDung.NEN_KEM);
        add(taoVungCuon(baoNoiDung), BorderLayout.CENTER);
        add(taoThanhThaoTac(), BorderLayout.SOUTH);
    }

    private static JPanel taoKhungCuonTheoChieuRong() {
        return new KhungCuonCoGian();
    }

    private static final class KhungCuonCoGian extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(
                Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(
                Rectangle visibleRect, int orientation, int direction) {
            return 64;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    @Override
    public void xuLyHienThi() {
        lamMoiDuLieuTab();
    }

    @Override
    public void xuLyLamMoi() {
        lamMoiDuLieuTab();
    }

    private void lamMoiDuLieuTab() {
        capNhatSoPhieuTreo();
        if (dangNhanLichHen) return;
        final int luotHen = luotNhanLichHen;
        taiGoiDichVuNen(
                () -> {
                    if (dangNhanLichHen || luotHen != luotNhanLichHen) return;
                    if (khachHang != null) {
                        taiThuCung();
                    }
                    capNhatGiaTuDong();
                    capNhatGiaCacGoiDaChon();
                });
    }

    // 3. XÂY DỰNG GIAO DIỆN & BẢNG DỊCH VỤ

    private void khoiTaoThanhPhanNhapLieu() {
        oSoDienThoai.addActionListener(e -> anToan(this::timKhach));
        oSoDienThoai.putClientProperty("JTextField.placeholderText", "Nhập SĐT hoặc tên khách...");
        oCanNang.putClientProperty("JTextField.placeholderText", "VD: 4.5");
        ganSuKienCanNang();
        LuaChon mauHienThiCombo = new LuaChon("", "Chọn thông tin phù hợp...");
        cboThuCung.setPrototypeDisplayValue(mauHienThiCombo);
        cboDichVu.setPrototypeDisplayValue(mauHienThiCombo);
        cboDichVu.addActionListener(e -> capNhatGiaTuDong());
        cboThuCung.addActionListener(
                e -> {
                    if (!boQuaSuKienThuCung) {
                        khiChonThuCung(true);
                    }
                });
        for (JComboBox<String> cbo :
                List.of(
                        cboKiemTraDa,
                        cboKiemTraVe,
                        cboKiemTraMatTai,
                        cboKiemTraLong,
                        cboKetLuanTiepNhan)) {
            cbo.addActionListener(e -> capNhatTomTatSucKhoe());
        }
        btnCapNhatSoTiem = nutPhu("Sổ tiêm", this::moSoTiemThuCung);
        btnCapNhatSoTiem.setEnabled(false);
        cauHinhBangDichVu();
        cboDichVu.setSelectedIndex(-1);
        taiGoiDichVuNen(null);
    }

    private void ganSuKienCanNang() {
        henGioCanNang =
                new Timer(
                        180,
                        e -> {
                            capNhatGiaTuDong();
                            capNhatGiaCacGoiDaChon();
                        });
        henGioCanNang.setRepeats(false);
        DocumentListener boLangNghe =
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        henGioCanNang.restart();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        henGioCanNang.restart();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        henGioCanNang.restart();
                    }
                };
        oCanNang.getDocument().addDocumentListener(boLangNghe);
    }

    private void cauHinhBangDichVu() {
        DinhDangBang.apDung(bangDichVuChon);
        bangDichVuChon.setAutoCreateRowSorter(false);
        bangDichVuChon.setRowSorter(null);
        bangDichVuChon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangDichVuChon.getTableHeader().setReorderingAllowed(false);
        bangDichVuChon.getColumnModel().getColumn(1).setPreferredWidth(115);
        bangDichVuChon.getColumnModel().getColumn(1).setMaxWidth(135);
        bangDichVuChon.getColumnModel().getColumn(2).setPreferredWidth(44);
        bangDichVuChon.getColumnModel().getColumn(2).setMaxWidth(48);
        bangDichVuChon
                .getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new DefaultTableCellRenderer() {
                            @Override
                            public Component getTableCellRendererComponent(
                                    JTable t,
                                    Object val,
                                    boolean sel,
                                    boolean focus,
                                    int r,
                                    int c) {
                                super.getTableCellRendererComponent(t, val, sel, false, r, c);
                                setHorizontalAlignment(SwingConstants.CENTER);
                                setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
                                setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
                                setBackground(
                                        sel
                                                ? t.getSelectionBackground()
                                                : (r % 2 == 0
                                                        ? MauUngDung.THE_NEN
                                                        : MauUngDung.DONG_XEN_KE));
                                return this;
                            }
                        });
        bangDichVuChon.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int dong = bangDichVuChon.rowAtPoint(e.getPoint());
                        int cot = bangDichVuChon.columnAtPoint(e.getPoint());
                        if (dong >= 0 && cot == 2) {
                            xoaGoiTaiDong(dong);
                        }
                    }
                });
    }

    private JPanel taoCotTrai(Runnable chonLich) {
        JPanel cot = new JPanel(new GridBagLayout());
        cot.setOpaque(false);
        GridBagConstraints gTop = new GridBagConstraints();
        gTop.gridx = 0;
        gTop.gridy = 0;
        gTop.weightx = 1.0;
        gTop.weighty = 0;
        gTop.fill = GridBagConstraints.HORIZONTAL;
        gTop.insets = new Insets(0, 0, 12, 0);
        cot.add(taoTheKhachVaThuCung(chonLich), gTop);

        GridBagConstraints gBot = new GridBagConstraints();
        gBot.gridx = 0;
        gBot.gridy = 1;
        gBot.weightx = 1.0;
        gBot.weighty = 1.0;
        gBot.fill = GridBagConstraints.BOTH;
        cot.add(taoTheKiemTraSucKhoe(), gBot);
        return cot;
    }

    private JPanel taoCotPhai() {
        JPanel cot = new JPanel(new GridBagLayout());
        cot.setOpaque(false);
        GridBagConstraints gTop = new GridBagConstraints();
        gTop.gridx = 0;
        gTop.gridy = 0;
        gTop.weightx = 1.0;
        gTop.weighty = 1.0;
        gTop.fill = GridBagConstraints.BOTH;
        gTop.insets = new Insets(0, 0, 12, 0);
        cot.add(taoTheDichVuSpa(), gTop);

        GridBagConstraints gBot = new GridBagConstraints();
        gBot.gridx = 0;
        gBot.gridy = 1;
        gBot.weightx = 1.0;
        gBot.weighty = 0;
        gBot.fill = GridBagConstraints.HORIZONTAL;
        cot.add(taoTheYeuCauKhach(), gBot);
        return cot;
    }

    private JPanel taoTheKhachVaThuCung(Runnable chonLich) {
        JButton btnTimKhach = nutPhu("Tìm", this::timKhach);
        JButton btnThemKhach = nutPhu("+ Khách", this::themKhach);
        btnThemThu = nutPhu("+ Thú", this::moHoSoThuCung);
        btnThemThu.setEnabled(khachHang != null);
        chuanHoaChieuCaoInput(oSoDienThoai, 205, MauUngDung.CAO_DAU_VAO);
        chuanHoaChieuCaoInput(cboThuCung, 172, MauUngDung.CAO_DAU_VAO);
        chuanHoaChieuCaoInput(oCanNang, 66, MauUngDung.CAO_DAU_VAO);
        nhanKhachHang.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanKhachHang.setForeground(MauUngDung.VAN_BAN_TOI);

        cauHinhTheHoSo();
        JPanel phaiTieuDe = taoHuyHieuNguon(chonLich);
        return taoThe(
                "Khách hàng & Thú cưng",
                phaiTieuDe,
                hangBieuMau("Khách hàng *", oSoDienThoai, btnTimKhach, btnThemKhach),
                hangBieuMau("Đã chọn", nhanKhachHang),
                hangBieuMau("Thú cưng *", cboThuCung, btnThemThu, nhanNho("Cân (kg) *"), oCanNang),
                khungTheHoSo);
    }

    private JPanel taoHuyHieuNguon(Runnable chonLich) {
        JPanel phai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        phai.setOpaque(false);
        nhanNguon.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanNguon.setForeground(MauUngDung.CHINH);
        nhanTrangThaiNhap.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 12));
        nhanTrangThaiNhap.setForeground(MauUngDung.VAN_BAN_XAM);
        phai.add(nhanNguon);
        phai.add(nhanTrangThaiNhap);
        if (chonLich != null) {
            phai.add(nutPhu("Chọn lịch hẹn", chonLich));
        }
        return phai;
    }

    private static void chuanHoaChieuCaoInput(JComponent truong, int rong, int cao) {
        Dimension kichThuoc = new Dimension(rong, cao);
        truong.setPreferredSize(kichThuoc);
        truong.setMinimumSize(new Dimension(Math.min(rong, 64), cao));
    }

    private void cauHinhTheHoSo() {
        khungTheHoSo.setLayout(new BorderLayout(8, 3));
        datVienKhungHoSo(MauUngDung.VIEN);
        JPanel dau = new JPanel(new BorderLayout(8, 0));
        dau.setOpaque(false);
        nhanHuyHieuHoSo.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanHuyHieuHoSo.setForeground(MauUngDung.VAN_BAN_XAM);
        dau.add(nhanHuyHieuHoSo, BorderLayout.CENTER);
        dau.add(btnCapNhatSoTiem, BorderLayout.EAST);
        nhanChiTietHoSo.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanChiTietHoSo.setForeground(MauUngDung.VAN_BAN_TOI);
        khungTheHoSo.add(dau, BorderLayout.NORTH);
        khungTheHoSo.add(nhanChiTietHoSo, BorderLayout.CENTER);
    }

    private JPanel taoTheKiemTraSucKhoe() {
        nhanTomTatSucKhoe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanTomTatSucKhoe.setForeground(MauUngDung.XANH_LA);
        btnChiTietSucKhoe = nutTrongSuot("Chi tiết ▾", this::doiCheDoChiTietSucKhoe);
        JPanel hangTomTat = new JPanel(new BorderLayout(8, 0));
        hangTomTat.setOpaque(false);
        hangTomTat.add(nhanTomTatSucKhoe, BorderLayout.CENTER);
        hangTomTat.add(btnChiTietSucKhoe, BorderLayout.EAST);

        khungChiTietSucKhoe.setOpaque(false);
        khungChiTietSucKhoe.add(taoOComboSucKhoe("Da & Lông", cboKiemTraDa));
        khungChiTietSucKhoe.add(taoOComboSucKhoe("Ký sinh trùng", cboKiemTraVe));
        khungChiTietSucKhoe.add(taoOComboSucKhoe("Mắt & Tai", cboKiemTraMatTai));
        khungChiTietSucKhoe.add(taoOComboSucKhoe("Độ rối lông", cboKiemTraLong));
        khungChiTietSucKhoe.add(taoOComboSucKhoe("Kết luận quầy", cboKetLuanTiepNhan));
        khungChiTietSucKhoe.add(Box.createGlue());
        khungChiTietSucKhoe.setVisible(false);

        return taoThe(
                "Kiểm tra sức khỏe khi nhận",
                null,
                hangTomTat,
                khungChiTietSucKhoe,
                hangBieuMauRong("Ghi chú", taoOChuCuon(oGhiChu, 3, 68)));
    }

    private JPanel taoOComboSucKhoe(String tieuDe, JComboBox<String> cbo) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setOpaque(false);
        p.add(nhanNho(tieuDe), BorderLayout.NORTH);
        cbo.setPrototypeDisplayValue("Từ chối tiếp nhận (có vết thương...)");
        cbo.setPreferredSize(new Dimension(215, MauUngDung.CAO_DAU_VAO));
        p.add(cbo, BorderLayout.CENTER);
        return p;
    }

    private void doiCheDoChiTietSucKhoe() {
        moChiTietSucKhoe = !moChiTietSucKhoe;
        khungChiTietSucKhoe.setVisible(moChiTietSucKhoe);
        btnChiTietSucKhoe.setText(moChiTietSucKhoe ? "Thu gọn ▴" : "Chi tiết ▾");
        khungChiTietSucKhoe.revalidate();
        revalidate();
        repaint();
    }

    private void capNhatTomTatSucKhoe() {
        int batThuong = demDiemBatThuong();
        int ketLuanIdx = cboKetLuanTiepNhan.getSelectedIndex();
        if (batThuong == 0 && ketLuanIdx == 0) {
            nhanTomTatSucKhoe.setText("Mặc định: Bình thường — Đủ điều kiện tiếp nhận");
            nhanTomTatSucKhoe.setForeground(MauUngDung.mau(0x065F46));
            return;
        }
        nhanTomTatSucKhoe.setText(
                cboKetLuanTiepNhan.getSelectedItem()
                        + (batThuong > 0 ? " (" + batThuong + " lưu ý)" : ""));
        nhanTomTatSucKhoe.setForeground(
                ketLuanIdx == 2 ? MauUngDung.VAN_BAN_NGUY_HIEM : MauUngDung.mau(0xB45309));
    }

    private int demDiemBatThuong() {
        int dem = 0;
        for (JComboBox<String> cbo :
                List.of(cboKiemTraDa, cboKiemTraVe, cboKiemTraMatTai, cboKiemTraLong)) {
            if (cbo.getSelectedIndex() > 0) {
                dem++;
            }
        }
        return dem;
    }

    private JPanel taoTheDichVuSpa() {
        chuanHoaChieuCaoInput(cboDichVu, 210, 34);
        JButton btnThemGoi = nutPhu("+ Thêm gói", this::themGoiVaoBang);
        nhanGia.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanGia.setForeground(MauUngDung.VAN_BAN_XAM);

        JScrollPane cuonBang = new JScrollPane(bangDichVuChon);
        cuonBang.setPreferredSize(new Dimension(340, 140));
        cuonBang.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));

        JPanel thanhChonGoi = new JPanel(new BorderLayout(8, 0));
        thanhChonGoi.setOpaque(false);
        thanhChonGoi.add(cboDichVu, BorderLayout.CENTER);
        thanhChonGoi.add(btnThemGoi, BorderLayout.EAST);

        btnXoaHetGoi = nutTrongSuot("Xóa hết", this::xoaTatCaGoiDaChon);
        btnXoaHetGoi.setVisible(false);

        JPanel chanBang = new JPanel(new BorderLayout(8, 0));
        chanBang.setOpaque(false);
        chanBang.add(
                goiY("Nếu bảng trống, hệ thống tự dùng gói đang chọn ở trên."), BorderLayout.WEST);
        chanBang.add(btnXoaHetGoi, BorderLayout.EAST);

        return taoThe("Dịch vụ Spa", null, thanhChonGoi, nhanGia, cuonBang, chanBang);
    }

    private JPanel taoTheYeuCauKhach() {
        return taoThe(
                "Yêu cầu khách hàng & Vật dụng mang theo",
                null,
                taoOChuCuon(oYeuCau, 3, 68),
                goiY("Ví dụ: không cắt sát mặt, thú nhạy cảm tiếng ồn, mang theo dây dắt đỏ..."));
    }

    private JPanel taoThanhThaoTac() {
        JPanel thanh = new JPanel(new BorderLayout(12, 0));
        thanh.setBackground(Color.WHITE);
        thanh.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, MauUngDung.VIEN),
                        BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JPanel trai = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        trai.setOpaque(false);
        JLabel nhanMucTong = new JLabel("Tổng ước tính:");
        nhanMucTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanMucTong.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanTongTamTinh.setFont(MauUngDung.taoPhongChu(Font.BOLD, 17));
        nhanTongTamTinh.setForeground(MauUngDung.CHINH);
        trai.add(nhanMucTong);
        trai.add(nhanTongTamTinh);

        JButton btnLamMoi = nutTrongSuot("Xóa trắng", this::xoaForm);
        btnPhieuTreo = nutPhu("Mở phiếu tạm (0)", this::moDanhSachPhieuTreo);
        btnTreo = nutPhu("Lưu phiếu tạm", this::treoPhieu);
        btnTaoPhieu = nutPhu("Tạo phiếu", () -> luu(false));
        btnTaoVaLamNgay = nutChinh("Tạo & Bắt đầu làm ngay", () -> luu(true));

        JPanel phai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        phai.setOpaque(false);
        phai.add(btnLamMoi);
        phai.add(btnPhieuTreo);
        phai.add(btnTreo);
        phai.add(btnTaoPhieu);
        phai.add(btnTaoVaLamNgay);

        thanh.add(trai, BorderLayout.WEST);
        thanh.add(phai, BorderLayout.EAST);
        return thanh;
    }

    private static JScrollPane taoVungCuon(JPanel noiDung) {
        JScrollPane cuon = new JScrollPane(noiDung);
        cuon.setBorder(null);
        cuon.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        cuon.getViewport().setBackground(MauUngDung.NEN_KEM);
        cuon.getVerticalScrollBar().setUnitIncrement(16);
        return cuon;
    }

    private JPanel taoThe(String tieuDe, Component phaiTieuDe, Component... cac) {
        BoGocPanel the = new BoGocPanel(12, MauUngDung.THE_NEN, true);
        the.setLayout(new BorderLayout(0, 10));
        the.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel dau = new JPanel(new BorderLayout(8, 0));
        dau.setOpaque(false);
        JLabel nhanTieuDe = taoTieuDeMuc(tieuDe);
        dau.add(nhanTieuDe, BorderLayout.WEST);
        if (phaiTieuDe != null) {
            dau.add(phaiTieuDe, BorderLayout.EAST);
        }
        the.add(dau, BorderLayout.NORTH);

        JPanel than = new JPanel();
        than.setOpaque(false);
        than.setLayout(new BoxLayout(than, BoxLayout.Y_AXIS));
        for (int i = 0; i < cac.length; i++) {
            Component c = cac[i];
            if (c instanceof JComponent j) {
                j.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            than.add(c);
            if (i < cac.length - 1) {
                than.add(Box.createVerticalStrut(6));
            }
        }
        the.add(than, BorderLayout.CENTER);
        return the;
    }

    private static JPanel hangBieuMau(String tieuDe, Component... cac) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(2, 0, 2, 8);
        g.anchor = GridBagConstraints.WEST;
        JLabel nhan = new JLabel(tieuDe);
        nhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhan.setForeground(MauUngDung.VAN_BAN_TOI);
        nhan.setPreferredSize(new Dimension(96, 30));
        p.add(nhan, g);

        JPanel phai = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        phai.setOpaque(false);
        for (Component c : cac) {
            phai.add(c);
        }
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        p.add(phai, g);
        return p;
    }

    private static JPanel hangBieuMauRong(String tieuDe, Component noiDung) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        JLabel nhan = new JLabel(tieuDe);
        nhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhan.setForeground(MauUngDung.VAN_BAN_TOI);
        nhan.setPreferredSize(new Dimension(96, 30));
        p.add(nhan, BorderLayout.WEST);
        p.add(noiDung, BorderLayout.CENTER);
        return p;
    }

    private static JScrollPane taoOChuCuon(JTextArea oNhap, int soDong, int chieuCao) {
        oNhap.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oNhap.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        oNhap.setLineWrap(true);
        oNhap.setWrapStyleWord(true);
        oNhap.setRows(soDong);
        JScrollPane cuon = new JScrollPane(oNhap);
        cuon.setPreferredSize(new Dimension(320, chieuCao));
        return cuon;
    }

    private static JLabel nhanNho(String vanBan) {
        JLabel l = new JLabel(vanBan);
        l.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        l.setForeground(MauUngDung.VAN_BAN_TOI);
        return l;
    }

    private static JLabel goiY(String noiDung) {
        JLabel l = new JLabel(noiDung);
        l.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 11));
        l.setForeground(MauUngDung.VAN_BAN_XAM);
        return l;
    }

    // 4. DỊCH VỤ, BẢNG GIÁ & TẠM TÍNH

    private void taiGoiDichVuNen(Runnable sauKhiNap) {
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                nghiepVuService::layLuaChonGoiDichVu,
                null,
                cacGoi -> {
                    capNhatComboDichVuTuDanhSach(cacGoi);
                    if (sauKhiNap != null) {
                        sauKhiNap.run();
                    }
                },
                ex -> {});
    }

    private void capNhatComboDichVuTuDanhSach(List<MucLuaChon> cacGoi) {
        LuaChon dangChon = (LuaChon) cboDichVu.getSelectedItem();
        cboDichVu.removeAllItems();
        for (MucLuaChon goi : cacGoi) {
            cboDichVu.addItem(new LuaChon(goi.giaTri(), goi.nhan()));
        }
        chonItemCombo(cboDichVu, dangChon == null ? null : dangChon.giaTri());
    }

    private static void chonItemCombo(JComboBox<LuaChon> cbo, String ma) {
        cbo.setSelectedIndex(-1);
        if (ma == null || ma.isBlank()) {
            return;
        }
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (ma.equals(cbo.getItemAt(i).giaTri())) {
                cbo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void khiChonThuCung(boolean canhBaoSom) {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        if (thu != null && oCanNang.getText().trim().isEmpty()) {
            taiCanNangMacDinhThuCung(thu.giaTri());
        }
        capNhatDanhGiaHoSo(thu != null && canhBaoSom);
        capNhatGiaTuDong();
        capNhatGiaCacGoiDaChon();
    }

    private void taiCanNangMacDinhThuCung(String maThu) {
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () ->
                        DanhMucService.macDinh().layThuCung().stream()
                                .filter(t -> maThu.equals(String.valueOf(t[0])))
                                .findFirst()
                                .map(t -> t[6] == null ? "" : String.valueOf(t[6]).trim())
                                .orElse(""),
                null,
                cn -> {
                    LuaChon hienTai = (LuaChon) cboThuCung.getSelectedItem();
                    if (hienTai != null
                            && maThu.equals(hienTai.giaTri())
                            && !cn.isEmpty()
                            && oCanNang.getText().trim().isEmpty()) {
                        oCanNang.setText(cn);
                    }
                },
                ex -> {});
    }

    private void themGoiVaoBang() {
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        if (goi == null) {
            HopThoai.hienThiCanhBao(this, "Vui lòng chọn gói dịch vụ Spa.");
            return;
        }
        long gia = layGiaChoGoi(goi.giaTri());
        dsGoiChon.add(new GoiDaChon(goi.giaTri(), goi.nhan(), gia));
        capNhatBangDichVu();
    }

    private long layGiaChoGoi(String maGoi) {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        double cn = docCanNangKhongLoi();
        if (thu == null || cn <= 0) {
            return 0L;
        }
        try {
            return lapPhieuSpaService.baoGia(thu.giaTri(), maGoi, cn);
        } catch (RuntimeException ex) {
            return 0L;
        }
    }

    private void xoaGoiTaiDong(int dong) {
        if (dong >= 0 && dong < dsGoiChon.size()) {
            dsGoiChon.remove(dong);
            capNhatBangDichVu();
        }
    }

    private void xoaTatCaGoiDaChon() {
        dsGoiChon.clear();
        capNhatBangDichVu();
    }

    private void capNhatBangDichVu() {
        moHinhDichVu.setRowCount(0);
        for (GoiDaChon g : dsGoiChon) {
            moHinhDichVu.addRow(
                    new Object[] {
                        g.tenGoi, g.donGia > 0 ? DinhDang.dinhDang(g.donGia) : "Theo cân", "×"
                    });
        }
        btnXoaHetGoi.setVisible(!dsGoiChon.isEmpty());
        capNhatTongTamTinh();
    }

    private void capNhatGiaCacGoiDaChon() {
        if (dsGoiChon.isEmpty()) {
            capNhatTongTamTinh();
            return;
        }
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        double cn = docCanNangKhongLoi();
        if (thu == null || cn <= 0) {
            dsGoiChon.forEach(g -> g.donGia = 0);
            capNhatBangDichVu();
            return;
        }
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        int luot = ++luotBaoGiaNhom;
        List<String> maGoiSnapshot = dsGoiChon.stream().map(g -> g.maGoi).toList();
        TacVuNen.chayCoPhien(
                phien,
                () -> {
                    LuaChon thuSau = (LuaChon) cboThuCung.getSelectedItem();
                    return luot == luotBaoGiaNhom
                            && thuSau != null
                            && thu.giaTri().equals(thuSau.giaTri());
                },
                () -> docNhieuGiaNen(thu.giaTri(), cn, maGoiSnapshot),
                null,
                giaMoi -> apDungGiaChoDanhSach(maGoiSnapshot, giaMoi),
                ex -> {});
    }

    private List<Long> docNhieuGiaNen(String maThu, double cn, List<String> maGois) {
        List<Long> kq = new ArrayList<>();
        for (String ma : maGois) {
            try {
                kq.add(lapPhieuSpaService.baoGia(maThu, ma, cn));
            } catch (RuntimeException ex) {
                kq.add(0L);
            }
        }
        return kq;
    }

    private void apDungGiaChoDanhSach(List<String> maGois, List<Long> giaMoi) {
        for (int i = 0; i < Math.min(dsGoiChon.size(), giaMoi.size()); i++) {
            if (dsGoiChon.get(i).maGoi.equals(maGois.get(i))) {
                dsGoiChon.get(i).donGia = giaMoi.get(i);
            }
        }
        capNhatBangDichVu();
    }

    private void capNhatTongTamTinh() {
        long tong =
                dsGoiChon.isEmpty()
                        ? donGiaHienTai
                        : dsGoiChon.stream().mapToLong(g -> g.donGia).sum();
        nhanTongTamTinh.setText(DinhDang.dinhDang(tong));
    }

    // 5. API CÔNG KHAI & QUẢN LÝ PHIẾU TREO / LỊCH HẸN

    public void xoaForm() {
        if (chupNhap().coNoiDung()
                && !HopThoai.xacNhan(this, "Xóa nội dung đang nhập trên biểu mẫu?")) {
            return;
        }
        khachHang = null;
        datLaiForm();
    }

    private void datLaiForm() {
        maPhieuTreo = "";
        maHen = "";
        nhanNguon.setText("Khách đến trực tiếp");
        nhanTrangThaiNhap.setText("");
        oSoDienThoai.setText("");
        nhanKhachHang.setText("Chưa chọn khách hàng");
        boQuaSuKienThuCung = true;
        cboThuCung.removeAllItems();
        boQuaSuKienThuCung = false;
        if (btnThemThu != null) btnThemThu.setEnabled(false);
        oCanNang.setText("");
        oGhiChu.setText("");
        oYeuCau.setText("");
        cboDichVu.setSelectedIndex(-1);
        dsGoiChon.clear();
        capNhatBangDichVu();
        donGiaHienTai = 0;
        nhanGia.setText("Chọn gói và nhập cân nặng để xem đơn giá");
        datLaiKiemTraSucKhoe();
        capNhatDanhGiaHoSo(false);
    }

    private void datLaiKiemTraSucKhoe() {
        cboKiemTraDa.setSelectedIndex(0);
        cboKiemTraVe.setSelectedIndex(0);
        cboKiemTraMatTai.setSelectedIndex(0);
        cboKiemTraLong.setSelectedIndex(0);
        cboKetLuanTiepNhan.setSelectedIndex(0);
        capNhatTomTatSucKhoe();
    }

    private PhieuSpaTreo chupNhap() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        String maGoiNhap =
                !dsGoiChon.isEmpty() ? dsGoiChon.get(0).maGoi : (goi != null ? goi.giaTri() : null);
        KhachHang kh = khachHang == null ? null : new KhachHang(String.valueOf(khachHang[0]));
        ThuCung tc = thu == null ? null : new ThuCung(thu.giaTri());
        GoiDichVuSpa goiDv = maGoiNhap == null ? null : new GoiDichVuSpa(maGoiNhap);
        LichHen lh = maHen == null || maHen.isBlank() ? null : new LichHen(maHen);
        return new PhieuSpaTreo(
                kh,
                tc,
                goiDv,
                lh,
                oSoDienThoai.getText(),
                oCanNang.getText(),
                taoTinhTrangPhieuTreo(),
                oYeuCau.getText());
    }

    private String taoTinhTrangPhieuTreo() {
        String ghiChu = oGhiChu.getText();
        boolean macDinhSucKhoe =
                cboKiemTraDa.getSelectedIndex() == 0
                        && cboKiemTraVe.getSelectedIndex() == 0
                        && cboKiemTraMatTai.getSelectedIndex() == 0
                        && cboKiemTraLong.getSelectedIndex() == 0
                        && cboKetLuanTiepNhan.getSelectedIndex() == 0;
        if (dsGoiChon.size() <= 1 && macDinhSucKhoe) {
            return ghiChu;
        }
        StringBuilder sb = new StringBuilder(ghiChu == null ? "" : ghiChu);
        if (dsGoiChon.size() > 1) {
            sb.append("\n[[GOI_SPA:")
                    .append(String.join(",", dsGoiChon.stream().map(g -> g.maGoi).toList()))
                    .append("]]");
        }
        if (!macDinhSucKhoe) {
            sb.append("\n[[SUC_KHOE:")
                    .append(cboKiemTraDa.getSelectedIndex())
                    .append(",")
                    .append(cboKiemTraVe.getSelectedIndex())
                    .append(",")
                    .append(cboKiemTraMatTai.getSelectedIndex())
                    .append(",")
                    .append(cboKiemTraLong.getSelectedIndex())
                    .append(",")
                    .append(cboKetLuanTiepNhan.getSelectedIndex())
                    .append("]]");
        }
        return sb.toString();
    }

    private void treoPhieu() {
        PhieuSpaTreo duLieu = chupNhap();
        if (!duLieu.coNoiDung()) {
            HopThoai.hienThiCanhBao(this, "Chưa có nội dung để lưu tạm.");
            return;
        }
        String maHienTai = maPhieuTreo;
        if (btnTreo != null) btnTreo.setEnabled(false);
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> phieuSpaTreoService.treo(maHienTai, duLieu),
                null,
                ma -> {
                    if (btnTreo != null) btnTreo.setEnabled(true);
                    khachHang = null;
                    datLaiForm();
                    capNhatSoPhieuTreo();
                    HopThoai.hienThiThanhCong(this, "Đã lưu phiếu tạm #" + ma + ".");
                },
                ex -> {
                    if (btnTreo != null) btnTreo.setEnabled(true);
                    HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex));
                });
    }

    public void moDanhSachPhieuTreo() {
        if (khiTreo != null) {
            khiTreo.run();
            return;
        }
        moHopThoaiPhieuTreo();
    }

    private void capNhatSoPhieuTreo() {
        if (btnPhieuTreo == null) {
            return;
        }
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        if (phien == null) {
            return;
        }
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> phieuSpaTreoService.danhSach().size(),
                null,
                so -> btnPhieuTreo.setText("Mở phiếu tạm (" + so + ")"),
                ex -> {});
    }

    private void moHopThoaiPhieuTreo() {
        Window cha = SwingUtilities.getWindowAncestor(this);
        JDialog hopThoai =
                new JDialog(
                        cha,
                        "Danh sách phiếu Spa đang tạm (treo)",
                        JDialog.ModalityType.APPLICATION_MODAL);
        DefaultTableModel moHinh =
                new DefaultTableModel(
                        new String[] {"Mã tạm", "Khách hàng", "Thú cưng", "Dịch vụ", "Cập nhật"},
                        0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
        JTable bang = new JTable(moHinh);
        DinhDangBang.apDung(bang);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Runnable taiLai = () -> napBangPhieuTreo(hopThoai, moHinh);
        JButton btnMo = nutChinh("Mở tiếp", () -> moTuBangPhieuTreo(hopThoai, bang));
        JButton btnXoa =
                nutNguyHiem("Xóa phiếu tạm", () -> xoaTuBangPhieuTreo(hopThoai, bang, taiLai));
        JButton btnDong = nutTrongSuot("Đóng", hopThoai::dispose);
        bang.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            moTuBangPhieuTreo(hopThoai, bang);
                        }
                    }
                });
        bang.setPreferredScrollableViewportSize(new Dimension(620, 220));
        hopThoai.setContentPane(taoKhungHopThoaiTreo(bang, btnMo, btnXoa, btnDong));
        HopThoai.canChinhKichThuocVuaManHinh(hopThoai, 640, 280);
        HopThoai.chuanHoaCuaSo(hopThoai, this);
        taiLai.run();
        hopThoai.setVisible(true);
    }

    private static JPanel taoKhungHopThoaiTreo(
            JTable bang, JButton btnMo, JButton btnXoa, JButton btnDong) {
        JPanel chan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        chan.setOpaque(false);
        chan.add(btnXoa);
        chan.add(btnDong);
        chan.add(btnMo);
        JPanel khung = new JPanel(new BorderLayout(0, 12));
        khung.setBackground(Color.WHITE);
        khung.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        khung.add(new JScrollPane(bang), BorderLayout.CENTER);
        khung.add(chan, BorderLayout.SOUTH);
        return khung;
    }

    private void napBangPhieuTreo(JDialog hopThoai, DefaultTableModel moHinh) {
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                phieuSpaTreoService::danhSach,
                null,
                ds -> {
                    moHinh.setRowCount(0);
                    for (SpaDTO.DongPhieuSpaTreo d : ds) {
                        PhieuSpaTreo p = d.getPhieuTreo();
                        String khach =
                                p.getKhachHang() != null && p.getKhachHang().getHoTen() != null
                                        ? p.getKhachHang().getHoTen()
                                        : p.getSoDienThoai();
                        String thu =
                                p.getThuCung() != null && p.getThuCung().getTen() != null
                                        ? p.getThuCung().getTen()
                                        : "";
                        String goi =
                                p.getGoiDichVu() != null && p.getGoiDichVu().getTenGoi() != null
                                        ? p.getGoiDichVu().getTenGoi()
                                        : "";
                        moHinh.addRow(
                                new Object[] {
                                    d.getMaKhoa(),
                                    khach,
                                    thu,
                                    goi,
                                    DinhDang.ngayGio(p.getCapNhatLuc())
                                });
                    }
                    btnPhieuTreo.setText("Mở phiếu tạm (" + ds.size() + ")");
                },
                ex -> HopThoai.hienThiCanhBao(hopThoai, TacVuNen.thongDiepLoi(ex)));
    }

    private void moTuBangPhieuTreo(JDialog hopThoai, JTable bang) {
        int dong = bang.getSelectedRow();
        if (dong < 0) {
            HopThoai.hienThiCanhBao(hopThoai, "Chọn một phiếu tạm cần mở tiếp.");
            return;
        }
        String ma = String.valueOf(bang.getValueAt(dong, 0));
        hopThoai.dispose();
        khoiPhucPhieu(ma);
    }

    private void xoaTuBangPhieuTreo(JDialog hopThoai, JTable bang, Runnable taiLai) {
        int dong = bang.getSelectedRow();
        if (dong < 0) {
            HopThoai.hienThiCanhBao(hopThoai, "Chọn một phiếu tạm cần xóa.");
            return;
        }
        String ma = String.valueOf(bang.getValueAt(dong, 0));
        if (!HopThoai.xacNhan(hopThoai, "Xóa phiếu tạm #" + ma + "?")) {
            return;
        }
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> {
                    phieuSpaTreoService.xoa(ma);
                    return null;
                },
                null,
                v -> taiLai.run(),
                ex -> HopThoai.hienThiCanhBao(hopThoai, TacVuNen.thongDiepLoi(ex)));
    }

    public void khoiPhucPhieu(String ma) {
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> taiDuLieuKhoiPhuc(ma),
                null,
                duLieu -> apDungKhoiPhucPhieu(ma, duLieu),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private Object[] taiDuLieuKhoiPhuc(String ma) {
        PhieuSpaTreo p = phieuSpaTreoService.lay(ma);
        String maKhach =
                p.getKhachHang() != null && p.getKhachHang().getMaKhachHang() != null
                        ? p.getKhachHang().getMaKhachHang()
                        : "";
        Object[] khach =
                maKhach.isBlank()
                        ? null
                        : DanhMucService.macDinh().layKhachHang().stream()
                                .filter(
                                        k ->
                                                maKhach.equals(String.valueOf(k[0]))
                                                        && laDangHoatDong(k[8]))
                                .findFirst()
                                .orElse(null);
        List<Object[]> dsThu = khach == null ? List.of() : DanhMucService.macDinh().layThuCung();
        return new Object[] {p, khach, dsThu, nghiepVuService.layLuaChonGoiDichVu()};
    }

    @SuppressWarnings("unchecked")
    private void apDungKhoiPhucPhieu(String ma, Object[] duLieu) {
        PhieuSpaTreo p = (PhieuSpaTreo) duLieu[0];
        Object[] khach = (Object[]) duLieu[1];
        List<Object[]> dsThu = (List<Object[]>) duLieu[2];
        capNhatComboDichVuTuDanhSach((List<MucLuaChon>) duLieu[3]);
        String maGoi = p.getGoiDichVu() != null ? p.getGoiDichVu().getMaGoi() : "";
        String maThu = p.getThuCung() != null ? p.getThuCung().getMaThuCung() : "";
        String maHenTreo = p.getLichHen() != null ? p.getLichHen().getMaLichHen() : "";

        chonKhach(khach, dsThu, maThu);
        if (khach == null) {
            oSoDienThoai.setText(p.getSoDienThoai());
        }
        chonItemCombo(cboDichVu, maGoi);
        dsGoiChon.clear();
        maHen = maHenTreo == null ? "" : maHenTreo;
        nhanNguon.setText(maHen.isBlank() ? "Khách đến trực tiếp" : "Theo lịch hẹn #" + maHen);
        oCanNang.setText(p.getCanNang());
        khoiPhucTinhTrangVaNhieuGoi(p.getTinhTrang(), (List<MucLuaChon>) duLieu[3]);
        oYeuCau.setText(p.getYeuCau());
        capNhatDanhGiaHoSo(false);
        capNhatGiaTuDong();
        capNhatGiaCacGoiDaChon();
        maPhieuTreo = ma;
        nhanTrangThaiNhap.setText("· Đang sửa phiếu tạm #" + ma);
        HopThoai.hienThiCanhBao(
                this,
                "Đã mở lại phiếu tạm #" + ma + ". Vui lòng kiểm tra lại sức khỏe trước khi tạo.");
    }

    private void khoiPhucTinhTrangVaNhieuGoi(String tinhTrang, List<MucLuaChon> dsLuaChon) {
        datLaiKiemTraSucKhoe();
        String ghiChu = tinhTrang == null ? "" : tinhTrang;
        int idxGoi = ghiChu.indexOf("\n[[GOI_SPA:");
        if (idxGoi >= 0) {
            int dong = ghiChu.indexOf("]]", idxGoi);
            if (dong > idxGoi) {
                String[] cacMa = ghiChu.substring(idxGoi + 11, dong).split(",");
                for (String maGoiChon : cacMa) {
                    String tenGoi =
                            dsLuaChon.stream()
                                    .filter(m -> m.giaTri().equals(maGoiChon))
                                    .map(MucLuaChon::nhan)
                                    .findFirst()
                                    .orElse(maGoiChon);
                    dsGoiChon.add(new GoiDaChon(maGoiChon, tenGoi, 0));
                }
                ghiChu = ghiChu.substring(0, idxGoi) + ghiChu.substring(dong + 2);
            }
        }
        int idxSk = ghiChu.indexOf("\n[[SUC_KHOE:");
        if (idxSk >= 0) {
            int dong = ghiChu.indexOf("]]", idxSk);
            if (dong > idxSk) {
                String[] chiSo = ghiChu.substring(idxSk + 12, dong).split(",");
                if (chiSo.length >= 5) {
                    datChiSoAnToan(cboKiemTraDa, chiSo[0]);
                    datChiSoAnToan(cboKiemTraVe, chiSo[1]);
                    datChiSoAnToan(cboKiemTraMatTai, chiSo[2]);
                    datChiSoAnToan(cboKiemTraLong, chiSo[3]);
                    datChiSoAnToan(cboKetLuanTiepNhan, chiSo[4]);
                    capNhatTomTatSucKhoe();
                }
                ghiChu = ghiChu.substring(0, idxSk) + ghiChu.substring(dong + 2);
            }
        }
        oGhiChu.setText(ghiChu);
        capNhatBangDichVu();
    }

    private static void datChiSoAnToan(JComboBox<String> cbo, String chiSoChu) {
        try {
            int idx = Integer.parseInt(chiSoChu.trim());
            if (idx >= 0 && idx < cbo.getItemCount()) cbo.setSelectedIndex(idx);
        } catch (NumberFormatException ex) {
            LoggerFactory.getLogger(LapPhieuSpaPanel.class)
                    .debug("Bỏ qua chỉ số trạng thái sức khỏe '{}': {}", chiSoChu, ex.getMessage());
        }
    }

    public void nhanLichHen(String maLichHen) {
        if (khachHang != null
                && !HopThoai.xacNhan(this, "Thay nội dung đang nhập bằng lịch hẹn đã chọn?")) {
            return;
        }
        dangNhanLichHen = true;
        final int luotHen = ++luotNhanLichHen;
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> luotHen == luotNhanLichHen,
                () -> taiDuLieuNhanLichHen(maLichHen),
                null,
                duLieu -> {
                    dangNhanLichHen = false;
                    apDungNhanLichHen(duLieu);
                },
                ex -> {
                    dangNhanLichHen = false;
                    HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex));
                });
    }

    private Object[] taiDuLieuNhanLichHen(String maLichHen) {
        var hen = quyTrinhService.layLichHen(maLichHen);
        if (hen.isEmpty()) {
            throw new IllegalArgumentException("Lịch hẹn không còn chờ tiếp nhận.");
        }
        LichHen chiTiet = spaService.layChiTietLichHen(maLichHen);
        List<Object[]> dsThu = DanhMucService.macDinh().layThuCung();
        String maThuChiTiet =
                chiTiet.getThuCung() != null ? chiTiet.getThuCung().getMaThuCung() : "";
        Object[] thu =
                dsThu.stream()
                        .filter(t -> String.valueOf(t[0]).equals(maThuChiTiet))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Không tìm thấy hồ sơ thú cưng."));
        String maChu = DanhMucService.layMaTuLuaChon(thu[8]);
        Object[] chu =
                DanhMucService.macDinh().layKhachHang().stream()
                        .filter(k -> String.valueOf(k[0]).equals(maChu))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Không tìm thấy hồ sơ khách hàng."));
        return new Object[] {
            hen.get().getMaLichHen(), chiTiet, chu, dsThu, nghiepVuService.layLuaChonGoiDichVu()
        };
    }

    @SuppressWarnings("unchecked")
    private void apDungNhanLichHen(Object[] duLieu) {
        String maHenMoi = (String) duLieu[0];
        LichHen chiTiet = (LichHen) duLieu[1];
        Object[] chu = (Object[]) duLieu[2];
        List<Object[]> dsThu = (List<Object[]>) duLieu[3];
        capNhatComboDichVuTuDanhSach((List<MucLuaChon>) duLieu[4]);
        maHen = maHenMoi;
        maPhieuTreo = "";
        nhanNguon.setText("Theo lịch hẹn #" + maHen);
        nhanTrangThaiNhap.setText("");
        dsGoiChon.clear();
        oCanNang.setText("");
        oGhiChu.setText("");
        oYeuCau.setText("");
        datLaiKiemTraSucKhoe();
        chonKhach(
                chu,
                dsThu,
                chiTiet.getThuCung() != null ? chiTiet.getThuCung().getMaThuCung() : "");
        String maGoi = chiTiet.getDichVu() != null ? chiTiet.getDichVu().getMaGoi() : "";
        chonItemCombo(cboDichVu, maGoi);
        if (cboDichVu.getSelectedItem() == null) {
            HopThoai.hienThiCanhBao(this, "Dịch vụ của lịch hẹn không còn hoạt động.");
            return;
        }
        themGoiVaoBang();
        capNhatGiaTuDong();
    }

    private double canNang() {
        try {
            double v = Double.parseDouble(oCanNang.getText().trim().replace(',', '.'));
            if (!Double.isFinite(v) || v <= 0 || v > 100) {
                throw new IllegalArgumentException(
                        "Cân nặng phải nằm trong khoảng từ 0,1 đến 100 kg.");
            }
            return v;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Cân nặng phải là số hợp lệ (VD: 4.5).");
        }
    }

    private double docCanNangKhongLoi() {
        try {
            double v = Double.parseDouble(oCanNang.getText().trim().replace(',', '.'));
            return Double.isFinite(v) && v > 0 && v <= 100 ? v : -1;
        } catch (RuntimeException ex) {
            return -1;
        }
    }

    private void capNhatGiaTuDong() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        double cn = docCanNangKhongLoi();
        if (thu == null || goi == null || cn <= 0) {
            donGiaHienTai = 0;
            nhanGia.setText("Chọn thú cưng, dịch vụ và cân nặng hợp lệ để xem giá.");
            capNhatTongTamTinh();
            return;
        }
        String maThu = thu.giaTri();
        String maGoi = goi.giaTri();
        int luot = ++luotBaoGia;
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> {
                    LuaChon thuSau = (LuaChon) cboThuCung.getSelectedItem();
                    LuaChon goiSau = (LuaChon) cboDichVu.getSelectedItem();
                    return luot == luotBaoGia
                            && thuSau != null
                            && goiSau != null
                            && maThu.equals(thuSau.giaTri())
                            && maGoi.equals(goiSau.giaTri());
                },
                () -> lapPhieuSpaService.baoGia(maThu, maGoi, cn),
                null,
                gia -> {
                    donGiaHienTai = gia;
                    nhanGia.setText("Đơn giá theo cân: " + DinhDang.dinhDang(gia));
                    capNhatTongTamTinh();
                },
                ex -> {
                    donGiaHienTai = 0;
                    nhanGia.setText(TacVuNen.thongDiepLoi(ex));
                    capNhatTongTamTinh();
                });
    }

    // 6. TIỆN ÍCH TẠO NÚT & KHUNG GIAO DIỆN

    private JButton nutChinh(String nhan, Runnable xuLy) {
        NutDinhDang nut = new NutDinhDang(nhan, NutDinhDang.KieuNut.CHINH);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (xuLy != null) {
            nut.addActionListener(e -> anToan(xuLy));
        }
        return nut;
    }

    private JButton nutPhu(String nhan, Runnable xuLy) {
        NutDinhDang nut = new NutDinhDang(nhan, NutDinhDang.KieuNut.PHU);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (xuLy != null) {
            nut.addActionListener(e -> anToan(xuLy));
        }
        return nut;
    }

    private JButton nutTrongSuot(String nhan, Runnable xuLy) {
        NutDinhDang nut = new NutDinhDang(nhan, NutDinhDang.KieuNut.TRONG_SUOT);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (xuLy != null) {
            nut.addActionListener(e -> anToan(xuLy));
        }
        return nut;
    }

    private JButton nutNguyHiem(String nhan, Runnable xuLy) {
        NutDinhDang nut = new NutDinhDang(nhan, NutDinhDang.KieuNut.NGUY_HIEM);
        nut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (xuLy != null) {
            nut.addActionListener(e -> anToan(xuLy));
        }
        return nut;
    }

    private void anToan(Runnable xuLy) {
        try {
            xuLy.run();
        } catch (RuntimeException loi) {
            HopThoai.hienThiCanhBao(this, loi.getMessage());
        }
    }

    // 7. KHÁCH HÀNG, THÚ CƯNG & LƯU PHIẾU

    private void timKhach() {
        if (dangTaiKhach) {
            return;
        }
        String khoa = oSoDienThoai.getText().trim();
        if (khoa.isEmpty()) {
            throw new IllegalArgumentException("Nhập số điện thoại hoặc tên khách hàng.");
        }
        String tuKhoaThuong = khoa.toLowerCase(Locale.ROOT);
        String tuKhoaSo = khoa.replaceAll("[\\s.]", "").toLowerCase(Locale.ROOT);
        dangTaiKhach = true;
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () ->
                        DanhMucService.macDinh().layKhachHang().stream()
                                .filter(k -> khopKhachHang(k, tuKhoaThuong, tuKhoaSo))
                                .toList(),
                null,
                kq -> {
                    dangTaiKhach = false;
                    xuLyKetQuaTimKhach(kq);
                },
                ex -> {
                    dangTaiKhach = false;
                    HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex));
                });
    }

    private static boolean khopKhachHang(Object[] khach, String tuKhoaThuong, String tuKhoaSo) {
        String ma = String.valueOf(khach[0]).toLowerCase(Locale.ROOT);
        String ten = String.valueOf(khach[1]).toLowerCase(Locale.ROOT);
        String sdt = String.valueOf(khach[2]).replaceAll("[\\s.]", "").toLowerCase(Locale.ROOT);
        return ma.contains(tuKhoaThuong)
                || ten.contains(tuKhoaThuong)
                || (!tuKhoaSo.isEmpty() && sdt.contains(tuKhoaSo));
    }

    private void xuLyKetQuaTimKhach(List<Object[]> ketQua) {
        if (ketQua.isEmpty()) {
            if (HopThoai.xacNhan(this, "Chưa có khách hàng phù hợp. Thêm khách hàng mới?")) {
                themKhach();
            }
            return;
        }
        List<Object[]> hoatDong = ketQua.stream().filter(k -> laDangHoatDong(k[8])).toList();
        if (hoatDong.isEmpty()) {
            HopThoai.hienThiCanhBao(this, "Khách hàng này đang ngừng hoạt động.");
            return;
        }
        if (hoatDong.size() == 1) {
            chonKhach(hoatDong.get(0));
            return;
        }
        Object[] daChon = chonTuNhieuKhach(hoatDong);
        if (daChon != null) {
            chonKhach(daChon);
        }
    }

    private Object[] chonTuNhieuKhach(List<Object[]> hoatDong) {
        String[] nhan = new String[hoatDong.size()];
        for (int i = 0; i < hoatDong.size(); i++) {
            Object[] k = hoatDong.get(i);
            nhan[i] = k[0] + " - " + k[1] + " (" + k[2] + ")";
        }
        Object chon =
                HopThoai.chonMuc(
                        this,
                        "Có nhiều khách hàng trùng khớp. Chọn khách hàng:",
                        "Chọn khách hàng",
                        nhan,
                        nhan[0]);
        if (chon == null) {
            return null;
        }
        String chuoiChon = String.valueOf(chon);
        for (int i = 0; i < nhan.length; i++) {
            if (nhan[i].equals(chuoiChon)) {
                return hoatDong.get(i);
            }
        }
        return null;
    }

    private void themKhach() {
        String tuKhoa = oSoDienThoai.getText().trim();
        String sdtGoiY = tuKhoa.chars().allMatch(Character::isDigit) ? tuKhoa : "";
        Object[] moi = DanhMucKhachHangPanel.moThem(this, sdtGoiY);
        if (moi != null) {
            chonKhach(moi);
            if (cboThuCung.getItemCount() == 0
                    && HopThoai.xacNhan(this, "Thêm thú cưng cho khách hàng mới ngay?")) {
                moHoSoThuCung();
            }
        }
    }

    private void chonKhach(Object[] khach) {
        this.khachHang = khach;
        if (btnThemThu != null) btnThemThu.setEnabled(khach != null);
        nhanKhachHang.setText(
                khach == null
                        ? "Chưa chọn khách hàng"
                        : khach[0] + " - " + khach[1] + " | " + khach[2]);
        if (khach != null) {
            oSoDienThoai.setText(String.valueOf(khach[2]));
        }
        taiThuCung();
    }

    private void chonKhach(Object[] khach, List<Object[]> dsThu, String maThuChon) {
        this.khachHang = khach;
        if (btnThemThu != null) btnThemThu.setEnabled(khach != null);
        nhanKhachHang.setText(
                khach == null
                        ? "Chưa chọn khách hàng"
                        : khach[0] + " - " + khach[1] + " | " + khach[2]);
        if (khach != null) {
            oSoDienThoai.setText(String.valueOf(khach[2]));
        }
        dienComboThuCung(dsThu, maThuChon);
    }

    private void taiThuCung() {
        if (khachHang == null) {
            cboThuCung.removeAllItems();
            capNhatDanhGiaHoSo(false);
            return;
        }
        LuaChon cu = (LuaChon) cboThuCung.getSelectedItem();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> DanhMucService.macDinh().layThuCung(),
                null,
                dsThu -> dienComboThuCung(dsThu, cu != null ? cu.giaTri() : null),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private void dienComboThuCung(List<Object[]> dsThu, String maThuChon) {
        boQuaSuKienThuCung = true;
        cboThuCung.removeAllItems();
        if (khachHang != null) {
            String maKhach = String.valueOf(khachHang[0]);
            for (Object[] thu : dsThu) {
                String maThu = String.valueOf(thu[0]);
                boolean khopChu = maKhach.equals(DanhMucService.layMaTuLuaChon(thu[8]));
                if (khopChu && (laDangHoatDong(thu[13]) || maThu.equals(maThuChon))) {
                    cboThuCung.addItem(new LuaChon(maThu, thu[0] + " - " + thu[1]));
                }
            }
            chonItemCombo(cboThuCung, maThuChon);
            if (cboThuCung.getSelectedIndex() < 0 && cboThuCung.getItemCount() > 0) {
                cboThuCung.setSelectedIndex(0);
            }
        }
        boQuaSuKienThuCung = false;
        khiChonThuCung(false);
    }

    private void moHoSoThuCung() {
        if (khachHang == null) {
            throw new IllegalArgumentException("Chọn khách hàng trước khi thêm thú cưng.");
        }
        Object[] thuMoi = DanhMucThuCungPanel.moThem(this, String.valueOf(khachHang[0]));
        if (thuMoi == null) {
            return;
        }
        String maThuMoi = String.valueOf(thuMoi[0]);
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> DanhMucService.macDinh().layThuCung(),
                null,
                dsThu -> dienComboThuCung(dsThu, maThuMoi),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private void moSoTiemThuCung() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        if (thu == null) {
            return;
        }
        DanhMucThuCungPanel.moSua(this, thu.giaTri());
        capNhatDanhGiaHoSo(false);
    }

    private void capNhatDanhGiaHoSo(boolean canhBaoSom) {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        if (thu == null) {
            khungTheHoSo.setBackground(MauUngDung.XAM_SIEU_NHAT);
            datVienKhungHoSo(MauUngDung.VIEN);
            nhanHuyHieuHoSo.setText("CHƯA CHỌN THÚ CƯNG");
            nhanHuyHieuHoSo.setForeground(MauUngDung.VAN_BAN_XAM);
            nhanChiTietHoSo.setText("Chọn thú cưng để kiểm tra điều kiện tuổi và tiêm phòng.");
            btnCapNhatSoTiem.setEnabled(false);
            datTrangThaiNutTao(true, null);
            return;
        }
        String maThu = thu.giaTri();
        int luot = ++luotHoSo;
        btnCapNhatSoTiem.setEnabled(true);
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> {
                    LuaChon thuSau = (LuaChon) cboThuCung.getSelectedItem();
                    return luot == luotHoSo && thuSau != null && maThu.equals(thuSau.giaTri());
                },
                () -> tiemPhongService.danhGiaNhanSpa(maThu),
                null,
                kq -> hienThiKetQuaHoSo(kq, canhBaoSom),
                ex -> {
                    khungTheHoSo.setBackground(MauUngDung.KHONG_HOP_LE_NEN);
                    datVienKhungHoSo(MauUngDung.VAN_BAN_NGUY_HIEM);
                    nhanHuyHieuHoSo.setText("KHÔNG KIỂM TRA ĐƯỢC HỒ SƠ TIÊM");
                    nhanHuyHieuHoSo.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
                    nhanChiTietHoSo.setText(TacVuNen.thongDiepLoi(ex));
                });
    }

    private void datVienKhungHoSo(Color mauVien) {
        this.vienKhungHoSo = mauVien;
        khungTheHoSo.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        khungTheHoSo.repaint();
    }

    private void hienThiKetQuaHoSo(TiemPhongService.KetQuaTiemPhong kq, boolean canhBaoSom) {
        if (kq.duDieuKien) {
            khungTheHoSo.setBackground(MauUngDung.mau(0xECFDF5));
            datVienKhungHoSo(MauUngDung.mau(0x6EE7B7));
            nhanHuyHieuHoSo.setText("✓ ĐẠT ĐIỀU KIỆN TIÊM PHÒNG");
            nhanHuyHieuHoSo.setForeground(MauUngDung.mau(0x065F46));
            nhanChiTietHoSo.setText(bocHtml(kq.thongDiep));
            datTrangThaiNutTao(true, null);
            return;
        }
        boolean thieuHoSo = kq.soMui == 0;
        khungTheHoSo.setBackground(
                thieuHoSo ? MauUngDung.mau(0xFFFBEB) : MauUngDung.KHONG_HOP_LE_NEN);
        datVienKhungHoSo(thieuHoSo ? MauUngDung.mau(0xF59E0B) : MauUngDung.VAN_BAN_NGUY_HIEM);
        nhanHuyHieuHoSo.setText(thieuHoSo ? "CHƯA CÓ SỔ TIÊM PHÒNG" : "HẾT HẠN / CHƯA ĐỦ MŨI");
        nhanHuyHieuHoSo.setForeground(
                thieuHoSo ? MauUngDung.mau(0xB45309) : MauUngDung.VAN_BAN_NGUY_HIEM);
        nhanChiTietHoSo.setText(bocHtml(kq.thongDiep));
        datTrangThaiNutTao(false, kq.thongDiep);
        if (canhBaoSom) {
            HopThoai.hienThiCanhBao(this, "Thú cưng chưa đủ điều kiện làm Spa:\n" + kq.thongDiep);
        }
    }

    private static String bocHtml(String vanBan) {
        if (vanBan == null || vanBan.isBlank()) return "";
        String escaped = vanBan.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return "<html><body style='width:300px'>" + escaped + "</body></html>";
    }

    private void datTrangThaiNutTao(boolean choPhep, String goiY) {
        if (btnTaoPhieu != null) {
            btnTaoPhieu.setEnabled(choPhep);
            btnTaoPhieu.setToolTipText(goiY);
        }
        if (btnTaoVaLamNgay != null) {
            btnTaoVaLamNgay.setEnabled(choPhep);
            btnTaoVaLamNgay.setToolTipText(goiY);
        }
    }

    private List<String> layDanhSachMaGoiKhiLuu() {
        if (!dsGoiChon.isEmpty()) {
            return dsGoiChon.stream().map(g -> g.maGoi).toList();
        }
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        return goi == null ? List.of() : List.of(goi.giaTri());
    }

    private void luu(boolean batDauNgay) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        if (khachHang == null) {
            throw new IllegalArgumentException("Chưa chọn khách hàng.");
        }
        LuaChon thuCung = (LuaChon) cboThuCung.getSelectedItem();
        if (thuCung == null) {
            throw new IllegalArgumentException("Chưa chọn thú cưng.");
        }
        List<String> dsMaGoi = layDanhSachMaGoiKhiLuu();
        if (dsMaGoi.isEmpty()) {
            throw new IllegalArgumentException("Cần chọn ít nhất 1 dịch vụ Spa.");
        }
        if (cboKetLuanTiepNhan.getSelectedIndex() == 2
                && !HopThoai.xacNhan(
                        this,
                        "Đánh giá quầy là Từ chối tiếp nhận. Bạn vẫn muốn tiếp tục tạo phiếu?")) {
            return;
        }
        double cn = canNang();
        String tinhTrangLuu = taoTinhTrangLuu();
        String yeuCau = oYeuCau.getText();
        String maThu = thuCung.giaTri();
        String maTreo = maPhieuTreo;
        String maHenLuu = maHen;
        datTrangThaiNutTao(false, null);
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> {
                    TiemPhongService.KetQuaTiemPhong kq = tiemPhongService.danhGiaNhanSpa(maThu);
                    if (!kq.duDieuKien) {
                        throw new IllegalArgumentException(kq.thongDiep);
                    }
                    return phieuSpaTreoService.taoPhieuNhieuDichVu(
                            maTreo, maThu, dsMaGoi, maHenLuu, cn, tinhTrangLuu, yeuCau, batDauNgay);
                },
                null,
                maPhieu -> {
                    datTrangThaiNutTao(true, null);
                    khachHang = null;
                    datLaiForm();
                    capNhatSoPhieuTreo();
                    if (khiLuu != null) {
                        khiLuu.accept(maPhieu);
                    }
                },
                ex -> {
                    datTrangThaiNutTao(true, null);
                    HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex));
                });
    }

    private String taoTinhTrangLuu() {
        StringBuilder dg = new StringBuilder();
        if (cboKiemTraDa.getSelectedIndex() > 0) {
            dg.append("Da/lông: ").append(cboKiemTraDa.getSelectedItem()).append("; ");
        }
        if (cboKiemTraVe.getSelectedIndex() > 0) {
            dg.append("Ký sinh: ").append(cboKiemTraVe.getSelectedItem()).append("; ");
        }
        if (cboKiemTraMatTai.getSelectedIndex() > 0) {
            dg.append("Mắt/tai: ").append(cboKiemTraMatTai.getSelectedItem()).append("; ");
        }
        if (cboKiemTraLong.getSelectedIndex() > 0) {
            dg.append("Độ rối lông: ").append(cboKiemTraLong.getSelectedItem()).append("; ");
        }
        dg.append("Kết luận quầy: ").append(cboKetLuanTiepNhan.getSelectedItem());
        String ghiChuNhap = oGhiChu.getText().trim();
        return ghiChuNhap.isEmpty() ? dg.toString() : dg + " | " + ghiChuNhap;
    }
}
