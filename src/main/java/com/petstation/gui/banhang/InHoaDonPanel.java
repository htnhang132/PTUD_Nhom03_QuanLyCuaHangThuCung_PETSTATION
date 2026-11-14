package com.petstation.gui.banhang;

import com.petstation.dto.GioHang;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.LoaiPhieuDoiTra;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.URL;
import java.util.function.Consumer;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

// Màn hình: In & Xem lại biên lai (Mã: IN_HOA_DON)
// Luồng xử lý: GUI -> BanHangService -> HoaDonDAOImpl -> SQL (hoa_don, chi_tiet_hoa_don)
// Mô tả: Xem trước và gửi lệnh in hóa đơn thanh toán ra máy in nhiệt / PDF
// Phân hệ: banhang
public final class InHoaDonPanel extends CoSoPanel {
    private static final int CHIEU_RONG = 40;

    private final GioHang banHang;

    private final CardLayout cacThe = new CardLayout();

    private final JPanel khungGoc = new JPanel(cacThe);

    private final HoaDonDocPanel khungHoaDon = new HoaDonDocPanel();

    private final JLabel nhanBieuNgu = new JLabel();

    private int luotTai = 0;

    public InHoaDonPanel(GioHang banHang, Consumer<MaManHinh> boDieuHuong) {
        this.banHang = banHang;
        setLayout(new BorderLayout(0, 14));
        JLabel nhanTieuDe = taoTieuDeMuc("Xem hóa đơn");
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        add(nhanTieuDe, BorderLayout.NORTH);
        khungGoc.setOpaque(false);
        nhanBieuNgu.setOpaque(true);
        nhanBieuNgu.setBackground(MauUngDung.XANH_LA_NEN_NHAT);
        nhanBieuNgu.setForeground(MauUngDung.mau(0x155724));
        nhanBieuNgu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanBieuNgu.setBorder(new EmptyBorder(10, 14, 10, 14));
        JScrollPane scrCuon =
                new JScrollPane(
                        khungHoaDon,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrCuon.setPreferredSize(new Dimension(460, 0));
        JPanel khungGiay = new JPanel(new BorderLayout());
        khungGiay.setOpaque(false);
        khungGiay.setBorder(new EmptyBorder(0, 0, 0, 0));
        JPanel khungOGiua = new JPanel(new GridBagLayout());
        khungOGiua.setOpaque(false);
        GridBagConstraints rangBuocLuoi = new GridBagConstraints();
        rangBuocLuoi.fill = GridBagConstraints.VERTICAL;
        rangBuocLuoi.weightx = 0;
        rangBuocLuoi.insets = new Insets(0, 18, 0, 18);
        rangBuocLuoi.weighty = 1;
        khungOGiua.add(scrCuon, rangBuocLuoi);
        khungGiay.add(khungOGiua, BorderLayout.CENTER);
        NutDinhDang btnIn = new NutDinhDang("In hóa đơn (Ctrl+P)", NutDinhDang.KieuNut.PHU);
        btnIn.addActionListener(suKien -> xuLyIn());
        NutDinhDang btnTiepTheo =
                new NutDinhDang("Lập hóa đơn mới (F2)", NutDinhDang.KieuNut.CHINH);
        btnTiepTheo.addActionListener(suKien -> boDieuHuong.accept(MaManHinh.LAP_HOA_DON));
        JLabel nhanGoiY =
                new JLabel(
                        "Xem lại hóa đơn bên trên. Nút In mở hộp thoại máy in (có thể chọn \"Lưu"
                                + " thành PDF\").");
        nhanGoiY.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        JPanel khungCacThaoTac = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        khungCacThaoTac.setOpaque(false);
        khungCacThaoTac.add(btnTiepTheo);
        khungCacThaoTac.add(btnIn);
        JPanel khungDuoi = taoVungTrongSuot(new BorderLayout(0, 6));
        khungDuoi.add(khungCacThaoTac, BorderLayout.CENTER);
        khungDuoi.add(nhanGoiY, BorderLayout.SOUTH);
        nhanGoiY.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel khungBieuMau = taoVungTrongSuot(new BorderLayout(0, 12));
        khungBieuMau.add(nhanBieuNgu, BorderLayout.NORTH);
        khungBieuMau.add(khungGiay, BorderLayout.CENTER);
        khungBieuMau.add(khungDuoi, BorderLayout.SOUTH);
        khungGoc.add(khungBieuMau, "receipt");
        NutDinhDang btnDi = new NutDinhDang("Về lập đơn hàng", NutDinhDang.KieuNut.CHINH);
        btnDi.addActionListener(suKien -> boDieuHuong.accept(MaManHinh.LAP_HOA_DON));
        khungGoc.add(
                layTrongTrangThai(
                        "Chưa có hóa đơn nào để in",
                        "Hóa đơn xuất hiện ở đây sau khi thanh toán.",
                        btnDi),
                "empty");
        add(khungGoc, BorderLayout.CENTER);
        lamMoi();
    }

    private void lamMoi() {
        GioHang.BienLai dongDuLieu = banHang.getBienLaiCuoi();
        if (dongDuLieu == null) {
            cacThe.show(khungGoc, "empty");
            return;
        }
        cacThe.show(khungGoc, "receipt");
        String thayDoi =
                dongDuLieu.getPhuongThuc() == PhuongThuc.TIEN_MAT
                        ? "  ·  Tiền thừa " + DinhDang.dinhDang(dongDuLieu.getThayDoi())
                        : "";
        nhanBieuNgu.setText(
                "Đã thanh toán  "
                        + dongDuLieu.getSoHoaDon()
                        + "  ·  Tổng "
                        + DinhDang.dinhDang(dongDuLieu.getTong())
                        + thayDoi);
        int luot = ++luotTai;
        String soHoaDon = dongDuLieu.getSoHoaDon();
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                () -> new BanHangService().layChiTietBienLai(soHoaDon),
                khungHoaDon::datDuLieu);
    }

    public static String hienThi(GioHang.BienLai dongDuLieu) {
        var nguon = new BanHangService().layNguonBienLai(dongDuLieu.getSoHoaDon());
        StringBuilder boGhepChuoi = new StringBuilder();
        themPhanDauHoaDonChu(boGhepChuoi, dongDuLieu, nguon);
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        for (GioHang.DongHang dongDangXet : dongDuLieu.getCacDong()) {
            boGhepChuoi.append(dongDangXet.getTen()).append('\n');
            boGhepChuoi
                    .append(
                            layCap(
                                    dongDangXet.getSoLuong()
                                            + "x "
                                            + DinhDang.dinhDang(dongDangXet.getDonGia()),
                                    DinhDang.dinhDang(dongDangXet.docSoTien())))
                    .append('\n');
        }
        themPhanThanhToanHoaDonChu(boGhepChuoi, dongDuLieu, nguon);
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        boGhepChuoi.append(layGiua("Cảm ơn quý khách!")).append('\n');
        return boGhepChuoi.toString();
    }

    private static void themPhanDauHoaDonChu(
            StringBuilder boGhepChuoi,
            GioHang.BienLai dongDuLieu,
            BanHangService.NguonBienLai nguon) {
        boGhepChuoi.append(layGiua(CaiDatService.macDinh().lay("STORE_NAME"))).append('\n');
        boGhepChuoi.append(layGiua(CaiDatService.macDinh().lay("STORE_ADDRESS"))).append('\n');
        boGhepChuoi.append(layGiua(CaiDatService.macDinh().lay("STORE_PHONE"))).append('\n');
        String tieuDe =
                nguon.loai().equals(LoaiPhieuDoiTra.DOI_HANG.maDB())
                        ? "Đổi hàng — thu chênh lệch"
                        : nguon.loai().equals("LUU_TRU") ? "Quyết toán Hotel" : "Hóa đơn bán hàng";
        boGhepChuoi.append(layGiua(tieuDe)).append('\n');
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        boGhepChuoi.append("Số HĐ : ").append(dongDuLieu.getSoHoaDon()).append('\n');
        boGhepChuoi
                .append("Ngày  : ")
                .append(dongDuLieu.getThoiGian().format(DinhDang.NGAY_GIO))
                .append('\n');
        boGhepChuoi.append("Thu ngân: ").append(dongDuLieu.getThuNgan()).append('\n');
        boGhepChuoi.append("Khách : ").append(dongDuLieu.getKhachHang()).append('\n');
        if (!nguon.ma().isBlank()) boGhepChuoi.append("Nguồn : ").append(nguon.ma()).append('\n');
        if (!nguon.hoaDonGoc().isBlank())
            boGhepChuoi.append("HĐ gốc: ").append(nguon.hoaDonGoc()).append('\n');
        if (nguon.loai().equals("LUU_TRU"))
            boGhepChuoi
                    .append(layCap("Tiền cọc đã trừ", DinhDang.dinhDang(nguon.daCanTru())))
                    .append('\n');
    }

    private static void themPhanThanhToanHoaDonChu(
            StringBuilder boGhepChuoi,
            GioHang.BienLai dongDuLieu,
            BanHangService.NguonBienLai nguon) {
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        boGhepChuoi
                .append(layCap("Tạm tính", DinhDang.dinhDang(dongDuLieu.getTamTinh())))
                .append('\n');
        if (dongDuLieu.getGiamGia() > 0) {
            String nhanGiam =
                    (nguon.loai().equals(LoaiPhieuDoiTra.DOI_HANG.maDB())
                                    ? "Giá trị hàng được đổi"
                                    : "Giảm giá")
                            + (dongDuLieu.getVoucher().isEmpty()
                                    ? ""
                                    : "(" + dongDuLieu.getVoucher() + ")");
            boGhepChuoi
                    .append(layCap(nhanGiam, "-" + DinhDang.dinhDang(dongDuLieu.getGiamGia())))
                    .append('\n');
        }
        boGhepChuoi
                .append(layCap("TỔNG CỘNG", DinhDang.dinhDang(dongDuLieu.getTong())))
                .append('\n');
        if (dongDuLieu.getVatPhanTram() > 0)
            boGhepChuoi
                    .append(
                            layCap(
                                    "Đã gồm VAT (" + dongDuLieu.getVatPhanTram() + "%)",
                                    DinhDang.dinhDang(dongDuLieu.tinhTienThueGtgt())))
                    .append('\n');
        boGhepChuoi
                .append(
                        layCap(
                                "Thanh toán (" + tenPhuongThuc(dongDuLieu.getPhuongThuc()) + ")",
                                DinhDang.dinhDang(dongDuLieu.getDaNhan())))
                .append('\n');
        if (dongDuLieu.getThayDoi() > 0)
            boGhepChuoi
                    .append(layCap("Tiền thừa", DinhDang.dinhDang(dongDuLieu.getThayDoi())))
                    .append('\n');
    }

    public static String hienThiHTML(GioHang.BienLai bienLai) {
        var nguon = new BanHangService().layNguonBienLai(bienLai.getSoHoaDon());
        StringBuilder html =
                new StringBuilder(
                        "<html><head><style>body{font-family:Arial;font-size:12px;color:#61341D;margin:20px}h1{color:#CE1D2D;text-align:center;margin:0;font-size:20px}h2{margin:6px"
                            + " 0;font-size:17px}table{width:100%;border-collapse:collapse}td,th{padding:9px;border-bottom:1px"
                            + " solid #e4ded6}th{background:#FDF9ED}p{margin:6px"
                            + " 0}</style></head><body>");
        URL logo = InHoaDonPanel.class.getResource("/images/pet-station-logo.png");
        html.append("<table><tr><td width='80'>");
        if (logo != null)
            html.append("<img src='").append(logo).append("' width='60' height='60'>");
        html.append("</td><td align='center'><h1>")
                .append(maHoa(CaiDatService.macDinh().lay("STORE_NAME")))
                .append("</h1>");
        html.append(maHoa(CaiDatService.macDinh().lay("STORE_ADDRESS")))
                .append("<br>")
                .append(maHoa(CaiDatService.macDinh().lay("STORE_PHONE")))
                .append("</td></tr></table>");
        html.append("<h2 align='center'>HÓA ĐƠN</h2><table><tr><td>Số hóa đơn: <b>")
                .append(maHoa(bienLai.getSoHoaDon()))
                .append("</b></td><td>Ngày: ")
                .append(bienLai.getThoiGian().format(DinhDang.NGAY_GIO))
                .append("</td></tr><tr><td>Khách hàng: ")
                .append(maHoa(bienLai.getKhachHang()))
                .append("</td><td>Thu ngân: ")
                .append(maHoa(bienLai.getThuNgan()))
                .append("</td></tr></table>");
        if (!nguon.ma().isBlank())
            html.append("<p>Chứng từ: ").append(maHoa(nguon.ma())).append("</p>");
        if (!nguon.hoaDonGoc().isBlank())
            html.append("<p>Hóa đơn gốc: ").append(maHoa(nguon.hoaDonGoc())).append("</p>");
        html.append(
                "<table><tr><th align='left' width='52%'>Tên hàng / dịch vụ</th><th align='center'"
                        + " width='8%'>SL</th><th align='right' width='20%'>Đơn giá</th><th"
                        + " align='right' width='20%'>Thành tiền</th></tr>");
        for (var dong : bienLai.getCacDong())
            html.append("<tr><td>")
                    .append(maHoa(dong.getTen()))
                    .append("</td><td align='center'>")
                    .append(dong.getSoLuong())
                    .append("</td><td align='right'>")
                    .append(DinhDang.dinhDang(dong.getDonGia()))
                    .append("</td><td align='right'>")
                    .append(DinhDang.dinhDang(dong.docSoTien()))
                    .append("</td></tr>");
        html.append("</table><table>");
        themTong(html, "Tạm tính", bienLai.getTamTinh());
        if (bienLai.getGiamGia() > 0)
            themTong(
                    html,
                    nguon.loai().equals(LoaiPhieuDoiTra.DOI_HANG.maDB())
                            ? "Giá trị hàng được đổi"
                            : "Giảm giá " + bienLai.getVoucher(),
                    -bienLai.getGiamGia());
        if (nguon.loai().equals("LUU_TRU")) themTong(html, "Tiền cọc đã trừ", nguon.daCanTru());
        themTong(html, "TỔNG THANH TOÁN", bienLai.getTong());
        if (bienLai.getVatPhanTram() > 0)
            themTong(
                    html,
                    "Đã gồm VAT (" + bienLai.getVatPhanTram() + "%)",
                    bienLai.tinhTienThueGtgt());
        String phuongThuc = tenPhuongThuc(bienLai.getPhuongThuc());
        themTong(html, "Đã nhận — " + phuongThuc, bienLai.getDaNhan());
        if (bienLai.getThayDoi() > 0) themTong(html, "Tiền thừa", bienLai.getThayDoi());
        return html.append("</table><p align='center'>Cảm ơn quý khách!</p></body></html>")
                .toString();
    }

    private static String tenPhuongThuc(PhuongThuc pt) {
        return switch (pt) {
            case TIEN_MAT -> "Tiền mặt";
            case CHUYEN_KHOAN -> "Chuyển khoản / QR";
            case THE_NGAN_HANG -> "Thẻ";
            case DIEM_TICH_LUY -> "Điểm tích lũy";
        };
    }

    private static void themTong(StringBuilder html, String nhan, long tien) {
        html.append("<tr><td align='right' width='75%'>")
                .append(maHoa(nhan))
                .append("</td><td align='right'><b>")
                .append(DinhDang.dinhDang(tien))
                .append("</b></td></tr>");
    }

    private static String maHoa(String chuoi) {
        return chuoi == null
                ? ""
                : chuoi.replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\"", "&quot;")
                        .replace("'", "&#39;");
    }

    private static String layGiua(String chuoi) {
        int dem = Math.max(0, (CHIEU_RONG - chuoi.length()) / 2);
        return " ".repeat(dem) + chuoi;
    }

    private static String layCap(String trai, String phai) {
        int khoangCach = Math.max(1, CHIEU_RONG - trai.length() - phai.length());
        return trai + " ".repeat(khoangCach) + phai;
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(
                false, false, false, banHang.getBienLaiCuoi() != null, false, true);
    }

    @Override
    public void xuLyIn() {
        if (banHang.getBienLaiCuoi() == null) return;
        try {
            PrinterJob congViec = PrinterJob.getPrinterJob();
            congViec.setJobName("Hóa đơn " + banHang.getBienLaiCuoi().getSoHoaDon());
            congViec.setPrintable(khungHoaDon);
            PrintRequestAttributeSet thuocTinh = new HashPrintRequestAttributeSet();
            if (congViec.printDialog(thuocTinh)) {
                congViec.setPrintable(khungHoaDon, congViec.getPageFormat(thuocTinh));
                congViec.print(thuocTinh);
            }
        } catch (PrinterException | HeadlessException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, "Không thể in: " + ngoaiLe.getMessage(), "Xem hóa đơn");
        }
    }

    @Override
    public void xuLyHienThi() {
        lamMoi();
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Bấm In hóa đơn để gửi tới máy in. Bấm Lập đơn mới để bắt đầu đơn tiếp theo.";
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Xem hóa đơn";
    }
}
