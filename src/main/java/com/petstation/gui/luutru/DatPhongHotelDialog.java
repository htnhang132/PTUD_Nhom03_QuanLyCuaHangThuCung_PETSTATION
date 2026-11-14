package com.petstation.gui.luutru;

import com.petstation.dto.HotelDTO;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.gui.chung.thanhphan.BoChonNgayGio;
import com.petstation.service.chung.NghiepVuService;
import com.petstation.service.luutru.LuuTruService;
import com.petstation.util.DinhDang;

import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public final class DatPhongHotelDialog {
    private final LuuTruService luuTruService = new LuuTruService();
    private final String maHopDong;
    private final JDialog hopThoai;
    private final JComboBox<BieuMauTruong.LuaChon> cboThuCung;
    private final JComboBox<BieuMauTruong.LuaChon> cboPhong;
    private final BoChonNgayGio spNgayVao;
    private final BoChonNgayGio spNgayRa;
    private final JTextArea oGhiChu = new JTextArea(3, 24);
    private final JLabel nhanSoNgay = new JLabel("—");
    private final JLabel nhanTongTien = new JLabel("—");
    private final JLabel nhanCoc = new JLabel("—");
    private final JLabel nhanTrangThai = new JLabel(" ");
    private final NutDinhDang btnLuu = new NutDinhDang("Lưu đặt phòng", NutDinhDang.KieuNut.CHINH);
    private boolean dangTaiDuLieu = true;
    private int luotDuKien;
    private String ketQua;

    private static final class DuLieuKhoiTao {
        final List<BieuMauTruong.LuaChon> thuCung;
        final List<BieuMauTruong.LuaChon> phong;
        final HotelDTO.ThongTinDatPhong thongTinSua;

        DuLieuKhoiTao(
                List<BieuMauTruong.LuaChon> thuCung,
                List<BieuMauTruong.LuaChon> phong,
                HotelDTO.ThongTinDatPhong thongTinSua) {
            this.thuCung = thuCung;
            this.phong = phong;
            this.thongTinSua = thongTinSua;
        }
    }

    private static String dinhDangTenPhong(String raw) {
        if (raw == null) return "";
        var m = Pattern.compile("^(.*?/\\s*)(\\d+(?:\\.\\d+)?)(.*)$").matcher(raw);
        if (m.find()) {
            try {
                long gia = Math.round(Double.parseDouble(m.group(2)));
                return m.group(1)
                        + DinhDang.dinhDang(gia)
                        + " "
                        + m.group(3).replaceAll("^\\s*/\\s*", "/ ");
            } catch (RuntimeException ex) {
                LoggerFactory.getLogger(DatPhongHotelDialog.class)
                        .debug("Giữ nguyên nhãn phòng '{}': {}", raw, ex.getMessage());
            }
        }
        return raw;
    }

    private DatPhongHotelDialog(Component parent, String maHopDong, String maPhongMacDinh) {
        this.maHopDong = maHopDong != null && !maHopDong.isBlank() ? maHopDong : null;
        this.hopThoai =
                new JDialog(
                        SwingUtilities.getWindowAncestor(parent),
                        this.maHopDong == null ? "Đặt phòng Pet Hotel" : "Sửa đặt phòng Pet Hotel",
                        Dialog.ModalityType.APPLICATION_MODAL);
        cboThuCung = new JComboBox<>();
        cboPhong = new JComboBox<>();
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        spNgayVao = new BoChonNgayGio(now);
        spNgayRa = new BoChonNgayGio(now.plusDays(1));
        btnLuu.setEnabled(false);
        nhanTrangThai.setText("Đang tải dữ liệu phòng và thú cưng...");
        taoGiaoDien();
        cboThuCung.addActionListener(e -> capNhatDuKien());
        cboPhong.addActionListener(e -> capNhatDuKien());
        spNgayVao.addChangeListener(this::capNhatDuKien);
        spNgayRa.addChangeListener(this::capNhatDuKien);
        hopThoai.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        hopThoai.setSize(680, 590);
        hopThoai.setMinimumSize(new Dimension(620, 530));
        HopThoai.chuanHoaCuaSo(hopThoai, hopThoai.getOwner());
        taiDuLieuBanDau(maPhongMacDinh);
    }

    private void taiDuLieuBanDau(String maPhongMacDinh) {
        TacVuNen.chayCoPhien(
                hopThoai,
                () -> true,
                () -> {
                    NghiepVuService nghiepVuService = new NghiepVuService();
                    List<BieuMauTruong.LuaChon> pets =
                            nghiepVuService.layLuaChonThuCung().stream()
                                    .map(d -> new BieuMauTruong.LuaChon(d.giaTri(), d.nhan()))
                                    .toList();
                    List<BieuMauTruong.LuaChon> rooms =
                            nghiepVuService.layLuaChonPhong().stream()
                                    .map(
                                            d ->
                                                    new BieuMauTruong.LuaChon(
                                                            d.giaTri(), dinhDangTenPhong(d.nhan())))
                                    .toList();
                    HotelDTO.ThongTinDatPhong thongTin =
                            maHopDong != null ? luuTruService.layThongTinDatPhong(maHopDong) : null;
                    return new DuLieuKhoiTao(pets, rooms, thongTin);
                },
                duLieu -> apDungDuLieuBanDau(duLieu, maPhongMacDinh),
                ex -> {
                    nhanTrangThai.setText(ex.getMessage());
                    HopThoai.hienThiCanhBao(hopThoai, ex.getMessage());
                    hopThoai.dispose();
                });
    }

    private void apDungDuLieuBanDau(DuLieuKhoiTao duLieu, String maPhongMacDinh) {
        if (duLieu.thuCung.isEmpty()) {
            HopThoai.hienThiCanhBao(hopThoai, "Chưa có thú cưng đang hoạt động để đặt lưu trú.");
            hopThoai.dispose();
            return;
        }
        if (duLieu.phong.isEmpty()) {
            HopThoai.hienThiCanhBao(hopThoai, "Không có phòng đang sẵn sàng nhận đặt chỗ.");
            hopThoai.dispose();
            return;
        }
        duLieu.thuCung.forEach(cboThuCung::addItem);
        duLieu.phong.forEach(cboPhong::addItem);
        if (duLieu.thongTinSua != null) {
            apDungDuLieuSua(duLieu.thongTinSua);
        } else {
            chonGiaTri(cboPhong, maPhongMacDinh);
            nhanTrangThai.setText(" ");
        }
        dangTaiDuLieu = false;
        capNhatDuKien();
    }

    public static String mo(Component parent, String maHopDong, String maPhongMacDinh) {
        DatPhongHotelDialog form = new DatPhongHotelDialog(parent, maHopDong, maPhongMacDinh);
        form.hopThoai.setVisible(true);
        return form.ketQua;
    }

    private void taoGiaoDien() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(MauUngDung.mau(0xFAF6F0));
        root.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel title = new JLabel(maHopDong == null ? "THÔNG TIN ĐẶT PHÒNG" : "CẬP NHẬT ĐẶT PHÒNG");
        title.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        title.setForeground(MauUngDung.NAU);
        root.add(title, BorderLayout.NORTH);

        // Khung thông tin đặt phòng - Thẻ trắng bo góc viền mảnh
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.KEM_VIEN_NHE, 1, true),
                        new EmptyBorder(16, 16, 16, 16)));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(7, 6, 7, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        int dong = 0;
        themDong(form, gc, dong++, "Thú cưng *", cboThuCung);
        themDong(form, gc, dong++, "Phòng *", cboPhong);
        themDong(form, gc, dong++, "Ngày/giờ nhận phòng *", spNgayVao);
        themDong(form, gc, dong++, "Ngày/giờ trả phòng *", spNgayRa);
        oGhiChu.setLineWrap(true);
        oGhiChu.setWrapStyleWord(true);
        JScrollPane scrGhiChu = new JScrollPane(oGhiChu);
        scrGhiChu.setBorder(BorderFactory.createLineBorder(MauUngDung.KEM_VIEN));
        themDong(form, gc, dong++, "Ghi chú", scrGhiChu);

        // Khung dự kiến chi phí - Thẻ trắng bo góc viền mảnh (bỏ TitledBorder mặc định)
        JPanel theDuKien = taoKhungDuKienChiPhi();
        JPanel khungDuoi = taoPhanDuoiHopThoai(theDuKien);
        JPanel khungGiua = new JPanel(new BorderLayout(0, 10));
        khungGiua.setOpaque(false);
        khungGiua.add(form, BorderLayout.NORTH);
        khungGiua.add(khungDuoi, BorderLayout.CENTER);
        JScrollPane scrChinh = new JScrollPane(khungGiua);
        scrChinh.setBorder(null);
        scrChinh.setOpaque(false);
        scrChinh.getViewport().setOpaque(false);
        scrChinh.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scrChinh, BorderLayout.CENTER);
        hopThoai.setContentPane(root);
        hopThoai.getRootPane().setDefaultButton(btnLuu);
        hopThoai.getRootPane()
                .registerKeyboardAction(
                        e -> hopThoai.dispose(),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private JPanel taoKhungDuKienChiPhi() {
        JPanel theDuKien = new JPanel(new BorderLayout(0, 10));
        theDuKien.setBackground(Color.WHITE);
        theDuKien.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.KEM_VIEN_NHE, 1, true),
                        new EmptyBorder(14, 16, 14, 16)));
        JLabel nhanTieuDePhi = new JLabel("Dự kiến chi phí");
        nhanTieuDePhi.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanTieuDePhi.setForeground(MauUngDung.NAU);
        theDuKien.add(nhanTieuDePhi, BorderLayout.NORTH);
        JPanel luoiDuKien = new JPanel(new GridLayout(3, 2, 16, 8));
        luoiDuKien.setOpaque(false);
        Color mauNhanPhi = MauUngDung.mau(0x57534E);
        themDongChiPhi(
                luoiDuKien,
                "Số ngày tính phí:",
                mauNhanPhi,
                Font.PLAIN,
                nhanSoNgay,
                13,
                MauUngDung.VAN_BAN_TOI);
        themDongChiPhi(
                luoiDuKien,
                "Tổng tiền phòng:",
                mauNhanPhi,
                Font.PLAIN,
                nhanTongTien,
                14,
                MauUngDung.VAN_BAN_TOI);
        themDongChiPhi(
                luoiDuKien,
                "Tiền cọc yêu cầu (50%):",
                MauUngDung.DO_SAU,
                Font.BOLD,
                nhanCoc,
                16,
                MauUngDung.DO_NHAN_MANH);
        theDuKien.add(luoiDuKien, BorderLayout.CENTER);
        return theDuKien;
    }

    private static void themDongChiPhi(
            JPanel luoi,
            String nhan,
            Color mauNhan,
            int kieuNhan,
            JLabel nhanGiaTri,
            int coChuGiaTri,
            Color mauGiaTri) {
        JLabel nhanChu = new JLabel(nhan);
        nhanChu.setForeground(mauNhan);
        nhanChu.setFont(MauUngDung.taoPhongChu(kieuNhan, 13));
        nhanGiaTri.setHorizontalAlignment(SwingConstants.RIGHT);
        nhanGiaTri.setFont(MauUngDung.taoPhongChu(Font.BOLD, coChuGiaTri));
        nhanGiaTri.setForeground(mauGiaTri);
        luoi.add(nhanChu);
        luoi.add(nhanGiaTri);
    }

    private JPanel taoPhanDuoiHopThoai(JPanel theDuKien) {
        JPanel khungDuoi = new JPanel(new BorderLayout(8, 10));
        khungDuoi.setOpaque(false);
        khungDuoi.add(theDuKien, BorderLayout.NORTH);
        nhanTrangThai.setForeground(MauUngDung.mau(0x962823));
        nhanTrangThai.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        khungDuoi.add(nhanTrangThai, BorderLayout.CENTER);

        JPanel khungThaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        khungThaoTac.setOpaque(false);
        NutDinhDang btnHuy = new NutDinhDang("Đóng", NutDinhDang.KieuNut.PHU);
        btnHuy.setPreferredSize(new Dimension(90, 36));
        btnHuy.addActionListener(e -> hopThoai.dispose());
        btnLuu.setPreferredSize(new Dimension(140, 36));
        btnLuu.addActionListener(e -> luu());
        khungThaoTac.add(btnHuy);
        khungThaoTac.add(btnLuu);
        khungDuoi.add(khungThaoTac, BorderLayout.SOUTH);
        return khungDuoi;
    }

    private static void themDong(
            JPanel khungBang, GridBagConstraints gc, int dong, String nhan, Component thanhPhan) {
        gc.gridy = dong;
        gc.gridx = 0;
        gc.weightx = 0;
        gc.gridwidth = 1;
        khungBang.add(new JLabel(nhan), gc);
        gc.gridx = 1;
        gc.weightx = 1;
        khungBang.add(thanhPhan, gc);
    }

    private static boolean chonGiaTri(JComboBox<BieuMauTruong.LuaChon> hopChon, String giaTri) {
        if (giaTri == null || giaTri.isBlank()) return false;
        for (int i = 0; i < hopChon.getItemCount(); i++) {
            if (hopChon.getItemAt(i).giaTri().equals(giaTri)) {
                hopChon.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    private static void chonHoacThemGiaTriCu(
            JComboBox<BieuMauTruong.LuaChon> hopChon, String giaTri, String nhan) {
        if (!chonGiaTri(hopChon, giaTri)) {
            hopChon.addItem(new BieuMauTruong.LuaChon(giaTri, nhan));
            chonGiaTri(hopChon, giaTri);
        }
    }

    private String giaTriDangChon(JComboBox<BieuMauTruong.LuaChon> hopChon) {
        Object mucChon = hopChon.getSelectedItem();
        return mucChon instanceof BieuMauTruong.LuaChon luaChon ? luaChon.giaTri() : "";
    }

    private void apDungDuLieuSua(HotelDTO.ThongTinDatPhong duLieu) {
        String thuCungCu =
                duLieu.getThuCung() != null && duLieu.getThuCung().getMaThuCung() != null
                        ? duLieu.getThuCung().getMaThuCung()
                        : "";
        String phongCu =
                duLieu.getPhong() != null && duLieu.getPhong().getMaPhong() != null
                        ? duLieu.getPhong().getMaPhong()
                        : "";
        chonHoacThemGiaTriCu(
                cboThuCung,
                thuCungCu,
                thuCungCu + " - Thú cưng đang gắn với hợp đồng (cần kiểm tra trạng thái)");
        chonHoacThemGiaTriCu(
                cboPhong, phongCu, phongCu + " - Phòng hiện tại (cần kiểm tra trạng thái)");
        spNgayVao.datGiaTri(duLieu.getNgayVao());
        spNgayRa.datGiaTri(duLieu.getNgayRaDuKien());
        oGhiChu.setText(duLieu.getGhiChu() == null ? "" : duLieu.getGhiChu());

        long daThu = duLieu.isDaNhanCoc() ? duLieu.getTienCoc() : 0;
        if (daThu > 0) {
            nhanTrangThai.setText(
                    "Đã nhận cọc: "
                            + DinhDang.dinhDang(daThu)
                            + ". Khi sửa, hệ thống giữ nguyên số tiền đã thu.");
        } else {
            nhanTrangThai.setText(" ");
        }
        btnLuu.setText("Lưu thay đổi");
    }

    private void capNhatDuKien() {
        if (dangTaiDuLieu) return;
        String maPhong = giaTriDangChon(cboPhong);
        LocalDateTime vao = spNgayVao.layGiaTri();
        LocalDateTime ra = spNgayRa.layGiaTri();
        if (maPhong.isBlank() || !ra.isAfter(vao)) {
            datTrangThaiDuKienLoi(
                    maPhong.isBlank()
                            ? "Vui lòng chọn phòng."
                            : "Ngày trả phòng phải sau ngày nhận phòng.");
            return;
        }
        int luot = ++luotDuKien;
        btnLuu.setEnabled(false);
        TacVuNen.chayCoPhien(
                hopThoai,
                () -> luot == luotDuKien,
                () -> luuTruService.tinhTienPhongDuKien(maPhong, vao, ra),
                duKien -> {
                    nhanSoNgay.setText(duKien[0] + " ngày");
                    nhanTongTien.setText(DinhDang.dinhDang(duKien[1]));
                    nhanCoc.setText(DinhDang.dinhDang(duKien[2]));
                    if (maHopDong == null || !nhanTrangThai.getText().startsWith("Đã nhận cọc")) {
                        nhanTrangThai.setText(" ");
                    }
                    btnLuu.setEnabled(true);
                },
                ex -> datTrangThaiDuKienLoi(ex.getMessage()));
    }

    private void datTrangThaiDuKienLoi(String thongDiep) {
        nhanSoNgay.setText("—");
        nhanTongTien.setText("—");
        nhanCoc.setText("—");
        if (maHopDong == null || nhanTrangThai.getText().isBlank()) {
            nhanTrangThai.setText(thongDiep);
        }
        btnLuu.setEnabled(false);
    }

    private void luu() {
        String maThuCung = giaTriDangChon(cboThuCung);
        String maPhong = giaTriDangChon(cboPhong);
        LocalDateTime ngayVao = spNgayVao.layGiaTri();
        LocalDateTime ngayRa = spNgayRa.layGiaTri();
        String ghiChu = oGhiChu.getText().trim();
        if (maThuCung.isBlank() || maPhong.isBlank()) {
            HopThoai.hienThiCanhBao(hopThoai, "Vui lòng chọn thú cưng và phòng.");
            return;
        }
        if (!ngayRa.isAfter(ngayVao)) {
            HopThoai.hienThiCanhBao(hopThoai, "Ngày trả phòng phải sau ngày nhận phòng.");
            return;
        }
        btnLuu.setEnabled(false);
        TacVuNen.chayCoPhien(
                hopThoai,
                () -> true,
                () -> {
                    if (maHopDong == null) {
                        return luuTruService.giuPhong(maThuCung, maPhong, ngayVao, ngayRa, ghiChu);
                    }
                    luuTruService.suaDatPhong(
                            maHopDong, maThuCung, maPhong, ngayVao, ngayRa, ghiChu);
                    return maHopDong;
                },
                maDaLuu -> {
                    ketQua = maDaLuu;
                    hopThoai.dispose();
                },
                ex -> {
                    btnLuu.setEnabled(true);
                    HopThoai.hienThiCanhBao(hopThoai, ex.getMessage());
                });
    }
}
