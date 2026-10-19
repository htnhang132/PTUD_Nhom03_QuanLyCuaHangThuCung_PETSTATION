package com.petstation.gui.components.common;

import com.petstation.model.ChiTietBienLai;
import com.petstation.service.CaThuNganService;
import com.petstation.util.TienTe;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

/** Cùng một bố cục theo đơn vị point cho bản xem và giấy 80mm; chỉ ngắt giữa các khối. */
public final class HoaDonDocPanel extends JComponent implements Scrollable, Printable {
    public static final double RONG_GIAY = 80.0 / 25.4 * 72;
    private static final double LE = 11, RONG_NOI_DUNG = RONG_GIAY - 2 * LE;
    private static final FontRenderContext DO_CHU = new FontRenderContext(null, true, true);
    private static final BufferedImage LOGO = taiLogo();

    private record Chu(double ngang, double doc, String vanBan, Font font, int can) {}

    private record Khoi(double cao, List<Chu> chu, boolean gach, boolean logo) {}

    private List<Khoi> cacKhoi = List.of();

    public HoaDonDocPanel() {
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    public void datDuLieu(ChiTietBienLai thongTin) {
        List<Khoi> khoi = new ArrayList<>();
        if (LOGO != null) khoi.add(new Khoi(51, List.of(), false, true));
        themChu(khoi, thongTin.tenCuaHang(), 12, true, 1);
        themChu(khoi, thongTin.diaChi(), 8, false, 1);
        themChu(khoi, thongTin.dienThoaiCuaHang(), 8, false, 1);
        khoi.add(new Khoi(8, List.of(), false, false));
        themChu(khoi, "HÓA ĐƠN", 12, true, 1);
        var bienLai = thongTin.bienLai();
        themChu(khoi, "Số: " + bienLai.soHoaDon(), 9, true, 1);
        gach(khoi);
        themChu(
                khoi,
                "Ngày: "
                        + bienLai.thoiGian()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                8,
                false,
                0);
        themChu(khoi, "KH: " + bienLai.khachHang(), 8, false, 0);
        if (!thongTin.dienThoaiKhach().isBlank())
            themChu(khoi, "SĐT: " + thongTin.dienThoaiKhach(), 8, false, 0);
        themChu(khoi, "NVBH: " + bienLai.thuNgan(), 8, false, 0);
        if (!thongTin.maNguon().isBlank())
            themChu(khoi, "Chứng từ: " + thongTin.maNguon(), 8, false, 0);
        if (!thongTin.hoaDonGoc().isBlank())
            themChu(khoi, "Hóa đơn gốc: " + thongTin.hoaDonGoc(), 8, false, 0);
        gach(khoi);
        themHang(khoi, "#", "Tên hàng / dịch vụ", "SL", "ĐG", "TT", true);
        int stt = 0;
        for (var dong : bienLai.cacDong())
            themHang(
                    khoi,
                    String.valueOf(++stt),
                    dong.ten(),
                    String.valueOf(dong.soLuong()),
                    TienTe.dinhDang(dong.donGia()),
                    TienTe.dinhDang(dong.docSoTien()),
                    false);
        gach(khoi);
        themCap(khoi, "Tổng SL mua", String.valueOf(thongTin.tongSoLuong()), false);
        themCap(
                khoi,
                thongTin.loaiNguon().equals("HOTEL") ? "Phải thu sau cọc" : "Tiền hàng",
                TienTe.dinhDang(bienLai.tamTinh()),
                false);
        if (bienLai.giamGia() > 0)
            themCap(
                    khoi,
                    thongTin.loaiNguon().equals("EXCHANGE")
                            ? "Giá trị hàng được đổi"
                            : "Giảm giá / voucher",
                    "−" + TienTe.dinhDang(bienLai.giamGia()),
                    false);
        if (!bienLai.voucher().isBlank())
            themChu(khoi, "Voucher: " + bienLai.voucher(), 8, false, 0);
        if (thongTin.loaiNguon().equals("HOTEL"))
            themCap(khoi, "Cọc đã cấn trừ", TienTe.dinhDang(thongTin.daCanTru()), false);
        themCap(khoi, "Tổng thanh toán", TienTe.dinhDang(bienLai.tong()), true);
        String phuongThuc =
                switch (bienLai.phuongThuc()) {
                    case CASH -> "Tiền mặt";
                    case TRANSFER -> "Chuyển khoản / QR";
                    case CARD -> "Thẻ";
                };
        themCap(khoi, phuongThuc, TienTe.dinhDang(bienLai.daNhan()), false);
        if (bienLai.phuongThuc() == CaThuNganService.PhuongThuc.CASH)
            themCap(khoi, "Trả lại khách", TienTe.dinhDang(bienLai.thayDoi()), false);
        gach(khoi);
        themCap(khoi, "Điểm tích thêm", String.valueOf(thongTin.diemCong()), false);
        if (thongTin.soDuDiemSau() != null)
            themCap(khoi, "Điểm sau giao dịch", String.valueOf(thongTin.soDuDiemSau()), false);
        gach(khoi);
        themChu(khoi, "Tích đủ 200 điểm, đổi voucher Spa 150.000đ — hạn 60 ngày.", 9, true, 1);
        themChu(khoi, "10.000đ = 1 điểm", 9, true, 1);
        khoi.add(new Khoi(7, List.of(), false, false));
        themChu(khoi, "Trân trọng cảm ơn và hẹn gặp lại!", 8, false, 1);
        cacKhoi = List.copyOf(khoi);
        revalidate();
        repaint();
    }

    private static Font font(float co, boolean dam) {
        return new Font(Font.SANS_SERIF, dam ? Font.BOLD : Font.PLAIN, 1).deriveFont(co);
    }

    private static double rong(String chu, Font font) {
        return font.getStringBounds(chu, DO_CHU).getWidth();
    }

    private static List<String> xuongDong(String chu, Font font, double rong) {
        List<String> cacDong = new ArrayList<>();
        String dong = "";
        for (String tu : Objects.toString(chu, "").split("\\s+")) {
            if (tu.isEmpty()) continue;
            if (!dong.isEmpty() && rong(dong + " " + tu, font) > rong) {
                cacDong.add(dong);
                dong = "";
            }
            while (rong(tu, font) > rong && tu.length() > 1) {
                int cat = 1;
                while (cat < tu.length() && rong(tu.substring(0, cat + 1), font) <= rong) cat++;
                cacDong.add(tu.substring(0, cat));
                tu = tu.substring(cat);
            }
            dong = dong.isEmpty() ? tu : dong + " " + tu;
        }
        if (!dong.isEmpty() || cacDong.isEmpty()) cacDong.add(dong);
        return cacDong;
    }

    private static void themChu(List<Khoi> khoi, String chu, float co, boolean dam, int can) {
        Font phong = font(co, dam);
        List<Chu> cacChu = new ArrayList<>();
        double doc = co + 2;
        for (String dong : xuongDong(chu, phong, RONG_NOI_DUNG)) {
            cacChu.add(new Chu(can == 1 ? RONG_NOI_DUNG / 2 : 0, doc, dong, phong, can));
            doc += co + 3;
        }
        khoi.add(new Khoi(doc - co + 1, cacChu, false, false));
    }

    private static void themCap(List<Khoi> khoi, String nhan, String giaTri, boolean dam) {
        Font phong = font(dam ? 9 : 8, dam);
        List<Chu> cacChu = new ArrayList<>();
        double doc = 10;
        for (String dong : xuongDong(nhan, phong, RONG_NOI_DUNG - 78)) {
            cacChu.add(new Chu(0, doc, dong, phong, 0));
            doc += 12;
        }
        cacChu.add(new Chu(RONG_NOI_DUNG, 10, giaTri, phong, 2));
        khoi.add(new Khoi(Math.max(15, doc - 8), cacChu, false, false));
    }

    private static void themHang(
            List<Khoi> khoi,
            String stt,
            String ten,
            String soLuong,
            String gia,
            String tien,
            boolean dam) {
        Font phong = font(7.5f, dam);
        List<Chu> chu = new ArrayList<>();
        double doc = 10;
        var dongTen = xuongDong(ten, phong, 77);
        for (String dong : dongTen) {
            chu.add(new Chu(13, doc, dong, phong, 0));
            doc += 10;
        }
        chu.add(new Chu(0, 10, stt, phong, 0));
        chu.add(new Chu(99, 10, soLuong, phong, 1));
        Font so = phong;
        while ((rong(gia, so) > 43 || rong(tien, so) > 49) && so.getSize2D() > 6)
            so = so.deriveFont(so.getSize2D() - .25f);
        chu.add(new Chu(150, 10, gia, so, 2));
        chu.add(new Chu(RONG_NOI_DUNG, 10, tien, so, 2));
        khoi.add(new Khoi(doc - 6, chu, true, false));
    }

    private static void gach(List<Khoi> khoi) {
        khoi.add(new Khoi(6, List.of(), true, false));
    }

    private static BufferedImage taiLogo() {
        try (var in = HoaDonDocPanel.class.getResourceAsStream("/images/pet-station-logo.png")) {
            return in == null ? null : ImageIO.read(in);
        } catch (IOException e) {
            return null;
        }
    }

    private void veKhoi(Graphics2D net, Khoi khoi, double doc) {
        if (khoi.logo() && LOGO != null) {
            double tiLe = Math.min(46.0 / LOGO.getWidth(), 46.0 / LOGO.getHeight());
            int rong = (int) (LOGO.getWidth() * tiLe), cao = (int) (LOGO.getHeight() * tiLe);
            net.drawImage(LOGO, (int) ((RONG_NOI_DUNG - rong) / 2), (int) doc, rong, cao, null);
        }
        net.setColor(Color.BLACK);
        for (var chu : khoi.chu()) {
            net.setFont(chu.font());
            double ngang = chu.ngang();
            if (chu.can() == 1) ngang -= rong(chu.vanBan(), chu.font()) / 2;
            else if (chu.can() == 2) ngang -= rong(chu.vanBan(), chu.font());
            net.drawString(chu.vanBan(), (float) ngang, (float) (doc + chu.doc()));
        }
        if (khoi.gach()) {
            net.setColor(new Color(190, 180, 170));
            net.setStroke(new BasicStroke(.35f));
            net.drawLine(
                    0,
                    (int) (doc + khoi.cao() - 1),
                    (int) RONG_NOI_DUNG,
                    (int) (doc + khoi.cao() - 1));
        }
    }

    private double chieuCao() {
        return cacKhoi.stream().mapToDouble(Khoi::cao).sum() + 2 * LE;
    }

    @Override
    public Dimension getPreferredSize() {
        int rong = getWidth() > 0 ? getWidth() : 420;
        return new Dimension(420, (int) Math.ceil(chieuCao() * rong / RONG_GIAY));
    }

    @Override
    protected void paintComponent(Graphics doHoa) {
        super.paintComponent(doHoa);
        Graphics2D net = (Graphics2D) doHoa.create();
        try {
            net.setColor(Color.WHITE);
            net.fillRect(0, 0, getWidth(), getHeight());
            net.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            net.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            net.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            double tiLe = getWidth() / RONG_GIAY;
            net.scale(tiLe, tiLe);
            net.translate(LE, LE);
            double doc = 0;
            for (var khoi : cacKhoi) {
                veKhoi(net, khoi, doc);
                doc += khoi.cao();
            }
        } finally {
            net.dispose();
        }
    }

    /** In theo khối nguyên vẹn, giữ vùng nội dung 80mm khi giấy đang chọn lớn hơn. */
    @Override
    public int print(Graphics doHoa, PageFormat trang, int soTrang) throws PrinterException {
        if (cacKhoi.isEmpty()) return NO_SUCH_PAGE;
        double tiLe = Math.min(1, trang.getImageableWidth() / RONG_GIAY),
                cao = trang.getImageableHeight() / tiLe - 2 * LE;
        if (cao < 60)
            throw new PrinterException("Vùng in quá nhỏ. Hãy chọn giấy có chiều cao lớn hơn.");
        int batDau = 0, hienTai = 0;
        while (batDau < cacKhoi.size()) {
            double tong = 0;
            int ketThuc = batDau;
            while (ketThuc < cacKhoi.size() && tong + cacKhoi.get(ketThuc).cao() <= cao) {
                tong += cacKhoi.get(ketThuc++).cao();
            }
            if (ketThuc == batDau)
                throw new PrinterException(
                        "Dòng hóa đơn dài hơn vùng in. Hãy tăng chiều cao giấy.");
            if (hienTai++ == soTrang) {
                Graphics2D net = (Graphics2D) doHoa.create();
                try {
                    net.translate(
                            trang.getImageableX()
                                    + (trang.getImageableWidth() - RONG_GIAY * tiLe) / 2,
                            trang.getImageableY());
                    net.scale(tiLe, tiLe);
                    net.translate(LE, LE);
                    double doc = 0;
                    for (int i = batDau; i < ketThuc; i++) {
                        veKhoi(net, cacKhoi.get(i), doc);
                        doc += cacKhoi.get(i).cao();
                    }
                } finally {
                    net.dispose();
                }
                return PAGE_EXISTS;
            }
            batDau = ketThuc;
        }
        return NO_SUCH_PAGE;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(420, 600);
    }

    public int getScrollableUnitIncrement(Rectangle vung, int huong, int chieu) {
        return 24;
    }

    public int getScrollableBlockIncrement(Rectangle vung, int huong, int chieu) {
        return Math.max(24, vung.height - 24);
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
