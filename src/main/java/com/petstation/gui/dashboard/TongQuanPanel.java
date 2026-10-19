package com.petstation.gui.dashboard;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.BieuDoCot;
import com.petstation.gui.components.common.DinhDangBang;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.service.CaThuNganService;
import com.petstation.service.DanhMucService;
import com.petstation.service.PheDuyetService;
import com.petstation.service.PhienDangNhapService;
import com.petstation.util.TienTe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho tổng quan vùng hiển thị.
 *
 * <p>Màn hình chính: lối tắt việc hằng ngày theo quyền, số liệu thật của ca/kho/phê duyệt, biểu đồ
 * doanh thu 7 ngày.
 */
public final class TongQuanPanel extends CoSoPanel implements HoTroThanhCongCu {
    /** Bộ điều hướng. */
    private final Consumer<MaManHinh> boDieuHuong;

    /** Có thể truy cập. */
    private final Predicate<MaManHinh> coTheTruyCap;

    /** Ca làm việc. */
    private final CaThuNganService caLamViec;

    /** Các phím tắt. */
    private final JPanel pnlCacPhimTat = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

    /** Các chỉ số. */
    private final JPanel pnlCacChiSo = new JPanel(new GridLayout(1, 4, 14, 0));

    /** Biểu đồ. */
    private final BieuDoCot bieuDo = new BieuDoCot();

    /** Trên mô hình. */
    private final DefaultTableModel trenMoHinh =
            new DefaultTableModel(new String[] {"#", "Sản phẩm", "Đã bán", "Doanh thu"}, 0) {
                /**
                 * Kiểm tra ô có thể sửa trong lớp xử lý giao diện.
                 *
                 * @param dongDuLieu dòng dữ liệu
                 * @param giaTriDangXet giá trị đang xét
                 * @return kết quả kiểm tra điều kiện xử lý
                 */
                @Override
                public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                    return false;
                }
            };

    /**
     * Khởi tạo TongQuanPanel với các giá trị được cung cấp.
     *
     * @param boDieuHuong bộ điều hướng
     * @param coTheTruyCap có thể truy cập
     * @param caLamViec ca làm việc
     */
    public TongQuanPanel(
            Consumer<MaManHinh> boDieuHuong,
            Predicate<MaManHinh> coTheTruyCap,
            CaThuNganService caLamViec) {
        this.boDieuHuong = boDieuHuong;
        this.coTheTruyCap = coTheTruyCap;
        this.caLamViec = caLamViec;
        setLayout(new BorderLayout(0, 14));

        // Đầu.
        JPanel pnlDau = taoVungTrongSuot(new BorderLayout());
        // Tiêu đề.
        JLabel lblTieuDe = taoTieuDeMuc("Tổng quan hôm nay");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 22));
        // Ngày.
        String ngay =
                LocalDate.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "EEEE, dd/MM/yyyy", new Locale("vi", "VN")));
        // Nhãn ngày.
        JLabel lblNhanNgay = new JLabel(ngay.substring(0, 1).toUpperCase() + ngay.substring(1));
        lblNhanNgay.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        lblNhanNgay.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlDau.add(lblTieuDe, BorderLayout.WEST);
        pnlDau.add(lblNhanNgay, BorderLayout.EAST);

        pnlCacPhimTat.setOpaque(false);
        pnlCacChiSo.setOpaque(false);
        // Trên.
        JPanel pnlTren = taoVungTrongSuot(new BorderLayout(0, 12));
        pnlTren.add(pnlDau, BorderLayout.NORTH);
        pnlTren.add(pnlCacPhimTat, BorderLayout.CENTER);
        pnlTren.add(pnlCacChiSo, BorderLayout.SOUTH);
        add(pnlTren, BorderLayout.NORTH);

        // Biểu đồ thẻ.
        JPanel pnlBieuDoThe = new JPanel(new BorderLayout(0, 6));
        pnlBieuDoThe.setBackground(Color.WHITE);
        pnlBieuDoThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(12, 14, 10, 14)));
        // Kiểu cột.
        JLabel lblKieuCot = new JLabel("Doanh thu 7 ngày qua");
        lblKieuCot.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        pnlBieuDoThe.add(lblKieuCot, BorderLayout.NORTH);
        pnlBieuDoThe.add(bieuDo, BorderLayout.CENTER);

        // Bảng.
        JTable tblBang = new JTable(trenMoHinh);
        DinhDangBang.apDung(tblBang);
        tblBang.setAutoCreateRowSorter(false);
        tblBang.getColumnModel().getColumn(0).setPreferredWidth(36);
        tblBang.getColumnModel().getColumn(1).setPreferredWidth(190);
        tblBang.getColumnModel().getColumn(3).setPreferredWidth(120);
        // Khung cuộn.
        JScrollPane scrKhungCuon = new JScrollPane(tblBang);
        scrKhungCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrKhungCuon.getViewport().setBackground(Color.WHITE);
        // Trên thẻ.
        JPanel pnlTrenThe = new JPanel(new BorderLayout(0, 6));
        pnlTrenThe.setOpaque(false);
        // Gợi ý.
        JLabel lblGoiY = new JLabel("Sản phẩm bán chạy");
        lblGoiY.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        pnlTrenThe.add(lblGoiY, BorderLayout.NORTH);
        pnlTrenThe.add(scrKhungCuon, BorderLayout.CENTER);

        // Giữa.
        JPanel pnlGiua = new JPanel(new GridLayout(1, 2, 14, 0));
        pnlGiua.setOpaque(false);
        pnlGiua.add(pnlBieuDoThe);
        pnlGiua.add(pnlTrenThe);
        add(pnlGiua, BorderLayout.CENTER);
        caLamViec.themBoLangNghe(this::lamMoi);
        lamMoi();
    }

    /**
     * Thêm phím tắt trong TongQuanPanel.
     *
     * @param vanBan văn bản
     * @param dich đích
     * @param kieu kiểu
     */
    private void themPhimTat(String vanBan, MaManHinh dich, NutDinhDang.KieuNut kieu) {
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (!coTheTruyCap.test(dich)) return;
        // Giá trị sau.
        NutDinhDang btnGiaTriSau = new NutDinhDang(vanBan, kieu);
        btnGiaTriSau.setPreferredSize(new Dimension(Math.max(110, vanBan.length() * 9 + 28), 42));
        btnGiaTriSau.addActionListener(suKien -> boDieuHuong.accept(dich));
        pnlCacPhimTat.add(btnGiaTriSau);
    }

    /** Làm mới trong TongQuanPanel. */
    private void lamMoi() {
        pnlCacPhimTat.removeAll();
        themPhimTat("Lập hóa đơn (F2)", MaManHinh.POS, NutDinhDang.KieuNut.CHINH);
        themPhimTat("Tạo lịch hẹn Spa", MaManHinh.SPA_SCHEDULE, NutDinhDang.KieuNut.PHU);
        themPhimTat("Đặt phòng Hotel", MaManHinh.HOTEL_BOOKING, NutDinhDang.KieuNut.PHU);
        themPhimTat(
                caLamViec.kiemTraMo() ? "Kết ca" : "Mở ca",
                MaManHinh.CASH_SHIFT,
                NutDinhDang.KieuNut.TRONG_SUOT);
        themPhimTat("Nhập kho", MaManHinh.PURCHASE, NutDinhDang.KieuNut.TRONG_SUOT);
        // Đang chờ.
        long dangCho = PheDuyetService.demChoDuyet();
        themPhimTat(
                dangCho > 0 ? "Phê duyệt (" + dangCho + ")" : "Phê duyệt",
                MaManHinh.APPROVALS,
                dangCho > 0 ? NutDinhDang.KieuNut.NGUY_HIEM : NutDinhDang.KieuNut.TRONG_SUOT);
        pnlCacPhimTat.revalidate();
        pnlCacPhimTat.repaint();

        pnlCacChiSo.removeAll();
        // Doanh thu.
        long doanhThu =
                caLamViec.layDoanhThuTienMat()
                        + caLamViec.layDoanhThuChuyenKhoan()
                        + caLamViec.layDoanhThuThe();
        pnlCacChiSo.add(
                layChiSoThe(
                        caLamViec.kiemTraMo() ? "Doanh thu ca hiện tại" : "Chưa mở ca",
                        caLamViec.kiemTraMo() ? TienTe.dinhDang(doanhThu) : "—",
                        caLamViec.kiemTraMo() ? caLamViec.layTenCa() : "Mở ca để bán hàng",
                        MauUngDung.XANH_LA));
        pnlCacChiSo.add(
                layChiSoThe(
                        "Hóa đơn trong ca",
                        caLamViec.kiemTraMo() ? String.valueOf(caLamViec.demHoaDon()) : "—",
                        "",
                        MauUngDung.XANH_DUONG));
        pnlCacChiSo.add(
                layChiSoThe(
                        "Sản phẩm tồn thấp",
                        String.valueOf(layThapTonKho()),
                        "Cần nhập thêm",
                        MauUngDung.DO_THUONG_HIEU));
        pnlCacChiSo.add(
                layChiSoThe(
                        "Yêu cầu chờ duyệt",
                        String.valueOf(dangCho),
                        dangCho > 0 ? "Cần Cửa hàng trưởng xử lý" : "Không có",
                        MauUngDung.CAM));
        pnlCacChiSo.revalidate();
        pnlCacChiSo.repaint();

        // Số ngày.
        String[] soNgay = new String[7];
        // Các giá trị.
        double[] cacGiaTri = new double[7];
        // Doanh thu theo ngày.
        java.util.Map<java.time.LocalDate, Double> doanhThuTheoNgay = new java.util.HashMap<>();
        // Giới hạn thao tác theo quyền và phiên đăng nhập hiện tại.
        if (PhienDangNhapService.layPhien().kiemTraCo(com.petstation.model.Quyen.REPORT_VIEW_ALL)
                || PhienDangNhapService.layPhien()
                        .kiemTraCo(com.petstation.model.Quyen.REPORT_VIEW_OWN_SHIFT)) {
            for (Object[] dong : com.petstation.service.ThongKeService.layDoanhThuTheoNgay(7))
                doanhThuTheoNgay.put(
                        ((java.sql.Date) dong[0]).toLocalDate(), ((Number) dong[1]).doubleValue());
        }
        for (int chiSo = 0; chiSo < 7; chiSo++) {
            // Ngày.
            java.time.LocalDate ngay = LocalDate.now().minusDays(6 - chiSo);
            soNgay[chiSo] = ngay.format(DateTimeFormatter.ofPattern("dd/MM"));
            cacGiaTri[chiSo] = doanhThuTheoNgay.getOrDefault(ngay, 0.0);
        }
        bieuDo.datDuLieu(soNgay, cacGiaTri, true, "");

        trenMoHinh.setRowCount(0);
        // Hạng.
        int hang = 1;
        // Giới hạn thao tác theo quyền và phiên đăng nhập hiện tại.
        if (PhienDangNhapService.layPhien().kiemTraCo(com.petstation.model.Quyen.REPORT_VIEW_ALL)
                || PhienDangNhapService.layPhien()
                        .kiemTraCo(com.petstation.model.Quyen.REPORT_VIEW_OWN_SHIFT)) {
            for (Object[] sanPham : com.petstation.service.ThongKeService.laySanPhamBanChay(7)) {
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (hang > 5) break;
                trenMoHinh.addRow(
                        new Object[] {
                            hang++,
                            sanPham[1],
                            sanPham[2],
                            TienTe.dinhDang(((Number) sanPham[3]).longValue())
                        });
            }
        }
    }

    /**
     * Lấy thấp tồn kho trong TongQuanPanel.
     *
     * @return kết quả lấy thấp tồn kho
     */
    private static long layThapTonKho() {
        // Số phần tử.
        long soPhanTu = 0;
        for (Object[] vungDangXet : DanhMucService.laySanPham()) {
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (TienTe.doc(String.valueOf(vungDangXet[8]))
                    <= Math.max(0, TienTe.doc(String.valueOf(vungDangXet[6])))) soPhanTu++;
        }
        return soPhanTu;
    }

    /** Xử lý hiển thị trong TongQuanPanel. */
    @Override
    public void xuLyHienThi() {
        lamMoi();
    }

    /**
     * Lấy tiêu đề trợ giúp trong TongQuanPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Màn hình chính";
    }

    /**
     * Lấy nội dung trợ giúp trong TongQuanPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Các nút phía trên là lối tắt việc hằng ngày (chỉ hiện chức năng bạn được phép"
                + " dùng).\n"
                + "Doanh thu ca, tồn thấp và yêu cầu chờ duyệt lấy từ dữ liệu hiện tại; biểu đồ"
                + " 7 ngày tổng hợp hóa đơn và hoàn trả đã lưu.";
    }
}
