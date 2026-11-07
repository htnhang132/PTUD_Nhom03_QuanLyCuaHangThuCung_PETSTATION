package com.petstation.gui.processing.sales;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.KhaNangThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.model.GioHang;
import com.petstation.model.MaManHinh;
import com.petstation.model.PhuongThuc;
import com.petstation.util.TienTe;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class InHoaDonPanel extends CoSoPanel {
    private static final int CHIEU_RONG = 40;

    private final GioHang banHang;

    private final Consumer<MaManHinh> boDieuHuong;

    private final CardLayout cacThe = new CardLayout();

    private final JPanel pnlGoc = new JPanel(cacThe);

    private final com.petstation.gui.components.common.HoaDonDocPanel pnlHoaDon =
            new com.petstation.gui.components.common.HoaDonDocPanel();

    private final JLabel lblBieuNgu = new JLabel();

    public InHoaDonPanel(GioHang banHang, Consumer<MaManHinh> boDieuHuong) {
        this.banHang = banHang;
        this.boDieuHuong = boDieuHuong;
        setLayout(new BorderLayout(0, 14));

        JLabel lblTieuDe = taoTieuDeMuc("Xem hóa đơn");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        add(lblTieuDe, BorderLayout.NORTH);

        pnlGoc.setOpaque(false);

        lblBieuNgu.setOpaque(true);
        lblBieuNgu.setBackground(new Color(0xE6, 0xF4, 0xEA));
        lblBieuNgu.setForeground(new Color(0x15, 0x57, 0x24));
        lblBieuNgu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblBieuNgu.setBorder(new EmptyBorder(10, 14, 10, 14));

        JScrollPane scrCuon =
                new JScrollPane(
                        pnlHoaDon,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrCuon.setPreferredSize(new Dimension(460, 0));

        JPanel pnlGiay = new JPanel(new BorderLayout());
        pnlGiay.setOpaque(false);
        pnlGiay.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel pnlOGiua = new JPanel(new GridBagLayout());
        pnlOGiua.setOpaque(false);

        GridBagConstraints rangBuocLuoi = new GridBagConstraints();
        rangBuocLuoi.fill = GridBagConstraints.VERTICAL;
        rangBuocLuoi.weightx = 0;
        rangBuocLuoi.insets = new Insets(0, 18, 0, 18);
        rangBuocLuoi.weighty = 1;
        pnlOGiua.add(scrCuon, rangBuocLuoi);
        pnlGiay.add(pnlOGiua, BorderLayout.CENTER);

        NutDinhDang btnIn = new NutDinhDang("In hóa đơn (Ctrl+P)", NutDinhDang.KieuNut.PHU);
        btnIn.addActionListener(suKien -> xuLyIn());

        NutDinhDang btnTiepTheo =
                new NutDinhDang("Lập hóa đơn mới (F2)", NutDinhDang.KieuNut.CHINH);
        btnTiepTheo.addActionListener(suKien -> boDieuHuong.accept(MaManHinh.LAP_HOA_DON));

        JLabel lblGoiY =
                new JLabel(
                        "Xem lại hóa đơn bên trên. Nút In mở hộp thoại máy in (có thể chọn \"Lưu"
                                + " thành PDF\").");
        lblGoiY.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);

        JPanel pnlCacThaoTac = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlCacThaoTac.setOpaque(false);
        pnlCacThaoTac.add(btnTiepTheo);
        pnlCacThaoTac.add(btnIn);

        JPanel pnlDuoi = taoVungTrongSuot(new BorderLayout(0, 6));
        pnlDuoi.add(pnlCacThaoTac, BorderLayout.CENTER);
        pnlDuoi.add(lblGoiY, BorderLayout.SOUTH);
        lblGoiY.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel pnlBieuMau = taoVungTrongSuot(new BorderLayout(0, 12));
        pnlBieuMau.add(lblBieuNgu, BorderLayout.NORTH);
        pnlBieuMau.add(pnlGiay, BorderLayout.CENTER);
        pnlBieuMau.add(pnlDuoi, BorderLayout.SOUTH);
        pnlGoc.add(pnlBieuMau, "receipt");

        NutDinhDang btnDi = new NutDinhDang("Về lập đơn hàng", NutDinhDang.KieuNut.CHINH);
        btnDi.addActionListener(suKien -> boDieuHuong.accept(MaManHinh.LAP_HOA_DON));
        pnlGoc.add(
                layTrongTrangThai(
                        "Chưa có hóa đơn nào để in",
                        "Hóa đơn xuất hiện ở đây sau khi thanh toán.",
                        btnDi),
                "empty");
        add(pnlGoc, BorderLayout.CENTER);
        lamMoi();
    }

    private void lamMoi() {
        GioHang.BienLai dongDuLieu = banHang.getBienLaiCuoi();
        if (dongDuLieu == null) {
            cacThe.show(pnlGoc, "empty");
            return;
        }
        cacThe.show(pnlGoc, "receipt");

        String thayDoi =
                dongDuLieu.getPhuongThuc() == PhuongThuc.TIEN_MAT
                        ? "  ·  Tiền thừa " + TienTe.dinhDang(dongDuLieu.getThayDoi())
                        : "";
        lblBieuNgu.setText(
                "Đã thanh toán  "
                        + dongDuLieu.getSoHoaDon()
                        + "  ·  Tổng "
                        + TienTe.dinhDang(dongDuLieu.getTong())
                        + thayDoi);
        pnlHoaDon.datDuLieu(
                com.petstation.service.BanHangService.layChiTietBienLai(dongDuLieu.getSoHoaDon()));
    }

    public static String hienThi(GioHang.BienLai dongDuLieu) {
        var nguon = com.petstation.service.BanHangService.layNguonBienLai(dongDuLieu.getSoHoaDon());

        StringBuilder boGhepChuoi = new StringBuilder();
        boGhepChuoi
                .append(layGiua(com.petstation.service.CaiDatService.lay("STORE_NAME")))
                .append('\n');
        boGhepChuoi
                .append(layGiua(com.petstation.service.CaiDatService.lay("STORE_ADDRESS")))
                .append('\n');
        boGhepChuoi
                .append(layGiua(com.petstation.service.CaiDatService.lay("STORE_PHONE")))
                .append('\n');
        boGhepChuoi
                .append(
                        layGiua(
                                nguon.loai().equals("DOI_HANG")
                                        ? "Đổi hàng — thu chênh lệch"
                                        : nguon.loai().equals("LUU_TRU")
                                                ? "Quyết toán Hotel"
                                                : "Hóa đơn bán hàng"))
                .append('\n');
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        boGhepChuoi.append("Số HĐ : ").append(dongDuLieu.getSoHoaDon()).append('\n');
        boGhepChuoi
                .append("Ngày  : ")
                .append(
                        dongDuLieu
                                .getThoiGian()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .append('\n');
        boGhepChuoi.append("Thu ngân: ").append(dongDuLieu.getThuNgan()).append('\n');
        boGhepChuoi.append("Khách : ").append(dongDuLieu.getKhachHang()).append('\n');
        if (!nguon.ma().isBlank()) boGhepChuoi.append("Nguồn : ").append(nguon.ma()).append('\n');
        if (!nguon.hoaDonGoc().isBlank())
            boGhepChuoi.append("HĐ gốc: ").append(nguon.hoaDonGoc()).append('\n');
        if (nguon.loai().equals("LUU_TRU"))
            boGhepChuoi
                    .append(layCap("Tiền cọc đã trừ", TienTe.dinhDang(nguon.daCanTru())))
                    .append('\n');
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        for (GioHang.DongHang dongDangXet : dongDuLieu.getCacDong()) {
            boGhepChuoi.append(dongDangXet.getTen()).append('\n');
            boGhepChuoi
                    .append(
                            layCap(
                                    ""
                                            + dongDangXet.getSoLuong()
                                            + "x "
                                            + TienTe.dinhDang(dongDangXet.getDonGia()),
                                    TienTe.dinhDang(dongDangXet.docSoTien())))
                    .append('\n');
        }
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        boGhepChuoi.append(layCap("Tạm tính", TienTe.dinhDang(dongDuLieu.getTamTinh()))).append('\n');
        if (dongDuLieu.getGiamGia() > 0) {
            boGhepChuoi
                    .append(
                            layCap(
                                    (nguon.loai().equals("DOI_HANG")
                                                    ? "Giá trị hàng được đổi"
                                                    : "Giảm giá")
                                            + (dongDuLieu.getVoucher().isEmpty()
                                                    ? ""
                                                    : "(" + dongDuLieu.getVoucher() + ")"),
                                    "-" + TienTe.dinhDang(dongDuLieu.getGiamGia())))
                    .append('\n');
        }
        boGhepChuoi.append(layCap("TỔNG CỘNG", TienTe.dinhDang(dongDuLieu.getTong()))).append('\n');

        String phuongThuc =
                switch (dongDuLieu.getPhuongThuc()) {
                    case TIEN_MAT -> "Tiền mặt";
                    case CHUYEN_KHOAN -> "Chuyển khoản/QR";
                    case THE_NGAN_HANG -> "Thẻ";
                    case DIEM_TICH_LUY -> "Điểm tích lũy";
                    case KET_HOP -> "Kết hợp";
                };
        boGhepChuoi
                .append(
                        layCap(
                                "Thanh toán (" + phuongThuc + ")",
                                TienTe.dinhDang(dongDuLieu.getDaNhan())))
                .append('\n');
        for (var d : dongDuLieu.getCacKhoanThu())
            boGhepChuoi
                    .append(layCap(tenPhuongThuc(d.getPhuongThuc()), TienTe.dinhDang(d.getSoTien())))
                    .append('\n');
        if (dongDuLieu.getThayDoi() > 0)
            boGhepChuoi
                    .append(layCap("Tiền thừa", TienTe.dinhDang(dongDuLieu.getThayDoi())))
                    .append('\n');
        boGhepChuoi.append("-".repeat(CHIEU_RONG)).append('\n');
        boGhepChuoi.append(layGiua("Cảm ơn quý khách!")).append('\n');
        return boGhepChuoi.toString();
    }

    public static String hienThiHTML(GioHang.BienLai bienLai) {
        var nguon = com.petstation.service.BanHangService.layNguonBienLai(bienLai.getSoHoaDon());
        StringBuilder html =
                new StringBuilder(
                        "<html><head><style>body{font-family:Arial;font-size:12px;color:#61341D;margin:20px}h1{color:#CE1D2D;text-align:center;margin:0;font-size:20px}h2{margin:6px"
                            + " 0;font-size:17px}table{width:100%;border-collapse:collapse}td,th{padding:9px;border-bottom:1px"
                            + " solid #e4ded6}th{background:#FDF9ED}p{margin:6px"
                            + " 0}</style></head><body>");
        java.net.URL logo = InHoaDonPanel.class.getResource("/images/pet-station-logo.png");
        html.append("<table><tr><td width='80'>");
        if (logo != null)
            html.append("<img src='").append(logo).append("' width='60' height='60'>");
        html.append("</td><td align='center'><h1>")
                .append(maHoa(com.petstation.service.CaiDatService.lay("STORE_NAME")))
                .append("</h1>");
        html.append(maHoa(com.petstation.service.CaiDatService.lay("STORE_ADDRESS")))
                .append("<br>")
                .append(maHoa(com.petstation.service.CaiDatService.lay("STORE_PHONE")))
                .append("</td></tr></table>");
        html.append("<h2 align='center'>HÓA ĐƠN</h2><table><tr><td>Số hóa đơn: <b>")
                .append(maHoa(bienLai.getSoHoaDon()))
                .append("</b></td><td>Ngày: ")
                .append(bienLai.getThoiGian().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
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
                    .append(TienTe.dinhDang(dong.getDonGia()))
                    .append("</td><td align='right'>")
                    .append(TienTe.dinhDang(dong.docSoTien()))
                    .append("</td></tr>");
        html.append("</table><table>");
        themTong(html, "Tạm tính", bienLai.getTamTinh());
        if (bienLai.getGiamGia() > 0)
            themTong(
                    html,
                    nguon.loai().equals("DOI_HANG")
                            ? "Giá trị hàng được đổi"
                            : "Giảm giá " + bienLai.getVoucher(),
                    -bienLai.getGiamGia());
        if (nguon.loai().equals("LUU_TRU")) themTong(html, "Tiền cọc đã trừ", nguon.daCanTru());
        themTong(html, "TỔNG THANH TOÁN", bienLai.getTong());
        String phuongThuc =
                switch (bienLai.getPhuongThuc()) {
                    case TIEN_MAT -> "Tiền mặt";
                    case CHUYEN_KHOAN -> "Chuyển khoản / QR";
                    case THE_NGAN_HANG -> "Thẻ";
                    case DIEM_TICH_LUY -> "Điểm tích lũy";
                    case KET_HOP -> "Kết hợp";
                };
        themTong(html, "Đã nhận — " + phuongThuc, bienLai.getDaNhan());
        for (var d : bienLai.getCacKhoanThu())
            themTong(html, tenPhuongThuc(d.getPhuongThuc()), d.getSoTien());
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
            case KET_HOP -> "Kết hợp";
        };
    }

    private static void themTong(StringBuilder html, String nhan, long tien) {
        html.append("<tr><td align='right' width='75%'>")
                .append(maHoa(nhan))
                .append("</td><td align='right'><b>")
                .append(TienTe.dinhDang(tien))
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

    private static String layCat(String chuoi, int lonNhat) {
        return chuoi.length() <= lonNhat ? chuoi : chuoi.substring(0, lonNhat - 1) + "…";
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
            java.awt.print.PrinterJob congViec = java.awt.print.PrinterJob.getPrinterJob();
            congViec.setJobName("Hóa đơn " + banHang.getBienLaiCuoi().getSoHoaDon());
            congViec.setPrintable(pnlHoaDon);
            javax.print.attribute.PrintRequestAttributeSet thuocTinh =
                    new javax.print.attribute.HashPrintRequestAttributeSet();
            if (congViec.printDialog(thuocTinh)) {
                congViec.setPrintable(pnlHoaDon, congViec.getPageFormat(thuocTinh));
                congViec.print(thuocTinh);
            }
        } catch (java.awt.print.PrinterException | HeadlessException ngoaiLe) {
            HopThoai.hienThiThongBaoHopThoai(
                    this,
                    "Không thể in: " + ngoaiLe.getMessage(),
                    "Xem hóa đơn",
                    JOptionPane.WARNING_MESSAGE);
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
