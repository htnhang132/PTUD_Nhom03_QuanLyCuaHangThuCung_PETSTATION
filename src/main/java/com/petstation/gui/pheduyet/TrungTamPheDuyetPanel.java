package com.petstation.gui.pheduyet;

import com.petstation.gui.chung.BieuMauDialog;
import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.chung.TrangThaiPheDuyet;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.pheduyet.PheDuyetService;
import com.petstation.service.pheduyet.PheDuyetService.YeuCau;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

// Màn hình: Trung tâm phê duyệt (Mã: TRUNG_TAM_PHE_DUYET)
// Luồng xử lý: GUI -> PheDuyetService, DeXuatGiaService -> YeuCauPheDuyetDAOImpl, DeXuatGiaDAOImpl
// -> SQL (yeu_cau_phe_duyet)
// Mô tả: Tiếp nhận, xem xét và phê duyệt hoặc từ chối các đề xuất thay đổi giá bán hoặc phiếu xuất
// hủy hàng
// Phân hệ: pheduyet
public final class TrungTamPheDuyetPanel extends CoSoPanel {
    private final PheDuyetService pheDuyetService = new PheDuyetService();
    private final DefaultTableModel moHinh =
            new DefaultTableModel(
                    new String[] {"Mã", "Loại yêu cầu", "Người gửi", "Thời gian", "Trạng thái"},
                    0) {
                @Override
                public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                    return false;
                }
            };

    private final JTable bangBang;

    private final List<YeuCau> daHienThi = new ArrayList<>();

    private final JCheckBox btnDangChoChi = new JCheckBox("Chỉ hiện yêu cầu chờ duyệt", true);

    private final JPanel khungChiTiet = new BoGocPanel(12, Color.WHITE, true);

    private final JLabel nhanBoDem = new JLabel();

    private YeuCau daChon;

    public TrungTamPheDuyetPanel() {
        setLayout(new BorderLayout(0, 10));
        JPanel khungTren = taoVungTrongSuot(new BorderLayout(10, 0));
        khungTren.add(taoTieuDeManHinh("Trung tâm phê duyệt"), BorderLayout.WEST);
        JPanel khungPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        khungPhai.setOpaque(false);
        nhanBoDem.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        nhanBoDem.setForeground(MauUngDung.CAM);
        btnDangChoChi.setOpaque(false);
        btnDangChoChi.addActionListener(suKien -> taiLai());
        NutDinhDang btnLamMoi = new NutDinhDang("Làm mới", NutDinhDang.KieuNut.TRONG_SUOT);
        btnLamMoi.addActionListener(suKien -> taiLai());
        khungPhai.add(nhanBoDem);
        khungPhai.add(btnDangChoChi);
        khungPhai.add(btnLamMoi);
        khungTren.add(khungPhai, BorderLayout.EAST);
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
        khungVungChua.setPreferredSize(new Dimension(420, 0));
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
                    daHienThi.clear();
                    moHinh.setRowCount(0);
                    for (YeuCau dongDuLieu : ketQua.tatCa) {
                        if (btnDangChoChi.isSelected()
                                && (dongDuLieu.layTrangThai() == null
                                        || !dongDuLieu.layTrangThai().coTheDuyet())) continue;
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
                    long dangCho = ketQua.dangCho;
                    nhanBoDem.setText(
                            dangCho == 0
                                    ? "Không có yêu cầu chờ duyệt"
                                    : dangCho + " yêu cầu chờ duyệt");
                    int chiSo = giu == null ? -1 : daHienThi.indexOf(giu);
                    int viewRow = chiSo < 0 ? -1 : bangBang.convertRowIndexToView(chiSo);
                    if (viewRow >= 0) bangBang.setRowSelectionInterval(viewRow, viewRow);
                    else {
                        daChon = null;
                        hienThiChiTiet();
                    }
                },
                ex -> {
                    if (luotHienTai != luotTai) return;
                    HopThoai.hienThiCanhBao(TrungTamPheDuyetPanel.this, TacVuNen.thongDiepLoi(ex));
                });
    }

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
                    layHop("Trước khi sửa", dongDuLieu.layTruoc(), MauUngDung.XAM_TRUNG_TINH));
            khungSoSanh.add(
                    layHop("Sau khi sửa", dongDuLieu.laySau(), MauUngDung.XANH_LA_NEN_NHAT));
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
                    List<String> nhap =
                            BieuMauDialog.layYeuCauNhap(
                                    this,
                                    "Yêu cầu bổ sung",
                                    new String[] {"Nội dung cần bổ sung *"},
                                    new String[] {""});
                    if (nhap == null) return;
                    thucHienXuLyPheDuyet(
                            () -> pheDuyetService.yeuCauBoSung(dongDuLieu.layId(), nhap.get(0)),
                            false);
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
        JLabel nhanDongDangXet =
                new JLabel(
                        "<html><span style='color:#7a7a7a'>"
                                + khoaDangXet
                                + ":</span> "
                                + giaTri
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

    private void duyet(YeuCau dongDuLieu) {
        if (!HopThoai.xacNhan(
                this,
                "Duyệt yêu cầu "
                        + dongDuLieu.layId()
                        + " ("
                        + dongDuLieu.layLoai().layNhan()
                        + ")?\nHệ thống sẽ tự thực hiện thay đổi và ghi Nhật ký.")) return;
        String nguoiDuyet = NhatKyHeThongService.macDinh().layNguoiDung();
        thucHienXuLyPheDuyet(() -> pheDuyetService.duyet(dongDuLieu, nguoiDuyet), true);
    }

    private void tuChoi(YeuCau dongDuLieu) {
        List<String> giaTri =
                BieuMauDialog.layYeuCauNhap(
                        this,
                        "Từ chối yêu cầu " + dongDuLieu.layId(),
                        new String[] {"Lý do từ chối *"},
                        new String[] {""});
        if (giaTri == null) return;
        if (giaTri.get(0).isBlank()) {
            HopThoai.hienThiCanhBao(this, "Cần nhập lý do từ chối.");
            return;
        }
        String nguoiDuyet = NhatKyHeThongService.macDinh().layNguoiDung();
        String lyDo = giaTri.get(0);
        thucHienXuLyPheDuyet(() -> pheDuyetService.tuChoi(dongDuLieu, nguoiDuyet, lyDo), true);
    }

    private void thucHienXuLyPheDuyet(Runnable tacVu, boolean taiLaiKhiLoi) {
        TacVuNen.chay(
                () -> {
                    tacVu.run();
                    return null;
                },
                null,
                ex -> {
                    HopThoai.hienThiCanhBao(TrungTamPheDuyetPanel.this, TacVuNen.thongDiepLoi(ex));
                    if (taiLaiKhiLoi) {
                        taiLai();
                    }
                });
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
