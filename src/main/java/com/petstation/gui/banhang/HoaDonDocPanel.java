package com.petstation.gui.banhang;

import com.petstation.dto.ChiTietBienLai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.model.chung.LoaiPhieuDoiTra;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.util.DinhDang;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Scrollable;

public final class HoaDonDocPanel extends JComponent implements Scrollable, Printable {
    public static final double RONG_GIAY = 80.0 / 25.4 * 72;
    private static final double LE = 11, RONG_NOI_DUNG = RONG_GIAY - 2 * LE;
    private static final FontRenderContext DO_CHU = new FontRenderContext(null, true, true);
    private static final BufferedImage LOGO = taiLogo();

    private static final class Chu {
        final double ngang;
        final double doc;
        final String vanBan;
        final Font font;
        final int can;

        private Chu(double ngang, double doc, String vanBan, Font font, int can) {
            this.ngang = ngang;
            this.doc = doc;
            this.vanBan = vanBan;
            this.font = font;
            this.can = can;
        }
    }

    private static final class Khoi {
        final double cao;
        final List<Chu> chu;
        final boolean gach;
        final boolean logo;

        private Khoi(double cao, List<Chu> chu, boolean gach, boolean logo) {
            this.cao = cao;
            this.chu = chu;
            this.gach = gach;
            this.logo = logo;
        }
    }

    private List<Khoi> cacKhoi = List.of();

    public HoaDonDocPanel() {
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    public void datDuLieu(ChiTietBienLai thongTin) {
        if (thongTin == null) {
            cacKhoi = List.of();
            revalidate();
            repaint();
            return;
        }
        List<Khoi> khoi = new ArrayList<>();
        if (LOGO != null) khoi.add(new Khoi(51, List.of(), false, true));
        themPhanDauBienLaiDoc(khoi, thongTin);
        themBangMatHangBienLaiDoc(khoi, thongTin);
        themTongKetVaThanhToanBienLaiDoc(khoi, thongTin);
        themChanTrangBienLaiDoc(khoi, thongTin);
        cacKhoi = khoi;
        revalidate();
        repaint();
    }

    private static void themPhanDauBienLaiDoc(List<Khoi> khoi, ChiTietBienLai thongTin) {
        var bienLai = thongTin.getBienLai();
        themChu(khoi, thongTin.getTenCuaHang(), 12, true, 1);
        themChu(khoi, thongTin.getDiaChi(), 8, false, 1);
        themChu(khoi, thongTin.getDienThoaiCuaHang(), 8, false, 1);
        khoi.add(new Khoi(8, List.of(), false, false));
        themChu(khoi, "HÓA ĐƠN", 12, true, 1);
        themChu(khoi, "Số: " + bienLai.getSoHoaDon(), 9, true, 1);
        gach(khoi);
        themChu(khoi, "Ngày: " + bienLai.getThoiGian().format(DinhDang.NGAY_GIO), 8, false, 0);
        themChu(khoi, "KH: " + bienLai.getKhachHang(), 8, false, 0);
        if (!thongTin.getDienThoaiKhach().isBlank())
            themChu(khoi, "SĐT: " + thongTin.getDienThoaiKhach(), 8, false, 0);
        themChu(khoi, "NVBH: " + bienLai.getThuNgan(), 8, false, 0);
        if (!thongTin.getMaNguon().isBlank())
            themChu(khoi, "Chứng từ: " + thongTin.getMaNguon(), 8, false, 0);
        if (!thongTin.getHoaDonGoc().isBlank())
            themChu(khoi, "Hóa đơn gốc: " + thongTin.getHoaDonGoc(), 8, false, 0);
        gach(khoi);
    }

    private static void themBangMatHangBienLaiDoc(List<Khoi> khoi, ChiTietBienLai thongTin) {
        themHang(khoi, "#", "Tên hàng / dịch vụ", "SL", "ĐG", "TT", true);
        int stt = 0;
        for (var dong : thongTin.getBienLai().getCacDong())
            themHang(
                    khoi,
                    String.valueOf(++stt),
                    dong.getTen(),
                    String.valueOf(dong.getSoLuong()),
                    DinhDang.dinhDang(dong.getDonGia()),
                    DinhDang.dinhDang(dong.docSoTien()),
                    false);
        gach(khoi);
    }

    private static void themTongKetVaThanhToanBienLaiDoc(List<Khoi> khoi, ChiTietBienLai thongTin) {
        var bienLai = thongTin.getBienLai();
        themCap(khoi, "Tổng SL mua", String.valueOf(thongTin.tongSoLuong()), false);
        themCap(
                khoi,
                thongTin.getLoaiNguon().equals("LUU_TRU") ? "Phải thu sau cọc" : "Tiền hàng",
                DinhDang.dinhDang(bienLai.getTamTinh()),
                false);
        if (bienLai.getGiamGia() > 0)
            themCap(
                    khoi,
                    thongTin.getLoaiNguon().equals(LoaiPhieuDoiTra.DOI_HANG.maDB())
                            ? "Giá trị hàng được đổi"
                            : "Giảm giá / voucher",
                    "−" + DinhDang.dinhDang(bienLai.getGiamGia()),
                    false);
        if (!bienLai.getVoucher().isBlank())
            themChu(khoi, "Voucher: " + bienLai.getVoucher(), 8, false, 0);
        if (thongTin.getLoaiNguon().equals("LUU_TRU"))
            themCap(khoi, "Cọc đã cấn trừ", DinhDang.dinhDang(thongTin.getDaCanTru()), false);
        themCap(khoi, "Tổng thanh toán", DinhDang.dinhDang(bienLai.getTong()), true);
        if (bienLai.getVatPhanTram() > 0)
            themCap(
                    khoi,
                    "Đã gồm VAT (" + bienLai.getVatPhanTram() + "%)",
                    DinhDang.dinhDang(bienLai.tinhTienThueGtgt()),
                    false);
        themCap(
                khoi,
                tenPhuongThucBienLai(bienLai.getPhuongThuc()),
                DinhDang.dinhDang(bienLai.getDaNhan()),
                false);
        if (bienLai.getThayDoi() > 0)
            themCap(khoi, "Trả lại khách", DinhDang.dinhDang(bienLai.getThayDoi()), false);
        gach(khoi);
    }

    private static String tenPhuongThucBienLai(PhuongThuc pt) {
        return switch (pt) {
            case TIEN_MAT -> "Tiền mặt";
            case CHUYEN_KHOAN -> "Chuyển khoản / QR";
            case THE_NGAN_HANG -> "Thẻ";
            case DIEM_TICH_LUY -> "Điểm tích lũy";
        };
    }

    private static void themChanTrangBienLaiDoc(List<Khoi> khoi, ChiTietBienLai thongTin) {
        themCap(khoi, "Điểm tích thêm", String.valueOf(thongTin.getDiemCong()), false);
        if (thongTin.getSoDuDiemSau() != null)
            themCap(khoi, "Điểm sau giao dịch", String.valueOf(thongTin.getSoDuDiemSau()), false);
        gach(khoi);
        themChu(khoi, "Tích đủ 200 điểm, đổi voucher Spa 150.000đ — hạn 60 ngày.", 9, true, 1);
        themChu(khoi, "10.000đ = 1 điểm", 9, true, 1);
        khoi.add(new Khoi(7, List.of(), false, false));
        themChu(khoi, "Trân trọng cảm ơn và hẹn gặp lại!", 8, false, 1);
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
        if (khoi.logo && LOGO != null) {
            double tiLe = Math.min(46.0 / LOGO.getWidth(), 46.0 / LOGO.getHeight());
            int rong = (int) (LOGO.getWidth() * tiLe), cao = (int) (LOGO.getHeight() * tiLe);
            net.drawImage(LOGO, (int) ((RONG_NOI_DUNG - rong) / 2), (int) doc, rong, cao, null);
        }
        net.setColor(Color.BLACK);
        for (var chu : khoi.chu) {
            net.setFont(chu.font);
            double ngang = chu.ngang;
            if (chu.can == 1) ngang -= rong(chu.vanBan, chu.font) / 2;
            else if (chu.can == 2) ngang -= rong(chu.vanBan, chu.font);
            net.drawString(chu.vanBan, (float) ngang, (float) (doc + chu.doc));
        }
        if (khoi.gach) {
            net.setColor(MauUngDung.mau(0xBEB4AA));
            net.setStroke(new BasicStroke(.35f));
            net.drawLine(
                    0, (int) (doc + khoi.cao - 1), (int) RONG_NOI_DUNG, (int) (doc + khoi.cao - 1));
        }
    }

    private double chieuCao() {
        return cacKhoi.stream().mapToDouble(k -> k.cao).sum() + 2 * LE;
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
                doc += khoi.cao;
            }
        } finally {
            net.dispose();
        }
    }

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
            while (ketThuc < cacKhoi.size() && tong + cacKhoi.get(ketThuc).cao <= cao) {
                tong += cacKhoi.get(ketThuc++).cao;
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
                        doc += cacKhoi.get(i).cao;
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
