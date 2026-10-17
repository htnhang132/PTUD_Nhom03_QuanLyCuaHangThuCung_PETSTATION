package com.petstation.gui.screens;

import com.petstation.gui.components.Money;
import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.common.DatePicker;
import com.petstation.gui.components.common.FlexibleDiscountPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class HotelBookingPanel extends BasePanel {

    private JComboBox<String> customerCombo;
    private JComboBox<String> petCombo;
    private JComboBox<String> roomCombo;
    private DatePicker checkinPicker;
    private DatePicker checkoutPicker;
    private JTextField depositField;
    private JTextField totalField;

    private final java.util.function.Consumer<com.petstation.gui.navigation.ScreenId> navigator;

    public HotelBookingPanel(java.util.function.Consumer<com.petstation.gui.navigation.ScreenId> navigator) {
        super();
        this.navigator = navigator;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // Header with back and title
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel title = new JLabel("🏨 Tạo đặt phòng mới");
        title.setFont(AppColors.font(Font.BOLD, 24));
        title.setForeground(AppColors.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // Main form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(0, 0, 0, 16));

        // Customer selection section
        formPanel.add(sectionLabel("👤 Thông tin khách hàng"));
        formPanel.add(Box.createVerticalStrut(8));

        JPanel customerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        customerRow.setOpaque(false);
        
        customerCombo = new JComboBox<>(new String[]{
            "🔍 Tìm khách hàng (nhập SĐT/tên)...",
            "Trần Thị Bích - 0901 234 567 (Vàng)",
            "Nguyễn Văn An - 0902 345 678 (Kim Cương)",
            "Lê Hoàng Cường - 0903 456 789 (Bạch Kim)",
            "Phạm Minh Đức - 0904 567 890 (Bạc)",
            "➕ Khách hàng mới"
        });
        customerCombo.setPreferredSize(new Dimension(400, 40));
        customerCombo.setFont(AppColors.font(Font.PLAIN, 14));
        customerCombo.setEditable(true);
        customerCombo.addActionListener(e -> onCustomerSelected());
        customerRow.add(new JLabel("Khách hàng:") {{ setPreferredSize(new Dimension(120, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        customerRow.add(customerCombo);
        formPanel.add(customerRow);
        formPanel.add(Box.createVerticalStrut(16));

        // Pet selection (dependent on customer)
        formPanel.add(sectionLabel("🐾 Thú cưng"));
        formPanel.add(Box.createVerticalStrut(8));

        JPanel petRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        petRow.setOpaque(false);
        
        petCombo = new JComboBox<>(new String[]{
            "Chọn khách hàng trước...",
        });
        petCombo.setPreferredSize(new Dimension(400, 40));
        petCombo.setFont(AppColors.font(Font.PLAIN, 14));
        petRow.add(new JLabel("Thú cưng:") {{ setPreferredSize(new Dimension(120, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        petRow.add(petCombo);
        formPanel.add(petRow);
        formPanel.add(Box.createVerticalStrut(16));

        // Room selection
        formPanel.add(sectionLabel("🏠 Phòng & Thời gian"));
        formPanel.add(Box.createVerticalStrut(8));

        JPanel roomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        roomRow.setOpaque(false);
        
        roomCombo = new JComboBox<>(new String[]{
            "P01 - Standard (350.000₫/ngày) ✓ Trống",
            "P02 - Standard (350.000₫/ngày) ✓ Trống",
            "P03 - Standard (350.000₫/ngày) ✓ Trống",
            "P05 - Standard (350.000₫/ngày) ✓ Trống",
            "P07 - Deluxe (500.000₫/ngày) ✓ Trống",
            "P08 - Deluxe (500.000₫/ngày) ✓ Trống",
            "P10 - Suite (800.000₫/ngày) ✓ Trống",
        });
        roomCombo.setPreferredSize(new Dimension(400, 40));
        roomCombo.setFont(AppColors.font(Font.PLAIN, 14));
        roomRow.add(new JLabel("Phòng:") {{ setPreferredSize(new Dimension(120, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        roomRow.add(roomCombo);
        formPanel.add(roomRow);
        formPanel.add(Box.createVerticalStrut(12));

        // Check-in / Check-out with DatePicker
        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        dateRow.setOpaque(false);
        
        checkinPicker = new DatePicker(LocalDate.now());
        checkinPicker.setPreferredSize(new Dimension(180, 40));
        checkinPicker.addPropertyChangeListener("date", evt -> updateTotal());
        
        checkoutPicker = new DatePicker(LocalDate.now().plusDays(2));
        checkoutPicker.setPreferredSize(new Dimension(180, 40));
        checkoutPicker.addPropertyChangeListener("date", evt -> updateTotal());

        dateRow.add(new JLabel("Check-in:") {{ setPreferredSize(new Dimension(120, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        dateRow.add(checkinPicker);
        dateRow.add(Box.createHorizontalStrut(24));
        dateRow.add(new JLabel("Check-out:") {{ setPreferredSize(new Dimension(100, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        dateRow.add(checkoutPicker);
        formPanel.add(dateRow);
        formPanel.add(Box.createVerticalStrut(16));

        // Pricing section
        formPanel.add(sectionLabel("💰 Giá & Thanh toán"));
        formPanel.add(Box.createVerticalStrut(8));

        JPanel priceRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        priceRow1.setOpaque(false);
        
        JLabel roomPriceLabel = new JLabel("Giá phòng: 350.000₫/ngày");
        roomPriceLabel.setFont(AppColors.font(Font.PLAIN, 14));
        roomPriceLabel.setForeground(AppColors.TEXT_DARK);
        priceRow1.add(roomPriceLabel);
        
        totalField = new JTextField("700.000₫ (2 đêm)");
        totalField.setPreferredSize(new Dimension(200, 40));
        totalField.setFont(AppColors.font(Font.BOLD, 16));
        totalField.setForeground(AppColors.RED_BRAND);
        totalField.setEditable(false);
        totalField.setHorizontalAlignment(SwingConstants.RIGHT);
        totalField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        totalField.setBackground(Color.WHITE);
        priceRow1.add(totalField);
        formPanel.add(priceRow1);
        formPanel.add(Box.createVerticalStrut(12));

        // Deposit field
        JPanel depositRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        depositRow.setOpaque(false);
        
        depositField = new JTextField("0");
        depositField.setPreferredSize(new Dimension(200, 40));
        depositField.setFont(AppColors.font(Font.PLAIN, 14));
        depositField.setHorizontalAlignment(SwingConstants.RIGHT);
        depositField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        depositField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateRemaining(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateRemaining(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateRemaining(); }
        });

        depositRow.add(new JLabel("Tiền cọc (₫):") {{ setPreferredSize(new Dimension(120, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        depositRow.add(depositField);
        
        JLabel remainingLabel = new JLabel("Còn lại: 700.000₫");
        remainingLabel.setFont(AppColors.font(Font.BOLD, 14));
        remainingLabel.setForeground(AppColors.RED_BRAND);
        remainingLabel.setName("remainingLabel");
        depositRow.add(remainingLabel);
        formPanel.add(depositRow);
        formPanel.add(Box.createVerticalStrut(8));

        // Payment method
        JPanel paymentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        paymentRow.setOpaque(false);
        
        JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "QR Code", "Thẻ tín dụng"});
        paymentCombo.setPreferredSize(new Dimension(200, 40));
        paymentCombo.setFont(AppColors.font(Font.PLAIN, 14));
        
        paymentRow.add(new JLabel("Phương thức:") {{ setPreferredSize(new Dimension(120, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        paymentRow.add(paymentCombo);
        formPanel.add(paymentRow);
        formPanel.add(Box.createVerticalStrut(24));

        // Notes
        formPanel.add(sectionLabel("📝 Ghi chú"));
        formPanel.add(Box.createVerticalStrut(8));
        
        JTextArea notesArea = new JTextArea(3, 40);
        notesArea.setFont(AppColors.font(Font.PLAIN, 13));
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(0, 100));
        formPanel.add(notesScroll);
        formPanel.add(Box.createVerticalStrut(32));

        // Action buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        btnRow.setOpaque(false);
        
        StyledButton cancelBtn = new StyledButton("Hủy", StyledButton.Variant.GHOST);
        cancelBtn.setPreferredSize(new Dimension(140, 44));
        cancelBtn.addActionListener(e -> handleCancel());
        btnRow.add(cancelBtn);
        
        StyledButton saveDraftBtn = new StyledButton("Lưu nháp", StyledButton.Variant.SECONDARY);
        saveDraftBtn.setPreferredSize(new Dimension(140, 44));
        saveDraftBtn.addActionListener(e -> handleSaveDraft());
        btnRow.add(saveDraftBtn);
        
        StyledButton confirmBtn = new StyledButton("Xác nhận đặt phòng", StyledButton.Variant.PRIMARY);
        confirmBtn.setPreferredSize(new Dimension(200, 44));
        confirmBtn.addActionListener(e -> handleConfirm());
        btnRow.add(confirmBtn);
        
        formPanel.add(btnRow);

        // Wrap in scroll pane
        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppColors.font(Font.BOLD, 14));
        label.setForeground(AppColors.TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void onCustomerSelected() {
        String selected = (String) customerCombo.getSelectedItem();
        if (selected == null) return;
        
        petCombo.removeAllItems();
        if (selected.contains("Trần Thị Bích")) {
            petCombo.addItem("Mochi - Poodle - 2 tuổi - Dị ứng gà");
            petCombo.addItem("Bông - Mèo Persian - 3 tuổi");
        } else if (selected.contains("Nguyễn Văn An")) {
            petCombo.addItem("Lucky - Corgi - 1 tuổi");
            petCombo.addItem("Max - Labrador - 4 tuổi - Béo phì");
        } else if (selected.contains("Lê Hoàng Cường")) {
            petCombo.addItem("Rex - GSD - 5 tuổi - Viêm khớp");
        } else if (selected.contains("Phạm Minh Đức")) {
            petCombo.addItem("Kitty - Mèo Persian - 3 tuổi");
        } else if (selected.contains("Khách hàng mới")) {
            petCombo.addItem("➕ Thêm thú cưng mới");
        } else {
            petCombo.addItem("Chọn khách hàng trước...");
        }
        updateTotal();
    }

    private void updateTotal() {
        LocalDate checkin = checkinPicker.getSelectedDate();
        LocalDate checkout = checkoutPicker.getSelectedDate();
        
        if (checkin != null && checkout != null) {
            long nights = java.time.temporal.ChronoUnit.DAYS.between(checkin, checkout);
            if (nights < 1) nights = 1;
            
            // Get room price
            String room = (String) roomCombo.getSelectedItem();
            int pricePerNight = 350000;
            if (room != null) {
                if (room.contains("Deluxe")) pricePerNight = 500000;
                else if (room.contains("Suite")) pricePerNight = 800000;
            }
            
            long total = nights * pricePerNight;
            totalField.setText(Money.format(total) + " (" + nights + " đêm)");
            
            // Update remaining
            Component[] comps = getComponents();
            findAndUpdateRemaining(total);
        }
    }

    private void findAndUpdateRemaining(long total) {
        for (Component c : getComponents()) {
            findRemainingRecursive(c, total);
        }
    }

    private void findRemainingRecursive(Component c, long total) {
        if (c instanceof JLabel && "remainingLabel".equals(c.getName())) {
            try {
                String depositText = depositField.getText().replace(",", "").replace("₫", "").trim();
                long deposit = depositText.isEmpty() ? 0 : Long.parseLong(depositText);
                long remaining = total - deposit;
                ((JLabel) c).setText("Còn lại: " + Money.format(Math.max(0, remaining)));
                ((JLabel) c).setForeground(remaining > 0 ? AppColors.RED_BRAND : AppColors.GREEN);
            } catch (NumberFormatException ignored) {}
            return;
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                findRemainingRecursive(child, total);
            }
        }
    }

    private void updateRemaining() {
        findAndUpdateRemaining(parseTotal());
    }

    private long parseTotal() {
        String totalText = totalField.getText();
        try {
            return Long.parseLong(totalText.replace("₫", "").replace(",", "").replace("(", "").split(" ")[0]);
        } catch (Exception e) {
            return 700000;
        }
    }

    private void handleCancel() {
        int confirm = Dialogs.showConfirmDialog(this,
            "Hủy đặt phòng? Dữ liệu đã nhập sẽ bị mất.",
            "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            navigator.accept(com.petstation.gui.navigation.ScreenId.HOTEL);
        }
    }

    private void handleSaveDraft() {
        Dialogs.showMessageDialog(this,
            "Đã lưu đặt phòng vào nháp!\nCó thể vào 'Tra cứu hóa đơn' để xem tiếp.\n(Mock UI)",
            "Lưu nháp thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleConfirm() {
        String customer = (String) customerCombo.getSelectedItem();
        String pet = (String) petCombo.getSelectedItem();
        String room = (String) roomCombo.getSelectedItem();
        
        if (customer == null || customer.contains("Tìm") || customer.contains("mới")) {
            Dialogs.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (pet == null || pet.contains("Chọn") || pet.contains("Thêm")) {
            Dialogs.showMessageDialog(this, "Vui lòng chọn thú cưng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        LocalDate checkin = checkinPicker.getSelectedDate();
        LocalDate checkout = checkoutPicker.getSelectedDate();
        if (checkin == null || checkout == null || checkout.isBefore(checkin)) {
            Dialogs.showMessageDialog(this, "Ngày check-in/check-out không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = Dialogs.showConfirmDialog(this,
            String.format("Xác nhận đặt phòng?\n\nKhách: %s\nThú cưng: %s\nPhòng: %s\nCheck-in: %s\nCheck-out: %s\nTổng: %s\nTiền cọc: %s₫",
                customer, pet, room, 
                checkin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                checkout.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                totalField.getText(), depositField.getText()),
            "Xác nhận đặt phòng", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Dialogs.showMessageDialog(this,
                "Đặt phòng thành công!\n\nMã đặt: BK-" + System.currentTimeMillis() % 10000 +
                "\nTrạng thái: Chờ xác nhận cọc\n\n(Mock UI - sẽ tạo bản ghi database)",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            navigator.accept(com.petstation.gui.navigation.ScreenId.HOTEL_JOURNAL);
        }
    }
}