package com.petstation.gui.luutru;

import com.petstation.gui.chung.BoGocPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.model.luutru.NhatKyChamSoc;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.luutru.ChamSocService;
import com.petstation.util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class NhatKyChamSocDialog {
    // 1. HÀM KHỞI TẠO & API CÔNG KHAI

    private NhatKyChamSocDialog() {}

    public static void mo(Component cha, String loai, String ma) {
        mo(cha, loai, ma, true);
    }

    public static void mo(Component cha, String loai, String ma, boolean choPhepChinhSua) {
        JDialog dialog =
                new JDialog(
                        SwingUtilities.getWindowAncestor(cha),
                        choPhepChinhSua ? "Phiếu chăm sóc" : "Phiếu chăm sóc (Chỉ xem)",
                        Dialog.ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().setBackground(MauUngDung.NEN_KEM);
        JPanel khungChinh = new JPanel(new BorderLayout(14, 14));
        khungChinh.setBackground(MauUngDung.NEN_KEM);
        khungChinh.setBorder(new EmptyBorder(14, 14, 14, 14));
        khungChinh.add(taoTieuDeHopThoai(loai, choPhepChinhSua), BorderLayout.NORTH);
        JTextArea health = taoTextArea(3);
        JTextArea food = taoTextArea(2);
        JTextArea medicine = taoTextArea(2);
        JTextArea tasks = taoTextArea(3);
        Path[] images = new Path[2];
        JLabel[] nhanAnhDaChon = new JLabel[2];
        ChamSocService chamSocService = new ChamSocService();
        NutDinhDang btnLuu = new NutDinhDang("Lưu phiếu chăm sóc", NutDinhDang.KieuNut.CHINH);
        JPanel khungBieuMauTrai =
                taoFormTrai(
                        dialog,
                        chamSocService,
                        loai,
                        choPhepChinhSua,
                        health,
                        food,
                        medicine,
                        tasks,
                        images,
                        nhanAnhDaChon,
                        btnLuu);
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        JEditorPane preview = new JEditorPane("text/html", "");
        NutDinhDang btnIn = new NutDinhDang("In / Xuất phiếu PDF…", NutDinhDang.KieuNut.TRONG_SUOT);
        btnIn.setEnabled(false);
        final List<NhatKyChamSoc> data = new ArrayList<>();
        final Map<String, String> uriAnh = new HashMap<>();
        JPanel khungPhai = taoKhungLichSuPhai(dialog, list, preview, data, btnIn);
        Runnable load =
                () ->
                        taiNhatKy(
                                dialog,
                                chamSocService,
                                loai,
                                ma,
                                data,
                                uriAnh,
                                model,
                                list,
                                preview,
                                btnIn);
        list.addListSelectionListener(e -> capNhatXemTruoc(list, data, uriAnh, preview));
        btnLuu.addActionListener(
                e ->
                        luuPhieuChamSoc(
                                dialog,
                                chamSocService,
                                loai,
                                ma,
                                health,
                                food,
                                medicine,
                                tasks,
                                images,
                                nhanAnhDaChon,
                                btnLuu,
                                load));
        JPanel khungNoiDung = taoKhungNoiDungChinh(choPhepChinhSua, khungBieuMauTrai, khungPhai);
        khungChinh.add(khungNoiDung, BorderLayout.CENTER);
        hienThiHopThoai(dialog, cha, khungChinh, choPhepChinhSua, load);
    }

    // 2. XÂY DỰNG KHUNG HỘP THOẠI & BIỂU MẪU TRÁI

    private static JPanel taoKhungNoiDungChinh(
            boolean choPhepChinhSua, JPanel khungBieuMauTrai, JPanel khungPhai) {
        if (!choPhepChinhSua) {
            return khungPhai;
        }
        JPanel khungNoiDung = new JPanel(new GridLayout(1, 2, 14, 0));
        khungNoiDung.setOpaque(false);
        khungNoiDung.add(taoCuonForm(khungBieuMauTrai));
        khungNoiDung.add(khungPhai);
        return khungNoiDung;
    }

    private static void hienThiHopThoai(
            JDialog dialog,
            Component cha,
            JPanel khungChinh,
            boolean choPhepChinhSua,
            Runnable load) {
        khungChinh.add(taoThanhChanHopThoai(dialog), BorderLayout.SOUTH);
        dialog.setContentPane(khungChinh);
        dialog.getRootPane()
                .registerKeyboardAction(
                        e -> dialog.dispose(),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
        HopThoai.canChinhKichThuocVuaManHinh(
                dialog, choPhepChinhSua ? 940 : 660, choPhepChinhSua ? 580 : 500);
        HopThoai.chuanHoaCuaSo(dialog, cha);
        load.run();
        dialog.setVisible(true);
    }

    private static JScrollPane taoCuonForm(JPanel khung) {
        JScrollPane cuon = new JScrollPane(khung);
        cuon.setBorder(null);
        cuon.getVerticalScrollBar().setUnitIncrement(16);
        return cuon;
    }

    private static JPanel taoThanhChanHopThoai(JDialog dialog) {
        JPanel chan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chan.setOpaque(false);
        NutDinhDang btnDong = new NutDinhDang("Đóng", NutDinhDang.KieuNut.TRONG_SUOT);
        btnDong.addActionListener(e -> dialog.dispose());
        chan.add(btnDong);
        return chan;
    }

    private static JPanel taoTieuDeHopThoai(String loai, boolean choPhepChinhSua) {
        JPanel khungDauTrangChinh = new JPanel(new BorderLayout());
        khungDauTrangChinh.setOpaque(false);
        boolean laSpa = "SPA".equalsIgnoreCase(loai);
        JLabel nhanTieuDe =
                new JLabel(laSpa ? "PHIẾU CHĂM SÓC SPA THÚ CƯNG" : "PHIẾU CHĂM SÓC THÚ CƯNG");
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        nhanTieuDe.setForeground(MauUngDung.DO_THUONG_HIEU);
        String moTaMacDinh =
                laSpa
                        ? "Theo dõi tình trạng tiếp nhận, sản phẩm dùng và quá trình chăm sóc tại"
                                + " Spa"
                        : "Theo dõi tình trạng sức khỏe, dinh dưỡng, thuốc dùng và việc chăm sóc"
                                + " qua từng ca";
        JLabel nhanPhuDe =
                new JLabel(
                        choPhepChinhSua
                                ? moTaMacDinh
                                : moTaMacDinh + " (Phiếu đã đóng — chế độ chỉ xem)");
        nhanPhuDe.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanPhuDe.setForeground(MauUngDung.VAN_BAN_XAM);
        khungDauTrangChinh.add(nhanTieuDe, BorderLayout.NORTH);
        khungDauTrangChinh.add(nhanPhuDe, BorderLayout.SOUTH);
        return khungDauTrangChinh;
    }

    private static JPanel taoFormTrai(
            JDialog dialog,
            ChamSocService chamSocService,
            String loai,
            boolean choPhepChinhSua,
            JTextArea health,
            JTextArea food,
            JTextArea medicine,
            JTextArea tasks,
            Path[] images,
            JLabel[] nhanAnhDaChon,
            NutDinhDang btnLuu) {
        boolean laSpa = "SPA".equalsIgnoreCase(loai);
        JPanel khungBieuMauTrai = new BoGocPanel(12, Color.WHITE);
        khungBieuMauTrai.setLayout(new BoxLayout(khungBieuMauTrai, BoxLayout.Y_AXIS));
        khungBieuMauTrai.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel nhanTieuDeBieuMauChinh =
                new JLabel(
                        choPhepChinhSua ? "Ghi nhận chăm sóc mới" : "Thông tin biểu mẫu (Chỉ xem)");
        nhanTieuDeBieuMauChinh.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanTieuDeBieuMauChinh.setForeground(MauUngDung.DO_THUONG_HIEU);
        themPhanTu(khungBieuMauTrai, nhanTieuDeBieuMauChinh);
        khungBieuMauTrai.add(Box.createVerticalStrut(10));
        themTruong(khungBieuMauTrai, "Tình trạng sức khỏe khi nhận *", taoCuon(health, 60));
        JPanel tagSucKhoe =
                taoThanhQuickTag(
                        health,
                        "Khỏe mạnh, tỉnh táo",
                        "Ăn uống tốt",
                        "Hơi nhút nhát",
                        "Cần theo dõi thêm");
        tagSucKhoe.setVisible(choPhepChinhSua);
        themPhanTu(khungBieuMauTrai, tagSucKhoe);
        themTruong(
                khungBieuMauTrai,
                laSpa ? "Ghi chú chăm sóc / ăn uống nhẹ" : "Hướng dẫn ăn uống & khẩu phần",
                taoCuon(food, 50));
        themTruong(
                khungBieuMauTrai,
                laSpa ? "Sản phẩm sử dụng / lưu ý da lông" : "Thuốc & chỉ định từ chủ nuôi",
                taoCuon(medicine, 50));
        themTruong(khungBieuMauTrai, "Việc đã thực hiện trong ca", taoCuon(tasks, 60));
        JPanel tagCongViec =
                laSpa
                        ? taoThanhQuickTag(
                                tasks,
                                "Tắm gội sạch",
                                "Sấy khô & chải lông",
                                "Cắt tỉa gọn gàng",
                                "Vệ sinh tai / móng",
                                "Hoạt bát, hợp tác")
                        : taoThanhQuickTag(
                                tasks,
                                "Ăn hết suất",
                                "Uống nước bình thường",
                                "Vận động vui vẻ",
                                "Tắm / vệ sinh sạch",
                                "Ngủ ngoan");
        tagCongViec.setVisible(choPhepChinhSua);
        themPhanTu(khungBieuMauTrai, tagCongViec);
        themAnhVaChanForm(
                khungBieuMauTrai,
                dialog,
                chamSocService,
                choPhepChinhSua,
                health,
                food,
                medicine,
                tasks,
                images,
                nhanAnhDaChon,
                btnLuu);
        return khungBieuMauTrai;
    }

    private static void themAnhVaChanForm(
            JPanel khungBieuMauTrai,
            JDialog dialog,
            ChamSocService chamSocService,
            boolean choPhepChinhSua,
            JTextArea health,
            JTextArea food,
            JTextArea medicine,
            JTextArea tasks,
            Path[] images,
            JLabel[] nhanAnhDaChon,
            NutDinhDang btnLuu) {
        JPanel khungAnh =
                taoHangChonAnh(dialog, chamSocService, choPhepChinhSua, images, nhanAnhDaChon);
        khungAnh.setVisible(choPhepChinhSua);
        themPhanTu(khungBieuMauTrai, khungAnh);
        khungBieuMauTrai.add(Box.createVerticalStrut(6));
        JLabel sharedHint =
                new JLabel(
                        "<html><i>"
                                + (choPhepChinhSua
                                        ? "* Ảnh được lưu vào thư mục mạng UNC đã cấu hình để các"
                                                + " máy cùng xem."
                                        : "Phiếu đã đóng — chọn một bản ghi ở cột Lịch sử bên phải"
                                                + " để xem chi tiết.")
                                + "</i></html>");
        sharedHint.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        sharedHint.setForeground(MauUngDung.VAN_BAN_XAM);
        themPhanTu(khungBieuMauTrai, sharedHint);
        khungBieuMauTrai.add(Box.createVerticalStrut(10));
        health.setEditable(choPhepChinhSua);
        food.setEditable(choPhepChinhSua);
        medicine.setEditable(choPhepChinhSua);
        tasks.setEditable(choPhepChinhSua);
        btnLuu.setEnabled(choPhepChinhSua);
        btnLuu.setVisible(choPhepChinhSua);
        btnLuu.setMaximumSize(new Dimension(Short.MAX_VALUE, MauUngDung.CAO_NUT));
        btnLuu.setPreferredSize(new Dimension(280, MauUngDung.CAO_NUT));
        khungBieuMauTrai.add(btnLuu);
    }

    private static JPanel taoThanhQuickTag(JTextArea mucTieu, String... tags) {
        JPanel thanhTag = new JPanel(new GridLayout(0, 2, 6, 6));
        thanhTag.setOpaque(false);
        thanhTag.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String tag : tags) {
            NutDinhDang btnTag = new NutDinhDang("+ " + tag, NutDinhDang.KieuNut.TRONG_SUOT);
            btnTag.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
            btnTag.addActionListener(
                    e -> {
                        String hienTai = mucTieu.getText().trim();
                        if (hienTai.contains(tag)) return;
                        mucTieu.setText(hienTai.isEmpty() ? tag : hienTai + ", " + tag);
                    });
            thanhTag.add(btnTag);
        }
        thanhTag.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, thanhTag.getPreferredSize().height + 2));
        return thanhTag;
    }

    private static JPanel taoHangChonAnh(
            JDialog dialog,
            ChamSocService chamSocService,
            boolean choPhepChinhSua,
            Path[] images,
            JLabel[] nhanAnhDaChon) {
        JPanel khungAnh = new JPanel(new GridLayout(1, 2, 10, 0));
        khungAnh.setOpaque(false);
        khungAnh.setMaximumSize(new Dimension(Short.MAX_VALUE, 68));
        for (int i = 0; i < 2; i++) {
            final int k = i;
            JPanel khungMucAnh = new JPanel(new BorderLayout(4, 4));
            khungMucAnh.setOpaque(false);
            NutDinhDang btnChonAnh =
                    new NutDinhDang(
                            i == 0 ? "Chọn ảnh trước" : "Chọn ảnh sau", NutDinhDang.KieuNut.PHU);
            btnChonAnh.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            btnChonAnh.setEnabled(choPhepChinhSua);
            NutDinhDang btnXoaAnh = new NutDinhDang("Xóa", NutDinhDang.KieuNut.TRONG_SUOT);
            btnXoaAnh.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
            btnXoaAnh.setEnabled(false);
            JLabel nhanTenAnh = new JLabel("Chưa chọn");
            nhanTenAnh.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 11));
            nhanTenAnh.setForeground(MauUngDung.VAN_BAN_XAM);
            nhanAnhDaChon[k] = nhanTenAnh;
            btnChonAnh.addActionListener(
                    e -> {
                        try {
                            chamSocService.kiemTraThuMucAnhDungChung();
                        } catch (RuntimeException loi) {
                            HopThoai.hienThiCanhBao(dialog, loi.getMessage());
                            return;
                        }
                        JFileChooser c = new JFileChooser();
                        c.setFileFilter(
                                new FileNameExtensionFilter("Ảnh JPEG/PNG", "jpg", "jpeg", "png"));
                        if (c.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                            images[k] = c.getSelectedFile().toPath();
                            String tenFile = images[k].getFileName().toString();
                            nhanTenAnh.setText(
                                    tenFile.length() > 16
                                            ? tenFile.substring(0, 14) + "..."
                                            : tenFile);
                            nhanTenAnh.setForeground(MauUngDung.XANH_LA);
                            nhanTenAnh.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
                            btnXoaAnh.setEnabled(true);
                        }
                    });
            btnXoaAnh.addActionListener(
                    e -> {
                        images[k] = null;
                        nhanTenAnh.setText("Chưa chọn");
                        nhanTenAnh.setForeground(MauUngDung.VAN_BAN_XAM);
                        nhanTenAnh.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 11));
                        btnXoaAnh.setEnabled(false);
                    });
            JPanel hangNutAnh = new JPanel(new BorderLayout(4, 0));
            hangNutAnh.setOpaque(false);
            hangNutAnh.add(btnChonAnh, BorderLayout.CENTER);
            hangNutAnh.add(btnXoaAnh, BorderLayout.EAST);
            khungMucAnh.add(hangNutAnh, BorderLayout.NORTH);
            khungMucAnh.add(nhanTenAnh, BorderLayout.CENTER);
            khungAnh.add(khungMucAnh);
        }
        return khungAnh;
    }

    // 3. LỊCH SỬ CHĂM SÓC & XEM TRƯỚC HTML

    private static JPanel taoKhungLichSuPhai(
            JDialog dialog,
            JList<String> list,
            JEditorPane preview,
            List<NhatKyChamSoc> data,
            NutDinhDang btnIn) {
        JPanel khungPhai = new BoGocPanel(12, Color.WHITE);
        khungPhai.setLayout(new BorderLayout(8, 8));
        khungPhai.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel nhanTieuDeLichSu = new JLabel("Lịch sử chăm sóc");
        nhanTieuDeLichSu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanTieuDeLichSu.setForeground(MauUngDung.DO_THUONG_HIEU);
        khungPhai.add(nhanTieuDeLichSu, BorderLayout.NORTH);
        list.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBackground(MauUngDung.mau(0xFAF8F5));
        list.setBorder(new EmptyBorder(4, 4, 4, 4));
        JScrollPane spList = new JScrollPane(list);
        spList.setPreferredSize(new Dimension(300, 110));
        spList.setBorder(new LineBorder(MauUngDung.VIEN, 1, true));
        preview.setEditable(false);
        preview.setBackground(Color.WHITE);
        preview.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane spPreview = new JScrollPane(preview);
        spPreview.setPreferredSize(new Dimension(460, 320));
        spPreview.setBorder(new LineBorder(MauUngDung.VIEN, 1, true));
        JPanel khungXemChiTiet = new JPanel(new BorderLayout(6, 6));
        khungXemChiTiet.setOpaque(false);
        khungXemChiTiet.add(spList, BorderLayout.NORTH);
        khungXemChiTiet.add(spPreview, BorderLayout.CENTER);
        btnIn.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        btnIn.setPreferredSize(new Dimension(180, MauUngDung.CAO_NUT));
        btnIn.addActionListener(
                e -> {
                    if (data.isEmpty() || list.getSelectedIndex() < 0) {
                        HopThoai.hienThiCanhBao(
                                dialog, "Chưa có phiếu chăm sóc để in hoặc xuất PDF.");
                        return;
                    }
                    try {
                        PrinterJob job = PrinterJob.getPrinterJob();
                        job.setPrintable(preview.getPrintable(null, null));
                        if (!job.printDialog()) return;
                        TacVuNen.chay(
                                () -> {
                                    job.print();
                                    return null;
                                },
                                v -> {},
                                ex -> HopThoai.hienThiCanhBao(dialog, TacVuNen.thongDiepLoi(ex)));
                    } catch (Exception ex) {
                        HopThoai.hienThiCanhBao(dialog, ex.getMessage());
                    }
                });
        JPanel khungDuoiPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        khungDuoiPhai.setOpaque(false);
        khungDuoiPhai.add(btnIn);
        khungPhai.add(khungXemChiTiet, BorderLayout.CENTER);
        khungPhai.add(khungDuoiPhai, BorderLayout.SOUTH);
        return khungPhai;
    }

    private static void taiNhatKy(
            JDialog dialog,
            ChamSocService chamSocService,
            String loai,
            String ma,
            List<NhatKyChamSoc> data,
            Map<String, String> uriAnh,
            DefaultListModel<String> model,
            JList<String> list,
            JEditorPane preview,
            NutDinhDang btnIn) {
        TacVuNen.chay(
                () -> {
                    List<NhatKyChamSoc> ds = chamSocService.lay(loai, ma);
                    Map<String, String> uris = new HashMap<>();
                    for (NhatKyChamSoc r : ds) {
                        kiemTraTepAnh(uris, r.getAnhTruoc());
                        kiemTraTepAnh(uris, r.getAnhSau());
                    }
                    return new Object[] {ds, uris};
                },
                kq -> {
                    @SuppressWarnings("unchecked")
                    List<NhatKyChamSoc> ketQua = (List<NhatKyChamSoc>) kq[0];
                    @SuppressWarnings("unchecked")
                    Map<String, String> uris = (Map<String, String>) kq[1];
                    uriAnh.clear();
                    uriAnh.putAll(uris);
                    data.clear();
                    data.addAll(ketQua);
                    model.clear();
                    for (NhatKyChamSoc r : data) {
                        String tg =
                                r.getThoiGian() == null
                                        ? ""
                                        : r.getThoiGian().format(DinhDang.NGAY_GIO);
                        String tenNv =
                                r.getNhanVien() != null && r.getNhanVien().getHoTen() != null
                                        ? r.getNhanVien().getHoTen()
                                        : "";
                        model.addElement(tg + " · " + tenNv);
                    }
                    btnIn.setEnabled(!data.isEmpty());
                    if (!data.isEmpty()) {
                        list.setSelectedIndex(0);
                    } else {
                        preview.setText(
                                "<html><body style='font-family:sans-serif; color:#777;"
                                        + " padding:20px; text-align:center;'><p"
                                        + " style='font-size:14px; font-weight:bold;"
                                        + " color:#B91C1C;'>Chưa có phiếu chăm sóc nào</p><p>Nhập"
                                        + " thông tin ở form bên trái và nhấn <b>Lưu phiếu chăm"
                                        + " sóc</b> để ghi nhận nhật ký.</p></body></html>");
                    }
                },
                ex -> HopThoai.hienThiCanhBao(dialog, TacVuNen.thongDiepLoi(ex)));
    }

    private static void kiemTraTepAnh(Map<String, String> uris, String duongDan) {
        if (duongDan == null || duongDan.isBlank()) return;
        String key = duongDan.trim();
        if (uris.containsKey(key)) return;
        try {
            Path p = Path.of(key);
            uris.put(key, Files.isRegularFile(p) ? p.toUri().toString() : "");
        } catch (InvalidPathException ex) {
            uris.put(key, null);
        }
    }

    private static void capNhatXemTruoc(
            JList<String> list,
            List<NhatKyChamSoc> data,
            Map<String, String> uriAnh,
            JEditorPane preview) {
        int i = list.getSelectedIndex();
        if (i < 0 || i >= data.size()) return;
        NhatKyChamSoc r = data.get(i);
        String tg = r.getThoiGian() == null ? "" : r.getThoiGian().format(DinhDang.NGAY_GIO);
        StringBuilder h =
                new StringBuilder(
                        "<html><body style='font-family:Segoe UI, sans-serif; margin:8px;"
                                + " color:#2A2521;'><div style='border-bottom:2px solid #B91C1C;"
                                + " padding-bottom:6px; margin-bottom:10px;'><span"
                                + " style='font-size:15px; font-weight:bold; color:#B91C1C;'>Phiếu"
                                + " chăm sóc thú cưng</span><br><span style='font-size:11px;"
                                + " color:#6B6560;'>Thời gian: <b>");
        String tenNv =
                r.getNhanVien() != null && r.getNhanVien().getHoTen() != null
                        ? r.getNhanVien().getHoTen()
                        : "";
        h.append(DinhDang.thoatHtmlXuongDong(tg))
                .append("</b> | Nhân viên: <b>")
                .append(DinhDang.thoatHtmlXuongDong(tenNv))
                .append("</b></span></div>");
        String[] labels = {
            "Tình trạng sức khỏe", "Chế độ ăn uống", "Thuốc & lưu ý", "Việc đã thực hiện"
        };
        String[] noiDung = {
            r.getTinhTrang(), r.getAnUong(), r.getHuongDanThuoc(), r.getDaThucHien()
        };
        for (int j = 0; j < labels.length; j++) {
            String val = DinhDang.thoatHtmlXuongDong(noiDung[j]);
            if (val.isBlank()) val = "<i>(Không có)</i>";
            h.append("<div style='margin-bottom:8px;'><div style='font-weight:bold;")
                    .append(" font-size:12px; color:#991B1B;'>")
                    .append(labels[j])
                    .append(":</div><div style='background:#FAF8F5; border:1px solid #E8E2D9;")
                    .append(" border-radius:4px; padding:6px 8px; font-size:12px;'>")
                    .append(val)
                    .append("</div></div>");
        }
        themHtmlHinhAnh(h, uriAnh, r.getAnhTruoc(), r.getAnhSau());
        preview.setText(h.append("</body></html>").toString());
        preview.setCaretPosition(0);
    }

    private static void themHtmlHinhAnh(
            StringBuilder h, Map<String, String> uriAnh, String anhTruoc, String anhSau) {
        if (anhTruoc == null && anhSau == null) return;
        String[] anh = {anhTruoc, anhSau};
        h.append(
                "<div style='font-weight:bold; font-size:12px; color:#991B1B;"
                        + " margin-top:10px;'>Hình ảnh ghi nhận:</div><table style='width:100%;"
                        + " margin-top:4px;'><tr>");
        for (int j = 0; j < 2; j++) {
            h.append(
                            "<td style='vertical-align:top; width:50%; text-align:center;'><div"
                                    + " style='font-size:11px; font-weight:bold; color:#555;'>")
                    .append(j == 0 ? "Trước khi làm" : "Sau khi làm")
                    .append("</div>");
            if (anh[j] != null && !anh[j].isBlank()) {
                String key = anh[j].trim();
                String uri = uriAnh.get(key);
                if (!uriAnh.containsKey(key)) {
                    h.append(
                            "<div style='color:#999; font-size:11px; padding:20px; border:1px"
                                    + " dashed #ccc;'>Đang kiểm tra ảnh...</div>");
                } else if (uri == null) {
                    h.append(
                            "<div style='color:#999; font-size:11px; padding:20px; border:1px"
                                    + " dashed #ccc;'>Đường dẫn ảnh không hợp lệ</div>");
                } else if (uri.isEmpty()) {
                    h.append(
                            "<div style='color:#999; font-size:11px; padding:20px; border:1px"
                                    + " dashed #ccc;'>Không tìm thấy tệp ảnh</div>");
                } else {
                    h.append(
                                    "<img style='border:1px solid #ccc; border-radius:4px;"
                                            + " margin-top:4px;' width='200' src='")
                            .append(uri)
                            .append("'>");
                }
            } else {
                h.append(
                        "<div style='color:#aaa; font-size:11px; padding:20px; border:1px dashed"
                                + " #ccc;'>Không có ảnh</div>");
            }
            h.append("</td>");
        }
        h.append("</tr></table>");
    }

    // 4. LƯU NHẬT KÝ & TIỆN ÍCH NỘI BỘ

    private static void luuPhieuChamSoc(
            JDialog dialog,
            ChamSocService chamSocService,
            String loai,
            String ma,
            JTextArea health,
            JTextArea food,
            JTextArea medicine,
            JTextArea tasks,
            Path[] images,
            JLabel[] nhanAnhDaChon,
            NutDinhDang save,
            Runnable load) {
        String a = health.getText().trim(),
                b = food.getText().trim(),
                c = medicine.getText().trim(),
                d = tasks.getText().trim();
        if (a.isEmpty()) {
            HopThoai.hienThiCanhBao(dialog, "Vui lòng nhập tình trạng sức khỏe tiếp nhận.");
            return;
        }
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        if (phien == null) {
            HopThoai.hienThiCanhBao(dialog, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
            return;
        }
        Path before = images[0], after = images[1];
        save.setEnabled(false);
        TacVuNen.chay(
                () ->
                        PhienDangNhapService.macDinh()
                                .trongPhien(
                                        phien,
                                        () -> {
                                            chamSocService.luu(loai, ma, a, b, c, d, before, after);
                                            return null;
                                        }),
                v -> {
                    save.setEnabled(true);
                    health.setText("");
                    food.setText("");
                    medicine.setText("");
                    tasks.setText("");
                    images[0] = images[1] = null;
                    for (JLabel nhanChu : nhanAnhDaChon) {
                        nhanChu.setText("Chưa chọn");
                        nhanChu.setForeground(MauUngDung.VAN_BAN_XAM);
                        nhanChu.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 11));
                    }
                    HopThoai.hienThiThanhCong(dialog, "Đã lưu phiếu chăm sóc thành công.");
                    load.run();
                },
                ex -> {
                    save.setEnabled(true);
                    HopThoai.hienThiCanhBao(dialog, TacVuNen.thongDiepLoi(ex));
                });
    }

    private static JTextArea taoTextArea(int rows) {
        JTextArea area = new JTextArea(rows, 24);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        area.setBorder(
                new CompoundBorder(
                        new LineBorder(MauUngDung.VIEN, 1, true), new EmptyBorder(4, 6, 4, 6)));
        return area;
    }

    private static void themTruong(JPanel form, String nhan, JComponent comp) {
        JLabel nhanChu = new JLabel(nhan);
        nhanChu.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        nhanChu.setForeground(MauUngDung.VAN_BAN_TOI);
        themPhanTu(form, nhanChu);
        form.add(Box.createVerticalStrut(4));
        themPhanTu(form, comp);
        form.add(Box.createVerticalStrut(8));
    }

    private static void themPhanTu(JPanel form, JComponent comp) {
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(comp);
    }

    private static JScrollPane taoCuon(JTextArea area, int height) {
        JScrollPane sp = new JScrollPane(area);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setPreferredSize(new Dimension(320, height));
        sp.setMaximumSize(new Dimension(Short.MAX_VALUE, height + 10));
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }
}
