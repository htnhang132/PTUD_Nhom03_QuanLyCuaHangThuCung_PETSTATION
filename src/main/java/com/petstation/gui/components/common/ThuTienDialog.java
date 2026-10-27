package com.petstation.gui.components.common;

import com.petstation.gui.components.HopThoai;
import com.petstation.model.DongThuTien;
import com.petstation.model.PhuongThuc;
import com.petstation.service.CaiDatService;
import com.petstation.util.TaoMaVietQR;
import com.petstation.util.TienTe;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Hộp thoại thu tiền đơn giản theo từng phương thức thanh toán. */
public final class ThuTienDialog {
    private ThuTienDialog() {}

    public static List<DongThuTien> nhap(Component cha, long canThu, String thamChieu) {
        JComboBox<PhuongThucItem> cboPhuongThuc =
                new JComboBox<>(
                        new PhuongThucItem[] {
                            new PhuongThucItem(PhuongThuc.CASH, "Tiền mặt"),
                            new PhuongThucItem(PhuongThuc.TRANSFER, "Chuyển khoản QR"),
                            new PhuongThucItem(PhuongThuc.CARD, "Thẻ ngân hàng")
                        });

        JTextField txtKhachDua = new JTextField(String.valueOf(canThu), 12);
        JLabel lblTienThua = new JLabel("0 đ");
        JCheckBox chkDaNhanTien = new JCheckBox("Đã xác nhận nhận đủ tiền");
        JLabel lblQr = new JLabel();
        lblQr.setHorizontalAlignment(SwingConstants.CENTER);

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

        JPanel pnlChinh = new JPanel(new BorderLayout(8, 8));
        pnlChinh.add(pnlNhap, BorderLayout.NORTH);
        pnlChinh.add(lblQr, BorderLayout.CENTER);
        pnlChinh.add(chkDaNhanTien, BorderLayout.SOUTH);

        Runnable capNhatGiaoDien =
                () -> {
                    PhuongThucItem chon = (PhuongThucItem) cboPhuongThuc.getSelectedItem();
                    PhuongThuc pt = chon != null ? chon.phuongThuc() : PhuongThuc.CASH;
                    if (pt == PhuongThuc.CASH) {
                        lblNhanKhachDua.setVisible(true);
                        txtKhachDua.setVisible(true);
                        lblNhanTienThua.setVisible(true);
                        lblTienThua.setVisible(true);
                        lblQr.setVisible(false);
                        chkDaNhanTien.setVisible(false);
                        try {
                            long khachDua = docTien(txtKhachDua);
                            long thua = Math.max(0, khachDua - canThu);
                            lblTienThua.setText(TienTe.dinhDang(thua));
                        } catch (Exception ex) {
                            lblTienThua.setText("Số tiền chưa đúng");
                        }
                    } else if (pt == PhuongThuc.TRANSFER) {
                        lblNhanKhachDua.setVisible(false);
                        txtKhachDua.setVisible(false);
                        lblNhanTienThua.setVisible(false);
                        lblTienThua.setVisible(false);
                        lblQr.setVisible(true);
                        chkDaNhanTien.setVisible(true);
                        chkDaNhanTien.setText("Đã kiểm tra và nhận tiền chuyển khoản");
                        try {
                            String bin = CaiDatService.layTuyChon("BANK_BIN");
                            String tk = CaiDatService.layTuyChon("BANK_ACCOUNT");
                            String ten = CaiDatService.layTuyChon("BANK_ACCOUNT_NAME");
                            TaoMaVietQR.kiemTraTaiKhoan(bin, tk, ten);
                            String nd =
                                    TaoMaVietQR.taoNoiDung(
                                            bin, tk, canThu, thamChieu.replace("-", ""));
                            lblQr.setIcon(new ImageIcon(TaoMaVietQR.taoAnh(nd, 200)));
                            lblQr.setText(
                                    "<html><center>" + ten + "<br>" + tk + "</center></html>");
                        } catch (Exception ex) {
                            lblQr.setIcon(null);
                            lblQr.setText("Chưa cấu hình thông tin ngân hàng.");
                        }
                    } else {
                        lblNhanKhachDua.setVisible(false);
                        txtKhachDua.setVisible(false);
                        lblNhanTienThua.setVisible(false);
                        lblTienThua.setVisible(false);
                        lblQr.setVisible(false);
                        chkDaNhanTien.setVisible(true);
                        chkDaNhanTien.setText("Đã quẹt thẻ thành công qua máy POS");
                    }
                };

        cboPhuongThuc.addActionListener(e -> capNhatGiaoDien.run());
        txtKhachDua
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            public void insertUpdate(DocumentEvent e) {
                                capNhatGiaoDien.run();
                            }

                            public void removeUpdate(DocumentEvent e) {
                                capNhatGiaoDien.run();
                            }

                            public void changedUpdate(DocumentEvent e) {
                                capNhatGiaoDien.run();
                            }
                        });

        capNhatGiaoDien.run();

        while (JOptionPane.showConfirmDialog(
                        cha,
                        pnlChinh,
                        "Thu tiền",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE)
                == JOptionPane.OK_OPTION) {
            try {
                PhuongThucItem chon = (PhuongThucItem) cboPhuongThuc.getSelectedItem();
                PhuongThuc pt = chon != null ? chon.phuongThuc() : PhuongThuc.CASH;
                if (pt == PhuongThuc.CASH) {
                    long khachDua = docTien(txtKhachDua);
                    if (khachDua < canThu) {
                        throw new IllegalArgumentException(
                                "Tiền khách đưa phải lớn hơn hoặc bằng số tiền cần thu.");
                    }
                    return List.of(new DongThuTien(PhuongThuc.CASH, canThu, khachDua, true));
                } else {
                    if (!chkDaNhanTien.isSelected()) {
                        throw new IllegalArgumentException(
                                "Vui lòng xác nhận đã nhận đủ tiền trước khi hoàn tất.");
                    }
                    return List.of(new DongThuTien(pt, canThu, canThu, true));
                }
            } catch (Exception ex) {
                HopThoai.hienThiCanhBao(pnlChinh, ex.getMessage());
            }
        }
        return null;
    }

    private static long docTien(JTextField f) {
        String s = f.getText().trim();
        if (!s.matches("[0-9]+")) {
            throw new IllegalArgumentException("Vui lòng nhập số tiền hợp lệ.");
        }
        return Long.parseLong(s);
    }

    private record PhuongThucItem(PhuongThuc phuongThuc, String ten) {
        @Override
        public String toString() {
            return ten;
        }
    }
}
