package com.petstation.gui.processing.approval;

import com.petstation.gui.components.BieuMauDialog;
import com.petstation.gui.components.CoSoPanel;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.service.NhatKyHeThongService;
import com.petstation.service.PheDuyetService;
import com.petstation.service.PheDuyetService.YeuCau;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
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

/**
 * Cung cấp giao diện và xử lý tương tác cho phê duyệt giữa vùng hiển thị.
 *
 * <p>Xử lý ▸ Phê duyệt yêu cầu (chỉ Cửa hàng trưởng): xem nội dung trước/sau, Duyệt hoặc Từ chối
 * kèm lý do.
 */
public final class TrungTamPheDuyetPanel extends CoSoPanel implements HoTroThanhCongCu {
    /** Mô hình. */
    private final DefaultTableModel moHinh =
            new DefaultTableModel(
                    new String[] {"Mã", "Loại yêu cầu", "Người gửi", "Thời gian", "Trạng thái"},
                    0) {
                /**
                 * Kiểm tra ô có thể sửa trong lớp xử lý giao diện.
                 *
                 * @param dongDuLieu dòng dữ liệu
                 * @param giaTriDangXet giá trị đang xét
                 * @return kết quả kiểm tra điều kiện xử lý
                 */
                @Override
                public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                    return false;
                }
            };

    /** Bảng. */
    private final JTable tblBang;

    /** Đã hiển thị. */
    private final List<YeuCau> daHienThi = new ArrayList<>();

    /** Đang chờ chỉ. */
    private final JCheckBox btnDangChoChi = new JCheckBox("Chỉ hiện yêu cầu chờ duyệt", true);

    /** Chi tiết. */
    private final JPanel pnlChiTiet = new JPanel(new BorderLayout(0, 8));

    /** Bộ đếm. */
    private final JLabel lblBoDem = new JLabel();

    /** Đã chọn. */
    private YeuCau daChon;

    /** Khởi tạo TrungTamPheDuyetPanel với các giá trị được cung cấp. */
    public TrungTamPheDuyetPanel() {
        setLayout(new BorderLayout(0, 10));
        // Trên.
        JPanel pnlTren = taoVungTrongSuot(new BorderLayout(10, 0));
        pnlTren.add(taoTieuDeMuc("Danh sách yêu cầu"), BorderLayout.WEST);
        // Phải.
        JPanel pnlPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlPhai.setOpaque(false);
        lblBoDem.setFont(MauUngDung.taoPhongChu(Font.BOLD, 13));
        lblBoDem.setForeground(MauUngDung.CAM);
        btnDangChoChi.setOpaque(false);
        btnDangChoChi.addActionListener(suKien -> taiLai());
        pnlPhai.add(lblBoDem);
        pnlPhai.add(btnDangChoChi);
        pnlTren.add(pnlPhai, BorderLayout.EAST);
        add(pnlTren, BorderLayout.NORTH);

        // Cuộn.
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        tblBang = layBang(scrCuon);
        tblBang.getSelectionModel()
                .addListSelectionListener(
                        suKien -> {
                            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                            if (suKien.getValueIsAdjusting()) return;
                            // Dòng dữ liệu.
                            int dongDuLieu = tblBang.getSelectedRow();
                            daChon =
                                    dongDuLieu < 0
                                            ? null
                                            : daHienThi.get(
                                                    tblBang.convertRowIndexToModel(dongDuLieu));
                            hienThiChiTiet();
                        });

        pnlChiTiet.setBackground(Color.WHITE);
        pnlChiTiet.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(14, 16, 14, 16)));
        // Vùng chứa.
        JPanel pnlVungChua = new JPanel(new BorderLayout());
        pnlVungChua.setPreferredSize(new Dimension(420, 0));
        pnlVungChua.add(pnlChiTiet);
        // Tách.
        JSplitPane tach = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrCuon, pnlVungChua);
        tach.setResizeWeight(0.55);
        tach.setBorder(null);
        tach.setOpaque(false);
        add(tach, BorderLayout.CENTER);
        PheDuyetService.themBoLangNghe(
                () ->
                        SwingUtilities.invokeLater(
                                () -> {
                                    if (isShowing()
                                            && com.petstation.service.PhienDangNhapService
                                                            .layPhienHienTai()
                                                    != null) taiLai();
                                }));
        taiLai();
    }

    /** Tải lại trong TrungTamPheDuyetPanel. */
    private void taiLai() {
        // Giữ.
        YeuCau giu = daChon;
        daHienThi.clear();
        moHinh.setRowCount(0);
        // Tất cả.
        List<YeuCau> tatCa = new ArrayList<>(PheDuyetService.layTatCa());

        for (YeuCau dongDuLieu : tatCa) {
            // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
            if (btnDangChoChi.isSelected() && !dongDuLieu.trangThai.equals("Chờ duyệt")) continue;
            daHienThi.add(dongDuLieu);
            moHinh.addRow(
                    new Object[] {
                        dongDuLieu.id,
                        dongDuLieu.loai.nhan,
                        dongDuLieu.nguoiGui,
                        dongDuLieu.layVanBanThoiGian(),
                        dongDuLieu.trangThai
                    });
        }
        // Đang chờ.
        long dangCho = PheDuyetService.demChoDuyet();
        lblBoDem.setText(
                dangCho == 0 ? "Không có yêu cầu chờ duyệt" : dangCho + " yêu cầu chờ duyệt");
        // Chỉ số.
        int chiSo = giu == null ? -1 : daHienThi.indexOf(giu);
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (chiSo >= 0) tblBang.setRowSelectionInterval(chiSo, chiSo);
        else {
            daChon = null;
            hienThiChiTiet();
        }
    }

    /** Hiển thị chi tiết trong TrungTamPheDuyetPanel. */
    private void hienThiChiTiet() {
        pnlChiTiet.removeAll();
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (daChon == null) {
            // Gợi ý.
            JLabel lblGoiY = new JLabel("Chọn một yêu cầu để xem chi tiết.", JLabel.CENTER);
            lblGoiY.setForeground(MauUngDung.VAN_BAN_XAM);
            pnlChiTiet.add(lblGoiY, BorderLayout.CENTER);
        } else {
            // Dòng dữ liệu.
            YeuCau dongDuLieu = daChon;
            // Đầu.
            JPanel pnlDau = new JPanel(new GridLayout(0, 1, 0, 3));
            pnlDau.setOpaque(false);
            // Giá trị tạm.
            JLabel lblGiaTriTam = new JLabel(dongDuLieu.id + " · " + dongDuLieu.loai.nhan);
            lblGiaTriTam.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
            pnlDau.add(lblGiaTriTam);
            pnlDau.add(
                    layDong(
                            "Người gửi",
                            dongDuLieu.nguoiGui + " lúc " + dongDuLieu.layVanBanThoiGian()));
            pnlDau.add(layDong("Lý do", dongDuLieu.lyDo));
            pnlDau.add(
                    layDong(
                            "Trạng thái",
                            dongDuLieu.trangThai
                                    + (dongDuLieu.daQuyetDinhTheo.isEmpty()
                                            ? ""
                                            : " bởi " + dongDuLieu.daQuyetDinhTheo)
                                    + (dongDuLieu.tuChoiLyDo.isEmpty()
                                            ? ""
                                            : " (" + dongDuLieu.tuChoiLyDo + ")")));
            pnlChiTiet.add(pnlDau, BorderLayout.NORTH);

            // So sánh.
            JPanel pnlSoSanh = new JPanel(new GridLayout(1, 2, 10, 0));
            pnlSoSanh.setOpaque(false);
            pnlSoSanh.add(layHop("Trước khi sửa", dongDuLieu.truoc, new Color(0xF5, 0xF5, 0xF5)));
            pnlSoSanh.add(layHop("Sau khi sửa", dongDuLieu.sau, new Color(0xE6, 0xF4, 0xEA)));
            pnlChiTiet.add(pnlSoSanh, BorderLayout.CENTER);

            // Đang chờ.
            boolean dangCho = dongDuLieu.trangThai.equals("Chờ duyệt");
            // Các nút.
            JPanel pnlCacNut = new JPanel(new GridLayout(1, 2, 8, 0));
            pnlCacNut.setOpaque(false);
            // Duyệt.
            NutDinhDang btnDuyet = new NutDinhDang("Duyệt", NutDinhDang.KieuNut.THANH_CONG);
            // Từ chối.
            NutDinhDang btnTuChoi = new NutDinhDang("Từ chối", NutDinhDang.KieuNut.NGUY_HIEM);
            btnDuyet.setEnabled(dangCho);
            btnTuChoi.setEnabled(dangCho);
            btnDuyet.addActionListener(suKien -> duyet(dongDuLieu));
            btnTuChoi.addActionListener(suKien -> tuChoi(dongDuLieu));
            pnlCacNut.add(btnDuyet);
            pnlCacNut.add(btnTuChoi);
            pnlChiTiet.add(pnlCacNut, BorderLayout.SOUTH);
        }
        pnlChiTiet.revalidate();
        pnlChiTiet.repaint();
    }

    /**
     * Lấy dòng trong TrungTamPheDuyetPanel.
     *
     * @param khoaDangXet khóa đang xét
     * @param giaTri giá trị
     * @return kết quả lấy dòng
     */
    private static JLabel layDong(String khoaDangXet, String giaTri) {
        // Dòng đang xét.
        JLabel lblDongDangXet =
                new JLabel(
                        "<html><span style='color:#7a7a7a'>"
                                + khoaDangXet
                                + ":</span> "
                                + giaTri
                                + "</html>");
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        return lblDongDangXet;
    }

    /**
     * Lấy hộp trong TrungTamPheDuyetPanel.
     *
     * @param tieuDe tiêu đề
     * @param vanBan văn bản
     * @param nen nền
     * @return kết quả lấy hộp
     */
    private static JPanel layHop(String tieuDe, String vanBan, Color nen) {
        // Vùng đang xét.
        JPanel pnlVungDangXet = new JPanel(new BorderLayout(0, 4));
        pnlVungDangXet.setOpaque(false);
        // Dòng đang xét.
        JLabel lblDongDangXet = new JLabel(tieuDe);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        lblDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        // Giá trị đầu.
        JTextArea txtGiaTriDau = new JTextArea(vanBan);
        txtGiaTriDau.setEditable(false);
        txtGiaTriDau.setLineWrap(true);
        txtGiaTriDau.setWrapStyleWord(true);
        txtGiaTriDau.setBackground(nen);
        txtGiaTriDau.setBorder(new EmptyBorder(8, 8, 8, 8));
        txtGiaTriDau.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        pnlVungDangXet.add(lblDongDangXet, BorderLayout.NORTH);
        pnlVungDangXet.add(new JScrollPane(txtGiaTriDau), BorderLayout.CENTER);
        return pnlVungDangXet;
    }

    /**
     * Duyệt trong TrungTamPheDuyetPanel.
     *
     * @param dongDuLieu dòng dữ liệu
     */
    private void duyet(YeuCau dongDuLieu) {
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (!HopThoai.xacNhan(
                this,
                "Duyệt yêu cầu "
                        + dongDuLieu.id
                        + " ("
                        + dongDuLieu.loai.nhan
                        + ")?\nHệ thống sẽ tự thực hiện thay đổi và ghi Nhật ký.")) return;
        try {
            PheDuyetService.duyet(dongDuLieu, NhatKyHeThongService.layNguoiDung());
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
            taiLai();
        }
    }

    /**
     * Từ chối trong TrungTamPheDuyetPanel.
     *
     * @param dongDuLieu dòng dữ liệu
     */
    private void tuChoi(YeuCau dongDuLieu) {
        // Giá trị.
        List<String> giaTri =
                BieuMauDialog.layYeuCauNhap(
                        this,
                        "Từ chối yêu cầu " + dongDuLieu.id,
                        new String[] {"Lý do từ chối *"},
                        new String[] {""});
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri == null) return;
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri.get(0).isBlank()) {
            HopThoai.hienThiCanhBao(this, "Cần nhập lý do từ chối.");
            return;
        }
        try {
            PheDuyetService.tuChoi(dongDuLieu, NhatKyHeThongService.layNguoiDung(), giaTri.get(0));
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
            taiLai();
        }
    }

    /** Xử lý hiển thị trong TrungTamPheDuyetPanel. */
    @Override
    public void xuLyHienThi() {
        taiLai();
    }

    /**
     * Lấy tiêu đề trợ giúp trong TrungTamPheDuyetPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return "Phê duyệt yêu cầu";
    }

    /**
     * Lấy nội dung trợ giúp trong TrungTamPheDuyetPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Chọn yêu cầu, xem nội dung trước và sau khi sửa, rồi Duyệt hoặc Từ chối (bắt buộc"
                + " ghi lý do).\n"
                + "Khi duyệt, hệ thống tự thực hiện thay đổi và ghi Nhật ký.";
    }
}
