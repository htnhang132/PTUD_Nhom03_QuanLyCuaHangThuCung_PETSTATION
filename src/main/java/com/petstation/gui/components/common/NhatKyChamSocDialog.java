package com.petstation.gui.components.common;

import com.petstation.gui.components.HopThoai;
import com.petstation.service.ChamSocService;

import java.awt.*;
import java.nio.file.*;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class NhatKyChamSocDialog {
    private NhatKyChamSocDialog() {}

    public static void mo(Component cha, String loai, long id) {
        JDialog dialog =
                new JDialog(
                        SwingUtilities.getWindowAncestor(cha),
                        "Phiếu chăm sóc · " + loai + " #" + id,
                        Dialog.ModalityType.APPLICATION_MODAL);
        JTextArea health = new JTextArea(3, 30),
                food = new JTextArea(2, 30),
                medicine = new JTextArea(2, 30),
                tasks = new JTextArea(3, 30);
        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Tình trạng tiếp nhận *"));
        form.add(new JScrollPane(health));
        form.add(new JLabel("Hướng dẫn ăn uống"));
        form.add(new JScrollPane(food));
        form.add(new JLabel("Hướng dẫn thuốc do chủ nuôi cung cấp"));
        form.add(new JScrollPane(medicine));
        form.add(new JLabel("Việc đã thực hiện"));
        form.add(new JScrollPane(tasks));
        var phien = com.petstation.service.PhienDangNhapService.layPhien();
        Path[] images = new Path[2];
        for (int i = 0; i < 2; i++) {
            final int k = i;
            JButton b =
                    new JButton(
                            i == 0
                                    ? "Chọn ảnh trước (JPEG/PNG ≤10 MB)"
                                    : "Chọn ảnh sau (JPEG/PNG ≤10 MB)");
            b.addActionListener(
                    e -> {
                        JFileChooser c = new JFileChooser();
                        c.setFileFilter(
                                new FileNameExtensionFilter("Ảnh JPEG/PNG", "jpg", "jpeg", "png"));
                        if (c.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                            images[k] = c.getSelectedFile().toPath();
                            b.setText(images[k].getFileName().toString());
                        }
                    });
            form.add(b);
        }
        JButton save = new JButton("Lưu phiếu chăm sóc");
        form.add(save);
        JEditorPane preview = new JEditorPane("text/html", "");
        preview.setEditable(false);
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        final java.util.List<Object[]> data = new java.util.ArrayList<>();
        Runnable load =
                () ->
                        new SwingWorker<List<Object[]>, Void>() {
                            protected List<Object[]> doInBackground() {
                                return ChamSocService.lay(loai, id);
                            }

                            protected void done() {
                                try {
                                    data.clear();
                                    data.addAll(get());
                                    model.clear();
                                    for (Object[] r : data) model.addElement(r[1] + " · " + r[2]);
                                    if (!data.isEmpty()) list.setSelectedIndex(0);
                                    else
                                        preview.setText(
                                                "<html>Chưa có phiếu chăm sóc bổ sung. Nhật ký cũ"
                                                        + " vẫn được giữ tại Xem nhật ký.</html>");
                                } catch (Exception ex) {
                                    HopThoai.hienThiCanhBao(dialog, loi(ex));
                                }
                            }
                        }.execute();
        list.addListSelectionListener(
                e -> {
                    int i = list.getSelectedIndex();
                    if (i >= 0 && i < data.size()) {
                        Object[] r = data.get(i);
                        StringBuilder h =
                                new StringBuilder(
                                        "<html><body style='font-family:sans-serif'><h2>Phiếu chăm"
                                                + " sóc "
                                                + loai
                                                + " #"
                                                + id
                                                + "</h2>");
                        String[] labels = {
                            "",
                            "Thời gian",
                            "Người ghi",
                            "Tình trạng",
                            "Ăn uống",
                            "Thuốc",
                            "Đã thực hiện"
                        };
                        for (int j = 1; j <= 6; j++)
                            h.append("<p><b>")
                                    .append(labels[j])
                                    .append(":</b> ")
                                    .append(escape(r[j]))
                                    .append("</p>");
                        for (int j = 7; j <= 8; j++)
                            if (r[j] != null) {
                                Path path = Path.of(r[j].toString());
                                h.append("<p>").append(j == 7 ? "Trước" : "Sau").append("</p>");
                                if (Files.isRegularFile(path))
                                    h.append("<img width='280' src='")
                                            .append(path.toUri())
                                            .append("'>");
                                else h.append("<p>Không tìm thấy ảnh đã lưu</p>");
                            }
                        preview.setText(h.append("</body></html>").toString());
                        preview.setCaretPosition(0);
                    }
                });
        save.addActionListener(
                e -> {
                    String a = health.getText(),
                            b = food.getText(),
                            c = medicine.getText(),
                            d = tasks.getText();
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
                                load.run();
                            } catch (Exception ex) {
                                HopThoai.hienThiCanhBao(dialog, loi(ex));
                            }
                        }
                    }.execute();
                });
        JPanel viewer = new JPanel(new BorderLayout(4, 4));
        JScrollPane history = new JScrollPane(list);
        history.setPreferredSize(new Dimension(280, 85));
        viewer.add(history, BorderLayout.NORTH);
        viewer.add(new JScrollPane(preview));
        JButton print = new JButton("In / Xuất phiếu PDF…");
        print.addActionListener(
                e -> {
                    try {
                        preview.print();
                    } catch (Exception ex) {
                        HopThoai.hienThiCanhBao(dialog, ex.getMessage());
                    }
                });
        viewer.add(print, BorderLayout.SOUTH);
        JSplitPane split =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(form), viewer);
        split.setResizeWeight(.45);
        dialog.add(split);
        dialog.setSize(1050, 680);
        dialog.setLocationRelativeTo(cha);
        load.run();
        dialog.setVisible(true);
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
