package com.petstation.gui.components;

import com.petstation.gui.processing.staff.ThaoTacNhanh;
import com.petstation.service.DanhMucService;
import com.petstation.service.LichLamViecService;
import com.petstation.service.LichLamViecService.Muc;
import com.petstation.service.NhatKyHeThongService;

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
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Cung cấp giao diện và xử lý tương tác cho tuần lịch làm việc vùng hiển thị.
 *
 * <p>Lịch làm việc theo tuần dạng lưới: hàng là ca (Sáng, Chiều, Tối), cột là các ngày trong tuần,
 * mỗi ô chứa thẻ nhân viên. Một giao diện cho 3 màn: xem toàn cửa hàng, xem cá nhân (kèm Đăng ký
 * nghỉ) và phân lịch (thêm/bớt nhân viên trong ô).
 */
public class LichTuanPanel extends CoSoPanel implements HoTroThanhCongCu {
    /** Cung cấp giao diện và xử lý tương tác cho chế độ. */
    public enum CheDo {
        HIEN_THI_TAT_CA,
        CA_NHAN,
        SUA
    }

    /** Ngày. */
    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(java.time.format.ResolverStyle.STRICT);

    /** Thứ. */
    private static final String[] THU = {
        "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"
    };

    /** Tiêu đề nền. */
    private static final Color TIEU_DE_NEN = new Color(0xEA, 0xF1, 0xFB);

    /** Tiêu đề chữ. */
    private static final Color TIEU_DE_CHU = new Color(0x1F, 0x6F, 0xB5);

    /** Dòng nhãn nền. */
    private static final Color DONG_NHAN_NEN = new Color(0xFF, 0xFF, 0xCC);

    /** Bán hàng. */
    private static final Color BAN_HANG = new Color(0xE6, 0xEA, 0xEE);

    /** Spa. */
    private static final Color SPA = new Color(0x72, 0xD1, 0x2F);

    /** Kho. */
    private static final Color KHO = new Color(0x92, 0xD2, 0xF6);

    /** Quản lý. */
    private static final Color QUAN_LY = new Color(0xFF, 0xF2, 0x8C);

    /** Nghỉ phép. */
    private static final Color NGHI_PHEP = new Color(0xE0, 0x4B, 0x3A);

    /** Chế độ. */
    private final CheDo cheDo;

    /** Hiện tại người dùng. */
    private final String hienTaiNguoiDung;

    /** Thứ hai. */
    private LocalDate thuHai = LichLamViecService.layThuHaiCuaTuan(LocalDate.now());

    /** Trường ngày. */
    private final JTextField txtTruongNgay = new JTextField(10);

    /** Tất cả. */
    private final JRadioButton btnTatCa = new JRadioButton("Tất cả", true);

    /** Làm việc. */
    private final JRadioButton btnLamViec = new JRadioButton("Ca làm việc");

    /** Nghỉ phép. */
    private final JRadioButton btnNghiPhep = new JRadioButton("Nghỉ phép");

    /** Lưới vùng chủ. */
    private final JPanel pnlLuoiVungChu = new JPanel(new BorderLayout());

    /** Tóm tắt. */
    private final JLabel lblTomTat = new JLabel(" ");

    public LichTuanPanel(CheDo cheDo, String hienTaiNguoiDung) {
        this.cheDo = cheDo;
        this.hienTaiNguoiDung = hienTaiNguoiDung == null ? "" : hienTaiNguoiDung;
        setLayout(new BorderLayout(0, 10));

        // Tiêu đề.
        String tieuDe =
                switch (cheDo) {
                    case HIEN_THI_TAT_CA -> "Lịch làm việc theo tuần";
                    case CA_NHAN -> "Lịch làm việc cá nhân theo tuần";
                    case SUA -> "Phân lịch nhân viên theo tuần";
                };
        // Tiêu đề.
        JLabel lblTieuDe = taoTieuDeMuc(tieuDe);
        // Trên.
        JPanel pnlTren = taoVungTrongSuot(new BorderLayout(0, 8));
        pnlTren.add(ThanhDanhSach.tieuDe(lblTieuDe, lblTomTat), BorderLayout.NORTH);
        pnlTren.add(taoThanhCongCu(), BorderLayout.CENTER);
        lblTomTat.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblTomTat.setForeground(MauUngDung.VAN_BAN_XAM);
        add(pnlTren, BorderLayout.NORTH);

        pnlLuoiVungChu.setOpaque(false);
        // Cuộn.
        JScrollPane scrCuon = new JScrollPane(pnlLuoiVungChu);
        scrCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrCuon.getVerticalScrollBar().setUnitIncrement(16);
        scrCuon.getViewport().setBackground(Color.WHITE);
        add(scrCuon, BorderLayout.CENTER);
        add(taoChuGiai(), BorderLayout.SOUTH);

        LichLamViecService.themBoLangNghe(
                () ->
                        SwingUtilities.invokeLater(
                                () -> {
                                    if (isShowing()
                                            && com.petstation.service.PhienDangNhapService
                                                            .layPhienHienTai()
                                                    != null) dungLai();
                                }));
        dungLai();
    }

    // ───────────── Thanh công cụ (giống form lịch tuần)
    private JPanel taoThanhCongCu() {
        // Thanh.
        ThanhDanhSach pnlThanh = new ThanhDanhSach();
        pnlThanh.setOpaque(false);
        // Nhóm.
        ButtonGroup nhom = new ButtonGroup();
        for (JRadioButton btnDongDuLieu : new JRadioButton[] {btnTatCa, btnLamViec, btnNghiPhep}) {
            btnDongDuLieu.setOpaque(false);
            btnDongDuLieu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            btnDongDuLieu.addActionListener(suKien -> dungLai());
            nhom.add(btnDongDuLieu);
            pnlThanh.themBoLoc(btnDongDuLieu);
        }
        txtTruongNgay.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        txtTruongNgay.setHorizontalAlignment(SwingConstants.CENTER);
        txtTruongNgay.setToolTipText("Nhập ngày dd/MM/yyyy rồi Enter để xem tuần chứa ngày đó");
        txtTruongNgay.addActionListener(suKien -> chuyenNgayDenDaNhap());
        pnlThanh.add(txtTruongNgay);

        // Hôm nay.
        NutDinhDang btnHomNay = new NutDinhDang("Hiện tại", NutDinhDang.KieuNut.CHINH);
        btnHomNay.addActionListener(
                suKien -> {
                    thuHai = LichLamViecService.layThuHaiCuaTuan(LocalDate.now());
                    dungLai();
                });
        // In.
        NutDinhDang btnIn = new NutDinhDang("In lịch", NutDinhDang.KieuNut.TRONG_SUOT);
        btnIn.addActionListener(suKien -> inLichLamViec());
        // Quay lại.
        NutDinhDang btnQuayLai = new NutDinhDang("‹ Trở về", NutDinhDang.KieuNut.TRONG_SUOT);
        btnQuayLai.addActionListener(
                suKien -> {
                    thuHai = thuHai.minusWeeks(1);
                    dungLai();
                });
        // Tiếp theo.
        NutDinhDang btnTiepTheo = new NutDinhDang("Tiếp ›", NutDinhDang.KieuNut.TRONG_SUOT);
        btnTiepTheo.addActionListener(
                suKien -> {
                    thuHai = thuHai.plusWeeks(1);
                    dungLai();
                });
        pnlThanh.add(btnHomNay);
        pnlThanh.add(btnIn);
        pnlThanh.add(btnQuayLai);
        pnlThanh.add(btnTiepTheo);

        if (cheDo == CheDo.SUA) {
            NutDinhDang btnSaoChep =
                    new NutDinhDang("Sao chép tuần trước", NutDinhDang.KieuNut.TRONG_SUOT);
            btnSaoChep.setToolTipText("Sao chép phân ca từ tuần trước sang tuần này");
            btnSaoChep.addActionListener(suKien -> saoChepLichTuan());
            pnlThanh.add(btnSaoChep);

            NutDinhDang btnHoanDoi = new NutDinhDang("Đổi ca", NutDinhDang.KieuNut.TRONG_SUOT);
            btnHoanDoi.setToolTipText("Hoán đổi ca làm việc trực tiếp giữa 2 nhân viên");
            btnHoanDoi.addActionListener(suKien -> hoanDoiCaNhanVien());
            pnlThanh.add(btnHoanDoi);

            NutDinhDang btnKhoaLich = new NutDinhDang("Khóa lịch tuần", NutDinhDang.KieuNut.PHU);
            btnKhoaLich.setToolTipText("Khóa lịch tuần này để công bố chính thức");
            btnKhoaLich.addActionListener(suKien -> khoaLichTuan());
            pnlThanh.add(btnKhoaLich);
        } else if (cheDo == CheDo.CA_NHAN) {
            // Yêu cầu nhập.
            NutDinhDang btnYeuCauNhap =
                    new NutDinhDang("Đăng ký nghỉ phép", NutDinhDang.KieuNut.PHU);
            btnYeuCauNhap.addActionListener(suKien -> ThaoTacNhanh.layNghiPhep(this));
            pnlThanh.add(btnYeuCauNhap);

            NutDinhDang btnYeuCauDoiCa =
                    new NutDinhDang("Yêu cầu đổi ca", NutDinhDang.KieuNut.TRONG_SUOT);
            btnYeuCauDoiCa.addActionListener(suKien -> guiYeuCauDoiCaCaNhan());
            pnlThanh.add(btnYeuCauDoiCa);
        }
        return pnlThanh;
    }

    private void chuyenNgayDenDaNhap() {
        try {
            thuHai =
                    LichLamViecService.layThuHaiCuaTuan(
                            LocalDate.parse(txtTruongNgay.getText().trim(), NGAY));
            dungLai();
        } catch (DateTimeParseException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, "Ngày phải theo dạng dd/MM/yyyy, ví dụ 09/10/2026.");
            txtTruongNgay.setText(LocalDate.now().format(NGAY));
        }
    }

    private JPanel taoChuGiai() {
        // Chú giải.
        JPanel pnlChuGiai = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        pnlChuGiai.setOpaque(false);
        pnlChuGiai.add(layNhan(BAN_HANG, "Nhân viên bán hàng"));
        pnlChuGiai.add(layNhan(SPA, "Nhân viên Spa/Hotel"));
        pnlChuGiai.add(layNhan(KHO, "Nhân viên kho"));
        pnlChuGiai.add(layNhan(QUAN_LY, "Cửa hàng trưởng"));
        pnlChuGiai.add(layNhan(NGHI_PHEP, "Nghỉ phép"));
        // Cảnh báo.
        JLabel lblCanhBao = new JLabel("Viền đỏ = ca thiếu người");
        lblCanhBao.setForeground(NGHI_PHEP);
        lblCanhBao.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        pnlChuGiai.add(lblCanhBao);
        return pnlChuGiai;
    }

    private static JPanel layNhan(Color giaTriDangXet, String vanBan) {
        JPanel pnlVungDangXet = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlVungDangXet.setOpaque(false);
        // Hộp.
        JPanel pnlHop = new JPanel();
        pnlHop.setBackground(giaTriDangXet);
        pnlHop.setPreferredSize(new Dimension(44, 14));
        pnlHop.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 40)));
        // Dòng đang xét.
        JLabel lblDongDangXet = new JLabel(vanBan);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlVungDangXet.add(pnlHop);
        pnlVungDangXet.add(lblDongDangXet);
        return pnlVungDangXet;
    }

    // ───────────── Lưới
    private void dungLai() {
        txtTruongNgay.setText(thuHai.format(NGAY));
        pnlLuoiVungChu.removeAll();
        // Lưới.
        JPanel pnlLuoi = new JPanel(new GridBagLayout());
        pnlLuoi.setBackground(Color.WHITE);
        // Giá trị đang xét.
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.fill = GridBagConstraints.BOTH;
        giaTriDangXet.insets = new Insets(0, 0, 0, 0);

        giaTriDangXet.gridy = 0;
        giaTriDangXet.gridx = 0;
        giaTriDangXet.weightx = 0;
        giaTriDangXet.weighty = 0;
        pnlLuoi.add(layOTieuDe("Ca làm", null, false), giaTriDangXet);
        for (int ngayDangXet = 0; ngayDangXet < 7; ngayDangXet++) {
            // Ngày.
            LocalDate ngay = thuHai.plusDays(ngayDangXet);
            giaTriDangXet.gridx = ngayDangXet + 1;
            giaTriDangXet.weightx = 1;
            pnlLuoi.add(
                    layOTieuDe(THU[ngayDangXet], ngay.format(NGAY), ngay.equals(LocalDate.now())),
                    giaTriDangXet);
        }
        for (int chuoi = 0; chuoi < LichLamViecService.CAC_CA_LAM_VIEC.length; chuoi++) {
            giaTriDangXet.gridy = chuoi + 1;
            giaTriDangXet.gridx = 0;
            giaTriDangXet.weightx = 0;
            giaTriDangXet.weighty = 1;
            pnlLuoi.add(layNhanDong(chuoi), giaTriDangXet);
            for (int ngayDangXet = 0; ngayDangXet < 7; ngayDangXet++) {
                giaTriDangXet.gridx = ngayDangXet + 1;
                giaTriDangXet.weightx = 1;
                pnlLuoi.add(layO(thuHai.plusDays(ngayDangXet), chuoi), giaTriDangXet);
            }
        }
        pnlLuoiVungChu.add(pnlLuoi, BorderLayout.CENTER);
        capNhatTomTat();
        pnlLuoiVungChu.revalidate();
        pnlLuoiVungChu.repaint();
    }

    private JPanel layOTieuDe(String tren, String duoi, boolean homNay) {
        JPanel pnlVungDangXet = new JPanel(new BorderLayout());
        pnlVungDangXet.setBackground(homNay ? new Color(0xD3, 0xE6, 0xFA) : TIEU_DE_NEN);
        pnlVungDangXet.setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(0xC9, 0xD6, 0xE5)));
        pnlVungDangXet.setPreferredSize(new Dimension(duoi == null ? 76 : 104, 52));
        // Dòng đang xét.
        JLabel lblDongDangXet =
                new JLabel(
                        "<html><div style='text-align:center'>"
                                + tren
                                + (duoi == null ? "" : "<br>" + duoi)
                                + "</div></html>",
                        SwingConstants.CENTER);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblDongDangXet.setForeground(TIEU_DE_CHU);
        pnlVungDangXet.add(lblDongDangXet, BorderLayout.CENTER);
        return pnlVungDangXet;
    }

    private JPanel layNhanDong(int caLamViec) {
        JPanel pnlVungDangXet = new JPanel(new BorderLayout());
        pnlVungDangXet.setBackground(DONG_NHAN_NEN);
        pnlVungDangXet.setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(0xE3, 0xE3, 0xB5)));
        // Dòng đang xét.
        JLabel lblDongDangXet =
                new JLabel(
                        "<html><div style='text-align:center'><b>"
                                + LichLamViecService.CAC_CA_LAM_VIEC[caLamViec]
                                + "</b><br><span style='font-size:9px;color:#666'>"
                                + LichLamViecService.CA_LAM_VIEC_CAC_THOI_GIAN[caLamViec]
                                + "</span></div></html>",
                        SwingConstants.CENTER);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlVungDangXet.add(lblDongDangXet, BorderLayout.CENTER);
        return pnlVungDangXet;
    }

    private boolean kiemTraHienThi(Muc suKien) {
        if (btnLamViec.isSelected() && suKien.kiemTraNghiPhep()) return false;
        if (btnNghiPhep.isSelected() && !suKien.kiemTraNghiPhep()) return false;
        if (cheDo == CheDo.CA_NHAN) return suKien.maNhanVien.equals(hienTaiNguoiDung);
        return true;
    }

    private JPanel layO(LocalDate ngay, int caLamViec) {
        JPanel pnlVungDangXet =
                new JPanel() {
                    /**
                     * Lấy kích thước ưu tiên trong lớp xử lý giao diện.
                     *
                     * @return kết quả lấy kích thước ưu tiên
                     */
                    @Override
                    public Dimension getPreferredSize() {
                        // Ngày đang xét.
                        Dimension ngayDangXet = super.getPreferredSize();
                        return new Dimension(
                                Math.max(104, ngayDangXet.width),
                                Math.max(130, ngayDangXet.height));
                    }
                };
        pnlVungDangXet.setLayout(new BoxLayout(pnlVungDangXet, BoxLayout.Y_AXIS));
        pnlVungDangXet.setBackground(
                ngay.equals(LocalDate.now()) ? new Color(0xFC, 0xFD, 0xFF) : Color.WHITE);
        // Ngắn 1.
        boolean ngan1 =
                cheDo != CheDo.CA_NHAN
                        && LichLamViecService.demCaLamViec(ngay, caLamViec)
                                < LichLamViecService.NHO_NHAT_NHAN_VIEN[caLamViec];
        pnlVungDangXet.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(0xE3, 0xE7, 0xEC)),
                        ngan1 && !btnNghiPhep.isSelected()
                                ? BorderFactory.createLineBorder(NGHI_PHEP, 2)
                                : new EmptyBorder(2, 2, 2, 2)));

        // Các mục.
        List<Muc> cacMuc = new ArrayList<>();
        for (Muc suKien :
                LichLamViecService.layO(
                        ngay,
                        caLamViec)) // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (kiemTraHienThi(suKien)) cacMuc.add(suKien);
        for (Muc suKien : cacMuc) {
            pnlVungDangXet.add(layThe(suKien));
            pnlVungDangXet.add(javax.swing.Box.createVerticalStrut(4));
        }
        if (ngan1 && !btnNghiPhep.isSelected()) {
            // Cần.
            long can =
                    LichLamViecService.NHO_NHAT_NHAN_VIEN[caLamViec]
                            - LichLamViecService.demCaLamViec(ngay, caLamViec);
            // Cảnh báo.
            JLabel lblCanhBao = new JLabel("Thiếu " + can + " người");
            lblCanhBao.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
            lblCanhBao.setForeground(NGHI_PHEP);
            lblCanhBao.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnlVungDangXet.add(lblCanhBao);
        }
        if (cheDo == CheDo.SUA) {
            // Thêm.
            JLabel lblThem = new JLabel("+ Thêm nhân viên");
            lblThem.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
            lblThem.setForeground(TIEU_DE_CHU);
            lblThem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblThem.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblThem.addMouseListener(
                    new MouseAdapter() {
                        /**
                         * Lấy chuột đã bấm trong lớp xử lý giao diện.
                         *
                         * @param suKien sự kiện
                         */
                        @Override
                        public void mouseClicked(MouseEvent suKien) {
                            themNhanVien(ngay, caLamViec);
                        }
                    });
            pnlVungDangXet.add(lblThem);
        }
        return pnlVungDangXet;
    }

    private JPanel layThe(Muc suKien) {
        // Nhân viên.
        Object[] nhanVien = layNhanVienCua(suKien.maNhanVien);
        // Tên.
        String ten = nhanVien == null ? suKien.maNhanVien : String.valueOf(nhanVien[1]);
        // Đầy đủ vị trí.
        String dayDuViTri = nhanVien == null ? "" : String.valueOf(nhanVien[4]);
        // Vị trí.
        String viTri =
                dayDuViTri.contains("bán hàng")
                        ? "Bán hàng"
                        : dayDuViTri.contains("Spa")
                                ? "Spa / Hotel"
                                : dayDuViTri.contains("kho") ? "Kho" : dayDuViTri;
        // Nền.
        Color nen =
                suKien.kiemTraNghiPhep()
                        ? NGHI_PHEP
                        : dayDuViTri.contains("Spa")
                                ? SPA
                                : dayDuViTri.contains("kho")
                                        ? KHO
                                        : dayDuViTri.contains("trưởng") ? QUAN_LY : BAN_HANG;
        // Chữ.
        Color chu = suKien.kiemTraNghiPhep() ? Color.WHITE : new Color(0x1B, 0x2A, 0x3A);
        // Thẻ.
        JPanel pnlThe = new JPanel(new BorderLayout());
        pnlThe.setBackground(nen);
        pnlThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0, 45)),
                        new EmptyBorder(5, 6, 5, 6)));
        pnlThe.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Nội dung.
        String noiDung =
                "<html><body style='width:88px'><b>"
                        + ten
                        + "</b><br>"
                        + viTri
                        + "<br>"
                        + (suKien.kiemTraNghiPhep()
                                ? "<b>Nghỉ phép</b>"
                                : LichLamViecService.CA_LAM_VIEC_CAC_THOI_GIAN[suKien.caLamViec])
                        + "</body></html>";
        // Dòng đang xét.
        JLabel lblDongDangXet = new JLabel(noiDung);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblDongDangXet.setForeground(chu);
        pnlThe.add(lblDongDangXet, BorderLayout.CENTER);
        pnlThe.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, pnlThe.getPreferredSize().height + 4));
        if (cheDo == CheDo.SUA) {
            pnlThe.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            pnlThe.setToolTipText("Bấm để bỏ " + ten + " khỏi ca này");
            pnlThe.addMouseListener(
                    new MouseAdapter() {
                        /**
                         * Lấy chuột đã bấm trong lớp xử lý giao diện.
                         *
                         * @param suKien2 sự kiện
                         */
                        @Override
                        public void mouseClicked(MouseEvent suKien2) {
                            xoaNhanVien(suKien, ten);
                        }
                    });
        }
        return pnlThe;
    }

    private static Object[] layNhanVienCua(String ma) {
        for (Object[] chuoi :
                DanhMucService
                        .layNhanVien()) // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (String.valueOf(chuoi[0]).equals(ma)) return chuoi;
        return null;
    }

    // ───────────── Phân lịch chuyên nghiệp
    private void themNhanVien(LocalDate ngay, int caLamViec) {
        moDialogPhanCa(ngay, caLamViec);
    }

    private void xoaNhanVien(Muc suKien, String ten) {
        if (!HopThoai.xacNhanNguyHiem(
                this,
                "Bỏ "
                        + ten
                        + " khỏi ca "
                        + LichLamViecService.CAC_CA_LAM_VIEC[suKien.caLamViec]
                        + " ngày "
                        + suKien.ngay.format(NGAY)
                        + "?",
                "Bỏ khỏi ca")) return;
        LichLamViecService.xoa(suKien);
        NhatKyHeThongService.ghiNhatKy(
                "Sửa",
                "Phân lịch: bỏ "
                        + ten
                        + " khỏi ca "
                        + LichLamViecService.CAC_CA_LAM_VIEC[suKien.caLamViec]
                        + " "
                        + suKien.ngay.format(NGAY));
        dungLai();
    }

    private void moDialogPhanCa(LocalDate ngay, int caLamViec) {
        java.awt.Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg =
                new JDialog(
                        owner,
                        "Phân ca: Ca "
                                + LichLamViecService.CAC_CA_LAM_VIEC[caLamViec]
                                + " ("
                                + LichLamViecService.CA_LAM_VIEC_CAC_THOI_GIAN[caLamViec]
                                + ") - Ngày "
                                + ngay.format(NGAY),
                        java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(0, 10));
        dlg.setSize(680, 500);
        dlg.setLocationRelativeTo(owner);

        // Danh sách nhân viên hiện tại trong ca
        List<Muc> hienCo = LichLamViecService.layO(ngay, caLamViec);
        java.util.Set<String> maHienCo = new java.util.HashSet<>();
        for (Muc m : hienCo) {
            if (!m.kiemTraNghiPhep()) maHienCo.add(m.maNhanVien);
        }

        // Tạo bảng nhân viên
        String[] columns = {
            "Chọn", "Mã NV", "Họ tên", "Chức vụ", "Giờ tuần này", "Trạng thái / Ghi chú"
        };
        DefaultTableModel model =
                new DefaultTableModel(columns, 0) {
                    @Override
                    public Class<?> getColumnClass(int col) {
                        return col == 0 ? Boolean.class : String.class;
                    }

                    @Override
                    public boolean isCellEditable(int row, int col) {
                        return col == 0;
                    }
                };

        for (Object[] nv : DanhMucService.layNhanVien()) {
            String ma = String.valueOf(nv[0]);
            String ten = String.valueOf(nv[1]);
            String cv = String.valueOf(nv[4]);
            String trangThai = String.valueOf(nv[6]);
            if ("Nghỉ việc".equals(trangThai)) continue;

            boolean daChon = maHienCo.contains(ma);
            int gioTuan = LichLamViecService.tinhGioTrongTuan(ma, thuHai);
            String canhBao = "";

            // Kiểm tra nghỉ phép ngày này
            boolean nghiPhep =
                    LichLamViecService.layKhoang(ngay, ngay.plusDays(1)).stream()
                            .anyMatch(m -> m.maNhanVien.equals(ma) && m.kiemTraNghiPhep());
            if (nghiPhep) {
                canhBao = "[!] Đang nghỉ phép";
            } else {
                // Kiểm tra ca khác cùng ngày
                for (int c = 0; c < 3; c++) {
                    if (c != caLamViec) {
                        int finalC = c;
                        boolean caKhac =
                                LichLamViecService.layO(ngay, finalC).stream()
                                        .anyMatch(
                                                m ->
                                                        m.maNhanVien.equals(ma)
                                                                && !m.kiemTraNghiPhep());
                        if (caKhac) {
                            canhBao = "[!] Đã có ca " + LichLamViecService.CAC_CA_LAM_VIEC[finalC];
                            break;
                        }
                    }
                }
            }
            if (canhBao.isEmpty() && gioTuan >= 40) {
                canhBao = "[!] Đã làm " + gioTuan + "h tuần này";
            }

            model.addRow(new Object[] {daChon, ma, ten, cv, gioTuan + " giờ", canhBao});
        }

        JTable table = new JTable(model);
        com.petstation.gui.components.common.DinhDangBang.apDung(table);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(75);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Top filter bar
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlTop.setOpaque(false);
        JTextField txtSearch = new JTextField(14);
        txtSearch.setToolTipText("Tìm tên hoặc mã nhân viên...");
        JComboBox<String> cboRole =
                new JComboBox<>(
                        new String[] {
                            "Tất cả chức vụ", "Bán hàng", "Spa", "Kho", "Cửa hàng trưởng"
                        });

        Runnable filterAction =
                () -> {
                    String text = txtSearch.getText().trim().toLowerCase();
                    String role = (String) cboRole.getSelectedItem();
                    sorter.setRowFilter(
                            new RowFilter<DefaultTableModel, Integer>() {
                                @Override
                                public boolean include(
                                        Entry<? extends DefaultTableModel, ? extends Integer>
                                                entry) {
                                    String ma = entry.getStringValue(1).toLowerCase();
                                    String ten = entry.getStringValue(2).toLowerCase();
                                    String cv = entry.getStringValue(3);
                                    boolean matchText =
                                            text.isEmpty()
                                                    || ma.contains(text)
                                                    || ten.contains(text);
                                    boolean matchRole =
                                            "Tất cả chức vụ".equals(role)
                                                    || cv.toLowerCase()
                                                            .contains(role.toLowerCase());
                                    return matchText && matchRole;
                                }
                            });
                };

        txtSearch
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                filterAction.run();
                            }

                            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                filterAction.run();
                            }

                            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                filterAction.run();
                            }
                        });
        cboRole.addActionListener(e -> filterAction.run());

        pnlTop.add(new JLabel("Tìm:"));
        pnlTop.add(txtSearch);
        pnlTop.add(new JLabel("Chức vụ:"));
        pnlTop.add(cboRole);

        // Bottom action bar
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 0));
        pnlBottom.setBorder(new EmptyBorder(8, 12, 10, 12));
        JLabel lblInfo =
                new JLabel(
                        "Yêu cầu tối thiểu: "
                                + LichLamViecService.NHO_NHAT_NHAN_VIEN[caLamViec]
                                + " nhân viên cho ca này.");
        lblInfo.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 12));
        lblInfo.setForeground(MauUngDung.VAN_BAN_XAM);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        NutDinhDang btnSave = new NutDinhDang("Lưu phân ca", NutDinhDang.KieuNut.CHINH);
        NutDinhDang btnCancel = new NutDinhDang("Đóng", NutDinhDang.KieuNut.TRONG_SUOT);

        btnSave.addActionListener(
                e -> {
                    int selectedCount = 0;
                    List<String> toAdd = new ArrayList<>();
                    List<String> toRemove = new ArrayList<>();

                    for (int r = 0; r < model.getRowCount(); r++) {
                        boolean checked = Boolean.TRUE.equals(model.getValueAt(r, 0));
                        String ma = String.valueOf(model.getValueAt(r, 1));
                        if (checked) {
                            selectedCount++;
                            if (!maHienCo.contains(ma)) toAdd.add(ma);
                        } else {
                            if (maHienCo.contains(ma)) toRemove.add(ma);
                        }
                    }

                    if (selectedCount < LichLamViecService.NHO_NHAT_NHAN_VIEN[caLamViec]) {
                        if (!HopThoai.xacNhan(
                                dlg,
                                "Ca này có "
                                        + selectedCount
                                        + " người (ít hơn mức tối thiểu "
                                        + LichLamViecService.NHO_NHAT_NHAN_VIEN[caLamViec]
                                        + "). Vẫn tiếp tục lưu?")) {
                            return;
                        }
                    }

                    for (String ma : toAdd) {
                        String err = LichLamViecService.phanCong(ngay, caLamViec, ma);
                        if (err != null) {
                            HopThoai.hienThiCanhBao(dlg, "Lỗi phân ca " + ma + ": " + err);
                        }
                    }
                    for (String ma : toRemove) {
                        LichLamViecService.Muc m =
                                hienCo.stream()
                                        .filter(x -> x.maNhanVien.equals(ma))
                                        .findFirst()
                                        .orElse(null);
                        if (m != null) LichLamViecService.xoa(m);
                    }

                    NhatKyHeThongService.ghiNhatKy(
                            "Phân lịch",
                            "Cập nhật ca "
                                    + LichLamViecService.CAC_CA_LAM_VIEC[caLamViec]
                                    + " ngày "
                                    + ngay.format(NGAY));
                    dlg.dispose();
                    dungLai();
                });

        btnCancel.addActionListener(e -> dlg.dispose());
        pnlButtons.add(btnCancel);
        pnlButtons.add(btnSave);

        pnlBottom.add(lblInfo, BorderLayout.WEST);
        pnlBottom.add(pnlButtons, BorderLayout.EAST);

        dlg.add(pnlTop, BorderLayout.NORTH);
        dlg.add(new JScrollPane(table), BorderLayout.CENTER);
        dlg.add(pnlBottom, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void saoChepLichTuan() {
        LocalDate tuanTruoc = thuHai.minusWeeks(1);
        if (!HopThoai.xacNhan(
                this,
                "Sao chép toàn bộ phân ca từ tuần "
                        + tuanTruoc.format(NGAY)
                        + " sang tuần "
                        + thuHai.format(NGAY)
                        + "?")) return;
        String thongBao = LichLamViecService.saoChepLichTuan(tuanTruoc, thuHai);
        HopThoai.hienThiThongTin(this, thongBao);
        dungLai();
    }

    private void hoanDoiCaNhanVien() {
        java.awt.Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg =
                new JDialog(
                        owner,
                        "Hoán đổi ca làm việc giữa 2 nhân viên",
                        java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(0, 12));
        dlg.setSize(520, 320);
        dlg.setLocationRelativeTo(owner);

        JPanel pnlCenter = new JPanel(new java.awt.GridLayout(2, 1, 0, 10));
        pnlCenter.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Nhóm 1
        JPanel pnl1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnl1.setBorder(BorderFactory.createTitledBorder("Ca thứ nhất"));
        JComboBox<String> cboDay1 = new JComboBox<>();
        JComboBox<String> cboShift1 = new JComboBox<>(LichLamViecService.CAC_CA_LAM_VIEC);
        JComboBox<String> cboStaff1 = new JComboBox<>();
        for (int i = 0; i < 7; i++)
            cboDay1.addItem(THU[i] + " (" + thuHai.plusDays(i).format(NGAY) + ")");

        Runnable napNv1 =
                () -> {
                    cboStaff1.removeAllItems();
                    int dayIdx = cboDay1.getSelectedIndex();
                    int shiftIdx = cboShift1.getSelectedIndex();
                    if (dayIdx >= 0 && shiftIdx >= 0) {
                        LocalDate d = thuHai.plusDays(dayIdx);
                        for (Muc m : LichLamViecService.layO(d, shiftIdx)) {
                            if (!m.kiemTraNghiPhep()) {
                                Object[] nv = layNhanVienCua(m.maNhanVien);
                                cboStaff1.addItem(
                                        m.maNhanVien + " - " + (nv == null ? m.maNhanVien : nv[1]));
                            }
                        }
                    }
                };
        cboDay1.addActionListener(e -> napNv1.run());
        cboShift1.addActionListener(e -> napNv1.run());
        napNv1.run();

        pnl1.add(new JLabel("Ngày:"));
        pnl1.add(cboDay1);
        pnl1.add(new JLabel("Ca:"));
        pnl1.add(cboShift1);
        pnl1.add(new JLabel("Nhân viên:"));
        pnl1.add(cboStaff1);

        // Nhóm 2
        JPanel pnl2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnl2.setBorder(BorderFactory.createTitledBorder("Ca thứ hai (hoán đổi cùng)"));
        JComboBox<String> cboDay2 = new JComboBox<>();
        JComboBox<String> cboShift2 = new JComboBox<>(LichLamViecService.CAC_CA_LAM_VIEC);
        JComboBox<String> cboStaff2 = new JComboBox<>();
        for (int i = 0; i < 7; i++)
            cboDay2.addItem(THU[i] + " (" + thuHai.plusDays(i).format(NGAY) + ")");

        Runnable napNv2 =
                () -> {
                    cboStaff2.removeAllItems();
                    int dayIdx = cboDay2.getSelectedIndex();
                    int shiftIdx = cboShift2.getSelectedIndex();
                    if (dayIdx >= 0 && shiftIdx >= 0) {
                        LocalDate d = thuHai.plusDays(dayIdx);
                        for (Muc m : LichLamViecService.layO(d, shiftIdx)) {
                            if (!m.kiemTraNghiPhep()) {
                                Object[] nv = layNhanVienCua(m.maNhanVien);
                                cboStaff2.addItem(
                                        m.maNhanVien + " - " + (nv == null ? m.maNhanVien : nv[1]));
                            }
                        }
                    }
                };
        cboDay2.addActionListener(e -> napNv2.run());
        cboShift2.addActionListener(e -> napNv2.run());
        napNv2.run();

        pnl2.add(new JLabel("Ngày:"));
        pnl2.add(cboDay2);
        pnl2.add(new JLabel("Ca:"));
        pnl2.add(cboShift2);
        pnl2.add(new JLabel("Nhân viên:"));
        pnl2.add(cboStaff2);

        pnlCenter.add(pnl1);
        pnlCenter.add(pnl2);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        NutDinhDang btnOk = new NutDinhDang("Xác nhận hoán đổi", NutDinhDang.KieuNut.CHINH);
        NutDinhDang btnClose = new NutDinhDang("Đóng", NutDinhDang.KieuNut.TRONG_SUOT);

        btnOk.addActionListener(
                e -> {
                    if (cboStaff1.getItemCount() == 0 || cboStaff2.getItemCount() == 0) {
                        HopThoai.hienThiCanhBao(dlg, "Cả hai ca phải có nhân viên để hoán đổi.");
                        return;
                    }
                    String ma1 = String.valueOf(cboStaff1.getSelectedItem()).split(" ")[0];
                    String ma2 = String.valueOf(cboStaff2.getSelectedItem()).split(" ")[0];
                    LocalDate d1 = thuHai.plusDays(cboDay1.getSelectedIndex());
                    LocalDate d2 = thuHai.plusDays(cboDay2.getSelectedIndex());
                    int s1 = cboShift1.getSelectedIndex();
                    int s2 = cboShift2.getSelectedIndex();

                    try {
                        String err = LichLamViecService.hoanDoiCa(d1, s1, ma1, d2, s2, ma2);
                        if (err != null) {
                            HopThoai.hienThiCanhBao(dlg, err);
                            return;
                        }
                        HopThoai.hienThiThanhCong(dlg, "Đã hoán đổi ca giữa " + ma1 + " và " + ma2);
                        dlg.dispose();
                        dungLai();
                    } catch (Exception ex) {
                        HopThoai.hienThiCanhBao(dlg, ex.getMessage());
                    }
                });
        btnClose.addActionListener(e -> dlg.dispose());

        pnlBtns.add(btnClose);
        pnlBtns.add(btnOk);

        dlg.add(pnlCenter, BorderLayout.CENTER);
        dlg.add(pnlBtns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void khoaLichTuan() {
        if (!HopThoai.xacNhan(
                this,
                "Khóa lịch tuần "
                        + thuHai.format(NGAY)
                        + " để công bố chính thức? Sau khi khóa, nhân viên chỉ có thể xem hoặc gửi"
                        + " yêu cầu đổi ca.")) return;
        LichLamViecService.khoaLichTuan(thuHai);
        HopThoai.hienThiThanhCong(this, "Đã khóa lịch tuần thành công.");
        dungLai();
    }

    private void guiYeuCauDoiCaCaNhan() {
        // Lấy các ca của bản thân trong tuần
        List<Muc> caCuaToi =
                LichLamViecService.layLichTrongTuan(thuHai).stream()
                        .filter(m -> m.maNhanVien.equals(hienTaiNguoiDung) && !m.kiemTraNghiPhep())
                        .toList();
        if (caCuaToi.isEmpty()) {
            HopThoai.hienThiCanhBao(
                    this, "Bạn chưa có ca làm việc nào trong tuần này để yêu cầu đổi.");
            return;
        }

        List<Object[]> dsCaCuaToi =
                caCuaToi.stream()
                        .map(
                                m ->
                                        new Object[] {
                                            m.ngay.format(NGAY) + "|" + m.caLamViec,
                                            m.ngay.format(NGAY)
                                                    + " - Ca "
                                                    + LichLamViecService.CAC_CA_LAM_VIEC[
                                                            m.caLamViec]
                                        })
                        .toList();

        List<Object[]> dsDongNghiep =
                DanhMucService.layLuaChonNhanVien().stream()
                        .filter(s -> !s.startsWith(hienTaiNguoiDung + " "))
                        .map(s -> new Object[] {s.substring(0, s.indexOf(' ')), s})
                        .toList();

        var giaTri =
                com.petstation.gui.components.common.BieuMauSQL.layYeuCauNhap(
                        this,
                        "Yêu cầu đổi ca làm việc",
                        com.petstation.gui.components.TruongNghiepVu.taoLuaChon(
                                "my_shift", "Ca của bạn muốn đổi", dsCaCuaToi),
                        com.petstation.gui.components.TruongNghiepVu.taoLuaChon(
                                "target_staff", "Đồng nghiệp muốn đổi cùng", dsDongNghiep),
                        com.petstation.gui.components.TruongNghiepVu.layTruongVanBan(
                                "reason", "Lý do đổi ca", ""));
        if (giaTri == null) return;
        try {
            com.petstation.service.PheDuyetService.guiThaoTac(
                    com.petstation.service.PheDuyetService.Loai.LEAVE,
                    giaTri.get("reason"),
                    "Đổi " + giaTri.get("my_shift"),
                    "Đổi với " + giaTri.get("target_staff"),
                    "SHIFT_SWAP",
                    com.petstation.service.PhienDangNhapService.layIdNhanVien(),
                    giaTri.get("target_staff"));
            HopThoai.hienThiThanhCong(this, "Đã gửi yêu cầu đổi ca đến Cửa hàng trưởng.");
        } catch (RuntimeException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
        }
    }

    private void capNhatTomTat() {
        // Kết thúc.
        LocalDate ketThuc = thuHai.plusDays(6);
        // Tuần.
        String tuan = "Tuần " + thuHai.format(NGAY) + " – " + ketThuc.format(NGAY);
        if (cheDo == CheDo.CA_NHAN) {
            // Bản thân.
            Object[] banThan = null;
            for (Object[] chuoi : DanhMucService.layNhanVien())
                if (String.valueOf(chuoi[0]).equals(hienTaiNguoiDung)) banThan = chuoi;
            if (banThan == null) {
                lblTomTat.setText(
                        tuan + " · Chưa tìm thấy hồ sơ nhân viên của bạn nên chưa có ca nào.");
                return;
            }
            // Cá nhân.
            final Object[] caNhan = banThan;
            // Rời khỏi.
            long roiKhoi =
                    LichLamViecService.layLichTrongTuan(thuHai).stream()
                            .filter(
                                    suKien ->
                                            suKien.maNhanVien.equals(caNhan[0])
                                                    && suKien.kiemTraNghiPhep())
                            .count();
            lblTomTat.setText(
                    tuan
                            + " · Tổng giờ làm trong tháng "
                            + thuHai.getMonthValue()
                            + ": "
                            + LichLamViecService.tinhGioTrongThang(
                                    String.valueOf(banThan[0]), thuHai.plusDays(3))
                            + " giờ · Nghỉ phép tuần này: "
                            + roiKhoi
                            + " ca");
            return;
        }
        // Ngắn 1.
        int ngan1 = 0;
        for (int ngayDangXet = 0; ngayDangXet < 7; ngayDangXet++)
            for (int chuoi = 0; chuoi < 3; chuoi++)
                if (LichLamViecService.demCaLamViec(thuHai.plusDays(ngayDangXet), chuoi)
                        < LichLamViecService.NHO_NHAT_NHAN_VIEN[chuoi]) ngan1++;
        lblTomTat.setText(
                tuan
                        + " · "
                        + (ngan1 == 0 ? "Đủ người ở mọi ca" : ngan1 + " ca thiếu người (viền đỏ)")
                        + (cheDo == CheDo.SUA
                                ? " · Bấm \"+ Thêm nhân viên\" để phân ca, bấm vào thẻ để bỏ khỏi"
                                        + " ca"
                                : ""));
    }

    // ───────────── In lịch
    private void inLichLamViec() {
        // Công việc.
        PrinterJob congViec = PrinterJob.getPrinterJob();
        congViec.setJobName("Lịch làm việc " + thuHai.format(NGAY));
        congViec.setPrintable(
                new Printable() {
                    /**
                     * In trong lớp xử lý giao diện.
                     *
                     * @param doHoa đồ họa
                     * @param dinhDangTrang định dạng trang
                     * @param trang trang
                     * @return kết quả in
                     * @throws PrinterException nếu thao tác không thể hoàn tất
                     */
                    @Override
                    public int print(Graphics doHoa, PageFormat dinhDangTrang, int trang)
                            throws PrinterException {
                        if (trang > 0) return NO_SUCH_PAGE;
                        // Đích.
                        Component dich = pnlLuoiVungChu;
                        // Đồ họa 2.
                        Graphics2D doHoa2 = (Graphics2D) doHoa;
                        doHoa2.translate(
                                dinhDangTrang.getImageableX(), dinhDangTrang.getImageableY());
                        // Gốc ngang.
                        double gocNgang = dinhDangTrang.getImageableWidth() / dich.getWidth();
                        // Gốc dọc.
                        double gocDoc = dinhDangTrang.getImageableHeight() / dich.getHeight();
                        // Chuỗi.
                        double chuoi = Math.min(1, Math.min(gocNgang, gocDoc));
                        doHoa2.scale(chuoi, chuoi);
                        dich.printAll(doHoa2);
                        return PAGE_EXISTS;
                    }
                });
        try {
            if (congViec.printDialog()) {
                congViec.print();
                NhatKyHeThongService.ghiNhatKy(
                        "In lịch", "Lịch làm việc tuần " + thuHai.format(NGAY));
            }
        } catch (PrinterException ngoaiLe) {
            HopThoai.hienThiLoi(this, "Không in được: " + ngoaiLe.getMessage());
        } catch (java.awt.HeadlessException ngoaiLe) {
            HopThoai.hienThiLoi(this, "Không tìm thấy máy in.");
        }
    }

    @Override
    public void xuLyHienThi() {
        dungLai();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Lịch làm việc theo tuần";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return switch (cheDo) {
            case HIEN_THI_TAT_CA ->
                    "Lịch cả cửa hàng theo tuần. Dùng Trở về / Tiếp, nhập ngày hoặc Hiện tại để đổi"
                            + " tuần. Ô viền đỏ là ca thiếu người.";
            case CA_NHAN ->
                    "Ca của bạn trong tuần, tổng giờ trong tháng. Bấm \"Đăng ký nghỉ phép\" để gửi"
                            + " yêu cầu sang Cửa hàng trưởng duyệt.";
            case SUA ->
                    "Chọn tuần rồi bấm \"+ Thêm nhân viên\" trong ô để phân ca; bấm vào thẻ nhân"
                            + " viên để bỏ khỏi ca. Ô viền đỏ là ca thiếu người.";
        };
    }
}
