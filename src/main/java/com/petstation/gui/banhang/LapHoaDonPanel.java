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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// Màn hình: Lập hóa đơn bán hàng (Mã: LAP_HOA_DON)
// Luồng xử lý: GUI -> BanHangService, GioHangService -> HoaDonDAOImpl -> SQL Server (hoa_don,
// chi_tiet_hoa_don)
// Mô tả: Giao diện POS bán hàng chuyên nghiệp, quét barcode/nhập mã SP, bảng giỏ hàng toàn màn
// hình, tính tiền và in bill
// Phân hệ: banhang
public class LapHoaDonPanel extends CoSoPanel {
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
    private final Deque<String> lichSuThem = new ArrayDeque<>();
    private int luotTaiSanPham = 0;

    // Component quét mã vạch
    private JTextField oTimKiem;
    private final JSpinner spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JLabel nhanTrangThaiQuet = new JLabel("Sẵn sàng quét mã vạch.");
    private final JLabel nhanKetNoiUsb = new JLabel("USB: chưa khởi tạo.");
    private BarcodeUsbServer barcodeUsbServer;

    // Bảng giỏ hàng POS
    private DefaultTableModel moHinhBang;
    private JTable bangChiTiet;
    private final JLabel nhanSoLuongMatHang = new JLabel("0 mặt hàng · 0 sản phẩm");

    // Cột phải: Khách hàng & Thanh toán
    private final BoChonKhachHang khungBoChon;
    private final JLabel nhanNhanTamTinh = new JLabel("0₫");
    private final JLabel nhanNhanKhuyenMai = new JLabel("—");
    private final JLabel nhanNhanGiamGia = new JLabel("0₫");
    private final JLabel nhanNhanTong = new JLabel("0₫");

    private final NutDinhDang btnNutThanhToan =
            new NutDinhDang("TIẾN HÀNH THANH TOÁN (F9)", NutDinhDang.KieuNut.CHINH);
    private final NutDinhDang btnNutTreo =
            new NutDinhDang("Lưu đơn nháp (Ctrl+S)", NutDinhDang.KieuNut.PHU);
    private final NutDinhDang btnNutLay =
            new NutDinhDang("Mở đơn nháp (F8)", NutDinhDang.KieuNut.TRONG_SUOT);

    public LapHoaDonPanel(GioHang banHang, Consumer<MaManHinh> boDieuHuong) {
        this.banHang = banHang;
        this.boDieuHuong = boDieuHuong;
        this.khungBoChon = new BoChonKhachHang(banHang, this::capNhatCacTong);
        dangKyLangNgheGioHang();
        setBorder(new EmptyBorder(14, 16, 14, 16));
        setLayout(new BorderLayout(16, 0));
        khoiTaoBangVaTimKiemPos();
        JPanel khungTrai = taoVungTrongSuot(new BorderLayout(0, 12));
        khungTrai.add(taoKhuVucQuet(), BorderLayout.NORTH);
        khungTrai.add(taoBangGioHang(), BorderLayout.CENTER);
        add(khungTrai, BorderLayout.CENTER);
        BoGocPanel khungPhai = taoKhungThanhToan();
        khungPhai.setPreferredSize(new Dimension(390, 0));
        khungPhai.setMinimumSize(new Dimension(340, 0));
        add(khungPhai, BorderLayout.EAST);
        lamMoiGioHang();
        dangKyPhimTatVaBarcodePos();
        napDanhSachSanPhamNen(null);
    }

    private void napDanhSachSanPhamNen(Consumer<List<SanPham>> khiXong) {
        int luot = ++luotTaiSanPham;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTaiSanPham,
                LapHoaDonPanel::laySanPham,
                ds -> {
                    if (khiXong != null) khiXong.accept(ds);
                },
                loi ->
                        capNhatTrangThaiQuet(
                                "Lỗi tải sản phẩm: " + loi.getMessage(),
                                MauUngDung.VAN_BAN_NGUY_HIEM));
    }

    private void dangKyLangNgheGioHang() {
        this.boLangNgheGioHang =
                gio -> {
                    if (Objects.equals(this.gioHang, gio.getCacDong())) return;
                    Runnable dongBo =
                            () -> {
                                this.gioHang.clear();
                                this.gioHang.addAll(gio.getCacDong());
                                this.khungBoChon.dongBo();
                                this.lamMoiGioHang();
                            };
                    if (SwingUtilities.isEventDispatchThread()) {
                        dongBo.run();
                    } else {
                        SwingUtilities.invokeLater(dongBo);
                    }
                };
        this.banHang.themBoLangNghe(this.boLangNgheGioHang);
    }

    private void khoiTaoBangVaTimKiemPos() {
        oTimKiem = new JTextField();
        oTimKiem.setPreferredSize(new Dimension(0, 42));
        oTimKiem.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        oTimKiem.setToolTipText("Quét mã vạch EAN-13, nhập mã SP hoặc tên rồi nhấn Enter.");
        oTimKiem.addActionListener(suKien -> themTheoQuet());
        String[] tieuDeCot = {
            "STT", "Mã SP / Barcode", "Tên hàng hóa / Dịch vụ", "Đơn giá", "Số lượng", "Thành tiền"
        };
        moHinhBang =
                new DefaultTableModel(tieuDeCot, 0) {
                    @Override
                    public boolean isCellEditable(int row, int col) {
                        return false;
                    }
                };
        bangChiTiet = new JTable(moHinhBang);
        DinhDangBang.apDung(bangChiTiet);
        thietLapCotBangChiTiet();
    }

    private void dangKyPhimTatVaBarcodePos() {
        dangKyPhimTat("F9", "pay", suKien -> quyetToan());
        dangKyPhimTat("control S", "hold", suKien -> treoGioHangHienTai());
        dangKyPhimTat("F8", "retrieve", suKien -> hienThiTreoCacGioHang());
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
        bangChiTiet.getColumnModel().getColumn(0).setPreferredWidth(45);
        bangChiTiet.getColumnModel().getColumn(1).setPreferredWidth(130);
        bangChiTiet.getColumnModel().getColumn(2).setPreferredWidth(260);
        bangChiTiet.getColumnModel().getColumn(3).setPreferredWidth(110);
        bangChiTiet.getColumnModel().getColumn(4).setPreferredWidth(80);
        bangChiTiet.getColumnModel().getColumn(5).setPreferredWidth(120);
        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);
        bangChiTiet.getColumnModel().getColumn(0).setCellRenderer(canGiua);
        bangChiTiet.getColumnModel().getColumn(4).setCellRenderer(canGiua);
        DefaultTableCellRenderer canPhai = new DefaultTableCellRenderer();
        canPhai.setHorizontalAlignment(SwingConstants.RIGHT);
        bangChiTiet.getColumnModel().getColumn(3).setCellRenderer(canPhai);
        bangChiTiet.getColumnModel().getColumn(5).setCellRenderer(canPhai);
    }

    private JPanel taoKhuVucQuet() {
        BoGocPanel panel = layThe(new BorderLayout(0, 8));
        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.DO_THUONG_HIEU, 2, true),
                        new EmptyBorder(10, 14, 10, 14)));
        JLabel tieuDe = new JLabel("QUÉT MÃ VẠCH SẢN PHẨM (POS)");
        tieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        tieuDe.setForeground(MauUngDung.DO_THUONG_HIEU);
        JPanel dongNhap = taoVungTrongSuot(new BorderLayout(8, 0));
        dongNhap.add(oTimKiem, BorderLayout.CENTER);
        JPanel khungSoLuongVaNut = taoVungTrongSuot(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JLabel nhanSoLuongNgan = new JLabel("SL:");
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
        nhanTrangThaiQuet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanTrangThaiQuet.setForeground(MauUngDung.XANH_LA);
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

        // Thanh thao tác nhanh cho bảng giỏ hàng
        JPanel khungThaoTacBang = taoVungTrongSuot(new FlowLayout(FlowLayout.LEFT, 8, 4));
        NutDinhDang btnTang = new NutDinhDang("+ Tăng SL", NutDinhDang.KieuNut.PHU);
        btnTang.addActionListener(e -> thayDoiSoLuongDongChon(1));
        NutDinhDang btnGiam = new NutDinhDang("− Giảm SL", NutDinhDang.KieuNut.PHU);
        btnGiam.addActionListener(e -> thayDoiSoLuongDongChon(-1));
        NutDinhDang btnXoaDong = new NutDinhDang("Xóa dòng chọn", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXoaDong.addActionListener(e -> xoaDongDangChon());
        NutDinhDang btnXoaHet =
                new NutDinhDang("Xóa toàn bộ giỏ (Alt+X)", NutDinhDang.KieuNut.NGUY_HIEM);
        btnXoaHet.addActionListener(e -> xoaGioHang(true));
        khungThaoTacBang.add(btnTang);
        khungThaoTacBang.add(btnGiam);
        khungThaoTacBang.add(btnXoaDong);
        khungThaoTacBang.add(Box.createHorizontalStrut(16));
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
        khungTongHop.add(tinhTongDong("Khuyến mãi tự áp", nhanNhanKhuyenMai, false));
        khungTongHop.add(Box.createVerticalStrut(6));
        khungTongHop.add(tinhTongDong("Giảm giá (KM + điểm)", nhanNhanGiamGia, false));
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
        khungPhai.add(khungTongHop, BorderLayout.CENTER);

        // Cụm nút điều khiển ở đáy
        JPanel khungDay = taoVungTrongSuot(new BorderLayout(0, 8));
        JPanel khungNutNho = taoVungTrongSuot(new GridLayout(1, 2, 8, 0));
        btnNutTreo.setPreferredSize(new Dimension(0, 36));
        btnNutTreo.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        btnNutTreo.addActionListener(e -> treoGioHangHienTai());
        btnNutLay.setPreferredSize(new Dimension(0, 36));
        btnNutLay.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
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
        napDanhSachSanPhamNen(ds -> timVaThemSanPham(tuKhoa, soLuong, ds));
    }

    private void timVaThemSanPham(String tuKhoa, int soLuong, List<SanPham> ds) {
        // 1. Tìm chính xác theo Barcode hoặc Mã SP
        List<SanPham> timThay =
                ds.stream()
                        .filter(
                                sp ->
                                        sp.barcode.equalsIgnoreCase(tuKhoa)
                                                || sp.ma.equalsIgnoreCase(tuKhoa))
                        .toList();

        // 2. Tìm theo 6 số cuối barcode
        if (timThay.isEmpty() && tuKhoa.matches("\\d{6}")) {
            timThay =
                    ds.stream()
                            .filter(
                                    sp ->
                                            (!sp.barcode.isBlank() && sp.barcode.endsWith(tuKhoa))
                                                    || sp.ma.endsWith(tuKhoa))
                            .toList();
        }

        // 3. Tìm theo tên sản phẩm
        if (timThay.isEmpty()) {
            String tuKhoaTen = tuKhoa.toLowerCase(Locale.ROOT);
            timThay =
                    ds.stream()
                            .filter(sp -> sp.ten.toLowerCase(Locale.ROOT).contains(tuKhoaTen))
                            .toList();
        }
        if (timThay.isEmpty()) {
            capNhatTrangThaiQuet(
                    "Không tìm thấy sản phẩm: " + tuKhoa, MauUngDung.VAN_BAN_NGUY_HIEM);
            HopThoai.hienThiCanhBao(
                    this, "Không tìm thấy sản phẩm với mã, barcode hoặc tên: \"" + tuKhoa + "\".");
            return;
        }

        // Nếu có nhiều sản phẩm phù hợp, hiển thị popup menu gợi ý để chọn nhanh
        if (timThay.size() > 1) {
            hienThiPopupGoiY(timThay, soLuong);
            return;
        }
        SanPham sanPham = timThay.get(0);
        themSanPhamVaoGio(sanPham, soLuong);
    }

    private void hienThiPopupGoiY(List<SanPham> danhSach, int soLuong) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(MauUngDung.DO_THUONG_HIEU, 1));
        for (int i = 0; i < Math.min(10, danhSach.size()); i++) {
            SanPham sp = danhSach.get(i);
            String label =
                    String.format(
                            "%s - %s [%s] - Tồn: %d",
                            sp.ma, sp.ten, DinhDang.dinhDang(sp.gia), sp.tonKho);
            JMenuItem item = new JMenuItem(label);
            item.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            item.addActionListener(e -> themSanPhamVaoGio(sp, soLuong));
            popup.add(item);
        }
        popup.show(oTimKiem, 0, oTimKiem.getHeight());
        capNhatTrangThaiQuet(
                "Có " + danhSach.size() + " sản phẩm phù hợp. Hãy chọn từ danh sách.",
                MauUngDung.CAM);
    }

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
            lichSuThem.push(sanPham.ma);
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
        lichSuThem.push(sanPham.ma);
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

    private void thayDoiSoLuongDongChon(int chenhLech) {
        int dongChon = bangChiTiet.getSelectedRow();
        if (dongChon < 0 || dongChon >= gioHang.size()) return;
        String maSanPham = gioHang.get(dongChon).getMa();
        napDanhSachSanPhamNen(ds -> apDungThayDoiSoLuong(dongChon, maSanPham, chenhLech, ds));
    }

    private void apDungThayDoiSoLuong(
            int dongChon, String maSanPham, int chenhLech, List<SanPham> ds) {
        if (dongChon < 0 || dongChon >= gioHang.size()) return;
        GioHang.DongHang dong = gioHang.get(dongChon);
        if (!dong.getMa().equals(maSanPham)) return;
        int tonKho =
                ds.stream()
                        .filter(sanPham -> sanPham.ma.equals(dong.getMa()))
                        .mapToInt(sp -> sp.tonKho)
                        .findFirst()
                        .orElse(Integer.MAX_VALUE);
        int slMoi = dong.getSoLuong() + chenhLech;
        if (slMoi > tonKho) {
            HopThoai.hienThiCanhBao(this, "Vượt quá tồn kho (" + tonKho + ").");
            return;
        }
        if (slMoi <= 0) {
            gioHang.remove(dongChon);
        } else {
            gioHang.set(
                    dongChon,
                    new GioHang.DongHang(dong.getMa(), dong.getTen(), dong.getDonGia(), slMoi));
        }
        lamMoiGioHang();
        if (!gioHang.isEmpty()) {
            int rowToSelect = Math.min(dongChon, gioHang.size() - 1);
            bangChiTiet.setRowSelectionInterval(rowToSelect, rowToSelect);
        }
    }

    private void xoaDongDangChon() {
        int dongChon = bangChiTiet.getSelectedRow();
        if (dongChon < 0 || dongChon >= gioHang.size()) return;
        gioHang.remove(dongChon);
        lamMoiGioHang();
    }

    private void lamMoiGioHang() {
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
                        DinhDang.dinhDang(dong.getDonGia() * dong.getSoLuong())
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
        capNhatTrangThaiQuet("Đã xóa sạch giỏ hàng.", MauUngDung.VAN_BAN_XAM);
        lamMoiGioHang();
    }

    private void quyetToan() {
        if (gioHang.isEmpty()) {
            HopThoai.hienThiCanhBao(this, "Giỏ hàng đang trống.");
            return;
        }
        capNhatCacTong();
        boDieuHuong.accept(MaManHinh.THANH_TOAN);
    }

    private void treoGioHangHienTai() {
        if (gioHang.isEmpty()) {
            HopThoai.hienThiCanhBao(this, "Giỏ hàng trống.");
            return;
        }
        btnNutTreo.setEnabled(false);
        banHang.setCacDong(gioHang);
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> new BanHangService().treo(banHang),
                () -> {
                    btnNutTreo.setEnabled(true);
                    xoaGioHang(false);
                    HopThoai.hienThiThanhCong(this, "Đã lưu đơn treo vào SQL Server.");
                },
                ngoaiLe -> {
                    btnNutTreo.setEnabled(true);
                    HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
                });
    }

    private void hienThiTreoCacGioHang() {
        boDieuHuong.accept(MaManHinh.HOA_DON_CHO);
    }

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
        lichSuThem.pop();
        thayDoiSoLuongDongChon(-1);
    }

    @Override
    public void xuLyHienThi() {
        napDanhSachSanPhamNen(null);
        lamMoiGioHang();
        khungBoChon.dongBo();
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

    private Consumer<GioHang> boLangNgheGioHang;

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

    private void capNhatTrangThaiQuet(String noiDung, Color mau) {
        nhanTrangThaiQuet.setText(noiDung);
        nhanTrangThaiQuet.setForeground(mau);
    }
}
