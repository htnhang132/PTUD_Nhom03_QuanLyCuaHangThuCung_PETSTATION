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
import javax.swing.border.EmptyBorder;

/**
 * Cung cấp giao diện và xử lý tương tác cho ca thu ngân vùng hiển thị.
 *
 * <p>Mở ca / Kết ca / Bàn giao. Mở ca: chọn ca (tự gợi ý theo giờ), gõ số tờ từng mệnh giá + Enter
 * để sang dòng kế, Enter ở dòng cuối là mở ca. Kết ca: kiểm đếm tương tự, hiện chênh lệch ngay, ghi
 * người nhận bàn giao.
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
    private final JPanel pnlCacChiSo = new JPanel(new GridLayout(1, 5, 10, 0));

    /** Đóng bộ đếm. */
    private final BoDemMenhGia pnlDongBoDem = new BoDemMenhGia();

    /** Chênh lệch. */
    private final JLabel lblChenhLech = new JLabel();

    /** Người nhận. */
    private final JTextField txtNguoiNhan = BieuMauPanel.layTruongVanBan("");

    /** Ghi chú. */
    private final JTextField txtGhiChu = BieuMauPanel.layTruongVanBan("");

    /** Đóng lỗi. */
    private final JLabel lblDongLoi = new JLabel("");

    /** Nút đóng. */
    private final NutDinhDang btnNutDong =
            new NutDinhDang("Đóng ca và bàn giao", NutDinhDang.KieuNut.CHINH);

    /**
     * Khởi tạo CaThuNganPanel với các giá trị được cung cấp.
     *
     * @param caLamViec ca làm việc
     */
    public CaThuNganPanel(CaThuNganService caLamViec) {
        this.caLamViec = caLamViec;
        setLayout(new BorderLayout(0, 14));
        // Tiêu đề.
        JLabel lblTieuDe = taoTieuDeMuc("Mở ca / Kết ca / Bàn giao");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        add(lblTieuDe, BorderLayout.NORTH);

        pnlGoc.setOpaque(false);
        pnlGoc.add(taoMo(), "open");
        pnlGoc.add(taoDangChay(), "running");
        add(pnlGoc, BorderLayout.CENTER);
        caLamViec.themBoLangNghe(this::lamMoi);
        lamMoi();
    }

    /**
     * Tạo mở trong CaThuNganPanel.
     *
     * @return kết quả tạo mở
     */
    private JPanel taoMo() {
        // Vùng đang xét.
        JPanel pnlVungDangXet = layThe(new BorderLayout(0, 12));
        // Trên.
        JPanel pnlTren = taoVungTrongSuot(new BorderLayout(0, 8));
        pnlTren.add(taoTieuDeMuc("Mở ca làm việc"), BorderLayout.NORTH);
        lblDangChoBieuNgu.setOpaque(true);
        lblDangChoBieuNgu.setBackground(new Color(0xFF, 0xF4, 0xCC));
        lblDangChoBieuNgu.setForeground(new Color(0x7A, 0x5A, 0x00));
        lblDangChoBieuNgu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        lblDangChoBieuNgu.setBorder(new EmptyBorder(8, 12, 8, 12));
        lblDangChoBieuNgu.setVisible(false);
        pnlTren.add(lblDangChoBieuNgu, BorderLayout.CENTER);
        pnlVungDangXet.add(pnlTren, BorderLayout.NORTH);

        // Trái: tiền đầu ca theo mệnh giá. Phải: chọn ca, tổng, nút mở ca.
        // Trái.
        JPanel pnlTrai = taoVungTrongSuot(new BorderLayout(0, 6));
        // Gợi ý.
        JLabel lblGoiY =
                new JLabel(
                        "Gõ số tờ rồi nhấn Enter để sang mệnh giá kế tiếp. Enter ở dòng cuối sẽ mở"
                                + " ca.");
        lblGoiY.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlTrai.add(lblGoiY, BorderLayout.NORTH);
        pnlTrai.add(pnlMoBoDem, BorderLayout.CENTER);

        // Phải.
        JPanel pnlPhai = new JPanel();
        pnlPhai.setOpaque(false);
        pnlPhai.setLayout(new BoxLayout(pnlPhai, BoxLayout.Y_AXIS));
        pnlPhai.setPreferredSize(new Dimension(320, 0));
        pnlPhai.add(layNho("Ca làm việc"));
        cboTenCaLamViec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboTenCaLamViec.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlPhai.add(cboTenCaLamViec);
        pnlPhai.add(Box.createVerticalStrut(14));
        pnlPhai.add(layNho("Tiền mặt đầu ca"));
        // Tổng.
        JLabel lblTong = new JLabel("0₫");
        lblTong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 26));
        lblTong.setForeground(MauUngDung.DO_THUONG_HIEU);
        lblTong.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlPhai.add(lblTong);
        pnlMoBoDem.layKhiThayDoi(() -> lblTong.setText(TienTe.dinhDang(pnlMoBoDem.tinhTong())));
        pnlPhai.add(Box.createVerticalStrut(10));
        lblCuoiCungBanGiao.setForeground(MauUngDung.VAN_BAN_XAM);
        lblCuoiCungBanGiao.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblCuoiCungBanGiao.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlPhai.add(lblCuoiCungBanGiao);
        pnlPhai.add(Box.createVerticalStrut(10));
        lblMoLoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
        lblMoLoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlPhai.add(lblMoLoi);
        pnlPhai.add(Box.createVerticalStrut(8));
        // Mở.
        NutDinhDang btnMo = new NutDinhDang("Mở ca (Enter)", NutDinhDang.KieuNut.CHINH);
        btnMo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnMo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnMo.addActionListener(suKien -> moCaLamViec());
        pnlPhai.add(btnMo);
        pnlMoBoDem.layKhiHoanTat(this::moCaLamViec);

        // Nội dung.
        JPanel pnlNoiDung = taoVungTrongSuot(new BorderLayout(24, 0));
        pnlNoiDung.add(pnlTrai, BorderLayout.CENTER);
        pnlNoiDung.add(pnlPhai, BorderLayout.EAST);
        pnlVungDangXet.add(pnlNoiDung, BorderLayout.CENTER);
        return pnlVungDangXet;
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
        pnlSoLuong.add(pnlDongBoDem, BorderLayout.CENTER);
        pnlDongBoDem.layKhiThayDoi(this::capNhatSoLuongTomTat);
        pnlDongBoDem.layKhiHoanTat(() -> txtNguoiNhan.requestFocusInWindow());

        // Bên.
        JPanel pnlBen = new JPanel();
        pnlBen.setOpaque(false);
        pnlBen.setLayout(new BoxLayout(pnlBen, BoxLayout.Y_AXIS));
        lblChenhLech.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        lblChenhLech.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBen.add(layNho("Chênh lệch so với dự kiến"));
        pnlBen.add(lblChenhLech);
        pnlBen.add(Box.createVerticalStrut(12));
        pnlBen.add(layNho("Người nhận bàn giao"));
        txtNguoiNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtNguoiNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBen.add(txtNguoiNhan);
        pnlBen.add(Box.createVerticalStrut(8));
        pnlBen.add(layNho("Ghi chú (bắt buộc khi có chênh lệch)"));
        txtGhiChu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtGhiChu.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBen.add(txtGhiChu);
        txtNguoiNhan.addActionListener(suKien -> txtGhiChu.requestFocusInWindow());
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
        long tienMat = pnlMoBoDem.tinhTong();
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
        long tong = pnlDongBoDem.tinhTong();
        // Chênh lệch.
        long chenhLech = tong - caLamViec.layTienMatDuKien();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (txtNguoiNhan.getText().isBlank()) {
            lblDongLoi.setText("Nhập tên người nhận bàn giao.");
            txtNguoiNhan.requestFocusInWindow();
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
                                + txtNguoiNhan.getText().trim()
                                + "\nSau khi đóng ca không thể bán hàng cho đến khi mở ca mới.");
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (dongY) {
            try {
                caLamViec.dong(
                        tong,
                        "Bàn giao "
                                + txtNguoiNhan.getText().trim()
                                + (txtGhiChu.getText().isBlank()
                                        ? ""
                                        : " – " + txtGhiChu.getText().trim()));
            } catch (RuntimeException ngoaiLe) {
                HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
                return;
            }
            txtGhiChu.setText("");
            txtNguoiNhan.setText("");
        }
    }

    /** Cập nhật số lượng tóm tắt trong CaThuNganPanel. */
    private void capNhatSoLuongTomTat() {
        // Chênh lệch.
        long chenhLech = pnlDongBoDem.tinhTong() - caLamViec.layTienMatDuKien();
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
            pnlMoBoDem.layTieuDiemDauTien();
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
                layChiSoThe("Dự kiến trong két", TienTe.dinhDang(caLamViec.layTienMatDuKien())));
        pnlCacChiSo.revalidate();
        pnlCacChiSo.repaint();
        pnlDongBoDem.datLai();
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
        return "Mở ca: chọn ca, gõ số tờ từng mệnh giá rồi Enter để sang dòng kế; Enter ở dòng cuối"
                + " là mở ca.\n"
                + "Kết ca: đếm tiền thực tế, so với tiền dự kiến (đầu ca + doanh thu tiền mặt)."
                + " Có chênh lệch thì phải ghi chú.";
    }

    /**
     * Lấy tiêu đề trợ giúp trong CaThuNganPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Mở ca / Kết ca / Bàn giao";
    }
}
