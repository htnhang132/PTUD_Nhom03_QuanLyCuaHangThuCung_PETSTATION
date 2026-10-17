package com.petstation.gui.components.common;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * Component chọn khoảng thời gian với preset options
 * Dùng cho các màn hình lọc theo ngày (Audit Log, Hóa đơn, Báo cáo)
 */
public class DateRangePicker extends JPanel {
    private JComboBox<String> presetCombo;
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private JPanel customDatePanel;

    public DateRangePicker() {
        setLayout(new BorderLayout(10, 5));

        // Preset options
        String[] presets = {
            "Hôm nay",
            "Hôm qua", 
            "7 ngày qua",
            "Tuần này",
            "Tuần trước",
            "Tháng này",
            "Tháng trước",
            "30 ngày qua",
            "Tùy chọn..."
        };

        presetCombo = new JComboBox<>(presets);
        presetCombo.addActionListener(e -> handlePresetChange());

        // Custom date range panel
        customDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        customDatePanel.add(new JLabel("Từ:"));
        fromDatePicker = new DatePicker();
        customDatePanel.add(fromDatePicker);
        
        customDatePanel.add(new JLabel("Đến:"));
        toDatePicker = new DatePicker();
        customDatePanel.add(toDatePicker);
        
        customDatePanel.setVisible(false);

        // Layout
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Khoảng thời gian:"));
        topPanel.add(presetCombo);

        add(topPanel, BorderLayout.NORTH);
        add(customDatePanel, BorderLayout.CENTER);

        // Set mặc định "Hôm nay"
        presetCombo.setSelectedIndex(0);
        handlePresetChange();
    }

    private void handlePresetChange() {
        String selected = (String) presetCombo.getSelectedItem();
        LocalDate today = LocalDate.now();
        LocalDate from = null;
        LocalDate to = null;

        switch (selected) {
            case "Hôm nay":
                from = to = today;
                customDatePanel.setVisible(false);
                break;
            case "Hôm qua":
                from = to = today.minusDays(1);
                customDatePanel.setVisible(false);
                break;
            case "7 ngày qua":
                from = today.minusDays(6);
                to = today;
                customDatePanel.setVisible(false);
                break;
            case "Tuần này":
                from = today.minusDays(today.getDayOfWeek().getValue() - 1);
                to = today;
                customDatePanel.setVisible(false);
                break;
            case "Tuần trước":
                LocalDate lastWeekStart = today.minusDays(today.getDayOfWeek().getValue() + 6);
                from = lastWeekStart;
                to = lastWeekStart.plusDays(6);
                customDatePanel.setVisible(false);
                break;
            case "Tháng này":
                from = today.withDayOfMonth(1);
                to = today;
                customDatePanel.setVisible(false);
                break;
            case "Tháng trước":
                LocalDate lastMonth = today.minusMonths(1);
                from = lastMonth.withDayOfMonth(1);
                to = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
                customDatePanel.setVisible(false);
                break;
            case "30 ngày qua":
                from = today.minusDays(29);
                to = today;
                customDatePanel.setVisible(false);
                break;
            case "Tùy chọn...":
                customDatePanel.setVisible(true);
                // Không set from/to, để người dùng chọn
                return;
        }

        // Set dates cho các preset
        if (from != null && to != null) {
            fromDatePicker.setSelectedDate(from);
            toDatePicker.setSelectedDate(to);
        }

        firePropertyChange("dateRange", null, new LocalDate[]{from, to});
    }

    // Getters
    public LocalDate getFromDate() {
        return fromDatePicker.getSelectedDate();
    }

    public LocalDate getToDate() {
        return toDatePicker.getSelectedDate();
    }

    public java.sql.Date getFromSqlDate() {
        return fromDatePicker.getSqlDate();
    }

    public java.sql.Date getToSqlDate() {
        return toDatePicker.getSqlDate();
    }

    public void setFromDate(LocalDate date) {
        presetCombo.setSelectedItem("Tùy chọn...");
        fromDatePicker.setSelectedDate(date);
    }

    public void setToDate(LocalDate date) {
        presetCombo.setSelectedItem("Tùy chọn...");
        toDatePicker.setSelectedDate(date);
    }

    public void reset() {
        presetCombo.setSelectedIndex(0);
        handlePresetChange();
    }
}
