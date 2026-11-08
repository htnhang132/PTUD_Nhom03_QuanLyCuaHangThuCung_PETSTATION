package com.petstation.gui.components.common;

import com.petstation.gui.components.BoGocPanel;
import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.MauUngDung;
import com.petstation.gui.components.NutDinhDang;
import com.petstation.service.ChamSocService;

import java.awt.*;
import java.nio.file.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class NhatKyChamSocDialog {
    private NhatKyChamSocDialog() {
    }

    public static void mo(Component cha, String loai, long id) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(cha),
                "Phiếu chăm sóc",
                Dialog.ModalityType.APPLICATION_MODAL);

        dialog.getContentPane().setBackground(MauUngDung.NEN_KEM);

        JPanel pnlChinh = new JPanel(new BorderLayout(14, 14));
        pnlChinh.setBackground(MauUngDung.NEN_KEM);
        pnlChinh.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Header Panel
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblTieuDe = new JLabel("PHIẾU CHĂM SÓC THÚ CƯNG");
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        lblTieuDe.setForeground(MauUngDung.DO_THUONG_HIEU);
        JLabel lblPhuDe = new JLabel(
                "Theo dõi tình trạng sức khỏe, dinh dưỡng, thuốc dùng và việc chăm sóc qua từng ca");
        lblPhuDe.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblPhuDe.setForeground(MauUngDung.VAN_BAN_XAM);
        pnlHeader.add(lblTieuDe, BorderLayout.NORTH);
        pnlHeader.add(lblPhuDe, BorderLayout.SOUTH);
        pnlChinh.add(pnlHeader, BorderLayout.NORTH);

        // Form fields bên trái
        JTextArea health = taoTextArea(3);
        JTextArea food = taoTextArea(2);
        JTextArea medicine = taoTextArea(2);
        JTextArea tasks = taoTextArea(3);

        JPanel pnlFormTrai = new BoGocPanel(12, Color.WHITE);
        pnlFormTrai.setLayout(new BoxLayout(pnlFormTrai, BoxLayout.Y_AXIS));
        pnlFormTrai.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblFormTitle = new JLabel("Ghi nhận chăm sóc mới");
        lblFormTitle.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblFormTitle.setForeground(MauUngDung.DO_THUONG_HIEU);
        themPhanTu(pnlFormTrai, lblFormTitle);
        pnlFormTrai.add(Box.createVerticalStrut(10));

        themTruong(pnlFormTrai, "Tình trạng sức khỏe khi nhận *", taoCuon(health, 60));
        themTruong(pnlFormTrai, "Hướng dẫn ăn uống & khẩu phần", taoCuon(food, 50));
        themTruong(pnlFormTrai, "Thuốc & chỉ định từ chủ nuôi", taoCuon(medicine, 50));
        themTruong(pnlFormTrai, "Việc đã thực hiện trong ca", taoCuon(tasks, 60));

        var phien = com.petstation.service.PhienDangNhapService.layPhien();
        Path[] images = new Path[2];
        JLabel[] lblAnhDaChon = new JLabel[2];

        JPanel pnlAnh = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlAnh.setOpaque(false);
        pnlAnh.setMaximumSize(new Dimension(Short.MAX_VALUE, 58));

        for (int i = 0; i < 2; i++) {
            final int k = i;
            JPanel pnlMucAnh = new JPanel(new BorderLayout(4, 4));
            pnlMucAnh.setOpaque(false);

            NutDinhDang btnChonAnh = new NutDinhDang(
                    i == 0 ? "Chọn ảnh trước" : "Chọn ảnh sau",
                    NutDinhDang.KieuNut.PHU);
            btnChonAnh.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            btnChonAnh.setPreferredSize(new Dimension(130, 32));

            JLabel lblTenAnh = new JLabel("Chưa chọn");
            lblTenAnh.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 11));
            lblTenAnh.setForeground(MauUngDung.VAN_BAN_XAM);
            lblAnhDaChon[k] = lblTenAnh;

            btnChonAnh.addActionListener(e -> {
                try {
                    ChamSocService.kiemTraThuMucAnhDungChung();
                } catch (RuntimeException loi) {
                    HopThoai.hienThiCanhBao(dialog, loi.getMessage());
                    return;
                }
                JFileChooser c = new JFileChooser();
                c.setFileFilter(new FileNameExtensionFilter("Ảnh JPEG/PNG", "jpg", "jpeg", "png"));
                if (c.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    images[k] = c.getSelectedFile().toPath();
                    String tenFile = images[k].getFileName().toString();
                    lblTenAnh.setText(tenFile.length() > 16 ? tenFile.substring(0, 14) + "..." : tenFile);
                    lblTenAnh.setForeground(MauUngDung.XANH_LA);
                    lblTenAnh.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
                }
            });

            pnlMucAnh.add(btnChonAnh, BorderLayout.NORTH);
            pnlMucAnh.add(lblTenAnh, BorderLayout.CENTER);
            pnlAnh.add(pnlMucAnh);
        }
        themPhanTu(pnlFormTrai, pnlAnh);
        pnlFormTrai.add(Box.createVerticalStrut(6));

        JLabel sharedHint = new JLabel(
                "<html><i>* Ảnh được lưu vào thư mục mạng UNC đã cấu hình để các máy cùng xem.</i></html>");
        sharedHint.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 11));
        sharedHint.setForeground(MauUngDung.VAN_BAN_XAM);
        themPhanTu(pnlFormTrai, sharedHint);
        pnlFormTrai.add(Box.createVerticalStrut(10));

        NutDinhDang save = new NutDinhDang("Lưu phiếu chăm sóc", NutDinhDang.KieuNut.CHINH);
        save.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
        save.setPreferredSize(new Dimension(280, 38));
        themPhanTu(pnlFormTrai, save);

        // Khung bên phải: Lịch sử & Chi tiết
        JPanel pnlPhai = new BoGocPanel(12, Color.WHITE);
        pnlPhai.setLayout(new BorderLayout(8, 8));
        pnlPhai.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblHistTitle = new JLabel("Lịch sử chăm sóc");
        lblHistTitle.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblHistTitle.setForeground(MauUngDung.DO_THUONG_HIEU);
        pnlPhai.add(lblHistTitle, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        list.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBackground(new Color(250, 248, 245));
        list.setBorder(new EmptyBorder(4, 4, 4, 4));

        JScrollPane spList = new JScrollPane(list);
        spList.setPreferredSize(new Dimension(300, 110));
        spList.setBorder(new LineBorder(MauUngDung.VIEN, 1, true));

        JEditorPane preview = new JEditorPane("text/html", "");
        preview.setEditable(false);
        preview.setBackground(Color.WHITE);
        preview.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane spPreview = new JScrollPane(preview);
        spPreview.setBorder(new LineBorder(MauUngDung.VIEN, 1, true));

        JPanel pnlXemChiTiet = new JPanel(new BorderLayout(6, 6));
        pnlXemChiTiet.setOpaque(false);
        pnlXemChiTiet.add(spList, BorderLayout.NORTH);
        pnlXemChiTiet.add(spPreview, BorderLayout.CENTER);

        NutDinhDang print = new NutDinhDang("In / Xuất phiếu PDF…", NutDinhDang.KieuNut.PHU);
        print.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        print.setPreferredSize(new Dimension(180, 36));
        print.addActionListener(e -> {
            try {
                preview.print();
            } catch (Exception ex) {
                HopThoai.hienThiCanhBao(dialog, ex.getMessage());
            }
        });

        JPanel pnlBottomPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBottomPhai.setOpaque(false);
        pnlBottomPhai.add(print);

        pnlPhai.add(pnlXemChiTiet, BorderLayout.CENTER);
        pnlPhai.add(pnlBottomPhai, BorderLayout.SOUTH);

        final java.util.List<Object[]> data = new java.util.ArrayList<>();
        Runnable load = () -> new SwingWorker<List<Object[]>, Void>() {
            protected List<Object[]> doInBackground() {
                return ChamSocService.lay(loai, id);
            }

            protected void done() {
                try {
                    data.clear();
                    data.addAll(get());
                    model.clear();
                    for (Object[] r : data) {
                        model.addElement(r[1] + " · " + r[2]);
                    }
                    if (!data.isEmpty()) {
                        list.setSelectedIndex(0);
                    } else {
                        preview.setText(
                                "<html><body style='font-family:sans-serif; color:#777; padding:20px; text-align:center;'>"
                                        + "<p style='font-size:14px; font-weight:bold; color:#B91C1C;'>Chưa có phiếu chăm sóc nào</p>"
                                        + "<p>Nhập thông tin ở form bên trái và nhấn <b>Lưu phiếu chăm sóc</b> để ghi nhận nhật ký.</p>"
                                        + "</body></html>");
                    }
                } catch (Exception ex) {
                    HopThoai.hienThiCanhBao(dialog, loi(ex));
                }
            }
        }.execute();

        list.addListSelectionListener(e -> {
            int i = list.getSelectedIndex();
            if (i >= 0 && i < data.size()) {
                Object[] r = data.get(i);
                StringBuilder h = new StringBuilder();
                h.append("<html><body style='font-family:Segoe UI, sans-serif; margin:8px; color:#2A2521;'>");
                h.append("<div style='border-bottom:2px solid #B91C1C; padding-bottom:6px; margin-bottom:10px;'>");
                h.append(
                        "<span style='font-size:15px; font-weight:bold; color:#B91C1C;'>Phiếu chăm sóc thú cưng</span><br>");
                h.append("<span style='font-size:11px; color:#6B6560;'>Thời gian: <b>").append(escape(r[1]))
                        .append("</b> | Nhân viên: <b>").append(escape(r[2])).append("</b></span>");
                h.append("</div>");

                String[] labels = { "", "Thời gian", "Người ghi", "Tình trạng sức khỏe", "Chế độ ăn uống",
                        "Thuốc & lưu ý", "Việc đã thực hiện" };
                for (int j = 3; j <= 6; j++) {
                    String val = escape(r[j]);
                    if (val.isBlank())
                        val = "<i>(Không có)</i>";
                    h.append("<div style='margin-bottom:8px;'>");
                    h.append("<div style='font-weight:bold; font-size:12px; color:#991B1B;'>").append(labels[j])
                            .append(":</div>");
                    h.append(
                            "<div style='background:#FAF8F5; border:1px solid #E8E2D9; border-radius:4px; padding:6px 8px; font-size:12px;'>")
                            .append(val).append("</div>");
                    h.append("</div>");
                }

                boolean coAnh = false;
                for (int j = 7; j <= 8; j++) {
                    if (r[j] != null)
                        coAnh = true;
                }
                if (coAnh) {
                    h.append(
                            "<div style='font-weight:bold; font-size:12px; color:#991B1B; margin-top:10px;'>Hình ảnh ghi nhận:</div>");
                    h.append("<table style='width:100%; margin-top:4px;'><tr>");
                    for (int j = 7; j <= 8; j++) {
                        h.append("<td style='vertical-align:top; width:50%; text-align:center;'>");
                        h.append("<div style='font-size:11px; font-weight:bold; color:#555;'>")
                                .append(j == 7 ? "Trước khi làm" : "Sau khi làm").append("</div>");
                        if (r[j] != null) {
                            Path path = Path.of(r[j].toString());
                            if (Files.isRegularFile(path)) {
                                h.append(
                                        "<img style='border:1px solid #ccc; border-radius:4px; margin-top:4px;' width='200' src='")
                                        .append(path.toUri()).append("'>");
                            } else {
                                h.append(
                                        "<div style='color:#999; font-size:11px; padding:20px; border:1px dashed #ccc;'>Không tìm thấy tệp ảnh</div>");
                            }
                        } else {
                            h.append(
                                    "<div style='color:#aaa; font-size:11px; padding:20px; border:1px dashed #ccc;'>Không có ảnh</div>");
                        }
                        h.append("</td>");
                    }
                    h.append("</tr></table>");
                }

                h.append("</body></html>");
                preview.setText(h.toString());
                preview.setCaretPosition(0);
            }
        });

        save.addActionListener(e -> {
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
            new SwingWorker<Void, Void>() {
                protected Void doInBackground() throws Exception {
                    return com.petstation.service.PhienDangNhapService.trongPhien(
                            phien,
                            () -> {
                                ChamSocService.luu(loai, id, a, b, c, d, before, after);
                                return null;
                            });
                }

                protected void done() {
                    save.setEnabled(true);
                    try {
                        get();
                        health.setText("");
                        food.setText("");
                        medicine.setText("");
                        tasks.setText("");
                        images[0] = images[1] = null;
                        lblAnhDaChon[0].setText("Chưa chọn");
                        lblAnhDaChon[0].setForeground(MauUngDung.VAN_BAN_XAM);
                        lblAnhDaChon[1].setText("Chưa chọn");
                        lblAnhDaChon[1].setForeground(MauUngDung.VAN_BAN_XAM);
                        HopThoai.hienThiThanhCong(dialog, "Đã lưu phiếu chăm sóc thành công.");
                        load.run();
                    } catch (Exception ex) {
                        HopThoai.hienThiCanhBao(dialog, loi(ex));
                    }
                }
            }.execute();
        });

        // Bố cục chia 2 cột cân xứng
        JPanel pnlNoiDung = new JPanel(new GridLayout(1, 2, 14, 0));
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.add(new JScrollPane(pnlFormTrai));
        pnlNoiDung.add(pnlPhai);

        pnlChinh.add(pnlNoiDung, BorderLayout.CENTER);

        dialog.setContentPane(pnlChinh);
        dialog.setSize(1020, 680);
        HopThoai.chuanHoaCuaSo(dialog, cha);
        load.run();
        dialog.setVisible(true);
    }

    private static JTextArea taoTextArea(int rows) {
        JTextArea area = new JTextArea(rows, 24);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        area.setBorder(new CompoundBorder(
                new LineBorder(MauUngDung.VIEN, 1, true),
                new EmptyBorder(4, 6, 4, 6)));
        return area;
    }

    private static void themTruong(JPanel form, String nhan, JComponent comp) {
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        lbl.setForeground(MauUngDung.VAN_BAN_TOI);
        themPhanTu(form, lbl);
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

    private static String loi(Exception e) {
        return e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
    }
}
