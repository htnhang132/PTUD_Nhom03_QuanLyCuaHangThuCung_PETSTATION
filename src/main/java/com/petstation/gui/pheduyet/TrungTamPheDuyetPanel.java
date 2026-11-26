package com.petstation.gui.pheduyet;

import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.model.chung.TrangThaiPheDuyet;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.pheduyet.PheDuyetService;
import com.petstation.service.pheduyet.PheDuyetService.YeuCau;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

// Màn hình: Trung tâm phê duyệt (Mã: TRUNG_TAM_PHE_DUYET)
// Luồng xử lý: GUI -> PheDuyetService, DeXuatGiaService -> YeuCauPheDuyetDAOImpl, DeXuatGiaDAOImpl
// -> SQL (yeu_cau_phe_duyet)
// Mô tả: Tiếp nhận, xem xét và phê duyệt hoặc từ chối các đề xuất thay đổi giá bán hoặc phiếu xuất
// hủy hàng
// Phân hệ: pheduyet
public final class TrungTamPheDuyetPanel extends CoSoPanel {
    // 1. THUỘC TÍNH & HÀM KHỞI TẠO

    private final PheDuyetService pheDuyetService = new PheDuyetService();
    private final DefaultTableModel moHinh =
            new DefaultTableModel(
                    new String[] {
                        "Mã yêu cầu", "Loại yêu cầu", "Người gửi", "Gửi lúc", "Trạng thái"
                    },
                    0) {
                @Override
                public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                    return false;
                }
            };

    private final JTable bangBang;

    private final List<YeuCau> daHienThi = new ArrayList<>();

    private final List<YeuCau> tatCaDaTai = new ArrayList<>();

    private final JComboBox<String> cboLoaiYeuCau = taoComboLoaiYeuCau();

    private final JTextField oTimKiem = taoOTimKiem("Mã yêu cầu hoặc người gửi");

    private final JCheckBox btnDangChoChi = new JCheckBox("Chỉ hiện yêu cầu chờ duyệt", true);

    private final JPanel khungChiTiet = new BoGocPanel(12, Color.WHITE, true);

    private final JLabel nhanBoDem = new JLabel();

    private YeuCau daChon;

    private static JComboBox<String> taoComboLoaiYeuCau() {
        JComboBox<String> cbo = new JComboBox<>();
        cbo.addItem("Tất cả");
        for (PheDuyetService.Loai loai : PheDuyetService.Loai.values()) {
            cbo.addItem(loai.layNhan());
        }
        return cbo;
    }

    public TrungTamPheDuyetPanel() {
        setLayout(new BorderLayout(0, 10));
        JPanel khungTren = taoVungTrongSuot(new BorderLayout(0, 8));
        nhanBoDem.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanBoDem.setForeground(MauUngDung.CAM);
        khungTren.add(
                ThanhDanhSach.tieuDe(taoTieuDeManHinh("Trung tâm phê duyệt"), nhanBoDem),
                BorderLayout.NORTH);

        oTimKiem.setPreferredSize(new Dimension(200, MauUngDung.CAO_DAU_VAO));
        Timer henGioTim = new Timer(150, e -> apDungBoLoc());
        henGioTim.setRepeats(false);
        themBoLangNgheTaiLieu(oTimKiem, henGioTim::restart);
        cboLoaiYeuCau.setPreferredSize(new Dimension(170, MauUngDung.CAO_DAU_VAO));
        cboLoaiYeuCau.addActionListener(suKien -> apDungBoLoc());
        btnDangChoChi.setOpaque(false);
        btnDangChoChi.addActionListener(suKien -> taiLai());

        ThanhDanhSach thanhCongCu = new ThanhDanhSach();
        thanhCongCu.add(oTimKiem);
        thanhCongCu.themBoLoc("Loại:", cboLoaiYeuCau);
        thanhCongCu.themBoLoc(btnDangChoChi);
        khungTren.add(thanhCongCu, BorderLayout.CENTER);
        add(khungTren, BorderLayout.NORTH);
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        bangBang = layBang(scrCuon);
        bangBang.getSelectionModel()
                .addListSelectionListener(
                        suKien -> {
                            if (suKien.getValueIsAdjusting()) return;
                            int dongDuLieu = bangBang.getSelectedRow();
                            daChon =
                                    dongDuLieu < 0
                                            ? null
                                            : daHienThi.get(
                                                    bangBang.convertRowIndexToModel(dongDuLieu));
                            hienThiChiTiet();
                        });
        khungChiTiet.setLayout(new BorderLayout(0, 8));
        khungChiTiet.setBorder(new EmptyBorder(16, 18, 16, 18));
        JPanel khungVungChua = new JPanel(new BorderLayout());
        khungVungChua.setOpaque(false);
        khungVungChua.setPreferredSize(new Dimension(396, 0));
        khungVungChua.add(khungChiTiet);
        JSplitPane tach = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrCuon, khungVungChua);
        tach.setResizeWeight(1.0);
        tach.setDividerSize(8);
        tach.setBorder(null);
        tach.setOpaque(false);
        add(tach, BorderLayout.CENTER);
        taiLai();
    }

    private int luotTai;

    private final BoLangNgheSuKien<String> boLangNghePheDuyet =
            suKien ->
                    SwingUtilities.invokeLater(
                            () -> {
                                if (isShowing()
                                        && PhienDangNhapService.macDinh().layPhienHienTai() != null)
                                    taiLai();
                            });

    // 2. TẢI DỮ LIỆU, LỌC & VÒNG ĐỜI PANEL

    @Override
    public void addNotify() {
        super.addNotify();
        PheDuyetService.themBoLangNghe(boLangNghePheDuyet);
    }

    @Override
    public void removeNotify() {
        PheDuyetService.xoaBoLangNghe(boLangNghePheDuyet);
        super.removeNotify();
    }

    private static final class DuLieuTaiLai {
        final List<YeuCau> tatCa;
        final long dangCho;

        private DuLieuTaiLai(List<YeuCau> tatCa, long dangCho) {
            this.tatCa = tatCa;
            this.dangCho = dangCho;
        }
    }

    private void taiLai() {
        final int luotHienTai = ++luotTai;
        final YeuCau giu = daChon;
        TacVuNen.chay(
                () -> {
                    List<YeuCau> tatCa = new ArrayList<>(pheDuyetService.layTatCa());
                    long dangCho = pheDuyetService.demChoDuyet();
                    return new DuLieuTaiLai(tatCa, dangCho);
                },
                ketQua -> {
                    if (luotHienTai != luotTai) return;
                    tatCaDaTai.clear();
                    tatCaDaTai.addAll(ketQua.tatCa);
                    long dangCho = ketQua.dangCho;
                    nhanBoDem.setText(
                            dangCho == 0
                                    ? "Không có yêu cầu chờ duyệt"
                                    : dangCho + " yêu cầu chờ duyệt");
                    apDungBoLoc(giu);
                },
                ex -> {
                    if (luotHienTai != luotTai) return;
                    HopThoai.hienThiCanhBao(TrungTamPheDuyetPanel.this, TacVuNen.thongDiepLoi(ex));
                });
    }

    private void apDungBoLoc() {
        apDungBoLoc(daChon);
    }

    private void apDungBoLoc(YeuCau giu) {
        String loaiChon =
                cboLoaiYeuCau.getSelectedItem() == null
                        ? "Tất cả"
                        : String.valueOf(cboLoaiYeuCau.getSelectedItem());
        String tuKhoa =
                oTimKiem.getText() == null
                        ? ""
                        : oTimKiem.getText().trim().toLowerCase(Locale.ROOT);
        daHienThi.clear();
        moHinh.setRowCount(0);
        for (YeuCau dongDuLieu : tatCaDaTai) {
            if (btnDangChoChi.isSelected()
                    && (dongDuLieu.layTrangThai() == null
                            || !dongDuLieu.layTrangThai().coTheDuyet())) continue;
            if (!"Tất cả".equals(loaiChon) && !loaiChon.equals(dongDuLieu.layLoai().layNhan())) {
                continue;
            }
            if (!tuKhoa.isEmpty()) {
                String ghep =
                        (dongDuLieu.layId()
                                        + " "
                                        + dongDuLieu.layLoai().layNhan()
                                        + " "
                                        + dongDuLieu.layNguoiGui()
                                        + " "
                                        + dongDuLieu.layTenTrangThai())
                                .toLowerCase(Locale.ROOT);
                if (!ghep.contains(tuKhoa)) continue;
            }
            daHienThi.add(dongDuLieu);
            moHinh.addRow(
                    new Object[] {
                        dongDuLieu.layId(),
                        dongDuLieu.layLoai().layNhan(),
                        dongDuLieu.layNguoiGui(),
                        dongDuLieu.layVanBanThoiGian(),
                        dongDuLieu.layTenTrangThai()
                    });
        }
        int chiSo = -1;
        if (giu != null) {
            for (int i = 0; i < daHienThi.size(); i++) {
                if (giu.layId().equals(daHienThi.get(i).layId())) {
                    chiSo = i;
                    break;
                }
            }
        }
        int viewRow = chiSo < 0 ? -1 : bangBang.convertRowIndexToView(chiSo);
        if (viewRow >= 0) bangBang.setRowSelectionInterval(viewRow, viewRow);
        else {
            daChon = null;
            hienThiChiTiet();
        }
    }

    // 3. XÂY DỰNG KHUNG CHI TIẾT YÊU CẦU

    private void hienThiChiTiet() {
        khungChiTiet.removeAll();
        if (daChon == null) {
            JLabel nhanGoiY = new JLabel("Chọn một yêu cầu để xem chi tiết.", JLabel.CENTER);
            nhanGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
            khungChiTiet.add(nhanGoiY, BorderLayout.CENTER);
        } else {
            YeuCau dongDuLieu = daChon;
            khungChiTiet.add(taoPhanDauChiTiet(dongDuLieu), BorderLayout.NORTH);
            JPanel khungSoSanh = new JPanel(new GridLayout(1, 2, 10, 0));
            khungSoSanh.setOpaque(false);
            khungSoSanh.add(
                    layHop("Thông tin hiện tại", dongDuLieu.layTruoc(), MauUngDung.XAM_TRUNG_TINH));
            khungSoSanh.add(
                    layHop("Nội dung đề nghị", dongDuLieu.laySau(), MauUngDung.XANH_LA_NEN_NHAT));
            khungChiTiet.add(khungSoSanh, BorderLayout.CENTER);
            khungChiTiet.add(taoThanhNutPheDuyet(dongDuLieu), BorderLayout.SOUTH);
        }
        khungChiTiet.revalidate();
        khungChiTiet.repaint();
    }

    private static JPanel taoPhanDauChiTiet(YeuCau dongDuLieu) {
        JPanel khungDau = new JPanel(new GridLayout(0, 1, 0, 3));
        khungDau.setOpaque(false);
        JLabel nhanTieuDe = new JLabel(dongDuLieu.layId() + " · " + dongDuLieu.layLoai().layNhan());
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        khungDau.add(nhanTieuDe);
        khungDau.add(
                layDong(
                        "Người gửi",
                        dongDuLieu.layNguoiGui() + " lúc " + dongDuLieu.layVanBanThoiGian()));
        khungDau.add(layDong("Lý do", dongDuLieu.layLyDo()));
        khungDau.add(
                layDong(
                        "Trạng thái",
                        dongDuLieu.layTenTrangThai()
                                + (dongDuLieu.layDaQuyetDinhTheo().isEmpty()
                                        ? ""
                                        : " bởi " + dongDuLieu.layDaQuyetDinhTheo())
                                + (dongDuLieu.layTuChoiLyDo().isEmpty()
                                        ? ""
                                        : " (" + dongDuLieu.layTuChoiLyDo() + ")")));
        return khungDau;
    }

    private JPanel taoThanhNutPheDuyet(YeuCau dongDuLieu) {
        boolean dangCho = dongDuLieu.layTrangThai() == TrangThaiPheDuyet.CHO_DUYET;
        boolean coTheQuyetDinh =
                dongDuLieu.layTrangThai() != null && dongDuLieu.layTrangThai().coTheDuyet();
        NutDinhDang btnBoSung = new NutDinhDang("Yêu cầu bổ sung", NutDinhDang.KieuNut.PHU);
        btnBoSung.setEnabled(dangCho);
        btnBoSung.addActionListener(
                e -> {
                    String noiDung =
                            nhapNoiDungBatBuoc(
                                    "Yêu cầu bổ sung",
                                    "Nội dung cần bổ sung *",
                                    "Cần nhập nội dung yêu cầu bổ sung.");
                    if (noiDung == null) return;
                    thucHienXuLyPheDuyet(
                            () -> pheDuyetService.yeuCauBoSung(dongDuLieu.layId(), noiDung),
                            true,
                            "Đã gửi yêu cầu bổ sung cho " + dongDuLieu.layId() + ".");
                });
        NutDinhDang btnDuyet = new NutDinhDang("Duyệt", NutDinhDang.KieuNut.THANH_CONG);
        NutDinhDang btnTuChoi = new NutDinhDang("Từ chối", NutDinhDang.KieuNut.NGUY_HIEM);
        btnDuyet.setEnabled(coTheQuyetDinh);
        btnTuChoi.setEnabled(coTheQuyetDinh);
        btnDuyet.addActionListener(suKien -> duyet(dongDuLieu));
        btnTuChoi.addActionListener(suKien -> tuChoi(dongDuLieu));
        JPanel khungCacNut = new JPanel(new GridLayout(1, 3, 8, 0));
        khungCacNut.setOpaque(false);
        khungCacNut.add(btnDuyet);
        khungCacNut.add(btnTuChoi);
        khungCacNut.add(btnBoSung);
        return khungCacNut;
    }

    private static JLabel layDong(String khoaDangXet, String giaTri) {
        String mauXam = String.format("#%06X", MauUngDung.VAN_BAN_XAM.getRGB() & 0xFFFFFF);
        JLabel nhanDongDangXet =
                new JLabel(
                        "<html><span style='color:"
                                + mauXam
                                + "'>"
                                + DinhDang.thoatHtml(khoaDangXet)
                                + ":</span> "
                                + DinhDang.thoatHtml(giaTri)
                                + "</html>");
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        return nhanDongDangXet;
    }

    private static JPanel layHop(String tieuDe, String vanBan, Color nen) {
        JPanel khungVungDangXet = new JPanel(new BorderLayout(0, 4));
        khungVungDangXet.setOpaque(false);
        JLabel nhanDongDangXet = new JLabel(tieuDe);
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        JTextArea oGiaTriDau = new JTextArea(vanBan);
        oGiaTriDau.setEditable(false);
        oGiaTriDau.setLineWrap(true);
        oGiaTriDau.setWrapStyleWord(true);
        oGiaTriDau.setBackground(nen);
        oGiaTriDau.setBorder(new EmptyBorder(8, 8, 8, 8));
        oGiaTriDau.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        khungVungDangXet.add(nhanDongDangXet, BorderLayout.NORTH);
        khungVungDangXet.add(new JScrollPane(oGiaTriDau), BorderLayout.CENTER);
        return khungVungDangXet;
    }

    // 4. PHÊ DUYỆT, TỪ CHỐI & THAO TÁC PHỤ

    private void duyet(YeuCau dongDuLieu) {
        if (!HopThoai.xacNhan(
                this,
                "Duyệt yêu cầu "
                        + dongDuLieu.layId()
                        + " ("
                        + dongDuLieu.layLoai().layNhan()
                        + ")?\nHệ thống sẽ tự thực hiện thay đổi và ghi Nhật ký.")) return;
        String nguoiDuyet = NhatKyHeThongService.macDinh().layNguoiDung();
        thucHienXuLyPheDuyet(
                () -> pheDuyetService.duyet(dongDuLieu, nguoiDuyet),
                true,
                "Đã duyệt yêu cầu " + dongDuLieu.layId() + ".");
    }

    private void tuChoi(YeuCau dongDuLieu) {
        String lyDo =
                nhapNoiDungBatBuoc(
                        "Từ chối yêu cầu " + dongDuLieu.layId(),
                        "Lý do từ chối *",
                        "Cần nhập lý do từ chối.");
        if (lyDo == null) return;
        String nguoiDuyet = NhatKyHeThongService.macDinh().layNguoiDung();
        thucHienXuLyPheDuyet(
                () -> pheDuyetService.tuChoi(dongDuLieu, nguoiDuyet, lyDo),
                true,
                "Đã từ chối yêu cầu " + dongDuLieu.layId() + ".");
    }

    private String nhapNoiDungBatBuoc(String tieuDe, String nhan, String thongBaoTrong) {
        Window cha = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(cha, tieuDe, Dialog.ModalityType.APPLICATION_MODAL);
        JTextField oNhap = new JTextField(26);
        oNhap.setPreferredSize(new Dimension(280, MauUngDung.CAO_DAU_VAO));
        String[] ketQua = {null};
        JPanel noiDung = new JPanel(new GridLayout(0, 1, 0, 6));
        noiDung.setBorder(new EmptyBorder(14, 16, 6, 16));
        noiDung.add(taoNhanTruong(nhan, false));
        noiDung.add(oNhap);
        JButton nutOk =
                new NutDinhDang(
                        tieuDe.startsWith("Từ chối") ? "Gửi lý do từ chối" : "Gửi yêu cầu bổ sung",
                        NutDinhDang.KieuNut.CHINH);
        JButton nutHuy = new NutDinhDang("Hủy", NutDinhDang.KieuNut.TRONG_SUOT);
        nutOk.addActionListener(
                e -> {
                    String vanBan = oNhap.getText() == null ? "" : oNhap.getText().trim();
                    if (vanBan.isEmpty()) {
                        HopThoai.hienThiCanhBao(dlg, thongBaoTrong);
                        oNhap.requestFocusInWindow();
                        return;
                    }
                    ketQua[0] = vanBan;
                    dlg.dispose();
                });
        nutHuy.addActionListener(e -> dlg.dispose());
        JPanel chan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        chan.add(nutHuy);
        chan.add(nutOk);
        dlg.getRootPane().setDefaultButton(nutOk);
        dlg.getRootPane()
                .registerKeyboardAction(
                        e -> dlg.dispose(),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
        dlg.setLayout(new BorderLayout(8, 8));
        dlg.add(noiDung, BorderLayout.CENTER);
        dlg.add(chan, BorderLayout.SOUTH);
        dlg.pack();
        HopThoai.chuanHoaCuaSo(dlg, cha);
        dlg.setVisible(true);
        return ketQua[0];
    }

    private void thucHienXuLyPheDuyet(
            Runnable tacVu, boolean taiLaiKhiLoi, String thongBaoThanhCong) {
        var phien = PhienDangNhapService.macDinh().yeuCauHienTai();
        TacVuNen.chayCoPhien(
                phien,
                this::isDisplayable,
                () -> {
                    tacVu.run();
                    return null;
                },
                null,
                v -> {
                    HopThoai.hienThiThanhCong(TrungTamPheDuyetPanel.this, thongBaoThanhCong);
                    taiLai();
                },
                ex -> {
                    HopThoai.hienThiCanhBao(TrungTamPheDuyetPanel.this, TacVuNen.thongDiepLoi(ex));
                    if (taiLaiKhiLoi) {
                        taiLai();
                    }
                });
    }

    @Override
    public void xuLyLamMoi() {
        oTimKiem.setText("");
        cboLoaiYeuCau.setSelectedIndex(0);
        btnDangChoChi.setSelected(true);
        taiLai();
    }

    @Override
    public void xuLyHienThi() {
        taiLai();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Phê duyệt yêu cầu";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Chọn yêu cầu, xem nội dung trước và sau khi sửa, rồi Duyệt hoặc Từ chối (bắt buộc"
                + " ghi lý do).\n"
                + "Khi duyệt, hệ thống tự thực hiện thay đổi và ghi Nhật ký.";
    }
}
