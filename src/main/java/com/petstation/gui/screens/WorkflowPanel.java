package com.petstation.gui.screens;

import com.petstation.gui.components.Dialogs;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.StyledButton;

/**
 * Khung dùng chung cho các màn dạng "thống kê + danh sách xử lý".
 * Dữ liệu hiển thị là dữ liệu mẫu; chưa đại diện dữ liệu production.
 * Lọc, sắp xếp và in bảng đã hoạt động thật (xem {@link BasePanel}).
 */
public class WorkflowPanel extends BasePanel {
    /** stats: mỗi phần tử "Nhãn|Giá trị". Giá trị chỉ có màu khi nhãn mang ý nghĩa cảnh báo. */
    public record Config(String title, String subtitle, String[] stats, String[] columns, Object[][] rows,
                          String primaryAction, String secondaryAction, String note) {}

    private final Config config;

    public WorkflowPanel(Config config) {
        this.config = Objects.requireNonNull(config);
        setLayout(new BorderLayout(0, 14));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel title = sectionTitle(config.title());
        title.setFont(AppColors.font(Font.BOLD, 21));
        JLabel subtitle = new JLabel(config.subtitle());
        subtitle.setFont(AppColors.font(Font.PLAIN, 13));
        subtitle.setForeground(AppColors.TEXT_GRAY);
        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);
        header.add(titles, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JTextField search = searchBar("Tìm trong danh sách (Ctrl+F)");
        actions.add(search);
        if (config.secondaryAction() != null) {
            StyledButton b = new StyledButton(config.secondaryAction(), StyledButton.Variant.GHOST);
            b.addActionListener(e -> { if (secondaryHandler != null) secondaryHandler.run(); else showMockMessage(config.secondaryAction()); });
            actions.add(b);
        }
        if (config.primaryAction() != null) {
            StyledButton b = new StyledButton(config.primaryAction(), StyledButton.Variant.PRIMARY);
            b.addActionListener(e -> { if (primaryHandler != null) primaryHandler.run(); else showMockMessage(config.primaryAction()); });
            actions.add(b);
        }
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, Math.max(1, config.stats().length), 10, 0));
        stats.setOpaque(false);
        for (String stat : config.stats()) {
            String[] p = stat.split("\\|", 2);
            String value = p.length > 1 ? p[1] : "—";
            stats.add(statCard(p[0], value, null, toneForStat(p[0], value)));
        }
        body.add(stats, BorderLayout.NORTH);

        JPanel tableCard = card(new BorderLayout(0, 10));
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(sectionTitle("Danh sách xử lý"), BorderLayout.WEST);
        tableHeader.add(badge("Dữ liệu mẫu", AppColors.WARN_BG, AppColors.BROWN), BorderLayout.EAST);
        tableCard.add(tableHeader, BorderLayout.NORTH);
        JScrollPane scroll = styledTable(config.columns(), config.rows());
        tableCard.add(scroll, BorderLayout.CENTER);
        body.add(tableCard, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(AppColors.WARN_BG);
        footer.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel note = new JLabel("Lưu ý: "+ config.note());
        note.setFont(AppColors.font(Font.PLAIN, 12));
        note.setForeground(AppColors.BROWN);
        footer.add(note, BorderLayout.WEST);
        add(footer, BorderLayout.SOUTH);
    }

    /** Số liệu mang ý nghĩa cảnh báo mới được tô màu; còn lại để chữ trung tính. */
    private static Color toneForStat(String label, String value) {
        String l = label.toLowerCase();
        boolean zero = value.equals("0") || value.equals("0₫");
        if (l.contains("quá hạn") || l.contains("từ chối") || l.contains("cảnh báo")) return zero ? AppColors.TEXT_DARK : AppColors.DANGER_TEXT;
        if (l.contains("chờ") || l.contains("cần") || l.contains("chênh lệch") || l.contains("ưu tiên")) return zero ? AppColors.TEXT_DARK : AppColors.ORANGE;
        return AppColors.TEXT_DARK;
    }

    private Runnable primaryHandler;
    private Runnable secondaryHandler;

    /** Gắn hành động thật (ví dụ chuyển bước) cho nút chính; chưa gắn thì hiện thông báo giao diện mẫu. */
    public WorkflowPanel onPrimary(Runnable r) { this.primaryHandler = r; return this; }
    public WorkflowPanel onSecondary(Runnable r) { this.secondaryHandler = r; return this; }

    private void showMockMessage(String action) {
        Dialogs.showMessageDialog(this,
            "\""+ action + "\"đang ở chế độ giao diện mẫu, chưa ghi vào database.",
            "Giao diện mẫu", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override protected String helpTitle() { return config.title(); }

    @Override
    protected String helpText() {
        return config.note() + "\n\nGõ vào ô tìm kiếm để lọc, bấm tiêu đề cột để sắp xếp.";
    }
}
