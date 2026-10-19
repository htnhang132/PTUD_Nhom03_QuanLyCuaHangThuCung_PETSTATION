package com.petstation.gui.components.common;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import javax.swing.*;

/**
 * Custom DatePicker component with calendar popup Thay thế cho việc nhập tay ngày tháng vào
 * JTextField
 */
public class DatePicker extends JPanel {
    private JTextField textField;
    private JButton calendarButton;
    private JDialog calendarDialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private LocalDate selectedDate;
    private JLabel monthYearLabel;
    private JPanel daysPanel;
    private int currentMonth;
    private int currentYear;

    public DatePicker() {
        this(LocalDate.now());
    }

    public DatePicker(LocalDate initialDate) {
        setLayout(new BorderLayout(5, 0));
        this.selectedDate = initialDate;
        this.currentMonth = initialDate.getMonthValue();
        this.currentYear = initialDate.getYear();

        // Text field để hiển thị ngày đã chọn
        textField = new JTextField(10);
        textField.setEditable(false);
        textField.setBackground(Color.WHITE);
        if (selectedDate != null) {
            textField.setText(formatDate(selectedDate));
        }

        // Nút mở calendar
        calendarButton = new JButton("Lịch");
        calendarButton.setPreferredSize(new Dimension(35, 25));
        calendarButton.setFocusPainted(false);
        calendarButton.addActionListener(e -> showCalendar());

        add(textField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
    }

    private void showCalendar() {
        if (calendarDialog == null) {
            createCalendarDialog();
        }

        // Reset về tháng hiện tại hoặc tháng đã chọn
        if (selectedDate != null) {
            currentMonth = selectedDate.getMonthValue();
            currentYear = selectedDate.getYear();
        }

        updateCalendar();

        // Hiển thị dialog ở vị trí gần component
        Point location = textField.getLocationOnScreen();
        calendarDialog.setLocation(location.x, location.y + textField.getHeight());
        calendarDialog.setVisible(true);
    }

    private void createCalendarDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            calendarDialog = new JDialog((Frame) window, "Chọn ngày", true);
        } else if (window instanceof Dialog) {
            calendarDialog = new JDialog((Dialog) window, "Chọn ngày", true);
        } else {
            calendarDialog = new JDialog((Frame) null, "Chọn ngày", true);
        }

        calendarDialog.setLayout(new BorderLayout());
        calendarDialog.setUndecorated(false);

        // Header với tháng/năm và nút điều hướng
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton prevButton = new JButton("◀");
        prevButton.addActionListener(
                e -> {
                    currentMonth--;
                    if (currentMonth < 1) {
                        currentMonth = 12;
                        currentYear--;
                    }
                    updateCalendar();
                });

        JButton nextButton = new JButton("▶");
        nextButton.addActionListener(
                e -> {
                    currentMonth++;
                    if (currentMonth > 12) {
                        currentMonth = 1;
                        currentYear++;
                    }
                    updateCalendar();
                });

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(monthYearLabel.getFont().deriveFont(Font.BOLD, 14f));

        headerPanel.add(prevButton, BorderLayout.WEST);
        headerPanel.add(monthYearLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        // Panel chứa các ngày trong tháng
        daysPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        daysPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Thêm header cho các ngày trong tuần
        String[] dayNames = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        for (String dayName : dayNames) {
            JLabel label = new JLabel(dayName, SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setForeground(Color.BLUE);
            daysPanel.add(label);
        }

        // Nút hôm nay và hủy
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton todayButton = new JButton("Hôm nay");
        todayButton.addActionListener(
                e -> {
                    LocalDate today = LocalDate.now();
                    selectDate(today);
                });

        JButton clearButton = new JButton("Xóa");
        clearButton.addActionListener(
                e -> {
                    selectedDate = null;
                    textField.setText("");
                    calendarDialog.setVisible(false);
                });

        footerPanel.add(todayButton);
        footerPanel.add(clearButton);

        calendarDialog.add(headerPanel, BorderLayout.NORTH);
        calendarDialog.add(daysPanel, BorderLayout.CENTER);
        calendarDialog.add(footerPanel, BorderLayout.SOUTH);

        calendarDialog.pack();
        calendarDialog.setSize(300, 280);
    }

    private void updateCalendar() {
        monthYearLabel.setText(String.format("Tháng %d/%d", currentMonth, currentYear));

        // Xóa các nút ngày cũ (giữ lại header)
        Component[] components = daysPanel.getComponents();
        for (int i = components.length - 1; i >= 7; i--) {
            daysPanel.remove(i);
        }

        // Tính toán ngày đầu tiên của tháng
        LocalDate firstDay = LocalDate.of(currentYear, currentMonth, 1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // 0 = CN, 1 = T2, ...

        // Thêm ô trống cho các ngày trước ngày 1
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        // Thêm các ngày trong tháng
        int daysInMonth = firstDay.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            final LocalDate date = LocalDate.of(currentYear, currentMonth, day);
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFocusPainted(false);

            // Highlight ngày được chọn
            if (selectedDate != null && date.equals(selectedDate)) {
                dayButton.setBackground(new Color(66, 133, 244));
                dayButton.setForeground(Color.WHITE);
                dayButton.setOpaque(true);
            }

            // Highlight ngày hôm nay
            if (date.equals(LocalDate.now())) {
                dayButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }

            dayButton.addActionListener(e -> selectDate(date));
            daysPanel.add(dayButton);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private void selectDate(LocalDate date) {
        this.selectedDate = date;
        textField.setText(formatDate(date));
        calendarDialog.setVisible(false);

        // Fire property change để notify listeners
        firePropertyChange("date", null, date);
    }

    private String formatDate(LocalDate date) {
        return String.format(
                "%02d/%02d/%d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    // Getters and Setters
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        if (date != null) {
            textField.setText(formatDate(date));
            currentMonth = date.getMonthValue();
            currentYear = date.getYear();
        } else {
            textField.setText("");
        }
    }

    public Date getDate() {
        if (selectedDate == null) return null;
        return Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void setDate(Date date) {
        if (date == null) {
            setSelectedDate(null);
        } else {
            setSelectedDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

    public java.sql.Date getSqlDate() {
        if (selectedDate == null) return null;
        return java.sql.Date.valueOf(selectedDate);
    }

    public void setSqlDate(java.sql.Date date) {
        if (date == null) {
            setSelectedDate(null);
        } else {
            setSelectedDate(date.toLocalDate());
        }
    }

    public void clear() {
        setSelectedDate(null);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        calendarButton.setEnabled(enabled);
    }
}
