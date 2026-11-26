package com.petstation.gui.banhang;

import com.petstation.dto.GioHang;
import com.petstation.dto.PhienDangNhap;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.gui.khachhang.BoChonKhachHang;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.kho.SanPhamService;
import com.petstation.util.BarcodeUsbServer;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

// Màn hình: Lập hóa đơn bán hàng (Mã: LAP_HOA_DON)
// Luồng xử lý: GUI -> BanHangService, GioHangService -> HoaDonDAOImpl -> SQL Server (hoa_don,
// chi_tiet_hoa_don)
// Mô tả: Giao diện POS bán hàng chuyên nghiệp, quét barcode/nhập mã SP, bảng giỏ hàng toàn màn
// hình, tính tiền và in bill
// Phân hệ: banhang
public class LapHoaDonPanel extends CoSoPanel {
    // 1. KIỂU DỮ LIỆU & THUỘC TÍNH POS

    private static final class SanPham {
        final String ma;
        final String ten;
        final long gia;
        final int tonKho;
        final String barcode;

        private SanPham(String ma, String ten, long gia, int tonKho, String barcode) {
            this.ma = ma;
            this.ten = ten;
            this.gia = gia;
            this.tonKho = tonKho;
            this.barcode = barcode;
        }
    }

    private static final class ThaoTacGioHang {
        final String ma;
        final String ten;
        final long donGia;
        final int chenhLechSoLuong;

        private ThaoTacGioHang(String ma, String ten, long donGia, int chenhLechSoLuong) {
            this.ma = ma;
            this.ten = ten;
            this.donGia = donGia;
            this.chenhLechSoLuong = chenhLechSoLuong;
        }
    }

    private static List<SanPham> laySanPham() {
        List<SanPham> ra = new ArrayList<>();
        for (Object[] dongDuLieu : new BanHangService().layCoTheBanCacSanPham()) {
            String barcode =
                    dongDuLieu.length > 6 && dongDuLieu[6] != null
                            ? String.valueOf(dongDuLieu[6]).trim()
                            : "";
            ra.add(
                    new SanPham(
                            String.valueOf(dongDuLieu[0]),
                            String.valueOf(dongDuLieu[1]),
                            ((Number) dongDuLieu[3]).longValue(),
                            ((Number) dongDuLieu[4]).intValue(),
                            barcode));
        }
        return ra;
    }

    private final GioHang banHang;
    private final Consumer<MaManHinh> boDieuHuong;
    private final List<GioHang.DongHang> gioHang = new ArrayList<>();
    private final Deque<ThaoTacGioHang> lichSuThem = new ArrayDeque<>();
    private List<SanPham> danhSachSanPhamCache;
    private int luotTaiSanPham = 0;
    private boolean dangTaiSanPham;
    private final List<Consumer<List<SanPham>>> choSanPhamCallbacks = new ArrayList<>();

    private enum VungO {
        GIAM,
        NHAP,
        TANG,
        XOA,
        KHAC
    }

    private static final int LE_NGANG_SO_LUONG = 4;
    private static final int RONG_NUT_SO_LUONG = 26;
    private static final int RONG_NUT_XOA = 26;
    private static final String TRANG_THAI_MAC_DINH =
            "Sẵn sàng quét mã vạch (Ctrl+K tìm nhanh · F5 tải kho)";

    // Component quét mã vạch
    private JTextField oTimKiem;
    private final JTextField oGhiChu = new JTextField();
    private boolean dangDongBoGhiChu = false;
    private final JSpinner spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JLabel nhanTrangThaiQuet = new JLabel(TRANG_THAI_MAC_DINH);
    private final JLabel nhanKetNoiUsb = new JLabel("Chưa kết nối máy quét");
    private Timer henGioDatLaiTrangThai;
    private BarcodeUsbServer barcodeUsbServer;

    // Bảng giỏ hàng POS
    private DefaultTableModel moHinhBang;
    private JTable bangChiTiet;
    private int dongDiChuot = -1;
    private VungO vungDiChuot = VungO.KHAC;
    private final JLabel nhanSoLuongMatHang = new JLabel("0 mặt hàng · 0 sản phẩm");

    // Cột phải: Khách hàng & Thanh toán
    private final BoChonKhachHang khungBoChon;
    private final JLabel nhanNhanTamTinh = new JLabel("0₫");
    private final JTextArea nhanNhanKhuyenMai = new JTextArea("—", 1, 0);
    private final JLabel nhanNhanGiamGia = new JLabel("0₫");
    private final JLabel nhanNhanTong = new JLabel("0₫");

    private final NutDinhDang btnNutThanhToan =
            new NutDinhDang("Sang thanh toán (F9)", NutDinhDang.KieuNut.CHINH);
    private final NutDinhDang btnNutTreo =
            new NutDinhDang("Lưu đơn nháp (Ctrl+S)", NutDinhDang.KieuNut.PHU);
    private final NutDinhDang btnNutLay =
            new NutDinhDang("Mở đơn nháp (F8)", NutDinhDang.KieuNut.TRONG_SUOT);
    private Consumer<GioHang> boLangNgheGioHang;

    // 2. HÀM KHỞI TẠO

    public LapHoaDonPanel(GioHang banHang, Consumer<MaManHinh> boDieuHuong) {
        this.banHang = banHang;
        this.boDieuHuong = boDieuHuong;
        this.khungBoChon = new BoChonKhachHang(banHang, this::capNhatCacTong);
        dangKyLangNgheGioHang();
        setLayout(new BorderLayout(0, 12));
        add(
                taoTieuDeManHinh(
                        "Lập hóa đơn",
                        "Quét mã vạch hoặc tìm sản phẩm, chọn khách hàng và chuyển sang thanh"
                                + " toán."),
                BorderLayout.NORTH);
        khoiTaoBangVaTimKiemPos();
        JPanel khungTrai = taoVungTrongSuot(new BorderLayout(0, 12));
        khungTrai.add(taoKhuVucQuet(), BorderLayout.NORTH);
        khungTrai.add(taoBangGioHang(), BorderLayout.CENTER);
        BoGocPanel khungPhai = taoKhungThanhToan();
        khungPhai.setPreferredSize(new Dimension(390, 0));
        khungPhai.setMinimumSize(new Dimension(340, 0));
        JPanel khungChinh = taoVungTrongSuot(new BorderLayout(16, 0));
        khungChinh.add(khungTrai, BorderLayout.CENTER);
        khungChinh.add(khungPhai, BorderLayout.EAST);
        add(khungChinh, BorderLayout.CENTER);
        lamMoiGioHang();
        dangKyPhimTatVaBarcodePos();
        napDanhSachSanPhamNen(null);
    }

    // 3. NẠP SẢN PHẨM & XÂY DỰNG GIAO DIỆN

    private void napDanhSachSanPhamNen(Consumer<List<SanPham>> khiXong) {
        if (khiXong != null) {
            choSanPhamCallbacks.add(khiXong);
        }
        if (dangTaiSanPham) return;
        dangTaiSanPham = true;
        int luot = ++luotTaiSanPham;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTaiSanPham,
                LapHoaDonPanel::laySanPham,
                ds -> {
                    dangTaiSanPham = false;
                    this.danhSachSanPhamCache = ds;
                    List<Consumer<List<SanPham>>> dsGoi = new ArrayList<>(choSanPhamCallbacks);
                    choSanPhamCallbacks.clear();
                    for (Consumer<List<SanPham>> cb : dsGoi) {
                        cb.accept(ds);
                    }
                },
                loi -> {
                    dangTaiSanPham = false;
                    choSanPhamCallbacks.clear();
                    capNhatTrangThaiQuet(
                            "Lỗi tải sản phẩm: " + loi.getMessage(), MauUngDung.VAN_BAN_NGUY_HIEM);
                });
    }

    private void layHoacNapSanPham(Consumer<List<SanPham>> khiSanSang) {
        if (danhSachSanPhamCache != null) {
            khiSanSang.accept(danhSachSanPhamCache);
            return;
        }
        napDanhSachSanPhamNen(khiSanSang);
    }

    private void dangKyLangNgheGioHang() {
        this.boLangNgheGioHang =
                gio -> {
                    if (Objects.equals(this.gioHang, gio.getCacDong())
                            && oGhiChu.getText().trim().equals(gio.getGhiChu())) return;
                    Runnable dongBo =
                            () -> {
                                this.gioHang.clear();
                                this.gioHang.addAll(gio.getCacDong());
                                if (this.gioHang.isEmpty()) {
                                    this.lichSuThem.clear();
                                    this.datLaiTrangThaiQuetMacDinh();
                                }
                                this.khungBoChon.dongBo();
                                this.dongBoGhiChu();
                                this.lamMoiGioHang();
                            };
                    if (SwingUtilities.isEventDispatchThread()) {
                        dongBo.run();
                    } else {
                        SwingUtilities.invokeLater(dongBo);
                    }
                };
    }

    private void dongBoGhiChu() {
        String ghiChuHienTai = banHang.getGhiChu();
        if (oGhiChu.getText().trim().equals(ghiChuHienTai)) return;
        dangDongBoGhiChu = true;
        try {
            oGhiChu.setText(ghiChuHienTai);
        } finally {
            dangDongBoGhiChu = false;
        }
    }

    private void khoiTaoBangVaTimKiemPos() {
        oTimKiem = new JTextField();
        oTimKiem.setPreferredSize(new Dimension(0, 42));
        oTimKiem.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        oTimKiem.setToolTipText("Quét mã vạch EAN-13, nhập mã SP hoặc tên rồi nhấn Enter.");
        oTimKiem.addActionListener(suKien -> themTheoQuet());
        oGhiChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        oGhiChu.setToolTipText("Ghi chú nhanh để phân biệt đơn khách lẻ khi lưu nháp");
        themBoLangNgheTaiLieu(
                oGhiChu,
                () -> {
                    if (!dangDongBoGhiChu) banHang.setGhiChu(oGhiChu.getText());
                });
        String[] tieuDeCot = {
            "STT",
            "Mã sản phẩm hoặc mã vạch",
            "Tên hàng hóa / Dịch vụ",
            "Đơn giá",
            "Số lượng",
            "Thành tiền",
            "Ngừng kinh doanh"
        };
        moHinhBang =
                new DefaultTableModel(tieuDeCot, 0) {
                    @Override
                    public boolean isCellEditable(int row, int col) {
                        return col == 4;
                    }
                };
        bangChiTiet = new JTable(moHinhBang);
        bangChiTiet.putClientProperty("giuDoRongCot", Boolean.TRUE);
        DinhDangBang.apDung(bangChiTiet);
        bangChiTiet.setRowHeight(40);
        bangChiTiet.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        thietLapCotBangChiTiet();
        dangKyTuongTacChuotBang();
        bangChiTiet.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        SwingUtilities.invokeLater(
                                LapHoaDonPanel.this::apDungKichThuocCotBangChiTiet);
                    }
                });
    }

    private void dangKyPhimTatVaBarcodePos() {
        dangKyPhimTat("F9", "pay", suKien -> quyetToan());
        dangKyPhimTat("control S", "hold", suKien -> treoGioHangHienTai());
        dangKyPhimTat("F8", "retrieve", suKien -> hienThiTreoCacGioHang());
        dangKyPhimTat(
                "F5",
                "refreshProducts",
                suKien ->
                        napDanhSachSanPhamNen(
                                ds ->
                                        capNhatTrangThaiQuet(
                                                "Đã làm mới " + ds.size() + " sản phẩm từ kho.",
                                                MauUngDung.XANH_LA)));
        dangKyPhimTat("control K", "search", suKien -> oTimKiem.requestFocusInWindow());
        dangKyPhimTat("alt X", "clear", suKien -> xoaGioHang(true));
        addHierarchyListener(
                suKien -> {
                    if ((suKien.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) == 0) return;
                    if (isShowing()) {
                        khoiDongBarcodeUsb();
                        oTimKiem.requestFocusInWindow();
                    } else {
                        dongBarcodeUsbServer();
                    }
                });
    }

    private void thietLapCotBangChiTiet() {
        TableColumnModel cotModel = bangChiTiet.getColumnModel();
        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);
        cotModel.getColumn(0).setCellRenderer(canGiua);
        DefaultTableCellRenderer canPhai = new DefaultTableCellRenderer();
        canPhai.setHorizontalAlignment(SwingConstants.RIGHT);
        cotModel.getColumn(3).setCellRenderer(canPhai);
        cotModel.getColumn(5).setCellRenderer(canPhai);
        cotModel.getColumn(4).setCellRenderer(new TrinhVeSoLuongCell());
        cotModel.getColumn(4).setCellEditor(new TrinhSuaSoLuongCell());
        cotModel.getColumn(6).setCellRenderer(new TrinhVeNutXoaCell());
        if (bangChiTiet.getRowSorter() instanceof TableRowSorter<?> boSapXep) {
            boSapXep.setSortable(4, false);
            boSapXep.setSortable(6, false);
        }
        apDungKichThuocCotBangChiTiet();
    }

    private void apDungKichThuocCotBangChiTiet() {
        if (bangChiTiet == null || bangChiTiet.getColumnModel().getColumnCount() < 7) return;
        TableColumnModel cotModel = bangChiTiet.getColumnModel();
        khoaChieuRongCot(cotModel.getColumn(0), 42, 46, 56);
        cotModel.getColumn(1).setPreferredWidth(120);
        cotModel.getColumn(2).setPreferredWidth(240);
        cotModel.getColumn(3).setPreferredWidth(105);
        khoaChieuRongCot(cotModel.getColumn(4), 124, 124, 124);
        cotModel.getColumn(5).setPreferredWidth(115);
        khoaChieuRongCot(cotModel.getColumn(6), 44, 48, 54);
    }

    private static void khoaChieuRongCot(TableColumn cot, int min, int pref, int max) {
        cot.setMinWidth(min);
        cot.setMaxWidth(max);
        cot.setPreferredWidth(pref);
    }

    private JPanel taoKhuVucQuet() {
        BoGocPanel panel = layThe(new BorderLayout(0, 8));
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel tieuDe = new JLabel("Quét mã hoặc tìm sản phẩm");
        tieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        tieuDe.setForeground(MauUngDung.DO_THUONG_HIEU);
        JPanel dongNhap = taoVungTrongSuot(new BorderLayout(8, 0));
        dongNhap.add(oTimKiem, BorderLayout.CENTER);
        JPanel khungSoLuongVaNut = taoVungTrongSuot(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JLabel nhanSoLuongNgan = new JLabel("Số lượng:");
        nhanSoLuongNgan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        spnSoLuong.setPreferredSize(new Dimension(65, 42));
        spnSoLuong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        NutDinhDang nutThem = new NutDinhDang("Thêm vào đơn (Enter)", NutDinhDang.KieuNut.CHINH);
        nutThem.setPreferredSize(new Dimension(170, 42));
        nutThem.addActionListener(suKien -> themTheoQuet());
        khungSoLuongVaNut.add(nhanSoLuongNgan);
        khungSoLuongVaNut.add(spnSoLuong);
        khungSoLuongVaNut.add(nutThem);
        dongNhap.add(khungSoLuongVaNut, BorderLayout.EAST);
        JPanel trangThai = taoVungTrongSuot(new BorderLayout(8, 0));
        nhanKetNoiUsb.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        nhanKetNoiUsb.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanTrangThaiQuet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanTrangThaiQuet.setForeground(MauUngDung.VAN_BAN_XAM);
        trangThai.add(nhanKetNoiUsb, BorderLayout.WEST);
        trangThai.add(nhanTrangThaiQuet, BorderLayout.EAST);
        panel.add(tieuDe, BorderLayout.NORTH);
        panel.add(dongNhap, BorderLayout.CENTER);
        panel.add(trangThai, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel taoBangGioHang() {
        BoGocPanel panel = layThe(new BorderLayout(0, 8));
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));
        JPanel khungTieuDe = taoVungTrongSuot(new BorderLayout(8, 0));
        JLabel nhanTieuDe = taoTieuDeMuc("Chi tiết mặt hàng trong hóa đơn");
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        nhanSoLuongMatHang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanSoLuongMatHang.setForeground(MauUngDung.VAN_BAN_XAM);
        khungTieuDe.add(nhanTieuDe, BorderLayout.WEST);
        khungTieuDe.add(nhanSoLuongMatHang, BorderLayout.EAST);
        panel.add(khungTieuDe, BorderLayout.NORTH);
        JScrollPane scrBang = new JScrollPane(bangChiTiet);
        scrBang.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrBang.getViewport().setBackground(Color.WHITE);
        panel.add(scrBang, BorderLayout.CENTER);

        // Chỉ giữ nút xóa toàn bộ giỏ hàng căn phải ở đáy bảng
        JPanel khungThaoTacBang = taoVungTrongSuot(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        NutDinhDang btnXoaHet =
                new NutDinhDang("Xóa toàn bộ giỏ (Alt+X)", NutDinhDang.KieuNut.NGUY_HIEM);
        btnXoaHet.addActionListener(e -> xoaGioHang(true));
        khungThaoTacBang.add(btnXoaHet);
        panel.add(khungThaoTacBang, BorderLayout.SOUTH);
        return panel;
    }

    private BoGocPanel taoKhungThanhToan() {
        BoGocPanel khungPhai = layThe(new BorderLayout(0, 10));
        khungPhai.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Tiêu đề & Chọn khách hàng
        JPanel khungTren = taoVungTrongSuot(new BorderLayout(0, 8));
        JLabel nhanTieuDe = taoTieuDeMuc("Thông tin thanh toán");
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        khungTren.add(nhanTieuDe, BorderLayout.NORTH);
        khungTren.add(khungBoChon, BorderLayout.CENTER);
        khungPhai.add(khungTren, BorderLayout.NORTH);

        // Khu vực Tóm tắt tiền
        JPanel khungTongHop = new JPanel();
        khungTongHop.setOpaque(false);
        khungTongHop.setLayout(new BoxLayout(khungTongHop, BoxLayout.Y_AXIS));
        khungTongHop.setBorder(new EmptyBorder(10, 0, 10, 0));
        khungTongHop.add(tinhTongDong("Tạm tính tiền hàng", nhanNhanTamTinh, false));
        khungTongHop.add(Box.createVerticalStrut(6));
        khungTongHop.add(taoDongKhuyenMai());
        khungTongHop.add(Box.createVerticalStrut(6));
        khungTongHop.add(tinhTongDong("Giảm giá khuyến mãi", nhanNhanGiamGia, false));
        khungTongHop.add(Box.createVerticalStrut(10));
        khungTongHop.add(taoKhungGhiChuDon());
        khungTongHop.add(Box.createVerticalStrut(10));
        khungTongHop.add(new JSeparator());
        khungTongHop.add(Box.createVerticalStrut(10));

        // Khung Tổng cộng nổi bật
        JPanel khungTongCong = taoVungTrongSuot(new BorderLayout());
        JLabel nhanChuTong = new JLabel("TỔNG CỘNG");
        nhanChuTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        nhanChuTong.setForeground(MauUngDung.DO_THUONG_HIEU);
        nhanNhanTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 24));
        nhanNhanTong.setForeground(MauUngDung.DO_THUONG_HIEU);
        khungTongCong.add(nhanChuTong, BorderLayout.WEST);
        khungTongCong.add(nhanNhanTong, BorderLayout.EAST);
        khungTongHop.add(khungTongCong);
        JPanel vungTongHop = taoVungTrongSuot(new BorderLayout());
        vungTongHop.add(khungTongHop, BorderLayout.NORTH);
        khungPhai.add(vungTongHop, BorderLayout.CENTER);

        // Cụm nút điều khiển ở đáy
        JPanel khungDay = taoVungTrongSuot(new BorderLayout(0, 8));
        JPanel khungNutNho = taoVungTrongSuot(new GridLayout(1, 2, 8, 0));
        btnNutTreo.setPreferredSize(new Dimension(0, MauUngDung.CAO_NUT));
        btnNutTreo.addActionListener(e -> treoGioHangHienTai());
        btnNutLay.setPreferredSize(new Dimension(0, MauUngDung.CAO_NUT));
        btnNutLay.addActionListener(e -> hienThiTreoCacGioHang());
        khungNutNho.add(btnNutTreo);
        khungNutNho.add(btnNutLay);
        btnNutThanhToan.setPreferredSize(new Dimension(0, 48));
        btnNutThanhToan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        btnNutThanhToan.addActionListener(e -> quyetToan());
        khungDay.add(khungNutNho, BorderLayout.NORTH);
        khungDay.add(btnNutThanhToan, BorderLayout.SOUTH);
        khungPhai.add(khungDay, BorderLayout.SOUTH);
        return khungPhai;
    }

    private JPanel taoKhungGhiChuDon() {
        JPanel khung = taoVungTrongSuot(new BorderLayout(0, 4));
        JLabel nhan = new JLabel("Ghi chú đơn hàng (phân biệt đơn nháp)");
        nhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhan.setForeground(MauUngDung.VAN_BAN_XAM);
        oGhiChu.setPreferredSize(new Dimension(0, MauUngDung.CAO_DAU_VAO));
        khung.add(nhan, BorderLayout.NORTH);
        khung.add(oGhiChu, BorderLayout.CENTER);
        khung.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        return khung;
    }

    private JPanel tinhTongDong(String nhan, JLabel nhanGiaTri, boolean manh) {
        JPanel khungDong = taoVungTrongSuot(new BorderLayout());
        JLabel nhanTen = new JLabel(nhan);
        nhanTen.setFont(MauUngDung.taoPhongChu(manh ? Font.BOLD : Font.PLAIN, manh ? 14 : 13));
        nhanTen.setForeground(manh ? MauUngDung.DO_THUONG_HIEU : MauUngDung.VAN_BAN_XAM);
        nhanGiaTri.setFont(MauUngDung.taoPhongChu(Font.BOLD, manh ? 16 : 13));
        nhanGiaTri.setForeground(manh ? MauUngDung.DO_THUONG_HIEU : MauUngDung.VAN_BAN_TOI);
        khungDong.add(nhanTen, BorderLayout.WEST);
        khungDong.add(nhanGiaTri, BorderLayout.EAST);
        return khungDong;
    }

    private JPanel taoDongKhuyenMai() {
        JPanel khung =
                new JPanel(new BorderLayout(0, 4)) {
                    @Override
                    public Dimension getMaximumSize() {
                        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
                    }
                };
        khung.setOpaque(false);
        JLabel nhan = new JLabel("Khuyến mãi tự áp");
        nhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhan.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanNhanKhuyenMai.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanNhanKhuyenMai.setForeground(MauUngDung.VAN_BAN_TOI);
        nhanNhanKhuyenMai.setEditable(false);
        nhanNhanKhuyenMai.setOpaque(false);
        nhanNhanKhuyenMai.setLineWrap(true);
        nhanNhanKhuyenMai.setWrapStyleWord(true);
        nhanNhanKhuyenMai.setBorder(null);
        khung.add(nhan, BorderLayout.NORTH);
        khung.add(nhanNhanKhuyenMai, BorderLayout.CENTER);
        return khung;
    }

    // 4. TÌM KIẾM, BARCODE & GỢI Ý SẢN PHẨM

    private void dangKyPhimTat(String khoa, String ten, ActionListener thaoTac) {
        KeyStroke phim = KeyStroke.getKeyStroke(khoa);
        if (phim == null) return;
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(phim, ten);
        getActionMap()
                .put(
                        ten,
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent suKien) {
                                thaoTac.actionPerformed(suKien);
                            }
                        });
    }

    private void themTheoQuet() {
        String tuKhoa = oTimKiem.getText().trim();
        if (tuKhoa.isEmpty()) return;
        int soLuong = (Integer) spnSoLuong.getValue();
        layHoacNapSanPham(ds -> timVaThemSanPham(tuKhoa, soLuong, ds));
    }

    private void timVaThemSanPham(String tuKhoa, int soLuong, List<SanPham> ds) {
        List<SanPham> timThay = timSanPhamPhuHop(tuKhoa, ds);
        if (timThay.isEmpty()) {
            capNhatTrangThaiQuet(
                    "Không tìm thấy sản phẩm: " + tuKhoa, MauUngDung.VAN_BAN_NGUY_HIEM);
            HopThoai.hienThiCanhBao(
                    this, "Không tìm thấy sản phẩm với mã, barcode hoặc tên: \"" + tuKhoa + "\".");
            return;
        }
        if (timThay.size() > 1) {
            hienThiPopupGoiY(timThay, soLuong);
            return;
        }
        themSanPhamVaoGio(timThay.get(0), soLuong);
    }

    private List<SanPham> timSanPhamPhuHop(String tuKhoa, List<SanPham> ds) {
        List<SanPham> khopChinhXac =
                ds.stream()
                        .filter(
                                sp ->
                                        sp.barcode.equalsIgnoreCase(tuKhoa)
                                                || sp.ma.equalsIgnoreCase(tuKhoa))
                        .toList();
        if (!khopChinhXac.isEmpty()) return khopChinhXac;

        if (tuKhoa.matches("\\d{6}")) {
            List<SanPham> khopDuoi =
                    ds.stream()
                            .filter(
                                    sp ->
                                            (!sp.barcode.isBlank() && sp.barcode.endsWith(tuKhoa))
                                                    || sp.ma.endsWith(tuKhoa))
                            .sorted(taoBoSapXepGoiY(tuKhoa))
                            .toList();
            if (!khopDuoi.isEmpty()) return khopDuoi;
        }

        String tuKhoaThuong = tuKhoa.toLowerCase(Locale.ROOT);
        return ds.stream()
                .filter(
                        sp ->
                                sp.ten.toLowerCase(Locale.ROOT).contains(tuKhoaThuong)
                                        || sp.ma.toLowerCase(Locale.ROOT).contains(tuKhoaThuong)
                                        || sp.barcode
                                                .toLowerCase(Locale.ROOT)
                                                .contains(tuKhoaThuong))
                .sorted(taoBoSapXepGoiY(tuKhoaThuong))
                .toList();
    }

    private static Comparator<SanPham> taoBoSapXepGoiY(String tuKhoaThuong) {
        return Comparator.comparingInt((SanPham sp) -> sp.tonKho > 0 ? 0 : 1)
                .thenComparingInt(
                        sp ->
                                (sp.ma.equalsIgnoreCase(tuKhoaThuong)
                                                || sp.barcode.equalsIgnoreCase(tuKhoaThuong))
                                        ? 0
                                        : 1)
                .thenComparingInt(
                        sp ->
                                (sp.ma.toLowerCase(Locale.ROOT).startsWith(tuKhoaThuong)
                                                || sp.ten
                                                        .toLowerCase(Locale.ROOT)
                                                        .startsWith(tuKhoaThuong))
                                        ? 0
                                        : 1)
                .thenComparing(sp -> sp.ten, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(sp -> sp.ma, String.CASE_INSENSITIVE_ORDER);
    }

    private void hienThiPopupGoiY(List<SanPham> danhSach, int soLuong) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(MauUngDung.DO_THUONG_HIEU, 1));
        DefaultListModel<SanPham> moHinhGoiY = new DefaultListModel<>();
        danhSach.forEach(moHinhGoiY::addElement);
        JList<SanPham> danhSachGoiY = taoDanhSachGoiY(moHinhGoiY, popup, soLuong);
        JScrollPane cuon = new JScrollPane(danhSachGoiY);
        int chieuCao = Math.min(10, danhSach.size()) * 26 + 6;
        int chieuRong = Math.max(420, oTimKiem.getWidth());
        cuon.setPreferredSize(new Dimension(chieuRong, chieuCao));
        cuon.setBorder(BorderFactory.createEmptyBorder());
        popup.add(cuon);
        popup.show(oTimKiem, 0, oTimKiem.getHeight());
        danhSachGoiY.requestFocusInWindow();
        capNhatTrangThaiQuet(
                "Có " + danhSach.size() + " sản phẩm phù hợp (ưu tiên còn tồn).", MauUngDung.CAM);
    }

    private JList<SanPham> taoDanhSachGoiY(
            DefaultListModel<SanPham> moHinhGoiY, JPopupMenu popup, int soLuong) {
        JList<SanPham> danhSachGoiY = new JList<>(moHinhGoiY);
        danhSachGoiY.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        danhSachGoiY.setFixedCellHeight(26);
        danhSachGoiY.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        if (!moHinhGoiY.isEmpty()) danhSachGoiY.setSelectedIndex(0);
        danhSachGoiY.setCellRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus) {
                        super.getListCellRendererComponent(
                                list, value, index, isSelected, cellHasFocus);
                        if (value instanceof SanPham sp) {
                            setText(
                                    String.format(
                                            "%s - %s [%s] - Tồn: %d",
                                            sp.ma, sp.ten, DinhDang.dinhDang(sp.gia), sp.tonKho));
                            if (!isSelected) {
                                setForeground(
                                        sp.tonKho > 0
                                                ? MauUngDung.VAN_BAN_TOI
                                                : MauUngDung.VAN_BAN_XAM);
                            }
                        }
                        return this;
                    }
                });
        Runnable chonMuc =
                () -> {
                    SanPham chon = danhSachGoiY.getSelectedValue();
                    if (chon == null) return;
                    popup.setVisible(false);
                    themSanPhamVaoGio(chon, soLuong);
                };
        danhSachGoiY.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() >= 1) chonMuc.run();
                    }
                });
        danhSachGoiY.addKeyListener(
                new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) chonMuc.run();
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            popup.setVisible(false);
                            oTimKiem.requestFocusInWindow();
                        }
                    }
                });
        return danhSachGoiY;
    }

    // 5. QUẢN LÝ GIỎ HÀNG, TÍNH TIỀN & TREO ĐƠN

    private void themSanPhamVaoGio(SanPham sanPham, int soLuong) {
        if (sanPham.tonKho <= 0) {
            capNhatTrangThaiQuet(
                    sanPham.ten + " đã hết hàng trong kho.", MauUngDung.VAN_BAN_NGUY_HIEM);
            HopThoai.hienThiCanhBao(this, sanPham.ten + " đã hết hàng.");
            return;
        }
        for (int chiSo = 0; chiSo < gioHang.size(); chiSo++) {
            GioHang.DongHang dongDangXet = gioHang.get(chiSo);
            if (!dongDangXet.getMa().equals(sanPham.ma)) continue;
            if (dongDangXet.getSoLuong() + soLuong > sanPham.tonKho) {
                HopThoai.hienThiCanhBao(
                        this,
                        "Kho chỉ còn "
                                + sanPham.tonKho
                                + " "
                                + sanPham.ten
                                + " (Đã có trong giỏ: "
                                + dongDangXet.getSoLuong()
                                + ").");
                return;
            }
            gioHang.set(
                    chiSo,
                    new GioHang.DongHang(
                            dongDangXet.getMa(),
                            dongDangXet.getTen(),
                            dongDangXet.getDonGia(),
                            dongDangXet.getSoLuong() + soLuong));
            lichSuThem.push(new ThaoTacGioHang(sanPham.ma, sanPham.ten, sanPham.gia, soLuong));
            capNhatTrangThaiQuet(
                    "✓ Đã tăng: " + sanPham.ten + " (+ " + soLuong + ")", MauUngDung.XANH_LA);
            oTimKiem.setText("");
            spnSoLuong.setValue(1);
            lamMoiGioHang();
            return;
        }
        if (soLuong > sanPham.tonKho) {
            HopThoai.hienThiCanhBao(
                    this, "Kho chỉ còn " + sanPham.tonKho + " " + sanPham.ten + ".");
            return;
        }
        gioHang.add(new GioHang.DongHang(sanPham.ma, sanPham.ten, sanPham.gia, soLuong));
        lichSuThem.push(new ThaoTacGioHang(sanPham.ma, sanPham.ten, sanPham.gia, soLuong));
        capNhatTrangThaiQuet(
                "✓ Đã thêm: "
                        + sanPham.ten
                        + " (SL: "
                        + soLuong
                        + " — "
                        + DinhDang.dinhDang(sanPham.gia * soLuong)
                        + ")",
                MauUngDung.XANH_LA);
        oTimKiem.setText("");
        spnSoLuong.setValue(1);
        lamMoiGioHang();
    }

    private int layDongMoHinh(int dongHienThi) {
        if (dongHienThi < 0 || dongHienThi >= bangChiTiet.getRowCount()) return -1;
        return bangChiTiet.getRowSorter() != null
                ? bangChiTiet.convertRowIndexToModel(dongHienThi)
                : dongHienThi;
    }

    private void chonDongTheoMoHinh(int dongMoHinh) {
        if (dongMoHinh < 0 || dongMoHinh >= moHinhBang.getRowCount()) return;
        int dongHienThi =
                bangChiTiet.getRowSorter() != null
                        ? bangChiTiet.convertRowIndexToView(dongMoHinh)
                        : dongMoHinh;
        if (dongHienThi >= 0 && dongHienThi < bangChiTiet.getRowCount()) {
            bangChiTiet.setRowSelectionInterval(dongHienThi, dongHienThi);
        }
    }

    private void ketThucChinhSuaBangNeuCo() {
        if (bangChiTiet.isEditing() && bangChiTiet.getCellEditor() != null) {
            bangChiTiet.getCellEditor().stopCellEditing();
        }
    }

    private int timViTriTrongGio(String maSanPham) {
        for (int i = 0; i < gioHang.size(); i++) {
            if (gioHang.get(i).getMa().equals(maSanPham)) return i;
        }
        return -1;
    }

    private int layTonKhoSanPham(String maSanPham, List<SanPham> ds) {
        if (ds == null) return Integer.MAX_VALUE;
        return ds.stream()
                .filter(sp -> sp.ma.equals(maSanPham))
                .mapToInt(sp -> sp.tonKho)
                .findFirst()
                .orElse(Integer.MAX_VALUE);
    }

    private void canhBaoVaKhoiPhucDong(int dongMoHinh, String thongBao) {
        HopThoai.hienThiCanhBao(this, thongBao);
        lamMoiGioHang();
        chonDongTheoMoHinh(dongMoHinh);
    }

    private void thayDoiSoLuongTheoMa(String maSanPham, int chenhLech) {
        int viTri = timViTriTrongGio(maSanPham);
        if (viTri < 0) return;
        if (chenhLech < 0 && gioHang.get(viTri).getSoLuong() <= 1) {
            chonDongTheoMoHinh(viTri);
            return;
        }
        layHoacNapSanPham(ds -> apDungThayDoiSoLuong(maSanPham, chenhLech, ds, true));
    }

    private void xuLyNhapSoLuongTheoModel(int dongMoHinh, String chuoiMoi) {
        if (dongMoHinh < 0 || dongMoHinh >= gioHang.size()) return;
        GioHang.DongHang dong = gioHang.get(dongMoHinh);
        int slMoi;
        try {
            slMoi = Integer.parseInt(chuoiMoi);
        } catch (NumberFormatException ex) {
            canhBaoVaKhoiPhucDong(dongMoHinh, "Số lượng phải là số nguyên dương.");
            return;
        }
        if (slMoi < 1) {
            canhBaoVaKhoiPhucDong(
                    dongMoHinh, "Số lượng tối thiểu là 1 (nhấn × ở cuối dòng nếu muốn xóa).");
            return;
        }
        if (slMoi == dong.getSoLuong()) return;
        String maSanPham = dong.getMa();
        layHoacNapSanPham(
                ds -> apDungThayDoiSoLuong(maSanPham, slMoi - dong.getSoLuong(), ds, true));
    }

    private void apDungThayDoiSoLuong(
            String maSanPham, int chenhLech, List<SanPham> ds, boolean ghiLichSu) {
        int viTri = timViTriTrongGio(maSanPham);
        if (viTri < 0) return;
        GioHang.DongHang dong = gioHang.get(viTri);
        int tonKho = layTonKhoSanPham(dong.getMa(), ds);
        int slMoi = dong.getSoLuong() + chenhLech;
        if (slMoi > tonKho) {
            canhBaoVaKhoiPhucDong(viTri, "Vượt quá tồn kho (" + tonKho + ").");
            return;
        }
        int chenhLechThucTe = slMoi <= 0 ? -dong.getSoLuong() : chenhLech;
        if (slMoi <= 0) {
            gioHang.remove(viTri);
        } else {
            gioHang.set(
                    viTri,
                    new GioHang.DongHang(dong.getMa(), dong.getTen(), dong.getDonGia(), slMoi));
        }
        if (ghiLichSu && chenhLechThucTe != 0) {
            lichSuThem.push(
                    new ThaoTacGioHang(
                            dong.getMa(), dong.getTen(), dong.getDonGia(), chenhLechThucTe));
        }
        lamMoiGioHang();
        if (!gioHang.isEmpty()) {
            chonDongTheoMoHinh(Math.min(viTri, gioHang.size() - 1));
        }
    }

    private void xoaDongTheoMa(String maSanPham) {
        int viTri = timViTriTrongGio(maSanPham);
        if (viTri < 0) return;
        GioHang.DongHang dong = gioHang.get(viTri);
        lichSuThem.push(
                new ThaoTacGioHang(
                        dong.getMa(), dong.getTen(), dong.getDonGia(), -dong.getSoLuong()));
        gioHang.remove(viTri);
        lamMoiGioHang();
        capNhatTrangThaiQuet("Đã xóa khỏi đơn: " + dong.getTen(), MauUngDung.VAN_BAN_XAM);
    }

    private void lamMoiGioHang() {
        dongDiChuot = -1;
        vungDiChuot = VungO.KHAC;
        moHinhBang.setRowCount(0);
        int stt = 1;
        int tongSanPham = 0;
        for (GioHang.DongHang dong : gioHang) {
            moHinhBang.addRow(
                    new Object[] {
                        stt++,
                        dong.getMa(),
                        dong.getTen(),
                        DinhDang.dinhDang(dong.getDonGia()),
                        dong.getSoLuong(),
                        DinhDang.dinhDang(dong.getDonGia() * dong.getSoLuong()),
                        "×"
                    });
            tongSanPham += dong.getSoLuong();
        }
        nhanSoLuongMatHang.setText(gioHang.size() + " mặt hàng · " + tongSanPham + " sản phẩm");
        banHang.setCacDong(gioHang);
        capNhatCacTong();
    }

    private void capNhatCacTong() {
        nhanNhanTamTinh.setText(DinhDang.dinhDang(banHang.tinhTamTinh()));
        nhanNhanGiamGia.setText(
                banHang.tinhGiamGia() == 0 ? "0₫" : "−" + DinhDang.dinhDang(banHang.tinhGiamGia()));
        nhanNhanTong.setText(DinhDang.dinhDang(banHang.tinhTong()));
        GioHang.KhuyenMai khuyenMai = banHang.getKhuyenMaiTotNhat();
        nhanNhanKhuyenMai.setText(
                khuyenMai == null
                        ? "—"
                        : khuyenMai.getTen() + ": −" + DinhDang.dinhDang(khuyenMai.getSoTien()));
        nhanNhanKhuyenMai.setToolTipText(
                khuyenMai == null
                        ? "Chưa có khuyến mãi phù hợp"
                        : khuyenMai.getMa() + " - " + khuyenMai.getTen());
        btnNutThanhToan.setEnabled(!gioHang.isEmpty());
    }

    private void xoaGioHang(boolean xacNhan) {
        if (gioHang.isEmpty()) return;
        ketThucChinhSuaBangNeuCo();
        if (xacNhan
                && !HopThoai.xacNhanDongY(
                        this,
                        "Xóa toàn bộ " + gioHang.size() + " dòng trong giỏ hàng?",
                        "Xóa giỏ hàng",
                        true)) {
            return;
        }
        gioHang.clear();
        lichSuThem.clear();
        banHang.xoa();
        khungBoChon.xoa();
        dongBoGhiChu();
        capNhatTrangThaiQuet("Đã xóa sạch giỏ hàng.", MauUngDung.VAN_BAN_XAM);
        lamMoiGioHang();
    }

    private void quyetToan() {
        ketThucChinhSuaBangNeuCo();
        if (gioHang.isEmpty()) {
            HopThoai.hienThiCanhBao(this, "Giỏ hàng đang trống.");
            return;
        }
        capNhatCacTong();
        boDieuHuong.accept(MaManHinh.THANH_TOAN);
    }

    private void treoGioHangHienTai() {
        ketThucChinhSuaBangNeuCo();
        if (gioHang.isEmpty()) {
            HopThoai.hienThiCanhBao(this, "Giỏ hàng trống.");
            return;
        }
        btnNutTreo.setEnabled(false);
        banHang.setGhiChu(oGhiChu.getText());
        banHang.setCacDong(gioHang);
        String voucherDangAp = banHang.getMaVoucher();
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> new BanHangService().treo(banHang),
                maDon -> {
                    btnNutTreo.setEnabled(true);
                    xoaGioHang(false);
                    String thongBao =
                            voucherDangAp.isBlank()
                                    ? "Đã lưu đơn treo (" + maDon + ") vào SQL Server."
                                    : "Đã lưu đơn treo ("
                                            + maDon
                                            + "). Lưu ý: mã voucher "
                                            + voucherDangAp
                                            + " chỉ được giữ tạm trong phiên làm việc hiện tại.";
                    HopThoai.hienThiThanhCong(this, thongBao);
                },
                ngoaiLe -> {
                    btnNutTreo.setEnabled(true);
                    HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
                });
    }

    private void hienThiTreoCacGioHang() {
        boDieuHuong.accept(MaManHinh.HOA_DON_CHO);
    }

    // 6. VÒNG ĐỜI PANEL, THANH CÔNG CỤ & KẾT NỐI USB

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(true, true, true, false, true, true);
    }

    @Override
    public void xuLyLuu() {
        treoGioHangHienTai();
    }

    @Override
    public void xuLyTaoMoi() {
        xoaGioHang(true);
    }

    @Override
    public void xuLyHoanTac() {
        if (lichSuThem.isEmpty()) return;
        ketThucChinhSuaBangNeuCo();
        ThaoTacGioHang buoc = lichSuThem.pop();
        int chenhLechHoanTac = -buoc.chenhLechSoLuong;
        if (chenhLechHoanTac == 0) return;
        if (timViTriTrongGio(buoc.ma) >= 0) {
            apDungThayDoiSoLuong(buoc.ma, chenhLechHoanTac, danhSachSanPhamCache, false);
            capNhatTrangThaiQuet("Đã hoàn tác: " + buoc.ten, MauUngDung.NAU);
            return;
        }
        if (chenhLechHoanTac > 0) {
            layHoacNapSanPham(
                    ds -> {
                        int tonKho = layTonKhoSanPham(buoc.ma, ds);
                        if (chenhLechHoanTac > tonKho) {
                            capNhatTrangThaiQuet(
                                    "Không thể hoàn tác: "
                                            + buoc.ten
                                            + " vượt tồn kho ("
                                            + tonKho
                                            + ").",
                                    MauUngDung.VAN_BAN_NGUY_HIEM);
                            return;
                        }
                        gioHang.add(
                                new GioHang.DongHang(
                                        buoc.ma, buoc.ten, buoc.donGia, chenhLechHoanTac));
                        lamMoiGioHang();
                        chonDongTheoMoHinh(gioHang.size() - 1);
                        capNhatTrangThaiQuet("Đã khôi phục dòng: " + buoc.ten, MauUngDung.NAU);
                    });
        }
    }

    @Override
    public void xuLyHienThi() {
        napDanhSachSanPhamNen(null);
        lamMoiGioHang();
        dongBoGhiChu();
        khungBoChon.dongBo();
        if (!banHang.kiemTraTrong() && !banHang.getKieuNguon().isBlank()) {
            capNhatTrangThaiQuet(
                    "Giỏ hiện tại đang giữ giao dịch "
                            + banHang.getKieuNguon()
                            + ". Hãy thanh toán hoặc xóa trước khi bán lẻ mới.",
                    MauUngDung.CAM);
        } else if (gioHang.isEmpty()) {
            datLaiTrangThaiQuetMacDinh();
            oTimKiem.setText("");
            spnSoLuong.setValue(1);
        }
    }

    private void khoiDongBarcodeUsb() {
        if (barcodeUsbServer != null) {
            nhanKetNoiUsb.setText(
                    "USB: chờ điện thoại | Ghép nối: "
                            + barcodeUsbServer.getPairingCode()
                            + " | Cổng: "
                            + barcodeUsbServer.getPort());
            return;
        }
        BarcodeUsbServer serverTam = null;
        try {
            serverTam =
                    new BarcodeUsbServer(
                            barcode ->
                                    SwingUtilities.invokeLater(
                                            () -> xuLyBarcodeTuDienThoai(barcode)));
            serverTam.start();
            barcodeUsbServer = serverTam;
            nhanKetNoiUsb.setForeground(MauUngDung.NAU);
            nhanKetNoiUsb.setText(
                    "USB Scanner sẵn sàng | Ghép nối: "
                            + serverTam.getPairingCode()
                            + " | Cổng: "
                            + serverTam.getPort());
        } catch (Exception loi) {
            if (serverTam != null) serverTam.close();
            barcodeUsbServer = null;
            nhanKetNoiUsb.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
            nhanKetNoiUsb.setText("USB Scanner chưa khởi động: " + loi.getMessage());
        }
    }

    private void dongBarcodeUsbServer() {
        if (barcodeUsbServer == null) return;
        barcodeUsbServer.close();
        barcodeUsbServer = null;
    }

    private void xuLyBarcodeTuDienThoai(String barcode) {
        if (!SanPhamService.laBarcodeHopLe(barcode)) {
            capNhatTrangThaiQuet(
                    "Mã không đúng định dạng EAN-13: " + barcode, MauUngDung.VAN_BAN_NGUY_HIEM);
            return;
        }
        if (!isShowing()) {
            capNhatTrangThaiQuet(
                    "Đã nhận " + barcode + " nhưng chưa mở màn hình Lập HĐ.",
                    MauUngDung.VAN_BAN_NGUY_HIEM);
            return;
        }
        PhienDangNhap phienXuLy = PhienDangNhapService.macDinh().layPhienHienTai();
        if (phienXuLy == null) {
            capNhatTrangThaiQuet("Phiên đăng nhập đã kết thúc.", MauUngDung.VAN_BAN_NGUY_HIEM);
            return;
        }
        capNhatTrangThaiQuet("Đã nhận barcode " + barcode + ". Đang xử lý...", MauUngDung.NAU);
        oTimKiem.setText(barcode);
        themTheoQuet();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        banHang.themBoLangNghe(boLangNgheGioHang);
    }

    @Override
    public void removeNotify() {
        if (henGioDatLaiTrangThai != null) henGioDatLaiTrangThai.stop();
        banHang.xoaBoLangNghe(boLangNgheGioHang);
        super.removeNotify();
    }

    // 7. TƯƠNG TÁC CHUỘT & RENDERER BẢNG

    private void capNhatTrangThaiQuet(String noiDung, Color mau) {
        nhanTrangThaiQuet.setText(noiDung);
        nhanTrangThaiQuet.setForeground(mau);
        nhanTrangThaiQuet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        if (henGioDatLaiTrangThai != null) henGioDatLaiTrangThai.stop();
        if (TRANG_THAI_MAC_DINH.equals(noiDung)) return;
        if (henGioDatLaiTrangThai == null) {
            henGioDatLaiTrangThai = new Timer(3500, e -> datLaiTrangThaiQuetMacDinh());
            henGioDatLaiTrangThai.setRepeats(false);
        }
        henGioDatLaiTrangThai.restart();
    }

    private void datLaiTrangThaiQuetMacDinh() {
        if (henGioDatLaiTrangThai != null) henGioDatLaiTrangThai.stop();
        nhanTrangThaiQuet.setText(TRANG_THAI_MAC_DINH);
        nhanTrangThaiQuet.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanTrangThaiQuet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
    }

    private void dangKyTuongTacChuotBang() {
        bangChiTiet.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (!SwingUtilities.isLeftMouseButton(e)) return;
                        xuLyNhanChuotBang(e);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        capNhatTrangThaiReChuot(-1, VungO.KHAC, Cursor.DEFAULT_CURSOR);
                    }
                });
        bangChiTiet.addMouseMotionListener(
                new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        xuLyDiChuyenChuotBang(e);
                    }
                });
    }

    private void xuLyNhanChuotBang(MouseEvent e) {
        int dongHienThi = bangChiTiet.rowAtPoint(e.getPoint());
        int cotHienThi = bangChiTiet.columnAtPoint(e.getPoint());
        if (dongHienThi < 0 || cotHienThi < 0) return;
        VungO vung = xacDinhVungO(dongHienThi, cotHienThi, e.getX());
        if (vung == VungO.KHAC || vung == VungO.NHAP) return;
        int dongMoHinh = layDongMoHinh(dongHienThi);
        if (dongMoHinh < 0 || dongMoHinh >= gioHang.size()) return;
        String maSanPham = gioHang.get(dongMoHinh).getMa();
        if (bangChiTiet.isEditing() && bangChiTiet.getCellEditor() != null) {
            if (vung == VungO.XOA && layDongMoHinh(bangChiTiet.getEditingRow()) == dongMoHinh) {
                bangChiTiet.getCellEditor().cancelCellEditing();
            } else {
                bangChiTiet.getCellEditor().stopCellEditing();
            }
        }
        if (vung == VungO.GIAM) {
            thayDoiSoLuongTheoMa(maSanPham, -1);
        } else if (vung == VungO.TANG) {
            thayDoiSoLuongTheoMa(maSanPham, 1);
        } else if (vung == VungO.XOA) {
            xoaDongTheoMa(maSanPham);
        }
    }

    private void xuLyDiChuyenChuotBang(MouseEvent e) {
        int dongHienThi = bangChiTiet.rowAtPoint(e.getPoint());
        int cotHienThi = bangChiTiet.columnAtPoint(e.getPoint());
        VungO vung =
                (dongHienThi >= 0 && cotHienThi >= 0)
                        ? xacDinhVungO(dongHienThi, cotHienThi, e.getX())
                        : VungO.KHAC;
        int conTro =
                switch (vung) {
                    case GIAM, TANG, XOA -> Cursor.HAND_CURSOR;
                    case NHAP -> Cursor.TEXT_CURSOR;
                    case KHAC -> Cursor.DEFAULT_CURSOR;
                };
        capNhatTrangThaiReChuot(dongHienThi, vung, conTro);
    }

    private void capNhatTrangThaiReChuot(int dongMoi, VungO vungMoi, int conTro) {
        bangChiTiet.setCursor(Cursor.getPredefinedCursor(conTro));
        if (dongDiChuot == dongMoi && vungDiChuot == vungMoi) return;
        int dongCu = dongDiChuot;
        dongDiChuot = dongMoi;
        vungDiChuot = vungMoi;
        veLaiDongBang(dongCu);
        veLaiDongBang(dongMoi);
    }

    private void veLaiDongBang(int dong) {
        if (dong < 0 || dong >= bangChiTiet.getRowCount()) return;
        Rectangle vung = bangChiTiet.getCellRect(dong, 0, true);
        vung.width = bangChiTiet.getWidth();
        bangChiTiet.repaint(vung);
    }

    private VungO xacDinhVungO(int dongHienThi, int cotHienThi, int xChuot) {
        int cotMoHinh = bangChiTiet.convertColumnIndexToModel(cotHienThi);
        Rectangle khungO = bangChiTiet.getCellRect(dongHienThi, cotHienThi, false);
        if (cotMoHinh == 6) {
            int tamX = khungO.x + khungO.width / 2;
            return Math.abs(xChuot - tamX) <= RONG_NUT_XOA / 2 + 6 ? VungO.XOA : VungO.KHAC;
        }
        if (cotMoHinh != 4) return VungO.KHAC;
        int xTrongO = xChuot - khungO.x;
        if (xTrongO <= LE_NGANG_SO_LUONG + RONG_NUT_SO_LUONG + 2) return VungO.GIAM;
        if (xTrongO >= khungO.width - LE_NGANG_SO_LUONG - RONG_NUT_SO_LUONG - 2) {
            return VungO.TANG;
        }
        return VungO.NHAP;
    }

    private static Color mauNenDongBang(JTable bang, int dong, boolean daChon) {
        if (daChon) return bang.getSelectionBackground();
        return dong % 2 == 0 ? MauUngDung.THE_NEN : MauUngDung.DONG_XEN_KE;
    }

    private static final class KhungOQuanLySoLuong extends JPanel {
        final JButton btnGiam = taoNutO("−");
        final JTextField oNhap = new JTextField();
        final JButton btnTang = taoNutO("+");

        private KhungOQuanLySoLuong(boolean choNhap) {
            setLayout(new BorderLayout(3, 0));
            setOpaque(true);
            setBorder(new EmptyBorder(6, LE_NGANG_SO_LUONG, 6, LE_NGANG_SO_LUONG));
            oNhap.setHorizontalAlignment(SwingConstants.CENTER);
            oNhap.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
            oNhap.setMargin(new Insets(0, 2, 0, 2));
            oNhap.setEditable(choNhap);
            oNhap.setFocusable(choNhap);
            add(btnGiam, BorderLayout.WEST);
            add(oNhap, BorderLayout.CENTER);
            add(btnTang, BorderLayout.EAST);
        }

        private static JButton taoNutO(String kyHieu) {
            JButton nut = new JButton(kyHieu);
            nut.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
            nut.setPreferredSize(new Dimension(RONG_NUT_SO_LUONG, 26));
            nut.setMargin(new Insets(0, 0, 0, 0));
            nut.setFocusPainted(false);
            nut.setContentAreaFilled(true);
            nut.setOpaque(true);
            nut.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
            return nut;
        }

        private void capNhatHienThi(
                Color nenDong, int soLuong, boolean dangSua, boolean reGiam, boolean reTang) {
            setBackground(nenDong);
            oNhap.setBackground(Color.WHITE);
            oNhap.setForeground(MauUngDung.VAN_BAN_TOI);
            oNhap.setBorder(
                    BorderFactory.createLineBorder(
                            dangSua ? MauUngDung.DO_THUONG_HIEU : MauUngDung.VIEN, 1));
            boolean coTheGiam = soLuong > 1;
            btnGiam.setBackground(
                    coTheGiam
                            ? (reGiam ? MauUngDung.DO_NHAT : Color.WHITE)
                            : MauUngDung.DONG_XEN_KE);
            btnGiam.setForeground(coTheGiam ? MauUngDung.VAN_BAN_TOI : MauUngDung.VO_HIEU_HOA);
            btnTang.setBackground(reTang ? MauUngDung.DO_NHAT : Color.WHITE);
            btnTang.setForeground(MauUngDung.DO_THUONG_HIEU);
        }
    }

    private final class TrinhVeSoLuongCell implements TableCellRenderer {
        private final KhungOQuanLySoLuong khung = new KhungOQuanLySoLuong(false);

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int col) {
            int soLuong = value instanceof Number so ? so.intValue() : 1;
            khung.oNhap.setText(String.valueOf(soLuong));
            boolean reGiam = row == dongDiChuot && vungDiChuot == VungO.GIAM;
            boolean reTang = row == dongDiChuot && vungDiChuot == VungO.TANG;
            khung.capNhatHienThi(
                    mauNenDongBang(table, row, isSelected), soLuong, false, reGiam, reTang);
            return khung;
        }
    }

    private final class TrinhSuaSoLuongCell extends AbstractCellEditor implements TableCellEditor {
        private final KhungOQuanLySoLuong khung = new KhungOQuanLySoLuong(true);
        private int dongMoHinhDangSua = -1;
        private int soLuongTruoc = 1;
        private boolean dangLuuChinhSua = false;

        private TrinhSuaSoLuongCell() {
            khung.oNhap.addActionListener(e -> stopCellEditing());
            khung.btnGiam.addActionListener(e -> thayDoiTuNutTrongEditor(-1));
            khung.btnTang.addActionListener(e -> thayDoiTuNutTrongEditor(1));
        }

        private void thayDoiTuNutTrongEditor(int chenhLech) {
            int hienTai = soLuongTruoc;
            try {
                int daNhap = Integer.parseInt(khung.oNhap.getText().trim());
                if (daNhap >= 1) hienTai = daNhap;
            } catch (NumberFormatException ignored) {
                hienTai = soLuongTruoc;
            }
            int moi = Math.max(1, hienTai + chenhLech);
            khung.oNhap.setText(String.valueOf(moi));
            stopCellEditing();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent me) {
                if (me.getClickCount() < 1) return false;
                int vRow = bangChiTiet.rowAtPoint(me.getPoint());
                int vCol = bangChiTiet.columnAtPoint(me.getPoint());
                return vRow >= 0 && vCol >= 0 && xacDinhVungO(vRow, vCol, me.getX()) == VungO.NHAP;
            }
            return true;
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            dongMoHinhDangSua = layDongMoHinh(row);
            soLuongTruoc = value instanceof Number so ? so.intValue() : 1;
            khung.oNhap.setText(String.valueOf(soLuongTruoc));
            khung.capNhatHienThi(
                    mauNenDongBang(table, row, true), soLuongTruoc, true, false, false);
            SwingUtilities.invokeLater(
                    () -> {
                        khung.oNhap.requestFocusInWindow();
                        khung.oNhap.selectAll();
                    });
            return khung;
        }

        @Override
        public Object getCellEditorValue() {
            return soLuongTruoc;
        }

        @Override
        public boolean stopCellEditing() {
            if (dangLuuChinhSua) return true;
            dangLuuChinhSua = true;
            try {
                int dongCanCapNhat = dongMoHinhDangSua;
                String chuoiMoi = khung.oNhap.getText().trim();
                boolean ok = super.stopCellEditing();
                if (ok && dongCanCapNhat >= 0) {
                    xuLyNhapSoLuongTheoModel(dongCanCapNhat, chuoiMoi);
                }
                return ok;
            } finally {
                dangLuuChinhSua = false;
            }
        }
    }

    private final class TrinhVeNutXoaCell extends JPanel implements TableCellRenderer {
        private final JLabel nhanXoa = new JLabel("×", SwingConstants.CENTER);

        private TrinhVeNutXoaCell() {
            setLayout(new GridLayout(1, 1));
            setOpaque(true);
            setBorder(new EmptyBorder(6, 9, 6, 9));
            nhanXoa.setOpaque(true);
            nhanXoa.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
            nhanXoa.setPreferredSize(new Dimension(RONG_NUT_XOA, 26));
            nhanXoa.setToolTipText("Xóa sản phẩm khỏi đơn");
            add(nhanXoa);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int col) {
            setBackground(mauNenDongBang(table, row, isSelected));
            boolean reChuot = row == dongDiChuot && vungDiChuot == VungO.XOA;
            nhanXoa.setBackground(reChuot ? MauUngDung.DO_NHAT : Color.WHITE);
            nhanXoa.setForeground(
                    reChuot ? MauUngDung.DO_THUONG_HIEU : MauUngDung.VAN_BAN_NGUY_HIEM);
            nhanXoa.setBorder(
                    BorderFactory.createLineBorder(
                            reChuot ? MauUngDung.DO_THUONG_HIEU : MauUngDung.VIEN));
            return this;
        }
    }
}
