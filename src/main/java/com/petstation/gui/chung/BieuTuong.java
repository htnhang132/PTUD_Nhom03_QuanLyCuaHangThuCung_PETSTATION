package com.petstation.gui.chung;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.Icon;

/**
 * Lớp tổng hợp toàn bộ các icon vẽ vector của ứng dụng: 1. BieuTuong.Net: Biểu tượng đường nét
 * (Home, Back, Help, Carts, Calendar...) 2. BieuTuong.Menu: Biểu tượng nhóm menu cấp cao (Hệ thống,
 * Danh mục, Xử lý...) 3. BieuTuong.GiaoDien: Biểu tượng trạng thái phân quyền, mũi tên trước/sau,
 * cảnh báo
 */
public final class BieuTuong {
    private BieuTuong() {}

    // ---------------------------------------------------------
    // 1. BIỂU TƯỢNG ĐƯỜNG NÉT (NET)
    // ---------------------------------------------------------
    public static class Net implements Icon {
        public enum Kieu {
            TRANG_CHINH,
            MOI,
            LUU,
            TIM_KIEM,
            IN,
            HOAN_TAC,
            TRO_GIUP,
            GIO_HANG,
            CAC_NGUOI_DUNG,
            DAU_CHAN,
            GIUONG,
            HOP,
            XE_TAI,
            BIEU_DO,
            LICH,
            KIEM_TRA,
            KHOA,
            TIEN_MAT,
            NHAN,
            DOI_TRA,
            TEP,
            GIOT_NUOC,
            BANH_RANG,
            DONG_HO,
            QUAY_LAI,
            LAM_MOI,
            CHUONG,
            KHIEN,
            QUA_TANG,
            THUNG_RAC,
            BO_NHO_TAM,
            KHOA2
        }

        private final Kieu kieu;
        private final int kichThuoc;
        private final Color mau;

        public Net(Kieu kieu, int kichThuoc, Color mau) {
            this.kieu = kieu;
            this.kichThuoc = kichThuoc;
            this.mau = mau;
        }

        @Override
        public int getIconWidth() {
            return kichThuoc;
        }

        @Override
        public int getIconHeight() {
            return kichThuoc;
        }

        @Override
        public void paintIcon(Component thanhPhan, Graphics doHoa, int toaDoNgang, int toaDoDoc) {
            Graphics2D doHoa2 = (Graphics2D) doHoa.create();
            doHoa2.translate(toaDoNgang, toaDoDoc);
            doHoa2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            doHoa2.setColor(thanhPhan.isEnabled() ? mau : MauUngDung.VO_HIEU_HOA);
            doHoa2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (kieu.ordinal() > Kieu.TRO_GIUP.ordinal()) {
                double tyLe = kichThuoc / 24.0;
                doHoa2.scale(tyLe, tyLe);
                doHoa2.setStroke(
                        new BasicStroke(
                                (float) (1.8 / tyLe),
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND));
                veKyHieu(doHoa2);
            } else {
                veKyHieuCoBan(doHoa2);
            }
            doHoa2.dispose();
        }

        private void veKyHieuCoBan(Graphics2D doHoa2) {
            int moHinhDangXet = 3;
            int suKien = kichThuoc - 4;
            switch (kieu) {
                case TRANG_CHINH -> veNgoiNha(doHoa2);
                case MOI -> {
                    doHoa2.drawLine(kichThuoc / 2, moHinhDangXet, kichThuoc / 2, suKien);
                    doHoa2.drawLine(moHinhDangXet, kichThuoc / 2, suKien, kichThuoc / 2);
                }
                case LUU -> {
                    doHoa2.drawRect(moHinhDangXet, moHinhDangXet, kichThuoc - 7, kichThuoc - 7);
                    doHoa2.drawRect(6, moHinhDangXet, kichThuoc - 12, 5);
                    doHoa2.drawRect(6, kichThuoc - 9, kichThuoc - 12, 6);
                }
                case TIM_KIEM -> {
                    doHoa2.drawOval(moHinhDangXet, moHinhDangXet, kichThuoc - 10, kichThuoc - 10);
                    doHoa2.drawLine(kichThuoc - 8, kichThuoc - 8, suKien, suKien);
                }
                case IN -> {
                    doHoa2.drawRect(5, moHinhDangXet, kichThuoc - 10, 6);
                    doHoa2.drawRect(moHinhDangXet, 8, kichThuoc - 6, 8);
                    doHoa2.drawRect(6, 13, kichThuoc - 12, kichThuoc - 17);
                }
                case HOAN_TAC -> {
                    doHoa2.draw(
                            new Arc2D.Double(
                                    4, 4, kichThuoc - 8, kichThuoc - 8, 30, 250, Arc2D.OPEN));
                    doHoa2.drawLine(4, 8, 4, 3);
                    doHoa2.drawLine(4, 8, 9, 8);
                }
                case TRO_GIUP -> veDauHoi(doHoa2);
                default -> {}
            }
        }

        private void veNgoiNha(Graphics2D doHoa2) {
            double s = kichThuoc;
            Path2D ngoiNha = new Path2D.Double();
            ngoiNha.moveTo(s * 0.12, s * 0.46);
            ngoiNha.lineTo(s * 0.50, s * 0.14);
            ngoiNha.lineTo(s * 0.88, s * 0.46);
            ngoiNha.moveTo(s * 0.23, s * 0.43);
            ngoiNha.lineTo(s * 0.23, s * 0.86);
            ngoiNha.lineTo(s * 0.77, s * 0.86);
            ngoiNha.lineTo(s * 0.77, s * 0.43);
            doHoa2.draw(ngoiNha);
            Path2D cuaChinh = new Path2D.Double();
            cuaChinh.moveTo(s * 0.42, s * 0.86);
            cuaChinh.lineTo(s * 0.42, s * 0.65);
            cuaChinh.quadTo(s * 0.42, s * 0.62, s * 0.45, s * 0.62);
            cuaChinh.lineTo(s * 0.55, s * 0.62);
            cuaChinh.quadTo(s * 0.58, s * 0.62, s * 0.58, s * 0.65);
            cuaChinh.lineTo(s * 0.58, s * 0.86);
            doHoa2.draw(cuaChinh);
        }

        private void veDauHoi(Graphics2D doHoa2) {
            double s = kichThuoc;
            doHoa2.drawOval(
                    (int) Math.round(s * 0.14),
                    (int) Math.round(s * 0.14),
                    (int) Math.round(s * 0.72),
                    (int) Math.round(s * 0.72));
            Path2D dauHoi = new Path2D.Double();
            dauHoi.moveTo(s * 0.38, s * 0.39);
            dauHoi.curveTo(s * 0.38, s * 0.27, s * 0.46, s * 0.22, s * 0.54, s * 0.22);
            dauHoi.curveTo(s * 0.65, s * 0.22, s * 0.69, s * 0.31, s * 0.66, s * 0.39);
            dauHoi.curveTo(s * 0.64, s * 0.46, s * 0.57, s * 0.49, s * 0.52, s * 0.54);
            dauHoi.curveTo(s * 0.50, s * 0.57, s * 0.50, s * 0.60, s * 0.50, s * 0.64);
            doHoa2.draw(dauHoi);
            int kichThuocCham = Math.max(2, (int) Math.round(s * 0.075));
            int toaDoCham = (int) Math.round(s * 0.50 - kichThuocCham / 2.0);
            doHoa2.fillOval(toaDoCham, (int) Math.round(s * 0.73), kichThuocCham, kichThuocCham);
        }

        private static Path2D layDuongDan(double... vungDangXet) {
            Path2D duongDan = new Path2D.Double();
            duongDan.moveTo(vungDangXet[0], vungDangXet[1]);
            for (int chiSo = 2; chiSo < vungDangXet.length; chiSo += 2)
                duongDan.lineTo(vungDangXet[chiSo], vungDangXet[chiSo + 1]);
            return duongDan;
        }

        private static void layDong(Graphics2D doHoa, double x1, double y1, double x2, double y2) {
            doHoa.draw(new Line2D.Double(x1, y1, x2, y2));
        }

        private static void layElip(
                Graphics2D doHoa, double tamNgang, double tamDoc, double dongDuLieu) {
            doHoa.draw(
                    new Ellipse2D.Double(
                            tamNgang - dongDuLieu,
                            tamDoc - dongDuLieu,
                            2 * dongDuLieu,
                            2 * dongDuLieu));
        }

        private void veKyHieu(Graphics2D doHoa) {
            if (!veKyHieuNghiepVu(doHoa)) veKyHieuHeThong(doHoa);
        }

        private boolean veKyHieuNghiepVu(Graphics2D doHoa) {
            switch (kieu) {
                case GIO_HANG -> {
                    doHoa.draw(layDuongDan(3, 4, 6, 4, 8.5, 15, 19, 15, 21, 7, 7, 7));
                    layElip(doHoa, 10, 19, 1.6);
                    layElip(doHoa, 17, 19, 1.6);
                }
                case CAC_NGUOI_DUNG -> {
                    layElip(doHoa, 12, 8, 4);
                    Path2D noiDung = new Path2D.Double();
                    noiDung.moveTo(4, 21);
                    noiDung.quadTo(4, 13.5, 12, 13.5);
                    noiDung.quadTo(20, 13.5, 20, 21);
                    doHoa.draw(noiDung);
                }
                case DAU_CHAN -> {
                    layElip(doHoa, 5.5, 11, 2);
                    layElip(doHoa, 9.5, 6.5, 2);
                    layElip(doHoa, 14.5, 6.5, 2);
                    layElip(doHoa, 18.5, 11, 2);
                    doHoa.draw(new Ellipse2D.Double(7, 13, 10, 8));
                }
                case GIUONG -> {
                    layDong(doHoa, 3, 19, 3, 7);
                    doHoa.draw(layDuongDan(3, 15, 21, 15, 21, 19));
                    doHoa.draw(layDuongDan(21, 15, 21, 11, 10, 11, 10, 15));
                    layElip(doHoa, 6.5, 12, 1.8);
                }
                case HOP -> {
                    doHoa.draw(layDuongDan(12, 3, 20, 7, 20, 17, 12, 21, 4, 17, 4, 7, 12, 3));
                    doHoa.draw(layDuongDan(4, 7, 12, 11, 20, 7));
                    layDong(doHoa, 12, 11, 12, 21);
                }
                case XE_TAI -> {
                    doHoa.drawRect(2, 6, 12, 10);
                    doHoa.draw(layDuongDan(14, 10, 18, 10, 21, 13, 21, 16, 14, 16));
                    layElip(doHoa, 7, 18, 2);
                    layElip(doHoa, 17, 18, 2);
                }
                case BIEU_DO -> {
                    layDong(doHoa, 4, 20, 20, 20);
                    doHoa.drawRect(6, 12, 3, 8);
                    doHoa.drawRect(11, 7, 3, 13);
                    doHoa.drawRect(16, 3, 3, 17);
                }
                case LICH -> {
                    doHoa.draw(new RoundRectangle2D.Double(4, 5, 16, 15, 3, 3));
                    layDong(doHoa, 4, 10, 20, 10);
                    layDong(doHoa, 8, 3, 8, 7);
                    layDong(doHoa, 16, 3, 16, 7);
                }
                case KIEM_TRA -> {
                    layElip(doHoa, 12, 12, 9);
                    doHoa.draw(layDuongDan(8, 12, 11, 15, 16, 9));
                }
                case TIEN_MAT -> {
                    doHoa.draw(new RoundRectangle2D.Double(2, 6, 20, 12, 2, 2));
                    layElip(doHoa, 12, 12, 2.8);
                    layDong(doHoa, 5.5, 12, 6, 12);
                    layDong(doHoa, 18, 12, 18.5, 12);
                }
                case NHAN -> {
                    doHoa.draw(layDuongDan(3, 12, 12, 3, 20, 3, 20, 11, 11, 20, 3, 12));
                    layElip(doHoa, 15.5, 7.5, 1.2);
                }
                case DOI_TRA -> {
                    doHoa.draw(new Arc2D.Double(5, 6, 14, 14, 90, 250, Arc2D.OPEN));
                    doHoa.draw(layDuongDan(5, 3, 5, 8, 10, 8));
                }
                default -> {
                    return false;
                }
            }
            return true;
        }

        private void veKyHieuHeThong(Graphics2D doHoa) {
            switch (kieu) {
                case KHOA, TEP, GIOT_NUOC, BANH_RANG, DONG_HO, QUAY_LAI, LAM_MOI ->
                        veKyHieuHeThongCoBan(doHoa);
                case CHUONG, KHIEN, QUA_TANG, THUNG_RAC, BO_NHO_TAM, KHOA2 ->
                        veKyHieuHeThongBoSung(doHoa);
                default -> {}
            }
        }

        private void veKyHieuHeThongCoBan(Graphics2D doHoa) {
            switch (kieu) {
                case KHOA -> {
                    doHoa.draw(new RoundRectangle2D.Double(5, 11, 14, 10, 2, 2));
                    Path2D quaiKhoa = new Path2D.Double();
                    quaiKhoa.moveTo(8, 11);
                    quaiKhoa.lineTo(8, 8);
                    quaiKhoa.append(new Arc2D.Double(8, 4, 8, 8, 180, -180, Arc2D.OPEN), true);
                    quaiKhoa.lineTo(16, 11);
                    doHoa.draw(quaiKhoa);
                }
                case TEP -> {
                    doHoa.draw(layDuongDan(6, 3, 14, 3, 19, 8, 19, 21, 6, 21, 6, 3));
                    doHoa.draw(layDuongDan(14, 3, 14, 8, 19, 8));
                    layDong(doHoa, 9, 13, 16, 13);
                    layDong(doHoa, 9, 17, 16, 17);
                }
                case GIOT_NUOC -> {
                    Path2D tha = new Path2D.Double();
                    tha.moveTo(12, 3);
                    tha.quadTo(19, 11, 19, 15);
                    tha.append(new Arc2D.Double(5, 8, 14, 14, 0, -180, Arc2D.OPEN), true);
                    tha.quadTo(5, 11, 12, 3);
                    doHoa.draw(tha);
                }
                case BANH_RANG -> {
                    layElip(doHoa, 12, 12, 3);
                    layElip(doHoa, 12, 12, 7);
                    for (int chiSo = 0; chiSo < 8; chiSo++) {
                        double goc = Math.toRadians(chiSo * 45);
                        layDong(
                                doHoa,
                                12 + 7 * Math.cos(goc),
                                12 + 7 * Math.sin(goc),
                                12 + 9.5 * Math.cos(goc),
                                12 + 9.5 * Math.sin(goc));
                    }
                }
                case DONG_HO -> {
                    layElip(doHoa, 12, 12, 9);
                    doHoa.draw(layDuongDan(12, 7, 12, 12, 16, 14));
                }
                case QUAY_LAI -> doHoa.draw(layDuongDan(15, 5, 8, 12, 15, 19));
                case LAM_MOI -> {
                    doHoa.draw(new Arc2D.Double(4.5, 4.5, 15, 15, 30, 285, Arc2D.OPEN));
                    doHoa.draw(layDuongDan(18.5, 4.3, 18.5, 8.25, 14.6, 7.6));
                }
                default -> {}
            }
        }

        private void veKyHieuHeThongBoSung(Graphics2D doHoa) {
            switch (kieu) {
                case CHUONG -> {
                    Path2D chuong = new Path2D.Double();
                    chuong.moveTo(6, 17);
                    chuong.lineTo(6, 11);
                    chuong.append(new Arc2D.Double(6, 5, 12, 12, 180, -180, Arc2D.OPEN), true);
                    chuong.lineTo(18, 17);
                    chuong.lineTo(20, 19);
                    chuong.lineTo(4, 19);
                    chuong.closePath();
                    doHoa.draw(chuong);
                    layDong(doHoa, 10, 21.5, 14, 21.5);
                }
                case KHIEN -> {
                    Path2D khien = new Path2D.Double();
                    khien.moveTo(12, 3);
                    khien.lineTo(20, 6);
                    khien.lineTo(20, 12);
                    khien.quadTo(20, 18, 12, 21);
                    khien.quadTo(4, 18, 4, 12);
                    khien.lineTo(4, 6);
                    khien.closePath();
                    doHoa.draw(khien);
                }
                case QUA_TANG -> {
                    doHoa.drawRect(4, 10, 16, 10);
                    doHoa.drawRect(3, 7, 18, 3);
                    layDong(doHoa, 12, 7, 12, 20);
                    doHoa.draw(new Ellipse2D.Double(7.5, 3, 4.5, 4));
                    doHoa.draw(new Ellipse2D.Double(12, 3, 4.5, 4));
                }
                case THUNG_RAC -> {
                    layDong(doHoa, 4, 7, 20, 7);
                    doHoa.draw(layDuongDan(9, 7, 9, 4, 15, 4, 15, 7));
                    doHoa.draw(layDuongDan(6, 7, 7.5, 21, 16.5, 21, 18, 7));
                    layDong(doHoa, 10, 11, 10, 17);
                    layDong(doHoa, 14, 11, 14, 17);
                }
                case BO_NHO_TAM -> {
                    doHoa.draw(new RoundRectangle2D.Double(5, 4, 14, 17, 2, 2));
                    doHoa.drawRect(9, 2, 6, 4);
                    doHoa.draw(layDuongDan(8.5, 12, 10.5, 14, 15, 10));
                    layDong(doHoa, 8.5, 18, 15.5, 18);
                }
                case KHOA2 -> {
                    layElip(doHoa, 8, 15, 4);
                    layDong(doHoa, 11, 12, 20, 3);
                    layDong(doHoa, 17, 6, 20, 9);
                }
                default -> {}
            }
        }
    }

    // ---------------------------------------------------------
    // 2. BIỂU TƯỢNG MENU CẤP CAO (MENU)
    // ---------------------------------------------------------
    public static class Menu implements Icon {
        private static final int KICH_THUOC = 18;
        private final char nhom;

        public Menu(char nhom) {
            this.nhom = Character.toUpperCase(nhom);
        }

        public static Icon choNhom(char nhom) {
            return new Menu(nhom);
        }

        @Override
        public int getIconWidth() {
            return KICH_THUOC;
        }

        @Override
        public int getIconHeight() {
            return KICH_THUOC;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.translate(x, y);
                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setStroke(new BasicStroke(1.65f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                boolean hover =
                        component instanceof AbstractButton button
                                && button.getModel().isRollover();
                g.setColor(hover ? MauUngDung.DO_THUONG_HIEU : MauUngDung.NAU);
                switch (nhom) {
                    case 'H' -> veHeThong(g);
                    case 'D' -> veDanhMuc(g);
                    case 'T' -> veTimKiem(g);
                    case 'X' -> veXuLy(g);
                    case 'K' -> veThongKe(g);
                    default -> veMacDinh(g);
                }
            } finally {
                g.dispose();
            }
        }

        private static void veHeThong(Graphics2D g) {
            g.draw(new Ellipse2D.Double(5.0, 5.0, 8.0, 8.0));
            g.draw(new Ellipse2D.Double(7.6, 7.6, 2.8, 2.8));
            g.draw(new Line2D.Double(9, 1.5, 9, 4.0));
            g.draw(new Line2D.Double(9, 14.0, 9, 16.5));
            g.draw(new Line2D.Double(1.5, 9, 4.0, 9));
            g.draw(new Line2D.Double(14.0, 9, 16.5, 9));
            g.draw(new Line2D.Double(3.7, 3.7, 5.4, 5.4));
            g.draw(new Line2D.Double(12.6, 12.6, 14.3, 14.3));
            g.draw(new Line2D.Double(12.6, 5.4, 14.3, 3.7));
            g.draw(new Line2D.Double(3.7, 14.3, 5.4, 12.6));
        }

        private static void veDanhMuc(Graphics2D g) {
            g.draw(new RoundRectangle2D.Double(2.0, 2.0, 6.0, 6.0, 1.4, 1.4));
            g.draw(new RoundRectangle2D.Double(10.0, 2.0, 6.0, 6.0, 1.4, 1.4));
            g.draw(new RoundRectangle2D.Double(2.0, 10.0, 6.0, 6.0, 1.4, 1.4));
            g.draw(new RoundRectangle2D.Double(10.0, 10.0, 6.0, 6.0, 1.4, 1.4));
        }

        private static void veTimKiem(Graphics2D g) {
            g.draw(new Ellipse2D.Double(2.0, 2.0, 10.0, 10.0));
            g.draw(new Line2D.Double(10.0, 10.0, 16.0, 16.0));
        }

        private static void veXuLy(Graphics2D g) {
            g.draw(new RoundRectangle2D.Double(3.0, 3.0, 12.0, 13.0, 2.0, 2.0));
            g.draw(new RoundRectangle2D.Double(6.0, 1.5, 6.0, 3.0, 1.2, 1.2));
            Path2D check = new Path2D.Double();
            check.moveTo(5.3, 10.0);
            check.lineTo(7.6, 12.2);
            check.lineTo(12.8, 7.0);
            g.draw(check);
        }

        private static void veThongKe(Graphics2D g) {
            g.draw(new Line2D.Double(2.0, 16.0, 16.0, 16.0));
            g.draw(new RoundRectangle2D.Double(3.0, 10.0, 2.8, 6.0, 0.8, 0.8));
            g.draw(new RoundRectangle2D.Double(7.7, 6.5, 2.8, 9.5, 0.8, 0.8));
            g.draw(new RoundRectangle2D.Double(12.4, 2.5, 2.8, 13.5, 0.8, 0.8));
        }

        private static void veMacDinh(Graphics2D g) {
            g.draw(new Ellipse2D.Double(3.0, 3.0, 12.0, 12.0));
        }
    }

    // ---------------------------------------------------------
    // 3. BIỂU TƯỢNG GIAO DIỆN & TRẠNG THÁI (GIAO_DIEN)
    // ---------------------------------------------------------
    public static class GiaoDien implements Icon {
        public enum Kieu {
            CHO_PHEP,
            KHONG_CHO_PHEP,
            TRUOC,
            SAU,
            CANH_BAO
        }

        private final Kieu kieu;

        public GiaoDien(Kieu kieu) {
            this.kieu = kieu;
        }

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }

        @Override
        public void paintIcon(Component thanhPhan, Graphics doHoa, int ngang, int doc) {
            Graphics2D net = (Graphics2D) doHoa.create();
            try {
                net.translate(ngang, doc);
                net.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                net.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                net.setColor(
                        kieu == Kieu.CHO_PHEP
                                ? MauUngDung.THANH_CONG
                                : kieu == Kieu.CANH_BAO ? MauUngDung.CANH_BAO : MauUngDung.NAU);
                switch (kieu) {
                    case CHO_PHEP -> {
                        net.drawOval(1, 1, 15, 15);
                        net.drawLine(5, 9, 8, 12);
                        net.drawLine(8, 12, 13, 6);
                    }
                    case KHONG_CHO_PHEP -> {
                        net.setColor(MauUngDung.VAN_BAN_XAM);
                        net.drawLine(5, 9, 13, 9);
                    }
                    case TRUOC -> {
                        net.drawLine(12, 3, 6, 9);
                        net.drawLine(6, 9, 12, 15);
                    }
                    case SAU -> {
                        net.drawLine(6, 3, 12, 9);
                        net.drawLine(12, 9, 6, 15);
                    }
                    case CANH_BAO -> {
                        net.drawPolygon(new int[] {9, 1, 17}, new int[] {1, 16, 16}, 3);
                        net.drawLine(9, 6, 9, 10);
                        net.fillOval(8, 12, 2, 2);
                    }
                }
            } finally {
                net.dispose();
            }
        }
    }
}
