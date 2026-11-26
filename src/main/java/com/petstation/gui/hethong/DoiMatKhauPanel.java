package com.petstation.gui.hethong;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.TruongMatKhauAnHien;
import com.petstation.service.hethong.PhienDangNhapService;
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

    private JLabel nhanNhanDoManh;

    public DoiMatKhauPanel() {
        setLayout(new BorderLayout(0, 12));
        add(
                taoTieuDeManHinh(
                        "Đổi mật khẩu", "Cập nhật mật khẩu đăng nhập của tài khoản hiện tại"),
                BorderLayout.NORTH);
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
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        khungNoiDung.add(taoTieuDeMuc("Thông tin mật khẩu"), gc);
        gc.gridwidth = 1;
        khungTruongHienTaiMatKhau = taoTruongMatKhau();
        khungTruongMoiMatKhau = taoTruongMatKhau();
        khungTruongXacNhanMatKhau = taoTruongMatKhau();
        themDong(khungNoiDung, gc, 1, "Mật khẩu hiện tại *", khungTruongHienTaiMatKhau);
        themDong(khungNoiDung, gc, 2, "Mật khẩu mới *", khungTruongMoiMatKhau);
        themDong(khungNoiDung, gc, 3, "Nhập lại mật khẩu mới *", khungTruongXacNhanMatKhau);
        nhanNhanDoManh = taoNhanDoManhMatKhau(khungNoiDung, gc);
        themGhiChuQuyTacMatKhau(khungNoiDung, gc);
        themThanhThaoTacDoiMatKhau(khungNoiDung, gc);
        khungNoiDung.setMinimumSize(new Dimension(480, 0));
        return khungNoiDung;
    }

    private JLabel taoNhanDoManhMatKhau(JPanel khungNoiDung, GridBagConstraints gc) {
        JLabel nhanDoManh = new JLabel("");
        nhanDoManh.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        gc.gridy = 4;
        gc.gridx = 1;
        gc.gridwidth = 1;
        khungNoiDung.add(nhanDoManh, gc);
        themBoLangNgheTaiLieu(
                khungTruongMoiMatKhau.layTruongMatKhau(), () -> capNhatNhanDoManh(nhanDoManh));
        return nhanDoManh;
    }

    private void capNhatNhanDoManh(JLabel nhanNhanDoManh) {
        String matKhau = khungTruongMoiMatKhau.layVanBanMatKhau();
        if (matKhau.isEmpty()) {
            nhanNhanDoManh.setText("");
            return;
        }
        if (matKhau.length() < 8) {
            nhanNhanDoManh.setText("Mật khẩu yếu (tối thiểu 8 ký tự)");
            nhanNhanDoManh.setForeground(MauUngDung.NGUY_HIEM);
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
        gc.gridy = 5;
        gc.gridx = 0;
        gc.gridwidth = 2;
        khungNoiDung.add(nhanQuyTac, gc);
    }

    private void themThanhThaoTacDoiMatKhau(JPanel khungNoiDung, GridBagConstraints gc) {
        JPanel khungCacThaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        khungCacThaoTac.setOpaque(false);
        NutDinhDang btnXoaTrang =
                new NutDinhDang("Xóa nội dung đã nhập", NutDinhDang.KieuNut.TRONG_SUOT);
        NutDinhDang btnLuu = new NutDinhDang("Đổi mật khẩu", NutDinhDang.KieuNut.CHINH);
        khungCacThaoTac.add(btnXoaTrang);
        khungCacThaoTac.add(btnLuu);
        gc.gridy = 6;
        khungNoiDung.add(khungCacThaoTac, gc);
        btnXoaTrang.addActionListener(
                e -> {
                    if (coThayDoiChuaLuu()
                            && !HopThoai.xacNhan(
                                    this, "Xóa nội dung đã nhập thông tin mật khẩu đang nhập?")) {
                        return;
                    }
                    xoaBieuMau();
                });
        btnLuu.addActionListener(e -> luuMatKhauMoi());
        khungTruongHienTaiMatKhau
                .layTruongMatKhau()
                .addActionListener(
                        e -> khungTruongMoiMatKhau.layTruongMatKhau().requestFocusInWindow());
        khungTruongMoiMatKhau
                .layTruongMatKhau()
                .addActionListener(
                        e -> khungTruongXacNhanMatKhau.layTruongMatKhau().requestFocusInWindow());
        khungTruongXacNhanMatKhau.layTruongMatKhau().addActionListener(e -> luuMatKhauMoi());
        dangKyBanPhimPhimTat("control S", e -> luuMatKhauMoi());
        dangKyBanPhimPhimTat("ESCAPE", e -> btnXoaTrang.doClick());
    }

    private void xoaBieuMau() {
        khungTruongHienTaiMatKhau.xoa();
        khungTruongMoiMatKhau.xoa();
        khungTruongXacNhanMatKhau.xoa();
        if (nhanNhanDoManh != null) nhanNhanDoManh.setText("");
    }

    public boolean coThayDoiChuaLuu() {
        return !khungTruongHienTaiMatKhau.layVanBanMatKhau().isEmpty()
                || !khungTruongMoiMatKhau.layVanBanMatKhau().isEmpty()
                || !khungTruongXacNhanMatKhau.layVanBanMatKhau().isEmpty();
    }

    @Override
    public boolean coTheRoiManHinh() {
        if (!coThayDoiChuaLuu()) return true;
        return HopThoai.xacNhan(
                this, "Thông tin đổi mật khẩu chưa được lưu. Bạn có chắc muốn rời màn hình này?");
    }

    @Override
    public void xuLyLamMoi() {
        if (coThayDoiChuaLuu()
                && !HopThoai.xacNhan(this, "Xóa nội dung đã nhập thông tin mật khẩu đang nhập?")) {
            return;
        }
        xoaBieuMau();
    }

    @Override
    public void xuLyHienThi() {
        xoaBieuMau();
    }

    private TruongMatKhauAnHien taoTruongMatKhau() {
        TruongMatKhauAnHien khungTruong = new TruongMatKhauAnHien(20);
        khungTruong.setPreferredSize(new Dimension(300, MauUngDung.CAO_DAU_VAO));
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
            HopThoai.hienThiCanhBao(
                    this, "Nhập lại mật khẩu mới không khớp.", "Dữ liệu chưa hợp lệ");
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
                    var phien = PhienDangNhapService.macDinh().layPhienHienTai();
                    if (phien != null && phien.getTaiKhoan() != null) {
                        phien.getTaiKhoan().setBatBuocDoiMatKhau(false);
                    }
                    HopThoai.hienThiThongTin(this, "Đã cập nhật mật khẩu.", "Thành công");
                    xoaBieuMau();
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
    protected String layTieuDeTroGiup() {
        return "Đổi mật khẩu";
    }
}
