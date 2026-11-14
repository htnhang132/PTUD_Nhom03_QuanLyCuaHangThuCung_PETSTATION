package com.petstation.gui.spa;

import com.petstation.dto.MucLuaChon;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.gui.chung.thanhphan.BoChonNgayGio;
import com.petstation.gui.khachhang.DanhMucKhachHangPanel;
import com.petstation.gui.khachhang.DanhMucThuCungPanel;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.spa.SpaService;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public final class DatLichSpaDialog {
    private final Component parent;
    private final JDialog hopThoai;
    private final JTextField oTimKhach;
    private final JComboBox<BieuMauTruong.LuaChon> cboKhachHang;
    private final JComboBox<BieuMauTruong.LuaChon> cboThuCung;
    private final JComboBox<BieuMauTruong.LuaChon> cboGoiDichVu;
    private final JComboBox<BieuMauTruong.LuaChon> cboNhanVien;
    private final BoChonNgayGio spThoiGian;
    private final JTextField oGhiChu;
    private final JButton btnThemKhach;
    private final JButton btnThemThuCung;
    private final NutDinhDang btnLuu;
    private final NutDinhDang btnHuy;
    private final Runnable onThanhCong;
    private List<BieuMauTruong.LuaChon> tatCaKhachHang = List.of();
    private List<Object[]> tatCaThuCung = List.of();

    public static void mo(Component parent, Runnable onThanhCong) {
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                DatLichSpaDialog::taiDuLieuBanDau,
                null,
                duLieu ->
                        new DatLichSpaDialog(parent, onThanhCong, duLieu).hopThoai.setVisible(true),
                ex -> HopThoai.hienThiCanhBao(parent, TacVuNen.thongDiepLoi(ex)));
    }

    private static Object[] taiDuLieuBanDau() {
        List<BieuMauTruong.LuaChon> dsKhach = taiDanhSachKhach();
        List<Object[]> dsThu = DanhMucService.macDinh().layThuCung();
        NghiepVuService nghiepVuService = new NghiepVuService();
        return new Object[] {
            dsKhach,
            dsThu,
            nghiepVuService.layLuaChonGoiDichVu(),
            nghiepVuService.layLuaChonNhanVien()
        };
    }

    private static List<BieuMauTruong.LuaChon> taiDanhSachKhach() {
        return DanhMucService.macDinh().layKhachHang().stream()
                .filter(k -> "Đang hoạt động".equals(k[8]))
                .map(
                        k ->
                                new BieuMauTruong.LuaChon(
                                        String.valueOf(k[0]),
                                        k[0] + " — " + k[1] + " (" + k[2] + ")"))
                .toList();
    }

    private DatLichSpaDialog(Component parent, Runnable onThanhCong, Object[] duLieu) {
        this.parent = parent;
        this.onThanhCong = onThanhCong;
        this.hopThoai =
                new JDialog(
                        SwingUtilities.getWindowAncestor(parent),
                        "Đặt lịch hẹn dịch vụ Spa",
                        Dialog.ModalityType.APPLICATION_MODAL);
        oTimKhach = new JTextField(12);
        oTimKhach.putClientProperty("JTextField.placeholderText", "Tìm tên, SĐT...");
        oTimKhach.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        oTimKhach.setPreferredSize(new Dimension(140, 32));
        cboKhachHang = new JComboBox<>();
        cboThuCung = new JComboBox<>();
        cboGoiDichVu = new JComboBox<>();
        cboNhanVien = new JComboBox<>();
        oGhiChu = new JTextField();
        btnThemKhach = new NutDinhDang("+ Thêm khách", NutDinhDang.KieuNut.PHU);
        btnThemKhach.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnThemKhach.setPreferredSize(new Dimension(110, 32));
        btnThemThuCung = new NutDinhDang("+ Thêm thú cưng", NutDinhDang.KieuNut.PHU);
        btnThemThuCung.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnThemThuCung.setPreferredSize(new Dimension(125, 32));
        spThoiGian =
                new BoChonNgayGio(LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0));
        btnLuu = new NutDinhDang("Đặt lịch hẹn", NutDinhDang.KieuNut.CHINH);
        btnHuy = new NutDinhDang("Hủy bỏ (ESC)", NutDinhDang.KieuNut.PHU);
        napDuLieu(duLieu);
        thietKeGiaoDien();
        caiDatSuKien();
    }

    @SuppressWarnings("unchecked")
    private void napDuLieu(Object[] duLieu) {
        tatCaKhachHang = (List<BieuMauTruong.LuaChon>) duLieu[0];
        tatCaThuCung = (List<Object[]>) duLieu[1];
        locKhachHang(oTimKhach.getText());
        cboGoiDichVu.removeAllItems();
        for (MucLuaChon g : (List<MucLuaChon>) duLieu[2]) {
            cboGoiDichVu.addItem(new BieuMauTruong.LuaChon(g.giaTri(), g.nhan()));
        }
        cboNhanVien.removeAllItems();
        for (MucLuaChon nv : (List<MucLuaChon>) duLieu[3]) {
            cboNhanVien.addItem(new BieuMauTruong.LuaChon(nv.giaTri(), nv.nhan()));
        }
        capNhatDanhSachThuCung();
    }

    private void locKhachHang(String tuKhoa) {
        String k = tuKhoa == null ? "" : tuKhoa.trim().toLowerCase();
        BieuMauTruong.LuaChon dangChon = (BieuMauTruong.LuaChon) cboKhachHang.getSelectedItem();
        cboKhachHang.removeAllItems();
        List<BieuMauTruong.LuaChon> hopLe =
                tatCaKhachHang.stream()
                        .filter(item -> k.isEmpty() || item.nhan().toLowerCase().contains(k))
                        .toList();
        for (var khach : hopLe) {
            cboKhachHang.addItem(khach);
        }
        if (dangChon != null) {
            for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
                if (cboKhachHang.getItemAt(i).giaTri().equals(dangChon.giaTri())) {
                    cboKhachHang.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (cboKhachHang.getSelectedIndex() < 0 && !hopLe.isEmpty()) {
            cboKhachHang.setSelectedIndex(0);
        }
        capNhatDanhSachThuCung();
    }

    private void capNhatDanhSachThuCung() {
        cboThuCung.removeAllItems();
        BieuMauTruong.LuaChon khachDangChon =
                (BieuMauTruong.LuaChon) cboKhachHang.getSelectedItem();
        if (khachDangChon == null) {
            btnThemThuCung.setEnabled(false);
            return;
        }
        btnThemThuCung.setEnabled(true);
        String maKhach = khachDangChon.giaTri();
        var dsThu =
                tatCaThuCung.stream()
                        .filter(
                                t ->
                                        "Đang hoạt động".equals(t[13])
                                                && maKhach.equals(
                                                        DanhMucService.layMaTuLuaChon(t[8])))
                        .map(
                                t ->
                                        new BieuMauTruong.LuaChon(
                                                String.valueOf(t[0]),
                                                t[0] + " — " + t[1] + " (" + t[2] + ")"))
                        .toList();
        if (dsThu.isEmpty()) {
            cboThuCung.addItem(
                    new BieuMauTruong.LuaChon("", "(Khách chưa có thú cưng - Bấm Thêm thú cưng)"));
        } else {
            for (var thu : dsThu) {
                cboThuCung.addItem(thu);
            }
        }
    }

    private void thietKeGiaoDien() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        root.setBackground(MauUngDung.NEN_KEM);
        root.add(taoPhanDauHopThoai(), BorderLayout.NORTH);
        root.add(taoBieuMauDatLich(), BorderLayout.CENTER);
        root.add(taoChanHopThoai(), BorderLayout.SOUTH);
        hopThoai.setContentPane(root);
        hopThoai.pack();
        hopThoai.setSize(Math.max(hopThoai.getWidth(), 680), 460);
        HopThoai.chuanHoaCuaSo(hopThoai, parent);
    }

    private JPanel taoPhanDauHopThoai() {
        JPanel khungTieuDe = new JPanel(new BorderLayout(0, 4));
        khungTieuDe.setOpaque(false);
        JLabel nhanTieuDe = new JLabel("Đặt lịch hẹn dịch vụ Spa");
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        nhanTieuDe.setForeground(MauUngDung.NAU);
        JLabel nhanPhu =
                new JLabel("Chọn khách hàng, thú cưng và gói dịch vụ để lên lịch tiếp đón");
        nhanPhu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanPhu.setForeground(MauUngDung.VAN_BAN_XAM);
        khungTieuDe.add(nhanTieuDe, BorderLayout.NORTH);
        khungTieuDe.add(nhanPhu, BorderLayout.CENTER);
        return khungTieuDe;
    }

    private BoGocPanel taoBieuMauDatLich() {
        BoGocPanel khungBieuMauNhap = new BoGocPanel(14, MauUngDung.THE_NEN);
        khungBieuMauNhap.setLayout(new GridBagLayout());
        khungBieuMauNhap.setBorder(new EmptyBorder(16, 18, 16, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel khungChonKhach = new JPanel(new BorderLayout(8, 0));
        khungChonKhach.setOpaque(false);
        khungChonKhach.add(oTimKhach, BorderLayout.WEST);
        khungChonKhach.add(cboKhachHang, BorderLayout.CENTER);
        themHangCoNutPhu(khungBieuMauNhap, gbc, 0, "Khách hàng:", khungChonKhach, btnThemKhach);
        themHangCoNutPhu(khungBieuMauNhap, gbc, 1, "Thú cưng:", cboThuCung, btnThemThuCung);
        themHangRong(khungBieuMauNhap, gbc, 2, "Gói dịch vụ Spa:", cboGoiDichVu);
        themHangRong(khungBieuMauNhap, gbc, 3, "Thợ phụ trách:", cboNhanVien);
        themHangRong(khungBieuMauNhap, gbc, 4, "Thời gian hẹn:", spThoiGian);
        themHangRong(khungBieuMauNhap, gbc, 5, "Ghi chú:", oGhiChu);
        return khungBieuMauNhap;
    }

    private void themHangCoNutPhu(
            JPanel khungTam,
            GridBagConstraints gbc,
            int dong,
            String nhan,
            JComponent truong,
            JComponent nutPhu) {
        gbc.gridy = dong;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        khungTam.add(taoNhan(nhan), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        khungTam.add(truong, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        khungTam.add(nutPhu, gbc);
    }

    private void themHangRong(
            JPanel khungTam, GridBagConstraints gbc, int dong, String nhan, JComponent truong) {
        gbc.gridy = dong;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        khungTam.add(taoNhan(nhan), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        khungTam.add(truong, gbc);
        gbc.gridwidth = 1;
    }

    private JPanel taoChanHopThoai() {
        JPanel khungDuoi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        khungDuoi.setOpaque(false);
        khungDuoi.add(btnHuy);
        khungDuoi.add(btnLuu);
        return khungDuoi;
    }

    private JLabel taoNhan(String tieuDe) {
        JLabel nhanChu = new JLabel(tieuDe);
        nhanChu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanChu.setForeground(MauUngDung.VAN_BAN_TOI);
        return nhanChu;
    }

    private void caiDatSuKien() {
        CoSoPanel.themBoLangNgheTaiLieu(oTimKhach, () -> locKhachHang(oTimKhach.getText()));
        cboKhachHang.addActionListener(e -> capNhatDanhSachThuCung());
        btnThemKhach.addActionListener(
                e -> {
                    Object[] khachMoi = DanhMucKhachHangPanel.moThem(hopThoai, "");
                    if (khachMoi != null) {
                        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
                        TacVuNen.chayCoPhien(
                                phien,
                                hopThoai::isDisplayable,
                                DatLichSpaDialog::taiDanhSachKhach,
                                null,
                                dsKhach -> {
                                    tatCaKhachHang = dsKhach;
                                    oTimKhach.setText("");
                                    locKhachHang("");
                                    String maMoi = String.valueOf(khachMoi[0]);
                                    for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
                                        if (cboKhachHang.getItemAt(i).giaTri().equals(maMoi)) {
                                            cboKhachHang.setSelectedIndex(i);
                                            break;
                                        }
                                    }
                                },
                                ex -> HopThoai.hienThiCanhBao(hopThoai, TacVuNen.thongDiepLoi(ex)));
                    }
                });
        btnThemThuCung.addActionListener(
                e -> {
                    BieuMauTruong.LuaChon khachDangChon =
                            (BieuMauTruong.LuaChon) cboKhachHang.getSelectedItem();
                    if (khachDangChon == null || khachDangChon.giaTri().isBlank()) {
                        HopThoai.hienThiCanhBao(
                                hopThoai, "Vui lòng chọn khách hàng trước khi thêm thú cưng.");
                        return;
                    }
                    Object[] thuMoi = DanhMucThuCungPanel.moThem(hopThoai, khachDangChon.giaTri());
                    if (thuMoi != null) {
                        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
                        TacVuNen.chayCoPhien(
                                phien,
                                hopThoai::isDisplayable,
                                () -> DanhMucService.macDinh().layThuCung(),
                                null,
                                dsThu -> {
                                    tatCaThuCung = dsThu;
                                    capNhatDanhSachThuCung();
                                    String maThuMoi = String.valueOf(thuMoi[0]);
                                    for (int i = 0; i < cboThuCung.getItemCount(); i++) {
                                        if (cboThuCung.getItemAt(i).giaTri().equals(maThuMoi)) {
                                            cboThuCung.setSelectedIndex(i);
                                            break;
                                        }
                                    }
                                },
                                ex -> HopThoai.hienThiCanhBao(hopThoai, TacVuNen.thongDiepLoi(ex)));
                    }
                });
        btnLuu.addActionListener(e -> xuLyLuu());
        btnHuy.addActionListener(e -> hopThoai.dispose());
    }

    private void xuLyLuu() {
        try {
            BieuMauTruong.LuaChon khach = (BieuMauTruong.LuaChon) cboKhachHang.getSelectedItem();
            BieuMauTruong.LuaChon thu = (BieuMauTruong.LuaChon) cboThuCung.getSelectedItem();
            BieuMauTruong.LuaChon goi = (BieuMauTruong.LuaChon) cboGoiDichVu.getSelectedItem();
            BieuMauTruong.LuaChon tho = (BieuMauTruong.LuaChon) cboNhanVien.getSelectedItem();
            if (khach == null || khach.giaTri().isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn khách hàng.");
            }
            if (thu == null || thu.giaTri().isBlank()) {
                throw new IllegalArgumentException(
                        "Vui lòng chọn thú cưng của khách (hoặc bấm + Thêm thú cưng).");
            }
            if (goi == null || goi.giaTri().isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn gói dịch vụ Spa.");
            }
            if (tho == null || tho.giaTri().isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn thợ phụ trách.");
            }
            LocalDateTime batDau = spThoiGian.layGiaTri();
            if (batDau.isBefore(LocalDateTime.now().withSecond(0).withNano(0))) {
                throw new IllegalArgumentException("Giờ hẹn không được ở quá khứ.");
            }
            btnLuu.setEnabled(false);
            var phien = PhienDangNhapService.macDinh().layPhienHienTai();
            TacVuNen.chayCoPhien(
                    phien,
                    hopThoai::isDisplayable,
                    () -> {
                        new SpaService()
                                .datLichHen(thu.giaTri(), goi.giaTri(), tho.giaTri(), batDau);
                        return null;
                    },
                    null,
                    v -> {
                        HopThoai.hienThiThanhCong(
                                hopThoai, "Đã đặt lịch hẹn Spa thành công cho thú cưng.");
                        if (onThanhCong != null) {
                            onThanhCong.run();
                        }
                        hopThoai.dispose();
                    },
                    ex -> {
                        btnLuu.setEnabled(true);
                        HopThoai.hienThiCanhBao(hopThoai, TacVuNen.thongDiepLoi(ex));
                    });
        } catch (RuntimeException ex) {
            btnLuu.setEnabled(true);
            HopThoai.hienThiCanhBao(hopThoai, ex.getMessage());
        }
    }
}
