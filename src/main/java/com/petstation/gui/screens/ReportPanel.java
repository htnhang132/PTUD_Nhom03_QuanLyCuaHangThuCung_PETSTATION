package com.petstation.gui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.DoubleFunction;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.ToolbarAware;
import com.petstation.gui.components.common.ExcelExporter;
import com.petstation.gui.components.common.SimpleBarChart;
import com.petstation.gui.components.common.TableStyler;

/**
 * Màn Thống kê chuẩn: bộ lọc khoảng thời gian, chọn góc nhìn, biểu đồ, bảng số liệu, nút Xuất báo cáo (Excel).
 * Bộ lọc chạy thật: đổi kỳ hoặc góc nhìn là biểu đồ, bảng và thẻ tổng hợp tính lại ngay.
 * Số liệu bán hàng, Spa, Hotel hiện là dữ liệu mẫu co giãn theo số ngày; số liệu kho, khách, nhân viên, nhà cung cấp lấy từ Danh mục.
 */
public final class ReportPanel extends BasePanel implements ToolbarAware {
    /** Kết quả một góc nhìn. */
    public record Data(String[] chartLabels, double[] chartValues, boolean money, String unit,
                       String[] columns, Object[][] rows, String[] cardTitles, String[] cardValues) {}

    public record View(String name, DoubleFunction<Data> build) {}

    private static final String[] PERIODS = {"Hôm nay", "7 ngày qua", "30 ngày qua", "Tháng này", "Năm nay"};
    private static final int[] DAYS = {1, 7, 30, 0, 0};

    private final List<View> views;
    private final JComboBox<String> period = new JComboBox<>(PERIODS);
    private final JComboBox<String> viewBox = new JComboBox<>();
    private final JPanel cards = new JPanel(new GridLayout(1, 4, 12, 0));
    private final SimpleBarChart chart = new SimpleBarChart();
    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable table = new JTable(model);
    private final JLabel range = new JLabel();
    private final String exportName;

    public ReportPanel(String title, String subtitle, String exportName, List<View> views) {
        this.views = views;
        this.exportName = exportName;
        setLayout(new BorderLayout(0, 12));

        JLabel heading = sectionTitle(title);
        JLabel sub = new JLabel(subtitle);
        sub.setFont(AppColors.font(Font.PLAIN, 12));
        sub.setForeground(AppColors.TEXT_GRAY);
        JPanel titleBox = transparent(new BorderLayout(0, 2));
        titleBox.add(heading, BorderLayout.NORTH);
        titleBox.add(sub, BorderLayout.SOUTH);

        for (View v : views) viewBox.addItem(v.name());
        period.setPreferredSize(new Dimension(140, 34));
        viewBox.setPreferredSize(new Dimension(190, 34));
        period.addActionListener(e -> rebuild());
        viewBox.addActionListener(e -> rebuild());
        range.setFont(AppColors.font(Font.PLAIN, 12));
        range.setForeground(AppColors.TEXT_GRAY);
        StyledButton export = new StyledButton("Xuất báo cáo (Excel)", StyledButton.Variant.PRIMARY);
        export.addActionListener(e -> ExcelExporter.exportTable(this, table, exportName));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        filters.add(new JLabel("Kỳ:"));
        filters.add(period);
        if (views.size() > 1) { filters.add(new JLabel("Xem theo:")); filters.add(viewBox); }
        filters.add(range);
        filters.add(export);

        JPanel top = transparent(new BorderLayout(10, 8));
        top.add(titleBox, BorderLayout.NORTH);
        top.add(filters, BorderLayout.SOUTH);
        cards.setOpaque(false);
        JPanel north = transparent(new BorderLayout(0, 10));
        north.add(top, BorderLayout.NORTH);
        north.add(cards, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        JPanel chartCard = new JPanel(new BorderLayout());
        chartCard.setBackground(Color.WHITE);
        chartCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppColors.BORDER), new EmptyBorder(10, 12, 6, 12)));
        chartCard.add(chart, BorderLayout.CENTER);
        chartCard.setPreferredSize(new Dimension(0, 230));

        TableStyler.apply(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        JPanel center = transparent(new BorderLayout(0, 10));
        center.add(chartCard, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        rebuild();
    }

    private double scale() {
        int i = period.getSelectedIndex();
        LocalDate today = LocalDate.now();
        int days = DAYS[i];
        if (i == 3) days = today.getDayOfMonth();
        if (i == 4) days = today.getDayOfYear();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        range.setText(days <= 1 ? f.format(today) : f.format(today.minusDays(days - 1L)) + " – " + f.format(today));
        return days;
    }

    private void rebuild() {
        double days = scale();
        int idx = Math.max(0, viewBox.getSelectedIndex());
        Data d = views.get(idx).build().apply(days);
        chart.setData(d.chartLabels(), d.chartValues(), d.money(), d.unit());
        model.setDataVector(d.rows(), d.columns());
        TableStyler.apply(table);
        cards.removeAll();
        Color[] tones = {AppColors.BLUE, AppColors.GREEN, AppColors.ORANGE, AppColors.RED_BRAND};
        for (int i = 0; i < d.cardTitles().length; i++) cards.add(statCard(d.cardTitles()[i], d.cardValues()[i], "", tones[i % tones.length]));
        cards.revalidate();
        cards.repaint();
    }

    @Override public void onShow() { rebuild(); }

    @Override protected String helpTitle() { return "Thống kê"; }

    @Override protected String helpText() {
        return "Chọn kỳ và góc nhìn: biểu đồ và bảng tự cập nhật. Bấm \"Xuất báo cáo\" để lưu Excel.\n"
            + "Màn Thống kê chỉ xem và xuất, không sửa dữ liệu.";
    }
}
