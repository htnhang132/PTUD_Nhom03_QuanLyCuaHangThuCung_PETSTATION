package com.petstation.gui.components;

import com.petstation.gui.navigation.NguCanhMoBanGhi;
import com.petstation.service.*;
import com.petstation.service.ViecCanLamService.Viec;

import java.awt.*;
import java.time.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class ViecCanLamPanel extends JPanel {
    private final DefaultTableModel model =
            new DefaultTableModel(new String[] {"Việc cần làm", "Thời hạn"}, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
    private final JTable table = new JTable(model);
    private List<Viec> data = List.of();
    private int version;

    public ViecCanLamPanel(Consumer<com.petstation.model.MaManHinh> nav) {
        super(new BorderLayout(8, 8));
        setOpaque(false);
        ThanhDanhSach bar = new ThanhDanhSach();
        JButton reload = new JButton("Làm mới"),
                open = new JButton("Mở xử lý"),
                seen = new JButton("Đã xem"),
                later = new JButton("Hoãn 30 phút");
        bar.themBoLoc(new JLabel("Việc cần làm và cảnh báo"));
        bar.add(reload);
        bar.add(open);
        bar.add(seen);
        bar.add(later);
        add(bar, BorderLayout.NORTH);
        com.petstation.gui.components.common.DinhDangBang.apDung(table);
        add(new JScrollPane(table));
        reload.addActionListener(e -> taiLai());
        open.addActionListener(
                e -> {
                    Viec v = chon();
                    if (v == null) return;
                    NguCanhMoBanGhi.dat(v.dich(), v.id(), v.ma());
                    Window w = SwingUtilities.getWindowAncestor(this);
                    if (w instanceof JDialog d) d.dispose();
                    nav.accept(v.dich());
                });
        seen.addActionListener(e -> luu(true));
        later.addActionListener(e -> luu(false));
        table.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (e.getClickCount() == 2) open.doClick();
                    }
                });
        taiLai();
    }

    private Viec chon() {
        int row = table.getSelectedRow();
        return row < 0 ? null : data.get(table.convertRowIndexToModel(row));
    }

    private void luu(boolean seen) {
        Viec v = chon();
        if (v == null) return;
        var session = PhienDangNhapService.layPhien();
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                return PhienDangNhapService.trongPhien(
                        session,
                        () -> {
                            ViecCanLamService.danhDau(
                                    v.khoa(),
                                    seen,
                                    seen ? null : LocalDateTime.now().plusMinutes(30));
                            return null;
                        });
            }

            protected void done() {
                try {
                    get();
                    taiLai();
                } catch (Exception ex) {
                    HopThoai.hienThiCanhBao(
                            ViecCanLamPanel.this,
                            ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage());
                }
            }
        }.execute();
    }

    public void taiLai() {
        int token = ++version;
        var session = PhienDangNhapService.layPhienHienTai();
        if (session == null) return;
        new SwingWorker<List<Viec>, Void>() {
            protected List<Viec> doInBackground() {
                return ViecCanLamService.lay();
            }

            protected void done() {
                if (token != version || PhienDangNhapService.layPhienHienTai() != session) return;
                try {
                    data = get();
                    model.setRowCount(0);
                    for (Viec v : data)
                        model.addRow(
                                new Object[] {
                                    v.noiDung(),
                                    v.han() == null
                                            ? "—"
                                            : v.han()
                                                    .format(
                                                            java.time.format.DateTimeFormatter
                                                                    .ofPattern("dd/MM/yyyy HH:mm"))
                                });
                } catch (Exception ex) {
                    setToolTipText("Không tải được việc cần làm: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
