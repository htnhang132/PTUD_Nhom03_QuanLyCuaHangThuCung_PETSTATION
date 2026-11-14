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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

    private final BoDemMenhGia khungMoBoDem = new BoDemMenhGia();

    private final JLabel nhanMoLoi = new JLabel("");

    private final JLabel nhanCuoiCungBanGiao = new JLabel("");

    private final JLabel nhanDangChoBieuNgu = new JLabel("");

    private final JPanel khungCacChiSo = new JPanel(new GridLayout(2, 3, 10, 10));

    private final BoDemMenhGia khungDongBoDem = new BoDemMenhGia();

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
        setLayout(new BorderLayout(0, 14));
        JLabel nhanTieuDe = taoTieuDeMuc("Ca thu ngân");
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 21));
        add(nhanTieuDe, BorderLayout.NORTH);
        khungGoc.setOpaque(false);
        khungGoc.add(taoMo(), "MO_CA");
        khungGoc.add(taoDangChay(), "DANG_CHAY");
        JScrollPane scrNoiDung = new JScrollPane(khungGoc);
        scrNoiDung.setBorder(BorderFactory.createEmptyBorder());
        scrNoiDung.getViewport().setBackground(MauUngDung.NEN_KEM);
        add(scrNoiDung, BorderLayout.CENTER);
        caLamViec.themBoLangNghe(boLangNgheCa);
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
        JPanel the = layThe(new BorderLayout(0, 12));
        the.setPreferredSize(new Dimension(860, 420));
        JPanel dau = taoVungTrongSuot(new BorderLayout(0, 8));
        dau.add(taoTieuDeMuc("Mở ca làm việc"), BorderLayout.NORTH);
        dau.add(nhanDangChoBieuNgu, BorderLayout.SOUTH);
        the.add(dau, BorderLayout.NORTH);

        // Bố cục 2 cột ngang: Trái = Thông tin & Mở ca, Phải = Bảng đếm mệnh giá
        JPanel noiDung = taoVungTrongSuot(new GridLayout(1, 2, 20, 0));
        JPanel cotTrai = taoVungTrongSuot(new BorderLayout(0, 12));
        JPanel form = taoVungTrongSuot(new GridBagLayout());
        GridBagConstraints viTri = new GridBagConstraints();
        viTri.insets = new Insets(6, 4, 6, 4);
        viTri.anchor = GridBagConstraints.WEST;
        viTri.gridx = 0;
        viTri.gridy = 0;
        form.add(new JLabel("Ca làm việc"), viTri);
        viTri.gridx = 1;
        cboTenCaLamViec.setPreferredSize(new Dimension(240, 36));
        form.add(cboTenCaLamViec, viTri);
        viTri.gridx = 0;
        viTri.gridy = 1;
        form.add(new JLabel("Tiền mặt đầu ca (đ)"), viTri);
        viTri.gridx = 1;
        oTienDauCa.setPreferredSize(new Dimension(240, 36));
        form.add(oTienDauCa, viTri);
        cotTrai.add(form, BorderLayout.NORTH);
        JPanel duoi = taoVungTrongSuot(new BorderLayout(0, 8));
        duoi.add(nhanCuoiCungBanGiao, BorderLayout.NORTH);
        duoi.add(nhanMoLoi, BorderLayout.CENTER);
        btnNutMo.setPreferredSize(new Dimension(0, 42));
        btnNutMo.addActionListener(e -> moCaLamViec());
        duoi.add(btnNutMo, BorderLayout.SOUTH);
        cotTrai.add(duoi, BorderLayout.SOUTH);
        JPanel cotPhai = taoVungTrongSuot(new BorderLayout(0, 6));
        cotPhai.add(taoKiemDemTuyChon(khungMoBoDem), BorderLayout.CENTER);
        noiDung.add(cotTrai);
        noiDung.add(cotPhai);
        khungMoBoDem.layKhiThayDoi(
                () -> oTienDauCa.setText(String.valueOf(khungMoBoDem.tinhTong())));
        khungMoBoDem.layKhiHoanTat(this::moCaLamViec);
        oTienDauCa.addActionListener(e -> moCaLamViec());
        the.add(noiDung, BorderLayout.CENTER);
        JPanel canGiua = taoVungTrongSuot(new GridBagLayout());
        GridBagConstraints viTriThe = new GridBagConstraints();
        viTriThe.weightx = 1;
        viTriThe.weighty = 1;
        viTriThe.anchor = GridBagConstraints.NORTH;
        canGiua.add(the, viTriThe);
        return canGiua;
    }

    private JCheckBox chkKiemDemDong;

    private JPanel taoDangChay() {
        JPanel khungVungDangXet = taoVungTrongSuot(new BorderLayout(0, 14));
        khungCacChiSo.setOpaque(false);
        khungVungDangXet.add(khungCacChiSo, BorderLayout.NORTH);
        JPanel khungGiua = taoVungTrongSuot(new BorderLayout(14, 0));
        khungGiua.add(taoTheThongTinKetCa(), BorderLayout.WEST);
        khungGiua.add(taoTheKiemDemMenhGia(), BorderLayout.CENTER);
        khungVungDangXet.add(khungGiua, BorderLayout.CENTER);
        return khungVungDangXet;
    }

    private JPanel taoTheKiemDemMenhGia() {
        JPanel khungSoLuong = layThe(new BorderLayout(0, 8));
        khungSoLuong.add(taoTieuDeMuc("Kiểm đếm tiền mặt trong két"), BorderLayout.NORTH);
        chkKiemDemDong = new JCheckBox("Kiểm đếm theo mệnh giá (tự động tính)", true);
        chkKiemDemDong.setOpaque(false);
        chkKiemDemDong.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        JPanel khungDemNoiDung = new JPanel(new BorderLayout(0, 8));
        khungDemNoiDung.setOpaque(false);
        khungDemNoiDung.add(chkKiemDemDong, BorderLayout.NORTH);
        khungDemNoiDung.add(khungDongBoDem, BorderLayout.CENTER);
        khungSoLuong.add(khungDemNoiDung, BorderLayout.CENTER);
        oTienThucDem.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        oTienThucDem.setHorizontalAlignment(SwingConstants.RIGHT);
        oTienThucDem.putClientProperty("JTextField.placeholderText", "0₫");
        capNhatCheDoKiemDem();
        chkKiemDemDong.addActionListener(
                e -> {
                    khungDongBoDem.setVisible(chkKiemDemDong.isSelected());
                    capNhatCheDoKiemDem();
                    khungSoLuong.revalidate();
                    khungSoLuong.repaint();
                });
        themBoLangNgheTaiLieu(oTienThucDem, this::capNhatSoLuongTomTat);
        khungDongBoDem.layKhiThayDoi(
                () -> {
                    if (chkKiemDemDong != null && chkKiemDemDong.isSelected()) {
                        oTienThucDem.setText(String.valueOf(khungDongBoDem.tinhTong()));
                    }
                });
        khungDongBoDem.layKhiHoanTat(() -> oGhiChu.requestFocusInWindow());
        return khungSoLuong;
    }

    private JPanel taoTheThongTinKetCa() {
        JPanel khungBen = new JPanel();
        khungBen.setOpaque(false);
        khungBen.setLayout(new BoxLayout(khungBen, BoxLayout.Y_AXIS));
        themTruongFormKetCa(khungBen, "Tổng tiền mặt thực đếm (đ)", oTienThucDem);
        nhanChenhLech.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        khungBen.add(taoCumNhapLieu(layNho("Chênh lệch so với dự kiến"), nhanChenhLech));
        khungBen.add(Box.createVerticalStrut(12));
        themTruongFormKetCa(khungBen, "Hình thức kết ca", cboHinhThuc);
        cboNguoiNhan.setEnabled(false);
        cboHinhThuc.addActionListener(
                e -> cboNguoiNhan.setEnabled(cboHinhThuc.getSelectedIndex() == 1));
        themTruongFormKetCa(khungBen, "Người nhận ca", cboNguoiNhan);
        themTruongFormKetCa(khungBen, "Ghi chú (bắt buộc khi có chênh lệch)", oGhiChu);
        oGhiChu.addActionListener(suKien -> dongCaLamViec());
        nhanDongLoi.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
        nhanDongLoi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanDongLoi.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungBen.add(nhanDongLoi);
        khungBen.add(Box.createVerticalStrut(8));
        btnNutDong.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnNutDong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnNutDong.setPreferredSize(new Dimension(300, 42));
        btnNutDong.addActionListener(suKien -> dongCaLamViec());
        khungBen.add(btnNutDong);
        JPanel khungBenThe = layThe(new BorderLayout());
        khungBenThe.setPreferredSize(new Dimension(360, 0));
        khungBenThe.add(khungBen, BorderLayout.NORTH);
        return khungBenThe;
    }

    private void themTruongFormKetCa(JPanel khungBen, String nhan, JComponent truong) {
        truong.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        truong.setPreferredSize(new Dimension(300, 36));
        khungBen.add(taoCumNhapLieu(layNho(nhan), truong));
        khungBen.add(Box.createVerticalStrut(12));
    }

    private void capNhatCheDoKiemDem() {
        boolean bat = chkKiemDemDong != null && chkKiemDemDong.isSelected();
        oTienThucDem.setEditable(!bat);
        oTienThucDem.setBackground(bat ? MauUngDung.mau(0xF5F5F4) : Color.WHITE);
        if (bat) {
            oTienThucDem.setText(String.valueOf(khungDongBoDem.tinhTong()));
        }
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
            khungMoBoDem.datLai();
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
                layChiSoThe(
                        "Ca hiện tại (từ " + caLamViec.layGioMoCa().format(DinhDang.GIO_PHUT) + ")",
                        caLamViec.layTenCa()));
        khungCacChiSo.add(layChiSoThe("Tiền đầu ca", DinhDang.dinhDang(caLamViec.layTienDauCa())));
        khungCacChiSo.add(
                layChiSoThe(
                        "Doanh thu tiền mặt", DinhDang.dinhDang(caLamViec.layDoanhThuTienMat())));
        khungCacChiSo.add(
                layChiSoThe(
                        "Chuyển khoản / thẻ",
                        DinhDang.dinhDang(
                                caLamViec.layDoanhThuChuyenKhoan()
                                        + caLamViec.layDoanhThuThe()
                                        + caLamViec.layCocHotel(PhuongThuc.CHUYEN_KHOAN)
                                        + caLamViec.layCocHotel(PhuongThuc.THE_NGAN_HANG))));
        khungCacChiSo.add(
                layChiSoThe("Cọc Hotel tiền mặt", DinhDang.dinhDang(caLamViec.layTienCocHotel())));
        khungCacChiSo.add(
                layChiSoTheNoiBat(
                        "Dự kiến trong két", DinhDang.dinhDang(caLamViec.layTienMatDuKien())));
        khungCacChiSo.revalidate();
        khungCacChiSo.repaint();
        khungDongBoDem.datLai();
        oTienThucDem.setText("");
        cboNguoiNhan.removeAllItems();
        caLamViec.layNguoiNhan().forEach(cboNguoiNhan::addItem);
        cboNguoiNhan.setSelectedItem(null);
        nhanDongLoi.setText("");
        capNhatSoLuongTomTat();
        cacThe.show(khungGoc, "DANG_CHAY");
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
        return "Mở ca: chọn ca và nhập tổng tiền mặt đầu ca. Kiểm đếm mệnh giá là tùy chọn.\n"
                + "Kết ca: nhập tổng tiền mặt thực đếm; hệ thống không điền hộ số tiền này.\n"
                + "Chọn nộp quỹ cửa hàng hoặc bàn giao cho quản lý/thu ngân đang hoạt động. "
                + "Có chênh lệch thì nhập lý do; xác nhận để kết ca.";
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

    private JPanel layChiSoTheNoiBat(String nhan, String giaTri) {
        BoGocPanel khungThe = new BoGocPanel(12, MauUngDung.mau(0xFFF9F5), true);
        khungThe.setLayout(new BorderLayout(0, 4));
        khungThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.DO_NHAN_MANH, 2, true),
                        new EmptyBorder(12, 16, 12, 16)));
        JLabel nhanHienThi = new JLabel(nhan);
        nhanHienThi.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanHienThi.setForeground(MauUngDung.DO_SAU);
        JLabel nhanGiaTri = new JLabel(giaTri);
        nhanGiaTri.setFont(MauUngDung.taoPhongChu(Font.BOLD, 26));
        nhanGiaTri.setForeground(MauUngDung.DO_NHAN_MANH);
        khungThe.add(nhanHienThi, BorderLayout.NORTH);
        khungThe.add(nhanGiaTri, BorderLayout.CENTER);
        return khungThe;
    }

    private JPanel taoKiemDemTuyChon(BoDemMenhGia boDem) {
        JPanel khung = new JPanel(new BorderLayout(0, 8));
        khung.setOpaque(false);
        JCheckBox hienThi = new JCheckBox("Kiểm đếm theo mệnh giá (tùy chọn)", true);
        hienThi.setOpaque(false);
        khung.add(hienThi, BorderLayout.NORTH);
        khung.add(boDem, BorderLayout.CENTER);
        boDem.setVisible(hienThi.isSelected());
        hienThi.addActionListener(
                e -> {
                    boDem.setVisible(hienThi.isSelected());
                    khung.revalidate();
                    khung.repaint();
                });
        return khung;
    }
}
