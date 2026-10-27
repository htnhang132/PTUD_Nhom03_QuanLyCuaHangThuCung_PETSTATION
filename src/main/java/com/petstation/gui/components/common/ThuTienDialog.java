package com.petstation.gui.components.common;

import com.petstation.gui.components.HopThoai;
import com.petstation.model.DongThuTien;
import com.petstation.model.PhuongThuc;
import com.petstation.service.CaiDatService;
import com.petstation.util.*;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

/** Cùng một form phân bổ tiền dùng cho hóa đơn và mỗi lần thu cọc. */
public final class ThuTienDialog {
    private ThuTienDialog() {}

    public static List<DongThuTien> nhap(Component cha, long canThu, String thamChieu) {
        JTextField cash = new JTextField(String.valueOf(canThu), 12),
                transfer = new JTextField("0", 12),
                card = new JTextField("0", 12),
                given = new JTextField(String.valueOf(canThu), 12);
        JCheckBox receivedTransfer = new JCheckBox("Đã kiểm tra và nhận chuyển khoản"),
                receivedCard = new JCheckBox("Đã kiểm tra và nhận tiền thẻ");
        JLabel qr = new JLabel(), note = new JLabel();
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Cần thu: " + TienTe.dinhDang(canThu)));
        form.add(new JLabel("Phân bổ đủ số tiền trước khi xác nhận"));
        form.add(new JLabel("Phần tiền mặt"));
        form.add(cash);
        form.add(new JLabel("Tiền mặt khách đưa"));
        form.add(given);
        form.add(new JLabel("Phần chuyển khoản / QR"));
        form.add(transfer);
        form.add(new JLabel("Phần thẻ"));
        form.add(card);
        form.add(receivedTransfer);
        form.add(receivedCard);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(form, BorderLayout.NORTH);
        panel.add(qr);
        panel.add(note, BorderLayout.SOUTH);
        final boolean[] validQr = {true};
        DocumentListener change =
                new DocumentListener() {
                    public void insertUpdate(DocumentEvent e) {
                        update();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        update();
                    }

                    public void changedUpdate(DocumentEvent e) {
                        update();
                    }

                    private void update() {
                        receivedTransfer.setSelected(false);
                        receivedCard.setSelected(false);
                        qr.setIcon(null);
                        qr.setText("");
                        validQr[0] = false;
                        try {
                            long value = doc(transfer);
                            if (value > 0) {
                                String bin = CaiDatService.layTuyChon("BANK_BIN"),
                                        tk = CaiDatService.layTuyChon("BANK_ACCOUNT"),
                                        ten = CaiDatService.layTuyChon("BANK_ACCOUNT_NAME");
                                TaoMaVietQR.kiemTraTaiKhoan(bin, tk, ten);
                                qr.setIcon(
                                        new ImageIcon(
                                                TaoMaVietQR.taoAnh(
                                                        TaoMaVietQR.taoNoiDung(
                                                                bin,
                                                                tk,
                                                                value,
                                                                thamChieu.replace("-", "")),
                                                        220)));
                                qr.setText(ten + " · " + tk);
                            }
                            validQr[0] = true;
                            long total = Math.addExact(Math.addExact(doc(cash), value), doc(card));
                            note.setText(
                                    "Đã phân bổ "
                                            + TienTe.dinhDang(total)
                                            + " / "
                                            + TienTe.dinhDang(canThu)
                                            + " · Thừa tiền mặt "
                                            + TienTe.dinhDang(Math.max(0, doc(given) - doc(cash))));
                        } catch (RuntimeException ex) {
                            note.setText(ex.getMessage());
                        }
                    }
                };
        for (var f : new JTextField[] {cash, transfer, card, given})
            f.getDocument().addDocumentListener(change);
        while (JOptionPane.showConfirmDialog(
                        cha,
                        panel,
                        "Phân bổ khoản thu",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE)
                == JOptionPane.OK_OPTION) {
            try {
                long c = doc(cash), t = doc(transfer), a = doc(card);
                List<DongThuTien> rows = new ArrayList<>();
                if (c > 0 || canThu == 0)
                    rows.add(new DongThuTien(PhuongThuc.CASH, c, doc(given), true));
                if (t > 0) {
                    if (!validQr[0])
                        throw new IllegalArgumentException(
                                "Cần cấu hình ngân hàng hợp lệ để tạo QR.");
                    rows.add(
                            new DongThuTien(
                                    PhuongThuc.TRANSFER, t, t, receivedTransfer.isSelected()));
                }
                if (a > 0)
                    rows.add(new DongThuTien(PhuongThuc.CARD, a, a, receivedCard.isSelected()));
                return DongThuTien.kiemTra(rows, canThu);
            } catch (RuntimeException ex) {
                HopThoai.hienThiCanhBao(panel, ex.getMessage());
            }
        }
        return null;
    }

    private static long doc(JTextField f) {
        String s = f.getText().trim();
        if (!s.matches("[0-9]+"))
            throw new IllegalArgumentException("Nhập số tiền nguyên không âm (đồng).");
        return Long.parseLong(s);
    }
}
