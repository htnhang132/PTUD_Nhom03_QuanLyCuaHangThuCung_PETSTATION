package com.petstation.gui.luutru;

import com.petstation.dto.PhienDangNhap;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class NhatKyChamSocDialog {
    private NhatKyChamSocDialog() {}

    public static void mo(Component cha, String loai, String ma) {
        JDialog dialog =
                new JDialog(
                        SwingUtilities.getWindowAncestor(cha),
                        "Phiếu chăm sóc",
                        Dialog.ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().setBackground(MauUngDung.NEN_KEM);
        JPanel khungChinh = new JPanel(new BorderLayout(14, 14));
        khungChinh.setBackground(MauUngDung.NEN_KEM);
        khungChinh.setBorder(new EmptyBorder(14, 14, 14, 14));
        khungChinh.add(taoTieuDeHopThoai(), BorderLayout.NORTH);
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
        JPanel khungPhai = taoKhungLichSuPhai(dialog, list, preview);
        final List<NhatKyChamSoc> data = new ArrayList<>();
        Runnable load =
                () -> taiNhatKy(dialog, chamSocService, loai, ma, data, model, list, preview);
        list.addListSelectionListener(e -> capNhatXemTruoc(list, data, preview));
        var phien = PhienDangNhapService.macDinh().layPhien();
        btnLuu.addActionListener(
                e ->
                        luuPhieuChamSoc(
                                dialog,
                                chamSocService,
                                phien,
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
        JPanel khungNoiDung = new JPanel(new GridLayout(1, 2, 14, 0));
        khungNoiDung.setOpaque(false);
        khungNoiDung.add(new JScrollPane(khungBieuMauTrai));
        khungNoiDung.add(khungPhai);
        khungChinh.add(khungNoiDung, BorderLayout.CENTER);
        dialog.setContentPane(khungChinh);
        dialog.setSize(1020, 680);
        HopThoai.chuanHoaCuaSo(dialog, cha);
        load.run();
        dialog.setVisible(true);
    }

    private static JPanel taoTieuDeHopThoai() {
        JPanel khungDauTrangChinh = new JPanel(new BorderLayout());
        khungDauTrangChinh.setOpaque(false);
        JLabel nhanTieuDe = new JLabel("PHIẾU CHĂM SÓC THÚ CƯNG");
        nhanTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        nhanTieuDe.setForeground(MauUngDung.DO_THUONG_HIEU);
        JLabel nhanPhuDe =
                new JLabel(
                        "Theo dõi tình trạng sức khỏe, dinh dưỡng, thuốc dùng và việc chăm sóc qua"
                                + " từng ca");
        nhanPhuDe.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanPhuDe.setForeground(MauUngDung.VAN_BAN_XAM);
        khungDauTrangChinh.add(nhanTieuDe, BorderLayout.NORTH);
        khungDauTrangChinh.add(nhanPhuDe, BorderLayout.SOUTH);
        return khungDauTrangChinh;
    }

    private static JPanel taoFormTrai(
            JDialog dialog,
            ChamSocService chamSocService,
            JTextArea health,
            JTextArea food,
            JTextArea medicine,
            JTextArea tasks,
            Path[] images,
            JLabel[] nhanAnhDaChon,
            NutDinhDang btnLuu) {
        JPanel khungBieuMauTrai = new BoGocPanel(12, Color.WHITE);
        khungBieuMauTrai.setLayout(new BoxLayout(khungBieuMauTrai, BoxLayout.Y_AXIS));
        khungBieuMauTrai.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel nhanTieuDeBieuMauChinh = new JLabel("Ghi nhận chăm sóc mới");
        nhanTieuDeBieuMauChinh.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanTieuDeBieuMauChinh.setForeground(MauUngDung.DO_THUONG_HIEU);
        themPhanTu(khungBieuMauTrai, nhanTieuDeBieuMauChinh);
        khungBieuMauTrai.add(Box.createVerticalStrut(10));
        themTruong(khungBieuMauTrai, "Tình trạng sức khỏe khi nhận *", taoCuon(health, 60));
        themTruong(khungBieuMauTrai, "Hướng dẫn ăn uống & khẩu phần", taoCuon(food, 50));
        themTruong(khungBieuMauTrai, "Thuốc & chỉ định từ chủ nuôi", taoCuon(medicine, 50));
        themTruong(khungBieuMauTrai, "Việc đã thực hiện trong ca", taoCuon(tasks, 60));
        themPhanTu(khungBieuMauTrai, taoHangChonAnh(dialog, chamSocService, images, nhanAnhDaChon));
        khungBieuMauTrai.add(Box.createVerticalStrut(6));
        JLabel sharedHint =
                new JLabel(
                        "<html><i>* Ảnh được lưu vào thư mục mạng UNC đã cấu hình để các máy cùng"
                                + " xem.</i></html>");
        sharedHint.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        sharedHint.setForeground(MauUngDung.VAN_BAN_XAM);
        themPhanTu(khungBieuMauTrai, sharedHint);
        khungBieuMauTrai.add(Box.createVerticalStrut(10));
        btnLuu.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
        btnLuu.setPreferredSize(new Dimension(280, 38));
        themPhanTu(khungBieuMauTrai, btnLuu);
        return khungBieuMauTrai;
    }

    private static JPanel taoHangChonAnh(
            JDialog dialog, ChamSocService chamSocService, Path[] images, JLabel[] nhanAnhDaChon) {
        JPanel khungAnh = new JPanel(new GridLayout(1, 2, 10, 0));
        khungAnh.setOpaque(false);
        khungAnh.setMaximumSize(new Dimension(Short.MAX_VALUE, 58));
        for (int i = 0; i < 2; i++) {
            final int k = i;
            JPanel khungMucAnh = new JPanel(new BorderLayout(4, 4));
            khungMucAnh.setOpaque(false);
            NutDinhDang btnChonAnh =
                    new NutDinhDang(
                            i == 0 ? "Chọn ảnh trước" : "Chọn ảnh sau", NutDinhDang.KieuNut.PHU);
            btnChonAnh.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            btnChonAnh.setPreferredSize(new Dimension(130, 32));
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
                        }
                    });
            khungMucAnh.add(btnChonAnh, BorderLayout.NORTH);
            khungMucAnh.add(nhanTenAnh, BorderLayout.CENTER);
            khungAnh.add(khungMucAnh);
        }
        return khungAnh;
    }

    private static JPanel taoKhungLichSuPhai(
            JDialog dialog, JList<String> list, JEditorPane preview) {
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
        spPreview.setBorder(new LineBorder(MauUngDung.VIEN, 1, true));
        JPanel khungXemChiTiet = new JPanel(new BorderLayout(6, 6));
        khungXemChiTiet.setOpaque(false);
        khungXemChiTiet.add(spList, BorderLayout.NORTH);
        khungXemChiTiet.add(spPreview, BorderLayout.CENTER);
        NutDinhDang btnIn = new NutDinhDang("In / Xuất phiếu PDF…", NutDinhDang.KieuNut.PHU);
        btnIn.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        btnIn.setPreferredSize(new Dimension(180, 36));
        btnIn.addActionListener(
                e -> {
                    try {
                        preview.print();
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
            DefaultListModel<String> model,
            JList<String> list,
            JEditorPane preview) {
        TacVuNen.chay(
                () -> chamSocService.lay(loai, ma),
                ketQua -> {
                    data.clear();
                    data.addAll(ketQua);
                    model.clear();
                    for (NhatKyChamSoc r : data) {
                        String tg =
                                r.getThoiGian() == null
                                        ? ""
                                        : r.getThoiGian().format(DinhDang.NGAY_GIO_SQL);
                        String tenNv =
                                r.getNhanVien() != null && r.getNhanVien().getHoTen() != null
                                        ? r.getNhanVien().getHoTen()
                                        : "";
                        model.addElement(tg + " · " + tenNv);
                    }
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

    private static void capNhatXemTruoc(
            JList<String> list, List<NhatKyChamSoc> data, JEditorPane preview) {
        int i = list.getSelectedIndex();
        if (i < 0 || i >= data.size()) return;
        NhatKyChamSoc r = data.get(i);
        String tg = r.getThoiGian() == null ? "" : r.getThoiGian().format(DinhDang.NGAY_GIO_SQL);
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
        h.append(escape(tg))
                .append("</b> | Nhân viên: <b>")
                .append(escape(tenNv))
                .append("</b></span></div>");
        String[] labels = {
            "Tình trạng sức khỏe", "Chế độ ăn uống", "Thuốc & lưu ý", "Việc đã thực hiện"
        };
        String[] noiDung = {
            r.getTinhTrang(), r.getAnUong(), r.getHuongDanThuoc(), r.getDaThucHien()
        };
        for (int j = 0; j < labels.length; j++) {
            String val = escape(noiDung[j]);
            if (val.isBlank()) val = "<i>(Không có)</i>";
            h.append("<div style='margin-bottom:8px;'><div style='font-weight:bold;")
                    .append(" font-size:12px; color:#991B1B;'>")
                    .append(labels[j])
                    .append(":</div><div style='background:#FAF8F5; border:1px solid #E8E2D9;")
                    .append(" border-radius:4px; padding:6px 8px; font-size:12px;'>")
                    .append(val)
                    .append("</div></div>");
        }
        themHtmlHinhAnh(h, r.getAnhTruoc(), r.getAnhSau());
        preview.setText(h.append("</body></html>").toString());
        preview.setCaretPosition(0);
    }

    private static void themHtmlHinhAnh(StringBuilder h, String anhTruoc, String anhSau) {
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
            if (anh[j] != null) {
                Path path = Path.of(anh[j]);
                if (Files.isRegularFile(path)) {
                    h.append(
                                    "<img style='border:1px solid #ccc; border-radius:4px;"
                                            + " margin-top:4px;' width='200' src='")
                            .append(path.toUri())
                            .append("'>");
                } else {
                    h.append(
                            "<div style='color:#999; font-size:11px; padding:20px; border:1px"
                                    + " dashed #ccc;'>Không tìm thấy tệp ảnh</div>");
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

    private static void luuPhieuChamSoc(
            JDialog dialog,
            ChamSocService chamSocService,
            PhienDangNhap phien,
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

    private static String escape(Object s) {
        return s == null
                ? ""
                : s.toString()
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\n", "<br>");
    }
}
