package com.petstation.gui.components.common;

import com.petstation.gui.components.HopThoai;
import com.petstation.model.DongThuTien;
import com.petstation.model.PhuongThuc;
import com.petstation.service.CaiDatService;
import com.petstation.util.TaoMaVietQR;
import com.petstation.util.TienTe;

import java.awt.*;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Hộp thoại thu tiền theo phương thức. QR chỉ cho xác nhận khi đã tạo được mã;
 * thẻ ngân hàng yêu cầu nhân viên kiểm tra giao dịch thành công trên máy POS.
 */
public final class ThuTienDialog {
    private ThuTienDialog() {}

    public static List<DongThuTien> nhap(Component cha, long canThu, String thamChieu) {
        if (canThu <= 0) throw new IllegalArgumentException("Số tiền cần thu phải lớn hơn 0.");

        JComboBox<PhuongThucItem> cboPhuongThuc =
                new JComboBox<>(
                        new PhuongThucItem[] {
                            new PhuongThucItem(PhuongThuc.TIEN_MAT, "Tiền mặt"),
                            new PhuongThucItem(PhuongThuc.CHUYEN_KHOAN, "Chuyển khoản QR"),
                            new PhuongThucItem(PhuongThuc.THE_NGAN_HANG, "Thẻ ngân hàng")
                        });

        JTextField txtKhachDua = new JTextField(String.valueOf(canThu), 14);
        JLabel lblTienThua = new JLabel(TienTe.dinhDang(0));
        JCheckBox chkDaNhanTien = new JCheckBox();
        JLabel lblQr = new JLabel();
        lblQr.setHorizontalAlignment(SwingConstants.CENTER);
        lblQr.setPreferredSize(new Dimension(220, 220));

        JTextArea txtHuongDan = new JTextArea(5, 36);
        txtHuongDan.setEditable(false);
        txtHuongDan.setLineWrap(true);
        txtHuongDan.setWrapStyleWord(true);
        txtHuongDan.setOpaque(false);
        txtHuongDan.setFocusable(true);

        JPanel pnlNhap = new JPanel(new GridLayout(0, 2, 8, 8));
        pnlNhap.add(new JLabel("Số tiền cần thu:"));
        JLabel lblCanThu = new JLabel(TienTe.dinhDang(canThu));
        lblCanThu.setFont(lblCanThu.getFont().deriveFont(Font.BOLD));
        pnlNhap.add(lblCanThu);

        pnlNhap.add(new JLabel("Phương thức:"));
        pnlNhap.add(cboPhuongThuc);

        JLabel lblNhanKhachDua = new JLabel("Tiền khách đưa:");
        pnlNhap.add(lblNhanKhachDua);
        pnlNhap.add(txtKhachDua);

        JLabel lblNhanTienThua = new JLabel("Tiền trả lại:");
        pnlNhap.add(lblNhanTienThua);
        pnlNhap.add(lblTienThua);

        JPanel pnlHuongDan = new JPanel(new BorderLayout(8, 8));
        pnlHuongDan.setBorder(BorderFactory.createTitledBorder("Thông tin thanh toán"));
        pnlHuongDan.add(lblQr, BorderLayout.NORTH);
        pnlHuongDan.add(txtHuongDan, BorderLayout.CENTER);
        pnlHuongDan.setPreferredSize(new Dimension(410, 360));

        JPanel pnlChinh = new JPanel(new BorderLayout(8, 8));
        pnlChinh.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pnlChinh.add(pnlNhap, BorderLayout.NORTH);
        pnlChinh.add(pnlHuongDan, BorderLayout.CENTER);
        pnlChinh.add(chkDaNhanTien, BorderLayout.SOUTH);

        // Giới hạn chiều cao vùng nội dung để nút xác nhận/hủy luôn nằm ngoài vùng cuộn.
        JScrollPane scrNoiDung = new JScrollPane(pnlChinh);
        scrNoiDung.setBorder(BorderFactory.createEmptyBorder());
        scrNoiDung.setPreferredSize(new Dimension(470, 440));
        scrNoiDung.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrNoiDung.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrNoiDung.getVerticalScrollBar().setUnitIncrement(18);
        scrNoiDung.getViewport().setBackground(pnlChinh.getBackground());

        Object[] cacNut = {"Xác nhận thu cọc", "Hủy"};

        final boolean[] qrHopLe = {false};
        final String[] loiTaoQr = {""};

        Runnable capNhatTienThua =
                () -> {
                    try {
                        long khachDua = docTien(txtKhachDua);
                        lblTienThua.setText(TienTe.dinhDang(Math.max(0, khachDua - canThu)));
                    } catch (Exception ex) {
                        lblTienThua.setText("Số tiền chưa đúng");
                    }
                };

        Runnable capNhatGiaoDien =
                () -> {
                    // Không cho dùng xác nhận của phương thức trước cho phương thức mới.
                    chkDaNhanTien.setSelected(false);
                    qrHopLe[0] = false;
                    loiTaoQr[0] = "";

                    PhuongThucItem chon = (PhuongThucItem) cboPhuongThuc.getSelectedItem();
                    PhuongThuc pt = chon != null ? chon.phuongThuc() : PhuongThuc.TIEN_MAT;

                    if (pt == PhuongThuc.TIEN_MAT) {
                        lblNhanKhachDua.setVisible(true);
                        txtKhachDua.setVisible(true);
                        lblNhanTienThua.setVisible(true);
                        lblTienThua.setVisible(true);
                        lblQr.setIcon(null);
                        lblQr.setText("");
                        lblQr.setVisible(false);
                        chkDaNhanTien.setVisible(false);
                        chkDaNhanTien.setEnabled(false);
                        txtHuongDan.setText(
                                "Nhập số tiền khách thực đưa. Hệ thống tự tính tiền thừa; "
                                        + "chỉ hoàn tất khi số tiền khách đưa không thấp hơn "
                                        + "số tiền cần thu.");
                        capNhatTienThua.run();
                    } else if (pt == PhuongThuc.CHUYEN_KHOAN) {
                        lblNhanKhachDua.setVisible(false);
                        txtKhachDua.setVisible(false);
                        lblNhanTienThua.setVisible(false);
                        lblTienThua.setVisible(false);
                        lblQr.setVisible(true);
                        chkDaNhanTien.setVisible(true);
                        chkDaNhanTien.setEnabled(false);
                        chkDaNhanTien.setText("Đã kiểm tra tài khoản và nhận đủ tiền chuyển khoản");

                        try {
                            String bin = CaiDatService.layTuyChon("BANK_BIN");
                            String tk = CaiDatService.layTuyChon("BANK_ACCOUNT");
                            String ten = CaiDatService.layTuyChon("BANK_ACCOUNT_NAME");
                            TaoMaVietQR.kiemTraTaiKhoan(bin, tk, ten);

                            String maThamChieu = taoThamChieuQR(thamChieu);
                            String noiDungQr =
                                    TaoMaVietQR.taoNoiDung(bin, tk, canThu, maThamChieu);
                            ImageIcon anhQr = new ImageIcon(TaoMaVietQR.taoAnh(noiDungQr, 220));

                            lblQr.setIcon(anhQr);
                            lblQr.setText("");
                            txtHuongDan.setText(
                                    "Ngân hàng nhận: BIN " + bin
                                            + "\nChủ tài khoản: " + ten
                                            + "\nSố tài khoản: " + tk
                                            + "\nSố tiền: " + TienTe.dinhDang(canThu)
                                            + "\nNội dung chuyển khoản: " + maThamChieu
                                            + "\n\nKhách quét mã bằng ứng dụng ngân hàng. "
                                            + "Chỉ đánh dấu xác nhận sau khi nhân viên kiểm tra "
                                            + "tiền đã vào đúng tài khoản.");
                            qrHopLe[0] = true;
                            chkDaNhanTien.setEnabled(true);
                        } catch (Exception ex) {
                            loiTaoQr[0] = ex.getMessage() == null
                                    ? "Không xác định được nguyên nhân."
                                    : ex.getMessage();
                            lblQr.setIcon(null);
                            lblQr.setText("<html><center>Không tạo được mã VietQR</center></html>");
                            txtHuongDan.setText(
                                    "Lý do: " + loiTaoQr[0]
                                            + "\n\nKiểm tra lại BIN, số tài khoản và tên chủ tài khoản "
                                            + "tại Cài đặt > Cửa hàng & Hóa đơn. "
                                            + "Không thể xác nhận thu cọc bằng QR khi chưa tạo được mã.");
                            chkDaNhanTien.setEnabled(false);
                        }
                    } else {
                        lblNhanKhachDua.setVisible(false);
                        txtKhachDua.setVisible(false);
                        lblNhanTienThua.setVisible(false);
                        lblTienThua.setVisible(false);
                        lblQr.setIcon(null);
                        lblQr.setText("");
                        lblQr.setVisible(false);
                        chkDaNhanTien.setVisible(true);
                        chkDaNhanTien.setEnabled(true);
                        chkDaNhanTien.setText(
                                "Tôi đã kiểm tra giao dịch thành công trên máy POS");
                        txtHuongDan.setText(
                                "1. Thực hiện thanh toán thẻ trên máy POS của cửa hàng.\n"
                                        + "2. Kiểm tra máy POS báo giao dịch THÀNH CÔNG và đúng số tiền "
                                        + TienTe.dinhDang(canThu) + ".\n"
                                        + "3. Chỉ đánh dấu xác nhận sau khi đã kiểm tra kết quả trên POS.\n\n"
                                        + "Lưu ý: ứng dụng hiện ghi nhận xác nhận của nhân viên, "
                                        + "chưa kết nối trực tiếp với máy POS.");
                    }

                    pnlChinh.revalidate();
                    pnlChinh.repaint();
                    scrNoiDung.revalidate();
                    scrNoiDung.repaint();
                };

        cboPhuongThuc.addActionListener(e -> capNhatGiaoDien.run());
        txtKhachDua.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void insertUpdate(DocumentEvent e) { capNhatTienThua.run(); }
                    public void removeUpdate(DocumentEvent e) { capNhatTienThua.run(); }
                    public void changedUpdate(DocumentEvent e) { capNhatTienThua.run(); }
                });

        capNhatGiaoDien.run();

        while (true) {
            int luaChon =
                    JOptionPane.showOptionDialog(
                            cha,
                            scrNoiDung,
                            "Thu cọc lưu trú",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            cacNut,
                            cacNut[0]);
            // Nút 0 là xác nhận; nút Hủy hoặc đóng hộp thoại đều không ghi nhận khoản thu.
            if (luaChon != 0) return null;

            try {
                PhuongThucItem chon = (PhuongThucItem) cboPhuongThuc.getSelectedItem();
                PhuongThuc pt = chon != null ? chon.phuongThuc() : PhuongThuc.TIEN_MAT;

                if (pt == PhuongThuc.TIEN_MAT) {
                    long khachDua = docTien(txtKhachDua);
                    if (khachDua < canThu) {
                        throw new IllegalArgumentException(
                                "Tiền khách đưa phải lớn hơn hoặc bằng số tiền cần thu.");
                    }
                    return List.of(
                            new DongThuTien(PhuongThuc.TIEN_MAT, canThu, khachDua, true));
                }

                if (pt == PhuongThuc.CHUYEN_KHOAN && !qrHopLe[0]) {
                    throw new IllegalArgumentException(
                            "Chưa tạo được mã QR hợp lệ. " + loiTaoQr[0]);
                }
                if (!chkDaNhanTien.isSelected()) {
                    throw new IllegalArgumentException(
                            pt == PhuongThuc.CHUYEN_KHOAN
                                    ? "Vui lòng kiểm tra tiền đã vào tài khoản rồi mới xác nhận."
                                    : "Vui lòng kiểm tra máy POS báo giao dịch thành công rồi mới xác nhận.");
                }

                return List.of(new DongThuTien(pt, canThu, canThu, true));
            } catch (Exception ex) {
                HopThoai.hienThiCanhBao(pnlChinh, ex.getMessage());
            }
        }
    }

    /**
     * Sử dụng nguyên mã hợp đồng làm nội dung chuyển khoản VietQR.
     * Mã hợp đồng phải gồm chữ/số không dấu và tối đa 25 ký tự theo giới hạn trường QR.
     */
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

    private record PhuongThucItem(PhuongThuc phuongThuc, String ten) {
        @Override
        public String toString() {
            return ten;
        }
    }
}
