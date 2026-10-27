package com.petstation.gui.components;

import com.petstation.model.MaManHinh;
import com.petstation.service.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;

/** Timer gắn vòng đời cửa sổ; không để worker cũ hiện dữ liệu sau đăng xuất. */
public final class NhacViecButton extends JButton {
    private final javax.swing.Timer timer;
    private final Set<String> daBao = new HashSet<>();
    private boolean busy;

    public NhacViecButton(Consumer<MaManHinh> nav) {
        super("Nhắc việc");
        setPreferredSize(new Dimension(155, 36));
        addActionListener(
                e -> {
                    JDialog dialog =
                            new JDialog(
                                    SwingUtilities.getWindowAncestor(this),
                                    "Việc cần làm",
                                    Dialog.ModalityType.MODELESS);
                    dialog.add(new ViecCanLamPanel(nav));
                    dialog.setSize(980, 550);
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                });
        timer = new javax.swing.Timer(60_000, e -> kiemTra());
    }

    @Override
    public void addNotify() {
        super.addNotify();
        timer.start();
        SwingUtilities.invokeLater(this::kiemTra);
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    private void kiemTra() {
        var session = PhienDangNhapService.layPhienHienTai();
        if (busy || session == null) return;
        busy = true;
        new SwingWorker<List<ViecCanLamService.Viec>, Void>() {
            protected List<ViecCanLamService.Viec> doInBackground() {
                return ViecCanLamService.lay();
            }

            protected void done() {
                busy = false;
                if (PhienDangNhapService.layPhienHienTai() != session || !isDisplayable()) return;
                try {
                    var rows = get().stream().filter(ViecCanLamService.Viec::nhac).toList();
                    setText("Nhắc việc (" + rows.size() + ")");
                    setForeground(rows.isEmpty() ? Color.DARK_GRAY : MauUngDung.DO_THUONG_HIEU);
                    boolean fresh = false;
                    for (var v : rows) if (daBao.add(v.khoa())) fresh = true;
                    if (fresh && isShowing()) {
                        JPopupMenu popup = new JPopupMenu();
                        JMenuItem item = new JMenuItem(rows.size() + " việc đến hạn · bấm để xem");
                        item.addActionListener(e -> doClick());
                        popup.add(item);
                        popup.show(NhacViecButton.this, 0, getHeight());
                        javax.swing.Timer hide =
                                new javax.swing.Timer(7000, e -> popup.setVisible(false));
                        hide.setRepeats(false);
                        hide.start();
                    }
                } catch (Exception ex) {
                    setToolTipText("Không tải được nhắc việc. Mở danh sách để thử lại.");
                }
            }
        }.execute();
    }
}
