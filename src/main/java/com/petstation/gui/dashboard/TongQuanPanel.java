package com.petstation.gui.dashboard;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.BieuDoCot;
import com.petstation.gui.components.common.DinhDangBang;
import com.petstation.model.MaManHinh;
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

public final class TongQuanPanel extends CoSoPanel implements HoTroThanhCongCu {
    private final Consumer<MaManHinh> boDieuHuong;

    private final Predicate<MaManHinh> coTheTruyCap;

    private final CaThuNganService caLamViec;

    private final JPanel pnlCacPhimTat = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

    private final JPanel pnlCacChiSo = new JPanel(new GridLayout(1, 4, 14, 0));

    private final BieuDoCot bieuDo = new BieuDoCot();

    private final DefaultTableModel trenMoHinh =
            new DefaultTableModel(new String[] {"#", "Sản phẩm", "Đã bán", "Doanh thu"}, 0) {
                @Override
                public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                    return false;
                }
            };

    public TongQuanPanel(
            Consumer<MaManHinh> boDieuHuong,
            Predicate<MaManHinh> coTheTruyCap,
            CaThuNganService caLamViec) {
        this.boDieuHuong = boDieuHuong;
        this.coTheTruyCap = coTheTruyCap;
        this.caLamViec = caLamViec;
        setLayout(new BorderLayout(0, 14));

        JPanel pnlDau = taoVungTrongSuot(new BorderLayout());

        JLabel lblTieuDe = taoTieuDeMuc("Tổng quan hôm nay");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 22));

        String ngay =
                LocalDate.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "EEEE, dd/MM/yyyy", new Locale("vi", "VN")));

        JLabel lblNhanNgay = new JLabel(ngay.substring(0, 1).toUpperCase() + ngay.substring(1));
        lblNhanNgay.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        lblNhanNgay.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlDau.add(lblTieuDe, BorderLayout.WEST);
        pnlDau.add(lblNhanNgay, BorderLayout.EAST);

        pnlCacPhimTat.setOpaque(false);
        pnlCacChiSo.setOpaque(false);

        JPanel pnlTren = taoVungTrongSuot(new BorderLayout(0, 12));
        pnlTren.add(pnlDau, BorderLayout.NORTH);
        pnlTren.add(pnlCacPhimTat, BorderLayout.CENTER);
        pnlTren.add(pnlCacChiSo, BorderLayout.SOUTH);
        add(pnlTren, BorderLayout.NORTH);

        JPanel pnlBieuDoThe = new JPanel(new BorderLayout(0, 6));
        pnlBieuDoThe.setBackground(Color.WHITE);
        pnlBieuDoThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(12, 14, 10, 14)));

        JLabel lblKieuCot = new JLabel("Doanh thu 7 ngày qua");
        lblKieuCot.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        pnlBieuDoThe.add(lblKieuCot, BorderLayout.NORTH);
        pnlBieuDoThe.add(bieuDo, BorderLayout.CENTER);

        JTable tblBang = new JTable(trenMoHinh);
        DinhDangBang.apDung(tblBang);
        tblBang.setAutoCreateRowSorter(false);
        tblBang.getColumnModel().getColumn(0).setPreferredWidth(36);
        tblBang.getColumnModel().getColumn(1).setPreferredWidth(190);
        tblBang.getColumnModel().getColumn(3).setPreferredWidth(120);

        JScrollPane scrKhungCuon = new JScrollPane(tblBang);
        scrKhungCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrKhungCuon.getViewport().setBackground(Color.WHITE);

        JPanel pnlTrenThe = new JPanel(new BorderLayout(0, 6));
        pnlTrenThe.setOpaque(false);

        JLabel lblGoiY = new JLabel("Sản phẩm bán chạy");
        lblGoiY.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        pnlTrenThe.add(lblGoiY, BorderLayout.NORTH);
        pnlTrenThe.add(scrKhungCuon, BorderLayout.CENTER);

        JPanel pnlGiua = new JPanel(new GridLayout(1, 2, 14, 0));
        pnlGiua.setOpaque(false);
        pnlGiua.add(pnlBieuDoThe);
        pnlGiua.add(pnlTrenThe);
        add(pnlGiua, BorderLayout.CENTER);
        caLamViec.themBoLangNghe(this::lamMoi);
        lamMoi();
    }

    private void themPhimTat(String vanBan, MaManHinh dich, NutDinhDang.KieuNut kieu) {
        if (!coTheTruyCap.test(dich)) return;

        NutDinhDang btnGiaTriSau = new NutDinhDang(vanBan, kieu);
        btnGiaTriSau.setPreferredSize(new Dimension(Math.max(110, vanBan.length() * 9 + 28), 42));
        btnGiaTriSau.addActionListener(suKien -> boDieuHuong.accept(dich));
        pnlCacPhimTat.add(btnGiaTriSau);
    }

    private void lamMoi() {
        pnlCacPhimTat.removeAll();
        themPhimTat("Lập hóa đơn (F2)", MaManHinh.LAP_HOA_DON, NutDinhDang.KieuNut.CHINH);
        themPhimTat("Tạo lịch hẹn Spa", MaManHinh.DAT_LICH_HEN_SPA, NutDinhDang.KieuNut.PHU);
        themPhimTat("Đặt phòng Hotel", MaManHinh.DAT_PHONG_LUU_TRU, NutDinhDang.KieuNut.PHU);
        themPhimTat(
                caLamViec.kiemTraMo() ? "Kết ca" : "Mở ca",
                MaManHinh.CA_THU_NGAN,
                NutDinhDang.KieuNut.TRONG_SUOT);
        themPhimTat("Lập phiếu nhập kho", MaManHinh.NHAP_KHO, NutDinhDang.KieuNut.TRONG_SUOT);

        long dangCho = PheDuyetService.demChoDuyet();
        themPhimTat(
                dangCho > 0 ? "Phê duyệt (" + dangCho + ")" : "Phê duyệt",
                MaManHinh.PHE_DUYET_YEU_CAU,
                dangCho > 0 ? NutDinhDang.KieuNut.NGUY_HIEM : NutDinhDang.KieuNut.TRONG_SUOT);
        pnlCacPhimTat.revalidate();
        pnlCacPhimTat.repaint();

        pnlCacChiSo.removeAll();

        long doanhThu =
                caLamViec.layDoanhThuTienMat()
                        + caLamViec.layDoanhThuChuyenKhoan()
                        + caLamViec.layDoanhThuThe()
                        + caLamViec.layTongCocHotel();
        pnlCacChiSo.add(
                layChiSoThe(
                        caLamViec.kiemTraMo() ? "Thu tiền ca hiện tại" : "Chưa mở ca",
                        caLamViec.kiemTraMo() ? TienTe.dinhDang(doanhThu) : "—",
                        caLamViec.kiemTraMo() ? caLamViec.layTenCa() : "Mở ca để bán hàng",
                        MauUngDung.XANH_LA));
        pnlCacChiSo.add(
                layChiSoThe(
                        "Hóa đơn trong ca",
                        caLamViec.kiemTraMo() ? String.valueOf(caLamViec.demHoaDon()) : "—",
                        "",
                        MauUngDung.NAU));
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

        String[] soNgay = new String[7];

        double[] cacGiaTri = new double[7];

        java.util.Map<java.time.LocalDate, Double> doanhThuTheoNgay = new java.util.HashMap<>();

        if (PhienDangNhapService.layPhien().kiemTraCo(com.petstation.model.Quyen.XEM_BAO_CAO_CHUNG)
                || PhienDangNhapService.layPhien()
                        .kiemTraCo(com.petstation.model.Quyen.XEM_BAO_CAO_CA)) {
            for (Object[] dong : com.petstation.service.ThongKeService.layDoanhThuTheoNgay(7))
                doanhThuTheoNgay.put(
                        ((java.sql.Date) dong[0]).toLocalDate(), ((Number) dong[1]).doubleValue());
        }
        for (int chiSo = 0; chiSo < 7; chiSo++) {
            java.time.LocalDate ngay = LocalDate.now().minusDays(6 - chiSo);
            soNgay[chiSo] = ngay.format(DateTimeFormatter.ofPattern("dd/MM"));
            cacGiaTri[chiSo] = doanhThuTheoNgay.getOrDefault(ngay, 0.0);
        }
        bieuDo.datDuLieu(soNgay, cacGiaTri, true, "");

        trenMoHinh.setRowCount(0);

        int hang = 1;

        if (PhienDangNhapService.layPhien().kiemTraCo(com.petstation.model.Quyen.XEM_BAO_CAO_CHUNG)
                || PhienDangNhapService.layPhien()
                        .kiemTraCo(com.petstation.model.Quyen.XEM_BAO_CAO_CA)) {
            for (Object[] sanPham : com.petstation.service.ThongKeService.laySanPhamBanChay(7)) {
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

    private static long layThapTonKho() {
        long soPhanTu = 0;
        for (Object[] vungDangXet : DanhMucService.laySanPham()) {
            if (TienTe.doc(String.valueOf(vungDangXet[8]))
                    <= Math.max(0, TienTe.doc(String.valueOf(vungDangXet[6])))) soPhanTu++;
        }
        return soPhanTu;
    }

    @Override
    public void xuLyHienThi() {
        lamMoi();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Màn hình chính";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Các nút phía trên là lối tắt việc hằng ngày (chỉ hiện chức năng bạn được phép"
                + " dùng).\n"
                + "Doanh thu ca, tồn thấp và yêu cầu chờ duyệt lấy từ dữ liệu hiện tại; biểu đồ"
                + " 7 ngày tổng hợp hóa đơn và hoàn trả đã lưu.";
    }
}
