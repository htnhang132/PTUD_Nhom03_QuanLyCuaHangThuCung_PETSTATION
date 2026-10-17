package com.petstation.gui.screens;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.StyledButton;
import com.petstation.gui.components.common.DatePicker;
import com.petstation.gui.components.common.FlexibleDiscountPanel;
import com.petstation.gui.components.common.HoldCartManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class SpaIntakePanel extends BasePanel {

    private JComboBox<String> customerCombo;
    private JComboBox<String> petCombo;
    private JComboBox<String> serviceCombo;
    private JComboBox<String> staffCombo;
    private DatePicker appointmentDatePicker;
    private JTextField appointmentTimeField;
    private JLabel photoLabel;
    private ImageIcon uploadedImage;

    private final java.util.function.Consumer<com.petstation.gui.navigation.ScreenId> navigator;

    public SpaIntakePanel(java.util.function.Consumer<com.petstation.gui.navigation.ScreenId> navigator) {
        super();
        this.navigator = navigator;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel title = new JLabel("🐾 Tiếp nhận Spa / Grooming");
        title.setFont(AppColors.font(Font.BOLD, 24));
        title.setForeground(AppColors.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // Main form in scroll pane
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(0, 0, 0, 16));

        // Customer search section
        formPanel.add(sectionLabel("👤 Khách hàng"));
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
        customerCombo.setPreferredSize(new Dimension(450, 40));
        customerCombo.setFont(AppColors.font(Font.PLAIN, 14));
        customerCombo.setEditable(true);
        customerCombo.addActionListener(e -> onCustomerSelected());
        customerRow.add(new JLabel("Khách hàng:") {{ setPreferredSize(new Dimension(100, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        customerRow.add(customerCombo);
        formPanel.add(customerRow);
        formPanel.add(Box.createVerticalStrut(16));

        // Pet selection (dependent on customer)
        formPanel.add(sectionLabel("🐕 Thú cưng"));
        formPanel.add(Box.createVerticalStrut(8));

        JPanel petRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        petRow.setOpaque(false);
        
        petCombo = new JComboBox<>(new String[]{
            "Chọn khách hàng trước...",
        });
        petCombo.setPreferredSize(new Dimension(450, 40));
        petCombo.setFont(AppColors.font(Font.PLAIN, 14));
        petCombo.addActionListener(e -> onPetSelected());
        petRow.add(new JLabel("Thú cưng:") {{ setPreferredSize(new Dimension(100, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        petRow.add(petCombo);
        
        StyledButton addPetBtn = new StyledButton("+ Thêm thú cưng", StyledButton.Variant.GHOST);
        addPetBtn.setPreferredSize(new Dimension(140, 40));
        addPetBtn.addActionListener(e -> handleAddPet());
        petRow.add(addPetBtn);
        
        formPanel.add(petRow);
        formPanel.add(Box.createVerticalStrut(16));

        // Service selection
        formPanel.add(sectionLabel("✂️ Dịch vụ"));
        formPanel.add(Box.createVerticalStrut(8));

        JPanel serviceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        serviceRow.setOpaque(false);
        
        serviceCombo = new JComboBox<>(new String[]{
            "Tắm & cắt lông - 150.000₫",
            "Tắm & sấy khô - 120.000₫",
            "Cắt móng + vệ sinh tai - 80.000₫",
            "Tắm gội đơn giản - 80.000₫",
            "Cạo lông toàn thân - 200.000₫",
            "Spa thư giãn (tắm + massage + tinh dầu) - 250.000₫",
            "🔧 Tùy chỉnh / Combo..."
        });
        serviceCombo.setPreferredSize(new Dimension(450, 40));
        serviceCombo.setFont(AppColors.font(Font.PLAIN, 14));
        serviceCombo.setEditable(true);
        serviceRow.add(new JLabel("Dịch vụ:") {{ setPreferredSize(new Dimension(100, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        serviceRow.add(serviceCombo);
        formPanel.add(serviceRow);
        formPanel.add(Box.createVerticalStrut(16));

        // Staff assignment
        formPanel.add(sectionLabel("👨‍🔧 Thợ phụ trách"));
        formPanel.add(Box.createVerticalStrut(8));

        JPanel staffRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        staffRow.setOpaque(false);
        
        staffCombo = new JComboBox<>(new String[]{
            "Hoa - Groomer chính (7 năm KN)",
            "Lan - Groomer senior (5 năm KN)",
            "Nam - Groomer junior (2 năm KN)",
            "Tự động phân công"
        });
        staffCombo.setPreferredSize(new Dimension(450, 40));
        staffCombo.setFont(AppColors.font(Font.PLAIN, 14));
        staffRow.add(new JLabel("Thợ:") {{ setPreferredSize(new Dimension(100, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        staffRow.add(staffCombo);
        formPanel.add(staffRow);
        formPanel.add(Box.createVerticalStrut(16));

        // Appointment date & time
        formPanel.add(sectionLabel("📅 Lịch hẹn"));
        formPanel.add(Box.createVerticalStrut(8));

        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        dateRow.setOpaque(false);
        
        appointmentDatePicker = new DatePicker(LocalDate.now());
        appointmentDatePicker.setPreferredSize(new Dimension(180, 40));
        
        appointmentTimeField = new JTextField("09:00");
        appointmentTimeField.setPreferredSize(new Dimension(100, 40));
        appointmentTimeField.setFont(AppColors.font(Font.PLAIN, 14));
        appointmentTimeField.setHorizontalAlignment(SwingConstants.CENTER);
        appointmentTimeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        dateRow.add(new JLabel("Ngày:") {{ setPreferredSize(new Dimension(100, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        dateRow.add(appointmentDatePicker);
        dateRow.add(Box.createHorizontalStrut(24));
        dateRow.add(new JLabel("Giờ:") {{ setPreferredSize(new Dimension(60, 40)); setFont(AppColors.font(Font.PLAIN, 14)); }});
        dateRow.add(appointmentTimeField);
        
        StyledButton quickTimeBtn = new StyledButton("Giờ trống", StyledButton.Variant.GHOST);
        quickTimeBtn.setPreferredSize(new Dimension(100, 40));
        quickTimeBtn.addActionListener(e -> showAvailableSlots());
        dateRow.add(quickTimeBtn);
        
        formPanel.add(dateRow);
        formPanel.add(Box.createVerticalStrut(16));

        // Photo upload section
        formPanel.add(sectionLabel("📸 Chụp ảnh / Tải ảnh tình trạng (BẮT BUỘC)"));
        formPanel.add(Box.createVerticalStrut(8));

        JPanel photoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        photoRow.setOpaque(false);
        
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(200, 150));
        photoLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createDashedBorder(AppColors.BORDER, 2, 10),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoLabel.setFont(AppColors.font(Font.PLAIN, 12));
        photoLabel.setForeground(AppColors.TEXT_GRAY);
        photoLabel.setText("<html><center>Chưa có ảnh<br>Nhấp để chụp/tải ảnh</center></html>");
        photoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        photoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                uploadPhoto();
            }
        });
        
        JPanel photoButtons = new JPanel();
        photoButtons.setLayout(new BoxLayout(photoButtons, BoxLayout.Y_AXIS));
        photoButtons.setOpaque(false);
        
        StyledButton uploadBtn = new StyledButton("📁 Tải từ máy", StyledButton.Variant.SECONDARY);
        uploadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadBtn.setMaximumSize(new Dimension(180, 40));
        uploadBtn.addActionListener(e -> uploadPhoto());
        photoButtons.add(uploadBtn);
        
        photoButtons.add(Box.createVerticalStrut(8));
        
        StyledButton cameraBtn = new StyledButton("📷 Chụp ảnh", StyledButton.Variant.SECONDARY);
        cameraBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cameraBtn.setMaximumSize(new Dimension(180, 40));
        cameraBtn.addActionListener(e -> takePhoto());
        photoButtons.add(cameraBtn);
        
        photoButtons.add(Box.createVerticalStrut(8));
        
        StyledButton clearBtn = new StyledButton("🗑️ Xóa ảnh", StyledButton.Variant.GHOST);
        clearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearBtn.setMaximumSize(new Dimension(180, 40));
        clearBtn.addActionListener(e -> clearPhoto());
        photoButtons.add(clearBtn);
        
        photoRow.add(photoLabel);
        photoRow.add(photoButtons);
        formPanel.add(photoRow);
        formPanel.add(Box.createVerticalStrut(8));

        JLabel photoHint = new JLabel("⚠️ Bắt buộc chụp ảnh trước/sau khi làm dịch vụ để tránh tranh chấp đền bù");
        photoHint.setFont(AppColors.font(Font.ITALIC, 11));
        photoHint.setForeground(AppColors.RED_BRAND);
        photoHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(photoHint);
        formPanel.add(Box.createVerticalStrut(16));

        // Notes
        formPanel.add(sectionLabel("📝 Ghi chú / Yêu cầu đặc biệt"));
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
        formPanel.add(Box.createVerticalStrut(24));

        // Flexible discount panel
        FlexibleDiscountPanel discountPanel = new FlexibleDiscountPanel();
        discountPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(discountPanel);
        formPanel.add(Box.createVerticalStrut(24));

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
        
        StyledButton confirmBtn = new StyledButton("✅ Xác nhận tiếp nhận", StyledButton.Variant.PRIMARY);
        confirmBtn.setPreferredSize(new Dimension(200, 44));
        confirmBtn.addActionListener(e -> handleConfirm());
        btnRow.add(confirmBtn);
        
        formPanel.add(btnRow);

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
    }

    private void onPetSelected() {
        String pet = (String) petCombo.getSelectedItem();
        if (pet != null && pet.contains("Dị ứng")) {
            Dialogs.showMessageDialog(this,
                "⚠️ CẢNH BÁO: Thú cưng này có dị ứng!\n\nMochi dị ứng gà - Tránh dùng sản phẩm chứa gà.\n\nĐã ghi nhận vào ghi chú.",
                "Cảnh báo dị ứng", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleAddPet() {
        Dialogs.showMessageDialog(this,
            "Mở form thêm thú cưng mới:\n\n• Tên thú cưng\n• Loài / Giống\n• Ngày sinh / Tuổi\n• Màu sắc\n• Dị ứng / Bệnh lý\n• Ảnh đại diện\n\n(Mock UI)",
            "Thêm thú cưng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void uploadPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Ảnh (JPG, PNG)", "jpg", "jpeg", "png"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIcon icon = new ImageIcon(fileChooser.getSelectedFile().getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                uploadedImage = new ImageIcon(img);
                photoLabel.setIcon(uploadedImage);
                photoLabel.setText("");
            } catch (Exception ex) {
                Dialogs.showMessageDialog(this, "Không thể tải ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void takePhoto() {
        Dialogs.showMessageDialog(this,
            "Mở camera để chụp ảnh...\n\n(Mock UI - tích hợp Webcam API)\n\nLưu ý: Cần cấp quyền truy cập camera.",
            "Chụp ảnh", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearPhoto() {
        uploadedImage = null;
        photoLabel.setIcon(null);
        photoLabel.setText("<html><center>Chưa có ảnh<br>Nhấp để chụp/tải ảnh</center></html>");
    }

    private void showAvailableSlots() {
        String[] slots = {"09:00", "09:30", "10:00", "10:30", "11:00", "14:00", "14:30", "15:00", "15:30", "16:00"};
        String selected = (String) JOptionPane.showInputDialog(this,
            "Chọn khung giờ trống:",
            "Giờ hẹn khả dụng - " + appointmentDatePicker.getSelectedDate(),
            JOptionPane.QUESTION_MESSAGE, null, slots, appointmentTimeField.getText());
        if (selected != null) {
            appointmentTimeField.setText(selected);
        }
    }

    private void handleCancel() {
        int confirm = Dialogs.showConfirmDialog(this,
            "Hủy tiếp nhận? Dữ liệu đã nhập sẽ bị mất.",
            "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            navigator.accept(com.petstation.gui.navigation.ScreenId.SPA);
        }
    }

    private void handleSaveDraft() {
        if (uploadedImage == null) {
            int confirm = Dialogs.showConfirmDialog(this,
                "⚠️ Chưa có ảnh tình trạng thú cưng!\nKhuyến nghị chụp ảnh trước khi lưu.\n\nVẫn muốn lưu nháp?",
                "Chưa có ảnh", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
        }
        Dialogs.showMessageDialog(this,
            "Đã lưu phiếu tiếp nhận vào nháp!\nCó thể vào 'Tiếp nhận Spa' để xem tiếp.\n(Mock UI)",
            "Lưu nháp thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleConfirm() {
        String customer = (String) customerCombo.getSelectedItem();
        String pet = (String) petCombo.getSelectedItem();
        String service = (String) serviceCombo.getSelectedItem();
        String staff = (String) staffCombo.getSelectedItem();
        LocalDate date = appointmentDatePicker.getSelectedDate();
        String time = appointmentTimeField.getText();
        
        if (customer == null || customer.contains("Tìm") || customer.contains("mới")) {
            Dialogs.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (pet == null || pet.contains("Chọn") || pet.contains("Thêm")) {
            Dialogs.showMessageDialog(this, "Vui lòng chọn thú cưng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (date == null) {
            Dialogs.showMessageDialog(this, "Vui lòng chọn ngày hẹn!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (uploadedImage == null) {
            int confirm = Dialogs.showConfirmDialog(this,
                "⚠️ CHƯA CÓ ẢNH TÌNH TRẠNG!\n\nNghiệp vụ Spa bắt buộc phải có ảnh trước khi làm.\nBạn có chắc muốn tiếp tục?",
                "Cảnh báo thiếu ảnh", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        int confirm = Dialogs.showConfirmDialog(this,
            String.format("Xác nhận tiếp nhận?\n\nKhách: %s\nThú cưng: %s\nDịch vụ: %s\nThợ: %s\nNgày: %s %s\n\n%s",
                customer, pet, service, staff,
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                time,
                uploadedImage == null ? "⚠️ CHƯA CÓ ẢNH!" : "✅ Đã có ảnh"),
            "Xác nhận tiếp nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Dialogs.showMessageDialog(this,
                "Tiếp nhận thành công!\n\nMã phiếu: SP-" + System.currentTimeMillis() % 10000 +
                "\nTrạng thái: Chờ làm\n\n(Mock UI - sẽ tạo phiếu Spa, chuyển sang màn hình Tiến độ Spa)",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            navigator.accept(com.petstation.gui.navigation.ScreenId.SPA_SERVICE_SELECTION);
        }
    }
}