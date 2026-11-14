package com.petstation.gui.banhang;

import com.petstation.gui.chung.BieuMauPanel;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.KhaNangThanhCongCu;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

// Màn hình: Ca thu ngân (Mã: CA_THU_NGAN)
// Luồng xử lý: GUI -> CaThuNganService -> PhienLamViecDAOImpl -> SQL (phien_lam_viec, hoa_don)
// Mô tả: Khởi tạo mở ca, đối soát tiền mặt thực tế và kết chuyển bàn giao ca thu ngân
// Phân hệ: banhang
public final class CaThuNganPanel extends CoSoPanel {
    private final CaThuNganService caLamViec;

    private final CardLayout cacThe = new CardLayout();

    private final JPanel khungGoc = new JPanel(cacThe);

    private final JComboBox<String> cboTenCaLamViec =
            BieuMauPanel.layHopChon(new String[] {"Ca sáng", "Ca chiều"});

    private final JLabel nhanMoLoi = new JLabel("");

    private final JLabel nhanCuoiCungBanGiao = new JLabel("");

    private final JLabel nhanDangChoBieuNgu = new JLabel("");

    private final JPanel khungCacChiSo = new JPanel(new GridLayout(2, 3, 14, 12));

    private final JLabel nhanChenhLech = new JLabel();

    private final JComboBox<CaThuNganService.NguoiNhan> cboNguoiNhan = new JComboBox<>();

    private final JComboBox<String> cboHinhThuc =
            new JComboBox<>(new String[] {"Nộp quỹ cửa hàng", "Bàn giao nhân viên"});
    private final JTextField oTienDauCa = BieuMauPanel.layTruongVanBan("");
    private final JTextField oTienThucDem = BieuMauPanel.layTruongVanBan("");

    private final JTextField oGhiChu = BieuMauPanel.layTruongVanBan("");

    private final JLabel nhanDongLoi = new JLabel("");

    private final NutDinhDang btnNutMo = new NutDinhDang("Mở ca", NutDinhDang.KieuNut.CHINH);

    private final NutDinhDang btnNutDong = new NutDinhDang("Kết ca", NutDinhDang.KieuNut.CHINH);

    private final BoLangNgheSuKien<CaThuNganService> boLangNgheCa =
            ca -> {
                if (SwingUtilities.isEventDispatchThread()) {
                    lamMoi();
                } else {
                    SwingUtilities.invokeLater(this::lamMoi);
                }
            };

    public CaThuNganPanel(CaThuNganService caLamViec) {
        this.caLamViec = caLamViec;
        setBorder(new EmptyBorder(12, 24, 16, 24));
        setLayout(new BorderLayout(0, 12));
        add(taoTieuDeManHinh("Ca thu ngân"), BorderLayout.NORTH);
        khungGoc.setOpaque(false);
        khungGoc.add(taoMo(), "MO_CA");
        khungGoc.add(taoDangChay(), "DANG_CHAY");
        add(khungGoc, BorderLayout.CENTER);
        lamMoi();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        caLamViec.themBoLangNghe(boLangNgheCa);
    }

    @Override
    public void removeNotify() {
        caLamViec.xoaBoLangNghe(boLangNgheCa);
        super.removeNotify();
    }

    private JPanel taoMo() {
        JPanel khungMo = taoVungTrongSuot(new GridBagLayout());

        BoGocPanel theThongTin = new BoGocPanel(12, MauUngDung.THE_NEN, true);
        theThongTin.setLayout(new BorderLayout(0, 18));
        theThongTin.setBorder(new EmptyBorder(24, 28, 24, 28));
        theThongTin.setPreferredSize(new Dimension(720, 310));

        JPanel dau = taoVungTrongSuot(new BorderLayout(0, 4));
        dau.add(taoTieuDeMuc("Mở ca làm việc"), BorderLayout.NORTH);
        JLabel nhanMoTa =
                layNho(
                        "Chọn ca làm việc và nhập tổng tiền mặt hiện có trong két để bắt đầu"
                                + " ca.");
        dau.add(nhanMoTa, BorderLayout.CENTER);
        nhanDangChoBieuNgu.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 12));
        nhanDangChoBieuNgu.setForeground(MauUngDung.DO_SAU);
        dau.add(nhanDangChoBieuNgu, BorderLayout.SOUTH);
        theThongTin.add(dau, BorderLayout.NORTH);

        JPanel form = taoVungTrongSuot(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        cboTenCaLamViec.setPreferredSize(new Dimension(260, 42));
        oTienDauCa.setPreferredSize(new Dimension(260, 42));
        oTienDauCa.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        oTienDauCa.setHorizontalAlignment(SwingConstants.RIGHT);
        oTienDauCa.putClientProperty("JTextField.placeholderText", "0₫");
        oTienDauCa.addActionListener(e -> moCaLamViec());

        JPanel hangNhap = new JPanel(new GridLayout(1, 2, 18, 0));
        hangNhap.setOpaque(false);
        hangNhap.add(taoCumNhapLieu(layNho("Ca làm việc"), cboTenCaLamViec));
        hangNhap.add(taoCumNhapLieu(layNho("Tiền mặt đầu ca (đ)"), oTienDauCa));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 14, 0);
        form.add(hangNhap, gbc);

        nhanCuoiCungBanGiao.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanCuoiCungBanGiao.setForeground(MauUngDung.VAN_BAN_XAM);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(nhanCuoiCungBanGiao, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        form.add(taoVungTrongSuot(new BorderLayout()), gbc);
        theThongTin.add(form, BorderLayout.CENTER);

        JPanel duoi = taoVungTrongSuot(new BorderLayout(0, 6));
        nhanMoLoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
        nhanMoLoi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        duoi.add(nhanMoLoi, BorderLayout.NORTH);
        btnNutMo.setPreferredSize(new Dimension(0, 44));
        btnNutMo.addActionListener(e -> moCaLamViec());
        duoi.add(btnNutMo, BorderLayout.SOUTH);
        theThongTin.add(duoi, BorderLayout.SOUTH);

        GridBagConstraints gbcThe = new GridBagConstraints();
        gbcThe.gridx = 0;
        gbcThe.gridy = 0;
        khungMo.add(theThongTin, gbcThe);
        return khungMo;
    }

    private JPanel taoDangChay() {
        JPanel khungVungDangXet = taoVungTrongSuot(new BorderLayout(0, 12));
        khungCacChiSo.setOpaque(false);
        khungVungDangXet.add(khungCacChiSo, BorderLayout.NORTH);
        khungVungDangXet.add(taoTheThongTinKetCa(), BorderLayout.CENTER);
        return khungVungDangXet;
    }

    private JPanel taoTheThongTinKetCa() {
        JPanel noiDungHaiCot = new JPanel(new GridLayout(1, 2, 28, 0));
        noiDungHaiCot.setOpaque(false);
        noiDungHaiCot.add(taoCotDoiSoatTienMat());
        noiDungHaiCot.add(taoCotBanGiaoKetCa());

        JPanel khungBenThe = layTheGon(new BorderLayout(0, 14));
        khungBenThe.setBorder(new EmptyBorder(16, 22, 18, 22));
        khungBenThe.add(taoTieuDeMuc("Đối soát tiền mặt & kết ca"), BorderLayout.NORTH);
        khungBenThe.add(noiDungHaiCot, BorderLayout.CENTER);
        return khungBenThe;
    }

    private JPanel taoCotDoiSoatTienMat() {
        oTienThucDem.setPreferredSize(new Dimension(320, 42));
        oTienThucDem.setFont(MauUngDung.taoPhongChu(Font.BOLD, 19));
        oTienThucDem.setHorizontalAlignment(SwingConstants.RIGHT);
        oTienThucDem.putClientProperty("JTextField.placeholderText", "0₫");
        themBoLangNgheTaiLieu(oTienThucDem, this::capNhatSoLuongTomTat);
        oTienThucDem.addActionListener(suKien -> dongCaLamViec());

        JPanel khungTrai = taoVungTrongSuot(new GridBagLayout());
        GridBagConstraints gbcTrai = new GridBagConstraints();
        gbcTrai.gridx = 0;
        gbcTrai.weightx = 1;
        gbcTrai.fill = GridBagConstraints.HORIZONTAL;
        gbcTrai.anchor = GridBagConstraints.NORTH;

        gbcTrai.gridy = 0;
        gbcTrai.insets = new Insets(0, 0, 12, 0);
        khungTrai.add(taoCumNhapLieu(layNho("Tổng tiền mặt thực đếm (đ)"), oTienThucDem), gbcTrai);

        JPanel khungChenhLech = new JPanel(new BorderLayout(10, 0));
        khungChenhLech.setBackground(MauUngDung.mau(0xFAF7F2));
        khungChenhLech.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.KEM_VIEN),
                        new EmptyBorder(12, 14, 12, 14)));
        khungChenhLech.add(layNho("Chênh lệch so với dự kiến"), BorderLayout.WEST);
        nhanChenhLech.setFont(MauUngDung.taoPhongChu(Font.BOLD, 17));
        nhanChenhLech.setHorizontalAlignment(SwingConstants.RIGHT);
        khungChenhLech.add(nhanChenhLech, BorderLayout.CENTER);
        gbcTrai.gridy = 1;
        gbcTrai.insets = new Insets(0, 0, 10, 0);
        khungTrai.add(khungChenhLech, gbcTrai);

        gbcTrai.gridy = 2;
        gbcTrai.insets = new Insets(0, 0, 0, 0);
        khungTrai.add(
                layNho("Nhập trực tiếp tổng tiền mặt thực đếm trong két để đối soát."), gbcTrai);

        gbcTrai.gridy = 3;
        gbcTrai.weighty = 1;
        khungTrai.add(taoVungTrongSuot(new BorderLayout()), gbcTrai);
        return khungTrai;
    }

    private JPanel taoCotBanGiaoKetCa() {
        JPanel khungPhai = taoVungTrongSuot(new BorderLayout(0, 12));
        JPanel khungFormPhai = taoVungTrongSuot(new GridBagLayout());
        GridBagConstraints gbcPhai = new GridBagConstraints();
        gbcPhai.gridx = 0;
        gbcPhai.weightx = 1;
        gbcPhai.fill = GridBagConstraints.HORIZONTAL;
        gbcPhai.anchor = GridBagConstraints.NORTH;

        cboHinhThuc.setPreferredSize(new Dimension(180, 40));
        cboNguoiNhan.setPreferredSize(new Dimension(180, 40));
        cboNguoiNhan.setEnabled(false);
        cboHinhThuc.addActionListener(
                e -> cboNguoiNhan.setEnabled(cboHinhThuc.getSelectedIndex() == 1));
        JPanel khungBanGiao = new JPanel(new GridLayout(1, 2, 14, 0));
        khungBanGiao.setOpaque(false);
        khungBanGiao.add(taoCumNhapLieu(layNho("Hình thức kết ca"), cboHinhThuc));
        khungBanGiao.add(taoCumNhapLieu(layNho("Người nhận ca"), cboNguoiNhan));
        gbcPhai.gridy = 0;
        gbcPhai.insets = new Insets(0, 0, 12, 0);
        khungFormPhai.add(khungBanGiao, gbcPhai);

        oGhiChu.setPreferredSize(new Dimension(300, 40));
        oGhiChu.addActionListener(suKien -> dongCaLamViec());
        gbcPhai.gridy = 1;
        gbcPhai.insets = new Insets(0, 0, 0, 0);
        khungFormPhai.add(
                taoCumNhapLieu(layNho("Ghi chú bàn giao (bắt buộc khi có chênh lệch)"), oGhiChu),
                gbcPhai);

        gbcPhai.gridy = 2;
        gbcPhai.weighty = 1;
        khungFormPhai.add(taoVungTrongSuot(new BorderLayout()), gbcPhai);

        JPanel khungChan = taoVungTrongSuot(new BorderLayout(0, 6));
        nhanDongLoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
        nhanDongLoi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        khungChan.add(nhanDongLoi, BorderLayout.NORTH);
        btnNutDong.setPreferredSize(new Dimension(0, 44));
        btnNutDong.addActionListener(suKien -> dongCaLamViec());
        khungChan.add(btnNutDong, BorderLayout.SOUTH);

        khungPhai.add(khungFormPhai, BorderLayout.CENTER);
        khungPhai.add(khungChan, BorderLayout.SOUTH);
        return khungPhai;
    }

    private static JPanel taoCumNhapLieu(JLabel nhanChu, JComponent comp) {
        JPanel khungTam = new JPanel(new BorderLayout(0, 4));
        khungTam.setOpaque(false);
        khungTam.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungTam.add(nhanChu, BorderLayout.NORTH);
        khungTam.add(comp, BorderLayout.CENTER);
        return khungTam;
    }

    private JLabel layNho(String vanBan) {
        JLabel nhanDongDangXet = new JLabel(vanBan);
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        nhanDongDangXet.setAlignmentX(Component.LEFT_ALIGNMENT);
        return nhanDongDangXet;
    }

    private void moCaLamViec() {
        long tienMat;
        try {
            tienMat = docSoTien(oTienDauCa);
        } catch (IllegalArgumentException e) {
            HopThoai.hienThiCanhBao(this, e.getMessage());
            return;
        }
        if (tienMat == 0 && !HopThoai.xacNhan(this, "Tiền đầu ca là 0₫. Vẫn mở ca?")) return;
        nhanMoLoi.setText("");
        String ten = String.valueOf(cboTenCaLamViec.getSelectedItem());
        btnNutMo.setEnabled(false);
        TacVuNen.chay(
                () -> {
                    caLamViec.mo(ten, tienMat);
                    return null;
                },
                v -> btnNutMo.setEnabled(true),
                ex -> {
                    btnNutMo.setEnabled(true);
                    HopThoai.hienThiCanhBao(CaThuNganPanel.this, TacVuNen.thongDiepLoi(ex));
                });
    }

    private void dongCaLamViec() {
        long tong;
        try {
            tong = docSoTien(oTienThucDem);
        } catch (IllegalArgumentException e) {
            nhanDongLoi.setText(e.getMessage());
            return;
        }
        long chenhLech = tong - caLamViec.layTienMatDuKien();
        if (cboHinhThuc.getSelectedIndex() == 1 && cboNguoiNhan.getSelectedItem() == null) {
            nhanDongLoi.setText("Nhập tên người nhận bàn giao.");
            cboNguoiNhan.requestFocusInWindow();
            return;
        }
        if (chenhLech != 0 && oGhiChu.getText().isBlank()) {
            nhanDongLoi.setText(
                    "Có chênh lệch "
                            + DinhDang.dinhDang(chenhLech)
                            + ": hãy nhập ghi chú bàn giao.");
            oGhiChu.requestFocusInWindow();
            return;
        }
        boolean dongY =
                HopThoai.xacNhan(
                        this,
                        "Đóng "
                                + caLamViec.layTenCa()
                                + " với tiền thực đếm "
                                + DinhDang.dinhDang(tong)
                                + (chenhLech == 0
                                        ? " (khớp)."
                                        : " (chênh lệch " + DinhDang.dinhDang(chenhLech) + ").")
                                + "\nBàn giao cho: "
                                + (cboHinhThuc.getSelectedIndex() == 0
                                        ? "Nộp quỹ cửa hàng"
                                        : String.valueOf(cboNguoiNhan.getSelectedItem()))
                                + "\nSau khi đóng ca không thể bán hàng cho đến khi mở ca mới.");
        if (dongY) {
            var nguoiNhan = (CaThuNganService.NguoiNhan) cboNguoiNhan.getSelectedItem();
            boolean banGiao = cboHinhThuc.getSelectedIndex() == 1;
            String ghiChu = oGhiChu.getText().trim();
            CaThuNganService.HinhThucBanGiao hinhThuc =
                    banGiao
                            ? CaThuNganService.HinhThucBanGiao.BAN_GIAO
                            : CaThuNganService.HinhThucBanGiao.NOP_QUY;
            String maNguoiNhan = banGiao && nguoiNhan != null ? nguoiNhan.maNhanVien() : null;
            btnNutDong.setEnabled(false);
            TacVuNen.chay(
                    () -> {
                        caLamViec.dong(tong, ghiChu, hinhThuc, maNguoiNhan);
                        return null;
                    },
                    v -> {
                        btnNutDong.setEnabled(true);
                        oGhiChu.setText("");
                        cboNguoiNhan.setSelectedItem(null);
                    },
                    ex -> {
                        btnNutDong.setEnabled(true);
                        HopThoai.hienThiCanhBao(CaThuNganPanel.this, TacVuNen.thongDiepLoi(ex));
                    });
        }
    }

    private void capNhatSoLuongTomTat() {
        long chenhLech;
        try {
            chenhLech = docSoTien(oTienThucDem) - caLamViec.layTienMatDuKien();
        } catch (IllegalArgumentException e) {
            nhanChenhLech.setText("Chưa nhập tiền thực đếm");
            nhanChenhLech.setForeground(MauUngDung.VAN_BAN_XAM);
            return;
        }
        nhanChenhLech.setText(
                chenhLech == 0
                        ? "Khớp"
                        : (chenhLech > 0 ? "Thừa " : "Thiếu ")
                                + DinhDang.dinhDang(Math.abs(chenhLech)));
        nhanChenhLech.setForeground(
                chenhLech == 0 ? MauUngDung.XANH_LA : MauUngDung.VAN_BAN_NGUY_HIEM);
    }

    private static String layDeXuatCaLamViec() {
        LocalTime now = LocalTime.now();
        return now.isBefore(LocalTime.of(14, 30)) ? "Ca sáng" : "Ca chiều";
    }

    private void lamMoi() {
        if (!caLamViec.kiemTraMo()) {
            nhanCuoiCungBanGiao.setText(
                    caLamViec.layBanGiaoGanNhat().isEmpty()
                            ? ""
                            : "Ca gần nhất – " + caLamViec.layBanGiaoGanNhat());
            cboTenCaLamViec.setSelectedItem(layDeXuatCaLamViec());
            boolean dangCho = !caLamViec.layTieuDeDangCho().isEmpty();
            nhanDangChoBieuNgu.setVisible(dangCho);
            nhanDangChoBieuNgu.setText(
                    dangCho
                            ? "Mở ca xong hệ thống sẽ đưa bạn về \""
                                    + caLamViec.layTieuDeDangCho()
                                    + "\"."
                            : "");
            cacThe.show(khungGoc, "MO_CA");
            oTienDauCa.setText("");
            oTienDauCa.requestFocusInWindow();
            return;
        }
        khungCacChiSo.removeAll();
        khungCacChiSo.add(
                layChiSoTheGon(
                        "Ca hiện tại (" + caLamViec.layGioMoCa().format(DinhDang.GIO_PHUT) + ")",
                        caLamViec.layTenCa()));
        khungCacChiSo.add(
                layChiSoTheGon("Tiền đầu ca", DinhDang.dinhDang(caLamViec.layTienDauCa())));
        khungCacChiSo.add(
                layChiSoTheGon(
                        "Doanh thu tiền mặt", DinhDang.dinhDang(caLamViec.layDoanhThuTienMat())));
        khungCacChiSo.add(
                layChiSoTheGon(
                        "Chuyển khoản / thẻ",
                        DinhDang.dinhDang(
                                caLamViec.layDoanhThuChuyenKhoan()
                                        + caLamViec.layDoanhThuThe()
                                        + caLamViec.layCocHotel(PhuongThuc.CHUYEN_KHOAN)
                                        + caLamViec.layCocHotel(PhuongThuc.THE_NGAN_HANG))));
        khungCacChiSo.add(
                layChiSoTheGon(
                        "Cọc Hotel tiền mặt", DinhDang.dinhDang(caLamViec.layTienCocHotel())));
        khungCacChiSo.add(
                layChiSoTheNoiBat(
                        "Dự kiến trong két", DinhDang.dinhDang(caLamViec.layTienMatDuKien())));
        khungCacChiSo.revalidate();
        khungCacChiSo.repaint();
        oTienThucDem.setText("");
        cboNguoiNhan.removeAllItems();
        caLamViec.layNguoiNhan().forEach(cboNguoiNhan::addItem);
        cboNguoiNhan.setSelectedItem(null);
        nhanDongLoi.setText("");
        capNhatSoLuongTomTat();
        cacThe.show(khungGoc, "DANG_CHAY");
        oTienThucDem.requestFocusInWindow();
    }

    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(false, false, false, false, false, true);
    }

    @Override
    public void xuLyHienThi() {
        lamMoi();
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Mở ca: chọn ca làm việc và nhập trực tiếp tổng tiền mặt đầu ca.\n"
                + "Kết ca: nhập trực tiếp tổng tiền mặt thực đếm; hệ thống không điền hộ số tiền"
                + " này.\n"
                + "Chọn nộp quỹ cửa hàng hoặc bàn giao cho quản lý/thu ngân đang hoạt động. "
                + "Có chênh lệch thì nhập ghi chú bàn giao; xác nhận để kết ca.";
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Ca thu ngân";
    }

    private static long docSoTien(JTextField truong) {
        String giaTri = truong.getText().trim();
        if (!giaTri.matches("[0-9]+"))
            throw new IllegalArgumentException(
                    "Nhập số tiền nguyên không âm, không có dấu phân cách.");
        try {
            return Long.parseLong(giaTri);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Số tiền quá lớn.");
        }
    }

    private BoGocPanel layTheGon(LayoutManager boCuc) {
        BoGocPanel khung = new BoGocPanel(12, MauUngDung.THE_NEN, true);
        khung.setLayout(boCuc);
        khung.setBorder(new EmptyBorder(14, 18, 14, 18));
        return khung;
    }

    private JPanel layChiSoTheGon(String nhan, String giaTri) {
        BoGocPanel khungThe = new BoGocPanel(10, MauUngDung.THE_NEN, true);
        khungThe.setLayout(new BorderLayout(0, 6));
        khungThe.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel nhanHienThi = new JLabel(nhan);
        nhanHienThi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhanHienThi.setForeground(MauUngDung.VAN_BAN_XAM);
        JLabel nhanGiaTri = new JLabel(giaTri);
        nhanGiaTri.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        nhanGiaTri.setForeground(MauUngDung.VAN_BAN_TOI);
        khungThe.add(nhanHienThi, BorderLayout.NORTH);
        khungThe.add(nhanGiaTri, BorderLayout.CENTER);
        return khungThe;
    }

    private JPanel layChiSoTheNoiBat(String nhan, String giaTri) {
        BoGocPanel khungThe = new BoGocPanel(10, MauUngDung.mau(0xFFF9F5), true);
        khungThe.setLayout(new BorderLayout(0, 6));
        khungThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.DO_NHAN_MANH, 2, true),
                        new EmptyBorder(10, 14, 10, 14)));
        JLabel nhanHienThi = new JLabel(nhan);
        nhanHienThi.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanHienThi.setForeground(MauUngDung.DO_SAU);
        JLabel nhanGiaTri = new JLabel(giaTri);
        nhanGiaTri.setFont(MauUngDung.taoPhongChu(Font.BOLD, 22));
        nhanGiaTri.setForeground(MauUngDung.DO_NHAN_MANH);
        khungThe.add(nhanHienThi, BorderLayout.NORTH);
        khungThe.add(nhanGiaTri, BorderLayout.CENTER);
        return khungThe;
    }
}
