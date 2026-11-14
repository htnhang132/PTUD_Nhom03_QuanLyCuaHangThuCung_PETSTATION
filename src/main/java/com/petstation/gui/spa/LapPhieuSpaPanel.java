package com.petstation.gui.spa;

import com.petstation.dto.MucLuaChon;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuMauTruong.LuaChon;
import com.petstation.gui.khachhang.DanhMucKhachHangPanel;
import com.petstation.gui.khachhang.DanhMucThuCungPanel;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiTaiKhoan;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.spa.GoiDichVuSpa;
import com.petstation.model.spa.LichHen;
import com.petstation.model.spa.PhieuSpaTreo;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.service.chung.QuyTrinhService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.TiemPhongService;
import com.petstation.service.spa.LapPhieuSpaService;
import com.petstation.service.spa.PhieuSpaTreoService;
import com.petstation.service.spa.SpaService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public final class LapPhieuSpaPanel extends CoSoPanel {
    private final JTextField oSoDienThoai = new JTextField(16);
    private final JLabel nhanKhachHang = new JLabel("Chưa chọn khách hàng");
    private final JComboBox<LuaChon> cboThuCung = new JComboBox<>();
    private final JTextField oCanNang = new JTextField();
    private final JTextArea oGhiChu = new JTextArea(3, 28);
    private Object[] khachHang;
    private final JComboBox<LuaChon> cboDichVu = new JComboBox<>();
    private final JLabel nhanGia = new JLabel("Chọn dịch vụ và cân nặng để xem giá");
    private final JLabel nhanNguon = new JLabel("Khách đến trực tiếp");
    private final TiemPhongService tiemPhongService = new TiemPhongService();
    private final LapPhieuSpaService lapPhieuSpaService = new LapPhieuSpaService();
    private final NghiepVuService nghiepVuService = new NghiepVuService();
    private final PhieuSpaTreoService phieuSpaTreoService = new PhieuSpaTreoService();
    private final QuyTrinhService quyTrinhService = new QuyTrinhService();
    private final SpaService spaService = new SpaService();
    private final JTextArea oYeuCau = new JTextArea(2, 28);

    private final JLabel nhanHuyHieuHoSo = new JLabel("CHƯA CHỌN THÚ CƯNG");
    private final JLabel nhanChiTietHoSo =
            new JLabel(
                    "Chọn thú cưng bên trên để hệ thống tự động kiểm tra điều kiện tuổi và tiêm"
                            + " phòng.");
    private final JPanel khungTheHoSo = new BoGocPanel(10, MauUngDung.XAM_SIEU_NHAT, true);
    private JButton btnCapNhatSoTiem;

    private final JComboBox<String> cboKiemTraDa =
            new JComboBox<>(
                    new String[] {
                        "Không thấy bất thường",
                        "Có vết trầy xước / Vết thương cũ",
                        "Dấu hiệu nghi nấm / Gàu ngứa",
                        "Khác (mô tả bên dưới)"
                    });
    private final JComboBox<String> cboKiemTraVe =
            new JComboBox<>(new String[] {"Không phát hiện", "Có ve / Rận / Bọ chét"});
    private final JComboBox<String> cboKiemTraMatTai =
            new JComboBox<>(
                    new String[] {
                        "Sạch sẽ, bình thường",
                        "Mắt đỏ / Tai hôi / Chảy dịch",
                        "Khác (mô tả bên dưới)"
                    });
    private final JComboBox<String> cboKiemTraLong =
            new JComboBox<>(
                    new String[] {
                        "Lông mượt, không rối",
                        "Rối nhẹ",
                        "Rối vừa",
                        "Bết dính nặng (Cần gỡ rối/cạo)"
                    });
    private final JComboBox<String> cboKetLuanTiepNhan =
            new JComboBox<>(
                    new String[] {
                        "Đủ điều kiện tiếp nhận",
                        "Cần điều chỉnh dịch vụ / Chăm sóc đặc biệt",
                        "Từ chối tiếp nhận (có vết thương hở / sức khỏe yếu)"
                    });

    private final Consumer<String> khiLuu;
    private final Runnable khiTreo;
    private JButton btnTaoPhieu;
    private String maPhieuTreo = "";
    private String maHen = "";
    private int luotThuCung;
    private int luotGia;
    private int luotHoSo;
    private boolean boQuaSuKienThuCung;
    private boolean hoSoDuDieuKien;

    private static boolean laDangHoatDong(Object giaTri) {
        String s = giaTri == null ? "" : String.valueOf(giaTri).trim();
        return "Đang hoạt động".equalsIgnoreCase(s)
                || TrangThaiTaiKhoan.HOAT_DONG.maDB().equalsIgnoreCase(s);
    }

    public LapPhieuSpaPanel(Consumer<String> khiLuu, Runnable chonLich, Runnable khiTreo) {
        this.khiLuu = khiLuu;
        this.khiTreo = khiTreo;
        JPanel noiDung =
                new JPanel() {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(880, super.getPreferredSize().height);
                    }
                };
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));
        noiDung.setOpaque(false);
        khoiTaoThanhPhanNhapLieu();
        noiDung.add(taoNhomNguonVaKhach(chonLich));
        noiDung.add(Box.createVerticalStrut(12));
        noiDung.add(taoNhomHoSoVaDanhGia());
        noiDung.add(Box.createVerticalStrut(12));
        noiDung.add(taoNhomDichVuSpa());
        noiDung.add(Box.createVerticalStrut(12));
        noiDung.add(taoNhomGhiChuVaYeuCau());
        add(taoVungCuon(noiDung), BorderLayout.CENTER);
        add(taoThanhThaoTac(), BorderLayout.SOUTH);
    }

    @Override
    public void xuLyHienThi() {
        lamMoiDuLieuTab();
    }

    @Override
    public void xuLyLamMoi() {
        lamMoiDuLieuTab();
    }

    private void lamMoiDuLieuTab() {
        taiGoiDichVuNen(
                () -> {
                    if (khachHang != null) {
                        taiThuCung();
                    } else {
                        capNhatDanhGiaHoSo(false);
                    }
                    capNhatGiaTuDong();
                });
    }

    private void khoiTaoThanhPhanNhapLieu() {
        nhanNguon.setForeground(MauUngDung.NAU);
        nhanNguon.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        oSoDienThoai.setPreferredSize(new Dimension(180, 32));
        oSoDienThoai.putClientProperty("JTextField.placeholderText", "Tên, mã KH hoặc SĐT...");
        oSoDienThoai.addActionListener(e -> anToan(this::timKhach));
        nhanKhachHang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhanKhachHang.setForeground(MauUngDung.DO_THUONG_HIEU);
        cboThuCung.setPreferredSize(new Dimension(280, 32));
        btnCapNhatSoTiem = nut("Xem / Sửa sổ tiêm", this::moSoTiemThuCung);
        btnCapNhatSoTiem.setPreferredSize(new Dimension(150, 26));
        btnCapNhatSoTiem.setEnabled(false);
        cboDichVu.setSelectedIndex(-1);
        cboDichVu.setPreferredSize(new Dimension(280, 32));
        oCanNang.setPreferredSize(new Dimension(85, 32));
        nhanGia.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhanGia.setForeground(MauUngDung.DO_THUONG_HIEU);
        cboDichVu.addActionListener(e -> capNhatGiaTuDong());
        themBoLangNgheTaiLieu(oCanNang, this::capNhatGiaTuDong);
        cboThuCung.addActionListener(
                e -> {
                    if (!boQuaSuKienThuCung) khiChonThuCung();
                });
        taiGoiDichVuNen(null);
    }

    private void taiGoiDichVuNen(Runnable sauKhiNap) {
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                nghiepVuService::layLuaChonGoiDichVu,
                null,
                cacGoi -> {
                    LuaChon dangChon = (LuaChon) cboDichVu.getSelectedItem();
                    cboDichVu.removeAllItems();
                    for (MucLuaChon goi : cacGoi) {
                        cboDichVu.addItem(new LuaChon(goi.giaTri(), goi.nhan()));
                    }
                    if (dangChon != null) {
                        for (int i = 0; i < cboDichVu.getItemCount(); i++) {
                            if (dangChon.giaTri().equals(cboDichVu.getItemAt(i).giaTri())) {
                                cboDichVu.setSelectedIndex(i);
                                break;
                            }
                        }
                    } else {
                        cboDichVu.setSelectedIndex(-1);
                    }
                    if (sauKhiNap != null) sauKhiNap.run();
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private void khiChonThuCung() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        if (thu != null && oCanNang.getText().trim().isEmpty()) {
            String maThu = thu.giaTri();
            var phien = PhienDangNhapService.macDinh().layPhienHienTai();
            TacVuNen.chayCoPhien(
                    phien,
                    () -> true,
                    () ->
                            DanhMucService.macDinh().layThuCung().stream()
                                    .filter(t -> maThu.equals(String.valueOf(t[0])))
                                    .findFirst()
                                    .map(t -> t[6] == null ? "" : String.valueOf(t[6]).trim())
                                    .orElse(""),
                    null,
                    cn -> {
                        LuaChon hienTai = (LuaChon) cboThuCung.getSelectedItem();
                        if (hienTai != null
                                && maThu.equals(hienTai.giaTri())
                                && !cn.isEmpty()
                                && oCanNang.getText().trim().isEmpty()) {
                            oCanNang.setText(cn);
                        }
                    },
                    ex -> {});
        }
        capNhatGiaTuDong();
        capNhatDanhGiaHoSo(true);
    }

    private JPanel taoNhomNguonVaKhach(Runnable chonLich) {
        JButton btnChonLich = nut("Khách có hẹn — Chọn lịch hẹn", chonLich);
        btnChonLich.setPreferredSize(new Dimension(240, 32));
        JButton btnTim = nut("Tìm khách", this::timKhach);
        btnTim.setPreferredSize(new Dimension(95, 32));
        JButton themKhach = nut("+ Thêm khách", this::themKhach);
        themKhach.setPreferredSize(new Dimension(125, 32));
        themKhach.setEnabled(
                PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.QUAN_LY_KHACH_HANG));
        return nhom(
                "1. Nguồn tiếp nhận & Khách hàng",
                hang(
                        nhan("Nguồn tiếp nhận:"),
                        nhanNguon,
                        Box.createHorizontalStrut(12),
                        btnChonLich),
                hang(nhan("Tìm khách (Tên/Mã/SĐT):"), oSoDienThoai, btnTim, themKhach),
                hang(nhan("Khách hàng:"), nhanKhachHang));
    }

    private JPanel taoNhomHoSoVaDanhGia() {
        JButton hoSo = nut("+ Thêm thú cưng", this::moHoSoThuCung);
        hoSo.setPreferredSize(new Dimension(145, 32));
        hoSo.setEnabled(
                PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.QUAN_LY_THU_CUNG));
        khungTheHoSo.setLayout(new BorderLayout(8, 6));
        khungTheHoSo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        khungTheHoSo.setPreferredSize(new Dimension(840, 74));
        khungTheHoSo.setMinimumSize(new Dimension(840, 68));
        JPanel khungDauHoSo = new JPanel(new BorderLayout(8, 0));
        khungDauHoSo.setOpaque(false);
        nhanHuyHieuHoSo.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanHuyHieuHoSo.setForeground(MauUngDung.VAN_BAN_XAM);
        khungDauHoSo.add(nhanHuyHieuHoSo, BorderLayout.WEST);
        khungDauHoSo.add(btnCapNhatSoTiem, BorderLayout.EAST);
        nhanChiTietHoSo.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanChiTietHoSo.setForeground(MauUngDung.VAN_BAN_TOI);
        khungTheHoSo.add(khungDauHoSo, BorderLayout.NORTH);
        khungTheHoSo.add(nhanChiTietHoSo, BorderLayout.CENTER);
        cboKiemTraDa.setPreferredSize(new Dimension(220, 32));
        cboKiemTraVe.setPreferredSize(new Dimension(190, 32));
        cboKiemTraMatTai.setPreferredSize(new Dimension(220, 32));
        cboKiemTraLong.setPreferredSize(new Dimension(190, 32));
        cboKetLuanTiepNhan.setPreferredSize(new Dimension(280, 32));
        return nhom(
                "2. Hồ sơ thú cưng & Đánh giá thực tế tại quầy",
                hang(nhan("Thú cưng:"), cboThuCung, hoSo),
                khungTheHoSo,
                Box.createVerticalStrut(4),
                hang(
                        nhan("Da & Lông:"),
                        cboKiemTraDa,
                        Box.createHorizontalStrut(8),
                        nhan("Ký sinh trùng:"),
                        cboKiemTraVe),
                hang(
                        nhan("Mắt & Tai:"),
                        cboKiemTraMatTai,
                        Box.createHorizontalStrut(8),
                        nhan("Độ rối lông:"),
                        cboKiemTraLong),
                hang(nhan("Đánh giá tại quầy:"), cboKetLuanTiepNhan),
                goiY(
                        "Hệ thống tự động kiểm tra tuổi và sổ tiêm. Đánh giá nhanh tình trạng"
                                + " thực tế trước khi nhận bé vào dịch vụ."));
    }

    private JPanel taoNhomDichVuSpa() {
        JPanel khungDichVuGia =
                hang(
                        nhan("Dịch vụ & Cân nặng:"),
                        new JLabel("Cân nặng:"),
                        oCanNang,
                        new JLabel("kg"),
                        Box.createHorizontalStrut(12),
                        new JLabel("Gói dịch vụ:"),
                        cboDichVu);
        return nhom(
                "3. Dịch vụ Spa & Cân nặng",
                khungDichVuGia,
                hang(nhan("Giá dự kiến:"), nhanGia),
                goiY(
                        "Giá được tính tự động theo loài thú cưng (Chó/Mèo), cân nặng và bảng giá"
                                + " của gói dịch vụ."));
    }

    private JPanel taoNhomGhiChuVaYeuCau() {
        JLabel nhanTieuDeGhiChu = taoNhanMo("Ghi chú thêm về sức khỏe / lưu ý đặc biệt (nếu có):");
        JLabel nhanTieuDeYeuCau = taoNhanMo("Yêu cầu của khách / Vật dụng gửi kèm:");
        return nhom(
                "4. Ghi chú sức khỏe & Yêu cầu gửi kèm",
                nhanTieuDeGhiChu,
                taoOChuCuon(oGhiChu, 4, 85),
                nhanTieuDeYeuCau,
                taoOChuCuon(oYeuCau, 3, 75));
    }

    private static JLabel taoNhanMo(String vanBan) {
        JLabel nhanChu = new JLabel(vanBan);
        nhanChu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanChu.setForeground(MauUngDung.VAN_BAN_XAM);
        return nhanChu;
    }

    private static JScrollPane taoOChuCuon(JTextArea oNhap, int soDong, int chieuCao) {
        oNhap.setRows(soDong);
        oNhap.setLineWrap(true);
        oNhap.setWrapStyleWord(true);
        oNhap.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(oNhap);
        sp.setPreferredSize(new Dimension(840, chieuCao));
        sp.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN, 1, true));
        return sp;
    }

    private JPanel taoThanhThaoTac() {
        JPanel thaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        thaoTac.setBackground(Color.WHITE);
        thaoTac.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, MauUngDung.VIEN),
                        BorderFactory.createEmptyBorder(6, 20, 6, 20)));
        thaoTac.add(nut("Phiếu đang treo", this::moDanhSachPhieuTreo));
        thaoTac.add(nut("Lập phiếu mới", this::xoaForm));
        thaoTac.add(nut("Treo phiếu", this::treoPhieu));
        btnTaoPhieu = nut("Tạo phiếu tiếp nhận", this::luu);
        btnTaoPhieu.setPreferredSize(new Dimension(180, 36));
        thaoTac.add(btnTaoPhieu);
        return thaoTac;
    }

    private JScrollPane taoVungCuon(JPanel noiDung) {
        JPanel khungCanGiua = new JPanel(new GridBagLayout());
        khungCanGiua.setBackground(MauUngDung.NEN_KEM);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(16, 20, 24, 20);
        khungCanGiua.add(noiDung, gbc);
        JScrollPane cuon = new JScrollPane(khungCanGiua);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(MauUngDung.NEN_KEM);
        cuon.getVerticalScrollBar().setUnitIncrement(18);
        return cuon;
    }

    private static JLabel nhan(String vanBan) {
        JLabel l = new JLabel(vanBan);
        l.setPreferredSize(new Dimension(140, 32));
        l.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        l.setForeground(MauUngDung.VAN_BAN_TOI);
        return l;
    }

    private static JPanel hang(Component... cac) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.setOpaque(false);
        for (Component c : cac) p.add(c);
        return p;
    }

    private static JPanel nhom(String ten, Component... cac) {
        JPanel p = new BoGocPanel(14, MauUngDung.THE_NEN);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 16));
        JLabel nhan = new JLabel(ten);
        nhan.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhan.setForeground(MauUngDung.NAU);
        nhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(nhan);
        for (Component c : cac) {
            p.add(Box.createVerticalStrut(8));
            if (c instanceof JComponent j) {
                j.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            p.add(c);
        }
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private static JLabel goiY(String noiDung) {
        JLabel nhan = new JLabel(noiDung);
        nhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhan.setForeground(MauUngDung.VAN_BAN_XAM);
        nhan.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return nhan;
    }

    public void xoaForm() {
        if (chupNhap().coNoiDung()
                && !HopThoai.xacNhan(
                        this,
                        "Bỏ nội dung đang nhập để lập phiếu mới? Phiếu đã treo vẫn được giữ."))
            return;
        datLaiForm();
    }

    private void datLaiForm() {
        maPhieuTreo = "";
        maHen = "";
        cboDichVu.setEnabled(true);
        cboThuCung.setEnabled(true);
        nhanNguon.setText("Khách đến trực tiếp");
        oSoDienThoai.setText("");
        chonKhach(null);
        oCanNang.setText("");
        oGhiChu.setText("");
        oYeuCau.setText("");
        cboDichVu.setSelectedIndex(-1);
        cboKiemTraDa.setSelectedIndex(0);
        cboKiemTraVe.setSelectedIndex(0);
        cboKiemTraMatTai.setSelectedIndex(0);
        cboKiemTraLong.setSelectedIndex(0);
        cboKetLuanTiepNhan.setSelectedIndex(0);
        capNhatDanhGiaHoSo(false);
    }

    private PhieuSpaTreo chupNhap() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        KhachHang kh =
                khachHang != null && khachHang[0] != null && !String.valueOf(khachHang[0]).isBlank()
                        ? new KhachHang(String.valueOf(khachHang[0]))
                        : null;
        ThuCung tc =
                thu != null && thu.giaTri() != null && !thu.giaTri().isBlank()
                        ? new ThuCung(thu.giaTri())
                        : null;
        GoiDichVuSpa goiDv =
                goi != null && goi.giaTri() != null && !goi.giaTri().isBlank()
                        ? new GoiDichVuSpa(goi.giaTri())
                        : null;
        LichHen lh = null;
        if (maHen != null && !maHen.isBlank()) {
            lh = new LichHen();
            lh.setMaLichHen(maHen);
        }
        return new PhieuSpaTreo(
                kh,
                tc,
                goiDv,
                lh,
                oSoDienThoai.getText(),
                oCanNang.getText(),
                oGhiChu.getText(),
                oYeuCau.getText());
    }

    private void treoPhieu() {
        String maTreo = maPhieuTreo;
        PhieuSpaTreo duLieu = chupNhap();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> {
                    phieuSpaTreoService.treo(maTreo, duLieu);
                    return null;
                },
                null,
                v -> {
                    datLaiForm();
                    HopThoai.hienThiThanhCong(
                            this, "Đã treo phiếu Spa. Chọn Phiếu đang treo để tiếp tục sau.");
                    khiTreo.run();
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    public void moDanhSachPhieuTreo() {
        if (khiTreo != null) {
            khiTreo.run();
        }
    }

    public void khoiPhucPhieu(String ma) {
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> taiDuLieuKhoiPhuc(ma),
                null,
                duLieu -> apDungKhoiPhucPhieu(ma, duLieu),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private Object[] taiDuLieuKhoiPhuc(String ma) {
        PhieuSpaTreo p = phieuSpaTreoService.lay(ma);
        String maKhach =
                p.getKhachHang() != null && p.getKhachHang().getMaKhachHang() != null
                        ? p.getKhachHang().getMaKhachHang()
                        : "";
        String maThu =
                p.getThuCung() != null && p.getThuCung().getMaThuCung() != null
                        ? p.getThuCung().getMaThuCung()
                        : "";
        Object[] khach =
                maKhach.isBlank()
                        ? null
                        : DanhMucService.macDinh().layKhachHang().stream()
                                .filter(
                                        k ->
                                                maKhach.equals(String.valueOf(k[0]))
                                                        && laDangHoatDong(k[8]))
                                .findFirst()
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "Khách của phiếu treo không còn hoạt"
                                                                + " động."));
        List<Object[]> dsThu = DanhMucService.macDinh().layThuCung();
        if (!maThu.isBlank()
                && dsThu.stream()
                        .noneMatch(
                                t ->
                                        maThu.equals(String.valueOf(t[0]))
                                                && laDangHoatDong(t[13])
                                                && maKhach.equals(
                                                        DanhMucService.layMaTuLuaChon(t[8])))) {
            throw new IllegalArgumentException(
                    "Thú cưng của phiếu treo không còn thuộc hồ sơ khách đang hoạt động.");
        }
        return new Object[] {p, khach, dsThu, nghiepVuService.layLuaChonGoiDichVu()};
    }

    @SuppressWarnings("unchecked")
    private void apDungKhoiPhucPhieu(String ma, Object[] duLieu) {
        PhieuSpaTreo p = (PhieuSpaTreo) duLieu[0];
        Object[] khach = (Object[]) duLieu[1];
        List<Object[]> dsThu = (List<Object[]>) duLieu[2];
        List<MucLuaChon> cacGoi = (List<MucLuaChon>) duLieu[3];
        capNhatComboDichVuTuDanhSach(cacGoi);
        String maGoi =
                p.getGoiDichVu() != null && p.getGoiDichVu().getMaGoi() != null
                        ? p.getGoiDichVu().getMaGoi()
                        : "";
        String maThu =
                p.getThuCung() != null && p.getThuCung().getMaThuCung() != null
                        ? p.getThuCung().getMaThuCung()
                        : "";
        String maHenTreo =
                p.getLichHen() != null && p.getLichHen().getMaLichHen() != null
                        ? p.getLichHen().getMaLichHen()
                        : "";
        if (!maGoi.isBlank()) {
            boolean coGoi = false;
            for (int i = 0; i < cboDichVu.getItemCount(); i++)
                if (maGoi.equals(cboDichVu.getItemAt(i).giaTri())) coGoi = true;
            if (!coGoi) {
                HopThoai.hienThiCanhBao(this, "Dịch vụ của phiếu treo không còn hoạt động.");
                return;
            }
        }
        datLaiForm();
        chonKhach(khach, dsThu, maThu);
        for (int i = 0; i < cboDichVu.getItemCount(); i++)
            if (maGoi.equals(cboDichVu.getItemAt(i).giaTri())) cboDichVu.setSelectedIndex(i);
        maHen = maHenTreo;
        cboThuCung.setEnabled(maHen.isBlank());
        cboDichVu.setEnabled(maHen.isBlank());
        nhanNguon.setText(
                maHen.isBlank()
                        ? "Khách đến trực tiếp — đang sửa phiếu treo"
                        : "Từ lịch hẹn: " + maHen + " — đang sửa phiếu treo");
        oSoDienThoai.setText(p.getSoDienThoai());
        oCanNang.setText(p.getCanNang());
        oGhiChu.setText(p.getTinhTrang());
        oYeuCau.setText(p.getYeuCau());
        cboKiemTraDa.setSelectedIndex(0);
        cboKiemTraVe.setSelectedIndex(0);
        cboKiemTraMatTai.setSelectedIndex(0);
        cboKiemTraLong.setSelectedIndex(0);
        cboKetLuanTiepNhan.setSelectedIndex(0);
        capNhatDanhGiaHoSo(false);
        capNhatGiaTuDong();
        maPhieuTreo = ma;
        HopThoai.hienThiCanhBao(
                this,
                "Đã khôi phục phiếu treo #"
                        + ma
                        + ".\nVui lòng kiểm tra và chọn lại 5 mục sức khỏe thực tế trước khi tạo"
                        + " phiếu.");
    }

    private void capNhatComboDichVuTuDanhSach(List<MucLuaChon> cacGoi) {
        if (cboDichVu.getItemCount() == 0 && cacGoi != null) {
            for (MucLuaChon g : cacGoi) {
                cboDichVu.addItem(new LuaChon(g.giaTri(), g.nhan()));
            }
            cboDichVu.setSelectedIndex(-1);
        }
    }

    public void nhanLichHen(String maLichHen) {
        if (khachHang != null
                && !HopThoai.xacNhan(this, "Thay nội dung đang nhập bằng lịch hẹn đã chọn?"))
            return;
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> taiDuLieuNhanLichHen(maLichHen),
                null,
                duLieu -> apDungNhanLichHen(duLieu),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private Object[] taiDuLieuNhanLichHen(String maLichHen) {
        var hen = quyTrinhService.layLichHen(maLichHen);
        if (hen.isEmpty()) throw new IllegalArgumentException("Lịch hẹn không còn chờ tiếp nhận.");
        LichHen chiTiet = spaService.layChiTietLichHen(maLichHen);
        List<Object[]> dsThu = DanhMucService.macDinh().layThuCung();
        String maThuChiTiet =
                chiTiet.getThuCung() != null ? chiTiet.getThuCung().getMaThuCung() : "";
        Object[] thu =
                dsThu.stream()
                        .filter(t -> String.valueOf(t[0]).equals(maThuChiTiet))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Không tìm thấy hồ sơ thú cưng."));
        String maChu = DanhMucService.layMaTuLuaChon(thu[8]);
        Object[] chu =
                DanhMucService.macDinh().layKhachHang().stream()
                        .filter(k -> String.valueOf(k[0]).equals(maChu))
                        .findFirst()
                        .orElseThrow(
                                () -> new IllegalArgumentException("Không tìm thấy khách hàng."));
        return new Object[] {
            hen.get().getMaLichHen(), chiTiet, chu, dsThu, nghiepVuService.layLuaChonGoiDichVu()
        };
    }

    @SuppressWarnings("unchecked")
    private void apDungNhanLichHen(Object[] duLieu) {
        String maHenMoi = (String) duLieu[0];
        LichHen chiTiet = (LichHen) duLieu[1];
        Object[] chu = (Object[]) duLieu[2];
        List<Object[]> dsThu = (List<Object[]>) duLieu[3];
        capNhatComboDichVuTuDanhSach((List<MucLuaChon>) duLieu[4]);
        chonKhach(
                chu,
                dsThu,
                chiTiet.getThuCung() != null ? chiTiet.getThuCung().getMaThuCung() : "");
        cboDichVu.setSelectedIndex(-1);
        String maGoi = chiTiet.getDichVu() != null ? chiTiet.getDichVu().getMaGoi() : "";
        for (int i = 0; i < cboDichVu.getItemCount(); i++)
            if (cboDichVu.getItemAt(i).giaTri().equals(maGoi)) cboDichVu.setSelectedIndex(i);
        if (cboDichVu.getSelectedItem() == null) {
            HopThoai.hienThiCanhBao(this, "Dịch vụ của lịch hẹn không còn hoạt động.");
            return;
        }
        maHen = maHenMoi;
        nhanNguon.setText("Từ lịch hẹn: " + maHen + " — vẫn cần kiểm tra và cân lại tại quầy");
        cboThuCung.setEnabled(false);
        cboDichVu.setEnabled(false);
        oCanNang.setText("");
        oGhiChu.setText("");
        oYeuCau.setText("");
        cboKiemTraDa.setSelectedIndex(0);
        cboKiemTraVe.setSelectedIndex(0);
        cboKiemTraMatTai.setSelectedIndex(0);
        cboKiemTraLong.setSelectedIndex(0);
        cboKetLuanTiepNhan.setSelectedIndex(0);
        capNhatDanhGiaHoSo(false);
    }

    private double canNang() {
        try {
            double cn = Double.parseDouble(oCanNang.getText().trim().replace(',', '.'));
            if (!Double.isFinite(cn) || cn <= 0)
                throw new IllegalArgumentException("Cân nặng phải lớn hơn 0, ví dụ 5,2.");
            return cn;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cân nặng phải là số, ví dụ 5,2.");
        }
    }

    private void capNhatGiaTuDong() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        String cnStr = oCanNang.getText().trim().replace(',', '.');
        if (thu == null || goi == null || cnStr.isEmpty()) {
            nhanGia.setText("Chọn thú cưng, dịch vụ và nhập cân nặng để tự động tính giá");
            return;
        }
        double cn;
        try {
            cn = Double.parseDouble(cnStr);
        } catch (NumberFormatException ex) {
            nhanGia.setText("Cân nặng phải là số, ví dụ 5,2");
            return;
        }
        if (cn <= 0) {
            nhanGia.setText("Cân nặng phải lớn hơn 0");
            return;
        }
        final int luot = ++luotGia;
        String maThu = thu.giaTri();
        String maGoi = goi.giaTri();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> luot == luotGia,
                () -> lapPhieuSpaService.baoGia(maThu, maGoi, cn),
                null,
                gia -> nhanGia.setText(DinhDang.dinhDang(gia)),
                ex -> nhanGia.setText(TacVuNen.thongDiepLoi(ex)));
    }

    private JButton nut(String nhan, Runnable xuLy) {
        JButton nut =
                new NutDinhDang(
                        nhan,
                        nhan.equals("Tạo phiếu tiếp nhận")
                                ? NutDinhDang.KieuNut.CHINH
                                : NutDinhDang.KieuNut.PHU);
        nut.addActionListener(e -> anToan(xuLy));
        return nut;
    }

    private void anToan(Runnable xuLy) {
        try {
            xuLy.run();
        } catch (RuntimeException loi) {
            HopThoai.hienThiCanhBao(this, loi.getMessage());
        }
    }

    private String soDienThoai() {
        return oSoDienThoai.getText().replaceAll("[\\s.]", "");
    }

    private void timKhach() {
        if (!maHen.isBlank())
            throw new IllegalArgumentException(
                    "Nhấn Lập phiếu mới để chuyển sang khách đến trực tiếp.");
        chonKhach(null);
        String tuKhoa = oSoDienThoai.getText() == null ? "" : oSoDienThoai.getText().trim();
        if (tuKhoa.isEmpty())
            throw new IllegalArgumentException(
                    "Nhập tên, mã khách hàng hoặc số điện thoại để tìm.");
        String tuKhoaThuong = tuKhoa.toLowerCase(Locale.ROOT);
        String tuKhoaSo = tuKhoa.replaceAll("[\\s.]", "").toLowerCase(Locale.ROOT);
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () ->
                        DanhMucService.macDinh().layKhachHang().stream()
                                .filter(k -> khopKhachHang(k, tuKhoaThuong, tuKhoaSo))
                                .toList(),
                null,
                ketQua -> xuLyKetQuaTimKhach(ketQua),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private static boolean khopKhachHang(Object[] khach, String tuKhoaThuong, String tuKhoaSo) {
        String ma = String.valueOf(khach[0]).toLowerCase(Locale.ROOT);
        String ten = String.valueOf(khach[1]).toLowerCase(Locale.ROOT);
        String sdt = String.valueOf(khach[2]).replaceAll("[\\s.]", "").toLowerCase(Locale.ROOT);
        return ma.contains(tuKhoaThuong)
                || ten.contains(tuKhoaThuong)
                || (!tuKhoaSo.isEmpty() && sdt.contains(tuKhoaSo));
    }

    private void xuLyKetQuaTimKhach(List<Object[]> ketQua) {
        if (ketQua.isEmpty()) {
            if (HopThoai.xacNhan(this, "Chưa có khách hàng phù hợp. Thêm khách hàng mới?")) {
                themKhach();
            }
            return;
        }
        List<Object[]> hoatDong = ketQua.stream().filter(k -> laDangHoatDong(k[8])).toList();
        if (hoatDong.isEmpty()) {
            HopThoai.hienThiCanhBao(
                    this,
                    "Hồ sơ khách hàng tìm thấy đã ngừng hoạt động; cần khôi phục tại danh mục.");
            return;
        }
        if (hoatDong.size() == 1) {
            chonKhach(hoatDong.get(0));
            return;
        }
        Object[] daChon = chonTuNhieuKhach(hoatDong);
        if (daChon != null) chonKhach(daChon);
    }

    private Object[] chonTuNhieuKhach(List<Object[]> hoatDong) {
        LuaChon[] muc = new LuaChon[hoatDong.size()];
        for (int i = 0; i < hoatDong.size(); i++) {
            Object[] k = hoatDong.get(i);
            muc[i] = new LuaChon(String.valueOf(k[0]), k[0] + " — " + k[1] + " (" + k[2] + ")");
        }
        Object chon =
                HopThoai.chonMuc(
                        this,
                        "Tìm thấy " + hoatDong.size() + " khách hàng phù hợp. Vui lòng chọn:",
                        "Chọn khách hàng",
                        muc,
                        muc[0]);
        if (!(chon instanceof LuaChon luaChon)) return null;
        return hoatDong.stream()
                .filter(k -> luaChon.giaTri().equals(String.valueOf(k[0])))
                .findFirst()
                .orElse(null);
    }

    private void themKhach() {
        if (!maHen.isBlank())
            throw new IllegalArgumentException("Nhấn Lập phiếu mới để tiếp nhận khách khác.");
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_KHACH_HANG);
        String sdtGoiY = soDienThoai().matches("\\d{1,15}") ? soDienThoai() : "";
        Object[] moi = DanhMucKhachHangPanel.moThem(this, sdtGoiY);
        if (moi != null) {
            if (!laDangHoatDong(moi[8]))
                throw new IllegalArgumentException(
                        "Khách vừa lưu đã ngừng hoạt động. Chọn hồ sơ đang hoạt động.");
            chonKhach(moi);
        }
    }

    private void chonKhach(Object[] khach) {
        khachHang = khach;
        nhanKhachHang.setText(
                khach == null
                        ? "Chưa chọn khách hàng"
                        : khach[0] + " - " + khach[1] + " | " + khach[2]);
        if (khach != null) oSoDienThoai.setText(String.valueOf(khach[2]));
        taiThuCung();
    }

    private void chonKhach(Object[] khach, List<Object[]> dsThu, String maThuChon) {
        khachHang = khach;
        nhanKhachHang.setText(
                khach == null
                        ? "Chưa chọn khách hàng"
                        : khach[0] + " - " + khach[1] + " | " + khach[2]);
        if (khach != null) oSoDienThoai.setText(String.valueOf(khach[2]));
        dienComboThuCung(dsThu, maThuChon);
    }

    private void taiThuCung() {
        LuaChon cu = (LuaChon) cboThuCung.getSelectedItem();
        if (khachHang == null) {
            cboThuCung.removeAllItems();
            return;
        }
        String maKhach = String.valueOf(khachHang[0]);
        final int luot = ++luotThuCung;
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () ->
                        luot == luotThuCung
                                && khachHang != null
                                && maKhach.equals(String.valueOf(khachHang[0])),
                () -> DanhMucService.macDinh().layThuCung(),
                null,
                dsThu -> dienComboThuCung(dsThu, cu != null ? cu.giaTri() : null),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private void dienComboThuCung(List<Object[]> dsThu, String maThuChon) {
        boQuaSuKienThuCung = true;
        try {
            cboThuCung.removeAllItems();
            if (khachHang == null) return;
            String maKhach = String.valueOf(khachHang[0]);
            for (Object[] thu : dsThu) {
                if (laDangHoatDong(thu[13])
                        && maKhach.equals(DanhMucService.layMaTuLuaChon(thu[8])))
                    cboThuCung.addItem(
                            new LuaChon(String.valueOf(thu[0]), thu[0] + " - " + thu[1]));
            }
            if (maThuChon != null) {
                for (int i = 0; i < cboThuCung.getItemCount(); i++) {
                    if (cboThuCung.getItemAt(i).giaTri().equals(maThuChon)) {
                        cboThuCung.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } finally {
            boQuaSuKienThuCung = false;
        }
        khiChonThuCung();
    }

    private void moHoSoThuCung() {
        if (khachHang == null)
            throw new IllegalArgumentException("Chọn khách hàng trước khi thêm thú cưng.");
        Object[] thuMoi = DanhMucThuCungPanel.moThem(this, String.valueOf(khachHang[0]));
        if (thuMoi != null) {
            String maThuMoi = String.valueOf(thuMoi[0]);
            var phien = PhienDangNhapService.macDinh().layPhienHienTai();
            TacVuNen.chayCoPhien(
                    phien,
                    () -> true,
                    () -> DanhMucService.macDinh().layThuCung(),
                    null,
                    dsThu -> dienComboThuCung(dsThu, maThuMoi),
                    ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
        }
    }

    private void moSoTiemThuCung() {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        if (thu == null) return;
        DanhMucThuCungPanel.moSua(this, thu.giaTri());
        capNhatDanhGiaHoSo(false);
    }

    private void capNhatDanhGiaHoSo(boolean canhBaoSom) {
        LuaChon thu = (LuaChon) cboThuCung.getSelectedItem();
        if (thu == null) {
            hoSoDuDieuKien = false;
            nhanHuyHieuHoSo.setText("CHƯA CHỌN THÚ CƯNG");
            nhanHuyHieuHoSo.setForeground(MauUngDung.VAN_BAN_XAM);
            nhanChiTietHoSo.setText(
                    "Chọn thú cưng bên trên để hệ thống tự động kiểm tra điều kiện tuổi và tiêm"
                            + " phòng.");
            khungTheHoSo.setBackground(MauUngDung.XAM_SIEU_NHAT);
            datVienKhungHoSo(MauUngDung.VIEN);
            btnCapNhatSoTiem.setEnabled(false);
            if (btnTaoPhieu != null) {
                btnTaoPhieu.setEnabled(true);
                btnTaoPhieu.setToolTipText(null);
            }
            return;
        }
        boolean coQuyenThuCung =
                PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.QUAN_LY_THU_CUNG);
        btnCapNhatSoTiem.setEnabled(coQuyenThuCung);
        btnCapNhatSoTiem.setToolTipText(
                coQuyenThuCung
                        ? null
                        : "Chỉ tài khoản có quyền Quản lý thú cưng mới được sửa sổ tiêm.");
        final int luot = ++luotHoSo;
        String maThu = thu.giaTri();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> luot == luotHoSo,
                () -> tiemPhongService.danhGiaNhanSpa(maThu),
                null,
                kq -> hienThiKetQuaHoSo(kq, canhBaoSom),
                ex -> {
                    hoSoDuDieuKien = false;
                    nhanChiTietHoSo.setText(TacVuNen.thongDiepLoi(ex));
                });
    }

    private void datVienKhungHoSo(Color mauVien) {
        khungTheHoSo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(mauVien, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
    }

    private void hienThiKetQuaHoSo(TiemPhongService.KetQuaTiemPhong kq, boolean canhBaoSom) {
        hoSoDuDieuKien = kq.duDieuKien;
        if (kq.duDieuKien) {
            nhanHuyHieuHoSo.setText("✓ ĐẠT ĐIỀU KIỆN HỒ SƠ TIÊM PHÒNG");
            nhanHuyHieuHoSo.setForeground(MauUngDung.mau(0x065F46));
            khungTheHoSo.setBackground(MauUngDung.mau(0xECFDF5));
            datVienKhungHoSo(MauUngDung.mau(0x10B981));
            nhanChiTietHoSo.setText(kq.thongDiep);
            if (btnTaoPhieu != null) {
                btnTaoPhieu.setEnabled(true);
                btnTaoPhieu.setToolTipText(null);
            }
        } else {
            boolean thieuHoSo = kq.soMui == 0;
            nhanHuyHieuHoSo.setText(
                    thieuHoSo
                            ? "✕ CHƯA ĐỦ ĐIỀU KIỆN SPA — CHƯA CÓ HỒ SƠ TIÊM PHÒNG"
                            : "✕ KHÔNG ĐỦ ĐIỀU KIỆN TIẾP NHẬN SPA");
            nhanHuyHieuHoSo.setForeground(MauUngDung.DO_THUONG_HIEU);
            khungTheHoSo.setBackground(MauUngDung.mau(0xFEF2F2));
            datVienKhungHoSo(MauUngDung.DO_THUONG_HIEU);
            nhanChiTietHoSo.setText(kq.thongDiep);
            if (btnTaoPhieu != null) {
                btnTaoPhieu.setEnabled(false);
                btnTaoPhieu.setToolTipText(kq.thongDiep);
            }
            if (canhBaoSom) {
                HopThoai.hienThiCanhBao(
                        this, "Thú cưng chưa đủ điều kiện làm Spa:\n" + kq.thongDiep);
            }
        }
        khungTheHoSo.revalidate();
        khungTheHoSo.repaint();
    }

    private void luu() {
        if (khachHang == null)
            throw new IllegalArgumentException("Tìm hoặc thêm khách hàng trước khi lưu phiếu.");
        String nhapHienTai = oSoDienThoai.getText() == null ? "" : oSoDienThoai.getText().trim();
        String sdtKhach = String.valueOf(khachHang[2]);
        if (!nhapHienTai.replaceAll("[\\s.]", "").equals(sdtKhach.replaceAll("[\\s.]", ""))
                && !nhapHienTai.equalsIgnoreCase(String.valueOf(khachHang[0]))
                && !nhapHienTai.equalsIgnoreCase(String.valueOf(khachHang[1])))
            throw new IllegalArgumentException(
                    "Thông tin khách hàng ở ô tìm kiếm đã thay đổi. Nhấn Tìm khách để chọn lại.");
        LuaChon thuCung = (LuaChon) cboThuCung.getSelectedItem();
        if (thuCung == null)
            throw new IllegalArgumentException(
                    "Khách chưa có thú cưng phù hợp. Bổ sung hồ sơ thú cưng trước.");
        if (cboKetLuanTiepNhan.getSelectedIndex() == 2)
            throw new IllegalArgumentException(
                    "Thú cưng không đủ điều kiện tiếp nhận thực tế tại quầy (bị từ chối do vết"
                            + " thương hở hoặc sức khỏe yếu).");
        LuaChon goi = (LuaChon) cboDichVu.getSelectedItem();
        if (goi == null) throw new IllegalArgumentException("Cần chọn dịch vụ.");
        double cn = canNang();
        String tinhTrangLuu = taoTinhTrangLuu();
        String yeuCau = oYeuCau.getText();
        String maThu = thuCung.giaTri();
        String maGoi = goi.giaTri();
        String maTreo = maPhieuTreo;
        String maHenLuu = maHen;
        if (btnTaoPhieu != null) btnTaoPhieu.setEnabled(false);
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> {
                    TiemPhongService.KetQuaTiemPhong kq = tiemPhongService.danhGiaNhanSpa(maThu);
                    if (!kq.duDieuKien) throw new IllegalArgumentException(kq.thongDiep);
                    return phieuSpaTreoService.taoPhieu(
                            maTreo, maThu, maGoi, maHenLuu, cn, tinhTrangLuu, yeuCau);
                },
                null,
                maPhieu -> {
                    if (btnTaoPhieu != null) btnTaoPhieu.setEnabled(true);
                    khachHang = null;
                    datLaiForm();
                    khiLuu.accept(maPhieu);
                },
                ex -> {
                    if (btnTaoPhieu != null) btnTaoPhieu.setEnabled(true);
                    HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex));
                });
    }

    private String taoTinhTrangLuu() {
        StringBuilder dg = new StringBuilder();
        if (cboKiemTraDa.getSelectedIndex() > 0)
            dg.append("Da/lông: ").append(cboKiemTraDa.getSelectedItem()).append("; ");
        if (cboKiemTraVe.getSelectedIndex() > 0)
            dg.append("Ký sinh: ").append(cboKiemTraVe.getSelectedItem()).append("; ");
        if (cboKiemTraMatTai.getSelectedIndex() > 0)
            dg.append("Mắt/tai: ").append(cboKiemTraMatTai.getSelectedItem()).append("; ");
        if (cboKiemTraLong.getSelectedIndex() > 0)
            dg.append("Độ rối lông: ").append(cboKiemTraLong.getSelectedItem()).append("; ");
        dg.append("Kết luận quầy: ").append(cboKetLuanTiepNhan.getSelectedItem());
        String ghiChuNhap = oGhiChu.getText().trim();
        return ghiChuNhap.isEmpty() ? dg.toString() : dg + " | " + ghiChuNhap;
    }
}
