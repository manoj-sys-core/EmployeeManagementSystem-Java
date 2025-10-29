package com.employee.gui;

import com.employee.model.User;
import com.employee.utils.ThemeUtil;
import com.employee.utils.DatabaseUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;
    private JLabel userLabel;
    private JButton logoutButton;
    private JButton themeButton;

    public MainFrame(User user) {
        this.currentUser = user;
        initializeComponents();
        layoutComponents();
        setupListeners();
        configureFrame();
    }

    private void initializeComponents() {
        contentPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();
        userLabel = new JLabel("Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        logoutButton = new JButton("Logout");
        themeButton = new JButton("Toggle Theme");
    }

    private void layoutComponents() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(userLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(themeButton);
        rightPanel.add(logoutButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Add tabs based on user role
        if ("ADMIN".equals(currentUser.getRole())) {
            tabbedPane.addTab("Dashboard", new DashboardPanel());
            tabbedPane.addTab("Employees", new EmployeeManagementPanel());
            tabbedPane.addTab("Departments", new DepartmentManagementPanel());
            tabbedPane.addTab("Attendance", new AttendancePanel());
            tabbedPane.addTab("Reports", new ReportsPanel());
            tabbedPane.addTab("Settings", new SettingsPanel());
        } else if ("MANAGER".equals(currentUser.getRole())) {
            tabbedPane.addTab("Dashboard", new DashboardPanel());
            tabbedPane.addTab("Employees", new EmployeeManagementPanel());
            tabbedPane.addTab("Attendance", new AttendancePanel());
            tabbedPane.addTab("Reports", new ReportsPanel());
        } else {
            tabbedPane.addTab("My Profile", new EmployeeProfilePanel(currentUser));
            tabbedPane.addTab("My Attendance", new EmployeeAttendancePanel(currentUser));
            tabbedPane.addTab("Leave Requests", new LeaveRequestPanel(currentUser));
        }

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        add(contentPanel);
    }

    private void setupListeners() {
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        themeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleTheme();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseUtil.closeConnection();
            }
        });
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            DatabaseUtil.closeConnection();
            this.dispose();

            // Open login frame
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }

    private void toggleTheme() {
        try {
            if (ThemeUtil.isLightTheme()) {
                ThemeUtil.setDarkTheme();
            } else {
                ThemeUtil.setLightTheme();
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to change theme", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configureFrame() {
        setTitle("Employee Management System");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}