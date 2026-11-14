package com.petstation.gui.banhang;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.util.DinhDang;
import com.petstation.util.TaoMaVietQR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public final class ThuTienDialog {
    public static final class KetQuaThu {
        private final PhuongThuc phuongThuc;
        private final long soTien;
        private final long khachDua;

        public KetQuaThu(PhuongThuc phuongThuc, long soTien, long khachDua) {
            this.phuongThuc = phuongThuc;
            this.soTien = soTien;
            this.khachDua = khachDua;
        }

        public PhuongThuc phuongThuc() {
            return phuongThuc;
        }

        public long soTien() {
            return soTien;
        }

        public long khachDua() {
            return khachDua;
        }
    }

    private ThuTienDialog() {}

    public static KetQuaThu nhap(Component cha, long canThu, String thamChieu) {
        if (canThu <= 0) throw new IllegalArgumentException("Số tiền cần thu phải lớn hơn 0.");
        JComboBox<PhuongThucItem> cboPhuongThuc =
                new JComboBox<>(
                        new PhuongThucItem[] {
                            new PhuongThucItem(PhuongThuc.TIEN_MAT, "Tiền mặt"),
                            new PhuongThucItem(PhuongThuc.CHUYEN_KHOAN, "Chuyển khoản QR"),
                            new PhuongThucItem(PhuongThuc.THE_NGAN_HANG, "Thẻ ngân hàng")
                        });
        JTextField oKhachDua = new JTextField(String.valueOf(canThu), 14);
        JLabel nhanTienThua = new JLabel(DinhDang.dinhDang(0));
        JCheckBox chkDaNhanTien = new JCheckBox();
        JLabel nhanQr = new JLabel();
        nhanQr.setHorizontalAlignment(SwingConstants.CENTER);
        nhanQr.setPreferredSize(new Dimension(220, 220));
        JTextArea oHuongDan = taoVungHuongDan();
        JLabel nhanNhanKhachDua = new JLabel("Tiền khách đưa:");
        JLabel nhanNhanTienThua = new JLabel("Tiền trả lại:");
        JPanel khungNhap =
                taoKhungNhapThu(
                        canThu,
                        cboPhuongThuc,
                        nhanNhanKhachDua,
                        oKhachDua,
                        nhanNhanTienThua,
                        nhanTienThua);
        JPanel khungHuongDan = taoKhungHuongDanThu(nhanQr, oHuongDan);
        JPanel khungChinh = taoKhungChinhThu(khungNhap, khungHuongDan, chkDaNhanTien);
        JScrollPane scrNoiDung = taoKhungCuon(khungChinh);
        boolean[] qrHopLe = {false};
        String[] loiTaoQr = {""};
        Runnable capNhatTienThua = () -> capNhatTienThua(oKhachDua, nhanTienThua, canThu);
        Runnable capNhatGiaoDien =
                () -> {
                    chkDaNhanTien.setSelected(false);
                    qrHopLe[0] = false;
                    loiTaoQr[0] = "";
                    PhuongThuc pt = layPhuongThuc(cboPhuongThuc);
                    boolean tienMat = pt == PhuongThuc.TIEN_MAT;
                    nhanNhanKhachDua.setVisible(tienMat);
                    oKhachDua.setVisible(tienMat);
                    nhanNhanTienThua.setVisible(tienMat);
                    nhanTienThua.setVisible(tienMat);
                    if (tienMat) {
                        hienThiTienMat(nhanQr, chkDaNhanTien, oHuongDan, capNhatTienThua);
                    } else if (pt == PhuongThuc.CHUYEN_KHOAN) {
                        hienThiChuyenKhoanQR(
                                canThu,
                                thamChieu,
                                nhanQr,
                                chkDaNhanTien,
                                oHuongDan,
                                qrHopLe,
                                loiTaoQr);
                    } else {
                        hienThiQuetThe(canThu, nhanQr, chkDaNhanTien, oHuongDan);
                    }
                    khungChinh.revalidate();
                    khungChinh.repaint();
                    scrNoiDung.revalidate();
                    scrNoiDung.repaint();
                };
        cboPhuongThuc.addActionListener(e -> capNhatGiaoDien.run());
        CoSoPanel.themBoLangNgheTaiLieu(oKhachDua, capNhatTienThua);
        capNhatGiaoDien.run();
        return thuXacNhanKhoanThu(
                cha,
                scrNoiDung,
                khungChinh,
                cboPhuongThuc,
                canThu,
                oKhachDua,
                chkDaNhanTien,
                qrHopLe,
                loiTaoQr);
    }

    private static JPanel taoKhungHuongDanThu(JLabel nhanQr, JTextArea oHuongDan) {
        JPanel khungHuongDan = new JPanel(new BorderLayout(8, 8));
        khungHuongDan.setBorder(BorderFactory.createTitledBorder("Thông tin thanh toán"));
        khungHuongDan.add(nhanQr, BorderLayout.NORTH);
        khungHuongDan.add(oHuongDan, BorderLayout.CENTER);
        khungHuongDan.setPreferredSize(new Dimension(410, 360));
        return khungHuongDan;
    }

    private static JPanel taoKhungChinhThu(
            JPanel khungNhap, JPanel khungHuongDan, JCheckBox chkDaNhanTien) {
        JPanel khungChinh = new JPanel(new BorderLayout(8, 8));
        khungChinh.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        khungChinh.add(khungNhap, BorderLayout.NORTH);
        khungChinh.add(khungHuongDan, BorderLayout.CENTER);
        khungChinh.add(chkDaNhanTien, BorderLayout.SOUTH);
        return khungChinh;
    }

    private static KetQuaThu thuXacNhanKhoanThu(
            Component cha,
            JScrollPane scrNoiDung,
            JPanel khungChinh,
            JComboBox<PhuongThucItem> cboPhuongThuc,
            long canThu,
            JTextField oKhachDua,
            JCheckBox chkDaNhanTien,
            boolean[] qrHopLe,
            String[] loiTaoQr) {
        Object[] cacNut = {"Xác nhận thu cọc", "Hủy"};
        while (true) {
            if (HopThoai.tuyChon(cha, scrNoiDung, "Thu cọc lưu trú", cacNut, false) != 0)
                return null;
            try {
                return xacNhanKhoanThu(
                        layPhuongThuc(cboPhuongThuc),
                        canThu,
                        oKhachDua,
                        chkDaNhanTien,
                        qrHopLe[0],
                        loiTaoQr[0]);
            } catch (Exception ex) {
                HopThoai.hienThiCanhBao(khungChinh, ex.getMessage());
            }
        }
    }

    private static JTextArea taoVungHuongDan() {
        JTextArea oNhapLieu = new JTextArea(5, 36);
        oNhapLieu.setEditable(false);
        oNhapLieu.setLineWrap(true);
        oNhapLieu.setWrapStyleWord(true);
        oNhapLieu.setOpaque(false);
        return oNhapLieu;
    }

    private static JPanel taoKhungNhapThu(
            long canThu,
            JComboBox<PhuongThucItem> cboPhuongThuc,
            JLabel nhanNhanKhachDua,
            JTextField oKhachDua,
            JLabel nhanNhanTienThua,
            JLabel nhanTienThua) {
        JPanel khungNhap = new JPanel(new GridLayout(0, 2, 8, 8));
        khungNhap.add(new JLabel("Số tiền cần thu:"));
        JLabel nhanCanThu = new JLabel(DinhDang.dinhDang(canThu));
        nhanCanThu.setFont(nhanCanThu.getFont().deriveFont(Font.BOLD));
        khungNhap.add(nhanCanThu);
        khungNhap.add(new JLabel("Phương thức:"));
        khungNhap.add(cboPhuongThuc);
        khungNhap.add(nhanNhanKhachDua);
        khungNhap.add(oKhachDua);
        khungNhap.add(nhanNhanTienThua);
        khungNhap.add(nhanTienThua);
        return khungNhap;
    }

    private static JScrollPane taoKhungCuon(JPanel khungChinh) {
        JScrollPane scr = new JScrollPane(khungChinh);
        scr.setBorder(BorderFactory.createEmptyBorder());
        scr.setPreferredSize(new Dimension(470, 440));
        scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scr.getVerticalScrollBar().setUnitIncrement(18);
        scr.getViewport().setBackground(khungChinh.getBackground());
        return scr;
    }

    private static PhuongThuc layPhuongThuc(JComboBox<PhuongThucItem> cbo) {
        PhuongThucItem chon = (PhuongThucItem) cbo.getSelectedItem();
        return chon != null ? chon.phuongThuc() : PhuongThuc.TIEN_MAT;
    }

    private static void capNhatTienThua(JTextField oKhachDua, JLabel nhanTienThua, long canThu) {
        try {
            nhanTienThua.setText(DinhDang.dinhDang(Math.max(0, docTien(oKhachDua) - canThu)));
        } catch (Exception ex) {
            nhanTienThua.setText("Số tiền chưa đúng");
        }
    }

    private static void hienThiTienMat(
            JLabel nhanQr, JCheckBox chkDaNhanTien, JTextArea oHuongDan, Runnable capNhatTienThua) {
        nhanQr.setIcon(null);
        nhanQr.setText("");
        nhanQr.setVisible(false);
        chkDaNhanTien.setVisible(false);
        chkDaNhanTien.setEnabled(false);
        oHuongDan.setText(
                "Nhập số tiền khách thực đưa. Hệ thống tự tính tiền thừa; chỉ hoàn tất khi số"
                        + " tiền khách đưa không thấp hơn số tiền cần thu.");
        capNhatTienThua.run();
    }

    private static final class ThongTinVietQR {
        final ImageIcon icon;
        final String noiDungHuongDan;

        ThongTinVietQR(ImageIcon icon, String noiDungHuongDan) {
            this.icon = icon;
            this.noiDungHuongDan = noiDungHuongDan;
        }
    }

    private static void hienThiChuyenKhoanQR(
            long canThu,
            String thamChieu,
            JLabel nhanQr,
            JCheckBox chkDaNhanTien,
            JTextArea oHuongDan,
            boolean[] qrHopLe,
            String[] loiTaoQr) {
        nhanQr.setVisible(true);
        nhanQr.setIcon(null);
        nhanQr.setText("<html><center>Đang tạo mã VietQR...</center></html>");
        chkDaNhanTien.setVisible(true);
        chkDaNhanTien.setEnabled(false);
        chkDaNhanTien.setText("Đã kiểm tra tài khoản và nhận đủ tiền chuyển khoản");
        oHuongDan.setText("Đang tải thông tin tài khoản ngân hàng...");
        TacVuNen.chayCoPhien(
                chkDaNhanTien,
                () -> true,
                () -> {
                    String bin = CaiDatService.macDinh().layTuyChon("BANK_BIN");
                    String tk = CaiDatService.macDinh().layTuyChon("BANK_ACCOUNT");
                    String ten = CaiDatService.macDinh().layTuyChon("BANK_ACCOUNT_NAME");
                    TaoMaVietQR.kiemTraTaiKhoan(bin, tk, ten);
                    String maThamChieu = taoThamChieuQR(thamChieu);
                    String noiDungQr = TaoMaVietQR.taoNoiDung(bin, tk, canThu, maThamChieu);
                    ImageIcon icon = new ImageIcon(TaoMaVietQR.taoAnh(noiDungQr, 220));
                    String huongDan =
                            "Ngân hàng nhận: BIN "
                                    + bin
                                    + "\nChủ tài khoản: "
                                    + ten
                                    + "\nSố tài khoản: "
                                    + tk
                                    + "\nSố tiền: "
                                    + DinhDang.dinhDang(canThu)
                                    + "\nNội dung chuyển khoản: "
                                    + maThamChieu
                                    + "\n\nKhách quét mã bằng ứng dụng ngân hàng. Chỉ đánh dấu xác"
                                    + " nhận sau khi nhân viên kiểm tra tiền đã vào đúng tài"
                                    + " khoản.";
                    return new ThongTinVietQR(icon, huongDan);
                },
                thongTin -> {
                    nhanQr.setIcon(thongTin.icon);
                    nhanQr.setText("");
                    oHuongDan.setText(thongTin.noiDungHuongDan);
                    qrHopLe[0] = true;
                    chkDaNhanTien.setEnabled(true);
                },
                ex -> {
                    loiTaoQr[0] =
                            ex.getMessage() == null
                                    ? "Không xác định được nguyên nhân."
                                    : ex.getMessage();
                    nhanQr.setIcon(null);
                    nhanQr.setText("<html><center>Không tạo được mã VietQR</center></html>");
                    oHuongDan.setText(
                            "Lý do: "
                                    + loiTaoQr[0]
                                    + "\n\nKiểm tra lại BIN, số tài khoản và tên chủ tài"
                                    + " khoản tại Cài đặt > Cửa hàng & Hóa đơn. Không thể xác nhận"
                                    + " thu cọc bằng QR khi chưa tạo được mã.");
                });
    }

    private static void hienThiQuetThe(
            long canThu, JLabel nhanQr, JCheckBox chkDaNhanTien, JTextArea oHuongDan) {
        nhanQr.setIcon(null);
        nhanQr.setText("");
        nhanQr.setVisible(false);
        chkDaNhanTien.setVisible(true);
        chkDaNhanTien.setEnabled(true);
        chkDaNhanTien.setText("Tôi đã kiểm tra giao dịch thành công trên máy POS");
        oHuongDan.setText(
                "1. Thực hiện thanh toán thẻ trên máy POS của cửa hàng.\n"
                        + "2. Kiểm tra máy POS báo giao dịch THÀNH CÔNG và đúng số tiền "
                        + DinhDang.dinhDang(canThu)
                        + ".\n"
                        + "3. Chỉ đánh dấu xác nhận sau khi đã kiểm tra kết quả trên POS.\n\n"
                        + "Lưu ý: ứng dụng hiện ghi nhận xác nhận của nhân viên, chưa kết nối trực"
                        + " tiếp với máy POS.");
    }

    private static KetQuaThu xacNhanKhoanThu(
            PhuongThuc pt,
            long canThu,
            JTextField oKhachDua,
            JCheckBox chkDaNhanTien,
            boolean qrHopLe,
            String loiTaoQr) {
        if (pt == PhuongThuc.TIEN_MAT) {
            long khachDua = docTien(oKhachDua);
            if (khachDua < canThu)
                throw new IllegalArgumentException(
                        "Tiền khách đưa phải lớn hơn hoặc bằng số tiền cần thu.");
            return new KetQuaThu(PhuongThuc.TIEN_MAT, canThu, khachDua);
        }
        if (pt == PhuongThuc.CHUYEN_KHOAN && !qrHopLe)
            throw new IllegalArgumentException("Chưa tạo được mã QR hợp lệ. " + loiTaoQr);
        if (!chkDaNhanTien.isSelected())
            throw new IllegalArgumentException(
                    pt == PhuongThuc.CHUYEN_KHOAN
                            ? "Vui lòng kiểm tra tiền đã vào tài khoản rồi mới xác nhận."
                            : "Vui lòng kiểm tra máy POS báo giao dịch thành công rồi mới xác"
                                    + " nhận.");
        return new KetQuaThu(pt, canThu, canThu);
    }

    private static String taoThamChieuQR(String maHopDong) {
        if (maHopDong == null || maHopDong.isBlank()) {
            throw new IllegalArgumentException("Không tìm thấy mã hợp đồng lưu trú.");
        }
        String ma = maHopDong.trim().toUpperCase(Locale.ROOT);
        if (!ma.matches("[A-Z0-9 ]{1,25}")) {
            throw new IllegalArgumentException(
                    "Mã hợp đồng không hợp lệ để dùng làm nội dung chuyển khoản VietQR.");
        }
        return ma;
    }

    private static long docTien(JTextField f) {
        String s = f.getText().trim();
        if (!s.matches("[0-9]+")) {
            throw new IllegalArgumentException("Vui lòng nhập số tiền hợp lệ.");
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số tiền vượt quá giới hạn cho phép.", ex);
        }
    }

    private static final class PhuongThucItem {
        private final PhuongThuc phuongThuc;
        private final String ten;

        private PhuongThucItem(PhuongThuc phuongThuc, String ten) {
            this.phuongThuc = phuongThuc;
            this.ten = ten;
        }

        PhuongThuc phuongThuc() {
            return phuongThuc;
        }

        @Override
        public String toString() {
            return ten;
        }
    }
}
