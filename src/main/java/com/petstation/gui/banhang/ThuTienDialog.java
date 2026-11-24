package com.petstation.gui.banhang;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.util.DinhDang;
import com.petstation.util.TaoMaVietQR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Thu một khoản tiền hoặc một phần khoản còn thiếu; không tự ghi giao dịch. */
public final class ThuTienDialog extends JDialog {
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

    private final long toiDa;
    private final String thamChieu;
    private final JTextField soTien = new JTextField(14);
    private final JTextField khachDua = new JTextField(14);
    private final JComboBox<PhuongThuc> phuongThuc =
            new JComboBox<>(
                    new PhuongThuc[] {
                        PhuongThuc.TIEN_MAT, PhuongThuc.CHUYEN_KHOAN, PhuongThuc.THE_NGAN_HANG
                    });
    private final JLabel tienThua = new JLabel();
    private final JLabel qr = new JLabel("", SwingConstants.CENTER);
    private final JLabel thongBao = new JLabel();
    private final JCheckBox xacNhan = new JCheckBox();
    private final Timer henTaiQr = new Timer(250, e -> taiQr());
    private int luotTai;
    private boolean qrHopLe;
    private KetQuaThu ketQua;

    private ThuTienDialog(Component cha, long toiDa, String thamChieu) {
        super(
                SwingUtilities.getWindowAncestor(cha),
                "Thu tiền cọc Hotel",
                Dialog.ModalityType.APPLICATION_MODAL);
        if (toiDa <= 0) throw new IllegalArgumentException("Số tiền cần thu phải lớn hơn 0.");
        this.toiDa = toiDa;
        this.thamChieu = thamChieu;
        soTien.setText(String.valueOf(toiDa));
        khachDua.setText(String.valueOf(toiDa));
        henTaiQr.setRepeats(false);
        taoNoiDung();
        ganSuKien();
        capNhat();
        HopThoai.canChinhKichThuocVuaManHinh(this, 500, 440);
        HopThoai.chuanHoaCuaSo(this, cha);
    }

    public static KetQuaThu nhap(Component cha, long toiDa, String thamChieu) {
        ThuTienDialog hopThoai = new ThuTienDialog(cha, toiDa, thamChieu);
        hopThoai.setVisible(true);
        return hopThoai.ketQua;
    }

    private void taoNoiDung() {
        JPanel nhap = new JPanel(new GridLayout(0, 2, 8, 8));
        nhap.add(new JLabel("Còn thiếu tối đa:"));
        nhap.add(new JLabel(DinhDang.dinhDang(toiDa)));
        nhap.add(new JLabel("Số tiền thu lần này:"));
        nhap.add(soTien);
        nhap.add(new JLabel("Phương thức:"));
        nhap.add(phuongThuc);
        nhap.add(new JLabel("Tiền khách đưa:"));
        nhap.add(khachDua);
        nhap.add(new JLabel("Tiền trả lại:"));
        nhap.add(tienThua);
        JPanel giua = new JPanel(new BorderLayout(0, 8));
        giua.add(qr, BorderLayout.CENTER);
        giua.add(xacNhan, BorderLayout.SOUTH);
        JPanel noiDung = new JPanel(new BorderLayout(0, 12));
        noiDung.add(nhap, BorderLayout.NORTH);
        noiDung.add(giua, BorderLayout.CENTER);
        noiDung.add(thongBao, BorderLayout.SOUTH);
        JButton huy = new NutDinhDang("Hủy", NutDinhDang.KieuNut.PHU);
        huy.addActionListener(e -> dispose());
        JButton thu = new NutDinhDang("Xác nhận thu cọc", NutDinhDang.KieuNut.CHINH);
        thu.addActionListener(e -> xacNhanThu());
        JPanel chan = new JPanel();
        chan.add(huy);
        chan.add(thu);
        JPanel goc = new JPanel(new BorderLayout(0, 12));
        goc.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        goc.add(new JScrollPane(noiDung), BorderLayout.CENTER);
        goc.add(chan, BorderLayout.SOUTH);
        setContentPane(goc);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(thu);
        getRootPane()
                .registerKeyboardAction(
                        e -> dispose(),
                        KeyStroke.getKeyStroke("ESCAPE"),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void ganSuKien() {
        CoSoPanel.themBoLangNgheTaiLieu(soTien, this::capNhat);
        CoSoPanel.themBoLangNgheTaiLieu(khachDua, this::capNhatTienThua);
        phuongThuc.addActionListener(e -> capNhat());
    }

    private void capNhat() {
        luotTai++;
        henTaiQr.stop();
        qrHopLe = false;
        xacNhan.setSelected(false);
        qr.setIcon(null);
        qr.setText("");
        PhuongThuc pt = (PhuongThuc) phuongThuc.getSelectedItem();
        khachDua.setEnabled(pt == PhuongThuc.TIEN_MAT);
        xacNhan.setVisible(pt != PhuongThuc.TIEN_MAT);
        xacNhan.setEnabled(pt == PhuongThuc.THE_NGAN_HANG);
        xacNhan.setText(
                pt == PhuongThuc.THE_NGAN_HANG
                        ? "Đã xác nhận giao dịch thẻ thành công"
                        : "Đã kiểm tra tiền vào đúng tài khoản ngân hàng");
        try {
            long thu = docSoTienThu();
            thongBao.setText("Thu lần này: " + DinhDang.dinhDang(thu));
            if (pt == PhuongThuc.CHUYEN_KHOAN) {
                qr.setText("Đang tạo mã VietQR...");
                henTaiQr.restart();
            }
        } catch (IllegalArgumentException ex) {
            thongBao.setText(ex.getMessage());
        }
        capNhatTienThua();
    }

    private void capNhatTienThua() {
        try {
            long thua =
                    phuongThuc.getSelectedItem() == PhuongThuc.TIEN_MAT
                            ? Math.max(0, docTien(khachDua) - docSoTienThu())
                            : 0;
            tienThua.setText(DinhDang.dinhDang(thua));
        } catch (IllegalArgumentException ex) {
            tienThua.setText("—");
        }
    }

    private void taiQr() {
        final long thu;
        try {
            thu = docSoTienThu();
        } catch (IllegalArgumentException ex) {
            return;
        }
        int luot = luotTai;
        TacVuNen.chayCoPhien(
                this,
                () ->
                        isDisplayable()
                                && luot == luotTai
                                && phuongThuc.getSelectedItem() == PhuongThuc.CHUYEN_KHOAN,
                () -> {
                    String bin = CaiDatService.macDinh().layTuyChon("BANK_BIN");
                    String tk = CaiDatService.macDinh().layTuyChon("BANK_ACCOUNT");
                    String ten = CaiDatService.macDinh().layTuyChon("BANK_ACCOUNT_NAME");
                    TaoMaVietQR.kiemTraTaiKhoan(bin, tk, ten);
                    return new ImageIcon(
                            TaoMaVietQR.taoAnh(
                                    TaoMaVietQR.taoNoiDung(bin, tk, ten, thu, thamChieu), 220));
                },
                anh -> {
                    qr.setText("");
                    qr.setIcon(anh);
                    qrHopLe = true;
                    xacNhan.setEnabled(true);
                },
                loi -> {
                    qr.setText("Không tạo được QR");
                    thongBao.setText(TacVuNen.thongDiepLoi(loi));
                });
    }

    private long docSoTienThu() {
        long thu = docTien(soTien);
        if (thu <= 0 || thu > toiDa)
            throw new IllegalArgumentException(
                    "Số tiền thu phải dương và không vượt phần còn thiếu.");
        return thu;
    }

    private static long docTien(JTextField oNhap) {
        String chuoi = oNhap.getText().trim().replaceAll("[.,\\s₫đ]", "");
        if (!chuoi.matches("[0-9]+"))
            throw new IllegalArgumentException("Số tiền phải là số nguyên.");
        try {
            return Long.parseLong(chuoi);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số tiền vượt giới hạn.");
        }
    }

    static KetQuaThu kiemTraKhoanThu(
            PhuongThuc pt,
            long toiDa,
            long thu,
            long khachDua,
            boolean daXacNhan,
            boolean qrHopLe) {
        if (pt == null || thu <= 0 || thu > toiDa)
            throw new IllegalArgumentException("Khoản thu không hợp lệ.");
        if (pt == PhuongThuc.TIEN_MAT && khachDua < thu)
            throw new IllegalArgumentException("Tiền khách đưa chưa đủ.");
        if (pt != PhuongThuc.TIEN_MAT && !daXacNhan)
            throw new IllegalArgumentException("Cần xác nhận đã nhận tiền.");
        if (pt == PhuongThuc.CHUYEN_KHOAN && !qrHopLe)
            throw new IllegalArgumentException("Mã QR chưa hợp lệ.");
        return new KetQuaThu(pt, thu, pt == PhuongThuc.TIEN_MAT ? khachDua : thu);
    }

    private void xacNhanThu() {
        try {
            PhuongThuc pt = (PhuongThuc) phuongThuc.getSelectedItem();
            long thu = docSoTienThu();
            ketQua =
                    kiemTraKhoanThu(
                            pt,
                            toiDa,
                            thu,
                            pt == PhuongThuc.TIEN_MAT ? docTien(khachDua) : thu,
                            xacNhan.isSelected(),
                            qrHopLe);
            dispose();
        } catch (IllegalArgumentException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
        }
    }

    @Override
    public void dispose() {
        luotTai++;
        henTaiQr.stop();
        super.dispose();
    }
}
