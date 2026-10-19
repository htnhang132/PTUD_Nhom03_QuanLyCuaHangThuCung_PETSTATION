package com.petstation.gui.processing.sales;

import com.petstation.gui.components.BoGocPanel;
import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.KhaNangThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.BoChonKhachHang;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.GioHang;
import com.petstation.service.BanHangService;
import com.petstation.util.TienTe;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Cung cấp giao diện và xử lý tương tác cho bán hàng vùng hiển thị.
 *
 * <p>Lập đơn hàng POS: tìm/quét sản phẩm, giỏ hàng chỉnh số lượng, khách hàng lookup, treo đơn,
 * giảm giá linh hoạt, chuyển sang thanh toán.
 */
public class LapHoaDonPanel extends CoSoPanel {

    /**
     * Cung cấp giao diện và xử lý tương tác cho sản phẩm.
     *
     * @param ma mã
     * @param ten tên
     * @param nhomHang nhóm hàng
     * @param gia giá
     * @param tonKho tồn kho
     */
    private record SanPham(
            String ma, String ten, String nhomHang, long gia, int tonKho, String anh) {}

    /**
     * Lấy sản phẩm trong LapHoaDonPanel.
     *
     * <p>Sản phẩm đang kinh doanh lấy từ Danh mục (giá và tồn luôn mới nhất).
     *
     * @return kết quả lấy sản phẩm
     */
    private static List<SanPham> laySanPham() {
        // Ra.
        List<SanPham> ra = new ArrayList<>();
        for (Object[] dongDuLieu : BanHangService.layCoTheBanCacSanPham()) {
            ra.add(
                    new SanPham(
                            String.valueOf(dongDuLieu[0]),
                            String.valueOf(dongDuLieu[1]),
                            String.valueOf(dongDuLieu[2]),
                            ((Number) dongDuLieu[3]).longValue(),
                            ((Number) dongDuLieu[4]).intValue(),
                            String.valueOf(dongDuLieu[5])));
        }
        return ra;
    }

    /** Các nhóm hàng. */
    private static final String[] CAC_NHOM_HANG = {
        "Tất cả", "Thức ăn", "Phụ kiện", "Grooming", "Vệ sinh", "Thuốc"
    };

    /** Bán hàng. */
    private final GioHang banHang;

    /** Bộ điều hướng. */
    private final Consumer<MaManHinh> boDieuHuong;

    /** Lưới. */
    private final JPanel pnlLuoi = new JPanel(new GridLayout(0, 3, 12, 12));

    /** Giỏ hàng các dòng. */
    private final JPanel pnlGioHangCacDong = new JPanel();

    /** Nhãn tạm tính. */
    private final JLabel lblNhanTamTinh = new JLabel();

    /** Nhãn giảm giá. */
    private final JLabel lblNhanGiamGia = new JLabel();

    /** Nhãn tổng. */
    private final JLabel lblNhanTong = new JLabel();

    /** Trống giỏ hàng. */
    private final JLabel lblTrongGioHang =
            new JLabel("Chưa có sản phẩm. Bấm vào thẻ sản phẩm hoặc quét mã.");

    /** Tìm kiếm. */
    private final JTextField txtTimKiem;

    /** Bộ chọn. */
    private final BoChonKhachHang pnlBoChon;

    /** Nhãn khuyến mãi. */
    private final JLabel lblNhanKhuyenMai = new JLabel("—");

    /** Nút thanh toán. */
    private final NutDinhDang btnNutThanhToan =
            new NutDinhDang("Thanh toán (F9)", NutDinhDang.KieuNut.CHINH);

    /** Nút treo. */
    private final NutDinhDang btnNutTreo =
            new NutDinhDang("Lưu đơn nháp (F7)", NutDinhDang.KieuNut.PHU);

    /** Nút lấy. */
    private final NutDinhDang btnNutLay =
            new NutDinhDang("Mở đơn nháp (F8)", NutDinhDang.KieuNut.TRONG_SUOT);

    /** Giỏ hàng. */
    private final List<GioHang.DongHang> gioHang = new ArrayList<>();

    /** Lịch sử thêm. */
    private final Deque<String> lichSuThem = new ArrayDeque<>();

    /** Nhóm hàng. */
    private String nhomHang = "Tất cả";

    /**
     * Khởi tạo LapHoaDonPanel với các giá trị được cung cấp.
     *
     * @param banHang bán hàng
     * @param boDieuHuong bộ điều hướng
     */
    public LapHoaDonPanel(GioHang banHang, Consumer<MaManHinh> boDieuHuong) {
        this.banHang = banHang;
        this.boDieuHuong = boDieuHuong;
        this.pnlBoChon = new BoChonKhachHang(banHang, this::capNhatCacTong);

        setBorder(new EmptyBorder(14, 16, 14, 16));
        setLayout(new BorderLayout(16, 0));
        pnlLuoi.addComponentListener(
                new java.awt.event.ComponentAdapter() {
                    @Override
                    public void componentResized(java.awt.event.ComponentEvent e) {
                        int cot = Math.max(1, Math.min(4, pnlLuoi.getWidth() / 200));
                        GridLayout luoi = (GridLayout) pnlLuoi.getLayout();
                        if (luoi.getColumns() != cot) {
                            luoi.setColumns(cot);
                            pnlLuoi.revalidate();
                        }
                    }
                });

        txtTimKiem = taoOTimKiem("Quét mã vạch hoặc gõ tên / mã sản phẩm, rồi Enter");
        txtTimKiem.setPreferredSize(new Dimension(0, 40));
        txtTimKiem
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            /**
                             * Thêm cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void insertUpdate(javax.swing.event.DocumentEvent suKien) {
                                dungLaiLuoi();
                            }

                            /**
                             * Xóa cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void removeUpdate(javax.swing.event.DocumentEvent suKien) {
                                dungLaiLuoi();
                            }

                            /**
                             * Lấy đã thay đổi cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void changedUpdate(javax.swing.event.DocumentEvent suKien) {
                                dungLaiLuoi();
                            }
                        });
        txtTimKiem.addActionListener(suKien -> themTheoQuet());

        // Trái.
        JPanel pnlTrai = taoVungTrongSuot(new BorderLayout(0, 10));
        // Trên.
        JPanel pnlTren = taoVungTrongSuot(new BorderLayout(0, 8));
        pnlTren.add(txtTimKiem, BorderLayout.NORTH);
        pnlTren.add(layNhomHangCacNhan(), BorderLayout.SOUTH);
        pnlTrai.add(pnlTren, BorderLayout.NORTH);
        pnlLuoi.setOpaque(false);
        // Lưới vùng chứa.
        JPanel pnlLuoiVungChua = taoVungTrongSuot(new BorderLayout());
        pnlLuoiVungChua.add(pnlLuoi, BorderLayout.NORTH);
        // Lưới cuộn.
        JScrollPane scrLuoiCuon = new JScrollPane(pnlLuoiVungChua);
        scrLuoiCuon.setBorder(null);
        scrLuoiCuon.setOpaque(false);
        scrLuoiCuon.getViewport().setOpaque(false);
        scrLuoiCuon.getVerticalScrollBar().setUnitIncrement(18);
        pnlTrai.add(scrLuoiCuon, BorderLayout.CENTER);

        add(pnlTrai, BorderLayout.CENTER);
        add(taoGioHang(), BorderLayout.EAST);

        dungLaiLuoi();
        lamMoiGioHang();

        // Đăng ký phím tắt
        dangKyPhimTat("F9", "pay", suKien -> quyetToan());
        dangKyPhimTat("F7", "hold", suKien -> treoGioHangHienTai());
        dangKyPhimTat("F8", "retrieve", suKien -> hienThiTreoCacGioHang());
        dangKyPhimTat("Ctrl+K", "search", suKien -> txtTimKiem.requestFocusInWindow());
    }

    /**
     * Đăng ký phím tắt trong LapHoaDonPanel.
     *
     * @param khoa khóa
     * @param ten tên
     * @param thaoTac thao tác
     */
    private void dangKyPhimTat(String khoa, String ten, java.awt.event.ActionListener thaoTac) {
        // Phím.
        KeyStroke phim = KeyStroke.getKeyStroke(khoa);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(phim, ten);
        getActionMap()
                .put(
                        ten,
                        new AbstractAction() {
                            /**
                             * Lấy thao tác đã thực hiện trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent suKien) {
                                thaoTac.actionPerformed(suKien);
                            }
                        });
    }

    // ── Bên trái: lưới sản phẩm ───────────────────────────────────
    /**
     * Lấy nhóm hàng các nhãn trong LapHoaDonPanel.
     *
     * @return kết quả lấy nhóm hàng các nhãn
     */
    private JPanel layNhomHangCacNhan() {
        // Dòng.
        JPanel pnlDong = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlDong.setOpaque(false);
        // Nhóm.
        ButtonGroup nhom = new ButtonGroup();
        for (String ten : CAC_NHOM_HANG) {
            // Nhãn.
            JToggleButton btnNhan = new JToggleButton(ten);
            btnNhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            btnNhan.setFocusPainted(false);
            btnNhan.setBackground(Color.WHITE);
            btnNhan.setSelected(ten.equals(nhomHang));
            btnNhan.addActionListener(
                    suKien -> {
                        nhomHang = ten;
                        dungLaiLuoi();
                    });
            nhom.add(btnNhan);
            pnlDong.add(btnNhan);
        }
        return pnlDong;
    }

    /** Dựng lại lưới trong LapHoaDonPanel. */
    private void dungLaiLuoi() {
        pnlLuoi.removeAll();
        // Từ khóa.
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        for (SanPham vungDangXet : laySanPham()) {
            // Vào nhóm hàng.
            boolean vaoNhomHang =
                    nhomHang.equals("Tất cả") || vungDangXet.nhomHang().equals(nhomHang);
            // Khớp.
            boolean khop =
                    tuKhoa.isEmpty()
                            || vungDangXet.ten().toLowerCase().contains(tuKhoa)
                            || vungDangXet.ma().toLowerCase().contains(tuKhoa);
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (vaoNhomHang && khop) pnlLuoi.add(laySanPhamThe(vungDangXet));
        }
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (pnlLuoi.getComponentCount() == 0) {
            // Không có.
            JLabel lblKhongCo = new JLabel("Không tìm thấy sản phẩm phù hợp.");
            lblKhongCo.setForeground(MauUngDung.VAN_BAN_XAM);
            pnlLuoi.add(lblKhongCo);
        }
        while (pnlLuoi.getComponentCount() % 3 != 0 && pnlLuoi.getComponentCount() > 0)
            pnlLuoi.add(Box.createGlue());
        pnlLuoi.revalidate();
        pnlLuoi.repaint();
    }

    /**
     * Lấy sản phẩm thẻ trong LapHoaDonPanel.
     *
     * @param vungDangXet vùng đang xét
     * @return kết quả lấy sản phẩm thẻ
     */
    private JPanel laySanPhamThe(SanPham vungDangXet) {
        // Ra.
        boolean ra = vungDangXet.tonKho() <= 0;
        // Thẻ.
        BoGocPanel pnlThe = new BoGocPanel(10, MauUngDung.THE_NEN, true);
        pnlThe.setLayout(new BorderLayout(0, 4));
        pnlThe.setBorder(new EmptyBorder(12, 14, 12, 14));
        pnlThe.setPreferredSize(new Dimension(180, 310));
        pnlThe.setCursor(
                ra ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pnlThe.getAccessibleContext().setAccessibleName(vungDangXet.ten());

        // Mã.
        JLabel lblMa = new JLabel(vungDangXet.ma() + "· " + vungDangXet.nhomHang());
        lblMa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblMa.setForeground(MauUngDung.VAN_BAN_XAM);
        // Tên.
        JLabel lblTen = new JLabel("<html>" + vungDangXet.ten() + "</html>");
        lblTen.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        lblTen.setForeground(ra ? MauUngDung.VO_HIEU_HOA : MauUngDung.VAN_BAN_TOI);
        // Dưới.
        JPanel pnlDuoi = taoVungTrongSuot(new BorderLayout());
        // Giá.
        JLabel lblGia = new JLabel(TienTe.dinhDang(vungDangXet.gia()));
        lblGia.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblGia.setForeground(ra ? MauUngDung.VO_HIEU_HOA : MauUngDung.DO_THUONG_HIEU);
        // Tồn kho.
        JLabel lblTonKho =
                new JLabel(
                        ra
                                ? "Hết hàng"
                                : vungDangXet.tonKho() <= 5
                                        ? "Còn " + vungDangXet.tonKho()
                                        : "Tồn " + vungDangXet.tonKho());
        lblTonKho.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        lblTonKho.setForeground(
                ra
                        ? MauUngDung.VAN_BAN_NGUY_HIEM
                        : vungDangXet.tonKho() <= 5 ? MauUngDung.CAM : MauUngDung.VAN_BAN_XAM);
        pnlDuoi.add(lblGia, BorderLayout.WEST);
        pnlDuoi.add(lblTonKho, BorderLayout.EAST);

        var pnlAnh = new com.petstation.gui.components.common.AnhSanPhamPanel(140);
        pnlAnh.datAnh(vungDangXet.anh());
        JPanel pnlThongTin = taoVungTrongSuot(new BorderLayout(0, 4));
        pnlThongTin.add(lblMa, BorderLayout.NORTH);
        pnlThongTin.add(lblTen, BorderLayout.CENTER);
        pnlThe.add(pnlAnh, BorderLayout.NORTH);
        pnlThe.add(pnlThongTin, BorderLayout.CENTER);
        pnlThe.add(pnlDuoi, BorderLayout.SOUTH);

        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (!ra) {
            ganThaoTacThe(
                    pnlThe,
                    new MouseAdapter() {
                        /**
                         * Lấy chuột đã bấm trong lớp xử lý giao diện.
                         *
                         * @param suKien sự kiện
                         */
                        @Override
                        public void mouseClicked(MouseEvent suKien) {
                            themDenGioHang(vungDangXet);
                        }
                    });
        }
        return pnlThe;
    }

    /** Gắn cùng thao tác lên ảnh và nhãn vì sự kiện chuột Swing không nổi lên panel cha. */
    private void ganThaoTacThe(Component thanhPhan, MouseAdapter thaoTac) {
        thanhPhan.addMouseListener(thaoTac);
        thanhPhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (thanhPhan instanceof Container vung)
            for (Component con : vung.getComponents()) ganThaoTacThe(con, thaoTac);
    }

    /** Thêm theo quét trong LapHoaDonPanel. */
    private void themTheoQuet() {
        // Từ khóa.
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (tuKhoa.isEmpty()) return;
        // Tìm thấy.
        List<SanPham> timThay =
                laySanPham().stream()
                        .filter(
                                vungDangXet ->
                                        vungDangXet.ma().equalsIgnoreCase(tuKhoa)
                                                || vungDangXet.ten().toLowerCase().contains(tuKhoa))
                        .toList();
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (timThay.size() == 1) {
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (timThay.get(0).tonKho() <= 0) {
                HopThoai.hienThiThongBaoHopThoai(
                        this,
                        timThay.get(0).ten() + " đã hết hàng.",
                        "Hết hàng",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                themDenGioHang(timThay.get(0));
                txtTimKiem.setText("");
            }
        } else // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (timThay.isEmpty()) {
            HopThoai.hienThiThongBaoHopThoai(
                    this,
                    "Không có sản phẩm mã hoặc tên \"" + txtTimKiem.getText().trim() + "\".",
                    "Không tìm thấy",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // ── Bên phải: giỏ hàng ────────────────────────────────────────
    /**
     * Tạo giỏ hàng trong LapHoaDonPanel.
     *
     * @return kết quả tạo giỏ hàng
     */
    private BoGocPanel taoGioHang() {
        // Giỏ hàng vùng hiển thị.
        BoGocPanel pnlGioHangVungHienThi = layThe(new BorderLayout(0, 10));
        pnlGioHangVungHienThi.setPreferredSize(new Dimension(480, 0));

        // Tiêu đề và nút treo hoặc mở lại đơn
        // Tiêu đề.
        JPanel pnlTieuDe = new JPanel(new BorderLayout(8, 0));
        pnlTieuDe.setOpaque(false);
        pnlTieuDe.add(taoTieuDeMuc("Giỏ hàng"), BorderLayout.WEST);

        // Tiêu đề các thao tác.
        JPanel pnlTieuDeCacThaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pnlTieuDeCacThaoTac.setOpaque(false);
        btnNutTreo.setPreferredSize(new Dimension(150, 32));
        btnNutTreo.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        btnNutLay.setPreferredSize(new Dimension(130, 32));
        btnNutLay.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        pnlTieuDeCacThaoTac.add(btnNutTreo);
        pnlTieuDeCacThaoTac.add(btnNutLay);
        pnlTieuDe.add(pnlTieuDeCacThaoTac, BorderLayout.EAST);
        pnlGioHangVungHienThi.add(pnlTieuDe, BorderLayout.NORTH);

        pnlGioHangCacDong.setLayout(new BoxLayout(pnlGioHangCacDong, BoxLayout.Y_AXIS));
        pnlGioHangCacDong.setOpaque(false);
        lblTrongGioHang.setForeground(MauUngDung.VAN_BAN_XAM);
        lblTrongGioHang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        // Các dòng vùng chứa.
        JPanel pnlCacDongVungChua = new TheoChieuRongPanel();
        pnlCacDongVungChua.setOpaque(false);
        pnlCacDongVungChua.add(pnlGioHangCacDong, BorderLayout.NORTH);
        // Cuộn.
        JScrollPane scrCuon = new JScrollPane(pnlCacDongVungChua);
        scrCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrCuon.getViewport().setBackground(Color.WHITE);
        scrCuon.getVerticalScrollBar().setUnitIncrement(16);
        scrCuon.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pnlGioHangVungHienThi.add(scrCuon, BorderLayout.CENTER);

        // Vùng phía dưới
        // Dưới.
        JPanel pnlDuoi = new JPanel();
        pnlDuoi.setOpaque(false);
        pnlDuoi.setLayout(new BoxLayout(pnlDuoi, BoxLayout.Y_AXIS));
        pnlDuoi.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Khách hàng: một ô tìm duy nhất, chọn xong hiện hạng, điểm và ô dùng điểm
        pnlDuoi.add(pnlBoChon);
        pnlDuoi.add(Box.createVerticalStrut(8));

        // Các khoản tổng cộng
        pnlDuoi.add(tinhTongDong("Tạm tính", lblNhanTamTinh, false));
        pnlDuoi.add(tinhTongDong("Khuyến mãi tự áp", lblNhanKhuyenMai, false));
        pnlDuoi.add(tinhTongDong("Giảm giá (KM + điểm)", lblNhanGiamGia, false));
        pnlDuoi.add(tinhTongDong("Tổng cộng", lblNhanTong, true));
        pnlDuoi.add(Box.createVerticalStrut(10));

        // Các nút thao tác
        // Xóa nút.
        NutDinhDang btnXoaNut = new NutDinhDang("Xóa giỏ", NutDinhDang.KieuNut.NGUY_HIEM);
        btnXoaNut.addActionListener(suKien -> xoaGioHang(true));
        btnNutThanhToan.addActionListener(suKien -> quyetToan());

        // Các nút.
        JPanel pnlCacNut = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlCacNut.setOpaque(false);
        pnlCacNut.add(btnXoaNut);
        pnlCacNut.add(btnNutThanhToan);
        pnlCacNut.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCacNut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnNutThanhToan.setPreferredSize(new Dimension(150, 44));
        pnlDuoi.add(pnlCacNut);
        pnlGioHangVungHienThi.add(pnlDuoi, BorderLayout.SOUTH);

        // Đăng ký thao tác treo và mở lại giỏ hàng
        btnNutTreo.addActionListener(suKien -> treoGioHangHienTai());
        btnNutLay.addActionListener(suKien -> hienThiTreoCacGioHang());

        return pnlGioHangVungHienThi;
    }

    /**
     * Lấy nhãn nhỏ trong LapHoaDonPanel.
     *
     * @param vanBan văn bản
     * @return kết quả lấy nhãn nhỏ
     */
    private JLabel layNhanNho(String vanBan) {
        // Dòng đang xét.
        JLabel lblDongDangXet = new JLabel(vanBan);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        lblDongDangXet.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lblDongDangXet;
    }

    /**
     * Tính tổng dòng trong LapHoaDonPanel.
     *
     * @param nhan nhãn
     * @param lblGiaTri giá trị
     * @param manh mạnh
     * @return kết quả tính tổng dòng
     */
    private JPanel tinhTongDong(String nhan, JLabel lblGiaTri, boolean manh) {
        // Dòng.
        JPanel pnlDong = taoVungTrongSuot(new BorderLayout());
        // Dòng đang xét.
        JLabel lblDongDangXet = new JLabel(nhan);
        lblDongDangXet.setFont(
                MauUngDung.taoPhongChu(manh ? Font.BOLD : Font.PLAIN, manh ? 15 : 13));
        lblGiaTri.setFont(MauUngDung.taoPhongChu(Font.BOLD, manh ? 22 : 13));
        lblGiaTri.setForeground(manh ? MauUngDung.DO_THUONG_HIEU : MauUngDung.VAN_BAN_TOI);
        pnlDong.add(lblDongDangXet, BorderLayout.WEST);
        pnlDong.add(lblGiaTri, BorderLayout.EAST);
        pnlDong.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, manh ? 36 : 24));
        return pnlDong;
    }

    /**
     * Thêm đến giỏ hàng trong LapHoaDonPanel.
     *
     * @param vungDangXet vùng đang xét
     */
    private void themDenGioHang(SanPham vungDangXet) {
        for (int chiSo = 0; chiSo < gioHang.size(); chiSo++) {
            // Dòng đang xét.
            GioHang.DongHang dongDangXet = gioHang.get(chiSo);
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
            if (dongDangXet.ma().equals(vungDangXet.ma())) {
                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                if (dongDangXet.soLuong() >= vungDangXet.tonKho()) {
                    HopThoai.hienThiThongBaoHopThoai(
                            this,
                            "Chỉ còn "
                                    + vungDangXet.tonKho()
                                    + " "
                                    + vungDangXet.ten()
                                    + " trong kho.",
                            "Vượt tồn kho",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                gioHang.set(
                        chiSo,
                        new GioHang.DongHang(
                                dongDangXet.ma(),
                                dongDangXet.ten(),
                                dongDangXet.donGia(),
                                dongDangXet.soLuong() + 1));
                lichSuThem.push(vungDangXet.ma());
                lamMoiGioHang();
                return;
            }
        }
        gioHang.add(
                new GioHang.DongHang(vungDangXet.ma(), vungDangXet.ten(), vungDangXet.gia(), 1));
        lichSuThem.push(vungDangXet.ma());
        lamMoiGioHang();
    }

    /**
     * Lấy thay đổi số lượng trong LapHoaDonPanel.
     *
     * @param ma mã
     * @param chenhLech chênh lệch
     */
    private void layThayDoiSoLuong(String ma, int chenhLech) {
        for (int chiSo = 0; chiSo < gioHang.size(); chiSo++) {
            // Dòng đang xét.
            GioHang.DongHang dongDangXet = gioHang.get(chiSo);
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (!dongDangXet.ma().equals(ma)) continue;
            // Tồn kho.
            int tonKho =
                    laySanPham().stream()
                            .filter(vungDangXet -> vungDangXet.ma().equals(ma))
                            .mapToInt(SanPham::tonKho)
                            .findFirst()
                            .orElse(Integer.MAX_VALUE);
            // Số lượng.
            int soLuong = dongDangXet.soLuong() + chenhLech;
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
            if (soLuong > tonKho) return;
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (soLuong <= 0) gioHang.remove(chiSo);
            else
                gioHang.set(
                        chiSo,
                        new GioHang.DongHang(
                                dongDangXet.ma(),
                                dongDangXet.ten(),
                                dongDangXet.donGia(),
                                soLuong));
            break;
        }
        lamMoiGioHang();
    }

    /** Làm mới giỏ hàng trong LapHoaDonPanel. */
    private void lamMoiGioHang() {
        pnlGioHangCacDong.removeAll();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (gioHang.isEmpty()) {
            // Vùng chứa.
            JPanel pnlVungChua = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
            pnlVungChua.setOpaque(false);
            pnlVungChua.add(lblTrongGioHang);
            pnlGioHangCacDong.add(pnlVungChua);
        }
        for (GioHang.DongHang dong : gioHang) pnlGioHangCacDong.add(layGioHangDong(dong));
        pnlGioHangCacDong.revalidate();
        pnlGioHangCacDong.repaint();
        banHang.datCacDong(gioHang);
        capNhatCacTong();
    }

    /**
     * Lấy giỏ hàng dòng trong LapHoaDonPanel.
     *
     * @param dong dòng
     * @return kết quả lấy giỏ hàng dòng
     */
    private JPanel layGioHangDong(GioHang.DongHang dong) {
        // Dòng.
        JPanel pnlDong = new JPanel(new BorderLayout(8, 0));
        pnlDong.setBackground(Color.WHITE);
        pnlDong.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN),
                        new EmptyBorder(8, 10, 8, 10)));
        pnlDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));

        // Thông tin.
        JPanel pnlThongTin = taoVungTrongSuot(new BorderLayout());
        // Tên.
        JLabel lblTen =
                new JLabel(
                        "<html><body style='width:175px'>"
                                + dong.ten().replace("&", "&amp;").replace("<", "&lt;")
                                + "</body></html>");
        lblTen.setToolTipText(dong.ten());
        lblTen.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        // Đơn vị.
        JLabel lblDonVi = new JLabel(TienTe.dinhDang(dong.donGia()));
        lblDonVi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        lblDonVi.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlThongTin.add(lblTen, BorderLayout.NORTH);
        pnlThongTin.add(lblDonVi, BorderLayout.SOUTH);

        // Số lượng.
        JPanel pnlSoLuong = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pnlSoLuong.setOpaque(false);
        // Trừ.
        JButton btnTru = layNutSoLuong("−", "Giảm số lượng " + dong.ten());
        // Cộng.
        JButton btnCong = layNutSoLuong("+", "Tăng số lượng " + dong.ten());
        btnTru.addActionListener(suKien -> layThayDoiSoLuong(dong.ma(), -1));
        btnCong.addActionListener(suKien -> layThayDoiSoLuong(dong.ma(), +1));
        // Số lượng.
        JLabel lblSoLuong = new JLabel(String.valueOf(dong.soLuong()), SwingConstants.CENTER);
        lblSoLuong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        lblSoLuong.setPreferredSize(new Dimension(24, 26));
        pnlSoLuong.add(btnTru);
        pnlSoLuong.add(lblSoLuong);
        pnlSoLuong.add(btnCong);

        // Số tiền.
        JLabel lblSoTien = new JLabel(TienTe.dinhDang(dong.docSoTien()), SwingConstants.RIGHT);
        lblSoTien.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        lblSoTien.setPreferredSize(new Dimension(84, 20));

        pnlDong.add(pnlThongTin, BorderLayout.CENTER);
        // Phải.
        JPanel pnlPhai = taoVungTrongSuot(new BorderLayout(6, 0));
        pnlPhai.add(pnlSoLuong, BorderLayout.WEST);
        pnlPhai.add(lblSoTien, BorderLayout.EAST);
        pnlDong.add(pnlPhai, BorderLayout.EAST);
        return pnlDong;
    }

    /**
     * Lấy nút số lượng trong LapHoaDonPanel.
     *
     * @param vanBan văn bản
     * @param tenTroNang tên trợ năng
     * @return kết quả lấy nút số lượng
     */
    private JButton layNutSoLuong(String vanBan, String tenTroNang) {
        // Giá trị sau.
        JButton btnGiaTriSau = new JButton(vanBan);
        btnGiaTriSau.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        btnGiaTriSau.setMargin(new Insets(0, 0, 0, 0));
        btnGiaTriSau.setPreferredSize(new Dimension(26, 26));
        btnGiaTriSau.setFocusPainted(false);
        btnGiaTriSau.setBackground(MauUngDung.BE_MAT_XEN_KE);
        btnGiaTriSau.getAccessibleContext().setAccessibleName(tenTroNang);
        return btnGiaTriSau;
    }

    /** Cập nhật các tổng trong LapHoaDonPanel. */
    private void capNhatCacTong() {
        lblNhanTamTinh.setText(TienTe.dinhDang(banHang.tinhTamTinh()));
        lblNhanGiamGia.setText(
                banHang.tinhGiamGia() == 0 ? "0₫" : "−" + TienTe.dinhDang(banHang.tinhGiamGia()));
        lblNhanTong.setText(TienTe.dinhDang(banHang.tinhTong()));
        // Khuyến mãi.
        GioHang.KhuyenMai khuyenMai = banHang.layKhuyenMaiTotNhat();
        lblNhanKhuyenMai.setText(
                khuyenMai == null
                        ? "—"
                        : khuyenMai.ten() + ": −" + TienTe.dinhDang(khuyenMai.soTien()));
        lblNhanKhuyenMai.setToolTipText(
                khuyenMai == null
                        ? "Chưa có khuyến mãi phù hợp"
                        : khuyenMai.ma() + " - " + khuyenMai.ten());
        btnNutThanhToan.setEnabled(!gioHang.isEmpty());
    }

    /**
     * Xóa giỏ hàng trong LapHoaDonPanel.
     *
     * @param xacNhan xác nhận
     */
    private void xoaGioHang(boolean xacNhan) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (gioHang.isEmpty()) return;
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (xacNhan
                && HopThoai.hienThiXacNhanHopThoai(
                                this,
                                "Xóa toàn bộ " + gioHang.size() + " dòng trong giỏ hàng?",
                                "Xóa giỏ hàng",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE)
                        != JOptionPane.YES_OPTION) return;
        gioHang.clear();
        lichSuThem.clear();
        banHang.xoa();
        pnlBoChon.xoa();
        lamMoiGioHang();
    }

    /** Quyết toán trong LapHoaDonPanel. */
    private void quyetToan() {
        capNhatCacTong();
        boDieuHuong.accept(MaManHinh.PAYMENT);
    }

    /** Treo giỏ hàng hiện tại trong LapHoaDonPanel. */
    private void treoGioHangHienTai() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (gioHang.isEmpty()) {
            HopThoai.hienThiCanhBao(this, "Giỏ hàng trống.");
            return;
        }
        try {
            banHang.datCacDong(gioHang);
            com.petstation.service.BanHangService.treo(banHang);
            xoaGioHang(false);
            HopThoai.hienThiThanhCong(this, "Đã lưu đơn treo vào SQL Server.");
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
        }
    }

    /** Hiển thị treo các giỏ hàng trong LapHoaDonPanel. */
    private void hienThiTreoCacGioHang() {
        boDieuHuong.accept(MaManHinh.PENDING_INVOICES);
    }

    /**
     * Lấy khả năng thanh công cụ trong LapHoaDonPanel.
     *
     * @return kết quả lấy khả năng thanh công cụ
     */
    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(true, false, true, false, true, true);
    }

    /** Xử lý tạo mới trong LapHoaDonPanel. */
    @Override
    public void xuLyTaoMoi() {
        xoaGioHang(true);
    }

    /** Xử lý hoàn tác trong LapHoaDonPanel. */
    @Override
    public void xuLyHoanTac() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (lichSuThem.isEmpty()) return;
        // Mã.
        String ma = lichSuThem.pop();
        for (int chiSo = 0; chiSo < gioHang.size(); chiSo++) {
            // Dòng đang xét.
            GioHang.DongHang dongDangXet = gioHang.get(chiSo);
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (!dongDangXet.ma().equals(ma)) continue;
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (dongDangXet.soLuong() <= 1) gioHang.remove(chiSo);
            else
                gioHang.set(
                        chiSo,
                        new GioHang.DongHang(
                                dongDangXet.ma(),
                                dongDangXet.ten(),
                                dongDangXet.donGia(),
                                dongDangXet.soLuong() - 1));
            break;
        }
        lamMoiGioHang();
    }

    /** Xử lý hiển thị trong LapHoaDonPanel. */
    @Override
    public void xuLyHienThi() {
        gioHang.clear();
        gioHang.addAll(banHang.layCacDong());
        dungLaiLuoi();
        pnlBoChon.dongBo();
        lamMoiGioHang();
        // Đồng bộ thẻ sản phẩm với giá và tồn kho hiện tại từ SQL.
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (banHang.kiemTraTrong() && !gioHang.isEmpty()) {
            gioHang.clear();
            lichSuThem.clear();
            pnlBoChon.dongBo();
            lamMoiGioHang();
        }
        txtTimKiem.requestFocusInWindow();
    }

    /**
     * Lấy nội dung trợ giúp trong LapHoaDonPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Quét mã vạch hoặc gõ mã/tên rồi Enter để thêm vào giỏ.\n"
                + "Bấm thẻ sản phẩm để thêm; dùng − / + để đổi số lượng.\n"
                + "Nhập SĐT khách hàng -> Enter để lookup (tự hiện tên, hạng, điểm).\n"
                + "Giảm giá: chọn % hoặc số tiền (VD: 50000đ).\n"
                + "F7: Treo đơn; F8: Mở đơn treo; F9: Thanh toán; Ctrl+Z: Bỏ SP vừa thêm;"
                + " Ctrl+K: Tìm SP.";
    }

    /**
     * Lấy tiêu đề trợ giúp trong LapHoaDonPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Lập đơn hàng";
    }

    /**
     * Cung cấp giao diện và xử lý tương tác cho theo dõi chiều rộng vùng hiển thị.
     *
     * <p>Panel luôn co theo bề rộng khung cuộn, tránh thanh cuộn ngang.
     */
    private static final class TheoChieuRongPanel extends JPanel implements Scrollable {
        /** Khởi tạo TheoChieuRongPanel với các giá trị được cung cấp. */
        TheoChieuRongPanel() {
            super(new BorderLayout());
        }

        /**
         * Lấy kích thước ưu tiên có thể cuộn khung nhìn trong TheoChieuRongPanel.
         *
         * @return kết quả lấy kích thước ưu tiên có thể cuộn khung nhìn
         */
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        /**
         * Lấy có thể cuộn đơn vị bước tăng trong TheoChieuRongPanel.
         *
         * @param dongDuLieu dòng dữ liệu
         * @param doiTuong đối tượng
         * @param ngayDangXet ngày đang xét
         * @return kết quả lấy có thể cuộn đơn vị bước tăng
         */
        @Override
        public int getScrollableUnitIncrement(Rectangle dongDuLieu, int doiTuong, int ngayDangXet) {
            return 16;
        }

        /**
         * Lấy có thể cuộn khối bước tăng trong TheoChieuRongPanel.
         *
         * @param dongDuLieu dòng dữ liệu
         * @param doiTuong đối tượng
         * @param ngayDangXet ngày đang xét
         * @return kết quả lấy có thể cuộn khối bước tăng
         */
        @Override
        public int getScrollableBlockIncrement(
                Rectangle dongDuLieu, int doiTuong, int ngayDangXet) {
            return 64;
        }

        /**
         * Lấy chiều rộng có thể cuộn theo dõi khung nhìn trong TheoChieuRongPanel.
         *
         * @return kết quả kiểm tra điều kiện xử lý
         */
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        /**
         * Lấy chiều cao có thể cuộn theo dõi khung nhìn trong TheoChieuRongPanel.
         *
         * @return kết quả kiểm tra điều kiện xử lý
         */
        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
