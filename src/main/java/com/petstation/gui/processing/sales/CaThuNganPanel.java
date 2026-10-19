package com.petstation.gui.processing.sales;

import com.petstation.gui.components.BieuMauPanel;
import com.petstation.gui.components.BoDemMenhGia;
import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.KhaNangThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.service.CaThuNganService;
import com.petstation.util.TienTe;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

/**
 * Cung cấp giao diện và xử lý tương tác cho ca thu ngân vùng hiển thị.
 *
 * <p>Ca thu ngân. Mở ca: chọn ca (tự gợi ý theo giờ), gõ số tờ từng mệnh giá + Enter để sang dòng
 * kế, Enter ở dòng cuối là mở ca. Kết ca: kiểm đếm tương tự, hiện chênh lệch ngay, ghi người nhận
 * bàn giao.
 */
public final class CaThuNganPanel extends CoSoPanel {
    /** Ca làm việc. */
    private final CaThuNganService caLamViec;

    /** Các thẻ. */
    private final CardLayout cacThe = new CardLayout();

    /** Gốc. */
    private final JPanel pnlGoc = new JPanel(cacThe);

    // Trang mở ca
    /** Tên ca làm việc. */
    private final JComboBox<String> cboTenCaLamViec =
            BieuMauPanel.layHopChon(new String[] {"Ca sáng", "Ca chiều", "Ca tối"});

    /** Mở bộ đếm. */
    private final BoDemMenhGia pnlMoBoDem = new BoDemMenhGia();

    /** Mở lỗi. */
    private final JLabel lblMoLoi = new JLabel("");

    /** Cuối cùng bàn giao. */
    private final JLabel lblCuoiCungBanGiao = new JLabel("");

    /** Đang chờ biểu ngữ. */
    private final JLabel lblDangChoBieuNgu = new JLabel("");

    // Trang đang chạy ca
    /** Các chỉ số. */
    private final JPanel pnlCacChiSo = new JPanel(new GridLayout(2, 3, 10, 10));

    /** Đóng bộ đếm. */
    private final BoDemMenhGia pnlDongBoDem = new BoDemMenhGia();

    /** Chênh lệch. */
    private final JLabel lblChenhLech = new JLabel();

    /** Người nhận. */
    private final JComboBox<CaThuNganService.NguoiNhan> cboNguoiNhan = new JComboBox<>();

    private final JComboBox<String> cboHinhThuc =
            new JComboBox<>(new String[] {"Nộp quỹ cửa hàng", "Bàn giao nhân viên"});
    private final JTextField txtTienDauCa = BieuMauPanel.layTruongVanBan("");
    private final JTextField txtTienThucDem = BieuMauPanel.layTruongVanBan("");

    /** Ghi chú. */
    private final JTextField txtGhiChu = BieuMauPanel.layTruongVanBan("");

    /** Đóng lỗi. */
    private final JLabel lblDongLoi = new JLabel("");

    /** Nút đóng. */
    private final NutDinhDang btnNutDong = new NutDinhDang("Kết ca", NutDinhDang.KieuNut.CHINH);

    /**
     * Khởi tạo CaThuNganPanel với các giá trị được cung cấp.
     *
     * @param caLamViec ca làm việc
     */
    public CaThuNganPanel(CaThuNganService caLamViec) {
        this.caLamViec = caLamViec;
        setLayout(new BorderLayout(0, 14));
        // Tiêu đề.
        JLabel lblTieuDe = taoTieuDeMuc("Ca thu ngân");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        add(lblTieuDe, BorderLayout.NORTH);

        pnlGoc.setOpaque(false);
        pnlGoc.add(taoMo(), "open");
        pnlGoc.add(taoDangChay(), "running");
        JScrollPane scrNoiDung = new JScrollPane(pnlGoc);
        scrNoiDung.setBorder(BorderFactory.createEmptyBorder());
        scrNoiDung.getViewport().setBackground(MauUngDung.NEN_KEM);
        add(scrNoiDung, BorderLayout.CENTER);
        caLamViec.themBoLangNghe(this::lamMoi);
        lamMoi();
    }

    /**
     * Tạo mở trong CaThuNganPanel.
     *
     * @return kết quả tạo mở
     */
    private JPanel taoMo() {
        JPanel the = layThe(new BorderLayout(0, 12));
        JPanel dau = taoVungTrongSuot(new BorderLayout(0, 8));
        dau.add(taoTieuDeMuc("Mở ca làm việc"), BorderLayout.NORTH);
        dau.add(lblDangChoBieuNgu, BorderLayout.SOUTH);
        the.add(dau, BorderLayout.NORTH);
        JPanel noiDung = taoVungTrongSuot(new BorderLayout(0, 12));
        JPanel form = taoVungTrongSuot(new GridBagLayout());
        GridBagConstraints viTri = new GridBagConstraints();
        viTri.insets = new Insets(5, 6, 5, 6);
        viTri.anchor = GridBagConstraints.WEST;
        viTri.gridx = 0;
        viTri.gridy = 0;
        form.add(new JLabel("Ca làm việc"), viTri);
        viTri.gridx = 1;
        cboTenCaLamViec.setPreferredSize(new Dimension(280, 36));
        form.add(cboTenCaLamViec, viTri);
        viTri.gridx = 0;
        viTri.gridy = 1;
        form.add(new JLabel("Tiền mặt đầu ca (đ)"), viTri);
        viTri.gridx = 1;
        txtTienDauCa.setPreferredSize(new Dimension(280, 36));
        form.add(txtTienDauCa, viTri);
        noiDung.add(form, BorderLayout.NORTH);
        noiDung.add(taoKiemDemTuyChon(pnlMoBoDem), BorderLayout.CENTER);
        pnlMoBoDem.layKhiThayDoi(() -> txtTienDauCa.setText(String.valueOf(pnlMoBoDem.tinhTong())));
        pnlMoBoDem.layKhiHoanTat(this::moCaLamViec);
        txtTienDauCa.addActionListener(e -> moCaLamViec());
        the.add(noiDung, BorderLayout.CENTER);
        JPanel duoi = taoVungTrongSuot(new BorderLayout(0, 6));
        duoi.add(lblCuoiCungBanGiao, BorderLayout.NORTH);
        duoi.add(lblMoLoi, BorderLayout.CENTER);
        NutDinhDang btnMo = new NutDinhDang("Mở ca", NutDinhDang.KieuNut.CHINH);
        btnMo.addActionListener(e -> moCaLamViec());
        duoi.add(btnMo, BorderLayout.SOUTH);
        the.add(duoi, BorderLayout.SOUTH);
        JPanel canGiua = taoVungTrongSuot(new GridBagLayout());
        GridBagConstraints viTriThe = new GridBagConstraints();
        viTriThe.weightx = 1;
        viTriThe.weighty = 1;
        viTriThe.anchor = GridBagConstraints.NORTH;
        canGiua.add(the, viTriThe);
        return canGiua;
    }

    /**
     * Tạo đang chạy trong CaThuNganPanel.
     *
     * @return kết quả tạo đang chạy
     */
    private JPanel taoDangChay() {
        // Vùng đang xét.
        JPanel pnlVungDangXet = taoVungTrongSuot(new BorderLayout(0, 14));
        pnlCacChiSo.setOpaque(false);
        pnlVungDangXet.add(pnlCacChiSo, BorderLayout.NORTH);

        // Số lượng.
        JPanel pnlSoLuong = layThe(new BorderLayout(0, 8));
        pnlSoLuong.add(taoTieuDeMuc("Kiểm đếm tiền mặt trong két"), BorderLayout.NORTH);
        JPanel pnlDem = new JPanel(new BorderLayout(0, 8));
        pnlDem.setOpaque(false);
        JPanel pnlNhapTong = new JPanel(new BorderLayout(8, 0));
        pnlNhapTong.add(new JLabel("Tổng tiền mặt thực đếm (đ)"), BorderLayout.WEST);
        txtTienThucDem.setPreferredSize(new Dimension(220, 36));
        pnlNhapTong.add(txtTienThucDem, BorderLayout.CENTER);
        pnlDem.add(pnlNhapTong, BorderLayout.NORTH);
        pnlDem.add(taoKiemDemTuyChon(pnlDongBoDem), BorderLayout.CENTER);
        pnlSoLuong.add(pnlDem, BorderLayout.CENTER);
        txtTienThucDem
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                capNhatSoLuongTomTat();
                            }

                            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                capNhatSoLuongTomTat();
                            }

                            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                capNhatSoLuongTomTat();
                            }
                        });
        pnlDongBoDem.layKhiThayDoi(
                () -> txtTienThucDem.setText(String.valueOf(pnlDongBoDem.tinhTong())));
        pnlDongBoDem.layKhiHoanTat(() -> txtGhiChu.requestFocusInWindow());

        // Bên.
        JPanel pnlBen = new JPanel();
        pnlBen.setOpaque(false);
        pnlBen.setLayout(new BoxLayout(pnlBen, BoxLayout.Y_AXIS));
        lblChenhLech.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        lblChenhLech.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBen.add(layNho("Chênh lệch so với dự kiến"));
        pnlBen.add(lblChenhLech);
        pnlBen.add(Box.createVerticalStrut(12));
        pnlBen.add(layNho("Hình thức kết ca"));
        cboHinhThuc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboHinhThuc.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBen.add(cboHinhThuc);
        pnlBen.add(layNho("Người nhận ca"));
        cboNguoiNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboNguoiNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboNguoiNhan.setEnabled(false);
        cboHinhThuc.addActionListener(
                e -> cboNguoiNhan.setEnabled(cboHinhThuc.getSelectedIndex() == 1));
        pnlBen.add(cboNguoiNhan);
        pnlBen.add(Box.createVerticalStrut(8));
        pnlBen.add(layNho("Ghi chú (bắt buộc khi có chênh lệch)"));
        txtGhiChu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtGhiChu.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBen.add(txtGhiChu);

        txtGhiChu.addActionListener(suKien -> dongCaLamViec());
        lblDongLoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
        lblDongLoi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblDongLoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBen.add(lblDongLoi);
        pnlBen.add(Box.createVerticalStrut(8));
        btnNutDong.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnNutDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnNutDong.addActionListener(suKien -> dongCaLamViec());
        pnlBen.add(btnNutDong);

        // Bên thẻ.
        JPanel pnlBenThe = layThe(new BorderLayout());
        pnlBenThe.setPreferredSize(new Dimension(330, 0));
        pnlBenThe.add(pnlBen, BorderLayout.NORTH);

        // Giữa.
        JPanel pnlGiua = taoVungTrongSuot(new BorderLayout(14, 0));
        pnlGiua.add(pnlSoLuong, BorderLayout.CENTER);
        pnlGiua.add(pnlBenThe, BorderLayout.EAST);
        pnlVungDangXet.add(pnlGiua, BorderLayout.CENTER);
        return pnlVungDangXet;
    }

    /**
     * Lấy nhỏ trong CaThuNganPanel.
     *
     * @param vanBan văn bản
     * @return kết quả lấy nhỏ
     */
    private JLabel layNho(String vanBan) {
        // Dòng đang xét.
        JLabel lblDongDangXet = new JLabel(vanBan);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        lblDongDangXet.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lblDongDangXet;
    }

    /** Mở ca làm việc trong CaThuNganPanel. */
    private void moCaLamViec() {
        // Tiền mặt.
        long tienMat;
        try {
            tienMat = docSoTien(txtTienDauCa);
        } catch (IllegalArgumentException e) {
            HopThoai.hienThiCanhBao(this, e.getMessage());
            return;
        }
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (tienMat == 0 && !HopThoai.xacNhan(this, "Tiền đầu ca là 0₫. Vẫn mở ca?")) return;
        lblMoLoi.setText("");
        // Tên.
        String ten = String.valueOf(cboTenCaLamViec.getSelectedItem());
        try {
            caLamViec.mo(ten, tienMat);
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
        }
    }

    /** Đóng ca làm việc trong CaThuNganPanel. */
    private void dongCaLamViec() {
        // Tổng.
        long tong;
        try {
            tong = docSoTien(txtTienThucDem);
        } catch (IllegalArgumentException e) {
            lblDongLoi.setText(e.getMessage());
            return;
        }
        // Chênh lệch.
        long chenhLech = tong - caLamViec.layTienMatDuKien();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (cboHinhThuc.getSelectedIndex() == 1 && cboNguoiNhan.getSelectedItem() == null) {
            lblDongLoi.setText("Nhập tên người nhận bàn giao.");
            cboNguoiNhan.requestFocusInWindow();
            return;
        }
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (chenhLech != 0 && txtGhiChu.getText().isBlank()) {
            lblDongLoi.setText(
                    "Có chênh lệch " + TienTe.dinhDang(chenhLech) + ": hãy nhập ghi chú bàn giao.");
            txtGhiChu.requestFocusInWindow();
            return;
        }
        // Đồng ý.
        boolean dongY =
                HopThoai.xacNhan(
                        this,
                        "Đóng "
                                + caLamViec.layTenCa()
                                + " với tiền thực đếm "
                                + TienTe.dinhDang(tong)
                                + (chenhLech == 0
                                        ? " (khớp)."
                                        : " (chênh lệch " + TienTe.dinhDang(chenhLech) + ").")
                                + "\nBàn giao cho: "
                                + (cboHinhThuc.getSelectedIndex() == 0
                                        ? "Nộp quỹ cửa hàng"
                                        : String.valueOf(cboNguoiNhan.getSelectedItem()))
                                + "\nSau khi đóng ca không thể bán hàng cho đến khi mở ca mới.");
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (dongY) {
            try {
                var nguoiNhan = (CaThuNganService.NguoiNhan) cboNguoiNhan.getSelectedItem();
                boolean banGiao = cboHinhThuc.getSelectedIndex() == 1;
                caLamViec.dong(
                        tong,
                        txtGhiChu.getText().trim(),
                        banGiao
                                ? CaThuNganService.HinhThucBanGiao.BAN_GIAO
                                : CaThuNganService.HinhThucBanGiao.NOP_QUY,
                        banGiao && nguoiNhan != null ? nguoiNhan.id() : null);
            } catch (RuntimeException ngoaiLe) {
                HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
                return;
            }
            txtGhiChu.setText("");
            cboNguoiNhan.setSelectedItem(null);
        }
    }

    /** Cập nhật số lượng tóm tắt trong CaThuNganPanel. */
    private void capNhatSoLuongTomTat() {
        // Chênh lệch.
        long chenhLech;
        try {
            chenhLech = docSoTien(txtTienThucDem) - caLamViec.layTienMatDuKien();
        } catch (IllegalArgumentException e) {
            lblChenhLech.setText("Chưa nhập tiền thực đếm");
            return;
        }
        lblChenhLech.setText(
                chenhLech == 0
                        ? "Khớp"
                        : (chenhLech > 0 ? "Thừa " : "Thiếu ")
                                + TienTe.dinhDang(Math.abs(chenhLech)));
        lblChenhLech.setForeground(
                chenhLech == 0 ? MauUngDung.XANH_LA : MauUngDung.VAN_BAN_NGUY_HIEM);
    }

    /**
     * Lấy đề xuất ca làm việc trong CaThuNganPanel.
     *
     * @return kết quả lấy đề xuất ca làm việc
     */
    private static String layDeXuatCaLamViec() {
        // Chiều cao.
        int chieuCao = LocalTime.now().getHour();
        return chieuCao < 12 ? "Ca sáng" : chieuCao < 18 ? "Ca chiều" : "Ca tối";
    }

    /** Làm mới trong CaThuNganPanel. */
    private void lamMoi() {
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (!caLamViec.kiemTraMo()) {
            lblCuoiCungBanGiao.setText(
                    caLamViec.layBanGiaoGanNhat().isEmpty()
                            ? ""
                            : "Ca gần nhất – " + caLamViec.layBanGiaoGanNhat());
            cboTenCaLamViec.setSelectedItem(layDeXuatCaLamViec());
            pnlMoBoDem.datLai();
            // Đang chờ.
            boolean dangCho = !caLamViec.layTieuDeDangCho().isEmpty();
            lblDangChoBieuNgu.setVisible(dangCho);
            lblDangChoBieuNgu.setText(
                    dangCho
                            ? "Mở ca xong hệ thống sẽ đưa bạn về \""
                                    + caLamViec.layTieuDeDangCho()
                                    + "\"."
                            : "");
            cacThe.show(pnlGoc, "open");
            txtTienDauCa.setText("");
            txtTienDauCa.requestFocusInWindow();
            return;
        }
        pnlCacChiSo.removeAll();
        pnlCacChiSo.add(
                layChiSoThe(
                        "Ca hiện tại (từ "
                                + caLamViec
                                        .layGioMoCa()
                                        .format(DateTimeFormatter.ofPattern("HH:mm"))
                                + ")",
                        caLamViec.layTenCa()));
        pnlCacChiSo.add(layChiSoThe("Tiền đầu ca", TienTe.dinhDang(caLamViec.layTienDauCa())));
        pnlCacChiSo.add(
                layChiSoThe("Doanh thu tiền mặt", TienTe.dinhDang(caLamViec.layDoanhThuTienMat())));
        pnlCacChiSo.add(
                layChiSoThe(
                        "Chuyển khoản / thẻ",
                        TienTe.dinhDang(
                                caLamViec.layDoanhThuChuyenKhoan() + caLamViec.layDoanhThuThe())));
        pnlCacChiSo.add(
                layChiSoThe("Cọc Hotel tiền mặt", TienTe.dinhDang(caLamViec.layTienCocHotel())));
        pnlCacChiSo.add(
                layChiSoThe("Dự kiến trong két", TienTe.dinhDang(caLamViec.layTienMatDuKien())));
        pnlCacChiSo.revalidate();
        pnlCacChiSo.repaint();
        pnlDongBoDem.datLai();
        txtTienThucDem.setText("");
        cboNguoiNhan.removeAllItems();
        caLamViec.layNguoiNhan().forEach(cboNguoiNhan::addItem);
        cboNguoiNhan.setSelectedItem(null);
        lblDongLoi.setText("");
        capNhatSoLuongTomTat();
        cacThe.show(pnlGoc, "running");
    }

    /**
     * Lấy khả năng thanh công cụ trong CaThuNganPanel.
     *
     * @return kết quả lấy khả năng thanh công cụ
     */
    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(false, false, false, false, false, true);
    }

    /** Xử lý hiển thị trong CaThuNganPanel. */
    @Override
    public void xuLyHienThi() {
        lamMoi();
    }

    /**
     * Lấy nội dung trợ giúp trong CaThuNganPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Mở ca: chọn ca và nhập tổng tiền mặt đầu ca. Kiểm đếm mệnh giá là tùy chọn.\n"
                + "Kết ca: nhập tổng tiền mặt thực đếm; hệ thống không điền hộ số tiền này.\n"
                + "Chọn nộp quỹ cửa hàng hoặc bàn giao cho quản lý/thu ngân đang hoạt động. "
                + "Có chênh lệch thì nhập lý do; xác nhận để kết ca.";
    }

    /**
     * Lấy tiêu đề trợ giúp trong CaThuNganPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Ca thu ngân";
    }

    private static long docSoTien(JTextField truong) {
        String giaTri = truong.getText().trim();
        if (!giaTri.matches("[0-9]+"))
            throw new IllegalArgumentException(
                    "Nhập số tiền nguyên không âm, không có dấu phân cách.");
        try {
            return Long.parseLong(giaTri);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Số tiền quá lớn.");
        }
    }

    private JPanel taoKiemDemTuyChon(BoDemMenhGia boDem) {
        JPanel khung = new JPanel(new BorderLayout(0, 8));
        khung.setOpaque(false);
        JCheckBox hienThi = new JCheckBox("Kiểm đếm theo mệnh giá (tùy chọn)");
        hienThi.setOpaque(false);
        khung.add(hienThi, BorderLayout.NORTH);
        khung.add(boDem, BorderLayout.CENTER);
        boDem.setVisible(false);
        hienThi.addActionListener(
                e -> {
                    boDem.setVisible(hienThi.isSelected());
                    khung.revalidate();
                });
        return khung;
    }
}
