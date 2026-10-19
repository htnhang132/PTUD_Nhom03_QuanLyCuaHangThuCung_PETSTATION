package com.petstation.gui.processing.approval;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.BasePanel;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.FormDialog;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.service.ApprovalService;
import com.petstation.service.ApprovalService.Request;
import com.petstation.service.AuditService;

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
 * Xử lý ▸ Phê duyệt yêu cầu (chỉ Cửa hàng trưởng): xem nội dung trước/sau, Duyệt hoặc Từ chối kèm
 * lý do.
 */
public final class ApprovalCenterPanel extends BasePanel implements ToolbarAware {
    private final DefaultTableModel model =
            new DefaultTableModel(
                    new String[] {"Mã", "Loại yêu cầu", "Người gửi", "Thời gian", "Trạng thái"},
                    0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
    private final JTable table;
    private final List<Request> shown = new ArrayList<>();
    private final JCheckBox pendingOnly = new JCheckBox("Chỉ hiện yêu cầu chờ duyệt", true);
    private final JPanel detail = new JPanel(new BorderLayout(0, 8));
    private final JLabel counter = new JLabel();
    private Request selected;

    public ApprovalCenterPanel() {
        setLayout(new BorderLayout(0, 10));
        JPanel top = transparent(new BorderLayout(10, 0));
        top.add(sectionTitle("Danh sách yêu cầu"), BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        counter.setFont(AppColors.font(Font.BOLD, 13));
        counter.setForeground(AppColors.ORANGE);
        pendingOnly.setOpaque(false);
        pendingOnly.addActionListener(e -> reload());
        right.add(counter);
        right.add(pendingOnly);
        top.add(right, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JScrollPane scroll = styledTable(model);
        table = tableOf(scroll);
        table.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (e.getValueIsAdjusting()) return;
                            int r = table.getSelectedRow();
                            selected = r < 0 ? null : shown.get(table.convertRowIndexToModel(r));
                            renderDetail();
                        });

        detail.setBackground(Color.WHITE);
        detail.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppColors.BORDER),
                        new EmptyBorder(14, 16, 14, 16)));
        JPanel holder = new JPanel(new BorderLayout());
        holder.setPreferredSize(new Dimension(420, 0));
        holder.add(detail);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, holder);
        split.setResizeWeight(0.55);
        split.setBorder(null);
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);
        ApprovalService.addListener(() -> SwingUtilities.invokeLater(this::reload));
        reload();
    }

    private void reload() {
        Request keep = selected;
        shown.clear();
        model.setRowCount(0);
        List<Request> all = new ArrayList<>(ApprovalService.all());

        for (Request r : all) {
            if (pendingOnly.isSelected() && !r.status.equals("Chờ duyệt")) continue;
            shown.add(r);
            model.addRow(new Object[] {r.id, r.kind.label, r.sender, r.timeText(), r.status});
        }
        long pending = ApprovalService.pendingCount();
        counter.setText(
                pending == 0 ? "Không có yêu cầu chờ duyệt" : pending + " yêu cầu chờ duyệt");
        int idx = keep == null ? -1 : shown.indexOf(keep);
        if (idx >= 0) table.setRowSelectionInterval(idx, idx);
        else {
            selected = null;
            renderDetail();
        }
    }

    private void renderDetail() {
        detail.removeAll();
        if (selected == null) {
            JLabel hint = new JLabel("Chọn một yêu cầu để xem chi tiết.", JLabel.CENTER);
            hint.setForeground(AppColors.TEXT_GRAY);
            detail.add(hint, BorderLayout.CENTER);
        } else {
            Request r = selected;
            JPanel head = new JPanel(new GridLayout(0, 1, 0, 3));
            head.setOpaque(false);
            JLabel t = new JLabel(r.id + " · " + r.kind.label);
            t.setFont(AppColors.font(Font.BOLD, 16));
            head.add(t);
            head.add(line("Người gửi", r.sender + " lúc " + r.timeText()));
            head.add(line("Lý do", r.reason));
            head.add(
                    line(
                            "Trạng thái",
                            r.status
                                    + (r.decidedBy.isEmpty() ? "" : " bởi " + r.decidedBy)
                                    + (r.rejectReason.isEmpty()
                                            ? ""
                                            : " (" + r.rejectReason + ")")));
            detail.add(head, BorderLayout.NORTH);

            JPanel compare = new JPanel(new GridLayout(1, 2, 10, 0));
            compare.setOpaque(false);
            compare.add(box("Trước khi sửa", r.before, new Color(0xF5, 0xF5, 0xF5)));
            compare.add(box("Sau khi sửa", r.after, new Color(0xE6, 0xF4, 0xEA)));
            detail.add(compare, BorderLayout.CENTER);

            boolean pending = r.status.equals("Chờ duyệt");
            JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
            buttons.setOpaque(false);
            StyledButton approve = new StyledButton("Duyệt", StyledButton.Variant.SUCCESS);
            StyledButton reject = new StyledButton("Từ chối", StyledButton.Variant.DANGER);
            approve.setEnabled(pending);
            reject.setEnabled(pending);
            approve.addActionListener(e -> approve(r));
            reject.addActionListener(e -> reject(r));
            buttons.add(approve);
            buttons.add(reject);
            detail.add(buttons, BorderLayout.SOUTH);
        }
        detail.revalidate();
        detail.repaint();
    }

    private static JLabel line(String k, String v) {
        JLabel l =
                new JLabel("<html><span style='color:#7a7a7a'>" + k + ":</span> " + v + "</html>");
        l.setFont(AppColors.font(Font.PLAIN, 13));
        return l;
    }

    private static JPanel box(String title, String text, Color bg) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(title);
        l.setFont(AppColors.font(Font.BOLD, 12));
        l.setForeground(AppColors.TEXT_GRAY);
        JTextArea a = new JTextArea(text);
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBackground(bg);
        a.setBorder(new EmptyBorder(8, 8, 8, 8));
        a.setFont(AppColors.font(Font.PLAIN, 13));
        p.add(l, BorderLayout.NORTH);
        p.add(new JScrollPane(a), BorderLayout.CENTER);
        return p;
    }

    private void approve(Request r) {
        if (!Dialogs.confirm(
                this,
                "Duyệt yêu cầu "
                        + r.id
                        + " ("
                        + r.kind.label
                        + ")?\nHệ thống sẽ tự thực hiện thay đổi và ghi Nhật ký.")) return;
        try {
            ApprovalService.approve(r, AuditService.user());
        } catch (RuntimeException exception) {
            Dialogs.warn(this, exception.getMessage());
            reload();
        }
    }

    private void reject(Request r) {
        List<String> v =
                FormDialog.ask(
                        this,
                        "Từ chối yêu cầu " + r.id,
                        new String[] {"Lý do từ chối *"},
                        new String[] {""});
        if (v == null) return;
        if (v.get(0).isBlank()) {
            Dialogs.warn(this, "Cần nhập lý do từ chối.");
            return;
        }
        try {
            ApprovalService.reject(r, AuditService.user(), v.get(0));
        } catch (RuntimeException exception) {
            Dialogs.warn(this, exception.getMessage());
            reload();
        }
    }

    @Override
    public void onShow() {
        reload();
    }

    @Override
    protected String helpTitle() {
        return "Phê duyệt yêu cầu";
    }

    @Override
    protected String helpText() {
        return "Chọn yêu cầu, xem nội dung trước và sau khi sửa, rồi Duyệt hoặc Từ chối (bắt buộc"
                + " ghi lý do).\n"
                + "Khi duyệt, hệ thống tự thực hiện thay đổi và ghi Nhật ký.";
    }
}
