package com.employee;

import com.employee.gui.LoginFrame;
import com.employee.utils.ThemeUtil;

import javax.swing.*;

public class EmployeeManagementSystem {
    public static void main(String[] args) {
        // Set the look and feel
        try {
            ThemeUtil.setLightTheme();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set system look and feel for dialogs
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show login frame
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}