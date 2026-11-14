package com.petstation.gui.hethong;

import com.petstation.dto.ChiTietBaoCao.DoanhThuTheoNgay;
import com.petstation.dto.ChiTietBaoCao.SanPhamBanChay;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuDoCot;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiSanPham;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.pheduyet.PheDuyetService;
import com.petstation.service.thongke.ThongKeService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

// Màn hình: Tổng quan (Mã: TONG_QUAN)
// Luồng xử lý: GUI -> ThongKeService -> ThongKeDAOImpl -> SQL (hoa_don, hop_dong_luu_tru,
// phieu_tiep_nhan_spa)
// Mô tả: Bảng chỉ số tổng quan (KPIs) doanh thu, đơn hàng, khách hàng mới trong ngày và biểu đồ xu
// hướng
// Phân hệ: thongke
public final class TongQuanPanel extends CoSoPanel {
    private final Consumer<MaManHinh> boDieuHuong;

    private final Predicate<MaManHinh> coTheTruyCap;

    private final CaThuNganService caLamViec;

    private final ThongKeService thongKeService = new ThongKeService();

    private final JPanel khungCacPhimTat = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

    private final JPanel khungCacChiSo = new JPanel(new GridLayout(1, 4, 14, 0));

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
        JPanel khungDau = taoVungTrongSuot(new BorderLayout());
        JLabel nhanTieuDe = taoTieuDeManHinh("Tổng quan hôm nay");
        String ngay = LocalDate.now().format(DinhDang.THU_NGAY_VI);
        JLabel nhanNhanNgay = new JLabel(ngay.substring(0, 1).toUpperCase() + ngay.substring(1));
        nhanNhanNgay.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhanNhanNgay.setForeground(MauUngDung.VAN_BAN_XAM);
        khungDau.add(nhanTieuDe, BorderLayout.WEST);
        khungDau.add(nhanNhanNgay, BorderLayout.EAST);
        khungCacPhimTat.setOpaque(false);
        khungCacChiSo.setOpaque(false);
        JPanel khungTren = taoVungTrongSuot(new BorderLayout(0, 12));
        khungTren.add(khungDau, BorderLayout.NORTH);
        khungTren.add(khungCacPhimTat, BorderLayout.CENTER);
        khungTren.add(khungCacChiSo, BorderLayout.SOUTH);
        add(khungTren, BorderLayout.NORTH);
        JPanel khungBieuDoThe = new JPanel(new BorderLayout(0, 6));
        khungBieuDoThe.setBackground(Color.WHITE);
        khungBieuDoThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(12, 14, 10, 14)));
        JLabel nhanKieuCot = new JLabel("Doanh thu 7 ngày qua");
        nhanKieuCot.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        khungBieuDoThe.add(nhanKieuCot, BorderLayout.NORTH);
        khungBieuDoThe.add(bieuDo, BorderLayout.CENTER);
        JTable bangBang = new JTable(trenMoHinh);
        DinhDangBang.apDung(bangBang);
        bangBang.setAutoCreateRowSorter(false);
        bangBang.getColumnModel().getColumn(0).setPreferredWidth(36);
        bangBang.getColumnModel().getColumn(1).setPreferredWidth(190);
        bangBang.getColumnModel().getColumn(3).setPreferredWidth(120);
        JScrollPane scrKhungCuon = new JScrollPane(bangBang);
        scrKhungCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.XAM_VIEN_NHE));
        scrKhungCuon.getViewport().setBackground(Color.WHITE);
        JPanel khungTrenThe = layThe(new BorderLayout(0, 8));
        JLabel nhanGoiY = new JLabel("Sản phẩm bán chạy");
        nhanGoiY.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanGoiY.setForeground(MauUngDung.NAU);
        khungTrenThe.add(nhanGoiY, BorderLayout.NORTH);
        khungTrenThe.add(scrKhungCuon, BorderLayout.CENTER);
        JPanel khungGiua = new JPanel(new GridLayout(1, 2, 14, 0));
        khungGiua.setOpaque(false);
        khungGiua.add(khungBieuDoThe);
        khungGiua.add(khungTrenThe);
        add(khungGiua, BorderLayout.CENTER);
        lamMoi();
    }

    private final BoLangNgheSuKien<CaThuNganService> boLangNgheCa =
            ca -> {
                if (SwingUtilities.isEventDispatchThread()) {
                    lamMoi();
                } else {
                    SwingUtilities.invokeLater(this::lamMoi);
                }
            };

    @Override
    public void addNotify() {
        super.addNotify();
        caLamViec.themBoLangNghe(boLangNgheCa);
    }

    @Override
    public void removeNotify() {
        caLamViec.xoaBoLangNghe(boLangNgheCa);
        super.removeNotify();
    }

    private void themPhimTat(String vanBan, MaManHinh dich, NutDinhDang.KieuNut kieu) {
        if (!coTheTruyCap.test(dich)) return;
        NutDinhDang btnGiaTriSau = new NutDinhDang(vanBan, kieu);
        btnGiaTriSau.setPreferredSize(new Dimension(Math.max(110, vanBan.length() * 9 + 28), 42));
        btnGiaTriSau.addActionListener(suKien -> boDieuHuong.accept(dich));
        khungCacPhimTat.add(btnGiaTriSau);
    }

    private static final class DuLieuTongQuan {
        final long dangCho;
        final long doanhThu;
        final long thapTonKho;
        final int demHoaDon;
        final boolean moCa;
        final String tenCa;
        final String[] soNgay;
        final double[] cacGiaTri;
        final List<SanPhamBanChay> spBanChay;

        private DuLieuTongQuan(
                long dangCho,
                long doanhThu,
                long thapTonKho,
                int demHoaDon,
                boolean moCa,
                String tenCa,
                String[] soNgay,
                double[] cacGiaTri,
                List<SanPhamBanChay> spBanChay) {
            this.dangCho = dangCho;
            this.doanhThu = doanhThu;
            this.thapTonKho = thapTonKho;
            this.demHoaDon = demHoaDon;
            this.moCa = moCa;
            this.tenCa = tenCa;
            this.soNgay = soNgay;
            this.cacGiaTri = cacGiaTri;
            this.spBanChay = spBanChay;
        }
    }

    private void lamMoi() {
        capNhatCacPhimTat(0);
        PheDuyetService pheDuyetService = new PheDuyetService();
        // Tải dữ liệu bất đồng bộ qua TacVuNen để không làm khựng giao diện
        TacVuNen.chay(() -> taiDuLieuTongQuan(pheDuyetService), this::hienThiDuLieuTongQuan, null);
    }

    private void capNhatCacPhimTat(long dangChoSoBo) {
        khungCacPhimTat.removeAll();
        themPhimTat("Lập hóa đơn (F2)", MaManHinh.LAP_HOA_DON, NutDinhDang.KieuNut.CHINH);
        themPhimTat("Tạo lịch hẹn Spa", MaManHinh.DAT_LICH_HEN_SPA, NutDinhDang.KieuNut.PHU);
        themPhimTat("Đặt phòng Hotel", MaManHinh.DAT_PHONG_LUU_TRU, NutDinhDang.KieuNut.PHU);
        themPhimTat(
                caLamViec.kiemTraMo() ? "Kết ca" : "Mở ca",
                MaManHinh.CA_THU_NGAN,
                NutDinhDang.KieuNut.TRONG_SUOT);
        themPhimTat("Lập phiếu nhập kho", MaManHinh.NHAP_KHO, NutDinhDang.KieuNut.TRONG_SUOT);
        themPhimTat(
                dangChoSoBo > 0 ? "Phê duyệt (" + dangChoSoBo + ")" : "Phê duyệt",
                MaManHinh.PHE_DUYET_YEU_CAU,
                dangChoSoBo > 0 ? NutDinhDang.KieuNut.NGUY_HIEM : NutDinhDang.KieuNut.TRONG_SUOT);
        khungCacPhimTat.revalidate();
        khungCacPhimTat.repaint();
    }

    private DuLieuTongQuan taiDuLieuTongQuan(PheDuyetService pheDuyetService) {
        long dangCho = pheDuyetService.demChoDuyet();
        boolean moCa = caLamViec.kiemTraMo();
        String tenCa = moCa ? caLamViec.layTenCa() : "";
        int demHoaDon = moCa ? caLamViec.demHoaDon() : 0;
        long doanhThu =
                caLamViec.layDoanhThuTienMat()
                        + caLamViec.layDoanhThuChuyenKhoan()
                        + caLamViec.layDoanhThuThe()
                        + caLamViec.layTongCocHotel();
        boolean coQuyenBaoCao =
                PhienDangNhapService.macDinh().layPhien() != null
                        && (PhienDangNhapService.macDinh()
                                        .layPhien()
                                        .kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG)
                                || PhienDangNhapService.macDinh()
                                        .layPhien()
                                        .kiemTraCo(Quyen.XEM_BAO_CAO_CA));
        Map<LocalDate, Double> doanhThuTheoNgay = new HashMap<>();
        if (coQuyenBaoCao) {
            for (DoanhThuTheoNgay dong : thongKeService.layDoanhThuTheoNgay(7)) {
                doanhThuTheoNgay.put(dong.getNgay(), dong.getTongTien());
            }
        }
        String[] soNgay = new String[7];
        double[] cacGiaTri = new double[7];
        for (int chiSo = 0; chiSo < 7; chiSo++) {
            LocalDate ngay = LocalDate.now().minusDays(6 - chiSo);
            soNgay[chiSo] = ngay.format(DinhDang.NGAY_THANG);
            cacGiaTri[chiSo] = doanhThuTheoNgay.getOrDefault(ngay, 0.0);
        }
        List<SanPhamBanChay> spBanChay =
                coQuyenBaoCao ? thongKeService.laySanPhamBanChay(7) : new ArrayList<>();
        return new DuLieuTongQuan(
                dangCho,
                doanhThu,
                layThapTonKho(),
                demHoaDon,
                moCa,
                tenCa,
                soNgay,
                cacGiaTri,
                spBanChay);
    }

    private void hienThiDuLieuTongQuan(DuLieuTongQuan d) {
        capNhatCacPhimTat(d.dangCho);
        khungCacChiSo.removeAll();
        khungCacChiSo.add(
                layChiSoThe(
                        d.moCa ? "Thu tiền ca hiện tại" : "Chưa mở ca",
                        d.moCa ? DinhDang.dinhDang(d.doanhThu) : "—",
                        d.moCa ? d.tenCa : "Mở ca để bán hàng",
                        MauUngDung.XANH_LA));
        khungCacChiSo.add(
                layChiSoThe(
                        "Hóa đơn trong ca",
                        d.moCa ? String.valueOf(d.demHoaDon) : "—",
                        "",
                        MauUngDung.NAU));
        khungCacChiSo.add(
                layChiSoThe(
                        "Sản phẩm tồn thấp",
                        String.valueOf(d.thapTonKho),
                        "Cần nhập thêm",
                        MauUngDung.DO_THUONG_HIEU));
        khungCacChiSo.add(
                layChiSoThe(
                        "Yêu cầu chờ duyệt",
                        String.valueOf(d.dangCho),
                        d.dangCho > 0 ? "Cần Cửa hàng trưởng xử lý" : "Không có",
                        MauUngDung.CAM));
        khungCacChiSo.revalidate();
        khungCacChiSo.repaint();
        bieuDo.datDuLieu(d.soNgay, d.cacGiaTri, true, "");
        trenMoHinh.setRowCount(0);
        int hang = 1;
        for (SanPhamBanChay sanPham : d.spBanChay) {
            if (hang > 5) break;
            trenMoHinh.addRow(
                    new Object[] {
                        hang++,
                        sanPham.getTenSanPham(),
                        sanPham.getSoBanSauTra(),
                        DinhDang.dinhDang(Math.round(sanPham.getDoanhThu()))
                    });
        }
    }

    private static long layThapTonKho() {
        long soPhanTu = 0;
        for (Object[] vungDangXet : DanhMucService.macDinh().laySanPham()) {
            if (!TrangThaiSanPham.DANG_BAN.tenHienThi().equals(vungDangXet[9])) continue;
            if (DinhDang.doc(String.valueOf(vungDangXet[8]))
                    <= Math.max(0, DinhDang.doc(String.valueOf(vungDangXet[6])))) soPhanTu++;
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
