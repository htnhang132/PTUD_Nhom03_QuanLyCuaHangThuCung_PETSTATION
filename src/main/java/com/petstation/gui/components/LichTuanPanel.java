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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

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
        if (cheDo == CheDo.CA_NHAN) {
            // Yêu cầu nhập.
            NutDinhDang btnYeuCauNhap =
                    new NutDinhDang("Đăng ký nghỉ phép", NutDinhDang.KieuNut.PHU);
            btnYeuCauNhap.addActionListener(suKien -> ThaoTacNhanh.layNghiPhep(this));
            pnlThanh.add(btnYeuCauNhap);
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

    // ───────────── Phân lịch
    private void themNhanVien(LocalDate ngay, int caLamViec) {
        // Các lựa chọn.
        List<String> cacLuaChon = new ArrayList<>();
        for (String doiTuong : DanhMucService.layLuaChonNhanVien()) {
            // Mã.
            String ma = doiTuong.substring(0, doiTuong.indexOf(' '));
            if (LichLamViecService.layO(ngay, caLamViec).stream()
                    .noneMatch(suKien -> suKien.maNhanVien.equals(ma))) cacLuaChon.add(doiTuong);
        }
        if (cacLuaChon.isEmpty()) {
            HopThoai.hienThiThongTin(this, "Mọi nhân viên đã có trong ca này.");
            return;
        }
        // Chọn.
        Object chon =
                JOptionPane.showInputDialog(
                        this,
                        "Thêm nhân viên vào ca "
                                + LichLamViecService.CAC_CA_LAM_VIEC[caLamViec]
                                + " ngày "
                                + ngay.format(NGAY)
                                + ":",
                        "Phân lịch",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        cacLuaChon.toArray(),
                        cacLuaChon.get(0));
        if (chon == null) return;
        // Mã.
        String ma = String.valueOf(chon).substring(0, String.valueOf(chon).indexOf(' '));
        // Lỗi.
        String loi = LichLamViecService.phanCong(ngay, caLamViec, ma);
        if (loi != null) {
            HopThoai.hienThiCanhBao(this, loi);
            return;
        }
        NhatKyHeThongService.ghiNhatKy(
                "Sửa",
                "Phân lịch: "
                        + chon
                        + " vào ca "
                        + LichLamViecService.CAC_CA_LAM_VIEC[caLamViec]
                        + " "
                        + ngay.format(NGAY));
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
