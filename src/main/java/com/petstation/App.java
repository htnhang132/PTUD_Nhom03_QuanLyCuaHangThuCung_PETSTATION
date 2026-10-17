package com.petstation;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatLightLaf;
import com.petstation.config.ApplicationContext;
import com.petstation.gui.auth.LoginFrame;

public final class App {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        com.petstation.gui.components.Dialogs.installDefaults();

        SwingUtilities.invokeLater(() -> {
            try {
                ApplicationContext context = ApplicationContext.create();
                context.databaseConnection().verify();
                new LoginFrame(context).setVisible(true);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(
                    null,
                    "Không thể khởi động Pet Station.\n\n"+ exception.getMessage()
                        + "\n\nHãy kiểm tra SQL Server và file SETUP.md.",
                    "Lỗi cấu hình",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
