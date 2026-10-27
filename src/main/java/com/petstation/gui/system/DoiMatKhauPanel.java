package com.petstation.gui.system;

import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.KhaNangThanhCongCu;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.gui.components.common.KiemTraBieuMau;
import com.petstation.gui.components.common.TruongMatKhauAnHien;
import com.petstation.service.TaiKhoanService;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** Cung cấp giao diện và xử lý tương tác cho thay đổi mật khẩu vùng hiển thị. */
public final class DoiMatKhauPanel extends CoSoPanel implements HoTroThanhCongCu {
    /** Điều hướng. */
    private final java.util.function.Consumer<com.petstation.model.MaManHinh> dieuHuong;

    /** Trường hiện tại mật khẩu. */
    private TruongMatKhauAnHien pnlTruongHienTaiMatKhau;

    /** Trường mới mật khẩu. */
    private TruongMatKhauAnHien pnlTruongMoiMatKhau;

    /** Trường xác nhận mật khẩu. */
    private TruongMatKhauAnHien pnlTruongXacNhanMatKhau;

    /** Bộ kiểm tra. */
    private KiemTraBieuMau boKiemTra;

    public DoiMatKhauPanel(java.util.function.Consumer<com.petstation.model.MaManHinh> dieuHuong) {
        this.dieuHuong = dieuHuong;
        setLayout(new BorderLayout());

        // Khung nội dung
        // Nội dung.
        JPanel pnlNoiDung = layThe(new GridBagLayout());
        // Giá trị đang xét.
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.insets = new Insets(8, 10, 8, 10);
        giaTriDangXet.fill = GridBagConstraints.HORIZONTAL;
        giaTriDangXet.weightx = 1;

        // Tiêu đề và hướng dẫn
        // Tiêu đề.
        JLabel lblTieuDe = taoTieuDeMuc("Đổi mật khẩu");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        // Gợi ý.
        JLabel lblGoiY = new JLabel("Cập nhật mật khẩu đăng nhập của tài khoản hiện tại");
        lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);

        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridy = 0;
        giaTriDangXet.gridwidth = 2;
        pnlNoiDung.add(lblTieuDe, giaTriDangXet);
        giaTriDangXet.gridy++;
        pnlNoiDung.add(lblGoiY, giaTriDangXet);

        // Trường mật khẩu có nút bật tắt hiển thị
        pnlTruongHienTaiMatKhau = taoTruongMatKhau();
        pnlTruongMoiMatKhau = taoTruongMatKhau();
        pnlTruongXacNhanMatKhau = taoTruongMatKhau();

        themDong(pnlNoiDung, giaTriDangXet, 2, "Mật khẩu hiện tại", pnlTruongHienTaiMatKhau);
        themDong(pnlNoiDung, giaTriDangXet, 3, "Mật khẩu mới", pnlTruongMoiMatKhau);
        themDong(pnlNoiDung, giaTriDangXet, 4, "Xác nhận mật khẩu", pnlTruongXacNhanMatKhau);

        // Chỉ báo độ mạnh mật khẩu
        // Nhãn độ mạnh.
        JLabel lblNhanDoManh = new JLabel("");
        lblNhanDoManh.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        giaTriDangXet.gridy = 5;
        giaTriDangXet.gridx = 1;
        giaTriDangXet.gridwidth = 1;
        pnlNoiDung.add(lblNhanDoManh, giaTriDangXet);

        // Thêm bộ lắng nghe để hiển thị độ mạnh mật khẩu
        pnlTruongMoiMatKhau
                .layTruongMatKhau()
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            /**
                             * Lấy đã thay đổi cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            public void changedUpdate(javax.swing.event.DocumentEvent suKien) {
                                capNhatDoManh();
                            }

                            /**
                             * Xóa cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            public void removeUpdate(javax.swing.event.DocumentEvent suKien) {
                                capNhatDoManh();
                            }

                            /**
                             * Thêm cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            public void insertUpdate(javax.swing.event.DocumentEvent suKien) {
                                capNhatDoManh();
                            }

                            /** Cập nhật độ mạnh trong lớp xử lý giao diện. */
                            private void capNhatDoManh() {
                                // Mật khẩu.
                                String matKhau = pnlTruongMoiMatKhau.layVanBanMatKhau();
                                if (matKhau.isEmpty()) {
                                    lblNhanDoManh.setText("");
                                    return;
                                }

                                // Độ mạnh.
                                int doManh = tinhMatKhauDoManh(matKhau);
                                if (doManh < 3) {
                                    lblNhanDoManh.setText("Mật khẩu yếu");
                                    lblNhanDoManh.setForeground(MauUngDung.NGUY_HIEM);
                                } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                                if (doManh < 4) {
                                    lblNhanDoManh.setText("Mật khẩu trung bình");
                                    lblNhanDoManh.setForeground(MauUngDung.CANH_BAO);
                                } else {
                                    lblNhanDoManh.setText("Mật khẩu mạnh");
                                    lblNhanDoManh.setForeground(MauUngDung.THANH_CONG);
                                }
                            }
                        });

        // Các quy tắc kiểm tra
        // Quy tắc.
        JLabel lblQuyTac =
                new JLabel(
                        "Mật khẩu tối thiểu 8 ký tự; nên có chữ hoa, chữ thường, số và ký tự đặc"
                                + " biệt.");
        lblQuyTac.setForeground(MauUngDung.VAN_BAN_XAM);
        lblQuyTac.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        giaTriDangXet.gridy = 6;
        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridwidth = 2;
        pnlNoiDung.add(lblQuyTac, giaTriDangXet);

        // Các thao tác
        // Các thao tác.
        JPanel pnlCacThaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlCacThaoTac.setOpaque(false);
        // Hủy.
        NutDinhDang btnHuy = new NutDinhDang("Hủy", NutDinhDang.KieuNut.TRONG_SUOT);
        // Lưu.
        NutDinhDang btnLuu = new NutDinhDang("Lưu mật khẩu", NutDinhDang.KieuNut.CHINH);
        pnlCacThaoTac.add(btnHuy);
        pnlCacThaoTac.add(btnLuu);
        giaTriDangXet.gridy = 7;
        pnlNoiDung.add(pnlCacThaoTac, giaTriDangXet);

        // Xử lý sự kiện
        btnHuy.addActionListener(
                suKien -> {
                    pnlTruongHienTaiMatKhau.xoa();
                    pnlTruongMoiMatKhau.xoa();
                    pnlTruongXacNhanMatKhau.xoa();
                    lblNhanDoManh.setText("");
                });

        btnLuu.addActionListener(suKien -> luuMatKhauMoi());

        // Phím tắt
        dangKyBanPhimPhimTat("F4", suKien -> luuMatKhauMoi());
        dangKyBanPhimPhimTat("ESCAPE", suKien -> btnHuy.doClick());

        pnlNoiDung.setPreferredSize(new Dimension(520, 425));
        JPanel pnlCanGiua = new JPanel(new GridBagLayout());
        pnlCanGiua.setOpaque(false);
        GridBagConstraints viTri = new GridBagConstraints();
        viTri.anchor = GridBagConstraints.NORTH;
        viTri.weightx = 1;
        viTri.weighty = 1;
        pnlCanGiua.add(pnlNoiDung, viTri);
        javax.swing.JScrollPane scrNoiDung = new javax.swing.JScrollPane(pnlCanGiua);
        scrNoiDung.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        scrNoiDung.getViewport().setBackground(MauUngDung.NEN_KEM);
        add(scrNoiDung, BorderLayout.CENTER);
    }

    private TruongMatKhauAnHien taoTruongMatKhau() {
        // Trường.
        TruongMatKhauAnHien pnlTruong = new TruongMatKhauAnHien(20);
        pnlTruong.setPreferredSize(new Dimension(300, 36));
        return pnlTruong;
    }

    /** Kiểm tra mật khẩu hiện tại, xác nhận mật khẩu mới và lưu thay đổi cho phiên đăng nhập. */
    private void luuMatKhauMoi() {
        // Hiện tại.
        String hienTai = pnlTruongHienTaiMatKhau.layVanBanMatKhau();
        // Mới mật khẩu.
        String moiMatKhau = pnlTruongMoiMatKhau.layVanBanMatKhau();
        // Xác nhận.
        String xacNhan = pnlTruongXacNhanMatKhau.layVanBanMatKhau();

        // Kiểm tra dữ liệu trống
        if (hienTai.isEmpty()) {
            hienThiThongBao(
                    "Vui lòng nhập mật khẩu hiện tại.",
                    "Dữ liệu chưa đầy đủ",
                    JOptionPane.WARNING_MESSAGE);
            pnlTruongHienTaiMatKhau.requestFocus();
            return;
        }

        if (moiMatKhau.isEmpty()) {
            hienThiThongBao(
                    "Vui lòng nhập mật khẩu mới.",
                    "Dữ liệu chưa đầy đủ",
                    JOptionPane.WARNING_MESSAGE);
            pnlTruongMoiMatKhau.requestFocus();
            return;
        }

        // Kiểm tra độ dài
        if (moiMatKhau.length() < 8) {
            hienThiThongBao(
                    "Mật khẩu mới phải có ít nhất 8 ký tự.",
                    "Dữ liệu chưa hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
            pnlTruongMoiMatKhau.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu xác nhận
        if (!moiMatKhau.equals(xacNhan)) {
            hienThiThongBao(
                    "Xác nhận mật khẩu không khớp.",
                    "Dữ liệu chưa hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
            pnlTruongXacNhanMatKhau.xoa();
            pnlTruongXacNhanMatKhau.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu mới khác mật khẩu hiện tại
        if (moiMatKhau.equals(hienTai)) {
            hienThiThongBao(
                    "Mật khẩu mới phải khác mật khẩu hiện tại.",
                    "Dữ liệu chưa hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
            pnlTruongMoiMatKhau.requestFocus();
            return;
        }

        try {
            TaiKhoanService.layThayDoiMatKhau(hienTai, moiMatKhau);
        } catch (RuntimeException ngoaiLe) {
            hienThiThongBao(ngoaiLe.getMessage(), "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        hienThiThongBao("Đã cập nhật mật khẩu.", "Thành công", JOptionPane.INFORMATION_MESSAGE);

        // Xóa nội dung biểu mẫu sau khi thành công
        pnlTruongHienTaiMatKhau.xoa();
        pnlTruongMoiMatKhau.xoa();
        pnlTruongXacNhanMatKhau.xoa();
    }

    private int tinhMatKhauDoManh(String matKhau) {
        // Độ mạnh.
        int doManh = 0;

        if (matKhau.length() >= 8) doManh++;
        if (matKhau.length() >= 12) doManh++;
        if (matKhau.matches(".*[a-z].*")) doManh++; // Có chữ thường
        if (matKhau.matches(".*[A-Z].*")) doManh++; // Có chữ hoa
        if (matKhau.matches(".*\\d.*")) doManh++; // Có chữ số
        if (matKhau.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"))
            doManh++; // Có ký tự đặc biệt

        return Math.min(doManh, 5);
    }

    private void dieuHuongQuayLai() {
        dieuHuong.accept(com.petstation.model.MaManHinh.DASHBOARD);
    }

    private void dieuHuongDenTrangChinh() {
        dieuHuong.accept(com.petstation.model.MaManHinh.DASHBOARD);
    }

    private void dangKyBanPhimPhimTat(String khoa, java.awt.event.ActionListener thaoTac) {
        // Khóa nét vẽ.
        javax.swing.KeyStroke khoaNetVe = javax.swing.KeyStroke.getKeyStroke(khoa);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(khoaNetVe, khoa);
        getActionMap()
                .put(
                        khoa,
                        new javax.swing.AbstractAction() {
                            /**
                             * Lấy thao tác đã thực hiện trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent suKien) {
                                thaoTac.actionPerformed(suKien);
                            }
                        });
    }

    private void themDong(
            JPanel pnlVungDangXet,
            GridBagConstraints giaTriDangXet,
            int toaDoDoc,
            String nhan,
            JComponent truong) {
        giaTriDangXet.gridwidth = 1;
        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridy = toaDoDoc;
        // Nhãn.
        JLabel lblNhan = new JLabel(nhan);
        lblNhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        pnlVungDangXet.add(lblNhan, giaTriDangXet);
        giaTriDangXet.gridx = 1;
        pnlVungDangXet.add(truong, giaTriDangXet);
    }

    private void hienThiThongBao(String vanBan, String tieuDe, int kieu) {
        HopThoai.hienThiThongBaoHopThoai(this, vanBan, tieuDe, kieu);
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(false, true, false, false, false, true);
    }

    @Override
    public void xuLyTroGiup() {
        hienThiThongBao(
                "Hướng dẫn đổi mật khẩu:\n\n"
                        + "1. Nhập mật khẩu hiện tại\n"
                        + "2. Nhập mật khẩu mới (tối thiểu 8 ký tự)\n"
                        + "3. Xác nhận lại mật khẩu mới\n"
                        + "4. Nhấn 'Lưu mật khẩu' hoặc phím F4\n\n"
                        + "Mẹo: Nhấn nút Hiện/Ẩn để xem hoặc che mật khẩu",
                "Trợ giúp",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
