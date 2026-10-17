package com.petstation.gui.screens;

import com.petstation.gui.components.Dialogs;
import com.petstation.gui.components.AppColors;
import com.petstation.gui.components.RoundedPanel;
import com.petstation.gui.components.StyledButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HotelPanel extends BasePanel {

    public HotelPanel() {
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.add(statCard("Phòng đang dùng", "5/12", "", AppColors.RED_BRAND));
        statsRow.add(statCard("Nhận phòng hôm nay", "2", "", AppColors.GREEN));
        statsRow.add(statCard("Trả phòng hôm nay", "1", "", AppColors.ORANGE));
        statsRow.add(statCard("Doanh thu Hotel", "2.400.000₫", "", AppColors.BLUE));

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 16, 0));
        top.add(statsRow, BorderLayout.NORTH);

        add(top, BorderLayout.NORTH);

        // Room grid
        JPanel rooms = buildRoomGrid();
        JScrollPane scroll = new JScrollPane(rooms);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildRoomGrid() {
        JPanel grid = new JPanel(new GridLayout(3, 4, 12, 12));
        grid.setOpaque(false);

        String[][] roomData = {
            {"P01","Mochi","Poodle","03/12","06/12","occupied"},
            {"P02","Coco","Corgi","04/12","07/12","occupied"},
            {"P03","—","—","—","—","empty"},
            {"P04","Max","Labrador","02/12","08/12","occupied"},
            {"P05","—","—","—","—","empty"},
            {"P06","Luna","Shih Tzu","05/12","09/12","occupied"},
            {"P07","—","—","—","—","empty"},
            {"P08","—","—","—","—","empty"},
            {"P09","Rex","GSD","04/12","10/12","occupied"},
            {"P10","—","—","—","—","empty"},
            {"P11","—","—","—","—","maintenance"},
            {"P12","—","—","—","—","empty"},
        };

        for (String[] r : roomData) {
            grid.add(roomCard(r));
        }
        return grid;
    }

    private JPanel roomCard(String[] r) {
        boolean occupied    = r[5].equals("occupied");
        boolean maintenance = r[5].equals("maintenance");

        Color bg = occupied ? new Color(0xFE, 0xE2, 0xE2)
                 : maintenance ? new Color(0xFE, 0xF3, 0xC7)
                 : new Color(0xD1, 0xFA, 0xE5);

        Color accent = occupied ? AppColors.RED_BRAND
                     : maintenance ? AppColors.ORANGE
                     : AppColors.GREEN;

        RoundedPanel card = new RoundedPanel(12, bg, false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        
        // Add cursor for occupied rooms
        if (occupied) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.setToolTipText("Nhấp đúp để mở nhật ký lưu trú");
        } else if (maintenance) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.setToolTipText("Nhấp đúp để xem chi tiết bảo trì");
        } else {
            card.setToolTipText("Phòng trống - Nhấp để đặt phòng");
        }

        // Add double-click listener
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (occupied) {
                        openHotelJournal(r[0], r[1], r[2]);
                    } else if (maintenance) {
                        openMaintenanceDetail(r[0]);
                    } else {
                        openNewBooking(r[0]);
                    }
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (occupied || maintenance) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accent, 2),
                        new EmptyBorder(12, 12, 12, 12)
                    ));
                    card.repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(14, 14, 14, 14));
                card.repaint();
            }
        });

        JLabel roomNo = new JLabel("Phòng " + r[0]);
        roomNo.setFont(AppColors.font(Font.BOLD, 15));
        roomNo.setForeground(accent);
        roomNo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String statusText = occupied ? "Đang ở" : maintenance ? "Bảo trì" : "Trống";
        JLabel status = new JLabel(statusText);
        status.setFont(AppColors.font(Font.PLAIN, 12));
        status.setForeground(accent);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(roomNo);
        card.add(Box.createVerticalStrut(6));
        card.add(status);

        if (occupied) {
            card.add(Box.createVerticalStrut(8));
            JLabel petName = new JLabel(r[1] + " (" + r[2] + ")");
            petName.setFont(AppColors.font(Font.PLAIN, 12));
            petName.setForeground(AppColors.TEXT_DARK);
            petName.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel dates = new JLabel(r[3] + " → " + r[4]);
            dates.setFont(AppColors.font(Font.PLAIN, 11));
            dates.setForeground(AppColors.TEXT_GRAY);
            dates.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel hint = new JLabel("Nhấp đúp → Nhật ký lưu trú");
            hint.setFont(AppColors.font(Font.ITALIC, 10));
            hint.setForeground(AppColors.TEXT_GRAY);
            hint.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(petName);
            card.add(Box.createVerticalStrut(4));
            card.add(dates);
            card.add(Box.createVerticalStrut(4));
            card.add(hint);
        } else if (maintenance) {
            card.add(Box.createVerticalStrut(8));
            JLabel hint = new JLabel("Nhấp đúp → Chi tiết bảo trì");
            hint.setFont(AppColors.font(Font.ITALIC, 10));
            hint.setForeground(AppColors.TEXT_GRAY);
            hint.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(hint);
        } else {
            card.add(Box.createVerticalStrut(8));
            JLabel hint = new JLabel("Nhấp → Đặt phòng mới");
            hint.setFont(AppColors.font(Font.ITALIC, 10));
            hint.setForeground(AppColors.TEXT_GRAY);
            hint.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(hint);
        }

        return card;
    }

    private void openHotelJournal(String roomCode, String petName, String breed) {
        int choice = JOptionPane.showOptionDialog(this,
            String.format("Phòng %s - %s (%s)\n\nBạn muốn mở màn hình nào?",
                roomCode, petName, breed),
            "Mở màn hình liên quan", 
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE, null,
            new String[]{"📓 Nhật ký lưu trú", "💰 Trả phòng & Quyết toán", "Hủy"}, 2);
        
        if (choice == 0) {
            Dialogs.showMessageDialog(this,
                String.format("Mở màn hình Nhật ký lưu trú cho:\n\nPhòng: %s\nThú cưng: %s (%s)\n\n(Mock UI - navigation đến HotelJournalPanel)",
                    roomCode, petName, breed),
                "Mở nhật ký", JOptionPane.INFORMATION_MESSAGE);
        } else if (choice == 1) {
            Dialogs.showMessageDialog(this,
                String.format("Mở màn hình Trả phòng & Quyết toán cho:\n\nPhòng: %s\nThú cưng: %s (%s)\n\n(Mock UI - navigation đến CheckoutPanel)",
                    roomCode, petName, breed),
                "Mở quyết toán", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openMaintenanceDetail(String roomCode) {
        Dialogs.showMessageDialog(this,
            String.format("Mở chi tiết bảo trì cho Phòng %s\n\n• Loại bảo trì: Sửa điều hòa, vệ sinhทั quát\n• Ngày bắt đầu: 01/10/2024\n• Dự kiến hoàn thành: 05/10/2024\n• Người phụ trách: Bộ phận kỹ thuật\n\n(Mock UI)",
                roomCode),
            "Chi tiết bảo trì", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openNewBooking(String roomCode) {
        Dialogs.showMessageDialog(this,
            String.format("Mở màn hình Đặt phòng cho Phòng %s\n\n• Phòng đang trống\n• Loại phòng: Standard\n• Giá: 350.000₫/ngày\n\n(Mock UI - navigation đến HotelBookingPanel)",
                roomCode),
            "Đặt phòng mới", JOptionPane.INFORMATION_MESSAGE);
    }
}