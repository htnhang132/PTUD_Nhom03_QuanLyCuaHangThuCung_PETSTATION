package com.petstation.gui.hethong;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.TruongMatKhauAnHien;
import com.petstation.service.hethong.TaiKhoanService;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

// Màn hình: Đổi mật khẩu người dùng (Mã: DOI_MAT_KHAU)
// Luồng xử lý: GUI -> XacThucService -> TaiKhoanDAOImpl -> SQL (tai_khoan)
// Mô tả: Cho phép người dùng chủ động thay đổi mật khẩu đăng nhập hệ thống với cơ chế mã hóa an
// toàn
// Phân hệ: hethong
public final class DoiMatKhauPanel extends CoSoPanel {
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    private TruongMatKhauAnHien khungTruongHienTaiMatKhau;

    private TruongMatKhauAnHien khungTruongMoiMatKhau;

    private TruongMatKhauAnHien khungTruongXacNhanMatKhau;

    public DoiMatKhauPanel() {
        setLayout(new BorderLayout());
        JPanel khungNoiDung = taoKhungBieuMauDoiMatKhau();
        JPanel khungCanGiua = new JPanel(new GridBagLayout());
        khungCanGiua.setOpaque(false);
        GridBagConstraints viTri = new GridBagConstraints();
        viTri.anchor = GridBagConstraints.NORTH;
        viTri.weightx = 1;
        viTri.weighty = 1;
        khungCanGiua.add(khungNoiDung, viTri);
        JScrollPane scrNoiDung = new JScrollPane(khungCanGiua);
        scrNoiDung.setBorder(BorderFactory.createEmptyBorder());
        scrNoiDung.getViewport().setBackground(MauUngDung.NEN_KEM);
        add(scrNoiDung, BorderLayout.CENTER);
    }

    private JPanel taoKhungBieuMauDoiMatKhau() {
        JPanel khungNoiDung = layThe(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        themPhanDauDoiMatKhau(khungNoiDung, gc);
        khungTruongHienTaiMatKhau = taoTruongMatKhau();
        khungTruongMoiMatKhau = taoTruongMatKhau();
        khungTruongXacNhanMatKhau = taoTruongMatKhau();
        themDong(khungNoiDung, gc, 2, "Mật khẩu hiện tại", khungTruongHienTaiMatKhau);
        themDong(khungNoiDung, gc, 3, "Mật khẩu mới", khungTruongMoiMatKhau);
        themDong(khungNoiDung, gc, 4, "Xác nhận mật khẩu", khungTruongXacNhanMatKhau);
        JLabel nhanNhanDoManh = taoNhanDoManhMatKhau(khungNoiDung, gc);
        themGhiChuQuyTacMatKhau(khungNoiDung, gc);
        themThanhThaoTacDoiMatKhau(khungNoiDung, gc, nhanNhanDoManh);
        khungNoiDung.setPreferredSize(new Dimension(520, 425));
        return khungNoiDung;
    }

    private void themPhanDauDoiMatKhau(JPanel khungNoiDung, GridBagConstraints gc) {
        JLabel nhanTieuDe = taoTieuDeMuc("Đổi mật khẩu");
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        JLabel nhanGoiY = new JLabel("Cập nhật mật khẩu đăng nhập của tài khoản hiện tại");
        nhanGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        khungNoiDung.add(nhanTieuDe, gc);
        gc.gridy++;
        khungNoiDung.add(nhanGoiY, gc);
    }

    private JLabel taoNhanDoManhMatKhau(JPanel khungNoiDung, GridBagConstraints gc) {
        JLabel nhanNhanDoManh = new JLabel("");
        nhanNhanDoManh.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        gc.gridy = 5;
        gc.gridx = 1;
        gc.gridwidth = 1;
        khungNoiDung.add(nhanNhanDoManh, gc);
        themBoLangNgheTaiLieu(
                khungTruongMoiMatKhau.layTruongMatKhau(), () -> capNhatNhanDoManh(nhanNhanDoManh));
        return nhanNhanDoManh;
    }

    private void capNhatNhanDoManh(JLabel nhanNhanDoManh) {
        String matKhau = khungTruongMoiMatKhau.layVanBanMatKhau();
        if (matKhau.isEmpty()) {
            nhanNhanDoManh.setText("");
            return;
        }
        int doManh = tinhMatKhauDoManh(matKhau);
        if (doManh < 3) {
            nhanNhanDoManh.setText("Mật khẩu yếu");
            nhanNhanDoManh.setForeground(MauUngDung.NGUY_HIEM);
        } else if (doManh < 4) {
            nhanNhanDoManh.setText("Mật khẩu trung bình");
            nhanNhanDoManh.setForeground(MauUngDung.CANH_BAO);
        } else {
            nhanNhanDoManh.setText("Mật khẩu mạnh");
            nhanNhanDoManh.setForeground(MauUngDung.THANH_CONG);
        }
    }

    private static void themGhiChuQuyTacMatKhau(JPanel khungNoiDung, GridBagConstraints gc) {
        JLabel nhanQuyTac =
                new JLabel(
                        "Mật khẩu tối thiểu 8 ký tự; nên có chữ hoa, chữ thường, số và ký tự đặc"
                                + " biệt.");
        nhanQuyTac.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanQuyTac.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        gc.gridy = 6;
        gc.gridx = 0;
        gc.gridwidth = 2;
        khungNoiDung.add(nhanQuyTac, gc);
    }

    private void themThanhThaoTacDoiMatKhau(
            JPanel khungNoiDung, GridBagConstraints gc, JLabel nhanNhanDoManh) {
        JPanel khungCacThaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        khungCacThaoTac.setOpaque(false);
        NutDinhDang btnHuy = new NutDinhDang("Hủy", NutDinhDang.KieuNut.TRONG_SUOT);
        NutDinhDang btnLuu = new NutDinhDang("Lưu mật khẩu", NutDinhDang.KieuNut.CHINH);
        khungCacThaoTac.add(btnHuy);
        khungCacThaoTac.add(btnLuu);
        gc.gridy = 7;
        khungNoiDung.add(khungCacThaoTac, gc);
        btnHuy.addActionListener(
                e -> {
                    khungTruongHienTaiMatKhau.xoa();
                    khungTruongMoiMatKhau.xoa();
                    khungTruongXacNhanMatKhau.xoa();
                    nhanNhanDoManh.setText("");
                });
        btnLuu.addActionListener(e -> luuMatKhauMoi());
        dangKyBanPhimPhimTat("control S", e -> luuMatKhauMoi());
        dangKyBanPhimPhimTat("ENTER", e -> luuMatKhauMoi());
        dangKyBanPhimPhimTat("ESCAPE", e -> btnHuy.doClick());
    }

    private TruongMatKhauAnHien taoTruongMatKhau() {
        TruongMatKhauAnHien khungTruong = new TruongMatKhauAnHien(20);
        khungTruong.setPreferredSize(new Dimension(300, 36));
        return khungTruong;
    }

    private void luuMatKhauMoi() {
        String hienTai = khungTruongHienTaiMatKhau.layVanBanMatKhau();
        String moiMatKhau = khungTruongMoiMatKhau.layVanBanMatKhau();
        String xacNhan = khungTruongXacNhanMatKhau.layVanBanMatKhau();
        if (hienTai.isEmpty()) {
            HopThoai.hienThiCanhBao(
                    this, "Vui lòng nhập mật khẩu hiện tại.", "Dữ liệu chưa đầy đủ");
            khungTruongHienTaiMatKhau.requestFocus();
            return;
        }
        if (moiMatKhau.isEmpty()) {
            HopThoai.hienThiCanhBao(this, "Vui lòng nhập mật khẩu mới.", "Dữ liệu chưa đầy đủ");
            khungTruongMoiMatKhau.requestFocus();
            return;
        }
        if (moiMatKhau.length() < 8) {
            HopThoai.hienThiCanhBao(
                    this, "Mật khẩu mới phải có ít nhất 8 ký tự.", "Dữ liệu chưa hợp lệ");
            khungTruongMoiMatKhau.requestFocus();
            return;
        }
        if (!moiMatKhau.equals(xacNhan)) {
            HopThoai.hienThiCanhBao(this, "Xác nhận mật khẩu không khớp.", "Dữ liệu chưa hợp lệ");
            khungTruongXacNhanMatKhau.xoa();
            khungTruongXacNhanMatKhau.requestFocus();
            return;
        }
        if (moiMatKhau.equals(hienTai)) {
            HopThoai.hienThiCanhBao(
                    this, "Mật khẩu mới phải khác mật khẩu hiện tại.", "Dữ liệu chưa hợp lệ");
            khungTruongMoiMatKhau.requestFocus();
            return;
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> taiKhoanService.layThayDoiMatKhau(hienTai, moiMatKhau),
                () -> {
                    HopThoai.hienThiThongTin(this, "Đã cập nhật mật khẩu.", "Thành công");
                    khungTruongHienTaiMatKhau.xoa();
                    khungTruongMoiMatKhau.xoa();
                    khungTruongXacNhanMatKhau.xoa();
                },
                ngoaiLe -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ngoaiLe), "Lỗi"));
    }

    private int tinhMatKhauDoManh(String matKhau) {
        int doManh = 0;
        if (matKhau.length() >= 8) doManh++;
        if (matKhau.length() >= 12) doManh++;
        if (matKhau.matches(".*[a-z].*")) doManh++;
        if (matKhau.matches(".*[A-Z].*")) doManh++;
        if (matKhau.matches(".*\\d.*")) doManh++;
        if (matKhau.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) doManh++;
        return Math.min(doManh, 5);
    }

    private void dangKyBanPhimPhimTat(String khoa, ActionListener thaoTac) {
        KeyStroke khoaNetVe = KeyStroke.getKeyStroke(khoa);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(khoaNetVe, khoa);
        getActionMap()
                .put(
                        khoa,
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent suKien) {
                                thaoTac.actionPerformed(suKien);
                            }
                        });
    }

    private void themDong(
            JPanel khungVungDangXet,
            GridBagConstraints giaTriDangXet,
            int toaDoDoc,
            String nhan,
            JComponent truong) {
        giaTriDangXet.gridwidth = 1;
        giaTriDangXet.gridx = 0;
        giaTriDangXet.gridy = toaDoDoc;
        JLabel nhanHienThi = new JLabel(nhan);
        nhanHienThi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        khungVungDangXet.add(nhanHienThi, giaTriDangXet);
        giaTriDangXet.gridx = 1;
        khungVungDangXet.add(truong, giaTriDangXet);
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(false, true, false, false, false, true);
    }

    @Override
    public void xuLyLuu() {
        luuMatKhauMoi();
    }

    @Override
    public void xuLyTroGiup() {
        HopThoai.hienThiThongTin(
                this,
                "Hướng dẫn đổi mật khẩu:\n\n"
                        + "1. Nhập mật khẩu hiện tại\n"
                        + "2. Nhập mật khẩu mới (tối thiểu 8 ký tự)\n"
                        + "3. Xác nhận lại mật khẩu mới\n"
                        + "4. Nhấn 'Lưu mật khẩu' hoặc phím Ctrl+S / Enter\n\n"
                        + "Mẹo: Nhấn nút Hiện/Ẩn để xem hoặc che mật khẩu",
                "Trợ giúp");
    }
}
